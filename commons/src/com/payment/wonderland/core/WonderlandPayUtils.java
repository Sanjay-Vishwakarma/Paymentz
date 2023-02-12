package com.payment.wonderland.core;

import com.directi.pg.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.apache.commons.httpclient.HttpException;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by ThinkPadT410 on 9/2/2016.
 */
public class WonderlandPayUtils
{
    public final static String charset = "UTF-8";

    private static Logger log = new Logger(WonderlandPayUtils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(WonderlandPayUtils.class.getName());

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
        StringBuffer result = new StringBuffer();
        URL obj;
        HttpURLConnection con=null;
        try
        {
            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            System.setProperty("https.protocols", "TLSv1.2");

            obj = new URL(strURL);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(req);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                result.append(inputLine);
            }
            in.close();
        }
        catch (HttpException he)
        {
            PZExceptionHandler.raiseTechnicalViolationException("WonderlandPayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("WonderlandPayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            if (con !=null)
            {
                con.disconnect();
            }
        }
        return result.toString();
    }

    public static String doPostHTTPSURLConnection_Old(String strURL, String req) throws PZTechnicalViolationException
    {
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        URLConnection con = null;

        try
        {
            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            URL url = new URL(strURL.trim());
            try
            {
                con = url.openConnection();
                con.setConnectTimeout(60000);
                con.setReadTimeout(60000);
            }
            catch (IOException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("WonderlandPayUtils.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, io.getMessage(), io.getCause());
            }

            if(con instanceof HttpURLConnection)
            {
                ((HttpURLConnection)con).setRequestMethod("POST");
            }
            con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            con.setDoInput(true);
            con.setDoOutput(true);

            out = new BufferedOutputStream(con.getOutputStream());
            byte outBuf[] = req.getBytes(charset);
            out.write(outBuf);

            out.close();

            in = new BufferedInputStream(con.getInputStream());
            result = ReadByteStream(in);
        }
        catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("WonderlandPayUtils.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null,me.getMessage(),me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("WonderlandPayUtils.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (UnsupportedEncodingException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("WonderlandPayUtils.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("WonderlandPayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, ex.getMessage(), ex.getCause());
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
                    PZExceptionHandler.raiseTechnicalViolationException("WonderlandPayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                } catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("WonderlandPayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
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
        LinkedList<Wonderland> bufList = new LinkedList<Wonderland>();
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
                bufList.add(new Wonderland(buf, num));
            } while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<Wonderland> p = bufList.listIterator(); p.hasNext();)
            {
                Wonderland b = p.next();
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
            PZExceptionHandler.raiseTechnicalViolationException("WonderlandPayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, io.getMessage(), io.getCause());
        }
        return buffer;
    }

    public static Map<String, String> ReadSalesResponse(String str)
    {
        Document doc = createDocumentFromString(str);

        Map<String, String> responseMap = new HashMap<String, String>();
        try
        {
            NodeList nList = doc.getElementsByTagName("respon");

            responseMap.put("merNo", getTagValue("merNo", ((Element) nList.item(0))));
            responseMap.put("gatewayNo", getTagValue("gatewayNo", ((Element) nList.item(0))));
            responseMap.put("tradeNo", getTagValue("tradeNo", ((Element) nList.item(0))));
            responseMap.put("orderNo", getTagValue("orderNo", ((Element) nList.item(0))));
            responseMap.put("orderAmount", getTagValue("orderAmount", ((Element) nList.item(0))));
            responseMap.put("orderCurrency", getTagValue("orderCurrency", ((Element) nList.item(0))));
            responseMap.put("orderStatus", getTagValue("orderStatus", ((Element) nList.item(0))));
            responseMap.put("orderErrorCode", getTagValue("orderErrorCode", ((Element) nList.item(0))));
            responseMap.put("orderInfo", getTagValue("orderInfo", ((Element) nList.item(0))));
            responseMap.put("paymentMethod", getTagValue("paymentMethod", ((Element) nList.item(0))));
            responseMap.put("returnType", getTagValue("returnType", ((Element) nList.item(0))));
            responseMap.put("billAddress", getTagValue("billAddress", ((Element) nList.item(0))));
            responseMap.put("signInfo", getTagValue("signInfo", ((Element) nList.item(0))));
            responseMap.put("remark", getTagValue("remark", ((Element) nList.item(0))));
        }
        catch (Exception e)
        {
            log.error("Exception in ReadSalesResponse", e);
            transactionLogger.error("Exception in ReadSalesResponse", e);
        }
        return responseMap;
    }

    public static Map<String, String> ReadInquiryResponse(String str)
    {
        Document doc = createDocumentFromString(str);

        Map<String, String> inquiryMap = new HashMap<String, String>();

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
            inquiryMap.put("sourceWebSite", getTagValue("sourceWebSite", (Element) nodeList.item(0)));
            inquiryMap.put("queryResult", getTagValue("queryResult", (Element) nodeList.item(0)));
            inquiryMap.put("returnStatus", getTagValue("returnStatus", (Element) nodeList.item(0)));

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
            log.error("Exception in createDocumentFromString of WonderlandPayUtils",pce);
            transactionLogger.error("Exception in createDocumentFromString of WonderlandPayUtils",pce);
        }
        catch (Exception e)
        {
            log.error("Exception in createDocumentFromString WonderlandPayUtils",e);
            transactionLogger.error("Exception in createDocumentFromString WonderlandPayUtils",e);
        }
        return doc;
    }

    public static void main(String[] args)
    {
        String readSale = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<respon>\n" +
                "<merNo>18076</merNo>\n" +
                "<gatewayNo>18076001</gatewayNo>\n" +
                "<tradeNo>T2016090214422928758967</tradeNo>\n" +
                "<orderNo>00001</orderNo>\n" +
                "<orderAmount>98.00</orderAmount>\n" +
                "<orderCurrency>EUR</orderCurrency>\n" +
                "<orderStatus>1</orderStatus>\n" +
                "<orderErrorCode></orderErrorCode>\n" +
                "<orderInfo>Transaction succeeded, we do not charge any fees for testing transation</orderInfo>\n" +
                "<paymentMethod>Credit Card</paymentMethod>\n" +
                "<returnType>2</returnType>\n" +
                "<billAddress>enjoyshe</billAddress>\n" +
                "<signInfo>1565EDD94307E9E676A9278C763627A678F7FE694B6E647EB72BB648D48ED1B4</signInfo>\n" +
                "<remark></remark>\n" +
                "</respon>\n";

        Map<String, String> responseMap = ReadSalesResponse(readSale.toString());
        //System.out.println(responseMap);

        //System.out.println("Response Sale---" + responseMap);
    }

    static class Wonderland
    {
        public byte buf[];
        public int size;

        public Wonderland(byte b[], int s)
        {
            buf = b;
            size = s;
        }
    }

    public String getCardholderIp(String cardNumber)
    {
        String customerIpTable = "0";
        Connection conn = null;
        ResultSet rs = null;

        String bin = Functions.getFirstSix(cardNumber);
        String lastFour = Functions.getLastFour(cardNumber);
        try
        {
            conn = Database.getConnection();
            String query = "SELECT ipAddress FROM wl_iptable WHERE bin=? AND lastfour=? ";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1, bin);
            p.setString(2, lastFour);
            transactionLogger.error("getCardholderIp for wonderland----" + p);

            rs = p.executeQuery();
            if (rs.next())
            {
                customerIpTable = rs.getString("ipAddress");
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError in isIpAddressBlocked---", se);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SystemError in isIpAddressBlocked---", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return customerIpTable;
    }
}
