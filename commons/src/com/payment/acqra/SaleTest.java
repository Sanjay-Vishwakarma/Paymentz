package com.payment.acqra;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Admin on 12/19/2019.
 */
public class SaleTest
{
    public static void main(String[] args) throws ParseException
    {
        //websiteRegisterTest();
        saleTest();
        //enquiryTest();
        //enquiryOrder_refTest();
        //refundTest();
        //refundEnquiryTest();
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyMMdd");
        SimpleDateFormat dateFormat2=new SimpleDateFormat("yyyy-MM-dd");
        String date="2019-12-20 12:43:12.0";

        System.out.println("date--->"+dateFormat.format(dateFormat2.parse(date)));


    }
    public static void saleTest()
    {
        String saleTestUrl="https://sandbox.acqra.com/v2/sales_payment";


        String mid="trans0404001test";
        String apikey="MjJQTCtiQUdFbUF6cGlBS0hUQ011V0xkNlhKbm9QVWhMYi9UMXVxTThKdz0=";
        String securitykey="boQlWBiUE8WfgFEPy8sycpJvKaHYr08U";
        String currency="USD";
        String amount="1.00";
        String order_ref="8444";
        String card_number="5111111111111111";
        String exp_month="12";
        String exp_year="2030";
        String security_code="123";
        String buyer_ip="192.168.24.2"; //customer IP
        String user_Agent="df";
        String accept_language="en";
        String hash=hashSHA256(mid+","+currency+","+order_ref+","+amount+","+securitykey);
        String lang="en";
        String card_holder_first_name="test";
        String card_holder_last_name="test";
        String bill_street_address="14/2 kari road";
        String bill_city="Mumbai";
        String bill_state="";
        String bill_zip="400895";
        String bill_country="IN";
        String email="test@gmail.com";
        String phone="9857845698";
        String website="shop.com";
        String referer="";


        StringBuffer request=new StringBuffer("");
        request.append("mid="+mid+"&apikey="+apikey+"&currency="+currency+"&amount="+amount+"&order_ref="+order_ref+"&card_number="+card_number+"&exp_month="+exp_month+"&exp_year="+exp_year
                +"&security_code="+security_code+"&buyer_ip="+buyer_ip+"&lang="+lang+"&card_holder_first_name="+card_holder_first_name+"&card_holder_last_name="+card_holder_last_name
                +"&bill_street_address="+bill_street_address+"&bill_city="+bill_city+"&bill_state="+bill_state+"&bill_zip="+bill_zip+"&bill_country="+bill_country+"&email="+email+"&phone="+phone
                +"&user_agent="+user_Agent+"&accept_language="+accept_language+"&hash="+hash+"&website="+website);
        System.out.println("saleTest request---->"+request);

        String response=doHttpPostConnection(saleTestUrl,request.toString());
        System.out.println("saleTest response----->"+response);
    }
    public static void websiteRegisterTest()
    {
        String saleTestUrl="https://sandbox.acqra.com/v2/register_website";


        String mid="trans0404001test";
        String apikey="MjJQTCtiQUdFbUF6cGlBS0hUQ011V0xkNlhKbm9QVWhMYi9UMXVxTThKdz0=";
        String securitykey="boQlWBiUE8WfgFEPy8sycpJvKaHYr08U";
        String website="carRental.com";
        String hash=hashSHA256(mid+","+website+","+securitykey);


        StringBuffer request=new StringBuffer("");
        request.append("mid="+mid+"&apikey="+apikey+"&website="+website+"&hash="+hash);
        System.out.println("websiteRegisterTest request---->"+request);

        String response=doHttpPostConnection(saleTestUrl,request.toString());
        System.out.println("websiteRegisterTest response----->"+response);
    }
    public static void enquiryTest()
    {
        String saleTestUrl="https://sandbox.acqra.com/v2/enquiry";


        String mid="trans0404001test";
        String apikey="MjJQTCtiQUdFbUF6cGlBS0hUQ011V0xkNlhKbm9QVWhMYi9UMXVxTThKdz0=";
        String securitykey="boQlWBiUE8WfgFEPy8sycpJvKaHYr08U";
        String transaction_id="19121918150639181998344601";
        String hash=hashSHA256(mid+","+transaction_id+","+securitykey);


        StringBuffer request=new StringBuffer("");
        request.append("mid="+mid+"&apikey="+apikey+"&website="+transaction_id+"&hash="+hash);
        System.out.println("enquiryTest request---->"+request);

        String response=doHttpPostConnection(saleTestUrl,request.toString());
        System.out.println("enquiryTest response----->"+response);
    }

