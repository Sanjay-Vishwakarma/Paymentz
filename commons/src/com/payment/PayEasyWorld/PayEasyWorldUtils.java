package com.payment.PayEasyWorld;

import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by Admin on 5/22/2021.
 */
public class PayEasyWorldUtils
{
    public final static String charset = "UTF-8";
    private static TransactionLogger transactionLogger=new TransactionLogger(PayEasyWorldUtils.class.getName());
    private static HashMap<String,String> resposeCodeMap=new HashMap<>();
    static {
        resposeCodeMap.put("0000","Successful operation");
        resposeCodeMap.put("1111","Operation failure");
        resposeCodeMap.put("2000","Data format error");
        resposeCodeMap.put("2001","The merchant number can not be empty");
        resposeCodeMap.put("2002","Terminal number can not be empty");
        resposeCodeMap.put("2003","Wrong merchant number / terminal number");
        resposeCodeMap.put("2004","Merchant number/terminal number deactivated");
        resposeCodeMap.put("2005","Encryption values can not be empty");
        resposeCodeMap.put("2006","Encryption error");
        resposeCodeMap.put("2007","Refund order records can not be empty");
        resposeCodeMap.put("2008","No more than 50 refund orders");
        resposeCodeMap.put("2009","Refund order running number empty");
        resposeCodeMap.put("2010","Refund order stream number not available");
        resposeCodeMap.put("2011","refund order refund reason can not be empty");
        resposeCodeMap.put("2012","Refund order refund currency empty");
        resposeCodeMap.put("2013","Refund Order Refund Currency Error");
        resposeCodeMap.put("2014","Refund order refund amount empty");
        resposeCodeMap.put("2015","Refund Order Refund Amount Wrong");
        resposeCodeMap.put("2016","Refund amount greater than refundable amount");
        resposeCodeMap.put("2017","Successful orders can be refunded");
        resposeCodeMap.put("2018","Confirming orders can be refunded");
        resposeCodeMap.put("2019","The order is still under refund processing");
        resposeCodeMap.put("2020","Order does not match merchant number");
        resposeCodeMap.put("2021","Order does not match terminal number");
        resposeCodeMap.put("2022","No refund for incoming orders");
        resposeCodeMap.put("2023","No refund in transfer processing");
        resposeCodeMap.put("2024","Deposit transfers made, no refund");
        resposeCodeMap.put("2025","Refund Order Information Invalid");
        resposeCodeMap.put("2026","Query types are not supported");
        resposeCodeMap.put("2027","Query order number is empty");
        resposeCodeMap.put("2028","No more than 100 queries");
        resposeCodeMap.put("2029","Query time format error");
        resposeCodeMap.put("2030","Query types are not supported");
        resposeCodeMap.put("2031","Query order number is empty");
        resposeCodeMap.put("2032","No more than 100 queries");
        resposeCodeMap.put("2033","Query time format error");
        resposeCodeMap.put("2034","exception type not supported");
        resposeCodeMap.put("2035","Current page number error");
        resposeCodeMap.put("2036","Record errors per page");
        resposeCodeMap.put("2037","Start time format error");
        resposeCodeMap.put("2038","End Time Format Error");
        resposeCodeMap.put("9999","System anomalies");
    }
    public static String SHA256forSales(String sha) throws PZTechnicalViolationException
    {
        sha.trim();
        // transactionlogger.error("sha256 combination---" + sha);

        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayEasyWorldUtils.class.getName(), "SHA256forSales()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayEasyWorldUtils.class.getName(), "SHA256forSales()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return hexString.toString().trim();

    }
    public static String doPostHTTPSURLConnectionClient(String strURL,String req) throws PZTechnicalViolationException
    {
        String responseStr="";
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try{
            httpClient = getHttpClient(60000);
            HttpPost httpPost = new HttpPost(strURL);
            StringEntity entity = new StringEntity(req.toString(),"utf-8");
            entity.setContentEncoding("utf-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            HttpResponse res = httpClient.execute(httpPost);

            responseStr=EntityUtils.toString(res.getEntity());
        }
        catch (ClientProtocolException e)
        {
            transactionLogger.error("ClientProtocolException-->", e);
            PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException-->",e);
            PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally {
            try {
                if(null != response) {
                    response.close();
                }
                if(null != httpClient) {
                    httpClient.close();
                }
            } catch (Exception e) {
                transactionLogger.error("Exception-->", e);
            }
        }
        return responseStr;
    }
    public static CloseableHttpClient getHttpClient(int timeout) throws PZTechnicalViolationException
    {
        CloseableHttpClient httpClient = null;
        try {
            SSLContext sslcontext = CustomSSLSocketFactory.createIgnoreVerifySSL();
            CustomSSLSocketFactory sslsf = new CustomSSLSocketFactory(sslcontext,new String[] {"TLSv1.2"},null,null);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build();
            httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).setDefaultRequestConfig(requestConfig).build();
        }
        catch (UnrecoverableKeyException e)
        {
            transactionLogger.error("UnrecoverableKeyException-->",e);
            PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.KEY_MANAGEMENT_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException-->",e);
            PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (KeyManagementException e)
        {
            transactionLogger.error("KeyManagementException-->",e);
            PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.KEY_MANAGEMENT_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (KeyStoreException e)
        {
            transactionLogger.error("KeyStoreException-->",e);
            PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.KEY_STORE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return httpClient;
    }
    public static String doPostHTTPSURLConnection(String strURL, String req) throws PZTechnicalViolationException
    {
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        URLConnection con = null;

        try
        {
            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            System.setProperty("https.protocols", "TLSv1.2");
            //java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            URL url = new URL(strURL.trim());
            try
            {
                con = url.openConnection();
                con.setConnectTimeout(60000);
                con.setReadTimeout(60000);
            }
            catch (IOException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE,null, io.getMessage(), io.getCause());
            }

            if(con instanceof HttpURLConnection)
            {
                ((HttpURLConnection)con).setRequestMethod("POST");
            }
            con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            con.setDoInput(true);
            con.setDoOutput(true);


            //con.setRequestProperty("Content-length",String.valueOf (req.length()));
            //con.setRequestProperty("Content-Type","application/x-www- form-urlencoded");
            //con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows 98; DigExt)");

            out = new BufferedOutputStream(con.getOutputStream());
            byte outBuf[] = req.getBytes(charset);
            out.write(outBuf);

            out.close();

            in = new BufferedInputStream(con.getInputStream());
            result = ReadByteStream(in);
        }
        catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null,me.getMessage(),me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (UnsupportedEncodingException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, ex.getMessage(), ex.getCause());
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                } catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }
    private static String ReadByteStream(BufferedInputStream in) throws PZTechnicalViolationException
    {
        LinkedList<Payeasyworld> bufList = new LinkedList<Payeasyworld>();
        int size = 0;
        String buffer = null;
        byte buf[];
        try
        {
            do
            {
                buf = new byte[128];
                int num = in.read(buf);
                if (num == -1)
                    break;
                size += num;
                bufList.add(new Payeasyworld(buf, num));
            } while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<Payeasyworld> p = bufList.listIterator(); p.hasNext();)
            {
                Payeasyworld b = p.next();
                for (int i = 0; i < b.size;) {
                    buf[pos] = b.buf[i];
                    i++;
                    pos++;
                }
            }
            buffer = new String(buf,charset);
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PayforasiaUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, io.getMessage(), io.getCause());
        }
        return buffer;
    }
    public static String getErrorMessage(String responseCode)
    {
        return resposeCodeMap.get(responseCode);
    }
    static class Payeasyworld
    {
        public byte buf[];
        public int size;

        public Payeasyworld(byte b[], int s)
        {
            buf = b;
            size = s;
        }
    }
}
