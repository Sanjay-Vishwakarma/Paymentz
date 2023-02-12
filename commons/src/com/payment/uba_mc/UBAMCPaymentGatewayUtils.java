package com.payment.uba_mc;

import com.directi.pg.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLHandshakeException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.ListIterator;


/**
 * Created by Admin on 2022-01-22.
 */
public class UBAMCPaymentGatewayUtils
{

    private static TransactionLogger transactionLogger  = new TransactionLogger(UBAMCPaymentGatewayUtils.class.getName());
    static Functions functions                          = new Functions();
    private final static String charset                 = "UTF-8";

    public  static String getLast2DigitOfExpiryYear(String ExpiryYear)
    {
        String expiryYearLast2Digit = "";
        if (functions.isValueNull(ExpiryYear)){
            expiryYearLast2Digit = ExpiryYear.substring(ExpiryYear.length()-2,ExpiryYear.length());
        }
        return expiryYearLast2Digit;
    }

    public static boolean isJSONValid(String test)
    {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    public static String getAmount(String amount)
    {
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2    = Double.valueOf(amount);
        dObj2           = dObj2 * 100;
        String amt      = d.format(dObj2);
        return amt;
    }

    public static void updateMainTableEntry(String transactionId, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET paymentid=? WHERE trackingid=?";
            PreparedStatement ps2   = connection.prepareStatement(updateQuery1);
            ps2.setString(1, transactionId);
            ps2.setString(2, trackingid);
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

    public  static  byte[] generateHmacSHA256(String algorithm, byte[] key, byte[] message)
    {
        Mac mac = null;
        try
        {
            mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key,algorithm));
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception while cretaing hmac --> " + e);
        }
        return mac.doFinal(message);
    }

    public static String bytesToHex(byte[] hashInBytes)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hashInBytes.length; i++) {
            sb.append(Integer.toString((hashInBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String getHmac256Signature(String message, byte[] key)
    {
        byte[] bytes = generateHmacSHA256("HmacSHA256", key, message.getBytes());
        return bytesToHex(bytes);
    }

    public static String doGetPostHTTPUrlConnection(String MerchantKey,String Sign,String url,String request) throws PZTechnicalViolationException
    {
        String response = "";
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();
        System.setProperty("https.protocols", "TLSv1.3");

        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(url);
        try{
            postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            postMethod.addRequestHeader("MerchantKey",MerchantKey);
            postMethod.addRequestHeader("Sign",Sign);
            postMethod.setRequestBody(request);

            httpClient.executeMethod(postMethod);
            response = new String(postMethod.getResponseBody());

            Header[] headers=  postMethod.getResponseHeaders();
            transactionLogger.error("response Headers[]: ");
            for(Header header2 :headers)
            {
                transactionLogger.error(header2.getName() + " : " + header2.getValue());
            }
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("OmnipayPaymentGatewayUtils.java", "doGetPostHTTPUrlConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("OmnipayPaymentGatewayUtils.java", "doGetPostHTTPUrlConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            postMethod.releaseConnection();
        }
        return response;
    }

    public static String  doPostHttpUrlConnection(String REQUEST_URL,String data,String ApiUsername,String Password) throws Exception {
        HttpClient httpClient   = new HttpClient();
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();

        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(ApiUsername, Password));

        String userPassword         = ApiUsername + ":" + Password;
        String encodedCredentials   = Base64.encode(userPassword.getBytes());

        PutMethod putMethod     = new PutMethod(REQUEST_URL);
        String body = null;
        try {
                putMethod.addRequestHeader("Authorization", "Basic " + encodedCredentials);
                putMethod.setDoAuthentication(true);

                StringRequestEntity entity = new StringRequestEntity(data, "application/json", "UTF-8");
                putMethod.setRequestEntity(entity);

                httpClient.executeMethod(putMethod);
                body    = putMethod.getResponseBodyAsString();

        } catch (HttpException he)
        {
            transactionLogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("OmnipayPaymentGatewayUtils.java", "doGetPostHTTPUrlConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("OmnipayPaymentGatewayUtils.java", "doGetPostHTTPUrlConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }finally {
            putMethod.releaseConnection();
        }
        return body;
    }


    public static String  doGetHttpUrlConnection(String REQUEST_URL,String ApiUsername,String Password) throws Exception {
        HttpClient httpClient   = new HttpClient();
        GetMethod getMethod     = new GetMethod(REQUEST_URL);
        String body             = null;
        String userPassword         = ApiUsername + ":" + Password;
        String encodedCredentials   = Base64.encode(userPassword.getBytes());
        httpClient.getState().setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(ApiUsername, Password));
        try {
            getMethod.addRequestHeader("Authorization", "Basic " + encodedCredentials);
            getMethod.setDoAuthentication(true);
            httpClient.executeMethod(getMethod);
            body        = getMethod.getResponseBodyAsString();
        } catch (HttpException he)
        {
            //transactionLogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("OmnipayPaymentGatewayUtils.java", "doGetPostHTTPUrlConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            //transactionLogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("OmnipayPaymentGatewayUtils.java", "doGetPostHTTPUrlConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }finally {
            getMethod.releaseConnection();
        }
        return body;
    }





}
