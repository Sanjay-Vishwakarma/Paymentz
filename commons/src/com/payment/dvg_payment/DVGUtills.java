package com.payment.dvg_payment;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
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
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 9/17/14
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class DVGUtills
{
    public final static String charset = "UTF-8";
    private static Logger log = new Logger(DVGUtills.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(DVGUtills.class.getName());

    public static String doPostHTTPSURLConnection(String strURL, String req) throws PZTechnicalViolationException
    {
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        URLConnection con = null;

        try
        {
            //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            //java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            URL url = new URL(strURL);
            try
            {
                con = url.openConnection();
                con.setConnectTimeout(12000);
                con.setReadTimeout(12000);
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
            PZExceptionHandler.raiseTechnicalViolationException("DVGUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, ex.getMessage(), ex.getCause());
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
    static class Dvg
    {
        public byte buf[];
        public int size;

        public Dvg(byte b[], int s)
        {
            buf = b;
            size = s;
        }
    }
    private static String ReadByteStream(BufferedInputStream in) throws PZTechnicalViolationException
    {
        LinkedList<Dvg> bufList = new LinkedList<Dvg>();
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
                bufList.add(new Dvg(buf, num));
            } while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<Dvg> p = bufList.listIterator(); p.hasNext();)
            {
                Dvg b = p.next();
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

    public static Map<String,String> ReadSalesResponse(String str)throws PZTechnicalViolationException
    {
        Document doc = createDocumentFromString(str);

        Map<String, String> responseMap  = new HashMap<String, String>();

        doc.getDocumentElement().getNodeName();
        NodeList nList = doc.getElementsByTagName("DIVITIA_BXML");
        nList = doc.getElementsByTagName("DIVITIA_RESPONSE");
        responseMap.put("VID",getTagValue("VID",((Element) nList.item(0))));
        responseMap.put("TrackID",getTagValue("TrackID",((Element) nList.item(0))));
        responseMap.put("TransactionId",getTagValue("TransactionId",((Element) nList.item(0))));
        responseMap.put("RESULT",getTagValue("RESULT",((Element) nList.item(0))));
        responseMap.put("ERRORNUMBER",getTagValue("ERRORNUMBER",((Element) nList.item(0))));
        responseMap.put("ERRORMESSAGE",getTagValue("ERRORMESSAGE",((Element) nList.item(0))));
        responseMap.put("AUTHCODE",getTagValue("AUTHCODE",((Element) nList.item(0))));
        responseMap.put("NOTE",getTagValue("NOTE",((Element) nList.item(0))));

        return responseMap;
    }
    private static String getTagValue(String sTag, Element eElement)
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

    public static Document createDocumentFromString(String xmlString ) throws PZTechnicalViolationException
    {
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
            log.error("Exception in createDocumentFromString of DVGUtill",pce);
            transactionLogger.error("Exception in createDocumentFromString of DVGUtill",pce);
            PZExceptionHandler.raiseTechnicalViolationException("DVGUtills.java","createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,pce.getMessage(),pce.getCause());
        }
        catch (SAXException e)
        {
            // TODO Auto-generated catch block
            log.error("Exception in createDocumentFromString DVGUtill",e);
            transactionLogger.error("Exception in createDocumentFromString DVGUtill",e);
            PZExceptionHandler.raiseTechnicalViolationException("DVGUtills.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.SAXEXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            log.error("Exception in createDocumentFromString DVGUtill",e);
            transactionLogger.error("Exception in createDocumentFromString DVGUtill",e);
            PZExceptionHandler.raiseTechnicalViolationException("DVGUtills.java","createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }
        return doc;
    }
    /*public static void main(String[] args)
    {

        String readXml= new String("<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<DIVITIA_BXML xmlns:xsi='http://www.w3.org/1999/XMLSchema-instance'\n" +
                "xsi:noNamespaceSchemaLocation='divitia.xsd'>\n" +
                "<DIVITIA_RESPONSE>\n" +
                "<VID>8880000</VID>\n" +
                "<TrackID>201410081225885</TrackID>\n" +
                "<TransactionId>201410081250891</TransactionId>\n" +
                "<RESULT>OK</RESULT>\n" +
                "<ERRORNUMBER></ERRORNUMBER>\n" +
                "<ERRORMESSAGE></ERRORMESSAGE>\n" +
                "<AUTHCODE></AUTHCODE>\n" +
                "<NOTE>Transaction Approved</NOTE>\n" +
                "</DIVITIA_RESPONSE>\n" +
                "</DIVITIA_BXML>");

        Document doc =null;

        Map<String, String> responseMap =  ReadSalesResponse(readXml.toString());
        System.out.println("map----"+responseMap);
        Set<String> keys = responseMap.keySet();
        System.out.println("  ");
        for(String key : keys)
        {
            System.out.println("  "+key+"="+responseMap.get(key));

        }
        String status = "fail";

            if(responseMap.get("RESULT")!=null && responseMap.get("RESULT").equals("OK"))
            {
                status = "success";
            }


        System.out.println("status---"+status);
    }*/
}
