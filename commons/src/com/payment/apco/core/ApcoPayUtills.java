package com.payment.apco.core;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.payment.common.core.CurrencyCodeISO4217;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.net.ssl.SSLHandshakeException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
/**
 * Created by Nikita on 2/3/2017.
 */
public class ApcoPayUtills
{
    private final static String charset = "UTF-8";
    private static TransactionLogger transactionLogger=new TransactionLogger(ApcoPayUtills.class.getName());
    ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.ApcoPayServlet");

    public static Map<String, String> readApcopayRedirectionXMLReponse(String str) throws Exception
    {
        Document doc = createDocumentFromString(str);
        Map<String, String> responseMap = new HashMap<String, String>();
        doc.getDocumentElement().getNodeName();
        NodeList nList = null;
        nList = doc.getElementsByTagName("Transaction");
        responseMap.put("Result", getTagValue("Result", ((Element) nList.item(0))));
        responseMap.put("ORef", getTagValue("ORef", ((Element) nList.item(0))));
        responseMap.put("AuthCode", getTagValue("AuthCode", ((Element) nList.item(0))));
        responseMap.put("pspid", getTagValue("pspid", ((Element) nList.item(0))));
        responseMap.put("Currency", getTagValue("Currency", ((Element) nList.item(0))));
        responseMap.put("Value", getTagValue("Value", ((Element) nList.item(0))));
        responseMap.put("Source", getTagValue("Source", ((Element) nList.item(0))));
        responseMap.put("Email", getTagValue("Email", ((Element) nList.item(0))));
        responseMap.put("ExtendedErr", getTagValue("ExtendedErr", ((Element) nList.item(0))));
        return responseMap;
    }

    public static Map<String, String> readApcopay3DRedirectionXMLReponse(String str) throws Exception
    {
        Document doc = createDocumentFromString(str);
        Map<String, String> responseMap = new HashMap<String, String>();
        doc.getDocumentElement().getNodeName();
        NodeList nList = null;
        nList = doc.getElementsByTagName("Do3DSTransactionResponse");
        responseMap.put("Do3DSTransactionResult", getTagValue("Do3DSTransactionResult", ((Element) nList.item(0))));
        return responseMap;
    }

    public static Map<String, String> readApcopay3DInquiryXMLReponse(String str) throws Exception
    {
        Document doc = createDocumentFromString(str);
        Map<String, String> responseMap = new HashMap<String, String>();
        doc.getDocumentElement().getNodeName();
        NodeList nList = null;
        nList = doc.getElementsByTagName("get3DSTransactionStatusResponse");
        responseMap.put("get3DSTransactionStatusResult", getTagValue("get3DSTransactionStatusResult", ((Element) nList.item(0))));
        return responseMap;
    }
    public static Map<String, String> readApcopayInquiryXMLReponse(String str) throws Exception
    {
        Functions functions=new Functions();
        Document doc = createDocumentFromString(str);
        Map<String, String> responseMap = new HashMap<String, String>();
        doc.getDocumentElement().getNodeName();
        NodeList nList = null;
        nList = doc.getElementsByTagName("getTransactionStatusResponse");
        String response=getTagValue("Response", ((Element) nList.item(0)));
        if(functions.isValueNull(response))
        {
            Document doc2 = createDocumentFromString(response);
            nList = doc2.getElementsByTagName("TransactionResult");
            responseMap.put("Result", getTagValue("Result", ((Element) nList.item(0))));
            responseMap.put("ORef", getTagValue("ORef", ((Element) nList.item(0))));
            responseMap.put("AuthCode", getTagValue("AuthCode", ((Element) nList.item(0))));
            responseMap.put("pspid", getTagValue("pspid", ((Element) nList.item(0))));
            responseMap.put("Currency", getTagValue("Currency", ((Element) nList.item(0))));
            responseMap.put("Value", getTagValue("Value", ((Element) nList.item(0))));
            responseMap.put("Source", getTagValue("Source", ((Element) nList.item(0))));
            responseMap.put("Email", getTagValue("Email", ((Element) nList.item(0))));
            responseMap.put("ExtendedErr", getTagValue("ExtendedErr", ((Element) nList.item(0))));
        }

        return responseMap;
    }

