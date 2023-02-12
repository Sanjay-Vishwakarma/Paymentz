
package com.directi.pg.core.credorax;

import com.directi.pg.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Sample Credorax API call for Operation 1 - Sale
 * This example used only standard JDK API.
 * This is for demonstration purposes only.
 * It could be dramatically simplified by using external utility libraries like apache commons and http client.
 * @author apoter
 * @version 1.0.0
 */
public class CredoraxApiExample {
	private static Logger logger=new Logger(CredoraxApiExample.class.getName());
	/**
	 * Character encoding. Credorax API currently supports only ASII
	 */
	private static final String CHARSET = "ASCII";

	private final String gateUrl;
	private final String secretKey;

	/**
	 * @param gateUrl - full URL of Credorax gateway
	 * @param secretKey - secure string for MD5 calculation
	 */
	public CredoraxApiExample(String gateUrl, String secretKey){
		this.gateUrl = gateUrl;
		this.secretKey = secretKey;
	}
	
	public static void main(String[] args) throws Exception {
		// Use gateway URL and secret code provided in integration email
		CredoraxApiExample api = new CredoraxApiExample("https://intconsole.credorax.com/intenv/service/gateway", "30361608");

        // All submission parameters goes here
        Map<String, String> parameters = new HashMap<String, String>();
        // Merchant code
        parameters.put("M", "80379315");
        // Oparation - Sale
        parameters.put("O", "2");
        // Request ID
        parameters.put("a1", ""+ System.currentTimeMillis());
        // Billing amount $10.07
        parameters.put("a4", "1007");
        // Card Number
        parameters.put("b1", "4111111111111111");
        // Card type ID - VISA
        parameters.put("b2", "1");
        // Card expiration month
        parameters.put("b3", "01");
        // Card expiration year
        parameters.put("b4", "14");
        // Card secure code
        parameters.put("b5", "092");
        // Cardholder Name
        parameters.put("c1", "John Doe");
        // email address
        parameters.put("c3", "JohnDoe@yahoo.com");
        // IP address
        parameters.put("d1", InetAddress.getLocalHost().getHostAddress());
        // echo, if empty transaction will be not approved in integration environment
        parameters.put("d2", "approve");

        //parameters1.put("g2", "1A182748");
        //parameters1.put("g3", "0");
        //parameters1.put("g4", "1372875078400");

        // do the call
        /*
           * gateway response should be something like this:
           * M=ED6E2II1&O=1&T=08%2F11%2F2011+10%3A47%3A29&V=413&a1=1313074044524&a2=2&a4=1007&a5=EUR&b1=4...1111&d2=approve&h5=99&z1=1810&z2=0&z3=APPROVED&z4=123456&z5=A95&z6=00&z9=-&K=2a7e45f51c603e8d4c0bb8b210a5cd6f
           */
        api.call(parameters);

    }
	
	/**
	 * do API call and print gateway response 
	 * @param parameters 
	 */
	public void call(Map<String, String> parameters){
		HttpURLConnection connection = null;
	      try {
	    	String urlParameters = this.getPostUrlParameters(parameters);
	    	//System.out.println("Request to send [" + urlParameters + "]");
	    	URL url = new URL(this.gateUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Language", "en-US"); 
			connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));			
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setUseCaches (false);
		    connection.setDoInput(true);
		    connection.setDoOutput(true);
			
			//Send request
		    DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
		    wr.writeBytes(urlParameters);
		    wr.flush ();
		    wr.close ();
		    int responseCode = connection.getResponseCode();
		    //System.out.println("HTTP response: " + responseCode + " " + connection.getResponseMessage());
		    if (connection.getResponseCode() >= 400) {  
		         /* error from server */  
		    	String response = getResponse(connection.getErrorStream());  
		    	System.err.println("Error responce from server");
		    	System.err.println(response.toString());
		     } else {  
		    	 String response = getResponse(connection.getInputStream());  
		    	 //System.out.println("Raw gateway response [" + response+ "]");
		    	 Map<String, String> responseParams = parseResponse(response);
		    	 // handle gateway response here
		    	 printGatewayResponse(responseParams);
		     }  
		} catch (MalformedURLException e) {
			  logger.error("MalformedURLException---->",e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			  logger.error("IOException---->", e);
			throw new RuntimeException(e);
		} finally {
			if(connection != null){
				connection.disconnect();
			}
		}
	}
	
	
	/**
	 * utility method to print response parameters
	 * @param responseParams
	 */
	
