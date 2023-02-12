package com.payment.endeavourmpi;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;

import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by Admin on 9/2/2020.
 */
public class EndeavourMPIV2Gateway
{
    public static final String GATEWAY_TYPE = "EndeavourMPIV2Gateway";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.endeavourmpiv2gateway");
    private static TransactionLogger transactionLogger = new TransactionLogger(EndeavourMPIV2Gateway.class.getName());

    public EnrollmentResponseVO processEnrollment(EnrollmentRequestVO enrollmentRequestVO) throws PZTechnicalViolationException
    {
        EnrollmentResponseVO enrollmentResponseVO=new EnrollmentResponseVO();
        EndeavourMPIGateway endeavourMPIGateway=new EndeavourMPIGateway();
        Functions functions=new Functions();
        String trackingId=enrollmentRequestVO.getTrackid();
        String threeDSServerTransID="";
        String enrolled="";
        String messagetype="";
        String errorCode="";
        String errorDescription="";
        String dsTransID="";
        String acsTransID="";
        String acsReferenceNumber="";
        String acsOperatorID="";
        String dsReferenceNumber="";
        String transStatus="";
        String authenticationType="";
        String acsChallengeMandated="";
        String acsURL="";
        String eci="";
        String authenticationValue="";
        String userAgent= "";
        String language= "";
        String colorDepth= "";
        String height= "";
        String width= "";
        String timezoneOffset= "";
        String browserJavaEnabled= "";
        String acceptHeader= "";
        String browserIP= "";
        String deviceChannel= "02";
        boolean isBrowserDetailsFound=false;
        if(functions.isValueNull(enrollmentRequestVO.getBrowserIp()))
            browserIP=enrollmentRequestVO.getBrowserIp();
        acceptHeader=enrollmentRequestVO.getAcceptHeader();
        if(functions.isValueNull(enrollmentRequestVO.getFingerprint()))
        {
            HashMap fingerPrintMap=functions.getFingerPrintMap(enrollmentRequestVO.getFingerprint());
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
                isBrowserDetailsFound=true;
            }
            catch (JSONException e)
            {
                transactionLogger.error("FIngerPrint JSONException---trackingId--" + trackingId + "--", e);
            }
        }else if(functions.isValueNull(enrollmentRequestVO.getUseragent()) && functions.isValueNull(enrollmentRequestVO.getAcceptHeader()) && functions.isValueNull(enrollmentRequestVO.getBrowserLanguage()) && functions.isValueNull(enrollmentRequestVO.getBrowserColorDepth()) && functions.isValueNull(enrollmentRequestVO.getBrowserTimezoneOffset()) && functions.isValueNull(enrollmentRequestVO.getBrowserScreenHeight()) && functions.isValueNull(enrollmentRequestVO.getBrowserScreenWidth()))
        {
            userAgent=enrollmentRequestVO.getUseragent();
            acceptHeader=enrollmentRequestVO.getAcceptHeader();
            language=enrollmentRequestVO.getBrowserLanguage();
            colorDepth=enrollmentRequestVO.getBrowserColorDepth();
            timezoneOffset=enrollmentRequestVO.getBrowserTimezoneOffset();
            height=enrollmentRequestVO.getBrowserScreenHeight();
            width=enrollmentRequestVO.getBrowserScreenWidth();
            isBrowserDetailsFound=true;
            deviceChannel="02";
        }else
        {
            if(functions.isValueNull(enrollmentRequestVO.getUseragent()))
                userAgent=enrollmentRequestVO.getUseragent();
            else
                userAgent="Mozilla/5.0 (Android; Mobile; rv:13.0) Gecko/13.0 Firefox/13.0";
            if(functions.isValueNull(enrollmentRequestVO.getAcceptHeader()))
                acceptHeader=enrollmentRequestVO.getAcceptHeader();
            else
                acceptHeader="text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8";
            if(functions.isValueNull(enrollmentRequestVO.getBrowserLanguage()))
                language=enrollmentRequestVO.getBrowserLanguage();
            else
                language="en-US";
            if(functions.isValueNull(enrollmentRequestVO.getBrowserColorDepth()))
                colorDepth=enrollmentRequestVO.getBrowserColorDepth();
            else
                colorDepth="24";
            if(functions.isValueNull(enrollmentRequestVO.getBrowserTimezoneOffset()))
                timezoneOffset=enrollmentRequestVO.getBrowserTimezoneOffset();
            else
                timezoneOffset="5";
            if(functions.isValueNull(enrollmentRequestVO.getBrowserScreenHeight()))
                height=enrollmentRequestVO.getBrowserScreenHeight();
            else
                height="939";
            if(functions.isValueNull(enrollmentRequestVO.getBrowserScreenWidth()))
                width=enrollmentRequestVO.getBrowserScreenWidth();
            else
                width="1255";
            isBrowserDetailsFound=true;
            deviceChannel="02";
        }
        if(functions.isValueNull(enrollmentRequestVO.getBrowserJavaEnabled()))
            browserJavaEnabled=enrollmentRequestVO.getBrowserJavaEnabled();
        else
            browserJavaEnabled="true";


