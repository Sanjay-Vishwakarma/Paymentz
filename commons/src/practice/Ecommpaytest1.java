// Dont use this file to run main method of ecommpay
package practice;

import com.directi.pg.*;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.transaction.vo.restVO.RequestVO.Date;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Base64;

/**
 * Created by Admin on 6/12/2020.
 */
public class Ecommpaytest1
{
    public static void main(String[] args)
    {
        recurring();
       // payout();
       //  sale();
       //  auth();
    }

    public static void recurring()
    {
        try
        {
            String key = "qwertyuiasdfghj12345t6y";
            String recurringurl = "https://api.accentpay.com/v2/payment/card/recurring";
            int payment_type = 1;
            int amount = 500;
            String id = "15936733253227";
            int amount1 = 1;
            int rate = 1000;
            boolean fire_safety_act_indicator = true;
            int price = 1000;
            int tax = 8;
            int year = 2023;
            int month = 2;
            String trackingId="174246";
            String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String callbackUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";


            String request = "{" +
                    " \"general\":" + "{" +
                    "\"project_id\":\"2345\"," +
                    "\"payment_id\":"+trackingId+"," +

                    "\"customer\":{" +
                    "\"id\":\"254\"," +
                    "\"country\":\"IND\"," +
                    "\"city\":\"Mumbai\"," +
                    "\"state\":\"MH\"," +
                    "\"phone\":\"08987786576\"," +
                    "\"day_of_birth\":\"2345\"," +
                    "\"birthplace\":\"30-08-2000\"," +
                    "\"first_name\":\"abc\"," +
                    "\"last_name\":\"pqr\"," +
                    "\"email\":\"abc@gmail.com\"," +
                    "\"ip_address\":\"192.34.43.1\"," +
                    "\"district\":\"Thane\"," +
                    "\"street\":\"abcdef\"," +

                    "\"payment\":{" +
                    "\"amount\":" + amount + "," +
                    "\"currency\":\"EUR 421\"}," +

                    "\"recurring\":{" +
                    "\"id\":" + id + "}," +

                    "\"acs_return_url\":{" +
                    "\"return_url\":\"" + returnUrl + "\"," +
                    "\"3ds_notification_url\":\"" + callbackUrl + "\"" +
                    " }}";
            System.out.println("Request-->" + request);
            String utf8 = sha512(request);
            System.out.println("Utfencoded-->" + utf8);
            String response = doPostHTTPSURLConnectionClient(recurringurl, request);
            System.out.println("response-->" + response);
            String hmac = getSignature(request, key);
            System.out.println("hmac-->" + hmac);

        }
        catch (PZTechnicalViolationException e)
        {
            e.printStackTrace();
        }

    }


