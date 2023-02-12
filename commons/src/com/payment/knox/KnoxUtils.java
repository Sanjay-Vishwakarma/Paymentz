package com.payment.knox;

import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;

/**
 * Created by Admin on 8/4/2020.
 */
public class KnoxUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(KnoxUtils.class.getName());

    public static String doPostHTTPSURLConnectionClient(String strURL, String API_key,String API_Secret,String req) throws PZTechnicalViolationException
    {
        transactionLogger.error("KnoxUtils:: Inside doPostHTTPSURLConnectionClient() of KnoxUtils.....!!!"+strURL);
        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
            post.addRequestHeader("Authorization",API_key + ":" +API_Secret);
            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("KnoxUtils:: HttpException-----"+he);
        }
        catch (IOException io){
            transactionLogger.error("KnoxUtils:: IOException-----"+io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
}
