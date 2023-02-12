package com.payment.safechargeV2;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.PzEncryptor;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by Admin on 9/7/2020.
 */
public class SafeChargeV2PaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger = new TransactionLogger(SafeChargeV2PaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "SafeChargeV2";
    ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.safechargeV2");
    public SafeChargeV2PaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----Entered in processSale()-----");
        Functions functions=new Functions();
        Comm3DResponseVO comm3DResponseVO=new Comm3DResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transDetailsVO =  commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        CommDeviceDetailsVO deviceDetailsVO=commRequestVO.getCommDeviceDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        String sessionTokenUrl="";
        String saleUrl="";
        String initPaymentUrl="";
        String mid=gatewayAccount.getMerchantId();
        String merchantSiteId=gatewayAccount.getFRAUD_FTP_USERNAME();
        String merchantSecretKey=gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest=gatewayAccount.isTest();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("YYYYMMddHHmmss");
        String timestamp=simpleDateFormat.format(new Date());
        String sessionToken="";
        String errCode="";
        String sessionTokenStatus="";
        String firstname="";
        String lastname="";
        String street="";
        String city="";
        String country="";
        String state="";
        String zip="";
        String termUrl="";
        String checksum="";
        String userAgent="";
        String language="";
        String colorDepth="";
        String timezoneOffset="";
        String height="";
        String width="";
        String transactionStatus="";
        String gwErrorCode="";
        String gwErrorReason="";
        String authCode="";
        String eci="";
        String acsUrl="";
        String cReq="";
        String paRequest="";
        String transactionId="";
        String notifyUrl="";
        String initTransactionStatus="";
        String methodUrl="";
        String methodPayload="";
        String serverTransId="";
        String reason="";
        String encryptedCVV="";
        String version="";
        String cardHolderName="";
        String ipAddress="";
        String userPaymentOptionId="";
        String v2supported="";
        String javaEnabled="";
        String orderid="";
        if(functions.isValueNull(addressDetailsVO.getFirstname()))
            firstname=new String(addressDetailsVO.getFirstname().getBytes(), StandardCharsets.UTF_8);
        if(functions.isValueNull(addressDetailsVO.getLastname()))
            lastname=new String(addressDetailsVO.getLastname().getBytes(), StandardCharsets.UTF_8);
        if(functions.isValueNull(addressDetailsVO.getStreet()))
            street=addressDetailsVO.getStreet();
        if(functions.isValueNull(addressDetailsVO.getCity()))
            city=addressDetailsVO.getStreet();
        if(functions.isValueNull(addressDetailsVO.getCountry()))
            country=addressDetailsVO.getCountry();
        if(functions.isValueNull(addressDetailsVO.getState()))
            state=addressDetailsVO.getState();
        if(functions.isValueNull(addressDetailsVO.getZipCode()))
            zip=addressDetailsVO.getZipCode();

        cardHolderName=firstname+" "+lastname;
        if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress()))
            ipAddress=addressDetailsVO.getCardHolderIpAddress();
        else
            ipAddress=addressDetailsVO.getIp();
        if(functions.isValueNull(deviceDetailsVO.getFingerprints()))
        {
            HashMap fingerPrintMap = functions.getFingerPrintMap(deviceDetailsVO.getFingerprints());
            JSONArray screenResolution = new JSONArray();
            try
            {
                if (fingerPrintMap.containsKey("userAgent"))
                    userAgent = (String) fingerPrintMap.get("userAgent");
                if (fingerPrintMap.containsKey("language"))
                    language = (String) fingerPrintMap.get("language");
                if (fingerPrintMap.containsKey("colorDepth"))
                    colorDepth = String.valueOf(fingerPrintMap.get("colorDepth"));
                if (fingerPrintMap.containsKey("timezoneOffset"))
                    timezoneOffset = String.valueOf(fingerPrintMap.get("timezoneOffset"));
                if (fingerPrintMap.containsKey("screenResolution"))
                    screenResolution = (JSONArray) fingerPrintMap.get("screenResolution");
                if (screenResolution.length() > 0)
                    height = String.valueOf(screenResolution.getString(0));
                if (screenResolution.length() > 1)
                    width = String.valueOf(screenResolution.getString(1));
                javaEnabled="TRUE";
            }
            catch (JSONException e)
            {
                transactionLogger.error("FIngerPrint JSONException---trackingId--" + trackingID + "--", e);
            }
        }else
        {
            if(functions.isValueNull(deviceDetailsVO.getUser_Agent()))
                userAgent=deviceDetailsVO.getUser_Agent();
            else
                userAgent="Mozilla/5.0 (Android; Mobile; rv:13.0) Gecko/13.0 Firefox/13.0";
            if(functions.isValueNull(deviceDetailsVO.getBrowserLanguage()))
                language=deviceDetailsVO.getBrowserLanguage();
            else
                language="en-US";
            if(functions.isValueNull(deviceDetailsVO.getBrowserColorDepth()))
                colorDepth=deviceDetailsVO.getBrowserColorDepth();
            else
                colorDepth="24";
            if(functions.isValueNull(deviceDetailsVO.getBrowserTimezoneOffset()))
                timezoneOffset=deviceDetailsVO.getBrowserTimezoneOffset();
            else
                timezoneOffset="5";
            if(functions.isValueNull(deviceDetailsVO.getBrowserScreenHeight()))
                height=deviceDetailsVO.getBrowserScreenHeight();
            else
                height="939";
            if(functions.isValueNull(deviceDetailsVO.getBrowserScreenWidth()))
                width=deviceDetailsVO.getBrowserScreenWidth();
            else
                width="1255";
            if(functions.isValueNull(deviceDetailsVO.getBrowserJavaEnabled()))
                javaEnabled=deviceDetailsVO.getBrowserJavaEnabled();
            else
                javaEnabled="TRUE";
        }
        try
        {
            if (isTest)
            {
                sessionTokenUrl = RB.getString("TEST_SESSION_TOKEN_URL");
                saleUrl = RB.getString("TEST_SALE_URL");
                initPaymentUrl = RB.getString("TEST_INIT_PAYMENT_URL");

                if("CL".equalsIgnoreCase(firstname) && "BRW".equalsIgnoreCase(lastname))
                {
                    if("4000027891380961".equalsIgnoreCase(cardDetailsVO.getCardNum()))
                        cardHolderName=firstname+"-"+lastname+"1";
                    else
                        cardHolderName=firstname+"-"+lastname+"2";
                }else if("FL".equalsIgnoreCase(firstname) && "BRW".equalsIgnoreCase(lastname))
                {
                    cardHolderName=firstname+"-"+lastname+"1";
                }else if("ERR".equalsIgnoreCase(firstname) && "BRW".equalsIgnoreCase(lastname))
                {
                    cardHolderName=firstname+"-"+lastname+"1";
                }
            }
            else
            {
                sessionTokenUrl = RB.getString("LIVE_SESSION_TOKEN_URL");
                saleUrl = RB.getString("LIVE_SALE_URL");
                initPaymentUrl = RB.getString("LIVE_INIT_PAYMENT_URL");
            }
            if (functions.isValueNull(cardDetailsVO.getcVV()))
            {
                encryptedCVV = PzEncryptor.encryptCVV(cardDetailsVO.getcVV());
            }
            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL")+trackingID+"&MD="+ URLEncoder.encode(encryptedCVV);
                transactionLogger.error("From HOST_URL----" + termUrl);
            }
            else
            {
            termUrl = RB.getString("TERM_URL")+trackingID+"&MD="+URLEncoder.encode(encryptedCVV);
            transactionLogger.error("From RB TERM_URL ----" + termUrl);
            }
            notifyUrl=RB.getString("NOTIFY_URL");
            checksum=SafeChargeV2Utils.generateChecksum(mid+merchantSiteId+trackingID+timestamp+merchantSecretKey);
            String sessionTokenRequest = "{" +
                    "\"merchantId\": \"" + mid + "\"," +
                    "\"merchantSiteId\": \"" + merchantSiteId + "\"," +
                    "\"clientRequestId\": \"" + trackingID + "\"," +
                    "\"timeStamp\": \"" + timestamp + "\"," +
                    "\"checksum\": \"" + checksum + "\"" +
                    "}";
            transactionLogger.error("sessionTokenRequest---"+trackingID+"-->"+sessionTokenRequest);
            String sessionTokenResponse = SafeChargeV2Utils.doPostHTTPSURLConnectionClient(sessionTokenUrl, sessionTokenRequest);
            transactionLogger.error("sessionTokenResponse---"+trackingID+"-->"+sessionTokenResponse);
            if (functions.isValueNull(sessionTokenResponse))
            {
                JSONObject sessionTokenResJSON = new JSONObject(sessionTokenResponse);
                if(sessionTokenResJSON.has("sessionToken"))
                    sessionToken=sessionTokenResJSON.getString("sessionToken");
                if(sessionTokenResJSON.has("status"))
                    sessionTokenStatus=sessionTokenResJSON.getString("status");
                if(sessionTokenResJSON.has("errCode"))
                    errCode=sessionTokenResJSON.getString("errCode");
                if(sessionTokenResJSON.has("reason"))
                    reason=sessionTokenResJSON.getString("reason");
            }
            boolean isInserted=SafeChargeV2Utils.insertSessionToken(trackingID,sessionToken,"SALE");
            if("SUCCESS".equalsIgnoreCase(sessionTokenStatus))
            {
                timestamp=simpleDateFormat.format(new Date());
                checksum=SafeChargeV2Utils.generateChecksum(mid+merchantSiteId+trackingID+transDetailsVO.getAmount()+transDetailsVO.getCurrency()+timestamp+merchantSecretKey);
                String initRequest="{" +
                        "\"sessionToken\": \""+sessionToken+"\"," +
                        "\"merchantId\": \""+mid+"\"," +
                        "\"merchantSiteId\": \""+merchantSiteId+"\"," +
                        "\"orderId\": \""+trackingID+"\"," +
                        "\"clientUniqueId\": \""+trackingID+"\"," +
                        "\"userTokenId\": \""+trackingID+"\"," +
                        "\"amount\": \""+transDetailsVO.getAmount()+"\"," +
                        "\"currency\": \""+transDetailsVO.getCurrency()+"\"," +
                        "\"paymentOption\": {" +
                        "\"card\": {" +
                        "\"cardNumber\": \""+cardDetailsVO.getCardNum()+"\"," +
                        "\"cardHolderName\": \""+cardHolderName+"\"," +
                        "\"expirationMonth\": \""+cardDetailsVO.getExpMonth()+"\"," +
                        "\"expirationYear\": \""+cardDetailsVO.getExpYear()+"\"," +
                        "\"CVV\": \""+cardDetailsVO.getcVV()+"\"," +
                        "\"threeD\": {" +
                        "\"methodNotificationUrl\": \""+termUrl+"\"" +
                        "}" +
                        "}" +
                        "}," +
                        "\"billingAddress\": {" +
                        "\"city\": \""+city+"\"," +
                        "\"country\": \""+country+"\"," +
                        "\"address\": \""+street+"\", " +
                        "\"zip\": \""+zip+"\"," +
                        "\"state\": \""+state+"\"," +
                        "\"email\": \""+addressDetailsVO.getEmail()+"\"," +
                        "\"phone\": \""+addressDetailsVO.getPhone()+"\"," +
                        "\"firstName\": \""+firstname+"\"," +
                        "\"lastName\": \""+lastname+"\"" +
                        "}," +
                        "\"deviceDetails\": {" +
                        "\"ipAddress\": \""+ipAddress+"\"" +
                        "}," +
                        "\"timeStamp\": \""+timestamp+"\"," +
                        "\"checksum\": \""+checksum+"\"" +
                        "}";
                String initRequestLog="{" +
                        "\"sessionToken\": \""+sessionToken+"\"," +
                        "\"merchantId\": \""+mid+"\"," +
                        "\"merchantSiteId\": \""+merchantSiteId+"\"," +
                        "\"orderId\": \""+trackingID+"\"," +
                        "\"clientUniqueId\": \""+trackingID+"\"," +
                        "\"userTokenId\": \""+trackingID+"\"," +
                        "\"amount\": \""+transDetailsVO.getAmount()+"\"," +
                        "\"currency\": \""+transDetailsVO.getCurrency()+"\"," +
                        "\"paymentOption\": {" +
                        "\"card\": {" +
                        "\"cardNumber\": \""+functions.maskingPan(cardDetailsVO.getCardNum())+"\"," +
                        "\"cardHolderName\": \""+cardHolderName+"\"," +
                        "\"expirationMonth\": \""+functions.maskingNumber(cardDetailsVO.getExpMonth())+"\"," +
                        "\"expirationYear\": \""+functions.maskingNumber(cardDetailsVO.getExpYear())+"\"," +
                        "\"CVV\": \""+functions.maskingNumber(cardDetailsVO.getcVV())+"\"," +
                        "\"threeD\": {" +
                        "\"methodNotificationUrl\": \""+termUrl+"\"" +
                        "}" +
                        "}" +
                        "}," +
                        "\"billingAddress\": {" +
                        "\"city\": \""+city+"\"," +
                        "\"country\": \""+country+"\"," +
                        "\"address\": \""+street+"\", " +
                        "\"zip\": \""+zip+"\"," +
                        "\"state\": \""+state+"\"," +
                        "\"email\": \""+addressDetailsVO.getEmail()+"\"," +
                        "\"phone\": \""+addressDetailsVO.getPhone()+"\"," +
                        "\"firstName\": \""+firstname+"\"," +
                        "\"lastName\": \""+lastname+"\"" +
                        "}," +
                        "\"deviceDetails\": {" +
                        "\"ipAddress\": \""+ipAddress+"\"" +
                        "}," +
                        "\"timeStamp\": \""+timestamp+"\"," +
                        "\"checksum\": \""+checksum+"\"" +
                        "}";
                transactionLogger.error("initRequest---"+trackingID+"-->"+initRequestLog);
                String initResponse=SafeChargeV2Utils.doPostHTTPSURLConnectionClient(initPaymentUrl,initRequest);
                transactionLogger.error("initResponse---"+trackingID+"-->"+initResponse);
                if(functions.isValueNull(initResponse))
                {
                    JSONObject initResponseJSON = new JSONObject(initResponse);
                    if (initResponseJSON.has("transactionStatus"))
                        initTransactionStatus = initResponseJSON.getString("transactionStatus");
                    if (initResponseJSON.has("transactionId"))
                        transactionId = initResponseJSON.getString("transactionId");
                    if (initResponseJSON.has("reason"))
                        reason = initResponseJSON.getString("reason");
                    if (initResponseJSON.has("orderId"))
                        orderid = initResponseJSON.getString("orderId");
                    if (initResponseJSON.has("paymentOption") && initResponseJSON.getJSONObject("paymentOption") != null && initResponseJSON.getJSONObject("paymentOption").has("card"))
                    {
                        JSONObject cardJSON=initResponseJSON.getJSONObject("paymentOption").getJSONObject("card");
                        if(cardJSON != null && cardJSON.has("threeD"))
                        {
                            JSONObject threeDJSON = cardJSON.getJSONObject("threeD");
                            if (threeDJSON.has("methodUrl"))
                                methodUrl = threeDJSON.getString("methodUrl");
                            if (threeDJSON.has("v2supported"))
                                v2supported = threeDJSON.getString("v2supported");
                            if (threeDJSON.has("methodPayload"))
                                methodPayload = threeDJSON.getString("methodPayload");
                            if (threeDJSON.has("serverTransId"))
                                serverTransId = threeDJSON.getString("serverTransId");
                            if (threeDJSON.has("version"))
                                version = threeDJSON.getString("version");
                        }
                    }
                    if("true".equalsIgnoreCase(v2supported))
                    {
                        boolean isUpdated=SafeChargeV2Utils.updateDetailTable(trackingID,orderid,authCode);
                    if(functions.isValueNull(commMerchantVO.getHostUrl()))
                        acsUrl="https://"+commMerchantVO.getHostUrl()+RB.getString("ACS_HOST_URL")+trackingID;
                    else
                        acsUrl=RB.getString("ACS_TERM_URL")+trackingID;
                        if(functions.isValueNull(cardDetailsVO.getcVV()))
                        {
                            encryptedCVV=PzEncryptor.encryptCVV(cardDetailsVO.getcVV());
                        }

                        comm3DResponseVO.setUrlFor3DRedirect(acsUrl);
                        comm3DResponseVO.setTransactionId(transactionId);
                        comm3DResponseVO.setMd(encryptedCVV);
                        comm3DResponseVO.setTerURL(termUrl);
                        comm3DResponseVO.setStatus("pending3DConfirmation");
                        comm3DResponseVO.setThreeDVersion("3Dv2");
                        return comm3DResponseVO;
                    }
                }
                else {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setRemark("No initiate response found");
                    comm3DResponseVO.setDescription("No initiate response found");
                    return comm3DResponseVO;
                }
                if("APPROVED".equalsIgnoreCase(initTransactionStatus)){
                    timestamp = simpleDateFormat.format(new Date());
                    checksum=SafeChargeV2Utils.generateChecksum(mid+merchantSiteId+trackingID+transDetailsVO.getAmount()+transDetailsVO.getCurrency()+timestamp+merchantSecretKey);
                    StringBuffer saleRequest = new StringBuffer("{");
                    saleRequest.append("\"timeStamp\": \"" + timestamp + "\"," +
                            "\"sessionToken\": \"" + sessionToken + "\"," +
                            "\"merchantId\": \"" + mid + "\"," +
                            "\"merchantSiteId\": \"" + merchantSiteId + "\"," +
                            "\"clientRequestId\": \"" + trackingID + "\"," +
                            "\"clientUniqueId\": \"" + trackingID + "\"," +
                            "\"userTokenId\": \"" + trackingID + "\"," +
                            "\"amount\": \"" + transDetailsVO.getAmount() + "\"," +
                            "\"currency\": \"" + transDetailsVO.getCurrency() + "\"," +
                            "\"transactionType\": \"Sale\"," +
                            "\"relatedTransactionId\": \""+transactionId+"\"," +
                            "\"paymentOption\": {" +
                            "\"card\": {" +
                            "\"cardNumber\": \"" + cardDetailsVO.getCardNum() + "\"," +
                            "\"cardHolderName\": \"" + cardHolderName + "\"," +
                            "\"expirationMonth\": \"" + cardDetailsVO.getExpMonth() + "\"," +
                            "\"expirationYear\": \"" + cardDetailsVO.getExpYear() + "\"," +
                            "\"CVV\": \"" + cardDetailsVO.getcVV() + "\"," +
                            "\"threeD\": {" +
                            "\"browserDetails\": {" +
                            "\"acceptHeader\": \""+deviceDetailsVO.getAcceptHeader()+"\"," +
                            "\"ip\": \"" + ipAddress + "\"," +
                            "\"javaEnabled\": \""+javaEnabled+"\"," +
                            "\"javaScriptEnabled\": \""+javaEnabled+"\"," +
                            "\"language\": \"" + language + "\"," +
                            "\"colorDepth\": \"" + colorDepth + "\"," +
                            "\"screenHeight\": \"" + height + "\"," +
                            "\"screenWidth\": \"" + width + "\"," +
                            "\"timeZone\": \"" + timezoneOffset + "\"," +
                            "\"userAgent\": \"" + userAgent + "\"" +
                            "}," +
                            "\"version\": \""+version+"\"," +
                            "\"notificationURL\": \"" + termUrl + "\"," +
                            "\"platformType\": \"02\"," +
                            "\"methodCompletionInd\": \"Y\"" +
                            /*"\"merchantURL\": \"https://staging.paymentz.com\"" +*/
                            "}" +
                            "}" +
                            "}," +
                            "\"deviceDetails\": {" +
                            "\"ipAddress\": \"" + ipAddress + "\"" +
                            "}," +
                            "\"userDetails\": {" +
                            "\"firstName\": \"" + firstname + "\"," +
                            "\"lastName\": \"" + lastname + "\"," +
                            "\"email\": \"" + addressDetailsVO.getEmail() + "\"," +
                            "\"phone\": \"" + addressDetailsVO.getPhone() + "\"" +
                            "}," +
                            "\"shippingAddress\": {" +
                            "\"address\": \"" + street + "\"," +
                            "\"city\": \"" + city + "\"," +
                            "\"country\": \"" + country + "\"," +
                            "\"state\": \"" + state + "\"," +
                            "\"zip\": \"" + zip + "\"" +
                            "}," +
                            "\"billingAddress\": {" +
                            "\"email\": \"" + addressDetailsVO.getEmail() + "\"," +
                            "\"firstName\": \"" + firstname + "\"," +
                            "\"lastName \": \"" + lastname + "\"," +
                            "\"address\": \"" + street + "\"," +
                            "\"city\": \"" + city + "\"," +
                            "\"country\": \"" + country + "\"," +
                            "\"state\": \"" + state + "\"," +
                            "\"zip\": \"" + zip + "\"" +
                            "}," +
                            "\"urlDetails\": {" +
                            "\"notificationUrl\": \"" + notifyUrl + "\"" +
                            "}," +
                            "\"checksum\":\""+checksum+"\""+
                            "}");
                    StringBuffer saleRequestLog = new StringBuffer("{");
                    saleRequestLog.append("\"timeStamp\": \"" + timestamp + "\"," +
                            "\"sessionToken\": \"" + sessionToken + "\"," +
                            "\"merchantId\": \"" + mid + "\"," +
                            "\"merchantSiteId\": \"" + merchantSiteId + "\"," +
                            "\"clientRequestId\": \"" + trackingID + "\"," +
                            "\"clientUniqueId\": \"" + trackingID + "\"," +
                            "\"userTokenId\": \"" + trackingID + "\"," +
                            "\"amount\": \"" + transDetailsVO.getAmount() + "\"," +
                            "\"currency\": \"" + transDetailsVO.getCurrency() + "\"," +
                            "\"transactionType\": \"Sale\"," +
                            "\"relatedTransactionId\": \""+transactionId+"\"," +
                            "\"paymentOption\": {" +
                            "\"card\": {" +
                            "\"cardNumber\": \"" + functions.maskingPan(cardDetailsVO.getCardNum()) + "\"," +
                            "\"cardHolderName\": \"" + cardHolderName + "\"," +
                            "\"expirationMonth\": \"" + functions.maskingNumber(cardDetailsVO.getExpMonth()) + "\"," +
                            "\"expirationYear\": \"" + functions.maskingNumber(cardDetailsVO.getExpYear()) + "\"," +
                            "\"CVV\": \"" + functions.maskingNumber(cardDetailsVO.getcVV()) + "\"," +
                            "\"threeD\": {" +
                            "\"browserDetails\": {" +
                            "\"acceptHeader\": \""+deviceDetailsVO.getAcceptHeader()+"\"," +
                            "\"ip\": \"" + ipAddress + "\"," +
                            "\"javaEnabled\": \""+javaEnabled+"\"," +
                            "\"javaScriptEnabled\": \""+javaEnabled+"\"," +
                            "\"language\": \"" + language + "\"," +
                            "\"colorDepth\": \"" + colorDepth + "\"," +
                            "\"screenHeight\": \"" + height + "\"," +
                            "\"screenWidth\": \"" + width + "\"," +
                            "\"timeZone\": \"" + timezoneOffset + "\"," +
                            "\"userAgent\": \"" + userAgent + "\"" +
                            "}," +
                            "\"version\": \""+version+"\"," +
                            "\"notificationURL\": \"" + termUrl + "\"," +
                            "\"platformType\": \"02\"," +
                            "\"methodCompletionInd\": \"Y\"" +
                            /*"\"merchantURL\": \"https://staging.paymentz.com\"" +*/
                            "}" +
                            "}" +
                            "}," +
                            "\"deviceDetails\": {" +
                            "\"ipAddress\": \"" + ipAddress + "\"" +
                            "}," +
                            "\"userDetails\": {" +
                            "\"firstName\": \"" + firstname + "\"," +
                            "\"lastName\": \"" + lastname + "\"," +
                            "\"email\": \"" + addressDetailsVO.getEmail() + "\"," +
                            "\"phone\": \"" + addressDetailsVO.getPhone() + "\"" +
                            "}," +
                            "\"shippingAddress\": {" +
                            "\"address\": \"" + street + "\"," +
                            "\"city\": \"" + city + "\"," +
                            "\"country\": \"" + country + "\"," +
                            "\"state\": \"" + state + "\"," +
                            "\"zip\": \"" + zip + "\"" +
                            "}," +
                            "\"billingAddress\": {" +
                            "\"email\": \"" + addressDetailsVO.getEmail() + "\"," +
                            "\"firstName\": \"" + firstname + "\"," +
                            "\"lastName \": \"" + lastname + "\"," +
                            "\"address\": \"" + street + "\"," +
                            "\"city\": \"" + city + "\"," +
                            "\"country\": \"" + country + "\"," +
                            "\"state\": \"" + state + "\"," +
                            "\"zip\": \"" + zip + "\"" +
                            "}," +
                            "\"urlDetails\": {" +
                            "\"notificationUrl\": \"" + notifyUrl + "\"" +
                            "}," +
                            "\"checksum\":\""+checksum+"\""+
                            "}");
                    transactionLogger.error("paymentRequest Sale---"+trackingID+"-->"+saleRequestLog);
                    String response = SafeChargeV2Utils.doPostHTTPSURLConnectionClient(saleUrl, saleRequest.toString());
                    transactionLogger.error("payment response Sale---"+trackingID+"-->"+response);
                    if (functions.isValueNull(response))
                    {
                        JSONObject responseJSON = new JSONObject(response);
                        if (responseJSON.has("transactionStatus"))
                            transactionStatus = responseJSON.getString("transactionStatus");
                        if (responseJSON.has("gwErrorCode"))
                            gwErrorCode = responseJSON.getString("gwErrorCode");
                        if (responseJSON.has("gwErrorReason"))
                            gwErrorReason = responseJSON.getString("gwErrorReason");
                        if (responseJSON.has("authCode"))
                            authCode = responseJSON.getString("authCode");
                        if (responseJSON.has("transactionId"))
                            transactionId = responseJSON.getString("transactionId");
                        if (responseJSON.has("errCode"))
                            errCode = responseJSON.getString("errCode");
                        if (responseJSON.has("reason"))
                            reason = responseJSON.getString("reason");
                        if (responseJSON.has("orderId"))
                            orderid = responseJSON.getString("orderId");
                        if(responseJSON.has("paymentOption") && responseJSON.getJSONObject("paymentOption")!=null && responseJSON.getJSONObject("paymentOption").has("userPaymentOptionId"))
                        {
                            userPaymentOptionId=responseJSON.getJSONObject("paymentOption").getString("userPaymentOptionId");
                        }
                        if (responseJSON.has("paymentOption") && responseJSON.getJSONObject("paymentOption")!=null && responseJSON.getJSONObject("paymentOption").has("card"))
                        {
                            JSONObject cardJson=responseJSON.getJSONObject("paymentOption").getJSONObject("card");
                            if(cardJson.has("threeD"))
                            {
                                JSONObject threeDJson = cardJson.getJSONObject("threeD");
                                if (threeDJson.has("eci"))
                                    eci = threeDJson.getString("eci");
                                if (threeDJson.has("acsUrl"))
                                    acsUrl = threeDJson.getString("acsUrl");
                                if (threeDJson.has("cReq"))
                                    cReq = threeDJson.getString("cReq");
                                if (threeDJson.has("paRequest"))
                                    paRequest = threeDJson.getString("paRequest");
                            }
                        }
                    }else {
                        comm3DResponseVO.setStatus("failed");
                        comm3DResponseVO.setRemark("No payment response found");
                        comm3DResponseVO.setDescription("No payment response found");
                        return comm3DResponseVO;
                    }
                    boolean isUpdated=SafeChargeV2Utils.updateDetailTable(trackingID,orderid,authCode);
                    if("APPROVED".equalsIgnoreCase(transactionStatus))
                    {
                        comm3DResponseVO.setStatus("success");
                        comm3DResponseVO.setTransactionId(transactionId);
                        comm3DResponseVO.setResponseHashInfo(userPaymentOptionId);
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        if(functions.isValueNull(reason))
                        {
                            comm3DResponseVO.setRemark(reason);
                            comm3DResponseVO.setDescription(reason);
                        }else if(functions.isValueNull(gwErrorReason))
                        {
                            comm3DResponseVO.setRemark(gwErrorReason);
                            comm3DResponseVO.setDescription(gwErrorReason);
                        }else
                        {
                            comm3DResponseVO.setRemark(transactionStatus);
                            comm3DResponseVO.setDescription(transactionStatus);
                        }
                    }else  if("REDIRECT".equalsIgnoreCase(transactionStatus))
                    {
                        comm3DResponseVO.setStatus("pending3DConfirmation");
                        comm3DResponseVO.setUrlFor3DRedirect(acsUrl);
                        transactionLogger.error("cReq---->" + cReq);
                        comm3DResponseVO.setCreq(cReq);
                        comm3DResponseVO.setThreeDSSessionData(encryptedCVV);
                        comm3DResponseVO.setPaReq(paRequest);
                        comm3DResponseVO.setTerURL(termUrl);
                        comm3DResponseVO.setMd(encryptedCVV);
                        comm3DResponseVO.setTransactionId(transactionId);
                        comm3DResponseVO.setThreeDVersion("3Dv1");
                        return comm3DResponseVO;
                    }else {
                        comm3DResponseVO.setStatus("failed");
                        if(functions.isValueNull(reason))
                        {
                            comm3DResponseVO.setRemark(reason);
                            comm3DResponseVO.setDescription(reason);
                        }else if(functions.isValueNull(gwErrorReason))
                        {
                            comm3DResponseVO.setRemark(gwErrorReason);
                            comm3DResponseVO.setDescription(gwErrorReason);
                        }else
                        {
                            comm3DResponseVO.setRemark(transactionStatus);
                            comm3DResponseVO.setDescription(transactionStatus);
                        }
                    }
                    comm3DResponseVO.setEci(eci);
                    comm3DResponseVO.setAuthCode(authCode);
                    if(!"0".equalsIgnoreCase(errCode))
                        comm3DResponseVO.setErrorCode(errCode);
                    else if(functions.isValueNull(gwErrorCode))
                        comm3DResponseVO.setErrorCode(gwErrorCode);
                }else
                {
                    comm3DResponseVO.setStatus("failed");
                    if(functions.isValueNull(reason))
                    {
                        comm3DResponseVO.setRemark(reason);
                        comm3DResponseVO.setDescription(reason);
                    }else
                    {
                        comm3DResponseVO.setRemark("Transaction failed");
                        comm3DResponseVO.setDescription("Transaction failed");
                    }
                    comm3DResponseVO.setErrorCode(errCode);
                }
            }else
            {
                comm3DResponseVO.setStatus("failed");
                if(functions.isValueNull(reason))
                {
                    comm3DResponseVO.setRemark(reason);
                    comm3DResponseVO.setDescription(reason);
                }else
                {
                    comm3DResponseVO.setRemark(sessionTokenStatus);
                    comm3DResponseVO.setDescription(sessionTokenStatus);
                }
                comm3DResponseVO.setErrorCode(errCode);

            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException--trackingId--"+trackingID+"-->",e);
            PZExceptionHandler.raiseTechnicalViolationException(SafeChargeV2PaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----Entered in processSale()-----");
        Functions functions=new Functions();
        Comm3DResponseVO comm3DResponseVO=new Comm3DResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transDetailsVO =  commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        CommDeviceDetailsVO deviceDetailsVO=commRequestVO.getCommDeviceDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        String sessionTokenUrl="";
        String saleUrl="";
        String initPaymentUrl="";
        String mid=gatewayAccount.getMerchantId();
        String merchantSiteId=gatewayAccount.getFRAUD_FTP_USERNAME();
        String merchantSecretKey=gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest=gatewayAccount.isTest();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("YYYYMMddHHmmss");
        String timestamp=simpleDateFormat.format(new Date());
        String sessionToken="";
        String errCode="";
        String sessionTokenStatus="";
        String firstname="";
        String lastname="";
        String street="";
        String city="";
        String country="";
        String state="";
        String zip="";
        String termUrl="";
        String checksum="";
        String userAgent="";
        String language="";
        String colorDepth="";
        String timezoneOffset="";
        String height="";
        String width="";
        String transactionStatus="";
        String gwErrorCode="";
        String gwErrorReason="";
        String authCode="";
        String eci="";
        String acsUrl="";
        String cReq="";
        String paRequest="";
        String transactionId="";
        String notifyUrl="";
        String initTransactionStatus="";
        String methodUrl="";
        String methodPayload="";
        String serverTransId="";
        String reason="";
        String encryptedCVV="";
        String version="";
        String cardHolderName="";
        String ipAddress="";
        String userPaymentOptionId="";
        String javaEnabled="";
        String orderid="";
        String v2supported="";
        if(functions.isValueNull(addressDetailsVO.getFirstname()))
            firstname=new String(addressDetailsVO.getFirstname().getBytes(), StandardCharsets.UTF_8);
        if(functions.isValueNull(addressDetailsVO.getLastname()))
            lastname=new String(addressDetailsVO.getLastname().getBytes(), StandardCharsets.UTF_8);
        if(functions.isValueNull(addressDetailsVO.getStreet()))
            street=addressDetailsVO.getStreet();
        if(functions.isValueNull(addressDetailsVO.getCity()))
            city=addressDetailsVO.getStreet();
        if(functions.isValueNull(addressDetailsVO.getCountry()))
            country=addressDetailsVO.getCountry();
        if(functions.isValueNull(addressDetailsVO.getState()))
            state=addressDetailsVO.getState();
        if(functions.isValueNull(addressDetailsVO.getZipCode()))
            zip=addressDetailsVO.getZipCode();
        cardHolderName=firstname+" "+lastname;
        if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress()))
            ipAddress=addressDetailsVO.getCardHolderIpAddress();
        else
            ipAddress=addressDetailsVO.getIp();
        if(functions.isValueNull(deviceDetailsVO.getFingerprints()))
        {
            HashMap fingerPrintMap = functions.getFingerPrintMap(deviceDetailsVO.getFingerprints());
            JSONArray screenResolution = new JSONArray();
            try
            {
                if (fingerPrintMap.containsKey("userAgent"))
                    userAgent = (String) fingerPrintMap.get("userAgent");
                if (fingerPrintMap.containsKey("language"))
                    language = (String) fingerPrintMap.get("language");
                if (fingerPrintMap.containsKey("colorDepth"))
                    colorDepth = String.valueOf(fingerPrintMap.get("colorDepth"));
                if (fingerPrintMap.containsKey("timezoneOffset"))
                    timezoneOffset = String.valueOf(fingerPrintMap.get("timezoneOffset"));
                if (fingerPrintMap.containsKey("screenResolution"))
                    screenResolution = (JSONArray) fingerPrintMap.get("screenResolution");
                if (screenResolution.length() > 0)
                    height = String.valueOf(screenResolution.getString(0));
                if (screenResolution.length() > 1)
                    width = String.valueOf(screenResolution.getString(1));
                javaEnabled="TRUE";
            }
            catch (JSONException e)
            {
                transactionLogger.error("FIngerPrint JSONException---trackingId--" + trackingID + "--", e);
            }
        }else
        {
            if(functions.isValueNull(deviceDetailsVO.getUser_Agent()))
                userAgent=deviceDetailsVO.getUser_Agent();
            else
                userAgent="Mozilla/5.0 (Android; Mobile; rv:13.0) Gecko/13.0 Firefox/13.0";
            if(functions.isValueNull(deviceDetailsVO.getBrowserLanguage()))
                language=deviceDetailsVO.getBrowserLanguage();
            else
                language="en-US";
            if(functions.isValueNull(deviceDetailsVO.getBrowserColorDepth()))
                colorDepth=deviceDetailsVO.getBrowserColorDepth();
            else
                colorDepth="24";
            if(functions.isValueNull(deviceDetailsVO.getBrowserTimezoneOffset()))
                timezoneOffset=deviceDetailsVO.getBrowserTimezoneOffset();
            else
                timezoneOffset="5";
            if(functions.isValueNull(deviceDetailsVO.getBrowserScreenHeight()))
                height=deviceDetailsVO.getBrowserScreenHeight();
            else
                height="939";
            if(functions.isValueNull(deviceDetailsVO.getBrowserScreenWidth()))
                width=deviceDetailsVO.getBrowserScreenWidth();
            else
                width="1255";
            if(functions.isValueNull(deviceDetailsVO.getBrowserJavaEnabled()))
                javaEnabled=deviceDetailsVO.getBrowserJavaEnabled();
            else
                javaEnabled="TRUE";
        }
        try
        {
            if (isTest)
            {
                sessionTokenUrl = RB.getString("TEST_SESSION_TOKEN_URL");
                saleUrl = RB.getString("TEST_SALE_URL");
                initPaymentUrl = RB.getString("TEST_INIT_PAYMENT_URL");

                if("CL".equalsIgnoreCase(firstname) && "BRW".equalsIgnoreCase(lastname))
                {
                    if("4000027891380961".equalsIgnoreCase(cardDetailsVO.getCardNum()))
                        cardHolderName=firstname+"-"+lastname+"1";
                    else
                        cardHolderName=firstname+"-"+lastname+"2";
                }else if("FL".equalsIgnoreCase(firstname) && "BRW".equalsIgnoreCase(lastname))
                {
                    cardHolderName=firstname+"-"+lastname+"1";
                }else if("ERR".equalsIgnoreCase(firstname) && "BRW".equalsIgnoreCase(lastname))
                {
                    cardHolderName=firstname+"-"+lastname+"1";
                }
            }
            else
            {
                sessionTokenUrl = RB.getString("LIVE_SESSION_TOKEN_URL");
                saleUrl = RB.getString("LIVE_SALE_URL");
                initPaymentUrl = RB.getString("LIVE_INIT_PAYMENT_URL");
            }
            if (functions.isValueNull(cardDetailsVO.getcVV()))
            {
                encryptedCVV = PzEncryptor.encryptCVV(cardDetailsVO.getcVV());
            }
            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL")+trackingID+"&MD="+URLEncoder.encode(encryptedCVV);
                transactionLogger.error("From HOST_URL----" + termUrl);
            }
            else
            {
                termUrl = RB.getString("TERM_URL")+trackingID+"&MD="+URLEncoder.encode(encryptedCVV);
                transactionLogger.error("From RB TERM_URL ----" + termUrl);
            }
            notifyUrl=RB.getString("NOTIFY_URL");
            checksum=SafeChargeV2Utils.generateChecksum(mid+merchantSiteId+trackingID+timestamp+merchantSecretKey);
            String sessionTokenRequest = "{" +
                    "\"merchantId\": \"" + mid + "\"," +
                    "\"merchantSiteId\": \"" + merchantSiteId + "\"," +
                    "\"clientRequestId\": \"" + trackingID + "\"," +
                    "\"timeStamp\": \"" + timestamp + "\"," +
                    "\"checksum\": \"" + checksum + "\"" +
                    "}";
            transactionLogger.error("sessionTokenRequest---"+trackingID+"-->"+sessionTokenRequest);
            String sessionTokenResponse = SafeChargeV2Utils.doPostHTTPSURLConnectionClient(sessionTokenUrl, sessionTokenRequest);
            transactionLogger.error("sessionTokenResponse---"+trackingID+"-->"+sessionTokenResponse);
            if (functions.isValueNull(sessionTokenResponse))
            {
                JSONObject sessionTokenResJSON = new JSONObject(sessionTokenResponse);
                if(sessionTokenResJSON.has("sessionToken"))
                    sessionToken=sessionTokenResJSON.getString("sessionToken");
                if(sessionTokenResJSON.has("status"))
                    sessionTokenStatus=sessionTokenResJSON.getString("status");
                if(sessionTokenResJSON.has("errCode"))
                    errCode=sessionTokenResJSON.getString("errCode");
                if(sessionTokenResJSON.has("reason"))
                    reason=sessionTokenResJSON.getString("reason");
            }
            boolean isInserted=SafeChargeV2Utils.insertSessionToken(trackingID,sessionToken,"AUTH");
            if("SUCCESS".equalsIgnoreCase(sessionTokenStatus))
            {
                timestamp=simpleDateFormat.format(new Date());
                checksum=SafeChargeV2Utils.generateChecksum(mid+merchantSiteId+trackingID+transDetailsVO.getAmount()+transDetailsVO.getCurrency()+timestamp+merchantSecretKey);
                String initRequest="{" +
                        "\"sessionToken\": \""+sessionToken+"\"," +
                        "\"merchantId\": \""+mid+"\"," +
                        "\"merchantSiteId\": \""+merchantSiteId+"\"," +
                        "\"orderId\": \""+trackingID+"\"," +
                        "\"clientUniqueId\": \""+trackingID+"\"," +
                        "\"userTokenId\": \""+trackingID+"\"," +
                        "\"amount\": \""+transDetailsVO.getAmount()+"\"," +
                        "\"currency\": \""+transDetailsVO.getCurrency()+"\"," +
                        "\"paymentOption\": {" +
                        "\"card\": {" +
                        "\"cardNumber\": \""+cardDetailsVO.getCardNum()+"\"," +
                        "\"cardHolderName\": \""+cardHolderName+"\"," +
                        "\"expirationMonth\": \""+cardDetailsVO.getExpMonth()+"\"," +
                        "\"expirationYear\": \""+cardDetailsVO.getExpYear()+"\"," +
                        "\"CVV\": \""+cardDetailsVO.getcVV()+"\"," +
                        "\"threeD\": {" +
                        "\"methodNotificationUrl\": \""+termUrl+"\"" +
                        "}" +
                        "}" +
                        "}," +
                        "\"billingAddress\": {" +
                        "\"city\": \""+city+"\"," +
                        "\"country\": \""+country+"\"," +
                        "\"address\": \""+street+"\", " +
                        "\"zip\": \""+zip+"\"," +
                        "\"state\": \""+state+"\"," +
                        "\"email\": \""+addressDetailsVO.getEmail()+"\"," +
                        "\"phone\": \""+addressDetailsVO.getPhone()+"\"," +
                        "\"firstName\": \""+firstname+"\"," +
                        "\"lastName\": \""+lastname+"\"" +
                        "}," +
                        "\"deviceDetails\": {" +
                        "\"ipAddress\": \""+ipAddress+"\"" +
                        "}," +
                        "\"timeStamp\": \""+timestamp+"\"," +
                        "\"checksum\": \""+checksum+"\"" +
                        "}";
                String initRequestLog="{" +
                        "\"sessionToken\": \""+sessionToken+"\"," +
                        "\"merchantId\": \""+mid+"\"," +
                        "\"merchantSiteId\": \""+merchantSiteId+"\"," +
                        "\"orderId\": \""+trackingID+"\"," +
                        "\"clientUniqueId\": \""+trackingID+"\"," +
                        "\"userTokenId\": \""+trackingID+"\"," +
                        "\"amount\": \""+transDetailsVO.getAmount()+"\"," +
                        "\"currency\": \""+transDetailsVO.getCurrency()+"\"," +
                        "\"paymentOption\": {" +
                        "\"card\": {" +
                        "\"cardNumber\": \""+functions.maskingPan(cardDetailsVO.getCardNum())+"\"," +
                        "\"cardHolderName\": \""+cardHolderName+"\"," +
                        "\"expirationMonth\": \""+functions.maskingNumber(cardDetailsVO.getExpMonth())+"\"," +
                        "\"expirationYear\": \""+functions.maskingNumber(cardDetailsVO.getExpYear())+"\"," +
                        "\"CVV\": \""+functions.maskingNumber(cardDetailsVO.getcVV())+"\"," +
                        "\"threeD\": {" +
                        "\"methodNotificationUrl\": \""+termUrl+"\"" +
                        "}" +
                        "}" +
                        "}," +
                        "\"billingAddress\": {" +
                        "\"city\": \""+city+"\"," +
                        "\"country\": \""+country+"\"," +
                        "\"address\": \""+street+"\", " +
                        "\"zip\": \""+zip+"\"," +
                        "\"state\": \""+state+"\"," +
                        "\"email\": \""+addressDetailsVO.getEmail()+"\"," +
                        "\"phone\": \""+addressDetailsVO.getPhone()+"\"," +
                        "\"firstName\": \""+firstname+"\"," +
                        "\"lastName\": \""+lastname+"\"" +
                        "}," +
                        "\"deviceDetails\": {" +
                        "\"ipAddress\": \""+ipAddress+"\"" +
                        "}," +
                        "\"timeStamp\": \""+timestamp+"\"," +
                        "\"checksum\": \""+checksum+"\"" +
                        "}";
                transactionLogger.error("initRequest---"+trackingID+"-->"+initRequestLog);
                String initResponse=SafeChargeV2Utils.doPostHTTPSURLConnectionClient(initPaymentUrl,initRequest);
                transactionLogger.error("initResponse---"+trackingID+"-->"+initResponse);
                if(functions.isValueNull(initResponse))
                {
                    JSONObject initResponseJSON = new JSONObject(initResponse);
                    if (initResponseJSON.has("transactionStatus"))
                        initTransactionStatus = initResponseJSON.getString("transactionStatus");
                    if (initResponseJSON.has("transactionId"))
                        transactionId = initResponseJSON.getString("transactionId");
                    if (initResponseJSON.has("reason"))
                        reason = initResponseJSON.getString("reason");
                    if (initResponseJSON.has("paymentOption") && initResponseJSON.getJSONObject("paymentOption") != null && initResponseJSON.getJSONObject("paymentOption").has("card"))
                    {
                        JSONObject cardJSON=initResponseJSON.getJSONObject("paymentOption").getJSONObject("card");
                        if(cardJSON != null && cardJSON.has("threeD"))
                        {
                            JSONObject threeDJSON = cardJSON.getJSONObject("threeD");
                            if (threeDJSON.has("methodUrl"))
                                methodUrl = threeDJSON.getString("methodUrl");
                            if (threeDJSON.has("methodPayload"))
                                methodPayload = threeDJSON.getString("methodPayload");
                            if (threeDJSON.has("serverTransId"))
                                serverTransId = threeDJSON.getString("serverTransId");
                            if (threeDJSON.has("version"))
                                version = threeDJSON.getString("version");
                            if (threeDJSON.has("v2supported"))
                                v2supported = threeDJSON.getString("v2supported");
                        }

                    }
                    if("true".equalsIgnoreCase(v2supported))
                    {
                        boolean isUpdated=SafeChargeV2Utils.updateDetailTable(trackingID,orderid,authCode);
                    if(functions.isValueNull(commMerchantVO.getHostUrl()))
                        acsUrl="https://"+commMerchantVO.getHostUrl()+RB.getString("ACS_HOST_URL")+trackingID;
                    else
                        acsUrl=RB.getString("ACS_TERM_URL")+trackingID;
                        if(functions.isValueNull(cardDetailsVO.getcVV()))
                        {
                            encryptedCVV=PzEncryptor.encryptCVV(cardDetailsVO.getcVV());
                        }

                        comm3DResponseVO.setUrlFor3DRedirect(acsUrl);
                        comm3DResponseVO.setTransactionId(transactionId);
                        comm3DResponseVO.setMd(encryptedCVV);
                        comm3DResponseVO.setTerURL(termUrl);
                        comm3DResponseVO.setStatus("pending3DConfirmation");
                        comm3DResponseVO.setThreeDVersion("3Dv2");
                        return comm3DResponseVO;
                    }
                }
                else {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setRemark("No initiate response found");
                    comm3DResponseVO.setDescription("No initiate response found");
                    return comm3DResponseVO;
                }
                if("APPROVED".equalsIgnoreCase(initTransactionStatus)){
                    timestamp = simpleDateFormat.format(new Date());
                    checksum=SafeChargeV2Utils.generateChecksum(mid+merchantSiteId+trackingID+transDetailsVO.getAmount()+transDetailsVO.getCurrency()+timestamp+merchantSecretKey);
                    StringBuffer authRequest = new StringBuffer("{");
                    authRequest.append("\"timeStamp\": \"" + timestamp + "\"," +
                            "\"sessionToken\": \"" + sessionToken + "\"," +
                            "\"merchantId\": \"" + mid + "\"," +
                            "\"merchantSiteId\": \"" + merchantSiteId + "\"," +
                            "\"clientRequestId\": \"" + trackingID + "\"," +
                            "\"clientUniqueId\": \"" + trackingID + "\"," +
                            "\"userTokenId\": \""+trackingID+"\"," +
                            "\"amount\": \"" + transDetailsVO.getAmount() + "\"," +
                            "\"currency\": \"" + transDetailsVO.getCurrency() + "\"," +
                            "\"transactionType\": \"Auth\"," +
                            "\"relatedTransactionId\": \""+transactionId+"\"," +
                            "\"paymentOption\": {" +
                            "\"card\": {" +
                            "\"cardNumber\": \"" + cardDetailsVO.getCardNum() + "\"," +
                            "\"cardHolderName\": \"" + cardHolderName + "\"," +
                            "\"expirationMonth\": \"" + cardDetailsVO.getExpMonth() + "\"," +
                            "\"expirationYear\": \"" + cardDetailsVO.getExpYear() + "\"," +
                            "\"CVV\": \"" + cardDetailsVO.getcVV() + "\"," +
                            "\"threeD\": {" +
                            "\"browserDetails\": {" +
                            "\"acceptHeader\": \""+deviceDetailsVO.getAcceptHeader()+"\"," +
                            "\"ip\": \"" + ipAddress + "\"," +
                            "\"javaEnabled\": \""+javaEnabled+"\"," +
                            "\"javaScriptEnabled\": \""+javaEnabled+"\"," +
                            "\"language\": \"" + language + "\"," +
                            "\"colorDepth\": \"" + colorDepth + "\"," +
                            "\"screenHeight\": \"" + height + "\"," +
                            "\"screenWidth\": \"" + width + "\"," +
                            "\"timeZone\": \"" + timezoneOffset + "\"," +
                            "\"userAgent\": \"" + userAgent + "\"" +
                            "}," +
                            "\"version\": \""+version+"\"," +
                            "\"notificationURL\": \"" + termUrl + "\"," +
                            "\"platformType\": \"02\"," +
                            "\"methodCompletionInd\": \"Y\"" +
                            /*"\"merchantURL\": \"https://staging.paymentz.com\"" +*/
                            "}" +
                            "}" +
                            "}," +
                            "\"deviceDetails\": {" +
                            "\"ipAddress\": \"" + ipAddress + "\"" +
                            "}," +
                            "\"userDetails\": {" +
                            "\"firstName\": \"" + firstname + "\"," +
                            "\"lastName\": \"" + lastname + "\"," +
                            "\"email\": \"" + addressDetailsVO.getEmail() + "\"," +
                            "\"phone\": \"" + addressDetailsVO.getPhone() + "\"" +
                            "}," +
                            "\"shippingAddress\": {" +
                            "\"address\": \"" + street + "\"," +
                            "\"city\": \"" + city + "\"," +
                            "\"country\": \"" + country + "\"," +
                            "\"state\": \"" + state + "\"," +
                            "\"zip\": \"" + zip + "\"" +
                            "}," +
                            "\"billingAddress\": {" +
                            "\"email\": \"" + addressDetailsVO.getEmail() + "\"," +
                            "\"firstName\": \"" + firstname + "\"," +
                            "\"lastName \": \"" + lastname + "\"," +
                            "\"address\": \"" + street + "\"," +
                            "\"city\": \"" + city + "\"," +
                            "\"country\": \"" + country + "\"," +
                            "\"state\": \"" + state + "\"," +
                            "\"zip\": \"" + zip + "\"" +
                            "}," +
                            "\"urlDetails\": {" +
                            "\"notificationUrl\": \"" + notifyUrl + "\"" +
                            "}," +
                            "\"checksum\":\""+checksum+"\""+
                            "}");
                    StringBuffer authRequestLog = new StringBuffer("{");
                    authRequestLog.append("\"timeStamp\": \"" + timestamp + "\"," +
                            "\"sessionToken\": \"" + sessionToken + "\"," +
                            "\"merchantId\": \"" + mid + "\"," +
                            "\"merchantSiteId\": \"" + merchantSiteId + "\"," +
                            "\"clientRequestId\": \"" + trackingID + "\"," +
                            "\"clientUniqueId\": \"" + trackingID + "\"," +
                            "\"userTokenId\": \""+trackingID+"\"," +
                            "\"amount\": \"" + transDetailsVO.getAmount() + "\"," +
                            "\"currency\": \"" + transDetailsVO.getCurrency() + "\"," +
                            "\"transactionType\": \"Auth\"," +
                            "\"relatedTransactionId\": \""+transactionId+"\"," +
                            "\"paymentOption\": {" +
                            "\"card\": {" +
                            "\"cardNumber\": \"" + functions.maskingPan(cardDetailsVO.getCardNum()) + "\"," +
                            "\"cardHolderName\": \"" + cardHolderName + "\"," +
                            "\"expirationMonth\": \"" + functions.maskingNumber(cardDetailsVO.getExpMonth()) + "\"," +
                            "\"expirationYear\": \"" + functions.maskingNumber(cardDetailsVO.getExpYear()) + "\"," +
                            "\"CVV\": \"" + functions.maskingNumber(cardDetailsVO.getcVV()) + "\"," +
                            "\"threeD\": {" +
                            "\"browserDetails\": {" +
                            "\"acceptHeader\": \""+deviceDetailsVO.getAcceptHeader()+"\"," +
                            "\"ip\": \"" + ipAddress + "\"," +
                            "\"javaEnabled\": \""+javaEnabled+"\"," +
                            "\"javaScriptEnabled\": \""+javaEnabled+"\"," +
                            "\"language\": \"" + language + "\"," +
                            "\"colorDepth\": \"" + colorDepth + "\"," +
                            "\"screenHeight\": \"" + height + "\"," +
                            "\"screenWidth\": \"" + width + "\"," +
                            "\"timeZone\": \"" + timezoneOffset + "\"," +
                            "\"userAgent\": \"" + userAgent + "\"" +
                            "}," +
                            "\"version\": \""+version+"\"," +
                            "\"notificationURL\": \"" + termUrl + "\"," +
                            "\"platformType\": \"02\"," +
                            "\"methodCompletionInd\": \"Y\"" +
                            /*"\"merchantURL\": \"https://staging.paymentz.com\"" +*/
                            "}" +
                            "}" +
                            "}," +
                            "\"deviceDetails\": {" +
                            "\"ipAddress\": \"" + ipAddress + "\"" +
                            "}," +
                            "\"userDetails\": {" +
                            "\"firstName\": \"" + firstname + "\"," +
                            "\"lastName\": \"" + lastname + "\"," +
                            "\"email\": \"" + addressDetailsVO.getEmail() + "\"," +
                            "\"phone\": \"" + addressDetailsVO.getPhone() + "\"" +
                            "}," +
                            "\"shippingAddress\": {" +
                            "\"address\": \"" + street + "\"," +
                            "\"city\": \"" + city + "\"," +
                            "\"country\": \"" + country + "\"," +
                            "\"state\": \"" + state + "\"," +
                            "\"zip\": \"" + zip + "\"" +
                            "}," +
                            "\"billingAddress\": {" +
                            "\"email\": \"" + addressDetailsVO.getEmail() + "\"," +
                            "\"firstName\": \"" + firstname + "\"," +
                            "\"lastName \": \"" + lastname + "\"," +
                            "\"address\": \"" + street + "\"," +
                            "\"city\": \"" + city + "\"," +
                            "\"country\": \"" + country + "\"," +
                            "\"state\": \"" + state + "\"," +
                            "\"zip\": \"" + zip + "\"" +
                            "}," +
                            "\"urlDetails\": {" +
                            "\"notificationUrl\": \"" + notifyUrl + "\"" +
                            "}," +
                            "\"checksum\":\""+checksum+"\""+
                            "}");
                    transactionLogger.error("paymentRequest Sale---"+trackingID+"-->"+authRequestLog);
                    String response = SafeChargeV2Utils.doPostHTTPSURLConnectionClient(saleUrl, authRequest.toString());
                    transactionLogger.error("payment response Sale---"+trackingID+"-->"+response);
                    if (functions.isValueNull(response))
                    {
                        JSONObject responseJSON = new JSONObject(response);
                        if (responseJSON.has("transactionStatus"))
                            transactionStatus = responseJSON.getString("transactionStatus");
                        if (responseJSON.has("gwErrorCode"))
                            gwErrorCode = responseJSON.getString("gwErrorCode");
                        if (responseJSON.has("gwErrorReason"))
                            gwErrorReason = responseJSON.getString("gwErrorReason");
                        if (responseJSON.has("authCode"))
                            authCode = responseJSON.getString("authCode");
                        if (responseJSON.has("transactionId"))
                            transactionId = responseJSON.getString("transactionId");
                        if (responseJSON.has("errCode"))
                            errCode = responseJSON.getString("errCode");
                        if (responseJSON.has("reason"))
                            reason = responseJSON.getString("reason");
                        if (responseJSON.has("orderId"))
                            orderid = responseJSON.getString("orderId");
                        if(responseJSON.has("paymentOption") && responseJSON.getJSONObject("paymentOption")!=null && responseJSON.getJSONObject("paymentOption").has("userPaymentOptionId"))
                        {
                            userPaymentOptionId=responseJSON.getJSONObject("paymentOption").getString("userPaymentOptionId");
                        }
                        if (responseJSON.has("paymentOption") && responseJSON.getJSONObject("paymentOption")!=null && responseJSON.getJSONObject("paymentOption").has("card"))
                        {
                            JSONObject cardJson=responseJSON.getJSONObject("paymentOption").getJSONObject("card");
                            if(cardJson.has("threeD"))
                            {
                                JSONObject threeDJson = cardJson.getJSONObject("threeD");
                                if (threeDJson.has("eci"))
                                    eci = threeDJson.getString("eci");
                                if (threeDJson.has("acsUrl"))
                                    acsUrl = threeDJson.getString("acsUrl");
                                if (threeDJson.has("cReq"))
                                    cReq = threeDJson.getString("cReq");
                                if (threeDJson.has("paRequest"))
                                    paRequest = threeDJson.getString("paRequest");
                            }
                        }
                    }else {
                        comm3DResponseVO.setStatus("failed");
                        comm3DResponseVO.setRemark("No payment response found");
                        comm3DResponseVO.setDescription("No payment response found");
                        return comm3DResponseVO;
                    }
                    boolean isUpdated=SafeChargeV2Utils.updateDetailTable(trackingID,orderid,authCode);
                    if("APPROVED".equalsIgnoreCase(transactionStatus))
                    {
                        comm3DResponseVO.setStatus("success");
                        comm3DResponseVO.setTransactionId(transactionId);
                        comm3DResponseVO.setResponseHashInfo(userPaymentOptionId);
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        if(functions.isValueNull(reason))
                        {
                            comm3DResponseVO.setRemark(reason);
                            comm3DResponseVO.setDescription(reason);
                        }else if(functions.isValueNull(gwErrorReason))
                        {
                            comm3DResponseVO.setRemark(gwErrorReason);
                            comm3DResponseVO.setDescription(gwErrorReason);
                        }else
                        {
                            comm3DResponseVO.setRemark(transactionStatus);
                            comm3DResponseVO.setDescription(transactionStatus);
                        }
                    }else  if("REDIRECT".equalsIgnoreCase(transactionStatus))
                    {
                        if (functions.isValueNull(cardDetailsVO.getcVV()))
                        {
                            encryptedCVV = PzEncryptor.encryptCVV(cardDetailsVO.getcVV());
                        }
                        comm3DResponseVO.setStatus("pending3DConfirmation");
                        comm3DResponseVO.setUrlFor3DRedirect(acsUrl);
                        transactionLogger.error("cReq---->" + cReq);
                        comm3DResponseVO.setCreq(cReq);
                        comm3DResponseVO.setThreeDSSessionData(encryptedCVV);
                        comm3DResponseVO.setPaReq(paRequest);
                        comm3DResponseVO.setTerURL(termUrl);
                        comm3DResponseVO.setMd(encryptedCVV);
                        comm3DResponseVO.setTransactionId(transactionId);
                        comm3DResponseVO.setThreeDVersion("3Dv1");
                        return comm3DResponseVO;
                    }else {
                        comm3DResponseVO.setStatus("failed");
                        if(functions.isValueNull(reason))
                        {
                            comm3DResponseVO.setRemark(reason);
                            comm3DResponseVO.setDescription(reason);
                        }else if(functions.isValueNull(gwErrorReason))
                        {
                            comm3DResponseVO.setRemark(gwErrorReason);
                            comm3DResponseVO.setDescription(gwErrorReason);
                        }else
                        {
                            comm3DResponseVO.setRemark(transactionStatus);
                            comm3DResponseVO.setDescription(transactionStatus);
                        }
                    }
                    comm3DResponseVO.setEci(eci);
                    comm3DResponseVO.setAuthCode(authCode);
                    if(!"0".equalsIgnoreCase(errCode))
                        comm3DResponseVO.setErrorCode(errCode);
                    else if(functions.isValueNull(gwErrorCode))
                        comm3DResponseVO.setErrorCode(gwErrorCode);
                }else
                {
                    comm3DResponseVO.setStatus("failed");
                    if(functions.isValueNull(reason))
                    {
                        comm3DResponseVO.setRemark(reason);
                        comm3DResponseVO.setDescription(reason);
                    }else
                    {
                        comm3DResponseVO.setRemark("Transaction failed");
                        comm3DResponseVO.setDescription("Transaction failed");
                    }
                    comm3DResponseVO.setErrorCode(errCode);
                }
            }else
            {
                comm3DResponseVO.setStatus("failed");
                if(functions.isValueNull(reason))
                {
                    comm3DResponseVO.setRemark(reason);
                    comm3DResponseVO.setDescription(reason);
                }else
                {
                    comm3DResponseVO.setRemark(sessionTokenStatus);
                    comm3DResponseVO.setDescription(sessionTokenStatus);
                }
                comm3DResponseVO.setErrorCode(errCode);

            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException--trackingId--"+trackingID+"-->",e);
            PZExceptionHandler.raiseTechnicalViolationException(SafeChargeV2PaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }
    public GenericResponseVO processCommon3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Comm3DRequestVO commRequestVO= (Comm3DRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommDeviceDetailsVO deviceDetailsVO=commRequestVO.getCommDeviceDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        Functions functions=new Functions();

        String PaRes=commRequestVO.getPaRes();
        String sessionTokenUrl="";
        String paymentUrl="";
        String checksum="";
        String timestamp="";
        String sessionToken="";
        String sessionTokenStatus="";
        String errCode="";
        String firstname="";
        String lastname="";
        String street="";
        String city="";
        String country="";
        String state="";
        String zip="";
        String email="";
        String transactionStatus="";
        String gwErrorCode="";
        String gwErrorReason="";
        String authCode="";
        String transactionId="";
        String reason="";
        String eci="";
        String userPaymentOptionId="";
        String cardHolderName="";
        String termUrl="";
        String acsUrl="";
        String cReq="";
        if(functions.isValueNull(addressDetailsVO.getFirstname()))
            firstname=new String(addressDetailsVO.getFirstname().getBytes(), StandardCharsets.UTF_8);
        if(functions.isValueNull(addressDetailsVO.getLastname()))
            lastname=new String(addressDetailsVO.getLastname().getBytes(), StandardCharsets.UTF_8);
        if(functions.isValueNull(addressDetailsVO.getEmail()))
            email=addressDetailsVO.getEmail();
        if(functions.isValueNull(addressDetailsVO.getStreet()))
            street=addressDetailsVO.getStreet();
        if(functions.isValueNull(addressDetailsVO.getCity()))
            city=addressDetailsVO.getStreet();
        if(functions.isValueNull(addressDetailsVO.getCountry()))
            country=addressDetailsVO.getCountry();
        if(functions.isValueNull(addressDetailsVO.getState()))
            state=addressDetailsVO.getState();
        if(functions.isValueNull(addressDetailsVO.getZipCode()))
            zip=addressDetailsVO.getZipCode();
        cardHolderName=firstname+" "+lastname;
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String mid=gatewayAccount.getMerchantId();
        String merchantSiteId=gatewayAccount.getFRAUD_FTP_USERNAME();
        String merchantSecretKey=gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest=gatewayAccount.isTest();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("YYYYMMddHHmmss");
        if (isTest)
        {
            sessionTokenUrl = RB.getString("TEST_SESSION_TOKEN_URL");
            paymentUrl = RB.getString("TEST_SALE_URL");
            if("CL".equalsIgnoreCase(firstname) && "BRW".equalsIgnoreCase(lastname))
            {
                if("4000027891380961".equalsIgnoreCase(commCardDetailsVO.getCardNum()))
                    cardHolderName=firstname+"-"+lastname+"1";
                else
                    cardHolderName=firstname+"-"+lastname+"2";
            }else if("FL".equalsIgnoreCase(firstname) && "BRW".equalsIgnoreCase(lastname))
            {
                cardHolderName=firstname+"-"+lastname+"1";
            }
            else if("ERR".equalsIgnoreCase(firstname) && "BRW".equalsIgnoreCase(lastname))
            {
                cardHolderName=firstname+"-"+lastname+"1";
            }
        }
        else
        {
            sessionTokenUrl = RB.getString("LIVE_SESSION_TOKEN_URL");
            paymentUrl = RB.getString("LIVE_SALE_URL");
        }
        String encryptedCVV="";
        if (functions.isValueNull(commCardDetailsVO.getcVV()))
        {
            encryptedCVV = PzEncryptor.encryptCVV(commCardDetailsVO.getcVV());
        }
        if (commMerchantVO!=null && functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL")+trackingID+"&MD="+URLEncoder.encode(encryptedCVV);
            transactionLogger.error("From HOST_URL----" + termUrl);
        }
        else
        {
        termUrl = RB.getString("TERM_URL")+trackingID+"&MD="+URLEncoder.encode(encryptedCVV);
        transactionLogger.error("From RB TERM_URL ----" + termUrl);
        }
        String notifyUrl=RB.getString("NOTIFY_URL");
        try
        {
            /*timestamp=simpleDateFormat.format(new Date());
            checksum = SafeChargeV2Utils.generateChecksum(mid + merchantSiteId + trackingID + timestamp + merchantSecretKey);
            String sessionTokenRequest = "{" +
                    "\"merchantId\": \"" + mid + "\"," +
                    "\"merchantSiteId\": \"" + merchantSiteId + "\"," +
                    "\"clientRequestId\": \"" + trackingID + "\"," +
                    "\"timeStamp\": \"" + timestamp + "\"," +
                    "\"checksum\": \"" + checksum + "\"" +
                    "}";
            transactionLogger.error("sessionTokenRequest---" + trackingID + "-->" + sessionTokenRequest);
            String sessionTokenResponse = SafeChargeV2Utils.doPostHTTPSURLConnectionClient(sessionTokenUrl, sessionTokenRequest);
            transactionLogger.error("sessionTokenResponse---" + trackingID + "-->" + sessionTokenResponse);
            if (functions.isValueNull(sessionTokenResponse))
            {
                JSONObject sessionTokenResJSON = new JSONObject(sessionTokenResponse);
                if (sessionTokenResJSON.has("sessionToken"))
                    sessionToken = sessionTokenResJSON.getString("sessionToken");
                if (sessionTokenResJSON.has("status"))
                    sessionTokenStatus = sessionTokenResJSON.getString("status");
                if (sessionTokenResJSON.has("sessionToken"))
                    errCode = sessionTokenResJSON.getString("errCode");
            }*/
            HashMap<String,String> hashMap=SafeChargeV2Utils.getSessionToken(trackingID);
            sessionToken=hashMap.get("sessionToken");
            String orderId=hashMap.get("orderId");
            timestamp=simpleDateFormat.format(new Date());
            checksum=SafeChargeV2Utils.generateChecksum(mid+merchantSiteId+trackingID+transactionDetailsVO.getAmount()+transactionDetailsVO.getCurrency()+timestamp+merchantSecretKey);
            StringBuffer paymentRequest=new StringBuffer();
            paymentRequest.append("{" +
                    "\"sessionToken\": \""+sessionToken+"\"," +
                    "\"merchantId\": \""+mid+"\"," +
                    "\"merchantSiteId\": \""+merchantSiteId+"\"," +
                    "\"clientRequestId\": \""+trackingID+"\"," +
                    "\"clientUniqueId\": \"" + trackingID + "\"," +
                    "\"userTokenId\": \""+trackingID+"\"," +
                    "\"orderid\": \""+orderId+"\"," +
                    "\"timeStamp\": \""+timestamp+"\"," +
                    "\"checksum\": \""+checksum+"\",");
            if(!functions.isValueNull(PaRes))
            {
                paymentRequest.append("\"relatedTransactionId\": \"" + transactionDetailsVO.getPreviousTransactionId() + "\",");
            }
            paymentRequest.append("\"currency\": \""+transactionDetailsVO.getCurrency()+"\"," +
                    "\"amount\": \""+transactionDetailsVO.getAmount()+"\"," +
                    "\"transactionType\": \"Sale\"," +
                    "\"paymentOption\": {" +
                    "\"card\": {" +
                    "\"cardNumber\": \""+commCardDetailsVO.getCardNum()+"\"," +
                    "\"cardHolderName\": \""+cardHolderName+"\"," +
                    "\"expirationMonth\": \""+commCardDetailsVO.getExpMonth()+"\"," +
                    "\"expirationYear\": \""+commCardDetailsVO.getExpYear()+"\"," +
                    "\"CVV\": \""+commCardDetailsVO.getcVV()+"\"") ;
            if(functions.isValueNull(PaRes))
            {
                paymentRequest.append(",\"threeD\":{" +
                        "\"paResponse\":\""+PaRes+"\""+
                        "}");
            }else if("3Dv2".equalsIgnoreCase(commRequestVO.getAttemptThreeD()))
            {
                paymentRequest.append(",\"threeD\": {" +
                        "\"browserDetails\": {" +
                        "\"acceptHeader\": \""+deviceDetailsVO.getAcceptHeader()+"\"," +
                        "\"ip\": \"" + addressDetailsVO.getCardHolderIpAddress() + "\"," +
                        "\"javaEnabled\": \""+deviceDetailsVO.getBrowserJavaEnabled()+"\"," +
                        "\"javaScriptEnabled\": \""+deviceDetailsVO.getBrowserJavaEnabled()+"\"," +
                        "\"language\": \"" + deviceDetailsVO.getBrowserLanguage() + "\"," +
                        "\"colorDepth\": \"" + deviceDetailsVO.getBrowserColorDepth() + "\"," +
                        "\"screenHeight\": \"" + deviceDetailsVO.getBrowserScreenHeight() + "\"," +
                        "\"screenWidth\": \"" + deviceDetailsVO.getBrowserScreenWidth() + "\"," +
                        "\"timeZone\": \"" + deviceDetailsVO.getBrowserTimezoneOffset() + "\"," +
                        "\"userAgent\": \"" + deviceDetailsVO.getUser_Agent() + "\"" +
                        "}," +
                        "\"version\": \"2.1.0\"," +
                        "\"notificationURL\": \"" + termUrl + "\"," +
                        "\"platformType\": \"02\"," +
                        "\"methodCompletionInd\": \"Y\"" +
                        "}");
            }
            paymentRequest.append("}" +
                    "}," +
                    "\"billingAddress\": {" +
                    "\"firstName\": \""+firstname+"\"," +
                    "\"lastName\": \""+lastname+"\"," +
                    "\"address\": \""+street+"\"," +
                    "\"city\": \""+city+"\"," +
                    "\"state\": \"" + state + "\"," +
                    "\"zip\": \"" + zip + "\"," +
                    "\"country\": \""+country+"\"," +
                    "\"email\": \""+email+"\"" +
                    "}," +
                    "\"urlDetails\": {" +
                    "\"notificationUrl\": \"" + notifyUrl + "\"" +
                    "}," +
                    "\"deviceDetails\": {" +
                    "\"ipAddress\": \""+addressDetailsVO.getCardHolderIpAddress()+"\"" +
                    "}" +
                    "}");

            StringBuffer paymentRequestLog=new StringBuffer();
            paymentRequestLog.append("{" +
                    "\"sessionToken\": \""+sessionToken+"\"," +
                    "\"merchantId\": \""+mid+"\"," +
                    "\"merchantSiteId\": \""+merchantSiteId+"\"," +
                    "\"clientRequestId\": \""+trackingID+"\"," +
                    "\"clientUniqueId\": \"" + trackingID + "\"," +
                    "\"userTokenId\": \""+trackingID+"\"," +
                    "\"orderid\": \""+orderId+"\"," +
                    "\"timeStamp\": \""+timestamp+"\"," +
                    "\"checksum\": \""+checksum+"\",");
            if(!functions.isValueNull(PaRes))
            {
                paymentRequestLog.append("\"relatedTransactionId\": \"" + transactionDetailsVO.getPreviousTransactionId() + "\",");
            }
            paymentRequestLog.append("\"currency\": \""+transactionDetailsVO.getCurrency()+"\"," +
                    "\"amount\": \""+transactionDetailsVO.getAmount()+"\"," +
                    "\"transactionType\": \"Sale\"," +
                    "\"paymentOption\": {" +
                    "\"card\": {" +
                    "\"cardNumber\": \""+commCardDetailsVO.getCardNum()+"\"," +
                    "\"cardHolderName\": \""+cardHolderName+"\"," +
                    "\"expirationMonth\": \""+commCardDetailsVO.getExpMonth()+"\"," +
                    "\"expirationYear\": \""+commCardDetailsVO.getExpYear()+"\"," +
                    "\"CVV\": \""+commCardDetailsVO.getcVV()+"\"") ;
            if(functions.isValueNull(PaRes))
            {
                paymentRequestLog.append(",\"threeD\":{" +
                        "\"paResponse\":\""+PaRes+"\""+
                        "}");
            }else if("3Dv2".equalsIgnoreCase(commRequestVO.getAttemptThreeD()))
            {
                paymentRequestLog.append(",\"threeD\": {" +
                        "\"browserDetails\": {" +
                        "\"acceptHeader\": \""+deviceDetailsVO.getAcceptHeader()+"\"," +
                        "\"ip\": \"" + addressDetailsVO.getCardHolderIpAddress() + "\"," +
                        "\"javaEnabled\": \""+deviceDetailsVO.getBrowserJavaEnabled()+"\"," +
                        "\"javaScriptEnabled\": \""+deviceDetailsVO.getBrowserJavaEnabled()+"\"," +
                        "\"language\": \"" + deviceDetailsVO.getBrowserLanguage() + "\"," +
                        "\"colorDepth\": \"" + deviceDetailsVO.getBrowserColorDepth() + "\"," +
                        "\"screenHeight\": \"" + deviceDetailsVO.getBrowserScreenHeight() + "\"," +
                        "\"screenWidth\": \"" + deviceDetailsVO.getBrowserScreenWidth() + "\"," +
                        "\"timeZone\": \"" + deviceDetailsVO.getBrowserTimezoneOffset() + "\"," +
                        "\"userAgent\": \"" + deviceDetailsVO.getUser_Agent() + "\"" +
                        "}," +
                        "\"version\": \"2.1.0\"," +
                        "\"notificationURL\": \"" + termUrl + "\"," +
                        "\"platformType\": \"02\"," +
                        "\"methodCompletionInd\": \"Y\"" +
                        "}");
            }
            paymentRequestLog.append("}" +
                    "}," +
                    "\"billingAddress\": {" +
                    "\"firstName\": \""+firstname+"\"," +
                    "\"lastName\": \""+lastname+"\"," +
                    "\"address\": \""+street+"\"," +
                    "\"city\": \""+city+"\"," +
                    "\"state\": \"" + state + "\"," +
                    "\"zip\": \"" + zip + "\"," +
                    "\"country\": \""+country+"\"," +
                    "\"email\": \""+email+"\"" +
                    "}," +
                    "\"urlDetails\": {" +
                    "\"notificationUrl\": \"" + notifyUrl + "\"" +
                    "}," +
                    "\"deviceDetails\": {" +
                    "\"ipAddress\": \""+addressDetailsVO.getCardHolderIpAddress()+"\"" +
                    "}" +
                    "}");
            transactionLogger.error("processCommon3DSaleConfirmation payment Request---"+trackingID+"-->"+paymentRequestLog.toString());
            String response = SafeChargeV2Utils.doPostHTTPSURLConnectionClient(paymentUrl, paymentRequest.toString());
            transactionLogger.error("processCommon3DSaleConfirmation payment response---"+trackingID+"-->"+response);
            if (functions.isValueNull(response))
            {
                JSONObject responseJSON = new JSONObject(response);
                if (responseJSON.has("transactionStatus"))
                    transactionStatus = responseJSON.getString("transactionStatus");
                if (responseJSON.has("gwErrorCode"))
                    gwErrorCode = responseJSON.getString("gwErrorCode");
                if (responseJSON.has("gwErrorReason"))
                    gwErrorReason = responseJSON.getString("gwErrorReason");
                if (responseJSON.has("authCode"))
                    authCode = responseJSON.getString("authCode");
                if (responseJSON.has("transactionId"))
                    transactionId = responseJSON.getString("transactionId");
                if (responseJSON.has("errCode"))
                    errCode = responseJSON.getString("errCode");
                if (responseJSON.has("reason"))
                    reason = responseJSON.getString("reason");
                if(responseJSON.has("paymentOption") && responseJSON.getJSONObject("paymentOption")!=null && responseJSON.getJSONObject("paymentOption").has("userPaymentOptionId"))
                {
                    userPaymentOptionId=responseJSON.getJSONObject("paymentOption").getString("userPaymentOptionId");
                }
                if (responseJSON.has("paymentOption") && responseJSON.getJSONObject("paymentOption")!=null && responseJSON.getJSONObject("paymentOption").has("card"))
                {
                    JSONObject cardJson=responseJSON.getJSONObject("paymentOption").getJSONObject("card");
                    if(cardJson.has("threeD"))
                    {
                        JSONObject threeDJson = cardJson.getJSONObject("threeD");
                        if (threeDJson.has("eci"))
                            eci = threeDJson.getString("eci");
                        if (threeDJson.has("acsUrl"))
                            acsUrl = threeDJson.getString("acsUrl");
                        if (threeDJson.has("cReq"))
                            cReq = threeDJson.getString("cReq");
                    }
                }
            }
            boolean isUpdated=SafeChargeV2Utils.updateDetailTable(trackingID,orderId,authCode);
            transactionLogger.error("transactionStatus--->"+transactionStatus);
            if("APPROVED".equalsIgnoreCase(transactionStatus))
            {
                commResponseVO.setStatus("success");
                commResponseVO.setTransactionId(transactionId);
                commResponseVO.setResponseHashInfo(userPaymentOptionId);
                if(functions.isValueNull(reason))
                {
                    commResponseVO.setRemark(reason);
                    commResponseVO.setDescription(reason);
                }else if(functions.isValueNull(gwErrorReason))
                {
                    commResponseVO.setRemark(gwErrorReason);
                    commResponseVO.setDescription(gwErrorReason);
                }else
                {
                    commResponseVO.setRemark(transactionStatus);
                    commResponseVO.setDescription(transactionStatus);
                }
            }else  if("REDIRECT".equalsIgnoreCase(transactionStatus))
            {
                commResponseVO.setStatus("pending3DConfirmation");
                commResponseVO.setUrlFor3DRedirect(acsUrl);
                transactionLogger.error("cReq---->" + cReq);
                commResponseVO.setCreq(cReq);
                commResponseVO.setThreeDSSessionData(encryptedCVV);
                commResponseVO.setTerURL(termUrl);
                commResponseVO.setTransactionId(transactionId);
                return commResponseVO;
            }else {
                commResponseVO.setStatus("failed");
                if(functions.isValueNull(reason))
                {
                    commResponseVO.setRemark(reason);
                    commResponseVO.setDescription(reason);
                }else if(functions.isValueNull(gwErrorReason))
                {
                    commResponseVO.setRemark(gwErrorReason);
                    commResponseVO.setDescription(gwErrorReason);
                }else
                {
                    commResponseVO.setRemark(transactionStatus);
                    commResponseVO.setDescription(transactionStatus);
                }
            }
            commResponseVO.setEci(eci);
            commResponseVO.setAuthCode(authCode);
            if(!"0".equalsIgnoreCase(errCode))
                commResponseVO.setErrorCode(errCode);
            else if(functions.isValueNull(gwErrorCode))
                commResponseVO.setErrorCode(gwErrorCode);

        }
        catch (JSONException e)
        {
            transactionLogger.error("processCommon3DSaleConfirmation JSONException--trackingId--"+trackingID+"-->",e);
            PZExceptionHandler.raiseTechnicalViolationException(SafeChargeV2PaymentGateway.class.getName(), "processCommon3DSaleConfirmation()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }
    public GenericResponseVO processCommon3DAuthConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Comm3DRequestVO commRequestVO= (Comm3DRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommDeviceDetailsVO deviceDetailsVO=commRequestVO.getCommDeviceDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        Functions functions=new Functions();

        String PaRes=commRequestVO.getPaRes();
        String sessionTokenUrl="";
        String paymentUrl="";
        String checksum="";
        String timestamp="";
        String sessionToken="";
        String sessionTokenStatus="";
        String errCode="";
        String firstname="";
        String lastname="";
        String street="";
        String city="";
        String country="";
        String state="";
        String zip="";
        String email="";
        String transactionStatus="";
        String gwErrorCode="";
        String gwErrorReason="";
        String authCode="";
        String transactionId="";
        String reason="";
        String eci="";
        String userPaymentOptionId="";
        String cardHolderName="";
        String termUrl="";
        String acsUrl="";
        String cReq="";
        if(functions.isValueNull(addressDetailsVO.getFirstname()))
            firstname=new String(addressDetailsVO.getFirstname().getBytes(), StandardCharsets.UTF_8);
        if(functions.isValueNull(addressDetailsVO.getLastname()))
            lastname=new String(addressDetailsVO.getLastname().getBytes(), StandardCharsets.UTF_8);
        if(functions.isValueNull(addressDetailsVO.getEmail()))
            email=addressDetailsVO.getEmail();
        if(functions.isValueNull(addressDetailsVO.getStreet()))
            street=addressDetailsVO.getStreet();
        if(functions.isValueNull(addressDetailsVO.getCity()))
            city=addressDetailsVO.getStreet();
        if(functions.isValueNull(addressDetailsVO.getCountry()))
            country=addressDetailsVO.getCountry();
        if(functions.isValueNull(addressDetailsVO.getState()))
            state=addressDetailsVO.getState();
        if(functions.isValueNull(addressDetailsVO.getZipCode()))
            zip=addressDetailsVO.getZipCode();
        cardHolderName=firstname+" "+lastname;
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String mid=gatewayAccount.getMerchantId();
        String merchantSiteId=gatewayAccount.getFRAUD_FTP_USERNAME();
        String merchantSecretKey=gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest=gatewayAccount.isTest();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("YYYYMMddHHmmss");
        if (isTest)
        {
            sessionTokenUrl = RB.getString("TEST_SESSION_TOKEN_URL");
            paymentUrl = RB.getString("TEST_SALE_URL");

            if("CL".equalsIgnoreCase(firstname) && "BRW".equalsIgnoreCase(lastname))
            {
                if("4000027891380961".equalsIgnoreCase(commCardDetailsVO.getCardNum()))
                    cardHolderName=firstname+"-"+lastname+"1";
                else
                    cardHolderName=firstname+"-"+lastname+"2";
            }else if("FL".equalsIgnoreCase(firstname) && "BRW".equalsIgnoreCase(lastname))
            {
                cardHolderName=firstname+"-"+lastname+"1";
            }else if("ERR".equalsIgnoreCase(firstname) && "BRW".equalsIgnoreCase(lastname))
            {
                cardHolderName=firstname+"-"+lastname+"1";
            }
        }
        else
        {
            sessionTokenUrl = RB.getString("LIVE_SESSION_TOKEN_URL");
            paymentUrl = RB.getString("LIVE_SALE_URL");
        }
        String encryptedCVV="";
        if (functions.isValueNull(commCardDetailsVO.getcVV()))
        {
            encryptedCVV = PzEncryptor.encryptCVV(commCardDetailsVO.getcVV());
        }
        if (commMerchantVO!=null && functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL")+trackingID+"&MD="+URLEncoder.encode(encryptedCVV);;
            transactionLogger.error("From HOST_URL----" + termUrl);
        }
        else
        {
        termUrl = RB.getString("TERM_URL")+trackingID+"&MD="+URLEncoder.encode(encryptedCVV);;
        transactionLogger.error("From RB TERM_URL ----" + termUrl);
        }
        String notifyUrl=RB.getString("NOTIFY_URL");
        try
        {
            /*timestamp=simpleDateFormat.format(new Date());
            checksum = SafeChargeV2Utils.generateChecksum(mid + merchantSiteId + trackingID + timestamp + merchantSecretKey);
            String sessionTokenRequest = "{" +
                    "\"merchantId\": \"" + mid + "\"," +
                    "\"merchantSiteId\": \"" + merchantSiteId + "\"," +
                    "\"clientRequestId\": \"" + trackingID + "\"," +
                    "\"timeStamp\": \"" + timestamp + "\"," +
                    "\"checksum\": \"" + checksum + "\"" +
                    "}";
            transactionLogger.error("sessionTokenRequest---" + trackingID + "-->" + sessionTokenRequest);
            String sessionTokenResponse = SafeChargeV2Utils.doPostHTTPSURLConnectionClient(sessionTokenUrl, sessionTokenRequest);
            transactionLogger.error("sessionTokenResponse---" + trackingID + "-->" + sessionTokenResponse);
            if (functions.isValueNull(sessionTokenResponse))
            {
                JSONObject sessionTokenResJSON = new JSONObject(sessionTokenResponse);
                if (sessionTokenResJSON.has("sessionToken"))
                    sessionToken = sessionTokenResJSON.getString("sessionToken");
                if (sessionTokenResJSON.has("status"))
                    sessionTokenStatus = sessionTokenResJSON.getString("status");
                if (sessionTokenResJSON.has("sessionToken"))
                    errCode = sessionTokenResJSON.getString("errCode");
            }*/
            HashMap<String,String> hashMap=SafeChargeV2Utils.getSessionToken(trackingID);
            sessionToken=hashMap.get("sessionToken");
            String orderId=hashMap.get("orderId");

            timestamp=simpleDateFormat.format(new Date());
            checksum=SafeChargeV2Utils.generateChecksum(mid+merchantSiteId+trackingID+transactionDetailsVO.getAmount()+transactionDetailsVO.getCurrency()+timestamp+merchantSecretKey);
            StringBuffer paymentRequest=new StringBuffer();
            paymentRequest.append("{" +
                    "\"sessionToken\": \""+sessionToken+"\"," +
                    "\"merchantId\": \""+mid+"\"," +
                    "\"merchantSiteId\": \""+merchantSiteId+"\"," +
                    "\"clientRequestId\": \""+trackingID+"\"," +
                    "\"clientUniqueId\": \"" + trackingID + "\"," +
                    "\"userTokenId\": \""+trackingID+"\"," +
                    "\"orderId\": \""+orderId+"\"," +
                    "\"timeStamp\": \""+timestamp+"\"," +
                    "\"checksum\": \""+checksum+"\",");
            if(!functions.isValueNull(PaRes))
            {
                paymentRequest.append("\"relatedTransactionId\": \"" + transactionDetailsVO.getPreviousTransactionId() + "\",");
            }
            paymentRequest.append("\"currency\": \""+transactionDetailsVO.getCurrency()+"\"," +
                    "\"amount\": \""+transactionDetailsVO.getAmount()+"\"," +
                    "\"transactionType\": \"Auth\"," +
                    "\"paymentOption\": {" +
                    "\"card\": {" +
                    "\"cardNumber\": \""+commCardDetailsVO.getCardNum()+"\"," +
                    "\"cardHolderName\": \""+cardHolderName+"\"," +
                    "\"expirationMonth\": \""+commCardDetailsVO.getExpMonth()+"\"," +
                    "\"expirationYear\": \""+commCardDetailsVO.getExpYear()+"\"," +
                    "\"CVV\": \""+commCardDetailsVO.getcVV()+"\"") ;
            if(functions.isValueNull(PaRes))
            {
                paymentRequest.append(",\"threeD\":{" +
                        "\"paResponse\":\""+PaRes+"\""+
                        "}");
            }else if("3Dv2".equalsIgnoreCase(commRequestVO.getAttemptThreeD()))
            {
                paymentRequest.append(",\"threeD\": {" +
                        "\"browserDetails\": {" +
                        "\"acceptHeader\": \""+deviceDetailsVO.getAcceptHeader()+"\"," +
                        "\"ip\": \"" + addressDetailsVO.getCardHolderIpAddress() + "\"," +
                        "\"javaEnabled\": \""+deviceDetailsVO.getBrowserJavaEnabled()+"\"," +
                        "\"javaScriptEnabled\": \""+deviceDetailsVO.getBrowserJavaEnabled()+"\"," +
                        "\"language\": \"" + deviceDetailsVO.getBrowserLanguage() + "\"," +
                        "\"colorDepth\": \"" + deviceDetailsVO.getBrowserColorDepth() + "\"," +
                        "\"screenHeight\": \"" + deviceDetailsVO.getBrowserScreenHeight() + "\"," +
                        "\"screenWidth\": \"" + deviceDetailsVO.getBrowserScreenWidth() + "\"," +
                        "\"timeZone\": \"" + deviceDetailsVO.getBrowserTimezoneOffset() + "\"," +
                        "\"userAgent\": \"" + deviceDetailsVO.getUser_Agent() + "\"" +
                        "}," +
                        "\"version\": \"2.1.0\"," +
                        "\"notificationURL\": \"" + termUrl + "\"," +
                        "\"platformType\": \"02\"," +
                        "\"methodCompletionInd\": \"Y\"" +
                        "}");
            }
            paymentRequest.append("}" +
                    "}," +
                    "\"billingAddress\": {" +
                    "\"firstName\": \""+firstname+"\"," +
                    "\"lastName\": \""+lastname+"\"," +
                    "\"address\": \""+street+"\"," +
                    "\"city\": \""+city+"\"," +
                    "\"state\": \"" + state + "\"," +
                    "\"zip\": \"" + zip + "\"," +
                    "\"country\": \""+country+"\"," +
                    "\"email\": \""+email+"\"" +
                    "}," +
                    "\"urlDetails\": {" +
                    "\"notificationUrl\": \"" + notifyUrl + "\"" +
                    "}," +
                    "\"deviceDetails\": {" +
                    "\"ipAddress\": \""+addressDetailsVO.getCardHolderIpAddress()+"\"" +
                    "}" +
                    "}");
            StringBuffer paymentRequestLog=new StringBuffer();
            paymentRequestLog.append("{" +
                    "\"sessionToken\": \""+sessionToken+"\"," +
                    "\"merchantId\": \""+mid+"\"," +
                    "\"merchantSiteId\": \""+merchantSiteId+"\"," +
                    "\"clientRequestId\": \""+trackingID+"\"," +
                    "\"clientUniqueId\": \"" + trackingID + "\"," +
                    "\"userTokenId\": \""+trackingID+"\"," +
                    "\"orderId\": \""+orderId+"\"," +
                    "\"timeStamp\": \""+timestamp+"\"," +
                    "\"checksum\": \""+checksum+"\",");
            if(!functions.isValueNull(PaRes))
            {
                paymentRequestLog.append("\"relatedTransactionId\": \"" + transactionDetailsVO.getPreviousTransactionId() + "\",");
            }
            paymentRequestLog.append("\"currency\": \""+transactionDetailsVO.getCurrency()+"\"," +
                    "\"amount\": \""+transactionDetailsVO.getAmount()+"\"," +
                    "\"transactionType\": \"Auth\"," +
                    "\"paymentOption\": {" +
                    "\"card\": {" +
                    "\"cardNumber\": \""+commCardDetailsVO.getCardNum()+"\"," +
                    "\"cardHolderName\": \""+cardHolderName+"\"," +
                    "\"expirationMonth\": \""+commCardDetailsVO.getExpMonth()+"\"," +
                    "\"expirationYear\": \""+commCardDetailsVO.getExpYear()+"\"," +
                    "\"CVV\": \""+commCardDetailsVO.getcVV()+"\"") ;
            if(functions.isValueNull(PaRes))
            {
                paymentRequestLog.append(",\"threeD\":{" +
                        "\"paResponse\":\""+PaRes+"\""+
                        "}");
            }else if("3Dv2".equalsIgnoreCase(commRequestVO.getAttemptThreeD()))
            {
                paymentRequestLog.append(",\"threeD\": {" +
                        "\"browserDetails\": {" +
                        "\"acceptHeader\": \""+deviceDetailsVO.getAcceptHeader()+"\"," +
                        "\"ip\": \"" + addressDetailsVO.getCardHolderIpAddress() + "\"," +
                        "\"javaEnabled\": \""+deviceDetailsVO.getBrowserJavaEnabled()+"\"," +
                        "\"javaScriptEnabled\": \""+deviceDetailsVO.getBrowserJavaEnabled()+"\"," +
                        "\"language\": \"" + deviceDetailsVO.getBrowserLanguage() + "\"," +
                        "\"colorDepth\": \"" + deviceDetailsVO.getBrowserColorDepth() + "\"," +
                        "\"screenHeight\": \"" + deviceDetailsVO.getBrowserScreenHeight() + "\"," +
                        "\"screenWidth\": \"" + deviceDetailsVO.getBrowserScreenWidth() + "\"," +
                        "\"timeZone\": \"" + deviceDetailsVO.getBrowserTimezoneOffset() + "\"," +
                        "\"userAgent\": \"" + deviceDetailsVO.getUser_Agent() + "\"" +
                        "}," +
                        "\"version\": \"2.1.0\"," +
                        "\"notificationURL\": \"" + termUrl + "\"," +
                        "\"platformType\": \"02\"," +
                        "\"methodCompletionInd\": \"Y\"" +
                        "}");
            }
            paymentRequestLog.append("}" +
                    "}," +
                    "\"billingAddress\": {" +
                    "\"firstName\": \""+firstname+"\"," +
                    "\"lastName\": \""+lastname+"\"," +
                    "\"address\": \""+street+"\"," +
                    "\"city\": \""+city+"\"," +
                    "\"state\": \"" + state + "\"," +
                    "\"zip\": \"" + zip + "\"," +
                    "\"country\": \""+country+"\"," +
                    "\"email\": \""+email+"\"" +
                    "}," +
                    "\"urlDetails\": {" +
                    "\"notificationUrl\": \"" + notifyUrl + "\"" +
                    "}," +
                    "\"deviceDetails\": {" +
                    "\"ipAddress\": \""+addressDetailsVO.getCardHolderIpAddress()+"\"" +
                    "}" +
                    "}");
            transactionLogger.error("processCommon3DSaleConfirmation payment Request---"+trackingID+"-->"+paymentRequestLog.toString());
            String response = SafeChargeV2Utils.doPostHTTPSURLConnectionClient(paymentUrl, paymentRequest.toString());
            transactionLogger.error("processCommon3DSaleConfirmation payment response---"+trackingID+"-->"+response);
            if (functions.isValueNull(response))
            {
                JSONObject responseJSON = new JSONObject(response);
                if (responseJSON.has("transactionStatus"))
                    transactionStatus = responseJSON.getString("transactionStatus");
                if (responseJSON.has("gwErrorCode"))
                    gwErrorCode = responseJSON.getString("gwErrorCode");
                if (responseJSON.has("gwErrorReason"))
                    gwErrorReason = responseJSON.getString("gwErrorReason");
                if (responseJSON.has("authCode"))
                    authCode = responseJSON.getString("authCode");
                if (responseJSON.has("transactionId"))
                    transactionId = responseJSON.getString("transactionId");
                if (responseJSON.has("errCode"))
                    errCode = responseJSON.getString("errCode");
                if (responseJSON.has("reason"))
                    reason = responseJSON.getString("reason");
                if(responseJSON.has("paymentOption") && responseJSON.getJSONObject("paymentOption")!=null && responseJSON.getJSONObject("paymentOption").has("userPaymentOptionId"))
                {
                    userPaymentOptionId=responseJSON.getJSONObject("paymentOption").getString("userPaymentOptionId");
                }
                if (responseJSON.has("paymentOption") && responseJSON.getJSONObject("paymentOption")!=null && responseJSON.getJSONObject("paymentOption").has("card"))
                {
                    JSONObject cardJson=responseJSON.getJSONObject("paymentOption").getJSONObject("card");
                    if(cardJson.has("threeD"))
                    {
                        JSONObject threeDJson = cardJson.getJSONObject("threeD");
                        if (threeDJson.has("eci"))
                            eci = threeDJson.getString("eci");
                        if (threeDJson.has("acsUrl"))
                            acsUrl = threeDJson.getString("acsUrl");
                        if (threeDJson.has("cReq"))
                            cReq = threeDJson.getString("cReq");
                    }
                }
            }
            boolean isUpdated=SafeChargeV2Utils.updateDetailTable(trackingID,orderId,authCode);
            transactionLogger.error("transactionStatus--->"+transactionStatus);
            if("APPROVED".equalsIgnoreCase(transactionStatus))
            {
                commResponseVO.setStatus("success");
                commResponseVO.setTransactionId(transactionId);
                commResponseVO.setResponseHashInfo(userPaymentOptionId);
                if(functions.isValueNull(reason))
                {
                    commResponseVO.setRemark(reason);
                    commResponseVO.setDescription(reason);
                }else if(functions.isValueNull(gwErrorReason))
                {
                    commResponseVO.setRemark(gwErrorReason);
                    commResponseVO.setDescription(gwErrorReason);
                }else
                {
                    commResponseVO.setRemark(transactionStatus);
                    commResponseVO.setDescription(transactionStatus);
                }
            }else  if("REDIRECT".equalsIgnoreCase(transactionStatus))
            {
                commResponseVO.setStatus("pending3DConfirmation");
                commResponseVO.setUrlFor3DRedirect(acsUrl);
                transactionLogger.error("cReq---->" + cReq);
                commResponseVO.setCreq(cReq);
                commResponseVO.setThreeDSSessionData(encryptedCVV);
                commResponseVO.setTerURL(termUrl);
                commResponseVO.setTransactionId(transactionId);
                return commResponseVO;
            }else {
                commResponseVO.setStatus("failed");
                if(functions.isValueNull(reason))
                {
                    commResponseVO.setRemark(reason);
                    commResponseVO.setDescription(reason);
                }else if(functions.isValueNull(gwErrorReason))
                {
                    commResponseVO.setRemark(gwErrorReason);
                    commResponseVO.setDescription(gwErrorReason);
                }else
                {
                    commResponseVO.setRemark(transactionStatus);
                    commResponseVO.setDescription(transactionStatus);
                }
            }
            commResponseVO.setEci(eci);
            commResponseVO.setAuthCode(authCode);
            if(!"0".equalsIgnoreCase(errCode))
                commResponseVO.setErrorCode(errCode);
            else if(functions.isValueNull(gwErrorCode))
                commResponseVO.setErrorCode(gwErrorCode);

        }
        catch (JSONException e)
        {
            transactionLogger.error("processCommon3DSaleConfirmation JSONException--trackingId--"+trackingID+"-->",e);
            PZExceptionHandler.raiseTechnicalViolationException(SafeChargeV2PaymentGateway.class.getName(), "processCommon3DSaleConfirmation()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error("Inside processRefund ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("YYYYMMddHHmmss");

        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mid = gatewayAccount.getMerchantId();
        String merchantSiteId=gatewayAccount.getFRAUD_FTP_USERNAME();
        String merchantSecretKey=gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest = gatewayAccount.isTest();

        String transactionId=commTransactionDetailsVO.getPreviousTransactionId();
        String amount=commTransactionDetailsVO.getAmount();
        String currency=commTransactionDetailsVO.getCurrency();
        String comment="";
        String clientUniqueId="R-"+trackingID;
        if(functions.isValueNull(commTransactionDetailsVO.getOrderDesc()))
            comment=commTransactionDetailsVO.getOrderDesc();
        String timestamp=simpleDateFormat.format(new Date());

        String refundUrl="";
        String notifyUrl="";
        String status="";
        String transactionStatus="";
        String authCode="";
        String errCode="";
        String errReason="";
        String gwErrorCode="";
        String gwErrorReason="";
        String responseTransactionId="";
        String reason="";
        if(isTest)
            refundUrl=RB.getString("TEST_REFUND_URL");
        else
            refundUrl=RB.getString("LIVE_REFUND_URL");

        notifyUrl=RB.getString("NOTIFY_URL");
        String checksum=SafeChargeV2Utils.generateChecksum(mid+merchantSiteId+clientUniqueId+amount+currency+transactionId+comment+notifyUrl+timestamp+merchantSecretKey);
        try
        {
            String refundRequest = "{" +
                    "\"merchantId\": \"" + mid + "\"," +
                    "\"merchantSiteId\": \"" + merchantSiteId + "\"," +
                    "\"clientUniqueId\": \"" + clientUniqueId + "\"," +
                    "\"amount\": \"" + amount + "\"," +
                    "\"currency\": \"" + currency + "\"," +
                    "\"relatedTransactionId\": \"" + transactionId + "\"," +
                    "\"comment\": \"" + comment + "\"," +
                    "\"urlDetails\": {" +
                    "\"notificationUrl\": \"" + notifyUrl + "\"" +
                    "}," +
                    "\"productId\":\"\"," +
                    "\"customData\":\"\"," +
                    "\"webMasterId\":\"\"," +
                    "\"timeStamp\": \"" + timestamp + "\"," +
                    "\"checksum\": \"" + checksum + "\"" +
                    "}";
            transactionLogger.error("Refund request for --" + trackingID + "-->" + refundRequest);
            String refundResponse = SafeChargeV2Utils.doPostHTTPSURLConnectionClient(refundUrl, refundRequest);
            transactionLogger.error("Refund response for --" + trackingID + "-->" + refundResponse);
            if (functions.isValueNull(refundResponse))
            {
                JSONObject refundResponseJSON=new JSONObject(refundResponse);
                if(refundResponseJSON.has("status"))
                    status=refundResponseJSON.getString("status");
                if(refundResponseJSON.has("transactionStatus"))
                    transactionStatus=refundResponseJSON.getString("transactionStatus");
                if(refundResponseJSON.has("authCode"))
                    authCode=refundResponseJSON.getString("authCode");
                if(refundResponseJSON.has("errCode"))
                    errCode=refundResponseJSON.getString("errCode");
                if(refundResponseJSON.has("errReason"))
                    errReason=refundResponseJSON.getString("errReason");
                if(refundResponseJSON.has("gwErrorCode"))
                    gwErrorCode=refundResponseJSON.getString("gwErrorCode");
                if(refundResponseJSON.has("gwErrorReason"))
                    gwErrorReason=refundResponseJSON.getString("gwErrorReason");
                if(refundResponseJSON.has("transactionId"))
                    responseTransactionId=refundResponseJSON.getString("transactionId");
                if(refundResponseJSON.has("reason"))
                    reason=refundResponseJSON.getString("reason");

                if("SUCCESS".equalsIgnoreCase(status) && "APPROVED".equalsIgnoreCase(transactionStatus))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("Refund Successful");
                    commResponseVO.setDescription("Refund Successful");
                }else
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setRemark("Refund failed");
                    commResponseVO.setDescription("Refund failed");
                }
                if(functions.isValueNull(responseTransactionId))
                    commResponseVO.setTransactionId(responseTransactionId);
                if(functions.isValueNull(errCode))
                    commResponseVO.setErrorCode(errCode);
                if(functions.isValueNull(gwErrorCode))
                    commResponseVO.setErrorCode(gwErrorCode);
                if(functions.isValueNull(errReason))
                {
                    commResponseVO.setRemark(errReason);
                    commResponseVO.setDescription(errReason);
                }else if(functions.isValueNull(gwErrorReason))
                {
                    commResponseVO.setRemark(gwErrorReason);
                    commResponseVO.setDescription(gwErrorReason);
                }else if(functions.isValueNull(reason))
                {
                    commResponseVO.setRemark(reason);
                    commResponseVO.setDescription(reason);
                }
                if(functions.isValueNull(authCode))
                {
                    commResponseVO.setAuthCode(authCode);
                }
            }else {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Transaction Failed");
                commResponseVO.setDescription("Transaction Failed");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("processRefund JSONException for--"+trackingID+"-->",e);
        }
        return commResponseVO;
    }
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {
        //Within 15 min with same sessionToken used while transaction
        transactionLogger.error("inside processQuery");
        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("YYYYMMddHHmmss");
        Functions functions = new Functions();
        String timestamp="";
        String checksum="";
        String sessionToken="";
        String sessionTokenStatus="";
        String errCode="";
        String transactionStatus="";
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mid = gatewayAccount.getMerchantId();
        String merchantSiteId=gatewayAccount.getFRAUD_FTP_USERNAME();
        String merchantSecretKey=gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest = gatewayAccount.isTest();

        String sessionTokenUrl="";
        String inquiryUrl="";
        if(isTest)
        {
            inquiryUrl = RB.getString("TEST_INQUIRY_URL");
        }
        else
        {
            inquiryUrl = RB.getString("TEST_INQUIRY_URL");
        }
        try
        {
            timestamp = simpleDateFormat.format(new Date());

            String inquiryRequest="{" +
                    "\"sessionToken\": \""+sessionToken+"\"" +
                    "}";
            transactionLogger.error("inquiryRequest---" + trackingID + "-->" + inquiryRequest);
            String inquiryResponse=SafeChargeV2Utils.doPostHTTPSURLConnectionClient(inquiryUrl,inquiryRequest);
            transactionLogger.error("inquiryRespone---" + trackingID + "-->" + inquiryResponse);
            if(functions.isValueNull(inquiryResponse))
            {
                JSONObject inquiryJSON=new JSONObject(inquiryResponse);
                if(inquiryJSON.has("transactionStatus"))
                    transactionStatus=inquiryJSON.getString("transactionStatus");
                if ("APPROVED".equalsIgnoreCase(transactionStatus))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else
                {
                    commResponseVO.setStatus("failed");
                }
                if(inquiryJSON.has("transactionId"))
                    commResponseVO.setTransactionId(inquiryJSON.getString("transactionId"));
                if(inquiryJSON.has("gwErrorReason"))
                {
                    commResponseVO.setRemark(inquiryJSON.getString("gwErrorReason"));
                    commResponseVO.setDescription(inquiryJSON.getString("gwErrorReason"));
                }
                if(inquiryJSON.has("transactionType"))
                    commResponseVO.setTransactionType(inquiryJSON.getString("transactionType"));
                if(inquiryJSON.has("gwErrorCode"))
                    commResponseVO.setErrorCode(inquiryJSON.getString("gwErrorCode"));
                if(inquiryJSON.has("authCode"))
                    commResponseVO.setAuthCode(inquiryJSON.getString("authCode"));
            }
            commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
            commResponseVO.setAmount(commTransactionDetailsVO.getAmount());
            commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
        }
        catch (JSONException e)
        {
            transactionLogger.error("processQuery JSONException for--" + trackingID + "-->", e);
        }

        return commResponseVO;
    }
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        transactionLogger.error("Inside processCapture ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("YYYYMMddHHmmss");
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mid = gatewayAccount.getMerchantId();
        String merchantSiteId = gatewayAccount.getFRAUD_FTP_USERNAME();
        String merchantSecretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest = gatewayAccount.isTest();
        String transactionId=commTransactionDetailsVO.getPreviousTransactionId();
        String amount=commTransactionDetailsVO.getAmount();
        String currency=commTransactionDetailsVO.getCurrency();
        String comment="";
        String clientUniqueId="C-"+trackingID;
        if(functions.isValueNull(commTransactionDetailsVO.getOrderDesc()))
            comment=commTransactionDetailsVO.getOrderDesc();
        String timestamp=simpleDateFormat.format(new Date());

        String notifyUrl="";
        String status="";
        String transactionStatus="";
        String authCode="";
        String errCode="";
        String errReason="";
        String gwErrorCode="";
        String gwErrorReason="";
        String responseTransactionId="";
        String reason="";

        String captureUrl = "";

        if (isTest)
        {
            captureUrl = RB.getString("TEST_CAPTURE_URL");
        }
        else
        {
            captureUrl = RB.getString("LIVE_CAPTURE_URL");
        }

        notifyUrl=RB.getString("NOTIFY_URL");
        String checksum=SafeChargeV2Utils.generateChecksum(mid+merchantSiteId+clientUniqueId+amount+currency+transactionId+comment+notifyUrl+timestamp+merchantSecretKey);
        try
        {
            String captureRequest = "{" +
                    "\"merchantId\": \"" + mid + "\"," +
                    "\"merchantSiteId\": \"" + merchantSiteId + "\"," +
                    "\"clientUniqueId\": \"" + clientUniqueId + "\"," +
                    "\"amount\": \"" + amount + "\"," +
                    "\"currency\": \"" + currency + "\"," +
                    "\"relatedTransactionId\": \"" + transactionId + "\"," +
                    "\"comment\": \"" + comment + "\"," +
                    "\"urlDetails\": {" +
                    "\"notificationUrl\": \"" + notifyUrl + "\"" +
                    "}," +
                    "\"productId\":\"\"," +
                    "\"customData\":\"\"," +
                    "\"webMasterId\":\"\"," +
                    "\"timeStamp\": \"" + timestamp + "\"," +
                    "\"checksum\": \"" + checksum + "\"" +
                    "}";
            transactionLogger.error("Capture request for --" + trackingID + "-->" + captureRequest);
            String captureResponse = SafeChargeV2Utils.doPostHTTPSURLConnectionClient(captureUrl, captureRequest);
            transactionLogger.error("Capture response for --" + trackingID + "-->" + captureResponse);
            if (functions.isValueNull(captureResponse))
            {
                JSONObject cancelResponseJSON=new JSONObject(captureResponse);
                if(cancelResponseJSON.has("status"))
                    status=cancelResponseJSON.getString("status");
                if(cancelResponseJSON.has("transactionStatus"))
                    transactionStatus=cancelResponseJSON.getString("transactionStatus");
                if(cancelResponseJSON.has("authCode"))
                    authCode=cancelResponseJSON.getString("authCode");
                if(cancelResponseJSON.has("errCode"))
                    errCode=cancelResponseJSON.getString("errCode");
                if(cancelResponseJSON.has("errReason"))
                    errReason=cancelResponseJSON.getString("errReason");
                if(cancelResponseJSON.has("gwErrorCode"))
                    gwErrorCode=cancelResponseJSON.getString("gwErrorCode");
                if(cancelResponseJSON.has("gwErrorReason"))
                    gwErrorReason=cancelResponseJSON.getString("gwErrorReason");
                if(cancelResponseJSON.has("transactionId"))
                    responseTransactionId=cancelResponseJSON.getString("transactionId");
                if (cancelResponseJSON.has("reason"))
                    reason = cancelResponseJSON.getString("reason");

                if("SUCCESS".equalsIgnoreCase(status) && "APPROVED".equalsIgnoreCase(transactionStatus))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("Capture successful");
                    commResponseVO.setDescription("Capture successful");
                }else
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setRemark("Capture failed");
                    commResponseVO.setDescription("Capture failed");

                }
                if(functions.isValueNull(errReason))
                {
                    commResponseVO.setRemark(errReason);
                    commResponseVO.setDescription(errReason);
                }else if(functions.isValueNull(gwErrorReason))
                {
                    commResponseVO.setRemark(gwErrorReason);
                    commResponseVO.setDescription(gwErrorReason);
                }else if(functions.isValueNull(reason))
                {
                    commResponseVO.setRemark(reason);
                    commResponseVO.setDescription(reason);
                }
                if(functions.isValueNull(responseTransactionId))
                    commResponseVO.setTransactionId(responseTransactionId);
                if(functions.isValueNull(errCode))
                    commResponseVO.setErrorCode(errCode);
                if(functions.isValueNull(gwErrorCode))
                    commResponseVO.setErrorCode(gwErrorCode);
                if(functions.isValueNull(authCode))
                {
                    commResponseVO.setAuthCode(authCode);
                }
            }else {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Transaction Failed");
                commResponseVO.setDescription("Transaction Failed");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("processCapture JSONException for--"+trackingID+"-->",e);
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("processCapture PZTechnicalViolationException for--" + trackingID + "-->", e);
        }
        return commResponseVO;
    }
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        transactionLogger.error("Inside processVoid ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("YYYYMMddHHmmss");
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mid = gatewayAccount.getMerchantId();
        String merchantSiteId = gatewayAccount.getFRAUD_FTP_USERNAME();
        String merchantSecretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest = gatewayAccount.isTest();
        String transactionId=commTransactionDetailsVO.getPreviousTransactionId();
        String amount=commTransactionDetailsVO.getAmount();
        String currency=commTransactionDetailsVO.getCurrency();
        String comment="";
        String clientUniqueId="V-"+trackingID;
        if(functions.isValueNull(commTransactionDetailsVO.getOrderDesc()))
            comment=commTransactionDetailsVO.getOrderDesc();
        String timestamp=simpleDateFormat.format(new Date());

        String notifyUrl="";
        String status="";
        String transactionStatus="";
        String authCode="";
        String errCode="";
        String errReason="";
        String gwErrorCode="";
        String gwErrorReason="";
        String responseTransactionId="";
        String reason="";

        String cancelUrl = "";

        if (isTest)
        {
            cancelUrl = RB.getString("TEST_VOID_URL");
        }
        else
        {
            cancelUrl = RB.getString("LIVE_VOID_URL");
        }

        notifyUrl=RB.getString("NOTIFY_URL");
        String checksum=SafeChargeV2Utils.generateChecksum(mid+merchantSiteId+clientUniqueId+amount+currency+transactionId+comment+notifyUrl+timestamp+merchantSecretKey);
        try
        {
            String cancelRequest = "{" +
                    "\"merchantId\": \"" + mid + "\"," +
                    "\"merchantSiteId\": \"" + merchantSiteId + "\"," +
                    "\"clientUniqueId\": \"" + clientUniqueId + "\"," +
                    "\"amount\": \"" + amount + "\"," +
                    "\"currency\": \"" + currency + "\"," +
                    "\"relatedTransactionId\": \"" + transactionId + "\"," +
                    "\"comment\": \"" + comment + "\"," +
                    "\"urlDetails\": {" +
                    "\"notificationUrl\": \"" + notifyUrl + "\"" +
                    "}," +
                    "\"productId\":\"\"," +
                    "\"customData\":\"\"," +
                    "\"webMasterId\":\"\"," +
                    "\"timeStamp\": \"" + timestamp + "\"," +
                    "\"checksum\": \"" + checksum + "\"" +
                    "}";
            transactionLogger.error("Cancel request for --" + trackingID + "-->" + cancelRequest);
            String cancelResponse = SafeChargeV2Utils.doPostHTTPSURLConnectionClient(cancelUrl, cancelRequest);
            transactionLogger.error("Cancel response for --" + trackingID + "-->" + cancelResponse);
            if (functions.isValueNull(cancelResponse))
            {
                JSONObject cancelResponseJSON=new JSONObject(cancelResponse);
                if(cancelResponseJSON.has("status"))
                    status=cancelResponseJSON.getString("status");
                if(cancelResponseJSON.has("transactionStatus"))
                    transactionStatus=cancelResponseJSON.getString("transactionStatus");
                if(cancelResponseJSON.has("authCode"))
                    authCode=cancelResponseJSON.getString("authCode");
                if(cancelResponseJSON.has("errCode"))
                    errCode=cancelResponseJSON.getString("errCode");
                if(cancelResponseJSON.has("errReason"))
                    errReason=cancelResponseJSON.getString("errReason");
                if(cancelResponseJSON.has("gwErrorCode"))
                    gwErrorCode=cancelResponseJSON.getString("gwErrorCode");
                if(cancelResponseJSON.has("gwErrorReason"))
                    gwErrorReason=cancelResponseJSON.getString("gwErrorReason");
                if(cancelResponseJSON.has("transactionId"))
                    responseTransactionId=cancelResponseJSON.getString("transactionId");
                if(cancelResponseJSON.has("reason"))
                    reason=cancelResponseJSON.getString("reason");


                if("SUCCESS".equalsIgnoreCase(status) && "APPROVED".equalsIgnoreCase(transactionStatus))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("Cancel successful");
                    commResponseVO.setDescription("Cancel successful");

                }else
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setRemark("Cancel failed");
                    commResponseVO.setDescription("Cancel failed");
                }
                if(functions.isValueNull(errReason))
                {
                    commResponseVO.setRemark(errReason);
                    commResponseVO.setDescription(errReason);
                }else if(functions.isValueNull(gwErrorReason))
                {
                    commResponseVO.setRemark(gwErrorReason);
                    commResponseVO.setDescription(gwErrorReason);
                }else if(functions.isValueNull(reason))
                {
                    commResponseVO.setRemark(reason);
                    commResponseVO.setDescription(reason);
                }
                if(functions.isValueNull(responseTransactionId))
                    commResponseVO.setTransactionId(responseTransactionId);
                if(functions.isValueNull(errCode))
                    commResponseVO.setErrorCode(errCode);
                if(functions.isValueNull(gwErrorCode))
                    commResponseVO.setErrorCode(gwErrorCode);
                if(functions.isValueNull(authCode))
                {
                    commResponseVO.setAuthCode(authCode);
                }
            }else {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Transaction Failed");
                commResponseVO.setDescription("Transaction Failed");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("processVoid JSONException for--"+trackingID+"-->",e);
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("processVoid PZTechnicalViolationException for--" + trackingID + "-->", e);
        }
        return commResponseVO;
    }
    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO)
    {
        transactionLogger.error("--- Inside IlixiumPaymentGateway Payout ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("YYYYMMddHHmmss");
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mid = gatewayAccount.getMerchantId();
        String merchantSiteId = gatewayAccount.getFRAUD_FTP_USERNAME();
        String merchantSecretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest = gatewayAccount.isTest();
        String transactionId="";
        String amount=commTransactionDetailsVO.getAmount();
        String currency=commTransactionDetailsVO.getCurrency();
        String previousTransactionId=commTransactionDetailsVO.getPreviousTransactionId();
        String comment="";
        String clientUniqueId=trackingID;
        if(functions.isValueNull(commTransactionDetailsVO.getOrderDesc()))
            comment=commTransactionDetailsVO.getOrderDesc();
        String timestamp=simpleDateFormat.format(new Date());
        String payoutUrl = "";
        String notifyUrl="";
        String ipAddress=commAddressDetailsVO.getIp();
        String userPaymentOptionId=commRequestVO.getUniqueId();
        String transactionStatus="";
        String userPaymentOptionIdRes="";
        String errCode="";
        String errReason="";
        String gwErrorCode="";
        String gwErrorReason="";
        String reason="";

        if (isTest)
        {
            payoutUrl = RB.getString("TEST_PAYOUT_URL");
        }
        else
        {
            payoutUrl = RB.getString("LIVE_PAYOUT_URL");
        }
        notifyUrl=RB.getString("NOTIFY_URL");
        String checksum=SafeChargeV2Utils.generateChecksum(mid+merchantSiteId+trackingID+amount+currency+timestamp+merchantSecretKey);
        try
        {
            String payoutRequest="{" +
                    "\"merchantId\": \""+mid+"\"," +
                    "\"merchantSiteId\": \""+merchantSiteId+"\"," +
                    "\"clientUniqueId\": \""+clientUniqueId+"\"," +
                    "\"clientRequestId\": \""+trackingID+"\"," +
                    "\"userTokenId\": \""+previousTransactionId+"\"," +
                    "\"amount\": \""+amount+"\"," +
                    "\"currency\": \""+currency+"\"," +
                    "\"deviceDetails\":{ " +
                    "\"ipAddress\":\""+ipAddress+"\"" +
                    "}," +
                    "\"userPaymentOption\":{" +
                    "\"userPaymentOptionId\":\""+userPaymentOptionId+"\"" +
                    "}," +
                    "\"urlDetails\": {" +
                    "\"notificationUrl\": \""+notifyUrl+"\"" +
                    "}," +
                    "\"timeStamp\": \""+timestamp+"\"," +
                    "\"checksum\": \""+checksum+"\"" +
                    "}";
            transactionLogger.error("Payout request for --" + trackingID + "-->" + payoutRequest);
            String payoutResponse = SafeChargeV2Utils.doPostHTTPSURLConnectionClient(payoutUrl, payoutRequest);
            transactionLogger.error("Payout response for --" + trackingID + "-->" + payoutResponse);
            if (functions.isValueNull(payoutResponse))
            {
                JSONObject payoutResponseJSON = new JSONObject(payoutResponse);
                if(payoutResponseJSON.has("transactionStatus"))
                    transactionStatus=payoutResponseJSON.getString("transactionStatus");
                if(payoutResponseJSON.has("transactionId"))
                    transactionId=payoutResponseJSON.getString("transactionId");
                if(payoutResponseJSON.has("userPaymentOptionId"))
                    userPaymentOptionIdRes=payoutResponseJSON.getString("userPaymentOptionId");
                if(payoutResponseJSON.has("errCode"))
                    errCode=payoutResponseJSON.getString("errCode");
                if(payoutResponseJSON.has("errReason"))
                    errReason=payoutResponseJSON.getString("errReason");
                if(payoutResponseJSON.has("gwErrorCode"))
                    gwErrorCode=payoutResponseJSON.getString("gwErrorCode");
                if(payoutResponseJSON.has("gwErrorReason"))
                    gwErrorReason=payoutResponseJSON.getString("gwErrorReason");
                if(payoutResponseJSON.has("reason"))
                    reason=payoutResponseJSON.getString("reason");

                if("APPROVED".equalsIgnoreCase(transactionStatus))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("Payout successful");
                    commResponseVO.setDescription("Payout successful");
                }else
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setRemark("Payout failed");
                    commResponseVO.setDescription("Payout failed");
                }
                commResponseVO.setTransactionId(transactionId);
                commResponseVO.setResponseHashInfo(userPaymentOptionIdRes);
                if(functions.isValueNull(errCode))
                    commResponseVO.setErrorCode(errCode);
                if(functions.isValueNull(gwErrorCode))
                    commResponseVO.setErrorCode(gwErrorCode);
                if(functions.isValueNull(errReason))
                {
                    commResponseVO.setRemark(errReason);
                    commResponseVO.setDescription(errReason);
                }else if(functions.isValueNull(gwErrorReason))
                {
                    commResponseVO.setRemark(gwErrorReason);
                    commResponseVO.setDescription(gwErrorReason);
                }else if(functions.isValueNull(reason))
                {
                    commResponseVO.setRemark(reason);
                    commResponseVO.setDescription(reason);
                }
            }
            else {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Transaction Failed");
                commResponseVO.setDescription("Transaction Failed");
            }
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("processPayout PZTechnicalViolationException for--"+trackingID+"-->",e);
        }
        catch (JSONException e)
        {
            transactionLogger.error("processPayout JSONException for--" + trackingID + "-->",e);
        }

        return commResponseVO;
    }

    /*public static void main(String[] args)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMddHHmmss");
        Functions functions = new Functions();
        String timestamp = simpleDateFormat.format(new Date());
        String mid = "6108514500565649941";
        String merchantSiteId = "207298";
        String merchantSecretKey = "E8a0edmYH2hrcSNS9H5LgbLbedl3suvQFnUYFiD3OaMBQ6bvOZOATmu5h8kFIcfS";
        String trackingID = "174771";
        String sessionTokenUrl = "https://ppp-test.safecharge.com/ppp/api/v1/getSessionToken.do";
        String inquiryUrl = "https://ppp-test.safecharge.com/ppp/api/v1/getPaymentStatus.do";
        String sessionToken = "";
        String sessionTokenStatus = "";
        String errCode = "";
        String checksum = SafeChargeV2Utils.generateChecksum(mid + merchantSiteId + trackingID + timestamp + merchantSecretKey);
        try
        {
            String sessionTokenRequest = "{" +
                    "\"merchantId\": \"" + mid + "\"," +
                    "\"merchantSiteId\": \"" + merchantSiteId + "\"," +
                    "\"clientRequestId\": \"" + trackingID + "\"," +
                    "\"timeStamp\": \"" + timestamp + "\"," +
                    "\"checksum\": \"" + checksum + "\"" +
                    "}";
            System.out.println("sessionTokenRequest---" + trackingID + "-->" + sessionTokenRequest);
            String sessionTokenResponse = SafeChargeV2Utils.doPostHTTPSURLConnectionClient(sessionTokenUrl, sessionTokenRequest);
            System.out.println("sessionTokenResponse---" + trackingID + "-->" + sessionTokenResponse);
            if (functions.isValueNull(sessionTokenResponse))
            {
                JSONObject sessionTokenResJSON = new JSONObject(sessionTokenResponse);
                if (sessionTokenResJSON.has("sessionToken"))
                    sessionToken = sessionTokenResJSON.getString("sessionToken");
                if (sessionTokenResJSON.has("status"))
                    sessionTokenStatus = sessionTokenResJSON.getString("status");
                if (sessionTokenResJSON.has("sessionToken"))
                    errCode = sessionTokenResJSON.getString("errCode");
            }
            if ("SUCCESS".equalsIgnoreCase(sessionTokenStatus))
            {
                String inquiryRequest = "{" +
                        "\"sessionToken\": \"" + "ca60e79f-e256-4548-9a99-2da48f519813" + "\"" +
                        "}";
                System.out.println("inquiryRequest---" + trackingID + "-->" + inquiryRequest);
                String inquiryResponse = SafeChargeV2Utils.doPostHTTPSURLConnectionClient(inquiryUrl, inquiryRequest);
                System.out.println("inquiryRespone---" + trackingID + "-->" + inquiryResponse);
            }
        }catch(PZTechnicalViolationException e)
        {
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }*/
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
