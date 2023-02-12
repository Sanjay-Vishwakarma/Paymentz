package com.directi.pg.core.pay132;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 13, 2012
 * Time: 8:53:56 AM
 * To change this template use File | Settings | File Templates.
 */

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.directi.pg.Logger;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

public class TrustAllCertificateSocketFactory implements SecureProtocolSocketFactory {
	private static Logger logger=new Logger(TrustAllCertificateSocketFactory.class.getName());
	private SSLContext sslcontext;

	static TrustManager trustAllCerts[] = { new X509TrustManager() {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
		public void checkClientTrusted(X509Certificate ax509certificate[], String s) {
		}
		public void checkServerTrusted(X509Certificate ax509certificate[], String s) {
		}
	}};

	public  TrustAllCertificateSocketFactory() {
		sslcontext = null;
	}

	private static SSLContext createEasySSLContext() {
        SSLContext context = null;
        try {
			context = SSLContext.getInstance("SSL");
	        context.init(null, trustAllCerts, null);
		} catch (NoSuchAlgorithmException e) {
			logger.error("NoSuchAlgorithmException---->",e);
		} catch (KeyManagementException e) {
			logger.error("KeyManagementException---->", e);
		}
        return context;
    }

	private SSLContext getSSLContext() {
		if (sslcontext == null){
			sslcontext = createEasySSLContext();
		}
		return sslcontext;
	}

	public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) throws IOException, UnknownHostException {
		return getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
	}

	public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort, HttpConnectionParams httpConnectionParams) throws IOException, UnknownHostException, ConnectTimeoutException {
		return getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
	}

	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		return getSSLContext().getSocketFactory().createSocket(host, port);
	}

	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
		return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
	}

}

