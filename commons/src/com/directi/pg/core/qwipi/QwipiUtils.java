package com.directi.pg.core.qwipi;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.valueObjects.QwipiResponseVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl;


/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: Aug 20, 2012
 * Time: 8:38:40 PM
 * To change this template use File | Settings | File Templates.
 * Copyright: Admin
 *
 */
public class QwipiUtils
{


    // character encoding
    public final static String charset = "UTF-8";

    private static Logger log = new Logger(QwipiUtils.class.getName());

    public static void main (String args[])
    {
        QwipiUtils q = new QwipiUtils();
        String jS = "{\"operation\":\"01\",\"resultCode\":\"1\",\"errorCode\":\"1009600\",\"orderId\":\"8889443071373\",\"merNo\":\"88894\",\"currency\":\"USD\",\"amount\":\"50.00\",\"billNo\":\"DSFSDF23\",\"dateTime\":\"20170808141847\",\"md5Info\":\"EA6077B92A7CAC85B05B8A92E5B11044\",\"remark\":\"Transaction failed by bank.\",\"billingDescriptor\":\"\"}";
        try
        {
            CommResponseVO c = q.readJsonResponse(jS);
        }
        catch (Exception e)
        {
        }
    }
    public String createQueryStr(String[] valueVo, String[] keyVo) {

        Map<String, String> map = new TreeMap<String, String>();
        for (int i = 0; i < keyVo.length; i++) {
            map.put(keyVo[i], valueVo[i]);
        }

        return joinMapValue(map, '&');
    }


    /**
     *
     * @param map
     * @param connector
     * @return
     */
    public static String joinMapValue(Map<String, String> map, char connector) {
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

    /**
     *
     * @param map
     * @param connector
     * @return
     */
    public String joinMapValueBySpecial(Map<String, String> map, char connector) {
        StringBuffer b = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {

            b.append(entry.getKey());
            b.append('=');
            if (entry.getValue() != null) {
                try {
                    b.append(java.net.URLEncoder.encode(entry.getValue(),charset));
                } catch (UnsupportedEncodingException e) {

                    log.error("UnsupportedEncodingException--->",e);
                }
            }
            b.append(connector);
        }
        return b.toString();
    }


    /**
     *
     * @param strURL
     * @param req
     * @return
     */
    public String doPostURLConnection(String strURL, String req) throws PZTechnicalViolationException{
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        URLConnection con = null;
        try
        {
            URL url = new URL(strURL);
            try
            {
                con = url.openConnection();
                con.setConnectTimeout(120000);
                con.setReadTimeout(120000);
            }
            catch (IOException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java", "doPostURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE,null, io.getMessage(), io.getCause());
            }

            /*		if (con instanceof HttpsURLConnection) {
                   ((HttpsURLConnection) con).setHostnameVerifier(new HostnameVerifier() {
                       @Override
                       public boolean verify(String hostname, SSLSession session) {
                           return true;
                       }
                   });
               }*/
            con.setUseCaches(false);
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
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java", "doPostURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java","doPostURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (UnsupportedEncodingException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java","doPostURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java","doPostURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION,null,ex.getMessage(),ex.getCause());
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java", "doPostURLConnection()", null, "common", "Technical Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java", "doPostURLConnection()", null, "common", "Technical Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }

    public static String doPostHTTPSURLConnection(String strURL, String req) throws PZTechnicalViolationException
    {
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        URLConnection con = null;

        try {

            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            URL url = new URL(strURL);
            try
            {
                con = url.openConnection();
            }
            catch (IOException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE,null, io.getMessage(), io.getCause());
            }
            if(con instanceof HttpURLConnection)
            {
                ((HttpURLConnection)con).setRequestMethod("POST");
            }
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
        } catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (UnsupportedEncodingException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION,null,ex.getMessage(),ex.getCause());
        }
        finally
        {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }

