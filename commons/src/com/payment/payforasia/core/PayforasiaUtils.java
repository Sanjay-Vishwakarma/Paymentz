package com.payment.payforasia.core;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 5/29/14
 * Time: 6:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayforasiaUtils
{

    public final static String charset = "UTF-8";

    private static Logger log = new Logger(PayforasiaUtils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayforasiaUtils.class.getName());

    public static String SHA256forSales(String merchantNo, String gatewayNo, String orderNo, String orderCurrency, String orderAmount, String firstName, String lastName, String cardNo, String expiryYear, String expiryMonth, String cvv, String email, String signKey) throws PZTechnicalViolationException
    {
        String sha = merchantNo.trim() + gatewayNo.trim() + orderNo.trim() + orderCurrency.trim() + orderAmount.trim() + firstName.trim() + lastName.trim() + cardNo.trim() + expiryYear.trim() + expiryMonth.trim() + cvv.trim() + email.trim() + signKey;
        sha.trim();
        //transactionLogger.error("sha256 combination---" + sha);

        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayforasiaUtils.class.getName(), "SHA256forSales()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayforasiaUtils.class.getName(), "SHA256forSales()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null, e.getMessage(), e.getCause());
        }

        return hexString.toString().trim();
    }

    public static String SHA256forInquiry(String merchantNo, String gatewayNo, String signKey) throws PZTechnicalViolationException
    {
        String sha = merchantNo.trim() + gatewayNo.trim() + signKey;
        sha.trim();

        StringBuffer hexString = new StringBuffer();
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayforasiaUtils.class.getName(), "SHA256forInquiry()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayforasiaUtils.class.getName(), "SHA256forInquiry()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null, e.getMessage(), e.getCause());
        }

        return hexString.toString().toLowerCase().trim();
    }

    public static String SHA256forRefund(String merchantNo, String gatewayNo, String tradeNo, String refundType, String signKey) throws PZTechnicalViolationException
    {
        String sha = merchantNo.trim() + gatewayNo.trim() + tradeNo.trim() + refundType.trim() + signKey;
        sha.trim();

        StringBuffer hexString = new StringBuffer();
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayforasiaUtils.class.getName(), "SHA256forRefund()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayforasiaUtils.class.getName(), "SHA256forRefund()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null, e.getMessage(), e.getCause());
        }

        return hexString.toString().toLowerCase().trim();
    }

    public static String joinMapValue(Map<String, String> map, char connector)
    {
        StringBuffer b = new StringBuffer();
        int cnt = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            b.append(entry.getKey());
            b.append('=');
            if (entry.getValue() != null) {
                b.append(entry.getValue());
            }
            cnt++;
            if(cnt<map.size())
            {
                b.append(connector);
            }
        }
        return b.toString();
    }
    public static String joinMapValueForLogging(Map<String, String> map, char connector)
    {
        StringBuffer b = new StringBuffer();
        int cnt = 0;
        for (Map.Entry<String, String> entry : map.entrySet())
        {
            String key=entry.getKey();
            if("cardNo".equals(key) || "cardExpireYear".equals(key) || "cardExpireMonth".equals(key) || "cardSecurityCode".equals(key))
            {
                continue;
            }

            b.append(key);
            b.append('=');
            if (entry.getValue() != null) {
                b.append(entry.getValue());
            }
            cnt++;
            if(cnt<map.size())
            {
                b.append(connector);
            }
        }
        return b.toString();
    }

    public static String doPostHTTPSURLConnection(String strURL, String req) throws PZTechnicalViolationException
    {
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        URLConnection con = null;

        try
        {
            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            System.setProperty("https.protocols", "TLSv1.2");
            //java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            URL url = new URL(strURL.trim());
            try
            {
                con = url.openConnection();
                con.setConnectTimeout(60000);
                con.setReadTimeout(60000);
            }
            catch (IOException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE,null, io.getMessage(), io.getCause());
            }

            if(con instanceof HttpURLConnection)
            {
                ((HttpURLConnection)con).setRequestMethod("POST");
            }
            con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            con.setDoInput(true);
            con.setDoOutput(true);


            //con.setRequestProperty("Content-length",String.valueOf (req.length()));
            //con.setRequestProperty("Content-Type","application/x-www- form-urlencoded");
            //con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows 98; DigExt)");

            out = new BufferedOutputStream(con.getOutputStream());
            byte outBuf[] = req.getBytes(charset);
            out.write(outBuf);

            out.close();

            in = new BufferedInputStream(con.getInputStream());
            result = ReadByteStream(in);
        }
        catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null,me.getMessage(),me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (UnsupportedEncodingException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, ex.getMessage(), ex.getCause());
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                } catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }

    private static String ReadByteStream(BufferedInputStream in) throws PZTechnicalViolationException
    {
        LinkedList<Payforasia> bufList = new LinkedList<Payforasia>();
        int size = 0;
        String buffer = null;
        byte buf[];
        try
        {
            do
            {
                buf = new byte[128];
                int num = in.read(buf);
                if (num == -1)
                    break;
                size += num;
                bufList.add(new Payforasia(buf, num));
            } while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<Payforasia> p = bufList.listIterator(); p.hasNext();)
            {
                Payforasia b = p.next();
                for (int i = 0; i < b.size;) {
                    buf[pos] = b.buf[i];
                    i++;
                    pos++;
                }
            }
            buffer = new String(buf,charset);
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, io.getMessage(), io.getCause());
        }
        return buffer;
    }

    static class Payforasia
    {
        public byte buf[];
        public int size;

        public Payforasia(byte b[], int s)
        {
            buf = b;
            size = s;
        }
    }

    public static Map<String,String> ReadSalesResponse(String str)
    {
        Document doc = createDocumentFromString(str);

        Map<String, String> responseMap  = new HashMap<String, String>();
        try
        {
            NodeList nList = doc.getElementsByTagName("respon");

            responseMap.put("merNo",getTagValue("merNo",((Element) nList.item(0))));
            responseMap.put("gatewayNo",getTagValue("gatewayNo",((Element) nList.item(0))));
            responseMap.put("tradeNo",getTagValue("tradeNo",((Element) nList.item(0))));
            responseMap.put("orderNo",getTagValue("orderNo",((Element) nList.item(0))));
            responseMap.put("orderAmount",getTagValue("orderAmount",((Element) nList.item(0))));
            responseMap.put("orderCurrency",getTagValue("orderCurrency",((Element) nList.item(0))));
            responseMap.put("orderStatus",getTagValue("orderStatus",((Element) nList.item(0))));
            responseMap.put("orderInfo",getTagValue("orderInfo",((Element) nList.item(0))));
            responseMap.put("signInfo",getTagValue("signInfo",((Element) nList.item(0))));
            responseMap.put("remark",getTagValue("remark",((Element) nList.item(0))));
            responseMap.put("riskInfo",getTagValue("riskInfo",((Element) nList.item(0))));
            responseMap.put("billAddress",getTagValue("billAddress",((Element) nList.item(0))));

            // For Amex Channel
            responseMap.put("w_mer_no",getTagValue("w_mer_no",((Element) nList.item(0))));
            responseMap.put("w_gateway_no",getTagValue("w_gateway_no",((Element) nList.item(0))));
            responseMap.put("w_trade_no",getTagValue("w_trade_no",((Element) nList.item(0))));
            responseMap.put("w_order_no",getTagValue("w_order_no",((Element) nList.item(0))));
            responseMap.put("w_order_amount",getTagValue("w_order_amount",((Element) nList.item(0))));
            responseMap.put("w_order_currency",getTagValue("w_order_currency",((Element) nList.item(0))));
            responseMap.put("w_order_status",getTagValue("w_order_status",((Element) nList.item(0))));
            responseMap.put("w_order_info",getTagValue("w_order_info",((Element) nList.item(0))));
            responseMap.put("w_sign_info",getTagValue("w_sign_info",((Element) nList.item(0))));
            responseMap.put("w_remark",getTagValue("w_remark",((Element) nList.item(0))));
            responseMap.put("w_risk_info",getTagValue("w_risk_info",((Element) nList.item(0))));
        }
        catch (Exception e)
        {
            log.error("Exception in ReadSalesResponse",e);
            transactionLogger.error("Exception in ReadSalesResponse",e);
        }
        return responseMap;
    }

    public static Map<String,String> ReadRefundResponse(String str)
    {
        Document doc = createDocumentFromString(str);

        Map<String, String> responseMap  = new HashMap<String, String>();
        try
        {
            doc.getDocumentElement().getNodeName();
            NodeList nList = doc.getElementsByTagName("applyRefund");

            responseMap.put("merNo",getTagValue("merNo",((Element) nList.item(0))));
            responseMap.put("gatewayNo",getTagValue("gatewayNo",((Element) nList.item(0))));
            responseMap.put("signInfo",getTagValue("signInfo",((Element) nList.item(0))));
            responseMap.put("tradeNo",getTagValue("tradeNo",((Element) nList.item(0))));
            responseMap.put("code",getTagValue("code",((Element) nList.item(0))));
            responseMap.put("description",getTagValue("description",((Element) nList.item(0))));
            responseMap.put("batchNo",getTagValue("batchNo",((Element) nList.item(0))));
            responseMap.put("remark",getTagValue("remark",((Element) nList.item(0))));
            responseMap.put("refundReason",getTagValue("refundReason",((Element) nList.item(0))));
        }
        catch (Exception e)
        {
            log.error("Exception in ReadRefundResponse",e);
            transactionLogger.error("Exception in ReadRefundResponse",e);
        }
        return responseMap;
    }

    public static Map<String,String> ReadInquiryResponse(String str)
    {
        Document doc = createDocumentFromString(str);

        Map<String,String> inquiryMap = new HashMap<String, String>();

        try
        {
            doc.getDocumentElement().getNodeName();
            NodeList nodeList = doc.getElementsByTagName("response");

            inquiryMap.put("merNo", getTagValue("merNo", (Element) nodeList.item(0)));
            inquiryMap.put("gatewayNo", getTagValue("gatewayNo", (Element) nodeList.item(0)));
            inquiryMap.put("orderNo", getTagValue("orderNo", (Element) nodeList.item(0)));
            inquiryMap.put("tradeNo", getTagValue("tradeNo", (Element) nodeList.item(0)));
            inquiryMap.put("tradeDate", getTagValue("tradeDate", (Element) nodeList.item(0)));
            inquiryMap.put("tradeAmount", getTagValue("tradeAmount", (Element) nodeList.item(0)));
            inquiryMap.put("tradeCurrency", getTagValue("tradeCurrency", (Element) nodeList.item(0)));
            inquiryMap.put("authStatus", getTagValue("authStatus", (Element) nodeList.item(0)));
            inquiryMap.put("sourceWebSite", getTagValue("sourceWebSite", (Element) nodeList.item(0)));
            inquiryMap.put("queryResult", getTagValue("queryResult", (Element) nodeList.item(0)));
            inquiryMap.put("barcode", getTagValue("barcode", (Element) nodeList.item(0)));
            inquiryMap.put("paytime", getTagValue("paytime", (Element) nodeList.item(0)));
        }
        catch (Exception e)
        {
            log.error("Exception in ReadInquiryResponse",e);
        }

        return inquiryMap;
    }


    private static String getTagValue(String sTag, Element eElement) {

        NodeList nlList = null;
        String value  ="";
        if(eElement!=null && eElement.getElementsByTagName(sTag)!=null && eElement.getElementsByTagName(sTag).item(0)!=null)
        {
            nlList =  eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        }
        if(nlList!=null && nlList.item(0)!=null)
        {
            Node nValue = nlList.item(0);
            value =	nValue.getNodeValue();
        }
        return value;
    }

    private static String getAttributeValue(Element eElement, String sTag)
    {
        String value  ="";
        if(eElement!=null)
        {
            value =	eElement.getAttribute(sTag);
        }
        return value;
    }

    public static Document createDocumentFromString(String xmlString ) {

        Document doc = null;
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            doc = docBuilder.parse(new InputSource(new StringReader( xmlString ) ));
        }
        catch (ParserConfigurationException pce)
        {
            log.error("Exception in createDocumentFromString of PayForAsiaUtils",pce);
            transactionLogger.error("Exception in createDocumentFromString of PayForAsiaUtils",pce);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            log.error("Exception in createDocumentFromString PayForAsiaUtils",e);
            transactionLogger.error("Exception in createDocumentFromString PayForAsiaUtils",e);
        }
        return doc;
    }

    public static void main(String[] a)
    {
        StringBuffer readXml = new StringBuffer();

        StringBuffer refundXml = new StringBuffer();

        readXml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");

        readXml.append("<respon>");
        readXml.append("<merNo>20543</merNo>");
        readXml.append("<gatewayNo>20543001</gatewayNo>");
        readXml.append("<tradeNo>2014070323522381639907</tradeNo>");
        readXml.append("<orderNo>vdssd8f7</orderNo>");
        readXml.append("<orderAmount>60.00</orderAmount>");
        readXml.append("<orderCurrency>USD</orderCurrency>");
        readXml.append("<orderStatus>0</orderStatus>");
        readXml.append("<orderInfo>I0071:Ip address can not be empty</orderInfo>");
        readXml.append("<signInfo>140A0D8AE4ACD02F9155BA98CCD82105DBB2229E7AA4F1CC36CE4D1FE8B623A5</signInfo>");
        readXml.append("<remark></remark>");
        readXml.append("<riskInfo>|||0.0|0.0|||||||</riskInfo>");
        readXml.append("</respon>");

        /*refundXml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
        refundXml.append("<response>");
        refundXml.append("<applyRefund>");
        refundXml.append("<batchNo>6320</batchNo>");
        refundXml.append("<merNo>2858</merNo>");
        refundXml.append("<gatewayNo>285808</gatewayNo>");
        refundXml.append("<signInfo>d98fg7</signInfo>");
        refundXml.append("<code>0</code>");
        refundXml.append("<description>Insufficient parameter transmission</description>");
        refundXml.append("<tradeNo>986876</tradeNo>");
        refundXml.append("<refundReason>Testing</refundReason>");
        refundXml.append("<remark>No Data</remark>");
        refundXml.append("</applyRefund>");
        refundXml.append("</response>");*/

        String i = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<response><tradeinfo><merNo>20928</merNo><gatewayNo>20928001</gatewayNo><orderNo>28440</orderNo><tradeNo>2016020621082687590426</tradeNo><tradeDate>2016-02-06 21:08:27</tradeDate><tradeAmount>50.00</tradeAmount><tradeCurrency>EUR</tradeCurrency><authStatus>0</authStatus><sourceWebSite></sourceWebSite><queryResult>0</queryResult><barcode></barcode><paytime></paytime></tradeinfo></response>";

        /*String inquiry = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<response>\n" +
                "<tradeinfo>\n" +
                "<merNo>20928</merNo>\n" +
                "<gatewayNo>20928001</gatewayNo>\n" +
                "<orderNo>28438</orderNo>\n" +
                "<tradeNo>2016020620253759521464</tradeNo>\n" +
                "<tradeDate>2016-02-06 20:25:38</tradeDate>\n" +
                "<tradeAmount>16.34</tradeAmount>\n" +
                "<tradeCurrency>EUR</tradeCurrency>\n" +
                "<authStatus>0</authStatus>\n" +
                "<sourceWebSite></sourceWebSite>\n" +
                "<queryResult>0</queryResult>\n" +
                "<barcode></barcode>\n" +
                "<paytime></paytime>\n" +
                "</tradeinfo>\n" +
                "</response>";*/

        Map<String, String> responseMap =  ReadInquiryResponse(i.toString());
        System.out.println(responseMap);

        System.out.println("Response inquiry---"+responseMap.get("queryResult"));

    }


}

