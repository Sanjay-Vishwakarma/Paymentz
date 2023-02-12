package com.payment.apcoFastpay;

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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by Admin on 5/21/18.
 */
public class ApcoFastpayUtils
{

    private final static String charset = "UTF-8";
    private static TransactionLogger transactionLogger=new TransactionLogger(ApcoFastpayUtils.class.getName());

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
                conn.setRequestProperty("Content-Type", "text/xml");
            }
            catch (SSLHandshakeException io){
                transactionLogger.error("SSLHandshakeException --->",io);
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
            transactionLogger.error("UnsupportedEncodingException --->",ue);
            PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null,ue.getMessage(), ue.getCause());
        }
        catch (MalformedURLException me){
            transactionLogger.error("MalformedURLException --->",me);
            PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON, null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe){
            transactionLogger.error("ProtocolException --->",pe);
            PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION, null, pe.getMessage(), pe.getCause());
        }
        catch (IOException ex){
            transactionLogger.error("IOException --->",ex);
            PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, ex.getMessage(), ex.getCause());
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
                    PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
            if (in != null){
                try{
                    in.close();
                } catch (IOException e){
                    transactionLogger.error("IOException finally in.close --->",e);
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
            transactionLogger.error("UnsupportedEncodingException -->",ue);
            PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ue.getMessage(), ue.getCause());
        }
        catch (IOException ie){
            transactionLogger.error("IOException -->",ie);
            PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ie.getMessage(), ie.getCause());
        }
        return buffer;
    }

    public static Map<String, String> readApcopayRedirectionXMLReponse(String str) throws Exception
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

    public static  String getCardType(String cardTypeid){
        String cardType="";
        if(cardTypeid.equals("VISA")){
            cardType="VISA";
        }else if(cardTypeid.equals("MC")){
            cardType="MASTERCARD";
        }
        return cardType;
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
