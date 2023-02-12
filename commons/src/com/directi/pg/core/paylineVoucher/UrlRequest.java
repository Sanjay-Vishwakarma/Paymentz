package com.directi.pg.core.paylineVoucher;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 13, 2012
 * Time: 8:45:04 AM
 * To change this template use File | Settings | File Templates.
 */
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.log4j.Logger;

public class UrlRequest {
	private static Logger LOGGER = Logger.getLogger("UrlRequest");

	public static final String GET = "GET";

	public static final String POST = "POST";

	public static final String CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";

	public static final int OK = 0;

	public static final int IO_ERROR = 1;

	public static final int ERROR = 2;

	public static final int READ_TIMEOUT = 3;

	public static final int CONNECTION_TIMEOUT = 4;

	private static final int HANDSHAKE_ERROR = 5;

	public static final boolean TEST_SYSTEM = true;

	private final HashMap<String, String> params_;

	private String type_;

	private String url_;

	private int response_;

	private int readTimeout_;

	private int retry_;

	private int connectionTimeout_;

	private AuthScope scopeForCredentials_;

	private Credentials credentials_;

	private StringRequestEntity requestEntity_;

	private boolean addContentParams_;

	static {
		//TEST_SYSTEM = UtilBaseConfig.getInstance().getProperty("testSystem").trim().equals("true");
		if (TEST_SYSTEM){
			setNoTrustManager();
		}
	}
	public UrlRequest(String _type, String _url) {
		this(_type, _url, 30000, 2000, 3);
	}

	public UrlRequest(String _type, String _url, int _readTimeout, int _connectionTimeout, int _retry) {
		params_ = new HashMap<String, String>();
		response_ = 0;
		addContentParams_ = false;
		url_ = _url;
		readTimeout_ = _readTimeout;
		connectionTimeout_ = _connectionTimeout;
		retry_ = _retry;
		setType(_type);
	}

	public UrlRequest() {
		params_ = new HashMap<String, String>();
		response_ = 0;
		addContentParams_ = false;
	}

	public int getRetry() {
		return retry_;
	}

	public int getConnectionTimeout() {
		return connectionTimeout_;
	}

	public int getReadTimeout() {
		return readTimeout_;
	}

	public String getType() {
		return type_;
	}

	public Map<String, String> getParams() {
		return Collections.unmodifiableMap(params_);
	}

	public UrlRequest setType(String _type) {
		type_ = _type;
		return this;
	}

	public void addParam(String key, String value) {
		params_.put(key, value);
	}

	public void addParams(Map<String, String> _params) {
		params_.putAll(_params);
	}

	public UrlRequest setCredentials(String _userName, String _password,
			String _host, int _port) {
		credentials_ = new UsernamePasswordCredentials(_userName, _password);
		scopeForCredentials_ = new AuthScope(_host, _port, AuthScope.ANY_REALM);
		return this;
	}

	public UrlRequest setRequestEntity(StringRequestEntity _requestEntity) {
		requestEntity_ = _requestEntity;
		return this;
	}

	public String sendAndReturnString() {
		ByteBuffer result = send();
		return result == null ? null : result.toString();
	}