    public static void sale()
    {
        try
        {
            String key = "qwertyuiasdfghj12345t6y";
            String saleurl = "https://api.ecommpay.com/v2/payment/card/sale";
            int payment_type = 1;
            int amount = 500;
            int id = 129;
            int amount1 = 1;
            int rate = 1000;
            boolean fire_safety_act_indicator = true;
            int price = 1000;
            int tax = 8;
            int year = 2023;
            int month = 2;
            String returnUrl="http://localhost:8081/transaction/Common3DFrontEndServlet";
            String callbackUrl="https://staging.paymentz.com/transaction/CommonBackEndServlet";

            String request = "{" +
                    " \"general\":" + "{" +
                    "\"project_id\":\"2345\"," +
                    "\"payment_id\":\"231\"}," +

                    "\"card\":{" +
                    "\"pan\":\"4242424242424242\"," +
                    "\"year\":" + year + "," +
                    "\"month\":" + month + "," +
                    "\"card_holder\":\"abc\"}," +

                    "\"customer\":{" +
                    "\"id\":\"254\"," +
                    "\"country\":\"IND\"," +
                    "\"city\":\"Mumbai\"," +
                    "\"state\":\"MH\"," +
                    "\"phone\":\"08987786576\"," +
                    "\"home_phone\":\"09087786576\"," +
                    "\"work_phone\":\"09087786575\"," +
                    "\"day_of_birth\":\"2345\"," +
                    "\"birthplace\":\"30-08-2000\"," +
                    "\"first_name\":\"abc\"," +
                    "\"middle_name\":\"def\"," +
                    "\"last_name\":\"pqr\"," +
                    "\"email\":\"abc@gmail.com\"," +
                    "\"ip_address\":\"192.34.43.1\"," +
                    "\"district\":\"Thane\"," +
                    "\"street\":\"abcdef\"," +
                    "\"building\":\"13\"}," +

                    "\"avs_data\":{" +
                    "\"avs_post_code\":\"1\"," +
                    "\"avs_street_address\":\"2\"}," +

                    "\"payment\":{" +
                    "\"amount\":" + amount + "," +
                    "\"currency\":\"EUR 421\"}," +

                    "\"positions\":{" +
                    "\"price\":" + price + "," +
                    "\"position_description\":\"1\"," +
                    "\"tax\":" + tax + "}," +

                    "\"payments\":{" +
                    "\"payment_type\":" + payment_type + "," +
                    "\"amount\":" + amount + "}," +

                    "\"interface_type\":{" +
                    "\"id\":" + id + "}," +

                    "\"receipt_data\":{" +
                    "\"positions\":{" +
                    "\"amount\":" + amount1 + "}" +
                    "  }," +

                    "\"addendum\":{" +
                    "\"lodging\":{" +
                    "\"customer_service_toll_free_number\":\"2345\"," +
                    "\"check_in_date\":\"2031\","+
                    "\"check_out_date\":\"12\"," +
                    "\"folio_number\":\"2345\"}" +
                    "}," +

                    "\"room\":{" +
                    "\"rate\":" + rate + "," +
                    "\"fire_safety_act_indicator\":" + fire_safety_act_indicator + "}," +

                    "\"acs_return_url\":{"+
                    "\"return_url\":\""+returnUrl+"\"," +
                    "\"3ds_notification_url\":\""+callbackUrl+"\"" +
                    " }}";

            System.out.println("Request-->" + request);
            String utf8 = sha512(request);
            System.out.println("Utfencoded-->"+utf8);
            String response = doPostHTTPSURLConnectionClient(saleurl, request);
            System.out.println("response-->" + response);
            String hmac=getSignature(request,key);
            System.out.println("hmac-->"+hmac);


    //  String encoded=new String(org.apache.commons.codec.binary.Base64.encodeBase64((request).getBytes()));
            //System.out.println("encoded-->"+encoded);


        }

        catch (PZTechnicalViolationException e)
        {
            e.printStackTrace();
        }
    }

