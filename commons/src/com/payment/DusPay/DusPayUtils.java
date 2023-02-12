package com.payment.DusPay;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jitendra on 06-Dec-18.
 */
public class DusPayUtils
{
    private final static String charset = "UTF-8";
    private static Logger log= new Logger(DusPayUtils.class.getName());
    private static TransactionLogger transactionLogger=new TransactionLogger(DusPayUtils.class.getName());

    public static String doPostHTTPSURLConnectionClient(String req,String strURL) throws PZTechnicalViolationException
    {
        transactionLogger.error("uRL----"+strURL);
        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        //System.setProperty("https.protocols", "TLSv1.2");
        try
        {
            System.setProperty("https.protocols", "TLSv1.2");
            //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            //java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
           // post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he)
        {
           log.error("---HttpException in DusPayUtils---",he);
           transactionLogger.error("---HttpException---",he);
        }
        catch (IOException io)
        {
            log.error("---IOException in DusPayUtils---",io);
            transactionLogger.error("---IOException---",io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String doGetHttpConnection(String url)
    {
        String response="";
        HttpClient httpClient= new HttpClient();
        try
        {
            GetMethod getMethod= new GetMethod(url);
            httpClient.executeMethod(getMethod);
            response=new String (getMethod.getResponseBody());
        }
        catch (IOException e)
        {
            transactionLogger.error("---Exception---", e);
        }
        return response;
    }
}
