package com.payment.brd;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpStatus;
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

/**
 * Created by Admin on 10/30/2018.
 */
public class BRDUtils
{
    private static TransactionLogger transactionLogger= new TransactionLogger(BRDUtils.class.getName());

    public  static String doGetHTTPSURLConnectionClient(String url) throws PZTechnicalViolationException
    {
        transactionLogger.debug("url--->" + url);
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();

        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        String result = "";
        try
        {
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK)
            {
                transactionLogger.debug("Method failed: " + method.getStatusLine());
            }

            byte[] response = method.getResponseBody();
            transactionLogger.error("Response BRD Utils-----" + response.toString());
            result = new String(response);

            method.releaseConnection();
        }
        catch (HttpException he)
        {
            transactionLogger.debug("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("NetellerUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.debug("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("NetellerUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }

        if (result == null)
            return "";
        else
            return result;
    }


    public static String doPostHTTPSURLConnectionClient(String strURL,String req) throws PZTechnicalViolationException
    {
        transactionLogger.error("in BRDUtils doPost");
        transactionLogger.error("strURL:::::"+ strURL);
        String result = "";

        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
           // java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(req);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException -----", he);
        }
        catch (IOException io){
            transactionLogger.error("IOException -----", io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
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

    public CommResponseVO getSaleResponse(String xmlResponseString)throws PZTechnicalViolationException
    {
        CommResponseVO res= new CommResponseVO();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        String strValue = null;
        NodeList list=null;
        Document document = null;
        Functions functions = new Functions();
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(xmlResponseString)));
        }
        catch (ParserConfigurationException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("BRDUtils.java","getSaleResponse()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SAXException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("BRDUtils.java","getSaleResponse()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.SAXEXCEPTION,null,e.getMessage(),e.getCause());

        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("BRDUtils.java","getSaleResponse()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
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
                res.setStatus("fail");

                if(functions.isValueNull(getTagValue("respcode", element)) && (getTagValue("respcode", element).equals("000") || getTagValue("respcode", element).equals("001") ||
                        getTagValue("respcode", element).equals("002") || getTagValue("respcode", element).equals("003") || getTagValue("respcode", element).equals("004") ||
                        getTagValue("respcode", element).equals("005") || getTagValue("respcode", element).equals("006") || getTagValue("respcode", element).equals("007") ||
                        getTagValue("respcode", element).equals("008") || getTagValue("respcode", element).equals("009")))
                    res.setStatus("success");

                res.setErrorCode(getTagValue("respcode", element));
                res.setRemark(getTagValue("respmsg", element));
                if(functions.isValueNull(getTagValue("displaymsg", element)))
                    res.setDescription(getTagValue("displaymsg", element));
                else
                    res.setDescription(getTagValue("respmsg", element));
            }
        }
        
        return res;
    }

    public static void main(String[] args)
    {
        String xml = "<response><respcode>500</respcode><respmsg>Request should contain [amount] or it has wrong format</respmsg><displaymsg></displaymsg></response>";

        BRDUtils b = new BRDUtils();
        try
        {
            CommResponseVO c = b.getSaleResponse(xml);
            System.out.println("coide---"+c.getErrorCode());
            System.out.println("remark---"+c.getRemark());
            System.out.println("msg---"+c.getDescription());
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception -----", e);
        }
    }

}
