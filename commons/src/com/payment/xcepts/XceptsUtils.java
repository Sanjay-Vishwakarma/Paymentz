package com.payment.xcepts;

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
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.util.HashMap;


/**
 * Created by Admin on 2022-05-14.
 */
public class XceptsUtils
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(XceptsUtils.class.getName());
    static Functions functions                          = new Functions();

    public static void updateMainTableEntry(String transactionId, String transaction_mode, String trackingid)
    {
        Connection connection = null;
        try
        {
            String addParam="";
            if(functions.isValueNull(transaction_mode)){
                addParam=",transaction_mode='"+transaction_mode+"'";
            }
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET paymentid='"+transactionId+"' "+addParam+" WHERE trackingid="+trackingid;
            Database.executeUpdate(updateQuery1, connection);
            transactionLogger.error("updateQuery1>>>>> "+ updateQuery1);
        }
        catch (SystemError s)
        {
            transactionLogger.error("SystemError---", s);
        }
        finally
        {
            if(connection!=null){
                Database.closeConnection(connection);
            }
        }
    }

    public  static HashMap<String,String> readQueryStringResponse(String str)
    {
        HashMap<String,String> hashMap = new HashMap<>();
        String[] response;
        try
        {
            if (functions.isValueNull(str))
            {
                if (str.contains("?") && str.split("\\?").length > 0)
                {
                    response = str.split("\\?");
                    if (response[1].contains("&"))
                    {
                        String[] resp = response[1].split("\\&");
                        for (int i = 0; i<resp.length; i++)
                        {
                            hashMap.put(resp[i].split("=")[0],resp[i].split("=")[1]);
                        }
                    }
                }
                else if (!str.contains("?") && str.contains("&") && str.split("\\&").length >0)
                {
                    String[] resp = str.split("\\&");;
                    for (int i = 0; i<resp.length; i++)
                    {
                        hashMap.put(resp[i].split("=")[0],resp[i].split("=")[1]);
                    }
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception while reading queryString Response>>>>>> "+e);
        }
        return hashMap;
    }

    public static HashMap<String, String> readXMLResponse(String str) throws Exception
    {
        HashMap<String, String> responseMap = new HashMap<String, String>();

        NodeList nList  = null;
        Document doc    = createDocumentFromString(str);
        doc.getDocumentElement().getNodeName();

        try
        {
            nList = doc.getElementsByTagName("response");

            responseMap.put("result", getTagValue("result", ((Element) nList.item(0))));
            responseMap.put("responsecode", getTagValue("responsecode", ((Element) nList.item(0))));
            responseMap.put("authcode", getTagValue("authcode", ((Element) nList.item(0))));
            responseMap.put("RRN", getTagValue("RRN", ((Element) nList.item(0))));
            responseMap.put("ECI", getTagValue("ECI", ((Element) nList.item(0))));
            responseMap.put("tranid", getTagValue("tranid", ((Element) nList.item(0))));
            responseMap.put("trackid", getTagValue("trackid", ((Element) nList.item(0))));
            responseMap.put("terminalid", getTagValue("terminalid", ((Element) nList.item(0))));
            responseMap.put("threedreason", getTagValue("threedreason", ((Element) nList.item(0))));
            responseMap.put("amount", getTagValue("amount", ((Element) nList.item(0))));
            responseMap.put("targetUrl", getTagValue("targetUrl", ((Element) nList.item(0))));
            responseMap.put("payId", getTagValue("payId", ((Element) nList.item(0))));
            responseMap.put("billingDescriptor", getTagValue("billingDescriptor", ((Element) nList.item(0))));
            responseMap.put("dynamic_billing_descriptor", getTagValue("dynamic_billing_descriptor", ((Element) nList.item(0))));
            responseMap.put("currency", getTagValue("currency", ((Element) nList.item(0))));
            responseMap.put("signature", getTagValue("signature", ((Element) nList.item(0))));
            responseMap.put("subscriptionId", getTagValue("subscriptionId", ((Element) nList.item(0))));
            responseMap.put("udf1", getTagValue("udf1", ((Element) nList.item(0))));
            responseMap.put("udf2", getTagValue("udf2", ((Element) nList.item(0))));
            responseMap.put("udf3", getTagValue("udf3", ((Element) nList.item(0))));
            responseMap.put("udf4", getTagValue("udf4", ((Element) nList.item(0))));
            responseMap.put("udf5", getTagValue("udf5", ((Element) nList.item(0))));
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception While reading xml response>>>>> "+ e);
        }
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
            transactionLogger.error("Exception in createDocumentFromString of XceptsUtils", pce);
            PZExceptionHandler.raiseTechnicalViolationException("XceptsUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e){
            transactionLogger.error("Exception in createDocumentFromString XceptsUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("XceptsUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.SAXEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e){
            transactionLogger.error("Exception in createDocumentFromString XceptsUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("XceptsUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
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

    public static String doPostHTTPUrlConnection(String requestURL, String request) throws PZTechnicalViolationException
    {
        String response       = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post       = new PostMethod(requestURL);
        try
        {
            post.addRequestHeader("accept", "application/xml");
            post.addRequestHeader("content-type", "application/xml");
            post.setRequestBody(request);
            httpClient.executeMethod(post);

            response = new String(post.getResponseBody());
            transactionLogger.error("response status code: "+post.getStatusCode());
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("XceptsUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("XceptsUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            post.releaseConnection();
        }
        return response;
    }

}