    public static Map<String, String> readApcoPayoutResponse(String str) throws Exception
    {

        Document doc = createDocumentFromString(str);
        Map<String, String> responseMap = new HashMap<String, String>();
        doc.getDocumentElement().getNodeName();
        NodeList nList = null;
        nList = doc.getElementsByTagName("PayoutResult");

        responseMap.put("Status", getTagValue("Status", ((Element) nList.item(0))));
        responseMap.put("ErrorMsg", getTagValue("ErrorMsg", ((Element) nList.item(0))));

        return  responseMap;

    }

    public static String getMD5HashVal(String str) throws PZTechnicalViolationException
    {
        String encryptedString = null;
        byte[] bytesToBeEncrypted;
        try
        {
            // convert string to bytes using a encoding scheme
            bytesToBeEncrypted = str.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] theDigest = md.digest(bytesToBeEncrypted);
            // convert each byte to a hexadecimal digit
            Formatter formatter = new Formatter();
            for (byte b : theDigest){
                formatter.format("%02x", b);
            }
            encryptedString = formatter.toString().toLowerCase();
        }
        catch (UnsupportedEncodingException e){
            PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "getMD5HashVal()", null, "Transaction", "UnsupportedEncodingException raised::::", PZTechnicalExceptionEnum.UNSUPPORTING_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e){
            PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "getMD5HashVal()", null, "Transaction", "UnsupportedEncodingException raised::::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return encryptedString;
    }

    public static String generateAutoSubmitForm1(String actionUrl, String xmlToPost)
    {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(actionUrl).append("\" method=\"post\">\n");
        html.append("<input type=\"hidden\" name=\"params\" maxlength=\"500\" value=\"" + xmlToPost + "\">");
        html.append("</form>\n");
        return html.toString();
    }

    public static void main(String args[])
    {
        ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.ApcoPayServlet");
        System.out.println("rb:::" + rb);
    }

