package com.payment.libill_payment.core;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import com.sun.net.ssl.TrustManager;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;


/**
 * Created by Nikita on 14/10/2015.
 */
public class LibillUtills
{
    public final static String charset = "UTF-8";
    private static Logger log = new Logger(LibillUtills.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(LibillUtills.class.getName());

    String login = "pz-merch";

    public static String doPostHTTPSURLConnection(String strURL, String request) throws PZTechnicalViolationException
    {
        String result=null;
        BufferedInputStream in=null;
        BufferedOutputStream out=null;
        URLConnection conn=null;

        try
        {
            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            URL url = new URL(strURL);
            try
            {
                conn = url.openConnection();
                conn.setConnectTimeout(120000);
                conn.setReadTimeout(120000);
            }
            catch (SSLHandshakeException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("LibillUtills.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, io.getMessage(), io.getCause());
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
            PZExceptionHandler.raiseTechnicalViolationException("LibillUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null,ue.getMessage(), ue.getCause());
        }
        catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("LibillUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON, null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("LibillUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION, null, pe.getMessage(), pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("LibillUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, ex.getMessage(), ex.getCause());
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
                    PZExceptionHandler.raiseTechnicalViolationException("LibillUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                } catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("LibillUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }

    static class libill
    {
        public byte buf[];
        public int size;

        public libill(byte b[], int s)
        {
            buf = b;
            size = s;
        }
    }

    private static String ReadByteStream(BufferedInputStream in) throws PZTechnicalViolationException
    {
        LinkedList<libill> bufList = new LinkedList<libill>();
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
                bufList.add(new libill(buf, num));
            }
            while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<libill> p = bufList.listIterator(); p.hasNext(); )
            {
                libill b = p.next();
                for (int i = 0; i < b.size; )
                {
                    buf[pos] = b.buf[i];
                    i++;
                    pos++;
                }

            }
            buffer = new String(buf, charset);
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("LibillUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }

        return buffer;
    }

    public static HashMap readSaleResponse(String response) throws PZTechnicalViolationException
    {
        String key = "";
        String value = "";

        HashMap responseMap = new HashMap();
        String[] split = response.split("&");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length; i++)
        {
            String temp = split[i];
            String[] data = temp.split("=");
            key = data[0];
            value = data[1];

            responseMap.put(key, value);
        }
        return responseMap;
    }

    public static String processTxSHA(String endpointid, String client_orderid, String amount, String email, String merchant_control) throws PZTechnicalViolationException
    {
        amount = (int)(Double.parseDouble(amount)*100)+"";
        String sha = endpointid + client_orderid + amount + email + merchant_control;
        sha.trim();

        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("LibillPaymentGateway.java", "processTxSHA()", null, "common", "Technical Exception Occured. Please Contact Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ex.getMessage(), ex.getCause());
        }
        catch (UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException("LibillPaymentGateway.java", "processTxSHA()", null, "common", "Technical Exception Occured. Please Contact Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ue.getMessage(), ue.getCause());
        }
        return hexString.toString().toLowerCase().trim();
    }

    public static String cancelTxSHA(String login, String client_orderid, String orderid, String merchant_control) throws PZTechnicalViolationException
    {
        String sha = login + client_orderid + orderid + merchant_control;
        log.debug("cancel sha---" + sha);
        sha.trim();

        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("LibillPaymentGateway.java", "cancelTxSHA()", null, "common", "Technical Exception Occured. Please Contact Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ex.getMessage(), ex.getCause());
        }
        catch (UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException("LibillPaymentGateway.java", "cancelTxSHA()", null, "common", "Technical Exception Occured. Please Contact Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ue.getMessage(), ue.getCause());
        }
        return hexString.toString().toLowerCase().trim();
    }

    public static String captureTxSHA(String login, String client_orderid, String orderid, String amount, String currency, String merchant_control) throws PZTechnicalViolationException
    {
        amount = (int)(Double.parseDouble(amount) * 100) + "";
        log.debug("Amount---" + amount);
        String sha = login + client_orderid + orderid + amount + currency + merchant_control;
        log.debug("capture sha---" + sha);
        sha.trim();

        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("LibillPaymentGateway.java", "captureTxSHA()", null, "common", "Technical Exception Occured. Please Contact Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ex.getMessage(), ex.getCause());
        }
        catch (UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException("LibillPaymentGateway.java", "captureTxSHA()", null, "common", "Technical Exception Occured. Please Contact Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ue.getMessage(), ue.getCause());
        }
        return hexString.toString().toLowerCase().trim();
    }
}
