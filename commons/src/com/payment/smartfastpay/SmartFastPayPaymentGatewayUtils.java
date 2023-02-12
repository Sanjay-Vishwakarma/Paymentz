package com.payment.smartfastpay;

import com.directi.pg.*;
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
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;


/**
 * Created by Admin on 2022-01-22.
 */
public class SmartFastPayPaymentGatewayUtils
{

    private static TransactionLogger transactionLogger  = new TransactionLogger(SmartFastPayPaymentGatewayUtils.class.getName());
    static Functions functions                          = new Functions();
    private final static String charset                 = "UTF-8";

    public static String getPaymentBrand(String paymentMode)
    {
        String payBrand = "";
        if("166".equalsIgnoreCase(paymentMode))
            payBrand = "bank_transfer";
        if("169".equalsIgnoreCase(paymentMode))
            payBrand = "pix";
        if("167".equalsIgnoreCase(paymentMode))
            payBrand = "picpay";
        if("168".equalsIgnoreCase(paymentMode))
            payBrand = "boleto";
        return payBrand;
    }
    public static String getAmount(String amount)
    {
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2    = Double.valueOf(amount);
        dObj2           = dObj2 * 100;
        String amt      = d.format(dObj2);
        return amt;
    }

    public static void updateMainTableEntry(String trackingid, String transactionId)
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


    public static String doPostTokenHTTPSURLConnection(String strURL,String token) throws Exception
    {
        String result = "";

        HttpClient httpClient   = new HttpClient();
        PostMethod post         = new PostMethod(strURL);
        try
        {
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Authorization", "Basic " + token);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he){
            transactionLogger.error("doPostTokenHTTPSURLConnection IOException ---->"+he);
            PZExceptionHandler.raiseTechnicalViolationException("SmartFastPayPaymentGatewayUtils.java", "doGetPostHTTPUrlConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io){
            transactionLogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("SmartFastPayPaymentGatewayUtils.java", "doGetPostHTTPUrlConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String doPostHTTPSURLConnection(String REQUEST_URL,String requestBody,String token) throws Exception
    {
        String result = "";

        HttpClient httpClient   = new HttpClient();
        PostMethod post         = new PostMethod(REQUEST_URL);
        try
        {
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Authorization", "Bearer "+token);
            post.setRequestBody(requestBody);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he){
            transactionLogger.error("doPostTokenHTTPSURLConnection IOException ---->"+he);
            PZExceptionHandler.raiseTechnicalViolationException("SmartFastPayPaymentGatewayUtils.java", "doGetPostHTTPUrlConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io){
            transactionLogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("SmartFastPayPaymentGatewayUtils.java", "doGetPostHTTPUrlConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public  static String doGetHTTPSURLConnectionClient(String url,String token) throws PZTechnicalViolationException
    {
        //transactionLogger.debug("url--->" + url);
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
                //  transactionLogger.debug("Method failed: " + method.getStatusLine());
            }

            byte[] response = method.getResponseBody();
            result = new String(response);

            method.releaseConnection();
        }
        catch (HttpException he)
        {
            transactionLogger.debug("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("SmartFastPayPaymentGatewayUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.debug("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("SmartFastPayPaymentGatewayUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }

        if (result == null)
            return "";
        else
            return result;
    }





}
