package flwbarter;

/**
 * Created by Admin on 1/17/2020.
 */
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class FlwBarterMain
{
    public static void main(String[] args)
    {
        processSale();
    }
    public static void processSale()
    {
        String publickey  ="FLWPUBK_TEST-f01c74964dd47f882a0ca80645a905d0-X";
        String request = "{" +
                "\"amount\": 1000," +
                "\"PBFPubKey\": \" "+ publickey +"\"," +
                "\"currency\": \"NGN\"," +
                "\"country\": \"NG\"," +
                "\"email\": \"test@example.com\"," +
                "\"txRef\": \"FX_XXXXXXXXX_XXXX_XX\"," +
                "\"meta\": [" +
                "{" +
                "\"metaname\": \"flight_id\"," +
                "\"metavalue\": \"LH0568\"" +
                "}" +
                "]," +
                "\"subaccounts\": null," +
                "\"is_barter\": 1," +
                "\"payment_type\": \"barter\"," +
                "\"payment_page\": null," +
                "\"campaign_id\": null," +
                "\"redirect_url\": \"https://redirect.mercchant.com/pay/callback\"," +
                "\"device_fingerprint\": \"ea466c56bd03366bae5f300ac1d42fe\"," +
                "\"ip\": \"123.40.56.189\"," +
                "\"firstname\": \"John\"," +
                "\"lastname\": \"Doe\"," +
                "\"charge_type\": null," +
                "\"cycle\": null," +
                "\"phonenumber\": \"08031112222\"" +
                "}";
        System.out.println("1st request---"+request);
        String key="FLWSECK_TEST-daa712a7f495b5ea334d1864de1d3ea4-X"; //Secret Key
        String encryptedKey=getKey(key);
        String encryptData =encryptData(request, encryptedKey);
        System.out.println(encryptData);

        String request2 = "{" +
                "\"PBFPubKey\": \""+ publickey +"\"," +
                "\"client\": \""+ encryptData +"\"," +
                "\"alg\": \"3DES-24\"" +
                "}";
        System.out.println(request2);
        try
        {
            String url1="https://api.ravepay.co/flwv3-pug/getpaidx/api/charge";
            String response2obj = doPostHTTPSURLConnectionClient(url1,request2,publickey);
            System.out.println("response2obj---"+response2obj);

            JSONObject myResponse = new JSONObject(response2obj);
            System.out.println("result after Reading JSON Response");
            String status = (String)myResponse.get("status");
            System.out.println(status);
            String message = (String)myResponse.get("message");
            System.out.println(message);


            JSONObject data = myResponse.getJSONObject("data");
            String response_code = (String)data.get("response_code");
            System.out.println(response_code);
            String response_message = (String)data.get("response_message");
            System.out.println(response_message);
            String flw_reference = (String)data.get("flw_reference");
            System.out.println(flw_reference);
            String redirect_url = (String)data.get("redirect_url");
            System.out.println(redirect_url);
            String requery_url = (String)data.get("requery_url");
            System.out.println(requery_url);


            String url2 = "https://pwb.azurewebsites.net/api/v1/barter/reference/details";
            String request3 = "" +
                    "{" +
                    "\"reference\":\"" + flw_reference+"\"," +
                    "\"public_key\":\""+ publickey +"\"" +
                    "}";
            System.out.println(request3);
            String response3obj = doPostHTTPSURLConnectionClient(url2,request3,publickey);
            System.out.println("response3obj------"+response3obj);
            String stri="{" +
                    "\"reference\":\"PWB_9695093503\"," +
                    "\"public_key\":\"FLWPUBK_TEST-f01c74964dd47f882a0ca80645a905d0-X\"" +
                    "}";

             JSONObject data1 = new JSONObject(response3obj);

          //  System.out.println("result after Reading JSON Response");
            JSONObject data2 = data1.getJSONObject("data");
           JSONObject data3= data2.getJSONObject("data");

            String url3="https://pwb.azurewebsites.net/api/v1/barter/pay";
            String flwref = data3.getString("flwref");
            System.out.println(flwref);
            String callbackurl =data3.getString("callbackurl");
            System.out.println(callbackurl);
            String amount =data3.getString("amount");
            System.out.println(amount);
            String currency =data3.getString("currency");
            System.out.println(currency);
            String appfee =data3.getString("appfee");
            System.out.println(appfee);

      String request4 ="{" +
                    "\"type\":\"auth\"," +
                    "\"currency\":\""+currency+"\"," +
                    "\"amount\":{" +
                    "\"transaction_amount\": \""+amount+"\"," +
                    "\"fee\": \""+appfee+"\"," +
                    "\"merchant_bears_fee\": true" +
                    "}," +
                    "\"authentication_request\":{" +
                    "\"identifier\":\"xyz@gmail.com\"," +
                    "\"password\":\"1243\"" +
                    "}," +
                    "\"transaction_reference\":\""+flwref+"\"," +
                    "\"call_back_url\":\""+callbackurl+"\"" +
                    "}";
            System.out.println("request4------"+request4);
            String response4obj = doPostHTTPSURLConnectionClient(url3,request4,publickey);
            System.out.println("response4obj------"+response4obj);

          /*  String request5="{\n" +
                    "\"type\":\"register\",\n" +
                    "\"currency\":\""+currency+"\" ,\n" +
                    "\"amount\":{\n" +
                    "\"transaction_amount\":\" "+amount+"\",\n" +
                    "\"fee\":\""+appfee+"\",\n" +
                    "\"merchant_bears_fee\": false\n" +
                    "},\n" +
                    "\"notes\":\"Pay with Barter\",\n" +
                    "\"register_and_pay_with_barter_request\": {\n" +
                    "\"first_name\":\"Mo\",\n" +
                    "\"last_name\":\"B\",\n" +
                    "\"mobile_number\":\"8080808080\",\n" +
                    "\"email_address\":\"xyz@gmail.com\",\n" +
                    "\"password\":\"1243\",\n" +
                    "\"country\":\"NG\",\n" +
                    "\"transaction_pin\":\"1243\"\n" +
                    "},\n" +
                    "\"transaction_reference\":\""+flwref+"\",\n" +
                    "\"call_back_url\":\""+callbackurl+"\"\n" +
                    "}";
            System.out.println("request5------"+request5);
            String response4obj = doPostHTTPSURLConnectionClient(url3,request5,publickey);
            System.out.println("response4obj------"+response4obj);
*/
            String request6 ="{" +
                    "\"type\":\"charge_card\"," +
                    "\"currency\":\""+currency+"\"," +
                    "\"amount\":{" +
                    "\"transaction_amount\":\""+amount+"\"," +
                    "\"fee\": 0," +
                    "\"merchant_bears_fee\":true" +
                    "}," +
                    "\"notes\":\"Pay with Barter\"," +
                    "\"authentication_request\":{" +
                    "\"auth_token\":\"PWB-AUTHX-******************-199138\"" +
                    "}," +
                    "\"pay_with_card_request\":{" +
                    "\"card_id\": 0," +
                    "\"pan\":\"5531882884804517\"," +
                    "\"cvv\":\"564\"," +
                    "\"expiry_month\":\"12\"," +
                    "\"expiry_year\":\"2024\"" +
                    "}," +
                    "\"transaction_reference\": \""+flwref+"\"," +
                    "\"call_back_url\":\""+callbackurl+"\"" +
                    "}\n" +
                    " ";
            System.out.println("request6------"+request6);
            String response5obj = doPostHTTPSURLConnectionClient(url3,request6,publickey);
            System.out.println("response5obj------"+response5obj);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
            }
    public static String doPostHTTPSURLConnectionClient(String strURL,String req,String public_key) throws PZTechnicalViolationException
    {
        String result ="";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();
            //  post.addRequestHeader("X-API-KEY",apiKey);
            post.addRequestHeader("Content-Type","application/json");
            post.addRequestHeader("publickey",public_key);
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


    public static String getKey(String seedKey)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] hashedString = md.digest(seedKey.getBytes("utf-8"));
            byte[] subHashString = toHexStr(Arrays.copyOfRange(hashedString, hashedString.length - 12, hashedString.length)).getBytes("utf-8");
            String subSeedKey = seedKey.replace("FLWSECK-", "");
            subSeedKey = subSeedKey.substring(0, 12);   // subSeedKey is the key
            byte[] combineArray = new byte[24];
            System.arraycopy(subSeedKey.getBytes(), 0, combineArray, 0, 12);
            System.arraycopy(subHashString, subHashString.length - 12, combineArray, 12, 12);
            return new String(combineArray);
        }
        catch (NoSuchAlgorithmException ex)
        {
            System.out.println("Exception in FlwBarterUtils :: " + ex);
        }
        catch (UnsupportedEncodingException ex)
        {
            System.out.println("Exception in FlwBarterUtils :: " + ex);
        }
        return null;
    }
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
    public static String toHexStr(byte[] bytes)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++)
        {
            builder.append(String.format("%02x", bytes[i]));
        }
        return builder.toString();
    }
}
