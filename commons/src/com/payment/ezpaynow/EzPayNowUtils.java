package com.payment.ezpaynow;

import com.directi.pg.TransactionLogger;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Diksha on 22-Jan-20.
 */
public class EzPayNowUtils
{
    private static TransactionLogger transactionLogger=new TransactionLogger(EzPayNowUtils.class.getName());
    public static String doHttpPostConnection(String url,String request)
    {

        String result = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException e)
        {
            transactionLogger.error("HttpException---->",e);
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException---->", e);
        }
        return result;

    }

    public static Map<String,String> getResponseVo(String response){
        Map<String,String> responseMap=new HashMap<>();
        String[] value={};
        if(response.contains("&")){
            String[] parameter=response.split("&");

            for (String param:parameter){
                if(param.contains("=")){
                    value=param.split("=");
                    if(value.length==2)
                        responseMap.put(value[0],value[1]);
                    else
                        responseMap.put(value[0],"");
                }
            }
        }
        return responseMap;

    }
}
