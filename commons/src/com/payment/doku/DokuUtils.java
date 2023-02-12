package com.payment.doku;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpStatus;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Rihen on 5/25/2021.
 */
public class DokuUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(DokuUtils.class.getName());

    public static String doPostHTTPSURLConnectionClient(String strURL,String req,String clientID,String requestID, String secKey) throws PZTechnicalViolationException
    {
        transactionLogger.error("strURL:::::"+ strURL);
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String timeStamp=simpleDateFormat.format(new Date());

            //signature prep
            String digest = DokuUtils.generateDigest(req);
            System.out.println("digest == "+digest);
            transactionLogger.error("digest == " + digest);
            System.out.println("timestamp == "+timeStamp);
            transactionLogger.error("timestamp == " + timeStamp);

            String requestTarget = "/checkout/v1/payment";

            String signature = DokuUtils.getSignature(clientID, requestID, timeStamp, digest, secKey, requestTarget);

            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Client-Id", clientID);
            post.addRequestHeader("Request-Id", requestID);
            post.addRequestHeader("Request-Timestamp", timeStamp);
            post.addRequestHeader("Signature", signature);
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Accept", "application/json");
            post.addRequestHeader("Cache-Control", "no-cache");
            post.setRequestBody(req);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException In DokuUtils :::::", he);
        }
        catch (IOException io){
            transactionLogger.error("IOException In DokuUtils :::::", io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public  static String doGetHTTPSURLConnectionClient(String url, String clientID, String requestID, String secKey) throws PZTechnicalViolationException
    {
        transactionLogger.debug("url--->" + url);
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();

        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        String result = "";
        try
        {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String timeStamp=simpleDateFormat.format(new Date());

            //signature prep
            String digest= "";
            System.out.println("timestamp == "+timeStamp);
            transactionLogger.error("timestamp == " + timeStamp);

            String requestTarget = "/orders/v1/status/"+requestID;

            String signature = DokuUtils.getSignature(clientID, requestID, timeStamp, digest, secKey, requestTarget);

            method.addRequestHeader("Client-Id", clientID);
            method.addRequestHeader("Request-Id", requestID);
            method.addRequestHeader("Request-Timestamp", timeStamp);
            method.addRequestHeader("Signature", signature);
            method.addRequestHeader("Content-Type", "application/json");
            method.addRequestHeader("Accept", "application/json");
            method.addRequestHeader("Cache-Control", "no-cache");

            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK)
            {
                transactionLogger.debug("Method failed: " + method.getStatusLine());
            }

            byte[] response = method.getResponseBody();
            transactionLogger.debug("Response-----" + response.toString());
            result = new String(response);

        }
        catch (HttpException he)
        {
            transactionLogger.debug("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("DokuUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null,he.getMessage(),he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.debug("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("DokuUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {
            method.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }


    public static String getSignature(String clientID, String requestID, String timeStamp, String digest, String secKey, String requestTarget){
        String sign = "";

        System.out.println("----- Component Signature -----");

        try
        {
            StringBuilder component = new StringBuilder();
            component.append("Client-Id").append(":").append(clientID);
            component.append("\n");
            component.append("Request-Id").append(":").append(requestID);
            component.append("\n");
            component.append("Request-Timestamp").append(":").append(timeStamp);
            component.append("\n");
            component.append("Request-Target").append(":").append(requestTarget);
            // If body not send when access API with HTTP method GET/DELETE
            if (digest != null && !digest.isEmpty())
            {
                component.append("\n");
                component.append("Digest").append(":").append(digest);
            }

            System.out.println(component.toString());
            System.out.println();

            // Calculate HMAC-SHA256 base64 from all the components above
            byte[] decodedKey = secKey.getBytes();
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            hmacSha256.init(originalKey);
            hmacSha256.update(component.toString().getBytes());
            byte[] HmacSha256DigestBytes = hmacSha256.doFinal();
            String signature = Base64.getEncoder().encodeToString(HmacSha256DigestBytes);
            // Prepend encoded result with algorithm info HMACSHA256=
            sign = "HMACSHA256=" + signature;
        }
        catch(Exception e) {
            transactionLogger.error("Exception ----", e);
        }

        return sign;
    }


    // Generate Digest
    public static String generateDigest(String myBodyJson) {
        String sign = "";
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(myBodyJson.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            sign = Base64.getEncoder().encodeToString(digest);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception ----", e);
        }

        return sign;
    }

    public static String getTransactionType(String transType){

        if("hold".equalsIgnoreCase(transType))
        {
            transType= PZProcessType.AUTH.toString();
        }
        else if("cancelled".equalsIgnoreCase(transType))
        {
            transType=PZProcessType.CANCEL.toString();
        }
        else
        {
            transType=PZProcessType.INQUIRY.toString();
        }

        return transType;
    }


    public static CommRequestVO getCommRequestFromUtils(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("inside DokuUtils getCommRequestFromUtils");
        CommRequestVO commRequestVO = new CommRequestVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();

        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();

        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();

        addressDetailsVO.setFirstname(genericAddressDetailsVO.getFirstname());
        addressDetailsVO.setLastname(genericAddressDetailsVO.getLastname());
        addressDetailsVO.setStreet(genericAddressDetailsVO.getStreet());
        addressDetailsVO.setCity(genericAddressDetailsVO.getCity());
        addressDetailsVO.setPhone(genericAddressDetailsVO.getPhone());
        addressDetailsVO.setState(genericAddressDetailsVO.getState());
        addressDetailsVO.setIp(genericAddressDetailsVO.getIp());
        addressDetailsVO.setEmail(genericAddressDetailsVO.getEmail());
        addressDetailsVO.setZipCode(genericAddressDetailsVO.getZipCode());
        addressDetailsVO.setCountry(genericAddressDetailsVO.getCountry());

        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setCustomerId(commonValidatorVO.getCustomerId());

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }
    public static String getAmount(String amount)
    {
        double amt      = Double.parseDouble(amount);
        double roundOff = Math.round(amt);
        int value       = (int)roundOff;
        amount          = String.valueOf(value);
        return amount.toString();
    }


}