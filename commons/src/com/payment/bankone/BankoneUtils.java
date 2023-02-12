package com.payment.bankone;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommRequestVO;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by Admin on 8/19/2017.
 */
public class BankoneUtils
{
    public final static String charset = "UTF-8";
    private static Logger log = new Logger(BankoneUtils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(BankoneUtils.class.getName());

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
                PZExceptionHandler.raiseTechnicalViolationException("BankoneUtils.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, io.getMessage(), io.getCause());
            }
            if(con instanceof HttpURLConnection)
            {
                ((HttpURLConnection)con).setRequestMethod("POST");
                ((HttpURLConnection)con).setRequestProperty("Content-Type","application/xml");
            }
            con.setDoInput(true);
            con.setDoOutput(true);


            //con.setRequestProperty("Content-length",String.valueOf (req.length()));
            con.setRequestProperty("Content-Type","application/xml");
            //con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows 98; DigExt)");

            out = new BufferedOutputStream(con.getOutputStream());
            byte outBuf[] = req.getBytes(charset);
            out.write(outBuf);

            out.close();

            in = new BufferedInputStream(con.getInputStream());
            result = ReadByteStream(in);
        } catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("BankoneUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("BankoneUtils.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (UnsupportedEncodingException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("BankoneUtils.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("BankoneUtils.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION,null,ex.getMessage(),ex.getCause());
        }
        finally
        {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("BankoneUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("BankoneUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }

    private static String ReadByteStream(BufferedInputStream in) throws PZTechnicalViolationException {
        LinkedList<Buf> bufList = new LinkedList<Buf>();
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
                bufList.add(new Buf(buf, num));
            } while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<Buf> p = bufList.listIterator(); p.hasNext();)
            {
                Buf b = p.next();
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
            PZExceptionHandler.raiseTechnicalViolationException("BankoneUtils.java", "ReadByteStream()", null, "common", "Technical Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, io.getMessage(), io.getCause());
        }
        return buffer;
    }

    public Comm3DResponseVO read3DResponse(String xmlResponse) throws PZTechnicalViolationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        String strValue = null;
        NodeList list=null;
        Document document = null;
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(xmlResponse)));
        }
        catch (ParserConfigurationException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("BankoneUtils.java","getQWIPIResponseVOInquiry()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SAXException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("BankoneUtils.java","getQWIPIResponseVOInquiry()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.SAXEXCEPTION,null,e.getMessage(),e.getCause());

        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("BankoneUtils.java","getQWIPIResponseVOInquiry()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
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
                /*System.out.println("url---"+getTagValue("targetUrl", element));
                System.out.println("result---" + getTagValue("result", element));*/
                if (functions.isValueNull(getTagValue("targetUrl", element)))
                {
                    comm3DResponseVO.setUrlFor3DRedirect(getTagValue("targetUrl", element));
                    if (functions.isValueNull(getTagValue("payId", element)))
                        comm3DResponseVO.setTransactionId(getTagValue("payId", element));
                    else
                        comm3DResponseVO.setTransactionId(getTagValue("payid", element));

                    comm3DResponseVO.setStatus("pending3DConfirmation");
                }
                else
                {
                    if(getTagValue("result", element).equalsIgnoreCase("UnSuccessful"))
                    {
                        comm3DResponseVO.setStatus("fail");
                    }
                    else
                    {
                        comm3DResponseVO.setStatus("success");
                    }
                    comm3DResponseVO.setErrorCode(getTagValue("responsecode", element));//response code
                    comm3DResponseVO.setResponseHashInfo(getTagValue("RRN", element));//rrn
                    comm3DResponseVO.setTransactionId(getTagValue("tranid", element));//tranid
                    comm3DResponseVO.setMerchantId(getTagValue("trackid", element));//trackid
                    comm3DResponseVO.setRemark(getTagValue("result", element) + " " + getTagValue("responsecode", element));//result
                    comm3DResponseVO.setDescription(getTagValue("result", element));//result
                }
            }

        }
        return comm3DResponseVO;
    }

    public CommResponseVO readResponse(String xmlResponse) throws PZTechnicalViolationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        String strValue = null;
        NodeList list=null;
        Document document = null;
        CommResponseVO commResponseVO = new CommResponseVO();
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(xmlResponse)));
        }
        catch (ParserConfigurationException e)
        {
            transactionLogger.error("ParserConfigurationException---",e);
            PZExceptionHandler.raiseTechnicalViolationException("BankoneUtils.java","getQWIPIResponseVOInquiry()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SAXException e)
        {
            transactionLogger.error("SAXException---",e);
            PZExceptionHandler.raiseTechnicalViolationException("BankoneUtils.java","getQWIPIResponseVOInquiry()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.SAXEXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException---",e);
            PZExceptionHandler.raiseTechnicalViolationException("BankoneUtils.java","getQWIPIResponseVOInquiry()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }

        Element rootElement = document.getDocumentElement();
        list = document.getElementsByTagName("response");

        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;
                String status = "fail";
                if(getTagValue("responsecode", element).equalsIgnoreCase("000"))
                    status = "success";
                commResponseVO.setStatus(status);
                commResponseVO.setErrorCode(getTagValue("responsecode", element));
                commResponseVO.setDescription(getTagValue("result", element));
                commResponseVO.setAuthCode(getTagValue("authcode", element));
                commResponseVO.setTransactionId(getTagValue("tranid", element));
            }
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

    public static void main(String[] args)
    {
        /*String x = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><result>\n" +
                "Successful</result><responsecode>000</responsecode><authcode>508227</authcode><RRN>000285508227</RRN><ECI>07</ECI><tranid>6473132833012227326</tranid><trackid>48559</trackid><terminalid>200</terminalid><udf1></udf1><udf2></udf2><udf3></udf3><udf4></udf4><udf5>Transaction Successful - </udf5></response>";*/



      //  String x = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?><response><targetUrl>https://secure.soft-connect.biz/3DRedirect.aspx?</targetUrl><payid>a=1727714519413880015</payid></response>";

        String x = "<?xml version='1.0' encoding='UTF-8'?> <response><result>UnSuccessful</result><responsecode>591</responsecode><authcode></authcode><RRN>228472445303</RRN><tranid>1732719228472445303</tranid><trackid>1008527</trackid><terminalid>DOMSON01</terminalid><udf1></udf1><udf2></udf2><udf3></udf3><udf4></udf4><udf5></udf5></response>";

        BankoneUtils b = new BankoneUtils();
        try
        {
            Comm3DResponseVO c = b.read3DResponse(x);


        }
        catch (Exception e)
        {
            transactionLogger.error("Exception BankoneUtils ::::::",e);
        }
    }
}
class Buf
{
    public byte buf[];
    public int size;

    public Buf(byte b[], int s)
    {
        buf = b;
        size = s;
    }
}