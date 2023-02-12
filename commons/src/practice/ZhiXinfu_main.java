package practice;

import com.directi.pg.Base64;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.payclub.PayClubUtils;
import jdk.nashorn.internal.runtime.JSONFunctions;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;
import sun.misc.BASE64Encoder;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 10/9/2020.
 */
public class ZhiXinfu_main
{
    public static void main(String[] args)
    {
       sale();

    }


    public static void sale()
    {
        try
        {

            String url = "https://cc.zxfgateway.com/deposit/";
            String merId = "38899130";  // New given MID
            String merOrdId = "T1582168333300";
            String version = "v3";
            int merOrdAmt = 10;
            String userIp = "179.12.33.0";
            String payType = "8001";
            String remark = "TestRemark";
            String respType = "01";
            String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String notifyUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
            String signType = "MD5";
            String ccDetails="{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\":\"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
           // String ccDetails="%7B%22ordCurrency%22%3A%22USD%22%2C%22cardNo%22%3A%225111111111111118%22%2C%22cardExpireMonth%22%3A%2212%22%2C%22cardExpireYear%22%3A%222022%22%2C%22cardSecurityCode%22%3A%22359%22%2C%22firstName%22%3A%22tian%22%2C%22lastName%22%3A%22wu%22%2C%22email%22%3A%22cc%40gmail.com%22%2C%22phone%22%3A%221389878098%22%2C%22country%22%3A%22us%22%2C%22city%22%3A%22los%22%2C%22address%22%3A%22Nofe+Street%22%2C%22zip%22%3A%222876%22%7D";
            String encodedccDetails = ESAPI.encoder().encodeForURL(ccDetails.toString());
          //  String encodedccDetails = "%7B%22ordCurrency%22%3A%22USD%22%2C%22cardNo%22%3A%225111111111111118%22%2C%22cardExpireMonth%22%3A%2212%22%2C%22cardExpireYear%22%3A%222022%22%2C%22cardSecurityCode%22%3A%22359%22%2C%22firstName%22%3A%22tian%22%2C%22lastName%22%3A%22wu%22%2C%22email%22%3A%22cc%40gmail.com%22%2C%22phone%22%3A%221389878098%22%2C%22country%22%3A%22us%22%2C%22city%22%3A%22los%22%2C%22address%22%3A%22Nofe+Street%22%2C%22zip%22%3A%222876%22%7D";
            System.out.println("ccDetails ---->" + encodedccDetails);
            String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version;
            String signMsg = convertmd5(signMsgstr);

            StringBuffer request = new StringBuffer();
            request.append("merId=" + merId + "&merOrdId=" + merOrdId + "&version=" + version + "&merOrdAmt=" + merOrdAmt + "&userIp=" + userIp + "&payType=" + payType + "&ccDetails=" + encodedccDetails +
                    "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&notifyUrl=" + notifyUrl +
                    "&signType=" + signType + "&signMsg=" + signMsg);
      /*      String request =
                    "{\n" +
                            "\"merId\": \""+merId+"\","+
                            "\"merOrdId\": \""+merOrdId+"\","+
                            "\"version\": \""+version+"\","+
                            "\"merOrdAmt\": \""+merOrdAmt+"\","+
                            "\"userIp\": \""+userIp+"\","+
                            "\"payType\": \""+payType+"\","+
                            "\"ccDetails\": \""+encodedccDetails+"\","+
                            "\"remark\":  \""+remark+"\","+
                            "\"respType\": \""+respType+"\","+
                            "\"returnUrl\": \""+returnUrl+"\","+
                            "\"notifyUrl\": \""+notifyUrl+"\","+
                            "\"signType\": \""+signType+"\","+
                            "\"signMsg\": \""+signMsg+"\""+
                            "}";*/
             System.out.println("signMsg---->" + signMsg);
            System.out.println("Sale request---->" + request);
            String response = doHttpPostConnection(url, request.toString());

            System.out.println("Sale response---->" + response);

        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (EncodingException e)
        {
            e.printStackTrace();
        }


    }

    public static String doHttpPostConnection(String url, String request)
    {
        String result = "";
        PostMethod post = new PostMethod(url);
        System.out.println(" URL --->" + url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
           // post.addRequestHeader("Content-Type", "text/html");
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
        catch(Exception e)
        {

            System.out.println("Exception --->" + e);
        }

        return result;
    }


    public static String convertmd5(String request) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        String md5 = null;

        if (null == request) return null;


        try
        {

            System.out.println("MD5 combination---"+request);

            MessageDigest digest = MessageDigest.getInstance("MD5");

            md5 = getString(digest.digest(request.getBytes()));

        }
        catch (NoSuchAlgorithmException e)
        {
            System.out.println("NoSuchAlgorithmException---"+e);
        }


        return md5;
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


    public static String hashSignature(String stringaMac) throws Exception
    {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        BASE64Encoder encoder = new BASE64Encoder();
        byte[] in = digest.digest(stringaMac.getBytes());

        String builder = "";
        builder=encoder.encode(in);
        return builder.toString();
    }

    public static String getSignature(List list, String privateKey)
    {
        Collections.sort(list);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++)
        {
            sb.append(list.get(i));
            if (i < list.size() - 1)
            {
                sb.append("|");
            }
        }

        String signature = HmacSHA512(sb.toString(), privateKey);
        System.out.println("signature =" + signature);

        return signature;
    }

    public static String HmacSHA512(String message, String key)
    {
        MessageDigest md = null;
        try
        {
            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            sha512_HMAC.init(secret_key);


            byte raw[] = sha512_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));


            String hex = Base64.encode(raw);
            return hex; //step 6
        }
        catch (Exception e)
        {
            System.out.println("exception " + e);
            return null;
        }

    }


    // + "&signMsg=" + signMsg +

    // 1st try
      /*  StringBuffer request = new StringBuffer();
        request.append("merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId  + "&notifyUrl=" + notifyUrl +  "&payType=" + payType +
                 "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl   +
                "&signType=" + signType+"&userIp=" + userIp + "&version=" + version + "&ordCurrency=" + ordCurrency + "&cardNo=" +cardNo + "&cardExpireYear=" +cardExpireYear + "&cardExpireMonth=" +cardExpireMonth +
                "&cardSecurityCode=" + cardSecurityCode + "&firstName=" + firstName + "&lastName=" + lastName + "&email="+ email + "&phone=" + phone + "&country=" + country + "&city=" + city +
                "&address=" + address + "&zip=" + zip);
        System.out.println("Sale Request ---->" + request);

        String response = doHttpPostConnection(url, request.toString());

        System.out.println("Sale response---->" + response);
*/

    // 2nd try
