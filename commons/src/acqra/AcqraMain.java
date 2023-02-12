package acqra;

import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.manager.vo.morrisBarVOs.Data;
import com.payment.acqra.AcqraUtils;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * Created by Admin on 1/18/2020.
 */
public class AcqraMain
{

    public static void main(String[] args)
    {

        sale();
      // refund();
        //enquiry();

    }

    public static void sale()
    {
        //Current time
//       String currenttime =new  SimpleDateFormat("HHmmss").format(new Data());
        String currenttime = "1230000004";

        String mid = "gyrvgub";
        String apikey = "34567=";
        String securitykey = "5678";
        String amount = "1.txercyfugi";
        String currency = "EUR";
        String card_number = "5111111111111111";
        String exp_month = "12";
        String exp_year = "2023";
        String security_code = "asdfv";
        String buyer_ip = "192.168.1.1"; //customer IP
        String user_Agent = "dscd";
        String accept_language = "en";
        String trackingID="1888";

        String url = "https://sandbox.acqra.com/v2/sales_payment";
        String hash = hashSHA256(mid + "," + currency + "," + trackingID + "," + amount + "," + securitykey);
        String lang = "en";
        String card_holder_first_name = "1234";
        String card_holder_last_name = "1234";
        String bill_street_address = "1234tg";
        String bill_city = "mumbai";
        String bill_state = "mh";
        String bill_zip = "400059";
        String bill_country = "IN";
        String email = "sagar.sonar@paymentz.com";
        String phone = "9850000125";
        String website = "shop.com";

        StringBuffer request = new StringBuffer();
        request.append("mid=" + mid + "&apikey=" + apikey + "&currency=" + currency + "&amount=" + amount + "&order_ref=" + trackingID + "&card_number=" + card_number + "&exp_month=" + exp_month + "&exp_year=" + exp_year
                + "&security_code=" + security_code + "&buyer_ip=" + buyer_ip + "&lang=" + lang + "&card_holder_first_name=" + card_holder_first_name + "&card_holder_last_name=" + card_holder_last_name
                + "&bill_street_address=" + bill_street_address + "&bill_city=" + bill_city + "&bill_state=" + bill_state + "&bill_zip=" + bill_zip + "&bill_country=" + bill_country + "&email=" + email + "&phone=" + phone
                + "&user_agent=" + user_Agent + "&accept_language=" + accept_language + "&hash=" + hash + "&website=" + website);

        System.out.println("request-------" + request);
        String response = doHttpPostConnection(url, request.toString());
        System.out.println("response---------" + response);


    }

    public static String hashSHA256(String plainText)
    {
        StringBuffer cipherText = new StringBuffer();
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
            System.out.println("NoSuchAlgorithmException --->" + e);
        }
        catch (UnsupportedEncodingException e)
        {
            System.out.println("UnsupportedEncodingException --->" + e);
        }
        return cipherText.toString();
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

    public static void refund()
    {
        String mid = "trans0404001test";
        String apikey = "MjJQTCtiQUdFbUF6cGlBS0hUQ011V0xkNlhKbm9QVWhMYi9UMXVxTThKdz0=";
        String amount = "1.00";
        String currency = "EUR";
        //String currenttime = "1230000004";
        String refund_description = "Test123";
        String transaction_id = "20011821060826872486597601";
        String securitykey = "boQlWBiUE8WfgFEPy8sycpJvKaHYr08U";
        String hash=hashSHA256(mid + "," + currency + "," + transaction_id + "," + amount + "," + securitykey);
        try{
            StringBuffer request2 = new StringBuffer();
            request2.append("mid=" + mid + "&apikey=" + apikey + "&transaction_id=" + transaction_id + "&currency=" + currency + "&amount=" + amount +"&refund_description="+refund_description+"&hash=" + hash);
            String url2 = "https://sandbox.acqra.com/v2/refund_payment";
            System.out.println("Request2-------" + request2);
            String response2 = doHttpPostConnection(url2, request2.toString());
            System.out.println("Response2--------" + response2);
            JSONObject obj = new JSONObject(response2);
            String status_message =obj.getString("status_message");
            System.out.println(status_message);
            String status_code =obj.getString("status_code");
            System.out.println(status_code);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void enquiry()
    {

        String mid="trans0404001test";
        String apikey ="MjJQTCtiQUdFbUF6cGlBS0hUQ011V0xkNlhKbm9QVWhMYi9UMXVxTThKdz0";
        String securitykey="boQlWBiUE8WfgFEPy8sycpJvKaHYr08U";
        String order_ref="20011821453036596115691201";
        String enquiry_url="https://sandbox.acqra.com/v2/enquiry_orderref";
        String transaction_date="200118";
        String tracking_Id="120909";


        String hash= AcqraUtils.hashSHA256(mid + ""+order_ref+","+transaction_date + ","+ securitykey);
        StringBuffer request = new StringBuffer("");
        request.append("mid=" + mid + "&apikey=" + apikey + "&order_ref=" + order_ref + "&transaction_date=" + transaction_date + "&hash=" + hash);
        System.out.println("enquiryOrder_refTest request---->" + request);
            System.out.println("transaction_date---->" + transaction_date);
        String response = AcqraUtils.doHttpPostConnection(enquiry_url,request.toString());
        System.out.println(response);
        }
    }

