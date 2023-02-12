package com.payment.easypay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
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
import org.codehaus.jettison.json.JSONException;
import org.json.JSONObject;

import javax.crypto.spec.IvParameterSpec;
import java.net.URLEncoder;
import java.util.*;


/**
 * Created by Admin on 10/19/2021.
 */
public class EasyPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE             = "EasyPay";
    private final static ResourceBundle RB              = LoadProperties.getProperty("com.directi.pg.EasyPay");
    private static TransactionLogger transactionlogger  = new TransactionLogger(EasyPaymentGateway.class.getName());

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public EasyPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("inside EasyPayment processAuthentication() ----------->");

        CommRequestVO commRequestVO         = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO   = new Comm3DResponseVO();
        Functions functions                 = new Functions();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        CommDeviceDetailsVO commDeviceDetailsVO         = commRequestVO.getCommDeviceDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        String payment_brand                            = transactionDetailsVO.getCardType();

        String isRecurring          = gatewayAccount.getIsRecurring();
        boolean isTest              = gatewayAccount.isTest();
        String merchantId           = gatewayAccount.getMerchantId();
        String productId            = gatewayAccount.getFRAUD_FTP_USERNAME();
        String encryptionKey        = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String REDIRECT_URL         = "";
        String NOTIFY_URL           = "";
        String paymentSolution      = "creditcards";
        String operationType        = "DEBIT";
        String paymentMethod        = "auth";

        String merchantTransactionId = "";
        String customerId            = "";

        String REQUEST_URL  = "";
        String HOST_URL     = "";
        String statusURL    = "";
        String successURL   = "";
        String errorURL     = "";
        String cancelURL    = "";
        String awaitingURL  = "";

        String customerEmail = "";
        String addressLine1  = "";
        String addressLine2  = "";
        String telephone     = "";
        String firstName     = "";
        String lastName      = "";
        String chName        = "";

        String cardType     = "";
        String cardNumber   = "";
        String expDate      = "";
        String cvnNumber    = "";
        String amount       = "";
        String currency     = "";
        String country      = "";
        String ipAddress    = "";
        String description  = "";
        String city         = "";
        String state        = "";
        String postCode     = "";

        String acceptHeader     = "";
        String browserIp        = "";
        String browserLanguage  = "";
        String browserTZ        = "";
        String challengeWindowSize = "";
        String javaEnabled      = "";
        String jsEnabled        = "";
        String screenColorDepth = "";
        String screenHeight     = "";
        String screenWidth      = "";
        String userAgent        = "";

        //for first recurring
        String paymentRecurringType  = "";
        String merchantExemptionsSca = "";

        String type = "";
        HashMap<String, String> resultMap;
        String ivString         = "";
        String encryptedString  = "";
        String integrityCheck   = "";
        String queryParameters  = "";
        String preAuthResponse  = "";


        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringBufferLog = new StringBuffer();
        try
        {
            NOTIFY_URL  = RB.getString("EASYPAY_NOTIFY_URL") + trackingID;

            if(functions.isValueNull(commMerchantVO.getHostUrl()))
                REDIRECT_URL = "https://"+commMerchantVO.getHostUrl() + RB.getString("ENDPOINT_URL")+trackingID;
            else
                REDIRECT_URL = RB.getString("REDIRECT_URL") + trackingID;

            merchantTransactionId = trackingID;
            cardType              = EasyPaymentGatewayUtils.getPaymentBrand(payment_brand);
            cardNumber            = commCardDetailsVO.getCardNum();
            expDate               = commCardDetailsVO.getExpMonth() + "" + EasyPaymentGatewayUtils.getLast2DigitOfExpiryYear(commCardDetailsVO.getExpYear());
            cvnNumber             = commCardDetailsVO.getcVV();
            ipAddress             = commAddressDetailsVO.getCardHolderIpAddress();
            amount                = transactionDetailsVO.getAmount();
            description           = trackingID;
            currency              = transactionDetailsVO.getCurrency();
            country               = commAddressDetailsVO.getCountry();
            customerId            = trackingID;
            city                  = commAddressDetailsVO.getCity();
            state                 = commAddressDetailsVO.getState();
            postCode              = commAddressDetailsVO.getZipCode();
            statusURL             = NOTIFY_URL + "_status";
            successURL            = REDIRECT_URL + "_success";
            errorURL              = REDIRECT_URL + "_error";
            cancelURL             = REDIRECT_URL + "_cancel";
            awaitingURL           = REDIRECT_URL + "_awaiting";
            jsEnabled             = "TRUE";

            if (functions.isValueNull(commAddressDetailsVO.getIp()))
                browserIp = commAddressDetailsVO.getIp();
            else
                browserIp = "192.168.1.1";

            if (commDeviceDetailsVO != null)
            {
                if (functions.isValueNull(commDeviceDetailsVO.getAcceptHeader())){
                    acceptHeader = commDeviceDetailsVO.getAcceptHeader();// if not work add else part here
                }else {
                    acceptHeader = "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9";
                }

                if (functions.isValueNull(commDeviceDetailsVO.getFingerprints()))
                {
                    transactionlogger.error(trackingID+ ">>>>>>Inside Fingerprints if condition----> ");
                    HashMap fingerPrintMap = functions.getFingerPrintMap(commDeviceDetailsVO.getFingerprints());
                    JSONArray screenResolution = new JSONArray();
                    try
                    {
                        if (fingerPrintMap.containsKey("userAgent"))
                            userAgent = (String) fingerPrintMap.get("userAgent");
                        if (fingerPrintMap.containsKey("language"))
                            browserLanguage = (String) fingerPrintMap.get("language");
                        if (fingerPrintMap.containsKey("colorDepth"))
                            screenColorDepth = String.valueOf(fingerPrintMap.get("colorDepth"));
                        if (fingerPrintMap.containsKey("timezoneOffset"))
                            browserTZ = String.valueOf(fingerPrintMap.get("timezoneOffset"));
                        if (fingerPrintMap.containsKey("screenResolution"))
                            screenResolution = (JSONArray) fingerPrintMap.get("screenResolution");
                        if (screenResolution.length() > 0)
                            screenHeight = String.valueOf(screenResolution.getString(0));
                        if (screenResolution.length() > 1)
                            screenWidth = String.valueOf(screenResolution.getString(1));
                        challengeWindowSize = "05";
                        javaEnabled = "TRUE";
                        transactionlogger.error(trackingID+ ">>>>>screenResolution: " + screenResolution);
                    }
                    catch (JSONException e)
                    {
                        transactionlogger.error("JSONException---trackingId--" + trackingID + "--", e);
                    }
                }
                else
                {
                    transactionlogger.error(trackingID+" >>>>Inside Fingerprints else condition --------> ");
                    if (functions.isValueNull(commDeviceDetailsVO.getUser_Agent()))
                        userAgent = commDeviceDetailsVO.getUser_Agent();
                    else
                        userAgent = "Mozilla/5.0 (Android; Mobile; rv:13.0) Gecko/13.0 Firefox/13.0";
                    if (functions.isValueNull(commDeviceDetailsVO.getBrowserLanguage()))
                        browserLanguage = commDeviceDetailsVO.getBrowserLanguage();
                    else
                        browserLanguage = "en-US";
                    if (functions.isValueNull(commDeviceDetailsVO.getBrowserColorDepth()))
                        screenColorDepth = commDeviceDetailsVO.getBrowserColorDepth();
                    else
                        screenColorDepth = "24";
                    if (functions.isValueNull(commDeviceDetailsVO.getBrowserTimezoneOffset()))
                        browserTZ = commDeviceDetailsVO.getBrowserTimezoneOffset();
                    else
                        browserTZ = "5";
                    if (functions.isValueNull(commDeviceDetailsVO.getBrowserScreenHeight()))
                        screenHeight = commDeviceDetailsVO.getBrowserScreenHeight();
                    else
                        screenHeight = "939";
                    if (functions.isValueNull(commDeviceDetailsVO.getBrowserScreenWidth()))
                        screenWidth = commDeviceDetailsVO.getBrowserScreenWidth();
                    else
                        screenWidth = "1255";
                    if (functions.isValueNull(commDeviceDetailsVO.getBrowserJavaEnabled()))
                        javaEnabled = commDeviceDetailsVO.getBrowserJavaEnabled();
                    else
                        javaEnabled = "TRUE";
                    challengeWindowSize = "05";
                }
            }
            else
            {
                acceptHeader        = "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9";
                userAgent           = "Mozilla/5.0 (Android; Mobile; rv:13.0) Gecko/13.0 Firefox/13.0";
                browserLanguage     = "en-US";
                screenColorDepth    = "24";
                browserTZ           = "5";
                screenHeight        = "939";
                screenWidth         = "1255";
                javaEnabled         = "TRUE";
                challengeWindowSize = "05";
            }

            //for recurring
            paymentRecurringType  = "newCof";
            merchantExemptionsSca = "LWV;TRA"; //"LWV;TRA[250.0];COR;MIT;ATD"; //"LWV;TRA";

            if (isTest)
            {
                REQUEST_URL = RB.getString("SALE_TEST_URL");
                HOST_URL    = RB.getString("TEST_HOST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("SALE_LIVE_URL");
                HOST_URL    = RB.getString("LIVE_HOST_URL");
            }


            if (functions.isValueNull(country) && country.length() != 2)
            {
                if (EasyPaymentGatewayUtils.getCountryCodeHash().containsKey(country))
                {
                    country = EasyPaymentGatewayUtils.getCountryCodeHash().getOrDefault(country, "ES").toString();
                }
            }

            if (functions.isValueNull(state) && state.length() != 2)
            {
                if (EasyPaymentGatewayUtils.getStateCodeHash().containsKey(state))
                {
                    state = EasyPaymentGatewayUtils.getStateCodeHash().getOrDefault(state, state).toString();
                }
            }      // as per bank no 2 char needed from now


            if (functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname()))
            {
                chName = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();
            }
            else
            {
                chName = "customer";
            }

            if (functions.isValueNull(commAddressDetailsVO.getFirstname()))
            {
                firstName = commAddressDetailsVO.getFirstname();
            }
            else
            {
                firstName = "customer";
            }

            if (functions.isValueNull(commAddressDetailsVO.getLastname()))
            {
                lastName = commAddressDetailsVO.getLastname();
            }
            else
            {
                lastName = "customer";
            }

            if (functions.isValueNull(commAddressDetailsVO.getEmail()))
            {
                customerEmail = commAddressDetailsVO.getEmail();
            }
            else
            {
                customerEmail = "customer@gmail.com";
            }

            if (functions.isValueNull(commAddressDetailsVO.getPhone()))
            {
                telephone = commAddressDetailsVO.getPhone();
            }
            else
            {
                telephone = "9999999999";
            }

            addressLine1 = commAddressDetailsVO.getStreet() + "," + commAddressDetailsVO.getCity();
            addressLine2 = commAddressDetailsVO.getState();

            //for first recurring
            if ("Y".equals(isRecurring))
            {
                type = "processRebilling";
                //for recurring
                transactionlogger.error("inside if for rebilling--->"+trackingID);
                stringBuffer.append("merchantId=" + merchantId);
                stringBuffer.append("&paymentSolution=" + paymentSolution);
                stringBuffer.append("&productId=" + productId);
                stringBuffer.append("&operationType=" + operationType);
                stringBuffer.append("&merchantTransactionId=" + merchantTransactionId);
                stringBuffer.append("&currency=" + currency);
                stringBuffer.append("&amount=" + amount);
                stringBuffer.append("&country=" + country);
                stringBuffer.append("&description=" + description);
                stringBuffer.append("&cvnNumber=" + cvnNumber);
                stringBuffer.append("&expDate=" + expDate);
                stringBuffer.append("&paymentRecurringType=" + paymentRecurringType);
                stringBuffer.append("&cardNumber=" + cardNumber);
                stringBuffer.append("&merchantExemptionsSca=" + merchantExemptionsSca);
                stringBuffer.append("&customerId=" + customerId);
                stringBuffer.append("&successURL=" + successURL);
                stringBuffer.append("&statusURL=" + statusURL);
                stringBuffer.append("&errorURL=" + errorURL);
                stringBuffer.append("&cancelURL=" + cancelURL);
                stringBuffer.append("&awaitingURL=" + awaitingURL);


                //for masking logs
                stringBufferLog.append("merchantId=" + merchantId);
                stringBufferLog.append("&paymentSolution=" + paymentSolution);
                stringBufferLog.append("&productId=" + productId);
                stringBufferLog.append("&operationType=" + operationType);
                stringBufferLog.append("&merchantTransactionId=" + merchantTransactionId);
                stringBufferLog.append("&currency=" + currency);
                stringBufferLog.append("&amount=" + amount);
                stringBufferLog.append("&country=" + country);
                stringBufferLog.append("&description=" + description);
                stringBufferLog.append("&cvnNumber=" + functions.maskingNumber(cvnNumber));
                stringBufferLog.append("&expDate=" + functions.maskingNumber(expDate));
                stringBufferLog.append("&paymentRecurringType=" + paymentRecurringType);
                stringBufferLog.append("&cardNumber=" + functions.maskingPan(cardNumber));
                stringBufferLog.append("&merchantExemptionsSca=" + merchantExemptionsSca);
                stringBufferLog.append("&customerId=" + customerId);
                stringBufferLog.append("&successURL=" + successURL);
                stringBufferLog.append("&statusURL=" + statusURL);
                stringBufferLog.append("&errorURL=" + errorURL);
                stringBufferLog.append("&cancelURL=" + cancelURL);
                stringBufferLog.append("&awaitingURL=" + awaitingURL);


                transactionlogger.error("processRebilling()  requestParameter : " + trackingID+ " " +stringBufferLog);
            }
            else
            {
                type = "processAuthentication";
                //for sale
                transactionlogger.error("inside else for processAuthentication--->"+trackingID);
                stringBuffer.append("merchantId=" + merchantId);
                stringBuffer.append("&productId=" + productId);
                stringBuffer.append("&paymentSolution=" + paymentSolution);
                stringBuffer.append("&operationType=" + operationType);
                stringBuffer.append("&paymentMethod=" + paymentMethod);
                stringBuffer.append("&amount=" + amount);
                stringBuffer.append("&currency=" + currency);
                stringBuffer.append("&country=" + country);
                stringBuffer.append("&description=" + description);
                stringBuffer.append("&merchantTransactionId=" + merchantTransactionId);
                stringBuffer.append("&customerId=" + customerId);
                stringBuffer.append("&customerEmail=" + customerEmail);
                stringBuffer.append("&addressLine1=" + addressLine1);
                stringBuffer.append("&addressLine2=" + addressLine2);
                stringBuffer.append("&city=" + city);
                stringBuffer.append("&telephone=" + telephone);
                stringBuffer.append("&firstName=" + firstName);
                stringBuffer.append("&lastName=" + lastName);
                stringBuffer.append("&ipAddress=" + ipAddress);
                stringBuffer.append("&customerCountry=" + country);
                stringBuffer.append("&state=" + state);
                stringBuffer.append("&postCode=" + postCode);
                stringBuffer.append("&successURL=" + successURL);
                stringBuffer.append("&statusURL=" + statusURL);
                stringBuffer.append("&errorURL=" + errorURL);
                stringBuffer.append("&cancelURL=" + cancelURL);
                stringBuffer.append("&awaitingURL=" + awaitingURL);

                //for card
                stringBuffer.append("&cardType=" + cardType);
                stringBuffer.append("&cardNumber=" + cardNumber);
                stringBuffer.append("&chName=" + chName);
                stringBuffer.append("&expDate=" + expDate);
                stringBuffer.append("&cvnNumber=" + cvnNumber);

                //for 3ds
                stringBuffer.append("&chAddress1=" + addressLine1);
                stringBuffer.append("&chAddress2=" + addressLine2);
                stringBuffer.append("&chCity=" + city);
                stringBuffer.append("&chPostCode=" + postCode);
                stringBuffer.append("&chCountry=" + country);
                stringBuffer.append("&chEmail=" + customerEmail);
                stringBuffer.append("&chFirstName=" + firstName);
                stringBuffer.append("&chLastName=" + lastName);
                stringBuffer.append("&chPhone=" + telephone);
                stringBuffer.append("&chState=" + state);

                //browser parameters
                stringBuffer.append("&acceptHeader="+acceptHeader);
                stringBuffer.append("&browserIp="+browserIp);
                stringBuffer.append("&browserLanguage="+browserLanguage);
                stringBuffer.append("&browserTZ="+browserTZ);
                stringBuffer.append("&challengeWindowSize="+challengeWindowSize);
                stringBuffer.append("&javaEnabled="+javaEnabled);
                stringBuffer.append("&jsEnabled="+jsEnabled);
                stringBuffer.append("&screenColorDepth="+screenColorDepth);
                stringBuffer.append("&screenHeight="+screenHeight);
                stringBuffer.append("&screenWidth="+screenWidth);
                stringBuffer.append("&userAgent="+userAgent);


                // masking for logs
                stringBufferLog.append("merchantId=" + merchantId);
                stringBufferLog.append("&productId=" + productId);
                stringBufferLog.append("&paymentSolution=" + paymentSolution);
                stringBufferLog.append("&operationType=" + operationType);
                stringBufferLog.append("&paymentMethod=" + paymentMethod);
                stringBufferLog.append("&amount=" + amount);
                stringBufferLog.append("&currency=" + currency);
                stringBufferLog.append("&country=" + country);
                stringBufferLog.append("&description=" + description);
                stringBufferLog.append("&merchantTransactionId=" + merchantTransactionId);
                stringBufferLog.append("&customerId=" + customerId);

                stringBufferLog.append("&customerEmail=" + functions.getEmailMasking(customerEmail));
                stringBufferLog.append("&addressLine1=" + addressLine1);
                stringBufferLog.append("&addressLine2=" + addressLine2);
                stringBufferLog.append("&city=" + city);
                stringBufferLog.append("&telephone=" + functions.getPhoneNumMasking(telephone));
                stringBufferLog.append("&firstName=" + functions.maskingFirstName(firstName));
                stringBufferLog.append("&lastName=" + functions.maskingLastName(lastName));
                stringBufferLog.append("&successURL=" + successURL);
                stringBufferLog.append("&statusURL=" + statusURL);
                stringBufferLog.append("&errorURL=" + errorURL);
                stringBufferLog.append("&cancelURL=" + cancelURL);
                stringBufferLog.append("&awaitingURL=" + awaitingURL);

                stringBufferLog.append("&ipAddress=" + ipAddress);
                stringBufferLog.append("&customerCountry=" + country);
                stringBufferLog.append("&state=" + state);
                stringBufferLog.append("&postCode=" + postCode);

                //for card
                stringBufferLog.append("&cardType=" + cardType);
                stringBufferLog.append("&cardNumber=" + functions.maskingPan(cardNumber));
                stringBufferLog.append("&chName=" + functions.maskingFirstName(chName));
                stringBufferLog.append("&expDate=" + functions.maskingNumber(expDate));
                stringBufferLog.append("&cvnNumber=" + functions.maskingNumber(cvnNumber));

                stringBufferLog.append("&chAddress1=" + addressLine1);
                stringBufferLog.append("&chAddress2=" + addressLine2);
                stringBufferLog.append("&chCity=" + city);
                stringBufferLog.append("&chPostCode=" + postCode);
                stringBufferLog.append("&chCountry=" + country);
                stringBufferLog.append("&chEmail=" + functions.getEmailMasking(customerEmail));
                stringBufferLog.append("&chFirstName=" + functions.maskingFirstName(firstName));
                stringBufferLog.append("&chLastName=" + functions.maskingLastName(lastName));
                stringBufferLog.append("&chPhone=" + functions.getPhoneNumMasking(telephone));
                stringBufferLog.append("&chState=" + state);

                //browser parameters
                stringBufferLog.append("&acceptHeader="+acceptHeader);
                stringBufferLog.append("&browserIp="+browserIp);
                stringBufferLog.append("&browserLanguage="+browserLanguage);
                stringBufferLog.append("&browserTZ="+browserTZ);
                stringBufferLog.append("&challengeWindowSize="+challengeWindowSize);
                stringBufferLog.append("&javaEnabled="+javaEnabled);
                stringBufferLog.append("&jsEnabled="+jsEnabled);
                stringBufferLog.append("&screenColorDepth="+screenColorDepth);
                stringBufferLog.append("&screenHeight="+screenHeight);
                stringBufferLog.append("&screenWidth="+screenWidth);
                stringBufferLog.append("&userAgent="+userAgent);

                transactionlogger.error("processAuthentication() requestParameters :" + trackingID+ " " +stringBufferLog.toString());
            }


            IvParameterSpec iv = EasyPaymentGatewayUtils.generateIv();
            ivString = Base64.getEncoder().encodeToString(iv.getIV());

            encryptedString = EasyPaymentGatewayUtils.encryptCBC(stringBuffer.toString(), encryptionKey, iv);

            integrityCheck = EasyPaymentGatewayUtils.generateSHA256Hash(stringBuffer.toString());

            queryParameters = REQUEST_URL + "merchantId=" + merchantId + "&encrypted=" + URLEncoder.encode(encryptedString, "UTF-8") + "&integrityCheck=" + integrityCheck;
            transactionlogger.error(type+"() queryParameters " + trackingID + " " + queryParameters);

            preAuthResponse = EasyPaymentGatewayUtils.doHttpPostRequestConnection(queryParameters, ivString);
            transactionlogger.error(type+"() response :" + trackingID + " " + preAuthResponse);

            resultMap = (HashMap) EasyPaymentGatewayUtils.readEasyPayment3DRedirectionXMLReponse(preAuthResponse);
            transactionlogger.error(type+ "() XML READED response :" + trackingID + " " + resultMap);

            String statusResponse           = "";
            String amount1                  = "";
            String currency1                = "";
            String code                     = "";
            String uuid                     = "";
            String message                  = "";
            String redirectionResponse      = "";
            String payFrexTransactionId     = "";
            String responsePaymentSolution  = "";
            String cardNumberToken          = "";
            String subscriptionPlan         = "";

            if (resultMap != null)
            {
                if (resultMap.containsKey("status") && functions.isValueNull(resultMap.get("status")))
                {
                    statusResponse = resultMap.get("status");
                }
                if (resultMap.containsKey("payFrexTransactionId") && functions.isValueNull(resultMap.get("payFrexTransactionId")))
                {
                    payFrexTransactionId = resultMap.get("payFrexTransactionId");
                }
                if (resultMap.containsKey("currency") && functions.isValueNull(resultMap.get("currency")))
                {
                    currency1 = resultMap.get("currency");
                }
                if (resultMap.containsKey("amount") && functions.isValueNull(resultMap.get("amount")))
                {
                    amount1 = resultMap.get("amount");
                }
                if (resultMap.containsKey("code") && functions.isValueNull(resultMap.get("code")))
                {
                    code = resultMap.get("code");
                }
                if (resultMap.containsKey("uuid") && functions.isValueNull(resultMap.get("uuid")))
                {
                    uuid = resultMap.get("uuid");
                }
                if (resultMap.containsKey("message") && functions.isValueNull(resultMap.get("message")))
                {
                    message = resultMap.get("message");
                }
                if (resultMap.containsKey("cardNumberToken") && functions.isValueNull(resultMap.get("cardNumberToken")))
                {
                    cardNumberToken = resultMap.get("cardNumberToken");
                }
                if (resultMap.containsKey("subscriptionPlan") && functions.isValueNull(resultMap.get("subscriptionPlan")))
                {
                    subscriptionPlan = resultMap.get("subscriptionPlan");
                }
                if (resultMap.containsKey("paymentSolution") && functions.isValueNull(resultMap.get("paymentSolution")))
                {
                    responsePaymentSolution = resultMap.get("paymentSolution");
                }

                if (resultMap.containsKey("redirectionResponse") && functions.isValueNull(resultMap.get("redirectionResponse")))
                {
                    redirectionResponse = resultMap.get("redirectionResponse");
                    if (functions.isValueNull(redirectionResponse) && redirectionResponse.contains("redirect:"))
                    {
                        if (redirectionResponse.split(":").length > 0)
                        {
                            redirectionResponse = redirectionResponse.split(":")[1];
                        }
                        else
                        {
                            redirectionResponse = "";
                        }
                    }
                }


                if ("REDIRECTED".equalsIgnoreCase(statusResponse) && functions.isValueNull(redirectionResponse))
                {
                    comm3DResponseVO.setUrlFor3DRedirect(HOST_URL + redirectionResponse);
                    comm3DResponseVO.setStatus("pending3DConfirmation");
                    comm3DResponseVO.setResponseHashInfo(responsePaymentSolution);
                    message = "";
                }
                else if ("declined".equalsIgnoreCase(statusResponse) || "ERROR".equalsIgnoreCase(statusResponse) || "FAIL".equalsIgnoreCase(statusResponse) || "Failed".equalsIgnoreCase(statusResponse) || "ERROR3DS".equalsIgnoreCase(statusResponse))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                }

                comm3DResponseVO.setTransactionId(payFrexTransactionId);
                comm3DResponseVO.setThreeDVersion("3Dv2");
                comm3DResponseVO.setAmount(amount1);
                comm3DResponseVO.setCurrency(currency1);
                comm3DResponseVO.setAuthCode(cardNumberToken);
                EasyPaymentGatewayUtils.updateMainTableEntry(payFrexTransactionId,message,cardNumberToken,subscriptionPlan,trackingID);
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EasyPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        return comm3DResponseVO;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("inside EasyPayment processSale()---------->");

        CommRequestVO commRequestVO         = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO   = new Comm3DResponseVO();
        Functions functions                 = new Functions();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        CommDeviceDetailsVO commDeviceDetailsVO         = commRequestVO.getCommDeviceDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        String payment_brand                            = transactionDetailsVO.getCardType();

        boolean isTest              = gatewayAccount.isTest();
        String isRecurring          = gatewayAccount.getIsRecurring();
        String merchantId           = gatewayAccount.getMerchantId();
        String productId            = gatewayAccount.getFRAUD_FTP_USERNAME();
        String encryptionKey        = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String REDIRECT_URL         = "";
        String NOTIFY_URL           = "";
        String paymentSolution      = "creditcards";
        String operationType        = "DEBIT";

        String merchantTransactionId = "";
        String customerId            = "";

        String REQUEST_URL   = "";
        String HOST_URL      = "";
        String statusURL     = "";
        String successURL    = "";
        String errorURL      = "";
        String cancelURL     = "";
        String awaitingURL   = "";

        String customerEmail = "";
        String addressLine1  = "";
        String addressLine2  = "";
        String telephone     = "";
        String firstName     = "";
        String lastName      = "";
        String chName        = "";

        String cardType      = "";
        String cardNumber    = "";
        String expDate       = "";
        String cvnNumber     = "";
        String amount        = "";
        String currency      = "";
        String ipAddress     = "";
        String description   = "";
        String country       = "";
        String city          = "";
        String state         = "";
        String postCode      = "";
        HashMap<String, String> resultMap;
        String ivString         = "";
        String encryptedString  = "";
        String integrityCheck   = "";
        String queryParameters  = "";
        String saleResponse     = "";

        String acceptHeader     = "";
        String browserIp        = "";
        String browserLanguage  = "";
        String browserTZ        = "";
        String challengeWindowSize = "";
        String javaEnabled      = "";
        String jsEnabled        = "";
        String screenColorDepth = "";
        String screenHeight     = "";
        String screenWidth      = "";
        String userAgent        = "";
        String type             = "";
        //for first recurring
        String paymentRecurringType  = "";
        String merchantExemptionsSca = "";

        try
        {
            NOTIFY_URL  = RB.getString("EASYPAY_NOTIFY_URL") + trackingID;

            if(functions.isValueNull(commMerchantVO.getHostUrl()))
                REDIRECT_URL ="https://"+commMerchantVO.getHostUrl() + RB.getString("ENDPOINT_URL")+trackingID;
            else
                REDIRECT_URL = RB.getString("REDIRECT_URL") + trackingID;

            merchantTransactionId   = trackingID;
            cardType                = EasyPaymentGatewayUtils.getPaymentBrand(payment_brand);
            cardNumber              = commCardDetailsVO.getCardNum();
            expDate                 = commCardDetailsVO.getExpMonth() + "" + EasyPaymentGatewayUtils.getLast2DigitOfExpiryYear(commCardDetailsVO.getExpYear());
            cvnNumber               = commCardDetailsVO.getcVV();
            ipAddress               = commAddressDetailsVO.getCardHolderIpAddress();
            amount                  = transactionDetailsVO.getAmount();
            description             = trackingID;
            currency                = transactionDetailsVO.getCurrency();
            country                 = commAddressDetailsVO.getCountry();
            customerId              = merchantTransactionId;
            city                    = commAddressDetailsVO.getCity();
            state                   = commAddressDetailsVO.getState();
            postCode                = commAddressDetailsVO.getZipCode();
            statusURL               = NOTIFY_URL + "_status";
            successURL              = REDIRECT_URL + "_success";
            errorURL                = REDIRECT_URL + "_error";
            cancelURL               = REDIRECT_URL + "_cancel";
            awaitingURL             = REDIRECT_URL + "_awaiting";
            jsEnabled               = "TRUE";

            if (functions.isValueNull(commAddressDetailsVO.getIp()))
                browserIp = commAddressDetailsVO.getIp();
            else
                browserIp = "192.168.1.1";

            if (commDeviceDetailsVO != null)
            {
                if (functions.isValueNull(commDeviceDetailsVO.getAcceptHeader())){
                    acceptHeader = commDeviceDetailsVO.getAcceptHeader();// if not work add else part here
                }else {
                    acceptHeader = "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9";
                }

                if (functions.isValueNull(commDeviceDetailsVO.getFingerprints()))
                {
                    transactionlogger.error(trackingID+ ">>>>>>Inside Fingerprints if condition----> ");
                    HashMap fingerPrintMap = functions.getFingerPrintMap(commDeviceDetailsVO.getFingerprints());
                    JSONArray screenResolution = new JSONArray();
                    try
                    {
                        if (fingerPrintMap.containsKey("userAgent"))
                            userAgent = (String) fingerPrintMap.get("userAgent");
                        if (fingerPrintMap.containsKey("language"))
                            browserLanguage = (String) fingerPrintMap.get("language");
                        if (fingerPrintMap.containsKey("colorDepth"))
                            screenColorDepth = String.valueOf(fingerPrintMap.get("colorDepth"));
                        if (fingerPrintMap.containsKey("timezoneOffset"))
                            browserTZ = String.valueOf(fingerPrintMap.get("timezoneOffset"));
                        if (fingerPrintMap.containsKey("screenResolution"))
                            screenResolution = (JSONArray) fingerPrintMap.get("screenResolution");
                        if (screenResolution.length() > 0)
                            screenHeight = String.valueOf(screenResolution.getString(0));
                        if (screenResolution.length() > 1)
                            screenWidth = String.valueOf(screenResolution.getString(1));
                        challengeWindowSize = "05";
                        javaEnabled = "TRUE";
                        transactionlogger.error(trackingID+ ">>>>>screenResolution: " + screenResolution);
                    }
                    catch (JSONException e)
                    {
                        transactionlogger.error("JSONException---trackingId--" + trackingID + "--", e);
                    }
                }
                else
                {
                    transactionlogger.error(trackingID+" >>>>Inside Fingerprints else condition --------> ");
                    if (functions.isValueNull(commDeviceDetailsVO.getUser_Agent()))
                        userAgent = commDeviceDetailsVO.getUser_Agent();
                    else
                        userAgent = "Mozilla/5.0 (Android; Mobile; rv:13.0) Gecko/13.0 Firefox/13.0";
                    if (functions.isValueNull(commDeviceDetailsVO.getBrowserLanguage()))
                        browserLanguage = commDeviceDetailsVO.getBrowserLanguage();
                    else
                        browserLanguage = "en-US";
                    if (functions.isValueNull(commDeviceDetailsVO.getBrowserColorDepth()))
                        screenColorDepth = commDeviceDetailsVO.getBrowserColorDepth();
                    else
                        screenColorDepth = "24";
                    if (functions.isValueNull(commDeviceDetailsVO.getBrowserTimezoneOffset()))
                        browserTZ = commDeviceDetailsVO.getBrowserTimezoneOffset();
                    else
                        browserTZ = "5";
                    if (functions.isValueNull(commDeviceDetailsVO.getBrowserScreenHeight()))
                        screenHeight = commDeviceDetailsVO.getBrowserScreenHeight();
                    else
                        screenHeight = "939";
                    if (functions.isValueNull(commDeviceDetailsVO.getBrowserScreenWidth()))
                        screenWidth = commDeviceDetailsVO.getBrowserScreenWidth();
                    else
                        screenWidth = "1255";
                    if (functions.isValueNull(commDeviceDetailsVO.getBrowserJavaEnabled()))
                        javaEnabled = commDeviceDetailsVO.getBrowserJavaEnabled();
                    else
                        javaEnabled = "TRUE";
                    challengeWindowSize = "05";
                }
            }
            else
            {
                acceptHeader        = "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9";
                userAgent           = "Mozilla/5.0 (Android; Mobile; rv:13.0) Gecko/13.0 Firefox/13.0";
                browserLanguage     = "en-US";
                screenColorDepth    = "24";
                browserTZ           = "5";
                screenHeight        = "939";
                screenWidth         = "1255";
                javaEnabled         = "TRUE";
                challengeWindowSize = "05";
            }

            if (isTest)
            {
                REQUEST_URL = RB.getString("SALE_TEST_URL");
                HOST_URL    = RB.getString("TEST_HOST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("SALE_LIVE_URL");
                HOST_URL    = RB.getString("LIVE_HOST_URL");
            }

            //for recurring
            paymentRecurringType  = "newCof";
            merchantExemptionsSca = "LWV;TRA"; //"LWV;TRA[250.0];COR;MIT;ATD"; //"LWV;TRA";


            if (functions.isValueNull(country) && country.length() != 2)
            {
                if (EasyPaymentGatewayUtils.getCountryCodeHash().containsKey(country))
                {
                    country = EasyPaymentGatewayUtils.getCountryCodeHash().getOrDefault(country,"ES").toString();
                }
            }

            if (functions.isValueNull(state) && state.length() != 2)
            {
                if (EasyPaymentGatewayUtils.getStateCodeHash().containsKey(state))
                {
                    state = EasyPaymentGatewayUtils.getStateCodeHash().getOrDefault(state, state).toString();
                }
            }     // as per bank no need 2 char state from now


            if (functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname()))
            {
                chName = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();
            }
            else
            {
                chName = "customer";
            }

            if (functions.isValueNull(commAddressDetailsVO.getFirstname()))
            {
                firstName = commAddressDetailsVO.getFirstname();
            }
            else
            {
                firstName = "customer";
            }

            if (functions.isValueNull(commAddressDetailsVO.getLastname()))
            {
                lastName = commAddressDetailsVO.getLastname();
            }
            else
            {
                lastName = "customer";
            }

            if (functions.isValueNull(commAddressDetailsVO.getEmail()))
            {
                customerEmail = commAddressDetailsVO.getEmail();
            }
            else
            {
                customerEmail = "customer@gmail.com";
            }

            if (functions.isValueNull(commAddressDetailsVO.getPhone()))
            {
                telephone = commAddressDetailsVO.getPhone();
            }
            else
            {
                telephone = "9999999999";
            }

            addressLine1 = commAddressDetailsVO.getStreet() + "," + commAddressDetailsVO.getCity();
            addressLine2 = commAddressDetailsVO.getState();


            StringBuffer stringBuffer = new StringBuffer();
            StringBuffer stringBufferLog = new StringBuffer();

            //first recurring
            if ("Y".equals(isRecurring))
            {
                type = "processRebilling";
                //for recurring
                transactionlogger.error("inside if for rebilling--->"+trackingID);
                stringBuffer.append("merchantId=" + merchantId);
                stringBuffer.append("&paymentSolution=" + paymentSolution);
                stringBuffer.append("&productId=" + productId);
                stringBuffer.append("&operationType=" + operationType);
                stringBuffer.append("&merchantTransactionId=" + merchantTransactionId);
                stringBuffer.append("&currency=" + currency);
                stringBuffer.append("&amount=" + amount);
                stringBuffer.append("&country=" + country);
                stringBuffer.append("&description=" + description);
                stringBuffer.append("&cvnNumber=" + cvnNumber);
                stringBuffer.append("&expDate=" + expDate);
                stringBuffer.append("&paymentRecurringType=" + paymentRecurringType);
                stringBuffer.append("&cardNumber=" + cardNumber);
                stringBuffer.append("&merchantExemptionsSca=" + merchantExemptionsSca);
                stringBuffer.append("&customerId=" + customerId);
                stringBuffer.append("&successURL=" + successURL);
                stringBuffer.append("&statusURL=" + statusURL);
                stringBuffer.append("&errorURL=" + errorURL);
                stringBuffer.append("&cancelURL=" + cancelURL);
                stringBuffer.append("&awaitingURL=" + awaitingURL);


                //for masking logs
                stringBufferLog.append("merchantId=" + merchantId);
                stringBufferLog.append("&paymentSolution=" + paymentSolution);
                stringBufferLog.append("&productId=" + productId);
                stringBufferLog.append("&operationType=" + operationType);
                stringBufferLog.append("&merchantTransactionId=" + merchantTransactionId);
                stringBufferLog.append("&currency=" + currency);
                stringBufferLog.append("&amount=" + amount);
                stringBufferLog.append("&country=" + country);
                stringBufferLog.append("&description=" + description);
                stringBufferLog.append("&cvnNumber=" + functions.maskingNumber(cvnNumber));
                stringBufferLog.append("&expDate=" + functions.maskingNumber(expDate));
                stringBufferLog.append("&paymentRecurringType=" + paymentRecurringType);
                stringBufferLog.append("&cardNumber=" + functions.maskingPan(cardNumber));
                stringBufferLog.append("&merchantExemptionsSca=" + merchantExemptionsSca);
                stringBufferLog.append("&customerId=" + customerId);
                stringBufferLog.append("&successURL=" + successURL);
                stringBufferLog.append("&statusURL=" + statusURL);
                stringBufferLog.append("&errorURL=" + errorURL);
                stringBufferLog.append("&cancelURL=" + cancelURL);
                stringBufferLog.append("&awaitingURL=" + awaitingURL);


                transactionlogger.error("processRebilling()  requestParameter : " + trackingID+ " " +stringBufferLog);
            }
            else
            {
                type = "processSale";
                //for sale
                stringBuffer.append("merchantId=" + merchantId);
                stringBuffer.append("&productId=" + productId);
                stringBuffer.append("&paymentSolution=" + paymentSolution);
                stringBuffer.append("&operationType=" + operationType);
                stringBuffer.append("&amount=" + amount);
                stringBuffer.append("&currency=" + currency);
                stringBuffer.append("&country=" + country);
                stringBuffer.append("&description=" + description);
                stringBuffer.append("&merchantTransactionId=" + merchantTransactionId);
                stringBuffer.append("&customerId=" + customerId);

                stringBuffer.append("&customerEmail=" + customerEmail);
                stringBuffer.append("&addressLine1=" + addressLine1);
                stringBuffer.append("&addressLine2=" + addressLine2);
                stringBuffer.append("&city=" + city);
                stringBuffer.append("&telephone=" + telephone);
                stringBuffer.append("&firstName=" + firstName);
                stringBuffer.append("&lastName=" + lastName);
                stringBuffer.append("&successURL=" + successURL);
                stringBuffer.append("&statusURL=" + statusURL);
                stringBuffer.append("&errorURL=" + errorURL);
                stringBuffer.append("&cancelURL=" + cancelURL);
                stringBuffer.append("&awaitingURL=" + awaitingURL);


                stringBuffer.append("&ipAddress=" + ipAddress);
                stringBuffer.append("&customerCountry=" + country);
                stringBuffer.append("&state=" + state);
                stringBuffer.append("&postCode=" + postCode);

                //for card
                stringBuffer.append("&cardType=" + cardType);
                stringBuffer.append("&cardNumber=" + cardNumber);
                stringBuffer.append("&chName=" + chName);
                stringBuffer.append("&expDate=" + expDate);
                stringBuffer.append("&cvnNumber=" + cvnNumber);

                stringBuffer.append("&chAddress1=" + addressLine1);
                stringBuffer.append("&chAddress2=" + addressLine2);
                stringBuffer.append("&chCity=" + city);
                stringBuffer.append("&chPostCode=" + postCode);
                stringBuffer.append("&chCountry=" + country);
                stringBuffer.append("&chEmail=" + customerEmail);
                stringBuffer.append("&chFirstName=" + firstName);
                stringBuffer.append("&chLastName=" + lastName);
                stringBuffer.append("&chPhone=" + telephone);
                stringBuffer.append("&chState=" + state);

                //browser parameters
                stringBuffer.append("&acceptHeader=" + acceptHeader);
                stringBuffer.append("&browserIp=" + browserIp);
                stringBuffer.append("&browserLanguage=" + browserLanguage);
                stringBuffer.append("&browserTZ=" + browserTZ);
                stringBuffer.append("&challengeWindowSize=" + challengeWindowSize);
                stringBuffer.append("&javaEnabled=" + javaEnabled);
                stringBuffer.append("&jsEnabled=" + jsEnabled);
                stringBuffer.append("&screenColorDepth=" + screenColorDepth);
                stringBuffer.append("&screenHeight=" + screenHeight);
                stringBuffer.append("&screenWidth=" + screenWidth);
                stringBuffer.append("&userAgent=" + userAgent);


                // masking for logs
                stringBufferLog.append("merchantId=" + merchantId);
                stringBufferLog.append("&productId=" + productId);
                stringBufferLog.append("&paymentSolution=" + paymentSolution);
                stringBufferLog.append("&operationType=" + operationType);
                stringBufferLog.append("&amount=" + amount);
                stringBufferLog.append("&currency=" + currency);
                stringBufferLog.append("&country=" + country);
                stringBufferLog.append("&description=" + description);
                stringBufferLog.append("&merchantTransactionId=" + merchantTransactionId);
                stringBufferLog.append("&customerId=" + customerId);

                stringBufferLog.append("&customerEmail=" + functions.getEmailMasking(customerEmail));
                stringBufferLog.append("&addressLine1=" + addressLine1);
                stringBufferLog.append("&addressLine2=" + addressLine2);
                stringBufferLog.append("&city=" + city);
                stringBufferLog.append("&telephone=" + functions.getPhoneNumMasking(telephone));
                stringBufferLog.append("&firstName=" + functions.maskingFirstName(firstName));
                stringBufferLog.append("&lastName=" + functions.maskingLastName(lastName));
                stringBufferLog.append("&successURL=" + successURL);
                stringBufferLog.append("&statusURL=" + statusURL);
                stringBufferLog.append("&errorURL=" + errorURL);
                stringBufferLog.append("&cancelURL=" + cancelURL);
                stringBufferLog.append("&awaitingURL=" + awaitingURL);

                stringBufferLog.append("&ipAddress=" + ipAddress);
                stringBufferLog.append("&customerCountry=" + country);
                stringBufferLog.append("&state=" + state);
                stringBufferLog.append("&postCode=" + postCode);

                //for card
                stringBufferLog.append("&cardType=" + cardType);
                stringBufferLog.append("&cardNumber=" + functions.maskingPan(cardNumber));
                stringBufferLog.append("&chName=" + functions.maskingFirstName(chName));
                stringBufferLog.append("&expDate=" + functions.maskingNumber(expDate));
                stringBufferLog.append("&cvnNumber=" + functions.maskingNumber(cvnNumber));

                stringBufferLog.append("&chAddress1=" + addressLine1);
                stringBufferLog.append("&chAddress2=" + addressLine2);
                stringBufferLog.append("&chCity=" + city);
                stringBufferLog.append("&chPostCode=" + postCode);
                stringBufferLog.append("&chCountry=" + country);
                stringBufferLog.append("&chEmail=" + functions.getEmailMasking(customerEmail));
                stringBufferLog.append("&chFirstName=" + functions.maskingFirstName(firstName));
                stringBufferLog.append("&chLastName=" + functions.maskingLastName(lastName));
                stringBufferLog.append("&chPhone=" + functions.getPhoneNumMasking(telephone));
                stringBufferLog.append("&chState=" + state);

                //browser parameters
                stringBufferLog.append("&acceptHeader=" + acceptHeader);
                stringBufferLog.append("&browserIp=" + browserIp);
                stringBufferLog.append("&browserLanguage=" + browserLanguage);
                stringBufferLog.append("&browserTZ=" + browserTZ);
                stringBufferLog.append("&challengeWindowSize=" + challengeWindowSize);
                stringBufferLog.append("&javaEnabled=" + javaEnabled);
                stringBufferLog.append("&jsEnabled=" + jsEnabled);
                stringBufferLog.append("&screenColorDepth=" + screenColorDepth);
                stringBufferLog.append("&screenHeight=" + screenHeight);
                stringBufferLog.append("&screenWidth=" + screenWidth);
                stringBufferLog.append("&userAgent=" + userAgent);
            }
            transactionlogger.error(type+"() requestParameters :" + trackingID + " " + stringBufferLog.toString());

            IvParameterSpec iv  = EasyPaymentGatewayUtils.generateIv();
            ivString     = Base64.getEncoder().encodeToString(iv.getIV());

            encryptedString  = EasyPaymentGatewayUtils.encryptCBC(stringBuffer.toString(), encryptionKey, iv);

            integrityCheck   = EasyPaymentGatewayUtils.generateSHA256Hash(stringBuffer.toString());

            queryParameters  = REQUEST_URL + "merchantId=" + merchantId + "&encrypted=" + URLEncoder.encode(encryptedString, "UTF-8") + "&integrityCheck=" + integrityCheck;
            transactionlogger.error(type+"() queryParameters: " + trackingID + " " + queryParameters);

            saleResponse = EasyPaymentGatewayUtils.doHttpPostRequestConnection(queryParameters, ivString);
            transactionlogger.error(type+"() response :" + trackingID + " " + saleResponse);

            resultMap = (HashMap) EasyPaymentGatewayUtils.readEasyPayment3DRedirectionXMLReponse(saleResponse);
            transactionlogger.error("processSale() responseHM : " + trackingID + " " + resultMap);


            String statusResponse           = "";
            String amount1                  = "";
            String currency1                = "";
            String code                     = "";
            String uuid                     = "";
            String message                  = "";
            String redirectionResponse      = "";
            String payFrexTransactionId     = "";
            String responsePaymentSolution  = "";
            String cardNumberToken          = "";
            String subscriptionPlan         = "";


            if (resultMap != null)
            {
                if (resultMap.containsKey("status") && functions.isValueNull(resultMap.get("status")))
                {
                    statusResponse = resultMap.get("status");
                }
                if (resultMap.containsKey("payFrexTransactionId") && functions.isValueNull(resultMap.get("payFrexTransactionId")))
                {
                    payFrexTransactionId = resultMap.get("payFrexTransactionId");
                }
                if (resultMap.containsKey("currency") && functions.isValueNull(resultMap.get("currency")))
                {
                    currency1 = resultMap.get("currency");
                }
                if (resultMap.containsKey("amount") && functions.isValueNull(resultMap.get("amount")))
                {
                    amount1 = resultMap.get("amount");
                }
                if (resultMap.containsKey("code") && functions.isValueNull(resultMap.get("code")))
                {
                    code = resultMap.get("code");
                }
                if (resultMap.containsKey("uuid") && functions.isValueNull(resultMap.get("uuid")))
                {
                    uuid = resultMap.get("uuid");
                }
                if (resultMap.containsKey("message") && functions.isValueNull(resultMap.get("message")))
                {
                    message = resultMap.get("message");
                }
                if (resultMap.containsKey("cardNumberToken") && functions.isValueNull(resultMap.get("cardNumberToken")))
                {
                    cardNumberToken = resultMap.get("cardNumberToken");
                }
                if (resultMap.containsKey("subscriptionPlan") && functions.isValueNull(resultMap.get("subscriptionPlan")))
                {
                    subscriptionPlan = resultMap.get("subscriptionPlan");
                }
                if (resultMap.containsKey("paymentSolution") && functions.isValueNull(resultMap.get("paymentSolution")))
                {
                    responsePaymentSolution = resultMap.get("paymentSolution");
                }

                if (resultMap.containsKey("redirectionResponse") && functions.isValueNull(resultMap.get("redirectionResponse")))
                {
                    redirectionResponse = resultMap.get("redirectionResponse");
                    if (functions.isValueNull(redirectionResponse) && redirectionResponse.contains("redirect:"))
                    {
                        if (redirectionResponse.split(":").length > 0)
                        {
                            redirectionResponse = redirectionResponse.split(":")[1];
                        }
                        else
                        {
                            redirectionResponse = "";
                        }
                    }
                }


                if ("REDIRECTED".equalsIgnoreCase(statusResponse) && functions.isValueNull(redirectionResponse))
                {
                    comm3DResponseVO.setUrlFor3DRedirect(HOST_URL + redirectionResponse);
                    comm3DResponseVO.setStatus("pending3DConfirmation");
                    comm3DResponseVO.setResponseHashInfo(responsePaymentSolution);
                    message = "";
                }
                else if ("declined".equalsIgnoreCase(statusResponse) || "ERROR".equalsIgnoreCase(statusResponse) || "FAIL".equalsIgnoreCase(statusResponse) || "Failed".equalsIgnoreCase(statusResponse) || "ERROR3DS".equalsIgnoreCase(statusResponse))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                }

                comm3DResponseVO.setTransactionId(payFrexTransactionId);
                comm3DResponseVO.setThreeDVersion("3Dv2");
                comm3DResponseVO.setAmount(amount1);
                comm3DResponseVO.setCurrency(currency1);
                comm3DResponseVO.setAuthCode(cardNumberToken);
                EasyPaymentGatewayUtils.updateMainTableEntry(payFrexTransactionId,message,cardNumberToken,subscriptionPlan,trackingID);
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            }
        }
        catch(Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EasyPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Inside EasyPayment processInquiry() ------>");

        Comm3DResponseVO comm3DResponseVO   = new Comm3DResponseVO();
        Functions functions                 = new Functions();
        GatewayAccount gatewayAccount       = GatewayAccountService.getGatewayAccount(accountId);

        String REQUEST_URL              = "";
        boolean isTest                  = gatewayAccount.isTest();
        String merchantId               = gatewayAccount.getMerchantId();
        String encryptionKey            = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String merchantTransactionId    = trackingID;
        HashMap<String, String> resultMap;
        String token            = "";
        String inquiryRequest   = "";
        String inquiryResponse  = "";

        try
        {
            if (isTest)
            {
                REQUEST_URL = RB.getString("INQUIRY_TEST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("INQUIRY_LIVE_URL");
            }

            token = merchantId + "." + merchantTransactionId + "." + encryptionKey;
            inquiryRequest = REQUEST_URL + "merchantId=" + merchantId + "&token=" + EasyPaymentGatewayUtils.applyMD5encryption(token) + "&transactions=" + merchantTransactionId;
            transactionlogger.error("processInquiry() queryParameters : " + trackingID + " " + inquiryRequest);

            inquiryResponse = EasyPaymentGatewayUtils.doHttpPostRequestConnection(inquiryRequest);
            transactionlogger.error("processInquiry() Response " + trackingID + " " + inquiryResponse);

            resultMap = (HashMap) EasyPaymentGatewayUtils.readEasyPayment3DRedirectionXMLReponse(inquiryResponse);
            transactionlogger.error("processInquiry() XML READED response : " + trackingID + " " + resultMap);

            String statusResponse       = "";
            String amount               = "";
            String code                 = "";
            String authCode             = "";
            String uuid                 = "";
            String message              = "";
            String payFrexTransactionId = "";
            String details              = "";
            String version              = "";
            String currency             = "";
            String paymentMethod        = "";
            String operation            = "";
            String cardNumberToken      = "";
            String subscriptionPlan     = "";
            String responsePaymentSolution  = "";
            String merchantExemptionsSca    = "";
            String rfOperationDateTime      = "";

            if (resultMap != null)
            {
                if (resultMap.containsKey("status") && functions.isValueNull(resultMap.get("status")))
                {
                    statusResponse = resultMap.get("status");
                }
                if (resultMap.containsKey("payFrexTransactionId") && functions.isValueNull(resultMap.get("payFrexTransactionId")))
                {
                    payFrexTransactionId = resultMap.get("payFrexTransactionId");
                }
                if (resultMap.containsKey("code") && functions.isValueNull(resultMap.get("code")))
                {
                    code = resultMap.get("code");
                }
                if (resultMap.containsKey("authCode") && functions.isValueNull(resultMap.get("authCode")))
                {
                    authCode = resultMap.get("authCode");
                }
                if (resultMap.containsKey("uuid") && functions.isValueNull(resultMap.get("uuid")))
                {
                    uuid = resultMap.get("uuid");
                }
                if (resultMap.containsKey("message") && functions.isValueNull(resultMap.get("message")))
                {
                    message = resultMap.get("message");
                }
                if (resultMap.containsKey("amount") && functions.isValueNull(resultMap.get("amount")))
                {
                    amount = resultMap.get("amount");
                }
                if (resultMap.containsKey("details") && functions.isValueNull(resultMap.get("details")))
                {
                    details = resultMap.get("details");
                }
                if (resultMap.containsKey("version") && functions.isValueNull(resultMap.get("version")))
                {
                    version = resultMap.get("version");
                }
                if (resultMap.containsKey("currency") && functions.isValueNull(resultMap.get("currency")))
                {
                    currency = resultMap.get("currency");
                }
                if (resultMap.containsKey("paymentMethod") && functions.isValueNull(resultMap.get("paymentMethod")))
                {
                    paymentMethod = resultMap.get("paymentMethod");
                }
                if (resultMap.containsKey("paymentSolution") && functions.isValueNull(resultMap.get("paymentSolution")))
                {
                    responsePaymentSolution = resultMap.get("paymentSolution");
                }
                if (resultMap.containsKey("cardNumberToken") && functions.isValueNull(resultMap.get("cardNumberToken")))
                {
                    cardNumberToken = resultMap.get("cardNumberToken");
                }
                if (resultMap.containsKey("subscriptionPlan") && functions.isValueNull(resultMap.get("subscriptionPlan")))
                {
                    subscriptionPlan = resultMap.get("subscriptionPlan");
                }

                if (functions.isValueNull(paymentMethod))
                {
                    JSONObject paymentMethodinfo = new JSONObject(paymentMethod);
                    if (paymentMethodinfo.has("operation") && functions.isValueNull(paymentMethodinfo.getString("operation")))
                    {
                        operation = paymentMethodinfo.getString("operation");
                    }
                }

                if (functions.isValueNull(details))
                {
                    JSONObject detailsJson = new JSONObject(details);
                    if (detailsJson.has("values") && functions.isValueNull(detailsJson.getString("values")))
                    {
                        JSONObject valuesJson = new JSONObject(detailsJson.getString("values"));
                        if (valuesJson.has("initEmv3dsResponse") && functions.isValueNull(valuesJson.getString("initEmv3dsResponse")))
                        {
                            JSONObject initEmv3dsResponseJSON = new JSONObject(valuesJson.getString("initEmv3dsResponse"));
                            if (initEmv3dsResponseJSON.has("exceptions") && functions.isValueNull(initEmv3dsResponseJSON.getString("exceptions")))
                            {
                                merchantExemptionsSca = initEmv3dsResponseJSON.getString("exceptions");
                            }
                        }

                        if (valuesJson.has("rfOperationDateTime") && functions.isValueNull(valuesJson.getString("rfOperationDateTime")))
                        {
                            rfOperationDateTime = valuesJson.getString("rfOperationDateTime");
                        }
                    }
                }


                if ("SUCCESS".equalsIgnoreCase(statusResponse))
                {
                    comm3DResponseVO.setStatus("Success");
                    comm3DResponseVO.setTransactionStatus("Success");
                    comm3DResponseVO.setDescriptor(operation);
                    comm3DResponseVO.setErrorCode(code);
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                    comm3DResponseVO.setAmount(amount);
                    comm3DResponseVO.setTransactionId(payFrexTransactionId);
                    comm3DResponseVO.setResponseHashInfo(responsePaymentSolution);
                    comm3DResponseVO.setTransactionType(PZProcessType.SALE.toString());
                }
                else if ("declined".equalsIgnoreCase(statusResponse) || "ERROR".equalsIgnoreCase(statusResponse) || "FAIL".equalsIgnoreCase(statusResponse) || "Failed".equalsIgnoreCase(statusResponse) || "ERROR3DS".equalsIgnoreCase(statusResponse))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                    comm3DResponseVO.setAmount(amount);
                    comm3DResponseVO.setErrorCode(code);
                    comm3DResponseVO.setTransactionId(payFrexTransactionId);
                    comm3DResponseVO.setResponseHashInfo(responsePaymentSolution);
                }
                else if ("pending".equalsIgnoreCase(statusResponse) && "auth".equalsIgnoreCase(operation))
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescriptor(operation);
                    comm3DResponseVO.setTransactionId(payFrexTransactionId);
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                    comm3DResponseVO.setErrorCode(code);
                    comm3DResponseVO.setResponseHashInfo(responsePaymentSolution);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setRemark("Transaction is pending");
                    comm3DResponseVO.setDescription("Transaction is pending");
                }

                comm3DResponseVO.setMerchantId(merchantId);
                comm3DResponseVO.setBankTransactionDate(rfOperationDateTime);
                comm3DResponseVO.setResponseTime(rfOperationDateTime);
                comm3DResponseVO.setBankCode(authCode);
                comm3DResponseVO.setBankDescription(message);
                comm3DResponseVO.setTransactionId(payFrexTransactionId);
                comm3DResponseVO.setTransactionType(PZProcessType.SALE.toString());
                comm3DResponseVO.setAmount(amount);
                comm3DResponseVO.setCurrency(currency);
                comm3DResponseVO.setThreeDVersion("3Dv2");
                comm3DResponseVO.setAuthCode(cardNumberToken);
                EasyPaymentGatewayUtils.updateMainTableEntry(payFrexTransactionId,message,cardNumberToken,subscriptionPlan,trackingID);
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EasyPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside EasyPayment processRefund() ---------->");

        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Functions functions                                 = new Functions();
        Comm3DResponseVO comm3DResponseVO                   = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);


        boolean isTest              = gatewayAccount.isTest();
        String encryptionKey        = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String merchantId           = gatewayAccount.getMerchantId();

        String REQUEST_URL           = "";
        String paymentSolution       = "";
        String merchantTransactionId = "";
        String amount                = "";
        String transactionId         = "";
        String description           = "";
        HashMap<String, String> resultMap;
        String ivString         = "";
        String encryptedString  = "";
        String integrityCheck   = "";
        String queryParameters  = "";
        String refundResponse   = "";

        try
        {
            paymentSolution          = EasyPaymentGatewayUtils.getPaymentSolution(trackingID);
            merchantTransactionId    = trackingID;
            amount                   = commTransactionDetailsVO.getAmount();
            transactionId            = commTransactionDetailsVO.getPreviousTransactionId();
            description              = trackingID;

            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_REFUND_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_REFUND_URL");
            }

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("merchantId=" + merchantId);
            stringBuffer.append("&transactionId=" + transactionId);
            stringBuffer.append("&paymentSolution=" + paymentSolution);
            stringBuffer.append("&description=" + description);
            stringBuffer.append("&merchantTransactionId=" + merchantTransactionId);
            stringBuffer.append("&amount=" + amount);

            transactionlogger.error("processRefund() requestParameters :" + trackingID + " " + stringBuffer);

            IvParameterSpec iv = EasyPaymentGatewayUtils.generateIv();
            ivString = Base64.getEncoder().encodeToString(iv.getIV());

            encryptedString = EasyPaymentGatewayUtils.encryptCBC(stringBuffer.toString(), encryptionKey, iv);

            integrityCheck = EasyPaymentGatewayUtils.generateSHA256Hash(stringBuffer.toString());

            queryParameters = REQUEST_URL + "merchantId=" + merchantId + "&encrypted=" + URLEncoder.encode(encryptedString, "UTF-8") + "&integrityCheck=" + integrityCheck;
            transactionlogger.error("processRefund() queryParameters : " + trackingID + " " + queryParameters);

            refundResponse = EasyPaymentGatewayUtils.doHttpPostRequestConnection(queryParameters, ivString);
            transactionlogger.error("processRefund() response : " + trackingID + " " + refundResponse);

            resultMap = (HashMap) EasyPaymentGatewayUtils.readEasyPayment3DRedirectionXMLReponse(refundResponse);
            transactionlogger.error("processRefund() XML READED response :" + trackingID + " " + resultMap);

            String statusResponse       = "";
            String amount1              = "";
            String code                 = "";
            String uuid                 = "";
            String message              = "";
            String authCode             = "";
            String currency             = "";
            String details              = "";
            String payFrexTransactionId = "";
            String rfOperationDateTime  = "";

            if (resultMap != null)
            {
                if (resultMap.containsKey("status") && functions.isValueNull(resultMap.get("status")))
                {
                    statusResponse = resultMap.get("status");
                }
                if (resultMap.containsKey("payFrexTransactionId") && functions.isValueNull(resultMap.get("payFrexTransactionId")))
                {
                    payFrexTransactionId = resultMap.get("payFrexTransactionId");
                }
                if (resultMap.containsKey("code") && functions.isValueNull(resultMap.get("code")))
                {
                    code = resultMap.get("code");
                }
                if (resultMap.containsKey("uuid") && functions.isValueNull(resultMap.get("uuid")))
                {
                    uuid = resultMap.get("uuid");
                }
                if (resultMap.containsKey("message") && functions.isValueNull(resultMap.get("message")))
                {
                    message = resultMap.get("message");
                }
                if (resultMap.containsKey("amount") && functions.isValueNull(resultMap.get("amount")))
                {
                    amount1 = resultMap.get("amount");
                }
                if (resultMap.containsKey("currency") && functions.isValueNull(resultMap.get("currency")))
                {
                    currency = resultMap.get("currency");
                }
                if (resultMap.containsKey("authCode") && functions.isValueNull(resultMap.get("authCode")))
                {
                    authCode = resultMap.get("authCode");
                }
                if (resultMap.containsKey("details") && functions.isValueNull(resultMap.get("details")))
                {
                    details = resultMap.get("details");
                }

                if (functions.isValueNull(details))
                {
                    JSONObject detailsJson = new JSONObject(details);
                    if (detailsJson.has("values") && functions.isValueNull(detailsJson.getString("values")))
                    {
                        JSONObject valuesJson = new JSONObject(detailsJson.getString("values"));
                        if (valuesJson.has("rfOperationDateTime") && functions.isValueNull(valuesJson.getString("rfOperationDateTime")))
                        {
                            rfOperationDateTime = valuesJson.getString("rfOperationDateTime");
                        }
                    }
                }

                if ("SUCCESS".equalsIgnoreCase(statusResponse))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    comm3DResponseVO.setTransactionId(payFrexTransactionId);
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                    comm3DResponseVO.setAmount(amount1);
                }
                else if ("declined".equalsIgnoreCase(statusResponse) || "ERROR".equalsIgnoreCase(statusResponse) || "FAIL".equalsIgnoreCase(statusResponse) || "Failed".equalsIgnoreCase(statusResponse) || "ERROR3DS".equalsIgnoreCase(statusResponse))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    comm3DResponseVO.setTransactionId(payFrexTransactionId);
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                    comm3DResponseVO.setAmount(amount1);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                }
                comm3DResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                comm3DResponseVO.setMerchantId(merchantId);
                comm3DResponseVO.setTransactionId(payFrexTransactionId);
                comm3DResponseVO.setAuthCode(authCode);
                comm3DResponseVO.setBankTransactionDate(rfOperationDateTime);
                comm3DResponseVO.setResponseTime(rfOperationDateTime);
                comm3DResponseVO.setBankCode(code);
                comm3DResponseVO.setBankDescription(message);
                comm3DResponseVO.setAmount(amount1);
                comm3DResponseVO.setCurrency(currency);
                comm3DResponseVO.setThreeDVersion("3Dv2");
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EasyPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        return comm3DResponseVO;
    }

    public GenericResponseVO processCapture(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionlogger.error("inside EasyPayment processCapture() --------->");
       
        CommRequestVO commRequestVO                 = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransDetailsVO = commRequestVO.getTransDetailsVO();
        Comm3DResponseVO comm3DResponseVO           = new Comm3DResponseVO();
        Functions functions                         = new Functions();
        GatewayAccount gatewayAccount               = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest                  = gatewayAccount.isTest();
        String merchantId               = gatewayAccount.getMerchantId();
        String encryptionKey            = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String merchantTransactionId    = trackingId;
        String apiVersion               = "3";

        String REQUEST_URL     = "";
        String paymentSolution = "";
        String transactionId   = "";
        String description     = "";
        HashMap<String, String> resultMap;
        String ivString         = "";
        String encryptedString  = "";
        String integrityCheck   = "";
        String queryParameters  = "";
        String captureResponse  = "";

        try
        {
            paymentSolution      = EasyPaymentGatewayUtils.getPaymentSolution(trackingId);
            transactionId        = commTransDetailsVO.getPreviousTransactionId();
            description          = trackingId;

            if (isTest)
            {
                REQUEST_URL = RB.getString("CAPTURE_TEST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("CAPTURE_LIVE_URL");
            }

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("merchantId=" + merchantId);
            stringBuffer.append("&transactionId=" + transactionId);
            stringBuffer.append("&paymentSolution=" + paymentSolution);
            stringBuffer.append("&description=" + description);
            stringBuffer.append("&merchantTransactionId=" + merchantTransactionId);
            stringBuffer.append("&apiVersion=" + apiVersion);

            transactionlogger.error("processCapture() requestParameters :"+ trackingId + " " + stringBuffer);

            IvParameterSpec iv = EasyPaymentGatewayUtils.generateIv();
            ivString = Base64.getEncoder().encodeToString(iv.getIV());

            encryptedString = EasyPaymentGatewayUtils.encryptCBC(stringBuffer.toString(), encryptionKey, iv);

            integrityCheck = EasyPaymentGatewayUtils.generateSHA256Hash(stringBuffer.toString());

            queryParameters = REQUEST_URL + "merchantId=" + merchantId + "&encrypted=" + URLEncoder.encode(encryptedString, "UTF-8") + "&integrityCheck=" + integrityCheck;
            transactionlogger.error("processCapture() queryParameters :" + trackingId + " " + queryParameters);

            captureResponse = EasyPaymentGatewayUtils.doHttpPostRequestConnection(queryParameters, ivString);
            transactionlogger.error("processCapture() response :" + trackingId + " " + captureResponse);

            resultMap = (HashMap) EasyPaymentGatewayUtils.readEasyPayment3DRedirectionXMLReponse(captureResponse);
            transactionlogger.error("processCapture() XML READED response :" + trackingId + " " + resultMap);

            String statusResponse   = "";
            String amount           = "";
            String code             = "";
            String authCode         = "";
            String uuid             = "";
            String message          = "";
            String details          = "";
            String version          = "";
            String currency         = "";
            String payFrexTransactionId = "";

            if (resultMap != null)
            {
                if (resultMap.containsKey("status") && functions.isValueNull(resultMap.get("status")))
                {
                    statusResponse = resultMap.get("status");
                }
                if (resultMap.containsKey("payFrexTransactionId") && functions.isValueNull(resultMap.get("payFrexTransactionId")))
                {
                    payFrexTransactionId = resultMap.get("payFrexTransactionId");
                }
                if (resultMap.containsKey("code") && functions.isValueNull(resultMap.get("code")))
                {
                    code = resultMap.get("code");
                }
                if (resultMap.containsKey("authCode") && functions.isValueNull(resultMap.get("authCode")))
                {
                    authCode = resultMap.get("authCode");
                }
                if (resultMap.containsKey("uuid") && functions.isValueNull(resultMap.get("uuid")))
                {
                    uuid = resultMap.get("uuid");
                }
                if (resultMap.containsKey("message") && functions.isValueNull(resultMap.get("message")))
                {
                    message = resultMap.get("message");
                }
                if (resultMap.containsKey("amount") && functions.isValueNull(resultMap.get("amount")))
                {
                    amount = resultMap.get("amount");
                }
                if (resultMap.containsKey("details") && functions.isValueNull(resultMap.get("details")))
                {
                    details = resultMap.get("details");
                }
                if (resultMap.containsKey("version") && functions.isValueNull(resultMap.get("version")))
                {
                    version = resultMap.get("version");
                }
                if (resultMap.containsKey("currency") && functions.isValueNull(resultMap.get("currency")))
                {
                    currency = resultMap.get("currency");
                }


                String rfOperationDateTime          = "";

                if (functions.isValueNull(details))
                {
                    JSONObject detailsJson = new JSONObject(details);
                    if (detailsJson.has("values") && functions.isValueNull(detailsJson.getString("values")))
                    {
                        JSONObject valuesJson = new JSONObject(detailsJson.getString("values"));
                        if (valuesJson.has("rfOperationDateTime") && functions.isValueNull(valuesJson.getString("rfOperationDateTime")))
                        {
                            rfOperationDateTime = valuesJson.getString("rfOperationDateTime");
                        }
                    }
                }


                if (functions.isValueNull(statusResponse))
                {
                    if ("SUCCESS".equalsIgnoreCase(statusResponse))
                    {
                        comm3DResponseVO.setStatus("Success");
                        comm3DResponseVO.setTransactionStatus("Success");
                        comm3DResponseVO.setRemark(message);
                        comm3DResponseVO.setDescription(message);
                        comm3DResponseVO.setAmount(amount);
                        comm3DResponseVO.setTransactionId(payFrexTransactionId);
                    }
                    else if ("declined".equalsIgnoreCase(statusResponse) || "ERROR".equalsIgnoreCase(statusResponse) || "FAIL".equalsIgnoreCase(statusResponse) || "Failed".equalsIgnoreCase(statusResponse) || "ERROR3DS".equalsIgnoreCase(statusResponse))
                    {
                        comm3DResponseVO.setStatus("Failed");
                        comm3DResponseVO.setTransactionStatus("Failed");
                        comm3DResponseVO.setRemark(message);
                        comm3DResponseVO.setDescription(message);
                        comm3DResponseVO.setAmount(amount);
                        comm3DResponseVO.setTransactionId(payFrexTransactionId);
                    }
                    else
                    {
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setTransactionStatus("pending");
                        comm3DResponseVO.setRemark("Transaction is pending");
                        comm3DResponseVO.setDescription("Transaction is pending");
                    }
                    comm3DResponseVO.setMerchantId(merchantId);
                    comm3DResponseVO.setAuthCode(authCode);
                    comm3DResponseVO.setTransactionId(payFrexTransactionId);
                    comm3DResponseVO.setBankTransactionDate(rfOperationDateTime);
                    comm3DResponseVO.setResponseTime(rfOperationDateTime);
                    comm3DResponseVO.setBankCode(code);
                    comm3DResponseVO.setBankDescription(message);
                    comm3DResponseVO.setBankDescription(message);
                    comm3DResponseVO.setAmount(amount);
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setThreeDVersion("3Dv2");
                    EasyPaymentGatewayUtils.updateMainTableEntry(payFrexTransactionId,message,"","",trackingId);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                }
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            }

        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EasyPaymentGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        return comm3DResponseVO;
    }

    public GenericResponseVO processVoid(String trackingId, GenericRequestVO requestVO)throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionlogger.error("inside EasyPayment processVoid() ---------->");

        CommRequestVO commRequestVO                 = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransDetailsVO = commRequestVO.getTransDetailsVO();
        Comm3DResponseVO comm3DResponseVO           = new Comm3DResponseVO();
        Functions functions                         = new Functions();
        GatewayAccount gatewayAccount               = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest              = gatewayAccount.isTest();
        String encryptionKey        = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String merchantId           = gatewayAccount.getMerchantId();
        String apiVersion           = "3";

        String REQUEST_URL            = "";
        String paymentSolution        = "";
        String merchantTransactionId  = "";
        String transactionId          = "";
        String description            = "";
        HashMap<String, String> resultMap;
        String ivString         = "";
        String encryptedString  = "";
        String integrityCheck   = "";
        String queryParameters  = "";
        String voidResponse     = "";

        try
        {
            paymentSolution        = EasyPaymentGatewayUtils.getPaymentSolution(trackingId);
            merchantTransactionId  = trackingId;
            transactionId          = commTransDetailsVO.getPreviousTransactionId();
            description            = trackingId;

            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_VOID_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_VOID_URL");
            }

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("merchantId=" + merchantId);
            stringBuffer.append("&transactionId=" + transactionId);
            stringBuffer.append("&paymentSolution=" + paymentSolution);
            stringBuffer.append("&description=" + description);
            stringBuffer.append("&merchantTransactionId=" + merchantTransactionId);
            stringBuffer.append("&apiVersion=" + apiVersion);

            transactionlogger.error("processVoid() requestParameter :" +trackingId + " " + stringBuffer);

            IvParameterSpec iv = EasyPaymentGatewayUtils.generateIv();
            ivString = Base64.getEncoder().encodeToString(iv.getIV());

            encryptedString = EasyPaymentGatewayUtils.encryptCBC(stringBuffer.toString(), encryptionKey, iv);

            integrityCheck = EasyPaymentGatewayUtils.generateSHA256Hash(stringBuffer.toString());

            queryParameters = REQUEST_URL + "merchantId=" + merchantId + "&encrypted=" + URLEncoder.encode(encryptedString, "UTF-8") + "&integrityCheck=" + integrityCheck;
            transactionlogger.error("processVoid() queryParameters :" +trackingId+ " " + queryParameters);

            voidResponse = EasyPaymentGatewayUtils.doHttpPostRequestConnection(queryParameters, ivString);
            transactionlogger.error("processVoid() response : " + trackingId + " " + voidResponse);

            resultMap = (HashMap) EasyPaymentGatewayUtils.readEasyPayment3DRedirectionXMLReponse(voidResponse);
            transactionlogger.error("processVoid() XML READED response :" + trackingId + " " + resultMap);


            String statusResponse       = "";
            String amount               = "";
            String code                 = "";
            String uuid                 = "";
            String message              = "";
            String authCode             = "";
            String currency             = "";
            String details              = "";
            String payFrexTransactionId = "";

            if (resultMap != null)
            {
                if (resultMap.containsKey("status") && functions.isValueNull(resultMap.get("status")))
                {
                    statusResponse = resultMap.get("status");
                }
                if (resultMap.containsKey("payFrexTransactionId") && functions.isValueNull(resultMap.get("payFrexTransactionId")))
                {
                    payFrexTransactionId = resultMap.get("payFrexTransactionId");
                }
                if (resultMap.containsKey("code") && functions.isValueNull(resultMap.get("code")))
                {
                    code = resultMap.get("code");
                }
                if (resultMap.containsKey("uuid") && functions.isValueNull(resultMap.get("uuid")))
                {
                    uuid = resultMap.get("uuid");
                }
                if (resultMap.containsKey("message") && functions.isValueNull(resultMap.get("message")))
                {
                    message = resultMap.get("message");
                }
                if (resultMap.containsKey("amount") && functions.isValueNull(resultMap.get("amount")))
                {
                    amount = resultMap.get("amount");
                }
                if (resultMap.containsKey("authCode") && functions.isValueNull(resultMap.get("authCode")))
                {
                    authCode = resultMap.get("authCode");
                }
                if (resultMap.containsKey("currency") && functions.isValueNull(resultMap.get("currency")))
                {
                    currency = resultMap.get("currency");
                }
                if (resultMap.containsKey("details") && functions.isValueNull(resultMap.get("details")))
                {
                    details = resultMap.get("details");
                }

                String rfOperationDateTime          = "";
                if (functions.isValueNull(details))
                {
                    JSONObject detailsJson = new JSONObject(details);
                    if (detailsJson.has("values") && functions.isValueNull(detailsJson.getString("values")))
                    {
                        JSONObject valuesJson = new JSONObject(detailsJson.getString("values"));
                        if (valuesJson.has("rfOperationDateTime") && functions.isValueNull(valuesJson.getString("rfOperationDateTime")))
                        {
                            rfOperationDateTime = valuesJson.getString("rfOperationDateTime");
                        }
                    }
                }

                if (functions.isValueNull(statusResponse))
                {
                    if ("SUCCESS".equalsIgnoreCase(statusResponse))
                    {
                        comm3DResponseVO.setStatus("Success");
                        comm3DResponseVO.setTransactionStatus("Success");
                        comm3DResponseVO.setRemark(message);
                        comm3DResponseVO.setDescription(message);
                        comm3DResponseVO.setMerchantId(merchantId);
                        comm3DResponseVO.setAmount(amount);
                        comm3DResponseVO.setTransactionId(payFrexTransactionId);
                    }
                    else if ("declined".equalsIgnoreCase(statusResponse) || "error".equalsIgnoreCase(statusResponse) || "FAIL".equalsIgnoreCase(statusResponse) || "Failed".equalsIgnoreCase(statusResponse) || "ERROR3DS".equalsIgnoreCase(statusResponse))
                    {
                        comm3DResponseVO.setStatus("Failed");
                        comm3DResponseVO.setTransactionStatus("Failed");
                        comm3DResponseVO.setRemark(message);
                        comm3DResponseVO.setDescription(message);
                        comm3DResponseVO.setMerchantId(merchantId);
                        comm3DResponseVO.setAmount(amount);
                        comm3DResponseVO.setTransactionId(payFrexTransactionId);
                    }
                    else
                    {
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setTransactionStatus("pending");
                    }
                    comm3DResponseVO.setMerchantId(merchantId);
                    comm3DResponseVO.setAuthCode(authCode);
                    comm3DResponseVO.setTransactionId(payFrexTransactionId);
                    comm3DResponseVO.setBankTransactionDate(rfOperationDateTime);
                    comm3DResponseVO.setResponseTime(rfOperationDateTime);
                    comm3DResponseVO.setBankCode(code);
                    comm3DResponseVO.setBankDescription(message);
                    comm3DResponseVO.setAmount(amount);
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setThreeDVersion("3Dv2");
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                }
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EasyPaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while cancelling transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    //Subsequence recurring payment
    public GenericResponseVO processRebilling(String trackingId,GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processRebilling of easypay......");

        CommRequestVO commRequestVO         = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO   = new Comm3DResponseVO();
        Functions functions                 = new Functions();

        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest               = gatewayAccount.isTest();
        String merchantId            = gatewayAccount.getMerchantId();
        String productId             = gatewayAccount.getFRAUD_FTP_USERNAME();
        String encryptionKey         = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String REQUEST_URL           = "";
        String HOST_URL              = "";
        String paymentRecurringType  = "cof";
        String merchantExemptionsSca = "LWV;TRA";
        String operationType         = "DEBIT";
        String paymentSolution       = "creditcards";
        String merchantTransactionId = "";
        String amount                = "";
        String description           = "";
        String currency              = "";
        String country               = "";
        String customerId            = "";
        String cardNumberToken       = "";
        String subscriptionPlan      = "";
        HashMap<String,String> hm;

//        INITIAL  = first recurring   // from sale
//        REPEATED = subsequence recurring  // from here

        try
        {
            merchantTransactionId = trackingId;
            customerId            = commTransactionDetailsVO.getPreviousTransactionId();
            amount                = commTransactionDetailsVO.getAmount();
            description           = trackingId;
            currency              = commTransactionDetailsVO.getCurrency();
            country               = commAddressDetailsVO.getCountry();
            hm                    = EasyPaymentGatewayUtils.getSubscriptionPlan_CardNumberToken(commTransactionDetailsVO.getPreviousTransactionId());
            subscriptionPlan      = hm.getOrDefault("subscriptionPlan", "");
            cardNumberToken       = hm.getOrDefault("cardNumberToken", "");


            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_FIRST_RECURRING");
                HOST_URL = RB.getString("TEST_HOST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_FIRST_RECURRING");
                HOST_URL = RB.getString("LIVE_HOST_URL");
            }


            if (functions.isValueNull(country) && country.length() != 2)
            {
                if (EasyPaymentGatewayUtils.getCountryCodeHash().containsKey(country))
                {
                    country = EasyPaymentGatewayUtils.getCountryCodeHash().getOrDefault(country, country).toString();
                }
            }

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("merchantId=" + merchantId);
            stringBuffer.append("&paymentSolution=" + paymentSolution);
            stringBuffer.append("&productId=" + productId);
            stringBuffer.append("&operationType=" + operationType);
            stringBuffer.append("&merchantTransactionId=" + merchantTransactionId);
            stringBuffer.append("&currency=" + currency);
            stringBuffer.append("&amount=" + amount);
            stringBuffer.append("&country=" + country);
            stringBuffer.append("&description=" + description);
            stringBuffer.append("&paymentRecurringType=" + paymentRecurringType);
            stringBuffer.append("&merchantExemptionsSca=" + merchantExemptionsSca);
            stringBuffer.append("&customerId=" + customerId);
            stringBuffer.append("&cardNumberToken=" + cardNumberToken);
            stringBuffer.append("&subscriptionPlan=" + subscriptionPlan);

            StringBuffer stringBufferLog = new StringBuffer();
            stringBufferLog.append("merchantId=" + merchantId);
            stringBufferLog.append("&paymentSolution=" + paymentSolution);
            stringBufferLog.append("&productId=" + productId);
            stringBufferLog.append("&operationType=" + operationType);
            stringBufferLog.append("&merchantTransactionId=" + merchantTransactionId);
            stringBufferLog.append("&currency=" + currency);
            stringBufferLog.append("&amount=" + amount);
            stringBufferLog.append("&country=" + country);
            stringBufferLog.append("&description=" + description);
            stringBufferLog.append("&paymentRecurringType=" + paymentRecurringType);
            stringBufferLog.append("&merchantExemptionsSca=" + merchantExemptionsSca);
            stringBufferLog.append("&customerId=" + customerId);
            stringBufferLog.append("&cardNumberToken=" + cardNumberToken);
            stringBufferLog.append("&subscriptionPlan=" + subscriptionPlan);

            transactionlogger.error("subsequence processRebilling()  requestParameter : " +trackingId + " " + stringBufferLog);

            IvParameterSpec iv = EasyPaymentGatewayUtils.generateIv();
            String ivString = Base64.getEncoder().encodeToString(iv.getIV());

            String encryptedString = EasyPaymentGatewayUtils.encryptCBC(stringBuffer.toString(), encryptionKey, iv);

            String integrityCheck = EasyPaymentGatewayUtils.generateSHA256Hash(stringBuffer.toString());

            String queryParameters = REQUEST_URL + "merchantId=" + merchantId + "&encrypted=" + URLEncoder.encode(encryptedString, "UTF-8") + "&integrityCheck=" + integrityCheck;
            transactionlogger.error("subsequence processRebilling  Request : " +trackingId+ " " + queryParameters);

            String response = EasyPaymentGatewayUtils.doHttpPostRequestConnection(queryParameters, ivString);
            transactionlogger.error("subsequence processRebilling  response : " + trackingId + " " +response);

            HashMap<String,String> resultMap = (HashMap<String, String>) EasyPaymentGatewayUtils.readEasyPayment3DRedirectionXMLReponse(response);
            transactionlogger.error("XML Readed Response : " + trackingId + " " + resultMap);

            String statusResponse           = "";
            String payFrexTransactionId     = "";
            String code                     = "";
            String uuid                     = "";
            String authCode                 = "";
            String message                  = "";
            String redirectionResponse      = "";
            String Rcurrency                = "";
            String RcardNumberToken         = "";
            String RsubscriptionPlan        = "";


            if (resultMap != null)
            {
                if (resultMap.containsKey("status") && functions.isValueNull(resultMap.get("status")))
                {
                    statusResponse = resultMap.get("status");
                }
                if (resultMap.containsKey("payFrexTransactionId") && functions.isValueNull(resultMap.get("payFrexTransactionId")))
                {
                    payFrexTransactionId = resultMap.get("payFrexTransactionId");
                }
                if (resultMap.containsKey("code") && functions.isValueNull(resultMap.get("code")))
                {
                    code = resultMap.get("code");
                }
                if (resultMap.containsKey("uuid") && functions.isValueNull(resultMap.get("uuid")))
                {
                    uuid = resultMap.get("uuid");
                }
                if (resultMap.containsKey("message") && functions.isValueNull(resultMap.get("message")))
                {
                    message = resultMap.get("message");
                }
                if (resultMap.containsKey("amount") && functions.isValueNull(resultMap.get("amount")))
                {
                    amount = resultMap.get("amount");
                }
                if (resultMap.containsKey("authCode") && functions.isValueNull(resultMap.get("authCode")))
                {
                    authCode = resultMap.get("authCode");
                }

                if (resultMap.containsKey("currency") && functions.isValueNull(resultMap.get("currency")))
                {
                    Rcurrency = resultMap.get("currency");
                }

                if (resultMap.containsKey("cardNumberToken") && functions.isValueNull(resultMap.get("cardNumberToken")))
                {
                    RcardNumberToken = resultMap.get("cardNumberToken");
                }
                if (resultMap.containsKey("subscriptionPlan") && functions.isValueNull(resultMap.get("subscriptionPlan")))
                {
                    RsubscriptionPlan = resultMap.get("subscriptionPlan");
                }

                /*if (resultMap.containsKey("redirectionResponse") && functions.isValueNull(resultMap.get("redirectionResponse")))
                {
                    redirectionResponse = resultMap.get("redirectionResponse");
                    if (functions.isValueNull(redirectionResponse) && redirectionResponse.contains("redirect:"))
                    {
                        if (redirectionResponse.split(":").length > 0)
                        {
                            redirectionResponse = redirectionResponse.split(":")[1];
                        }
                        else
                        {
                            redirectionResponse = "";
                        }
                    }
                }

                if ("REDIRECTED".equalsIgnoreCase(statusResponse) && functions.isValueNull(redirectionResponse))
                {
                    comm3DResponseVO.setUrlFor3DRedirect(HOST_URL + redirectionResponse);
                    comm3DResponseVO.setStatus("pending3DConfirmation");
                    comm3DResponseVO.setDescription(message);

                }
                else*/
                if ("SUCCESS".equalsIgnoreCase(statusResponse))
                {
                    comm3DResponseVO.setStatus("Success");
                    comm3DResponseVO.setTransactionStatus("Success");
                    comm3DResponseVO.setTransactionId(payFrexTransactionId);
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                }
                else if ("declined".equalsIgnoreCase(statusResponse) || "error".equalsIgnoreCase(statusResponse) || "FAIL".equalsIgnoreCase(statusResponse) || "ERROR3DS".equalsIgnoreCase(statusResponse))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    comm3DResponseVO.setTransactionId(payFrexTransactionId);
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }

                comm3DResponseVO.setMerchantId(merchantId);
                comm3DResponseVO.setTransactionId(payFrexTransactionId);
                comm3DResponseVO.setErrorCode(code);
                comm3DResponseVO.setAuthCode(authCode);
                comm3DResponseVO.setCurrency(Rcurrency);
                comm3DResponseVO.setThreeDVersion("3Dv2");
                EasyPaymentGatewayUtils.updateMainTableEntry(payFrexTransactionId,message,cardNumberToken,RsubscriptionPlan,trackingId);
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EasyPaymentGateway.class.getName(), "processRecurring()", null, "common", "Technical Exception while cancelling transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("inside EasyPayment processPayout() --------------->");
        CommRequestVO commRequestVO                 = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO   = commRequestVO.getAddressDetailsVO();
        Comm3DResponseVO comm3DResponseVO           = new Comm3DResponseVO();
        Functions functions                         = new Functions();
        GatewayAccount gatewayAccount               = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest              = gatewayAccount.isTest();
        String encryptionKey        = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String productId            = gatewayAccount.getFRAUD_FTP_USERNAME();
        String merchantId           = gatewayAccount.getMerchantId();
        String operationType        = "CREDIT";
        String paymentSolution      = "creditcards";

        String merchantTransactionId  = trackingID;
        String description            = trackingID;
        String REQUEST_URL            = "";
        String amount                 = "";
        String currency               = "";
        String country                = "";
        String customerId             = "";
        String cardNumberToken        = "";

        String chAddress1   = "";
        String chAddress2   = "";
        String chCity       = "";
        String chPostCode   = "";
        String chCountry    = "";
        String chEmail      = "";
        String chFirstName  = "";
        String chLastName   = "";
        String chPhone      = "";
        String chState      = "";
        HashMap<String, String> resultMap;
        String ivString         = "";
        String encryptedString  = "";
        String integrityCheck   = "";
        String queryParameters  = "";
        String payoutResponse   = "";


        try
        {
            amount           = commTransDetailsVO.getAmount();
            currency         = commTransDetailsVO.getCurrency();
            country          = commAddressDetailsVO.getCountry();
            customerId       = commTransDetailsVO.getPreviousTransactionId();
            cardNumberToken  = commTransDetailsVO.getAuthorization_code();

            chAddress1  = commAddressDetailsVO.getStreet() + "," + commAddressDetailsVO.getCity();
            chAddress2  = commAddressDetailsVO.getState();
            chCity      = commAddressDetailsVO.getCity();
            chPostCode  = commAddressDetailsVO.getZipCode();
            chCountry   = commAddressDetailsVO.getCountry();
            chEmail     = commAddressDetailsVO.getEmail();
            chFirstName = commAddressDetailsVO.getFirstname();
            chLastName  = commAddressDetailsVO.getLastname();
            chPhone     = commAddressDetailsVO.getPhone();
            chState     = commAddressDetailsVO.getState();

            if (isTest)
            {
                REQUEST_URL = RB.getString("PAYOUT_TEST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("PAYOUT_LIVE_URL");
            }

            if (functions.isValueNull(country) && country.length() != 2)
            {
                if (EasyPaymentGatewayUtils.getCountryCodeHash().containsKey(country))
                {
                    country = EasyPaymentGatewayUtils.getCountryCodeHash().getOrDefault(country, "ES").toString();
                }
            }


            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("merchantId="+merchantId);
            stringBuffer.append("&productId="+productId);
            stringBuffer.append("&paymentSolution="+paymentSolution);
            stringBuffer.append("&operationType="+operationType);
            stringBuffer.append("&amount="+amount);
            stringBuffer.append("&currency="+currency);
            stringBuffer.append("&country="+country);
            stringBuffer.append("&description="+description);
            stringBuffer.append("&merchantTransactionId="+merchantTransactionId);
            stringBuffer.append("&customerId="+customerId);
            stringBuffer.append("&cardNumberToken=" + cardNumberToken);

//            stringBuffer.append("&chAddress1="+chAddress1);
//            stringBuffer.append("&chAddress2="+chAddress2);
//            stringBuffer.append("&chCity="+chCity);
//            stringBuffer.append("&chPostCode="+chPostCode);
//            stringBuffer.append("&chCountry="+chCountry);
//            stringBuffer.append("&chEmail="+chEmail);
//            stringBuffer.append("&chFirstName="+chFirstName);
//            stringBuffer.append("&chLastName="+chLastName);
//            stringBuffer.append("&chPhone="+chPhone);
//            stringBuffer.append("&chState="+chState);

            StringBuffer stringBufferLOGS = new StringBuffer();
            stringBufferLOGS.append("merchantId="+merchantId);
            stringBufferLOGS.append("&productId="+productId);
            stringBufferLOGS.append("&paymentSolution="+paymentSolution);
            stringBufferLOGS.append("&operationType="+operationType);
            stringBufferLOGS.append("&amount="+amount);
            stringBufferLOGS.append("&currency="+currency);
            stringBufferLOGS.append("&country="+country);
            stringBufferLOGS.append("&description="+description);
            stringBufferLOGS.append("&merchantTransactionId="+merchantTransactionId);
            stringBufferLOGS.append("&customerId="+customerId);
            stringBufferLOGS.append("&cardNumberToken=" + cardNumberToken);

//            stringBufferLOGS.append("&chAddress1="+chAddress1);
//            stringBufferLOGS.append("&chAddress2="+chAddress2);
//            stringBufferLOGS.append("&chCity="+chCity);
//            stringBufferLOGS.append("&chPostCode="+chPostCode);
//            stringBufferLOGS.append("&chCountry="+chCountry);
//            stringBufferLOGS.append("&chEmail="+functions.getEmailMasking(chEmail));
//            stringBufferLOGS.append("&chFirstName="+functions.maskingFirstName(chFirstName));
//            stringBufferLOGS.append("&chLastName="+functions.maskingLastName(chLastName));
//            stringBufferLOGS.append("&chPhone="+functions.getPhoneNumMasking(chPhone));
//            stringBufferLOGS.append("&chState="+chState);

            transactionlogger.error("processPayout() requestParameter : " +trackingID +" "+ stringBufferLOGS);

            IvParameterSpec iv = EasyPaymentGatewayUtils.generateIv();
            ivString = Base64.getEncoder().encodeToString(iv.getIV());

            encryptedString = EasyPaymentGatewayUtils.encryptCBC(stringBuffer.toString(), encryptionKey, iv);

            integrityCheck = EasyPaymentGatewayUtils.generateSHA256Hash(stringBuffer.toString());

            queryParameters = REQUEST_URL + "merchantId=" + merchantId + "&encrypted=" + URLEncoder.encode(encryptedString, "UTF-8") + "&integrityCheck=" + integrityCheck;
            transactionlogger.error("processPayout() queryParameters : "+trackingID +" "+ queryParameters);

            payoutResponse = EasyPaymentGatewayUtils.doHttpPostRequestConnection(queryParameters, ivString);
            transactionlogger.error("processPayout() Response :" + trackingID + " "+ payoutResponse);

            resultMap = (HashMap) EasyPaymentGatewayUtils.readEasyPayment3DRedirectionXMLReponse(payoutResponse);
            transactionlogger.error("processPayout() XML READED response :" + trackingID + " " + resultMap);


            String statusResponse       = "";
            String amount1              = "";
            String code                 = "";
            String uuid                 = "";
            String message              = "";
            String details              = "";
            String currency1             = "";
            String authCode             = "";
            String payFrexTransactionId = "";

            if (resultMap != null )
            {
                if (resultMap.containsKey("status") && functions.isValueNull(resultMap.get("status")))
                {
                    statusResponse = resultMap.get("status");
                }
                if (resultMap.containsKey("payFrexTransactionId") && functions.isValueNull(resultMap.get("payFrexTransactionId")))
                {
                    payFrexTransactionId = resultMap.get("payFrexTransactionId");
                }
                if (resultMap.containsKey("code") && functions.isValueNull(resultMap.get("code")))
                {
                    code = resultMap.get("code");
                }
                if (resultMap.containsKey("uuid") && functions.isValueNull(resultMap.get("uuid")))
                {
                    uuid = resultMap.get("uuid");
                }
                if (resultMap.containsKey("message") && functions.isValueNull(resultMap.get("message")))
                {
                    message = resultMap.get("message");
                }
                if (resultMap.containsKey("amount") && functions.isValueNull(resultMap.get("amount")))
                {
                    amount1 = resultMap.get("amount");
                }
                if (resultMap.containsKey("currency") && functions.isValueNull(resultMap.get("currency")))
                {
                    currency1 = resultMap.get("currency");
                }
                if (resultMap.containsKey("authCode") && functions.isValueNull(resultMap.get("authCode")))
                {
                    authCode = resultMap.get("authCode");
                }
                if (resultMap.containsKey("details") && functions.isValueNull(resultMap.get("details")))
                {
                    details = resultMap.get("details");
                }


                String rfOperationDateTime = "";
                if (functions.isValueNull(details))
                {
                    JSONObject detailsJson = new JSONObject(details);
                    if (detailsJson.has("values") && functions.isValueNull(detailsJson.getString("values")))
                    {
                        JSONObject valuesJson = new JSONObject(detailsJson.getString("values"));
                        if (valuesJson.has("rfOperationDateTime") && functions.isValueNull(valuesJson.getString("rfOperationDateTime")))
                        {
                            rfOperationDateTime = valuesJson.getString("rfOperationDateTime");
                        }
                    }
                }


                if ("SUCCESS".equalsIgnoreCase(statusResponse))
                {
                    comm3DResponseVO.setStatus("Success");
                    comm3DResponseVO.setTransactionStatus("Success");
                    comm3DResponseVO.setTransactionId(payFrexTransactionId);
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                    comm3DResponseVO.setAmount(amount1);
                }
                else if ("declined".equalsIgnoreCase(statusResponse) || "ERROR".equalsIgnoreCase(statusResponse) || "FAIL".equalsIgnoreCase(statusResponse) || "Failed".equalsIgnoreCase(statusResponse) || "ERROR3DS".equalsIgnoreCase(statusResponse))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    comm3DResponseVO.setTransactionId(payFrexTransactionId);
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                    comm3DResponseVO.setAmount(amount1);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                }

                comm3DResponseVO.setMerchantId(merchantId);
                comm3DResponseVO.setAuthCode(authCode);
                comm3DResponseVO.setBankTransactionDate(rfOperationDateTime);
                comm3DResponseVO.setResponseTime(rfOperationDateTime);
                comm3DResponseVO.setBankCode(code);
                comm3DResponseVO.setBankDescription(message);
                comm3DResponseVO.setErrorCode(code);
                comm3DResponseVO.setTransactionId(payFrexTransactionId);
                comm3DResponseVO.setAmount(amount1);
                comm3DResponseVO.setCurrency(currency1);
                comm3DResponseVO.setThreeDVersion("3Dv2");
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EasyPaymentGateway.class.getName(), "processPayout()", null, "common", "Technical Exception while cancelling transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

}
