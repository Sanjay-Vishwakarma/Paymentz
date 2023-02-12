package practice;

import com.directi.pg.Base64;
import com.directi.pg.Functions;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.romcard.RomCardUtils;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
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
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.*;

/**`
 * Created by Admin on 10/9/2020.
 */
public class ZhiXinfu_main_1
{
    public static void main(String[] args)
    {
      sale23();
    }

    public static void sale23()// same as per sample given in group
    {
        try
        {
            Functions functions = new Functions();
            String url = "https://cc.zxfgateway.com/deposit/";
            String merId = "38899130";  // New given MID
            String merOrdId = "T1915311124089";
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

            String signMsg = convertmd5(treeMapSign,secretkey);
            System.out.println("treeMap----->" + treeMapSign);
            System.out.println("signMsg---->" + signMsg);

            String request = "merId=" + merId +
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
                    "&signMsg=" + signMsg+
                    "&ccDetails=" + encodedccDetails ;



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

    public static void sale22()// same as zhxinfuMain 18/11/2020
    {
        try
        {
            Functions functions = new Functions();
            String url = "https://cc.zxfgateway.com/deposit/";
            String merId = "38899130";  // New given MID
            String merOrdId = "T158216110395";
            String version = "v3";
            String version1 = "v3XXXXX";
            String merOrdAmt = "10.00";
            String userIp = "179.12.33.0";
            String payType = "8001";
            String remark = "TestRemark";
            String respType = "01";
            String respType1 = "02";
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

            String request = "merId=" + merId +
                    "&merOrdId=" + merOrdId +
                    "&version=" + version +
                    "&merOrdAmt=" + merOrdAmt +
                    "&userIp=" + userIp +
                    "&payType=" + payType +
                    "&ccDetails=" + encodedccDetails +
                    "&remark=" + remark +
                    "&respType=" + respType +
                    "&returnUrl=" + returnUrl +
                    "&notifyUrl=" + notifyUrl +
                    "&signType=" + signType +
                    "&signMsg=" + signMsg;


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
    public static void sale20()
    {
        try
        {

            String url = "https://cc.zxfgateway.com/deposit/";
            String merId = "38899130";  // New given MID
            String merOrdId = "T1582168333300";
            String version = "v3";
            String merOrdAmt = "10.00";
            String userIp = "127.0.0.1";
            String payType = "8001";
            String remark = "TestRemark";
            String respType = "01";
            String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String notifyUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
            String signType = "MD5";
            String ccDetails="{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\":\"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            String encodedccDetails = URLEncoder.encode(URLEncoder.encode(ccDetails.toString(),"UTF-8"),"UTF-8");
            //String encodedccDetails = "%257B%2522ordCurrency%2522%253A%2522USD%2522%252C%2522cardNo%2522%253A%25225111111111111118%2522%252C%2522cardExpireMonth%2522%253A%252212%2522%252C%2522cardExpireYear%2522%253A%25222022%2522%252C%2522cardSecurityCode%2522%253A%2522359%2522%252C%2522firstName%2522%253A%2522tian%2522%252C%2522lastName%2522%253A%2522wu%2522%252C%2522email%2522%253A%2522cc%2540gmail.com%2522%252C%2522phone%2522%253A%25221389878098%2522%252C%2522country%2522%253A%2522us%2522%252C%2522city%2522%253A%2522los%2522%252C%2522address%2522%253A%2522Nofe%2BStreet%2522%252C%2522zip%2522%253A%25222876%2522%257D";
            System.out.println("ccDetails ---->" + URLDecoder.decode(encodedccDetails, "UTF-8"));
            String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version;
            String signMsg = convertmd5(signMsgstr);

            StringBuffer request = new StringBuffer();
            request.append("merId=" + merId +
                    "&merOrdId=" + merOrdId +
                    "&version=" + version +
                    "&merOrdAmt=" + merOrdAmt +
                    "&userIp=" + userIp +
                    "&payType=" + payType +
                    "&ccDetails=" + encodedccDetails +
                    "&remark=" + remark +
                    "&respType=" + respType +
                    "&returnUrl=" + returnUrl +
                    "&notifyUrl=" + notifyUrl +
                    "&signType=" + signType +
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
    public static void sale19()// UTF-8 for paramaters
    {
        try
        {

            String url = "https://cc.zxfgateway.com/deposit/";
            String merId = URLEncoder.encode("38899130", "UTF-8");// New given MID
            String merOrdId = URLEncoder.encode("T1582168333300", "UTF-8");
            String version = URLEncoder.encode("v3", "UTF-8");
            String merOrdAmt = URLEncoder.encode("10.00", "UTF-8");
            String userIp = URLEncoder.encode("127.0.0.1", "UTF-8");
            String payType = URLEncoder.encode("8001", "UTF-8");
            String remark = URLEncoder.encode("TestRemark", "UTF-8");
            String respType = URLEncoder.encode("01", "UTF-8");
            String returnUrl = URLEncoder.encode("http://localhost:8081/transaction/Common3DFrontEndServlet", "UTF-8");
            String notifyUrl = URLEncoder.encode("https://staging.paymentz.com/transaction/CommonBackEndServlet", "UTF-8");
            String signType = URLEncoder.encode("MD5", "UTF-8");
            String ccDetails = "{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\":\"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            String encodedccDetails = URLEncoder.encode(URLEncoder.encode(ccDetails.toString(), "UTF-8"), "UTF-8");
            System.out.println("ccDetails ---->" + URLDecoder.decode(URLDecoder.decode(encodedccDetails, "UTF-8")));
            String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version;
            String signMsg = convertmd5(signMsgstr);

            StringBuffer request = new StringBuffer();
            request.append("merId=" + merId +
                    "&merOrdId=" + merOrdId +
                    "&version=" + version +
                    "&merOrdAmt=" + merOrdAmt +
                    "&userIp=" + userIp +
                    "&payType=" + payType +
                    "&ccDetails=" + encodedccDetails +
                    "&remark=" + remark +
                    "&respType=" + respType +
                    "&returnUrl=" + returnUrl +
                    "&notifyUrl=" + notifyUrl +
                    "&signType=" + signType +
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
    public static void sale18()
    {
        try
        {

            String url = "https://cc.zxfgateway.com/deposit/";
            String merId = URLEncoder.encode("38899130", "UTF-8");// New given MID
            String merOrdId = "T1582168333300";
            String version = URLEncoder.encode("v3", "UTF-8");
            String merOrdAmt = URLEncoder.encode("10.00", "UTF-8");
            String userIp = URLEncoder.encode("127.0.0.1", "UTF-8");
            String payType = URLEncoder.encode("8001", "UTF-8");
            String remark = URLEncoder.encode("TestRemark", "UTF-8");
            String respType = URLEncoder.encode("01", "UTF-8");
            String returnUrl = URLEncoder.encode("http://localhost:8081/transaction/Common3DFrontEndServlet", "UTF-8");
            String notifyUrl = URLEncoder.encode("https://staging.paymentz.com/transaction/CommonBackEndServlet", "UTF-8");
            String signType = URLEncoder.encode("MD5", "UTF-8");
            String ccDetails = "{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\":\"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            String encodedccDetails = URLEncoder.encode(URLEncoder.encode(ccDetails.toString(), "UTF-8"), "UTF-8");
            System.out.println("ccDetails ---->" + URLDecoder.decode(URLDecoder.decode(encodedccDetails, "UTF-8")));
            String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version;
            String signMsg = convertmd5(signMsgstr);

            StringBuffer request = new StringBuffer();
            request.append("merId=" + merId +
                    "&merOrdId=" + merOrdId +
                    "&version=" + version +
                    "&merOrdAmt=" + merOrdAmt +
                    "&userIp=" + userIp +
                    "&payType=" + payType +
                    "&ccDetails=" + encodedccDetails +
                    "&remark=" + remark +
                    "&respType=" + respType +
                    "&returnUrl=" + returnUrl +
                    "&notifyUrl=" + notifyUrl +
                    "&signType=" + signType +
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
    public static void sale17()// Vivek main method & all md5 parameter in utf-8
    {
        try
        {
            //With UTF-8 for MD5
            String merId = (URLEncoder.encode("38899130","UTF-8"));  // New given MID
            String merOrdId = (URLEncoder.encode("T1582168333300","UTF-8"));
            String version =(URLEncoder.encode("v3XXXXX","UTF-8"));
            String merOrdAmt = (URLEncoder.encode("10.00", "UTF-8"));
            String userIp = (URLEncoder.encode("127.0.0.1","UTF-8"));
            String payType = (URLEncoder.encode("8001","UTF-8"));
            String remark = (URLEncoder.encode("TestRemark","UTF-8"));
            String respType =(URLEncoder.encode("02","UTF-8"));
            String returnUrl = (URLEncoder.encode("http://localhost:8081/transaction/Common3DFrontEndServlet","UTF-8"));
            String notifyUrl = (URLEncoder.encode("https://staging.paymentz.com/transaction/CommonBackEndServlet","UTF-8"));
            String signType = (URLEncoder.encode("MD5","UTF-8"));
          String url = "https://cc.zxfgateway.com/deposit/";
            String merId1 = "38899130";  // New given MID
            String merOrdId1 = "T1582168333300";
            String version1 = "v3";
            String merOrdAmt1 = "10.00";
            String userIp1 =  "127.0.0.1";
            String payType1 = "8001";
            String remark1 = "TestRemark";
            String respType1 = "01";
            String returnUrl1 = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String notifyUrl1 = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
            String signType1 = "MD5";

            String ccDetails="{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\":\"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            String encodedccDetails = URLEncoder.encode(URLEncoder.encode(ccDetails.toString(),"UTF-8"),"UTF-8");
            System.out.println("ccDetails ---->" + URLDecoder.decode(URLDecoder.decode(encodedccDetails, "UTF-8")));
            String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version;
            String signMsg = convertmd5(signMsgstr);
            StringBuffer request = new StringBuffer();
            request.append("merId=" + merId1 +
                    "&merOrdId=" + merOrdId1 +
                    "&version=" + version1 +
                    "&merOrdAmt=" + merOrdAmt1 +
                    "&userIp=" + userIp1 +
                    "&payType=" + payType1 +
                    "&ccDetails=" + encodedccDetails +
                    "&remark=" + remark1 +
                    "&respType=" + respType1 +
                    "&returnUrl=" + returnUrl1 +
                    "&notifyUrl=" + notifyUrl1 +
                    "&signType=" + signType1 +
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

    public static void sale16()
    {try{
        String url = "https://cc.zxfgateway.com/deposit/";
        String merId = "38899130"; // New given MID
        String merOrdId = "T1582168333301";
        String version = "v3";
        int merOrdAmt = (int) 10.00;
        String userIp = "179.12.33.0";
        String payType = "81";
        String remark = "TestRemark";
        String respType = "01";
        String returnUrl ="https://staging.paymentz.com/partner/login.jsp";
        String notifyUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
        String signType = "MD5";//this is default

        String ccDetails = "{\"ordcurrency\":\"usd\",\"cardno\":\"5111111111111118\",\"cardexpiremonth\":\"12\",\"cardexpireyear\":\"2022\",\"cardsecuritycode\":\"359\",\"firstname\":\"tian\",\"lastname\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
        System.out.println(ccDetails);
        String encodedccDetails = ESAPI.encoder().encodeForURL(ccDetails);
//System.out.println("ccDetails ---->" + encodedccDetails);
        String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version;
        String signMsg = convertmd5(signMsgstr);

        StringBuffer request = new StringBuffer();
        request.append("merId=" + merId + "&merOrdId=" + merOrdId + "&version=" + version + "&merOrdAmt=" + merOrdAmt + "&userIp=" + userIp + "&payType=" + payType + "&ccDetails=" + encodedccDetails +
                "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&notifyUrl=" + notifyUrl +
                "&signType=" + signType + "&signMsg=" + signMsg);
        System.out.println("Sale Request ---->" + request);

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
    public static void sale15()
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
            // String ccDetails = "{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\": \"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            String remark = "TestRemark";
            String respType = "01";
            String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String notifyUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
            String signType = "MD5";//this is default
            String ordCurrency="USD";
            String cardNo = "5111111111111118";
            String cardExpireMonth = "12";
            String cardExpireYear = "2022";
            String cardSecurityCode="359";
            String firstName ="tian";
            String lastName = "wu";
            String email = "cc@gmail.com";
            String phone = "1389878098";
            String country = "us";
            String city = "los";
            String address ="Nofe Street";
            String zip="2876";
            //String ccDetails = "{\"ordCurrency\":"+ordCurrency+",\"cardNo\":"+cardNo+",\"cardExpireMonth\":"+cardExpireMonth+",\"cardExpireYear\": "+cardExpireYear+",\"cardSecurityCode\":"+cardSecurityCode+",\"firstName\":"+firstName+",\"lastName\":"+lastName+",\"email\":"+email+",\"phone\":"+phone+",\"country\":"+country+",\"city\":"+city+",\"address\":"+address+",\"zip\":"+zip+"}";
            String ccDetails="{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\": \"2022\",\"cardsecuritycode\":\"359\",\"firstname\":\"tian\",\"lastname\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            //    String verificationRequest = "{\"api\":{\"version\":\"2.1.0.1.0\",\"trackid\":\"" + trackingId + "\",\"compressed\":false,\"merchantidentifier\":\"" + enrollmentRequestVO.getMid() + "\"},\"messagetype\":\"VReq\",\"message\":\"{\\\"acctNumber\\\":\\\"" + enrollmentRequestVO.getPan() + "\\\",\\\"acquirerMerchantID\\\":\\\"" + enrollmentRequestVO.getAcquirerMerchantID() + "\\\",\\\"messageType\\\":\\\"VReq\\\",\\\"messageVersion\\\":\\\"2.1.0\\\"}\"\n}";
            String ccDetails1="{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\":\"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            //String ccDetailsReq = "{\"ccDetails\":{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\":\"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}\"\n}";
            String encodedccDetails = ESAPI.encoder().encodeForURL(ccDetails1);
            System.out.println("ccDetails ---->" + encodedccDetails);
            String signMsgstr ="merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version;
            String signMsg = convertmd5(signMsgstr);

            StringBuffer request = new StringBuffer();
            request.append("merId=" + merId + "&merOrdId=" + merOrdId + "&version=" + version + "&merOrdAmt=" + merOrdAmt + "&userIp=" + userIp + "&payType=" + payType + "&ccDetails=" + encodedccDetails +
                    "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&notifyUrl=" + notifyUrl +
                    "&signType=" + signType + "&signMsg=" + signMsg);

            System.out.println("signMsg ---->" + signMsg);
            System.out.println("Sale Request ---->" + request);

            String response = doPostHTTPSURLConnectionClient(url, request.toString());

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
        catch (PZTechnicalViolationException e)
        {
            e.printStackTrace();
        }
    }
    public static void sale14() // html form request
    {
        try
        {
            String url="https://cc.zxfgateway.com/deposit/";
            String ccDetails="{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\":\"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
           String encodedccDetails = ESAPI.encoder().encodeForURL(ccDetails.toString());
            System.out.println("ccDetails ---->" + encodedccDetails);
            // String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version;

            String request ="<form action=\"https://cc.zxfgateway.com/deposit/\" class=\"payment_3d\" id=\"main-form\" method=\"post\">\n" +
                    "        <input type=\"hidden\" name=\"merId\" value=\"38899130\">\n" +
                    "        <input type=\"hidden\" name=\"merOrdId\" value=\"T1582168333300\">\n" +
                    "        <input type=\"hidden\" name=\"version\" value=\"v3\">\n" +
                    "        <input type=\"hidden\" name=\"merOrdAmt\" value=\"10\">\n" +
                    "        <input type=\"hidden\" name=\"userIp\" value=\"179.12.33.0\">\n" +
                    "        <input type=\"hidden\" name=\"payType\" value=\"8001\">\n" +
                    "        <input type=\"hidden\" name=\"ccDetails\" value=\"%7B%22ordCurrency%22%3A%22USD%22%2C%22cardNo%22%3A%225111111111111118%22%2C%22cardExpireMonth%22%3A%2212%22%2C%22cardExpireYear%22%3A%222022%22%2C%22cardSecurityCode%22%3A%22359%22%2C%22firstName%22%3A%22tian%22%2C%22lastName%22%3A%22wu%22%2C%22email%22%3A%22cc%40gmail.com%22%2C%22phone%22%3A%221389878098%22%2C%22country%22%3A%22us%22%2C%22city%22%3A%22los%22%2C%22address%22%3A%22Nofe+Street%22%2C%22zip%22%3A%222876%22%7D\">     \n" +
                    "        <input type=\"hidden\" name=\"remark\" value=\"TestRemark\">\n" +
                    "        <input type=\"hidden\" name=\"respType\" value=\"01\">\n" +
                    "        <input type=\"hidden\" name=\"returnUrl\" value=\"http://localhost:8081/transaction/Common3DFrontEndServlet\\\">\n" +
                    "        <input type=\"hidden\" name=\"notifyUrl\" value=\"https://staging.paymentz.com/transaction/CommonBackEndServlet\\\">\n" +
                    "        <input type=\"hidden\" name=\"signType\" value=\"MD5\">\n" +
                    "        <input type=\"hidden\" name=\"signMsg\" value=\"6b8acb65b4fdeb252b4433dd3ce13fd6\">\n" +
                    "        <input type=\"submit\" id=\"submit-button\" value=\"submit\" style=\"display: none;\">\n" +
                    "</form>";
          System.out.println("Sale request---->" + request);
            String response = doHttpPostConnection(url, request.toString());

            System.out.println("Sale response---->" + response);

        }

        catch (EncodingException e)
        {
            e.printStackTrace();
        }


    }

    public static void sale13() // JSON & JSONObject
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
            /*JSONObject jsonObject = new JSONObject();
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
            String encodedccDetails = ESAPI.encoder().encodeForURL(ccDetails.toString());
            System.out.println("ccDetails ---->" + encodedccDetails);
            // String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version;
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("merId=",merId);
            jsonObject1.put("&merOrdAmt=",merOrdAmt);
            jsonObject1.put("&merOrdId=",merOrdId);
            jsonObject1.put("&notifyUrl=",notifyUrl);
            jsonObject1.put("&payType=",payType);
            jsonObject1.put("&remark=",remark);
            jsonObject1.put("&respType=",respType);
            jsonObject1.put("&returnUrl=",returnUrl);
            jsonObject1.put("&signType=",signType);
            jsonObject1.put("&userIp=",userIp);
            jsonObject1.put("&version=",version);
            jsonObject1.put("&ccDetails=",encodedccDetails);

            String signMsg = convertmd5(ESAPI.encoder().encodeForURL(jsonObject1.toString()));

            StringBuffer request = new StringBuffer();
            request.append( "&ccDetails=" + encodedccDetails +
                    "&signMsg=" + signMsg);
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
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    public static void sale12() // JSONObject
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
            //   String ccDetails="{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\":\"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            JSONObject jsonObject = new JSONObject();
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
            jsonObject.put("zip","2876");
            String encodedccDetails = ESAPI.encoder().encodeForURL(jsonObject.toString());
            System.out.println("ccDetails ---->" + encodedccDetails);
           // String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version;
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("merId=",merId);
            jsonObject1.put("&merOrdAmt=",merOrdAmt);
            jsonObject1.put("&merOrdId=",merOrdId);
            jsonObject1.put("&notifyUrl=",notifyUrl);
            jsonObject1.put("&payType=",payType);
            jsonObject1.put("&remark=",remark);
            jsonObject1.put("&respType=",respType);
            jsonObject1.put("&returnUrl=",returnUrl);
            jsonObject1.put("&signType=",signType);
            jsonObject1.put("&userIp=",userIp);
            jsonObject1.put("&version=",version);

            String signMsg = convertmd5(ESAPI.encoder().encodeForURL(jsonObject1.toString()));

            StringBuffer request = new StringBuffer();
            request.append( "&ccDetails=" + encodedccDetails +
                    "&signMsg=" + signMsg);
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
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    /*  public static void sale11()// New way
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
            // String ccDetails = "{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\": \"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            String remark = "TestRemark";
            String respType = "01";
            String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String notifyUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
            String signType = "MD5";//this is default
            String ordCurrency="USD";
            String cardNo = "5111111111111118";
            String cardExpireMonth = "12";
            String cardExpireYear = "2022";
            String cardSecurityCode="359";
            String firstName ="tian";
            String lastName = "wu";
            String email = "cc@gmail.com";
            String phone = "1389878098";
            String country = "us";
            String city = "los";
            String address ="Nofe Street";
            String zip="2876";
            String sysOrdId="28760";
            String tradeStatus="completed";
            //String ccDetails = "{\"ordCurrency\":"+ordCurrency+",\"cardNo\":"+cardNo+",\"cardExpireMonth\":"+cardExpireMonth+",\"cardExpireYear\": "+cardExpireYear+",\"cardSecurityCode\":"+cardSecurityCode+",\"firstName\":"+firstName+",\"lastName\":"+lastName+",\"email\":"+email+",\"phone\":"+phone+",\"country\":"+country+",\"city\":"+city+",\"address\":"+address+",\"zip\":"+zip+"}";
            String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version;
            String signMsg = convertmd5(signMsgstr);


            String request="{\n" +
                    "  \"merId\": \"38899130\",\n" +
                    "  \"merOrdId\": \"T1582168333300\",\n" +
                    "  \"version\": \"v3\",\n" +
                    "  \"merOrdAmt\": \"10\",\n" +
                    "  \"userIp\": \"179.12.33.0\",\n" +
                    "  \"payType\": \"8001\",\n" +
                    "  \"remark\": \"TestRemark\",\n" +
                    "  \"respType\": \"01\",\n" +
                    "  \"returnUrl\": \"http://localhost:8081/transaction/Common3DFrontEndServlet\",\n" +
                    "  \"notifyUrl\": \"https://staging.paymentz.com/transaction/CommonBackEndServlet\",\n" +
                    "  \"signType\": \"MD5\",\n" +
                    "  \"signMsg\": "+signMsg+",\n" +
                    "  \"ccDetails\": \n" +
                    "    {\n" +
                    "      \"product_sku\": \"47\",\n" +
                    "      \"product_name\": \"HP LP3065\",\n" +
                    "      \"product_description\": \"\\r\\nStop your co-workers in their tracks with the stunning new 30-inc\",\n" +
                    "      \"product_price\": \"100.0000\",\n" +
                    "      \"product_url\": \"http://ts.oc-develop.com/index.php?route\\u003dproduct/product\\u0026amp;product_id\\u003d47\",\n" +
                    "      \"product_image\": \"http://ts.oc-develop.com/image/cache/catalog/demo/hp_1-500x500.jpg\"\n" +
                    "    }";
            System.out.println("ccDetails ---->" + ccDetails);
            //String ccDetailsReq = "{\"ccDetails\":{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\":\"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}\"\n}";
            String encodedccDetails = ESAPI.encoder().encodeForURL(ccDetails);
            System.out.println("ccDetails ---->" + encodedccDetails);

            //    System.out.println("ccDetails ---->" + ccDetails);
            System.out.println("Sale Request ---->" + request);

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
    }*/
    public static void sale10()// without ccDetails
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
            // String ccDetails = "{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\": \"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            String remark = "TestRemark";
            String respType = "01";
            String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String notifyUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
            String signType = "MD5";//this is default
            String ordCurrency="USD";
            String cardNo = "5111111111111118";
            String cardExpireMonth = "12";
            String cardExpireYear = "2022";
            String cardSecurityCode="359";
            String firstName ="tian";
            String lastName = "wu";
            String email = "cc@gmail.com";
            String phone = "1389878098";
            String country = "us";
            String city = "los";
            String address ="Nofe Street";
            String zip="2876";
            String sysOrdId="28760";
            String tradeStatus="completed";
            //String ccDetails = "{\"ordCurrency\":"+ordCurrency+",\"cardNo\":"+cardNo+",\"cardExpireMonth\":"+cardExpireMonth+",\"cardExpireYear\": "+cardExpireYear+",\"cardSecurityCode\":"+cardSecurityCode+",\"firstName\":"+firstName+",\"lastName\":"+lastName+",\"email\":"+email+",\"phone\":"+phone+",\"country\":"+country+",\"city\":"+city+",\"address\":"+address+",\"zip\":"+zip+"}";
            String ccDetails = "{"
                    + "\"ordCurrency\": " + ordCurrency + ","
                    + "\"cardNo\": " + cardNo + ","
                    + "\"cardExpireMonth\": "+cardExpireMonth+","
                    + "\"cardExpireYear\": "+cardExpireYear+","
                    + "\"cardSecurityCode\": "+cardSecurityCode+","
                    + "\"firstName\": "+firstName+","
                    + "\"lastName\": "+lastName+","
                    + "\"email\": "+email+","
                    + "\"phone\": "+phone+","
                    + "\"country\": "+country+","
                    + "\"city\": "+city+","
                    + "\"address\": "+address+","
                    + "\"zip\": "+zip+","
                    +"}";
            System.out.println("ccDetails ---->" + ccDetails);
            //String ccDetailsReq = "{\"ccDetails\":{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\":\"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}\"\n}";
            String encodedccDetails = ESAPI.encoder().encodeForURL(ccDetails);
            System.out.println("ccDetails ---->" + encodedccDetails);
            String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version;
            String signMsg = convertmd5(signMsgstr);

            StringBuffer request = new StringBuffer();
            request.append("merId=" + merId + "&merOrdId=" + merOrdId + "&version=" + version + "&merOrdAmt=" + merOrdAmt  + "&sysOrdId=" + sysOrdId +
                    "&tradeStatus=" + tradeStatus + "&remark=" + remark + "&returnUrl=" + returnUrl + "&notifyUrl=" + notifyUrl +
                    "&signType=" + signType + "&signMsg=" + signMsg);
            //    System.out.println("ccDetails ---->" + ccDetails);
            System.out.println("Sale Request ---->" + request);

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
    public static void sale9()// same as zhixinfu_main.java  //same as there working string
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
            // String ccDetails = "{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\": \"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            String remark = "TestRemark";
            String respType = "01";
            String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String notifyUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
            String signType = "MD5";//this is default
            String ordCurrency="USD";
            String cardNo = "5111111111111118";
            String cardExpireMonth = "12";
            String cardExpireYear = "2022";
            String cardSecurityCode="359";
            String firstName ="tian";
            String lastName = "wu";
            String email = "cc@gmail.com";
            String phone = "1389878098";
            String country = "us";
            String city = "los";
            String address ="Nofe Street";
            String zip="2876";
            //String ccDetails = "{\"ordCurrency\":"+ordCurrency+",\"cardNo\":"+cardNo+",\"cardExpireMonth\":"+cardExpireMonth+",\"cardExpireYear\": "+cardExpireYear+",\"cardSecurityCode\":"+cardSecurityCode+",\"firstName\":"+firstName+",\"lastName\":"+lastName+",\"email\":"+email+",\"phone\":"+phone+",\"country\":"+country+",\"city\":"+city+",\"address\":"+address+",\"zip\":"+zip+"}";
            String ccDetails = "{"
                    + "\"ordCurrency\": " + ordCurrency + ","
                    + "\"cardNo\": " + cardNo + ","
                    + "\"cardExpireMonth\": "+cardExpireMonth+","
                    + "\"cardExpireYear\": "+cardExpireYear+","
                    + "\"cardSecurityCode\": "+cardSecurityCode+","
                    + "\"firstName\": "+firstName+","
                    + "\"lastName\": "+lastName+","
                    + "\"email\": "+email+","
                    + "\"phone\": "+phone+","
                    + "\"country\": "+country+","
                    + "\"city\": "+city+","
                    + "\"address\": "+address+","
                    + "\"zip\": "+zip+","
                  +"}";
            System.out.println("ccDetails ---->" + ccDetails);
            //String ccDetailsReq = "{\"ccDetails\":{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\":\"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}\"\n}";
            String encodedccDetails = ESAPI.encoder().encodeForURL(ccDetails);
            System.out.println("ccDetails ---->" + encodedccDetails);
            String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version;
            String signMsg = convertmd5(signMsgstr);

            /*StringBuffer request = new StringBuffer();
            request.append("merId=" + merId + "&merOrdId=" + merOrdId + "&version=" + version + "&merOrdAmt=" + merOrdAmt + "&userIp=" + userIp + "&payType=" + payType + "&ccDetails=" + encodedccDetails +
                    "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&notifyUrl=" + notifyUrl +
                    "&signType=" + signType + "&signMsg=" + signMsg);*/
            String request ="merId=38899130&merOrdId=T1582168333300&version=v3&merOrdAmt=10&userIp=179.12.33.0&payType=8001&ccDetails=%7B%22ordCurrency%22%3A%22USD%22%2C%22cardNo%22%3A%225111111111111118%22%2C%22cardExpireMonth%22%3A%2212%22%2C%22cardExpireYear%22%3A%222022%22%2C%22cardSecurityCode%22%3A%22359%22%2C%22firstName%22%3A%22tian%22%2C%22lastName%22%3A%22wu%22%2C%22email%22%3A%22cc%40gmail.com%22%2C%22phone%22%3A%221389878098%22%2C%22country%22%3A%22us%22%2C%22city%22%3A%22los%22%2C%22address%22%3A%22Nofe+Street%22%2C%22zip%22%3A%222876%22%7D&remark=TestRemark&respType=01&returnUrl=http://localhost:8081/transaction/Common3DFrontEndServlet&notifyUrl=https://staging.paymentz.com/transaction/CommonBackEndServlet&signType=MD5&signMsg=b112996e7823e7ac9fabb7027b4fa594";
            //    System.out.println("ccDetails ---->" + ccDetails);
            System.out.println("Sale Request ---->" + request);

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
    public static void sale8()// using hashmap
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
            // String ccDetails = "{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\": \"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            String remark = "TestRemark";
            String respType = "01";
            String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String notifyUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
            String signType = "MD5";//this is default
            String ordCurrency="USD";
            String cardNo = "5111111111111118";
            String cardExpireMonth = "12";
            String cardExpireYear = "2022";
            String cardSecurityCode="359";
            String firstName ="tian";
            String lastName = "wu";
            String email = "cc@gmail.com";
            String phone = "1389878098";
            String country = "us";
            String city = "los";
            String address ="Nofe Street";
            String zip="2876";
            //String ccDetails = "{\"ordCurrency\":"+ordCurrency+",\"cardNo\":"+cardNo+",\"cardExpireMonth\":"+cardExpireMonth+",\"cardExpireYear\": "+cardExpireYear+",\"cardSecurityCode\":"+cardSecurityCode+",\"firstName\":"+firstName+",\"lastName\":"+lastName+",\"email\":"+email+",\"phone\":"+phone+",\"country\":"+country+",\"city\":"+city+",\"address\":"+address+",\"zip\":"+zip+"}";
//            String ccDetails="{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\": \"2022\",\"cardsecuritycode\":\"359\",\"firstname\":\"tian\",\"lastname\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            //    String verificationRequest = "{\"api\":{\"version\":\"2.1.0.1.0\",\"trackid\":\"" + trackingId + "\",\"compressed\":false,\"merchantidentifier\":\"" + enrollmentRequestVO.getMid() + "\"},\"messagetype\":\"VReq\",\"message\":\"{\\\"acctNumber\\\":\\\"" + enrollmentRequestVO.getPan() + "\\\",\\\"acquirerMerchantID\\\":\\\"" + enrollmentRequestVO.getAcquirerMerchantID() + "\\\",\\\"messageType\\\":\\\"VReq\\\",\\\"messageVersion\\\":\\\"2.1.0\\\"}\"\n}";
            HashMap<String, String> ccDetails = new HashMap<String, String>();
            ccDetails.put("ordCurrency","USD");
            ccDetails.put("cardNo","5111111111111118");
            ccDetails.put("cardExpireMonth","12");
            ccDetails.put("cardExpireYear","2022");
            ccDetails.put("cardsecuritycode","359");
            ccDetails.put("firstname","tian");
            ccDetails.put("lastname","wu");
            ccDetails.put("email","cc@gmail.com");
            ccDetails.put("phone","1389878098");
            ccDetails.put("country","us");
            ccDetails.put("city","los");
            ccDetails.put("address","Nofe Street");
            ccDetails.put("zip","2876");
            System.out.println("ccDetails hashmap->"+ccDetails);

            //String ccDetailsReq = "{\"ccDetails\":{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\":\"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}\"\n}";
            String encodedccDetails = ESAPI.encoder().encodeForURL(String.valueOf(ccDetails));
            System.out.println("ccDetails ---->" + encodedccDetails);
            String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version;
            String signMsg = convertmd5(signMsgstr);

            StringBuffer request = new StringBuffer();
            request.append("merId=" + merId + "&merOrdId=" + merOrdId + "&version=" + version + "&merOrdAmt=" + merOrdAmt + "&userIp=" + userIp + "&payType=" + payType + "&ccDetails=" + encodedccDetails +
                    "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&notifyUrl=" + notifyUrl +
                    "&signType=" + signType + "&signMsg=" + signMsg);
            //    System.out.println("ccDetails ---->" + ccDetails);
            System.out.println("Sale Request ---->" + request);

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


    public static void sale7()// same as zhixinfu_main.java
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
            // String ccDetails = "{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\": \"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            String remark = "TestRemark";
            String respType = "01";
            String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String notifyUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
            String signType = "MD5";//this is default
            String ordCurrency="USD";
            String cardNo = "5111111111111118";
            String cardExpireMonth = "12";
            String cardExpireYear = "2022";
            String cardSecurityCode="359";
            String firstName ="tian";
            String lastName = "wu";
            String email = "cc@gmail.com";
            String phone = "1389878098";
            String country = "us";
            String city = "los";
            String address ="Nofe Street";
            String zip="2876";
            //String ccDetails = "{\"ordCurrency\":"+ordCurrency+",\"cardNo\":"+cardNo+",\"cardExpireMonth\":"+cardExpireMonth+",\"cardExpireYear\": "+cardExpireYear+",\"cardSecurityCode\":"+cardSecurityCode+",\"firstName\":"+firstName+",\"lastName\":"+lastName+",\"email\":"+email+",\"phone\":"+phone+",\"country\":"+country+",\"city\":"+city+",\"address\":"+address+",\"zip\":"+zip+"}";
              String ccDetails="{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\": \"2022\",\"cardsecuritycode\":\"359\",\"firstname\":\"tian\",\"lastname\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
        //    String verificationRequest = "{\"api\":{\"version\":\"2.1.0.1.0\",\"trackid\":\"" + trackingId + "\",\"compressed\":false,\"merchantidentifier\":\"" + enrollmentRequestVO.getMid() + "\"},\"messagetype\":\"VReq\",\"message\":\"{\\\"acctNumber\\\":\\\"" + enrollmentRequestVO.getPan() + "\\\",\\\"acquirerMerchantID\\\":\\\"" + enrollmentRequestVO.getAcquirerMerchantID() + "\\\",\\\"messageType\\\":\\\"VReq\\\",\\\"messageVersion\\\":\\\"2.1.0\\\"}\"\n}";

            //String ccDetailsReq = "{\"ccDetails\":{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\":\"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}\"\n}";
            String encodedccDetails = ESAPI.encoder().encodeForURL(ccDetails);
            System.out.println("ccDetails ---->" + encodedccDetails);
            String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version;
            String signMsg = convertmd5(signMsgstr);

            StringBuffer request = new StringBuffer();
            request.append("merId=" + merId + "&merOrdId=" + merOrdId + "&version=" + version + "&merOrdAmt=" + merOrdAmt + "&userIp=" + userIp + "&payType=" + payType + "&ccDetails=" + encodedccDetails +
                    "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&notifyUrl=" + notifyUrl +
                    "&signType=" + signType + "&signMsg=" + signMsg);
            //    System.out.println("ccDetails ---->" + ccDetails);
            System.out.println("Sale Request ---->" + request);

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
    public static void sale6()  //Done with namrata
    {
        try{
        String url = "https://cc.zxfgateway.com/deposit/";
        String merId = "38899130"; // New given MID
        String merOrdId = "T1582168333300"; //T1582168333300
        String version = "v3";
        int merOrdAmt = (int) 10.00;
        String userIp = "179.1.3.0";
        String payType = "10";
        String remark = "TestRemark";
        String respType = "02";
        String returnUrl ="http://localhost:8081/transaction/Common3DFrontEndServlet";
        String notifyUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
        //String returnUrl ="http://www.baidu.com/returnUrl.php";
        //String notifyUrl ="http://www.baidu.com/notifyUrl.php";
        String signType = "MD5";//this is default
        String ccDetails = "{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\":\"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";

       // System.out.println(ccDetails);
        String encodedccDetails = ESAPI.encoder().encodeForURL(ccDetails);
        System.out.println("ccDetails ---->" + encodedccDetails);
        String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version;
        String signMsg = convertmd5(signMsgstr);

       StringBuffer request = new StringBuffer();
        request.append("merId=" + merId + "&merOrdId=" + merOrdId + "&version=" + version + "&merOrdAmt=" + merOrdAmt + "&userIp=" + userIp + "&payType=" + payType + "&ccDetails=" + encodedccDetails +
                "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&notifyUrl=" + notifyUrl +
                "&signType=" + signType + "&signMsg=" + signMsg);

            // request in ascending order

           /* StringBuffer request = new StringBuffer();
            request.append( "ccDetails=" + encodedccDetails+"&merId=" + merId + "&merOrdAmt=" + merOrdAmt+ "&merOrdId=" + merOrdId+ "&notifyUrl=" + notifyUrl + "&payType=" + payType +
                    "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl  + "&signMsg=" + signMsg+
                    "&signType=" + signType  + "&userIp=" + userIp + "&version=" + version  );*/

        System.out.println("Sale Request ---->" + request);

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
    public static void sale5()
    {
        try{
        String url = "https://cc.zxfgateway.com/deposit/";
        String merId = "38899130";  // New given MID
        String merOrdId = "T1582168333300";
        String version = "v3";
        String merOrdAmt = "10.00";
        String userIp = "179.12.33.0";
        String payType = "11";
        String ordCurrency = "";
        String cardNo = "";
        String cardExpireMonth = "";
        String cardExpireYear = "";
        String cardSecurityCode ="";
        String firstName = "";
        String lastName = "";
        String email = "";
        String phone = "";
        String country = "";
        String city = "";
        String address ="";
        String zip ="";
        String remark = "TestRemark";
        String respType = "01";
        String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
        String notifyUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
        String signType = "MD5";//this is default
        String ccDetails="{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\":\"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
        //String ccDetails="{\"address\":\"Nofe\",\"cardExpireYear\":\"2022\",\"cardExpireMonth\":\"12\",\"cardSecurityCode\":\"359\",\"cardNo\":\"4444333322221111\",\"city\":\"los\",\"country\":\"us\",\"email\":\"c@gmail.com\",\"firstName\":\"ian\",\"lastName\":\"bell\",\"ordCurrency\":\"USD\",\"phone\":\"0999878098\",\"zip\":\"2806\"}";
        //String ccDetails="{\"address\":\"Nofe\",\"cardExpireYear\":\"2022\",\"cardExpireMonth\":\"12\",\"cardSecurityCode\":\"359\",\"cardNo\":\"4444333322221111\",\"city\":\"los\",\"country\":\"us\",\"email\":\"c@gmail.com\",\"firstName\":\"ian\",\"lastName\":\"bell\",\"ordCurrency\":\"USD\",\"phone\":\"0999878098\",\"zip\":\"2806\"}";
        String encodedccDetails=ESAPI.encoder().encodeForURL(ccDetails);
        System.out.println("EncodedccDetails ---->" + encodedccDetails);
        String signMsgstr= "merId=" + merId + "&merOrdId=" + merOrdId + "&version=" + version + "&merOrdAmt=" + merOrdAmt + "&userIp=" + userIp + "&payType=" + payType + "&remark=" + remark + "&respType=" + respType +  "&returnUrl="+ returnUrl + "&notifyUrl=" +notifyUrl + "&signType=" + signType ;
        String signMsg = convertmd5(signMsgstr);
/*        StringBuffer request = new StringBuffer();
            request.append("merId=" + merId + "&merOrdId=" + merOrdId + "&merOrdAmt=" + merOrdAmt + "&notifyUrl=" + notifyUrl + "&payType=" + payType +"&ccDetails=" + encodedccDetails +
                    "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl +
                    "&signType=" + signType + "&userIp=" + userIp + "&version=" + version +  "&signMsg=" + signMsg);*/
            String jsonRequest="{\"merId\":" + merId + ",\"merOrdId\":" + merOrdId + ",\"merOrdAmt\":" + merOrdAmt + ",\"notifyUrl\": " + notifyUrl + ",\"payType\":" + payType + ",\"ccDetails\":" + encodedccDetails + ",\"remark\":" + remark + ",\"respType\":" + respType + ",\"returnUrl\":" + returnUrl + ",\"signType\":" + signType + ",\"userIp\":" + userIp + ",\"version\":" + version + ",\"signMsg\":" + signMsg+"}";

            System.out.println("Sale Request ---->" + jsonRequest);

            String response = doHttpPostConnection(url, jsonRequest.toString());

            System.out.println("Sale response---->" + response);

        }
        catch (EncodingException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }

    public static void sale4()
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
            String ordCurrency = URLEncoder.encode("USD", "UTF-8");
            String cardNo = URLEncoder.encode("5111111111111118", "UTF-8");
            String cardExpireMonth = URLEncoder.encode("12", "UTF-8");
            String cardExpireYear = URLEncoder.encode("2022", "UTF-8");
            String cardSecurityCode = URLEncoder.encode("359", "UTF-8");
            String firstName = URLEncoder.encode("tian", "UTF-8");
            String lastName = URLEncoder.encode("wu", "UTF-8");
            String email = URLEncoder.encode("cc@gmail.com", "UTF-8");
            String phone = URLEncoder.encode("1389878098", "UTF-8");
            String country = URLEncoder.encode("us", "UTF-8");
            String city = URLEncoder.encode("los", "UTF-8");
            String address = URLEncoder.encode("Nofe Street", "UTF-8");
            String zip = URLEncoder.encode("2876", "UTF-8");
            String remark = "TestRemark";
            String respType = "01";
            String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String notifyUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
            String signType = "MD5";//this is default

            String ccDetails = "{\"ordCurrency\":" + ordCurrency + ",\"cardNo\":" + cardNo + ",\"cardExpireMonth\":" + cardExpireMonth + ",\"cardExpireYear\": " + cardExpireYear + ",\"cardSecurityCode\":" + cardSecurityCode + ",\"firstName\":" + firstName + ",\"lastName\":" + lastName + ",\"email\":" + email + ",\"phone\":" + phone + ",\"country\":" + country + ",\"city\":" + city + ",\"address\":" + address + ",\"zip\":" + zip + "}";
            //  String ccDetails ="{" +" \"ccDetails\":" "{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\": \"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
        /*   String ccDetails="{\n" +
                   "   \"ccDetails\":{\n" +
                   "      \"ordCurrency\":\"USD\",\n" +
                   "      \"cardNo\":\"5111111111111118\",\n" +
                   "      \"cardExpireMonth\":\"12\",\n" +
                   "      \"cardExpireYear\":\"2022\",\n" +
                   "      \"cardSecurityCode\":\"359\",\n" +
                   "      \"firstName\":\"tian\",\n" +
                   "      \"lastName\":\"wu\",\n" +
                   "      \"email\":\"cc@gmail.com\",\n" +
                   "      \"phone\":\"1389878098\",\n" +
                   "      \"country\":\"us\",\n" +
                   "      \"city\":\"los\",\n" +
                   "      \"address\":\"Nofe Street\",\n" +
                   "      \"zip\":\"2876\"\n" +
                   "   }\n" +
                   "}";*/
        //    String ccDetails = "{\"ordCurrency\":\"USD\",\n" + "\"cardNo\":\"5111111111111118\",\n" + "\"cardExpireMonth\":\"12\",\n" + "\"cardExpireYear\":\"2022\",\n" + "\"cardSecurityCode\":\"359\",\n" + "\"firstName\":\"tian\",\n" + "\"lastName\":\"wu\",\n" + "\"email\":\"cc@gmail.com\",\n" + "\"phone\":\"1389878098\",\n" + "\"country\":\"us\",\n" + "\"city\":\"los\",\n" + "\"address\":\"Nofe Street\",\n" + "\"zip\":\"2876\"\n" + "}\n" + "}";
                   /*     StringBuffer request1 = new StringBuffer();
            request1.append("{" +
                    "\"ordCurrency\":\"USD\"," +
                    "\"cardNo\":\"5111111111111118\"," +
                    "\"cardExpireMonth\":\"12\"," +
                    "\"cardExpireYear\":\"2022\"," +
                    "\"cardSecurityCode\":\"359\"," +
                    "\"firstName\":\"tian\"," +
                    "\"lastName\":\"wu\"," +
                    "\"email\":\"cc@gmail.com\"," +
                    "\"phone\":\"1389878098\"," +
                    "\"country\":\"us\"," +
                    "\"city\":\"los\"," +
                    "\"address\":\"Nofe Street\"," +
                    "\"zip\":\"2876\" " +
                    "}");*/

            String encodedccDetails = ESAPI.encoder().encodeForURL(ccDetails);

            System.out.println("ccDetails ---->" + ccDetails);
            String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&ccDetails=" + encodedccDetails + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version;
            String signMsg = convertmd5(signMsgstr);

            StringBuffer request = new StringBuffer();
            request.append("merId=" + merId + "&merOrdId=" + merOrdId + "&merOrdAmt=" + merOrdAmt + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&ccDetails=" + encodedccDetails +
                    "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl +
                    "&signType=" + signType + "&userIp=" + userIp + "&version=" + version + "&signMsg=" + signMsg);
            //    System.out.println("ccDetails ---->" + ccDetails);
            System.out.println("Sale Request ---->" + request);

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
    public static void sale3()
    {
        try
        {

            String url = "https://cc.zxfgateway.com/deposit/";
            String merId = "38899130";  // New given MID
            String merOrdId = "T1582168333300";
            String version = "v3XXXXX";
            int merOrdAmt = (int) 10.00;
            String userIp = "179.12.33.0";
            String payType = "8001";
            String ordCurrency = URLEncoder.encode("USD", "UTF-8");
            String cardNo =URLEncoder.encode("5111111111111118", "UTF-8");
            String cardExpireMonth = URLEncoder.encode("12", "UTF-8");
            String cardExpireYear = URLEncoder.encode("2022", "UTF-8");
            String cardSecurityCode =URLEncoder.encode("359", "UTF-8");
            String firstName = URLEncoder.encode("tian", "UTF-8");
            String lastName = URLEncoder.encode("wu", "UTF-8");
            String email = URLEncoder.encode("cc@gmail.com", "UTF-8");
            String phone = URLEncoder.encode("1389878098", "UTF-8");
            String country = URLEncoder.encode("us", "UTF-8");
            String city = URLEncoder.encode("los", "UTF-8");
            String address = URLEncoder.encode("Nofe Street", "UTF-8");
            String zip = URLEncoder.encode("2876", "UTF-8");
            // String ccDetails = "{\"ordCurrency\":" + ordCurrency + ",\"cardNo\":" + cardNo + ",\"cardExpireMonth\":" + cardExpireMonth + ",\"cardExpireYear\": " + cardExpireYear + ",\"cardSecurityCode\":" + cardSecurityCode + ",\"firstName\":" + firstName + ",\"lastName\":" + lastName + ",\"email\":" + email + ",\"phone\":" + phone + ",\"country\":" + country + ",\"city\":" + city + ",\"address\":" + address + ",\"zip\":" + zip + "}";
            //  String ccDetails ="{" +" \"ccDetails\":" "{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\": \"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
           String ccDetails="{\"ccDetails\":{\n" +
                   "      \"ordCurrency\":\"USD\",\n" +
                   "      \"cardNo\":\"5111111111111118\",\n" +
                   "      \"cardExpireMonth\":\"12\",\n" +
                   "      \"cardExpireYear\":\"2022\",\n" +
                   "      \"cardSecurityCode\":\"359\",\n" +
                   "      \"firstName\":\"tian\",\n" +
                   "      \"lastName\":\"wu\",\n" +
                   "      \"email\":\"cc@gmail.com\",\n" +
                   "      \"phone\":\"1389878098\",\n" +
                   "      \"country\":\"us\",\n" +
                   "      \"city\":\"los\",\n" +
                   "      \"address\":\"Nofe Street\",\n" +
                   "      \"zip\":\"2876\"\n" +
                   "   }\n" +
                   "}";
          //  String ccDetails="{\"ordCurrency\":\"USD\",\n" +"\"cardNo\":\"5111111111111118\",\n" +"\"cardExpireMonth\":\"12\",\n" +"\"cardExpireYear\":\"2022\",\n" +"\"cardSecurityCode\":\"359\",\n" +"\"firstName\":\"tian\",\n" +"\"lastName\":\"wu\",\n" +"\"email\":\"cc@gmail.com\",\n" +"\"phone\":\"1389878098\",\n" +"\"country\":\"us\",\n" +"\"city\":\"los\",\n" +"\"address\":\"Nofe Street\",\n" +"\"zip\":\"2876\"\n" +"}\n" +"}";
            String remark = "TestRemark";
            String respType = "01";
            String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String notifyUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
            String signType = "MD5";//this is default

            String encodedccDetails = ESAPI.encoder().encodeForURL(ccDetails);

            System.out.println("ccDetails ---->" + ccDetails);
            String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&ccDetails=" + encodedccDetails + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version ;
            String signMsg = convertmd5(signMsgstr);

            StringBuffer request = new StringBuffer();
            request.append("merId=" + merId + "&merOrdId=" + merOrdId + "&merOrdAmt=" + merOrdAmt + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&ccDetails=" +encodedccDetails+
                    "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl +
                    "&signType=" + signType + "&userIp=" + userIp + "&version=" + version + "&signMsg=" + signMsg );
            //    System.out.println("ccDetails ---->" + ccDetails);
            System.out.println("Sale Request ---->" + request);

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
    public static void sale2()
    {
        try
        {

            String url = "https://cc.zxfgateway.com/deposit/";
            String merId = "38899130";  // New given MID
            String merOrdId = "T1582168333300";
            String version = "v3XXXXX";
            int merOrdAmt = 10;
            String userIp = "179.12.33.0";
            String payType = "8001";
            String ordCurrency = URLEncoder.encode("USD", "UTF-8");
            String cardNo =URLEncoder.encode("5111111111111118", "UTF-8");
            String cardExpireMonth = URLEncoder.encode("12", "UTF-8");
            String cardExpireYear = URLEncoder.encode("2022", "UTF-8");
            String cardSecurityCode =URLEncoder.encode("359", "UTF-8");
            String firstName = URLEncoder.encode("tian", "UTF-8");
            String lastName = URLEncoder.encode("wu", "UTF-8");
            String email = URLEncoder.encode("cc@gmail.com", "UTF-8");
            String phone = URLEncoder.encode("1389878098", "UTF-8");
            String country = URLEncoder.encode("us", "UTF-8");
            String city = URLEncoder.encode("los", "UTF-8");
            String address = URLEncoder.encode("Nofe Street", "UTF-8");
            String zip = URLEncoder.encode("2876", "UTF-8");
            // String ccDetails = "{\"ordCurrency\":" + ordCurrency + ",\"cardNo\":" + cardNo + ",\"cardExpireMonth\":" + cardExpireMonth + ",\"cardExpireYear\": " + cardExpireYear + ",\"cardSecurityCode\":" + cardSecurityCode + ",\"firstName\":" + firstName + ",\"lastName\":" + lastName + ",\"email\":" + email + ",\"phone\":" + phone + ",\"country\":" + country + ",\"city\":" + city + ",\"address\":" + address + ",\"zip\":" + zip + "}";
            //  String ccDetails ="{" +" \"ccDetails\":" "{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\": \"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            //String ccDetails="{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\":\"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            String ccDetails="{\"ordCurrency\":\"USD\",\n" +"\"cardNo\":\"5111111111111118\",\n" +"\"cardExpireMonth\":\"12\",\n" +"\"cardExpireYear\":\"2022\",\n" +"\"cardSecurityCode\":\"359\",\n" +"\"firstName\":\"tian\",\n" +"\"lastName\":\"wu\",\n" +"\"email\":\"cc@gmail.com\",\n" +"\"phone\":\"1389878098\",\n" +"\"country\":\"us\",\n" +"\"city\":\"los\",\n" +"\"address\":\"Nofe Street\",\n" +"\"zip\":\"2876\"\n" +"}\n" +"}";
            String remark = "TestRemark";
            String respType = "01";
            String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String notifyUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
            String signType = "MD5";//this is default

            String encodedccDetails = ESAPI.encoder().encodeForURL(ccDetails);

            System.out.println("ccDetails ---->" + ccDetails);
            String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&ccDetails=" + encodedccDetails + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version ;
            String signMsg = convertmd5Test(signMsgstr);

            StringBuffer request = new StringBuffer();
            request.append("merId=" + merId + "&merOrdId=" + merOrdId + "&merOrdAmt=" + merOrdAmt + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&ccDetails=" +encodedccDetails+
                    "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl +
                    "&signType=" + signType + "&userIp=" + userIp + "&version=" + version + "&signMsg=" + signMsg );
            //    System.out.println("ccDetails ---->" + ccDetails);
            System.out.println("Sale Request ---->" + request);

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
    public static void sale1()
    {
        try
        {

            String url = "https://cc.zxfgateway.com/deposit/";
            String merId = "38899130";  // New given MID
            String merOrdId = "T1582168333300";
            String version = "v3XXXXX";
            int merOrdAmt = 10;
            String userIp = "179.12.33.0";
            String payType = "8001";
            String ordCurrency = URLEncoder.encode("USD", "UTF-8");
            String cardNo =URLEncoder.encode("5111111111111118", "UTF-8");
            String cardExpireMonth = URLEncoder.encode("12", "UTF-8");
            String cardExpireYear = URLEncoder.encode("2022", "UTF-8");
            String cardSecurityCode =URLEncoder.encode("359", "UTF-8");
            String firstName = URLEncoder.encode("tian", "UTF-8");
            String lastName = URLEncoder.encode("wu", "UTF-8");
            String email = URLEncoder.encode("cc@gmail.com", "UTF-8");
            String phone = URLEncoder.encode("1389878098", "UTF-8");
            String country = URLEncoder.encode("us", "UTF-8");
            String city = URLEncoder.encode("los", "UTF-8");
            String address = URLEncoder.encode("Nofe Street", "UTF-8");
            String zip = URLEncoder.encode("2876", "UTF-8");
            // String ccDetails = "{\"ordCurrency\":" + ordCurrency + ",\"cardNo\":" + cardNo + ",\"cardExpireMonth\":" + cardExpireMonth + ",\"cardExpireYear\": " + cardExpireYear + ",\"cardSecurityCode\":" + cardSecurityCode + ",\"firstName\":" + firstName + ",\"lastName\":" + lastName + ",\"email\":" + email + ",\"phone\":" + phone + ",\"country\":" + country + ",\"city\":" + city + ",\"address\":" + address + ",\"zip\":" + zip + "}";
            //  String ccDetails ="{" +" \"ccDetails\":" "{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\": \"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            //String ccDetails="{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\":\"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            String ccDetails="{\"ordCurrency\":\"USD\",\n" +"\"cardNo\":\"5111111111111118\",\n" +"\"cardExpireMonth\":\"12\",\n" +"\"cardExpireYear\":\"2022\",\n" +"\"cardSecurityCode\":\"359\",\n" +"\"firstName\":\"tian\",\n" +"\"lastName\":\"wu\",\n" +"\"email\":\"cc@gmail.com\",\n" +"\"phone\":\"1389878098\",\n" +"\"country\":\"us\",\n" +"\"city\":\"los\",\n" +"\"address\":\"Nofe Street\",\n" +"\"zip\":\"2876\"\n" +"}\n" +"}";
            String remark = "TestRemark";
            String respType = "01";
            String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String notifyUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
            String signType = "MD5";//this is default

           // String encodedccDetails = ESAPI.encoder().encodeForURL(ccDetails);
            String encodedccDetails = convertmd5Test(ccDetails);

            System.out.println("ccDetails ---->" + ccDetails);
            String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&ccDetails=" + encodedccDetails + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version ;
            String signMsg = convertmd5(signMsgstr);

            StringBuffer request = new StringBuffer();
            request.append("merId=" + merId + "&merOrdId=" + merOrdId + "&merOrdAmt=" + merOrdAmt + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&ccDetails=" +encodedccDetails+
                    "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl +
                    "&signType=" + signType + "&userIp=" + userIp + "&version=" + version + "&signMsg=" + signMsg );
            //    System.out.println("ccDetails ---->" + ccDetails);
            System.out.println("Sale Request ---->" + request);

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
       /* catch (EncodingException e)
        {
            e.printStackTrace();
        }*/
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
            String payType = "91";
            // String ccDetails = "{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\": \"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            String remark = "TestRemark";
            String respType = "01";
            String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String notifyUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
            String signType = "MD5";//this is default
            String ordCurrency="USD";
            String cardNo = "5111111111111118";
            String cardExpireMonth = "12";
            String cardExpireYear = "2022";
            String cardSecurityCode="359";
            String firstName ="tian";
            String lastName = "wu";
            String email = "cc@gmail.com";
            String phone = "1389878098";
            String country = "us";
            String city = "los";
            String address ="Nofe Street";
            String zip="2876";
            //String ccDetails = "{\"ordCurrency\":"+ordCurrency+",\"cardNo\":"+cardNo+",\"cardExpireMonth\":"+cardExpireMonth+",\"cardExpireYear\": "+cardExpireYear+",\"cardSecurityCode\":"+cardSecurityCode+",\"firstName\":"+firstName+",\"lastName\":"+lastName+",\"email\":"+email+",\"phone\":"+phone+",\"country\":"+country+",\"city\":"+city+",\"address\":"+address+",\"zip\":"+zip+"}";
            //String ccDetails="{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\": \"2022\",\"cardsecuritycode\":\"359\",\"firstname\":\"tian\",\"lastname\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            String ccDetails = "{" +
                    "\"ordCurrency\":\"" + ordCurrency + "\"," +
                    "\"cardNo\":\"" + cardNo + "\"," +
                    "\"cardExpireMonth\":\"" + cardExpireMonth + "\"," +
                    "\"cardExpireYear\":\"" + cardExpireYear + "\"," +
                    "\"cardSecurityCode\":\"" + cardSecurityCode + "\"," +
                    "\"cardSecurityCode\":\"" + cardSecurityCode + "\"," +
                    "\"firstName\":\""+firstName+"\"," +
                    "\"lastName\":\""+lastName+"\"," +
                    "\"email\":\"" + email + "\"," +
                    "\"phone\":\"" + phone + "\"," +
                    "\"country\":\"" + country + "\"," +
                    "\"city\":\"" + city + "\"," +
                    "\"address\":\"" + address + "\"," +
                    "\"zip\":\"" + zip + "\"" +
                    "}";

            String encodedccDetails = ESAPI.encoder().encodeForURL(ccDetails);
            System.out.println("ccDetails ---->" + encodedccDetails);
            String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version;
            String signMsg = convertmd5(signMsgstr);


            StringBuffer request = new StringBuffer();
            request.append("merId=" + merId + "&merOrdId=" + merOrdId + "&merOrdAmt=" + merOrdAmt + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&ccDetails=" +encodedccDetails+
                    "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl +
                    "&signType=" + signType + "&userIp=" + userIp + "&version=" + version + "&signMsg=" + signMsg );
            //    System.out.println("ccDetails ---->" + ccDetails);
            System.out.println("Sale Request ---->" + request);

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
    public static void romcard()
    {


        try{
            String url = "https://www.activare3dsecure.ro/teste3d/cgi-bin/";
            String encryptionKey="2B026FF3B2FB0B532D935165ACD54A6F";

            String trackingid = "3214987";

            String amount="1.00";
            String currency="RON";
            String order=trackingid;
            String desc="Test order";
            String merch_name="ALTEX ROMANIA SRL";
            String merch_url="https://www.altex.ro";
            String terminal="10627001";
            String merchant="0000000"+terminal;
            String email="alexandru.termegan@altex.ro";
            String trtype="0";
            String country="";
            String merch_gmt="";
            String timpstamp =  RomCardUtils.currentTimeToGMT();
            String nonce= Functions.convertmd5(new Long(new Date().getTime()).toString());
            String backref="http://localhost:8081/transaction/Common3DFrontEndServlet";


            String hmac_request =amount.length()+amount
                    +currency.length()+currency
                    +order.length()+order
                    +desc.length()+desc
                    +merch_name.length()+merch_name
                    +merch_url.length()+merch_url
                    +merchant.length()+merchant
                    +terminal.length()+terminal
                    +email.length()+email
                    +trtype.length()+trtype+"--"
                    +timpstamp.length()+timpstamp
                    +nonce.length()+nonce
                    +backref.length()+backref;

            byte[] hex_key = Hex.decodeHex(encryptionKey.toCharArray());
            String p_sign = RomCardUtils.calculateRFC2104HMAC(hmac_request, hex_key);
            p_sign = p_sign.toUpperCase();

            String request = "" +
                    "AMOUNT="+amount+
                    "&CURRENCY="+currency+
                    "&ORDER="+order+
                    "&DESC="+desc+
                    "&MERCH_NAME="+merch_name+
                    "&MERCH_URL="+merch_url+
                    "&MERCHANT="+merchant+
                    "&TERMINAL="+terminal+
                    "&EMAIL="+email+
                    "&TRTYPE="+trtype+
                    "&COUNTRY="+country+
                    "&MERCH_GMT="+merch_gmt+
                    "&TIMESTAMP="+timpstamp+
                    "&NONCE="+nonce+
                    "&BACKREF="+backref+
                    "&P_SIGN="+p_sign;

            RomCardUtils.doPostHTTPSURLConnectionClient(url,request);

            System.out.println("request-------"+request);

            String response = RomCardUtils.doPostHTTPSURLConnectionClient(url,request);

            System.out.println("response ----------"+response);

        }
        catch (Exception e)
        {
            System.out.println(" Exception ---------- " +e);
        }
    }
    public static void sale21()//Acc. to Romcard Gateway
    {
        try
        {

            String url = "https://cc.zxfgateway.com/deposit/";
            String encryptionKey="7PmNDGeZTJb6wY5AsPGbND88a52p2irZ";
            String merId = "38899130";
            String merOrdId = "T1582168333300";
            String version = "v3";
            String version1 = "v3XXXXX";
            String merOrdAmt = "10.00";
            String userIp = "179.12.33.0";
            String payType = "8001";
            String remark = "TestRemark";
            String respType = "01";
            String respType1 = "02";
            String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String notifyUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";
            String signType = "MD5";
            String ccDetails="{\"ordCurrency\":\"USD\",\"cardNo\":\"5111111111111118\",\"cardExpireMonth\":\"12\",\"cardExpireYear\":\"2022\",\"cardSecurityCode\":\"359\",\"firstName\":\"tian\",\"lastName\":\"wu\",\"email\":\"cc@gmail.com\",\"phone\":\"1389878098\",\"country\":\"us\",\"city\":\"los\",\"address\":\"Nofe Street\",\"zip\":\"2876\"}";
            String encodedccDetails = URLEncoder.encode(URLEncoder.encode(ccDetails.toString(),"UTF-8"),"UTF-8");
            //String encodedccDetails = "%257B%2522ordCurrency%2522%253A%2522USD%2522%252C%2522cardNo%2522%253A%25225111111111111118%2522%252C%2522cardExpireMonth%2522%253A%252212%2522%252C%2522cardExpireYear%2522%253A%25222022%2522%252C%2522cardSecurityCode%2522%253A%2522359%2522%252C%2522firstName%2522%253A%2522tian%2522%252C%2522lastName%2522%253A%2522wu%2522%252C%2522email%2522%253A%2522cc%2540gmail.com%2522%252C%2522phone%2522%253A%25221389878098%2522%252C%2522country%2522%253A%2522us%2522%252C%2522city%2522%253A%2522los%2522%252C%2522address%2522%253A%2522Nofe%2BStreet%2522%252C%2522zip%2522%253A%25222876%2522%257D";
            System.out.println("ccDetails ---->" + URLDecoder.decode(URLDecoder.decode(encodedccDetails, "UTF-8")));
            String signMsgstr = "merId=" + merId + "&merOrdAmt=" + merOrdAmt + "&merOrdId=" + merOrdId + "&notifyUrl=" + notifyUrl + "&payType=" + payType + "&remark=" + remark + "&respType=" + respType + "&returnUrl=" + returnUrl + "&signType=" + signType + "&userIp=" + userIp + "&version=" + version;
            // String signMsg = convertmd5(signMsgstr);
            byte[] hex_key = Hex.decodeHex(encryptionKey.toCharArray());
            String p_sign = RomCardUtils.calculateRFC2104HMAC(signMsgstr, hex_key);
            p_sign = p_sign.toUpperCase();

            String request = ""+"merId=" + merId +
                    "&merOrdId=" + merOrdId +
                    "&version=" + version +
                    "&merOrdAmt=" + merOrdAmt +
                    "&userIp=" + userIp +
                    "&payType=" + payType +
                    "&ccDetails=" + encodedccDetails +
                    "&remark=" + remark +
                    "&respType=" + respType +
                    "&returnUrl=" + returnUrl +
                    "&notifyUrl=" + notifyUrl +
                    "&signType=" + signType +
                    "&signMsg=" + p_sign;
            System.out.println("signMsg---->" + signMsgstr);
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
        catch (SignatureException e)
        {
            e.printStackTrace();
        }
        catch (InvalidKeyException e)
        {
            e.printStackTrace();
        }
        catch (DecoderException e)
        {
            e.printStackTrace();
        }


    }



    public static String convertmd5Test(String value) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        String md5 = null;

        if (null == value) return null;

        try
        {

            //Create MessageDigest object for MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");

            //Update input string in message digest
            digest.update(value.getBytes(), 0, value.length());

            //Converts message digest value in base 16 (hex)
            md5 = new BigInteger(1, digest.digest()).toString(16);

        }
        catch (NoSuchAlgorithmException e)
        {

            e.printStackTrace();
        }

        return md5;
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
    public static String doPostHTTPSURLConnectionClient(String strURL,String req) throws PZTechnicalViolationException
    {
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {

            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/json");
           // post.addRequestHeader("Content-Type", "text/html");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            System.out.println("HttpException he--->" + he);
         }
        catch (IOException io){
            System.out.println("IOException io--->"+ io);
       }
        finally
        {
            post.releaseConnection();
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
    public static String convertmd5(TreeMap<String,String> treeMap) throws NoSuchAlgorithmException, UnsupportedEncodingException
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
                signMsgStr=key+"="+URLEncoder.encode(value,"UTF-8");
            else
                signMsgStr+="&"+key+"="+URLEncoder.encode(value,"UTF-8");
        }

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
