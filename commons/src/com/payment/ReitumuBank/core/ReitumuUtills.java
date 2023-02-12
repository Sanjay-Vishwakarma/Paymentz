package com.payment.ReitumuBank.core;

import com.directi.pg.Logger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 2/13/15
 * Time: 9:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReitumuUtills
{
    private static Logger log = new Logger(ReitumuUtills.class.getName());

    public static String doPostHTTPSURLConnection(String strURL, Map<String,String> map) throws PZTechnicalViolationException
    {
        String result = "";
        BufferedReader in=null;
        BufferedOutputStream out = null;
        NameValuePair[] data = null;

        try
        {
            //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            //java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();

            data = getNamedValuePairArray(map);
            log.error("Request Data ReitumuBank---"+data+"---POST Url---"+strURL);
            HttpClient httpClient = new HttpClient();
            PostMethod post = new PostMethod(strURL);
            post.setRequestBody(data);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            log.error("Response from Reitumu---" + response);
            result= response;

        }
        catch (HttpException he)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PayWorldUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION,null, he.getMessage(), he.getCause());

        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("ReitumuUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }
    private static NameValuePair[] getNamedValuePairArray(Map<String,String> mapInternal)
    {
        NameValuePair result[] = new NameValuePair[mapInternal.size()];
        int counter = 0;
        for (Iterator iterator = mapInternal.keySet().iterator(); iterator.hasNext();)
        {
            String key = (String) iterator.next();
            result[counter++] = new NameValuePair(key, (String) mapInternal.get(key));
        }
        return result;
    }

    public static String convertSha1(String password) throws NoSuchAlgorithmException
    {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(password.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
