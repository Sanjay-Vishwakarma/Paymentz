package com.payment.airpay;

import com.directi.pg.AirPayTransactionLogger;
import com.directi.pg.Database;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
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
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by Admin on 10/17/2021.
 */
public class AirpayUtils
{
    //private static TransactionLogger transactionlogger         = new TransactionLogger(AirpayUtils.class.getName());
    private static AirPayTransactionLogger transactionlogger      = new AirPayTransactionLogger(AirpayUtils.class.getName());

    private static Stack<MessageDigest> stack       = new Stack<MessageDigest>();
    private final static String hashingAlgo         = "SHA-256";
    private static final Charset ASCII              = Charset.forName("UTF-8");

    public String getPaymentType(String paymentMode)
    {
        String payMode = "";
        if("CC".equalsIgnoreCase(paymentMode))
            payMode = "pg";
        if("DC".equalsIgnoreCase(paymentMode))
            payMode = "pg";
        if("NBI".equalsIgnoreCase(paymentMode))
            payMode = "nb";
        if("UPI".equalsIgnoreCase(paymentMode))
            payMode = "upi";

        return payMode;
    }

    private static MessageDigest provide() throws NoSuchAlgorithmException
    {
        MessageDigest digest = null;

        try {
            digest = stack.pop();
        } catch (EmptyStackException emptyStackException) {
            digest = MessageDigest.getInstance(hashingAlgo);
        }
        return digest;
    }

    private static void consume(MessageDigest digest) {
        stack.push(digest);
    }

    // Generate hash from the supplied string
    public static String getPrivateKey(String input) throws NoSuchAlgorithmException {
        String response = null;
        MessageDigest md1 = provide();
        md1.update(input.getBytes());
        byte byteData[] = md1.digest();
        StringBuffer sb1 = new StringBuffer();
        for (int i = 0; i < byteData.length; i++)
        {
            sb1.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        response = sb1.toString();

        return response;
    }


    public static String getChecksum(String input) throws NoSuchAlgorithmException {
        String response     = null;
        MessageDigest md    = MessageDigest.getInstance("MD5");
        byte[] array        = md.digest(input.getBytes());

        StringBuffer sb     = new StringBuffer();

        for (int i = 0; i < array.length; ++i)
        {
            sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
        }
        response = sb.toString();

        return response;
    }

    public CommRequestVO getAirPayRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO             = new CommRequestVO();
        CommAddressDetailsVO addressDetailsVO   = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO         = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO           = new CommMerchantVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());

        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());

        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getProcessorName());
        commRequestVO.setCustomerId(commonValidatorVO.getVpa_address());
        transactionlogger.error("utils vpa--->" + commonValidatorVO.getVpa_address());


        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }

    public Boolean updateTransaction (String trackingid, String  customerId ){

        transactionlogger.error("in side  updateTransaction----------->"+customerId);
        Connection con = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate=false;
        try
        {

            con                 = Database.getConnection();
            String update       = "update transaction_common set customerId= ? where trackingid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1, customerId);
            psUpdateTransaction.setString(2, trackingid);
            transactionlogger.error("transaction common query----"+psUpdateTransaction);
            int i=psUpdateTransaction.executeUpdate();
            if(i>0)
            {
                isUpdate=true;
            }
        }

        catch (SQLException e)
        {
            transactionlogger.error("SQLException----",e);

        }
        catch (SystemError systemError)
        {
            transactionlogger.error("SystemError----", systemError);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            if(con!=null){
                Database.closeConnection(con);
            }
        }
        return isUpdate;

    }

    public static String doGetHTTPSURLConnectionClient(String url,String request) throws PZTechnicalViolationException
    {
        String result   = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.setRequestHeader("Content-Type", "application/json");

            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he)
        {
            transactionlogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("AirpayUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionlogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("AirpayUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {
            post.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result.trim();
    }

    public static Document createDocumentFromString(String xmlString) throws PZTechnicalViolationException
    {
        Document document = null;
        try
        {
            DocumentBuilderFactory docFactory   = DocumentBuilderFactory.newInstance();
            docFactory.setCoalescing(true);
            DocumentBuilder docBuilder          = docFactory.newDocumentBuilder();
            document                            = docBuilder.parse(new InputSource(new StringReader(xmlString)));

        }
        catch (ParserConfigurationException pce){
            transactionlogger.error("Exception in createDocumentFromString of AirpayUtils", pce);
            PZExceptionHandler.raiseTechnicalViolationException("AirpayUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e){
            transactionlogger.error("Exception in createDocumentFromString AirpayUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("AirpayUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.SAXEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e){
            transactionlogger.error("Exception in createDocumentFromString AirpayUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("AirpayUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
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

    public static Map<String, String> readXMLReponse(String str) throws Exception
    {
        Map<String, String> responseMap = new HashMap<String, String>();

        Document doc = createDocumentFromString(str);
        doc.getDocumentElement().getNodeName();
        NodeList nList  = null;

        nList = doc.getElementsByTagName("TRANSACTION");

        responseMap.put("APTRANSACTIONID", getTagValue("APTRANSACTIONID", ((Element) nList.item(0))));
        responseMap.put("TRANSACTIONPAYMENTSTATUS", getTagValue("TRANSACTIONPAYMENTSTATUS", ((Element) nList.item(0))));
        responseMap.put("TRANSACTIONID", getTagValue("TRANSACTIONID", ((Element) nList.item(0))));
        responseMap.put("AMOUNT", getTagValue("AMOUNT", ((Element) nList.item(0))));
        responseMap.put("TRANSACTIONSTATUS", getTagValue("TRANSACTIONSTATUS", ((Element) nList.item(0))));
        responseMap.put("AMOUNT", getTagValue("AMOUNT", ((Element) nList.item(0))));
        responseMap.put("AUTHCODE", getTagValue("AUTHCODE", ((Element) nList.item(0))));
        responseMap.put("CONVERSIONRATE", getTagValue("CONVERSIONRATE", ((Element) nList.item(0))));
        responseMap.put("TRANSACTIONTIME", getTagValue("TRANSACTIONTIME", ((Element) nList.item(0))));
        responseMap.put("MESSAGE", getTagValue("MESSAGE", ((Element) nList.item(0))));
        responseMap.put("RRN", getTagValue("RRN", ((Element) nList.item(0))));



        return responseMap;
    }


    public static String doGetMehtodHTTPSURLConnectionClient(String url,String request) throws PZTechnicalViolationException
    {
        String result   = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he)
        {

        }
        catch (IOException io)
        {
        }finally
        {
            post.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }


}
