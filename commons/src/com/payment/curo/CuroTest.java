package com.payment.curo;


import com.directi.pg.Functions;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Admin on 1/3/2020.
 */
public class CuroTest
{
//    Functions functions = new Functions();
    public static void main(String[] args)
    {
        sofortRequest();
        //creditCardRequest();
        //paySafeCardRequest();
    }
    public static void sofortRequest()
{
    try
    {
        String url="https://secure-staging.curopayments.net/rest/v1/curo/payment/sofortbanking/";

        String request="{" +
                "\"site_id\":\"23184\"," +
                "\"url_success\":\"https://staging.paymentz.com/transaction/Common3DFrontEndServlet?status=success\"," +
                "\"url_pending\":\"https://staging.paymentz.com/transaction/Common3DFrontEndServlet?status=pending\"," +
                "\"url_failure\":\"https://staging.paymentz.com/transaction/Common3DFrontEndServlet?status=failed\"," +
                "\"url_cancel\":\"https://staging.paymentz.com/transaction/Common3DFrontEndServlet?status=cancel\"," +
                "\"url_callback\":\"https://staging.paymentz.com/transaction/CommonBackEndServlet\"," +
                "\"reference\":\"6541\"," +
                "\"amount\":\"1.00\"," +
                "\"currency_id\":\"EUR\"," +
                "\"description\":\"vfgbh\"," +
                "\"ip\":\"198.25.145.1\"," +
                "\"country_id\":\"IN\"," +
                "\"language_id\":\"en\"," +
                "\"customer\":{" +
                "\"firstname\":\"test\"," +
                "\"initials\":\"\"," +
                "\"lastname\":\"test\"," +
                "\"gender\":\"M\"," +
                "\"dob\":\"10-10-1991\"," +
                "\"ssn\":\"41563\"," +
                "\"address\":\"malad\"," +
                "\"city\":\"mumbai\"," +
                "\"state\":\"MH\"," +
                "\"zipcode\":\"400100\"," +
                "\"country_id\":\"IN\"," +
                "\"phone\":\"8456214756\"," +
                "\"email\":\"test@gmail.com\"}," +
                "\"cartitem\":{" +
                "\"sku\":\"415\"," +
                "\"name\":\"Test\"," +
                "\"price\":\"1.00\"," +
                "\"vat\":\"10\"}" +
                "}";
                String hash=encodeBase64("15136:3EE.K2k$c744P$iI9Wu65r5NFt7KSu2XVJP$_8Mk7NmS30bF2w-RfKuIfZf5QEhM");

        String prefix = "TEST";
        String siteID = "23184";
        String amt  = "1.00";
        String ref  = /*"6541";*/"15136";
        String key  = "3EE.K2k$c744P$iI9Wu65r5NFt7KSu2XVJP$_8Mk7NmS30bF2w-RfKuIfZf5QEhM";

        //String hash = Functions.convertmd5(prefix + siteID + amt + ref + key);

        String response=doHttpPostConnection(url,request,hash);
        System.out.println("response---->"+response);
        System.out.println("hash----"+hash);
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
}
    public static void creditCardRequest()
{
    try
    {
        String url="https://secure-staging.curopayments.net/rest/v1/curo/payment/creditcard/";

        String request="{" +
                "\"site_id\":\"23184\"," +
                "\"url_success\":\"https://staging.paymentz.com/transaction/Common3DFrontEndServlet?status=success\"," +
                "\"url_pending\":\"https://staging.paymentz.com/transaction/Common3DFrontEndServlet?status=pending\"," +
                "\"url_failure\":\"https://staging.paymentz.com/transaction/Common3DFrontEndServlet?status=failed\"," +
                "\"url_cancel\":\"https://staging.paymentz.com/transaction/Common3DFrontEndServlet?status=cancel\"," +
                "\"url_callback\":\"https://staging.paymentz.com/transaction/CommonBackEndServlet\"," +
                "\"reference\":\"6541\"," +
                "\"card_holder\":\"test test\"," +
                "\"card_firstname\":\"test\"," +
                "\"card_lastname\":\"test\"," +
                "\"amount\":\"1.00\"," +
                "\"currency_id\":\"EUR\"," +
                "\"description\":\"vfgb51h\"," +
                "\"ip\":\"198.25.145.1\"," +
                "\"country_id\":\"IN\"," +
                "\"language_id\":\"en\"," +
                "\"customer\":{" +
                "\"firstname\":\"test\"," +
                "\"initials\":\"\"," +
                "\"lastname\":\"test\"," +
                "\"gender\":\"M\"," +
                "\"dob\":\"10-10-1991\"," +
                "\"ssn\":\"41563\"," +
                "\"address\":\"malad\"," +
                "\"city\":\"mumbai\"," +
                "\"state\":\"MH\"," +
                "\"zipcode\":\"400100\"," +
                "\"country_id\":\"IN\"," +
                "\"phone\":\"8456214756\"," +
                "\"email\":\"test@gmail.com\"}," +
                "\"cartitem\":{" +
                "\"sku\":\"415\"," +
                "\"name\":\"Test\"," +
                "\"price\":\"1.00\"," +
                "\"vat\":\"10\"}" +
                "}";
        String hash=encodeBase64("15136:3EE.K2k$c744P$iI9Wu65r5NFt7KSu2XVJP$_8Mk7NmS30bF2w-RfKuIfZf5QEhM");



        String response=doHttpPostConnection(url,request,hash);
        System.out.println("response---->"+response);
        System.out.println("hash----"+hash);
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
}
    public static void paySafeCardRequest()
    {
        try
        {
            String url="https://secure-staging.curopayments.net/rest/v1/curo/payment/paysafecard/";

            String request="{" +
                    "\"site_id\":\"23184\"," +
                    "\"url_success\":\"https://staging.paymentz.com/transaction/Common3DFrontEndServlet?status=success\"," +
                    "\"url_pending\":\"https://staging.paymentz.com/transaction/Common3DFrontEndServlet?status=pending\"," +
                    "\"url_failure\":\"https://staging.paymentz.com/transaction/Common3DFrontEndServlet?status=failed\"," +
                    "\"url_cancel\":\"https://staging.paymentz.com/transaction/Common3DFrontEndServlet?status=cancel\"," +
                    "\"url_callback\":\"https://staging.paymentz.com/transaction/CommonBackEndServlet\"," +
                    "\"reference\":\"6548541\"," +
                    "\"amount\":\"1.00\"," +
                    "\"currency_id\":\"EUR\"," +
                    "\"description\":\"vfgb51h\"," +
                    "\"ip\":\"198.25.145.1\"," +
                    "\"country_id\":\"IN\"," +
                    "\"language_id\":\"en\"," +
                    "\"customer\":{" +
                    "\"firstname\":\"test\"," +
                    "\"initials\":\"\"," +
                    "\"lastname\":\"test\"," +
                    "\"gender\":\"M\"," +
                    "\"dob\":\"10-10-1991\"," +
                    "\"ssn\":\"41563\"," +
                    "\"address\":\"malad\"," +
                    "\"city\":\"mumbai\"," +
                    "\"state\":\"MH\"," +
                    "\"zipcode\":\"400100\"," +
                    "\"country_id\":\"IN\"," +
                    "\"phone\":\"8456214756\"," +
                    "\"email\":\"test@gmail.com\"}," +
                    "\"cartitem\":{" +
                    "\"sku\":\"415\"," +
                    "\"name\":\"Test\"," +
                    "\"price\":\"1.00\"," +
                    "\"vat\":\"10\"}" +
                    "}";
            String hash=encodeBase64("15136:3EE.K2k$c744P$iI9Wu65r5NFt7KSu2XVJP$_8Mk7NmS30bF2w-RfKuIfZf5QEhM");



            String response=doHttpPostConnection(url,request,hash);
            System.out.println("response---->"+response);
            System.out.println("hash----"+hash);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
public static String encodeBase64(String value)
{
    return new String(Base64.encodeBase64(value.getBytes()));
}
    public static String doHttpPostConnection(String url,String request,String hash)
    {
        String result="";
        PostMethod post=new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Authorization", "Basic " + hash);
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response=new String(post.getResponseBody());
            result=response;
        }
        catch (HttpException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }


}
