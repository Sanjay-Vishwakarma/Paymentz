package com.payment.octapay;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;

/**
 * Created by Admin on 12/4/2020.
 */
public class OctapayMain
{
    public static void main(String[] args)
    {
          sale();
      //  inquiry();
       // refund();
    }
    public static void sale()
    {
        String url ="https://octapay.global/api/transaction";
        String api_key="Ab2K5e0YLbprjHN2GGsqHSPQ4gFeVdHeHPla4RVN8IdgWW6gHOoUqt9sWlChnqYX";  //mrq63WHGUVcKRU7jrcL2xlesa0XEZuqQ86YexuKxf6JNp8HwJMKKrdd9bvWGBFYu
        String first_name="abc";
        String last_name="def";
        String address="mumbai";
        String country="IN";
        String state="MH";
        String city="mumbai";
        String zip="400004";
        String ip_address="1.186.206.210";
        String birth_date="06/12/1990";
        String email="test1@paymentz.com";
        String phone_no="9898898998";
        String card_type="2";
        double amount=10.00;
        String currency="USD";
        String customer_order_id="338268160811354784";
        String card_no="4242424242424242";
        String ccExpiryMonth="02";
        String ccExpiryYear="2022";
        String cvvNumber="123";
        String response_url="http://localhost:8081/transaction/Common3DFrontEndServlet";
        String webhook_url="https://staging.paymentz.com/transaction/CommonBackEndServlet";

        StringBuffer request = new StringBuffer();
        request.append("api_key="+api_key+"&first_name="+first_name+"&last_name="+last_name+"&address="+address+"&customer_order_id="+customer_order_id+"&country="+country+"&state="+state+
                       "&city="+city+"&zip="+zip+"&ip_address="+ip_address+"&birth_date="+birth_date+"&email="+email+"&phone_no="+phone_no+"&card_type="+Integer.parseInt(card_type)+"&amount="+amount+
                        "&currency="+currency+"&card_no="+card_no+"&ccExpiryMonth="+ccExpiryMonth+"&ccExpiryYear="+ccExpiryYear+"&cvvNumber="+cvvNumber+"&response_url="+response_url+
                        "&webhook_url="+webhook_url);
        System.out.println("Request-->" + request);
        String response = doHttpPostConnection(url, request.toString());
        System.out.println("response-->" + response);

    }

    public static void HostedPageAPI()
    {
        String url ="https://octapay.global/api/hosted-pay/payment-request";
        String api_key="Ab2K5e0YLbprjHN2GGsqHSPQ4gFeVdHeHPla4RVN8IdgWW6gHOoUqt9sWlChnqYX";
        String first_name="abc";
        String last_name="def";
        String address="mumbai";
        String country="IN";
        String state="MH";
        String city="mumbai";
        String zip="400004";
        String ip_address="1.186.206.210";
        String birth_date="06/12/1990";
        String email="test100@paymentz.com";
        String phone_no="9898898998";
        int card_type=2;
        double amount=10.00;
        String currency="USD";
        String customer_order_id="338268160811354784";
        String response_url="http://localhost:8081/transaction/Common3DFrontEndServlet";
        String webhook_url="https://staging.paymentz.com/transaction/CommonBackEndServlet";

        StringBuffer request = new StringBuffer();
        request.append("api_key="+api_key+"&first_name="+first_name+"&last_name="+last_name+"&address="+address+"&customer_order_id="+customer_order_id+"&country="+country+"&state="+state+
                "&city="+city+"&zip="+zip+"&ip_address="+ip_address+"&birth_date="+birth_date+"&email="+email+"&phone_no="+phone_no+"&amount="+amount+
                "&currency="+currency+"&response_url="+response_url+
                "&webhook_url="+webhook_url);
        System.out.println("Request-->" + request);

        String response = doHttpPostConnection(url, request.toString());
        System.out.println("response-->" + response);




    }
    public static void inquiry()
    {
        String url ="https://octapay.global/api/get/transaction";
        String api_key="";
        String order_id="2019159410836467";
        String customer_order_id="338268160811354784"; //Atleast one parameter from the above two parameters is required.
        StringBuffer request = new StringBuffer();
        request.append("api_key="+api_key+"&order_id="+order_id+"&customer_order_id="+customer_order_id);
        System.out.println("Request-->" + request);

        String response = doHttpPostConnection(url, request.toString());
        System.out.println("response-->" + response);
    }
    public static void refund()
    {
        String url ="https://octapay.global/api/refund/transaction";
        String api_key="";
        String order_id="2019159410836467";
        String customer_order_id="338268160811354784"; //Atleast one parameter from the above two parameters is required.
        String refund_reason="Refund reason";
        StringBuffer request = new StringBuffer();
        request.append("api_key="+api_key+"&refund_reason="+refund_reason+"&order_id="+order_id+"&customer_order_id="+customer_order_id);
        System.out.println("Request-->" + request);

        String response = doHttpPostConnection(url, request.toString());
        System.out.println("response-->" + response);
    }

    public static String doHttpPostConnection(String url, String request)
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
            System.out.println("url-->" + url);
        }
        catch (HttpException e)
        {
            System.out.println("HttpException --->" + e);
        }
        catch (IOException e)
        {
            System.out.println("IOException --->" + e);
        }
        return result;
    }

}
