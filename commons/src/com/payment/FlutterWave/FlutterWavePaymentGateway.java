package com.payment.FlutterWave;

import com.directi.pg.*;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.manager.TerminalManager;
import com.manager.vo.TerminalVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.endeavourmpi.EndeavourMPIGateway;
import com.payment.endeavourmpi.EnrollmentRequestVO;
import com.payment.endeavourmpi.EnrollmentResponseVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.log4j.*;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by Jitendra on 19-Apr-19.
 */
public class FlutterWavePaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE             = "Flutter";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.FlutterWave");
  //private static TransactionLogger transactionLogger  = new TransactionLogger(FlutterWavePaymentGateway.class.getName());
    private static FlutterWaveLogger transactionLogger  = new FlutterWaveLogger(FlutterWavePaymentGateway.class.getName());
    private static Logger log                           = new Logger(FlutterWavePaymentGateway.class.getName());
    org.apache.log4j.Logger facileroLogger              = LogManager.getLogger("facilerolog");

    public FlutterWavePaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static void main(String[] args)
    {
       /* STEP 1: Encrypt the secret key.
       *  STEP 2: Make card payments request and encrypt it.
       *  STEP 3: Initiate payment using encrypted data(encrypted card payment request and encrypted secret key )
       *  initiatePaymentResponse ------------{"status":"success","message":"AUTH_SUGGESTION","data":{"suggested_auth":"NOAUTH_INTERNATIONAL"}}
       *  Step :4 Initiate the payment again as we are getting "suggested_auth": "NOAUTH_INTERNATIONAL" / "AVS_VBVSECURECODE"
       *  initiatePaymentResponseAgain ------------{"status":"success","message":"V-COMP","data":{"id":543846,"txRef":"18041832",
       *  "orderRef":"URF_1555592553121_4093735","flwRef":"FLW-MOCK-30f3aed4a48d1e40034dfd02d31fc578","redirectUrl":"http://localhost:8081/transaction/CommonBackEndServlet?trackingId=18041832",
       *  "device_fingerprint":"N/A","settlement_token":null,"cycle":"one-time","amount":10,"charged_amount":10,"appfee":0.35,"merchantfee":0,"merchantbearsfee":1,"chargeResponseCode":"02",
       *  "raveRef":"RV3155559255184490215DE517","chargeResponseMessage":"Please enter the OTP sent to your mobile number 080****** and email te**@rave**.com",
       *  "authModelUsed":"VBVSECURECODE","currency":"NGN","IP":"::ffff:10.45.71.96","narration":"CARD Transaction ","status":"success-pending-validation",
       *  "modalauditid":"222e212c7f135714c044c63190408b1d","vbvrespmessage":"Approved. Successful","authurl":"https://ravesandboxapi.flutterwave.com/mockvbvpage?ref=FLW-MOCK-30f3aed4a48d1e40034dfd02d31fc578&code=00&message=Approved. Successful&receiptno=RN1555592553214",
       *  "vbvrespcode":"00","acctvalrespmsg":null,"acctvalrespcode":null,"paymentType":"card","paymentPlan":null,"paymentPage":null,"paymentId":"4242","fraud_status":"ok","charge_type":"normal","is_live":0,"createdAt":"2019-04-18T13:02:33.000Z","updatedAt":"2019-04-18T13:02:33.000Z","deletedAt":null,"customerId":118340,"AccountId":40834,"customer":{"id":118340,"phone":null,"fullName":"Anonymous customer","customertoken":null,"email":"user@gmail.com",
       *  "createdAt":"2019-04-18T13:02:32.000Z","updatedAt":"2019-04-18T13:02:32.000Z","deletedAt":null,"AccountId":40834},"customercandosubsequentnoauth":false}}
       *  Step :5 Use auth url and load it to enter otp.
       *  Step :6 now response will come through webhook or on front end validate and varify it .
       *  step :7 if "suggested_auth": "PIN" in step 3 initiatePaymentResponse then use pin and then opt ahead.
       *
       *  -----Notification JSON----- on front end or backend
        {"id":548729,"txRef":"85014","flwRef":"FLW-MOCK-da4c4070f01ecece0cfa5a0a5b1c97fe","orderRef":"URF_1555929781883_2985735","paymentPlan":null,
        "createdAt":"2019-04-22T10:43:01.000Z","amount":10,"charged_amount":10,"status":"successful","IP":"::ffff:10.65.146.73","currency":"USD",
        "customer":{"id":119420,"phone":null,"fullName":"Anonymous customer","customertoken":null,"email":"<emailaddress>","createdAt":"2019-04-22T10:43:01.000Z",
        "updatedAt":"2019-04-22T10:43:01.000Z","deletedAt":null,"AccountId":40834},"payment_entity":"7d5e71ac9d7e1d2997950341a6c49524","entity":{"card6":"455605","card_last4":"2643"},
        "event.type":"CARD_TRANSACTION"}
       *  we have not implemented "suggested_auth": "PIN" here
       *23/04/2019
       * International Card Number -4556052704172643 899 01/2021
       * Local Card -
       *
       * */
        FlutterWaveUtils flutterWaveUtils = new FlutterWaveUtils();
        String txref="6594546";
        //FLWSECK-620fc59d6c1b1d9eb247547b9f2cf191-X
        String secretKey="FLWSECK-d75cab8cd266007eb172c2f39b32a900-X";
        String inquiryRequest = "{\n" +
                "  \"txref\": \"" + txref + "\",\n" +
                "  \"SECKEY\": \"" + secretKey + "\"\n" +
                "}";

        System.out.println("inquiryRequest --for--" + txref + "--" + inquiryRequest);
        String inquiryResponse = "";

        transactionLogger.error("inside isLive-----" + RB.getString("LIVE_PAYMENT_VERIFICATION_URL"));
        try
        {
            inquiryResponse = flutterWaveUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_PAYMENT_VERIFICATION_URL"), inquiryRequest);
            System.out.println("inquiryRequest --for--" + txref + "--" + inquiryResponse);
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException" ,e);
        }
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Inside processSale ---");
        Comm3DResponseVO commResponseVO     = new Comm3DResponseVO();
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Functions functions                                 = new Functions();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        CommMerchantVO commMerchantVO                       = commRequestVO.getCommMerchantVO();
        GenericTransDetailsVO transDetailsVO                = commRequestVO.getTransDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO                  = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO            = commRequestVO.getAddressDetailsVO();
        FlutterWaveUtils flutterWaveUtils                   = new FlutterWaveUtils();
        boolean isTest                  = gatewayAccount.isTest();
        String merchanId                = gatewayAccount.getMerchantId();
        String _3DSupported             = gatewayAccount.get_3DSupportAccount();
        String CALL_EXECUTE_AFTER       = RB.getString("CALL_EXECUTE_AFTER");
        String CALL_EXECUTE_INTERVAL    = RB.getString("CALL_EXECUTE_INTERVAL");
        String MAX_EXECUTE_SEC          = RB.getString("MAX_EXECUTE_SEC");
        String isThreadAllowed          =gatewayAccount.getFRAUD_FILE_SHORT_NAME();
        String cardNumber = "", cvv = "", expiryMonth = "", expiryYear = "", amount = "", email = "", billingzip = "", billingcity = "", billingaddress = "", billingstate = "", billingcountry = "", status = "", message = "",
                responseAgainStatus = "", responseAgainMessage = "", validateTxRef = "", validateStatus = "", currency = "", country = "";


        if (functions.isValueNull(transDetailsVO.getCurrency()))
            currency = transDetailsVO.getCurrency();
        if (functions.isValueNull(addressDetailsVO.getCountry()))
            country = addressDetailsVO.getCountry();
        if (functions.isValueNull(cardDetailsVO.getCardNum()))
            cardNumber = cardDetailsVO.getCardNum();
        if (functions.isValueNull(cardDetailsVO.getcVV()))
            cvv = cardDetailsVO.getcVV();
        if (functions.isValueNull(cardDetailsVO.getExpMonth()))
            expiryMonth = cardDetailsVO.getExpMonth();
        if (functions.isValueNull(cardDetailsVO.getExpYear()))
            expiryYear = cardDetailsVO.getExpYear();
        if (functions.isValueNull(transDetailsVO.getAmount()))
            amount = transDetailsVO.getAmount();
        if (functions.isValueNull(addressDetailsVO.getEmail()))
            email = addressDetailsVO.getEmail();
        if (functions.isValueNull(addressDetailsVO.getZipCode()))
            billingzip = addressDetailsVO.getZipCode();
        if (functions.isValueNull(addressDetailsVO.getCity()))
            billingcity = addressDetailsVO.getCity();
        if (functions.isValueNull(addressDetailsVO.getStreet()))
            billingaddress = addressDetailsVO.getStreet();
        if (functions.isValueNull(addressDetailsVO.getState()))
            billingstate = addressDetailsVO.getState();
        if (functions.isValueNull(addressDetailsVO.getCountry()))
            billingcountry = addressDetailsVO.getCountry();

        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(transDetailsVO.getCurrency())) {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount())) {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency())) {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }

        String toType =  "";
        if(functions.isValueNull(transDetailsVO.getTotype())){
            toType =  transDetailsVO.getTotype();
        }
        transactionLogger.error("ToType ---> " +trackingID+" "+ toType);
        String termUrl = "";
        transactionLogger.error("HOST_URL ---" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            transactionLogger.error("From HOST_URL----" + termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("From RB TERM_URL ----" + termUrl);
        }

        String secretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String publicKey = gatewayAccount.getFRAUD_FTP_USERNAME();

        // STEP 1: ENCRYPT THE SECRET KEY  #####
        String encryptedKey = "";
        if (functions.isValueNull(secretKey))
        {
            encryptedKey = flutterWaveUtils.getKey(secretKey);
        }
        transactionLogger.debug("secretKey ---" + secretKey);
        transactionLogger.debug("encryptedKey ---" + encryptedKey);
        transactionLogger.debug("publicKey ---" + publicKey);
        transactionLogger.error("_3DSupported ---"+trackingID+"-->" + _3DSupported);
        try
        {
        if("R".equalsIgnoreCase(_3DSupported))
        {
            String mpiMid       = gatewayAccount.getCHARGEBACK_FTP_PATH();
            String currencyId   = CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
            transactionLogger.error("currencyId-----"+currencyId);

            String year     = cardDetailsVO.getExpYear();
            String expyear  = year.substring(2, 4);

            transactionLogger.error("year-----"+expyear);
            transactionLogger.error("Inside 3-D Supported----");
            EndeavourMPIGateway endeavourMPIGateway = new EndeavourMPIGateway();
            EnrollmentRequestVO enrollmentRequestVO = new EnrollmentRequestVO();
            enrollmentRequestVO.setMid(mpiMid);
            enrollmentRequestVO.setName(addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname());
            enrollmentRequestVO.setPan(cardDetailsVO.getCardNum());
            enrollmentRequestVO.setExpiry(expyear + "" + cardDetailsVO.getExpMonth());
            enrollmentRequestVO.setCurrency(currencyId);
            enrollmentRequestVO.setAmount(flutterWaveUtils.getCentAmount(transDetailsVO.getAmount()));
            enrollmentRequestVO.setDesc(transDetailsVO.getOrderDesc());
            enrollmentRequestVO.setUseragent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US)");
            enrollmentRequestVO.setAccept("en-us");
            enrollmentRequestVO.setTrackid(trackingID);

            EnrollmentResponseVO enrollmentResponseVO = endeavourMPIGateway.processEnrollment(enrollmentRequestVO);

            if (enrollmentResponseVO != null)
            {
                transactionLogger.error("Inside Enrollment ResponseVO-----");
                String result   = enrollmentResponseVO.getResult();
                String avr      = enrollmentResponseVO.getAvr();
                transactionLogger.error("result--->"+result);
                transactionLogger.error("avr--->"+avr);

                if (!"Enrolled".equals(result) && !"Y".equals(avr))
                {
                    String fromAccountId            = gatewayAccount.getFromAccountId();
                    String fromMid                  = gatewayAccount.getFromMid();
                    TerminalManager terminalManager = new TerminalManager();
                    TerminalVO terminalVO           = terminalManager.getRoutingTerminalByFromAccountId(fromAccountId, transDetailsVO.getToId(), transDetailsVO.getPaymentType(), transDetailsVO.getCardType());
                    if (terminalVO != null && functions.isValueNull(fromMid))
                    {
                        gatewayAccount  = GatewayAccountService.getGatewayAccount(fromAccountId);
                        publicKey       = gatewayAccount.getFRAUD_FTP_USERNAME();
                        secretKey       = gatewayAccount.getFRAUD_FTP_PASSWORD();
                        commResponseVO.setTerminalId(terminalVO.getTerminalId());
                        commResponseVO.setFromAccountId(fromAccountId);
                        commResponseVO.setFromMid(fromMid);
                    }else
                    {
                        transactionLogger.error("Card Not Enrolled For 3D --"+trackingID);
                        commResponseVO.setStatus("fail");
                        commResponseVO.setDescription("Card Not Enrolled For 3D");
                        commResponseVO.setRemark("Card Not Enrolled For 3D");
                        return commResponseVO;
                    }
                }
            }
        }


            String terminalID = commTransactionDetailsVO.getTerminalId();

            transactionLogger.error("terminalId ===== " + terminalID);
            transactionLogger.error("fromCurrency ===== " + currency);

            String conversion_currency  = "";
            if (functions.isValueNull(terminalID))
            {
                HashMap<String, String> hashMap = null;
                hashMap                         = flutterWaveUtils.getTerminalConversionDetails(terminalID);
                String currency_conversion      = hashMap.get("currency_conversion");
                conversion_currency             = hashMap.get("conversion_currency");
                transactionLogger.error("Currency Conversion Flag---"+currency_conversion);
                if ("Y".equalsIgnoreCase(currency_conversion))
                {
                    transactionLogger.error("conversion_currency---"+conversion_currency);
                    transactionLogger.error("amount in "+currency+" ----"+amount);
                    amount      = flutterWaveUtils.makeCurrencyConversion(currency,conversion_currency,amount);
                    transactionLogger.error("after conversion in "+conversion_currency+" >> amount ----"+amount);
                    currency    = conversion_currency;
                }
            }


// STEP 2: Card Payments Request  #####
        String cardRequest = "{\n" +
                "\"PBFPubKey\": \"" + publicKey + "\",\n" +
                "\"cardno\": \"" + cardNumber + "\",\n" +
                "\"cvv\": \"" + cvv + "\",\n" +
                "\"expirymonth\": \"" + expiryMonth + "\",\n" +
                "\"expiryyear\": \"" + expiryYear + "\",\n" +
                "\"currency\": \"" + currency + "\",\n" +
                //"\"country\": \""+country+"\",\n" +
                "\"amount\": \"" + amount + "\",\n" +
                "\"email\": \"" + email + "\",\n" +
                "\"txRef\": \"" + trackingID + "\",\n" +
                "\"redirect_url\": \"" + termUrl + "" + trackingID + "\"\n" +
                "}";

            String cardRequestLog = "{" +
                    "\"PBFPubKey\": \"" + publicKey + "\"," +
                    "\"cardno\": \"" + functions.maskingPan(cardNumber) + "\"," +
                    "\"cvv\": \"" + functions.maskingNumber(cvv) + "\"," +
                    "\"expirymonth\": \"" + functions.maskingNumber(expiryMonth) + "\"," +
                    "\"expiryyear\": \"" + functions.maskingNumber(expiryYear) + "\"," +
                    "\"currency\": \"" + currency + "\"," +
                    //"\"country\": \""+country+"\",\n" +
                    "\"amount\": \"" + amount + "\"," +
                    "\"email\": \"" + email + "\"," +
                    "\"txRef\": \"" + trackingID + "\"," +
                    "\"redirect_url\": \"" + termUrl + "" + trackingID + "\"" +
                    "}";

                if("Facilero".equalsIgnoreCase(toType)){
                    facileroLogger.error("FlutterWave cardRequest --for--"+trackingID+"--" + cardRequestLog);
                }else{
                    transactionLogger.error("cardRequest --for--"+trackingID+"--" + cardRequestLog);
                }

                String encryptData = flutterWaveUtils.encryptData(cardRequest, encryptedKey);
                transactionLogger.debug("encryptData(Client) ---" + encryptData);



            // STEP 3: Initiate Payment Using encrypted Data  #####
            String initiatePaymentRequest = "{\n" +
                "  \"PBFPubKey\": \"" + publicKey + "\",\n" +
                "  \"client\": \"" + encryptData + "\",\n" +
                "  \"alg\": \"3DES-24\"\n" +
                "}";

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("FlutterWave Sale initiatePaymentRequest --for--"+trackingID+"--" + initiatePaymentRequest);
            }else{
                transactionLogger.error("Sale initiatePaymentRequest --for--"+trackingID+"--" + initiatePaymentRequest);
            }


            String initiatePaymentResponse = "";
            if (isTest)
            {
                transactionLogger.error("inside isTest-----" + RB.getString("TEST_INITIATE_PAYMENT_URL"));
                initiatePaymentResponse = flutterWaveUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_INITIATE_PAYMENT_URL"), initiatePaymentRequest);
            }
            else
            {
                transactionLogger.error("inside isLive-----" + RB.getString("LIVE_INITIATE_PAYMENT_URL"));
                initiatePaymentResponse = flutterWaveUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_INITIATE_PAYMENT_URL"), initiatePaymentRequest);
            }

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("FlutterWave Sale initiatePaymentResponse --for--"+trackingID+"--" + initiatePaymentResponse);
            }else{
                transactionLogger.error("Sale initiatePaymentResponse --for--"+trackingID+"--" + initiatePaymentResponse);
            }
        //initiatePaymentResponse type 1. ------------{"status":"success","message":"AUTH_SUGGESTION","data":{"suggested_auth":"PIN"}}
        //initiatePaymentResponse type 2. ------------"status":"success" "chargecode":"02" pending means 3d
        //initiatePaymentResponse type 3. ------------"status":"success" "chargecode":"00" means direct sucess
        //initiatePaymentResponse type 4. ------------"status":"error"  means fail
        String initiatePayment_Status       = "";
        String initiatePayment_Message      = "";
        String initiatePaymenSuggested_auth = "";
        String initiatePayment_flwRef       = "";
        String initiatePayment_authurl      = "";
        String initiatePayment_createdAt    = "";
        String initiatePayment_status       = "";
        String initiatePayment_chargeResponseCode   = "";
        String initiatePayment_vbvrespmessage       = "";

            if (initiatePaymentResponse != null)
            {
                if(!initiatePaymentResponse.contains("{"))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setRemark("Transaction is pending");
                    commResponseVO.setDescription("Transaction is pending");
                    return commResponseVO;
                }
                JSONObject jsonObject = new JSONObject(initiatePaymentResponse);
                if (jsonObject != null)
                {
                    if (jsonObject.has("status"))
                    {
                        initiatePayment_Status = jsonObject.getString("status");
                        transactionLogger.error("initiatePayment_Status ---" + initiatePayment_Status);
                    }
                    if (jsonObject.has("message"))
                    {
                        initiatePayment_Message = jsonObject.getString("message");
                        transactionLogger.error("initiatePayment_Message ---" + initiatePayment_Message);
                        if(functions.isValueNull(initiatePayment_Message) && initiatePayment_Message.contains(":") && !initiatePayment_Message.contains("FAILURE"))
                            initiatePayment_Message=initiatePayment_Message.split(":")[0];
                    }
                    if (functions.isValueNull(jsonObject.getString("data")))
                    {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        if (jsonObject1.has("suggested_auth"))
                        {
                            initiatePaymenSuggested_auth = jsonObject1.getString("suggested_auth");
                            transactionLogger.error("initiatePaymenSuggested_auth ---" + initiatePaymenSuggested_auth);
                        }
                        if (jsonObject1.has("flwRef"))
                        {
                            initiatePayment_flwRef = jsonObject1.getString("flwRef");
                            transactionLogger.error("initiatePayment_flwRef ---" + initiatePayment_flwRef);
                        }
                        if (jsonObject1.has("authurl"))
                        {
                            initiatePayment_authurl = jsonObject1.getString("authurl");
                            transactionLogger.error("initiatePayment_authurl ---" + initiatePayment_authurl);
                        }
                        if (jsonObject1.has("createdAt"))
                        {
                            initiatePayment_createdAt = jsonObject1.getString("createdAt");
                            transactionLogger.error("initiatePayment_createdAt ---" + initiatePayment_createdAt);
                        }
                        if (jsonObject1.has("status"))
                        {
                            initiatePayment_status = jsonObject1.getString("status");
                            transactionLogger.error("initiatePayment_status ---" + initiatePayment_status);
                        }
                        if (jsonObject1.has("chargeResponseCode"))
                        {
                            initiatePayment_chargeResponseCode = jsonObject1.getString("chargeResponseCode");
                            transactionLogger.error("initiatePayment_chargeResponseCode ---" + initiatePayment_chargeResponseCode);
                        }
                        if (jsonObject1.has("vbvrespmessage"))
                        {
                            initiatePayment_vbvrespmessage = jsonObject1.getString("vbvrespmessage");
                            transactionLogger.error("initiatePayment_vbvrespmessage ---" + initiatePayment_vbvrespmessage);
                            if(functions.isValueNull(initiatePayment_vbvrespmessage) && initiatePayment_vbvrespmessage.contains(":") && !initiatePayment_Message.contains("FAILURE"))
                                initiatePayment_vbvrespmessage=initiatePayment_vbvrespmessage.split(":")[0];
                        }
                    }
                }
                if (functions.isValueNull(initiatePayment_authurl) && !initiatePayment_Status.equals("error") && initiatePayment_chargeResponseCode.equals("02"))
                {
                    commResponseVO.setStatus("pending3DConfirmation");
                    commResponseVO.setUrlFor3DRedirect(initiatePayment_authurl);
                    commResponseVO.setTerURL(termUrl + trackingID);
                    commResponseVO.setDescription("SYS:3D Authentication Pending");
                    commResponseVO.setResponseTime(initiatePayment_createdAt);
                    commResponseVO.setTransactionId(initiatePayment_flwRef);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                    commResponseVO.setRemark(initiatePayment_status);
                    if("Y".equalsIgnoreCase(isThreadAllowed))
                        new AsyncFlutterwaveQueryService(trackingID,accountId,initiatePayment_flwRef,"Y",CALL_EXECUTE_AFTER,CALL_EXECUTE_INTERVAL,MAX_EXECUTE_SEC);
                   // commResponseVO.setTarget("_blank");

                    //return commResponseVO;
                }
                else if (!initiatePayment_Status.equals("error") && initiatePayment_chargeResponseCode.equals("00"))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescription(initiatePayment_vbvrespmessage);
                    commResponseVO.setRemark(initiatePayment_vbvrespmessage);
                    commResponseVO.setTransactionId(initiatePayment_flwRef);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setTmpl_Currency(tmpl_currency);
                    commResponseVO.setTmpl_Amount(tmpl_amount);
                    commResponseVO.setMerchantId(merchanId);
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    commResponseVO.setResponseTime(initiatePayment_createdAt);
                    commResponseVO.setErrorCode(initiatePayment_chargeResponseCode);
                    //return commResponseVO;
                }

                else if (initiatePayment_Status.equals("error")&&initiatePayment_Message.equalsIgnoreCase("An error occured. Please contact support"))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription(initiatePayment_Message);
                    commResponseVO.setRemark(initiatePayment_Message);
                    commResponseVO.setTransactionId(initiatePayment_flwRef);
                    commResponseVO.setErrorCode(initiatePayment_chargeResponseCode);
                }
                else if (initiatePayment_Status.equals("error"))
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setDescription(initiatePayment_Message);
                    commResponseVO.setRemark(initiatePayment_Message);
                    commResponseVO.setTransactionId(initiatePayment_flwRef);
                    commResponseVO.setErrorCode(initiatePayment_chargeResponseCode);
                }
                else
                {
                    // Step :4 Initiate the payment again by encrypting card again as we are getting "suggested_auth": "PIN"  or     "suggested_auth": ""NOAUTH_INTERNATIONAL"
                    //todo
                    String pin = "";
                    String cardRequestAgain = "{\n" +
                            "  \"PBFPubKey\": \"" + publicKey + "\",\n" +  //public Key
                            "  \"cardno\": \"" + cardNumber + "\",\n" +   //5438898014560229  4111111111111111
                            "  \"cvv\": \"" + cvv + "\",\n" +
                            "  \"expirymonth\": \"" + expiryMonth + "\",\n" +
                            "  \"expiryyear\": \"" + expiryYear + "\",\n" +
                            "  \"currency\": \"" + currency + "\",\n" +
                            // "  \"country\": \""+country+"\",\n" +
                            "  \"pin\": \"" + pin + "\",\n" +
                            "  \"suggested_auth\": \"" + initiatePaymenSuggested_auth + "\",\n" +
                            "  \"amount\": \"" + amount + "\",\n" +
                            "  \"email\": \"" + email + "\",\n" +
                            "  \"billingzip\": \"" + billingzip + "\",\n" +
                            "  \"billingcity\": \"" + billingcity + "\",\n" +
                            "  \"billingaddress\": \"" + billingaddress + "\",\n" +
                            "  \"billingstate\": \"" + billingstate + "\",\n" +
                            "  \"billingcountry\": \"" + billingcountry + "\",\n" +
                            "  \"txRef\": \"" + trackingID + "\",\n" +
                            "  \"redirect_url\": \"" + termUrl + "" + trackingID + "\"\n" +
                            "}";

                    String cardRequestAgainLog = "{" +
                            "  \"PBFPubKey\": \"" + publicKey + "\"," +  //public Key
                            "  \"cardno\": \"" + functions.maskingPan(cardNumber) + "\"," +   //5438898014560229  4111111111111111
                            "  \"cvv\": \"" + functions.maskingNumber(cvv) + "\"," +
                            "  \"expirymonth\": \"" + functions.maskingNumber(expiryMonth) + "\"," +
                            "  \"expiryyear\": \"" + functions.maskingNumber(expiryYear) + "\"," +
                            "  \"currency\": \"" + currency + "\",\n" +
                            // "  \"country\": \""+country+"\",\n" +
                            "  \"pin\": \"" + pin + "\",\n" +
                            "  \"suggested_auth\": \"" + initiatePaymenSuggested_auth + "\"," +
                            "  \"amount\": \"" + amount + "\"," +
                            "  \"email\": \"" + email + "\"," +
                            "  \"billingzip\": \"" + billingzip + "\"," +
                            "  \"billingcity\": \"" + billingcity + "\"," +
                            "  \"billingaddress\": \"" + billingaddress + "\"," +
                            "  \"billingstate\": \"" + billingstate + "\"," +
                            "  \"billingcountry\": \"" + billingcountry + "\"," +
                            "  \"txRef\": \"" + trackingID + "\"," +
                            "  \"redirect_url\": \"" + termUrl + "" + trackingID + "\"" +
                            "}";
                    if("Facilero".equalsIgnoreCase(toType)){
                        facileroLogger.error("FlutterWave Sale cardRequestAgain --for--"+trackingID+"--" + cardRequestAgainLog);
                    }else{
                        transactionLogger.error("Sale cardRequestAgain --for--"+trackingID+"--" + cardRequestAgainLog);
                    }


                    String cardResponseagain = flutterWaveUtils.encryptData(cardRequestAgain, encryptedKey);   //Returns Client
                    transactionLogger.error("Sale cardResponseagain (client )--for--"+trackingID+"--" + cardResponseagain);

                    // Step 5: Intialte payment again

                    String initiatePaymentRequestAgain = "{\n" +
                            "  \"PBFPubKey\": \"" + publicKey + "\",\n" +  //public Key
                            "  \"client\": \"" + cardResponseagain + "\",\n" +
                            "  \"alg\": \"3DES-24\"\n" +
                            "}";

                    if("Facilero".equalsIgnoreCase(toType)){
                        facileroLogger.error("FlutterWave Sale initiatePaymentRequestAgain --for--"+trackingID+"--" + initiatePaymentRequestAgain);
                    }else{
                        transactionLogger.error("Sale initiatePaymentRequestAgain --for--"+trackingID+"--" + initiatePaymentRequestAgain);
                    }

                    String initiatePaymentResponseAgain = "";
                    if (isTest)
                    {
                        transactionLogger.error("inside isTest-----" + RB.getString("TEST_INITIATE_PAYMENT_URL"));
                        initiatePaymentResponseAgain = flutterWaveUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_INITIATE_PAYMENT_URL"), initiatePaymentRequestAgain);
                    }
                    else
                    {
                        transactionLogger.error("inside isLive-----" + RB.getString("LIVE_INITIATE_PAYMENT_URL"));
                        initiatePaymentResponseAgain = flutterWaveUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_INITIATE_PAYMENT_URL"), initiatePaymentRequestAgain);
                    }

                    if("Facilero".equalsIgnoreCase(toType)){
                        facileroLogger.error("FlutterWave Sale initiatePaymentResponseAgain --for--"+trackingID+"--" + initiatePaymentResponseAgain);
                    }else{
                        transactionLogger.error("Sale initiatePaymentResponseAgain --for--"+trackingID+"--" + initiatePaymentResponseAgain);
                    }
                    String responceAgainAuthurl             = "";
                    String responceAgain_chargeResponseCode = "";
                    String responceAgain_status             = "";
                    String responceAgain_authModelUsed          = "";
                    String responceAgain_chargeResponseMessage  = "";
                    String responceAgain_paymentType            = "";
                    String responceAgain_createdAt      = "";
                    String responseAgainflwRef          = "";
                    String responceAgain_DataStatus     = "";
                    String responseAgainflwRef_error    = "";

                    if (initiatePaymentResponseAgain!=null)
                    {
                        if(!initiatePaymentResponseAgain.contains("{"))
                        {
                            commResponseVO.setStatus("pending");
                            commResponseVO.setRemark("Transaction is pending");
                            commResponseVO.setDescription("Transaction is pending");
                            return commResponseVO;
                        }
                         jsonObject             = new JSONObject(initiatePaymentResponseAgain);
                        JSONObject jsonObject1  = jsonObject.getJSONObject("data");
                        JSONObject jsonObject2  = null;
                        if (jsonObject1.has("tx"))
                        {
                            jsonObject2 = jsonObject1.getJSONObject("tx");
                        }
                        if (jsonObject != null)
                        {
                            if (jsonObject.has("status"))
                                responseAgainStatus = jsonObject.getString("status");
                            transactionLogger.debug("responseAgainStatus ---" + status);
                            if (jsonObject.has("message"))
                            {
                                responseAgainMessage = jsonObject.getString("message");
                                if(functions.isValueNull(responseAgainMessage) && responseAgainMessage.contains(":") && !initiatePayment_Message.contains("FAILURE"))
                                    responseAgainMessage=responseAgainMessage.split(":")[0];
                            }
                            transactionLogger.debug("responseAgainMessage ---" + message);
                            if (jsonObject1.has("flwRef"))
                                responseAgainflwRef = jsonObject1.getString("flwRef");
                            transactionLogger.debug("responseAgainflwRef ---" + responseAgainflwRef);
                            if (jsonObject1.has("authurl"))
                                responceAgainAuthurl = jsonObject1.getString("authurl");
                            transactionLogger.debug("responceAgainAuthurl  ----" + responceAgainAuthurl);
                            if (jsonObject1.has("chargeResponseCode"))
                                responceAgain_chargeResponseCode = jsonObject1.getString("chargeResponseCode");
                            transactionLogger.debug("responceAgain_chargeResponseCode  ----" + responceAgain_chargeResponseCode);
                            if (jsonObject1.has("status"))
                                responceAgain_status = jsonObject1.getString("status");
                            transactionLogger.debug("responceAgain_status  ----" + responceAgain_status);
                            if (jsonObject1.has("authModelUsed"))
                                responceAgain_authModelUsed = jsonObject1.getString("authModelUsed");
                            transactionLogger.debug("responceAgain_authModelUsed  ----" + responceAgain_authModelUsed);
                            if (jsonObject1.has("chargeResponseMessage"))
                            {
                                responceAgain_chargeResponseMessage = jsonObject1.getString("chargeResponseMessage");
                                transactionLogger.debug("responceAgain_chargeResponseMessage  ----" + responceAgain_chargeResponseMessage);
                                if(functions.isValueNull(responceAgain_chargeResponseMessage) && responceAgain_chargeResponseMessage.contains(":") && !initiatePayment_Message.contains("FAILURE"))
                                    responceAgain_chargeResponseMessage=responceAgain_chargeResponseMessage.split(":")[0];
                            }
                            if (jsonObject1.has("paymentType"))
                                responceAgain_paymentType = jsonObject1.getString("paymentType");
                            transactionLogger.debug("responceAgain_paymentType  ----" + responceAgain_paymentType);
                            if (jsonObject1.has("createdAt"))
                                responceAgain_createdAt = jsonObject1.getString("createdAt");
                            transactionLogger.debug("responceAgain_createdAt  ----" + responceAgain_createdAt);
                            if (jsonObject1.has("status"))
                                responceAgain_DataStatus = jsonObject1.getString("status");
                            transactionLogger.debug("responceAgain_DataStatus  ----" + responceAgain_DataStatus);
                            if (jsonObject2!=null)
                            {
                                if (jsonObject2.has("flwRef"))
                                    responseAgainflwRef_error = jsonObject2.getString("flwRef");
                                transactionLogger.debug("responseAgainflwRef_error ---" + responseAgainflwRef_error);
                            }
                        }
                        if (functions.isValueNull(responceAgain_authModelUsed) && !responceAgain_status.equals("error"))
                        {
                            // Step 6:  Validate the payment by using OPT  if authModelUsed IS PIN ###########
                            if (responceAgain_authModelUsed.equalsIgnoreCase("PIN"))
                            {
                                String opt = "";
                                String validateRequest = "{\n" +
                                        "  \"PBFPubKey\": \"" + publicKey + "\",\n" +  //public Key
                                        "  \"transaction_reference\": \"" + responseAgainflwRef + "\",\n" +
                                        "  \"otp\": \"" + opt + "\"\n" +
                                        "}";
                                if("Facilero".equalsIgnoreCase(toType)){
                                    facileroLogger.error("FlutterWave Sale validateRequest --for--"+trackingID+"--" + validateRequest);
                                }else{
                                    transactionLogger.error("Sale validateRequest --for--"+trackingID+"--" + validateRequest);
                                }


                                String validateResponse = "";
                                if (isTest)
                                {
                                    transactionLogger.error("inside isTest-----" + RB.getString("TEST_VALIDATE_URL"));
                                    validateResponse = flutterWaveUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_VALIDATE_URL"), validateRequest);
                                }
                                else
                                {
                                    transactionLogger.error("inside isLive-----" + RB.getString("LIVE_VALIDATE_URL"));
                                    validateResponse = flutterWaveUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_VALIDATE_URL"), validateRequest);
                                }
                                if("Facilero".equalsIgnoreCase(toType)){
                                    facileroLogger.error("FlutterWave Sale validateResponse --for--"+trackingID+"--" + validateResponse);
                                }else{
                                    transactionLogger.error("Sale validateResponse --for--"+trackingID+"--" + validateResponse);
                                }


                                if (validateResponse != null)
                                {
                                    if(!validateResponse.contains("{"))
                                    {
                                        commResponseVO.setStatus("pending");
                                        commResponseVO.setRemark("Transaction is pending");
                                        commResponseVO.setDescription("Transaction is pending");
                                        return commResponseVO;
                                    }
                                    jsonObject = new JSONObject(validateResponse);
                                    if (jsonObject != null)
                                    {
                                        if (jsonObject.has("status"))
                                        {
                                            validateStatus = jsonObject.getString("status");
                                            transactionLogger.debug("validateResponse ---" + validateResponse);
                                        }
                                        jsonObject1 = jsonObject.getJSONObject("data");
                                        jsonObject2 = jsonObject1.getJSONObject("tx");
                                        if (jsonObject2.has("txRef"))
                                        {
                                            validateTxRef = jsonObject2.getString("txRef");
                                            transactionLogger.debug("validateTxRef ---" + validateTxRef);
                                        }
                                    }
                                }
                                //Step 7: Last Step:  Payment verification.   ##########################

                                String paymentVerificationRequest = "{\n" +
                                        "  \"txref\": \"" + validateTxRef + "\",\n" +  //public Key
                                        "  \"SECKEY\": \"" + secretKey + "\"\n" +
                                        "}";
                                if("Facilero".equalsIgnoreCase(toType)){
                                    facileroLogger.error("FlutterWave Sale paymentVerificationRequest --for--"+trackingID+"--" + paymentVerificationRequest);
                                }else{

                                    transactionLogger.error("Sale paymentVerificationRequest --for--"+trackingID+"--" + paymentVerificationRequest);
                                }

                                String paymentVerificationResponse = "";
                                if (isTest)
                                {
                                    transactionLogger.error("inside isTest-----" + RB.getString("TEST_PAYMENT_VERIFICATION_URL"));
                                    paymentVerificationResponse = flutterWaveUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_PAYMENT_VERIFICATION_URL"), paymentVerificationRequest);
                                }
                                else
                                {
                                    transactionLogger.error("inside isLive-----" + RB.getString("LIVE_PAYMENT_VERIFICATION_URL"));
                                    paymentVerificationResponse = flutterWaveUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_PAYMENT_VERIFICATION_URL"), paymentVerificationRequest);
                                }

                                if("Facilero".equalsIgnoreCase(toType)){
                                    facileroLogger.error("FlutterWave Sale paymentVerificationResponse --for--"+trackingID+"--" + paymentVerificationResponse);
                                }else{
                                    transactionLogger.error("Sale paymentVerificationResponse --for--"+trackingID+"--" + paymentVerificationResponse);
                                }


                                String paymentVerification_chargedamount    = "";
                                String paymentVerification_vbvmessage       = "";
                                String paymentVerification_flwRef           = "";
                                if (paymentVerificationResponse != null)
                                {
                                    if(!paymentVerificationResponse.contains("{"))
                                    {
                                        commResponseVO.setStatus("pending");
                                        commResponseVO.setRemark("Transaction is pending");
                                        commResponseVO.setDescription("Transaction is pending");
                                        return commResponseVO;
                                    }
                                    jsonObject  = new JSONObject(paymentVerificationResponse);
                                    if (jsonObject != null)
                                    {
                                        jsonObject1                         = jsonObject.getJSONObject("data");
                                        paymentVerification_chargedamount   = jsonObject1.getString("chargedamount");
                                        paymentVerification_vbvmessage      = jsonObject1.getString("vbvmessage");
                                        if(functions.isValueNull(paymentVerification_vbvmessage) && paymentVerification_vbvmessage.contains(":") && !initiatePayment_Message.contains("FAILURE"))
                                            paymentVerification_vbvmessage  = paymentVerification_vbvmessage.split(":")[0];
                                        paymentVerification_flwRef  = jsonObject1.getString("flwRef");
                                        transactionLogger.debug("paymentVerification_chargedamount -------------" + paymentVerification_chargedamount);
                                        transactionLogger.debug("paymentVerification_vbvmessage -------------" + paymentVerification_vbvmessage);
                                        transactionLogger.debug("paymentVerification_flwRef -------------" + paymentVerification_flwRef);
                                    }
                                }

                                if (functions.isValueNull(paymentVerification_chargedamount) && paymentVerification_vbvmessage.equalsIgnoreCase("successful"))
                                {
                                    commResponseVO.setStatus("success");
                                    commResponseVO.setDescription(paymentVerification_vbvmessage);
                                    commResponseVO.setRemark(paymentVerification_vbvmessage);
                                    commResponseVO.setTransactionId(paymentVerification_flwRef);
                                }
                                else
                                {
                                    commResponseVO.setStatus("failed");
                                    commResponseVO.setDescription(paymentVerification_vbvmessage);
                                    commResponseVO.setRemark(paymentVerification_vbvmessage);
                                }
                            }
                            else
                            {
                                transactionLogger.error("responceAgain_authModelUsed" + responceAgain_authModelUsed);
                                transactionLogger.error(" auth url " + responceAgainAuthurl);
                                commResponseVO.setStatus("pending3DConfirmation");
                                commResponseVO.setUrlFor3DRedirect(responceAgainAuthurl);
                                commResponseVO.setTerURL(termUrl + trackingID);
                                commResponseVO.setDescription("SYS:3D Authentication Pending");
                                commResponseVO.setResponseTime(responceAgain_createdAt);
                                commResponseVO.setTransactionId(responseAgainflwRef);
                                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                                commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                                commResponseVO.setRemark(responceAgain_DataStatus);
                                if("Y".equalsIgnoreCase(isThreadAllowed))
                                    new AsyncFlutterwaveQueryService(trackingID,accountId,responseAgainflwRef,"Y",CALL_EXECUTE_AFTER,CALL_EXECUTE_INTERVAL,MAX_EXECUTE_SEC);
                            }
                        }
                        else
                        {
                            transactionLogger.error("No Suggersted auth bcz of error");
                            commResponseVO.setStatus("failed");
                            commResponseVO.setRemark(responseAgainMessage);
                            //commResponseVO.setDescription(responseAgainStatus + "/" + responseAgainMessage);
                            commResponseVO.setDescription(responseAgainMessage);
                            commResponseVO.setTransactionId(responseAgainflwRef_error);
                            commResponseVO.setErrorCode(responceAgain_chargeResponseCode);
                        }

                    }

                }
            }
            else
            {
                transactionLogger.error("initiatePaymentResponse is null");
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("No initiatePaymentResponse");
                commResponseVO.setDescription("No initiatePaymentResponse");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("Sale JSONException --for--"+trackingID+"--",e);
            PZExceptionHandler.raiseTechnicalViolationException(FlutterWavePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("Sale PZDBViolationException --for--"+trackingID+"--",e);
            PZExceptionHandler.raiseTechnicalViolationException(FlutterWavePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return  commResponseVO;
    }

    @Override
    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error(" ---- Autoredict in flutterwave  ");
        FlutterWavePaymentProcess flutterWavePaymentProcess = new FlutterWavePaymentProcess();
        String html = "";
        Comm3DResponseVO transRespDetails = null;

        FlutterWaveUtils flutterWaveUtils   = new FlutterWaveUtils();
            String paymentMode              = commonValidatorVO.getPaymentMode();

        CommRequestVO commRequestVO     = flutterWaveUtils.getFlutterPaymentRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        try
        {
            transactionLogger.error("tracking id  is -----" + commonValidatorVO.getTrackingid());

            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {

                html = flutterWavePaymentProcess.get3DConfirmationForm(commonValidatorVO.getTrackingid(),"", transRespDetails);
                transactionLogger.error("automatic redirect flutterwave form -- >>" + html);
            }
            else{
               html=transRespDetails.getStatus();
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException in flutterwave---", e);
        }
        return html;
    }


    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error(":::::Enter into processRefund:::::");
        Functions functions                 = new Functions();
        FlutterWaveUtils flutterWaveUtils   = new FlutterWaveUtils();
        CommRequestVO commRequestVO         = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO       = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO            = commRequestVO.getAddressDetailsVO();
        // String Amount=SecureTradingUtils.getAmount(commTransactionDetailsVO.getAmount());
        String ref          = commTransactionDetailsVO.getPreviousTransactionId();
        transactionLogger.debug("PreviousTransactionId ref  ---"+commTransactionDetailsVO.getPreviousTransactionId());
        String toRefund     = commTransactionDetailsVO.getAmount();
        transactionLogger.debug("toRefund -------"+toRefund);


        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                  = gatewayAccount.isTest();
        String member_id                = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String secretKey                = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String currency         = "";
        String tmpl_amount      = "";
        String tmpl_currency    = "";

        String toType      = "";
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency    = commTransactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency = commTransactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }

        if(functions.isValueNull(commTransactionDetailsVO.getTotype())){
            toType = commTransactionDetailsVO.getTotype();
        }
        transactionLogger.error("toType --for--> "+trackingID+" --> "+toType);

        String refundRequest="{\n" +
                "  \"ref\": \""+ref+"\",\n" +
                "  \"seckey\": \""+secretKey+"\",\n"+
                "  \"amount\": \""+toRefund+"\"\n"+
                "}";
        if("Facilero".equalsIgnoreCase(toType)){
            facileroLogger.error("FlutterWave Refund Request --for--> "+trackingID+" --> "+refundRequest);
        }else{
            transactionLogger.error("refundRequest --for--> "+trackingID+" --> "+refundRequest);
        }


        String refundResponce   = "";
        if(isTest)
        {
            transactionLogger.error("inside isTest-----" + RB.getString("TEST_REFUND_URL"));
            refundResponce = flutterWaveUtils.doPostHTTPSURLConnectionClient( RB.getString("TEST_REFUND_URL"), refundRequest);
        }
        else
        {
            transactionLogger.error("inside isLive-----" + RB.getString("LIVE_REFUND_URL"));
            refundResponce = flutterWaveUtils.doPostHTTPSURLConnectionClient( RB.getString("LIVE_REFUND_URL"), refundRequest);
        }
        if("Facilero".equalsIgnoreCase(toType)){
            facileroLogger.error("FlutterWave Refund Response --for-- > "+trackingID+" -- "+refundResponce);
        }else{
            transactionLogger.error("refundResponce --for--"+trackingID+"--"+refundResponce);
        }


        try
        {
            if (refundResponce != null)
            {
                String refundStatus             = "";
                String refundErrorData          = "";
                String refundMessage            = "";
                String refundSuccess_PaymentId  = "";
                String refundSuccess_DataStatus = "";
                String createdAt                = "";
                String FlwRef                   = "";
                JSONObject jsonObject   = new JSONObject(refundResponce);
                //JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                if (jsonObject.has("status"))
                {
                    refundStatus = jsonObject.getString("status");
                    transactionLogger.debug("refundStatus ----" + refundStatus);
                }
                if (jsonObject.has("data"))
                {
                    refundErrorData = jsonObject.getString("data");
                    transactionLogger.debug("refundErrorData ---"+refundErrorData);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    if (jsonObject1.has("FlwRef"))
                    {
                        refundSuccess_PaymentId = jsonObject1.getString("FlwRef");
                    }
                    if(jsonObject1.has("status"))
                    {
                        refundSuccess_DataStatus = jsonObject1.getString("status");
                        if(functions.isValueNull(refundSuccess_DataStatus) && refundSuccess_DataStatus.contains(":"))
                            refundSuccess_DataStatus=refundSuccess_DataStatus.split(":")[0];
                    }
                    if (jsonObject1.has("createdAt"))
                    {
                        createdAt = jsonObject1.getString("createdAt");
                    }
                    if (jsonObject1.has("FlwRef"))
                    {
                        FlwRef = jsonObject1.getString("FlwRef");
                    }
                    transactionLogger.debug("refundSuccess_PaymentId ---" + refundSuccess_PaymentId);
                    transactionLogger.debug("refundSuccess_DataStatus ---" + refundSuccess_DataStatus);
                    transactionLogger.debug("createdAt ---" + createdAt);
                    transactionLogger.debug("FlwRef ---" + FlwRef);

                    if (refundStatus.equalsIgnoreCase("success"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescription(refundSuccess_DataStatus);
                        commResponseVO.setRemark(refundStatus+"/"+refundSuccess_DataStatus);
                        commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                        commResponseVO.setBankTransactionDate(createdAt);
                        commResponseVO.setTransactionId(FlwRef);
                    }
                    else
                    {
                        commResponseVO.setStatus("waitforreversal");
                        commResponseVO.setDescription(refundErrorData);
                        commResponseVO.setRemark(refundErrorData);
                    }
                }
            }
            else
            {
                transactionLogger.error("No refundResponce");
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("No refundResponce");
                commResponseVO.setDescription("No refundResponce");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("Refund JSONException --for--"+trackingID+"--",e);
        }

        return commResponseVO;
    }

    public GenericResponseVO processInquiry( GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        transactionLogger.error(" Entering into processQuery ---");
        CommRequestVO commRequestVO     = (CommRequestVO) requestVO;
        Functions functions             = new Functions();
        CommResponseVO commResponseVO   = new CommResponseVO();
        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        FlutterWaveUtils flutterWaveUtils                   = new FlutterWaveUtils();
        String secretKey    = gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest      = gatewayAccount.isTest();
        String totype       = "";

        String txref = commTransactionDetailsVO.getOrderId();

        String paymentVerificationstatus            = "";
        String paymentVerification_chargedamount    = "";
        String paymentVerification_vbvmessage       = "";
        String paymentVerification_amount           = "";
        String paymentVerification_currency         = "";
        String paymentVerification_created          = "";
        String paymentVerification_flwref           = "";
        String paymentVerification_chargecode       = "";
        String paymentVerification_Message          = "";
        String paymentVerification_DataStatus       = "";
        try
        {
            if(functions.isValueNull(commTransactionDetailsVO.getTotype())){
                totype = commTransactionDetailsVO.getTotype();
            }
            transactionLogger.error("totype --> " + txref + " " + totype);

            String inquiryRequest = "{\n" +
                    "  \"txref\": \"" + txref + "\",\n" +
                    "  \"SECKEY\": \"" + secretKey + "\"\n" +
                    "}";
            if("Facilero".equalsIgnoreCase(totype)){
                facileroLogger.error("FlutterWave Inquiry Request --for--> "+txref+"--" + inquiryRequest);
            }else{
                transactionLogger.error("inquiryRequest --for--> "+txref+"--" + inquiryRequest);
            }


            String inquiryResponse = "";
            if (isTest)
            {
                transactionLogger.error("inside isTest-----" + RB.getString("TEST_PAYMENT_VERIFICATION_URL"));
                inquiryResponse = flutterWaveUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_PAYMENT_VERIFICATION_URL"), inquiryRequest);
            }
            else
            {
                transactionLogger.error("inside isLive-----" + RB.getString("LIVE_PAYMENT_VERIFICATION_URL"));
                inquiryResponse = flutterWaveUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_PAYMENT_VERIFICATION_URL"), inquiryRequest);
            }
            if("Facilero".equalsIgnoreCase(totype)){
                facileroLogger.error("FlutterWave Inquiry Response --for--> "+txref+" -- " + inquiryResponse);
            }else{
                transactionLogger.error("inquiryResponse --for--> "+txref+" -- " + inquiryResponse);
            }


            if (inquiryResponse != null)
            {
                JSONObject jsonObject = new JSONObject(inquiryResponse);
                if (jsonObject != null)
                {
                    if (jsonObject.has("status"))
                    {
                        paymentVerificationstatus = jsonObject.getString("status");
                        transactionLogger.error("paymentVerificationstatus ---" + paymentVerificationstatus);
                    }
                    if (jsonObject.has("message"))
                    {
                        paymentVerification_Message = jsonObject.getString("message");
                        transactionLogger.error("paymentVerification_Message ---" + paymentVerification_Message);
                        if(functions.isValueNull(paymentVerification_Message) && paymentVerification_Message.contains(":"))
                            paymentVerification_Message = paymentVerification_Message.split(":")[0];
                    }
                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    if (jsonObject1.has("flwref"))
                    {
                        paymentVerification_flwref = jsonObject1.getString("flwref");
                    }
                    else
                    {
                        transactionLogger.debug("no flwref-------");
                    }
                    if (jsonObject1.has("amount"))
                    {
                        paymentVerification_amount = jsonObject1.getString("amount");
                    }
                    if (jsonObject1.has("currency"))
                    {
                        paymentVerification_currency = jsonObject1.getString("currency");
                    }
                    if (jsonObject1.has("chargedamount"))
                    {
                        paymentVerification_chargedamount = jsonObject1.getString("chargedamount");
                    }
                    if (jsonObject1.has("chargecode"))
                    {
                        paymentVerification_chargecode = jsonObject1.getString("chargecode");
                    }
                    if (jsonObject1.has("status"))
                    {
                        paymentVerification_DataStatus = jsonObject1.getString("status");
                    }
                    if (jsonObject1.has("vbvmessage"))
                    {
                        paymentVerification_vbvmessage = jsonObject1.getString("vbvmessage");
                        if(functions.isValueNull(paymentVerification_vbvmessage) && paymentVerification_vbvmessage.contains(":") && !paymentVerification_vbvmessage.contains("FAILURE"))
                            paymentVerification_vbvmessage=paymentVerification_vbvmessage.split(":")[0];
                    }
                    if (jsonObject1.has("created"))
                    {
                        paymentVerification_created = jsonObject1.getString("created");
                    }
                    transactionLogger.debug("paymentVerification_chargedamount -----" + paymentVerification_chargedamount);
                    transactionLogger.debug("paymentVerification_vbvmessage ----" + paymentVerification_vbvmessage);
                    transactionLogger.debug("paymentVerification_amount ----" + paymentVerification_amount);
                    transactionLogger.debug("paymentVerification_currency ----" + paymentVerification_currency);
                    transactionLogger.debug("paymentVerification_created ----" + paymentVerification_created);
                    transactionLogger.debug("paymentVerification_flwref ----" + paymentVerification_flwref);
                    transactionLogger.debug("paymentVerification_chargecode ----" + paymentVerification_chargecode);
                    transactionLogger.debug("paymentVerification_DataStatus ----" + paymentVerification_DataStatus);
                }
               /* if (paymentVerificationstatus.equalsIgnoreCase("error")) // As we are getting only paymentVerificationstatus in response we have to add this condition
                {
                    transactionLogger.error("Inside error of inquiry ---");
                    commResponseVO.setStatus("fail");
                    commResponseVO.setDescription(paymentVerification_Message);
                }*/
                if (paymentVerificationstatus.equalsIgnoreCase("success") && "00".equals(paymentVerification_chargecode))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setDescription(paymentVerification_vbvmessage);
                    commResponseVO.setRemark(paymentVerification_DataStatus);
                }else if(paymentVerificationstatus.equalsIgnoreCase("success") && "failed".equalsIgnoreCase(paymentVerification_DataStatus)){
                    transactionLogger.error("Inside else  If failed condition of inquiry ---");
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setDescription(paymentVerification_Message);
                    commResponseVO.setRemark(paymentVerification_DataStatus);
                }
                else if (paymentVerificationstatus.equalsIgnoreCase("pending"))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setDescription(paymentVerification_vbvmessage);
                    commResponseVO.setRemark(paymentVerification_DataStatus);
                }
                else if (paymentVerificationstatus.equalsIgnoreCase("success") && "02".equals(paymentVerification_chargecode))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setDescription(paymentVerification_vbvmessage);
                    commResponseVO.setRemark(paymentVerification_DataStatus);
                }
                else if (paymentVerificationstatus.equalsIgnoreCase("Error"))
                {
                    transactionLogger.error("Inside else  If of inquiry ---");
                    commResponseVO.setStatus("fail");
                    commResponseVO.setTransactionStatus("fail");
                    commResponseVO.setDescription(paymentVerification_Message);
                    commResponseVO.setRemark(paymentVerification_DataStatus);
                }
                else
                {
                    transactionLogger.error("Inside else of inquiry ---");
                    commResponseVO.setStatus("fail");
                    commResponseVO.setTransactionStatus("fail");
                    commResponseVO.setDescription(paymentVerification_vbvmessage);
                    commResponseVO.setRemark(paymentVerification_DataStatus);
                }

                commResponseVO.setAmount(paymentVerification_amount);
                // commResponseVO.setStatus(settlestatus);
                commResponseVO.setResponseTime(paymentVerification_created);
                commResponseVO.setMerchantId(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
                commResponseVO.setTransactionId(paymentVerification_flwref);
                commResponseVO.setAuthCode(paymentVerification_chargecode);
                commResponseVO.setBankTransactionDate(paymentVerification_created);
                commResponseVO.setCurrency(paymentVerification_currency);
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
            }
            else
            {
                transactionLogger.error("No inquiryResponse");
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("No inquiryResponse");
                commResponseVO.setDescription("No inquiryResponse");
            }
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("Inquiry PZTechnicalViolationException--for--"+txref+"--",e);
        }
        catch (JSONException e)
        {
            transactionLogger.error("Inquiry JSONException--for--" + txref + "--",e);
        }
        return commResponseVO;
    }



    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint   = new PZGenericConstraint("FlutterWavePaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by FlutterWave.Please contact your Tech. support Team:::",null);
    }

    public GenericResponseVO processCommon3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException, PZGenericConstraintViolationException
    {
        transactionLogger.error("inside processCommon3DSaleConfirmation -----");

        FlutterWaveUtils flutterWaveUtils   = new FlutterWaveUtils();
        Functions functions                 = new Functions();
        // Comm3DResponseVO comm3DResponseVO =  new Comm3DResponseVO();
        CommRequestVO commRequestVO     = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO   = new CommResponseVO();
        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                  = gatewayAccount.isTest();
        String userName                 = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password                 = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        String toType = "";
        try
        {
            if(functions.isValueNull(commTransactionDetailsVO.getTotype())){
                toType = commTransactionDetailsVO.getTotype();
            }

            String paymentVerificationRequest="{\n" +
                    "  \"txref\": \""+trackingID+"\",\n" +
                    "  \"SECKEY\": \""+password+"\"\n" +
                    "}";

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("FlutterWave processCommon3DSaleConfirmation paymentVerificationRequest ---> " + trackingID + " --> " + paymentVerificationRequest);
            }else{
                transactionLogger.error("processCommon3DSaleConfirmation paymentVerificationRequest --for-- " + trackingID + "--" + paymentVerificationRequest);
            }

            String paymentVerificationResponse="";
            if(isTest)
            {
                transactionLogger.error("inside isTest-----" + RB.getString("TEST_PAYMENT_VERIFICATION_URL"));
                paymentVerificationResponse = flutterWaveUtils.doPostHTTPSURLConnectionClient( RB.getString("TEST_PAYMENT_VERIFICATION_URL"), paymentVerificationRequest);
            }
            else
            {
                transactionLogger.error("inside isLive-----" + RB.getString("LIVE_PAYMENT_VERIFICATION_URL"));
                paymentVerificationResponse = flutterWaveUtils.doPostHTTPSURLConnectionClient( RB.getString("LIVE_PAYMENT_VERIFICATION_URL"), paymentVerificationRequest);
            }
            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("FlutterWave processCommon3DSaleConfirmation paymentVerificationResponse ---> " + trackingID + " ---> " +paymentVerificationResponse);
            }else{
                transactionLogger.error("processCommon3DSaleConfirmation paymentVerificationResponse --for-- " + trackingID + "--" +paymentVerificationResponse);
            }


            String paymentVerification_chargedamount    ="";
            String paymentVerification_vbvmessage       ="";
            String paymentVerification_amount           ="";
            String paymentVerification_currency         ="";
            String paymentVerification_created          ="";
            String paymentVerification_flwref           ="";
            String paymentVerification_chargecode       ="";
            if (paymentVerificationResponse != null)
            {
                try
                {
                    String paymentVerificationstatus    ="";
                    String paymentVerification_Message  ="";
                    String paymentVerification_txref    ="";
                    String paymentVerification_status   ="";
                    JSONObject jsonObject = new JSONObject(paymentVerificationResponse);
                    if (jsonObject!=null)
                    {
                        if (jsonObject.has("status"))
                        {
                            paymentVerificationstatus=jsonObject.getString("status");
                            transactionLogger.error("paymentVerificationstatus ---"+paymentVerificationstatus);
                        }
                        if (jsonObject.has("message"))
                        {
                            paymentVerification_Message=jsonObject.getString("message");
                            transactionLogger.error("paymentVerification_Message ---"+paymentVerification_Message);
                        }
                        JSONObject jsonObject1           = jsonObject.getJSONObject("data");
                        paymentVerification_txref        = jsonObject1.getString("txref");
                        paymentVerification_flwref       = jsonObject1.getString("flwref");
                        paymentVerification_amount       = jsonObject1.getString("amount");
                        paymentVerification_currency     = jsonObject1.getString("currency");
                        paymentVerification_chargecode   = jsonObject1.getString("chargecode");
                        paymentVerification_chargedamount =jsonObject1.getString("chargedamount");
                        paymentVerification_vbvmessage   = jsonObject1.getString("vbvmessage");
                        paymentVerification_created      = jsonObject1.getString("created");
                        paymentVerification_status       = jsonObject1.getString("status");

                        transactionLogger.debug("paymentVerification_chargedamount -----"+paymentVerification_chargedamount);
                        transactionLogger.error("paymentVerification_vbvmessage ----"+paymentVerification_vbvmessage);
                        transactionLogger.debug("paymentVerification_amount ----"+paymentVerification_amount);
                        transactionLogger.debug("paymentVerification_currency ----"+paymentVerification_currency);
                        transactionLogger.debug("paymentVerification_created ----"+paymentVerification_created);
                        transactionLogger.debug("paymentVerification_flwref ----"+paymentVerification_flwref);
                        transactionLogger.error("paymentVerification_chargecode ----"+paymentVerification_chargecode);
                        transactionLogger.debug("paymentVerification_txref ----"+paymentVerification_txref);
                        transactionLogger.error("paymentVerification_status ----"+paymentVerification_status);
                        if(functions.isValueNull(paymentVerification_vbvmessage) && paymentVerification_vbvmessage.contains(":"))
                            paymentVerification_vbvmessage  = paymentVerification_vbvmessage.split(":")[0];
                    }
                    if (paymentVerification_status.equalsIgnoreCase("successful") && paymentVerification_chargecode.equals("00") )
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        //commResponseVO.setDescription(paymentVerification_status+" / "+paymentVerification_vbvmessage);
                        commResponseVO.setDescription(paymentVerification_vbvmessage);
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setDescription(paymentVerification_vbvmessage);
                    }
                    //commResponseVO.setAmount(paymentVerification_amount);
                    commResponseVO.setRemark(paymentVerification_vbvmessage);
                    commResponseVO.setCurrency(paymentVerification_currency);
                    commResponseVO.setBankTransactionDate(paymentVerification_created);
                    commResponseVO.setTransactionId(paymentVerification_flwref);
                    commResponseVO.setEci("-");
                    commResponseVO.setErrorCode(paymentVerification_chargecode);
                    commResponseVO.setTransactionStatus(paymentVerification_status);
                }
                catch (JSONException e)
                {
                    transactionLogger.error("processCommon3DSaleConfirmation JSONException--for--" + trackingID + "--",e);
                    PZExceptionHandler.raiseTechnicalViolationException(FlutterWavePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
                }
            }
            else
            {
                transactionLogger.error("No paymentVerificationResponse");
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("No paymentVerificationResponse");
                commResponseVO.setDescription("No paymentVerificationResponse");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("processCommon3DSaleConfirmation Exception--for--" + trackingID + "--",e);
        }

        return commResponseVO;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
