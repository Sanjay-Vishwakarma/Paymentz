package practice;


import com.directi.pg.Base64;
import com.directi.pg.Functions;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Admin on 10/9/2020.
 */
public class zhxinfuMain
{
    public static void main(String[] args)
    {
        //refund();
        //sale();
       // inquiry();
        sale3D();
    }
    public static void sale3D()
    {
        try
        {
            Functions functions = new Functions();
            String url = "https://cc.zxfgateway.com/3d/";
            String merId = "38899137";  // New given MID
            String merOrdId = "T15821682075029";
            String version = "v3";
            String merOrdAmt = "10.00";
            String userIp = "127.0.0.1";
            String payType = "8001";
            String remark = "TestRemark";
            String respType = "02";
            String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String notifyUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
            //String returnUrl = "http://api.cpmnl.com/v3/deposit/returnUrl.php";
            //String notifyUrl = "http://api.cpmnl.com/v3/deposit/notifyUrl.php";
            String secretkey = "ZP84paXPCZ4hreSTYGfc6tsfshWNhB7d";
            String signType = "MD5";
            String ccDetails = "{\"ordCurrency\":\"USD\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            String encodedccDetails = URLEncoder.encode(URLEncoder.encode(ccDetails.toString(), "UTF-8"), "UTF-8");
            System.out.println("ccDetails ---->" + URLDecoder.decode(URLDecoder.decode(encodedccDetails, "UTF-8")));

            TreeMap treeMapSign = new TreeMap();
            if (functions.isValueNull(merId))
                treeMapSign.put("merId", merId);
            if (functions.isValueNull(merOrdAmt))
                treeMapSign.put("merOrdAmt", merOrdAmt);
            if (functions.isValueNull(merOrdId))
                treeMapSign.put("merOrdId", merOrdId);
            if (functions.isValueNull(notifyUrl))
                treeMapSign.put("notifyUrl", notifyUrl);
            if (functions.isValueNull(payType))
                treeMapSign.put("payType", payType);
            if (functions.isValueNull(remark))
                treeMapSign.put("remark", remark);
            if (functions.isValueNull(respType))
                treeMapSign.put("respType", respType);
            if (functions.isValueNull(returnUrl))
                treeMapSign.put("returnUrl", returnUrl);
            if (functions.isValueNull(signType))
                treeMapSign.put("signType", signType);
            if (functions.isValueNull(userIp))
                treeMapSign.put("userIp", userIp);
            if (functions.isValueNull(version))
                treeMapSign.put("version", version);

            String signMsg = convertmd5(treeMapSign, secretkey);
            System.out.println("treeMap----->" + treeMapSign);

            StringBuffer request = new StringBuffer();
            request.append("merId=" + merId +
                    "&merOrdAmt=" + merOrdAmt +
                    "&merOrdId=" + merOrdId +
                    "&notifyUrl=" + notifyUrl +
                    "&payType=" + payType +
                    "&remark=" + remark +
                    "&respType=" + respType +
                    "&returnUrl=" + returnUrl +
                    "&signType=" + signType +
                    "&userIp=" + userIp +
                    "&version=" + version +
                    "&signMsg=" + signMsg +
                    "&ccDetails=" + encodedccDetails);

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
    }

    public static void sale()
    {
        try
        {
            Functions functions = new Functions();
            String url = "https://cc.zxfgateway.com/deposit/";
            String merId = "38899130";  // New given MID
            String merOrdId = "T15821682075129";
            String version = "v3";
            String merOrdAmt = "10.00";
            String userIp = "127.0.0.1";
            String payType = "8001";
            String remark = "TestRemark";
            String respType = "01";
            String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String notifyUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
            //String returnUrl = "http://api.cpmnl.com/v3/deposit/returnUrl.php";
            //String notifyUrl = "http://api.cpmnl.com/v3/deposit/notifyUrl.php";
            String secretkey = "7PmNDGeZTJb6wY5AsPGbND88a52p2irZ";
            String signType = "MD5";
            String ccDetails = "{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\":\"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            String encodedccDetails = URLEncoder.encode(URLEncoder.encode(ccDetails.toString(), "UTF-8"), "UTF-8");
            System.out.println("ccDetails ---->" + URLDecoder.decode(URLDecoder.decode(encodedccDetails, "UTF-8")));

            TreeMap treeMapSign = new TreeMap();
            if (functions.isValueNull(merId))
                treeMapSign.put("merId", merId);
            if (functions.isValueNull(merOrdAmt))
                treeMapSign.put("merOrdAmt", merOrdAmt);
            if (functions.isValueNull(merOrdId))
                treeMapSign.put("merOrdId", merOrdId);
            if (functions.isValueNull(notifyUrl))
                treeMapSign.put("notifyUrl", notifyUrl);
            if (functions.isValueNull(payType))
                treeMapSign.put("payType", payType);
            if (functions.isValueNull(remark))
                treeMapSign.put("remark", remark);
            if (functions.isValueNull(respType))
                treeMapSign.put("respType", respType);
            if (functions.isValueNull(returnUrl))
                treeMapSign.put("returnUrl", returnUrl);
            if (functions.isValueNull(signType))
                treeMapSign.put("signType", signType);
            if (functions.isValueNull(userIp))
                treeMapSign.put("userIp", userIp);
            if (functions.isValueNull(version))
                treeMapSign.put("version", version);

            String signMsg = convertmd5(treeMapSign, secretkey);
            System.out.println("treeMap----->" + treeMapSign);

            StringBuffer request = new StringBuffer();
            request.append("merId=" + merId +
                    "&merOrdAmt=" + merOrdAmt +
                    "&merOrdId=" + merOrdId +
                    "&notifyUrl=" + notifyUrl +
                    "&payType=" + payType +
                    "&remark=" + remark +
                    "&respType=" + respType +
                    "&returnUrl=" + returnUrl +
                    "&signType=" + signType +
                    "&userIp=" + userIp +
                    "&version=" + version +
                    "&signMsg=" + signMsg +
                    "&ccDetails=" + encodedccDetails);

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
    }
    public static void refund()
    {
        try
        {
            Functions functions = new Functions();
            String url = "https://cc.zxfgateway.com/refund/";
            String merId = "38899130";  // New given MID
            String merOrdId ="182066";//TB2011226068377
            String refundAmt ="50.00";
            String refundRemark ="TestRemark";
            String refundType = "1";
            String timeStamp="20201122005040";
            String secretkey="7PmNDGeZTJb6wY5AsPGbND88a52p2irZ";
            String signType = "MD5";

            TreeMap treeMapSign=new TreeMap();
            if(functions.isValueNull(merId))
                treeMapSign.put("merId",merId);

            if(functions.isValueNull(merOrdId))
                treeMapSign.put("merOrdId",merOrdId);
            if(functions.isValueNull(refundType))
                treeMapSign.put("refundType",refundType);
            if(functions.isValueNull(refundRemark))
                treeMapSign.put("refundRemark",refundRemark);
            if(functions.isValueNull(refundAmt))
                treeMapSign.put("refundAmt",refundAmt);
            if(functions.isValueNull(signType))
                treeMapSign.put("signType",signType);
            if(functions.isValueNull(timeStamp))
                treeMapSign.put("timeStamp",timeStamp);


            String signMsg = convertmd5(treeMapSign,secretkey);
            System.out.println("treeMap----->"+treeMapSign);

            StringBuffer request = new StringBuffer();
            request.append("merId=" + merId +
                    "&merOrdId=" + merOrdId +
                    "&refundType=" + refundType +
                    "&refundAmt=" + refundAmt +
                    "&refundRemark=" + refundRemark +
                    "&signType=" + signType +
                    "&timeStamp=" + timeStamp +
                     "&signMsg=" + signMsg);


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


    }
    public static void inquiry()
    {
        try
        {
            Functions functions = new Functions();
            String url = "https://query.zxfgateway.com/deposit/";
            String merId = "38899130";  // New given MID
            String merOrdId ="181887";//TB2011226068377
            String timeStamp="20201122005040";
            String secretkey="7PmNDGeZTJb6wY5AsPGbND88a52p2irZ";
            String signType = "MD5";

            TreeMap treeMapSign=new TreeMap();
            if(functions.isValueNull(merId))
                treeMapSign.put("merId",merId);
            if(functions.isValueNull(merOrdId))
                treeMapSign.put("merOrdId",merOrdId);
            if(functions.isValueNull(signType))
                treeMapSign.put("signType",signType);
            if(functions.isValueNull(timeStamp))
                treeMapSign.put("timeStamp",timeStamp);


            String signMsg = convertmd5(treeMapSign,secretkey);
            System.out.println("treeMap----->"+treeMapSign);

            StringBuffer request = new StringBuffer();
            request.append("merId=" + merId +
                    "&merOrdId=" + merOrdId +
                   "&signType=" + signType +
                    "&timeStamp=" + timeStamp +
                    "&signMsg=" + signMsg);


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
    public static String convertmd5(TreeMap<String,String> treeMap,String secretKey) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        String md5 = null;
        Functions functions=new Functions();
        if (null == treeMap) return null;
        String signMsgStr="";
        for (Map.Entry<String,String> entry:treeMap.entrySet())
        {
            String key=entry.getKey();
            String value=entry.getValue();
            if(!functions.isValueNull(signMsgStr))
                signMsgStr=key+"="+value;
            else
                signMsgStr+="&"+key+"="+value;
        }
        signMsgStr+=secretKey;

        try
        {

            System.out.println("MD5 combination---"+signMsgStr);

            MessageDigest digest = MessageDigest.getInstance("MD5");

            md5 = getString(digest.digest(signMsgStr.getBytes()));

        }
        catch (NoSuchAlgorithmException e)
        {
            System.out.println("NoSuchAlgorithmException---"+e);
        }


        return md5;
    }

}

