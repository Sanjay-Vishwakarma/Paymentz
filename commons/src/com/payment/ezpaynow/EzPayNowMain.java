package com.payment.ezpaynow;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.security.MessageDigest;

class EzPayNowMain
{
    public static void main(String[] args)
    {
        sale();
//        refund();
        //refund_void();
//        authorize();
        //refund_settle();


    }

    public static void sale()
    {

        String affiliate="EZPAYTE";
        String paymethod="Check";
        String post_method="sync";
        String processing_mode="sale";
        String redirect="http://localhost:8081/transaction/Common3DFrontEndServlet";
        //String notification_url="";
        String order_id="29958756666";
        String terminal_name="EZPAYTEtm1";
        //String customer_id="";
        String first_name="dev";
        String last_name="pandey";
        String address1="andheri";
        //String address2="";
        String city="mumbai";
        String state="MH";
        String country="EC";
        String zip="4444444";
        String telephone="8888888888";
        // String shipping_address1="";
        String amount="5.00";
        String currency="USD";
        String email="sagar@paymentz.com";
        String card_type="Visa";
        String card_number="4111111111111111";
        String cvv="123";
        String expiry_mo="12";
        String expiry_yr="2024";
        String check_number="x123456789";
        String routing_number="075000022";
        String account_number="";
        String bank_name="SBI";
        String bank_phone="8999999999";

        StringBuffer request = new StringBuffer("");
        request.append("affiliate=" + affiliate + "&paymethod=" + paymethod+"&post_method"+post_method
                + "&processing_mode=" + processing_mode + "&redirect=" + redirect + "&order_id=" + order_id
                + "&terminal_name=" + terminal_name +"&first_name="+first_name+"&last_name=" + last_name+"&address1="+address1+"&city="+city+
                "&state="+state+"&country="+country+"&zip="+zip+"&telephone="+telephone+"&amount="+amount+
                "&currency="+currency+"&email="+email+"&card_type="+card_type+"&card_number="+card_number+
                "&cvv="+cvv+"&expiry_mo="+expiry_mo+"&expiry_yr="+expiry_yr+"&check_number="+check_number+
                "&routing_number="+routing_number+"&account_number="+account_number+"&bank_name="+bank_name+"&bank_phone="+bank_phone);



        System.out.println("request----->>"+request);
        String saleUrl="https://backoffice.ezpaynow.biz/api/transact.php";//bank testing api url
        String response=doHttpPostConnection(saleUrl, request.toString());
        System.out.println("response---->>"+response);

    }


