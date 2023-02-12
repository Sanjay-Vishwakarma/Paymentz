package com.payment.kotakbank.core;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import org.apache.commons.codec.binary.*;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.security.Key;
import java.security.MessageDigest;
import java.util.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Iterator;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.X509TrustManager;

/**
 * Created by Nikita on 3/15/2017.
 */
public class KotakEncryptionDecryptionUtils
{
    private static Logger logger                        = new Logger(KotakEncryptionDecryptionUtils.class.getName());
    private static TransactionLogger transactionLogger  = new TransactionLogger(KotakEncryptionDecryptionUtils.class.getName());

    private final static String charset = "UTF-8";
    private static final String ALGO = "AES";

    //add the PG URL
    String URL = "https://203.196.200.42/allpaypg/Genius/Kotak/request.action";

    //add your secure secret
    String SECURE_SECRET = "F3C63F6FFFC176E4AAD530C59833719D"; //F3C63F6FFFC176E4AAD530C59833719D

    //add your encryption key
    String encKey = "";
    String hashKeys = new String();
    String hashValues = new String();

    //Sale and Auth
    public String hashAllFields(Map fields)
    {
        hashKeys = "";
        hashValues = "";
        List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);

        //create a buffer for the SHA-256 input and add the secure secret first
        StringBuffer buf = new StringBuffer();
        buf.append(SECURE_SECRET);

