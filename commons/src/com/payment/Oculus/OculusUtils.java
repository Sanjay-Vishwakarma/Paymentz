package com.payment.Oculus;

/**
 * Created by Admin on 7/7/2021.
 */
        import com.directi.pg.TransactionLogger;
        import com.payment.exceptionHandler.PZExceptionHandler;
        import com.payment.exceptionHandler.PZTechnicalViolationException;
        import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
        import okhttp3.*;
        import org.w3c.dom.*;
        import org.xml.sax.InputSource;
        import org.xml.sax.SAXException;

        import javax.xml.parsers.DocumentBuilder;
        import javax.xml.parsers.DocumentBuilderFactory;
        import javax.xml.parsers.ParserConfigurationException;
        import javax.xml.soap.*;
        import java.io.ByteArrayOutputStream;
        import java.io.IOException;
        import java.io.StringReader;
        import java.util.HashMap;

/**
 * Created by Admin on 7/7/2021.
 */
public class OculusUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(OculusUtils.class.getName());

    public SOAPEnvelope createEnvelope(String prefix, String namespaceUri,SOAPMessage soapMessage) throws PZTechnicalViolationException
    {
        try
        {
            SOAPPart soapPart = soapMessage.getSOAPPart();
            // SOAP Envelope
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration(prefix, namespaceUri);
            return envelope;
        }
        catch (SOAPException e)
        {
            transactionLogger.error("Exception in createEnvelope OculusUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("OculusUtils.java", "createEnvelope()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.AXISFAULT, null, e.getMessage(), e.getCause());
        }
        return  null;
    }

    public String call(SOAPMessage soapMessage, String actionUrl, String endpointUrl) throws PZTechnicalViolationException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SOAPConnection soapConnection=null;
        try
        {
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            soapConnection = soapConnectionFactory.createConnection();
            MimeHeaders headers = soapMessage.getMimeHeaders();
            headers.setHeader("SOAPAction", actionUrl);

            soapMessage.saveChanges();
            SOAPMessage soapResponse = soapConnection.call(soapMessage, endpointUrl);

            soapResponse.writeTo(out);
        }
        catch (SOAPException e)
        {
            transactionLogger.error("Exception in call() OculusUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("OculusUtils.java", "call()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.AXISFAULT, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            transactionLogger.error("Exception in call() OculusUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("OculusUtils.java", "call()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }finally
        {
            try
            {
                if(soapConnection!=null)
                    soapConnection.close();
            }
            catch (SOAPException e)
            {
                transactionLogger.error("Exception in call() OculusUtils", e);
            }
        }
        String strMsg = new String(out.toByteArray());
        return strMsg;
    }

    //todo this method added because existing call method which uses SOAPConnection not able to make connection with request Url.
    public HashMap<String,String> doPostOkhttp3Connection(SOAPMessage soapMessage, String actionUrl, String endpointUrl, String type, String trackingid) throws PZTechnicalViolationException
    {
        OkHttpClient client = null;
        MediaType mediaType = null;
        RequestBody body    = null;
        Request request     = null;
        Response response   = null;
        HashMap<String, String> HM = new HashMap<>();

        try
        {
            client      = new OkHttpClient().newBuilder().build();
            mediaType   = MediaType.parse("text/xml");
            body        = RequestBody.create(mediaType, convertToString(soapMessage));
            request     = new Request.Builder()
                            .url(endpointUrl)
                            .method("POST", body)
                            .addHeader("SOAPAction", actionUrl)
                            .addHeader("Content-Type", "text/xml")
                            .build();
            response    = client.newCall(request).execute();
            //todo dont print response otherwise not able to pass response in next method, okhttpclient3 response only readable once.
//            transactionLogger.error(trackingid+" response: "+ response.body().string());
            HM = readResponseSoap(response.body().string(), type);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception in call() OculusUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("OculusUtils.java", "call()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            // okhttpclient3 automatically close connection.
        }
        return HM;
    }

    public static HashMap<String, String> readResponseSoap(String response,String transactionType) throws PZTechnicalViolationException
    {
        HashMap<String, String> hashMap = new HashMap<>();

        Document doc = createDocumentFromString(response);
        NodeList Credit_SoapResult = null;
        NodeList TokenData = null;
        NodeList Result =null;
        NodeList AuthChallenge =null;
        NodeList DCC =null;
        TokenData = doc.getElementsByTagName("TokenData");
        Result=doc.getElementsByTagName("Result");
        AuthChallenge=doc.getElementsByTagName("AuthChallenge");
        DCC=doc.getElementsByTagName("DCC");

        if("AUTH".equalsIgnoreCase(transactionType))
        {
            Credit_SoapResult = doc.getElementsByTagName("CreditAuth_SoapResult");
        }
        else if("REFUND".equalsIgnoreCase(transactionType))
        {
            Credit_SoapResult = doc.getElementsByTagName("CreditCredit_SoapResult");
        }
        else if("CAPTURE".equalsIgnoreCase(transactionType))
        {
            Credit_SoapResult = doc.getElementsByTagName("CreditCapture_SoapResult");
        }
        else if("CANCEL".equalsIgnoreCase(transactionType))
        {
            Credit_SoapResult = doc.getElementsByTagName("CreditVoid_SoapResult");
        }
        else
        {
            Credit_SoapResult = doc.getElementsByTagName("CreditSale_SoapResult");
        }


        hashMap.put("MCSTransactionID", getTagValue("MCSTransactionID", (Element) Credit_SoapResult.item(0)));
        hashMap.put("Amount", getTagValue("Amount", (Element) Credit_SoapResult.item(0)));
        hashMap.put("ProcessorApprovalCode", getTagValue("ProcessorApprovalCode", (Element) Credit_SoapResult.item(0)));

        hashMap.put("ResultCode", getTagValue("ResultCode", (Element) Result.item(0)));
        hashMap.put("ResultDetail", getTagValue("ResultDetail", (Element) Result.item(0)));
        hashMap.put("AVSResponse", getTagValue("AVSResponse", (Element) Result.item(0)));
        hashMap.put("CVVResponse", getTagValue("CVVResponse", (Element) Result.item(0)));
        hashMap.put("BatchID", getTagValue("BatchID", (Element) Result.item(0)));
        hashMap.put("AdditionalAuthType", getTagValue("AdditionalAuthType", (Element) Result.item(0)));
        hashMap.put("AuthType", getTagValue("AuthType", (Element) Result.item(0)));
        hashMap.put("ChallengeURL", getTagValue("ChallengeURL", (Element) Result.item(0)));

        hashMap.put("ChallengeURL", getTagValue("ChallengeURL", (Element) AuthChallenge.item(0)));
        hashMap.put("ChallengeKey", getTagValue("ChallengeKey", (Element) AuthChallenge.item(0)));
        hashMap.put("XID", getTagValue("XID", (Element) AuthChallenge.item(0)));
        hashMap.put("CompleteChallengeURL", getTagValue("CompleteChallengeURL", (Element) AuthChallenge.item(0)));

        hashMap.put("Token", getTagValue("Token", (Element) TokenData.item(0)));
        hashMap.put("IsActive", getTagValue("IsActive", (Element) TokenData.item(0)));
        hashMap.put("TokenType", getTagValue("TokenType", (Element) TokenData.item(0)));
        hashMap.put("CardType", getTagValue("CardType", (Element) TokenData.item(0)));
        hashMap.put("DateModified", getTagValue("DateModified", (Element) TokenData.item(0)));

        hashMap.put("MCSTransactionID", getTagValue("MCSTransactionID", (Element) DCC.item(0)));
        hashMap.put("DCCDate", getTagValue("DCCDate", (Element) DCC.item(0)));
        hashMap.put("MerchantCountryCode", getTagValue("MerchantCountryCode", (Element) DCC.item(0)));
        hashMap.put("MerchantCurrencyCode", getTagValue("MerchantCurrencyCode", (Element) DCC.item(0)));
        hashMap.put("DCCCountryCode", getTagValue("DCCCountryCode", (Element) DCC.item(0)));
        hashMap.put("DCCCurrencyCode", getTagValue("DCCCurrencyCode", (Element) DCC.item(0)));
        hashMap.put("DCCExchangeRate", getTagValue("DCCExchangeRate", (Element) DCC.item(0)));
        hashMap.put("DCCAmount", getTagValue("DCCAmount", (Element) DCC.item(0)));
        hashMap.put("Amount", getTagValue("Amount", (Element) DCC.item(0)));

        return hashMap;
    }

    public static Document createDocumentFromString(String xmlString) throws PZTechnicalViolationException
    {
        Document document = null;
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            document = docBuilder.parse(new InputSource(new StringReader(xmlString)));
        }
        catch (ParserConfigurationException pce)
        {
            transactionLogger.error("Exception in createDocumentFromString of OculusUtils", pce);
            PZExceptionHandler.raiseTechnicalViolationException("OculusUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e)
        {
            transactionLogger.error("Exception in createDocumentFromString OculusUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("OculusUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.SAXEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            transactionLogger.error("Exception in createDocumentFromString OculusUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("OculusUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return document;
    }

    private static String getTagValue(String trackID, Element eElement)
    {
        NodeList nlList = null;
        String value = "";
        if (eElement != null && eElement.getElementsByTagName(trackID) != null && eElement.getElementsByTagName(trackID).item(0) != null)
        {
            nlList = eElement.getElementsByTagName(trackID).item(0).getChildNodes();
        }
        if (nlList != null && nlList.item(0) != null)
        {
            org.w3c.dom.Node nValue = (org.w3c.dom.Node) nlList.item(0);
            value = nValue.getNodeValue();
        }
        return value;
    }

    public String convertToString(SOAPMessage soapMessage) throws PZTechnicalViolationException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try
        {
            soapMessage.writeTo(out);
        }
        catch (SOAPException e)
        {
            transactionLogger.error("Exception in convertToString OculusUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("OculusUtils.java", "convertToString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.AXISFAULT, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            transactionLogger.error("Exception in convertToString OculusUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("OculusUtils.java", "convertToString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return new String(out.toByteArray());
    }
    private static HashMap<String,String> errorMap=new HashMap();
    static {
        errorMap.put("0","Approved");
        errorMap.put("1","Referral / Call Issuer");
        errorMap.put("2","Referral / Call Issuer");
        errorMap.put("4","Pick Up Card");
        errorMap.put("5","Decline Do Not Honor");
        errorMap.put("07","Pick Up Card");
        errorMap.put("10","Approved Lesser");
        errorMap.put("11","Invalid Terminal Number Terminal set up incorrectly.");
        errorMap.put("12","Invalid Request");
        errorMap.put("13","Invalid Terminal Type The terminal is not supported or is not valid for the card presented.");
        errorMap.put("14","Invalid Account");
        errorMap.put("15","Terminal Deactivated");
        errorMap.put("16","Invalid Application Type");
        errorMap.put("17","Invalid Batch Number");
        errorMap.put("18","Invalid Sequence Number");
        errorMap.put("19","Invalid Date Time");
        errorMap.put("20","Invalid Auth Number");
        errorMap.put("21","Invalid Retrieval Data");
        errorMap.put("22","Service Not Allowed");
        errorMap.put("23","Invalid Program Type");
        errorMap.put("24","Balance Inquiry Returned");
        errorMap.put("25","Transaction Reversed");
        errorMap.put("26","Duplicate Duplicate record");
        errorMap.put("29","Unauthorized Transaction");
        errorMap.put("30","Invalid Card");
        errorMap.put("31","Card Not Active");
        errorMap.put("32","Card Already Active");
        errorMap.put("33","Activation Amount Below Limit");
        errorMap.put("34","Invalid Activation Code");
        errorMap.put("35","Invalid Password");
        errorMap.put("36","Exceeds Sale Amount");
        errorMap.put("41","Pick Up Card");
        errorMap.put("43","Pick Up Card");
        errorMap.put("50","General Decline");
        errorMap.put("51","Decline Insufficient Funds");
        errorMap.put("52","Number of PIN tries exceeded");
        errorMap.put("53","Transaction could not be routed");
        errorMap.put("54","Expired Card Card is expired.");
        errorMap.put("55","Transaction Not Voided,Already Settled");
        errorMap.put("59","Restricted Status");
        errorMap.put("61","Decline Exceeds Issuer Withdrawal Limit");
        errorMap.put("62","Decline Invalid Service Code, Restricted");
        errorMap.put("64","Bad Track Information");
        errorMap.put("65","Decline Activity Limit Exceeded");
        errorMap.put("82","Maximum number of times used");
        errorMap.put("93","Decline Violation, Cannot Complete");
        errorMap.put("95","Amount over maximum");
        errorMap.put("96","Error System malfunction A system error has occurred.");
        errorMap.put("105","Card Not Supported Card type not supported.");
        errorMap.put("201","Incorrect PIN Issuer Bank did not receive correct PIN.");
        errorMap.put("209","Invalid Transaction Code Transaction Code Incorrect");
        errorMap.put("210","Bad CAVV 3DS secure check failed due to wrong CAVV");
        errorMap.put("211","Bad CVV2 CVV2 incorrect.");
        errorMap.put("212","Invalid Merchant Invalid Merchant");
        errorMap.put("213","Invalid Amount Invalid Amount");
        errorMap.put("214","Function Not Supported Function Not Supported for Merchant");
        errorMap.put("215","Invalid Effective Date");
        errorMap.put("216","Format Error");
        errorMap.put("217","Invalid Currency Code");
        errorMap.put("218","New Card Issued");
        errorMap.put("219","Closed Merchant");
        errorMap.put("220","Incorrect Currency");
        errorMap.put("221","Processor Timeout");
        errorMap.put("222","Issuer Unavailable");
        errorMap.put("801","Original Transaction Not Found");
        errorMap.put("966","Transaction declined by acquirers");
        errorMap.put("996","Only full 3D Secure authentication allowed");
        errorMap.put("997","3DS checkup returned ‘U’");
        errorMap.put("998","3DS checkup returned ‘Y’, but merchant did not supply XID and CAVV fields");
        errorMap.put("999","Password Must Be Changed");
        errorMap.put("1000","Unknown User Unknown User");
        errorMap.put("1001","Invalid Token Invalid Token");
        errorMap.put("1002","AVS Decline The processor declined for AVS");
        errorMap.put("1003","Invalid Gateway");
        errorMap.put("1004","Balance Not Available");
        errorMap.put("1005","Processor Not Available");
    }
    public String getErrorMessage(String errorCode)
    {
        return errorMap.get(errorCode);
    }
}
