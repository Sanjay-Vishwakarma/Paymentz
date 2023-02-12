package com.payment.processingmpi;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.payment.endeavourmpi.EnrollmentResponseVO;
import com.payment.endeavourmpi.ParesDecodeResponseVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.json.JSONObject;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by admin on 2/9/2018.
 */
public class ProcessingMpiUtils
{
    private final static String charset = "UTF-8";
    private static TransactionLogger transactionLogger=new TransactionLogger(ProcessingMpiUtils.class.getName());

    public static String doPostHTTPSURLConnection(String strURL, String request) throws PZTechnicalViolationException
    {
        String result=null;
        BufferedInputStream in=null;
        BufferedOutputStream out=null;
        URLConnection conn=null;

        try
        {
            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            System.setProperty("https.protocols", "TLSv1.2");
            URL url = new URL(strURL);
            try
            {
                conn = url.openConnection();
                conn.setConnectTimeout(120000);
                conn.setReadTimeout(120000);
            }
            catch (SSLHandshakeException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("ProcessingMpiUtils.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, io.getMessage(), io.getCause());
            }
            if(conn instanceof HttpURLConnection)
            {
                ((HttpURLConnection)conn).setRequestMethod("POST");
            }
            conn.setDoInput(true);
            conn.setDoOutput(true);

            out = new BufferedOutputStream(conn.getOutputStream());
            byte outBuf[] = request.getBytes(charset);
            out.write(outBuf);

            out.close();

            in = new BufferedInputStream(conn.getInputStream());
            result = ReadByteStream(in);
        }
        catch (UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException("ProcessingMpiUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null,ue.getMessage(), ue.getCause());
        }
        catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("ProcessingMpiUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON, null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("ProcessingMpiUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION, null, pe.getMessage(), pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("ProcessingMpiUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, ex.getMessage(), ex.getCause());
        }
        catch (PZConstraintViolationException pze)
        {
            PZExceptionHandler.raiseTechnicalViolationException("ProcessingMpiUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, pze.getMessage(), pze.getCause());
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
                    PZExceptionHandler.raiseTechnicalViolationException("ProcessingMpiUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                } catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("ProcessingMpiUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
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
        LinkedList<EndeavourMPI> bufList = new LinkedList<EndeavourMPI>();
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
                bufList.add(new EndeavourMPI(buf, num));
            }
            while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<EndeavourMPI> p = bufList.listIterator(); p.hasNext();)
            {
                EndeavourMPI b = p.next();
                for (int i = 0; i < b.size;)
                {
                    buf[pos] = b.buf[i];
                    i++;
                    pos++;
                }
            }
            buffer = new String(buf,charset);
        }
        catch (UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException("ProcessingMpiUtils.java", "ReadByteStream()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ue.getMessage(), ue.getCause());
        }
        catch (IOException ie)
        {
            PZExceptionHandler.raiseTechnicalViolationException("ProcessingMpiUtils.java", "ReadByteStream()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ie.getMessage(), ie.getCause());
        }
        return buffer;
    }

    public static Map<String, String> getQueryMap(String query)
    {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String[] p = param.split("=",2);
            String name = p[0];
            if (p.length > 1)
            {
                String value = p[1];
                map.put(name, value);
                transactionLogger.debug(name+":::"+value);
            }
        }
        return map;
    }

    public static Map<String, String> ReadParesResponse(String str) throws PZTechnicalViolationException
    {
        org.w3c.dom.Document doc = createDocumentFromString(str);

        Map<String, String> responseMap = new HashMap<String, String>();

        doc.getDocumentElement().getNodeName();
        NodeList nList = doc.getElementsByTagName("EPG");
        nList = doc.getElementsByTagName("Response");
        responseMap.put("signature", getTagValue("signature", ((org.w3c.dom.Element) nList.item(0))));
        responseMap.put("cavv", getTagValue("cavv", ((org.w3c.dom.Element) nList.item(0))));
        responseMap.put("purchAmount", getTagValue("purchAmount", ((org.w3c.dom.Element) nList.item(0))));
        responseMap.put("xid", getTagValue("xid", ((org.w3c.dom.Element) nList.item(0))));
        responseMap.put("MessageID", getTagValue("MessageID", ((org.w3c.dom.Element) nList.item(0))));
        responseMap.put("date", getTagValue("date", ((org.w3c.dom.Element) nList.item(0))));
        responseMap.put("status", getTagValue("status", ((org.w3c.dom.Element) nList.item(0))));
        responseMap.put("merID", getTagValue("merID", ((org.w3c.dom.Element) nList.item(0))));
        responseMap.put("exponent", getTagValue("exponent", ((org.w3c.dom.Element) nList.item(0))));
        responseMap.put("pan", getTagValue("pan", ((org.w3c.dom.Element) nList.item(0))));
        responseMap.put("cavvAlgorithm", getTagValue("cavvAlgorithm", ((org.w3c.dom.Element) nList.item(0))));
        responseMap.put("eci", getTagValue("eci", ((org.w3c.dom.Element) nList.item(0))));
        responseMap.put("trackid", getTagValue("trackid", ((org.w3c.dom.Element) nList.item(0))));
        return responseMap;
    }
    public static EnrollmentResponseVO getEnrollmentResponse(String response)throws Exception
    {
        EnrollmentResponseVO  responseVO=new EnrollmentResponseVO();
        JSONObject jsonObject=new JSONObject(response);
        JSONObject jsonObject1=null;
        JSONObject jsonObject2=null;

        String statusObj = jsonObject.getString("status");

        if(statusObj !=null){
            jsonObject1=new JSONObject(statusObj);
        }

        if(jsonObject1!=null){
            transactionLogger.error("json object code result::>"+jsonObject1.getString("code"));
            if ("200".equals(jsonObject1.getString("code"))){
                if(jsonObject1.has("message")){
                    responseVO.setResult(jsonObject1.getString("message"));
                }
                String resultObj = jsonObject.getString("result");
                if(resultObj !=null){
                    jsonObject2=new JSONObject(resultObj);
                }
            }
            else if ("500".equals(jsonObject1.getString("code"))){
                if(jsonObject1.has("message")){
                    responseVO.setResult(jsonObject1.getString("message"));
                }
            }

        }
        if(jsonObject2!=null){

            if(jsonObject2.has("MD")){
                responseVO.setMD(jsonObject2.getString("MD"));
            }
            if(jsonObject2.has("PaReq")){
                responseVO.setPAReq(jsonObject2.getString("PaReq"));
            }
            if(jsonObject2.has("acs_url")){
                responseVO.setAcsUrl(jsonObject2.getString("acs_url"));
            }
        }
        return responseVO;
    }
    public static ParesDecodeResponseVO getPaResDecodeResponse(String response)throws Exception
    {
        Functions functions= new Functions();
        ParesDecodeResponseVO responseVO=new ParesDecodeResponseVO();

        JSONObject jsonObject=new JSONObject(response);
        JSONObject jsonObject1=null;
        JSONObject jsonObject2=null;

        String statusObj = jsonObject.getString("status");
        String resultObj = jsonObject.getString("result");
        if(functions.isValueNull(statusObj) && statusObj.contains("{")){
            jsonObject1=new JSONObject(statusObj);
        }
        if(functions.isValueNull(resultObj) && statusObj.contains("{")){
            jsonObject2=new JSONObject(resultObj);
        }
        if(jsonObject1!=null){
            if(jsonObject1.has("message")){
                responseVO.setStatus(jsonObject1.getString("message"));
            }
            if(jsonObject1.has("secure_hash")){
                responseVO.setSignature(jsonObject1.getString("secure_hash"));
            }
        }
        if(jsonObject2!=null){

            if(jsonObject2.has("cavv")){
                responseVO.setCavv(jsonObject2.getString("cavv"));
            }
            if(jsonObject2.has("eci")){
                responseVO.setEci(jsonObject2.getString("eci"));
            }
            if(jsonObject2.has("xid")){
                responseVO.setXid(jsonObject2.getString("xid"));
            }
        }
        return responseVO;
    }

    private static String getTagValue(String sTag, org.w3c.dom.Element eElement)
    {
        NodeList nlList = null;
        String value = "";
        if (eElement != null && eElement.getElementsByTagName(sTag) != null && eElement.getElementsByTagName(sTag).item(0) != null)
        {
            nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        }
        if (nlList != null && nlList.item(0) != null)
        {
            Node nValue = (Node) nlList.item(0);
            value = nValue.getNodeValue();
        }
        return value;
    }

    private static String getAttributeValue(org.w3c.dom.Element eElement, String sTag)
    {
        String value = "";
        if (eElement != null)
        {
            value = eElement.getAttribute(sTag);
        }
        return value;
    }

    public static org.w3c.dom.Document createDocumentFromString(String xmlString) throws PZTechnicalViolationException
    {
        org.w3c.dom.Document doc = null;
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            doc = docBuilder.parse(new InputSource(new StringReader(xmlString)));
        }
        catch (ParserConfigurationException pce)
        {
            transactionLogger.error("Exception in createDocumentFromString of ProcessingMpiUtils", pce);
            PZExceptionHandler.raiseTechnicalViolationException("ProcessingMpiUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e)
        {
            transactionLogger.error("Exception in createDocumentFromString ProcessingMpiUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("ProcessingMpiUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.SAXEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            transactionLogger.error("Exception in createDocumentFromString ProcessingMpiUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("ProcessingMpiUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return doc;
    }

    static class EndeavourMPI
    {
        public byte buf[];
        public int size;

        public EndeavourMPI(byte b[], int s)
        {
            buf = b;
            size = s;
        }
    }

}