	public ByteBuffer send() {
		ByteBuffer bytebuffer;
		HttpClient client = createHttpClient();
		HttpMethod method = getMethod();
		HttpConnectionManagerParams params = client.getHttpConnectionManager().getParams();

		params.setSoTimeout(getReadTimeout());
		params.setConnectionTimeout(getConnectionTimeout());

		if (credentials_ != null && scopeForCredentials_ != null){
			client.getState().setCredentials(scopeForCredentials_, credentials_);
		}

		if (requestEntity_ != null){
			((PostMethod) method).setRequestEntity(requestEntity_);
		}else{
			method.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
		}

		int status_code = -1;
		for (int attempt = 0; status_code == -1 && attempt < getRetry() || response_ == 5;) {
			if (attempt != 0){
				LOGGER.info((new StringBuilder("retrying ")).append(String.valueOf(attempt)).append(". time").toString());
			}

			attempt++;

			try {
				status_code = client.executeMethod(method);
				LOGGER.info((new StringBuilder("STATUS:")).append(status_code).toString());
			} catch (SSLHandshakeException e) {
				setNoTrustManager();
				releaseMethod(method);
				method = getMethod();
				if (response_ == 5){
					response_ = 1;
				}else{
					response_ = 5;
				}
                //System.out.println((new StringBuilder(String.valueOf(getUrl()))).append(": SSLHandshakeException encountered!").toString()+ e);
				LOGGER.error((new StringBuilder(String.valueOf(getUrl()))).append(": SSLHandshakeException encountered!").toString(), e);
			} catch (SocketTimeoutException e) {
				response_ = 3;
				attempt = 0x7fffffff;
                //System.out.println((new StringBuilder(String.valueOf(getUrl()))).append(": SocketTimeoutException encountered!").toString()+ e);
				LOGGER.error((new StringBuilder(String.valueOf(getUrl()))).append(": SocketTimeoutException encountered! ").toString(), e);
			} catch (IOException e) {
				response_ = 1;
                //System.out.println((new StringBuilder(String.valueOf(getUrl()))).append(": IOException encountered!").toString()+ e);
				LOGGER.error((new StringBuilder(String.valueOf(getUrl()))).append(": IOException encountered!").toString(), e);
			} catch (Exception e) {
				response_ = 1;
                //System.out.println((new StringBuilder(String.valueOf(getUrl()))).append(": Exception encountered!").toString()+ e);
				LOGGER.fatal((new StringBuilder(String.valueOf(getUrl()))).append(": Exception encountered!").toString(), e);
			}
		}

		ByteBuffer result = null;
		if (status_code != -1) {
			byte body[] = new byte[0];
			boolean bodyRead = false;
			try {
				body = method.getResponseBody();
				bodyRead = true;
			} catch (Exception e) {
				LOGGER.info("PROBLEM reading response body", e);
			}
			if (body != null) {
				LOGGER.info((new StringBuilder("BODY: ")).append(
						new String(body)).toString());
			} else {
				LOGGER.info("BODY was null");
				bodyRead = false;
			}
			if (bodyRead) {
				result = new ByteBuffer(body);
				response_ = 0;
			} else {
				response_ = 3;
				result = null;
			}
		}
		bytebuffer = result;
		releaseMethod(method);
		return bytebuffer;
	}

	protected HttpClient createHttpClient() {
		return new HttpClient();
	}

	private void releaseMethod(HttpMethod method) {
		if (method != null)
			try {
				method.releaseConnection();
			} catch (RuntimeException e) {
				LOGGER.error("error releaseing HttpClient connection", e);
			}
	}

	public int getResponse() {
		return response_;
	}

	public String getUrl() {
		if ("POST".equals(getType())){
			return url_;
		}else{
			return (new StringBuilder(String.valueOf(url_))).append(getAppendedGetParams()).toString();
		}
	}

	public void setAddContentParams(boolean _addContentParams) {
		addContentParams_ = _addContentParams;
	}

	public boolean isAddContentParams() {
		return addContentParams_;
	}

	private HttpMethod getMethod() {
		HttpMethod result;
		if (type_.equals("GET")) {
			result = new GetMethod(getUrl());
		} else {
			result = new PostMethod(getUrl());
			((PostMethod) result).setRequestBody(getPostParams());
		}
		return result;
	}

	private NameValuePair[] getPostParams() {
		int size = params_.size();
		if (addContentParams_)
			size += 2;
		NameValuePair result[] = new NameValuePair[size];
		int counter = 0;
		for (Iterator iterator = params_.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			result[counter++] = new NameValuePair(key, (String) params_.get(key));
		}

		if (addContentParams_) {
			result[counter++] = new NameValuePair("Content-Type", "application/x-www-form-urlencoded");
			result[counter++] = new NameValuePair("Content-Length",	(new StringBuilder()).append(params_.size()).toString());
		}
		return result;
	}

	public String getAppendedGetParams() {
		if (params_.size() > 0) {
			StringBuilder result = new StringBuilder();
			String key;
			for (Iterator iterator = params_.keySet().iterator(); iterator.hasNext(); result.append((String) params_.get(key))) {
				key = (String) iterator.next();
				result.append("&");
				result.append(key);
				result.append("=");
			}

			String appendSign = url_.indexOf("?") != -1 ? "&" : "?";
			return (new StringBuilder(String.valueOf(appendSign))).append(result.substring(1)).toString();
		} else {
			return "";
		}
	}

	public String toString() {
		return (new StringBuilder(String.valueOf(url_))).append(getAppendedGetParams()).toString();
	}

	public static void setNoTrustManager() {
		setTrustManager(new TrustAllCertificateSocketFactory());
	}

	public static void setTrustManager(ProtocolSocketFactory _factory) {
		try {
			Protocol.registerProtocol("https", new Protocol("https", _factory,443));
		} catch (Exception e) {
			LOGGER.info(e.getMessage(), e);
		}
	}

}

