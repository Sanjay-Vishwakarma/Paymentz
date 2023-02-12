package com.payment.uba_mc;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.PzEncryptor;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.Enum.PZProcessType;

import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.codehaus.jettison.json.JSONArray;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by Admin on 2022-01-22.
 */
public class UBAMCPaymentGateway extends AbstractPaymentGateway
{

    public static final String GATEWAY_TYPE = "ubamc";
    private final static ResourceBundle RB  = LoadProperties.getProperty("com.directi.pg.ubamc");

    private static TransactionLogger transactionLogger = new TransactionLogger(UBAMCPaymentGateway.class.getName());


    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public UBAMCPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingId, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processSale() of UBAMCPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        CommDeviceDetailsVO commDeviceDetailsVO         = commRequestVO.getCommDeviceDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        UBAMCPaymentGatewayUtils uBAMCUtils =  new UBAMCPaymentGatewayUtils();
        boolean isTest                      = gatewayAccount.isTest();
        String is3dSupported                = gatewayAccount.get_3DSupportAccount();

        String merchantId    = gatewayAccount.getMerchantId();
        String merchantName  = "merchant."+gatewayAccount.getMerchantId();
        String password      = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String RETURN_URL            = "";
        String orderReference       = trackingId;
        String transaction          = trackingId;
        String sourceOfFundsType    = "CARD";


        String first_name  = "";
        String last_name   = "";
        String address     = "";
        String city        = "";
        String zip_code    = "";
        String phone       = "";
        String email       = "";
        String country     = "";
        String state                 = "";

        String cardNumber           = "";
        String cardExpiryMonth      = "";
        String cardExpiryYear       = "";
        String cardSecurityCode     = "";
        String nameOnCard           = "";
        String enData               = "";

        String transactionReference = orderReference;
        String customerIpAddress    = "";
        String transactionAmount    = "";
        String transactionCurrency  = "";
        String apiOperation         = "";//SALE Non3D=PAY,3D=INITIATE_AUTHENTICATION

        JSONObject requestJSON       = new JSONObject();
        JSONObject sourceOfFundsJSON = new JSONObject();
        JSONObject providedJSON      = new JSONObject();
        JSONObject cardJSON          = new JSONObject();
        JSONObject expiryJSON        = new JSONObject();

        JSONObject orderJSON         = new JSONObject();
        JSONObject transactionJSON   = new JSONObject();
        JSONObject authentication   = new JSONObject();

        JSONObject customerJSON      = new JSONObject();
        JSONObject billingJSON       = new JSONObject();
        JSONObject addressJSON       = new JSONObject();

        String REQUEST_URL          = "";
        try
        {

            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }

