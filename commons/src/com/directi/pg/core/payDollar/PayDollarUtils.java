package com.directi.pg.core.payDollar;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


import javax.net.ssl.SSLSocketFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.URL;
import java.net.URLConnection;

import static com.directi.pg.core.payDollar.Elements.*;


/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 12, 2012
 * Time: 1:35:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayDollarUtils
{
    // character encoding
    public final static String charset = "UTF-8";

    private static Logger log = new Logger(PayDollarUtils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayDollarUtils.class.getName());

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

                    log.error("UnsupportedEncodingException---->", e);
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
        public String doPostURLConnection(String strURL, String req) throws PZTechnicalViolationException
        {
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

                if(con instanceof com.sun.net.ssl.HttpsURLConnection){
                    ((com.sun.net.ssl.HttpsURLConnection)con).setSSLSocketFactory((SSLSocketFactory)SSLSocketFactory.getDefault());
                }
                con.setUseCaches(false);
                con.setDoInput(true);
                con.setDoOutput(true);

                // Set request headers for content type and length
                con.setRequestProperty("Content-type","application/x-www-form-urlencoded");
                con.setRequestProperty("Content-length",String.valueOf(strURL.length()));

                out = new BufferedOutputStream(con.getOutputStream());
                byte outBuf[] = req.getBytes(charset);
                out.write(outBuf);
                out.close();

                in = new BufferedInputStream(con.getInputStream());
                result = ReadByteStream(in);
            }
            catch (MalformedURLException e)
            {
                log.error("MalFormed URL Exception while connecting to the PayDollar Gateway",e);
                transactionLogger.error("MalFormed URL Exception while connecting to the PayDollar Gateway",e);

                PZExceptionHandler.raiseTechnicalViolationException(PayDollarUtils.class.getName(),"doPostURLConnection()",null,"common","MalFormed URL Exception while connecting to the PayDollar Gateway", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null,e.getMessage(),e.getCause());
            }
            catch (UnsupportedEncodingException e)
            {
                log.error("UnSupported Encoding Exception while connecting to the PayDollar Gateway",e);
                transactionLogger.error("UnSupported Encoding Exception while connecting to the PayDollar Gateway",e);

                PZExceptionHandler.raiseTechnicalViolationException(PayDollarUtils.class.getName(), "doPostURLConnection()", null, "common", "UnSupported Encoding Exception while connecting to the PayDollar Gateway", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null, e.getMessage(), e.getCause());
            }
            catch (IOException e)
            {
                log.error("IO Exception while connecting to the PayDollar Gateway",e);
                transactionLogger.error("IO Exception while connecting to the PayDollar Gateway",e);

                PZExceptionHandler.raiseTechnicalViolationException(PayDollarUtils.class.getName(), "doPostURLConnection()", null, "common", "IO Exception while connecting to the PayDollar Gateway", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
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
           LinkedList<PayDollarBuf> bufList = new LinkedList<PayDollarBuf>();
           int size = 0;
           byte buf[];
           do {
               buf = new byte[128];
               int num = in.read(buf);
               if (num == -1)
                   break;
               size += num;
               bufList.add(new PayDollarBuf(buf, num));
           } while (true);
           buf = new byte[size];
           int pos = 0;
           for (ListIterator<PayDollarBuf> p = bufList.listIterator(); p.hasNext();) {
               PayDollarBuf b = p.next();
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

    public String generateAutoSubmitForm(String actionUrl, Map<String, String> paramMap) {
		StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<body>");
		//html.append("<script language=\"javascript\">window.onload=function(){document.payForm.submit();}</script>\n");
		html.append("<form name=\"payForm\" action=\"").append(actionUrl).append("\" method=\"post\">\n");

		for (String key : paramMap.keySet()) {
			html.append("<input type=\"hidden\" name=\"" + key + "\" value=\"" + paramMap.get(key) + "\">\n");
		}
        //html.append("<input type=\"submit\" value=\"Pay Now\">\n");
		html.append("</form>\n");
        html.append("<script language=\"javascript\">");
        html.append("document.payForm.submit();");
        html.append("</script>");
        html.append("</body>");
        html.append("</html>");
		return html.toString();
	}


    public static Document createDocumentFromString(String xmlString ) {

        Document doc = null;

        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            doc = docBuilder.parse(new InputSource(new StringReader( xmlString ) ));
        }
        catch (ParserConfigurationException pce) {
            log.error("ParserConfigurationException---->",pce);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            log.error("Exception---->", e);
        }

        return doc;

    }




    public static Map<String, String> ReadXMLResponseForInquiry(Document doc) {


        Map<String, String> responseMap  = new TreeMap<String, String>();
        try {

             NodeList nList = doc.getElementsByTagName(ELEM_RECORD);
             responseMap.put(ELEM_ORDERSTATUS,getTagValue(ELEM_ORDERSTATUS,((Element) nList.item(0))));
             responseMap.put(ELEM_REF,getTagValue(ELEM_REF,((Element) nList.item(0))));
             responseMap.put(ELEM_PAYREF,getTagValue(ELEM_PAYREF,((Element) nList.item(0))));
             responseMap.put(ELEM_SRC,getTagValue(ELEM_SRC,((Element) nList.item(0))));
             responseMap.put(ELEM_PRC,getTagValue(ELEM_PRC,((Element) nList.item(0))));
             responseMap.put(ELEM_ORD,getTagValue(ELEM_ORD,((Element) nList.item(0))));
             responseMap.put(ELEM_HOLDER,getTagValue(ELEM_HOLDER,((Element) nList.item(0))));
             responseMap.put(ELEM_ERRMSG,getTagValue(ELEM_ERRMSG,((Element) nList.item(0))));
             responseMap.put(ELEM_AMT,getTagValue(ELEM_AMT,((Element) nList.item(0))));
            //TODO read from XML




        } catch (Exception e)
        {
            log.error("Exception---->",e);
        }

        return responseMap;
    }

   private static String getTagValue(String sTag, Element eElement) {

        NodeList nlList = null;
        String value  ="";
        if(eElement!=null && eElement.getElementsByTagName(sTag)!=null && eElement.getElementsByTagName(sTag).item(0)!=null)
        {
             nlList =  eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        }
        if(nlList!=null && nlList.item(0)!=null)
        {
            Node nValue = (Node) nlList.item(0);
            value =	nValue.getNodeValue();

        }

        return value;

  }

}

class PayDollarBuf {

	public byte buf[];
	public int size;

	public PayDollarBuf(byte b[], int s) {
		buf = b;
		size = s;
	}



}