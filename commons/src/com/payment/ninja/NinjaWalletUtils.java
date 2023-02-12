package com.payment.ninja;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;

/**
 * Created by Admin on 5/29/2019.
 */
public class NinjaWalletUtils
{
   private static Logger logger = new Logger(NinjaWalletUtils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(NinjaWalletUtils.class.getName());

    public static String doPostHTTPSURLConnectionClient(String strURL, String authorizationToken,String req) throws PZTechnicalViolationException
    {
        logger.error("Inside NinjaWalletUtils.....!!!"+strURL);
        transactionLogger.error("Inside NinjaWalletUtils.....!!!"+strURL);
        String result = "";
        try
        {
            HttpClient httpClient = new HttpClient();
            PostMethod post = new PostMethod(strURL);
            post.addRequestHeader("Authorization","Bearer "+authorizationToken+"");
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException-----"+he);
        }
        catch (IOException io){
            transactionLogger.error("IOException-----"+io);
        }
        return result;
    }
}
