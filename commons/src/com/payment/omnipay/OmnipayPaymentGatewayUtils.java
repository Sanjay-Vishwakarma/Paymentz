package com.payment.omnipay;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

import javax.net.ssl.*;


/**
 * Created by Admin on 2022-01-22.
 */
public class OmnipayPaymentGatewayUtils
{

    private static TransactionLogger transactionLogger  = new TransactionLogger(OmnipayPaymentGatewayUtils.class.getName());
    static Functions functions                          = new Functions();
    private final static String charset                 = "UTF-8";

    public  static String getLast2DigitOfExpiryYear(String ExpiryYear)
    {
        String expiryYearLast2Digit = "";
        if (functions.isValueNull(ExpiryYear)){
            expiryYearLast2Digit = ExpiryYear.substring(ExpiryYear.length()-2,ExpiryYear.length());
        }
        return expiryYearLast2Digit;
    }

    public static boolean isJSONValid(String test)
    {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    public static String getAmount(String amount)
    {
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2    = Double.valueOf(amount);
        dObj2           = dObj2 * 100;
        String amt      = d.format(dObj2);
        return amt;
    }

    public static void updateMainTableEntry(String transactionId, String transaction_mode, String trackingid)
    {
        Connection connection = null;
        try
        {
            String addParam="";
            if(functions.isValueNull(transaction_mode)){
               addParam=",transaction_mode='"+transaction_mode+"'";
            }
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET paymentid='"+transactionId+"' "+addParam+" WHERE trackingid="+trackingid;
            Database.executeUpdate(updateQuery1, connection);
        }

        catch (SystemError s)
        {
            transactionLogger.error("SystemError---", s);
        }
        finally
        {
            if(connection!=null){
                Database.closeConnection(connection);
            }
        }
    }

    public  static  byte[] generateHmacSHA256(String algorithm, byte[] key, byte[] message)
    {
        Mac mac = null;
        try
        {
            mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key,algorithm));
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception while cretaing hmac --> " + e);
        }
        return mac.doFinal(message);
    }

    public static String bytesToHex(byte[] hashInBytes)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hashInBytes.length; i++) {
            sb.append(Integer.toString((hashInBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String getHmac256Signature(String message, byte[] key)
    {
        byte[] bytes = generateHmacSHA256("HmacSHA256", key, message.getBytes());
        return bytesToHex(bytes);
    }

    public static String doGetPostHTTPUrlConnection(String MerchantKey,String Sign,String url,String request) throws PZTechnicalViolationException
    {
        String response = "";
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();
//        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
//        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,TLSv1.3");
        System.setProperty("https.protocols", "TLSv1.3");
//        System.setProperty("https.protocols", "TLSv1");
//        System.setProperty("https.protocols", "TLSv2");
//        System.setProperty("https.protocols", "SSLv3");
//        System.setProperty("javax.net.debug", "all");



//        // Create a trust manager that does not validate certificate chains like the default
//        TrustManager[] trustAllCerts = new TrustManager[]{
//                new X509TrustManager() {
//
//                    public java.security.cert.X509Certificate[] getAcceptedIssuers()
//                    {
//                        return null;
//                    }
//                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
//                    {
//                        //No need to implement.
//                    }
//                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
//                    {
//                        //No need to implement.
//                    }
//                }
//        };
//
//            // Install the all-trusting trust manager
//        try
//        {
//            SSLContext sc = SSLContext.getInstance("SSL");
//            sc.init(null, trustAllCerts, new java.security.SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//        }
//        catch (Exception e)
//        {
//            System.out.println(e);
//        }

        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(url);
        try{
            postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            postMethod.addRequestHeader("MerchantKey",MerchantKey);
            postMethod.addRequestHeader("Sign",Sign);
            postMethod.setRequestBody(request);

            httpClient.executeMethod(postMethod);
            response = new String(postMethod.getResponseBody());

            Header[] headers=  postMethod.getResponseHeaders();
            transactionLogger.error("response Headers[]: ");
            for(Header header2 :headers)
            {
                transactionLogger.error(header2.getName() + " : " + header2.getValue());
            }
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("OmnipayPaymentGatewayUtils.java", "doGetPostHTTPUrlConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("OmnipayPaymentGatewayUtils.java", "doGetPostHTTPUrlConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            postMethod.releaseConnection();
        }
        return response;
    }

    public static String doPostHTTPSURLConnection(String merchantKey, String merchant_secret, String REQUEST_URL,String request, String trackingId) throws PZTechnicalViolationException
    {
        String result=null;
        BufferedInputStream in=null;
        BufferedOutputStream out=null;
        URLConnection conn=null;

        try
        {
            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            //java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            System.setProperty("https.protocols", "TLSv1.2");
            System.setProperty("https.protocols", "TLSv1.3");
            URL url = new URL(REQUEST_URL);
            try
            {

                String authorization = com.directi.pg.Base64.encode(("Basic " + merchantKey + ":" + merchant_secret).getBytes());
                conn = url.openConnection();
                conn.setRequestProperty("Content-Type","application/json");
                conn.setRequestProperty("MerchantKey",merchantKey);
//                conn.setRequestProperty("Sign",Sign);       // not needed as per bank
                conn.setRequestProperty("authorization",authorization );

                transactionLogger.error(trackingId + "----> authorization:"+ authorization);

                Map<String,List<String>> map = conn.getRequestProperties();

                transactionLogger.error("request Headers[]: \n ");
                for(Map.Entry header1 : map.entrySet())
                {
                    transactionLogger.error(trackingId + " >>> "+header1.getKey() + " : " + header1.getValue());
                }

                conn.setConnectTimeout(120000);
                conn.setReadTimeout(120000);
            }
            catch (SSLHandshakeException io)
            {
                transactionLogger.error("SSLHandshakeException Exception", io);
                PZExceptionHandler.raiseTechnicalViolationException("OmnipayPaymentGatewayUtils.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, io.getMessage(), io.getCause());
            }
            if(conn instanceof HttpURLConnection)
            {
                ((HttpURLConnection)conn).setRequestMethod("POST");
            }
            conn.setDoInput(true);
            conn.setDoOutput(true);

            out = new BufferedOutputStream(conn.getOutputStream());
            byte outBuf[] = request.getBytes(charset);
            out.write(outBuf);

            out.close();

            in = new BufferedInputStream(conn.getInputStream());
            result = ReadByteStream(in);
        }
        catch (UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException("OmnipayPaymentGatewayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null,ue.getMessage(), ue.getCause());
        }
        catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("OmnipayPaymentGatewayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON, null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("OmnipayPaymentGatewayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION, null, pe.getMessage(), pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("OmnipayPaymentGatewayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, ex.getMessage(), ex.getCause());
        }
        catch (PZConstraintViolationException pze)
        {
            PZExceptionHandler.raiseTechnicalViolationException("OmnipayPaymentGatewayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, pze.getMessage(), pze.getCause());
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
                    PZExceptionHandler.raiseTechnicalViolationException("OmnipayPaymentGatewayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                } catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("OmnipayPaymentGatewayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }

    private static String ReadByteStream(BufferedInputStream in) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        LinkedList<Omnipay> bufList = new LinkedList<Omnipay>();
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
                bufList.add(new Omnipay(buf, num));
            }
            while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<Omnipay> p = bufList.listIterator(); p.hasNext();)
            {
                Omnipay b = p.next();
                for (int i = 0; i < b.size;)
                {
                    buf[pos] = b.buf[i];
                    i++;
                    pos++;
                }
            }
            buffer = new String(buf,charset);
        }
        catch (UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException("OmnipayPaymentGatewayUtils.java", "ReadByteStream()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ue.getMessage(), ue.getCause());
        }
        catch (IOException ie)
        {
            PZExceptionHandler.raiseTechnicalViolationException("OmnipayPaymentGatewayUtils.java", "ReadByteStream()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ie.getMessage(), ie.getCause());
        }
        return buffer;
    }


    static class Omnipay
    {
        public byte buf[];
        public int size;

        public Omnipay(byte b[], int s)
        {
            buf = b;
            size = s;
        }
    }

    public CommRequestVO getOmniPayPaymentRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = new CommRequestVO();
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();
        CommDeviceDetailsVO commDeviceDetailsVO         = new CommDeviceDetailsVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());

        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCustomerBankId(commonValidatorVO.getProcessorName());
        transDetailsVO.setCardType(commonValidatorVO.getCardType());
        commRequestVO.setCustomerId(commonValidatorVO.getVpa_address());
        cardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
        cardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());
        cardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());
        cardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        commRequestVO.setCommDeviceDetailsVO(commDeviceDetailsVO);
        return commRequestVO;
    }


}