    private static String ReadByteStream(BufferedInputStream in) throws PZTechnicalViolationException {
        LinkedList<QwipiBuf> bufList = new LinkedList<QwipiBuf>();
        int size = 0;
        byte buf[];
        String buffer = null;
        try
        {
            do
            {
                buf = new byte[128];
                int num = in.read(buf);
                if (num == -1)
                    break;
                size += num;
                bufList.add(new QwipiBuf(buf, num));
            } while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<QwipiBuf> p = bufList.listIterator(); p.hasNext();)
            {
                QwipiBuf b = p.next();
                for (int i = 0; i < b.size;)
                {
                    buf[pos] = b.buf[i];
                    i++;
                    pos++;
                }

            }
            buffer = new String(buf,charset);
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java", "ReadByteStream()", null, "common", "Technical Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, io.getMessage(), io.getCause());
        }
        return buffer;
    }


    public static String[] getResArr(String str) {
        String regex = "(.*?cupReserved\\=)(\\{[^}]+\\})(.*)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);

        String reserved = "";
        if (matcher.find()) {
            reserved = matcher.group(2);
        }

        String result = str.replaceFirst(regex, "$1$3");
        String[] resArr = result.split("&");
        for (int i = 0; i < resArr.length; i++) {
            if ("cupReserved=".equals(resArr[i])) {
                resArr[i] += reserved;
            }
        }
        return resArr;
    }

    public QwipiResponseVO getQWIPIResponseVOInquiry(String xmlResponseString)throws PZTechnicalViolationException
    {
        QwipiResponseVO res= new QwipiResponseVO();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        String strValue = null;
        NodeList list=null;
        Document document = null;
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(xmlResponseString)));
        }
        catch (ParserConfigurationException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtils.java","getQWIPIResponseVOInquiry()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SAXException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtils.java","getQWIPIResponseVOInquiry()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.SAXEXCEPTION,null,e.getMessage(),e.getCause());

        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtils.java","getQWIPIResponseVOInquiry()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }

        Element rootElement = document.getDocumentElement();
        strValue = getTagValue("response", rootElement);
        list = document.getElementsByTagName("response");
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;
                res.setResult(getTagValue("result", element));
                res.setCode(getTagValue("code", element));
                res.setText(getTagValue("text", element));
            }
        }

        list = document.getElementsByTagName("order");
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;
                res.setId(getTagValue("id",element));
                res.setStatus(getTagValue("status",element));
                res.setBillNo(getTagValue("billNo",element));
                res.setAmount(getTagValue("amount",element));
                res.setDateTime(getTagValue("date",element));
                res.setCurrency(getTagValue("currency",element));

            }
        }
        list = document.getElementsByTagName("refund");
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;
                res.setRefundCode(getTagValue("code",element));
                res.setRefundText(getTagValue("text",element));
                res.setRefundAmount(getTagValue("amount",element));
                res.setRefundDate(getTagValue("date",element));
                res.setRefundRemark(getTagValue("remark",element));
                res.setRefundMessage(getTagValue("message",element));

            }
        }
        list = document.getElementsByTagName("chargeback");
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;
                res.setCbCode(getTagValue("code",element));
                res.setCbText(getTagValue("text",element));
            }
        }
        list = document.getElementsByTagName("settle");
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;
                res.setStCode(getTagValue("code",element));
                res.setStText(getTagValue("text",element));
            }
        }

        return res;
    }

    public QwipiResponseVO getQWIPIResponseVO(String xmlResponseString)throws PZTechnicalViolationException
    {
        QwipiResponseVO res= new QwipiResponseVO();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        String strValue = null;
        Document document = null;
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(xmlResponseString)));
        }
        catch (ParserConfigurationException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtils.java","getQWIPIResponseVO()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SAXException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtils.java","getQWIPIResponseVO()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.SAXEXCEPTION,null,e.getMessage(),e.getCause());

        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtils.java","getQWIPIResponseVO()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }

        Element rootElement = document.getDocumentElement();
        strValue = getTagValue("operation", rootElement);
        if(strValue !=null)
        {
            res.setOperation(strValue.trim());
        }
        strValue = getTagValue("resultCode", rootElement);
        if(strValue !=null)
        {
            res.setResultCode(strValue.trim());
        }
        strValue = getTagValue("errorCode", rootElement);
        if(strValue !=null)
        {
            res.setErrorCode(strValue.trim());
        }
        strValue = getTagValue("orderId", rootElement);
        if(strValue !=null)
        {
            res.setPaymentOrderNo(strValue.trim());
        }
        strValue = getTagValue("merNo", rootElement);
        if(strValue !=null)
        {
            res.setMerNo(strValue.trim());
        }
        strValue = getTagValue("billNo", rootElement);
        if(strValue !=null)
        {
            res.setBillNo(strValue.trim());
        }
        strValue = getTagValue("currency", rootElement);
        if(strValue !=null)
        {
            res.setCurrency(strValue.trim());
        }
        strValue = getTagValue("amount", rootElement);
        if(strValue !=null)
        {
            res.setAmount(strValue.trim());
        }
        strValue = getTagValue("dateTime", rootElement);
        if(strValue !=null)
        {
            res.setDateTime(strValue.trim());
        }
        strValue = getTagValue("paymentOrderNo", rootElement);
        if(strValue !=null)
        {
            res.setPaymentOrderNo(strValue.trim());
        }
        strValue = getTagValue("remark", rootElement);
        if(strValue !=null)
        {
            res.setRemark(strValue.trim());
        }
        strValue = getTagValue("md5Info", rootElement);
        if(strValue !=null)
        {
            res.setMd5Info(strValue.trim());
        }
        strValue = getTagValue("billingDescriptor", rootElement);
        if(strValue !=null)
        {
            res.setBillingDescriptor(strValue.trim());
        }
        strValue = getTagValue("refundAmount", rootElement);
        if(strValue !=null)
        {
            res.setRefundAmount(strValue.trim());
        }

        if(res.getResultCode()!=null)
        {
            if(res.getResultCode().equals("0"))
            {
                res.setStatus("success");
            }
            else if(res.getResultCode().equals("1"))
            {
                res.setStatus("fail");
            }
            else if(res.getResultCode().equals("2"))
            {
                res.setStatus("Processing");
            }
        }
        return res;
    }

    public QwipiResponseVO getQWIPIResponseVOI(String xmlResponseString)throws PZTechnicalViolationException
    {
        QwipiResponseVO res= new QwipiResponseVO();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        String strValue = null;
        NodeList list=null;
        Document document = null;
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(xmlResponseString)));
        }
        catch (ParserConfigurationException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtils.java","getQWIPIResponseVOInquiry()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SAXException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtils.java","getQWIPIResponseVOInquiry()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.SAXEXCEPTION,null,e.getMessage(),e.getCause());

        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtils.java","getQWIPIResponseVOInquiry()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }

        Element rootElement = document.getDocumentElement();
        strValue = getTagValue("response", rootElement);
        list = document.getElementsByTagName("response");
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;
                res.setOperation(getTagValue("operation", element));
                res.setResultCode(getTagValue("resultCode", element));
                res.setErrorCode(getTagValue("errorCode", element));
                res.setPaymentOrderNo(getTagValue("orderId", element));
                res.setMerNo(getTagValue("merNo", element));
                res.setCurrency(getTagValue("currency", element));
                res.setAmount(getTagValue("amount", element));
                res.setBillNo(getTagValue("billNo", element));
                res.setDateTime(getTagValue("dateTime", element));
                res.setMd5Info(getTagValue("md5Info", element));
                res.setRemark(getTagValue("remark", element));
                res.setBillingDescriptor(getTagValue("billingDescriptor", element));
                res.setRefundAmount(getTagValue("amountRefund", element));
            }
        }
        if(res.getResultCode()!=null)
        {
            if(res.getResultCode().equals("0"))
            {
                res.setStatus("success");
            }
            else if(res.getResultCode().equals("1"))
            {
                res.setStatus("fail");
            }
            else if(res.getResultCode().equals("2"))
            {
                res.setStatus("Processing");
            }
        }
        return res;
    }

    public CommResponseVO readJsonResponse(String jsonResponseString)throws PZTechnicalViolationException
    {
        CommResponseVO commResponseVO = new CommResponseVO();
        try
        {
            JSONObject jObject = new JSONObject(jsonResponseString);

            if(jObject.getString("operation").equals("01"))
                commResponseVO.setTransactionType("sale");
            if(jObject.getString("operation").equals("02"))
                commResponseVO.setTransactionType("refund");

            if(jObject.getString("resultCode").equals("0"))
            {
                commResponseVO.setStatus("success");
                if(jObject.has("billingDescriptor"))
                    commResponseVO.setDescriptor(jObject.getString("billingDescriptor"));
            }
            else if(jObject.getString("resultCode").equals("1"))
            {
                commResponseVO.setStatus("fail");
            }
            else if(jObject.getString("resultCode").equals("2"))
            {
                commResponseVO.setStatus("Processing");
            }

            commResponseVO.setErrorCode(jObject.getString("errorCode"));
            commResponseVO.setTransactionId(jObject.getString("orderId"));
            commResponseVO.setAmount(jObject.getString("amount"));
            commResponseVO.setRemark(jObject.getString("remark"));
            commResponseVO.setDescription(jObject.getString("remark"));
            if(jObject.has("currency"))
                commResponseVO.setCurrency(jObject.getString("currency"));
            if(jObject.has("dateTime"))
                commResponseVO.setBankTransactionDate(jObject.getString("dateTime"));
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtils.java","getQWIPIResponseVOInquiry()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,e.getMessage(),e.getCause());
        }

        return commResponseVO;
    }

    public CommResponseVO readInquiryJsonResponse(String jsonResponseString)throws PZTechnicalViolationException
    {
        CommResponseVO commResponseVO = new CommResponseVO();
        try
        {
            JSONObject jObject = new JSONObject(jsonResponseString);

            if(jObject.getString("operation").equals("01"))
                commResponseVO.setTransactionType("sale");

            if(jObject.getString("resultCode").equals("0"))
            {
                commResponseVO.setStatus("success");
            }
            else if(jObject.getString("resultCode").equals("1"))
            {
                commResponseVO.setStatus("fail");
            }
            else if(jObject.getString("resultCode").equals("2"))
            {
                commResponseVO.setStatus("Processing");
            }

            commResponseVO.setErrorCode(jObject.getString("errorCode"));
            commResponseVO.setTransactionId(jObject.getString("orderId"));
            commResponseVO.setAmount(jObject.getString("amount"));
            commResponseVO.setRemark(jObject.getString("remark"));
            commResponseVO.setDescription(jObject.getString("remark"));
            commResponseVO.setCurrency(jObject.getString("currency"));
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtils.java","getQWIPIResponseVOInquiry()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,e.getMessage(),e.getCause());
        }

        return commResponseVO;
    }

    private String getTagValue(String sTag, Element eElement)
    {
        NodeList nlList = null;
        String value  ="";
        if(eElement!=null && eElement.getElementsByTagName(sTag)!=null && eElement.getElementsByTagName(sTag).item(0)!=null)
        {
            nlList =  eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        }
        if(nlList!=null && nlList.item(0)!=null)
        {
            Node nValue = (Node) nlList.item(0);
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

    public Hashtable getMidKeyForQwipi(String accountId)
    {
        Connection con = null;
        Hashtable qwipiMidDetails = new Hashtable();
        try
        {
            con = Database.getConnection();

            String q1 = "select midkey,isksnurl from gateway_accounts_qwipi where accountid=?";
            PreparedStatement p8 = con.prepareStatement(q1);
            p8.setString(1, accountId);
            ResultSet rs = p8.executeQuery();
            if (rs.next())
            {
                qwipiMidDetails.put("midkey",rs.getString("midkey").trim());
                qwipiMidDetails.put("isksnurlflag",rs.getString("isksnurl"));
            }
        }
        catch (SQLException e)
        {
            log.error("midkey is missing", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("Transaction.java","getMidKeyForQwipi()",null,"common","SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,null);
        }
        catch (SystemError se)
        {
            log.error("midkey is missing", se);
            PZExceptionHandler.raiseAndHandleGenericViolationException("Transaction.java", "getMidKeyForQwipi()", null, "common", "SQLException Thrown:::", null,se.getMessage(), se.getCause(), null, null);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return qwipiMidDetails;
    }
}

class QwipiBuf
{
    public byte buf[];
    public int size;

    public QwipiBuf(byte b[], int s)
    {
        buf = b;
        size = s;
    }
}