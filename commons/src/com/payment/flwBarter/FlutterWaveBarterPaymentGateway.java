package com.payment.flwBarter;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by Balaji on 16-Jan-2020.
 */
public class FlutterWaveBarterPaymentGateway extends AbstractPaymentGateway
{
    public FlutterWaveBarterPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static final String GATEWAY_TYPE = "flwBarter";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.flwBarter");
    private static TransactionLogger transactionLogger = new TransactionLogger(FlutterWaveBarterPaymentGateway.class.getName());

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside FlutterWaveBarterPaymentGateway processSale ---");
        transactionLogger.error("trackingid-------" + trackingID);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        Functions functions = new Functions();
        FlwBarterUtils utils = new FlwBarterUtils();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        String card_number = cardDetailsVO.getCardNum();
        String cvv = cardDetailsVO.getcVV();
        String expiration_month = cardDetailsVO.getExpMonth();
        String expiration_year = cardDetailsVO.getExpYear();
        boolean isTest = gatewayAccount.isTest();

        String publicKey = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String amount = transDetailsVO.getAmount();
        String currency = transDetailsVO.getCurrency();
        String country = addressDetailsVO.getCountry();
        String email = addressDetailsVO.getEmail();
        String phone = addressDetailsVO.getPhone();
        String firstName = addressDetailsVO.getFirstname();
        String lastName = addressDetailsVO.getLastname();

        String redirectUrl = "";
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
            redirectUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL") + trackingID;
        else
            redirectUrl = RB.getString("TERM_URL") + trackingID;

        transactionLogger.error("step 1 ::----------create request----------");
//       step 1  ::::::::::::::

        String initiateRequest = "{" +
                "\"amount\":\"" + amount + "\"," +
                "\"PBFPubKey\":\"" + publicKey + "\"," +
                "\"currency\":\"" + currency + "\"," +
                "\"country\":\"" + country + "\"," +
                "\"email\":\"" + email + "\"," +
                "\"txRef\":\"" + trackingID + "\"," +
                "\"is_barter\":\"1\"," +
                "\"payment_type\":\"barter\"," +
                "\"phonenumber\":\"" + phone + "\"," +
                "\"firstname\":\"" + firstName + "\"," +
                "\"lastname\":\"" + lastName + "\"," +
                "\"redirect_url\":\"" + redirectUrl + "\"" +
                "}";
        transactionLogger.error("initiateRequest-------" +trackingID + "--" +  initiateRequest);

        String encryptedKey = utils.getKeyEncryption(secretKey);
        String encryptRequest = utils.encryptData(initiateRequest, encryptedKey);
        transactionLogger.error("encryptRequest-------" + trackingID + "--" + encryptRequest);

//        step 1.2 :::::::::::::::
        transactionLogger.error("step 1.2 ::---------hit encoded request-----------");
        String initiatePaymentRequest = "{" +
                "\"PBFPubKey\": \"" + publicKey + "\"," +  //public Key
                "\"client\": \"" + encryptRequest + "\"," +
                "\"alg\": \"3DES-24\"" +
                "}";
        transactionLogger.error("initiatePaymentRequest-------" + trackingID + "--" + initiatePaymentRequest);

        String initiatePaymentUrl = "";
        if (isTest)
        {
            initiatePaymentUrl = RB.getString("TEST_INITIATE_PAYMENT_URL");
        }
        else
        {
            initiatePaymentUrl = RB.getString("LIVE_INITIATE_PAYMENT_URL");
        }
        String initiatePaymentResponse = utils.doPostHTTPSURLConnectionClient(initiatePaymentUrl, "", initiatePaymentRequest);
        transactionLogger.error("initiatePaymentResponse-------" + trackingID + "--" + initiatePaymentResponse);

