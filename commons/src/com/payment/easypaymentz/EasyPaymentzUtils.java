package com.payment.easypaymentz;

import com.directi.pg.Database;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.*;
import com.payment.validators.vo.CommonValidatorVO;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;

import io.jsonwebtoken.*;
import okhttp3.*;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Map;
/**
 * Created by Admin on 6/23/2021.
 */
public class EasyPaymentzUtils
{
    private final static String separator               = "~";
    private final static String equator                 = "=";
    private final static String hashingAlgo             = "SHA-256";
    private static Stack<MessageDigest> stack           = new Stack<MessageDigest>();

    private static TransactionLogger transactionlogger  = new TransactionLogger(EasyPaymentzUtils.class.getName());

    public static String encryptSignature(String secretKey, Map<String, String> postData) throws NoSuchAlgorithmException, InvalidKeyException
    {
        String data             = "";
        SortedSet<String> keys  = new TreeSet<String>(postData.keySet());

        for (String key : keys) {
            data = data + key + postData.get(key);
        }
        Mac sha256_HMAC                 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key_spec   = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");

        sha256_HMAC.init(secret_key_spec);

        String signature                = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(data.getBytes()));
        postData.put("signature", signature.replace("=", ""));

        transactionlogger.error("EasyPaymentzUtils signature-->"+signature);

