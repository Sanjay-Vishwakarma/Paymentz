package com.payment.powercash21;

import com.directi.pg.*;
import com.payment.Enum.PZProcessType;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.nestpay.NestPayPaymentGateway;
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
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Admin on 1/7/2022.
 */
public class PowerCash21Utils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(PowerCash21Utils.class.getName());
    private static Logger log = new Logger(PowerCash21Utils.class.getName());


    public static HashMap<String, String> readresponsep21(String demoresponse){


        String[] saleResponse=demoresponse.split("&");
        HashMap<String, String> responseHM = new HashMap<>();

        System.out.println("saleResponse "+saleResponse);

        if(saleResponse.length>0)
        {
            for(String keyValue: saleResponse){
                System.out.println("keyValue "+keyValue);

                String[] splitValue = keyValue.split("=");

                System.out.println("length "+splitValue.length);
                if(splitValue.length == 2){

                    responseHM.put(splitValue[0],splitValue[1]);
                }else if(splitValue.length == 1){
                    responseHM.put(splitValue[0],"");
                }
            }
            System.out.println("map "+responseHM);;

        }
        return responseHM;
    }



    public static String getSignature(TreeMap<String ,String> signatureHM,String secret)
    {
        String signature="";
        try
        {
            for(String key : signatureHM.keySet()){
                signature = signature + signatureHM.get(key);

            }
            signature = signature + secret;
            return hashMac(signature);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public static String hashMac(String sha) throws Exception
    {
        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest messageDigest=MessageDigest.getInstance("SHA-1");
            byte[] hash=messageDigest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NestPayPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NestPayPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return hexString.toString().toLowerCase();
    }

    public static String doPostHTTPSURLConnectionClient(String strURL, String request) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside doPostHTTPSURLConnectionClient of PowerCash21Utils ===== " + strURL);

        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException httpException)
        {
            transactionLogger.error("PowerCash21Utils:: HttpException----- " + httpException);

        }
        catch (IOException ioException)
        {
            transactionLogger.error("PowerCash21Utils:: IOException----- " + ioException);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String doHttpPostConnection(String url, String request)
    {
        String result = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");

            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException e)
        {
            transactionLogger.error("HttpException--------->",e);

        }
        catch (IOException e)
        {
            transactionLogger.error("IOException--------->",e);
        }

        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public Document createDocumentFromString(String xmlString) throws PZTechnicalViolationException
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
            log.error("Exception in createDocumentFromString of ECPUtils", pce);
            transactionLogger.error("Exception in createDocumentFromString of ECPUtils", pce);
            PZExceptionHandler.raiseTechnicalViolationException("ECPUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e)
        {
            log.error("Exception in createDocumentFromString ECPUtils", e);
            transactionLogger.error("Exception in createDocumentFromString ECPUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("ECPUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.SAXEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            log.error("Exception in createDocumentFromString ECPUtils", e);
            transactionLogger.error("Exception in createDocumentFromString ECPUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("ECPUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return doc;
    }

    private  String getTagValue(String sTag, Element eElement)
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

    public Map<String, String> readSoapResponse(String response) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside readSoapResponse ---");
        Map<String, String> responseMap = new HashMap<String, String>();
        Functions functions=new Functions();
        try
        {
            Document doc = createDocumentFromString(response.trim());
            NodeList process = doc.getElementsByTagName("process");
            NodeList transaction = doc.getElementsByTagName("transaction");
            NodeList step1 = doc.getElementsByTagName("step1");
            NodeList step2 = doc.getElementsByTagName("step2");
            NodeList step3 = doc.getElementsByTagName("step3");
            NodeList step4 = doc.getElementsByTagName("step4");
            NodeList nList = doc.getElementsByTagName("response");


            responseMap.put("error_message",getTagValue("error_message",((Element) process.item(0))));
            responseMap.put("process_time",getTagValue("process_time",((Element) process.item(0))));
            responseMap.put("status",getTagValue("status",((Element) process.item(0))));
            responseMap.put("transaction_id",getTagValue("transaction_id",((Element) transaction.item(0))));

            if(step4 != null && step4.getLength()>0){

                responseMap.put("amount",getTagValue("amount",((Element) step4.item(0))));
                responseMap.put("creation_date",getTagValue("creation_date",((Element) step4.item(0))));
                responseMap.put("currency",getTagValue("currency",((Element) step4.item(0))));
                responseMap.put("custom1",getTagValue("custom1",((Element) step4.item(0))));
                responseMap.put("error_code",getTagValue("error_code",((Element) step4.item(0))));
                responseMap.put("order_id",getTagValue("order_id",((Element) step4.item(0))));
                //responseMap.put("type", PZProcessType.REFUND.toString());
                responseMap.put("type",getTagValue("type",((Element) step4.item(0))));

            }
            else if(step3 != null && step3.getLength()>0){

                responseMap.put("amount",getTagValue("amount",((Element) step3.item(0))));
                responseMap.put("creation_date",getTagValue("creation_date",((Element) step3.item(0))));
                responseMap.put("currency",getTagValue("currency",((Element) step3.item(0))));
                responseMap.put("custom1",getTagValue("custom1",((Element) step3.item(0))));
                responseMap.put("error_code",getTagValue("error_code",((Element) step3.item(0))));
                responseMap.put("order_id",getTagValue("order_id",((Element) step3.item(0))));
                //responseMap.put("type", PZProcessType.REFUND.toString());
                responseMap.put("type",getTagValue("type",((Element) step3.item(0))));

            }
            /*else  if(step2 != null )
            {
                if(step2.getLength()<=1)
                {
                    System.out.println("INSIDE STEP2======>"+step2.getLength());//1
                responseMap.put("amount",getTagValue("amount",((Element) step2.item(0))));
                responseMap.put("creation_date",getTagValue("creation_date",((Element) step2.item(0))));
                responseMap.put("currency",getTagValue("currency",((Element) step2.item(0))));
                responseMap.put("custom1",getTagValue("custom1",((Element) step2.item(0))));
                responseMap.put("error_code",getTagValue("error_code",((Element) step2.item(0))));
                responseMap.put("order_id",getTagValue("order_id",((Element) step2.item(0))));
                //responseMap.put("type",PZProcessType.SALE.toString());
                responseMap.put("type",getTagValue("type",((Element) step2.item(0))));
                }
                else{

                    System.out.println("INSIDE else STEP1======>"+step1.getLength());//1
                responseMap.put("amount",getTagValue("amount",((Element) step1.item(0))));
                responseMap.put("creation_date",getTagValue("creation_date",((Element) step1.item(0))));
                responseMap.put("currency",getTagValue("currency",((Element) step1.item(0))));
                responseMap.put("custom1",getTagValue("custom1",((Element) step1.item(0))));
                responseMap.put("error_code",getTagValue("error_code",((Element) step1.item(0))));
                responseMap.put("order_id",getTagValue("order_id",((Element) step1.item(0))));
                //responseMap.put("type",PZProcessType.SALE.toString());
                responseMap.put("type",getTagValue("type",((Element) step1.item(0))));

            }


            }*/
           else  if(step2 != null && step2.getLength()>0){

                responseMap.put("order_id",getTagValue("order_id",((Element) step2.item(0))));

                String orderid=responseMap.get("order_id");
                if(functions.isValueNull(orderid)){

                    responseMap.put("amount",getTagValue("amount",((Element) step2.item(0))));
                    responseMap.put("creation_date",getTagValue("creation_date",((Element) step2.item(0))));
                    responseMap.put("currency",getTagValue("currency",((Element) step2.item(0))));
                    responseMap.put("custom1",getTagValue("custom1",((Element) step2.item(0))));
                    responseMap.put("error_code",getTagValue("error_code",((Element) step2.item(0))));
                    responseMap.put("order_id",getTagValue("order_id",((Element) step2.item(0))));
                    //responseMap.put("type",PZProcessType.SALE.toString());
                    responseMap.put("type",getTagValue("type",((Element) step2.item(0))));

                }
                else if(!functions.isValueNull(orderid))
                {
                    responseMap.put("amount", getTagValue("amount", ((Element) step1.item(0))));
                    responseMap.put("creation_date", getTagValue("creation_date", ((Element) step1.item(0))));
                    responseMap.put("currency", getTagValue("currency", ((Element) step1.item(0))));
                    responseMap.put("custom1", getTagValue("custom1", ((Element) step1.item(0))));
                    responseMap.put("error_code", getTagValue("error_code", ((Element) step1.item(0))));
                    responseMap.put("order_id", getTagValue("order_id", ((Element) step1.item(0))));
                    // responseMap.put("type",PZProcessType.AUTH.toString());
                    responseMap.put("type", getTagValue("type", ((Element) step1.item(0))));
                }

            }
            else{

                responseMap.put("amount",getTagValue("amount",((Element) step1.item(0))));
                responseMap.put("creation_date",getTagValue("creation_date",((Element) step1.item(0))));
                responseMap.put("currency",getTagValue("currency",((Element) step1.item(0))));
                responseMap.put("custom1",getTagValue("custom1",((Element) step1.item(0))));
                responseMap.put("error_code",getTagValue("error_code",((Element) step1.item(0))));
                responseMap.put("order_id",getTagValue("order_id",((Element) step1.item(0))));
               // responseMap.put("type",PZProcessType.AUTH.toString());
                responseMap.put("type",getTagValue("type",((Element) step1.item(0))));

            }

        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }

        return responseMap;
    }

    public String getDBAmount(String trackingId){

        Connection con=null;
        PreparedStatement ps=null;
        ResultSet rs= null;
        String response="";
        try
        {
           // String query = "Select captureamount from transaction_common where trackingid='" + trackingId + "'";
            String query = "SELECT amount FROM transaction_common_details WHERE TrackingId="+ trackingId +" AND STATUS='authsuccessful'";
            con = Database.getConnection();
            ps = con.prepareStatement(query);
            rs=ps.executeQuery();
            transactionLogger.debug("Sql Query-----" + ps);
            System.out.println("Sql Query-----"+ps);
            if (rs.next())
            {
              //  response = rs.getString("captureamount");
                response = rs.getString("amount");
            }
        }
        catch (SystemError e)
        {
            transactionLogger.error("SystemError-----", e);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException-----", e);
        }
        return response;
    }
}
