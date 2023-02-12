package com.payment.EPaySolution;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.apache.commons.httpclient.HttpException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import sun.misc.BASE64Encoder;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Jitendra on 01-Sep-18.
 */
public class EPaySolutionUtils
{
    private static TransactionLogger transactionLogger= new TransactionLogger(EPaySolutionUtils.class.getName());
    public static String doPostHTTPSURLConnection(String strURL, String req) throws PZTechnicalViolationException
    {
        StringBuffer result = new StringBuffer();
        Functions functions=new Functions();
        URL obj;
        HttpURLConnection con=null;
        BufferedReader in = null;
        try
        {
            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            System.setProperty("http.agent", "");
           // java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            System.setProperty("https.protocols", "TLSv1.2");

            obj = new URL(strURL);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(req);
            wr.flush();
            wr.close();

             in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                result.append(inputLine);
            }

        }

        catch (HttpException he)
        {
            PZExceptionHandler.raiseTechnicalViolationException("EPaySolutionUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("EPaySolutionUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            if(in !=null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    transactionLogger.error("IOException EPaySolutionUtils ::::",e);
                }
            }

        }
        return result.toString();
    }

    public static String doPostHTTPSURLConnectionJSON(String strURL, String req) throws PZTechnicalViolationException
    {
        StringBuffer result = new StringBuffer();
        Functions functions=new Functions();
        URL obj;
        HttpURLConnection con=null;
        BufferedReader in = null;
        try
        {
            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            System.setProperty("http.agent", "");
           // java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            System.setProperty("https.protocols", "TLSv1.2");

            obj = new URL(strURL);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("Content-Type","application/json");
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(req);
            wr.flush();
            wr.close();

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                result.append(inputLine);
            }

        }

        catch (HttpException he)
        {
            PZExceptionHandler.raiseTechnicalViolationException("EPaySolutionUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("EPaySolutionUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            if(in !=null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    transactionLogger.error("IOException EPaySolutionUtils ::::",e);
                }
            }

        }
        return result.toString();
    }


    public static String aesEncrypt(String reqest, String signKey) throws Exception
    {
        return base64Encode(aesEncryptToBytes(reqest, signKey));
    }

    public static String base64Encode(byte[] bytes)
    {
        return new BASE64Encoder().encode(bytes);
    }

    public static byte[] aesEncryptToBytes(String reqest, String signKey) throws Exception
    {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(signKey.getBytes(), "AES"));
        return cipher.doFinal(reqest.getBytes("utf-8"));
    }

    public static String SHA256forSales(String merNo, String orderNo, String orderAmount, String email, String returnURL, String currency, String signkey) throws PZTechnicalViolationException
    {
        String sha = merNo.trim() + orderNo.trim() + orderAmount + email.trim() + returnURL.trim() + currency.trim() + signkey.trim();

        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sha.getBytes());
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EPaySolutionUtils.class.getName(), "SHA256forSales()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return hexString.toString();
    }

    public static String SHA256forRefund(Long merNo,String gatewayNo, String tradeNo,String signkey) throws PZTechnicalViolationException
    {
        String sha = merNo + gatewayNo.trim() + tradeNo.trim() +signkey.trim();
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
            PZExceptionHandler.raiseTechnicalViolationException(EPaySolutionUtils.class.getName(), "SHA256forSales()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EPaySolutionUtils.class.getName(), "SHA256forSales()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return hexString.toString().trim();
    }

    public static String SHA256forInquire(String merNo,String gatewayNo, String inquiryOrderId,String signkey) throws PZTechnicalViolationException
    {
        String sha = merNo.trim() + gatewayNo.trim() + inquiryOrderId.trim() +signkey.trim();
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
            PZExceptionHandler.raiseTechnicalViolationException(EPaySolutionUtils.class.getName(), "SHA256forSales()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EPaySolutionUtils.class.getName(), "SHA256forSales()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return hexString.toString().trim();
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
            transactionLogger.error("Exception in createDocumentFromString of EPaySolutionUtils",pce);
            PZExceptionHandler.raiseTechnicalViolationException("EPaySolutionUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e)
        {
            transactionLogger.error("Exception in createDocumentFromString EPaySolutionUtils",e);
            PZExceptionHandler.raiseTechnicalViolationException("EPaySolutionUtils.java","createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.SAXEXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (IOException e)
        {
            transactionLogger.error("Exception in createDocumentFromString EPaySolutionUtils",e);
            PZExceptionHandler.raiseTechnicalViolationException("EPaySolutionUtils.java","createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }
        return document;
    }

    public static CommResponseVO readXmlResponse(String response) throws PZTechnicalViolationException
    {
        Functions functions= new Functions();
        CommResponseVO commResponseVO=new Comm3DResponseVO();
        Document doc = createDocumentFromString(response);
        NodeList nList = doc.getElementsByTagName("returnData");

        String merNo=(getTagValue("merNo", ((Element) nList.item(0))));
        String gatewayNo=(getTagValue("gatewayNo", ((Element) nList.item(0))));
        String tradeNo=(getTagValue("tradeNo", ((Element) nList.item(0))));
        String orderNo=(getTagValue("orderNo", ((Element) nList.item(0))));
        String returnCode=(getTagValue("returnCode", ((Element) nList.item(0))));
        String returnMsg=(getTagValue("returnMsg", ((Element) nList.item(0))));
        String orderAmount=(getTagValue("orderAmount", ((Element) nList.item(0))));
        String orderCurrency=(getTagValue("orderCurrency", ((Element) nList.item(0))));
        String signData=(getTagValue("signData", ((Element) nList.item(0))));
        String remark=(getTagValue("remark", ((Element) nList.item(0))));
        String billingDes=(getTagValue("acquirer", ((Element) nList.item(0))));
        System.out.println("returnMsg 230------" + returnMsg);

        String status="";
        String ErrorDescription="";
        if (functions.isValueNull(returnCode))
        {
            ErrorDescription=EPaySolnErrorCode.getDescription(returnCode);
        }
        if("00".equalsIgnoreCase(returnCode))
        {
            status="success";
        }
        else if("-1".equalsIgnoreCase(returnCode))
        {
            status = "pending";
        }
        else
        {
            status="fail";
        }

        if(functions.isValueNull(ErrorDescription)){
            commResponseVO.setRemark(ErrorDescription);
            commResponseVO.setDescription(ErrorDescription);
        }else {
            commResponseVO.setRemark(returnMsg);
            commResponseVO.setDescription(returnMsg);
        }

        commResponseVO.setStatus(status);
        commResponseVO.setErrorCode(returnCode);
        commResponseVO.setMerchantId(merNo);
        commResponseVO.setTransactionId(tradeNo);
        commResponseVO.setDescriptor(billingDes);

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


    public static void main(String[] args)
    {
        String month="2022";
        String expiryMonth=  month.substring(2,4);
        System.out.println(expiryMonth);
    }
}