        String vrequrl=RB.getString("VERIFICTION_URL");
        String arequrl=RB.getString("AUTHENTICATION_URL");
        try
        {
            //Verification Request
            String verificationRequest = "{\"api\":{\"version\":\"2.1.0.1.0\",\"trackid\":\"" + trackingId + "\",\"compressed\":false,\"merchantidentifier\":\"" + enrollmentRequestVO.getMid() + "\"},\"messagetype\":\"VReq\",\"message\":\"{\\\"acctNumber\\\":\\\"" + enrollmentRequestVO.getPan() + "\\\",\\\"acquirerMerchantID\\\":\\\"" + enrollmentRequestVO.getAcquirerMerchantID() + "\\\",\\\"messageType\\\":\\\"VReq\\\",\\\"messageVersion\\\":\\\"2.1.0\\\"}\"\n}";
            String verificationRequestLog = "{\"api\":{\"version\":\"2.1.0.1.0\",\"trackid\":\"" + trackingId + "\",\"compressed\":false,\"merchantidentifier\":\"" + enrollmentRequestVO.getMid() + "\"},\"messagetype\":\"VReq\",\"message\":\"{\\\"acctNumber\\\":\\\"" + functions.maskingPan(enrollmentRequestVO.getPan()) + "\\\",\\\"acquirerMerchantID\\\":\\\"" + enrollmentRequestVO.getAcquirerMerchantID() + "\\\",\\\"messageType\\\":\\\"VReq\\\",\\\"messageVersion\\\":\\\"2.1.0\\\"}\"\n}";

            transactionLogger.error("VReq Request---trackingId---" + trackingId + "-->" + verificationRequestLog);
            String verficationResponse = EndeavourMPIUtils.doPostHTTPSURLConnection(vrequrl, verificationRequest);
            transactionLogger.error("VRes Response---trackingId---" + trackingId + "-->" + verficationResponse.replaceAll(enrollmentRequestVO.getPan(),functions.maskingPan(enrollmentRequestVO.getPan())));
            //Verification Response

            if (functions.isValueNull(verficationResponse))
            {
                JSONObject vResJSON = new JSONObject(verficationResponse);
                if (vResJSON.has("message"))
                {
                    String message = vResJSON.getString("message");
                    JSONObject messageJson = new JSONObject(message);
                    if (messageJson.has("threeDSServerTransID"))
                        threeDSServerTransID = messageJson.getString("threeDSServerTransID");
                    if (messageJson.has("enrolled"))
                        enrolled = messageJson.getString("enrolled");
                    if(messageJson.has("messageType"))
                        messagetype=messageJson.getString("messageType");
                    if(messageJson.has("errorCode"))
                        errorCode=messageJson.getString("errorCode");
                    if(messageJson.has("errorDescription"))
                        errorDescription=messageJson.getString("errorDescription");

                }
                if("Erro".equalsIgnoreCase(messagetype))
                {
                    transactionLogger.error("===Error in Vreq===");
                    enrollmentResponseVO=endeavourMPIGateway.processEnrollment(enrollmentRequestVO);
                    return enrollmentResponseVO;
                }
                if("N".equalsIgnoreCase(enrolled))
                {
                    transactionLogger.error("--3D2 Not Enrolled--");
                    enrollmentResponseVO=endeavourMPIGateway.processEnrollment(enrollmentRequestVO);
                    // Call old MPI method - fallback to version 1
                    return enrollmentResponseVO;
                }
                StringBuffer aReq=new StringBuffer();
                aReq.append("{" +
                        "\"api\":{" +
                        "\"version\":\"2.1.0.1.0\"," +
                        "\"trackid\":\""+trackingId+"\"," +
                        "\"compressed\":false," +
                        "\"merchantidentifier\":\""+enrollmentRequestVO.getMid()+"\" }," +
                        "\"messagetype\":\"AReq\","+
                        "\"message\": \"{\\\"messageType\\\":\\\"AReq\\\",\\\"deviceChannel\\\":\\\""+deviceChannel+"\\\",\\\"messageVersion\\\":\\\"2.1.0\\\",\\\"threeDSCompInd\\\":\\\"U\\\",\\\"threeDSRequestorAuthenticationInd\\\":\\\"01\\\",\\\"threeDSServerTransID\\\":\\\""+threeDSServerTransID+"\\\"");
                if(functions.isValueNull(browserIP))
                    aReq.append(",\\\"browserIP\\\":\\\""+browserIP+"\\\"");
                if(isBrowserDetailsFound){
                    aReq.append(",\\\"browserAcceptHeader\\\":\\\""+acceptHeader+"\\\",\\\"browserJavaEnabled\\\":"+browserJavaEnabled+",\\\"browserLanguage\\\":\\\""+language+"\\\",\\\"browserColorDepth\\\":\\\""+colorDepth+"\\\",\\\"browserScreenHeight\\\":\\\""+height+"\\\",\\\"browserScreenWidth\\\":\\\""+width+"\\\",\\\"browserTZ\\\":\\\""+timezoneOffset+"\\\",\\\"browserUserAgent\\\":\\\""+userAgent+"\\\",");
                }
                aReq.append("\\\"notificationURL\\\":\\\""+enrollmentRequestVO.getHostUrl()+trackingId+"\\\",\\\"cardExpiryDate\\\":\\\""+enrollmentRequestVO.getExpiry()+"\\\",\\\"acctNumber\\\":\\\""+enrollmentRequestVO.getPan()+"\\\",\\\"merchantCountryCode\\\":\\\""+enrollmentRequestVO.getMerchantCountry()+"\\\",\\\"messageCategory\\\":\\\"01\\\",\\\"purchaseAmount\\\":\\\""+enrollmentRequestVO.getAmount()+"\\\",\\\"purchaseCurrency\\\":\\\""+enrollmentRequestVO.getCurrency()+"\\\",\\\"CardholderName\\\":\\\""+enrollmentRequestVO.getName()+"\\\",\\\"transType\\\":\\\"01\\\"}\""+
                        "}");

                StringBuffer aReqLog=new StringBuffer();
                aReqLog.append("{" +
                        "\"api\":{" +
                        "\"version\":\"2.1.0.1.0\"," +
                        "\"trackid\":\""+trackingId+"\"," +
                        "\"compressed\":false," +
                        "\"merchantidentifier\":\""+enrollmentRequestVO.getMid()+"\" }," +
                        "\"messagetype\":\"AReq\","+
                        "\"message\": \"{\\\"messageType\\\":\\\"AReq\\\",\\\"deviceChannel\\\":\\\""+deviceChannel+"\\\",\\\"messageVersion\\\":\\\"2.1.0\\\",\\\"threeDSCompInd\\\":\\\"U\\\",\\\"threeDSRequestorAuthenticationInd\\\":\\\"01\\\",\\\"threeDSServerTransID\\\":\\\""+threeDSServerTransID+"\\\"");
                if(functions.isValueNull(browserIP))
                    aReqLog.append(",\\\"browserIP\\\":\\\""+browserIP+"\\\"");
                if(isBrowserDetailsFound){
                    aReqLog.append(",\\\"browserAcceptHeader\\\":\\\""+acceptHeader+"\\\",\\\"browserJavaEnabled\\\":"+browserJavaEnabled+",\\\"browserLanguage\\\":\\\""+language+"\\\",\\\"browserColorDepth\\\":\\\""+colorDepth+"\\\",\\\"browserScreenHeight\\\":\\\""+height+"\\\",\\\"browserScreenWidth\\\":\\\""+width+"\\\",\\\"browserTZ\\\":\\\""+timezoneOffset+"\\\",\\\"browserUserAgent\\\":\\\""+userAgent+"\\\",");
                }
                aReqLog.append("\\\"notificationURL\\\":\\\""+enrollmentRequestVO.getHostUrl()+trackingId+"\\\",\\\"cardExpiryDate\\\":\\\""+functions.maskingNumber(enrollmentRequestVO.getExpiry())+"\\\",\\\"acctNumber\\\":\\\""+functions.maskingPan(enrollmentRequestVO.getPan())+"\\\",\\\"merchantCountryCode\\\":\\\""+enrollmentRequestVO.getMerchantCountry()+"\\\",\\\"messageCategory\\\":\\\"01\\\",\\\"purchaseAmount\\\":\\\""+enrollmentRequestVO.getAmount()+"\\\",\\\"purchaseCurrency\\\":\\\""+enrollmentRequestVO.getCurrency()+"\\\",\\\"CardholderName\\\":\\\""+enrollmentRequestVO.getName()+"\\\",\\\"transType\\\":\\\"01\\\"}\""+
                        "}");
                transactionLogger.error("Areq Request--trackingId---" + trackingId + "--->" + aReqLog);
                String aRes= "";
                aRes = EndeavourMPIUtils.doPostHTTPSURLConnection(arequrl, aReq.toString());
                transactionLogger.error("Ares Response--trackingId---"+trackingId+"--->"+aRes);
                if(functions.isValueNull(aRes))
                {
                    JSONObject aResJSON = new JSONObject(aRes);
                    if(aResJSON.has("message"))
                    {
                        String message=aResJSON.getString("message");
                        JSONObject messageJSON=new JSONObject(message);
                        if(messageJSON.has("dsTransID"))
                            dsTransID=messageJSON.getString("dsTransID");
                        if(messageJSON.has("acsTransID"))
                            acsTransID=messageJSON.getString("acsTransID");
                        if(messageJSON.has("acsReferenceNumber"))
                            acsReferenceNumber=messageJSON.getString("acsReferenceNumber");
                        if(messageJSON.has("acsOperatorID"))
                            acsOperatorID=messageJSON.getString("acsOperatorID");
                        if(messageJSON.has("dsReferenceNumber"))
                            dsReferenceNumber=messageJSON.getString("dsReferenceNumber");
                        if(messageJSON.has("transStatus"))
                            transStatus=messageJSON.getString("transStatus");
                        if(messageJSON.has("transStatus"))
                            transStatus=messageJSON.getString("transStatus");
                        if(messageJSON.has("authenticationType"))
                            authenticationType=messageJSON.getString("authenticationType");
                        if(messageJSON.has("acsChallengeMandated"))
                            acsChallengeMandated=messageJSON.getString("acsChallengeMandated");
                        if(messageJSON.has("acsURL"))
                            acsURL=messageJSON.getString("acsURL");
                        if (messageJSON.has("eci"))
                            eci=messageJSON.getString("eci");
                        if(messageJSON.has("authenticationValue"))
                            authenticationValue=messageJSON.getString("authenticationValue");
                        if(messageJSON.has("messageType"))
                            messagetype= messageJSON.getString("messageType");
                        if(messageJSON.has("errorCode"))
                            errorCode=messageJSON.getString("errorCode");
                        if(messageJSON.has("errorDescription"))
                            errorDescription=messageJSON.getString("errorDescription");

                    }
                    if("Erro".equalsIgnoreCase(messagetype))
                    {
                        transactionLogger.error("====Error in Areq====");
                        enrollmentResponseVO=endeavourMPIGateway.processEnrollment(enrollmentRequestVO);
                        return enrollmentResponseVO;
                    }

                    String cReq="{\"messageVersion\":\"2.1.0\",\"messageType\":\"CReq\",\"threeDSServerTransID\":\""+threeDSServerTransID+"\",\"acsTransID\":\""+acsTransID+"\",\"challengeWindowSize\":\"04\"}";
                    String creqjson= Base64.encodeBase64String(cReq.getBytes());
                    String threeDSSessionData = trackingId;

                    enrollmentResponseVO.setAcsUrl(acsURL);
                    enrollmentResponseVO.setCreq(creqjson);
                    enrollmentResponseVO.setThreeDSSessionData(threeDSSessionData);
                    enrollmentResponseVO.setAvr(transStatus);
                    enrollmentResponseVO.setEci(eci);
                    enrollmentResponseVO.setCAVV(authenticationValue);
                    enrollmentResponseVO.set_20BytesBinaryCAVVBytes(com.directi.pg.Base64.decode(authenticationValue));
                    enrollmentResponseVO.set_20BytesBinaryCAVV(new String(com.directi.pg.Base64.decode(authenticationValue)));
                    if(functions.isValueNull(dsTransID))
                    {
                        enrollmentResponseVO.setXID(dsTransID.substring(0, 20));
                        enrollmentResponseVO.set_20BytesBinaryXID(dsTransID.substring(0, 20));
                        enrollmentResponseVO.set_20BytesBinaryXIDBytes(dsTransID.substring(0, 20).getBytes());
                    }
                    enrollmentResponseVO.setDsTransId(dsTransID);
                    if("C".equalsIgnoreCase(transStatus))
                        enrollmentResponseVO.setResult("Enrolled");
                    else if("Y".equalsIgnoreCase(transStatus) || "N".equalsIgnoreCase(transStatus) || "R".equalsIgnoreCase(transStatus) || "A".equalsIgnoreCase(transStatus))
                        enrollmentResponseVO.setResult("Frictionless");
                }
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---trackingId--"+trackingId+"---",e);
            PZExceptionHandler.raiseTechnicalViolationException(EndeavourMPIV2Gateway.class.getName(), "processEnrollment()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return enrollmentResponseVO;
    }
    public EnrollmentResponseVO processVerification(EnrollmentRequestVO enrollmentRequestVO) throws PZTechnicalViolationException
    {
        EnrollmentResponseVO enrollmentResponseVO=new EnrollmentResponseVO();
        EndeavourMPIGateway endeavourMPIGateway=new EndeavourMPIGateway();
        Functions functions=new Functions();
        String trackingId=enrollmentRequestVO.getTrackid();
        String threeDSServerTransID="";
        String enrolled="";
        String messagetype="";
        String errorCode="";
        String errorDescription="";
        String dsTransID="";
        String acsTransID="";
        String acsReferenceNumber="";
        String acsOperatorID="";
        String dsReferenceNumber="";
        String transStatus="";
        String authenticationType="";
        String acsChallengeMandated="";

        String vrequrl=RB.getString("VERIFICTION_URL");
        try
        {
            //Verification Request
            String verificationRequest = "{\"api\":{\"version\":\"2.1.0.1.0\",\"trackid\":\"" + trackingId + "\",\"compressed\":false,\"merchantidentifier\":\"" + enrollmentRequestVO.getMid() + "\"},\"messagetype\":\"VReq\",\"message\":\"{\\\"acctNumber\\\":\\\"" + enrollmentRequestVO.getPan() + "\\\",\\\"acquirerMerchantID\\\":\\\"" + enrollmentRequestVO.getAcquirerMerchantID() + "\\\",\\\"messageType\\\":\\\"VReq\\\",\\\"messageVersion\\\":\\\"2.1.0\\\"}\"\n}";
            String verificationRequestLog = "{\"api\":{\"version\":\"2.1.0.1.0\",\"trackid\":\"" + trackingId + "\",\"compressed\":false,\"merchantidentifier\":\"" + enrollmentRequestVO.getMid() + "\"},\"messagetype\":\"VReq\",\"message\":\"{\\\"acctNumber\\\":\\\"" + functions.maskingPan(enrollmentRequestVO.getPan()) + "\\\",\\\"acquirerMerchantID\\\":\\\"" + enrollmentRequestVO.getAcquirerMerchantID() + "\\\",\\\"messageType\\\":\\\"VReq\\\",\\\"messageVersion\\\":\\\"2.1.0\\\"}\"\n}";

            transactionLogger.error("VReq Request---trackingId---" + trackingId + "-->" + verificationRequestLog);
            String verficationResponse = EndeavourMPIUtils.doPostHTTPSURLConnection(vrequrl, verificationRequest);
            transactionLogger.error("VRes Response---trackingId---" + trackingId + "-->" + verficationResponse.replaceAll(enrollmentRequestVO.getPan(),functions.maskingPan(enrollmentRequestVO.getPan())));
            //Verification Response

            if (functions.isValueNull(verficationResponse))
            {
                JSONObject vResJSON = new JSONObject(verficationResponse);
                if (vResJSON.has("message"))
                {
                    String message = vResJSON.getString("message");
                    JSONObject messageJson = new JSONObject(message);
                    if (messageJson.has("threeDSServerTransID"))
                        threeDSServerTransID = messageJson.getString("threeDSServerTransID");
                    if (messageJson.has("enrolled"))
                        enrolled = messageJson.getString("enrolled");
                    if (messageJson.has("messageType"))
                        messagetype = messageJson.getString("messageType");
                    if (messageJson.has("errorCode"))
                        errorCode = messageJson.getString("errorCode");
                    if (messageJson.has("errorDescription"))
                        errorDescription = messageJson.getString("errorDescription");

                }
                if ("Erro".equalsIgnoreCase(messagetype))
                {
                    transactionLogger.error("===Error in Vreq===");
                    enrollmentResponseVO = endeavourMPIGateway.processEnrollment(enrollmentRequestVO);
                    return enrollmentResponseVO;
                }
                if ("N".equalsIgnoreCase(enrolled))
                {
                    transactionLogger.error("--3D2 Not Enrolled--");
                    enrollmentResponseVO = endeavourMPIGateway.processEnrollment(enrollmentRequestVO);
                    // Call old MPI method - fallback to version 1
                    return enrollmentResponseVO;
                }
                else
                {
                    String acsUrl="";
                    if(functions.isValueNull(enrollmentRequestVO.getHostUrl()))
                        acsUrl="https://"+enrollmentRequestVO.getHostUrl()+RB.getString("ACS_HOST_URL")+enrollmentRequestVO.getTrackid()+ESAPI.encoder().encodeForURL("&threeDsTransId="+threeDSServerTransID);
                    else
                        acsUrl=RB.getString("ACS_TERM_URL")+enrollmentRequestVO.getTrackid()+ ESAPI.encoder().encodeForURL("&threeDsTransId="+threeDSServerTransID);

                    enrollmentResponseVO.setAcsUrl(acsUrl);
                    enrollmentResponseVO.setThreeDSServerTransID(threeDSServerTransID);
                    enrollmentResponseVO.setAvr("Y");
                    enrollmentResponseVO.setResult("Enrolled");
                    enrollmentResponseVO.setThreeDVersion("3Dv2");
                    return enrollmentResponseVO;
                }
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---trackingId--"+trackingId+"---",e);
            PZExceptionHandler.raiseTechnicalViolationException(EndeavourMPIV2Gateway.class.getName(), "processVerificationForRest()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (EncodingException e)
        {
            transactionLogger.error("JSONException---trackingId--"+trackingId+"---",e);
            PZExceptionHandler.raiseTechnicalViolationException(EndeavourMPIV2Gateway.class.getName(), "processVerificationForRest()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return enrollmentResponseVO;
    }
    public EnrollmentResponseVO processAuthentication(EnrollmentRequestVO enrollmentRequestVO) throws PZTechnicalViolationException
    {
        EnrollmentResponseVO enrollmentResponseVO=new EnrollmentResponseVO();
        EndeavourMPIGateway endeavourMPIGateway=new EndeavourMPIGateway();
        Functions functions=new Functions();
        String trackingId=enrollmentRequestVO.getTrackid();
        String threeDSServerTransID="";
        String messagetype="";
        String errorCode="";
        String errorDescription="";
        String dsTransID="";
        String acsTransID="";
        String acsReferenceNumber="";
        String acsOperatorID="";
        String dsReferenceNumber="";
        String transStatus="";
        String authenticationType="";
        String acsChallengeMandated="";
        String acsURL="";
        String eci="";
        String authenticationValue="";
        String userAgent= "";
        String language= "";
        String colorDepth= "";
        String height= "";
        String width= "";
        String timezoneOffset= "";
        String browserJavaEnabled= "";
        String acceptHeader= "";
        String browserIP= "";
        String deviceChannel= "02";
        boolean isBrowserDetailsFound=false;
        if(functions.isValueNull(enrollmentRequestVO.getBrowserIp()))
            browserIP=enrollmentRequestVO.getBrowserIp();
        acceptHeader=enrollmentRequestVO.getAcceptHeader();
        userAgent=enrollmentRequestVO.getUseragent();
        acceptHeader=enrollmentRequestVO.getAcceptHeader();
        language=enrollmentRequestVO.getBrowserLanguage();
        colorDepth=enrollmentRequestVO.getBrowserColorDepth();
        timezoneOffset=enrollmentRequestVO.getBrowserTimezoneOffset();
        height=enrollmentRequestVO.getBrowserScreenHeight();
        width=enrollmentRequestVO.getBrowserScreenWidth();
        browserJavaEnabled=enrollmentRequestVO.getBrowserJavaEnabled();
        threeDSServerTransID=enrollmentRequestVO.getThreeDSServerTransID();
        isBrowserDetailsFound=true;
        deviceChannel="02";
        String arequrl=RB.getString("AUTHENTICATION_URL");
        try
        {
            StringBuffer aReq = new StringBuffer();
            aReq.append("{" +
                    "\"api\":{" +
                    "\"version\":\"2.1.0.1.0\"," +
                    "\"trackid\":\"" + trackingId + "\"," +
                    "\"compressed\":false," +
                    "\"merchantidentifier\":\"" + enrollmentRequestVO.getMid() + "\" }," +
                    "\"messagetype\":\"AReq\"," +
                    "\"message\": \"{\\\"messageType\\\":\\\"AReq\\\",\\\"deviceChannel\\\":\\\"" + deviceChannel + "\\\",\\\"messageVersion\\\":\\\"2.1.0\\\",\\\"threeDSCompInd\\\":\\\"U\\\",\\\"threeDSRequestorAuthenticationInd\\\":\\\"01\\\",\\\"threeDSServerTransID\\\":\\\"" + threeDSServerTransID + "\\\"");
            if (functions.isValueNull(browserIP))
                aReq.append(",\\\"browserIP\\\":\\\"" + browserIP + "\\\"");
            if (isBrowserDetailsFound)
            {
                aReq.append(",\\\"browserAcceptHeader\\\":\\\"" + acceptHeader + "\\\",\\\"browserJavaEnabled\\\":" + browserJavaEnabled + ",\\\"browserLanguage\\\":\\\"" + language + "\\\",\\\"browserColorDepth\\\":\\\"" + colorDepth + "\\\",\\\"browserScreenHeight\\\":\\\"" + height + "\\\",\\\"browserScreenWidth\\\":\\\"" + width + "\\\",\\\"browserTZ\\\":\\\"" + timezoneOffset + "\\\",\\\"browserUserAgent\\\":\\\"" + userAgent + "\\\",");
            }
            aReq.append("\\\"notificationURL\\\":\\\"" + enrollmentRequestVO.getTermUrl() + trackingId + "\\\",\\\"cardExpiryDate\\\":\\\"" + enrollmentRequestVO.getExpiry() + "\\\",\\\"acctNumber\\\":\\\"" + enrollmentRequestVO.getPan() + "\\\",\\\"merchantCountryCode\\\":\\\"" + enrollmentRequestVO.getMerchantCountry() + "\\\",\\\"messageCategory\\\":\\\"01\\\",\\\"purchaseAmount\\\":\\\"" + enrollmentRequestVO.getAmount() + "\\\",\\\"purchaseCurrency\\\":\\\"" + enrollmentRequestVO.getCurrency() + "\\\",\\\"CardholderName\\\":\\\"" + enrollmentRequestVO.getName() + "\\\",\\\"transType\\\":\\\"01\\\"}\"" +
                    "}");
            StringBuffer aReqLog=new StringBuffer();
            aReqLog.append("{" +
                    "\"api\":{" +
                    "\"version\":\"2.1.0.1.0\"," +
                    "\"trackid\":\""+trackingId+"\"," +
                    "\"compressed\":false," +
                    "\"merchantidentifier\":\""+enrollmentRequestVO.getMid()+"\" }," +
                    "\"messagetype\":\"AReq\","+
                    "\"message\": \"{\\\"messageType\\\":\\\"AReq\\\",\\\"deviceChannel\\\":\\\""+deviceChannel+"\\\",\\\"messageVersion\\\":\\\"2.1.0\\\",\\\"threeDSCompInd\\\":\\\"U\\\",\\\"threeDSRequestorAuthenticationInd\\\":\\\"01\\\",\\\"threeDSServerTransID\\\":\\\""+threeDSServerTransID+"\\\"");
            if(functions.isValueNull(browserIP))
                aReqLog.append(",\\\"browserIP\\\":\\\""+browserIP+"\\\"");
            if(isBrowserDetailsFound){
                aReqLog.append(",\\\"browserAcceptHeader\\\":\\\""+acceptHeader+"\\\",\\\"browserJavaEnabled\\\":"+browserJavaEnabled+",\\\"browserLanguage\\\":\\\""+language+"\\\",\\\"browserColorDepth\\\":\\\""+colorDepth+"\\\",\\\"browserScreenHeight\\\":\\\""+height+"\\\",\\\"browserScreenWidth\\\":\\\""+width+"\\\",\\\"browserTZ\\\":\\\""+timezoneOffset+"\\\",\\\"browserUserAgent\\\":\\\""+userAgent+"\\\",");
            }
            aReqLog.append("\\\"notificationURL\\\":\\\""+enrollmentRequestVO.getHostUrl()+trackingId+"\\\",\\\"cardExpiryDate\\\":\\\""+functions.maskingNumber(enrollmentRequestVO.getExpiry())+"\\\",\\\"acctNumber\\\":\\\""+functions.maskingPan(enrollmentRequestVO.getPan())+"\\\",\\\"merchantCountryCode\\\":\\\""+enrollmentRequestVO.getMerchantCountry()+"\\\",\\\"messageCategory\\\":\\\"01\\\",\\\"purchaseAmount\\\":\\\""+enrollmentRequestVO.getAmount()+"\\\",\\\"purchaseCurrency\\\":\\\""+enrollmentRequestVO.getCurrency()+"\\\",\\\"CardholderName\\\":\\\""+enrollmentRequestVO.getName()+"\\\",\\\"transType\\\":\\\"01\\\"}\""+
                    "}");
            transactionLogger.error("Areq Request--trackingId---" + trackingId + "--->" + aReqLog);
            String aRes = "";
            aRes = EndeavourMPIUtils.doPostHTTPSURLConnection(arequrl, aReq.toString());
            transactionLogger.error("Ares Response--trackingId---" + trackingId + "--->" + aRes);
            if (functions.isValueNull(aRes))
            {
                JSONObject aResJSON = new JSONObject(aRes);
                if (aResJSON.has("message"))
                {
                    String message = aResJSON.getString("message");
                    JSONObject messageJSON = new JSONObject(message);
                    if (messageJSON.has("dsTransID"))
                        dsTransID = messageJSON.getString("dsTransID");
                    if (messageJSON.has("acsTransID"))
                        acsTransID = messageJSON.getString("acsTransID");
                    if (messageJSON.has("acsReferenceNumber"))
                        acsReferenceNumber = messageJSON.getString("acsReferenceNumber");
                    if (messageJSON.has("acsOperatorID"))
                        acsOperatorID = messageJSON.getString("acsOperatorID");
                    if (messageJSON.has("dsReferenceNumber"))
                        dsReferenceNumber = messageJSON.getString("dsReferenceNumber");
                    if (messageJSON.has("transStatus"))
                        transStatus = messageJSON.getString("transStatus");
                    if (messageJSON.has("transStatus"))
                        transStatus = messageJSON.getString("transStatus");
                    if (messageJSON.has("authenticationType"))
                        authenticationType = messageJSON.getString("authenticationType");
                    if (messageJSON.has("acsChallengeMandated"))
                        acsChallengeMandated = messageJSON.getString("acsChallengeMandated");
                    if (messageJSON.has("acsURL"))
                        acsURL = messageJSON.getString("acsURL");
                    if (messageJSON.has("eci"))
                        eci = messageJSON.getString("eci");
                    if (messageJSON.has("authenticationValue"))
                        authenticationValue = messageJSON.getString("authenticationValue");
                    if (messageJSON.has("messageType"))
                        messagetype = messageJSON.getString("messageType");
                    if (messageJSON.has("errorCode"))
                        errorCode = messageJSON.getString("errorCode");
                    if (messageJSON.has("errorDescription"))
                        errorDescription = messageJSON.getString("errorDescription");

                }
                if ("Erro".equalsIgnoreCase(messagetype))
                {
                    transactionLogger.error("====Error in Areq====");
                    enrollmentResponseVO.setStatus("failed");
                    enrollmentResponseVO.setResult(errorDescription);
                    return enrollmentResponseVO;
                }

                String cReq = "{\"messageVersion\":\"2.1.0\",\"messageType\":\"CReq\",\"threeDSServerTransID\":\"" + threeDSServerTransID + "\",\"acsTransID\":\"" + acsTransID + "\",\"challengeWindowSize\":\"04\"}";
                String creqjson = Base64.encodeBase64String(cReq.getBytes());
                String threeDSSessionData = trackingId;

                enrollmentResponseVO.setAcsUrl(acsURL);
                enrollmentResponseVO.setCreq(creqjson);
                enrollmentResponseVO.setThreeDSSessionData(threeDSSessionData);
                enrollmentResponseVO.setAvr(transStatus);
                enrollmentResponseVO.setEci(eci);
                enrollmentResponseVO.setCAVV(authenticationValue);
                enrollmentResponseVO.set_20BytesBinaryCAVVBytes(com.directi.pg.Base64.decode(authenticationValue));
                enrollmentResponseVO.set_20BytesBinaryCAVV(new String(com.directi.pg.Base64.decode(authenticationValue)));
                if (functions.isValueNull(dsTransID))
                {
                    enrollmentResponseVO.setXID(dsTransID.substring(0, 20));
                    enrollmentResponseVO.set_20BytesBinaryXID(dsTransID.substring(0, 20));
                    enrollmentResponseVO.set_20BytesBinaryXIDBytes(dsTransID.substring(0, 20).getBytes());
                }
                enrollmentResponseVO.setDsTransId(dsTransID);
                if ("C".equalsIgnoreCase(transStatus))
                    enrollmentResponseVO.setResult("Enrolled");
                else if ("Y".equalsIgnoreCase(transStatus) || "N".equalsIgnoreCase(transStatus) || "R".equalsIgnoreCase(transStatus) || "A".equalsIgnoreCase(transStatus))
                    enrollmentResponseVO.setResult("Frictionless");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---trackingId--"+trackingId+"---",e);
            PZExceptionHandler.raiseTechnicalViolationException(EndeavourMPIV2Gateway.class.getName(), "processAuthenticationForRest()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return enrollmentResponseVO;
    }
    public String recallRRes(ParesDecodeRequestVO paresDecodeRequestVO,String cresJson) throws PZTechnicalViolationException
    {
        String creqUrl=RB.getString("RRESDECODE_URL");

        String creq = "{\"api\":{\"version\":\"2.1.0.1.0\",\"istestcard\":false,\"compressed\":false,\"merchantidentifier\":\"" + paresDecodeRequestVO.getMid() + "\",\"trackid\":\"" + paresDecodeRequestVO.getTrackid() + "\",\"nonce\":\"SharedSecret\"},\"messagetype\":\"CRes\",\"message\":\"" + cresJson + "\"}";

        String rres = "";
        transactionLogger.error("rreq Request---trackingid--" + paresDecodeRequestVO.getTrackid() + "--->" + creq);
        rres = EndeavourMPIUtils.doPostHTTPSURLConnection(creqUrl, creq);

        return rres;
    }
    public ParesDecodeResponseVO processRresDecode(ParesDecodeRequestVO paresDecodeRequestVO) throws PZTechnicalViolationException
    {
        ParesDecodeResponseVO paresDecodeResponseVO=new ParesDecodeResponseVO();
        Functions functions=new Functions();
        String creqUrl=RB.getString("RRESDECODE_URL");
        String cRes=paresDecodeRequestVO.getcRes();
        String decodeCRes=new String(Base64.decodeBase64(cRes.getBytes()));
        //FrontEnd cres decrypted JSON
        String cresJson="";
        String threeDSServerTransID="";
        String acsTransID="";
        String authenticationValue="";
        String dsTransID="";
        String eci="";
        String authenticationMethod="";
        String authenticationType="";
        String messagetype="";
        String errorCode="";
        String errorDescription="";
        String trackid="";
        String transStatus="";

        cresJson=decodeCRes.replace("\"", "\\\"");
        transactionLogger.error("cresJson----->"+cresJson);
        try
        {
            String creq = "{\"api\":{\"version\":\"2.1.0.1.0\",\"istestcard\":false,\"compressed\":false,\"merchantidentifier\":\"" + paresDecodeRequestVO.getMid() + "\",\"trackid\":\"" + paresDecodeRequestVO.getTrackid() + "\",\"nonce\":\"SharedSecret\"},\"messagetype\":\"CRes\",\"message\":\"" + cresJson + "\"}";

            String rres = "";
            transactionLogger.error("rreq Request---trackingid--" + paresDecodeRequestVO.getTrackid() + "--->" + creq);
            rres = EndeavourMPIUtils.doPostHTTPSURLConnection(creqUrl, creq);
            //Authentication response
            //{"api":{"version":"2.1.0.1.0","istestcard":false,"compressed":false,"merchantidentifier":"WORLDPAY_INTERNETUSD","trackid":"1327","nonce":"SharedSecret"},"messagetype":"RReq","message":"{\"threeDSServerTransID\":\"52468be6-34b9-4103-a72f-365f605ec315\",\"acsTransID\":\"22120db5-71cf-4bdf-b031-51cfc2d7b70e\",\"authenticationMethod\":\"02\",\"authenticationType\":\"03\",\"authenticationValue\":\"AJkCCReZIncEQFcQBZkiAAAAAAA=\",\"dsTransID\":\"af408843-3955-4d34-b048-1f9d3c98915e\",\"eci\":\"05\",\"interactionCounter\":\"01\",\"messageCategory\":\"01\",\"messageType\":\"RReq\",\"messageVersion\":\"2.1.0\",\"transStatus\":\"Y\"}"}
            transactionLogger.error("rres Response------trackingid--" + paresDecodeRequestVO.getTrackid() + "--->" + rres);
            if(functions.isValueNull(rres))
            {
                JSONObject rresJSON=new JSONObject(rres);
                if(rresJSON.has("api"))
                {
                    JSONObject apiJSON=rresJSON.getJSONObject("api");
                    if(apiJSON.has("trackid"))
                        trackid=apiJSON.getString("trackid");
                }
                if(rresJSON.has("message"))
                {
                    String message=rresJSON.getString("message");
                    JSONObject messageJSON=new JSONObject(message);
                    if(messageJSON.has("threeDSServerTransID"))
                        threeDSServerTransID=messageJSON.getString("threeDSServerTransID");
                    if(messageJSON.has("acsTransID"))
                        acsTransID = messageJSON.getString("acsTransID");
                    if(messageJSON.has("authenticationValue"))
                    {
                        authenticationValue = messageJSON.getString("authenticationValue");
                    }
                    if (messageJSON.has("dsTransID"))
                        dsTransID=messageJSON.getString("dsTransID");
                    if(messageJSON.has("eci"))
                        eci=messageJSON.getString("eci");
                    if(messageJSON.has("authenticationMethod"))
                        authenticationMethod=messageJSON.getString("authenticationMethod");
                    if(messageJSON.has("authenticationType"))
                        authenticationType=messageJSON.getString("authenticationType");
                    if(messageJSON.has("messageType"))
                        messagetype=messageJSON.getString("messageType");
                    if(messageJSON.has("errorCode"))
                        errorCode=messageJSON.getString("errorCode");
                    if(messageJSON.has("errorDescription"))
                        errorDescription=messageJSON.getString("errorDescription");
                    if(messageJSON.has("transStatus"))
                        transStatus=messageJSON.getString("transStatus");
                }
                if("Erro".equalsIgnoreCase(messagetype))
                {
                    Date date  = new Date();
                    transactionLogger.error(" startTime---" + date.getTime());
                    Thread.sleep(8000);//wait for 5 miliseconds
                    String rres2 = recallRRes(paresDecodeRequestVO,cresJson);
                    transactionLogger.error(" End time--->" + (new Date()).getTime());
                    transactionLogger.error(" Difference time--->" + ((new Date()).getTime() - date.getTime()));
                    transactionLogger.error("2nd call for rres2 Response------trackingid--" + paresDecodeRequestVO.getTrackid() + "--->" + rres2);

                    String msgType = "";
                    if(functions.isValueNull(rres2))
                    {
                        JSONObject rres2JSON=new JSONObject(rres2);
                        if(rres2JSON.has("message"))
                        {
                            String messageTwo=rres2JSON.getString("message");
                            JSONObject msgJSON=new JSONObject(messageTwo);

                            if(msgJSON.has("messageType"))
                                msgType=msgJSON.getString("messageType");

                            if("Erro".equalsIgnoreCase(msgType))
                            {
                                paresDecodeResponseVO.setStatus("failed");
                                return paresDecodeResponseVO;
                            }
                            else
                            {
                                if(rres2JSON.has("api"))
                                {
                                    JSONObject apiJSON=rres2JSON.getJSONObject("api");
                                    if(apiJSON.has("trackid"))
                                        trackid=apiJSON.getString("trackid");
                                }
                                if(msgJSON.has("threeDSServerTransID"))
                                    threeDSServerTransID=msgJSON.getString("threeDSServerTransID");
                                if(msgJSON.has("acsTransID"))
                                    acsTransID = msgJSON.getString("acsTransID");
                                if(msgJSON.has("authenticationValue"))
                                {
                                    authenticationValue = msgJSON.getString("authenticationValue");
                                }
                                if (msgJSON.has("dsTransID"))
                                    dsTransID=msgJSON.getString("dsTransID");
                                if(msgJSON.has("eci"))
                                    eci=msgJSON.getString("eci");
                                if(msgJSON.has("authenticationMethod"))
                                    authenticationMethod=msgJSON.getString("authenticationMethod");
                                if(msgJSON.has("authenticationType"))
                                    authenticationType=msgJSON.getString("authenticationType");
                                if(msgJSON.has("messageType"))
                                    messagetype=msgJSON.getString("messageType");
                                if(msgJSON.has("errorCode"))
                                    errorCode=msgJSON.getString("errorCode");
                                if(msgJSON.has("errorDescription"))
                                    errorDescription=msgJSON.getString("errorDescription");
                                if(msgJSON.has("transStatus"))
                                    transStatus=msgJSON.getString("transStatus");
                            }
                        }
                    }
                }
                paresDecodeResponseVO.setEci(eci);
                paresDecodeResponseVO.setCavv(authenticationValue);
                paresDecodeResponseVO.set_20BytesBinaryCAVVBytes(com.directi.pg.Base64.decode(authenticationValue));
                paresDecodeResponseVO.set_20BytesBinaryCAVV(new String(com.directi.pg.Base64.decode(authenticationValue)));
                if(functions.isValueNull(dsTransID))
                {
                    paresDecodeResponseVO.setXid(dsTransID.substring(0, 20));
                    paresDecodeResponseVO.set_20BytesBinaryXID(dsTransID.substring(0, 20));
                    paresDecodeResponseVO.set_20BytesBinaryXIDBytes(dsTransID.substring(0, 20).getBytes());
                }
                paresDecodeResponseVO.setDsTransId(dsTransID);
                paresDecodeResponseVO.setTrackid(trackid);
                paresDecodeResponseVO.setStatus(transStatus);
                paresDecodeResponseVO.setVersion("3DS2");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---trackingId--"+paresDecodeRequestVO.getTrackid()+"--",e);
            PZExceptionHandler.raiseTechnicalViolationException(EndeavourMPIV2Gateway.class.getName(), "processRresDecode()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (InterruptedException e)
        {
            transactionLogger.error("InterruptedException---trackingId--" + paresDecodeRequestVO.getTrackid() + "--", e);
            PZExceptionHandler.raiseTechnicalViolationException(EndeavourMPIV2Gateway.class.getName(), "processRresDecode()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }


        return paresDecodeResponseVO;
    }

}