	void printGatewayResponse(Map<String, String> responseParams) {
		for (String key : responseParams.keySet()) {
			String value = responseParams.get(key);
			//System.out.println(key + "=" + value);
		}
	}

	/**
	 * utility method to assemble gateway response to String
	 * @param is - input stream
	 * @return String representation of server response
	 * @throws java.io.IOException
	 */
	
	String getResponse(InputStream is) throws IOException{
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer(); 
        while((line = rd.readLine()) != null) {
          response.append(line);
          response.append('\r');
        }
        rd.close();
        return response.toString();
	}
	
	/**
	 * utility method to parse URL encoded gateway response
	 * @param response
	 * @return
	 * @throws java.io.UnsupportedEncodingException
	 */
	Map<String, String> parseResponse(String response) throws UnsupportedEncodingException {
		Map<String, String> params = new LinkedHashMap<String, String>();
		String[] pairs = response.trim().split("\\&");
	    for (int i = 0; i < pairs.length; i++) {
	      String[] fields = pairs[i].split("=");
	      String name = URLDecoder.decode(fields[0], CHARSET);
	      String value = null;
	      if(fields.length > 1){
	    	  value = URLDecoder.decode(fields[1], CHARSET);
	      }
	      params.put(name, value);
	    }
	    return params;
	}
	
	/**
	 * Utility method for building URL encoded request parameters
	 * @param map
	 * @return
	 * @throws java.io.UnsupportedEncodingException
	 */
	String getPostUrlParameters(Map<String, String> map) throws UnsupportedEncodingException {
		// Use TreeMap to sort parameters in alpha numeric order
		TreeMap<String, String> sortedMap = new TreeMap<String, String>(map);
		String md5 = doGetPackageSignature(sortedMap.values());
		StringBuffer buff = new StringBuffer();
		buff.append("K=").append(URLEncoder.encode(md5, CHARSET));
		for (String key : sortedMap.keySet()) {
			String value = sortedMap.get(key);
			buff.append('&').append(key).append('=').append(URLEncoder.encode(value, CHARSET));
		}
		return buff.toString();
	}
	
	/**
	 * Utility method to calculate package signature MD5
	 * @param sorted
	 * @return
	 */
	String doGetPackageSignature(Iterable<String> sorted) {
		MessageDigest algorithm;
		try {
			StringBuffer b = new StringBuffer();
			algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			Charset charset = Charset.forName(CHARSET);
			for (String value : sorted) {
				algorithm.update(value.getBytes(CHARSET));
				b.append(value);
			}
			// append secret key to the end
			algorithm.update(this.secretKey.getBytes(CHARSET));
			b.append(this.secretKey);
			//System.out.println("String for MD5 calculation [" + b.toString()+ "]");
			return new String(getHexString(algorithm.digest()));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Hex string utility
	 */
	
	static final byte[] HEX_CHAR_TABLE = { 
			(byte) '0', (byte) '1', (byte) '2',	(byte) '3', (byte) '4', (byte) '5', 
			(byte) '6', (byte) '7',	(byte) '8', (byte) '9', (byte) 'a', (byte) 'b', 
			(byte) 'c',	(byte) 'd', (byte) 'e', (byte) 'f' };

	static String getHexString(byte[] raw) throws UnsupportedEncodingException {
		byte[] hex = new byte[2 * raw.length];
		int index = 0;

		for (byte b : raw) {
			int v = b & 0xFF;
			hex[index++] = HEX_CHAR_TABLE[v >>> 4];
			hex[index++] = HEX_CHAR_TABLE[v & 0xF];
		}
		return new String(hex, CHARSET);
	}

}
