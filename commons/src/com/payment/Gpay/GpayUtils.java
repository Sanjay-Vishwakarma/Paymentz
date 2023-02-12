package com.payment.Gpay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.google.common.net.HttpHeaders;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
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

public class GpayUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(GpayUtils.class.getName());
    private final static String charset = "UTF-8";
    private static ResourceBundle RBTemplate = LoadProperties.getProperty("com.directi.pg.3CharCountryList");
    ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.GpayServlet");
    
    public static String doGetHTTPSURLConnectionClient(String url,String request) throws PZTechnicalViolationException
    {
        String result   = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.setRequestHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("GpayUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("GpayUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {
            post.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }
    public CommRequestVO getGPayRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO             = new CommRequestVO();
        CommAddressDetailsVO addressDetailsVO   = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO         = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO           = new CommMerchantVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());

        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getProcessorName());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        commRequestVO.setCustomerId(commonValidatorVO.getVpa_address());
        transactionLogger.error("utils vpa--->" + commonValidatorVO.getVpa_address());

        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }
    public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D){
        transactionLogger.error("inside  GpayUtils Form---");

        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" ;

        form+="</form><script language=\"javascript\">document.creditcard_checkout.submit();</script>";


        transactionLogger.error("GpayUtils Form---"+form.toString());
        return form.toString();
    }
    public static String generateAutoSubmitForm(Comm3DResponseVO commResponseVO)
    {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(commResponseVO.getUrlFor3DRedirect()).append("\" method=\"POST\">\n");
        html.append("</form>\n");
        return html.toString();
    }
    public static Map<String, String> readGpayRedirectionXMLReponse(String str) throws Exception
    {
        Document doc = createDocumentFromString(str);
        Map<String, String> responseMap = new HashMap<String, String>();
        doc.getDocumentElement().getNodeName();
        NodeList nList = null;
        nList = doc.getElementsByTagName("PopulateTransactionData2Response");
        responseMap.put("PopulateTransactionData2Result", getTagValue("PopulateTransactionData2Result", ((Element) nList.item(0))));
        //System.out.println("map----"+responseMap.get("PopulateTransactionData2Result"));
        return responseMap;
    }

    public static  String getCardType(String cardTypeid){
        String cardType="";
        if(cardTypeid.equals("VISA")){
            cardType="VISA";
        }else if(cardTypeid.equals("MC")){
            cardType="MASTERCARD";
        }
        return cardType;
    }
    public static String doPostHTTPSURLConnection(String strURL, String request) throws PZTechnicalViolationException
    {
        transactionLogger.error("inside dopost utils====>"+strURL);
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
                conn.setRequestProperty("Content-Type", "text/xml");
            }
            catch (SSLHandshakeException io){
                transactionLogger.error("SSLHandshakeException --->",io);
                PZExceptionHandler.raiseTechnicalViolationException("GpayUtils.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, io.getMessage(), io.getCause());
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
            transactionLogger.error("UnsupportedEncodingException --->",ue);
            PZExceptionHandler.raiseTechnicalViolationException("GpayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null,ue.getMessage(), ue.getCause());
        }
        catch (MalformedURLException me){
            transactionLogger.error("MalformedURLException --->",me);
            PZExceptionHandler.raiseTechnicalViolationException("GpayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON, null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe){
            transactionLogger.error("ProtocolException --->",pe);
            PZExceptionHandler.raiseTechnicalViolationException("GpayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION, null, pe.getMessage(), pe.getCause());
        }
        catch (IOException ex){
            transactionLogger.error("IOException --->",ex);
            PZExceptionHandler.raiseTechnicalViolationException("GpayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, ex.getMessage(), ex.getCause());
        }
        catch (PZConstraintViolationException e){
            transactionLogger.error("PZConstraintViolationException --->",e);
        }
        finally{
            if (out != null){
                try{
                    out.close();
                }
                catch (IOException e){
                    transactionLogger.error("IOException finally out.close --->",e);
                    PZExceptionHandler.raiseTechnicalViolationException("GpayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
            if (in != null){
                try{
                    in.close();
                } catch (IOException e){
                    transactionLogger.error("IOException finally in.close --->",e);
                    PZExceptionHandler.raiseTechnicalViolationException("GpayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }
    public static Map<String, String> readGPayRedirectionXMLResponse(String str) throws Exception
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
        responseMap.put("ISOResp", getTagValue("ISOResp", ((Element) nList.item(0))));
        responseMap.put("CardHName", getTagValue("CardHName", ((Element) nList.item(0))));
        responseMap.put("CardNum", getTagValue("CardNum", ((Element) nList.item(0))));

        return responseMap;
    }
    public static Map<String, String> readGpayInquiryXMLResponse(String str) throws Exception
    {
        Functions functions=new Functions();
        Document doc = createDocumentFromString(str);
        Map<String, String> responseMap = new HashMap<String, String>();
        doc.getDocumentElement().getNodeName();
        NodeList nList,nList2 = null;
        nList = doc.getElementsByTagName("Table1");

        if(nList!=null)
        {

            if(nList.getLength()>1){
                String response=getTagValue("Response", ((Element) nList.item(nList.getLength()-1)));
                if(functions.isValueNull(response))
                {

                    Document doc2 = createDocumentFromString(response);
                    nList2 = doc2.getElementsByTagName("TransactionResult");
                    responseMap.put("Result", getTagValue("Result", ((Element) nList2.item(0))));
                    responseMap.put("ORef", getTagValue("ORef", ((Element) nList2.item(0))));
                    responseMap.put("AuthCode", getTagValue("AuthCode", ((Element) nList2.item(0))));
                    responseMap.put("pspid", getTagValue("pspid", ((Element) nList2.item(0))));
                    responseMap.put("Currency", getTagValue("Currency", ((Element) nList2.item(0))));
                    responseMap.put("Value", getTagValue("Value", ((Element) nList2.item(0))));
                    responseMap.put("Source", getTagValue("Source", ((Element) nList2.item(0))));
                    responseMap.put("Email", getTagValue("Email", ((Element) nList2.item(0))));
                    responseMap.put("ExtendedErr", getTagValue("ExtendedErr", ((Element) nList2.item(0))));
                }

            }
            else
            {
                String response = getTagValue("Response", ((Element) nList.item(0)));
                if (functions.isValueNull(response))
                {

                    Document doc2 = createDocumentFromString(response);
                    nList2 = doc2.getElementsByTagName("TransactionResult");
                    responseMap.put("Result", getTagValue("Result", ((Element) nList2.item(0))));
                    responseMap.put("ORef", getTagValue("ORef", ((Element) nList2.item(0))));
                    responseMap.put("AuthCode", getTagValue("AuthCode", ((Element) nList2.item(0))));
                    responseMap.put("pspid", getTagValue("pspid", ((Element) nList2.item(0))));
                    responseMap.put("Currency", getTagValue("Currency", ((Element) nList2.item(0))));
                    responseMap.put("Value", getTagValue("Value", ((Element) nList2.item(0))));
                    responseMap.put("Source", getTagValue("Source", ((Element) nList2.item(0))));
                    responseMap.put("Email", getTagValue("Email", ((Element) nList2.item(0))));
                    responseMap.put("ExtendedErr", getTagValue("ExtendedErr", ((Element) nList2.item(0))));
                }
            }
        }


        return responseMap;
    }

    private static String ReadByteStream(BufferedInputStream in) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        LinkedList<GPAY> bufList = new LinkedList<GPAY>();
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
                bufList.add(new GPAY(buf, num));
            }
            while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<GPAY> p = bufList.listIterator(); p.hasNext(); ){
                GPAY b = p.next();
                for (int i = 0; i < b.size; ){
                    buf[pos] = b.buf[i];
                    i++;
                    pos++;
                }
            }
            buffer = new String(buf, charset);
        }
        catch (UnsupportedEncodingException ue){
            PZExceptionHandler.raiseTechnicalViolationException("GpayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ue.getMessage(), ue.getCause());
        }
        catch (IOException ie){
            PZExceptionHandler.raiseTechnicalViolationException("GpayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ie.getMessage(), ie.getCause());
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
            transactionLogger.error("Exception in createDocumentFromString of GpayUtils", pce);
            PZExceptionHandler.raiseTechnicalViolationException("GpayUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e){
            transactionLogger.error("Exception in createDocumentFromString GpayUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("GpayUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.SAXEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e){
            transactionLogger.error("Exception in createDocumentFromString GpayUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("GpayUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
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
    static class GPAY
    {
        public byte buf[];
        public int size;

        public GPAY(byte b[], int s){
            buf = b;
            size = s;
        }
    }

    public static void main(String args[])
    {
        ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.GpayServlet");
        transactionLogger.error("rb:::" + rb);
    }
    public String getGPayRequest(CommonValidatorVO commonValidatorVO, String ticketNo, String fromType) throws PZDBViolationException, PZTechnicalViolationException
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
        String mobile = commonValidatorVO.getAddressDetailsVO().getPhone();
        String language = commonValidatorVO.getAddressDetailsVO().getLanguage();
        String name = commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname();
        String street = commonValidatorVO.getAddressDetailsVO().getStreet() + "," + commonValidatorVO.getAddressDetailsVO().getStreet() + "," + commonValidatorVO.getAddressDetailsVO().getCity() + "," + commonValidatorVO.getAddressDetailsVO().getZipCode() + "," + commonValidatorVO.getAddressDetailsVO().getState();
        String country = RBTemplate.getString(commonValidatorVO.getAddressDetailsVO().getCountry());
        String firstname = commonValidatorVO.getAddressDetailsVO().getFirstname();
        String lastname  = commonValidatorVO.getAddressDetailsVO().getLastname();
        String regCountry = commonValidatorVO.getAddressDetailsVO().getCountry();
        String redirectURLSuccess = rb.getString("GPAY_FRONTEND")+trackingId;
        String redirectURLFailed = rb.getString("GPAY_FRONTEND")+trackingId;
        String statusURL = rb.getString("GPAY_BACKEND")+trackingId;
        String actionType = "1";
        String cardTypeId = commonValidatorVO.getCardType();
        String userAgent ="Mozilla/5.0 (Android; Mobile; rv:13.0) Gecko/13.0 Firefox/13.0"; //deviceDetailsVO.getUser_Agent();

        //generate md5 hash from xml
        String xmlToPostForMD5 = "<Transaction hash=\""+secretWord.trim()+"\">\n" +
                "<RedirectionURL>"+redirectURLSuccess+"</RedirectionURL>\n" +
                "<status_url urlEncode=\"true\">"+statusURL+"</status_url>\n" +
                "<FailedRedirectionURL>"+redirectURLFailed+"</FailedRedirectionURL>\n" +
                "<ProfileID>"+profileId+"</ProfileID>\n" +
                "<TicketNo>" +ticketNo  + "</TicketNo>\n" +
                "<ActionType>1</ActionType>\n" +
                "<Value>"+amount+"</Value>\n" +
                "<Curr>"+currencyCode+"</Curr>\n" +
                "<Lang>en</Lang>\n" +
                "<ORef>"+trackingId+"</ORef>\n" +
                "<Email>"+emailId+"</Email>\n" +
                "<MobileNo>"+mobile+"</MobileNo>\n" +
                "<Address>"+street+"</Address>\n" +
                "<RegCountry>"+regCountry+"</RegCountry>\n" +
                "<Country>"+country+"</Country>\n" +
                "<UDF1></UDF1>\n" +
                "<UDF2></UDF2>\n" +
                "<UDF3 />\n" +
                "<TEST />\n" +
                "<ForceBank>FRP</ForceBank>\n" +
                "<ForcePayment>FRP</ForcePayment>\n" +
                "<CIP>" + commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress() + "</CIP>\n" +
                "<UserAgent>"+userAgent+"</UserAgent>\n" +
                "<NoCardList/>\n" +
                "<ClientAcc>"+firstname+" "+lastname+"</ClientAcc>\n" +
                "<RegName>"+firstname+" "+lastname+"</RegName>\n" +
                "<CardInput>New</CardInput>\n" +
                "</Transaction>";
        transactionLogger.error("xmlToPostForMD5::: fastpay**************" + xmlToPostForMD5);
        transactionLogger.error("userAgent==================>" + userAgent);
        String hash = getMD5HashVal(xmlToPostForMD5);
        transactionLogger.error("hash:::" + hash);
        //use generated hash in below xml
        String xmlToPost ="<Transaction hash=\""+secretWord.trim()+"\">\n" +
                "<RedirectionURL>"+redirectURLSuccess+"</RedirectionURL>\n" +
                "<status_url urlEncode=\"true\">"+statusURL+"</status_url>\n" +
                "<FailedRedirectionURL>"+redirectURLFailed+"</FailedRedirectionURL>\n" +
                "<ProfileID>"+profileId+"</ProfileID>\n" +
                "<TicketNo>" + ticketNo + "</TicketNo>\n" +
                "<ActionType>1</ActionType>\n" +
                "<Value>"+amount+"</Value>\n" +
                "<Curr>"+currencyCode+"</Curr>\n" +
                "<Lang>en</Lang>\n" +
                "<ORef>"+trackingId+"</ORef>\n" +
                "<Email>"+emailId+"</Email>\n" +
                "<MobileNo>"+mobile+"</MobileNo>\n" +
                "<Address>"+street+"</Address>\n" +
                "<RegCountry>"+regCountry+"</RegCountry>\n" +
                "<Country>"+country+"</Country>\n" +
                "<UDF1></UDF1>\n" +
                "<UDF2></UDF2>\n" +
                "<UDF3 />\n" +
                "<TEST />\n" +
                "<ForceBank>FRP</ForceBank>\n" +
                "<ForcePayment>FRP</ForcePayment>\n" +
                "<CIP>" + commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress() + "</CIP>\n" +
                "<UserAgent>"+userAgent+"</UserAgent>\n" +
                "<NoCardList />\n" +
                "<ClientAcc>"+firstname+" "+lastname+"</ClientAcc>\n" +
                "<RegName>"+firstname+" "+lastname+"</RegName>\n" +
                "<CardInput>New</CardInput>\n" +
                "</Transaction>";
        transactionLogger.error("xmlToPost:::" + xmlToPost);
        String redirectHTML = generateAutoSubmitForm1(rb.getString("URL"), xmlToPost);
        transactionLogger.error("redirectHTML:::" + redirectHTML);
        return redirectHTML.toString();
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
            PZExceptionHandler.raiseTechnicalViolationException("GpayUtils.java", "getMD5HashVal()", null, "Transaction", "UnsupportedEncodingException raised::::", PZTechnicalExceptionEnum.UNSUPPORTING_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e){
            PZExceptionHandler.raiseTechnicalViolationException("GpayUtils.java", "getMD5HashVal()", null, "Transaction", "UnsupportedEncodingException raised::::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return encryptedString;
    }
}
