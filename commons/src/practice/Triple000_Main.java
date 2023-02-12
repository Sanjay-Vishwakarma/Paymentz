package practice;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;

/**
 * Created by Admin on 10/12/2020.
 */
public class Triple000_Main
{
    public static void main(String[] args)
    {
   sale_Non3D();
//       sale_3D();
      //payout();
       // Inquiry();
       // Refund();
    }
    public static void sale_Non3D()
    {
        String url="https://gwapi.triple000.com/GWTransaction/Payment";
        String type="Payment";
        String id="fidostg8403";
        String key="mx3PmIpyRSe8";
        String operator_id="156d1f5556";
        String ext_id="876152";// this should be unique everytime
        String currency="USD";
        String tran_amount="100";
        String card_type="1";
        String cc_number="4200000000000000";
        String cc_expiry="09/22";
        String cvv="123";
        String ip_address="192.10.8.0";
        String cust_fname="abc";
        String cust_lname="pqr";
        String birthdate="1998-10-06";
        String ssn="111";
        String address1="abcdfghijklmnop";
        String address2="fgfdgfrgtgtgt";
        String city="Mumbai";
        String state="MH";
        String country_code="IN";
        String zip_code="400004";
        String email="abc908@gmail.com";
        String phone_no="9865321470";
        //String notification_url="http://localhost:8081/transaction/Common3DFrontEndServlet";
        String server_url="http://localhost:8081/transaction/Common3DFrontEndServlet";
        //String browser_url="https://staging.paymentz.com/transaction/CommonBackEndServlet";
        String mid="156d1f5556";

        StringBuffer request = new StringBuffer();
        request.append("type=" + type + "&id="+ id + "&key="+ key + "&operator_id=" + operator_id + "&ext_id=" + ext_id + "&currency=" + currency +
                       "&tran_amount=" +tran_amount + "&card_type=" + card_type + "&cc_number=" + cc_number + "&cc_expiry=" + cc_expiry + "&cvv=" + cvv +
                       "&ip_address=" + ip_address + "&cust_fname=" + cust_fname + "&cust_lname=" + cust_lname + "&birthdate=" + birthdate + "&ssn=" + ssn +
                       "&address1=" + address1 + "&address2=" + address2 + "&city=" + city + "&state=" + state + "&country_code=" + country_code +
                       "&zip_code=" + zip_code + "&email=" + email + "&phone_no=" + phone_no +"&server_url=" + server_url  /*"&browser_url=" + browser_url*/);


        System.out.println("Request-->" + request);
        String response = doHttpPostConnection(url, request.toString());
        System.out.println("response-->" + response);
    }

