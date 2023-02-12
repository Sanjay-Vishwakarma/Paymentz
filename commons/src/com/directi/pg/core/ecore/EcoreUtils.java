package com.directi.pg.core.ecore;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
//import com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: Aug 20, 2012
 * Time: 8:38:40 PM
 * To change this template use File | Settings | File Templates.
 * Copyright: Admin
 *
 */
public class EcoreUtils
{


  // character encoding
  public final static String charset = "UTF-8";

  private static Logger log = new Logger(EcoreUtils.class.getName());
  private static TransactionLogger transactionLogger = new TransactionLogger(EcoreUtils.class.getName());

 public static void main (String args[])
 {

    /* String s="<response><operation>01</operation><resultCode>7</resultCode><merNo>88888</merNo><billNo>Order851</billNo><currency>USD</currency><amount>100.67</amount><dateTime>20121106021858</dateTime><paymentOrderNo>888881420625</paymentOrderNo><remark>Payment Declined(Your card was declined several times, please use different card or deposit in 24 hours.Thank you)</remark><md5Info>188512A009FF710EDC72BCACE751A88D</md5Info><billingDescriptor></billingDescriptor></response>";
     String []result = getResArr(s);
     String responseXMLString = result[0];
     //responseXMLString.substring();
     for(int i=0; i<result.length;i++)
     {

        System.out.println("result :" + i + "=" + result[i].trim());
     }
     System.out.println("size :" + + result.length);
     EcoreUtils n = new EcoreUtils();
     n.getEcoreResponseVO(responseXMLString);
*/

 }
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
    public static String joinMapValue(Map<String, String> map, char connector) {
		StringBuffer b = new StringBuffer();
        int cnt = 0;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			b.append(entry.getKey());
			b.append('=');
			if (entry.getValue() != null) {
				b.append(entry.getValue());
			}
			cnt++;
            if(cnt<map.size())
            {
                b.append(connector);
            }
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

					log.error("UnsupportedEncodingException---->",e);
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
    public static String doPostHTTPSURLConnection(String strURL, String req) throws PZTechnicalViolationException
    {
        OutputStreamWriter outSW = null;
        BufferedReader in = null;
        String strResponse="";
		try {

            URL url = new URL(strURL);
            URLConnection connection = url.openConnection();

            if(connection instanceof HttpURLConnection)
            {
                ((HttpURLConnection)connection).setRequestMethod("POST");
            }

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Set request headers for content type and length
            connection.setRequestProperty("Content-type","text/xml");

            outSW = new OutputStreamWriter(
                    connection.getOutputStream());
            outSW.write(req);
            outSW.close();

            in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));
            String decodedString;
            while ((decodedString = in.readLine()) != null)
            {
                strResponse = strResponse + decodedString;
            }
            in.close();


		}
        catch (MalformedURLException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EcoreUtils.class.getName(),"doPostHTTPSURLConnection()",null,"common","MalFormed URL Exception during URL Connection:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null,e.getMessage(),e.getCause());
        }
        catch (ProtocolException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EcoreUtils.class.getName(),"doPostHTTPSURLConnection()",null,"common","Protocol Exception during URL Connection:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EcoreUtils.class.getName(), "doPostHTTPSURLConnection()", null, "common", "IO Exception during URL Connection:::", PZTechnicalExceptionEnum.IOEXCEPTION, null,e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EcoreUtils.class.getName(),"doPostHTTPSURLConnection()",null,"common","MalFormed URL Exception during URL Connection:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null,e.getMessage(),e.getCause());
        }
        finally {
			if (outSW != null) {
				try {
					outSW.close();
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
		if (strResponse == null)
			return "";
		else
			return strResponse;
	}


    /**
     *
     * @param in
     * @return
     * @throws java.io.IOException
     */
    private static String ReadByteStream(BufferedInputStream in) throws IOException {
		LinkedList<EcoreBuf> bufList = new LinkedList<EcoreBuf>();
		int size = 0;
		byte buf[];
		do {
			buf = new byte[128];
			int num = in.read(buf);
			if (num == -1)
				break;
			size += num;
			bufList.add(new EcoreBuf(buf, num));
		} while (true);
		buf = new byte[size];
		int pos = 0;
		for (ListIterator<EcoreBuf> p = bufList.listIterator(); p.hasNext();) {
			EcoreBuf b = p.next();
			for (int i = 0; i < b.size;) {
				buf[pos] = b.buf[i];
				i++;
				pos++;
			}

		}

		return new String(buf,charset);
	}

    public static String createTransUpdateRequestXML(String processType, String accountId, String accountAuth, String transactionId, String referenceId) {
        String xmlRequest="<?xml version=\"1.0\" encoding=\"utf-8\"?><Request type=\""+processType+"\">" +
                "<AccountID>"+accountId+"</AccountID>" +
                "<AccountAuth>"+accountAuth+"</AccountAuth><Transaction>";
        if(referenceId != null){
            xmlRequest = xmlRequest +"<Reference>"+referenceId+"</Reference>";
        }
        if(transactionId != null){
            xmlRequest = xmlRequest + "<TransactionID>"+transactionId+"</TransactionID>";
        }
        xmlRequest = xmlRequest + "</Transaction> </Request>";
        return xmlRequest;
    }

    public static String createTransactionRequestXML(EcoreRequestVO ecoreRequest, String processType) {
        GenericCardDetailsVO cardDetails = ecoreRequest.getCardDetails();
        EcoreAddressDetailsVO address = ecoreRequest.getBillingAddr();
        EcoreTransDetailsVO transaction = ecoreRequest.getTransDetails();
        String xmlRequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?><Request type=\""+processType+"\">" +
                "<AccountID>"+transaction.getMerNo()+"</AccountID>" +
                "<AccountAuth>"+address.getMd5key()+"</AccountAuth>" +
                "<Transaction><Reference>"+transaction.getOrderId()+"</Reference>" +
                "<Amount>"+transaction.getAmount()+"</Amount>" +
                "<Currency>"+transaction.getCurrency()+"</Currency>" +
                " <Email>"+address.getEmail()+"</Email> " +
                "<IPAddress>"+address.getIp()+"</IPAddress>" +
                "<Phone>"+address.getPhone()+"</Phone>" +
                "<FirstName>"+address.getFirstname()+"</FirstName>" +
                "<LastName>"+address.getLastname()+"</LastName>" +
                "<Address>"+address.getStreet()+"</Address>" +
                "<City>"+address.getCity()+"</City>" +
                "<State>"+address.getState()+"</State>" +
                "<PostCode>"+address.getZipCode()+"</PostCode>" +
                "<Country>"+address.getCountry()+"</Country>" +
                "<CardNumber>"+cardDetails.getCardNum()+"</CardNumber>" +
                "<CardExpMonth>"+cardDetails.getExpMonth()+"</CardExpMonth>" +
                "<CardExpYear>"+cardDetails.getExpYear()+"</CardExpYear>" +
                "<CardCVV>"+cardDetails.getcVV()+"</CardCVV> </Transaction> </Request>";
        return xmlRequest;
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

    public EcoreResponseVO getEcoreResponseVOInquiry(String xmlResponseString)
    {
        return null;
    }

    public static EcoreResponseVO getEcoreResponseVO(String xmlResponseString) throws PZTechnicalViolationException
    {
        EcoreResponseVO res= new EcoreResponseVO();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        String strValue = null;
        try
        {
            builder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EcoreUtils.class.getName(),"getEcoreResponseVO()",null,"common","ParserConfigurationException while parsing the XML response::",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        Document document = null;
        try
        {
            document = builder.parse(new InputSource(new StringReader(xmlResponseString)));
        }
        catch (SAXException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EcoreUtils.class.getName(), "getEcoreResponseVO()", null, "common", "SAX Exception while parsing the XML response::", PZTechnicalExceptionEnum.SAXEXCEPTION, null,e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EcoreUtils.class.getName(), "getEcoreResponseVO()", null, "common", "IO Exception while parsing the XML response::", PZTechnicalExceptionEnum.IOEXCEPTION, null,e.getMessage(), e.getCause());
        }
        Element rootElement = document.getDocumentElement();
        strValue = getTagValue("ResponseCode", rootElement);
        if(strValue !=null)
        {
            res.setResponseCode(strValue.trim());
        }
        strValue = getTagValue("Description", rootElement);
        if(strValue !=null)
        {
            res.setDescription(strValue.trim());
        }
        strValue = getTagValue("Reference", rootElement);
        if(strValue !=null)
        {
            res.setReference(strValue.trim());
        }
        strValue = getTagValue("TransactionID", rootElement);
        if(strValue !=null)
        {
            res.setTransactionID(strValue.trim());
        }
        strValue = getTagValue("ProcessingTime", rootElement);
        if(strValue !=null)
        {
            res.setProcessingTime(strValue.trim());
        }
        strValue = getTagValue("StatusCode", rootElement);
        if(strValue !=null)
        {
            res.setStatusCode(strValue.trim());
        }
        strValue = getTagValue("StatusDescription", rootElement);
        if(strValue !=null)
        {
            res.setStatusDescription(strValue.trim());
        }
        strValue = getTagValue("AuthCode", rootElement);
        if(strValue !=null)
        {
            res.setAuthCode(strValue.trim());
        }
        strValue = getTagValue("ScrubResult", rootElement);
        if(strValue !=null)
        {
            res.setScrubResult(strValue.trim());
        }
        strValue = getTagValue("AVSResult", rootElement);
        if(strValue !=null)
        {
            res.setAVSResult(strValue.trim());
        }
        strValue = getTagValue("CVVResult", rootElement);
        if(strValue !=null)
        {
            res.setCVVResult(strValue.trim());
        }
        strValue = getTagValue("CustomerID", rootElement);
        if(strValue !=null)
        {
            res.setCustomerID(strValue.trim());
        }

        return res;
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

    private static String getAttributeValue(Element eElement, String sTag)
    {


        String value  ="";

        if(eElement!=null)
        {

            value =	eElement.getAttribute(sTag);

        }

        return value;

    }

}


class EcoreBuf {

	public byte buf[];
	public int size;

	public EcoreBuf(byte b[], int s) {
		buf = b;
		size = s;
	}



}