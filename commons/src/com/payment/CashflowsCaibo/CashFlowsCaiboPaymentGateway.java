package com.payment.CashflowsCaibo;


import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;

import com.directi.pg.core.valueObjects.*;

import com.payment.common.core.*;
import com.payment.endeavourmpi.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

/**N
 * Created by admin on 16-Sep-21.
 */
public class CashFlowsCaiboPaymentGateway extends AbstractPaymentGateway
{

    public static final String GATEWAY_TYPE     = "CFCaibo";
    private final static ResourceBundle RB      = LoadProperties.getProperty("com.directi.pg.CashflowsCaibo");

    private static TransactionLogger transactionLogger = new TransactionLogger(CashFlowsCaiboPaymentGateway.class.getName());

    public CashFlowsCaiboPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public GenericResponseVO processInitialSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("inside processInitialSale(Verify3D) --->");

        CommRequestVO commRequestVO                 = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO             = new Comm3DResponseVO();
        Functions functions                         = new Functions();
        CommTransactionDetailsVO transDetailsVO     = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO   = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount               = GatewayAccountService.getGatewayAccount(accountId);

        String MerchantId      = gatewayAccount.getMerchantId();
        String ApiKey          = gatewayAccount.getFRAUD_FTP_USERNAME();
        String SecurityToken   = gatewayAccount.getFRAUD_FTP_PASSWORD();

        boolean isTest                      = gatewayAccount.isTest();
        JSONObject requestParameter         = new JSONObject();
        JSONObject requestBodyParameter     = new JSONObject();
        JSONObject requestParameterLOG      = new JSONObject();
        JSONObject requestBodyParameterLOG  = new JSONObject();
        HashMap<String ,String> requestHM   = new HashMap<>();

        String Version           = "1.1";
        String TestMode          = "Live";
        String PaymentType       = "Verify";
        String REQUEST_URL       = "";
        String OrderReference    = "";
        String Amount            = "";
        String Currency          = "";
        String CardNumber        = "";
        String ExpiryDateMonth   = "";
        String ExpiryDateYear    = "";
        String cvv               = "";
        String TermUrl           = "";
        String dob               = "";

        try
        {
            OrderReference    = trackingID;
            Amount            = transDetailsVO.getAmount();
            Currency          = transDetailsVO.getCurrency();
            CardNumber        = cardDetailsVO.getCardNum();
            ExpiryDateMonth   = cardDetailsVO.getExpMonth();
            ExpiryDateYear    = CashFlowsCaiboPaymentGatewayUtils.getLast2DigitOfExpiryYear(cardDetailsVO.getExpYear());
            cvv               = cardDetailsVO.getcVV();
            dob               = commAddressDetailsVO.getBirthdate();

            if (isTest)
            {
                REQUEST_URL = RB.getString("3D_VERIFY_TEST_URL");
                TermUrl     = RB.getString("ACS_TERM_URL")+ trackingID + "&DM="+ PaymentzEncryptor.encryptCVV(cvv) + "&DB="+ PaymentzEncryptor.encryptName(dob);
            }
            else
            {
                REQUEST_URL = RB.getString("3D_VERIFY_LIVE_URL");
                TermUrl     = RB.getString("ACS_TERM_URL")+ trackingID + "&DM="+ PaymentzEncryptor.encryptCVV(cvv) + "&DB="+ PaymentzEncryptor.encryptName(dob);
            }
            transactionLogger.error("processInitialSale REQUEST_URL ---> " +REQUEST_URL);
            transactionLogger.error("processInitialSale TermUrl -----> " +TermUrl);

            // requestParameter
            requestParameter.put("MerchantId", MerchantId);
            requestParameter.put("OrderReference",OrderReference);
            requestParameter.put("TestMode",TestMode);
            requestParameter.put("Amount",Amount);
            requestParameter.put("Currency",Currency);
            requestParameter.put("CardNumber",CardNumber);
            requestParameter.put("ExpiryDateMonth",ExpiryDateMonth);
            requestParameter.put("ExpiryDateYear",ExpiryDateYear);
            requestParameter.put("PaymentType",PaymentType);

            String Signature = CashFlowsCaiboPaymentGatewayUtils.generateSHA512(SecurityToken + requestParameter.toString().substring(1, requestParameter.toString().length() - 1));
            //RequestBody
            requestBodyParameter.put("ApiKey", ApiKey);
            requestBodyParameter.put("Signature", Signature);
            requestBodyParameter.put("Request", requestParameter);
            requestBodyParameter.put("Version", Version);

            // requestParameterLOG
            requestParameterLOG.put("MerchantId",MerchantId);
            requestParameterLOG.put("OrderReference",OrderReference);
            requestParameterLOG.put("TestMode",TestMode);
            requestParameterLOG.put("Amount",Amount);
            requestParameterLOG.put("Currency",Currency);
            requestParameterLOG.put("CardNumber",functions.maskingPan(CardNumber));
            requestParameterLOG.put("ExpiryDateMonth",functions.maskingNumber(ExpiryDateMonth));
            requestParameterLOG.put("ExpiryDateYear",functions.maskingNumber(ExpiryDateYear));
            requestParameterLOG.put("PaymentType",PaymentType);
            //RequestBodyLOG
            requestBodyParameterLOG.put("ApiKey", ApiKey);
            requestBodyParameterLOG.put("Signature", Signature);
            requestBodyParameterLOG.put("Request", requestParameterLOG);
            requestBodyParameterLOG.put("Version", Version);
            transactionLogger.error("processInitialSale(Verify3D) RequestParameters ---> "+trackingID +" "+requestBodyParameterLOG.toString());

            String responseString = CashFlowsCaiboPaymentGatewayUtils.doPostHTTPSURLConnectionClient(REQUEST_URL, requestBodyParameter.toString());
            transactionLogger.error("processInitialSale(Verify3D) Response ---"+ trackingID +"-->"+ responseString);

            JSONObject logResp    = new JSONObject();
            if (CashFlowsCaiboPaymentGatewayUtils.isJSONValid(responseString))
            {
                JSONObject respJson   = new JSONObject(responseString);
                JSONArray keys        = respJson.names();
                for (int i = 0; i < keys.length (); i++)
                {
                    logResp.put(keys.getString(i),respJson.getString (keys.getString(i)));
                }
            }
            transactionLogger.error("processInitialSale(Verify3D) Response for grep ---"+ trackingID +"-->"+ logResp);

            StringBuffer error      =  new StringBuffer();
            String EnrolmentStatus  = "";
            String MessageId        = "";
            String PaReq            = "";
            String AcsUrl           = "";
            String Code             = "";

            if(responseString != null && CashFlowsCaiboPaymentGatewayUtils.isJSONValid(responseString))
            {
                JSONObject jsonObject   = new JSONObject(responseString);
                JSONObject responseJSON = null;

                if(jsonObject.has("Response")){
                    responseJSON = jsonObject.getJSONObject("Response");
                }else if(jsonObject.has("Error")){
                    responseJSON = jsonObject.getJSONObject("Error");
                }

                if(responseJSON != null)
                {
                    if(responseJSON.has("EnrolmentStatus") && functions.isValueNull(responseJSON.getString("EnrolmentStatus"))){
                        EnrolmentStatus = responseJSON.getString("EnrolmentStatus");
                    }

                    if(responseJSON.has("MessageId") && functions.isValueNull(responseJSON.getString("MessageId"))){
                        MessageId = responseJSON.getString("MessageId");
                        requestHM.put("Md",MessageId);
                    }

                    if(responseJSON.has("PaReq") && functions.isValueNull(responseJSON.getString("PaReq"))){
                        PaReq = responseJSON.getString("PaReq");
                        requestHM.put("PaReq",PaReq);
                    }

                    if(responseJSON.has("AcsUrl") && functions.isValueNull(responseJSON.getString("AcsUrl"))){
                        AcsUrl = responseJSON.getString("AcsUrl");

                    }
                    if(functions.isValueNull(TermUrl))
                    {
                        requestHM.put("TermUrl",TermUrl);
                    }

                    if (responseJSON.has("Code") && functions.isValueNull(responseJSON.getString("Code"))){
                        Code = responseJSON.getString("Code");
                    }
                    if (responseJSON.has("Details") && functions.isValueNull(String.valueOf(responseJSON.getJSONArray("Details"))))
                    {
                        JSONArray jsonArray = responseJSON.getJSONArray("Details");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject errObject = (JSONObject) jsonArray.get(i);
                            error.append(errObject.getString("Message"));
                        }
                    }


                    if("Y".equalsIgnoreCase(EnrolmentStatus) && functions.isValueNull(AcsUrl))
                    {
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setUrlFor3DRedirect(AcsUrl);
                        commResponseVO.setMd(MessageId);
                        commResponseVO.setPaReq(PaReq);
                        commResponseVO.setTerURL(TermUrl);
                    }  //todo Y means enroll 3d crad, N means non 3d card, U means un-available, but if not enroll then do what, direct sale?
                    else if (functions.isValueNull(error.toString()))
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(error.toString());
                    }
                    else
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CashFlowsCaiboPaymentGateway.class.getName(), "processInitialSale()", null, "common", "Technical Exception while cancelling transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        return commResponseVO;
    }