    public static void enquiryOrder_refTest()
    {
        String saleTestUrl="https://sandbox.acqra.com/v2/enquiry_orderref";


        String mid="trans0404001test";
        String apikey="MjJQTCtiQUdFbUF6cGlBS0hUQ011V0xkNlhKbm9QVWhMYi9UMXVxTThKdz0=";
        String securitykey="boQlWBiUE8WfgFEPy8sycpJvKaHYr08U";
        String order_ref="2861911";
        String transaction_date="191219";
        String hash=hashSHA256(mid+","+order_ref+","+transaction_date+","+securitykey);


        StringBuffer request=new StringBuffer("");
        request.append("mid="+mid+"&apikey="+apikey+"&order_ref="+order_ref+"&transaction_date="+transaction_date+"&hash="+hash);
        System.out.println("enquiryOrder_refTest request---->"+request);

        String response=doHttpPostConnection(saleTestUrl,request.toString());
        System.out.println("enquiryOrder_refTest response----->"+response);
    }
    public static void refundEnquiryTest()
    {
        String saleTestUrl="https://sandbox.acqra.com/v2/refund_enquiry";


        String mid="trans0404001test";
        String apikey="MjJQTCtiQUdFbUF6cGlBS0hUQ011V0xkNlhKbm9QVWhMYi9UMXVxTThKdz0=";
        String securitykey="boQlWBiUE8WfgFEPy8sycpJvKaHYr08U";
        String settlement_ref="19121922152242304411805201-8";
        String hash=hashSHA256(mid+","+settlement_ref+","+securitykey);


        StringBuffer request=new StringBuffer("");
        request.append("mid="+mid+"&apikey="+apikey+"&settlement_ref="+settlement_ref+"&hash="+hash);
        System.out.println("refundEnquiryTest request---->"+request);

        String response=doHttpPostConnection(saleTestUrl,request.toString());
        System.out.println("refundEnquiryTest response----->"+response);
    }


    public static void refundTest()
    {
        String refundTestUrl="https://sandbox.acqra.com/v2/refund_payment";


        String mid="trans0404001test";
        String apikey="MjJQTCtiQUdFbUF6cGlBS0hUQ011V0xkNlhKbm9QVWhMYi9UMXVxTThKdz0=";
        String securitykey="boQlWBiUE8WfgFEPy8sycpJvKaHYr08U";
        String currency="USD";
        String amount="1.00";
        String transaction_id="19121919171509538338336101";
        String hash=hashSHA256(mid+","+currency+","+transaction_id+","+amount+","+securitykey);


        StringBuffer request=new StringBuffer("");
        request.append("mid="+mid+"&apikey="+apikey+"&transaction_id="+transaction_id+"&currency="+currency+"&amount="+amount+"&hash="+hash);
        System.out.println("refundTest request---->"+request);

        String response=doHttpPostConnection(refundTestUrl,request.toString());
        System.out.println("refundTest response----->"+response);
    }

    public static String hashSHA256(String plainText)
    {
        StringBuffer cipherText=new StringBuffer();
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(plainText.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) cipherText.append('0');
                cipherText.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return cipherText.toString();
    }
    public static String doHttpPostConnection(String url,String request)
    {
        String result="";
        PostMethod post=new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
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
