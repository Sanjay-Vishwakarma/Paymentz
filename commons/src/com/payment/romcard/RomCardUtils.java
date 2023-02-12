package com.payment.romcard;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Rihen on 12/12/2018.
 */
public class RomCardUtils
{
    private static TransactionLogger transactionLogger= new TransactionLogger(RomCardUtils.class.getName());

  /*  public static String doPostHTTPSURLConnectionClient(String strURL,String req) throws PZTechnicalViolationException
    {
        String result = "";
        System.setProperty("jsse.enableSNIExtension", "false");

        CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
        //HttpClient httpClient = new HttpClient();
       // PostMethod post = new PostMethod(strURL);
        HttpPost httpPost = new HttpPost(strURL);

        try
        {
            System.setProperty("https.protocols", "TLSv1.2");

            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

            *//*post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(req);
            httpClient.e(post);*//*

            StringEntity jsonRequest = new StringEntity(req, "UTF-8");
            httpPost.addHeader("content-type", "application/x-www-form-urlencoded");
            httpPost.setEntity(jsonRequest);

            HttpResponse response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), "UTF-8");


           // result= String.valueOf(response.getEntity());;
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException in RomCardGateway---", he);
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException in RomCardGateway---", io);
        }
        finally
        {
            httpPost.releaseConnection();
        }
        return result;
    }
*/

    public static String doPostHTTPSURLConnectionClient(String strURL,String req) throws PZTechnicalViolationException
    {
        transactionLogger.error("----inside Connection-----"+strURL);
        String result = "";
        System.setProperty("jsse.enableSNIExtension", "false");

        CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
        HttpPost httpPost = new HttpPost(strURL);

        try
        {
            System.setProperty("https.protocols", "TLSv1.2");

            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            //java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            StringEntity jsonRequest = new StringEntity(req, "UTF-8");
            httpPost.addHeader("content-type", "application/x-www-form-urlencoded");
            httpPost.setEntity(jsonRequest);

            HttpResponse response = httpClient.execute(httpPost);
            result=   EntityUtils.toString(response.getEntity(), "UTF-8");

        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException in RomCardGateway---", he);
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException in RomCardGateway---", io);
        }
        finally
        {
            httpPost.releaseConnection();
        }
        transactionLogger.error("result-----"+result);
        return result;
    }

    public final static String charset = "UTF-8";


    public static String getSignature(String KEY, String VALUE)
    {
        try
        {
            SecretKeySpec signingKey = new SecretKeySpec(KEY.getBytes("UTF-8"), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(VALUE.getBytes("UTF-8"));

            byte[] hexArray = {
                    (byte)'0', (byte)'1', (byte)'2', (byte)'3',
                    (byte)'4', (byte)'5', (byte)'6', (byte)'7',
                    (byte)'8', (byte)'9', (byte)'a', (byte)'b',
                    (byte)'c', (byte)'d', (byte)'e', (byte)'f'
            };
            byte[] hexChars = new byte[rawHmac.length * 2];
            for ( int j = 0; j < rawHmac.length; j++ )
            {
                int v = rawHmac[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public static String currentTimeToGMT()
    {
        Date date = new Date();
        DateFormat gmtFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        TimeZone gmtTime = TimeZone.getTimeZone("GMT");
        gmtFormat.setTimeZone(gmtTime);
        //System.out.println("Current Time: "+date);
        //System.out.println("GMT Time: " + gmtFormat.format(date));
        return gmtFormat.format(date);
    }

    public static String hmacSha(String KEY, String VALUE, String SHA_TYPE)
    {
        try
        {
            SecretKeySpec signingKey = new SecretKeySpec(KEY.getBytes("UTF-8"), SHA_TYPE);
            Mac mac = Mac.getInstance(SHA_TYPE);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(VALUE.getBytes("UTF-8"));

            byte[] hexArray = {
                    (byte)'0', (byte)'1', (byte)'2', (byte)'3',
                    (byte)'4', (byte)'5', (byte)'6', (byte)'7',
                    (byte)'8', (byte)'9', (byte)'a', (byte)'b',
                    (byte)'c', (byte)'d', (byte)'e', (byte)'f'
            };
            byte[] hexChars = new byte[rawHmac.length * 2];
            for ( int j = 0; j < rawHmac.length; j++ ) {
                int v = rawHmac[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public static String pack(String hex)
    {
        String input = hex.length() % 2 == 0 ? hex : hex  + "0";
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i+=2) {
            String str = input.substring(i, i+2);
            output.append((char)Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    public static String toHexString(byte[] bytes)
    {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    public static String calculateRFC2104HMAC(String data, byte[] key) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException
    {
        SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        return toHexString(mac.doFinal(data.getBytes()));
    }


    public static String convertmd5(String value) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        String md5 = null;
        if (null == value) return null;
        try
        {
            transactionLogger.debug("MD5 combination for qwipi---"+value);
            //Create MessageDigest object for MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");
            md5 = getString(digest.digest(value.getBytes()));
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException while calculating MD5 for RomCard---",e);
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

    public static CommRequestVO getRomCardRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();

        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String merchantId = account.getMerchantId();

        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTrackingid());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setEmiCount(commonValidatorVO.getTransDetailsVO().getEmiCount());

        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        merchantAccountVO.setMerchantId(merchantId);
        merchantAccountVO.setDisplayName(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        merchantAccountVO.setAliasName(commonValidatorVO.getMerchantDetailsVO().getSiteName());

        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);

        return commRequestVO;
    }

    public static String AlterHtml(String html, String url)
    {
        Document doc = Jsoup.parse(html);
        doc.html();
        Elements elements = doc.select("form");
        // rename all 'font'-tags to 'span'-tags, will also keep attributs etc.
        elements.attr("action", url);

        transactionLogger.debug(doc.html());
        return doc.html();
    }


}