package com.payment.payvisionV2;

import com.directi.pg.TransactionLogger;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Created by Admin on 9/6/2019.
 */
public class PayvisionV2Utils
{
    private static TransactionLogger transactionLogger=new TransactionLogger(PayvisionV2Utils.class.getName());

    public static String doPostHttpConnection(String url,String request)
    {
        transactionLogger.error("Inside PayvisionV2Utils-----"+url);
        String result="";

        try
        {
            HttpClient httpClient=new HttpClient();
            PostMethod postMethod=new PostMethod(url);
            postMethod.addRequestHeader("Content-Type","application/json");
            postMethod.setRequestBody(request);
            httpClient.executeMethod(postMethod);
            result=new String(postMethod.getResponseBody());
            transactionLogger.error("Result----"+result);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception in PayvisionV2----",e);
        }
        return result;
    }
}
