package practice;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;

public class Octapay_Main
{
    public static void main(String[] args)
    {
        sale();
    }
    public static void sale()
    {
        String url = "https://octapay.global/api/transaction";
        String api_key="";
        String first_name="abc";
        String last_name="def";
        String address="mumbai";
        String country="IN";
        String state="MH";
        String city="mumbai";
        String zip="400004";
        String ip_address="192.168.2.1";
        String birth_date="06/12/1990";
        String email="test100@paymentz.com";
        String phone_no="9898898998";
        String card_type="2";
        String amount="10.00";
        String currency="USD";
        String customer_order_id="";
        String card_no="4242424242424242";
        String ccExpiryMonth="02";
        String ccExpiryYear="2022";
        String cvvNumber="123";
        String response_url="http://localhost:8081/transaction/Common3DFrontEndServlet";
        String webhook_url="https://staging.paymentz.com/transaction/CommonBackEndServlet";

        StringBuffer request = new StringBuffer();
        request.append("api_key="+api_key+"&first_name="+first_name+"&last_name="+last_name+"&address="+address+"&customer_order_id="+customer_order_id+"&country="+country+"&state="+state+
                "&city="+city+"&zip="+zip+"&ip_address"+ip_address+"&birth_date="+birth_date+"&email="+email+"phone_no="+phone_no+"&card_type="+card_type+"&amount="+amount+
                "&currency="+currency+"&card_no="+card_no+"&ccExpiryMonth="+ccExpiryMonth+"&ccExpiryYear="+ccExpiryYear+"&cvvNumber="+cvvNumber+"&response_url="+response_url+
                "&webhook_url="+webhook_url);
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