        String trans_refrenc = "";
        String err_status = "";
        String err_message = "";
        if (initiatePaymentResponse != null)
            try
            {
                JSONObject jsonObject = new JSONObject(initiatePaymentResponse);
                if (jsonObject != null)
                {
                    if (functions.isValueNull(jsonObject.getString("data")))
                    {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data") != null ? jsonObject.getJSONObject("data") : null;
                        if (jsonObject1.has("flw_reference"))
                        {
                            trans_refrenc = jsonObject1.getString("flw_reference");
                            transactionLogger.error("trans_refrenc---------- " + trans_refrenc);
                        }
                    }
                    if (jsonObject.has("status"))
                    {
                        err_status = jsonObject.getString("status");
                        err_message = jsonObject.getString("message");
                    }
                }
            }
            catch (JSONException e)
            {
                transactionLogger.error("JSONException step1 --", e);
            }
        if (!err_status.equals("error") && functions.isValueNull(trans_refrenc))
        {

            // Step 2:Get details of the transaction on Barter  :::::::::::::::::::
            transactionLogger.error("step 2 ::---------Get details of the transaction on Barter-----------");

            String transactionDetailBarterRequest = "{" +
                    "\"reference\": \"" + trans_refrenc + "\"," + // reference...........
                    "\"public_key\": \"" + publicKey + "\"" +
                    "}";
            transactionLogger.error("transactionDetailBarterRequest-----------" + trackingID + "--" + transactionDetailBarterRequest);

            String transactionBarterURL = "";
            if (isTest)
            {
                transactionBarterURL = RB.getString("TEST_TRANSACTION_DETAIL_BARTER_URL");
            }
            else
            {
                transactionBarterURL = RB.getString("LIVE_TRANSACTION_DETAIL_BARTER_URL");
            }
            String transactionDetailBarterResponse = utils.doPostHTTPSURLConnectionClient(transactionBarterURL, publicKey, transactionDetailBarterRequest);
            transactionLogger.error("transactionDetailBarterResponse-----------" + trackingID + "--" + transactionDetailBarterResponse);

            String response_callbackURL = "";
            String response_amount = "";
            String response_currency = "";
            String appfee = "";
            String response_authURL = "";
            try
            {
                JSONObject jsonObject = new JSONObject(transactionDetailBarterResponse);
                if (jsonObject != null)
                {

                    if (!jsonObject.isNull("data"))
                    {
                        JSONObject data1 = jsonObject.getJSONObject("data");
//                        JSONObject data2 = data1.getJSONObject("data")!=null?data1.getJSONObject("data"):null;
                        if (!data1.isNull("data"))
                        {
                            JSONObject data2 = data1.getJSONObject("data");

                            if (data2.has("amount"))
                                response_amount = data2.get("amount").toString();

                            if (data2.has("currency"))
                                response_currency = (String) data2.get("currency");

                            if (data2.has("appfee"))
                                appfee = data2.get("appfee").toString();

                            if (data2.has("callbackurl"))
                                response_callbackURL = (String) data2.get("callbackurl");

                            if (data2.has("authurl"))
                                response_authURL = (String) data2.get("authurl");
                        }
                    }
                }
            }
            catch (JSONException e)
            {
                transactionLogger.error("JSONException step2---", e);
            }


            // Step 3 - Authenticate the customer on Barter  ::
            transactionLogger.error("Step 3 - Authenticate the customer on Barter ------------------");

            HashMap<String, String> userDetails = utils.checkCardPresetInDB(utils.masking(card_number));
            String userType = userDetails.get("userType");
            String authenticateCustomerResponse = "";
            String authenticateUserUrl = "";
            if (isTest)
            {
                authenticateUserUrl = RB.getString("TEST_AUTHENTICATE_USER_URL");
            }
            else
            {
                authenticateUserUrl = RB.getString("LIVE_AUTHENTICATE_USER_URL");
            }
            if (userType.equals("existing"))
            {
                //request for existing card/user
                transactionLogger.error("inside existing user request");

                String emailId = userDetails.get("email");
                String password = userDetails.get("password");

                transactionLogger.error(trackingID + " " + "email--" + email + "    pass--" + password);

                String existingUserRequest = ""
                        + "{"
                        + "\"type\": \"auth\","
                        + "\"currency\": \"" + response_currency + "\","
                        + "\"amount\": {"
                        + "\"transaction_amount\": " + response_amount + ","
                        + "\"fee\": " + appfee + ","
                        + "\"merchant_bears_fee\": true"
                        + "},"
                        + "\"authentication_request\": {"
                        + "\"identifier\": \"" + emailId + "\","
                        + "\"password\": \"" + password + "\""
                        + "},"
                        + "\"transaction_reference\": \"" + trans_refrenc + "\","
                        + "\"call_back_url\": \"" + response_callbackURL + "\""
                        + "}";

                transactionLogger.error("existingUserRequest-----------" +trackingID + "--" +  existingUserRequest);

                authenticateCustomerResponse = utils.doPostHTTPSURLConnectionClient(authenticateUserUrl, publicKey, existingUserRequest);

                transactionLogger.error("authenticateCustomerResponse-----------" +trackingID + "--" +  authenticateCustomerResponse);
            }
            else if (userType.equals("new"))
            {
//       request for new card/user
                transactionLogger.error("inside new user request");

                String pass = card_number.substring(card_number.length() - 4);
                transactionLogger.debug("card_number----before new entry---" + card_number);
                String encryptedCard = PzEncryptor.encryptPAN(card_number);

                transactionLogger.debug("encryptedCard----before new entry---" + encryptedCard);
                String maskedCard = utils.masking(card_number);

                // insert new customer entry
                utils.addNewUserEntry(encryptedCard, maskedCard, email, pass, phone);

                //create request for new user
                String newUserRequest = ""
                        + "{"
                        + "\"type\": \"register\","
                        + "\"currency\": \"" + response_currency + "\","
                        + "\"amount\": {"
                        + "\"transaction_amount\": " + response_amount + ","
                        + "\"fee\": " + appfee + ","
                        + "\"merchant_bears_fee\": false"
                        + "},"
                        + "\"notes\": \"Pay with Barter\","
                        + "\"register_and_pay_with_barter_request\": {"
                        + "\"first_name\": \"" + firstName + "\","
                        + "\"last_name\": \"" + lastName + "\","
                        + "\"mobile_number\": \"" + phone + "\","
                        + "\"email_address\": \"" + email + "\","
                        + "\"password\": \"" + pass + "\","
                        + "\"country\": \"" + country + "\","
                        + "\"transaction_pin\": \"" + pass + "\""
                        + "},"
                        + "\"transaction_reference\": \"" + trans_refrenc + "\","
                        + "\"call_back_url\": \"" + response_callbackURL + "\""
                        + "}";

                transactionLogger.error("newUserRequest--------------" + trackingID + "--" + newUserRequest);
                authenticateCustomerResponse = utils.doPostHTTPSURLConnectionClient(authenticateUserUrl, publicKey, newUserRequest);

                transactionLogger.error("authenticateCustomerResponse----------" + trackingID + "--" + authenticateCustomerResponse);
            }
            String authenticateStatus = "";
            String authToken = "";
            String desc = "";
            try
            {
                JSONObject jsonObj = new JSONObject(authenticateCustomerResponse);
                if (jsonObj != null)
                {
                    if (jsonObj.has("status"))
                        authenticateStatus = jsonObj.getString("status");

                    if (jsonObj.has("description"))
                        desc = jsonObj.getString("description");

//                    JSONObject data = jsonObj.getJSONObject("data")!=null?jsonObj.getJSONObject("data"):null;
                    if (!jsonObj.isNull("data"))
                    {
                        JSONObject data = jsonObj.getJSONObject("data");
//                        JSONObject customer_response = data.getJSONObject("customer_response")!=null?data.getJSONObject("customer_response"):null;
                        if (!data.isNull("customer_response"))
                        {
                            JSONObject customer_response = data.getJSONObject("customer_response");
                            if (customer_response.has("auth_token"))
                                authToken = customer_response.getString("auth_token");
                        }
                    }
                }
            }
            catch (JSONException e)
            {
                transactionLogger.error("JSONException 3---->", e);
            }


            //     4.2 Charge the customer using card ::::::::::::::::::::::::::::::::::::::::
            transactionLogger.error("4 Charge the customer using card ------------------");

            if (authenticateStatus.equals("success"))
            {
                String chargeCustomerRequest = "{" +
                        "\"type\": \"charge_card\"," +
                        "\"currency\": \"" + response_currency + "\"," +
                        "\"amount\": {" +
                        "\"transaction_amount\": " + amount + "," +
                        "\"fee\": " + appfee + "," +
                        "\"merchant_bears_fee\": true" +
                        "}," +
                        "\"notes\": \"Pay with Barter\"," +
                        "\"authentication_request\": {" +
                        "\"auth_token\": \"" + authToken + "\"" +
                        "}," +
                        "\"pay_with_card_request\": {" +
                        "\"card_id\": 0," +
                        "\"pan\": \"" + card_number + "\"," +
                        "\"cvv\": \"" + cvv + "\"," +
                        "\"expiry_month\": \"" + expiration_month + "\"," +
                        "\"expiry_year\": \"" + expiration_year + "\"" +
                        "}," +
                        "\"transaction_reference\": \"" + trans_refrenc + "\"," +
                        "\"call_back_url\": \"" + response_callbackURL + "\"" +
                        "}";
                String chargeCustomerRequestlog = "{" +
                        "\"type\": \"charge_card\"," +
                        "\"currency\": \"" + response_currency + "\"," +
                        "\"amount\": {" +
                        "\"transaction_amount\": " + amount + "," +
                        "\"fee\": " + appfee + "," +
                        "\"merchant_bears_fee\": true" +
                        "}," +
                        "\"notes\": \"Pay with Barter\"," +
                        "\"authentication_request\": {" +
                        "\"auth_token\": \"" + authToken + "\"" +
                        "}," +
                        "\"pay_with_card_request\": {" +
                        "\"card_id\": 0," +
                        "\"pan\": \"" + functions.maskingPan(card_number) + "\"," +
                        "\"cvv\": \"" + functions.maskingNumber(cvv) + "\"," +
                        "\"expiry_month\": \"" + functions.maskingNumber(expiration_month) + "\"," +
                        "\"expiry_year\": \"" + functions.maskingNumber(expiration_year) + "\"" +
                        "}," +
                        "\"transaction_reference\": \"" + trans_refrenc + "\"," +
                        "\"call_back_url\": \"" + response_callbackURL + "\"" +
                        "}";

                transactionLogger.error("chargeCustomerRequest---------------" + trackingID + "--" + chargeCustomerRequestlog);

                String chargeCustomerUrl = "";
                if (isTest)
                {
                    chargeCustomerUrl = RB.getString("TEST_AUTHENTICATE_USER_URL");
                    transactionLogger.debug("test url---" + chargeCustomerUrl);
                }
                else
                {
                    chargeCustomerUrl = RB.getString("LIVE_AUTHENTICATE_USER_URL");
                }
                String chargeCustomerResponse = utils.doPostHTTPSURLConnectionClient(chargeCustomerUrl, publicKey, chargeCustomerRequest);
                transactionLogger.error("chargeCustomerResponse---------------" + trackingID + "--" + chargeCustomerResponse);

                String status = "";
                String description = "";
                String responseMessage = "";
                String transactionIdentifier = "";
                String transactionReference = "";
                String status2 = "";
                String responseCode = "";
                String authUrl = "";
                try
                {
                    JSONObject jObj = new JSONObject(chargeCustomerResponse);
                    if (jObj != null)
                    {
                        if (jObj.has("status"))
                            status = jObj.getString("status");

                        if (jObj.has("description"))
                            description = jObj.getString("description");

                        if (!jObj.isNull("data"))
                        {
                            JSONObject data = jObj.getJSONObject("data");
//                            JSONObject data2 = data.getJSONObject("data")!=null?data.getJSONObject("data"):null;
                            if (!data.isNull("data"))
                            {
                                JSONObject data2 = data.getJSONObject("data");
                                if (data2.has("authurl"))
                                    authUrl = data2.getString("authurl");
                                if (data2.has("responsemessage"))
                                    responseMessage = data2.getString("responsemessage");

                                if (data2.has("otptransactionidentifier"))
                                    transactionIdentifier = data2.getString("otptransactionidentifier");

                                if (data2.has("transactionreference"))
                                    transactionReference = data2.getString("transactionreference");

                                if (data2.has("responsecode"))
                                    responseCode = data2.getString("responsecode");
                            }
                        }
                    }
                    transactionLogger.debug("responsecode--------" + trackingID + "--" + responseCode);
                    transactionLogger.debug("message--------" + responseMessage);
                    transactionLogger.debug("transactionIdentifier--------" + transactionIdentifier);
                    transactionLogger.debug("responseCode--------" + trackingID + "--" + responseCode);
                    transactionLogger.debug("description--------" + description);
                }
                catch (JSONException e)
                {
                    transactionLogger.error("JSONException 4-----", e);
                }
//            fill vo from chargeCustomerResponse
                if (responseCode.equalsIgnoreCase("0") || responseCode.equalsIgnoreCase("00"))
                {
//                    successfull transaction
                    transactionLogger.error("inside--------- success non 3d ");
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionId(trans_refrenc);
                    commResponseVO.setRemark(responseMessage);
                    commResponseVO.setDescription(description);
                    commResponseVO.setErrorCode(responseCode);
                }
                else if (responseCode.equals("02"))
                {
//   pending transaction
                    transactionLogger.error("inside 3D pending transaction -------");
                    commResponseVO.setStatus("pending3DConfirmation");
                    commResponseVO.setUrlFor3DRedirect(authUrl);
                    commResponseVO.setTransactionId(trans_refrenc);
                    commResponseVO.setRemark(responseMessage);
                    commResponseVO.setDescription(description);
                    commResponseVO.setErrorCode(responseCode);
                }
                else
                {
                    //failed transaction
                    transactionLogger.error("inside--------- failed transaction entry in Charge the customer using card");
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionId(trans_refrenc);
                    commResponseVO.setRemark(description);
                    commResponseVO.setDescription(responseMessage);
                    commResponseVO.setErrorCode(responseCode);
                }
            }
            else
            {
                //failed to authenticate user in barter
                transactionLogger.error("inside---------failed to authenticate user in barter");
                commResponseVO.setStatus("fail");
                commResponseVO.setTransactionId(trans_refrenc);
                commResponseVO.setRemark("SYS:Transaction Declined");
                commResponseVO.setDescription(desc);
            }
        }
        else
        {
            //error in initiate payment request
            transactionLogger.error("inside---------error in initiate payment request");
            commResponseVO.setStatus("fail");
            commResponseVO.setTransactionId(trans_refrenc);
            commResponseVO.setRemark(err_message);
            commResponseVO.setDescription("SYS: transaction failed");
        }