    public static void authorize()
    {
        String affiliate = "EZPAYTE";
        String paymethod = "Check";
        String post_method = "sync";
        String processing_mode = "authorize";
        String redirect = "http://localhost:8081/transaction/Common3DFrontEndServlet";
        //String notification_url = "http://backoffice.ezpaynow.biz/api/notification.php";
        String order_id = "29958756666";
        String terminal_name = "EZPAYTEtm1";
        //String customer_id = "a123";
        String first_name = "John";
        String last_name = "Doe";
        String address1 = "1 main st.";
        String address2 = "pqr";
        String city = "miami";
        String state = "FL";
        String country = "US";
        String zip = "12345";
        String telephone = "9988877888";
        String shipping_address1 = "malad";
        String shipping_address2 = "mumbai";
        String shipping_city = "mumbai";
        String shipping_state = "maharashtra";
        String shipping_country = "mumbai";
        String shipping_zip = "400004";
        String amount = "10";
        String currency = "USD";
        String email = "johndoe@yahoo.com";
        String card_type = "visa";
        String card_number = "4111111111111111";
        String cvv = "123";
        String expiry_mo = "01";
        String expiry_yr = "2020";
        String check_number = "x123456789";
        String routing_number = "122105155";
      //  String account_number = "x123456789";
        String bank_name = "BOI";
        String bank_phone = "8787201478";
        String customer_ip = "116.73.224.188";
        String product_id = "101";
        String product_description = "abc";

        String url = "https://backoffice.ezpaynow.biz/api/transact.php";


        StringBuffer request = new StringBuffer();
        request.append("affiliate=" + affiliate + "&paymethod=" + paymethod + "&processing_mode=" + processing_mode + "&redirect=" + redirect + "&amount=" + amount +"&order_id=" + order_id + "&terminal_name=" + terminal_name + "&first_name=" + first_name
                + "&last_name=" + last_name + "&address1=" + address1 + "&city=" + city + "&state=" + state + "&country=" + country
                + "&zip=" + zip + "&telephone=" + telephone + "&amount=" + amount + "&currency=" + currency + "&email=" + email + "&email=" + email + "&card_type=" + card_type
                + "&card_number=" + card_number + "&cvv=" + cvv + "&expiry_mo=" + expiry_mo + "&expiry_yr=" + expiry_yr + "&check_number=" + check_number + "&routing_number=" + routing_number + "&bank_name=" + bank_name + "&bank_phone=" + bank_phone);

        System.out.println("request-------" + request);
        String response = doHttpPostConnection(url, request.toString());
        System.out.println("response---------" + response);
    }
    public static void refund()
    {
        String affiliate="EZPAYTE";
        String paymethod="CreditCard";
        String processing_mode="refund";
        String redirect="http://localhost:8081/transaction/Common3DFrontEndServlet";

        String terminal_name="EZPAYTEtm1";
        String amount="3.00";
        String currency="USD";
        String reference_transaction_no="EZPAYTE 5E283E12763BA";
        //String transaction_memo="";
        //String product_description="";
        String url = "https://backoffice.ezpaynow.biz/api/transact.php";

        try{
            StringBuffer request2 = new StringBuffer();
            request2.append("affiliate=" + affiliate + "&paymethod=" + paymethod + "&processing_mode=" + processing_mode + "&redirect=" + redirect + "&terminal_name=" + terminal_name +"&amount="+amount+"&currency=" + currency+ "&reference_transaction_no=" + reference_transaction_no) ;
            System.out.println("Request2-------" + request2);
            String response2 = doHttpPostConnection(url, request2.toString());
            System.out.println("Response2--------" + response2);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void refund_void()
    {
        String affiliate="";
        String paymethod="";
        String processing_mode="";
        String redirect="";
        String terminal_name="";
        String amount="";
        String currency="";
        String reference_transaction_no="";
        String transaction_memo="";
        String product_description="";
        String url = "https://backoffice.ezpaynow.biz/api/transact.php";

        try{
            StringBuffer request2 = new StringBuffer();
            request2.append("affiliate=" + affiliate + "&paymethod=" + paymethod + "&processing_mode=" + processing_mode + "&redirect=" + redirect + "&terminal_name=" + terminal_name +"&amount="+amount+"&currency=" + currency+ "&reference_transaction_no=" + reference_transaction_no + "&transaction_memo=" + transaction_memo+ "&product_description=" + product_description) ;
            System.out.println("Request2-------" + request2);
            String response2 = doHttpPostConnection(url, request2.toString());
            System.out.println("Response2--------" + response2);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void refund_settle()
    {
        String affiliate="";
        String paymethod="";
        String processing_mode="";
        String redirect="";
        String terminal_name="";
        String amount="";
        String currency="";
        String reference_transaction_no="";
        String transaction_memo="";
        String product_description="";
        String url = "https://backoffice.ezpaynow.biz/api/transact.php";

        try{
            StringBuffer request2 = new StringBuffer();
            request2.append("affiliate=" + affiliate + "&paymethod=" + paymethod + "&processing_mode=" + processing_mode + "&redirect=" + redirect + "&terminal_name=" + terminal_name +"&amount="+amount+"&currency=" + currency+ "&reference_transaction_no=" + reference_transaction_no + "&transaction_memo=" + transaction_memo+ "&product_description=" + product_description) ;
            System.out.println("Request2-------" + request2);
            String response2 = doHttpPostConnection(url, request2.toString());
            System.out.println("Response2--------" + response2);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    private static String getString(byte buf[])
    {
        StringBuffer sb = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++)
        {
            int h = (buf[i] & 0xf0) >> 4;
            int l = (buf[i] & 0x0f);
            sb.append(new Character((char) ((h > 9) ? 'a' + h - 10 : '0' + h)));
            sb.append(new Character((char) ((l > 9) ? 'a' + l - 10 : '0' + l)));
        }
        return sb.toString();
    }
    public String getmd5(String value)
    {
        String md5=" ";
        value = "EZPAYTE-TestTransaction-21063606:123:10:";
        try
        {

            MessageDigest digest = MessageDigest.getInstance("MD5");
             md5 = getString(digest.digest(value.getBytes()));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return md5;
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