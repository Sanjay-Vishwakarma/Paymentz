package com.payment.quickpayments;

import com.directi.pg.Base64;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;

/**
 * Created by Admin on 4/26/2021.
 */
public class QuickPaymentsMain
{
    public static void main(String[] args)
    {
        sale();
    }
    public static void sale()
    {
        try
        {
            String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String payment_url = "https://btc.quickpaymentsgateway.com/preparePost";
            String clientId = "123456";
            String currency = "USD";
            String amount = "10000.10";
            String foundingSourceName = "BTC3";  //card
            String foundingSourceName1 = "BTC1";  //btc
            String foundingSourceName4 = "BTC4";  //wire
            String notes = "";
            String firstName = "James";
            String lastName = "Franco";
            String emailAddress = "test@test.com";
            String data = "{\"firstName\":" + firstName + ",\"lastName\":" + lastName + ",\"clientId\":" + clientId + ",\"currency\":" + currency + ",\"amount\":" + amount + "\"}";
         // String options = "{\"http\":\"\",\"method\":\"POST\",\"content\":" + data + ",\"header\":\"Authorization: Bearer <>\"}";
            String apiKey = "dFp6RdzF44xAaTPUmkG4WY88srKfKEe2";

         //https://btc.quickpaymentsgateway.com/BTC3/Start?mcTxId=a31edc1763bc4de888fe185fc9f2a8d8

            StringBuffer request = new StringBuffer();
            request.append("clientId=" + clientId + "&currency=" + currency + "&amount=" + amount + "&foundingSourceName=" + foundingSourceName + "&notes =" + notes+"&returnUrl="+returnUrl + "&firstName=" + firstName + "&lastName=" + lastName +
                    "&emailAddress=" + emailAddress);

            System.out.println("Sale request---->" + request);
            String response = doPostHTTPSURLConnectionClient(payment_url, request.toString(), apiKey);
            System.out.println("Sale response---->" + response);

        }
        catch (PZTechnicalViolationException e)
        {
            e.printStackTrace();
        }
    }
    public static String doPostHTTPSURLConnectionClient(String strURL,String req,String key) throws PZTechnicalViolationException
    {
        System.out.println ("strURL------------>"+ strURL);
        String result = "";

        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.addRequestHeader("Authorization"," Bearer "+key);
            post.setRequestBody(req);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            System.out.println("HttpException ---->"+he);
        }
        catch (IOException io){
            System.out.println("IOException ---->"+io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
}
