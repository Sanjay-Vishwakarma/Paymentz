package com.payment.fenige;

import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by Admin on 8/28/2020.
 */
public class FenigeUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(FenigeUtils.class.getName());

    public static String doPostHTTPSURLConnectionClient(String strURL, String AUTH,String KEY,String req) throws PZTechnicalViolationException
    {
        transactionLogger.error("FenigeUtils:: Inside doPostHTTPSURLConnectionClient() of FenigeUtils.....!!!"+strURL);
        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
            post.addRequestHeader("Authorization", AUTH + " " +KEY);
            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("FenigeUtils:: HttpException-----"+he);
        }
        catch (IOException io){
            transactionLogger.error("FenigeUtils:: IOException-----"+io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public String doGetHTTPSURLConnectionClient(String strURL,String AUTH ,String KEY) throws PZTechnicalViolationException
    {
        transactionLogger.error("FenigeUtils:: Inside doGetHTTPSURLConnectionClient() of FenigeUtils.....!!!"+strURL);
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();

        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(strURL);
        String result = "";
        try
        {
            method.addRequestHeader("Authorization", AUTH + " " +KEY);

            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK)
            {
                transactionLogger.error("Method failed: " + method.getStatusLine());
            }

            byte[] response = method.getResponseBody();
            result = new String(response);

            method.releaseConnection();
        }
        catch (HttpException he)
        {
            PZExceptionHandler.raiseTechnicalViolationException("FenigeUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("FenigeUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            method.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }


    public static String getCentAmount(String amount)//For USD
    {
        BigDecimal bigDecimal = new BigDecimal(amount);
        bigDecimal = bigDecimal.multiply(new BigDecimal(100));

        Long newAmount = bigDecimal.longValue();
        return newAmount.toString();
    }

    public static String getJPYAmount(String amount)//for JPY
    {
        double amt = Double.parseDouble(amount);
        double roundOff = Math.round(amt);
        int value = (int) roundOff;
        amount = String.valueOf(value);
        return amount.toString();
    }

    public static String getKWDSupportedAmount(String amount)
    {
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2 = Double.valueOf(amount);
        dObj2 = dObj2 * 1000;
        String amt = d.format(dObj2);
        return amt;
    }
}
