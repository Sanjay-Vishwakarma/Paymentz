package com.directi.pg.core.safecharge;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.valueObjects.SafeChargeResponseVO;
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
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Saurabh.b
 * Date: 8/28/13
 * Time: 8:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class SafeChargeUtils
{
    public final static String charset = "UTF-8";

    private static Logger log = new Logger(SafeChargeUtils.class.getName());

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

    public static String doPostHTTPSURLConnection(String strURL, String req) throws PZTechnicalViolationException
    {
        OutputStreamWriter outSW = null;
        BufferedReader in = null;
        String strResponse="";
        URLConnection connection = null;

        try
        {

            URL url = new URL(strURL);
            try
            {
                connection = url.openConnection();
                connection.setConnectTimeout(120000);
                connection.setReadTimeout(120000);
            }
            catch (IOException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE,null, io.getMessage(), io.getCause());
            }
            if(connection instanceof HttpURLConnection)
            {
                ((HttpURLConnection)connection).setRequestMethod("POST");
            }

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Set request headers for content type and length
            connection.setRequestProperty("Content-type", "text/xml");

            outSW = new OutputStreamWriter(
                    connection.getOutputStream());
            outSW.write(req);
            outSW.close();

            in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));
            String decodedString;
            while ((decodedString = in.readLine()) != null)
            {
                strResponse = strResponse + decodedString;
            }
        }
        catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (UnsupportedEncodingException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (SocketTimeoutException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.GATEWAY_CONNECTION_TIMEOUT,null,pe.getMessage(),pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, ex.getMessage(), ex.getCause());
        }
        finally
        {
            if (outSW != null) {
                try {
                    outSW.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java","doPostHTTPSURLConnection()",null,"common","IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
                }
            }
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java","doPostHTTPSURLConnection()",null,"common","IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
                }
            }
        }
        if (strResponse == null)
            return "";
        else
            return strResponse;
    }

    public static String doPostHTTPSURLConnectionForInquiry(String strURL, String req) throws PZTechnicalViolationException
    {
        OutputStreamWriter outSW = null;
        BufferedReader in = null;
        String strResponse="";
        URLConnection connection = null;

        try
        {

            URL url = new URL(strURL);
            try
            {
                connection = url.openConnection();
                connection.setConnectTimeout(120000);
                connection.setReadTimeout(120000);
            }
            catch (IOException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE,null, io.getMessage(), io.getCause());
            }
            if(connection instanceof HttpURLConnection)
            {
                ((HttpURLConnection)connection).setRequestMethod("POST");
            }

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Set request headers for content type and length
            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(req.length()));

            outSW = new OutputStreamWriter(
                    connection.getOutputStream());
            outSW.write(req);
            outSW.close();

            in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));
            String decodedString;
            while ((decodedString = in.readLine()) != null)
            {
                strResponse = strResponse + decodedString;
            }
        }
        /*catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null, me.getMessage(), me.getCause());
        }*/
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (UnsupportedEncodingException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (SocketTimeoutException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.GATEWAY_CONNECTION_TIMEOUT,null,pe.getMessage(),pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, ex.getMessage(), ex.getCause());
        }
        finally
        {
            if (outSW != null) {
                try {
                    outSW.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java","doPostHTTPSURLConnection()",null,"common","IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
                }
            }
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java","doPostHTTPSURLConnection()",null,"common","IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
                }
            }
        }
        if (strResponse == null)
            return "";
        else
            return strResponse;
    }



    /**
     *
     * @param in
     * @return
     * @throws IOException
     */
    private static String ReadByteStream(BufferedInputStream in) throws PZTechnicalViolationException
    {
        LinkedList<SafeChargeBuf> bufList = new LinkedList<SafeChargeBuf>();
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
            bufList.add(new SafeChargeBuf(buf, num));
        } while (true);
        buf = new byte[size];
        int pos = 0;
        for (ListIterator<SafeChargeBuf> p = bufList.listIterator(); p.hasNext();) {
            SafeChargeBuf b = p.next();
            for (int i = 0; i < b.size;) {
                buf[pos] = b.buf[i];
                i++;
                pos++;
            }

        }
        buffer = new String(buf,charset);
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java","doPostHTTPSURLConnection()",null,"common","IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }
        return buffer;
    }
    public static SafeChargeResponseVO getSafeChargeResponseVO(String xmlResponseString) throws PZTechnicalViolationException
    {
        SafeChargeResponseVO res= new SafeChargeResponseVO();
        Functions functions = new Functions();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        String strValue = null;
        NodeList list=null;
        try
        {
            builder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            log.error("ParserConfigurationException in SafeChargeUtils----",e);
        }
        Document document = null;
        try
        {
            document = builder.parse(new InputSource(new StringReader(xmlResponseString)));
        }
        catch (SAXException e)
        {
            log.error("SAXException in SafeChargeUtils----",e);
            PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java","doPostHTTPSURLConnection()",null,"common","IO Exception:::", PZTechnicalExceptionEnum.SAXEXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (IOException e)
        {
            log.error("IOException in SafeChargeUtils----",e);
            PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java","doPostHTTPSURLConnection()",null,"common","IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }

        Element rootElement = document.getDocumentElement();
        strValue = getTagValue("Response", rootElement);

        list = document.getElementsByTagName("Response");
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;
                res.setTransactionId(getTagValue("TransactionID",element));
                res.setTransactionStatus(getTagValue("Status", element));
                res.setErrorCode(getTagValue("ErrCode", element));
                res.setDescription(getTagValue("Reason", element));
                res.setRemark(getTagValue("Reason", element));
                res.setAuthCode(getTagValue("AuthCode", element));
                res.setExErrCode(getTagValue("ExErrCode", element));
                res.setToken(getTagValue("Token", element));
                res.setAVSCode(getTagValue("AVSCode", element));
                res.setPaReq(getTagValue("PaReq", element));
                res.setMerchantID(getTagValue("MerchantID", element));
                res.setACSUrl(getTagValue("ACSurl", element));
                res.setThreeDFlow(getTagValue("ThreeDFlow", element));
                res.setRequestId(getTagValue("RequestID", element));
                res.setBankTransactionDate(getTagValue("TransactionDate", element));
            }
        }

        return res;
    }
    public static SafeChargeResponseVO getSafeChargeQueryResponseVO(String xmlResponseString) throws PZTechnicalViolationException
    {
        SafeChargeResponseVO res= new SafeChargeResponseVO();
        Functions functions = new Functions();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        String strValue = null;
        NodeList list=null;
        try
        {
            builder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            log.error("ParserConfigurationException in SafeChargeUtils----",e);
        }
        Document document = null;
        try
        {

            document = builder.parse(new InputSource(new StringReader(xmlResponseString)));
        }
        catch (SAXException e)
        {
            log.error("SAXException in SafeChargeUtils----",e);
            PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java","doPostHTTPSURLConnection()",null,"common","IO Exception:::", PZTechnicalExceptionEnum.SAXEXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (IOException e)
        {
            log.error("IOException in SafeChargeUtils----",e);
            PZExceptionHandler.raiseTechnicalViolationException("SafeChargeUtills.java","doPostHTTPSURLConnection()",null,"common","IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }

        Element rootElement = document.getDocumentElement();
        strValue = getTagValue("Response", rootElement);

        list = document.getElementsByTagName("Response");
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;
                res.setTransactionId(getTagValue("TransactionID",element));
                res.setDescription(getTagValue("Reason", element));
                res.setAuthCode(getTagValue("AuthCode", element));
                res.setRequestId(getTagValue("RequestID", element));
                res.setTransactionType(getTagValue("TransactionType", element));
                res.setCreditTypeId(getTagValue("CreditTypeId", element));
                res.setResult(getTagValue("Result", element));
                res.setBankTransactionDate(getTagValue("TransactionDate", element));
                res.setAmount(getTagValue("Amount", element));
                res.setCurrency(getTagValue("Currency", element));
                res.setTransactionStatus(getTagValue("Result", element));
            }
        }

        return res;
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
            Node nValue = (Node) nlList.item(0);
            value =	nValue.getNodeValue();
        }

        return value;

    }
    public static Map<String,String> getResponseString(String resValue)
    {
        String res[]=resValue.split("&");
        Map<String,String> responseString= new TreeMap<String, String>();
        for(int i=0;i<res.length;i++)
        {
            String field=res[i];
            String temp[]=field.split("=");
            String response[]=new String[2];

            for(int x=0;x<temp.length;x++)
            {
                response[x]=temp[x];
            }
            if(response[1]!=null && !response[1].equalsIgnoreCase(""))
            {
                responseString.put(response[0],response[1]);
            }
            else
            {
                responseString.put(response[0],"");
            }
        }

        return responseString;
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


}

