package com.payment.secureTrading;

import com.directi.pg.*;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by Jitendra on 19-Nov-18.
 */
public class SecureTradingUtils
{
    public final static String charset = "UTF-8";
    private static Logger log = new Logger(SecureTradingUtils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(SecureTradingUtils.class.getName());

    public static String doHttpPostConnection(String url1, String req, String type, String credentials)throws PZTechnicalViolationException
    {
        StringBuffer result = new StringBuffer();
        try
        {
            String data = req;
            URL url = new URL(url1);
            System.setProperty("https.protocols", "TLSv1.2");
            System.setProperty("http.maxRedirects", "2"); // Avoid java bug which causes 20 redirects when the password is wrong
            //HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            URLConnection conn = null;
            //Authenticator.setDefault(new MyAuthenticator()); //Basic auth username and password provided
            conn = url.openConnection();
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "text/xml;charset=utf-8"); //Add the html headers to the xml before sending
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.setRequestProperty("Accept", "text/xml");
            conn.setRequestProperty("Accept-Encoding", "gzip"); // Optionally compress responses for speed
            conn.setRequestProperty("Authorization", type + " " + credentials);
            conn.setRequestProperty("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)");
            conn.setRequestProperty("Host", "webservices.securetrading.net");
            conn.setRequestProperty("Connection", "close");
            if(conn instanceof HttpURLConnection)
            {
                ((HttpURLConnection)conn).setRequestMethod("POST");
            }
            //conn.setRequestMethod("POST");
            conn.connect();
            // Write the Request
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            wr.close();

            InputStream inputStream = conn.getInputStream();
            // Check for response being gzip compressed
            if ("gzip".equals(conn.getHeaderField("content-encoding")))
            {
                inputStream = new GZIPInputStream(inputStream);
            }
            // Read the response. Note the response will be encoded in utf-8 character encoding
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = rd.readLine()) != null)
            {
                result.append(line);
            }
            rd.close();
        }
        catch (MalformedURLException e)
        {
            transactionLogger.error("-----Exception-----",e);
            PZExceptionHandler.raiseTechnicalViolationException("SecureTradingUtils.java", "doPostHTTPSURLConnection()", null, "common", "Malformed URL Exception:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null, e.getMessage(), e.getCause());
        }
        catch (ProtocolException e)
        {
            transactionLogger.error("-----Exception-----",e);
            PZExceptionHandler.raiseTechnicalViolationException("SecureTradingUtils.java", "doPostHTTPSURLConnection()", null, "common", "Protocol Exception:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            transactionLogger.error("-----Exception-----",e);
            PZExceptionHandler.raiseTechnicalViolationException("SecureTradingUtils.java", "doPostHTTPSURLConnection()", null, "common", "IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }
       /* catch (Exception e)
        {
            transactionLogger.error("-----Exception-----",e);
            PZExceptionHandler.raiseTechnicalViolationException("SecureTradingUtils.java", "doPostHTTPSURLConnection()", null, "common", "IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }*/
        return result.toString();
    }

    public static String verifyXML(String filename) throws Exception
    {
        // Read in XML from a file
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(filename)));
        Source source = new DOMSource(document);
        // Parse the XML to ensure it is correctly encoded
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StringWriter writer = new StringWriter();
        //transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.transform(source, new StreamResult(writer));
        writer.close();
        String xml = writer.toString();
        //Return the XML as a string
        return xml;
    }

    public static Document createDocumentFromString(String xmlString) throws PZTechnicalViolationException
    {
        Document doc = null;
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            doc = docBuilder.parse(new InputSource(new StringReader(xmlString.toString().trim())));
        }
        catch (ParserConfigurationException pce)
        {
            log.error("Exception in createDocumentFromString of SecureTradingUtils", pce);
            transactionLogger.error("Exception in createDocumentFromString of SecureTradingUtils", pce);
            PZExceptionHandler.raiseTechnicalViolationException("SecureTradingUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e)
        {
            log.error("Exception in createDocumentFromString SecureTradingUtils", e);
            transactionLogger.error("Exception in createDocumentFromString SecureTradingUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("SecureTradingUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.SAXEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            log.error("Exception in createDocumentFromString SecureTradingUtils", e);
            transactionLogger.error("Exception in createDocumentFromString SecureTradingUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("SecureTradingUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return doc;
    }

    private static String getTagValue(String sTag, Element eElement)
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
    private static String getAttributeValue(String sTag,String attribute, Element eElement)
    {
        NodeList nlList = null;
        String value = "";
        System.out.println("sTag--->"+sTag);
        if (eElement != null && eElement.getElementsByTagName(sTag) != null && eElement.getElementsByTagName(sTag).item(0) != null)
        {
            Node node = eElement.getElementsByTagName(sTag).item(0);
            if(node.hasAttributes() && node.getAttributes().getNamedItem(attribute)!=null)
            {
                value=node.getAttributes().getNamedItem(attribute).getNodeValue();
            }
        }
        return value;
    }

    public static Map<String, String> readSoapResponse(String response) throws PZTechnicalViolationException
    {
        Functions functions= new Functions();
        Comm3DResponseVO comm3DResponseVO=new Comm3DResponseVO();
        Map<String, String> responseMap = new HashMap<String, String>();

        Document doc = createDocumentFromString(response.trim());
        NodeList nList = doc.getElementsByTagName("responseblock");
        NodeList nList1=doc.getElementsByTagName("response");
        NodeList nList2=doc.getElementsByTagName("merchant");
        NodeList nList3=doc.getElementsByTagName("settlement");
        NodeList nList4=doc.getElementsByTagName("error");
        NodeList nList5=doc.getElementsByTagName("security");
        NodeList nList6=doc.getElementsByTagName("threedsecure");
        NodeList nList7=doc.getElementsByTagName("operation");
        NodeList nList8=doc.getElementsByTagName("billing");
        NodeList nList9=doc.getElementsByTagName("record");
        NodeList nList10Dcc=doc.getElementsByTagName("dcc");
        NodeList nList11other=doc.getElementsByTagName("other");

        responseMap.put("transactionreference",getTagValue("transactionreference",((Element) nList1.item(0))));
        responseMap.put("timestamp",getTagValue("timestamp",((Element) nList1.item(0))));
        responseMap.put("acquirerresponsecode",getTagValue("acquirerresponsecode",((Element) nList1.item(0))));
        responseMap.put("acquirerresponsemessage",getTagValue("acquirerresponsemessage",((Element) nList1.item(0))));
        responseMap.put("chargedescription",getTagValue("chargedescription",((Element) nList2.item(0))));
        responseMap.put("settlestatus",getTagValue("settlestatus",((Element) nList3.item(0))));
        responseMap.put("settleduedate",getTagValue("settleduedate",((Element) nList3.item(0))));
        responseMap.put("settlebaseamount",getTagValue("settlebaseamount",((Element) nList3.item(0))));
        responseMap.put("updatereason",getTagValue("updatereason",((Element) nList3.item(0))));
        responseMap.put("settledtimestamp",getTagValue("settledtimestamp",((Element) nList3.item(0))));
        responseMap.put("found",getTagValue("found",((Element) nList1.item(0))));


        responseMap.put("message",getTagValue("message",((Element) nList4.item(0))));
        responseMap.put("code",getTagValue("code",((Element) nList4.item(0))));
        responseMap.put("data",getTagValue("data",((Element) nList4.item(0))));

        responseMap.put("securitycode",getTagValue("securitycode",((Element) nList5.item(0))));

        responseMap.put("acsurl",getTagValue("acsurl",((Element) nList6.item(0))));
        responseMap.put("md",getTagValue("md",((Element) nList6.item(0))));
        responseMap.put("xid",getTagValue("xid",((Element) nList6.item(0))));
        responseMap.put("pareq",getTagValue("pareq",((Element) nList6.item(0))));
        responseMap.put("enrolled",getTagValue("enrolled",((Element) nList6.item(0))));
        responseMap.put("status",getTagValue("status",((Element) nList6.item(0))));
        responseMap.put("eci",getTagValue("eci",((Element) nList6.item(0))));
        responseMap.put("cavv",getTagValue("cavv",((Element) nList6.item(0))));

        responseMap.put("parenttransactionreference",getTagValue("parenttransactionreference",((Element) nList7.item(0))));
        responseMap.put("amount",getTagValue("amount", ((Element) nList8.item(0))));
        responseMap.put("currencyiso3a",getAttributeValue("amount", "currencycode", ((Element) nList8.item(0))));
        responseMap.put("authcode",getTagValue("authcode", ((Element) nList9.item(0))));
        responseMap.put("dcctype",getTagValue("dcctype",((Element) nList10Dcc.item(0))));
        responseMap.put("conversionrate",getTagValue("conversionrate", ((Element) nList10Dcc.item(0))));
        responseMap.put("dcccurrencyiso3a",getAttributeValue("amount", "currencycode", ((Element) nList10Dcc.item(0))));
        responseMap.put("dccamount",getTagValue("amount", ((Element) nList10Dcc.item(0))));

        responseMap.put("retrievalreferencenumber",getTagValue("retrievalreferencenumber", ((Element) nList11other.item(0))));

        for (Map.Entry<String,String> entry:responseMap.entrySet())
        {
            String key=entry.getKey();
            String value=entry.getValue();
            transactionLogger.debug("key line :::::"+key);
            System.out.println(key + "---" + value);
            transactionLogger.debug("value line :::::" + value);
        }
        return responseMap;
    }

    public static  String getCardType(String cardTypeid)
    {
        String cardType="";
        if(cardTypeid.equals("VISA")){
            cardType="VISA";
        }else if(cardTypeid.equals("MC")){
            cardType="MASTERCARD";
        }
        return cardType;
    }

    public static String getAmount(String amount)
    {
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;
        String amt = d.format(dObj2);
        return amt;
    }

   /* public static String getEncodedCredentials(String userName,String password)
    {
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
        return encodedCredentials;
    }*/


    public static String getJPYAmount(String amount)
    {
        double amt= Double.parseDouble(amount);
        double roundOff=Math.round(amt);
        int value=(int)roundOff;
        amount=String.valueOf(value);
        return amount.toString();
    }
    public static String getKRWAmount(String amount)
    {
        double amt= Double.parseDouble(amount);
        double roundOff=Math.round(amt);
        int value=(int)roundOff;
        amount=String.valueOf(value);
        return amount.toString();
    }
    public static String getKWDSupportedAmount(String amount)
    {
        transactionLogger.debug("formatting amount for KWD Currency ---")
        ;
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 1000;
        String amt = d.format(dObj2);
        return amt;
    }

    public static String getParentTransactionReference(String trackingId)
    {
        String paymentid = "";
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query = "SELECT paymentid FROM transaction_common WHERE trackingId=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, trackingId);
            transactionLogger.error("Query of paymentid ->"+ps);

            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                paymentid = rs.getString("paymentid");
            }


        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException ->",e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError ->",systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return paymentid;
    }

    public static String getsubscriptionnumber(String trackingid,String parentTrackingid,String type)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String currentSTAN="";
        long previousSTAN=0;
        try
        {
            conn = Database.getConnection();
            String query = "select stan as stan from transaction_secureTrad_details where type='RECURRING' and parentTrackingid=? order by stan desc ";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,parentTrackingid);
            transactionLogger.error("stmt---->"+stmt);
            rs = stmt.executeQuery();
            if (rs.next())
            {
                previousSTAN = rs.getLong("stan");
            }
            else
            {
                previousSTAN=1;
            }
            transactionLogger.error("previous STAN------->" + previousSTAN);
             currentSTAN = String.valueOf(previousSTAN + 1);
            transactionLogger.error("Current STAN------->" + currentSTAN);
            query = "insert into transaction_secureTrad_details(stan,trackingid,parentTrackingid,type) values(?,?,?,?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, currentSTAN);
            stmt.setString(2, trackingid);
            stmt.setString(3, parentTrackingid);
            stmt.setString(4, type);
            transactionLogger.error("SecureTrading Utils------------>" + stmt);
            int k = stmt.executeUpdate();
            if (k > 0)
            {
                return currentSTAN;
            }

        }
        catch(SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch(SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closeResultSet(rs);
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        return currentSTAN;
    }

    public static String getToken(String payload,String secret)
    {
        String encodedPayLoad=Base64.encode(payload.getBytes());
        String encodedHeader=Base64.encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
        MessageDigest md = null;
        try
        {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);


            byte raw[] = sha256_HMAC.doFinal((encodedHeader + "." + encodedPayLoad).getBytes());

            StringBuffer ls_sb=new StringBuffer();
            for(int i=0;i<raw.length;i++)
                ls_sb.append(char2hex(raw[i]));
            return ls_sb.toString(); //step 6
        }catch(Exception e){
            transactionLogger.error("Exception--->",e);
            return null;
        }
    }
    public static String char2hex(byte x)
    {
        char arr[]={
                '0','1','2','3',
                '4','5','6','7',
                '8','9','A','B',
                'C','D','E','F'
        };

        char c[] = {arr[(x & 0xF0)>>4],arr[x & 0x0F]};
        return (new String(c));
    }

    public static void main(String[] args)
    {
        String response="<?xml version='1.0' encoding='utf-8'?>" +
                " <responseblock version=\"3.67\"> " +
                " <requestreference>W56-hfq65nce</requestreference> " +
                " <response type=\"CURRENCYRATE\">  " +
                " <acquirerresponsecode>0</acquirerresponsecode>  " +
                " <acquirerresponsemessage>Success</acquirerresponsemessage>  " +
                " <billing>   " +
                " <amount currencycode=\"USD\">137</amount>   " +
                " <dcc>    " +
                " <amount currencycode=\"EUR\">100</amount>    " +
                " <conversionrate>1.3704</conversionrate>   " +
                " <conversionratesource>Reuters wholesale interbank</conversionratesource>   " +
                " <dcctype>DCC</dcctype>     " +
                " <expirytimestamp>2021-02-06 10:26:00</expirytimestamp>   " +
                " <marginratepercentage>2.5000</marginratepercentage>  " +
                " </dcc>   " +
                " <payment type=\"VISA\">    " +
                " <iin>411111000</iin>    " +
                " <issuer>SecureTrading Test Issuer1</issuer>  " +
                " <issuercountry>US</issuercountry>  " +
                " <pan>411111######0021</pan>" +
                "</payment>  " +
                " </billing> " +
                " <error>    " +
                " <code>0</code>  " +
                " <message>Ok</message>  " +
                " </error> " +
                " <live>0</live> " +
                " <merchant>  " +
                " <merchantnumber>00000000</merchantnumber>    " +
                " <operatorname>api@transactworld.com</operatorname> " +
                " <orderreference>6579557</orderreference>  " +
                " </merchant>  " +
                " <operation>  " +
                " <accounttypedescription>CURRENCYRATE</accounttypedescription>    " +
                " <processor>FEXCO Merchant Services</processor> " +
                " </operation> " +
                " <timestamp>2021-02-02 06:26:57</timestamp>  " +
                " <transactionreference>56-71-10280</transactionreference> " +
                " </response> " +
                " <secrand>MH2</secrand>" +
                " </responseblock>";

        try
        {
            readSoapResponse(response);
        }
        catch (PZTechnicalViolationException e)
        {
            //e.printStackTrace();
        }
    }
}
