package com.payment.emerchantpay;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
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
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 1/28/2020.
 */
public class EMerchantPayUtils
{
    static Functions functions=new Functions();
    private static TransactionLogger transactionLogger=new TransactionLogger(EMerchantPayUtils.class.getName());
    private static HashMap<String,String> countryCodeHash=new HashMap<>();
    static {
        countryCodeHash.put("ALA","AX");
        countryCodeHash.put("AFG","AF");
        countryCodeHash.put("ALB","AL");
        countryCodeHash.put("DZA","DZ");
        countryCodeHash.put("ASM","AS");
        countryCodeHash.put("AND","AD");
        countryCodeHash.put("AGO","AO");
        countryCodeHash.put("AIA","AI");
        countryCodeHash.put("ATA","AQ");
        countryCodeHash.put("ATG","AG");
        countryCodeHash.put("ARG","AR");
        countryCodeHash.put("ARM","AM");
        countryCodeHash.put("ABW","AW");
        countryCodeHash.put("AUS","AU");
        countryCodeHash.put("AUT","AT");
        countryCodeHash.put("AZE","AZ");
        countryCodeHash.put("BHS","BS");
        countryCodeHash.put("BHR","BH");
        countryCodeHash.put("BGD","BD");
        countryCodeHash.put("BRB","BB");
        countryCodeHash.put("BLR","BY");
        countryCodeHash.put("BEL","BE");
        countryCodeHash.put("BLZ","BZ");
        countryCodeHash.put("BEN","BJ");
        countryCodeHash.put("BMU","BM");
        countryCodeHash.put("BTN","BT");
        countryCodeHash.put("BOL","BO");
        countryCodeHash.put("BIH","BA");
        countryCodeHash.put("BWA","BW");
        countryCodeHash.put("BVT","BV");
        countryCodeHash.put("BRA","BR");
        countryCodeHash.put("IOT","IO");
        countryCodeHash.put("BRN","BN");
        countryCodeHash.put("BGR","BG");
        countryCodeHash.put("BFA","BF");
        countryCodeHash.put("BDI","BI");
        countryCodeHash.put("KHM","KH");
        countryCodeHash.put("CMR","CM");
        countryCodeHash.put("CAN","CA");
        countryCodeHash.put("CPV","CV");
        countryCodeHash.put("CYM","KY");
        countryCodeHash.put("CAF","CF");
        countryCodeHash.put("TCD","TD");
        countryCodeHash.put("CHL","CL");
        countryCodeHash.put("CHN","CN");
        countryCodeHash.put("CXR","CX");
        countryCodeHash.put("CCK","CC");
        countryCodeHash.put("COL","CO");
        countryCodeHash.put("COM","KM");
        countryCodeHash.put("COD","CD");
        countryCodeHash.put("COG","CG");
        countryCodeHash.put("COK","CK");
        countryCodeHash.put("CRI","CR");
        countryCodeHash.put("CIV","CI");
        countryCodeHash.put("HRV","HR");
        countryCodeHash.put("CUB","CU");
        countryCodeHash.put("CYP","CY");
        countryCodeHash.put("CZE","CZ");
        countryCodeHash.put("DNK","DK");
        countryCodeHash.put("DJI","DJ");
        countryCodeHash.put("DMA","DM");
        countryCodeHash.put("DOM","DO");
        countryCodeHash.put("ECU","EC");
        countryCodeHash.put("EGY","EG");
        countryCodeHash.put("SLV","SV");
        countryCodeHash.put("GNQ","GQ");
        countryCodeHash.put("ERI","ER");
        countryCodeHash.put("EST","EE");
        countryCodeHash.put("ETH","ET");
        countryCodeHash.put("FLK","FK");
        countryCodeHash.put("FRO","FO");
        countryCodeHash.put("FJI","FJ");
        countryCodeHash.put("FIN","FI");
        countryCodeHash.put("FRA","FR");
        countryCodeHash.put("GUF","GF");
        countryCodeHash.put("PYF","PF");
        countryCodeHash.put("ATF","TF");
        countryCodeHash.put("GAB","GA");
        countryCodeHash.put("GMB","GM");
        countryCodeHash.put("GEO","GE");
        countryCodeHash.put("DEU","DE");
        countryCodeHash.put("GHA","GH");
        countryCodeHash.put("GIB","GI");
        countryCodeHash.put("GRC","GR");
        countryCodeHash.put("GRL","GL");
        countryCodeHash.put("GRD","GD");
        countryCodeHash.put("GLP","GP");
        countryCodeHash.put("GUM","GU");
        countryCodeHash.put("GTM","GT");
        countryCodeHash.put("GIN","GN");
        countryCodeHash.put("GNB","GW");
        countryCodeHash.put("GUY","GY");
        countryCodeHash.put("HTI","HT");
        countryCodeHash.put("HMD","HM");
        countryCodeHash.put("HND","HN");
        countryCodeHash.put("HKG","HK");
        countryCodeHash.put("HUN","HU");
        countryCodeHash.put("ISL","IS");
        countryCodeHash.put("IND","IN");
        countryCodeHash.put("IDN","ID");
        countryCodeHash.put("IRN","IR");
        countryCodeHash.put("IRQ","IQ");
        countryCodeHash.put("IRL","IE");
        countryCodeHash.put("ISR","IL");
        countryCodeHash.put("ITA","IT");
        countryCodeHash.put("JAM","JM");
        countryCodeHash.put("JPN","JP");
        countryCodeHash.put("JOR","JO");
        countryCodeHash.put("KAZ","KZ");
        countryCodeHash.put("KEN","KE");
        countryCodeHash.put("KIR","KI");
        countryCodeHash.put("PRK","KP");
        countryCodeHash.put("KOR","KR");
        countryCodeHash.put("KWT","KW");
        countryCodeHash.put("KGZ","KG");
        countryCodeHash.put("LAO","LA");
        countryCodeHash.put("LVA","LV");
        countryCodeHash.put("LBN","LB");
        countryCodeHash.put("LSO","LS");
        countryCodeHash.put("LBR","LR");
        countryCodeHash.put("LBY","LY");
        countryCodeHash.put("LIE","LI");
        countryCodeHash.put("LTU","LT");
        countryCodeHash.put("LUX","LU");
        countryCodeHash.put("MAC","MO");
        countryCodeHash.put("MKD","MK");
        countryCodeHash.put("MDG","MG");
        countryCodeHash.put("MWI","MW");
        countryCodeHash.put("MYS","MY");
        countryCodeHash.put("MDV","MV");
        countryCodeHash.put("MLI","ML");
        countryCodeHash.put("MLT","MT");
        countryCodeHash.put("MHL","MH");
        countryCodeHash.put("MTQ","MQ");
        countryCodeHash.put("MRT","MR");
        countryCodeHash.put("MUS","MU");
        countryCodeHash.put("MYT","YT");
        countryCodeHash.put("MEX","MX");
        countryCodeHash.put("FSM","FM");
        countryCodeHash.put("MDA","MD");
        countryCodeHash.put("MCO","MC");
        countryCodeHash.put("MNG","MN");
        countryCodeHash.put("MSR","MS");
        countryCodeHash.put("MAR","MA");
        countryCodeHash.put("MOZ","MZ");
        countryCodeHash.put("MMR","MM");
        countryCodeHash.put("NAM","NA");
        countryCodeHash.put("NRU","NR");
        countryCodeHash.put("NPL","NP");
        countryCodeHash.put("NLD","NL");
        countryCodeHash.put("ANT","AN");
        countryCodeHash.put("NCL","NC");
        countryCodeHash.put("NZL","NZ");
        countryCodeHash.put("NIC","NI");
        countryCodeHash.put("NER","NE");
        countryCodeHash.put("NGA","NG");
        countryCodeHash.put("NIU","NU");
        countryCodeHash.put("NFK","NF");
        countryCodeHash.put("MNP","MP");
        countryCodeHash.put("NOR","NO");
        countryCodeHash.put("OMN","OM");
        countryCodeHash.put("PAK","PK");
        countryCodeHash.put("PLW","PW");
        countryCodeHash.put("PSE","PS");
        countryCodeHash.put("PAN","PA");
        countryCodeHash.put("PNG","PG");
        countryCodeHash.put("PRY","PY");
        countryCodeHash.put("PER","PE");
        countryCodeHash.put("PHL","PH");
        countryCodeHash.put("PCN","PN");
        countryCodeHash.put("POL","PL");
        countryCodeHash.put("PRT","PT");
        countryCodeHash.put("PRI","PR");
        countryCodeHash.put("QAT","QA");
        countryCodeHash.put("REU","RE");
        countryCodeHash.put("ROU","RO");
        countryCodeHash.put("RUS","RU");
        countryCodeHash.put("RWA","RW");
        countryCodeHash.put("SHN","SH");
        countryCodeHash.put("KNA","KN");
        countryCodeHash.put("LCA","LC");
        countryCodeHash.put("SPM","PM");
        countryCodeHash.put("VCT","VC");
        countryCodeHash.put("WSM","WS");
        countryCodeHash.put("SMR","SM");
        countryCodeHash.put("STP","ST");
        countryCodeHash.put("SAU","SA");
        countryCodeHash.put("SEN","SN");
        countryCodeHash.put("SCG","CS");
        countryCodeHash.put("SYC","SC");
        countryCodeHash.put("SLE","SL");
        countryCodeHash.put("SGP","SG");
        countryCodeHash.put("SVK","SK");
        countryCodeHash.put("SVN","SI");
        countryCodeHash.put("SLB","SB");
        countryCodeHash.put("SOM","SO");
        countryCodeHash.put("ZAF","ZA");
        countryCodeHash.put("SGS","GS");
        countryCodeHash.put("ESP","ES");
        countryCodeHash.put("LKA","LK");
        countryCodeHash.put("SDN","SD");
        countryCodeHash.put("SUR","SR");
        countryCodeHash.put("SJM","SJ");
        countryCodeHash.put("SWZ","SZ");
        countryCodeHash.put("SWE","SE");
        countryCodeHash.put("CHE","CH");
        countryCodeHash.put("SYR","SY");
        countryCodeHash.put("TWN","TW");
        countryCodeHash.put("TJK","TJ");
        countryCodeHash.put("TZA","TZ");
        countryCodeHash.put("THA","TH");
        countryCodeHash.put("TLS","TL");
        countryCodeHash.put("TGO","TG");
        countryCodeHash.put("TKL","TK");
        countryCodeHash.put("TON","TO");
        countryCodeHash.put("TTO","TT");
        countryCodeHash.put("TUN","TN");
        countryCodeHash.put("TUR","TR");
        countryCodeHash.put("TKM","TM");
        countryCodeHash.put("TCA","TC");
        countryCodeHash.put("TUV","TV");
        countryCodeHash.put("UGA","UG");
        countryCodeHash.put("UKR","UA");
        countryCodeHash.put("ARE","AE");
        countryCodeHash.put("GBR","GB");
        countryCodeHash.put("USA","US");
        countryCodeHash.put("UMI","UM");
        countryCodeHash.put("URY","UY");
        countryCodeHash.put("UZB","UZ");
        countryCodeHash.put("VUT","VU");
        countryCodeHash.put("VAT","VA");
        countryCodeHash.put("VEN","VE");
        countryCodeHash.put("VNM","VN");
        countryCodeHash.put("VGB","VG");
        countryCodeHash.put("VIR","VI");
        countryCodeHash.put("WLF","WF");
        countryCodeHash.put("ESH","EH");
        countryCodeHash.put("YEM","YE");
        countryCodeHash.put("ZMB","ZM");
        countryCodeHash.put("ZWE","ZW");
    }
    /*public static String doHttpPostConnection( String urlString,String request, String username,String password)
    {
        HttpURLConnection connection = null;
        String response="";
        try
        {
            URL url = new URL(urlString);


            String user_pass = username + ":" + password;
            String encoded = Base64.getEncoder().encodeToString(user_pass.getBytes());

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.addRequestProperty("Accept", "application/xml");
            connection.setRequestProperty("Content-Type", "application/xml");
            connection.addRequestProperty("Authorization", "Basic " + encoded);
            connection.setDoOutput(true);
            connection.setReadTimeout(60000);
            if (request != null) {
                OutputStream outputStream = null;
                try {
                    outputStream = connection.getOutputStream();
                    outputStream.write(request.getBytes("UTF-8"));
                } finally {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            }
            connection.getResponseCode();

            if (response != null) {
                InputStream responseStream  = null;
                try {
                    responseStream = connection.getResponseCode() == 422 ? connection.getErrorStream()
                            : connection.getInputStream();
                    String xml = inputStreamToString(responseStream);
                    response=xml;
                } finally {
                    if (responseStream != null) {
                        responseStream.close();
                    }
                }
            }
        }
        catch (MalformedURLException e)
        {
            transactionLogger.error("MalformedURLException --- > ",e);
        }
        catch (ProtocolException e)
        {
            transactionLogger.error("ProtocolException --- > ", e);
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException --- > ", e);
        }
        finally
        {
            connection.disconnect();
        }
        return response;
    }*/
    public static String doPostHTTPSURLConnectionClient(String strURL, String req, String authType, String username,String password)
    {
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            String user_pass = username + ":" + password;
            String encodedCredentials = Base64.getEncoder().encodeToString(user_pass.getBytes());
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Authorization", authType + " " + encodedCredentials);
            post.addRequestHeader("Content-Type", "text/xml");
            post.addRequestHeader("Cache-Control", "no-cache");
            post.setRequestBody(req);

            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException --- > ",he);
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException --- > ",io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
    public static String inputStreamToString(InputStream inputStream) throws IOException {
        InputStreamReader inputReader = new InputStreamReader(inputStream);
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[0x1000];
        int bytesRead = inputReader.read(buffer, 0, buffer.length);
        while (bytesRead >= 0) {
            builder.append(buffer, 0, bytesRead);
            bytesRead = inputReader.read(buffer, 0, buffer.length);
        }
        return builder.toString();
    }
    public static String getCentAmount(String amount)
    {
        BigDecimal bigDecimal = new BigDecimal(amount);
        bigDecimal = bigDecimal.multiply(new BigDecimal(100));
        Long newAmount = bigDecimal.longValue();
        return newAmount.toString();
    }

    public static String getJPYAmount(String amount)
    {
        double amt= Double.parseDouble(amount);
        double roundOff=Math.round(amt);
        int value=(int)roundOff;
        amount=String.valueOf(value);
        return amount.toString();
    }
    public static String getKWDSupportedAmount(String amount)
    {
        transactionLogger.debug("formatting amount for KWD Currency ---");
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 1000;
        String amt = d.format(dObj2);
        return amt;
    }
    public static Map<String, String> readSoapResponse(String response) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside readSoapResponse ---");
        Map<String, String> responseMap = new HashMap<String, String>();

        try
        {
            Document doc = createDocumentFromString(response.trim());
            NodeList nList = doc.getElementsByTagName("payment_response");

            responseMap.put("transaction_type",getTagValue("transaction_type",((Element) nList.item(0))));
            responseMap.put("status",getTagValue("status",((Element) nList.item(0))));
            responseMap.put("authorization_code",getTagValue("authorization_code",((Element) nList.item(0))));
            responseMap.put("unique_id",getTagValue("unique_id",((Element) nList.item(0))));
            responseMap.put("transaction_id",getTagValue("transaction_id",((Element) nList.item(0))));
            responseMap.put("response_code",getTagValue("response_code",((Element) nList.item(0))));
            responseMap.put("technical_message",getTagValue("technical_message",((Element) nList.item(0))));
            responseMap.put("message",getTagValue("message",((Element) nList.item(0))));
            responseMap.put("mode",getTagValue("mode",((Element) nList.item(0))));
            responseMap.put("timestamp",getTagValue("timestamp",((Element) nList.item(0))));
            responseMap.put("descriptor",getTagValue("descriptor",((Element) nList.item(0))));
            responseMap.put("amount",getTagValue("amount",((Element) nList.item(0))));
            responseMap.put("currency",getTagValue("currency",((Element) nList.item(0))));
            responseMap.put("sent_to_acquirer",getTagValue("sent_to_acquirer",((Element) nList.item(0))));
            responseMap.put("redirect_url",getTagValue("redirect_url",((Element) nList.item(0))));
            responseMap.put("transaction_date",getTagValue("transaction_date",((Element) nList.item(0))));
            responseMap.put("auth_code",getTagValue("auth_code",((Element) nList.item(0))));

        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
        return responseMap;
    }
    private static String getTagValue(String sTag, Element eElement)
    {
        // transactionLogger.error("Inside getTagValue ---");
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
    public static Document createDocumentFromString(String xmlString) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside createDocumentFromString ---");
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
            transactionLogger.error("Exception in createDocumentFromString of ECPUtils", pce);
            PZExceptionHandler.raiseTechnicalViolationException("ECPUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e)
        {
            transactionLogger.error("Exception in createDocumentFromString ECPUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("ECPUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.SAXEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            transactionLogger.error("Exception in createDocumentFromString ECPUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("ECPUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return doc;
    }
    public static   HashMap<String,String> getToken(String currency,Boolean isTest,String accountId)
    {
        transactionLogger.error("Inside getToken ---");
        Connection con=null;
        HashMap<String,String> tokenMap=new HashMap<>();
        String isTestNew="N";
        if (isTest)
            isTestNew="Y";

        String token = null;
        try{
            con= Database.getConnection();
            String query = "SELECT non3D_token,3D_token,payout_token FROM gateway_accounts_emerchant WHERE currency=? AND isTest=? AND accountId=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,currency);
            ps.setString(2, isTestNew);
            ps.setString(3, accountId);
            transactionLogger.error("getToken ps---->"+ps);
            ResultSet rs=ps.executeQuery();
            if (rs.next()){
                tokenMap.put("non3D_token",rs.getString("non3D_token"));
                tokenMap.put("3D_token",rs.getString("3D_token"));
                tokenMap.put("payout_token",rs.getString("payout_token"));
            }
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException  ---" , e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError  ---", systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return tokenMap;
    }
    public static   String checkPreviousTransaction3D_Non3D(String trackingId)
    {
        transactionLogger.error("Inside getTransaction3D_Non3D ---");
        transactionLogger.error("tracking id --------"+trackingId);
        Connection con=null;
        String is3D="N";
        try{
            con= Database.getConnection();
            String query = "SELECT STATUS FROM transaction_common_details WHERE trackingid = ? AND STATUS = '3D_authstarted'";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, trackingId);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                is3D= "Y";
            }else{
                is3D= "N";
            }
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException  ---" , e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError  ---", systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }
        transactionLogger.error("is 3D -------"+is3D);
        return is3D;
    }
    public static String getCountryTwoDigit(String countryCode)
    {
        return countryCodeHash.get(countryCode);
    }
    public static String isNull(String s)
    {
        if(functions.isValueNull(s))
            return s;
        else
            return "";
    }
}
