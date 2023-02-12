package com.payment.totalPay;

import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Admin on 7/20/2020.
 */
public class TotalaPayUtils
{

    private static TransactionLogger transactionLogger = new TransactionLogger(TotalaPayUtils.class.getName());

    public static String doPostHTTPSURLConnectionClient(String strURL, String authorizationToken,String req) throws PZTechnicalViolationException
    {
        transactionLogger.error("TotalaPayUtils:: Inside doPostHTTPSURLConnectionClient() of TotalaPayUtils.....!!!"+strURL);
        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
            post.addRequestHeader("Authorization","Basic "+authorizationToken+"");
            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("TotalaPayUtils:: HttpException-----"+he);
        }
        catch (IOException io){
            transactionLogger.error("TotalaPayUtils:: IOException-----"+io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }


    public String doGetHTTPSURLConnectionClient(String strURL) throws PZTechnicalViolationException
    {
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();

        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(strURL);
        String result = "";
        try
        {
            method.setRequestHeader("Accept", "application/json");
            method.setRequestHeader("Cache-Control", "no-cache");

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
            PZExceptionHandler.raiseTechnicalViolationException("TotalaPayUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("TotalaPayUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
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


}
