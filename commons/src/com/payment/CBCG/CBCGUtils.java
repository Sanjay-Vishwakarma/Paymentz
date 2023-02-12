package com.payment.CBCG;

import com.directi.pg.TransactionLogger;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Admin on 8/10/2020.
 */
public class CBCGUtils
{
    private static TransactionLogger transactionLogger=new TransactionLogger(CBCGUtils.class.getName());
    public static String sha256Encoding(String value)
    {
        StringBuffer hexString=new StringBuffer();
        try
        {
            MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
            byte[] hash=messageDigest.digest(value.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException--->",e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException--->", e);
        }
        return hexString.toString();
    }
    public static String doHttpPostConnection(String url,String request)
    {
        String response="";
        PostMethod post=new PostMethod(url);
        try
        {
            HttpClient httpClient=new HttpClient();
            post.addRequestHeader("Content-Type","application/x-www-form-urlencoded");
            post.setRequestBody(request);
            httpClient.executeMethod(post);

            String result=new String(post.getResponseBody());
            response=result;
        }
        catch (HttpException e)
        {
            transactionLogger.error("HttpException--->", e);
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException--->", e);
        }
        finally
        {
            post.releaseConnection();
        }

        return response;
    }
    public static String doHttpGetConnection(String url)
    {
        String response="";
        GetMethod get=new GetMethod(url);
        try
        {
            HttpClient httpClient=new HttpClient();
            httpClient.executeMethod(get);

            String result=new String(get.getResponseBody());
            response=result;
        }
        catch (HttpException e)
        {
            transactionLogger.error("HttpException--->", e);
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException--->", e);
        }
        finally
        {
            get.releaseConnection();
        }

        return response;
    }
}
