package com.payment.ilixium;

import com.directi.pg.Base64;
import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.payment.cardpay.CardPayErrorCode;
import com.payment.common.core.Comm3DResponseVO;
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
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vivek on 3/31/2020.
 */
public class IlixiumUtils
{
    private static TransactionLogger transactionLogger=new TransactionLogger(IlixiumUtils.class.getName());

    public static String doPostHTTPSURLConnectionClient(String strURL,String req,String hash) throws PZTechnicalViolationException
    {
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {

            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "text/xml");
            post.addRequestHeader("X-MERCHANT-DIGEST", hash);
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException he--->",he);
        }
        catch (IOException io){
            transactionLogger.error("IOException io--->", io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
    public static String sha512(String input)
    {
        String sha = input;
        sha.trim();
        String hexString ="";
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(sha.getBytes("UTF-8"));
            hexString= Base64.encode(hash);
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException e-->",e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException e-->", e);
        }

        return hexString.toString().trim();
    }
    public Map<String, String> readSoapResponse(String response) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside readSoapResponse ---");
        Map<String, String> responseMap = new HashMap<String, String>();

        try
        {
            Document doc = createDocumentFromString(response.trim());
            NodeList nList = doc.getElementsByTagName("paymentResponse");

            responseMap.put("type",getTagValue("type",((Element) nList.item(0))));
            responseMap.put("status",getTagValue("code",((Element) nList.item(0))));
            responseMap.put("code",getTagValue("code",((Element) nList.item(0))));
            responseMap.put("gatewayRef",getTagValue("gatewayRef",((Element) nList.item(0))));
            responseMap.put("merchantRef",getTagValue("merchantRef",((Element) nList.item(0))));
            responseMap.put("message",getTagValue("message",((Element) nList.item(0))));
            responseMap.put("reason",getTagValue("reason",((Element) nList.item(0))));
            responseMap.put("timestamp",getTagValue("timestamp",((Element) nList.item(0))));
            responseMap.put("descriptor",getTagValue("descriptor",((Element) nList.item(0))));
            responseMap.put("amount",getTagValue("amount",((Element) nList.item(0))));
            responseMap.put("currency",getTagValue("currency",((Element) nList.item(0))));
            responseMap.put("threeDSecureAcsUrl",getTagValue("threeDSecureAcsUrl",((Element) nList.item(0))));
            responseMap.put("threeDSecureMd",getTagValue("threeDSecureMd",((Element) nList.item(0))));
            responseMap.put("threeDSecurePaReq",getTagValue("threeDSecurePaReq",((Element) nList.item(0))));

        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
      /*  for (Map.Entry<String,String> entry:responseMap.entrySet())
        {
            String key=entry.getKey();
            String value=entry.getValue();
            transactionLogger.debug("key line :::::"+key);
            transactionLogger.debug("value line :::::"+value);
        }*/
        return responseMap;
    }
    public Map<String, Map<String, String>> readSoapResponseGorInquiry(String response) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside readSoapResponse ---");
        Map<String, String> innerResponseMap = new HashMap<String, String>();
        Map<String, Map<String, String>> responseMap = new HashMap<String, Map<String, String>>();
        String trackingId="";
        try
        {
            Document doc = createDocumentFromString(response.trim());
            NodeList nList = doc.getElementsByTagName("historyResponse");
            NodeList nList2 = doc.getElementsByTagName("operation");
            trackingId=getTagValue("merchantRef",((Element) nList.item(0)));
            innerResponseMap.put("type",getTagValue("type",((Element) nList2.item(0))));
            innerResponseMap.put("status",getTagValue("code",((Element) nList.item(0))));
            innerResponseMap.put("code",getTagValue("code",((Element) nList2.item(0))));
            innerResponseMap.put("gatewayRef",getTagValue("gatewayRef",((Element) nList.item(0))));
            innerResponseMap.put("merchantRef",getTagValue("merchantRef",((Element) nList.item(0))));
            innerResponseMap.put("message",getTagValue("message",((Element) nList2.item(0))));
            innerResponseMap.put("timestamp",getTagValue("timestamp",((Element) nList.item(0))));
            innerResponseMap.put("amount",getTagValue("amount",((Element) nList2.item(0))));
            innerResponseMap.put("currency",getTagValue("currency",((Element) nList2.item(0))));

            responseMap.put(trackingId,innerResponseMap);
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
        return responseMap;
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
            transactionLogger.error("ParserConfigurationException in createDocumentFromString of IlixiumUtils",pce);
            PZExceptionHandler.raiseTechnicalViolationException(IlixiumUtils.class.getName(), "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e)
        {
            transactionLogger.error("SAXException in createDocumentFromString CardPayUtils",e);
            PZExceptionHandler.raiseTechnicalViolationException(IlixiumUtils.class.getName(),"createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.SAXEXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException in createDocumentFromString CardPayUtils",e);
            PZExceptionHandler.raiseTechnicalViolationException(IlixiumUtils.class.getName(),"createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }
        return document;
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
}