    public static void payout()
    {
        try
        {
            String key = "qwertyuiasdfghj12345t6y";
            String payouturl = "https://api.accentpay.com/v2/payment/card/payout";
            int payment_type = 1;
            int amount = 500;
            int id = 129;
            int amount1 = 1;
            int rate = 1000;
            boolean fire_safety_act_indicator = true;
            int price = 1000;
            int tax = 8;
            int year = 2023;
            int month = 2;
            String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String callbackUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";

            String request = "{" +
                    " \"general\":" + "{" +
                    "\"project_id\":\"2345\"," +
                    "\"payment_id\":\"231\"}," +

                    "\"card\":{" +
                    "\"pan\":\"4242424242424242\"," +
                    "\"year\":" + year + "," +
                    "\"month\":" + month + "," +
                    "\"card_holder\":\"abc\"}," +

                    "\"customer\":{" +
                    "\"id\":\"254\"," +
                    "\"country\":\"IND\"," +
                    "\"city\":\"Mumbai\"," +
                    "\"state\":\"MH\"," +
                    "\"phone\":\"08987786576\"," +
                    "\"home_phone\":\"09087786576\"," +
                    "\"work_phone\":\"09087786575\"," +
                    "\"day_of_birth\":\"2345\"," +
                    "\"birthplace\":\"30-08-2000\"," +
                    "\"first_name\":\"abc\"," +
                    "\"middle_name\":\"def\"," +
                    "\"last_name\":\"pqr\"," +
                    "\"email\":\"abc@gmail.com\"," +
                    "\"ip_address\":\"192.34.43.1\"," +
                    "\"district\":\"Thane\"," +
                    "\"street\":\"abcdef\"," +
                    "\"building\":\"13\"}," +

                    "\"payment\":{" +
                    "\"amount\":" + amount + "," +
                    "\"currency\":\"EUR 421\"}," +


                    "\"acs_return_url\":{"+
                    "\"return_url\":\""+returnUrl+"\"," +
                    "\"3ds_notification_url\":\""+callbackUrl+"\"" +
                    " }}";
            System.out.println("Request-->" + request);
            String utf8 = sha512(request);
            System.out.println("Utfencoded-->" + utf8);
            String response = doPostHTTPSURLConnectionClient(payouturl, request);
            System.out.println("response-->" + response);
            String hmac = getSignature(request, key);
            System.out.println("hmac-->" + hmac);


            //  String encoded=new String(org.apache.commons.codec.binary.Base64.encodeBase64((request).getBytes()));
            //System.out.println("encoded-->"+encoded);


        }

        catch (PZTechnicalViolationException e)
        {
            e.printStackTrace();
        }
    }
    public static void auth()
    {
try{
    String returnUrl="http://localhost:8081/transaction/Common3DFrontEndServlet";
    String callbackUrl="https://staging.paymentz.com/transaction/CommonBackEndServlet";

    String authurl = "https://api.ecommpay.com/v2/payment/card/auth";
        int project_id=1209;
        int year=2025;
        int month=10;
        int payment_type =1;
        int amount =500;
        int id =129;
        int amount1=1;
        int price=1000;
        int tax =8;
        int rate=1000;
        boolean fire_safety_act_indicator=true;
        String request ="{" + "" +
                " \"general\":" + "{" +
                "\"project_id\":\"1523\"," +
                "\"payment_id\":\"787\","+
                "\"signature\":\"78\","+
                "},"+

                "\"card\":{"+
                "\"pan\":\"4242424242424287\","+
                "\"year\":"+year+"," +
                "\"month\":"+month+"," +
                "\"card_holder\":\"abcdefghi\"},"+

                "\"avs_data\":{" +
                "\"avs_post_code\":\"9\"," +
                "\"avs_street_address\":\"6\"}," +

                "\"customer\":{" +
                "\"id\":\"254\"," +
                "\"country\":\"IND\"," +
                "\"city\":\"Mumbai\"," +
                "\"state\":\"MH\"," +
                "\"phone\":\"08987786576\"," +
                "\"home_phone\":\"09087786576\"," +
                "\"work_phone\":\"09087786575\"," +
                "\"day_of_birth\":\"2345\"," +
                "\"birthplace\":\"30-08-2000\"," +
                "\"first_name\":\"abc\"," +
                "\"middle_name\":\"def\"," +
                "\"last_name\":\"pqr\"," +
                "\"email\":\"abc@gmail.com\"," +
                "\"ip_address\":\"192.34.43.1\"," +
                "\"district\":\"Thane\"," +
                "\"street\":\"abcdef\"," +
                "\"building\":\"13\"}," +

                "\"payment\":{" +
                "\"amount\":"+amount+"," +
                "\"currency\":\"EUR 421\"}," +

                "\"positions\":{" +
                "\"price\":"+price+"," +
                "\"position_description\":\"EUR 421\"," +
                "\"tax\":"+tax+"}," +

                "\"payments\":{" +
                "\"payment_type\":" + payment_type + ","  +
                "\"amount\":" + amount + "}," +

                "\"receipt_data\":{" +
                "\"positions\":{" +
                "\"amount\":"+ amount1 + "}" +
                "  }," +

                "\"interface_type\":{" +
                "\"id\":" + id + "}," +

                "\"addendum\":{" +
                "\"lodging\":{" +
                "\"customer_service_toll_free_number\":\"2345\"," +
                "\"check_in_date\":\"2031\"," +
                "\"check_out_date\":\"12\"," +
                "\"folio_number\":\"2345\"}" +
                "}," +

                "\"room\":{" +
                "\"rate\":"+rate+"," +
                "\"fire_safety_act_indicator\":"+fire_safety_act_indicator+"}," +
                "\"acs_return_url\":{"+
                "\"return_url\":\""+returnUrl+"\"," +
                "\"3ds_notification_url\":\""+callbackUrl+"\"" +
                " }"+

                "}";
        System.out.println("Request-->" + request);
        String response = doPostHTTPSURLConnectionClient(authurl, request);
        System.out.println("response-->" + response);
    }    catch (PZTechnicalViolationException e)
{
    e.printStackTrace();
}
    }