    public EnrollmentResponseVO processAuthentication(EnrollmentRequestVO enrollmentRequestVO ) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("inside CashFlowsCaiboPaymentGateway processAuthenticateThreeDSecure ---> ");

        GatewayAccount gatewayAccount              = GatewayAccountService.getGatewayAccount(accountId);
        EnrollmentResponseVO enrollmentResponseVO  = new EnrollmentResponseVO();
        Functions functions = new Functions();

        JSONObject jsonRequestParameter         = new JSONObject();
        JSONObject jsonRequestBodyParameter     = new JSONObject();
        JSONObject jsonRequestParameterLOG      = new JSONObject();
        JSONObject jsonRequestBodyParameterLOG  = new JSONObject();
        boolean isTest         = gatewayAccount.isTest();
        String MerchantId      = gatewayAccount.getMerchantId();
        String ApiKey          = gatewayAccount.getFRAUD_FTP_USERNAME();
        String SecurityToken   = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String Version          = "1.1";
        String TestMode         = "Live";
        String REQUEST_URL      = "";
        String Signature        = "";
        String response         = "";
        String Amount           = "";
        String Currency         = "";
        String CardNumber       = "";
        String ExpiryDateMonth  = "";
        String ExpiryDateYear   = "";
        String OrderReference   = "";
        String MessageId        = "";
        String PaRes            = "";

