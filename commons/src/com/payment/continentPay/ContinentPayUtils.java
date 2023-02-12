package com.payment.continentPay;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Admin on 2022-04-20.
 */
public class ContinentPayUtils
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(ContinentPayUtils.class.getName());
    private static Functions functions  = new Functions();

    public static boolean isJSONValid(String test)
    {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    public  static String getLast2DigitOfExpiryYear(String ExpiryYear)
    {
        String expiryYearLast2Digit = "";
        if (functions.isValueNull(ExpiryYear)){
            expiryYearLast2Digit = ExpiryYear.substring(ExpiryYear.length()-2,ExpiryYear.length());
        }
        return expiryYearLast2Digit;
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

    public static String doPostHTTPUrlConnection(String requestURL, String Api_Token, String request) throws PZTechnicalViolationException
    {
        String response       = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post       = new PostMethod(requestURL);
        try
        {
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Authorization", "Token token=" + Api_Token);
            post.setRequestBody(request);
            httpClient.executeMethod(post);

            response = new String(post.getResponseBody());
            transactionLogger.error("sale response code: "+post.getStatusCode());

//            Header[] requestHeaders =  post.getRequestHeaders();
//            transactionLogger.error("request Headers[]: \n ");
//            for(Header header1 : requestHeaders)
//            {
//                transactionLogger.error(header1.getName() + " : " + header1.getValue());
//            }
//
//            Header[] responseHeaders =  post.getResponseHeaders();
//            transactionLogger.error("response Headers[]: \n ");
//            for(Header header2 : responseHeaders)
//            {
//                transactionLogger.error(header2.getName() + " : " + header2.getValue());
//            }
        }
        catch (HttpException he)
        {
            transactionLogger.error("ContinentPayUtils:: HttpException-----", he);
            PZExceptionHandler.raiseTechnicalViolationException(ContinentPayUtils.class.getName(), "doPostHTTPUrlConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("ContinentPayUtils:: IOException-----", io);
            PZExceptionHandler.raiseTechnicalViolationException(ContinentPayUtils.class.getName(), "doPostHTTPUrlConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            post.releaseConnection();
        }

        return  response;
    }

    public static String doGetHTTPUrlConnection(String requestURL, String Api_Token) throws PZTechnicalViolationException
    {
        String response       = "";
        HttpClient httpClient = new HttpClient();
        GetMethod get         = new GetMethod(requestURL);
        try
        {
            get.addRequestHeader("Content-Type","application/json");
            get.addRequestHeader("Authorization", "Token token=" + Api_Token);
            httpClient.executeMethod(get);

            response = new String(get.getResponseBody());
            transactionLogger.error("inquiry response code: "+get.getStatusCode());

//            Header[] requestHeaders =  get.getRequestHeaders();
//            transactionLogger.error("request Headers[]: \n ");
//            for(Header header1 : requestHeaders)
//            {
//                transactionLogger.error(header1.getName() + " : " + header1.getValue());
//            }
//
//            Header[] responseHeaders =  get.getResponseHeaders();
//            transactionLogger.error("response Headers[]: \n ");
//            for(Header header2 : responseHeaders)
//            {
//                transactionLogger.error(header2.getName() + " : " + header2.getValue());
//            }
        }
        catch (HttpException he)
        {
            transactionLogger.error("ContinentPayUtils:: HttpException-----", he);
            PZExceptionHandler.raiseTechnicalViolationException(ContinentPayUtils.class.getName(), "doGetHTTPUrlConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("ContinentPayUtils:: IOException-----", io);
            PZExceptionHandler.raiseTechnicalViolationException(ContinentPayUtils.class.getName(), "doGetHTTPUrlConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            get.releaseConnection();
        }

        return  response;
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
        return mac != null ? mac.doFinal(message) : new byte[0];
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

}
