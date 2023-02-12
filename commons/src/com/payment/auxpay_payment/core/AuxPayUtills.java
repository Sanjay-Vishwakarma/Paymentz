package com.payment.auxpay_payment.core;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZConstraintViolationException;
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

import static com.payment.dvg_payment.DVGUtills.createDocumentFromString;

/**
 * Created by Nikita on 16/9/15.
 */
public class AuxPayUtills
{
    private final static String charset = "UTF-8";
    private static Logger log= new Logger(AuxPayUtills.class.getName());
    private static TransactionLogger transactionLogger=new TransactionLogger(AuxPayUtills.class.getName());

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
            try
            {
                conn = url.openConnection();
                conn.setConnectTimeout(120000);
                conn.setReadTimeout(120000);
            }
            catch (SSLHandshakeException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("AuxPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE,null, io.getMessage(), io.getCause());
            }
            if(conn instanceof HttpURLConnection)
            {
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
        catch (UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException("AuxPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null,ue.getMessage(), ue.getCause());
        }
        catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("AuxPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON, null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("AuxPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION, null, pe.getMessage(), pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("AuxPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, ex.getMessage(), ex.getCause());
        }

        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("PZConstraintViolationException In AuxPayUtills :::: ",e);
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
                    PZExceptionHandler.raiseTechnicalViolationException("AuxPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                } catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("AuxPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }
    static class AuxPay
    {
        public byte buf[];
        public int size;

        public AuxPay(byte b[], int s)
        {
            buf = b;
            size = s;
        }
    }
    private static String ReadByteStream(BufferedInputStream in) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        LinkedList<AuxPay> bufList = new LinkedList<AuxPay>();
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
                bufList.add(new AuxPay(buf, num));
            }
            while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<AuxPay> p = bufList.listIterator(); p.hasNext();)
            {
                AuxPay b = p.next();
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
            PZExceptionHandler.raiseTechnicalViolationException("AuxPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ue.getMessage(), ue.getCause());
        }
        catch (IOException ie)
        {
            PZExceptionHandler.raiseTechnicalViolationException("AuxPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ie.getMessage(), ie.getCause());
        }
        return buffer;
    }
    public static Map<String,String> readStepOneResponse(String str) throws PZTechnicalViolationException
    {
        Document doc = createDocumentFromString(str);

        Map<String, String> responseMap  = new HashMap<String, String>();

        doc.getDocumentElement().getNodeName();
        NodeList nList = null;
        nList = doc.getElementsByTagName("wallet");
        responseMap.put("error_code",getTagValue("error_code",((Element) nList.item(0))));
        responseMap.put("error_msg",getTagValue("error_msg",((Element) nList.item(0))));
        responseMap.put("sessionid",getTagValue("sessionid",((Element) nList.item(0))));
        responseMap.put("expires",getTagValue("expires",((Element) nList.item(0))));

        return responseMap;
    }
    public static Map<String,String> readStepTwoResponse(String str) throws PZTechnicalViolationException
    {
        Document doc = createDocumentFromString(str);

        Map<String, String> responseMap  = new HashMap<String, String>();

        doc.getDocumentElement().getNodeName();
        NodeList nList = null;
        nList = doc.getElementsByTagName("wallet");
        responseMap.put("error_code",getTagValue("error_code",((Element) nList.item(0))));
        responseMap.put("error_msg",getTagValue("error_msg",((Element) nList.item(0))));
        responseMap.put("sessionid", getTagValue("sessionid", ((Element) nList.item(0))));

        /*if(getTagValue("error_code",((Element) nList.item(0)))==null && getTagValue("error_code",((Element) nList.item(0)))=="")
        {
            responseMap.put("sessionid", getTagValue("sessionid", ((Element) nList.item(0))));
            responseMap.put("expires", getTagValue("expires", ((Element) nList.item(0))));
        }*/
        return responseMap;
    }
    public static Map<String,String> readStepThreeResponse(String str) throws PZTechnicalViolationException
    {
        Document doc = createDocumentFromString(str);

        Map<String, String> responseMap  = new HashMap<String, String>();

        doc.getDocumentElement().getNodeName();
        NodeList nList = null;
        nList = doc.getElementsByTagName("wallet");
        responseMap.put("error_code",getTagValue("error_code",((Element) nList.item(0))));
        responseMap.put("error_msg",getTagValue("error_msg",((Element) nList.item(0))));
        responseMap.put("sessionid", getTagValue("sessionid", ((Element) nList.item(0))));
        responseMap.put("merchanttxid", getTagValue("merchanttxid", ((Element) nList.item(0))));
        responseMap.put("wallettxid", getTagValue("wallettxid", ((Element) nList.item(0))));
        responseMap.put("amount", getTagValue("amount", ((Element) nList.item(0))));
        responseMap.put("error", getTagValue("error", ((Element) nList.item(0))));
        responseMap.put("errormessage",getTagValue("errormessage",((Element) nList.item(0))));
        responseMap.put("txtimestamp", getTagValue("txtimestamp", ((Element) nList.item(0))));
        responseMap.put("redirecturl", getTagValue("redirecturl", ((Element) nList.item(0))));
        responseMap.put("vouchercode", getTagValue("vouchercode", ((Element) nList.item(0))));
        responseMap.put("voucherpin", getTagValue("voucherpin", ((Element) nList.item(0))));

        /*if(getTagValue("error_code",((Element) nList.item(0)))==null && getTagValue("error_code",((Element) nList.item(0)))=="")
        {
            responseMap.put("sessionid", getTagValue("sessionid", ((Element) nList.item(0))));
            responseMap.put("expires", getTagValue("expires", ((Element) nList.item(0))));
        }*/
        return responseMap;
    }
    public static Map<String,String> readStoreCardResponse(String str) throws PZTechnicalViolationException
    {
        Document doc = createDocumentFromString(str);

        Map<String, String> responseMap  = new HashMap<String, String>();

        doc.getDocumentElement().getNodeName();
        NodeList nList = null;
        nList = doc.getElementsByTagName("wallet");
        responseMap.put("error",getTagValue("error",((Element) nList.item(0))));
        responseMap.put("errormessage",getTagValue("errormessage",((Element) nList.item(0))));
        responseMap.put("sessionid", getTagValue("sessionid", ((Element) nList.item(0))));
        responseMap.put("customercardid", getTagValue("customercardid", ((Element) nList.item(0))));

        /*if(getTagValue("error_code",((Element) nList.item(0)))==null && getTagValue("error_code",((Element) nList.item(0)))=="")
        {
            responseMap.put("sessionid", getTagValue("sessionid", ((Element) nList.item(0))));
            responseMap.put("expires", getTagValue("expires", ((Element) nList.item(0))));
        }*/
        return responseMap;
    }
    private static String getTagValue(String Tag, Element eElement)
    {
        NodeList nlList = null;
        String value  ="";
        if(eElement!=null && eElement.getElementsByTagName(Tag)!=null && eElement.getElementsByTagName(Tag).item(0)!=null)
        {
            nlList =  eElement.getElementsByTagName(Tag).item(0).getChildNodes();
        }
        if(nlList!=null && nlList.item(0)!=null)
        {
            Node nValue = nlList.item(0);
            value =	nValue.getNodeValue();
        }
        return value;
    }
    public static Document createDocumentFromString(String xmlString ) throws PZTechnicalViolationException
    {
        Document document = null;
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            document = docBuilder.parse(new InputSource(new StringReader( xmlString ) ));
        }
        catch (ParserConfigurationException pce)
        {
            log.error("Exception in createDocumentFromString of AuxPayUtill",pce);
            transactionLogger.error("Exception in createDocumentFromString of AuxPayUtill",pce);
            PZExceptionHandler.raiseTechnicalViolationException("AuxPayUtills.java","createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,pce.getMessage(),pce.getCause());
        }
        catch (SAXException e)
        {
            log.error("Exception in createDocumentFromString AuxPayUtill",e);
            transactionLogger.error("Exception in createDocumentFromString AuxPayUtill",e);
            PZExceptionHandler.raiseTechnicalViolationException("AuxPayUtills.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.SAXEXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            log.error("Exception in createDocumentFromString AuxPayUtill",e);
            transactionLogger.error("Exception in createDocumentFromString AuxPayUtill",e);
            PZExceptionHandler.raiseTechnicalViolationException("AuxPayUtills.java","createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }
        return document;
    }

    public static void main(String[] args) throws PZTechnicalViolationException
    {
        String xml = "<wallet version=\"2.0\">\n" +
                "<response type=\"CreateSession\">\n" +
                "<sessionid>QH9HPV6BQN5829893RFWX15GERQW</sessionid>\n" +
                "<expires>2015-09-19T06:58:29</expires>\n" +
                "</response>\n" +
                "</wallet>";

        String xml2 = "<wallet version=\"2.0\">\n" +
                "<response type=\"CustomerBillingInfo\">\n" +
                "<sessionid>QH9HPV6BQN5829893RFWX15GERQW</sessionid>\n" +
                "<extusername>-</extusername>\n" +
                "<title>-</title>\n" +
                "<firstname>Matthew</firstname>\n" +
                "<lastname>Murdoch</lastname>\n" +
                "<email>matt@murdoch.com</email>\n" +
                "</response>\n" +
                "</wallet>";

        String errorXml = "<wallet version=\"2.0\">\n" +
                "<response type=\"CustomerBillingInfo\">\n" +
                "<error>\n" +
                "<error_code>5202</error_code>\n" +
                "<error_msg>Input string was not in a correct format.</error_msg>\n" +
                "</error>\n" +
                "</response>\n" +
                "</wallet>";

        String cardXml = "<wallet version=\"2.0\">\n" +
                "<response type=\"PaymentNewCard\">\n" +
                "<sessionid>EX9NEP14WIL3819193GIEJ15FXYOW</sessionid>\n" +
                "<merchanttxid>TX0001</merchanttxid>\n" +
                "<wallettxid>945011</wallettxid>\n" +
                "<txtimestamp>2015-09-18T14:38:20</txtimestamp>\n" +
                "<amount>43.00</amount>\n" +
                "<currency>GBP</currency>\n" +
                "<threedsecure>0</threedsecure>\n" +
                "<pares></pares>\n" +
                "<error>0</error>\n" +
                "<errormessage></errormessage>\n" +
                "</response>\n" +
                "</wallet>";

        String statusXml =  "<wallet version=\"2.0\">\n" +
                "<response type=\"transactionstatus\">\n" +
                "<sessionid>LM9NHK9JHA244343CDIF15BFMNW</sessionid>\n" +
                "<merchanttxid>11257</merchanttxid>\n" +
                "<wallettxid>960290</wallettxid>\n" +
                "<txtimestamp>2015-09-21T13:17:11</txtimestamp>\n" +
                "<amount>89.00</amount>\n" +
                "<currency>GBP</currency>\n" +
                "<transactiontype>DEPOSIT</transactiontype>\n" +
                "<error>0</error>\n" +
                "<errormessage></errormessage>\n" +
                "</response>\n" +
                "</wallet>";

        String threeds = "<wallet version=\"2.0\">\n" +
                "<response type=\"PaymentNewCard\">\n" +
                "<sessionid>IW11HDF9JNL4231863KFTW15BJOUW</sessionid>\n" +
                "<threedsecure>\n" +
                "<redirecturl>https://directapi2.londonmultigames.com/Emex3DSecure.ashx?sessionid=IW11HDF9JNL4231863KFTW15BJOUW&amp;txtype=PaymentNewCard</redirecturl>\n" +
                "</threedsecure>\n" +
                "</response>\n" +
                "</wallet>";

        String frontEnd = "<wallet version=\"2.0\">\n" +
                "<response type=\"PaymentNewCard\">\n" +
                "<sessionid>EU11KMN10CYE4958870YMZM15WVFPW</sessionid>\n" +
                "<merchanttxid>22927</merchanttxid>\n" +
                "<wallettxid>1392643</wallettxid>\n" +
                "<txtimestamp>2015-11-24T10:50:03</txtimestamp>\n" +
                "<amount>60.00</amount>\n" +
                "<currency>GBP</currency>\n" +
                "<error>0</error>\n" +
                "<errormessage>Failed</errormessage>\n" +
                "<md5sig>C3312F1C069A8EFE4B1EAB8A81C73A14</md5sig>\n" +
                "</response>\n" +
                "</wallet>";

        String store = "<wallet version=\"2.0\">\n" +
                "<response type=\"StoreNewCard\">  \n" +
                "<sessionid>DN11DQV16FHT952447IXGG15ELIQW</sessionid>\n" +
                "<customercardid>1088236</customercardid>\n" +
                "<error>0</error>\n" +
                "<errormessage></errormessage>\n" +
                "</response>\n" +
                "</wallet>";



        try
        {
            Map<String, String> responseMap = readStoreCardResponse(store.toString());
           /* System.out.println("------"+responseMap.get("error"));
            System.out.println("------"+responseMap.get("errormessage"));*/

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception In AuxPayUtills :::: ", e);
        }
    }
}