        return getString(postData);
    }

    private static  String getString(Map<String, String> parameters) {

        Map<String, String> treeMap = new TreeMap<String, String>(parameters);
        StringBuilder allFields     = new StringBuilder();

        for (String key : treeMap.keySet()) {
            allFields.append(separator);
            allFields.append(key);
            allFields.append(equator);
            allFields.append(treeMap.get(key));
        }

        allFields.deleteCharAt(0);
        return allFields.toString();
    }
    public static String encryptInputData(String data, String saltKey , String secret) {
        Base64.Encoder base64Encoder = Base64.getEncoder();
        try {
            Cipher cipher       = Cipher.getInstance("AES/CBC/NoPadding");
            int blockSize       = cipher.getBlockSize();
            byte[] dataBytes    = data.getBytes();
            int plaintextLength = dataBytes.length;

            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }
            byte[] plaintext = new byte[plaintextLength];

            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keyspec   = new SecretKeySpec(secret.getBytes(), "AES");
            IvParameterSpec ivspec  = new IvParameterSpec(saltKey.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

            byte[] encrypted    = cipher.doFinal(plaintext);

            return new String(base64Encoder.encode(encrypted));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String getAmount(String amount)
    {
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2    = Double.valueOf(amount);
        dObj2           = dObj2 * 100;
        String amt      = d.format(dObj2);
        return amt;
    }
    public String getPaymentType(String paymentMode)
    {
        String payMode = "";
        if("CC".equalsIgnoreCase(paymentMode))
            payMode = "CC";
        if("DC".equalsIgnoreCase(paymentMode))
            payMode = "DC";
        if("NBI".equalsIgnoreCase(paymentMode))
            payMode = "NB";
        if("EWI".equalsIgnoreCase(paymentMode))
            payMode = "WL";
        if("UPI".equalsIgnoreCase(paymentMode))
            payMode = "UP";

        return payMode;
    }

    public Boolean updateTransaction (String trackingid, String  customerId ){

        transactionlogger.error("in side  updateTransaction----------->"+customerId);
        Connection con      = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate    = false;
        try
        {

            con                 = Database.getConnection();
            String update       = "update transaction_common set customerId= ? where trackingid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());

            psUpdateTransaction.setString(1, customerId);
            psUpdateTransaction.setString(2, trackingid);

            transactionlogger.error("transaction common query----"+psUpdateTransaction);
            int i   = psUpdateTransaction.executeUpdate();

            if(i>0)
            {
                isUpdate    = true;
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
            Database.closeConnection(con);
        }
        return isUpdate;

    }
    public CommRequestVO getEasyPayRequestVO(CommonValidatorVO commonValidatorVO)
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
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getProcessorName());
        commRequestVO.setCustomerId(commonValidatorVO.getVpa_address());
        transactionlogger.error("utils vpa--->" + commonValidatorVO.getVpa_address());

        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }


    public void updateMainTableEntry(String remark, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET remark=? WHERE trackingid=?";
            PreparedStatement ps2   = connection.prepareStatement(updateQuery1);

            ps2.setString(1, remark);
            ps2.setString(2, trackingid);
            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            transactionlogger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            transactionlogger.error("SystemError---", s);
        }
        finally
        {
            if(connection!=null)
                Database.closeConnection(connection);
        }
    }

    public static String doPostHTTPSURLConnectionClient(String post_data,String requestUrl,String tokenString ) throws PZTechnicalViolationException
    {
        String result       = "";
        PostMethod post     = new PostMethod(requestUrl);
        try
        {
            HttpClient httpClient = new HttpClient();

            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.addRequestHeader("Authorization", tokenString);

            post.setRequestBody(post_data);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he){
            transactionlogger.error("HttpException --->", he);
            PZExceptionHandler.raiseTechnicalViolationException(EasyPaymentzUtils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io){
            transactionlogger.error("IOException --->", io);
            PZExceptionHandler.raiseTechnicalViolationException(EasyPaymentzUtils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }


    public static String doHttpPostConnection(String url, String request,String MERCHANT_ID,String SECRET_KEY) throws PZTechnicalViolationException
    {
        String result   = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();

            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("merchantid", MERCHANT_ID);
            post.addRequestHeader("sec", SECRET_KEY);

            post.setRequestBody(request);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result          = response;
        }catch (HttpException he){
            transactionlogger.error("HttpException --->", he);
            PZExceptionHandler.raiseTechnicalViolationException(EasyPaymentzUtils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io){
            transactionlogger.error("IOException --->", io);
            PZExceptionHandler.raiseTechnicalViolationException(EasyPaymentzUtils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String generateCheckSum(Map<String, String> parameters,String secretKey)  {
        Map<String, String> treeMap     = new TreeMap<String, String>(parameters);
        StringBuilder allFields         = new StringBuilder();
        String generateCheckSum         = "";
        try{
            for (String key : treeMap.keySet()) {
                allFields.append(separator);
                allFields.append(key);
                allFields.append(equator);
                allFields.append(treeMap.get(key));
            }

            allFields.deleteCharAt(0);
            allFields.append(secretKey);
            generateCheckSum = getHash(allFields.toString());
        }catch (Exception e){
            transactionlogger.error("Exception generateCheckSum --->", e);
        }
        return generateCheckSum;
    }
    public static String createJWTToken (Map<String, Object> claims,Map<String, Object> header,String SECRET_KEY) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis  = System.currentTimeMillis();
        Date now        = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes =SECRET_KEY.getBytes();

        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setHeader(header)
                .setClaims(claims)
                .signWith(signatureAlgorithm, signingKey);


        //Builds the JWT and serializes it to a compact, URL-safe string
        //System.out.println("token ::: "+builder.compact());
        return builder.compact();
    }


    public static String getHash(String input) throws NoSuchAlgorithmException {
        String response = null;

        MessageDigest messageDigest = provide();
        messageDigest.update(input.getBytes());
        consume(messageDigest);

        response = new String(Hex.encodeHex(messageDigest.digest()));

        return response.toUpperCase();
    }

    private static MessageDigest provide() throws NoSuchAlgorithmException {
        MessageDigest digest = null;

        try {
                digest = stack.pop();
        } catch (EmptyStackException emptyStackException) {
            digest = MessageDigest.getInstance(hashingAlgo);
        }
        return digest;
    }

    public static String generateCheckSum2(String secretKey)  {

        String generateCheckSum         = "";
        try{
            generateCheckSum = getHash(secretKey);
            }
        catch (Exception e){
            transactionlogger.error("Exception generateCheckSum --->", e);
        }
        return generateCheckSum;
    }

    static String doMultipartBodyConnection(String url ,String orderId,String secret,String appId)
    {
        String result = "";
        OkHttpClient client =null;
        Response response2=null;
        try
        {
             client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("multipart/form-data");
            MultipartBody multipartBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("appId", appId)
                    .addFormDataPart("orderId",orderId)
                    .addFormDataPart("secret",secret)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", multipartBody)
                    .build();
            response2 = client.newCall(request).execute();
            result=response2.body().string();
        }
        catch (HttpException e)
        {
            transactionlogger.error("HttpException--------->" + e);

        }
        catch (IOException e)
        {
            transactionlogger.error("IOException--------->"+e);
        }
        finally  
        {
            response2.close();

        }

        return result;
    }
    public static String  geDummyMobileNo(String paymentMode)
    {  String randomMobileNo="";
        int num1, num2, num3; //3 numbers in area code
        int set2, set3; //sequence 2 and 3 of the phone number
        int low = 7;
        int high = 9;

        Random generator = new Random();

        num1 = generator.nextInt(high - low) + low;
        num2 = generator.nextInt(high - low) + low;
        num3 = generator.nextInt(high - low) + low;

        set2 = generator.nextInt(643) + 100;

        set3 = generator.nextInt(8999) + 1000;

        transactionlogger.error(num1 + "" + num2 + "" + num3 + set2 + set3);
        return randomMobileNo =num1 + "" + num2 + "" + num3 + set2 + set3;
    }

    private static void consume(MessageDigest digest) {
        stack.push(digest);
    }

    public static String base64UrlDecoder(String str) {
        return Base64.getUrlEncoder().encodeToString(str.getBytes());
    }

}
