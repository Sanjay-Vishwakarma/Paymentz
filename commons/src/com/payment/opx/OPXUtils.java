package com.payment.opx;

import com.directi.pg.Logger;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
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
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 4/3/15
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class OPXUtils
{
    public final static String charset = "UTF-8";
    private static Logger log = new Logger(OPXUtils.class.getName());
    public static String doPostHTTPSURLConnection(String strURL, String req,String serviceKey,String routingKey) throws PZTechnicalViolationException
    {
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        URLConnection con = null;

        try
        {
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            URL url = new URL(strURL);
            try
            {
                con = url.openConnection();
                con.setConnectTimeout(120000);
                con.setReadTimeout(120000);
            }
            catch (SSLHandshakeException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("DVGUtills.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE,null, io.getMessage(), io.getCause());
            }
            if(con instanceof HttpURLConnection)
            {
                ((HttpURLConnection)con).setRequestMethod("POST");
            }
            con.setDoInput(true);
            con.setDoOutput(true);

            con.setRequestProperty("Content-Type","application/xml; charset=UTF-8");
            con.setRequestProperty("Service-Key",serviceKey);
            con.setRequestProperty("Routing-Key",routingKey);

            out = new BufferedOutputStream(con.getOutputStream());
            byte outBuf[] = req.getBytes(charset);
            out.write(outBuf);
            out.close();

            in = new BufferedInputStream(con.getInputStream());
            result = ReadByteStream(in);
        }
        catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("DVGUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null,me.getMessage(),me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("DVGUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (UnsupportedEncodingException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("DVGUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("DVGUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION, null,ex.getMessage(), ex.getCause());
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
                    PZExceptionHandler.raiseTechnicalViolationException("DVGUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                } catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("DVGUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }
    static class OPX
    {
        public byte buf[];
        public int size;

        public OPX(byte b[], int s)
        {
            buf = b;
            size = s;
        }
    }
    private static String ReadByteStream(BufferedInputStream in) throws PZTechnicalViolationException
    {
        LinkedList<OPX> bufList = new LinkedList<OPX>();
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
                bufList.add(new OPX(buf, num));
            } while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<OPX> p = bufList.listIterator(); p.hasNext();)
            {
                OPX b = p.next();
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
            PZExceptionHandler.raiseTechnicalViolationException("DVGUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, io.getMessage(), io.getCause());
        }

        return buffer;
    }

    public CommResponseVO getResponseVo(String xmlDOC)
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        NodeList list=null;
        Document document = null;
        CommResponseVO commResponseVO=new CommResponseVO();
        try
        {
            builder = factory.newDocumentBuilder();
            String status="";
            document = builder.parse(new InputSource(new StringReader(xmlDOC)));
            list = document.getElementsByTagName("ProcessDebitResponse");
            for (int i = 0; i < list.getLength(); i++)
            {
                Node nNode = list.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) nNode;
                    status=eElement.getAttribute("status");
                    log.debug(status);
                    commResponseVO.setStatus(eElement.getAttribute("status"));
                }
            }
                list = document.getElementsByTagName("transactionResults");
                for(int j=0; j<list.getLength();j++)
                {
                    Node node= list.item(j);
                    if(node.getNodeType()==node.ELEMENT_NODE)
                    {
                        Element element = (Element) node;
                        log.debug(element.getAttribute("txId"));
                        System.out.println(element.getAttribute("txId"));
                        commResponseVO.setStatus(element.getAttribute("status"));
                        commResponseVO.setTransactionId(element.getAttribute("txId"));
                        commResponseVO.setDescription(element.getAttribute("message"));
                        commResponseVO.setResponseTime(element.getAttribute("endDate"));
                    }
                }

        }
        catch (ParserConfigurationException e)
        {
            log.error("error",e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException(OPXUtils.class.getName(),"getResponseVo()",null,"common","Technical exception::::",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,e.getMessage(),e.getCause(),null,"Parsing response for OPX");
        }
        catch (SAXException e)
        {   log.error("error",e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException(OPXUtils.class.getName(),"getResponseVo()",null,"common","Technical exception::::",PZTechnicalExceptionEnum.SAXEXCEPTION,null,e.getMessage(),e.getCause(),null,"Parsing response for OPX");
        }
        catch (IOException e)
        {   log.error("error",e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException(OPXUtils.class.getName(),"getResponseVo()",null,"common","Technical exception::::",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause(),null,"Parsing response for OPX");
        }
        return commResponseVO;
    }
    public CommResponseVO getRefundResponseVo(String xmlDOC) throws PZTechnicalViolationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        NodeList list=null;
        Document document = null;
        CommResponseVO commResponseVO=new CommResponseVO();
        try
        {
            builder = factory.newDocumentBuilder();
            String status="";
            document = builder.parse(new InputSource(new StringReader(xmlDOC)));
            list = document.getElementsByTagName("ProcessRefundResponse");
            for (int i = 0; i < list.getLength(); i++)
            {
                Node nNode = list.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) nNode;
                    status=eElement.getAttribute("status");
                    log.debug(status);
                    commResponseVO.setStatus(eElement.getAttribute("status"));
                }
            }
            list = document.getElementsByTagName("refundResult");
            for(int j=0; j<list.getLength();j++)
            {
                Node node= list.item(j);
                if(node.getNodeType()==node.ELEMENT_NODE)
                {
                    Element element = (Element) node;
                    commResponseVO.setStatus(element.getAttribute("status"));
                    commResponseVO.setTransactionId(element.getAttribute("txId"));
                    commResponseVO.setDescription(element.getAttribute("message"));

                }
            }

        }
        catch (ParserConfigurationException e)
        {
            log.error("ParserConfigurationException while parsing the response::::",e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException(OPXUtils.class.getName(),"getRefundResponseVo()",null,"common","Technical exception::::",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,e.getMessage(),e.getCause(),null,"Parsing response for OPX");
        }
        catch (SAXException e)
        {
            log.error("SAXException while parsing the response::::",e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException(OPXUtils.class.getName(),"getRefundResponseVo()",null,"common","Technical exception::::",PZTechnicalExceptionEnum.SAXEXCEPTION,null,e.getMessage(),e.getCause(),null,"Parsing response for OPX");
        }
        catch (IOException e)
        {
            log.error("SAXException while parsing the response::::",e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException(OPXUtils.class.getName(),"getRefundResponseVo()",null,"common","Technical exception::::",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause(),null,"Parsing response for OPX");
        }
        return commResponseVO;
    }
}