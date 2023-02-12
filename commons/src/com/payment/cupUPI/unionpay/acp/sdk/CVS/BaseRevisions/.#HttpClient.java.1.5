/**
 *
 * Licensed Property to China UnionPay Co., Ltd.
 *
 * (C) Copyright of China UnionPay Co., Ltd. 2010
 *     All Rights Reserved.
 *
 *
 * Modification History:
 * =============================================================================
 *   Author         Date          Description
 *   ------------ ---------- ---------------------------------------------------
 *   xshu       2014-05-28       HTTP communication tools
 * =============================================================================
 */
package com.payment.cupUPI.unionpay.acp.sdk;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import com.directi.pg.UnionPayInternationalLogger;
import com.payment.cupUPI.unionpay.acp.sdk.BaseHttpSSLSocketFactory.TrustAnyHostnameVerifier;
//import payment.UnionPayInternationalFrontEndServlet;

/**
 *
 * @ClassName HttpClient
 * @Description acpsdk sends background http request class
 * @date 2016-7-22 ??4:03:25
 *
 */
public class HttpClient {

    private static UnionPayInternationalLogger transactionLogger = new UnionPayInternationalLogger(HttpClient.class.getName());

    /**
     * Destination address
     */
    private URL url;

    /**
     * Communication connection timeout
     */
    private int connectionTimeout;

    /**
     * Communication read timeout
     */
    private int readTimeOut;

    /**
     * Communication result
     */
    private String result;

    /**
     * Obtain the communication result
     * @return
     */
    public String getResult() {
        return result;
    }

    /**
     * Set the communication result
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * Construct functions
     * //@param Url: Destination address
     * @param connectionTimeout: HTTP connection timeout
     * @param readTimeOut: HTTP read timeout
     */
    public HttpClient(String url, int connectionTimeout, int readTimeOut) {
        try {
            //this.url = new URL(url);
     // handled  com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl cannot be cast to javax.net.ssl.HttpsURLConnectionjava.lang.ClassCastException: com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl cannot be cast to javax.net.ssl.HttpsURLConnection

            this.url = new URL(this.url, url,new sun.net.www.protocol.https.Handler());
            this.connectionTimeout = connectionTimeout;
            this.readTimeOut = readTimeOut;
        } catch (MalformedURLException e) {
            transactionLogger.error(e.getMessage() + e);
        }
    }