    public static String getSHA512(String string) throws PZTechnicalViolationException
    {
        String sha = string.trim();

        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(sha.getBytes());
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
         e.printStackTrace();
        }
        return hexString.toString();
    }
/*    public static String sha512(String request)
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
        System.out.println(hexString);
        return hexString.toString().trim();
    }*/
    public static String encryptData(String message, String _encryptionKey)
    {
        try
        {
            final byte[] digestOfPassword = _encryptionKey.getBytes("utf-8");
            final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

            final SecretKey key = new SecretKeySpec( keyBytes , "DESede");
            final Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final byte[] plainTextBytes = message.getBytes("utf-8");
            final byte[] cipherText = cipher.doFinal(plainTextBytes);
            return Base64.getEncoder().encodeToString(cipherText);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }
    public static String HmacSHA512(String message,String key)
    {
        MessageDigest md = null;
        try
        {
            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            sha512_HMAC.init(secret_key);
            byte raw[] = sha512_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));
            String hex = bytesToHex(raw);
            System.out.println("=====hex2 String=="+hex);
            return hex; //step 6
        }
        catch(Exception e)
        {
            System.out.println("exception "+ e);
            return null;
        }
    }
    private static String bytesToHex(byte[] hashInBytes) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hashInBytes.length; i++) {
            sb.append(Integer.toString((hashInBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();

    }

    public static String doPostHTTPSURLConnectionClient(String strURL,String req) throws PZTechnicalViolationException

    {
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {    HttpClient httpClient = new HttpClient();

            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(req);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            he.printStackTrace();
        }
        catch (IOException io){
            io.printStackTrace();
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
    public static String sha512(String request)
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

    public static  String getSignature(String Reqparameter,String apiKey)
    {
        String result="";
        try
        {
            String HMAC_SHA1_ALGORITHM = "HmacSHA1";
            SecretKeySpec signingKey = new SecretKeySpec(apiKey.getBytes(), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(Reqparameter.getBytes());
            result = com.directi.pg.Base64.encode(rawHmac);

        }catch (NoSuchAlgorithmException e){
            System.out.println("NoSuchAlgorithmException-----"+e);
        }catch (InvalidKeyException k){
            System.out.println("InvalidKeyException-----"+k);
        }

        return result;

    }

    public static String calculateSignature(TreeMap<String,TreeMap<String,String>> requestHashMap,String secretKey)
    {
        Functions functions=new Functions();
        String hash="";
        StringBuffer requestString=new StringBuffer();
        Set<String> keySet=requestHashMap.keySet();
        int i=0;
        int lastIndex=keySet.size()-1;
        for (String key:keySet)
        {
            TreeMap<String,String> innerTreeMap= requestHashMap.get(key);

            Set<String> innerKeySet=innerTreeMap.keySet();
            for (String innerKey:innerKeySet)
            {
                String value=innerTreeMap.get(innerKey);
                if (lastIndex == i)
                {
                    if (functions.isValueNull(value))
                        requestString.append(key+":"+innerKey + ":" + value);
                    else
                        requestString.append(key+":"+innerKey + ":" + "");
                }
                else
                {
                    if (functions.isValueNull(value))
                        requestString.append(key+":"+innerKey + ":" + value+";");
                    else
                        requestString.append(key+":"+innerKey + ":" + ";");
                }
            }
            i++;
        }
        System.out.println("requestString-->"+requestString);
        hash=HmacSHA512(requestString.toString(),secretKey);

        return hash;
    }

}