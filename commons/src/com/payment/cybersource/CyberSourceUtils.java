package com.payment.cybersource;

import com.cybersource.ws.client.Client;
import com.cybersource.ws.client.ClientException;
import com.cybersource.ws.client.FaultException;
import com.directi.pg.Logger;
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
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Created by Admin on 11/28/18.
 */
public class CyberSourceUtils
{
   static TransactionLogger transactionLogger= new TransactionLogger(CyberSourceUtils.class.getName());
    private static Logger log = new Logger(CyberSourceUtils.class.getName());

    public static void displayMap(String header, Map map) {
        transactionLogger.error(header);
        StringBuffer dest = new StringBuffer();

        if (map != null && !map.isEmpty()) {
            Iterator iter = map.keySet().iterator();
            String key, val;
            while (iter.hasNext()) {
                key = (String) iter.next();
                val = (String) map.get(key);
                dest.append(key + "=" + val +"&"+ "\n");
            }
        }
        transactionLogger.error(dest.toString());
    }

    public static Properties convertResourceBundleToProperties(ResourceBundle resource) {
        Properties properties = new Properties();

        Enumeration<String> keys = resource.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            properties.put(key, resource.getString(key));
        }

        return properties;
    }

    public static String getCardType(String cardType){
        String schema="";
        if("1".equalsIgnoreCase(cardType)){
            schema="001";
        }else if("2".equalsIgnoreCase(cardType)){
            schema="002";
        }else if("3".equalsIgnoreCase(cardType)){
            schema="005";
        }else if("4".equalsIgnoreCase(cardType)){
            schema="003";
        }else if("5".equalsIgnoreCase(cardType)){
            schema="004";
        }
    return schema;
    }

    public static String getResponseTime(String responseTime)
    {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
        Date date1 = null;
        String newDate="";
        try
        {
            transactionLogger.debug("response time------------"+responseTime);
            date1 = format1.parse(responseTime);
            newDate=format2.format(date1);
            transactionLogger.debug("newdate after format-------"+newDate);
        }
        catch (ParseException e)
        {
            log.error("ParseException in CyberSourceUtils---",e);
            transactionLogger.error("ParseException in CyberSourceUtils---", e);
        }

        return newDate;
    }

    public static String doPostHTTPSURLConnectionClient(String req,String strURL , String authType, String encodedCredentials) throws PZTechnicalViolationException
    {
        URL url = null;
        try
        {
            url = new URL(strURL);
        }
        catch (MalformedURLException e)
        {
            transactionLogger.error("MalformedURLException In CyberSourceUtils :::::: ",e);
        }

        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        System.setProperty("https.protocols", "TLSv1.2");
        System.setProperty("http.maxRedirects", "2");
        try
        {
            post.addRequestHeader("Authorization", authType + " " + encodedCredentials);
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he)
        {
              log.error("HttpException in CyberSourceUtils---",he);
              transactionLogger.error("HttpException in CyberSourceUtils---",he);
        }
        catch (IOException io)
        {
             log.error("IOException in CyberSourceUtils---",io);
             transactionLogger.error("IOException in CyberSourceUtils---",io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
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
             log.error("Exception in createDocumentFromString of CyberSourceUtils", pce);
             transactionLogger.error("Exception in createDocumentFromString of CyberSourceUtils", pce);
            PZExceptionHandler.raiseTechnicalViolationException("CyberSourceUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e)
        {
             log.error("Exception in createDocumentFromString CyberSourceUtils", e);
             transactionLogger.error("Exception in createDocumentFromString CyberSourceUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("CyberSourceUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.SAXEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
             log.error("Exception in createDocumentFromString CyberSourceUtils", e);
             transactionLogger.error("Exception in createDocumentFromString CyberSourceUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("CyberSourceUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
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

    public static Map<String, String> readSoapResponse(String response) throws PZTechnicalViolationException
    {
        Map<String, String> responseMap = new HashMap<String, String>();

        Document doc = createDocumentFromString(response.trim());
        // 1. BillTo
        NodeList nList1=doc.getElementsByTagName("BillTo");
        if (nList1.getLength()>0)
        {
            responseMap.put("FirstName", getTagValue("FirstName", ((Element) nList1.item(0))));
            responseMap.put("LastName", getTagValue("LastName", ((Element) nList1.item(0))));
            responseMap.put("Address1", getTagValue("Address1", ((Element) nList1.item(0))));
            responseMap.put("City", getTagValue("City", ((Element) nList1.item(0))));
            responseMap.put("State", getTagValue("State", ((Element) nList1.item(0))));
            responseMap.put("Zip", getTagValue("Zip", ((Element) nList1.item(0))));
            responseMap.put("Email", getTagValue("Email", ((Element) nList1.item(0))));
            responseMap.put("Country", getTagValue("Country", ((Element) nList1.item(0))));
            responseMap.put("Phone", getTagValue("Phone", ((Element) nList1.item(0))));
            responseMap.put("IPAddress", getTagValue("IPAddress", ((Element) nList1.item(0))));
        }

        //2. ShipTo
        NodeList nList2=doc.getElementsByTagName("ShipTo");
        if (nList2.getLength()>0)
        {
            responseMap.put("Phone", getTagValue("Phone", ((Element) nList2.item(0))));
        }

        //3. ApplicationReply
        NodeList nList3 = doc.getElementsByTagName("ApplicationReply");   //ApplicationReply  ( In ApplicationReplies Name=ics_auth)
        if (nList3.getLength()>0)
        {
            responseMap.put("RCode", getTagValue("RCode", ((Element) nList3.item(0))));
            responseMap.put("RFlag", getTagValue("RFlag", ((Element) nList3.item(0))));
            responseMap.put("RMsg", getTagValue("RMsg", ((Element) nList3.item(0))));
        }

        // 4. ApplicationReply Name=ics_bill
        // NodeList nList4=doc.getElementsByTagName("ApplicationReply Name=ics_bill");   //ApplicationReply  ( In ApplicationReplies Name=ics_bill)
        // responseMap.put("RCode",getTagValue("RCode",((Element) nList4.item(0))));
        // responseMap.put("RFlag",getTagValue("RFlag",((Element) nList4.item(0))));

        // 8. PayerAuthenticationInfo
        NodeList nList8=doc.getElementsByTagName("PayerAuthenticationInfo");
        if (nList8.getLength()>0)
        {
            responseMap.put("ECI", getTagValue("ECI", ((Element) nList8.item(0))));
        }

        //9. PaymentData
        NodeList nList9=doc.getElementsByTagName("PaymentData");
        if (nList9.getLength()>0)
        {
            responseMap.put("PaymentRequestID", getTagValue("PaymentRequestID", ((Element) nList9.item(0))));
            responseMap.put("PaymentProcessor", getTagValue("PaymentProcessor", ((Element) nList9.item(0))));
            responseMap.put("Amount", getTagValue("Amount", ((Element) nList9.item(0))));
            responseMap.put("CurrencyCode", getTagValue("CurrencyCode", ((Element) nList9.item(0))));
            responseMap.put("TotalTaxAmount", getTagValue("TotalTaxAmount", ((Element) nList9.item(0))));
            responseMap.put("AuthorizationCode", getTagValue("AuthorizationCode", ((Element) nList9.item(0))));
            responseMap.put("AVSResult", getTagValue("AVSResult", ((Element) nList9.item(0))));
            responseMap.put("AVSResultMapped", getTagValue("AVSResultMapped", ((Element) nList9.item(0))));
            responseMap.put("CVResult", getTagValue("CVResult", ((Element) nList9.item(0))));
        }

        if (responseMap!=null)
        {
            for (Map.Entry<String, String> entry : responseMap.entrySet())
            {
                String key = entry.getKey();
                String value = entry.getValue();
                transactionLogger.debug("key line :::::" + key);
                transactionLogger.debug("value line :::::" + value);
            }
        }

        return responseMap;
    }

}
