package com.payment.borgun.core;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by ThinkPadT410 on 8/8/2016.
 */
public class BorgunUtills
{
    private final static String charset = "UTF-8";
    private static Logger log= new Logger(BorgunUtills.class.getName());
    private static TransactionLogger transactionLogger=new TransactionLogger(BorgunUtills.class.getName());

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
            URL url = new URL(strURL);
            try
            {
                conn = url.openConnection();
                conn.setConnectTimeout(120000);
                conn.setReadTimeout(120000);
            }
            catch (SSLHandshakeException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("BorgunUtills.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, io.getMessage(), io.getCause());
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
            PZExceptionHandler.raiseTechnicalViolationException("BorgunUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null,ue.getMessage(), ue.getCause());
        }
        catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("BorgunUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON, null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("BorgunUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION, null, pe.getMessage(), pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("BorgunUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, ex.getMessage(), ex.getCause());
        }
        catch (PZConstraintViolationException pze)
        {
            PZExceptionHandler.raiseTechnicalViolationException("BorgunUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, pze.getMessage(), pze.getCause());
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
                    PZExceptionHandler.raiseTechnicalViolationException("BorgunUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                } catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("BorgunUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }
    static class Borgun
    {
        public byte buf[];
        public int size;

        public Borgun(byte b[], int s)
        {
            buf = b;
            size = s;
        }
    }
    private static String ReadByteStream(BufferedInputStream in) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        LinkedList<Borgun> bufList = new LinkedList<Borgun>();
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
                bufList.add(new Borgun(buf, num));
            }
            while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<Borgun> p = bufList.listIterator(); p.hasNext();)
            {
                Borgun b = p.next();
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
            PZExceptionHandler.raiseTechnicalViolationException("BorgunUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ue.getMessage(), ue.getCause());
        }
        catch (IOException ie)
        {
            PZExceptionHandler.raiseTechnicalViolationException("BorgunUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ie.getMessage(), ie.getCause());
        }
        return buffer;
    }
    public static Map<String,String> ReadInquiryResponse(String str)throws PZTechnicalViolationException
    {
        Document doc = createDocumentFromString(str);

        Map<String, String> responseMap  = new HashMap<String, String>();

        Element rootElement = doc.getDocumentElement();
        NodeList nList = doc.getElementsByTagName("Transaction");
        responseMap.put("ActionCode",getTagValue("ActionCode",((Element) nList.item(0))));
        for (int i = 0; i < nList.getLength(); i++)
        {
            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;

                if(getTagValue("TransactionType", element).equalsIgnoreCase("5"))
                {
                    responseMap.put("auth",getTagValue("TransactionType", element));
                }
                if(getTagValue("TransactionType", element).equalsIgnoreCase("3"))
                {
                    responseMap.put("refund",getTagValue("TransactionType", element));
                }
                if(getTagValue("TransactionType", element).equalsIgnoreCase("1"))
                {
                    responseMap.put("sale",getTagValue("TransactionType", element));
                }
            }
            responseMap.put("TransactionNumber"+i,getTagValue("TransactionNumber",((Element) nList.item(i))));
            responseMap.put("BatchNumber"+i,getTagValue("BatchNumber",((Element) nList.item(i))));
            responseMap.put("TransactionDate"+i,getTagValue("TransactionDate",((Element) nList.item(i))));
            responseMap.put("PAN"+i,getTagValue("PAN",((Element) nList.item(i))));
            responseMap.put("RRN"+i,getTagValue("RRN",((Element) nList.item(i))));
            responseMap.put("ActionCode"+i,getTagValue("ActionCode",((Element) nList.item(i))));
            responseMap.put("AuthorizationCode"+i,getTagValue("AuthorizationCode",((Element) nList.item(i))));
            responseMap.put("TrAmount"+i,getTagValue("TrAmount",((Element) nList.item(i))));
            responseMap.put("TrCurrency"+i,getTagValue("TrCurrency",((Element) nList.item(i))));
            responseMap.put("Status"+i,getTagValue("Status",((Element) nList.item(i))));
            responseMap.put("TerminalNr"+i,getTagValue("TerminalNr",((Element) nList.item(i))));
        }

        return responseMap;
    }

    private static String getTagValue(String trackID, Element eElement)
    {
        NodeList nlList = null;
        String value  ="";
        if(eElement!=null && eElement.getElementsByTagName(trackID)!=null && eElement.getElementsByTagName(trackID).item(0)!=null)
        {
            nlList =  eElement.getElementsByTagName(trackID).item(0).getChildNodes();
        }
        if(nlList!=null && nlList.item(0)!=null)
        {
            Node nValue = (Node) nlList.item(0);
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
            log.error("Exception in createDocumentFromString of BorgunUtill",pce);
            transactionLogger.error("Exception in createDocumentFromString of BorgunUtill",pce);
            PZExceptionHandler.raiseTechnicalViolationException("BorgunUtills.java","createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,pce.getMessage(),pce.getCause());
        }
        catch (SAXException e)
        {
            log.error("Exception in createDocumentFromString BorgunUtill",e);
            transactionLogger.error("Exception in createDocumentFromString BorgunUtill",e);
            PZExceptionHandler.raiseTechnicalViolationException("BorgunUtills.java","createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.SAXEXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (IOException e)
        {
            log.error("Exception in createDocumentFromString BorgunUtill",e);
            transactionLogger.error("Exception in createDocumentFromString BorgunUtill",e);
            PZExceptionHandler.raiseTechnicalViolationException("BorgunUtills.java","createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }
        return document;
    }

    public static Map<String,InquiryTransaction> ReadInquiryResponseNew(String str)throws PZTechnicalViolationException
    {
        Document doc = createDocumentFromString(str);

        //Map<String, Map<String,List<String>>> responseMap  = new HashMap<String, Map<String,List<String>>>();
        //Map<String,List<InquiryTransaction>> listMap = new HashMap<String, List<InquiryTransaction>>();
        Map<String,InquiryTransaction> listMap = null;
        InquiryTransaction inquiryTransaction = null;

        Element rootElement = doc.getDocumentElement();
        NodeList nList1 = doc.getElementsByTagName("TransactionList");
        String actionCode = getTagValue("ActionCode", ((Element) nList1.item(0)));
        NodeList nList = doc.getElementsByTagName("Transaction");

        if(actionCode.equalsIgnoreCase("000"))
        {
            listMap = new HashMap<String, InquiryTransaction>();
            for (int i = 0; i < nList.getLength(); i++)
            {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element element = (Element) node;

                    if (getTagValue("TransactionType", ((Element) nList.item(i))).equalsIgnoreCase("5"))
                    {
                        //System.out.println("auth");
                        inquiryTransaction = new InquiryTransaction();
                        inquiryTransaction.setTransactionType("Auth");
                        inquiryTransaction.setTransactionNumber(getTagValue("TransactionNumber", ((Element) nList.item(i))));
                        inquiryTransaction.setTransactionDate(getTagValue("TransactionDate", ((Element) nList.item(i))));
                        inquiryTransaction.setPan(getTagValue("PAN", ((Element) nList.item(i))));
                        inquiryTransaction.setRrn(getTagValue("RRN", ((Element) nList.item(i))));
                        inquiryTransaction.setActionCode(getTagValue("ActionCode", ((Element) nList.item(i))));
                        inquiryTransaction.setAuthorizationCode(getTagValue("AuthorizationCode", ((Element) nList.item(i))));
                        inquiryTransaction.setStatus(getTagValue("Status", ((Element) nList.item(i))));
                        inquiryTransaction.setTrAmount(getTagValue("TrAmount", ((Element) nList.item(i))));
                        inquiryTransaction.setTrCurrency(getTagValue("TrCurrency", ((Element) nList.item(i))));

                        listMap.put("auth", inquiryTransaction);
                    }
                    if (getTagValue("TransactionType", ((Element) nList.item(i))).equalsIgnoreCase("3"))
                    {
                        //System.out.println("Refund");
                        inquiryTransaction = new InquiryTransaction();
                        inquiryTransaction.setTransactionType("Refund");
                        inquiryTransaction.setTransactionNumber(getTagValue("TransactionNumber", ((Element) nList.item(i))));
                        inquiryTransaction.setTransactionDate(getTagValue("TransactionDate", ((Element) nList.item(i))));
                        inquiryTransaction.setPan(getTagValue("PAN", ((Element) nList.item(i))));
                        inquiryTransaction.setRrn(getTagValue("RRN", ((Element) nList.item(i))));
                        inquiryTransaction.setActionCode(getTagValue("ActionCode", ((Element) nList.item(i))));
                        inquiryTransaction.setAuthorizationCode(getTagValue("AuthorizationCode", ((Element) nList.item(i))));
                        inquiryTransaction.setStatus(getTagValue("Status", ((Element) nList.item(i))));
                        inquiryTransaction.setTrAmount(getTagValue("TrAmount", ((Element) nList.item(i))));
                        inquiryTransaction.setTrCurrency(getTagValue("TrCurrency", ((Element) nList.item(i))));

                        listMap.put("refund", inquiryTransaction);
                    }
                    if (getTagValue("TransactionType", ((Element) nList.item(i))).equalsIgnoreCase("1"))
                    {
                        //System.out.println("sale");
                        inquiryTransaction = new InquiryTransaction();
                        inquiryTransaction.setTransactionType("Sale");
                        inquiryTransaction.setTransactionNumber(getTagValue("TransactionNumber", ((Element) nList.item(i))));
                        inquiryTransaction.setTransactionDate(getTagValue("TransactionDate", ((Element) nList.item(i))));
                        inquiryTransaction.setPan(getTagValue("PAN", ((Element) nList.item(i))));
                        inquiryTransaction.setRrn(getTagValue("RRN", ((Element) nList.item(i))));
                        inquiryTransaction.setActionCode(getTagValue("ActionCode", ((Element) nList.item(i))));
                        inquiryTransaction.setAuthorizationCode(getTagValue("AuthorizationCode", ((Element) nList.item(i))));
                        inquiryTransaction.setStatus(getTagValue("Status", ((Element) nList.item(i))));
                        inquiryTransaction.setTrAmount(getTagValue("TrAmount", ((Element) nList.item(i))));
                        inquiryTransaction.setTrCurrency(getTagValue("TrCurrency", ((Element) nList.item(i))));

                        listMap.put("sale", inquiryTransaction);
                    }
                }
            }
        }
        return listMap;
    }

    public static void main(String[] args) throws PZTechnicalViolationException
    {

        /*String inquiry="<?xml version=\"1.0\"?>\n" +
                "                <TransactionList>\n" +
                "                <Version>1000</Version>\n" +
                "                <Processor>108</Processor>\n" +
                "                <MerchantID>108</MerchantID>\n" +
                "                <ActionCode>000</ActionCode>\n" +
                "                <Transaction>\n" +
                "                <TransactionType>5</TransactionType>\n" +
                "                <TransactionNumber>469</TransactionNumber>\n" +
                "                <BatchNumber>1</BatchNumber>\n" +
                "                <TransactionDate>20160711173808</TransactionDate>\n" +
                "                <PAN>4741520000000003</PAN>\n" +
                "                <RRN>PBG000035029</RRN>\n" +
                "                <ActionCode>000</ActionCode>\n" +
                "                <AuthorizationCode>123456</AuthorizationCode>\n" +
                "                <TrAmount>10</TrAmount>\n" +
                "                <TrCurrency>392</TrCurrency>\n" +
                "                <Voided/>\n" +
                "                <Status>8</Status>\n" +
                "                <TerminalNr>1</TerminalNr>\n" +
                "                <Credit/>\n" +
                "                </Transaction>\n" +
                "\t\t<Transaction>\n" +
                "                <TransactionType>3</TransactionType>\n" +
                "                <TransactionNumber>469</TransactionNumber>\n" +
                "                <BatchNumber>1</BatchNumber>\n" +
                "                <TransactionDate>20160711173808</TransactionDate>\n" +
                "                <PAN>4741520000000003</PAN>\n" +
                "                <RRN>PBG000035029</RRN>\n" +
                "                <ActionCode>000</ActionCode>\n" +
                "                <AuthorizationCode>123456</AuthorizationCode>\n" +
                "                <TrAmount>10</TrAmount>\n" +
                "                <TrCurrency>392</TrCurrency>\n" +
                "                <Voided/>\n" +
                "                <Status>8</Status>\n" +
                "                <TerminalNr>1</TerminalNr>\n" +
                "                <Credit/>\n" +
                "                </Transaction>\n" +
                "\t\t<Transaction>\n" +
                "                <TransactionType>1</TransactionType>\n" +
                "                <TransactionNumber>469</TransactionNumber>\n" +
                "                <BatchNumber>1</BatchNumber>\n" +
                "                <TransactionDate>20160711173808</TransactionDate>\n" +
                "                <PAN>4741520000000003</PAN>\n" +
                "                <RRN>PBG000035029</RRN>\n" +
                "                <ActionCode>000</ActionCode>\n" +
                "                <AuthorizationCode>123456</AuthorizationCode>\n" +
                "                <TrAmount>10</TrAmount>\n" +
                "                <TrCurrency>392</TrCurrency>\n" +
                "                <Voided/>\n" +
                "                <Status>8</Status>\n" +
                "                <TerminalNr>1</TerminalNr>\n" +
                "                <Credit/>\n" +
                "                </Transaction>\n" +
                "                </TransactionList>";*/

        /*String inquiry="<?xml version=\"1.0\"?>\n" +
                "<TransactionList>\n" +
                "  <Version>1000</Version>\n" +
                "  <Processor>108</Processor>\n" +
                "  <MerchantID>108</MerchantID>\n" +
                "  <ActionCode>000</ActionCode>\n" +
                "  <Transaction>\n" +
                "    <TransactionType>3</TransactionType>\n" +
                "    <TransactionNumber>234</TransactionNumber>\n" +
                "    <BatchNumber>1</BatchNumber>\n" +
                "    <TransactionDate>20150820134518</TransactionDate>\n" +
                "    <PAN>4176661000001056</PAN>\n" +
                "    <RRN>PBG000020684</RRN>\n" +
                "    <ActionCode>000</ActionCode>\n" +
                "    <AuthorizationCode>      </AuthorizationCode>\n" +
                "    <TrAmount>58</TrAmount>\n" +
                "    <TrCurrency>392</TrCurrency>\n" +
                "    <Voided/>\n" +
                "    <Status>5</Status>\n" +
                "    <TerminalNr>1</TerminalNr>\n" +
                "    <Credit/>\n" +
                "  </Transaction>\n" +
                "  <Transaction>\n" +
                "    <TransactionType>1</TransactionType>\n" +
                "    <TransactionNumber>233</TransactionNumber>\n" +
                "    <BatchNumber>1</BatchNumber>\n" +
                "    <TransactionDate>20150820134518</TransactionDate>\n" +
                "    <PAN>4176661000001056</PAN>\n" +
                "    <RRN>PBG000020684</RRN>\n" +
                "    <ActionCode>000</ActionCode>\n" +
                "    <AuthorizationCode>081622</AuthorizationCode>\n" +
                "    <TrAmount>58</TrAmount>\n" +
                "    <TrCurrency>392</TrCurrency>\n" +
                "    <Voided/>\n" +
                "    <Status>2</Status>\n" +
                "    <TerminalNr>1</TerminalNr>\n" +
                "    <Credit/>\n" +
                "  </Transaction>\n" +
                "</TransactionList>";*/

        String inquiry = "<?xml version=\"1.0\"?>\n" +
                "<TransactionList>\n" +
                "  <Version>1000</Version>\n" +
                "  <Processor>108</Processor>\n" +
                "  <MerchantID>108</MerchantID>\n" +
                "  <ActionCode>001</ActionCode>\n" +
                "</TransactionList>";

        Map<String, InquiryTransaction> responseMap =  ReadInquiryResponseNew(inquiry.toString());
        /*System.out.println(responseMap);
        System.out.println("refund---"+responseMap.get("refund").getTransactionNumber());
        System.out.println("refund---"+responseMap.get("refund").getTransactionType());
        System.out.println("refund---"+responseMap.get("refund").getTransactionDate());*/

        if(responseMap.get("auth")!=null)
        {
            /*System.out.println("refund---" + responseMap.get("auth").getTransactionNumber());
            System.out.println("refund---" + responseMap.get("auth").getTransactionType());
            System.out.println("refund---" + responseMap.get("auth").getTransactionDate());*/
        }

        /*System.out.println("refund---"+responseMap.get("sale").getTransactionNumber());
        System.out.println("refund---"+responseMap.get("sale").getTransactionType());
        System.out.println("refund---"+responseMap.get("sale").getTransactionDate());*/

        //System.out.println("Response inquiry---"+responseMap.get("sale"));
    }

    public Hashtable getBorgunAcoountDetails(String accountid)
    {
        Connection connection = null;
        Hashtable accountHash = new Hashtable();
        try
        {
            connection = Database.getConnection();
            String query = "SELECT * FROM gateway_accounts_borgun WHERE accountid = " +accountid;
            PreparedStatement p = connection.prepareStatement(query);
            ResultSet rs = p.executeQuery();
            if (rs.next())
            {
                accountHash.put("accountid",rs.getString("accountid"));
                accountHash.put("MerchantHome", rs.getString("MerchantHome"));
                accountHash.put("MerchantCity", rs.getString("MerchantCity"));
                accountHash.put("MerchantZipCode", rs.getString("MerchantZipCode"));
                accountHash.put("MerchantCountry", rs.getString("MerchantCountry"));
                accountHash.put("Ecommerce", rs.getString("Ecommerce"));
                accountHash.put("EcommercePhone", rs.getString("EcommercePhone"));
            }
        }
        catch (SQLException e)
        {
            log.error("SQLException----",e);
        }
        catch (SystemError e)
        {
            log.error("SystemError----",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return accountHash;
    }
}
