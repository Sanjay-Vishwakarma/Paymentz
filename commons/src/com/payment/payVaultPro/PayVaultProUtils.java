package com.payment.payVaultPro;

import com.directi.pg.TransactionLogger;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;

/**
 * Created by Jeet Gupta on 12/10/18.
 */
public class PayVaultProUtils
{
    static TransactionLogger transactionLogger= new TransactionLogger(PayVaultProUtils.class.getName());

    public static String doPostHttpUrlConnection(String url,String req)
    {
        transactionLogger.error("url-----"+url);
        PostMethod postMethod = new PostMethod(url);
        String result="";
        try
        {
            HttpClient httpClient = new HttpClient();
            postMethod.setRequestHeader("Content-Type", "application/json");
            postMethod.setRequestBody(req);
            httpClient.executeMethod(postMethod);
            result= new String(postMethod.getResponseBody());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException--->",io);
        }
        finally
        {
            postMethod.releaseConnection();
        }
        return result;
    }
}
