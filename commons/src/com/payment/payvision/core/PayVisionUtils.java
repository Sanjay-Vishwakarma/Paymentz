package com.payment.payvision.core;

import com.directi.pg.TransactionLogger;
import com.google.gson.Gson;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.payvision.core.type.*;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;

/**
 * Created by Admin on 2/22/18.
 */
public class PayVisionUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(PayVisionUtils.class.getName());

    public static String doPostHTTPSURLConnectionClient(String strURL, String req) throws PZTechnicalViolationException
    {
        transactionLogger.error("strURL-->" + strURL);
        String result = "";
        try
        {
            HttpClient httpClient = new HttpClient();

            PostMethod post = new PostMethod(strURL);
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Cache-Control", "no-cache");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException--" , he);
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException--",io);
        }
        return result;
    }

    public static String toJson(final CheckEnrollment response) {
        return new Gson().toJson(response);
    }

    public static String toJson(final PaymentUsingIntegratedMPI response) {
        return new Gson().toJson(response);
    }

    public static String toJson(final com.payment.payvision.core.type.Payment response) {
        return new Gson().toJson(response);
    }

    public static String toJson(final AuthorizeUsingIntegratedMPI response) {
        return new Gson().toJson(response);
    }

    public static String toJson(final Authorize response) {
        return new Gson().toJson(response);
    }

    public static String toJson(final com.payment.payvision.core.type.Void response) {
        return new Gson().toJson(response);
    }

    public static String toJson(final Refund response) {
        return new Gson().toJson(response);
    }

    public static String toJson(final Capture response) {
        return new Gson().toJson(response);
    }

    public static String toJson(final Inquiry response) {
        return new Gson().toJson(response);
    }
}