/*
      StringBuffer request = new StringBuffer();
        request.append("merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl +  "&payType=" + payType +
                "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl   +
                "&signType=" + signType + "&userIp=" + userIp + "&version=" + version + "&ccDetails=" + ordCurrency + cardNo + cardExpireYear + cardExpireMonth +
                 cardSecurityCode + firstName + lastName +  email + phone +  country +  city + address + zip);
        System.out.println("Sale Request ---->" + request);

        String response = doHttpPostConnection(url, request.toString());

        System.out.println("Sale response---->" + response);
*/

       /*   String request =
                    "{\n" +
                            "\"merId\": \""+merId+
                            "\"merOrdId\": \""+merOrdId+"\","+
                            "\"version\": \""+version+"\","+
                            "\"merOrdAmt\": \""+merOrdAmt+"\","+
                            "\"userIp\": \""+userIp+"\","+
                            "\"payType\": \""+payType+"\","+
                            "\"ccDetails\":  \""+ccDetails+"\""+
                            "\"remark\":  \""+remark+"\""+
                            "\"respType\":  \""+respType+"\""+
                            "\"returnUrl\":  \""+returnUrl+"\""+
                            "\"notifyUrl\":  \""+notifyUrl+"\""+
                            "\"signType\":  \""+signType+"\""+
                            "\"signMsg\":  \""+signMsg+"\""+
                            "}";*/
     /*  JSONObject jsonObject = new JSONObject();
    jsonObject.put("ordCurrency","USD");
    jsonObject.put("cardNo","5111111111111118");
    jsonObject.put("cardExpireMonth","12");
    jsonObject.put("cardExpireYear","2022");
    jsonObject.put("cardSecurityCode","359");
    jsonObject.put("firstName","tian");
    jsonObject.put("lastName","wu");
    jsonObject.put("email","cc@gmail.com");
    jsonObject.put("phone","1389878098");
    jsonObject.put("country","us");
    jsonObject.put("city","los");
    jsonObject.put("address","Nofe Street");
    jsonObject.put("zip","2876");*/
}