        //iterate through the list and add the remaining field values
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);
            hashKeys += fieldName + ", ";
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                buf.append(fieldValue);
            }
        }
        transactionLogger.debug("kotak buffer---" + buf);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(buf.toString().getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public String encrypt(String Data,String keySet) throws Exception {
        byte[] keyByte = keySet.getBytes();
        Key key = generateKey(keyByte);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        byte[] encryptedByteValue = new org.apache.commons.codec.binary.Base64().encode(encVal);
        String encryptedValue = new String(encryptedByteValue);
        return encryptedValue;
    }

    private static Key generateKey(byte[] keyByte) throws Exception {
        Key key = new SecretKeySpec(keyByte, ALGO);
        return key;
    }


    //Refund and Capture
    public static X509TrustManager s_x509TrustManager = null;
    public static SSLSocketFactory s_sslSocketFactory = null;

    static {
        s_x509TrustManager = new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[] {}; }
            public boolean isClientTrusted(X509Certificate[] chain) { return true; }
            public boolean isServerTrusted(X509Certificate[] chain) { return true; }
        };

        java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[] { s_x509TrustManager }, null);
            s_sslSocketFactory = context.getSocketFactory();
        } catch (Exception ex) {
            logger.error("Exception ::::::::: ",ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * This method is for performing a Form POST operation from input data parameters.
     */
    public static String doPost(String vpc_Host, String data, boolean useProxy, String proxyHost, int proxyPort)throws UnknownHostException,IOException{
        InputStream is = null;
        OutputStream os = null;
        int vpc_Port = proxyPort;
        String fileName = "";
        boolean useSSL = false;
        //String vpcHost = "https://uat-geniusepay.in";
        String vpcHost = proxyHost;

        //determine if SSL encryption is being used
        if (vpc_Host.substring(0, 8).equalsIgnoreCase("https://")) {
            useSSL = true;

            //remove https:// from url
            vpcHost = vpc_Host.substring(8);

            //get File Name
            fileName = vpcHost.substring(vpcHost.indexOf("/"), vpcHost.length());

            //get host
            vpcHost = vpcHost.substring(0, vpcHost.indexOf("/"));

            logger.debug("vpcHost---"+vpcHost+"-fileName--"+fileName+"-vpcHost--"+vpcHost);
        }

        logger.debug("useProxy---"+useProxy);
        //use the next block of code if using a proxy server
        if (useProxy)
        {
            logger.debug("if useProxy---"+useProxy);
            Socket s = new Socket(proxyHost, proxyPort);
            os = s.getOutputStream();
            is = s.getInputStream();

            //use next block of code if using SSL encryption
            if (useSSL) {
                String msg = "CONNECT " + vpcHost + ":" + vpc_Port + " HTTP/1.0\r\n" + "User-Agent: HTTP Client\r\n\r\n";
                os.write(msg.getBytes());
                byte[] buf = new byte[4096];
                int len = is.read(buf);
                String res = new String(buf, 0, len);

                //check if a successful HTTP connection
                if (res.indexOf("200") < 0) {
                    throw new IOException("Proxy would now allow connection - " + res);
                }

                //write output to VPC
                SSLSocket ssl = (SSLSocket)s_sslSocketFactory.createSocket(s, vpcHost, vpc_Port, true);
                ssl.startHandshake();
                os = ssl.getOutputStream();

                //get response data from VPC
                is = ssl.getInputStream();

                //use the next block of code if NOT using SSL encryption
            } else {
                fileName = vpcHost;
            }

            //use the next block of code if NOT using a proxy server
        }
        else
        {
            logger.debug("else useProxy---"+useProxy);

            //use next block of code if using SSL encryption
            if (useSSL) {
                logger.debug("useSSL if---"+useSSL);
                try
                {
                    logger.debug("try socket---");
                    Socket s = s_sslSocketFactory.createSocket("203.196.200.42", 443);    //add the server details here
                    os = s.getOutputStream();
                    is = s.getInputStream();
                }
                catch (Exception e)
                {
                    logger.error("inside catch---",e);
                }
                //use next block of code if NOT using SSL encryption
            } else {
                logger.debug("useSSL else---"+useSSL);
                Socket s = new Socket(vpcHost, vpc_Port);
                os = s.getOutputStream();
                is = s.getInputStream();
            }
        }

        String req = "POST " + fileName + " HTTP/1.0\r\n"
                + "User-Agent: HTTP Client\r\n"
                + "Content-Type: application/x-www-form-urlencoded\r\n"
                + "Content-Length: " + data.length() + "\r\n\r\n"
                + data;

        logger.debug("socket req---"+req);

        os.write(req.getBytes());
        String res = new String(readAll(is));

        //check if connection is successfully established
        if (res.indexOf("200") < 0) {
            throw new IOException("Connection Refused - " + res);
        }

        if (res.indexOf("404 Not Found") > 0) {
            throw new IOException("File Not Found Error - " + res);
        }

        int resIndex = res.indexOf("\r\n\r\n");
        String body = res.substring(resIndex + 4, res.length());
        return body;
    }

    /**
     * This method is for creating a byte array from input stream data.
     */
    private static byte[] readAll(InputStream is) throws IOException
    {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];

        while (true) {
            int len = is.read(buf);
            if (len < 0) {
                break;
            }
            baos.write(buf, 0, len);
        }
        return baos.toByteArray();
    }

    /**
     * This method is for creating a URL POST data string.
     */
    public String createPostDataFromMap(Map fields) {
        StringBuffer buf = new StringBuffer();
        String ampersand = "";

        //append all fields in a data string
        //System.out.println("rMap---"+fields);
        for (Iterator i = fields.keySet().iterator(); i.hasNext(); ) {
            String key = (String)i.next();
            String value = (String)fields.get(key);
            if ((value != null) && (value.length() > 0)) {

                //append the parameters
                buf.append(ampersand);
                buf.append(URLEncoder.encode(key));
                buf.append('=');
                buf.append(URLEncoder.encode(value));
            }
            ampersand = "&";
        }
        return buf.toString();
    }

    public String createPostDataForSale(Map fields) {
        StringBuffer buf = new StringBuffer();
        String ampersand = "";

        //append all fields in a data string
        //System.out.println("rMap---"+fields);
        for (Iterator i = fields.keySet().iterator(); i.hasNext(); ) {
            String key = (String)i.next();
            String value = (String)fields.get(key);
            if ((value != null) && (value.length() > 0)) {

                //append the parameters
                buf.append(ampersand);
                buf.append(key);
                buf.append('=');
                buf.append(value);
            }
            ampersand = "::";
        }
        return buf.toString();
    }
    /**
     * This method is for creating a URL POST data string.
     */
    public Map createMapFromResponse(String queryString)
    {
        Map map = new HashMap();
        StringTokenizer st = new StringTokenizer(queryString, "&");
        while (st.hasMoreTokens())
        {
            String token = st.nextToken();
            int i = token.indexOf('=');
            if (i > 0)
            {
                try
                {
                    String key = token.substring(0, i);
                    String value = URLDecoder.decode(token.substring(i + 1, token.length()));
                    map.put(key, value);
                }
                catch (Exception ex)
                {
                    //do nothing and keep looping through data
                }
            }
        }
        return map;
    }


}
