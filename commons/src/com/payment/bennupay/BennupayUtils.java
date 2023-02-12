package com.payment.bennupay;

import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by Admin on 4/21/2021.
 */
public class BennupayUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(BennupayUtils.class.getName());

    public static String doPostHTTPSURLConnectionClient(String strURL,String Auth,String API_Secret,String req) throws PZTechnicalViolationException
    {
        transactionLogger.error("strURL:::::"+ strURL);
        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
            post.setRequestBody(req.toString());
            post.addRequestHeader("Authorization", Auth + " " + API_Secret);
            post.addRequestHeader("Accept", "application/json");
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0 FirePHP/0.7.4");
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException Bennupay HttpClient connection:::::", he);
        }
        catch (IOException io){
            transactionLogger.error("IOException Bennupay HttpClient connection:::::", io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String requestHashSignature(String transactionid, String Privatekey) throws Exception
    {
        String sha = transactionid+Privatekey;

        StringBuffer hexString=new StringBuffer();
        try
        {
            MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
            byte[] hash=messageDigest.digest(sha.getBytes("UTF-8"));
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
            transactionLogger.error("NoSuchAlgorithmException Bennupay requestHashSignature connection:::::", e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException Bennupay requestHashSignature connection:::::", e);
        }
        return hexString.toString();
    }

    public static String getCentAmount(String amount)//For USD
    {
        BigDecimal bigDecimal = new BigDecimal(amount);
        bigDecimal = bigDecimal.multiply(new BigDecimal(100));

        Long newAmount = bigDecimal.longValue();
        return newAmount.toString();
    }

    public static String getJPYAmount(String amount)//for JPY
    {
        double amt = Double.parseDouble(amount);
        double roundOff = Math.round(amt);
        int value = (int) roundOff;
        amount = String.valueOf(value);
        return amount.toString();
    }

    public static String getKWDSupportedAmount(String amount)
    {
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2 = Double.valueOf(amount);
        dObj2 = dObj2 * 1000;
        String amt = d.format(dObj2);
        return amt;
    }

    public String generateAuthorizationIdentificationResponse(){
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder((100000 + rnd.nextInt(900000))+"");
        return sb.toString();
    }

    public String UniqueforRefund(){
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder((100000 + rnd.nextInt(900000))+"");
        return sb.toString();
    }



}