class SafeChargeBuf {

    public byte buf[];
    public int size;

    public SafeChargeBuf(byte b[], int s) {
        buf = b;
        size = s;
    }

    public static void main(String[] args)
    {
        SafeChargeResponseVO responseVO = null;
        String response = "<?xml version=\"1.0\" encoding=\"utf-8\"?><Response Type=\"Transactions\" Version=\"1.0.0\"><RequestID>4d5640b7-0c1e-4300-aa60-b10926f8afce</RequestID><FromDate></FromDate><ToDate></ToDate><PageTransactionsCount>1</PageTransactionsCount><Transaction TransactionID=\"1509754236\"><TransactionID>1509754236</TransactionID><ClientName>Trillion PAY Test</ClientName><RelatedTransactionID>0</RelatedTransactionID><ClientUniqueID>54359</ClientUniqueID><Result>Declined</Result><Reason>Decline</Reason><AuthCode></AuthCode><Amount>50.0000</Amount><Currency>EUR</Currency><UserID></UserID><TransactionDate>2017-10-23 13:01:49</TransactionDate><TransactionType>Sale</TransactionType><IsRebill>false</IsRebill><MembershipID></MembershipID><Bank>Safecharge bank</Bank><CardCompany>Visa</CardCompany><Last4Digits>1111</Last4Digits><Email>nikita.lanjewar@pz.com</Email><FirstName>sagar</FirstName><LastName>inchanale</LastName><WebSite>https://betcart.amelco.co.uk</WebSite><ShopID>0</ShopID><CreditTypeId>0</CreditTypeId><APMTransactionID></APMTransactionID><APMReferenceID></APMReferenceID></Transaction></Response>";
        try
    {
        responseVO = SafeChargeUtils.getSafeChargeResponseVO(response);
        /*System.out.println("respone----"+responseVO.getRequestId());
        System.out.println("tid----"+responseVO.getRemark());
        System.out.println("tid----"+responseVO.getRequestId());*/
    }
    catch (Exception e)
    {
    }
    }

}