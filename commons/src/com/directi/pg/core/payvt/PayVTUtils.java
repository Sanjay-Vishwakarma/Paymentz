package com.directi.pg.core.payvt;

//import com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnection;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
//import com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
//import com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl;


import javax.net.ssl.HttpsURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.TreeMap;
import java.util.Map;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.UnsupportedEncodingException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.net.URLConnection;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: Aug 20, 2012
 * Time: 8:38:40 PM
 * To change this template use File | Settings | File Templates.
 * Copyright: Admin
 *
 */
public class PayVTUtils
{


  // character encoding
  public final static String charset = "UTF-8";

  private static Logger log = new Logger(PayVTUtils.class.getName());
  private static TransactionLogger transactionLogger = new TransactionLogger(PayVTUtils.class.getName());
 /**
  *
  * @param valueVo
  * @param keyVo
  * @return
  */
  public String createQueryStr(String[] valueVo, String[] keyVo) {

		Map<String, String> map = new TreeMap<String, String>();
		for (int i = 0; i < keyVo.length; i++) {
			map.put(keyVo[i], valueVo[i]);
		}

		return joinMapValue(map, '&');
	}


   /**
    *
    * @param map
    * @param connector
    * @return
    */
    public String joinMapValue(Map<String, String> map, char connector) {
		StringBuffer b = new StringBuffer();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			b.append(entry.getKey());
			b.append('=');
			if (entry.getValue() != null) {
				b.append(entry.getValue());
			}
			b.append(connector);
		}
		return b.toString();
	}

    /**
     *
     * @param map
     * @param connector
     * @return
     */
    public String joinMapValueBySpecial(Map<String, String> map, char connector) {
		StringBuffer b = new StringBuffer();
		for (Map.Entry<String, String> entry : map.entrySet()) {

			b.append(entry.getKey());
			b.append('=');
			if (entry.getValue() != null) {
				try {
					b.append(java.net.URLEncoder.encode(entry.getValue(),charset));
				} catch (UnsupportedEncodingException e) {

					log.error("UnsupportedEncodingException--->",e);
				}
			}
			b.append(connector);
		}
		return b.toString();
	}


    /**
     *
     * @param strURL
     * @param req
     * @return
     */
    public String doPostURLConnection(String strURL, String req) throws SystemError{
		String result = null;
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			URL url = new URL(strURL);
			URLConnection con = url.openConnection();
            	/*		if (con instanceof HttpsURLConnection) {
				((HttpsURLConnection) con).setHostnameVerifier(new HostnameVerifier() {
					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});
			}*/
			con.setUseCaches(false);
			con.setDoInput(true);
			con.setDoOutput(true);
			out = new BufferedOutputStream(con.getOutputStream());
			byte outBuf[] = req.getBytes(charset);
			out.write(outBuf);
			out.close();
                    
			in = new BufferedInputStream(con.getInputStream());
			result = ReadByteStream(in);
		} catch (Exception ex) {

            log.error("Exception during URL Connection=  1" + ex);
            System.out.print(ex);

            throw new SystemError("Exception during URL Connection");


		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		if (result == null)
			return "";
		else
			return result;
	}



   /**
    *
    * @param strURL
    * @param req
    * @return
    */
    public String doPostHTTPSURLConnection(String strURL, String req) throws PZTechnicalViolationException
    {
		String result = null;
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {

            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            URL url = new URL(strURL);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);

            con.setRequestMethod("POST");

            //con.setRequestProperty("Content-length",String.valueOf (req.length()));
            //con.setRequestProperty("Content-Type","application/x-www- form-urlencoded");
            //con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows 98; DigExt)");

			out = new BufferedOutputStream(con.getOutputStream());
			byte outBuf[] = req.getBytes(charset);
			out.write(outBuf);
			out.close();

            log.debug("Resp Code:"+con.getResponseCode());
            log.debug("Resp Message:"+ con.getResponseMessage());
            
			in = new BufferedInputStream(con.getInputStream());
			result = ReadByteStream(in);
		}
        catch (MalformedURLException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayVTUtils.class.getName(),"doPostHTTPSURLConnection()",null,"common","MalFormed URL exception while connecting to PayVT", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null,e.getMessage(),e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayVTUtils.class.getName(), "doPostHTTPSURLConnection()", null, "common", "Unsupported  Encoding exception while connecting to PayVT", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (ProtocolException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayVTUtils.class.getName(), "doPostHTTPSURLConnection()", null, "common", "Protocol exception while connecting to PayVT", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayVTUtils.class.getName(), "doPostHTTPSURLConnection()", null, "common", "IO exception while connecting to PayVT", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		if (result == null)
			return "";
		else
			return result;
	}


    /**
     *
     * @param in
     * @return
     * @throws IOException
     */
    private static String ReadByteStream(BufferedInputStream in) throws IOException {
		LinkedList<PayVTBuf> bufList = new LinkedList<PayVTBuf>();
		int size = 0;
		byte buf[];
		do {
			buf = new byte[128];
			int num = in.read(buf);
			if (num == -1)
				break;
			size += num;
			bufList.add(new PayVTBuf(buf, num));
		} while (true);
		buf = new byte[size];
		int pos = 0;
		for (ListIterator<PayVTBuf> p = bufList.listIterator(); p.hasNext();) {
			PayVTBuf b = p.next();
			for (int i = 0; i < b.size;) {
				buf[pos] = b.buf[i];
				i++;
				pos++;
			}

		}

		return new String(buf,charset);
	}


    public static String[] getResArr(String str) {
		String regex = "(.*?cupReserved\\=)(\\{[^}]+\\})(.*)";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);

		String reserved = "";
		if (matcher.find()) {
			reserved = matcher.group(2);
		}

		String result = str.replaceFirst(regex, "$1$3");
		String[] resArr = result.split("&");
		for (int i = 0; i < resArr.length; i++) {
			if ("cupReserved=".equals(resArr[i])) {
				resArr[i] += reserved;
			}
		}
		return resArr;
	}

}


class PayVTBuf {

	public byte buf[];
	public int size;

	public PayVTBuf(byte b[], int s) {
		buf = b;
		size = s;
	}



}