    /**
     * Send information to the server
     * @param data
     * @param encoding
     * @return
     * @throws Exception
     */
    public int send(Map<String, String> data, String encoding) throws Exception {
        try {
            HttpURLConnection httpURLConnection = createConnection(encoding);
            if (null == httpURLConnection) {
                throw new Exception("Create httpURLConnection Failure");
            }
            String sendData = this.getRequestParamString(data, encoding);
            transactionLogger.error("Request message:[" + sendData + "]");
            this.requestServer(httpURLConnection, sendData,
                    encoding);
            this.result = this.response(httpURLConnection, encoding);
            transactionLogger.error("Response message:[" + result + "]");
            return httpURLConnection.getResponseCode();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Send information to the server by means of GET
     * //@param data
     * @param encoding
     * @return
     * @throws Exception
     */
    public int sendGet(String encoding) throws Exception {
        try {
            HttpURLConnection httpURLConnection = createConnectionGet(encoding);
            if(null == httpURLConnection){
                throw new Exception("Fail to create a connection");
            }
            this.result = this.response(httpURLConnection, encoding);
            transactionLogger.error("Message returned synchronously:[" + result + "]");
            return httpURLConnection.getResponseCode();
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Send information by means of HTTP Post
     *
     * @param connection
     * @param message
     * @throws IOException
     */
    private void requestServer(final URLConnection connection, String message, String encoder)
            throws Exception {
        PrintStream out = null;
        try {
            connection.connect();
            out = new PrintStream(connection.getOutputStream(), false, encoder);
            out.print(message);
            out.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != out) {
                out.close();
            }
        }
    }

    /**
     * Display Response messages
     *
     * @param connection
     * //@param CharsetName
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    private String response(final HttpURLConnection connection, String encoding)
            throws URISyntaxException, IOException, Exception {
        InputStream in = null;
        StringBuilder sb = new StringBuilder(1024);
        BufferedReader br = null;
        try {
            if (200 == connection.getResponseCode()) {
                in = connection.getInputStream();
                sb.append(new String(read(in), encoding));
            } else {
                in = connection.getErrorStream();
                sb.append(new String(read(in), encoding));
            }
            transactionLogger.error("HTTP Return Status-Code:["
                    + connection.getResponseCode() + "]");
            return sb.toString();
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != br) {
                br.close();
            }
            if (null != in) {
                in.close();
            }
            if (null != connection) {
                connection.disconnect();
            }
        }
    }

    public static byte[] read(InputStream in) throws IOException {
        byte[] buf = new byte[1024];
        int length = 0;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        while ((length = in.read(buf, 0, buf.length)) > 0) {
            bout.write(buf, 0, length);
        }
        bout.flush();
        return bout.toByteArray();
    }

    /**
     * Create a connection
     *
     * @return
     * @throws ProtocolException
     */
    private HttpURLConnection createConnection(String encoding) throws ProtocolException {
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            transactionLogger.error(e.getMessage() + e);
            return null;
        }
        httpURLConnection.setConnectTimeout(this.connectionTimeout);// Connection timeout
        httpURLConnection.setReadTimeout(this.readTimeOut);// Timeout for reading results
        httpURLConnection.setDoInput(true); // Readable
        httpURLConnection.setDoOutput(true); // Writable
        httpURLConnection.setUseCaches(false);// Cancel caching
        httpURLConnection.setRequestProperty("Content-type",
                "application/x-www-form-urlencoded;charset=" + encoding);
        httpURLConnection.setRequestMethod("POST");
        if ("https".equalsIgnoreCase(url.getProtocol())) {
            HttpsURLConnection husn = (HttpsURLConnection) httpURLConnection;
            //
            if(!SDKConfig.getConfig().isIfValidateRemoteCert()){
                husn.setSSLSocketFactory(new BaseHttpSSLSocketFactory());
                husn.setHostnameVerifier(new TrustAnyHostnameVerifier());//Solve the problem that HTTPS cannot be accessed due to the server certificate.
            }
            return husn;
        }
        return httpURLConnection;
    }

    /**
     * Create a connection
     *
     * @return
     * @throws ProtocolException
     */
    private HttpURLConnection createConnectionGet(String encoding) throws ProtocolException {
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            transactionLogger.error(e.getMessage() + e);
            return null;
        }
        httpURLConnection.setConnectTimeout(this.connectionTimeout);// Connection timeout
        httpURLConnection.setReadTimeout(this.readTimeOut);// Timeout for reading results
        httpURLConnection.setUseCaches(false);// Cancel caching
        httpURLConnection.setRequestProperty("Content-type",
                "application/x-www-form-urlencoded;charset=" + encoding);
        httpURLConnection.setRequestMethod("GET");
        if ("https".equalsIgnoreCase(url.getProtocol())) {
            HttpsURLConnection husn = (HttpsURLConnection) httpURLConnection;
            //
            if(!SDKConfig.getConfig().isIfValidateRemoteCert()){
                husn.setSSLSocketFactory(new BaseHttpSSLSocketFactory());
                husn.setHostnameVerifier(new TrustAnyHostnameVerifier());//Solve the problem that HTTPS cannot be accessed due to the server certificate.
            }
            return husn;
        }
        return httpURLConnection;
    }

    /**
     *
     *
     * @param requestParam
     * @param coder
     * @return
     */
    private String getRequestParamString(Map<String, String> requestParam, String coder) {
        if (null == coder || "".equals(coder)) {
            coder = "UTF-8";
        }
        StringBuffer sf = new StringBuffer("");
        String reqstr = "";
        if (null != requestParam && 0 != requestParam.size()) {
            for (Entry<String, String> en : requestParam.entrySet()) {
                try {
                    sf.append(en.getKey()
                            + "="
                            + (null == en.getValue() || "".equals(en.getValue()) ? "" : URLEncoder
                            .encode(en.getValue(), coder)) + "&");
                } catch (UnsupportedEncodingException e) {
                    transactionLogger.error(e.getMessage() + e);
                    return "";
                }
            }
            reqstr = sf.substring(0, sf.length() - 1);
        }
        transactionLogger.error("Request Message:[" + reqstr + "]");
        return reqstr;
    }

}