            if(functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname())){
                nameOnCard = commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname();
            }

            cardNumber       = commCardDetailsVO.getCardNum();
            cardExpiryMonth  = commCardDetailsVO.getExpMonth();
            cardExpiryYear   = UBAMCPaymentGatewayUtils.getLast2DigitOfExpiryYear(commCardDetailsVO.getExpYear());
            cardSecurityCode = commCardDetailsVO.getcVV();

            transactionCurrency = transactionDetailsVO.getCurrency();
            transactionAmount   = transactionDetailsVO.getAmount();

            address    = commAddressDetailsVO.getStreet() + "," + commAddressDetailsVO.getCity();
            city       = commAddressDetailsVO.getCity();
            zip_code   = commAddressDetailsVO.getZipCode();
            first_name = commAddressDetailsVO.getFirstname();
            last_name  = commAddressDetailsVO.getLastname();
            email      = commAddressDetailsVO.getEmail();
            phone      = commAddressDetailsVO.getPhone();
            country    = commAddressDetailsVO.getCountry();
            state      = commAddressDetailsVO.getState();

            customerJSON.put("firstName",first_name);
            customerJSON.put("lastName",last_name);
            customerJSON.put("email",email);
            customerJSON.put("phone",phone);
           // requestJSON.put("customer",customerJSON);

            addressJSON.put("city",city);
            addressJSON.put("country",country);
            addressJSON.put("postcodeZip",zip_code);
            addressJSON.put("street",address);

           /* billingJSON.put("address",addressJSON);
            requestJSON.put("billing",billingJSON);*/


            REQUEST_URL = REQUEST_URL + merchantId+ "/order/"+orderReference+"/transaction/"+transaction;

            if(is3dSupported.equalsIgnoreCase("Y") || is3dSupported.equalsIgnoreCase("O"))
            {
                apiOperation = "INITIATE_AUTHENTICATION";

                requestJSON.put("apiOperation",apiOperation);

                authentication.put("channel","PAYER_BROWSER");
                authentication.put("acceptVersions","3DS1");
                requestJSON.put("authentication",authentication);

                requestJSON.put("correlationId",orderReference);

                orderJSON.put("currency",transactionCurrency);
                requestJSON.put("order",orderJSON);

                cardJSON.put("number",cardNumber);
                providedJSON.put("card",cardJSON);
                sourceOfFundsJSON.put("provided",providedJSON);
                requestJSON.put("sourceOfFunds",sourceOfFundsJSON);

                String requestString = requestJSON.toString();

                // for log
                JSONObject requestLogJSON = new JSONObject(requestString);
                cardJSON.put("number",functions.maskingPan(cardNumber));
                providedJSON.put("card",cardJSON);
                sourceOfFundsJSON.put("provided",providedJSON);
                requestLogJSON.put("sourceOfFunds",sourceOfFundsJSON);


                transactionLogger.error("INITIATE_AUTHENTICATION REQUEST_URL " + trackingId + " " + REQUEST_URL);
                transactionLogger.error("INITIATE_AUTHENTICATION REQUEST " + trackingId + " " + requestLogJSON.toString());
                //transactionLogger.error("INITIATE_AUTHENTICATION REQUEST " + trackingId + " " + requestJSON.toString());

                //String responseString = uBAMCUtils.doPostHttpUrlConnection(REQUEST_URL, requestJSON.toString(), merchantName, password);
                String responseString = uBAMCUtils.doPostHttpUrlConnection(REQUEST_URL, requestString, merchantName, password);

                transactionLogger.error("INITIATE_AUTHENTICATION RESPONSE " + trackingId + " " + responseString);

                if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                    JSONObject responseJSON     =  new JSONObject(responseString);
                    JSONObject resOrderJSON     =  null;
                    JSONObject resResponseJSON  =  null;
                    JSONObject respErrorJSON  =  null;

                    String gatewayCode  = "";
                    String respStatus   = "";
                    String authenticationStatus   = "";
                    String respCause            = "";
                    String respexplanation      = "";

                    if(responseJSON.has("error")){
                        respErrorJSON = responseJSON.getJSONObject("error");
                        transactionLogger.error("errorJSON >>>>>> " + trackingId + " " + respErrorJSON);
                        if(respErrorJSON != null  && responseJSON.has("cause")){
                            respCause = respErrorJSON.getString("cause");
                        }
                        if(respErrorJSON != null  && respErrorJSON.has("explanation")){
                            respexplanation = respErrorJSON.getString("explanation");
                        }
                    }

                    if(responseJSON != null){
                        if(responseJSON.has("response")){
                            resResponseJSON = responseJSON.getJSONObject("response");

                            if(resResponseJSON != null  && resResponseJSON.has("gatewayCode")){
                                gatewayCode = resResponseJSON.getString("gatewayCode");
                            }
                        }

                        if(responseJSON.has("order")){
                            resOrderJSON = responseJSON.getJSONObject("order");

                            if(resOrderJSON != null  && resOrderJSON.has("authenticationStatus")){
                                authenticationStatus = resOrderJSON.getString("authenticationStatus");
                            }
                            if(resOrderJSON != null  && resOrderJSON.has("status")){
                                respStatus = resOrderJSON.getString("status");
                            }
                        }
                    }

                    if("AUTHENTICATION_INITIATED".equalsIgnoreCase(respStatus) && "AUTHENTICATION_IN_PROGRESS".equalsIgnoreCase(gatewayCode) )
                    {
                        apiOperation                = "AUTHENTICATE_PAYER";
                        JSONObject deviceJSON       = new JSONObject();
                        JSONObject browserDetails   = new JSONObject();

                        requestJSON       = new JSONObject();
                        sourceOfFundsJSON = new JSONObject();
                        providedJSON      = new JSONObject();
                        cardJSON          = new JSONObject();
                        expiryJSON        = new JSONObject();
                        orderJSON         = new JSONObject();
                        authentication    = new JSONObject();

                        String javaEnabled   = "true";
                        String language      = "en-US";
                        String screenHeight  = "939";
                        String screenWidth   = "1255";
                        String timeZone      = "5";
                        String colorDepth    = "5";
                        String browser       = "Mozilla/5.0 (Android; Mobile; rv:13.0) Gecko/13.0 Firefox/13.0";
                        String javaScriptEnabled            = "true";
                        String secureChallengeWindowSize    = "FULL_SCREEN";
                        String acceptHeaders                = "text/html,application/xhtml xml,application/xml;image/avif,image/webp,image/apng,*/*;application/signed-exchange;v=b3";

                        enData = PzEncryptor.encryptCVV(commCardDetailsVO.getcVV());

                        if (functions.isValueNull(commDeviceDetailsVO.getAcceptHeader())){
                            acceptHeaders = commDeviceDetailsVO.getAcceptHeader();// if not work add else part here
                        }

                        if (functions.isValueNull(commDeviceDetailsVO.getFingerprints()))
                        {
                            HashMap fingerPrintMap      = functions.getFingerPrintMap(commDeviceDetailsVO.getFingerprints());
                            JSONArray screenResolution  = new JSONArray();

                            if (fingerPrintMap.containsKey("userAgent"))
                                browser = (String) fingerPrintMap.get("userAgent");

                            if (fingerPrintMap.containsKey("language")){
                                language = (String) fingerPrintMap.get("language");
                            }

                            if (fingerPrintMap.containsKey("colorDepth")){
                                colorDepth = String.valueOf(fingerPrintMap.get("colorDepth"));
                            }
                            if (fingerPrintMap.containsKey("timezoneOffset")){
                                timeZone = String.valueOf(fingerPrintMap.get("timezoneOffset"));
                            }
                            if (fingerPrintMap.containsKey("screenResolution")){
                                screenResolution = (JSONArray) fingerPrintMap.get("screenResolution");
                            }

                            if (screenResolution.length() > 0){
                                screenHeight = String.valueOf(screenResolution.getString(0));
                            }

                            if (screenResolution.length() > 1){
                                screenWidth = String.valueOf(screenResolution.getString(1));
                            }
                        }else
                        {
                            transactionLogger.error("Inside Fingerprints else condition --------> ");
                            if (functions.isValueNull(commDeviceDetailsVO.getUser_Agent()))
                            {
                                browser = commDeviceDetailsVO.getUser_Agent();
                            }
                            if (functions.isValueNull(commDeviceDetailsVO.getBrowserLanguage()))
                            {
                                language = commDeviceDetailsVO.getBrowserLanguage();
                            }

                            if (functions.isValueNull(commDeviceDetailsVO.getBrowserColorDepth()))
                            {
                                colorDepth = commDeviceDetailsVO.getBrowserColorDepth();
                            }
                            if (functions.isValueNull(commDeviceDetailsVO.getBrowserTimezoneOffset()))
                            {
                                timeZone = commDeviceDetailsVO.getBrowserTimezoneOffset();
                            }
                            if (functions.isValueNull(commDeviceDetailsVO.getBrowserScreenHeight()))
                            {
                                screenHeight = commDeviceDetailsVO.getBrowserScreenHeight();
                            }
                            if (functions.isValueNull(commDeviceDetailsVO.getBrowserScreenWidth()))
                            {
                                screenWidth = commDeviceDetailsVO.getBrowserScreenWidth();
                            }
                            if (functions.isValueNull(commDeviceDetailsVO.getBrowserJavaEnabled())){
                                javaEnabled = commDeviceDetailsVO.getBrowserJavaEnabled();
                            }
                        }

                        if (functions.isValueNull(commMerchantVO.getHostUrl()))
                        {
                            //RETURN_URL = RB.getString("RETURN_URL") + trackingId;
                            RETURN_URL = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL") + trackingId+"&data=" + ESAPI.encoder().encodeForURL(enData);
                        }else{
                            RETURN_URL = RB.getString("RETURN_URL") + trackingId+"&data=" + ESAPI.encoder().encodeForURL(enData);
                        }

                        authentication.put("redirectResponseUrl",RETURN_URL);
                        requestJSON.put("authentication",authentication);

                        requestJSON.put("apiOperation",apiOperation);

                        expiryJSON.put("month",cardExpiryMonth);
                        expiryJSON.put("year",cardExpiryYear);
                        cardJSON.put("number",cardNumber);
                        cardJSON.put("expiry",expiryJSON);
                        cardJSON.put("nameOnCard",nameOnCard);
                        cardJSON.put("securityCode",cardSecurityCode);
                        providedJSON.put("card",cardJSON);
                        sourceOfFundsJSON.put("provided",providedJSON);

                        requestJSON.put("sourceOfFunds",sourceOfFundsJSON);

                        orderJSON.put("currency",transactionCurrency);
                        orderJSON.put("amount",transactionAmount);
                        requestJSON.put("order",orderJSON);

                        browserDetails.put("javaEnabled", javaEnabled);
                        browserDetails.put("language",language);
                        browserDetails.put("screenHeight",screenHeight);
                        browserDetails.put("screenWidth",screenWidth);
                        browserDetails.put("timeZone",timeZone);
                        browserDetails.put("colorDepth",colorDepth);
                        browserDetails.put("javaScriptEnabled",javaScriptEnabled);
                        browserDetails.put("3DSecureChallengeWindowSize",secureChallengeWindowSize);
                        browserDetails.put("acceptHeaders",acceptHeaders);
                        deviceJSON.put("browserDetails",browserDetails);
                        deviceJSON.put("browser",browser);
                        requestJSON.put("device",deviceJSON);

                        String authenticateRequestString = requestJSON.toString();
                        //For Log
                        JSONObject authenticateReqLog = new JSONObject(authenticateRequestString);

                        expiryJSON.put("month",functions.maskingNumber(cardExpiryMonth));
                        expiryJSON.put("year",functions.maskingNumber(cardExpiryYear));
                        cardJSON.put("number",functions.maskingPan(cardNumber));
                        cardJSON.put("expiry",expiryJSON);
                        cardJSON.put("nameOnCard",functions.getNameMasking(nameOnCard));
                        cardJSON.put("securityCode",functions.maskingNumber(cardSecurityCode));
                        providedJSON.put("card",cardJSON);
                        sourceOfFundsJSON.put("provided",providedJSON);

                        authenticateReqLog.put("sourceOfFunds",sourceOfFundsJSON);

                        transactionLogger.error("AUTHENTICATE_PAYER REQUEST_URL " + trackingId + " " + REQUEST_URL);
                        transactionLogger.error("AUTHENTICATE_PAYER REQUEST " + trackingId + " " + authenticateReqLog.toString());
                        //transactionLogger.error("AUTHENTICATE_PAYER REQUEST " + trackingId + " " + requestJSON.toString());

                       // String authenticatePayerResponse = uBAMCUtils.doPostHttpUrlConnection(REQUEST_URL, requestJSON.toString(), merchantName, password);
                        String authenticatePayerResponse = uBAMCUtils.doPostHttpUrlConnection(REQUEST_URL, authenticateRequestString.toString(), merchantName, password);

                        transactionLogger.error("AUTHENTICATE_PAYER RESPONSE " + trackingId + " " + authenticatePayerResponse);

                        if(functions.isValueNull(authenticatePayerResponse) && functions.isJSONValid(authenticatePayerResponse)){
                            JSONObject authPayerResJSON          =  new JSONObject(authenticatePayerResponse);
                            JSONObject authPayerOrderJSON     =  null;
                            JSONObject authenticationJSON     =  null;
                            JSONObject authPayerResponseJSON  =  null;
                            JSONObject errorJSON  =  null;

                            String authGatewayCode  = "";
                            String authStatus       = "";
                            String authRespStatus   = "";
                            String redirectHtml     = "";
                            String explanation      = "";
                            String cause            = "";

                            if(authPayerResJSON != null){
                                if(authPayerResJSON.has("error")){
                                    errorJSON = authPayerResJSON.getJSONObject("error");
                                    transactionLogger.error("errorJSON RESPONSE " + trackingId + " " + errorJSON);
                                    if(errorJSON != null  && errorJSON.has("cause")){
                                        cause = errorJSON.getString("cause");
                                    }
                                    if(errorJSON != null  && errorJSON.has("explanation")){
                                        explanation = errorJSON.getString("explanation");
                                    }

                                    if(functions.isValueNull(cause) && functions.isValueNull(explanation)){
                                        comm3DResponseVO.setStatus("failed");
                                        comm3DResponseVO.setTransactionStatus("failed");
                                        comm3DResponseVO.setRemark(cause);
                                        comm3DResponseVO.setDescription(cause);
                                    }
                                }
                                if(authPayerResJSON.has("response")){
                                    authPayerResponseJSON = authPayerResJSON.getJSONObject("response");
                                    transactionLogger.error("response RESPONSE " + trackingId + " " + authPayerResponseJSON);
                                    if(authPayerResponseJSON != null  && authPayerResponseJSON.has("gatewayCode")){
                                        authGatewayCode = authPayerResponseJSON.getString("gatewayCode");
                                    }
                                }

                                if(authPayerResJSON.has("order")){
                                    authPayerOrderJSON = authPayerResJSON.getJSONObject("order");
                                    transactionLogger.error("order RESPONSE " + trackingId + " " + authPayerOrderJSON);
                                    if(authPayerOrderJSON != null  && resOrderJSON.has("authenticationStatus")){
                                        authStatus = authPayerOrderJSON.getString("authenticationStatus");
                                    }
                                    if(authPayerOrderJSON != null  && authPayerOrderJSON.has("status")){
                                        authRespStatus = authPayerOrderJSON.getString("status");
                                    }
                                }

                                if(authPayerResJSON.has("authentication")){
                                    authenticationJSON = authPayerResJSON.getJSONObject("authentication");
                                    transactionLogger.error("order RESPONSE " + trackingId + " " + authenticationJSON);
                                    if(authenticationJSON != null  && authenticationJSON.has("redirectHtml")){
                                        redirectHtml = authenticationJSON.getString("redirectHtml");
                                    }
                                }

                                if(functions.isValueNull(redirectHtml) &&  "AUTHENTICATION_INITIATED".equalsIgnoreCase(authRespStatus)
                                        && "PENDING".equalsIgnoreCase(authGatewayCode)){
                                    comm3DResponseVO.setStatus("pending3DConfirmation");
                                    comm3DResponseVO.setUrlFor3DRedirect(redirectHtml);
                                }else if(functions.isValueNull(explanation) && functions.isValueNull(cause)){
                                    comm3DResponseVO.setStatus("Failed");
                                    comm3DResponseVO.setTransactionStatus("Failed");
                                    comm3DResponseVO.setRemark(cause);
                                    comm3DResponseVO.setDescription(explanation);
                                }else{
                                    comm3DResponseVO.setStatus("Failed");
                                    comm3DResponseVO.setTransactionStatus("Failed");
                                    comm3DResponseVO.setRemark(authRespStatus);
                                    comm3DResponseVO.setDescription(authGatewayCode);
                                }
                            }else{
                                comm3DResponseVO.setStatus("pending");
                                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                                comm3DResponseVO.setTransactionStatus("pending");
                            }
                        }else{
                            comm3DResponseVO.setStatus("pending");
                            comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            comm3DResponseVO.setTransactionStatus("pending");
                        }
                    }else if("AUTHENTICATION_UNSUCCESSFUL".equalsIgnoreCase(respStatus) && "DECLINED".equalsIgnoreCase(gatewayCode) )
                    {
                        comm3DResponseVO.setStatus("failed");
                        comm3DResponseVO.setTransactionStatus("failed");
                        comm3DResponseVO.setRemark(respStatus);
                        comm3DResponseVO.setDescription(gatewayCode);
                    }else{
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setTransactionStatus("pending");
                    }
                }else{
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }
            }else{
                apiOperation         = "PAY";
                requestJSON.put("apiOperation",apiOperation);

                sourceOfFundsJSON.put("type",sourceOfFundsType);
                expiryJSON.put("month",cardExpiryMonth);
                expiryJSON.put("year",cardExpiryYear);
                cardJSON.put("number", cardNumber);
                cardJSON.put("expiry", expiryJSON);
                cardJSON.put("nameOnCard", nameOnCard);
                cardJSON.put("securityCode", cardSecurityCode);
                providedJSON.put("card", cardJSON);
                sourceOfFundsJSON.put("provided",providedJSON);

                requestJSON.put("sourceOfFunds", sourceOfFundsJSON);

                orderJSON.put("reference", orderReference);
                orderJSON.put("currency", transactionCurrency);
                orderJSON.put("amount", transactionAmount);
                requestJSON.put("order",orderJSON);

                transactionJSON.put("reference",transactionReference);
                requestJSON.put("transaction",transactionJSON);

                String requestString = requestJSON.toString();

                // for log
                expiryJSON.put("month",functions.maskingNumber(cardExpiryMonth));
                expiryJSON.put("year",functions.maskingNumber(cardExpiryYear));
                cardJSON.put("number", functions.maskingPan(cardNumber));
                cardJSON.put("expiry", expiryJSON);
                cardJSON.put("nameOnCard", functions.getNameMasking(nameOnCard));
                cardJSON.put("securityCode", functions.maskingNumber(cardSecurityCode));
                providedJSON.put("card", cardJSON);
                sourceOfFundsJSON.put("provided",providedJSON);

                JSONObject requestLogJOSN = new JSONObject(requestString);
                requestLogJOSN.put("sourceOfFunds", sourceOfFundsJSON);

                transactionLogger.error("RequestString REQUEST_URL " + trackingId + " " + REQUEST_URL);
               // transactionLogger.error("RequestString " + trackingId + " " + requestString.toString());
                transactionLogger.error("RequestLogString " + trackingId + " " + requestLogJOSN.toString());

                String responseString = uBAMCUtils.doPostHttpUrlConnection(REQUEST_URL, requestString.toString(), merchantName, password);
                transactionLogger.error("responseString "+trackingId+" " +responseString);

                if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                    JSONObject responseJSON     =  new JSONObject(responseString);
                    JSONObject resOrderJSON             =  null;
                    JSONObject resTransactionJSON       =  null;
                    JSONObject resResponseJSON          =  null;
                    JSONObject resErrorJSON             =  null;
                    String resAmount    = "";
                    String resStatus    = "";
                    String authenticationStatus = "";
                    String acquirerMessage      = "";
                    String gatewayCode          = "";
                    String transactionId        = "";
                    String respId               = "";
                    String error                = "";
                    String lastUpdatedTime     = "";
                    String authorizationCode    = "";
                    String acquirerCode         = "";

                    if(responseJSON != null){
                        if(responseJSON.has("error")){
                            resErrorJSON = responseJSON.getJSONObject("error");
                            transactionLogger.error("resErrorJSON " + trackingId + " " + resResponseJSON);
                            if(resErrorJSON != null){
                                error   = resErrorJSON.getString("explanation");
                            }
                        }
                        if(responseJSON.has("response")){
                            resResponseJSON =  responseJSON.getJSONObject("response");
                            transactionLogger.error("resResponseJSON "+trackingId+" " +resResponseJSON);
                            if(resResponseJSON != null){
                                if(resResponseJSON.has("acquirerMessage")){
                                    acquirerMessage = resResponseJSON.getString("acquirerMessage");
                                }
                                if(resResponseJSON.has("gatewayCode")){
                                    gatewayCode = resResponseJSON.getString("gatewayCode");
                                }
                                if(resResponseJSON.has("acquirerCode")){
                                    acquirerCode    = resResponseJSON.getString("acquirerCode");
                                }
                            }
                        }
                        if(responseJSON.has("order")){
                            resOrderJSON = responseJSON.getJSONObject("order");
                            transactionLogger.error("resOrderJSON "+trackingId+" " +resOrderJSON);
                            if(resOrderJSON != null){
                                if(resOrderJSON.has("amount")){
                                    resAmount = resOrderJSON.getString("amount");
                                }
                                if(resOrderJSON.has("authenticationStatus")){
                                    authenticationStatus = resOrderJSON.getString("authenticationStatus");
                                }
                                if(resOrderJSON.has("status")){
                                    resStatus = resOrderJSON.getString("status");
                                }
                                if(resOrderJSON.has("lastUpdatedTime")){
                                    lastUpdatedTime = resOrderJSON.getString("lastUpdatedTime");
                                }
                            }
                        }

                        if(responseJSON.has("transaction")){
                            resTransactionJSON = responseJSON.getJSONObject("transaction");
                            transactionLogger.error("resTransactionJSON "+trackingId+" " +resTransactionJSON);
                            if(resTransactionJSON != null){
                                if(resTransactionJSON.has("id")){
                                    transactionId =  resTransactionJSON.getString("id");
                                }
                                if(resTransactionJSON.has("authorizationCode")){
                                    authorizationCode =  resTransactionJSON.getString("authorizationCode");
                                }
                                if(resTransactionJSON.has("acquirer")){
                                    JSONObject acquirerJSON = resTransactionJSON.getJSONObject("acquirer");
                                    if(acquirerJSON != null){
                                        if(acquirerJSON.has("id")){
                                            respId =  acquirerJSON.getString("id");
                                        }
                                    }
                                }
                            }
                        }

                    }

                    if ("CAPTURED".equalsIgnoreCase(resStatus) && "APPROVED".equalsIgnoreCase(gatewayCode))
                    {
                        comm3DResponseVO.setStatus("Success");
                        comm3DResponseVO.setTransactionStatus("Success");
                        comm3DResponseVO.setRemark(acquirerMessage);
                        comm3DResponseVO.setDescription(acquirerMessage);
                        comm3DResponseVO.setAmount(resAmount);
                        comm3DResponseVO.setTransactionId(transactionId);
                        comm3DResponseVO.setResponseHashInfo(respId);
                        comm3DResponseVO.setResponseTime(lastUpdatedTime);
                        comm3DResponseVO.setAuthCode(authorizationCode);
                        comm3DResponseVO.setErrorCode(acquirerCode);
                    }else if (functions.isValueNull(error) || "DECLINED".equalsIgnoreCase(gatewayCode) || "EXPIRED_CARD".equalsIgnoreCase(gatewayCode) || "TIMED_OUT".equalsIgnoreCase(gatewayCode)
                            || "ACQUIRER_SYSTEM_ERROR".equalsIgnoreCase(gatewayCode)  || "UNSPECIFIED_FAILURE".equalsIgnoreCase(gatewayCode) || "UNKNOWN".equalsIgnoreCase(gatewayCode))
                    {
                        comm3DResponseVO.setStatus("Failed");
                        comm3DResponseVO.setTransactionStatus("Failed");
                        comm3DResponseVO.setRemark(acquirerMessage);
                        comm3DResponseVO.setDescription(acquirerMessage);
                        comm3DResponseVO.setAmount(resAmount);
                        comm3DResponseVO.setTransactionId(transactionId);
                        comm3DResponseVO.setResponseHashInfo(respId);
                        comm3DResponseVO.setResponseTime(lastUpdatedTime);
                        comm3DResponseVO.setErrorCode(acquirerCode);
                    }else
                    {
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setTransactionStatus("pending");
                    }
                }else{
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("SSLHandshakeException ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(UBAMCPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }


    public GenericResponseVO processAuthentication(String trackingId, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processAuthentication() of UBAMCPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        CommDeviceDetailsVO commDeviceDetailsVO         = commRequestVO.getCommDeviceDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        UBAMCPaymentGatewayUtils uBAMCUtils =  new UBAMCPaymentGatewayUtils();
        boolean isTest                      = gatewayAccount.isTest();
        String is3dSupported                = gatewayAccount.get_3DSupportAccount();

        String merchantId    = gatewayAccount.getMerchantId();
        String merchantName  = "merchant."+gatewayAccount.getMerchantId();
        String password      = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String RETURN_URL            = "";


        String orderReference       = trackingId;
        String transaction          = "A_"+orderReference;
        String sourceOfFundsType    = "CARD";

        String cardNumber           = "";
        String cardExpiryMonth      = "";
        String cardExpiryYear       = "";
        String cardSecurityCode     = "";
        String nameOnCard           = "";

        String transactionReference = transaction;
        String customerIpAddress    = "";
        String transactionAmount    = "";
        String transactionCurrency  = "";
        String targetTransactionId  = "";
        String apiOperation         = "";
        String enData               = "";


        JSONObject requestJSON       = new JSONObject();
        JSONObject sourceOfFundsJSON = new JSONObject();
        JSONObject providedJSON      = new JSONObject();
        JSONObject cardJSON          = new JSONObject();
        JSONObject expiryJSON        = new JSONObject();

        JSONObject orderJSON         = new JSONObject();
        JSONObject transactionJSON   = new JSONObject();
        JSONObject customerJSON      = new JSONObject();
        JSONObject authentication   = new JSONObject();

        String REQUEST_URL          = "";
        try
        {

            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }

            cardNumber       = commCardDetailsVO.getCardNum();
            cardExpiryMonth  = commCardDetailsVO.getExpMonth();
            cardExpiryYear   = UBAMCPaymentGatewayUtils.getLast2DigitOfExpiryYear(commCardDetailsVO.getExpYear());
            cardSecurityCode = commCardDetailsVO.getcVV();

            transactionCurrency  = transactionDetailsVO.getCurrency();
            transactionAmount   = transactionDetailsVO.getAmount();

            if(functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname()))
                nameOnCard   = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();
            else {
                nameOnCard  = "customer";
            }

            REQUEST_URL = REQUEST_URL + merchantId+ "/order/"+orderReference+"/transaction/"+transaction;


            if(is3dSupported.equalsIgnoreCase("Y") || is3dSupported.equalsIgnoreCase("O"))
            {

                apiOperation = "INITIATE_AUTHENTICATION";

                requestJSON.put("apiOperation",apiOperation);

                authentication.put("channel","PAYER_BROWSER");
                authentication.put("acceptVersions","3DS1");
                requestJSON.put("authentication",authentication);

                requestJSON.put("correlationId",orderReference);

                orderJSON.put("currency",transactionCurrency);
                requestJSON.put("order",orderJSON);

                cardJSON.put("number",cardNumber);
                providedJSON.put("card",cardJSON);
                sourceOfFundsJSON.put("provided",providedJSON);
                requestJSON.put("sourceOfFunds",sourceOfFundsJSON);

                String initRequestString  = requestJSON.toString();

                //Log
                JSONObject initLogJSON = new JSONObject(initRequestString);
                cardJSON.put("number",functions.maskingPan(cardNumber));
                providedJSON.put("card",cardJSON);
                sourceOfFundsJSON.put("provided",providedJSON);
                initLogJSON.put("sourceOfFunds",sourceOfFundsJSON);

                transactionLogger.error("INITIATE_AUTHENTICATION REQUEST_URL " + trackingId + " " + REQUEST_URL);
                transactionLogger.error("INITIATE_AUTHENTICATION REQUEST " + trackingId + " " + initLogJSON.toString());

                //String responseString = uBAMCUtils.doPostHttpUrlConnection(REQUEST_URL, requestJSON.toString(), merchantName, password);
                String responseString = uBAMCUtils.doPostHttpUrlConnection(REQUEST_URL, initRequestString.toString(), merchantName, password);

                transactionLogger.error("INITIATE_AUTHENTICATION RESPONSE " + trackingId + " " + responseString);

                if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                    JSONObject responseJSON     =  new JSONObject(responseString);
                    JSONObject resOrderJSON     =  null;
                    JSONObject resResponseJSON  =  null;

                    String gatewayCode  = "";
                    String respStatus   = "";
                    String authenticationStatus   = "";

                    if(responseJSON != null){
                        if(responseJSON.has("response")){
                            resResponseJSON = responseJSON.getJSONObject("response");

                            if(resResponseJSON != null  && resResponseJSON.has("gatewayCode")){
                                gatewayCode = resResponseJSON.getString("gatewayCode");
                            }
                        }

                        if(responseJSON.has("order")){
                            resOrderJSON = responseJSON.getJSONObject("order");

                            if(resOrderJSON != null  && resOrderJSON.has("authenticationStatus")){
                                authenticationStatus = resOrderJSON.getString("authenticationStatus");
                            }
                            if(resOrderJSON != null  && resOrderJSON.has("status")){
                                respStatus = resOrderJSON.getString("status");
                            }
                        }
                    }

                    if("AUTHENTICATION_INITIATED".equalsIgnoreCase(respStatus) && "AUTHENTICATION_IN_PROGRESS".equalsIgnoreCase(gatewayCode) )
                    {
                        apiOperation                = "AUTHENTICATE_PAYER";
                        JSONObject deviceJSON       = new JSONObject();
                        JSONObject browserDetails   = new JSONObject();

                        requestJSON       = new JSONObject();
                        sourceOfFundsJSON = new JSONObject();
                        providedJSON      = new JSONObject();
                        cardJSON          = new JSONObject();
                        expiryJSON        = new JSONObject();
                        orderJSON         = new JSONObject();
                        authentication    = new JSONObject();

                        String javaEnabled   = "true";
                        String language      = "en-US";
                        String screenHeight  = "939";
                        String screenWidth   = "1255";
                        String timeZone      = "5";
                        String colorDepth    = "5";
                        String browser       = "Mozilla/5.0 (Android; Mobile; rv:13.0) Gecko/13.0 Firefox/13.0";
                        String javaScriptEnabled            = "true";
                        String secureChallengeWindowSize    = "FULL_SCREEN";
                        String acceptHeaders                = "text/html,application/xhtml xml,application/xml;image/avif,image/webp,image/apng,*/*;application/signed-exchange;v=b3";

                        enData = PzEncryptor.encryptCVV(commCardDetailsVO.getcVV());

                        if (functions.isValueNull(commDeviceDetailsVO.getAcceptHeader())){
                            acceptHeaders = commDeviceDetailsVO.getAcceptHeader();// if not work add else part here
                        }

                        if (functions.isValueNull(commDeviceDetailsVO.getFingerprints()))
                        {
                            HashMap fingerPrintMap      = functions.getFingerPrintMap(commDeviceDetailsVO.getFingerprints());
                            JSONArray screenResolution  = new JSONArray();

                            if (fingerPrintMap.containsKey("userAgent"))
                                browser = (String) fingerPrintMap.get("userAgent");

                            if (fingerPrintMap.containsKey("language")){
                                language = (String) fingerPrintMap.get("language");
                            }

                            if (fingerPrintMap.containsKey("colorDepth")){
                                colorDepth = String.valueOf(fingerPrintMap.get("colorDepth"));
                            }
                            if (fingerPrintMap.containsKey("timezoneOffset")){
                                timeZone = String.valueOf(fingerPrintMap.get("timezoneOffset"));
                            }
                            if (fingerPrintMap.containsKey("screenResolution")){
                                screenResolution = (JSONArray) fingerPrintMap.get("screenResolution");
                            }

                            if (screenResolution.length() > 0){
                                screenHeight = String.valueOf(screenResolution.getString(0));
                            }

                            if (screenResolution.length() > 1){
                                screenWidth = String.valueOf(screenResolution.getString(1));
                            }
                        }else
                        {
                            transactionLogger.error("Inside Fingerprints else condition --------> ");
                            if (functions.isValueNull(commDeviceDetailsVO.getUser_Agent()))
                            {
                                browser = commDeviceDetailsVO.getUser_Agent();
                            }
                            if (functions.isValueNull(commDeviceDetailsVO.getBrowserLanguage()))
                            {
                                language = commDeviceDetailsVO.getBrowserLanguage();
                            }

                            if (functions.isValueNull(commDeviceDetailsVO.getBrowserColorDepth()))
                            {
                                colorDepth = commDeviceDetailsVO.getBrowserColorDepth();
                            }
                            if (functions.isValueNull(commDeviceDetailsVO.getBrowserTimezoneOffset()))
                            {
                                timeZone = commDeviceDetailsVO.getBrowserTimezoneOffset();
                            }
                            if (functions.isValueNull(commDeviceDetailsVO.getBrowserScreenHeight()))
                            {
                                screenHeight = commDeviceDetailsVO.getBrowserScreenHeight();
                            }
                            if (functions.isValueNull(commDeviceDetailsVO.getBrowserScreenWidth()))
                            {
                                screenWidth = commDeviceDetailsVO.getBrowserScreenWidth();
                            }
                            if (functions.isValueNull(commDeviceDetailsVO.getBrowserJavaEnabled())){
                                javaEnabled = commDeviceDetailsVO.getBrowserJavaEnabled();
                            }
                        }

                        if (functions.isValueNull(commMerchantVO.getHostUrl()))
                        {
                            //RETURN_URL = RB.getString("RETURN_URL") + trackingId;
                            RETURN_URL = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL") + trackingId+"&data=" + ESAPI.encoder().encodeForURL(enData);
                        }else{
                            RETURN_URL = RB.getString("RETURN_URL") + trackingId+"&data=" + ESAPI.encoder().encodeForURL(enData);
                        }

                        authentication.put("redirectResponseUrl",RETURN_URL);
                        requestJSON.put("authentication",authentication);

                        requestJSON.put("apiOperation",apiOperation);

                        expiryJSON.put("month",cardExpiryMonth);
                        expiryJSON.put("year",cardExpiryYear);
                        cardJSON.put("number",cardNumber);
                        cardJSON.put("expiry",expiryJSON);
                        cardJSON.put("nameOnCard",nameOnCard);
                        cardJSON.put("securityCode",cardSecurityCode);
                        providedJSON.put("card",cardJSON);
                        sourceOfFundsJSON.put("provided",providedJSON);

                        requestJSON.put("sourceOfFunds",sourceOfFundsJSON);

                        orderJSON.put("currency",transactionCurrency);
                        orderJSON.put("amount",transactionAmount);
                        requestJSON.put("order",orderJSON);

                        browserDetails.put("javaEnabled", javaEnabled);
                        browserDetails.put("language",language);
                        browserDetails.put("screenHeight",screenHeight);
                        browserDetails.put("screenWidth",screenWidth);
                        browserDetails.put("timeZone",timeZone);
                        browserDetails.put("colorDepth",colorDepth);
                        browserDetails.put("javaScriptEnabled",javaScriptEnabled);
                        browserDetails.put("3DSecureChallengeWindowSize",secureChallengeWindowSize);
                        browserDetails.put("acceptHeaders",acceptHeaders);
                        deviceJSON.put("browserDetails",browserDetails);
                        deviceJSON.put("browser",browser);
                        requestJSON.put("device",deviceJSON);

                        String authPayReqString = requestJSON.toString();

                         //Log
                        JSONObject authPayLogJSON =  new JSONObject(authPayReqString);
                        expiryJSON.put("month",functions.maskingNumber(cardExpiryMonth));
                        expiryJSON.put("year",functions.maskingNumber(cardExpiryYear));
                        cardJSON.put("number",functions.maskingPan(cardNumber));
                        cardJSON.put("expiry",expiryJSON);
                        cardJSON.put("nameOnCard",functions.getNameMasking(nameOnCard));
                        cardJSON.put("securityCode",functions.maskingNumber(cardSecurityCode));
                        providedJSON.put("card",cardJSON);
                        sourceOfFundsJSON.put("provided",providedJSON);
                        authPayLogJSON.put("sourceOfFunds",sourceOfFundsJSON);


                        transactionLogger.error("AUTHENTICATE_PAYER REQUEST_URL " + trackingId + " " + REQUEST_URL);
                        transactionLogger.error("AUTHENTICATE_PAYER REQUEST " + trackingId + " " + authPayLogJSON.toString());

                        //String authenticatePayerResponse = uBAMCUtils.doPostHttpUrlConnection(REQUEST_URL, requestJSON.toString(), merchantName, password);
                        String authenticatePayerResponse = uBAMCUtils.doPostHttpUrlConnection(REQUEST_URL, authPayReqString.toString(), merchantName, password);

                        transactionLogger.error("AUTHENTICATE_PAYER RESPONSE " + trackingId + " " + authenticatePayerResponse);

                        if(functions.isValueNull(authenticatePayerResponse) && functions.isJSONValid(authenticatePayerResponse)){
                            JSONObject authPayerResJSON          =  new JSONObject(authenticatePayerResponse);
                            JSONObject authPayerOrderJSON     =  null;
                            JSONObject authenticationJSON     =  null;
                            JSONObject authPayerResponseJSON  =  null;
                            JSONObject errorJSON  =  null;

                            String authGatewayCode  = "";
                            String authStatus       = "";
                            String authRespStatus   = "";
                            String redirectHtml     = "";
                            String explanation      = "";
                            String cause            = "";

                            if(authPayerResJSON != null){
                                if(authPayerResJSON.has("error")){
                                    errorJSON = authPayerResJSON.getJSONObject("error");
                                    transactionLogger.error("errorJSON >>>>>> " + trackingId + " " + errorJSON);
                                    if(errorJSON != null  && errorJSON.has("cause")){
                                        cause = errorJSON.getString("cause");
                                    }
                                    if(errorJSON != null  && errorJSON.has("explanation")){
                                        explanation = errorJSON.getString("explanation");
                                    }
                                }
                                if(authPayerResJSON.has("response")){
                                    authPayerResponseJSON = authPayerResJSON.getJSONObject("response");
                                    transactionLogger.error("response >>>> " + trackingId + " " + authPayerResponseJSON);
                                    if(authPayerResponseJSON != null  && authPayerResponseJSON.has("gatewayCode")){
                                        authGatewayCode = authPayerResponseJSON.getString("gatewayCode");
                                    }
                                }

                                if(authPayerResJSON.has("order")){
                                    authPayerOrderJSON = authPayerResJSON.getJSONObject("order");
                                    transactionLogger.error("order >>>> " + trackingId + " " + authPayerOrderJSON);
                                    if(authPayerOrderJSON != null  && resOrderJSON.has("authenticationStatus")){
                                        authStatus = authPayerOrderJSON.getString("authenticationStatus");
                                    }
                                    if(authPayerOrderJSON != null  && authPayerOrderJSON.has("status")){
                                        authRespStatus = authPayerOrderJSON.getString("status");
                                    }
                                }

                                if(authPayerResJSON.has("authentication")){
                                    authenticationJSON = authPayerResJSON.getJSONObject("authentication");
                                    transactionLogger.error("authentication >>>>> " + trackingId + " " + authenticationJSON);
                                    if(authenticationJSON != null  && authenticationJSON.has("redirectHtml")){
                                        redirectHtml = authenticationJSON.getString("redirectHtml");
                                    }
                                }

                                if(functions.isValueNull(redirectHtml) &&  "AUTHENTICATION_INITIATED".equalsIgnoreCase(authRespStatus)
                                        && "PENDING".equalsIgnoreCase(authGatewayCode)){
                                    comm3DResponseVO.setStatus("pending3DConfirmation");
                                    comm3DResponseVO.setUrlFor3DRedirect(redirectHtml);
                                }else if(functions.isValueNull(explanation) && functions.isValueNull(cause)){
                                    comm3DResponseVO.setStatus("Failed");
                                    comm3DResponseVO.setTransactionStatus("Failed");
                                    comm3DResponseVO.setRemark(cause);
                                    comm3DResponseVO.setDescription(explanation);
                                }else{
                                    comm3DResponseVO.setStatus("Failed");
                                    comm3DResponseVO.setTransactionStatus("Failed");
                                    comm3DResponseVO.setRemark(authRespStatus);
                                    comm3DResponseVO.setDescription(authGatewayCode);
                                }
                            }else{
                                comm3DResponseVO.setStatus("pending");
                                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                                comm3DResponseVO.setTransactionStatus("pending");
                            }
                        }else{
                            comm3DResponseVO.setStatus("pending");
                            comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            comm3DResponseVO.setTransactionStatus("pending");
                        }
                    }else if("AUTHENTICATION_UNSUCCESSFUL".equalsIgnoreCase(respStatus) && "DECLINED".equalsIgnoreCase(gatewayCode) )
                    {
                        comm3DResponseVO.setStatus("failed");
                        comm3DResponseVO.setTransactionStatus("failed");
                        comm3DResponseVO.setRemark(respStatus);
                        comm3DResponseVO.setDescription(gatewayCode);
                    }else{
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setTransactionStatus("pending");
                    }
                }else{
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }

            }else{
                apiOperation         = "AUTHORIZE";

                requestJSON.put("apiOperation",apiOperation);

                sourceOfFundsJSON.put("type",sourceOfFundsType);
                expiryJSON.put("month",cardExpiryMonth);
                expiryJSON.put("year",cardExpiryYear);
                cardJSON.put("number",cardNumber);
                cardJSON.put("expiry",expiryJSON);
                cardJSON.put("nameOnCard",nameOnCard);
                cardJSON.put("securityCode",cardSecurityCode);
                providedJSON.put("card",cardJSON);
                sourceOfFundsJSON.put("provided",providedJSON);

                requestJSON.put("sourceOfFunds",sourceOfFundsJSON);

                orderJSON.put("reference",orderReference);
                orderJSON.put("currency",transactionCurrency);
                orderJSON.put("amount",transactionAmount);
                requestJSON.put("order",orderJSON);

                transactionJSON.put("reference",transactionReference);
                requestJSON.put("transaction",transactionJSON);

                String requestString = requestJSON.toString();
                JSONObject requestLogJSON = new JSONObject(requestString);

                expiryJSON.put("month",functions.maskingNumber(cardExpiryMonth));
                expiryJSON.put("year",functions.maskingNumber(cardExpiryYear));
                cardJSON.put("number",functions.maskingPan(cardNumber));
                cardJSON.put("expiry",expiryJSON);
                cardJSON.put("nameOnCard",functions.getNameMasking(nameOnCard));
                cardJSON.put("securityCode",functions.maskingNumber(cardSecurityCode));
                providedJSON.put("card",cardJSON);
                sourceOfFundsJSON.put("provided",providedJSON);
                requestLogJSON.put("sourceOfFunds",sourceOfFundsJSON);


                transactionLogger.error("RequestString REQUEST_URL " + trackingId + " " + REQUEST_URL);
                transactionLogger.error("RequestString " + trackingId + " " + requestLogJSON.toString());

                //String responseString = uBAMCUtils.doPostHttpUrlConnection(REQUEST_URL, requestJSON.toString(), merchantName, password);
                String responseString   = uBAMCUtils.doPostHttpUrlConnection(REQUEST_URL, requestString.toString(), merchantName, password);
                transactionLogger.error("responseString " + trackingId + " " + responseString.toString());

                if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                    JSONObject responseJSON        =  new JSONObject(responseString);
                    JSONObject resOrderJSON        =  null;
                    JSONObject resTransactionJSON  =  null;
                    JSONObject resResponseJSON     =  null;
                    JSONObject resErrorJSON        =  null;
                    String resAmount    = "";
                    String resStatus    = "";
                    String authenticationStatus = "";
                    String acquirerMessage      = "";
                    String gatewayCode          = "";
                    String transactionId        = "";
                    String error                = "";
                    String lastUpdatedTime     = "";
                    String authorizationCode    = "";
                    String acquirerCode         = "";
                    String respId               = "";

                    if(responseJSON != null){

                        if(responseJSON.has("error")){
                            resErrorJSON = responseJSON.getJSONObject("error");
                            transactionLogger.error("resErrorJSON " + trackingId + " " + resResponseJSON);
                            if(resErrorJSON != null){
                                error   = resErrorJSON.getString("explanation");
                            }
                        }
                        if(responseJSON.has("response")){
                            resResponseJSON =  responseJSON.getJSONObject("response");
                            transactionLogger.error("resResponseJSON "+trackingId+" " +resResponseJSON);
                            if(resResponseJSON != null){
                                if(resResponseJSON.has("acquirerMessage")){
                                    acquirerMessage = resResponseJSON.getString("acquirerMessage");
                                }
                                if(resResponseJSON.has("gatewayCode")){
                                    gatewayCode = resResponseJSON.getString("gatewayCode");
                                }
                                if(resResponseJSON.has("acquirerCode")){
                                    acquirerCode    = resResponseJSON.getString("acquirerCode");
                                }
                            }
                        }
                        if(responseJSON.has("order")){
                            resOrderJSON = responseJSON.getJSONObject("order");
                            transactionLogger.error("resOrderJSON "+trackingId+" " +resOrderJSON);
                            if(resOrderJSON != null){
                                if(resOrderJSON.has("amount")){
                                    resAmount = resOrderJSON.getString("amount");
                                }
                                if(resOrderJSON.has("authenticationStatus")){
                                    authenticationStatus = resOrderJSON.getString("authenticationStatus");
                                }
                                if(resOrderJSON.has("status")){
                                    resStatus = resOrderJSON.getString("status");
                                }
                                if(resOrderJSON.has("lastUpdatedTime")){
                                    lastUpdatedTime = resOrderJSON.getString("lastUpdatedTime");
                                }
                            }
                        }

                        if(responseJSON.has("transaction")){
                            resTransactionJSON = responseJSON.getJSONObject("transaction");
                            transactionLogger.error("resTransactionJSON "+trackingId+" " +resTransactionJSON);
                            if(resTransactionJSON != null){
                                if(resTransactionJSON.has("authorizationCode")){
                                    authorizationCode =  resTransactionJSON.getString("authorizationCode");
                                }

                                if(resTransactionJSON.has("acquirer")){
                                    JSONObject acquirerJSON = resTransactionJSON.getJSONObject("acquirer");
                                    if(acquirerJSON != null){
                                        if(acquirerJSON.has("transactionId")){
                                            respId =  acquirerJSON.getString("transactionId");
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if ("AUTHORIZED".equalsIgnoreCase(resStatus) && "APPROVED".equalsIgnoreCase(gatewayCode))
                    {
                        comm3DResponseVO.setStatus("Success");
                        comm3DResponseVO.setTransactionStatus("Success");
                        comm3DResponseVO.setRemark(acquirerMessage);
                        comm3DResponseVO.setDescription(acquirerMessage);
                        comm3DResponseVO.setAmount(resAmount);
                        comm3DResponseVO.setTransactionId(transaction);
                        comm3DResponseVO.setResponseHashInfo(respId);
                        comm3DResponseVO.setResponseTime(lastUpdatedTime);
                        comm3DResponseVO.setAuthCode(authorizationCode);
                        comm3DResponseVO.setErrorCode(acquirerCode);
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }else if (functions.isValueNull(error) || "DECLINED".equalsIgnoreCase(gatewayCode) || "EXPIRED_CARD".equalsIgnoreCase(gatewayCode) || "TIMED_OUT".equalsIgnoreCase(gatewayCode)
                            || "ACQUIRER_SYSTEM_ERROR".equalsIgnoreCase(gatewayCode)  || "UNSPECIFIED_FAILURE".equalsIgnoreCase(gatewayCode) || "UNKNOWN".equalsIgnoreCase(gatewayCode))
                    {
                        comm3DResponseVO.setStatus("Failed");
                        comm3DResponseVO.setTransactionStatus("Failed");
                        if(functions.isValueNull(error)){
                            comm3DResponseVO.setRemark(error);
                            comm3DResponseVO.setDescription(acquirerMessage);
                        }else{
                            comm3DResponseVO.setRemark(acquirerMessage);
                            comm3DResponseVO.setDescription(acquirerMessage);
                        }

                        comm3DResponseVO.setAmount(resAmount);
                        comm3DResponseVO.setTransactionId(transaction);
                        comm3DResponseVO.setResponseHashInfo(respId);
                        comm3DResponseVO.setResponseTime(lastUpdatedTime);
                        comm3DResponseVO.setAuthCode(authorizationCode);
                        comm3DResponseVO.setErrorCode(acquirerCode);
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }else
                    {
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setTransactionStatus("pending");
                        comm3DResponseVO.setRemark("Transaction is pending");
                        comm3DResponseVO.setDescription("Transaction is pending");
                    }
                }
            }


        }
        catch (Exception e)
        {
            transactionLogger.error("SSLHandshakeException ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(UBAMCPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processVoid(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Entering processVoid() of UBAMCPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        UBAMCPaymentGatewayUtils uBAMCUtils =  new UBAMCPaymentGatewayUtils();
        boolean isTest                      = gatewayAccount.isTest();

        String merchantId    = gatewayAccount.getMerchantId();
        String merchantName  = "merchant."+gatewayAccount.getMerchantId();
        String password      = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String orderReference       = trackingId;
        String transaction          = "V_"+trackingId;//new transctionId from our side
        String targetTransactionId  = transactionDetailsVO.getPreviousTransactionId();//transction.id
        String apiOperation         = "VOID";

        JSONObject requestJSON       = new JSONObject();
        JSONObject transactionJSON   = new JSONObject();

        String REQUEST_URL          = "";
        try
        {
            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }

            REQUEST_URL = REQUEST_URL + merchantId+ "/order/"+orderReference+"/transaction/"+transaction;

            requestJSON.put("apiOperation",apiOperation);

            transactionJSON.put("targetTransactionId",targetTransactionId);
            transactionJSON.put("reference",orderReference);
            requestJSON.put("transaction",transactionJSON);

            transactionLogger.error("targetTransactionId " + trackingId + " " + targetTransactionId);
            transactionLogger.error("RequestString REQUEST_URL " + trackingId + " " + REQUEST_URL);
            transactionLogger.error("RequestString " + trackingId + " " + requestJSON.toString());

            String responseString = uBAMCUtils.doPostHttpUrlConnection(REQUEST_URL, requestJSON.toString(), merchantName, password);
            transactionLogger.error("responseString " + trackingId + " " + responseString.toString());

            if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                JSONObject responseJSON        =  new JSONObject(responseString);
                JSONObject resOrderJSON        =  null;
                JSONObject resTransactionJSON  =  null;
                JSONObject resResponseJSON     =  null;
                JSONObject resErrorJSON        =  null;
                String resAmount    = "";
                String resStatus    = "";
                String authenticationStatus = "";
                String acquirerMessage      = "";
                String gatewayCode          = "";
                String transactionId        = "";
                String error                = "";
                String lastUpdatedTime      = "";
                String acquirerCode      = "";

                if(responseJSON != null){

                    if(responseJSON.has("error")){
                        resErrorJSON = responseJSON.getJSONObject("error");
                        transactionLogger.error("resErrorJSON " + trackingId + " " + resResponseJSON);
                        if(resErrorJSON != null){
                            error   = resErrorJSON.getString("explanation");
                        }
                    }
                    if(responseJSON.has("response")){
                        resResponseJSON =  responseJSON.getJSONObject("response");
                        transactionLogger.error("resResponseJSON "+trackingId+" " +resResponseJSON);
                        if(resResponseJSON != null){
                            if(resResponseJSON.has("acquirerMessage")){
                                acquirerMessage = resResponseJSON.getString("acquirerMessage");
                            }
                            if(resResponseJSON.has("gatewayCode")){
                                gatewayCode = resResponseJSON.getString("gatewayCode");
                            }
                            if(resResponseJSON.has("acquirerCode")){
                                acquirerCode = resResponseJSON.getString("acquirerCode");
                            }
                        }
                    }
                    if(responseJSON.has("order")){
                        resOrderJSON = responseJSON.getJSONObject("order");
                        transactionLogger.error("resOrderJSON "+trackingId+" " +resOrderJSON);
                        if(resOrderJSON != null){
                            if(resOrderJSON.has("amount")){
                                resAmount = resOrderJSON.getString("amount");
                            }
                            if(resOrderJSON.has("authenticationStatus")){
                                authenticationStatus = resOrderJSON.getString("authenticationStatus");
                            }
                            if(resOrderJSON.has("status")){
                                resStatus = resOrderJSON.getString("status");
                            }
                            if(resOrderJSON.has("lastUpdatedTime")){
                                lastUpdatedTime = resOrderJSON.getString("lastUpdatedTime");
                            }
                        }
                    }

                    if(responseJSON.has("transaction")){
                        resTransactionJSON = responseJSON.getJSONObject("transaction");
                        transactionLogger.error("resTransactionJSON "+trackingId+" " +resTransactionJSON);
                        if(resTransactionJSON != null){
                            if(resTransactionJSON.has("acquirer")){
                                JSONObject acquirerJSON = resTransactionJSON.getJSONObject("acquirer");
                                if(acquirerJSON != null){
                                    if(acquirerJSON.has("transactionId")){
                                        transactionId =  acquirerJSON.getString("transactionId");
                                    }
                                }
                            }
                        }
                    }
                }
                if ("CANCELLED".equalsIgnoreCase(resStatus) && "APPROVED".equalsIgnoreCase(gatewayCode))
                {
                    comm3DResponseVO.setStatus("Success");
                    comm3DResponseVO.setTransactionStatus("Success");
                    comm3DResponseVO.setRemark(acquirerMessage);
                    comm3DResponseVO.setDescription(acquirerMessage);
                    comm3DResponseVO.setAmount(resAmount);
                    comm3DResponseVO.setTransactionId(transaction);
                    comm3DResponseVO.setResponseHashInfo(transactionId);
                    comm3DResponseVO.setResponseTime(lastUpdatedTime);
                    comm3DResponseVO.setErrorCode(acquirerCode);
                    comm3DResponseVO.setAuthCode(acquirerCode);
                }else if (functions.isValueNull(error) || "DECLINED".equalsIgnoreCase(gatewayCode) || "ACQUIRER_SYSTEM_ERROR".equalsIgnoreCase(gatewayCode)  || "UNSPECIFIED_FAILURE".equalsIgnoreCase(gatewayCode) || "UNKNOWN".equalsIgnoreCase(gatewayCode))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    comm3DResponseVO.setRemark(acquirerMessage);
                    comm3DResponseVO.setDescription(acquirerMessage);
                    comm3DResponseVO.setAmount(resAmount);
                    comm3DResponseVO.setTransactionId(transaction);
                    comm3DResponseVO.setResponseHashInfo(transactionId);
                    comm3DResponseVO.setResponseTime(lastUpdatedTime);
                    comm3DResponseVO.setErrorCode(acquirerCode);
                    comm3DResponseVO.setAuthCode(acquirerCode);
                }else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setRemark("Transaction is pending");
                    comm3DResponseVO.setDescription("Transaction is pending");
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("SSLHandshakeException ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(UBAMCPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }


    public GenericResponseVO processCapture(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Entering processCapture() of UBAMCPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        CommResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        CommDeviceDetailsVO commDeviceDetailsVO         = commRequestVO.getCommDeviceDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        UBAMCPaymentGatewayUtils uBAMCUtils = new UBAMCPaymentGatewayUtils();
        boolean isTest                      = gatewayAccount.isTest();

        String merchantId    = gatewayAccount.getMerchantId();
        String merchantName  = "merchant."+gatewayAccount.getMerchantId();
        String password      = gatewayAccount.getFRAUD_FTP_PASSWORD();



        String orderReference       = trackingId;
        String sourceOfFundsType    = "CARD";

        String cardNumber           = "";
        String cardExpiryMonth      = "";
        String cardExpiryYear       = "";


        String transactionAmount    = "";
        String transactionCurrency  = "";
        String apiOperation         = "CAPTURE";
        String transaction          = "C_"+orderReference;
        String transactionReference = transaction;


        JSONObject requestJSON       = new JSONObject();
        JSONObject sourceOfFundsJSON = new JSONObject();
        JSONObject providedJSON      = new JSONObject();
        JSONObject cardJSON          = new JSONObject();
        JSONObject expiryJSON        = new JSONObject();

        JSONObject orderJSON         = new JSONObject();
        JSONObject transactionJSON   = new JSONObject();

        String REQUEST_URL          = "";
        String nameOnCard          = "";
        try
        {

            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }

            if(functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname()))
                nameOnCard   = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();
            else {
                nameOnCard  = "customer";
            }

            cardNumber       = commCardDetailsVO.getCardNum();
            cardExpiryMonth  = commCardDetailsVO.getExpMonth();
            cardExpiryYear   = UBAMCPaymentGatewayUtils.getLast2DigitOfExpiryYear(commCardDetailsVO.getExpYear());

            transactionCurrency  = transactionDetailsVO.getCurrency();
            transactionAmount   = transactionDetailsVO.getAmount();

            REQUEST_URL = REQUEST_URL + merchantId+ "/order/"+orderReference+"/transaction/"+transaction;

            requestJSON.put("apiOperation",apiOperation);

            sourceOfFundsJSON.put("type",sourceOfFundsType);
            expiryJSON.put("month",cardExpiryMonth);
            expiryJSON.put("year",cardExpiryYear);
            cardJSON.put("number",cardNumber);
            cardJSON.put("nameOnCard",nameOnCard);
            cardJSON.put("expiry",expiryJSON);
            providedJSON.put("card",cardJSON);
            sourceOfFundsJSON.put("provided",providedJSON);
            requestJSON.put("sourceOfFunds",sourceOfFundsJSON);

            orderJSON.put("reference",orderReference);
            requestJSON.put("order",orderJSON);

            transactionJSON.put("amount",transactionAmount);
            transactionJSON.put("currency",transactionCurrency);
            transactionJSON.put("reference",transactionReference);
            requestJSON.put("transaction", transactionJSON);

            String requestString = requestJSON.toString();

            //Log
            JSONObject cpLogJSON = new JSONObject(requestString);

            expiryJSON.put("month",functions.maskingNumber(cardExpiryMonth));
            expiryJSON.put("year",functions.maskingNumber(cardExpiryYear));
            cardJSON.put("number",functions.maskingPan(cardNumber));
            cardJSON.put("nameOnCard",functions.getNameMasking(nameOnCard));
            cardJSON.put("expiry",expiryJSON);
            providedJSON.put("card",cardJSON);
            sourceOfFundsJSON.put("provided",providedJSON);
            cpLogJSON.put("sourceOfFunds",sourceOfFundsJSON);

            transactionLogger.error("RequestString REQUEST_URL " + trackingId + " " + REQUEST_URL);
            transactionLogger.error("RequestString " + trackingId + " " + cpLogJSON.toString());


            //String responseString = uBAMCUtils.doPostHttpUrlConnection(REQUEST_URL, requestJSON.toString(), merchantName, password);
            String responseString = uBAMCUtils.doPostHttpUrlConnection(REQUEST_URL, requestString.toString(), merchantName, password);
            transactionLogger.error("responseString " + trackingId + " " + responseString.toString());

            if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                JSONObject responseJSON             =  new JSONObject(responseString);
                JSONObject resOrderJSON             =  null;
                JSONObject resTransactionJSON       =  null;
                JSONObject resResponseJSON          =  null;
                JSONObject resErrorJSON             =  null;
                String resAmount    = "";
                String resStatus    = "";
                String authenticationStatus    = "";
                String acquirerMessage      = "";
                String gatewayCode          = "";
                String transactionId        = "";
                String error                = "";
                String authorizationCode    = "";
                String acquirerCode         = "";
                String lastUpdatedTime      = "";

                if(responseJSON != null){
                    if(responseJSON.has("error")){
                        resErrorJSON = responseJSON.getJSONObject("error");
                        transactionLogger.error("resErrorJSON " + trackingId + " " + resResponseJSON);
                        if(resErrorJSON != null){
                            error   = resErrorJSON.getString("explanation");
                        }
                    }
                    if(responseJSON.has("response")){
                        resResponseJSON =  responseJSON.getJSONObject("response");
                        transactionLogger.error("resResponseJSON " + trackingId + " " + resResponseJSON);
                        if(resResponseJSON != null){
                            if(resResponseJSON.has("acquirerMessage")){
                                acquirerMessage = resResponseJSON.getString("acquirerMessage");
                            }
                            if(resResponseJSON.has("gatewayCode")){
                                gatewayCode = resResponseJSON.getString("gatewayCode");
                            }
                            if(resResponseJSON.has("acquirerCode")){
                                acquirerCode = resResponseJSON.getString("acquirerCode");
                            }
                        }
                    }
                    if(responseJSON.has("order")){
                        resOrderJSON = responseJSON.getJSONObject("order");
                        transactionLogger.error("resOrderJSON "+trackingId+" " +resOrderJSON);
                        if(resOrderJSON != null){
                            if(resOrderJSON.has("amount")){
                                resAmount = resOrderJSON.getString("amount");
                            }
                            if(resOrderJSON.has("authenticationStatus")){
                                authenticationStatus = resOrderJSON.getString("authenticationStatus");
                            }
                            if(resOrderJSON.has("status")){
                                resStatus = resOrderJSON.getString("status");
                            }
                            if(resOrderJSON.has("lastUpdatedTime")){
                                lastUpdatedTime = resOrderJSON.getString("lastUpdatedTime");
                            }
                        }
                    }

                    if(responseJSON.has("transaction")){
                        resTransactionJSON = responseJSON.getJSONObject("transaction");
                        transactionLogger.error("resTransactionJSON "+trackingId+" " +resTransactionJSON);
                        if(resTransactionJSON != null){
                            if(resTransactionJSON.has("authorizationCode")){
                                authorizationCode =  resTransactionJSON.getString("authorizationCode");
                            }
                            if(resTransactionJSON.has("acquirer")){
                                JSONObject acquirerJSON = resTransactionJSON.getJSONObject("acquirer");
                                if(acquirerJSON != null){
                                    if(acquirerJSON.has("transactionId")){
                                        transactionId =  acquirerJSON.getString("transactionId");
                                    }
                                }
                            }
                        }
                    }
                }

                if ("CAPTURED".equalsIgnoreCase(resStatus) && "APPROVED".equalsIgnoreCase(gatewayCode))
                {
                    comm3DResponseVO.setStatus("Success");
                    comm3DResponseVO.setTransactionStatus("Success");
                    comm3DResponseVO.setRemark(acquirerMessage);
                    comm3DResponseVO.setDescription(acquirerMessage);
                    comm3DResponseVO.setAmount(resAmount);
                    comm3DResponseVO.setTransactionId(transaction);
                    comm3DResponseVO.setResponseHashInfo(transactionId);
                    comm3DResponseVO.setErrorCode(acquirerCode);
                    comm3DResponseVO.setAuthCode(authorizationCode);
                    comm3DResponseVO.setResponseTime(lastUpdatedTime);
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());

                }else if (functions.isValueNull(error) || "DECLINED".equalsIgnoreCase(gatewayCode) || "EXPIRED_CARD".equalsIgnoreCase(gatewayCode) || "TIMED_OUT".equalsIgnoreCase(gatewayCode)
                        || "ACQUIRER_SYSTEM_ERROR".equalsIgnoreCase(gatewayCode)  || "UNSPECIFIED_FAILURE".equalsIgnoreCase(gatewayCode) || "UNKNOWN".equalsIgnoreCase(gatewayCode))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    comm3DResponseVO.setRemark(acquirerMessage);
                    comm3DResponseVO.setDescription(acquirerMessage);
                    comm3DResponseVO.setAmount(resAmount);
                    comm3DResponseVO.setTransactionId(transaction);
                    comm3DResponseVO.setResponseHashInfo(transactionId);
                    comm3DResponseVO.setErrorCode(acquirerCode);
                    comm3DResponseVO.setAuthCode(authorizationCode);
                    comm3DResponseVO.setResponseTime(lastUpdatedTime);
                }else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setRemark("Transaction is pending");
                    comm3DResponseVO.setDescription("Transaction is pending");
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("SSLHandshakeException ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(UBAMCPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }



    public GenericResponseVO processRefund(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Entering processRefund() of UBAMCPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        UBAMCPaymentGatewayUtils uBAMCUtils =  new UBAMCPaymentGatewayUtils();
        boolean isTest                      = gatewayAccount.isTest();

        String merchantId    = gatewayAccount.getMerchantId();
        String merchantName  = "merchant."+gatewayAccount.getMerchantId();
        String password      = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String currency             = "";
        String orderReference       = trackingId;
        String transaction          = "R_"+trackingId;
        String transactionAmount    = "";
        String transactionCurrency  = "";
        String apiOperation         = "REFUND";//auth=AUTHORIZE,SALE=PAY


        JSONObject requestJSON       = new JSONObject();
        JSONObject orderJSON         = new JSONObject();
        JSONObject transactionJSON   = new JSONObject();

        String REQUEST_URL          = "";
        try
        {

            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }

            transactionCurrency  = transactionDetailsVO.getCurrency();
            transactionAmount    = transactionDetailsVO.getAmount();


            REQUEST_URL = REQUEST_URL + merchantId+ "/order/"+orderReference+"/transaction/"+transaction;

            requestJSON.put("apiOperation",apiOperation);

            orderJSON.put("reference",orderReference);
            requestJSON.put("order",orderJSON);

            transactionJSON.put("amount",transactionAmount);
            transactionJSON.put("currency",transactionCurrency);
            transactionJSON.put("reference",transaction);
            requestJSON.put("transaction",transactionJSON);

            transactionLogger.error("Request REQUEST_URL "+trackingId +" "+REQUEST_URL);
            transactionLogger.error("Request  "+trackingId +" "+requestJSON.toString());

            String responseString = uBAMCUtils.doPostHttpUrlConnection(REQUEST_URL, requestJSON.toString(), merchantName, password);

            transactionLogger.error("processRefund Response  "+trackingId +" "+responseString);

            if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                JSONObject responseJSON     =  new JSONObject(responseString);
                JSONObject resOrderJSON             =  null;
                JSONObject resTransactionJSON       =  null;
                JSONObject resResponseJSON          =  null;
                JSONObject resErrorJSON             =  null;
                String resAmount    = "";
                String resStatus    = "";
                String authenticationStatus = "";
                String acquirerMessage      = "";
                String gatewayCode          = "";
                String transactionId        = "";
                String error                = "";
                String acquirerCode             = "";
                String lastUpdatedTime             = "";

                if(responseJSON != null){
                    if(responseJSON.has("error")){
                        resErrorJSON = responseJSON.getJSONObject("error");
                        transactionLogger.error("resErrorJSON " + trackingId + " " + resResponseJSON);
                        if(resErrorJSON != null){
                            error   = resErrorJSON.getString("explanation");
                        }
                    }
                    if(responseJSON.has("response")){
                        resResponseJSON =  responseJSON.getJSONObject("response");
                        transactionLogger.error("resResponseJSON "+trackingId+" " +resResponseJSON);
                        if(resResponseJSON != null){
                            if(resResponseJSON.has("acquirerMessage")){
                                acquirerMessage = resResponseJSON.getString("acquirerMessage");
                            }
                            if(resResponseJSON.has("gatewayCode")){
                                gatewayCode = resResponseJSON.getString("gatewayCode");
                            }
                            if(resResponseJSON.has("acquirerCode")){
                                acquirerCode = resResponseJSON.getString("acquirerCode");
                            }
                        }
                    }
                    if(responseJSON.has("order")){
                        resOrderJSON = responseJSON.getJSONObject("order");
                        transactionLogger.error("resOrderJSON "+trackingId+" " +resOrderJSON);
                        if(resOrderJSON != null){
                            if(resOrderJSON.has("amount")){
                                resAmount = resOrderJSON.getString("amount");
                            }
                            if(resOrderJSON.has("authenticationStatus")){
                                authenticationStatus = resOrderJSON.getString("authenticationStatus");
                            }
                            if(resOrderJSON.has("status")){
                                resStatus = resOrderJSON.getString("status");
                            }
                            if(resOrderJSON.has("currency")){
                                currency = resOrderJSON.getString("currency");
                            }
                            if(resOrderJSON.has("lastUpdatedTime")){
                                lastUpdatedTime = resOrderJSON.getString("lastUpdatedTime");
                            }
                        }
                    }

                    if(responseJSON.has("transaction")){
                        resTransactionJSON = responseJSON.getJSONObject("transaction");
                        transactionLogger.error("resTransactionJSON "+trackingId+" " +resTransactionJSON);
                        if(resTransactionJSON != null){
                            if(resTransactionJSON.has("acquirer")){
                                JSONObject acquirerJSON = resTransactionJSON.getJSONObject("acquirer");
                                if(acquirerJSON != null){
                                    if(acquirerJSON.has("transactionId")){
                                        transactionId =  acquirerJSON.getString("transactionId");
                                    }
                                }
                            }
                        }
                    }
                }
                if ("APPROVED".equalsIgnoreCase(gatewayCode) && ("PARTIALLY_REFUNDED".equalsIgnoreCase(resStatus) || "REFUNDED".equalsIgnoreCase(resStatus)) )
                {
                    comm3DResponseVO.setStatus("Success");
                    comm3DResponseVO.setTransactionStatus("Success");
                    comm3DResponseVO.setRemark(acquirerMessage);
                    comm3DResponseVO.setDescription(resStatus);
                    comm3DResponseVO.setAmount(resAmount);
                    comm3DResponseVO.setTransactionId(transaction);
                    comm3DResponseVO.setResponseHashInfo(transactionId);
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setErrorCode(acquirerCode);
                    comm3DResponseVO.setAuthCode(acquirerCode);
                    comm3DResponseVO.setResponseTime(lastUpdatedTime);
                    comm3DResponseVO.setMerchantId(merchantId);
                }else if (functions.isValueNull(error) || "DECLINED".equalsIgnoreCase(gatewayCode) || "EXPIRED_CARD".equalsIgnoreCase(gatewayCode) || "TIMED_OUT".equalsIgnoreCase(gatewayCode)
                        || "ACQUIRER_SYSTEM_ERROR".equalsIgnoreCase(gatewayCode)  || "UNSPECIFIED_FAILURE".equalsIgnoreCase(gatewayCode) || "UNKNOWN".equalsIgnoreCase(gatewayCode))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    comm3DResponseVO.setRemark(acquirerMessage);
                    comm3DResponseVO.setDescription(acquirerMessage);
                    comm3DResponseVO.setAmount(resAmount);
                    comm3DResponseVO.setTransactionId(transactionId);
                    comm3DResponseVO.setResponseHashInfo(transactionId);
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setErrorCode(acquirerCode);
                    comm3DResponseVO.setErrorCode(acquirerCode);
                    comm3DResponseVO.setAuthCode(acquirerCode);
                    comm3DResponseVO.setResponseTime(lastUpdatedTime);
                }else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }
            }else{
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");
            }

        }
        catch (Exception e)
        {
            transactionLogger.error("SSLHandshakeException ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(UBAMCPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }


    public GenericResponseVO processPayout(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Entering processPayout() of UBAMCPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        CommDeviceDetailsVO commDeviceDetailsVO         = commRequestVO.getCommDeviceDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        UBAMCPaymentGatewayUtils uBAMCUtils =  new UBAMCPaymentGatewayUtils();
        boolean isTest                      = gatewayAccount.isTest();

        String merchantId    = gatewayAccount.getMerchantId();
        String merchantName  = "merchant."+gatewayAccount.getMerchantId();
        String password      = gatewayAccount.getFRAUD_FTP_PASSWORD();



        String orderReference       = trackingId;
        String transaction          = "P_"+trackingId;
        String sourceOfFundsType    = "CARD";

        String cardNumber           = "";
        String cardExpiryMonth      = "";
        String cardExpiryYear       = "";
        String cardSecurityCode     = "";
        String nameOnCard           = "";

        String transactionReference = orderReference;
        String transactionAmount    = "";
        String transactionCurrency  = "";

        //CREDIT_CARD_BILL_PAYMENT Indicates that you are paying a credit card bill.
        // GAMING_WINNINGS Indicates that you are paying out gaming winnings to a payer.
        String disbursementType     = "";
        String apiOperation         = "DISBURSEMENT";//auth=AUTHORIZE,SALE=PAY


        JSONObject requestJSON       = new JSONObject();
        JSONObject sourceOfFundsJSON = new JSONObject();
        JSONObject providedJSON      = new JSONObject();
        JSONObject cardJSON          = new JSONObject();
        JSONObject expiryJSON        = new JSONObject();

        JSONObject orderJSON         = new JSONObject();
        JSONObject transactionJSON   = new JSONObject();
        JSONObject customerJSON      = new JSONObject();

        String REQUEST_URL          = "";
        try
        {

            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }


            cardNumber       = commCardDetailsVO.getCardNum();
            cardExpiryMonth  = commCardDetailsVO.getExpMonth();
            cardExpiryYear   = UBAMCPaymentGatewayUtils.getLast2DigitOfExpiryYear(commCardDetailsVO.getExpYear());
            cardSecurityCode = commCardDetailsVO.getcVV();

            transactionCurrency  = transactionDetailsVO.getCurrency();
            transactionAmount   = transactionDetailsVO.getAmount();

            REQUEST_URL = REQUEST_URL + merchantId+ "/order/"+orderReference+"/transaction/"+transaction;

            requestJSON.put("apiOperation",apiOperation);
            requestJSON.put("disbursementType",disbursementType);

            sourceOfFundsJSON.put("type",sourceOfFundsType);
            expiryJSON.put("month",cardExpiryMonth);
            expiryJSON.put("year",cardExpiryYear);
            cardJSON.put("number",cardNumber);
            cardJSON.put("expiry",expiryJSON);
            cardJSON.put("nameOnCard",nameOnCard);
            cardJSON.put("securityCode",cardSecurityCode);
            providedJSON.put("card",cardJSON);
            sourceOfFundsJSON.put("provided",providedJSON);

            requestJSON.put("sourceOfFunds",sourceOfFundsJSON);

            orderJSON.put("reference",orderReference);
            orderJSON.put("amount",transactionAmount);
            orderJSON.put("currency",transactionCurrency);
            requestJSON.put("order",orderJSON);


            transactionJSON.put("reference",transactionReference);
            requestJSON.put("transaction",transactionJSON);

            transactionLogger.error("Payout Request REQUEST_URL "+trackingId +" "+REQUEST_URL);
            transactionLogger.error("Payout Request "+trackingId +" "+requestJSON.toString());

            String responseString = uBAMCUtils.doPostHttpUrlConnection(REQUEST_URL, requestJSON.toString(), merchantName, password);

            transactionLogger.error("Payout Response "+trackingId +" "+requestJSON.toString());

            if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                JSONObject responseJSON     =  new JSONObject(responseString);
                JSONObject resOrderJSON             =  null;
                JSONObject resTransactionJSON       =  null;
                JSONObject resResponseJSON          =  null;
                JSONObject resErrorJSON             =  null;
                String resAmount    = "";
                String resStatus    = "";
                String authenticationStatus = "";
                String acquirerMessage      = "";
                String gatewayCode          = "";
                String transactionId        = "";
                String error                = "";
                String currency             = "";
                String acquirerCode         = "";
                String lastUpdatedTime      = "";
                String authorizationCode    = "";

                if(responseJSON != null){
                    if(responseJSON.has("error")){
                        resErrorJSON = responseJSON.getJSONObject("error");
                        transactionLogger.error("resErrorJSON " + trackingId + " " + resResponseJSON);
                        if(resErrorJSON != null){
                            error   = resErrorJSON.getString("explanation");
                        }
                    }
                    if(responseJSON.has("response")){
                        resResponseJSON =  responseJSON.getJSONObject("response");
                        transactionLogger.error("resResponseJSON "+trackingId+" " +resResponseJSON);
                        if(resResponseJSON != null){
                            if(resResponseJSON.has("acquirerMessage")){
                                acquirerMessage = resResponseJSON.getString("acquirerMessage");
                            }
                            if(resResponseJSON.has("gatewayCode")){
                                gatewayCode = resResponseJSON.getString("gatewayCode");
                            }
                            if(resResponseJSON.has("acquirerCode")){
                                acquirerCode = resResponseJSON.getString("acquirerCode");
                            }
                        }
                    }
                    if(responseJSON.has("order")){
                        resOrderJSON = responseJSON.getJSONObject("order");
                        transactionLogger.error("resOrderJSON "+trackingId+" " +resOrderJSON);
                        if(resOrderJSON != null){
                            if(resOrderJSON.has("amount")){
                                resAmount = resOrderJSON.getString("amount");
                            }
                            if(resOrderJSON.has("authenticationStatus")){
                                authenticationStatus = resOrderJSON.getString("authenticationStatus");
                            }
                            if(resOrderJSON.has("status")){
                                resStatus = resOrderJSON.getString("status");
                            }
                            if(resOrderJSON.has("currency")){
                                currency = resOrderJSON.getString("currency");
                            }
                            if(resOrderJSON.has("lastUpdatedTime")){
                                lastUpdatedTime = resOrderJSON.getString("lastUpdatedTime");
                            }

                        }
                    }

                    if(responseJSON.has("transaction")){
                        resTransactionJSON = responseJSON.getJSONObject("transaction");
                        transactionLogger.error("resTransactionJSON "+trackingId+" " +resTransactionJSON);
                        if(resTransactionJSON != null){

                            if(resTransactionJSON.has("authorizationCode")){
                                authorizationCode =  resTransactionJSON.getString("authorizationCode");
                            }
                            if(resTransactionJSON.has("acquirer")){
                                JSONObject acquirerJSON = resTransactionJSON.getJSONObject("acquirer");
                                if(acquirerJSON != null){
                                    if(acquirerJSON.has("transactionId")){
                                        transactionId =  acquirerJSON.getString("transactionId");
                                    }
                                }
                            }
                        }
                    }
                }

                if ("DISBURSED".equalsIgnoreCase(resStatus) && "APPROVED".equalsIgnoreCase(gatewayCode))
                {
                    comm3DResponseVO.setStatus("Success");
                    comm3DResponseVO.setTransactionStatus("Success");
                    comm3DResponseVO.setRemark(acquirerMessage);
                    comm3DResponseVO.setDescription(acquirerMessage);
                    comm3DResponseVO.setAmount(resAmount);
                    comm3DResponseVO.setTransactionId(transaction);
                    comm3DResponseVO.setResponseHashInfo(transactionId);
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setErrorCode(acquirerCode);
                    comm3DResponseVO.setAuthCode(authorizationCode);
                    comm3DResponseVO.setResponseTime(lastUpdatedTime);
                    comm3DResponseVO.setMerchantId(merchantId);
                }
                else if (functions.isValueNull(error) || "DECLINED".equalsIgnoreCase(gatewayCode) || "EXPIRED_CARD".equalsIgnoreCase(gatewayCode) || "TIMED_OUT".equalsIgnoreCase(gatewayCode)
                        || "ACQUIRER_SYSTEM_ERROR".equalsIgnoreCase(gatewayCode)  || "UNSPECIFIED_FAILURE".equalsIgnoreCase(gatewayCode) || "UNKNOWN".equalsIgnoreCase(gatewayCode))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    comm3DResponseVO.setRemark(acquirerMessage);
                    comm3DResponseVO.setDescription(acquirerMessage);
                    comm3DResponseVO.setAmount(resAmount);
                    comm3DResponseVO.setTransactionId(transaction);
                    comm3DResponseVO.setResponseHashInfo(transactionId);
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setErrorCode(acquirerCode);
                    comm3DResponseVO.setAuthCode(authorizationCode);
                    comm3DResponseVO.setResponseTime(lastUpdatedTime);
                }else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }
            }else{
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("SSLHandshakeException ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(UBAMCPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }



    public GenericResponseVO processQuery(String trackingId, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processQuery() of UBAMCPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        UBAMCPaymentGatewayUtils uBAMCUtils             =  new UBAMCPaymentGatewayUtils();
        String merchantId    = gatewayAccount.getMerchantId();
        String merchantName  = "merchant."+gatewayAccount.getMerchantId();
        String password      = gatewayAccount.getFRAUD_FTP_PASSWORD();

        boolean isTest = gatewayAccount.isTest();

        String REQUEST_URL          = "";
        String orderReference       = "";
        String transaction=  transactionDetailsVO.getPreviousTransactionId();
        try
        {
            orderReference = trackingId;

            if (isTest){
                REQUEST_URL = RB.getString("INQUIRY_TEST_URL");
            }else {
                REQUEST_URL = RB.getString("INQUIRY_LIVE_URL");
            }
            REQUEST_URL = REQUEST_URL + merchantId+ "/order/"+orderReference+"/transaction/"+transaction;

            transactionLogger.error("processQuery() Response--" + trackingId + "--->" + REQUEST_URL);

            String responseString = uBAMCUtils.doGetHttpUrlConnection(REQUEST_URL, merchantName, password);

            transactionLogger.error("processQuery() Response--" + trackingId + "--->" + responseString);

            if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                JSONObject responseJSON     =  new JSONObject(responseString);
                JSONObject resOrderJSON             =  null;
                JSONObject resTransactionJSON       =  null;
                JSONObject resResponseJSON          =  null;
                JSONObject resErrorJSON             =  null;
                String resAmount    = "";
                String resStatus    = "";
                String authenticationStatus = "";
                String acquirerMessage      = "";
                String gatewayCode          = "";
                String transactionId        = "";
                String error                = "";
                String currency             = "";
                String acquirerCode         = "";
                String lastUpdatedTime      = "";
                String authorizationCode    = "";
                String type                 = "";

                if(responseJSON != null){
                    if(responseJSON.has("error")){
                        resErrorJSON = responseJSON.getJSONObject("error");
                        transactionLogger.error("resErrorJSON " + trackingId + " " + resResponseJSON);
                        if(resErrorJSON != null){
                            error   = resErrorJSON.getString("explanation");
                        }
                    }
                    if(responseJSON.has("response")){
                        resResponseJSON =  responseJSON.getJSONObject("response");
                        transactionLogger.error("resResponseJSON "+trackingId+" " +resResponseJSON);
                        if(resResponseJSON != null){
                            if(resResponseJSON.has("acquirerMessage")){
                                acquirerMessage = resResponseJSON.getString("acquirerMessage");
                            }
                            if(resResponseJSON.has("gatewayCode")){
                                gatewayCode = resResponseJSON.getString("gatewayCode");
                            }
                            if(resResponseJSON.has("acquirerCode")){
                                acquirerCode = resResponseJSON.getString("acquirerCode");
                            }
                        }
                    }
                    if(responseJSON.has("order")){
                        resOrderJSON = responseJSON.getJSONObject("order");
                        transactionLogger.error("resOrderJSON "+trackingId+" " +resOrderJSON);
                        if(resOrderJSON != null){
                            if(resOrderJSON.has("amount")){
                                resAmount = resOrderJSON.getString("amount");
                            }
                            if(resOrderJSON.has("authenticationStatus")){
                                authenticationStatus = resOrderJSON.getString("authenticationStatus");
                            }
                            if(resOrderJSON.has("status")){
                                resStatus = resOrderJSON.getString("status");
                            }
                            if(resOrderJSON.has("currency")){
                                currency = resOrderJSON.getString("currency");
                            }
                            if(resOrderJSON.has("lastUpdatedTime")){
                                lastUpdatedTime = resOrderJSON.getString("lastUpdatedTime");
                            }

                        }
                    }

                    if(responseJSON.has("transaction")){
                        resTransactionJSON = responseJSON.getJSONObject("transaction");
                        transactionLogger.error("resTransactionJSON "+trackingId+" " +resTransactionJSON);
                        if(resTransactionJSON != null){

                            if(resTransactionJSON.has("authorizationCode")){
                                authorizationCode =  resTransactionJSON.getString("authorizationCode");
                            }
                            if(resTransactionJSON.has("type")){
                                type =  resTransactionJSON.getString("type");
                            }
                            if(resTransactionJSON.has("acquirer")){
                                JSONObject acquirerJSON = resTransactionJSON.getJSONObject("acquirer");
                                if(acquirerJSON != null){
                                    if(acquirerJSON.has("transactionId")){
                                        transactionId =  acquirerJSON.getString("transactionId");
                                    }
                                }
                            }
                        }
                    }
                }
                transactionLogger.error("resStatus >>>>>>>> "+resStatus+" gatewayCode >>>>>> "+gatewayCode+" type>>> "+type+" lastUpdatedTime---> "+lastUpdatedTime);

                if ("CAPTURED".equalsIgnoreCase(resStatus) && "APPROVED".equalsIgnoreCase(gatewayCode))
                {
                    comm3DResponseVO.setStatus("Success");
                    comm3DResponseVO.setTransactionStatus("Success");
                    comm3DResponseVO.setRemark(acquirerMessage);
                    comm3DResponseVO.setDescription(acquirerMessage);
                    comm3DResponseVO.setAmount(resAmount);
                    comm3DResponseVO.setTransactionId(transaction);
                    comm3DResponseVO.setResponseHashInfo(transactionId);
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setErrorCode(acquirerCode);
                    comm3DResponseVO.setAuthCode(authorizationCode);
                    comm3DResponseVO.setResponseTime(lastUpdatedTime);
                    comm3DResponseVO.setMerchantId(merchantId);
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else if ("APPROVED".equalsIgnoreCase(gatewayCode) && ("PARTIALLY_REFUNDED".equalsIgnoreCase(resStatus) || "REFUNDED".equalsIgnoreCase(resStatus)) )
                {
                    comm3DResponseVO.setStatus("reversed");
                    comm3DResponseVO.setTransactionStatus("reversed");
                    comm3DResponseVO.setRemark(acquirerMessage);
                    comm3DResponseVO.setDescription(acquirerMessage);
                    comm3DResponseVO.setAmount(resAmount);
                    comm3DResponseVO.setTransactionId(transaction);
                    comm3DResponseVO.setResponseHashInfo(transactionId);
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setErrorCode(acquirerCode);
                    comm3DResponseVO.setAuthCode(authorizationCode);
                    comm3DResponseVO.setResponseTime(lastUpdatedTime);
                    comm3DResponseVO.setMerchantId(merchantId);
                }
                else if (functions.isValueNull(error) || "DECLINED".equalsIgnoreCase(gatewayCode) || "EXPIRED_CARD".equalsIgnoreCase(gatewayCode) || "TIMED_OUT".equalsIgnoreCase(gatewayCode)
                        || "ACQUIRER_SYSTEM_ERROR".equalsIgnoreCase(gatewayCode)  || "UNSPECIFIED_FAILURE".equalsIgnoreCase(gatewayCode) || "UNKNOWN".equalsIgnoreCase(gatewayCode))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    comm3DResponseVO.setRemark(acquirerMessage);
                    comm3DResponseVO.setDescription(acquirerMessage);
                    comm3DResponseVO.setAmount(resAmount);
                    comm3DResponseVO.setTransactionId(transaction);
                    comm3DResponseVO.setResponseHashInfo(transactionId);
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setErrorCode(acquirerCode);
                    comm3DResponseVO.setAuthCode(authorizationCode);
                    comm3DResponseVO.setResponseTime(lastUpdatedTime);
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }
            }else{
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(UBAMCPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }



    public GenericResponseVO processPayoutInquiry(String trackingId,GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processPayoutInquiry() of UBAMCPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        UBAMCPaymentGatewayUtils uBAMCUtils             =  new UBAMCPaymentGatewayUtils();
        String merchantId    = gatewayAccount.getMerchantId();
        String merchantName  = "merchant."+gatewayAccount.getMerchantId();
        String password      = gatewayAccount.getFRAUD_FTP_PASSWORD();

        boolean isTest = gatewayAccount.isTest();

        String REQUEST_URL     = "";
        String orderReference  = "";
        String transaction     =  transactionDetailsVO.getPreviousTransactionId();
        try
        {
            orderReference = trackingId;

            if (isTest){
                REQUEST_URL = RB.getString("INQUIRY_TEST_URL");
            }else {
                REQUEST_URL = RB.getString("INQUIRY_LIVE_URL");
            }
            REQUEST_URL = REQUEST_URL + merchantId+ "/order/"+orderReference+"/transaction/"+transaction;

            transactionLogger.error("processPayoutInquiry() Request ---> " + trackingId + "--->" + REQUEST_URL);

            String responseString = uBAMCUtils.doGetHttpUrlConnection(REQUEST_URL, merchantName, password);

            transactionLogger.error("processPayoutInquiry() Response ---> " + trackingId + "--->" + responseString);

            if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                JSONObject responseJSON     =  new JSONObject(responseString);
                JSONObject resOrderJSON             =  null;
                JSONObject resTransactionJSON       =  null;
                JSONObject resResponseJSON          =  null;
                JSONObject resErrorJSON             =  null;
                String resAmount    = "";
                String resStatus    = "";
                String authenticationStatus = "";
                String acquirerMessage      = "";
                String gatewayCode          = "";
                String transactionId        = "";
                String error                = "";
                String currency             = "";
                String acquirerCode         = "";
                String lastUpdatedTime      = "";
                String authorizationCode    = "";

                if(responseJSON != null){
                    if(responseJSON.has("error")){
                        resErrorJSON = responseJSON.getJSONObject("error");
                        transactionLogger.error("resErrorJSON " + trackingId + " " + resResponseJSON);
                        if(resErrorJSON != null){
                            error   = resErrorJSON.getString("explanation");
                        }
                    }
                    if(responseJSON.has("response")){
                        resResponseJSON =  responseJSON.getJSONObject("response");
                        transactionLogger.error("resResponseJSON "+trackingId+" " +resResponseJSON);
                        if(resResponseJSON != null){
                            if(resResponseJSON.has("acquirerMessage")){
                                acquirerMessage = resResponseJSON.getString("acquirerMessage");
                            }
                            if(resResponseJSON.has("gatewayCode")){
                                gatewayCode = resResponseJSON.getString("gatewayCode");
                            }
                            if(resResponseJSON.has("acquirerCode")){
                                acquirerCode = resResponseJSON.getString("acquirerCode");
                            }
                        }
                    }
                    if(responseJSON.has("order")){
                        resOrderJSON = responseJSON.getJSONObject("order");
                        transactionLogger.error("resOrderJSON "+trackingId+" " +resOrderJSON);
                        if(resOrderJSON != null){
                            if(resOrderJSON.has("amount")){
                                resAmount = resOrderJSON.getString("amount");
                            }
                            if(resOrderJSON.has("authenticationStatus")){
                                authenticationStatus = resOrderJSON.getString("authenticationStatus");
                            }
                            if(resOrderJSON.has("status")){
                                resStatus = resOrderJSON.getString("status");
                            }
                            if(resOrderJSON.has("currency")){
                                currency = resOrderJSON.getString("currency");
                            }
                            if(resOrderJSON.has("lastUpdatedTime")){
                                lastUpdatedTime = resOrderJSON.getString("lastUpdatedTime");
                            }

                        }
                    }

                    if(responseJSON.has("transaction")){
                        resTransactionJSON = responseJSON.getJSONObject("transaction");
                        transactionLogger.error("resTransactionJSON "+trackingId+" " +resTransactionJSON);
                        if(resTransactionJSON != null){

                            if(resTransactionJSON.has("authorizationCode")){
                                authorizationCode =  resTransactionJSON.getString("authorizationCode");
                            }
                            if(resTransactionJSON.has("acquirer")){
                                JSONObject acquirerJSON = resTransactionJSON.getJSONObject("acquirer");
                                if(acquirerJSON != null){
                                    if(acquirerJSON.has("transactionId")){
                                        transactionId =  acquirerJSON.getString("transactionId");
                                    }
                                }
                            }
                        }
                    }
                }

                if ("DISBURSED".equalsIgnoreCase(resStatus) && "APPROVED".equalsIgnoreCase(gatewayCode))
                {
                    comm3DResponseVO.setStatus("Success");
                    comm3DResponseVO.setTransactionStatus("Success");
                    comm3DResponseVO.setRemark(acquirerMessage);
                    comm3DResponseVO.setDescription(acquirerMessage);
                    comm3DResponseVO.setAmount(resAmount);
                    comm3DResponseVO.setTransactionId(transaction);
                    comm3DResponseVO.setResponseHashInfo(transactionId);
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setErrorCode(acquirerCode);
                    comm3DResponseVO.setAuthCode(authorizationCode);
                    comm3DResponseVO.setResponseTime(lastUpdatedTime);
                    comm3DResponseVO.setMerchantId(merchantId);
                }
                else if (functions.isValueNull(error) || "DECLINED".equalsIgnoreCase(gatewayCode) || "EXPIRED_CARD".equalsIgnoreCase(gatewayCode) || "TIMED_OUT".equalsIgnoreCase(gatewayCode)
                        || "ACQUIRER_SYSTEM_ERROR".equalsIgnoreCase(gatewayCode)  || "UNSPECIFIED_FAILURE".equalsIgnoreCase(gatewayCode) || "UNKNOWN".equalsIgnoreCase(gatewayCode))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    comm3DResponseVO.setRemark(acquirerMessage);
                    comm3DResponseVO.setDescription(acquirerMessage);
                    comm3DResponseVO.setAmount(resAmount);
                    comm3DResponseVO.setTransactionId(transaction);
                    comm3DResponseVO.setResponseHashInfo(transactionId);
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setErrorCode(acquirerCode);
                    comm3DResponseVO.setAuthCode(authorizationCode);
                    comm3DResponseVO.setResponseTime(lastUpdatedTime);
                }else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }
            }else{
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(UBAMCPaymentGateway.class.getName(), "processPayoutInquiry()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }


    @Override
    public GenericResponseVO processCommon3DSaleConfirmation(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException, PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processCommon3DSaleConfirmation() of UBAMCPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO   commCardDetailsVO           = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        CommDeviceDetailsVO commDeviceDetailsVO         = commRequestVO.getCommDeviceDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        UBAMCPaymentGatewayUtils uBAMCUtils =  new UBAMCPaymentGatewayUtils();
        boolean isTest                      = gatewayAccount.isTest();

        String merchantId    = gatewayAccount.getMerchantId();
        String merchantName  = "merchant."+gatewayAccount.getMerchantId();
        String password      = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String orderReference       = trackingId;
        String sourceOfFundsType    = "CARD";

        String cardNumber           = "";
        String cardExpiryMonth      = "";
        String cardExpiryYear       = "";
        String cardSecurityCode     = "";
        String nameOnCard           = "";


        String transactionAmount    = "";
        String transactionCurrency  = "";
        String apiOperation         = "PAY";
        String transaction          = "C_"+orderReference;
        String transactionId        = "";
        String transactionReference = transaction;


        JSONObject requestJSON       = new JSONObject();
        JSONObject sourceOfFundsJSON = new JSONObject();
        JSONObject providedJSON      = new JSONObject();
        JSONObject cardJSON          = new JSONObject();
        JSONObject expiryJSON        = new JSONObject();

        JSONObject orderJSON         = new JSONObject();
        JSONObject transactionJSON   = new JSONObject();
        JSONObject authenticationJSON   = new JSONObject();

        String REQUEST_URL          = "";
        try
        {

            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }

            cardNumber       = commCardDetailsVO.getCardNum();
            cardExpiryMonth  = commCardDetailsVO.getExpMonth();
            cardExpiryYear   = UBAMCPaymentGatewayUtils.getLast2DigitOfExpiryYear(commCardDetailsVO.getExpYear());
            cardSecurityCode = commCardDetailsVO.getcVV();
            transactionId    = transactionDetailsVO.getPreviousTransactionId();

            transactionCurrency  = transactionDetailsVO.getCurrency();
            transactionAmount   = transactionDetailsVO.getAmount();

            if(functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname())){
                nameOnCard   = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();
            }

            REQUEST_URL = REQUEST_URL + merchantId+ "/order/"+orderReference+"/transaction/"+transaction;

            requestJSON.put("apiOperation",apiOperation);
            authenticationJSON.put("transactionId",transactionId);

            sourceOfFundsJSON.put("type",sourceOfFundsType);
            expiryJSON.put("month",cardExpiryMonth);
            expiryJSON.put("year",cardExpiryYear);
            cardJSON.put("number",cardNumber);
            cardJSON.put("expiry",expiryJSON);
            cardJSON.put("nameOnCard",nameOnCard);
            cardJSON.put("securityCode",cardSecurityCode);
            providedJSON.put("card",cardJSON);
            sourceOfFundsJSON.put("provided",providedJSON);

            requestJSON.put("sourceOfFunds",sourceOfFundsJSON);

            orderJSON.put("reference",orderReference);
            orderJSON.put("currency",transactionCurrency);
            orderJSON.put("amount",transactionAmount);
            requestJSON.put("order",orderJSON);

            transactionJSON.put("reference",transactionReference);
            requestJSON.put("transaction",transactionJSON);

            String requestString = requestJSON.toString();

            JSONObject requestLogJOSN   =  new JSONObject(requestString);

            expiryJSON.put("month",functions.maskingNumber(cardExpiryMonth));
            expiryJSON.put("year",functions.maskingNumber(cardExpiryYear));
            cardJSON.put("number",functions.maskingPan(cardNumber));
            cardJSON.put("expiry",expiryJSON);
            cardJSON.put("nameOnCard",functions.getNameMasking(nameOnCard));
            cardJSON.put("securityCode",functions.maskingNumber(cardSecurityCode));
            providedJSON.put("card",cardJSON);
            sourceOfFundsJSON.put("provided",providedJSON);

            requestLogJOSN.put("sourceOfFunds",sourceOfFundsJSON);

            transactionLogger.error("3DSaleConfirmation REQUEST_URL " + trackingId + " " + REQUEST_URL);
            //transactionLogger.error("3DSaleConfirmation Request " + trackingId + " " + requestJSON.toString());
            transactionLogger.error("3DSaleConfirmation Request " + trackingId + " " + requestJSON.toString());

            //String responseString = uBAMCUtils.doPostHttpUrlConnection(REQUEST_URL, requestString.toString(), merchantName, password);
            String responseString = uBAMCUtils.doPostHttpUrlConnection(REQUEST_URL, requestString.toString(), merchantName, password);
            transactionLogger.error("3DSaleConfirmation Response " + trackingId + " " + responseString.toString());

            if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                JSONObject responseJSON        = new JSONObject(responseString);
                JSONObject resOrderJSON        = null;
                JSONObject resTransactionJSON  = null;
                JSONObject resAcquirerJSON     =  null;
                JSONObject resResponseJSON     = null;
                JSONObject resErrorJSON        = null;
                String resAmount    = "";
                String resStatus    = "";
                String authenticationStatus = "";
                String acquirerMessage      = "";
                String gatewayCode          = "";
                String error                = "";
                String explanation          = "";
                String lastUpdatedTime      = "";
                String authorizationCode    = "";
                String acquirerCode         = "";

                if(responseJSON != null){

                    if(responseJSON.has("error")){
                        resErrorJSON = responseJSON.getJSONObject("error");
                        transactionLogger.error("resErrorJSON " + trackingId + " " + resResponseJSON);
                        if(resErrorJSON != null){

                            if(resErrorJSON.has("explanation")){
                                explanation = resErrorJSON.getString("explanation");
                            }
                            if(resErrorJSON.has("error")){
                                error = resErrorJSON.getString("error");
                            }
                        }
                    }
                    if(responseJSON.has("response")){
                        resResponseJSON =  responseJSON.getJSONObject("response");
                        transactionLogger.error("resResponseJSON "+trackingId+" " +resResponseJSON);
                        if(resResponseJSON != null){
                            if(resResponseJSON.has("acquirerMessage")){
                                acquirerMessage = resResponseJSON.getString("acquirerMessage");
                            }
                            if(resResponseJSON.has("gatewayCode")){
                                gatewayCode = resResponseJSON.getString("gatewayCode");
                            }
                            if(resResponseJSON.has("acquirerCode")){
                                acquirerCode = resResponseJSON.getString("acquirerCode");
                            }
                        }
                    }
                    if(responseJSON.has("order")){
                        resOrderJSON = responseJSON.getJSONObject("order");
                        transactionLogger.error("resOrderJSON "+trackingId+" " +resOrderJSON);
                        if(resOrderJSON != null){
                            if(resOrderJSON.has("amount")){
                                resAmount = resOrderJSON.getString("amount");
                            }
                            if(resOrderJSON.has("authenticationStatus")){
                                authenticationStatus = resOrderJSON.getString("authenticationStatus");
                            }
                            if(resOrderJSON.has("status")){
                                resStatus = resOrderJSON.getString("status");
                            }
                            if(resOrderJSON.has("lastUpdatedTime")){
                                lastUpdatedTime = resOrderJSON.getString("lastUpdatedTime");
                            }
                        }
                    }

                    if(responseJSON.has("transaction")){
                        resTransactionJSON = responseJSON.getJSONObject("transaction");
                        transactionLogger.error("resTransactionJSON "+trackingId+" " +resTransactionJSON);
                        if(resTransactionJSON != null){
                            if(resTransactionJSON.has("authorizationCode")){
                                authorizationCode = resTransactionJSON.getString("authorizationCode");
                            }

                            if(resTransactionJSON.has("acquirer")){
                                resAcquirerJSON =  resTransactionJSON.getJSONObject("acquirer");
                                if(resAcquirerJSON != null){
                                    if(resAcquirerJSON.has("transactionId")){
                                        transactionId = resAcquirerJSON.getString("transactionId");
                                    }
                                }
                            }
                        }
                    }
                }
                transactionLogger.error("acquirerCode "+trackingId+" " +acquirerCode+ " lastUpdatedTime "+lastUpdatedTime);
                if ("CAPTURED".equalsIgnoreCase(resStatus) && "APPROVED".equalsIgnoreCase(gatewayCode))
                {
                    comm3DResponseVO.setStatus("Success");
                    comm3DResponseVO.setTransactionStatus("Success");
                    comm3DResponseVO.setRemark(acquirerMessage);
                    comm3DResponseVO.setDescription(acquirerMessage);
                    comm3DResponseVO.setAmount(resAmount);
                    comm3DResponseVO.setTransactionId(transaction);
                    comm3DResponseVO.setResponseHashInfo(transactionId);
                    comm3DResponseVO.setResponseTime(lastUpdatedTime);
                    comm3DResponseVO.setAuthCode(authorizationCode);
                    comm3DResponseVO.setErrorCode(acquirerCode);
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else if (functions.isValueNull(error) || "DECLINED".equalsIgnoreCase(gatewayCode) || "EXPIRED_CARD".equalsIgnoreCase(gatewayCode) || "TIMED_OUT".equalsIgnoreCase(gatewayCode)
                        || "ACQUIRER_SYSTEM_ERROR".equalsIgnoreCase(gatewayCode)  || "UNSPECIFIED_FAILURE".equalsIgnoreCase(gatewayCode) || "UNKNOWN".equalsIgnoreCase(gatewayCode))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    if(functions.isValueNull(explanation)){
                        comm3DResponseVO.setRemark(explanation);
                        comm3DResponseVO.setDescription(error);
                    }else{
                        comm3DResponseVO.setRemark(acquirerMessage);
                        comm3DResponseVO.setDescription(acquirerMessage);
                    }

                    comm3DResponseVO.setAmount(resAmount);
                    comm3DResponseVO.setTransactionId(transaction);
                    comm3DResponseVO.setResponseHashInfo(transactionId);
                    comm3DResponseVO.setResponseTime(lastUpdatedTime);
                    comm3DResponseVO.setAuthCode(authorizationCode);
                    comm3DResponseVO.setErrorCode(acquirerCode);
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setRemark("Transaction is pending");
                    comm3DResponseVO.setDescription("Transaction is pending");
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("SSLHandshakeException ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(UBAMCPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }


    @Override
    public GenericResponseVO processCommon3DAuthConfirmation(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException, PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processCommon3DAuthConfirmation() of UBAMCPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO   commCardDetailsVO           = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        CommDeviceDetailsVO commDeviceDetailsVO         = commRequestVO.getCommDeviceDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        UBAMCPaymentGatewayUtils uBAMCUtils =  new UBAMCPaymentGatewayUtils();
        boolean isTest                      = gatewayAccount.isTest();

        String merchantId    = gatewayAccount.getMerchantId();
        String merchantName  = "merchant."+gatewayAccount.getMerchantId();
        String password      = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String orderReference       = trackingId;
        String transaction          = "AU_"+orderReference;
        String sourceOfFundsType    = "CARD";

        String cardNumber           = "";
        String cardExpiryMonth      = "";
        String cardExpiryYear       = "";
        String cardSecurityCode     = "";
        String nameOnCard           = "";

        String transactionAmount    = "";
        String transactionCurrency  = "";
        String apiOperation         = "AUTHORIZE";
        String transactionId        = "";


        JSONObject requestJSON       = new JSONObject();
        JSONObject sourceOfFundsJSON = new JSONObject();
        JSONObject providedJSON      = new JSONObject();
        JSONObject cardJSON          = new JSONObject();
        JSONObject expiryJSON        = new JSONObject();

        JSONObject orderJSON         = new JSONObject();
        JSONObject transactionJSON   = new JSONObject();
        JSONObject authenticationJSON   = new JSONObject();

        String REQUEST_URL          = "";
        try
        {

            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }

            cardNumber       = commCardDetailsVO.getCardNum();
            cardExpiryMonth  = commCardDetailsVO.getExpMonth();
            cardExpiryYear   = UBAMCPaymentGatewayUtils.getLast2DigitOfExpiryYear(commCardDetailsVO.getExpYear());
            cardSecurityCode = commCardDetailsVO.getcVV();
            transactionId    = transactionDetailsVO.getPreviousTransactionId();

            transactionCurrency  = transactionDetailsVO.getCurrency();
            transactionAmount   = transactionDetailsVO.getAmount();

            if(functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname()))
                nameOnCard   = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();
            else {
                nameOnCard  = "customer";
            }

            REQUEST_URL = REQUEST_URL + merchantId+ "/order/"+orderReference+"/transaction/"+transaction;

            requestJSON.put("apiOperation",apiOperation);

            authenticationJSON.put("transactionId",transactionId);

            sourceOfFundsJSON.put("type",sourceOfFundsType);
            expiryJSON.put("month",cardExpiryMonth);
            expiryJSON.put("year",cardExpiryYear);
            cardJSON.put("number",cardNumber);
            cardJSON.put("expiry",expiryJSON);
            cardJSON.put("nameOnCard",nameOnCard);
            cardJSON.put("securityCode",cardSecurityCode);
            providedJSON.put("card",cardJSON);
            sourceOfFundsJSON.put("provided",providedJSON);

            requestJSON.put("sourceOfFunds",sourceOfFundsJSON);

            orderJSON.put("reference",orderReference);
            orderJSON.put("currency",transactionCurrency);
            orderJSON.put("amount",transactionAmount);
            requestJSON.put("order",orderJSON);

            transactionJSON.put("reference",transaction);
            requestJSON.put("transaction",transactionJSON);

            String requestString = requestJSON.toString();

            JSONObject requestLogJONS = new JSONObject(requestString);
            expiryJSON.put("month",functions.maskingNumber(cardExpiryMonth));
            expiryJSON.put("year",functions.maskingNumber(cardExpiryYear));
            cardJSON.put("number",functions.maskingPan(cardNumber));
            cardJSON.put("expiry",expiryJSON);
            cardJSON.put("nameOnCard",functions.getNameMasking(nameOnCard));
            cardJSON.put("securityCode",functions.maskingNumber(cardSecurityCode));
            providedJSON.put("card",cardJSON);
            sourceOfFundsJSON.put("provided",providedJSON);
            requestLogJONS.put("sourceOfFunds",sourceOfFundsJSON);

            transactionLogger.error("3DAuthConfirmation Request REQUEST_URL" + trackingId + " " + REQUEST_URL);
            transactionLogger.error("3DAuthConfirmation Request" + trackingId + " " + requestLogJONS.toString());

            //String responseString = uBAMCUtils.doPostHttpUrlConnection(REQUEST_URL, requestJSON.toString(), merchantName, password);
            String responseString = uBAMCUtils.doPostHttpUrlConnection(REQUEST_URL, requestString.toString(), merchantName, password);
            transactionLogger.error("3DAuthConfirmation Response " + trackingId + " " + responseString.toString());

            if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){
                JSONObject responseJSON        =  new JSONObject(responseString);
                JSONObject resOrderJSON        =  null;
                JSONObject resTransactionJSON  =  null;
                JSONObject resResponseJSON     =  null;
                JSONObject resErrorJSON        =  null;
                JSONObject resAcquirerJSON      =  null;
                String resAmount    = "";
                String resStatus    = "";
                String authenticationStatus = "";
                String acquirerMessage      = "";
                String gatewayCode          = "";
                String respTransactionId        = "";
                String error                = "";
                String lastUpdatedTime                = "";
                String acquirerCode                = "";
                String authorizationCode                = "";

                if(responseJSON != null){

                    if(responseJSON.has("error")){
                        resErrorJSON = responseJSON.getJSONObject("error");
                        transactionLogger.error("resErrorJSON " + trackingId + " " + resResponseJSON);
                        if(resErrorJSON != null){
                            error   = resErrorJSON.getString("explanation");
                        }
                    }
                    if(responseJSON.has("response")){
                        resResponseJSON =  responseJSON.getJSONObject("response");
                        transactionLogger.error("resResponseJSON "+trackingId+" " +resResponseJSON);
                        if(resResponseJSON != null){
                            if(resResponseJSON.has("acquirerMessage")){
                                acquirerMessage = resResponseJSON.getString("acquirerMessage");
                            }
                            if(resResponseJSON.has("gatewayCode")){
                                gatewayCode = resResponseJSON.getString("gatewayCode");
                            }
                            if(resResponseJSON.has("acquirerCode")){
                                acquirerCode = resResponseJSON.getString("acquirerCode");
                            }
                        }
                    }
                    if(responseJSON.has("order")){
                        resOrderJSON = responseJSON.getJSONObject("order");
                        transactionLogger.error("resOrderJSON "+trackingId+" " +resOrderJSON);
                        if(resOrderJSON != null){
                            if(resOrderJSON.has("amount")){
                                resAmount = resOrderJSON.getString("amount");
                            }
                            if(resOrderJSON.has("authenticationStatus")){
                                authenticationStatus = resOrderJSON.getString("authenticationStatus");
                            }
                            if(resOrderJSON.has("status")){
                                resStatus = resOrderJSON.getString("status");
                            }
                            if(resOrderJSON.has("lastUpdatedTime")){
                                lastUpdatedTime = resOrderJSON.getString("lastUpdatedTime");
                            }
                        }
                    }

                    if(responseJSON.has("transaction")){
                        resTransactionJSON = responseJSON.getJSONObject("transaction");
                        transactionLogger.error("resTransactionJSON "+trackingId+" " +resTransactionJSON);
                        if(resTransactionJSON != null){
                            if(resTransactionJSON.has("id")){
                                respTransactionId =  resTransactionJSON.getString("id");
                            }
                            if(resTransactionJSON.has("authorizationCode")){
                                authorizationCode = resTransactionJSON.getString("authorizationCode");
                            }

                            if(resTransactionJSON.has("acquirer")){
                                resAcquirerJSON =  resTransactionJSON.getJSONObject("acquirer");
                                if(resAcquirerJSON != null){
                                    if(resAcquirerJSON.has("transactionId")){
                                        transactionId = resAcquirerJSON.getString("transactionId");
                                    }
                                }
                            }
                        }
                    }
                }

                if ("AUTHORIZED".equalsIgnoreCase(resStatus) && "APPROVED".equalsIgnoreCase(gatewayCode))
                {
                    comm3DResponseVO.setStatus("Success");
                    comm3DResponseVO.setTransactionStatus("Success");
                    comm3DResponseVO.setRemark(acquirerMessage);
                    comm3DResponseVO.setDescription(acquirerMessage);
                    comm3DResponseVO.setAmount(resAmount);
                    comm3DResponseVO.setTransactionId(transaction);
                    comm3DResponseVO.setResponseHashInfo(respTransactionId);
                    comm3DResponseVO.setResponseTime(lastUpdatedTime);
                    comm3DResponseVO.setErrorCode(acquirerCode);
                    comm3DResponseVO.setAuthCode(authorizationCode);
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());

                }else if (functions.isValueNull(error) || "DECLINED".equalsIgnoreCase(gatewayCode) || "EXPIRED_CARD".equalsIgnoreCase(gatewayCode) || "TIMED_OUT".equalsIgnoreCase(gatewayCode)
                        || "ACQUIRER_SYSTEM_ERROR".equalsIgnoreCase(gatewayCode)  || "UNSPECIFIED_FAILURE".equalsIgnoreCase(gatewayCode) || "UNKNOWN".equalsIgnoreCase(gatewayCode))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    comm3DResponseVO.setRemark(acquirerMessage);
                    comm3DResponseVO.setDescription(acquirerMessage);
                    comm3DResponseVO.setAmount(resAmount);
                    comm3DResponseVO.setTransactionId(transaction);
                    comm3DResponseVO.setResponseHashInfo(respTransactionId);
                    comm3DResponseVO.setResponseTime(lastUpdatedTime);
                    comm3DResponseVO.setErrorCode(acquirerCode);
                    comm3DResponseVO.setAuthCode(authorizationCode);
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setRemark("Transaction is pending");
                    comm3DResponseVO.setDescription("Transaction is pending");
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("SSLHandshakeException ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(UBAMCPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }


}
