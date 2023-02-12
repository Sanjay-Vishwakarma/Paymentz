package com.payment.giftpay;

import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.payonOppwa.PayonOppwaUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Admin on 4/2/2021.
 */
public class Giftpay_main
{
    public static void main(String[] args)
    {
      sale();
   //sale_2();

    }
    public static void sale()
    {
        String url ="https://live.giftpay.app/api/v1/payment";
        String description="description1";
        float paymentAmount=15.00f;
        String returnUrl="http://localhost:8081/transaction/Common3DFrontEndServlet";
        String currency="gbp";
       // String authorizationToken="72e903be-3d23-4c4f-9165-b14ee13ed7ca:22f8d0e0-283d-4535-b6a5-0c68587b8740";


        try
        {
            String saleresponse="";
            StringBuffer salerequest=new StringBuffer();
            salerequest.append("{" + "\"description\":\""+description+"\","+
                            "\"paymentAmount\":\""+paymentAmount+"\","+
                            "\"returnUrl\":\""+returnUrl+"\","+
                            "\"currency\":\""+currency+"\"" +
                            "}"
            );


            System.out.println("Salerequest-------" + salerequest);
             saleresponse=doPostHTTPSURLConnectionClient(url, salerequest.toString());
            System.out.println("Saleresponse-------" + saleresponse);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static String doPostHTTPSURLConnectionClient(String strURL,String req) throws PZTechnicalViolationException
    {
        System.out.println("Inside doPostHTTPSURLConnectionClient()");
        String result = "";
       String apikey="72e903be-3d23-4c4f-9165-b14ee13ed7ca";
       String secretkey="22f8d0e0-283d-4535-b6a5-0c68587b8740";

        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
            post.addRequestHeader("Accept","application/json");
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Authentication",apikey+":"+secretkey);
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            System.out.println("HttpException-----" + he);
        }
        catch (IOException io){
            System.out.println("IOException-----" + io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    //Validating a Payment Request

    public static void sale_2()
    {
        String url ="https://live.giftpay.app/api/v1/payment/validate";
        String authorizationToken="72e903be-3d23-4c4f-9165-b14ee13ed7ca:22f8d0e0-283d-4535-b6a5-0c68587b8740";


        //  String authorizationToken="22f8d0e0-283d-4535-b6a5-0c68587b8740";

        try
        {
            String paymentId="957a9f67-750b-4908-b526-6d52cd3214ef";
            String saleresponse_2="";
            StringBuffer salerequest_2=new StringBuffer();
            salerequest_2.append("{" + "\"paymentId\":\""+paymentId+"\"" + "}");


            System.out.println("Salerequest__2-------" + salerequest_2);
            saleresponse_2=doPostHTTPSURLConnectionClient(url, salerequest_2.toString());
            System.out.println("Saleresponse___2-------" + saleresponse_2);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