        if(functions.isValueNull(commMerchantVO.getHostUrl())){
            commResponseVO.setTerURL("https://"+commMerchantVO.getHostUrl());
        }
        return commResponseVO;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("-----inside processInquiry-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();
        FlwBarterUtils utils = new FlwBarterUtils();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String uniqueId = commTransactionDetailsVO.getPreviousTransactionId();
        String publicKey = gatewayAccount.getFRAUD_FTP_USERNAME();
        boolean isTest = gatewayAccount.isTest();
        String url = "";
        if (isTest)
        {
            url = RB.getString("TEST_INQUITUY") + uniqueId;
        }
        else
        {
            url = RB.getString("LIVE_INQUITUY") + uniqueId;
        }
        String response = utils.doGetHTTPSURLConnectionClient(url, publicKey);
        transactionLogger.error("Inquiry response-----"+response);

        String status = "";
        String description = "";
        String transCode = "";
        String message = "";
        String payment_source = "";
        String transaction_amount = "";
        String payment_reference = "";
        String callback_url = "";
        String date_created = "";
        if (functions.isValueNull(response))
        {
            try
            {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("status"))
                    status=jsonObject.getString("status");
                if (jsonObject.has("description"))
                    description=jsonObject.getString("description");

                if(!jsonObject.isNull("data")){
                    JSONObject data=jsonObject.getJSONObject("data");
                    if (data.has("code"))
                        transCode=data.getString("code");
                    if (data.has("message"))
                        message=data.getString("message");
                    if (data.has("payment_source"))
                        payment_source=data.getString("payment_source");
                    if (data.has("transaction_amount"))
                        transaction_amount=data.getString("transaction_amount");
                    if (data.has("payment_reference"))
                        payment_reference=data.getString("payment_reference");
                    if (data.has("callback_url"))
                        callback_url=data.getString("callback_url");
                    if (data.has("date_created"))
                        date_created=data.getString("date_created");
                }

                transactionLogger.error("code-----"+transCode);
                if(transCode.equalsIgnoreCase("00") || transCode.equalsIgnoreCase("0")){
                    //  successful transaction
                    transactionLogger.error("Inside successfulll transaction ");
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setDescription("Transaction Successful");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else if(transCode.equalsIgnoreCase("02")){
                    //  pending transaction
                    transactionLogger.error("Inside pending transaction ");
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setDescription("Transaction Pending");
                    commResponseVO.setRemark(message);
                }
                else{
                    //  failed transaction
                    transactionLogger.error("Inside failed transaction ");
                    if(jsonObject.isNull("data"))
                    {
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setTransactionStatus("pending3DConfirmation");
                        commResponseVO.setDescription("Pending 3D Confirmation");
                        commResponseVO.setRemark(description);
                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionStatus("failed");
                        commResponseVO.setDescription("Transaction Failed");
                        commResponseVO.setRemark(message);
                        commResponseVO.setErrorCode(transCode);
                    }

                }
                commResponseVO.setTransactionType("Inquiry");
                commResponseVO.setTransactionId(payment_reference);
                commResponseVO.setBankTransactionDate(date_created);
                commResponseVO.setAmount(commTransactionDetailsVO.getAmount());
                commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
                commResponseVO.setMerchantId(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
            }
            catch (JSONException e)
            {
                transactionLogger.error("JSONException ::::::::::", e);
            }
        }
        return commResponseVO;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("FlutterWaveBarterPaymentGateway:: processAuthentication() ---This Functionality is not supported by processing gateway");
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint(FlutterWaveBarterPaymentGateway.class.getName(), "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    @Override
    public String getMaxWaitDays ()
    {
        return null;
    }
}