    public static void sale_3D()
    {
        String url="https://gwapi.triple000.com/GWTransaction/Payment";
        String type="Payment";
        String id="fidostg8403";
        String key="mx3PmIpyRSe8";
        String operator_id="156d1f5556";

        String ext_id="119601";// this should be unique everytime
        String currency="USD";
        String tran_amount="100";
        String card_type="1";
        String cc_number="4242424242424242";
        String cc_expiry="09/22";
        String cvv="123";
        String ip_address="192.10.8.0";
        String cust_fname="abc";
        String cust_lname="pqr";
        String birthdate="1998-10-06";
        String ssn="111";
        String address1="abcdfghijklmnop";
        String address2="fgfdgfrgtgtgt";
        String city="Mumbai";
        String state="MH";
        String country_code="IN";
        String zip_code="400004";
        String email="abc908@gmail.com";
        String phone_no="9865321470";
        String server_url="http://localhost:8081/transaction/Common3DFrontEndServlet";
        String browser_url="https://staging.paymentz.com/transaction/CommonBackEndServlet";


        StringBuffer request = new StringBuffer();
        request.append("type=" + type + "&id="+ id + "&key="+ key + "&operator_id=" + operator_id + "&ext_id=" + ext_id + "&currency=" + currency +
                "&tran_amount=" +tran_amount + "&card_type=" + card_type + "&cc_number=" + cc_number + "&cc_expiry=" + cc_expiry + "&cvv=" + cvv +
                "&ip_address=" + ip_address + "&cust_fname=" + cust_fname + "&cust_lname=" + cust_lname + "&birthdate=" + birthdate + "&ssn=" + ssn +
                "&address1=" + address1 + "&address2=" + address2 + "&city=" + city + "&state=" + state + "&country_code=" + country_code +
                "&zip_code=" + zip_code + "&email=" + email + "&phone_no=" + phone_no + "&server_url=" + server_url + "&browser_url=" + browser_url);


        System.out.println("Request-->" + request);
        String response = doHttpPostConnection(url,request.toString());
        System.out.println("response-->" + response);
    }
    public static void payout()
    {
        String url="https://gwapi.triple000.com/GWTransaction/Payment";
        String type="Payout";
        String id="fidostg8403";
        String key="mx3PmIpyRSe8";
        String operator_id="156d1f5556";
        String ext_id="828913";// this should be unique everytime
        String currency="USD";
        String tran_amount="300";
        String card_type="1";
        String cc_number="4242424242424242";
        String cc_expiry="09/22";
        String payout_type="1";
        String ip_address="192.10.8.0";
        String cust_fname="abc";
        String cust_lname="pqr";
        String birthdate="1998-10-06";
        String ssn="111";
        String address1="abcdfghijklmnop";
        String address2="fgfdgfrgtgtgt";
        String city="Mumbai";
        String state="MH";
        String country_code="IN";
        String zip_code="400004";
        String email="abc908@gmail.com";
        String phone_no="9865321470";
        String server_url="http://localhost:8081/transaction/Common3DFrontEndServlet";
        String browser_url="https://staging.paymentz.com/transaction/CommonBackEndServlet";
        String mid="156d1f5556";

        StringBuffer request = new StringBuffer();
        request.append("type=" + type + "&id="+ id + "&key="+ key + "&payout_type=" + payout_type + "&operator_id=" + operator_id + "&ext_id=" + ext_id + "&currency=" + currency +
                "&tran_amount=" +tran_amount + "&card_type=" + card_type + "&cc_number=" + cc_number + "&cc_expiry=" + cc_expiry +
                "&ip_address=" + ip_address + "&cust_fname=" + cust_fname + "&cust_lname=" + cust_lname + "&birthdate=" + birthdate + "&ssn=" + ssn +
                "&address1=" + address1 + "&address2=" + address2 + "&city=" + city + "&state=" + state + "&country_code=" + country_code +
                "&zip_code=" + zip_code + "&email=" + email + "&phone_no=" + phone_no + "&server_url=" + server_url + "&browser_url=" + browser_url);


        System.out.println("Request-->" + request);
        String response = doHttpPostConnection(url,request.toString());
        System.out.println("response-->" + response);
    }

    public static void Inquiry()
    {
        String url="https://gwapi.triple000.com/GWTransaction/Payment";
        String type="CheckStatus";
        String id="fidostg8403";
        String key="mx3PmIpyRSe8";
        String operator_id="156d1f5556";
        String ext_id="181664";// this should be unique everytime
        String tran_id="2724745730025057508";
        String server_url="http://localhost:8081/transaction/Common3DFrontEndServlet";
        String browser_url="https://staging.paymentz.com/transaction/CommonBackEndServlet";

        StringBuffer request = new StringBuffer();
        request.append("type=" + type + "&id="+ id + "&key="+ key +  "&operator_id=" + operator_id + "&ext_id=" + ext_id +"&tran_id=" +tran_id + "&server_url=" + server_url + "&browser_url=" + browser_url);


        System.out.println("Request-->" + request);
        String response = doHttpPostConnection(url,request.toString());
        System.out.println("response-->" + response);
    }
    public static void Refund()
    {
        String url="https://gwapi.triple000.com/GWTransaction/Payment";
        String type="Refund";
        String id="fidostg8403";
        String key="mx3PmIpyRSe8";
        String operator_id="156d1f5556";
        String ext_id="181726";// this should be unique everytime
        String tran_id="2725514200036740110";
        String server_url="http://localhost:8081/transaction/Common3DFrontEndServlet";
        String browser_url="https://staging.paymentz.com/transaction/CommonBackEndServlet";

        StringBuffer request = new StringBuffer();
        request.append("type=" + type + "&id="+ id + "&key="+ key +  "&operator_id=" + operator_id + "&ext_id=" + ext_id +"&tran_id=" +tran_id + "&server_url=" + server_url + "&browser_url=" + browser_url);


        System.out.println("Request-->" + request);
        String response = doHttpPostConnection(url,request.toString());
        System.out.println("response-->" + response);
    }
    public static String doPostHTTPSURLConnectionClient(String request)
    {
        String result = "";
        PostMethod post = new PostMethod(request);
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
        finally
        {
            post.releaseConnection();
        }

        return result;
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

