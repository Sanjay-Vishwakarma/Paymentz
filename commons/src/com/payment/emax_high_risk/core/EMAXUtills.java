package com.payment.emax_high_risk.core;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpStatus;


import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 1/22/15
 * Time: 11:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class EMAXUtills
{
    public final static String charset = "UTF-8";
    private static Logger log = new Logger(EMAXUtills.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(EMAXUtills.class.getName());

    public static String doPostHTTPSURLConnectionClient(String strURL, String req,String encodedCredentials) throws PZTechnicalViolationException
    {
        String result = "";
        //BufferedReader in=null;
        //BufferedOutputStream out = null;
        //NameValuePair[] data = null;

        try
        {
            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();

            HttpClient httpClient = new HttpClient();
            PostMethod post = new PostMethod(strURL);
            post.setRequestBody(req);
            post.addRequestHeader("Authorization", "Basic " + encodedCredentials);

            post.addRequestHeader("content-type", "application/json");
            post.addRequestHeader("Accept", "application/json");
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            log.error("Response======" + response);
            result= response;

        }
        catch (HttpException he)
        {
            PZExceptionHandler.raiseTechnicalViolationException("EmaxUtils.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION,null,he.getMessage(),he.getCause());
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("EmaxUtils.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }

        if (result == null)
            return "";
        else
            return result;
    }

    public  static String doGetHTTPSURLConnectionClient(String url, String encodedCredentials) throws PZTechnicalViolationException
    {
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();

        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        String result = "";
        try
        {
            method.setRequestHeader("Accept", "application/json;q=1.0, application/xml;q=0.8");
            method.setRequestHeader("Authorization", "Basic "+encodedCredentials);
            //method.setRequestHeader("content-type", "application/json");
            //method.setRequestHeader("Accept", "application/json");

            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK)
            {
                System.err.println("Method failed: " + method.getStatusLine());
            }

            byte[] response = method.getResponseBody();
            //System.out.println("Response-----"+response);
            result = new String(response);

            method.releaseConnection();
        }
        catch (HttpException he)
        {
            PZExceptionHandler.raiseTechnicalViolationException("EmaxUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION,null,he.getMessage(),he.getCause());
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("EmaxUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }

        if (result == null)
            return "";
        else
            return result;
    }
}