        try
        {
            Amount           = enrollmentRequestVO.getAmount();
            Currency         = enrollmentRequestVO.getCurrency();
            CardNumber       = enrollmentRequestVO.getPan();
            ExpiryDateMonth  = enrollmentRequestVO.getExpiryMonth();
            ExpiryDateYear   = CashFlowsCaiboPaymentGatewayUtils.getLast2DigitOfExpiryYear(enrollmentRequestVO.getExpiryYear());
            OrderReference   = enrollmentRequestVO.getTrackid();
            MessageId        = enrollmentRequestVO.getMessageId();
            PaRes            = enrollmentRequestVO.getPaRes();

            if (isTest)
            {
                REQUEST_URL = RB.getString("AuthenticateThreeDSecure_TEST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("AuthenticateThreeDSecure_LIVE_URL");
            }
            transactionLogger.error("processAuthenticateThreeDSecure REQUEST_URL ---> "+REQUEST_URL);

            //RequestParameter
            jsonRequestParameter.put("MerchantId", MerchantId);
            jsonRequestParameter.put("OrderReference", OrderReference);
            jsonRequestParameter.put("TestMode", TestMode);
            jsonRequestParameter.put("Amount", Amount);
            jsonRequestParameter.put("Currency", Currency);
            jsonRequestParameter.put("CardNumber", CardNumber);
            jsonRequestParameter.put("ExpiryDateMonth", ExpiryDateMonth);
            jsonRequestParameter.put("ExpiryDateYear", ExpiryDateYear);
            jsonRequestParameter.put("MessageId", MessageId);
            jsonRequestParameter.put("PaRes", PaRes);

            Signature = CashFlowsCaiboPaymentGatewayUtils.generateSHA512(SecurityToken + jsonRequestParameter.toString().substring(1, jsonRequestParameter.toString().length() - 1));
            //RequestBody
            jsonRequestBodyParameter.put("ApiKey", ApiKey);
            jsonRequestBodyParameter.put("Signature", Signature);
            jsonRequestBodyParameter.put("Request", jsonRequestParameter);
            jsonRequestBodyParameter.put("Version", Version);

            //RequestParameterLOG
            jsonRequestParameterLOG.put("MerchantId", MerchantId);
            jsonRequestParameterLOG.put("OrderReference", OrderReference);
            jsonRequestParameterLOG.put("TestMode", TestMode);
            jsonRequestParameterLOG.put("Amount", Amount);
            jsonRequestParameterLOG.put("Currency", Currency);
            jsonRequestParameterLOG.put("CardNumber", functions.maskingPan(CardNumber));
            jsonRequestParameterLOG.put("ExpiryDateMonth", functions.maskingNumber(ExpiryDateMonth));
            jsonRequestParameterLOG.put("ExpiryDateYear", functions.maskingNumber(ExpiryDateYear));
            jsonRequestParameterLOG.put("MessageId", MessageId);
            jsonRequestParameterLOG.put("PaRes", PaRes);
            //RequestBodyLOG
            jsonRequestBodyParameterLOG.put("ApiKey", ApiKey);
            jsonRequestBodyParameterLOG.put("Signature", Signature);
            jsonRequestBodyParameterLOG.put("Request", jsonRequestParameterLOG);
            jsonRequestBodyParameterLOG.put("Version", Version);
            transactionLogger.error("processAuthenticateThreeDSecure Parameters ----> " + OrderReference + " " + jsonRequestBodyParameterLOG);

            response = CashFlowsCaiboPaymentGatewayUtils.doPostHTTPSURLConnectionClient(REQUEST_URL, jsonRequestBodyParameter.toString());
            transactionLogger.error("processAuthenticateThreeDSecure Response ---" + OrderReference + "-->" + response);

            JSONObject logResp    = new JSONObject();
            if (CashFlowsCaiboPaymentGatewayUtils.isJSONValid(response))
            {
                JSONObject respJson   = new JSONObject(response);
                JSONArray keys        = respJson.names();
                for (int i = 0; i < keys.length (); i++)
                {
                    logResp.put(keys.getString(i),respJson.getString (keys.getString(i)));
                }
            }
            transactionLogger.error("processAuthenticateThreeDSecure Response for grep ---" + OrderReference + "-->" + logResp);

            StringBuffer error          =  new StringBuffer();
            String AuthenticationStatus = "";
            String Xid                  = "";
            String Cavv                 = "";
            String Eci                  = "";
            String Code                 = "";

            if(response != null && CashFlowsCaiboPaymentGatewayUtils.isJSONValid(response))
            {
                JSONObject jsonObject   = new JSONObject(response);
                JSONObject responseJSON = null;

                if(jsonObject.has("Response")){
                    responseJSON = jsonObject.getJSONObject("Response");
                }else if(jsonObject.has("Error")){
                    responseJSON = jsonObject.getJSONObject("Error");
                }

                if(responseJSON != null)
                {
                    if(responseJSON.has("AuthenticationStatus") && functions.isValueNull(responseJSON.getString("AuthenticationStatus"))){
                        AuthenticationStatus = responseJSON.getString("AuthenticationStatus");
                    }

                    if(responseJSON.has("Xid") && functions.isValueNull(responseJSON.getString("Xid"))){
                        Xid = responseJSON.getString("Xid");
                    }

                    if(responseJSON.has("Cavv") && functions.isValueNull(responseJSON.getString("Cavv"))){
                        Cavv = responseJSON.getString("Cavv");
                    }

                    if(responseJSON.has("Eci") && functions.isValueNull(responseJSON.getString("Eci"))){
                        Eci = responseJSON.getString("Eci");
                    }

                    if (responseJSON.has("Code") && functions.isValueNull(responseJSON.getString("Code"))){
                        Code = responseJSON.getString("Code");
                    }
                    if (responseJSON.has("Details") && functions.isValueNull(String.valueOf(responseJSON.getJSONArray("Details"))))
                    {
                        JSONArray jsonArray = responseJSON.getJSONArray("Details");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject errObject = (JSONObject) jsonArray.get(i);
                            error.append(errObject.getString("Message"));
                        }
                    }

                    if("Y".equalsIgnoreCase(AuthenticationStatus) || "A".equalsIgnoreCase(AuthenticationStatus))
                    { //Y = authentication succesfull, In this case WE get all parameter value in response
                      //A = authentication attempted, In this case WE get all parameter value in response
                        enrollmentResponseVO.setXID(Xid);
                        enrollmentResponseVO.setCAVV(Cavv);
                        enrollmentResponseVO.setEci(Eci);
                        enrollmentResponseVO.setResult(AuthenticationStatus);
                        enrollmentResponseVO.setStatus(AuthenticationStatus);
                    }
                    else if("N".equalsIgnoreCase(AuthenticationStatus))
                    { //N = authentication Failed, In this case WE get only AuthenticationStatus & ECI in response
                        enrollmentResponseVO.setEci(Eci);
                        enrollmentResponseVO.setResult(AuthenticationStatus);
                        enrollmentResponseVO.setStatus(AuthenticationStatus);
                    }
                    else if("U".equalsIgnoreCase(AuthenticationStatus))
                    {   //U = authentication un-available, In this case WE get only AuthenticationStatus in response
                        enrollmentResponseVO.setResult(AuthenticationStatus);
                        enrollmentResponseVO.setStatus(AuthenticationStatus);
                    }
                    else if (functions.isValueNull(error.toString()))
                    {
                        enrollmentResponseVO.setStatus("Failed");
                        enrollmentResponseVO.setResult("Failed");
                    }
                    else
                    {
                        enrollmentResponseVO.setStatus("pending");
                        enrollmentResponseVO.setResult("pending");
                    }
                }
                else
                {
                    enrollmentResponseVO.setStatus("pending");
                    enrollmentResponseVO.setResult("pending");
                }
            }
            else
            {
                enrollmentResponseVO.setStatus("pending");
                enrollmentResponseVO.setResult("pending");
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CashFlowsCaiboPaymentGateway.class.getName(), "processAuthenticateThreeDSecure()", null, "common", "Technical Exception while cancelling transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return enrollmentResponseVO;
    }

    public GenericResponseVO processSale(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("inside processSale() ----> ");

        CommRequestVO commRequestVO                 = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO             = new Comm3DResponseVO();
        Functions functions                         = new Functions();
        CommTransactionDetailsVO transDetailsVO     = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount               = GatewayAccountService.getGatewayAccount(accountId);
        ParesDecodeResponseVO paresDecodeResponseVO = commRequestVO.getParesDecodeResponseVO();

        JSONObject jsonRequestParameter             = null;
        JSONObject jsonRequestBody                  = null;
        JSONObject jsonRequestParameterLOG          = null;
        JSONObject jsonRequestBodyLOG               = null;
        JSONObject ThreeDSecureDataParameter        = null;
        JSONObject Mcc6012DataParameter             = null;
        JSONObject Mcc6012DataParameterLOG          = null;

        String MerchantId      = gatewayAccount.getMerchantId();
        String ApiKey          = gatewayAccount.getFRAUD_FTP_USERNAME();
        String SecurityToken   = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String mccCode         = gatewayAccount.getFRAUD_FTP_SITE();
        boolean isTest         = gatewayAccount.isTest();

        String Version           = "1.1";
        String TestMode          = "Live";
        String ReturnUrl         = RB.getString("ReturnUrl");
        String REQUEST_URL       = "";
        String Signature         = "";
        String response          = "";
        boolean Is3Ds            = true;
        boolean ReturnToken      = true;
        String PaymentType       = "Payment";

        String OrderReference    = "";
        String Amount            = "";
        String Currency          = "";
        String CardNumber        = "";
        String ExpiryDateMonth   = "";
        String ExpiryDateYear    = "";
        String cvv               = "";

        String OriginalTransactionId   = "";
        String Xid                     = "";
        String Cavv                    = "";
        String Eci                     = "";
        String ThreeDSecureVersion     = "";
        String DSTransId               = "";
        String PrimaryAccountNumber    = "";
        String Lastname                = "";
        String DateOfBirth             = "";
        String Postcode                = "";


        try
        {
            OrderReference          = trackingId;
            OriginalTransactionId   = trackingId;
            Amount                  = transDetailsVO.getAmount();
            Currency                = transDetailsVO.getCurrency();
            CardNumber              = cardDetailsVO.getCardNum();
            ExpiryDateMonth         = cardDetailsVO.getExpMonth();
            ExpiryDateYear          = CashFlowsCaiboPaymentGatewayUtils.getLast2DigitOfExpiryYear(cardDetailsVO.getExpYear());
            cvv                     = cardDetailsVO.getcVV();
            Xid                     = paresDecodeResponseVO.getXid();
            Cavv                    = paresDecodeResponseVO.getCavv();
            Eci                     = paresDecodeResponseVO.getEci();
            ThreeDSecureVersion     = paresDecodeResponseVO.getVersion();
//            DSTransId               = "104";     // 3dAuthentication response for 3DSv2
            PrimaryAccountNumber    = functions.getFirstSix(CardNumber)+functions.getLastFour(CardNumber);
            Lastname                = addressDetailsVO.getLastname();
            DateOfBirth             = addressDetailsVO.getBirthdate();
            Postcode                = addressDetailsVO.getZipCode();

            String dobDate  = "";
            String dobMonth = "";
            String dobYear  = "";
            if (DateOfBirth.length() == 8 & !DateOfBirth.contains("-"))
            {
                dobYear  = DateOfBirth.substring(0,4);
                dobMonth = DateOfBirth.substring(4,6);
                dobDate  = DateOfBirth.substring(6,8);
                DateOfBirth = dobYear+"-"+dobMonth+"-"+dobDate;
            }


            if (isTest)
            {
                REQUEST_URL = RB.getString("Authorisation_TEST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("Authorisation_LIVE_URL");
            }
            transactionLogger.error("processSale REQUEST_URL -----> "+ REQUEST_URL);

            //ThreeDSecureDataParameter
            ThreeDSecureDataParameter = new JSONObject();
            ThreeDSecureDataParameter.put("Xid", Xid);
            ThreeDSecureDataParameter.put("Cavv", Cavv);
            ThreeDSecureDataParameter.put("Eci", Eci);
            ThreeDSecureDataParameter.put("ThreeDSecureVersion", ThreeDSecureVersion);
//            ThreeDSecureDataParameter.put("DSTransId",DSTransId);      //should not be provided in a ThreeDSecureVersion 1 request & from ThreeDSecureVersion2() response

            //Mcc6012DataParameter
            Mcc6012DataParameter = new JSONObject();
            Mcc6012DataParameter.put("PrimaryAccountNumber", PrimaryAccountNumber);
            Mcc6012DataParameter.put("Lastname", Lastname);
            Mcc6012DataParameter.put("DateOfBirth", DateOfBirth);
            Mcc6012DataParameter.put("Postcode", Postcode);

            //jsonRequestParameter
            jsonRequestParameter = new JSONObject();
            jsonRequestParameter.put("MerchantId", MerchantId);
            jsonRequestParameter.put("TestMode", TestMode);
            jsonRequestParameter.put("PaymentType", PaymentType);
            jsonRequestParameter.put("OrderReference", OrderReference);
            jsonRequestParameter.put("Amount", Amount);
            jsonRequestParameter.put("Currency", Currency);
            jsonRequestParameter.put("CardNumber", CardNumber);
            jsonRequestParameter.put("ReturnToken", ReturnToken);
            jsonRequestParameter.put("Cvv", cvv);
            jsonRequestParameter.put("ExpiryDateMonth", ExpiryDateMonth);
            jsonRequestParameter.put("ExpiryDateYear", ExpiryDateYear);
            jsonRequestParameter.put("Is3Ds",Is3Ds);
            jsonRequestParameter.put("ThreeDSecureData",ThreeDSecureDataParameter);
            if (functions.isValueNull(mccCode) && ("6012".equals(mccCode) || "6051".equals(mccCode) || "7299".equals(mccCode)))
            {
                jsonRequestParameter.put("Mcc6012Data",Mcc6012DataParameter);
            }

            Signature = CashFlowsCaiboPaymentGatewayUtils.generateSHA512(SecurityToken + jsonRequestParameter.toString().substring(1, jsonRequestParameter.toString().length() - 1));

            //jsonRequestBody
            jsonRequestBody = new JSONObject();
            jsonRequestBody.put("ApiKey", ApiKey);
            jsonRequestBody.put("Signature", Signature);
            jsonRequestBody.put("Request", jsonRequestParameter);
            jsonRequestBody.put("Version", Version);

            //Mcc6012DataParameterLOG
            Mcc6012DataParameterLOG = new JSONObject();
            Mcc6012DataParameterLOG.put("PrimaryAccountNumber", functions.maskingPan(PrimaryAccountNumber));
            Mcc6012DataParameterLOG.put("Lastname", functions.maskingLastName(Lastname));
            Mcc6012DataParameterLOG.put("DateOfBirth", DateOfBirth);
            Mcc6012DataParameterLOG.put("Postcode", Postcode);
            //jsonRequestParameterLOG
            jsonRequestParameterLOG = new JSONObject();
            jsonRequestParameterLOG.put("MerchantId", MerchantId);
            jsonRequestParameterLOG.put("TestMode", TestMode);
            jsonRequestParameterLOG.put("PaymentType", PaymentType);
            jsonRequestParameterLOG.put("OrderReference", OrderReference);
            jsonRequestParameterLOG.put("Amount", Amount);
            jsonRequestParameterLOG.put("Currency", Currency);
            jsonRequestParameterLOG.put("CardNumber", functions.maskingPan(CardNumber));
            jsonRequestParameterLOG.put("ReturnToken", ReturnToken);
            jsonRequestParameterLOG.put("Cvv", functions.maskingNumber(cvv));
            jsonRequestParameterLOG.put("ExpiryDateMonth", functions.maskingNumber(ExpiryDateMonth));
            jsonRequestParameterLOG.put("ExpiryDateYear", functions.maskingNumber(ExpiryDateYear));
            jsonRequestParameterLOG.put("Is3Ds",Is3Ds);
            jsonRequestParameterLOG.put("ThreeDSecureData",ThreeDSecureDataParameter);
            if (functions.isValueNull(mccCode) && ("6012".equals(mccCode) || "6051".equals(mccCode) || "7299".equals(mccCode)))
            {
                jsonRequestParameterLOG.put("Mcc6012Data",Mcc6012DataParameterLOG);
            }
            //jsonRequestBodyLOG
            jsonRequestBodyLOG = new JSONObject();
            jsonRequestBodyLOG.put("ApiKey", ApiKey);
            jsonRequestBodyLOG.put("Signature", Signature);
            jsonRequestBodyLOG.put("Request", jsonRequestParameterLOG);
            jsonRequestBodyLOG.put("Version", Version);

            transactionLogger.error("processSale RequestParameters ---> "+jsonRequestBodyLOG);

            response = CashFlowsCaiboPaymentGatewayUtils.doPostHTTPSURLConnectionClient(REQUEST_URL, jsonRequestBody.toString());
            transactionLogger.error("processSale Response ---"+trackingId +"-->" +response);

            JSONObject logResp    = new JSONObject();
            if (CashFlowsCaiboPaymentGatewayUtils.isJSONValid(response))
            {
                JSONObject respJson   = new JSONObject(response);
                JSONArray keys        = respJson.names();
                for (int i = 0; i < keys.length (); i++)
                {
                    logResp.put(keys.getString(i),respJson.getString (keys.getString(i)));
                }
            }
            transactionLogger.error("processSale Response for grep ---"+trackingId +"-->" +logResp);

            StringBuffer error  = new StringBuffer();
            String AuthCode     = "";
            String Arn          = "";
            String Message      = "";
            String TimeStamp    = "";
            String Status       = "";
            String Currency1    = "";
            String amount1      = "";
            String Code         = "";
            String TransactionId        = "";
            String SchemeTransactionId  = "";
            String IssuerResponseCode   = "";
            String CvvAvsResult         = "";
            String AcquirerResponseCode = "";
            String CardToken            = "";



            if(response != null && CashFlowsCaiboPaymentGatewayUtils.isJSONValid(response))
            {
                JSONObject jsonObject   = new JSONObject(response);
                JSONObject responseJSON = null;

                if(jsonObject.has("Response")){
                    responseJSON = jsonObject.getJSONObject("Response");
                }else if(jsonObject.has("Error")){
                    responseJSON = jsonObject.getJSONObject("Error");
                }

                if(responseJSON != null)
                {
                    if(responseJSON.has("AuthCode") && functions.isValueNull(responseJSON.getString("AuthCode"))){
                        AuthCode = responseJSON.getString("AuthCode");
                    }

                    if(responseJSON.has("Arn") && functions.isValueNull(responseJSON.getString("Arn"))){
                        Arn = responseJSON.getString("Arn");
                    }

                    if(responseJSON.has("Currency") && functions.isValueNull(responseJSON.getString("Currency"))){
                        Currency1 = responseJSON.getString("Currency");
                    }

                    if(responseJSON.has("Amount") && functions.isValueNull(responseJSON.getString("Amount"))){
                        amount1 = responseJSON.getString("Amount");
                    }

                    if(responseJSON.has("Message") && functions.isValueNull(responseJSON.getString("Message"))){
                        Message = responseJSON.getString("Message");
                    }

                    if(responseJSON.has("TimeStamp") && functions.isValueNull(responseJSON.getString("TimeStamp"))){
                        TimeStamp = responseJSON.getString("TimeStamp");
                    }

                    if(responseJSON.has("Status") && functions.isValueNull(responseJSON.getString("Status"))){
                        Status = responseJSON.getString("Status");
                    }

                    if(responseJSON.has("TransactionId") && functions.isValueNull(responseJSON.getString("TransactionId"))){
                        TransactionId = responseJSON.getString("TransactionId");
                    }

                    if(responseJSON.has("SchemeTransactionId") && functions.isValueNull(responseJSON.getString("SchemeTransactionId"))){
                        SchemeTransactionId = responseJSON.getString("SchemeTransactionId");
                    }

                    if(responseJSON.has("IssuerResponseCode") && functions.isValueNull(responseJSON.getString("IssuerResponseCode"))){
                        IssuerResponseCode = responseJSON.getString("IssuerResponseCode");
                    }

                    if(responseJSON.has("CvvAvsResult") && functions.isValueNull(responseJSON.getString("CvvAvsResult"))){
                        CvvAvsResult = responseJSON.getString("CvvAvsResult");
                    }

                    if(responseJSON.has("AcquirerResponseCode") && functions.isValueNull(responseJSON.getString("AcquirerResponseCode"))){
                        AcquirerResponseCode = responseJSON.getString("AcquirerResponseCode");
                    }
                    if(responseJSON.has("CardToken") && functions.isValueNull(responseJSON.getString("CardToken"))){
                        CardToken = responseJSON.getString("CardToken");
                    }

                    if (responseJSON.has("Code") && functions.isValueNull(responseJSON.getString("Code"))){
                        Code = responseJSON.getString("Code");
                    }
                    if (responseJSON.has("Details") && functions.isValueNull(String.valueOf(responseJSON.getJSONArray("Details"))))
                    {
                        JSONArray jsonArray = responseJSON.getJSONArray("Details");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject errObject = (JSONObject) jsonArray.get(i);
                            error.append(errObject.getString("Message"));
                        }
                    }

                    if("Successful".equalsIgnoreCase(Status) && "AUTHOK".equalsIgnoreCase(AuthCode))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setTransactionStatus("success");
                        commResponseVO.setDescription(SchemeTransactionId);
                        commResponseVO.setRemark(Message);
                        commResponseVO.setAmount(amount1);
                        commResponseVO.setMerchantId(MerchantId);
                        commResponseVO.setTransactionId(TransactionId);
                        commResponseVO.setCurrency(Currency1);
                        commResponseVO.setAuthCode(AuthCode);
                        commResponseVO.setBankTransactionDate(TimeStamp);
                        commResponseVO.setResponseTime(TimeStamp);
                        commResponseVO.setBankCode(IssuerResponseCode);
                        commResponseVO.setBankDescription(Message);
                        commResponseVO.setArn(Arn);
                        commResponseVO.setErrorCode(AcquirerResponseCode);
                        commResponseVO.setResponseHashInfo(CardToken);
                        CashFlowsCaiboPaymentGatewayUtils.updateMainTableEntry(Arn,AuthCode,trackingId);
                    }
                    else if ("Failed".equalsIgnoreCase(Status) || "Declined".equalsIgnoreCase(Status))
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setDescription(SchemeTransactionId);
                        commResponseVO.setRemark(Message);
                        commResponseVO.setAmount(amount1);
                        commResponseVO.setArn(Arn);
                        commResponseVO.setAuthCode(AuthCode);
                        commResponseVO.setBankTransactionDate(TimeStamp);
                        commResponseVO.setResponseTime(TimeStamp);
                        commResponseVO.setBankCode(IssuerResponseCode);
                        commResponseVO.setBankDescription(Message);
                        commResponseVO.setMerchantId(MerchantId);
                        commResponseVO.setTransactionId(TransactionId);
                        commResponseVO.setCurrency(Currency1);
                        commResponseVO.setResponseHashInfo(CardToken);
                        CashFlowsCaiboPaymentGatewayUtils.updateMainTableEntry(Arn,AuthCode,trackingId);
                    }
                    else if (functions.isValueNull(error.toString()))
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(error.toString());
                    }
                    else
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CashFlowsCaiboPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while cancelling transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processCommon3DAuthConfirmation(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("inside CashFlowsCaiboPaymentGateway process3DAuthSaleConfirmation ----> ");

        CommRequestVO commRequestVO                 = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO             = new Comm3DResponseVO();
        Functions functions                         = new Functions();
        CommTransactionDetailsVO transDetailsVO     = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount               = GatewayAccountService.getGatewayAccount(accountId);
        ParesDecodeResponseVO paresDecodeResponseVO = commRequestVO.getParesDecodeResponseVO();

        JSONObject jsonRequestParameter             = null;
        JSONObject jsonRequestBody                  = null;
        JSONObject jsonRequestParameterLOG          = null;
        JSONObject jsonRequestBodyLOG               = null;
        JSONObject ThreeDSecureDataParameter        = null;
        JSONObject Mcc6012DataParameter             = null;
        JSONObject Mcc6012DataParameterLOG          = null;

        String MerchantId      = gatewayAccount.getMerchantId();
        String ApiKey          = gatewayAccount.getFRAUD_FTP_USERNAME();
        String SecurityToken   = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String mccCode         = gatewayAccount.getFRAUD_FTP_SITE();

        boolean isTest         = gatewayAccount.isTest();

        String Version           = "1.1";
        String TestMode          = "Live";
        String ReturnUrl         = RB.getString("ReturnUrl");
        String REQUEST_URL       = "";
        String Signature         = "";
        String response          = "";
        boolean Is3Ds            = true;
        boolean ReturnToken      = true;
        String PaymentType       = "Auth";

        String OrderReference    = "";
        String Amount            = "";
        String Currency          = "";
        String CardNumber        = "";
        String ExpiryDateMonth   = "";
        String ExpiryDateYear    = "";
        String cvv               = "";

        String OriginalTransactionId   = "";
        String Xid                     = "";
        String Cavv                    = "";
        String Eci                     = "";
        String ThreeDSecureVersion     = "";
        String DSTransId               = "";
        String PrimaryAccountNumber    = "";
        String Lastname                = "";
        String DateOfBirth             = "";
        String Postcode                = "";


        try
        {
            OrderReference          = trackingId;
            OriginalTransactionId   = trackingId;
            Amount                  = transDetailsVO.getAmount();
            Currency                = transDetailsVO.getCurrency();
            CardNumber              = cardDetailsVO.getCardNum();
            ExpiryDateMonth         = cardDetailsVO.getExpMonth();
            ExpiryDateYear          = CashFlowsCaiboPaymentGatewayUtils.getLast2DigitOfExpiryYear(cardDetailsVO.getExpYear());
            cvv                     = cardDetailsVO.getcVV();
            Xid                     = paresDecodeResponseVO.getXid();
            Cavv                    = paresDecodeResponseVO.getCavv();
            Eci                     = paresDecodeResponseVO.getEci();
            ThreeDSecureVersion     = paresDecodeResponseVO.getVersion();
//            DSTransId               = "104";     // 3dAuthentication response for 3DSv2
            PrimaryAccountNumber    = functions.getFirstSix(CardNumber)+functions.getLastFour(CardNumber);
            Lastname                = addressDetailsVO.getLastname();
            DateOfBirth             = addressDetailsVO.getBirthdate();
            Postcode                = addressDetailsVO.getZipCode();

            String dobDate  = "";
            String dobMonth = "";
            String dobYear  = "";
            if (DateOfBirth.length() == 8 & !DateOfBirth.contains("-"))
            {
                dobYear  = DateOfBirth.substring(0,4);
                dobMonth = DateOfBirth.substring(4,6);
                dobDate  = DateOfBirth.substring(6,8);
                DateOfBirth = dobYear+"-"+dobMonth+"-"+dobDate;
            }


            if (isTest)
            {
                REQUEST_URL = RB.getString("Authorisation_TEST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("Authorisation_LIVE_URL");
            }
            transactionLogger.error("process3DAuthSaleConfirmation REQUEST_URL -----> "+ REQUEST_URL);

            //ThreeDSecureDataParameter
            ThreeDSecureDataParameter = new JSONObject();
            ThreeDSecureDataParameter.put("Xid", Xid);
            ThreeDSecureDataParameter.put("Cavv", Cavv);
            ThreeDSecureDataParameter.put("Eci", Eci);
            ThreeDSecureDataParameter.put("ThreeDSecureVersion", ThreeDSecureVersion);
//            ThreeDSecureDataParameter.put("DSTransId",DSTransId);      //should not be provided in a ThreeDSecureVersion 1 request & from ThreeDSecureVersion2() response

            //Mcc6012DataParameter
            Mcc6012DataParameter = new JSONObject();
            Mcc6012DataParameter.put("PrimaryAccountNumber", PrimaryAccountNumber);
            Mcc6012DataParameter.put("Lastname", Lastname);
            Mcc6012DataParameter.put("DateOfBirth", DateOfBirth);
            Mcc6012DataParameter.put("Postcode", Postcode);

            //jsonRequestParameter
            jsonRequestParameter = new JSONObject();
            jsonRequestParameter.put("MerchantId", MerchantId);
            jsonRequestParameter.put("TestMode", TestMode);
            jsonRequestParameter.put("PaymentType", PaymentType);
            jsonRequestParameter.put("OrderReference", OrderReference);
            jsonRequestParameter.put("Amount", Amount);
            jsonRequestParameter.put("Currency", Currency);
            jsonRequestParameter.put("CardNumber", CardNumber);
            jsonRequestParameter.put("ReturnToken", ReturnToken);
            jsonRequestParameter.put("Cvv", cvv);
            jsonRequestParameter.put("ExpiryDateMonth", ExpiryDateMonth);
            jsonRequestParameter.put("ExpiryDateYear", ExpiryDateYear);
            jsonRequestParameter.put("Is3Ds",Is3Ds);
            jsonRequestParameter.put("ThreeDSecureData",ThreeDSecureDataParameter);
            if (functions.isValueNull(mccCode) && ("6012".equals(mccCode) || "6051".equals(mccCode) || "7299".equals(mccCode)))
            {
                jsonRequestParameter.put("Mcc6012Data",Mcc6012DataParameter);
            }

            Signature = CashFlowsCaiboPaymentGatewayUtils.generateSHA512(SecurityToken + jsonRequestParameter.toString().substring(1, jsonRequestParameter.toString().length() - 1));

            //jsonRequestBody
            jsonRequestBody = new JSONObject();
            jsonRequestBody.put("ApiKey", ApiKey);
            jsonRequestBody.put("Signature", Signature);
            jsonRequestBody.put("Request", jsonRequestParameter);
            jsonRequestBody.put("Version", Version);

            //Mcc6012DataParameterLOG
            Mcc6012DataParameterLOG = new JSONObject();
            Mcc6012DataParameterLOG.put("PrimaryAccountNumber", functions.maskingPan(PrimaryAccountNumber));
            Mcc6012DataParameterLOG.put("Lastname", functions.maskingLastName(Lastname));
            Mcc6012DataParameterLOG.put("DateOfBirth", DateOfBirth);
            Mcc6012DataParameterLOG.put("Postcode", Postcode);
            //jsonRequestParameterLOG
            jsonRequestParameterLOG = new JSONObject();
            jsonRequestParameterLOG.put("MerchantId", MerchantId);
            jsonRequestParameterLOG.put("TestMode", TestMode);
            jsonRequestParameterLOG.put("PaymentType", PaymentType);
            jsonRequestParameterLOG.put("OrderReference", OrderReference);
            jsonRequestParameterLOG.put("Amount", Amount);
            jsonRequestParameterLOG.put("Currency", Currency);
            jsonRequestParameterLOG.put("CardNumber", functions.maskingPan(CardNumber));
            jsonRequestParameterLOG.put("ReturnToken", ReturnToken);
            jsonRequestParameterLOG.put("Cvv", functions.maskingNumber(cvv));
            jsonRequestParameterLOG.put("ExpiryDateMonth", functions.maskingNumber(ExpiryDateMonth));
            jsonRequestParameterLOG.put("ExpiryDateYear", functions.maskingNumber(ExpiryDateYear));
            jsonRequestParameterLOG.put("Is3Ds",Is3Ds);
            jsonRequestParameterLOG.put("ThreeDSecureData",ThreeDSecureDataParameter);
            if (functions.isValueNull(mccCode) && ("6012".equals(mccCode) || "6051".equals(mccCode) || "7299".equals(mccCode)))
            {
                jsonRequestParameterLOG.put("Mcc6012Data",Mcc6012DataParameterLOG);
            }
            //jsonRequestBodyLOG
            jsonRequestBodyLOG = new JSONObject();
            jsonRequestBodyLOG.put("ApiKey", ApiKey);
            jsonRequestBodyLOG.put("Signature", Signature);
            jsonRequestBodyLOG.put("Request", jsonRequestParameterLOG);
            jsonRequestBodyLOG.put("Version", Version);

            transactionLogger.error("process3DAuthSaleConfirmation RequestParameters ---> "+jsonRequestBodyLOG);

            response = CashFlowsCaiboPaymentGatewayUtils.doPostHTTPSURLConnectionClient(REQUEST_URL, jsonRequestBody.toString());
            transactionLogger.error("process3DAuthSaleConfirmation Response ---"+trackingId +"-->" + response);

            JSONObject logResp    = new JSONObject();
            if (CashFlowsCaiboPaymentGatewayUtils.isJSONValid(response))
            {
                JSONObject respJson   = new JSONObject(response);
                JSONArray keys        = respJson.names();
                for (int i = 0; i < keys.length (); i++)
                {
                    logResp.put(keys.getString(i),respJson.getString (keys.getString(i)));
                }
            }
            transactionLogger.error("process3DAuthSaleConfirmation Response for grep ---"+trackingId +"-->" + logResp);

            StringBuffer error  = new StringBuffer();
            String AuthCode     = "";
            String Arn          = "";
            String Message      = "";
            String TimeStamp    = "";
            String Status       = "";
            String Currency1    = "";
            String Code         = "";
            String amount1      = "";
            String TransactionId        = "";
            String SchemeTransactionId  = "";
            String IssuerResponseCode   = "";
            String CvvAvsResult         = "";
            String AcquirerResponseCode = "";
            String CardToken            = "";



            if(response != null && CashFlowsCaiboPaymentGatewayUtils.isJSONValid(response))
            {
                JSONObject jsonObject   = new JSONObject(response);
                JSONObject responseJSON = null;

                if(jsonObject.has("Response")){
                    responseJSON = jsonObject.getJSONObject("Response");
                }else if(jsonObject.has("Error")){
                    responseJSON = jsonObject.getJSONObject("Error");
                }

                if(responseJSON != null)
                {
                    if(responseJSON.has("AuthCode") && functions.isValueNull(responseJSON.getString("AuthCode"))){
                        AuthCode = responseJSON.getString("AuthCode");
                    }

                    if(responseJSON.has("Arn") && functions.isValueNull(responseJSON.getString("Arn"))){
                        Arn = responseJSON.getString("Arn");
                    }

                    if(responseJSON.has("Currency") && functions.isValueNull(responseJSON.getString("Currency"))){
                        Currency1 = responseJSON.getString("Currency");
                    }

                    if(responseJSON.has("Amount") && functions.isValueNull(responseJSON.getString("Amount"))){
                        amount1 = responseJSON.getString("Amount");
                    }

                    if(responseJSON.has("Message") && functions.isValueNull(responseJSON.getString("Message"))){
                        Message = responseJSON.getString("Message");
                    }

                    if(responseJSON.has("TimeStamp") && functions.isValueNull(responseJSON.getString("TimeStamp"))){
                        TimeStamp = responseJSON.getString("TimeStamp");
                    }

                    if(responseJSON.has("Status") && functions.isValueNull(responseJSON.getString("Status"))){
                        Status = responseJSON.getString("Status");
                    }

                    if(responseJSON.has("TransactionId") && functions.isValueNull(responseJSON.getString("TransactionId"))){
                        TransactionId = responseJSON.getString("TransactionId");
                    }

                    if(responseJSON.has("SchemeTransactionId") && functions.isValueNull(responseJSON.getString("SchemeTransactionId"))){
                        SchemeTransactionId = responseJSON.getString("SchemeTransactionId");
                    }

                    if(responseJSON.has("IssuerResponseCode") && functions.isValueNull(responseJSON.getString("IssuerResponseCode"))){
                        IssuerResponseCode = responseJSON.getString("IssuerResponseCode");
                    }

                    if(responseJSON.has("CvvAvsResult") && functions.isValueNull(responseJSON.getString("CvvAvsResult"))){
                        CvvAvsResult = responseJSON.getString("CvvAvsResult");
                    }

                    if(responseJSON.has("AcquirerResponseCode") && functions.isValueNull(responseJSON.getString("AcquirerResponseCode"))){
                        AcquirerResponseCode = responseJSON.getString("AcquirerResponseCode");
                    }
                    if(responseJSON.has("CardToken") && functions.isValueNull(responseJSON.getString("CardToken"))){
                        CardToken = responseJSON.getString("CardToken");
                    }

                    if (responseJSON.has("Code") && functions.isValueNull(responseJSON.getString("Code"))){
                        Code = responseJSON.getString("Code");
                    }
                    if (responseJSON.has("Details") && functions.isValueNull(String.valueOf(responseJSON.getJSONArray("Details"))))
                    {
                        JSONArray jsonArray = responseJSON.getJSONArray("Details");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject errObject = (JSONObject) jsonArray.get(i);
                            error.append(errObject.getString("Message"));
                        }
                    }

                    if("Successful".equalsIgnoreCase(Status) && "AUTHOK".equalsIgnoreCase(AuthCode))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setTransactionStatus("success");
                        commResponseVO.setDescription(SchemeTransactionId);
                        commResponseVO.setRemark(Message);
                        commResponseVO.setAmount(amount1);
                        commResponseVO.setMerchantId(MerchantId);
                        commResponseVO.setTransactionId(TransactionId);
                        commResponseVO.setCurrency(Currency1);
                        commResponseVO.setAuthCode(AuthCode);
                        commResponseVO.setBankTransactionDate(TimeStamp);
                        commResponseVO.setResponseTime(TimeStamp);
                        commResponseVO.setBankCode(IssuerResponseCode);
                        commResponseVO.setBankDescription(Message);
                        commResponseVO.setArn(Arn);
                        commResponseVO.setErrorCode(AcquirerResponseCode);
                        commResponseVO.setResponseHashInfo(CardToken);
                        CashFlowsCaiboPaymentGatewayUtils.updateMainTableEntry(Arn,AuthCode,trackingId);
                    }
                    else if ("Failed".equalsIgnoreCase(Status) || "Declined".equalsIgnoreCase(Status))
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setDescription(SchemeTransactionId);
                        commResponseVO.setRemark(Message);
                        commResponseVO.setAmount(amount1);
                        commResponseVO.setArn(Arn);
                        commResponseVO.setAuthCode(AuthCode);
                        commResponseVO.setBankTransactionDate(TimeStamp);
                        commResponseVO.setResponseTime(TimeStamp);
                        commResponseVO.setBankCode(IssuerResponseCode);
                        commResponseVO.setBankDescription(Message);
                        commResponseVO.setMerchantId(MerchantId);
                        commResponseVO.setTransactionId(TransactionId);
                        commResponseVO.setCurrency(Currency1);
                        commResponseVO.setResponseHashInfo(CardToken);
                        CashFlowsCaiboPaymentGatewayUtils.updateMainTableEntry(Arn,AuthCode,trackingId);
                    }
                    else if (functions.isValueNull(error.toString()))
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(error.toString());
                    }
                    else
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CashFlowsCaiboPaymentGateway.class.getName(), "process3DAuthSaleConfirmation()", null, "common", "Technical Exception while cancelling transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {

        transactionLogger.error("inside CashFlowsCaiboPaymentGateway processVoid -----> ");

        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO                       = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions                                 = new Functions();

        boolean isTest                      = gatewayAccount.isTest();
        JSONObject jsonRequestParameter     = new JSONObject();
        JSONObject jsonRequestBodyParameter = new JSONObject();

        String ApiKey          = gatewayAccount.getFRAUD_FTP_USERNAME();
        String SecurityToken   = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String Version         = "1.1";
        String REQUEST_URL     = "";
        String Signature       = "";
        String response        = "";

        String TestMode        = "Live";
        String TransactionId   = commTransactionDetailsVO.getPreviousTransactionId();


        try
        {
            if (isTest)
            {
                REQUEST_URL = RB.getString("CANCEL_TEST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("CANCEL_LIVE_URL");
            }
            transactionLogger.error("processVoid REQUEST_URL ----> "+REQUEST_URL);

            //jsonRequestParameter
            jsonRequestParameter.put("TransactionId", TransactionId);
            jsonRequestParameter.put("TestMode", TestMode);

            Signature = CashFlowsCaiboPaymentGatewayUtils.generateSHA512(SecurityToken + jsonRequestParameter.toString().substring(1,jsonRequestParameter.toString().length()-1));

            //RequestBody
            jsonRequestBodyParameter.put("ApiKey",ApiKey);
            jsonRequestBodyParameter.put("Signature",Signature);
            jsonRequestBodyParameter.put("Request",jsonRequestParameter);
            jsonRequestBodyParameter.put("Version",Version);

            transactionLogger.error("processVoid RequestBodyParameter ---> "+trackingID +" " +jsonRequestBodyParameter.toString());

            response = CashFlowsCaiboPaymentGatewayUtils.doPostHTTPSURLConnectionClient(REQUEST_URL, jsonRequestBodyParameter.toString());
            transactionLogger.error("processVoid Response ---"+trackingID +"-->" +response);

            JSONObject logResp    = new JSONObject();
            if (CashFlowsCaiboPaymentGatewayUtils.isJSONValid(response))
            {
                JSONObject respJson   = new JSONObject(response);
                JSONArray keys        = respJson.names();
                for (int i = 0; i < keys.length (); i++)
                {
                    logResp.put(keys.getString(i),respJson.getString (keys.getString(i)));
                }
            }
            transactionLogger.error("processVoid Response for grep ---"+trackingID +"-->" +logResp);

            StringBuffer error              = new StringBuffer();
            String Code                     = "";
            String Message                  = "";
            String TimeStamp                = "";
            String Status                   = "";
            String Arn                      = "";
            String IssuerResponseCode       = "";
            String AcquirerResponseCode     = "";
            String AuthCode                 = "";
            String TransactionIdRes         = "";


            if(response != null && CashFlowsCaiboPaymentGatewayUtils.isJSONValid(response)){
                JSONObject jsonObject   = new JSONObject(response);
                JSONObject responseJSON = null;
                JSONObject errorJSONS   = null;

                if(jsonObject.has("Response")){
                    responseJSON = jsonObject.getJSONObject("Response");
                }else if(jsonObject.has("Error")){
                    responseJSON = jsonObject.getJSONObject("Error");
                }

                if(responseJSON != null)
                {
                    if(responseJSON.has("Message") && functions.isValueNull(responseJSON.getString("Message"))){
                        Message = responseJSON.getString("Message");
                    }

                    if(responseJSON.has("TimeStamp") && functions.isValueNull(responseJSON.getString("TimeStamp"))){
                        TimeStamp = responseJSON.getString("TimeStamp");
                    }

                    if(responseJSON.has("Status") && functions.isValueNull(responseJSON.getString("Status"))){
                        Status = responseJSON.getString("Status");
                    }

                    if(responseJSON.has("Arn") && functions.isValueNull(responseJSON.getString("Arn"))){
                        Arn = responseJSON.getString("Arn");
                    }

                    if(responseJSON.has("TransactionId") && functions.isValueNull(responseJSON.getString("TransactionId"))){
                        TransactionIdRes = responseJSON.getString("TransactionId");
                    }

                    if(responseJSON.has("IssuerResponseCode") && functions.isValueNull(responseJSON.getString("IssuerResponseCode"))) {
                        IssuerResponseCode = responseJSON.getString("IssuerResponseCode");
                    }

                    if(responseJSON.has("AcquirerResponseCode") && functions.isValueNull(responseJSON.getString("AcquirerResponseCode"))) {
                        AcquirerResponseCode = responseJSON.getString("AcquirerResponseCode");
                    }

                    if(responseJSON.has("AuthCode") && functions.isValueNull(responseJSON.getString("AuthCode"))) {
                        AuthCode = responseJSON.getString("AuthCode");
                    }

                    if (responseJSON.has("Code") && functions.isValueNull(responseJSON.getString("Code"))){
                        Code = responseJSON.getString("Code");
                    }
                    if (responseJSON.has("Details") && functions.isValueNull(String.valueOf(responseJSON.getJSONArray("Details"))))
                    {
                        JSONArray jsonArray = responseJSON.getJSONArray("Details");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject errObject = (JSONObject) jsonArray.get(i);
                            error.append(errObject.getString("Message"));
                        }
                    }


                    if(Status.equalsIgnoreCase("Successful"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setTransactionStatus("success");
                        commResponseVO.setResponseTime(TimeStamp);
                        commResponseVO.setBankTransactionDate(TimeStamp);
                        commResponseVO.setTransactionId(String.valueOf(TransactionIdRes));
                        commResponseVO.setAuthCode(AuthCode);
                        commResponseVO.setErrorCode(AcquirerResponseCode);
                        commResponseVO.setRemark(Message);
                        commResponseVO.setBankDescription(Message);
                        commResponseVO.setArn(Arn);
                    }
                    else if(Status.equalsIgnoreCase("Failed") || Status.equalsIgnoreCase("Declined"))
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setResponseTime(TimeStamp);
                        commResponseVO.setBankTransactionDate(TimeStamp);
                        commResponseVO.setTransactionId(String.valueOf(TransactionIdRes));
                        commResponseVO.setAuthCode(AuthCode);
                        commResponseVO.setErrorCode(AcquirerResponseCode);
                        commResponseVO.setRemark(Message);
                        commResponseVO.setBankDescription(Message);
                        commResponseVO.setArn(Arn);
                    }
                    else if (functions.isValueNull(error.toString()))
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(error.toString());
                    }
                    else
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CashFlowsCaiboPaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while cancelling transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processCapture(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("inside CashFlowsCaiboPaymentGateway processCapture ----> ");

        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        CommRequestVO reqVO                                 = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO                       = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = reqVO.getTransDetailsVO();
        Functions functions = new Functions();

        JSONObject jsonRequestParameter     = new JSONObject();
        JSONObject jsonRequestBodyParameter = new JSONObject();
        boolean isTest                      = gatewayAccount.isTest();
        String ApiKey                       = gatewayAccount.getFRAUD_FTP_USERNAME();
        String SecurityToken                = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String Version                      = "1.1";
        String REQUEST_URL                  = "";
        String TestMode                     = "Live";

        String Signature                    = "";
        String response                     = "";
        String TransactionId                = commTransactionDetailsVO.getPreviousTransactionId();

        try
        {
            if (isTest)
            {
                REQUEST_URL = RB.getString("CAPTURE_TEST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("CAPTURE_LIVE_URL");
            }
            transactionLogger.error("processCapture REQUEST_URL ---> "+ REQUEST_URL);

            //RequestParameter
            jsonRequestParameter.put("TransactionId",TransactionId);
            jsonRequestParameter.put("TestMode", TestMode);

            Signature = CashFlowsCaiboPaymentGatewayUtils.generateSHA512(SecurityToken + jsonRequestParameter.toString().substring(1,jsonRequestParameter.toString().length()-1));

            //RequestBody
            jsonRequestBodyParameter.put("ApiKey",ApiKey);
            jsonRequestBodyParameter.put("Signature",Signature);
            jsonRequestBodyParameter.put("Request",jsonRequestParameter);
            jsonRequestBodyParameter.put("Version",Version);
            transactionLogger.error("processCapture RequestParameters : "+jsonRequestBodyParameter);

            response = CashFlowsCaiboPaymentGatewayUtils.doPostHTTPSURLConnectionClient(REQUEST_URL, jsonRequestBodyParameter.toString());
            transactionLogger.error("processCapture Response ---"+ trackingId + "-->" + response);

            JSONObject logResp    = new JSONObject();
            if (CashFlowsCaiboPaymentGatewayUtils.isJSONValid(response))
            {
                JSONObject respJson   = new JSONObject(response);
                JSONArray keys        = respJson.names();
                for (int i = 0; i < keys.length (); i++)
                {
                    logResp.put(keys.getString(i),respJson.getString (keys.getString(i)));
                }
            }
            transactionLogger.error("processCapture Response for grep ---"+ trackingId + "-->" + logResp);

            StringBuffer error      = new StringBuffer();
            String Message          = "";
            String TimeStamp        = "";
            String Status           = "";
            String TransactionIdRes = "";
            String Code             = "";

            if(response != null && CashFlowsCaiboPaymentGatewayUtils.isJSONValid(response)){
                JSONObject jsonObject   = new JSONObject(response);
                JSONObject responseJSON = null;

                if(jsonObject.has("Response")){
                    responseJSON = jsonObject.getJSONObject("Response");
                }else if(jsonObject.has("Error")){
                    responseJSON = jsonObject.getJSONObject("Error");
                }

                if(responseJSON != null)
                {
                    if(responseJSON.has("Message") && functions.isValueNull(responseJSON.getString("Message"))){
                        Message = responseJSON.getString("Message");
                    }

                    if(responseJSON.has("TimeStamp") && functions.isValueNull(responseJSON.getString("TimeStamp"))){
                        TimeStamp = responseJSON.getString("TimeStamp");
                    }

                    if(responseJSON.has("Status") && functions.isValueNull(responseJSON.getString("Status"))){
                        Status = responseJSON.getString("Status");
                    }

                    if(responseJSON.has("TransactionId") && functions.isValueNull(responseJSON.getString("TransactionId"))){
                        TransactionIdRes = responseJSON.getString("TransactionId");
                    }

                    if (responseJSON.has("Code") && functions.isValueNull(responseJSON.getString("Code"))){
                        Code = responseJSON.getString("Code");
                    }
                    if (responseJSON.has("Details") && functions.isValueNull(String.valueOf(responseJSON.getJSONArray("Details"))))
                    {
                        JSONArray jsonArray = responseJSON.getJSONArray("Details");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject errObject = (JSONObject) jsonArray.get(i);
                            error.append(errObject.getString("Message"));
                        }
                    }


                    if(Status.equalsIgnoreCase("Successful"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setTransactionStatus("success");
                        commResponseVO.setRemark(Message);
                        commResponseVO.setResponseTime(TimeStamp);
                        commResponseVO.setBankTransactionDate(TimeStamp);
                        commResponseVO.setTransactionId(String.valueOf(TransactionIdRes));
                    }
                    else if(Status.equalsIgnoreCase("Failed") || Status.equalsIgnoreCase("Declined"))
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setResponseTime(TimeStamp);
                        commResponseVO.setBankTransactionDate(TimeStamp);
                        commResponseVO.setTransactionId(String.valueOf(TransactionIdRes));
                        commResponseVO.setRemark(Message);
                    }
                    else if (functions.isValueNull(error.toString()))
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(error.toString());
                    }
                    else
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CashFlowsCaiboPaymentGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while cancelling transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return  commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error("inside CashFlowsCaiboPaymentGateway processRefund ---> ");

        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        CommRequestVO reqVO                                 = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO                       = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = reqVO.getTransDetailsVO();
        Functions functions                                 = new Functions();

        boolean isTest                          = gatewayAccount.isTest();
        JSONObject jsonRequestParameter         = new JSONObject();
        JSONObject jsonRequestBodyParameter     = new JSONObject();

        String ApiKey           = gatewayAccount.getFRAUD_FTP_USERNAME();
        String SecurityToken    = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String Version          = "1.1";
        String REQUEST_URL      = "";
        String TestMode         = "Live";
        String Signature        = "";
        String response         = "";

        String TransactionID    = commTransactionDetailsVO.getPreviousTransactionId();
        String Amount           = commTransactionDetailsVO.getAmount();

        try
        {
            if (isTest)
            {
                REQUEST_URL = RB.getString("REFUND_TEST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("REFUND_LIVE_URL");
            }
            transactionLogger.error("processRefund REQUEST_URL ----> " +REQUEST_URL);

            //RequestParameter
            jsonRequestParameter.put("TransactionId", TransactionID);
            jsonRequestParameter.put("TestMode", TestMode);
            jsonRequestParameter.put("Amount", Amount);

            Signature = CashFlowsCaiboPaymentGatewayUtils.generateSHA512(SecurityToken + jsonRequestParameter.toString().substring(1, jsonRequestParameter.toString().length() - 1));

            //RequestBody
            jsonRequestBodyParameter.put("ApiKey", ApiKey);
            jsonRequestBodyParameter.put("Signature", Signature);
            jsonRequestBodyParameter.put("Request", jsonRequestParameter);
            jsonRequestBodyParameter.put("Version", Version);

            transactionLogger.error("processRefund Request Parameters ---> "+ trackingID + " " +jsonRequestBodyParameter.toString());

            response = CashFlowsCaiboPaymentGatewayUtils.doPostHTTPSURLConnectionClient(REQUEST_URL, jsonRequestBodyParameter.toString());
            transactionLogger.error("processRefund Response ---"+ trackingID + "-->" + response);

            JSONObject logResp    = new JSONObject();
            if (CashFlowsCaiboPaymentGatewayUtils.isJSONValid(response))
            {
                JSONObject respJson   = new JSONObject(response);
                JSONArray keys        = respJson.names();
                for (int i = 0; i < keys.length (); i++)
                {
                    logResp.put(keys.getString(i),respJson.getString (keys.getString(i)));
                }
            }
            transactionLogger.error("processRefund Response for grep ---"+ trackingID + "-->" + logResp);

            StringBuffer error              = new StringBuffer();
            String Message                  = "";
            String TimeStamp                = "";
            String Status                   = "";
            String Arn                      = "";
            String IssuerResponseCode       = "";
            String AcquirerResponseCode     = "";
            String AuthCode                 = "";
            String TransactionIdRes         = "";
            String Code                     = "";

            if(response != null && CashFlowsCaiboPaymentGatewayUtils.isJSONValid(response))
            {
                JSONObject jsonObject   = new JSONObject(response);
                JSONObject responseJSON = null;

                if(jsonObject.has("Response")){
                    responseJSON = jsonObject.getJSONObject("Response");
                }else if(jsonObject.has("Error")){
                    responseJSON = jsonObject.getJSONObject("Error");
                }

                if(responseJSON != null)
                {
                    if(responseJSON.has("Message") && functions.isValueNull(responseJSON.getString("Message"))){
                        Message = responseJSON.getString("Message");
                    }

                    if(responseJSON.has("TimeStamp") && functions.isValueNull(responseJSON.getString("TimeStamp"))){
                        TimeStamp = responseJSON.getString("TimeStamp");
                    }

                    if(responseJSON.has("Status") && functions.isValueNull(responseJSON.getString("Status"))){
                        Status = responseJSON.getString("Status");
                    }

                    if(responseJSON.has("Arn") && functions.isValueNull(responseJSON.getString("Arn"))){
                        Arn = responseJSON.getString("Arn");
                    }

                    if(responseJSON.has("TransactionId") && functions.isValueNull(responseJSON.getString("TransactionId"))){
                        TransactionIdRes = responseJSON.getString("TransactionId");
                    }

                    if(responseJSON.has("IssuerResponseCode") && functions.isValueNull(responseJSON.getString("IssuerResponseCode"))) {
                        IssuerResponseCode = responseJSON.getString("IssuerResponseCode");
                    }

                    if(responseJSON.has("AcquirerResponseCode") && functions.isValueNull(responseJSON.getString("AcquirerResponseCode"))) {
                        AcquirerResponseCode = responseJSON.getString("AcquirerResponseCode");
                    }

                    if(responseJSON.has("AuthCode") && functions.isValueNull(responseJSON.getString("AuthCode"))) {
                        AuthCode = responseJSON.getString("AuthCode");
                    }

                    if (responseJSON.has("Code") && functions.isValueNull(responseJSON.getString("Code"))){
                        Code = responseJSON.getString("Code");
                    }
                    if (responseJSON.has("Details") && functions.isValueNull(String.valueOf(responseJSON.getJSONArray("Details"))))
                    {
                        JSONArray jsonArray = responseJSON.getJSONArray("Details");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject errObject = (JSONObject) jsonArray.get(i);
                            error.append(errObject.getString("Message"));
                        }
                    }


                    if(Status.equalsIgnoreCase("Successful"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setTransactionStatus("success");
                        commResponseVO.setResponseTime(TimeStamp);
                        commResponseVO.setBankTransactionDate(TimeStamp);
                        commResponseVO.setTransactionId(String.valueOf(TransactionIdRes));
                        commResponseVO.setAuthCode(AuthCode);
                        commResponseVO.setErrorCode(AcquirerResponseCode);
                        commResponseVO.setRemark(Message);
                        commResponseVO.setArn(Arn);
                    }
                    else if(Status.equalsIgnoreCase("Failed") || Status.equalsIgnoreCase("Declined"))
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(Message);
                        commResponseVO.setResponseTime(TimeStamp);
                        commResponseVO.setTransactionId(String.valueOf(TransactionIdRes));
                        commResponseVO.setAuthCode(AuthCode);
                        commResponseVO.setErrorCode(AcquirerResponseCode);
                        commResponseVO.setArn(Arn);
                    }
                    else if (functions.isValueNull(error.toString()))
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(error.toString());
                    }
                    else
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CashFlowsCaiboPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while cancelling transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

}