    public static String doPostHTTPSURLConnection(String strURL, String request) throws PZTechnicalViolationException
    {
        String result=null;
        BufferedInputStream in=null;
        BufferedOutputStream out=null;
        URLConnection conn=null;
        try
        {
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            URL url = new URL(strURL);
            try{
                conn = url.openConnection();
                conn.setConnectTimeout(120000);
                conn.setReadTimeout(120000);
            }
            catch (SSLHandshakeException io){
                PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, io.getMessage(), io.getCause());
            }
            if(conn instanceof HttpURLConnection){
                ((HttpURLConnection)conn).setRequestMethod("POST");
            }
            assert conn != null;
            conn.setDoInput(true);
            conn.setDoOutput(true);
            out = new BufferedOutputStream(conn.getOutputStream());
            byte outBuf[] = request.getBytes(charset);
            out.write(outBuf);
            out.close();
            in = new BufferedInputStream(conn.getInputStream());
            result = ReadByteStream(in);
        }
        catch (UnsupportedEncodingException ue){
            PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null,ue.getMessage(), ue.getCause());
        }
        catch (MalformedURLException me){
            PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON, null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe){
            PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION, null, pe.getMessage(), pe.getCause());
        }
        catch (IOException ex){
            PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, ex.getMessage(), ex.getCause());
        }
        catch (PZConstraintViolationException e){
            transactionLogger.error("PZConstraintViolationException :::: ApcoPayUtils ",e);
        }
        finally{
            if (out != null){
                try{
                    out.close();
                }
                catch (IOException e){
                    PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
            if (in != null){
                try{
                    in.close();
                } catch (IOException e){
                    PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }

    private static String ReadByteStream(BufferedInputStream in) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        LinkedList<ApcoPay> bufList = new LinkedList<ApcoPay>();
        int size = 0;
        String buffer = null;
        byte buf[];
        try
        {
            do {
                buf = new byte[128];
                int num = in.read(buf);
                if (num == -1)
                    break;
                size += num;
                bufList.add(new ApcoPay(buf, num));
            }
            while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<ApcoPay> p = bufList.listIterator(); p.hasNext(); ){
                ApcoPay b = p.next();
                for (int i = 0; i < b.size; ){
                    buf[pos] = b.buf[i];
                    i++;
                    pos++;
                }
            }
            buffer = new String(buf, charset);
        }
        catch (UnsupportedEncodingException ue){
            PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ue.getMessage(), ue.getCause());
        }
        catch (IOException ie){
            PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ie.getMessage(), ie.getCause());
        }
        return buffer;
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
        catch (ParserConfigurationException pce){
            transactionLogger.error("Exception in createDocumentFromString of ApcoPayUtills", pce);
            PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e){
            transactionLogger.error("Exception in createDocumentFromString ApcoPayUtills", e);
            PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.SAXEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e){
            transactionLogger.error("Exception in createDocumentFromString ApcoPayUtills", e);
            PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return document;
    }

    private static String getTagValue(String Tag, Element eElement)
    {
        NodeList nlList = null;
        String value = "";
        if (eElement != null && eElement.getElementsByTagName(Tag) != null && eElement.getElementsByTagName(Tag).item(0) != null){
            nlList = eElement.getElementsByTagName(Tag).item(0).getChildNodes();
        }
        if (nlList != null && nlList.item(0) != null){
            Node nValue = nlList.item(0);
            value = nValue.getNodeValue();
        }
        return value;
    }

    public static String getBirthday(String birthday,String birthdayFromat)
    {
        Functions functions=new Functions();
        String year = birthday.substring(0, 4);
        String month = birthday.substring(4, 6);
        String day = birthday.substring(6, 8);
        if(functions.isValueNull(birthdayFromat) && birthdayFromat.equalsIgnoreCase("DD-MM-YYYY"))
            birthday = day + "-" + month + "-" + year;
        else
            birthday = year + "-" + month + "-" + day;

        return birthday;
    }

    public String getApcoPayRequest(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZTechnicalViolationException
    {
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String profileId=gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretWord=gatewayAccount.getFRAUD_FTP_PATH();
        String birthday = "";
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getBirthdate()))
        {
            birthday = getBirthday(commonValidatorVO.getAddressDetailsVO().getBirthdate(),null);
        }
        boolean isTest = gatewayAccount.isTest();
        String testString = "";
        if (isTest){
            testString = "<TEST />";
        }
        String amount = commonValidatorVO.getTransDetailsVO().getAmount();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(commonValidatorVO.getTransDetailsVO().getCurrency());
        Integer currencyCode = Integer.parseInt(currency);
        String trackingId = commonValidatorVO.getTrackingid();
        String phoneNo = commonValidatorVO.getAddressDetailsVO().getPhone();
        String emailId = commonValidatorVO.getAddressDetailsVO().getEmail();
        String language = commonValidatorVO.getAddressDetailsVO().getLanguage();
        String name = commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname();
        String street = commonValidatorVO.getAddressDetailsVO().getStreet() + "," + commonValidatorVO.getAddressDetailsVO().getStreet() + "," + commonValidatorVO.getAddressDetailsVO().getCity() + "," + commonValidatorVO.getAddressDetailsVO().getZipCode() + "," + commonValidatorVO.getAddressDetailsVO().getState();
        String country = commonValidatorVO.getAddressDetailsVO().getCountry();
        String redirectURLSuccess = rb.getString("APCOPAY_FRONTEND");
        String redirectURLFailed = rb.getString("APCOPAY_FRONTEND");
        String statusURL = rb.getString("APCOPAY_BACKEND");
        String actionType = "1";
        String cardTypeId = commonValidatorVO.getCardType();
        String apmName=getAPMName(cardTypeId);
        String mainAcq = "";
        String addressXML = "";

        if ("AVISA".equalsIgnoreCase(apmName) || ("AMASTERC".equalsIgnoreCase(apmName)))
        {
            mainAcq = "<MainAcquirer>ALDRAPAY</MainAcquirer>";
            addressXML = "<RegName>" + name + "</RegName>" +
                    "<Address>" + street + "</Address>" +
                    "<RegCountry>" + country + "</RegCountry>" +
                    "<DOB>" + birthday + "</DOB>";
        }
        else if ("PVISA".equalsIgnoreCase(apmName) || ("RAVE".equalsIgnoreCase(apmName)))
        {
            addressXML = "<RegName>" + name + "</RegName>" +
                    "<Address>" + street + "</Address>" +
                    "<RegCountry>" + country + "</RegCountry>" +
                    "<DOB>" + birthday + "</DOB>";
        }

        //generate md5 hash from xml
        String xmlToPostForMD5 = "<Transaction hash=\"" + secretWord + "\">" +
                "<ProfileID>" + profileId + "</ProfileID>" +
                "<Value>" + amount + "</Value>" +
                "<Curr>" + currencyCode + "</Curr>" +
                "<Lang>" + language + "</Lang>" +
                "<ORef>" + trackingId + "</ORef>" +
                "<ClientAcc></ClientAcc>" +
                "<MobileNo>" + phoneNo + "</MobileNo>" +
                "<Email>" + emailId + "</Email>" +
                "<RedirectionURL>" + redirectURLSuccess + "</RedirectionURL>" +
                "<FailedRedirectionURL>"+redirectURLFailed+"</FailedRedirectionURL>" +
                "<UDF1 />" +
                "<UDF2 />" +
                "<UDF3 />" +
                "<FastPay>" +
                "<ListAllCards></ListAllCards>" +
                "<NewCard1Try />" +
                "<NewCardOnFail />" +
                "<PromptCVV />" +
                "<PromptExpiry />" +
                "</FastPay>" +
                "<ActionType>" + actionType + "</ActionType>" +
                "<status_url>" + statusURL + "</status_url>" +
                "<HideSSLLogo></HideSSLLogo>" +
                "<AntiFraud>" +
                "<Provider></Provider>" +
                "</AntiFraud>" +
                "" + testString + "" +
                "<return_pspid></return_pspid>" +
                "<ForcePayment>" + apmName + "</ForcePayment>" +
                mainAcq +
                addressXML +
                "</Transaction>";
        transactionLogger.error("xmlToPostForMD5:::" + xmlToPostForMD5);
        String hash = getMD5HashVal(xmlToPostForMD5);
        transactionLogger.error("hash:::" + hash);
        //use generated hash in below xml
        String xmlToPost = "<Transaction hash='" + hash + "'>" +
                "<ProfileID>" + profileId + "</ProfileID>" +
                "<Value>" + amount + "</Value>" +
                "<Curr>" + currencyCode + "</Curr>" +
                "<Lang>" + language + "</Lang>" +
                "<ORef>" + trackingId + "</ORef>" +
                "<ClientAcc></ClientAcc>" +
                "<MobileNo>" + phoneNo + "</MobileNo>" +
                "<Email>" + emailId + "</Email>" +
                "<RedirectionURL>" + redirectURLSuccess + "</RedirectionURL>" +
                "<FailedRedirectionURL>" + redirectURLFailed + "</FailedRedirectionURL>" +
                "<UDF1 />" +
                "<UDF2 />" +
                "<UDF3 />" +
                "<FastPay>" +
                "<ListAllCards></ListAllCards>" +
                "<NewCard1Try />" +
                "<NewCardOnFail />" +
                "<PromptCVV />" +
                "<PromptExpiry />" +
                "</FastPay>" +
                "<ActionType>" + actionType + "</ActionType>" +
                "<status_url>" + statusURL + "</status_url>" +
                "<HideSSLLogo></HideSSLLogo>" +
                "<AntiFraud>" +
                "<Provider></Provider>" +
                "</AntiFraud>" +
                "" + testString + "" +
                "<return_pspid></return_pspid>" +
                "<ForcePayment>" + apmName + "</ForcePayment>" +
                mainAcq +
                addressXML +
                "</Transaction>";
        transactionLogger.error("xmlToPost:::" + xmlToPost);
        String redirectHTML = generateAutoSubmitForm1(rb.getString("URL"), xmlToPost);
        transactionLogger.error("redirectHTML:::" + redirectHTML);
        return redirectHTML.toString();
    }

    public String getApcoPayRequest(CommonValidatorVO commonValidatorVO, String ticketNo, String fromType) throws PZDBViolationException, PZTechnicalViolationException
    {
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String profileId = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretWord = gatewayAccount.getFRAUD_FTP_PATH();
        String birthday = "";

        boolean isTest = gatewayAccount.isTest();
        String testString = "";
        String Card="";
        String amount = commonValidatorVO.getTransDetailsVO().getAmount();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(commonValidatorVO.getTransDetailsVO().getCurrency());
        Integer currencyCode = Integer.parseInt(currency);
        String trackingId = commonValidatorVO.getTrackingid();
        String phoneNo = commonValidatorVO.getAddressDetailsVO().getPhone();
        String emailId = commonValidatorVO.getAddressDetailsVO().getEmail();
        String language = commonValidatorVO.getAddressDetailsVO().getLanguage();
        String name = commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname();
        String street = commonValidatorVO.getAddressDetailsVO().getStreet() + "," + commonValidatorVO.getAddressDetailsVO().getStreet() + "," + commonValidatorVO.getAddressDetailsVO().getCity() + "," + commonValidatorVO.getAddressDetailsVO().getZipCode() + "," + commonValidatorVO.getAddressDetailsVO().getState();
        String country = commonValidatorVO.getAddressDetailsVO().getCountry();
        String redirectURLSuccess = rb.getString("APCOPAY_FRONTEND");
        String redirectURLFailed = rb.getString("APCOPAY_FRONTEND");
        String statusURL = rb.getString("APCOPAY_BACKEND");
        String actionType = "1";
        String cardTypeId = commonValidatorVO.getCardType();
        //String apmName = getAPMName(cardTypeId, fromType);
        String apmName="";
        if(fromType.equals("fastpay"))
        {
            apmName=gatewayAccount.getCHARGEBACK_FTP_PATH();
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getBirthdate()))
            {
                birthday = getBirthday(commonValidatorVO.getAddressDetailsVO().getBirthdate(),rb.getString(apmName));
            }

        }
        else
        {
            apmName=getAPMName(cardTypeId, fromType);
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getBirthdate()))
            {
                birthday = getBirthday(commonValidatorVO.getAddressDetailsVO().getBirthdate(),null);
            }
        }
      //  String mainAcq = "";
        String addressXML = "";
        if (isTest)
        {
            testString = "<TEST />";
            Card="<TESTCARD />";
        }else{
            Card="<ForcePayment>" + apmName + "</ForcePayment>";
        }

        if ("ALDRAPAY".equalsIgnoreCase(apmName) || ("ALDRAPAY".equalsIgnoreCase(apmName)))
        {
            //mainAcq = "<MainAcquirer>ALDRAPAY</MainAcquirer>";
            addressXML = "<RegName>" + name + "</RegName>" +
                    "<Address>" + street + "</Address>" +
                    "<RegCountry>" + country + "</RegCountry>" +
                    "<DOB>" + birthday + "</DOB>";
        }
        else if ("PVISA".equalsIgnoreCase(apmName) || ("RAVE".equalsIgnoreCase(apmName))  || ("RAVEDIRECT".equalsIgnoreCase(apmName)))
        {
            addressXML = "<RegName>" + name + "</RegName>" +
                    "<Address>" + street + "</Address>" +
                    "<RegCountry>" + country + "</RegCountry>" +
                    "<DOB>" + birthday + "</DOB>";
        }
        else if (fromType.equals("fastpay"))
        {
            addressXML = "<RegName>" + name + "</RegName>" +
                    "<Address>" + street + "</Address>" +
                    "<RegCountry>" + country + "</RegCountry>" +
                    "<DOB>" + birthday + "</DOB>";
        }

        //generate md5 hash from xml
        String xmlToPostForMD5 = "<Transaction hash=\"" + secretWord + "\">" +
                "<ProfileID>" + profileId + "</ProfileID>" +
                "<Value>" + amount + "</Value>" +
                "<Curr>" + currencyCode + "</Curr>" +
                "<Lang>" + language + "</Lang>" +
                "<ORef>" + trackingId + "</ORef>" +
                "<ClientAcc></ClientAcc>" +
                "<MobileNo>" + phoneNo + "</MobileNo>" +
                "<Email>" + emailId + "</Email>" +
                "<CIP>" + commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress() + "</CIP>" +
                "<RedirectionURL>" + redirectURLSuccess + "</RedirectionURL>" +
                "<FailedRedirectionURL>" + redirectURLFailed + "</FailedRedirectionURL>" +
                "<UDF1 />" +
                "<UDF2 />" +
                "<UDF3 />" +
                "<FastPay>" +
                "<ListAllCards></ListAllCards>" +
                "<NewCard1Try />" +
                "<NewCardOnFail />" +
                "<PromptCVV />" +
                "<PromptExpiry />" +
                "</FastPay>" +
                "<ActionType>" + actionType + "</ActionType>" +
                "<status_url>" + statusURL + "</status_url>" +
                "<HideSSLLogo></HideSSLLogo>" +
                "<AntiFraud>" +
                "<Provider></Provider>" +
                "</AntiFraud>" +
                "" + testString + "" +
                "<return_pspid></return_pspid>" +
                "<TicketNo>" + ticketNo + "</TicketNo>" +
                Card+
              //  mainAcq +
                addressXML +
                "</Transaction>";
        transactionLogger.error("xmlToPostForMD5:::" + xmlToPostForMD5);
        String hash = getMD5HashVal(xmlToPostForMD5);
        transactionLogger.error("hash:::" + hash);
        //use generated hash in below xml
        String xmlToPost = "<Transaction hash='" + hash + "'>" +
                "<ProfileID>" + profileId + "</ProfileID>" +
                "<Value>" + amount + "</Value>" +
                "<Curr>" + currencyCode + "</Curr>" +
                "<Lang>" + language + "</Lang>" +
                "<ORef>" + trackingId + "</ORef>" +
                "<ClientAcc></ClientAcc>" +
                "<MobileNo>" + phoneNo + "</MobileNo>" +
                "<Email>" + emailId + "</Email>" +
                "<CIP>" + commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress() + "</CIP>" +
                "<RedirectionURL>" + redirectURLSuccess + "</RedirectionURL>" +
                "<FailedRedirectionURL>" + redirectURLFailed + "</FailedRedirectionURL>" +
                "<UDF1 />" +
                "<UDF2 />" +
                "<UDF3 />" +
                "<FastPay>" +
                "<ListAllCards></ListAllCards>" +
                "<NewCard1Try />" +
                "<NewCardOnFail />" +
                "<PromptCVV />" +
                "<PromptExpiry />" +
                "</FastPay>" +
                "<ActionType>" + actionType + "</ActionType>" +
                "<status_url>" + statusURL + "</status_url>" +
                "<HideSSLLogo></HideSSLLogo>" +
                "<AntiFraud>" +
                "<Provider></Provider>" +
                "</AntiFraud>" +
                "" + testString + "" +
                "<return_pspid></return_pspid>" +
                "<TicketNo>" + ticketNo + "</TicketNo>" +
                Card+
               // mainAcq +
                addressXML +
                "</Transaction>";
        transactionLogger.error("xmlToPost:::" + xmlToPost);
        String redirectHTML = generateAutoSubmitForm1(rb.getString("URL"), xmlToPost);
        transactionLogger.error("redirectHTML:::" + redirectHTML);
        return redirectHTML.toString();
    }

    public String getAPMName(String cardTypeId){
        Functions functions=new Functions();
        String apmName="";
        if (functions.isValueNull(cardTypeId)){
            if (cardTypeId.equals("10")){
                apmName = "SOFORT";
            }
            else if (cardTypeId.equals("18")){
                apmName = "NEOSURF";
            }
            else if (cardTypeId.equals("19")){
                apmName = "GIROPAY";
            }
            else if (cardTypeId.equals("20")){
                apmName = "MULTIBANCO";
            }
            else if (cardTypeId.equals("21")){
                apmName = "ASTROPAY";
            }
            else if (cardTypeId.equals("25")){
                apmName = "QIWI";
            }
            else if (cardTypeId.equals("26")){
                apmName = "YANDEXMONEY";
            }
            else if (cardTypeId.equals("39"))
            {
                apmName = "PVISA";
            }
            else if (cardTypeId.equals("40"))
            {
                apmName = "ALDRAPAY";
            }
            else if (cardTypeId.equals("41"))
            {
                apmName = "RAVE";
            }
            else if (cardTypeId.equals("42"))
            {
                apmName = "ALDRAPAY";
            }
        }
        return apmName;
    }

    public String getAPMName(String cardTypeId, String fromType)
    {
        Functions functions = new Functions();
        String apmName = "";
        if (functions.isValueNull(cardTypeId))
        {
            if (cardTypeId.equals("1") && (fromType.equalsIgnoreCase("purplepay")))
            {
                apmName = "PVISA";
            }
            else if (cardTypeId.equals("2") && (fromType.equalsIgnoreCase("purplepay")))
            {
                apmName = "PMASTERC";
            }
            else if (cardTypeId.equals("1") && (fromType.equalsIgnoreCase("aldrapay")))
            {
                apmName = "ALDRAPAY";
            }
            else if (cardTypeId.equals("2") && (fromType.equalsIgnoreCase("aldrapay")))
            {
                apmName = "ALDRAPAY";
            }
            else if (cardTypeId.equals("1") && (fromType.equalsIgnoreCase("ravedirect")))
            {
                apmName = "RAVEDIRECT";
            }
            else if (cardTypeId.equals("2") && (fromType.equalsIgnoreCase("ravedirect")))
            {
                apmName = "RAVEDIRECT";
            }
        }
        return apmName;
    }

    static class ApcoPay
    {
        public byte buf[];
        public int size;

        public ApcoPay(byte b[], int s){
            buf = b;
            size = s;
        }
    }


}