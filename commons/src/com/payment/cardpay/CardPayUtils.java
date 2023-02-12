package com.payment.cardpay;

import com.directi.pg.*;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.payforasia.core.PayforasiaPaymentGateway;
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
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Admin on 7/25/2018.
 */
public class CardPayUtils
{
    private static CardPayLogger transactionLogger= new CardPayLogger(CardPayUtils.class.getName());

    public static String doPostHTTPSURLConnectionClient(String strURL,String req) throws PZTechnicalViolationException
    {
        transactionLogger.error("strURL:::::"+ strURL);
        String result = "";

        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(req);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException ---->"+he);
        }
        catch (IOException io){
            transactionLogger.error("IOException ---->"+io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String doPostHTTPSURLConnectionClient(String strURL,String req,String token) throws PZTechnicalViolationException
    {
        String result = "";

        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Authorization", "Bearer "+token);
            post.setRequestBody(req);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException ---->"+he);
        }
        catch (IOException io){
            io.printStackTrace();
            transactionLogger.error("IOException ---->"+io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }


    public  static String doGetHTTPSURLConnectionClient(String url,String authType,String clientCredentials) throws PZTechnicalViolationException
    {
        transactionLogger.debug("url--->" + url);
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();

        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        String result = "";
        try
        {

            method.addRequestHeader("Authorization", authType + " " + clientCredentials);
            method.setRequestHeader("Content-Type", "application/json");
           // method.setRequestHeader("Cache-Control", "no-cache");

            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK)
            {
                transactionLogger.debug("Method failed: " + method.getStatusLine());
            }

            byte[] response = method.getResponseBody();
            transactionLogger.debug("Response-----" + response.toString());
            result = new String(response);

            method.releaseConnection();
        }
        catch (HttpException he)
        {
            transactionLogger.debug("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("CardPayUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.debug("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("CardPayUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }

        if (result == null)
            return "";
        else
            return result;
    }

    public static String sha512(String input) throws PZTechnicalViolationException
    {
        String sha = input;
        sha.trim();
        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayforasiaPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayforasiaPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return hexString.toString().trim();
    }

    public static String sha256(String input) throws PZTechnicalViolationException
    {
        String sha = input;
        sha.trim();
        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException ---->"+e);
            PZExceptionHandler.raiseTechnicalViolationException(PayforasiaPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException ---->"+e);
            PZExceptionHandler.raiseTechnicalViolationException(PayforasiaPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return hexString.toString().trim();
    }

    public static void main(String[] args)
    {
        String response="<response refund_id=\"1208231\" is_executed=\"yes\">\n" +
                "    <order id=\"1208227\" status_to=\"refund\" currency=\"USD\"  refund_amount=\"10.00\" remaining_amount=\"90.00\" />\n" +
                "</response>";
        try
        {
            readXmlStatusResponse(response, "pz");

        }catch (Exception e){
            transactionLogger.error("Exception ---->"+e);
        }
    }

    public static Comm3DResponseVO readXmlResponse(String response,String descriptor) throws PZTechnicalViolationException
    {
        Functions functions= new Functions();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Document doc = createDocumentFromString(response);
        String url="";
        String md="";
        String paReq="";
        String transactionId="";
        String remark="";
        String date="";
        String errorCode="";
        String approvalCode="";
        String transType="";
        String amount="";
        String currency="";
        String description="";
        String status="";
        String decline_code="";

        NodeList nList = doc.getElementsByTagName("redirect");
        NodeList nList1=doc.getElementsByTagName("order");


        if(nList.getLength() > 0){
            if(functions.isValueNull(String.valueOf(nList.item(0).getAttributes().getNamedItem("url"))))
                url = nList.item(0).getAttributes().getNamedItem("url").getNodeValue();
            if(functions.isValueNull(String.valueOf(nList.item(0).getAttributes().getNamedItem("MD"))))
                md = nList.item(0).getAttributes().getNamedItem("MD").getNodeValue();
            if(functions.isValueNull(String.valueOf(nList.item(0).getAttributes().getNamedItem("PaReq"))))
                paReq = nList.item(0).getAttributes().getNamedItem("PaReq").getNodeValue();
        }else if(nList1.getLength() > 0) {
            if(functions.isValueNull(String.valueOf(nList1.item(0).getAttributes().getNamedItem("date"))))
                date = nList1.item(0).getAttributes().getNamedItem("date").getNodeValue();
            if(functions.isValueNull(String.valueOf(nList1.item(0).getAttributes().getNamedItem("id"))))
                transactionId = nList1.item(0).getAttributes().getNamedItem("id").getNodeValue();
            if(functions.isValueNull(String.valueOf(nList1.item(0).getAttributes().getNamedItem("status"))))
                status = nList1.item(0).getAttributes().getNamedItem("status").getNodeValue();
            if(functions.isValueNull(String.valueOf(nList1.item(0).getAttributes().getNamedItem("description"))))
                description = nList1.item(0).getAttributes().getNamedItem("description").getNodeValue();
            if(functions.isValueNull(String.valueOf(nList1.item(0).getAttributes().getNamedItem("approval_code"))))
                approvalCode = nList1.item(0).getAttributes().getNamedItem("approval_code").getNodeValue();
            if(functions.isValueNull(String.valueOf(nList1.item(0).getAttributes().getNamedItem("decline_code"))))
                errorCode = nList1.item(0).getAttributes().getNamedItem("decline_code").getNodeValue();
            if(functions.isValueNull(String.valueOf(nList1.item(0).getAttributes().getNamedItem("is_3d"))))
                transType = nList1.item(0).getAttributes().getNamedItem("is_3d").getNodeValue();
            if(functions.isValueNull(String.valueOf(nList1.item(0).getAttributes().getNamedItem("amount"))))
                amount = nList1.item(0).getAttributes().getNamedItem("amount").getNodeValue();
            if(functions.isValueNull(String.valueOf(nList1.item(0).getAttributes().getNamedItem("currency"))))
                currency = nList1.item(0).getAttributes().getNamedItem("currency").getNodeValue();
        }

        transactionLogger.debug("url------"+url);
        transactionLogger.debug("md------"+md);
        transactionLogger.debug("paReq------"+paReq);
        transactionLogger.debug("date------"+date);
        transactionLogger.debug("transactionId------"+transactionId);
        transactionLogger.debug("status------"+status);
        transactionLogger.debug("description------"+description);
        transactionLogger.debug("errorCode------"+errorCode);
        transactionLogger.debug("transType------"+transType);
        transactionLogger.debug("approvalCode------"+approvalCode);

        if(functions.isValueNull(url))
        {
            transactionLogger.debug("---inside 3d-----");
            commResponseVO.setStatus("pending3DConfirmation");
            commResponseVO.setUrlFor3DRedirect(url);
            commResponseVO.setPaReq(paReq);
            commResponseVO.setMd(md);
            commResponseVO.setRemark("Authentication Pending");
            commResponseVO.setDescription("Authentication Pending");
            commResponseVO.setCurrency(currency);
            commResponseVO.setAmount(amount);
            return commResponseVO;
        }else
        {
            transactionLogger.error("----inside non-3D" + response);
            if("Approved".equalsIgnoreCase(status) || "Pending".equalsIgnoreCase(status))
            {
                status = "success";
                commResponseVO.setDescriptor(descriptor);
            }/*else if("Pending".equalsIgnoreCase(status)){
                status="success";
                description="successful";
            }*/
            else {
                status="fail";
            }
            if(functions.isValueNull(errorCode))
            {
                remark = CardPayErrorCode.getDescription(errorCode);
            }
            if(!functions.isValueNull(remark)){
                remark=description;
            }
            transactionLogger.error("-----remark-----"+remark);
            commResponseVO.setStatus(status);
            commResponseVO.setRemark(remark);
            commResponseVO.setDescription(description);
            commResponseVO.setBankTransactionDate(date);
            commResponseVO.setErrorCode(errorCode);
            commResponseVO.setAuthCode(approvalCode);
            commResponseVO.setTransactionId(transactionId);
            commResponseVO.setAmount(amount);
            commResponseVO.setCurrency(currency);
        }
        return commResponseVO;
    }

    public static Comm3DResponseVO readXmlStatusResponse(String response,String descriptor) throws PZTechnicalViolationException
    {
        Functions functions= new Functions();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Document doc = createDocumentFromString(response);
        String transactionId="";
        String description="";
        String currency="";
        String status="";
        String transType="";
        String refund_amount="";
        String payout_amount="";

        String is_executed = "";


        NodeList nList = doc.getElementsByTagName("order");
        NodeList nList1 = doc.getElementsByTagName("response");

      //  System.out.println("nList-------------------"+(nList.item(0).getAttributes['']));
//        System.out.println("nList-------------------"+(String.valueOf(nList.item(0).getAttributes().getNamedItem("status")) ));



        if(nList.getLength() > 0)
        {
            is_executed = nList1.item(0).getAttributes().getNamedItem("is_executed").getNodeValue();
            transactionId = nList.item(0).getAttributes().getNamedItem("id").getNodeValue();
            transType = nList.item(0).getAttributes().getNamedItem("status_to").getNodeValue();

            if(functions.isValueNull(String.valueOf(nList.item(0).getAttributes().getNamedItem("status"))))
                status = nList.item(0).getAttributes().getNamedItem("status").getNodeValue();
            if(functions.isValueNull(String.valueOf(nList.item(0).getAttributes().getNamedItem("currency"))))
                currency = nList.item(0).getAttributes().getNamedItem("currency").getNodeValue();
            if(functions.isValueNull(String.valueOf(nList.item(0).getAttributes().getNamedItem("refund_amount"))))
                refund_amount = nList.item(0).getAttributes().getNamedItem("refund_amount").getNodeValue();
            if(functions.isValueNull(String.valueOf(nList.item(0).getAttributes().getNamedItem("payout_amount"))))
                payout_amount = nList.item(0).getAttributes().getNamedItem("payout_amount").getNodeValue();
        }

            transactionLogger.debug("----inside non-3D");
            if("yes".equalsIgnoreCase(is_executed))
            {
                status = "success";
                commResponseVO.setDescriptor(descriptor);

            }else
            {
                status="fail";
            }
            commResponseVO.setStatus(status);
            commResponseVO.setRemark(status);
            commResponseVO.setDescription(status);
            commResponseVO.setTransactionId(transactionId);
            commResponseVO.setTransactionType(transType);
        return commResponseVO;
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
            transactionLogger.error("Exception in createDocumentFromString of CardPayUtils",pce);
            PZExceptionHandler.raiseTechnicalViolationException("CardPayUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e)
        {
            transactionLogger.error("Exception in createDocumentFromString CardPayUtils",e);
            PZExceptionHandler.raiseTechnicalViolationException("CardPayUtils.java","createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.SAXEXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (IOException e)
        {
            transactionLogger.error("Exception in createDocumentFromString CardPayUtils",e);
            PZExceptionHandler.raiseTechnicalViolationException("CardPayUtils.java","createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }
        return document;
    }


    public static String doPostFormHTTPSURLConnectionClient(String strURL,String req) throws PZTechnicalViolationException
    {
        transactionLogger.error("strURL:::::"+ strURL);
        String result = "";

        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(req);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException in doPostFormHTTPSURLConnectionClient CardPayUtils ---->"+he);
        }
        catch (IOException io){
            transactionLogger.error("IOException in doPostFormHTTPSURLConnectionClient CardPayUtils ---->"+io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String doPostHTTPSURLConnection(String strURL,String req,String token)
    {
        String result = "";

        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Authorization", "Bearer "+token);
            post.setRequestBody(req);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException ---->"+he);
        }
        catch (IOException io){
            transactionLogger.error("IOException ---->"+io);
        }catch (Exception e){
            transactionLogger.error("IOException ---->"+e);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public  static String doGetHTTPSURLConnectionClient(String url,String token) throws PZTechnicalViolationException
    {
        transactionLogger.debug("url--->" + url);
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();

        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        String result = "";
        try
        {


            method.addRequestHeader("Content-Type", "application/json");
            method.addRequestHeader("Authorization", "Bearer "+token);

            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK)
            {
                transactionLogger.debug("Method failed: " + method.getStatusLine());
            }

            byte[] response = method.getResponseBody();
            result = new String(response);

            method.releaseConnection();
        }
        catch (HttpException he)
        {
            transactionLogger.debug("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("CardPayUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.debug("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("CardPayUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }

        if (result == null)
            return "";
        else
            return result;
    }

    public static void updatePaymentId(String paymentid, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET paymentid=? WHERE trackingid=?";
            PreparedStatement ps2   = connection.prepareStatement(updateQuery1);
            ps2.setString(1, paymentid);
            ps2.setString(2, trackingid);
            transactionLogger.error("payg updateOrderid--->"+ps2);

            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            transactionLogger.error("SQLException---", se);
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

}