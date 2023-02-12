package practice;

import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by Admin on 10/22/2020.
 */
public class All_Methods
{
    public static String sha512(String request)// for UTF-8
    {
        String sha = request;
        sha.trim();
        String hexString ="";
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(sha.getBytes("UTF-8"));
            hexString= com.directi.pg.Base64.encode(hash);
        }
        catch (NoSuchAlgorithmException e)
        {
            System.out.println("NoSuchAlgorithmException e-->"+e);
        }
        catch (UnsupportedEncodingException e)
        {
            System.out.println("UnsupportedEncodingException e-->"+ e);
        }
        return hexString.toString().trim();
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
            System.out.println("HttpException--->"+ e);
        }
        catch (IOException e)
        {
            System.out.println("IOException--->"+ e);
        }
        finally
        {
            get.releaseConnection();
        }

        return response;
    }

    public static String doPostHTTPSURLConnectionClient(String req,String strURL , String authType, String encodedCredentials) throws PZTechnicalViolationException
    {
        URL url = null;
        try
        {
            url = new URL(strURL);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        System.setProperty("https.protocols", "TLSv1.2");
        System.setProperty("http.maxRedirects", "2");
        try
        {
            post.addRequestHeader("Authorization", authType + " " + encodedCredentials);
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he)
        {
            System.out.println("HttpException in CyberSourceUtils---"+he);
            System.out.println("HttpException in CyberSourceUtils---"+he);
        }
        catch (IOException io)
        {
            System.out.println("IOException in CyberSourceUtils---"+io);
            System.out.println("IOException in CyberSourceUtils---"+io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public Map createMapFromResponse(String queryString)
    {
        Map map = new HashMap();
        StringTokenizer st = new StringTokenizer(queryString, "&");
        while (st.hasMoreTokens())
        {
            String token = st.nextToken();
            int i = token.indexOf('=');
            if (i > 0)
            {
                try
                {
                    String key = token.substring(0, i);
                    String value = URLDecoder.decode(token.substring(i + 1, token.length()));
                    map.put(key, value);
                }
                catch (Exception ex)
                {
                    //do nothing and keep looping through data
                }
            }
        }
        return map;
    }

    public  static String doGetHTTPSURLConnectionClient(String url, String authType, String encodedCredentials) throws PZTechnicalViolationException
    {

        System.out.println("url--->" + url);

        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();

        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        String result = "";
        try
        {
            method.setRequestHeader("Accept", "application/json");
            method.setRequestHeader("Authorization", authType+ " " +encodedCredentials);
            method.setRequestHeader("Cache-Control", "no-cache");

            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK)
            {
                System.out.println("Method failed: " + method.getStatusLine());

            }

            byte[] response = method.getResponseBody();

            System.out.println("Response-----" + response.toString());

            result = new String(response);

            method.releaseConnection();
        }
        catch (HttpException he)
        {
            System.out.println("HttpException--->" + he);

            PZExceptionHandler.raiseTechnicalViolationException("NetellerUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            System.out.println("IOException----"+io);

            PZExceptionHandler.raiseTechnicalViolationException("NetellerUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(),io.getCause());
        }

        if (result == null)
            return "";
        else
            return result;
    }




}
