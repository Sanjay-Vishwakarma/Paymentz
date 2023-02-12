package com.directi.pg.core.paylineVoucher;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
//import com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl;


import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Aug 28, 2012
 * Time: 9:34:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayLineUtils
{

    // character encoding
  public final static String charset = "UTF-8";

  private static Logger log = new Logger(PayLineUtils.class.getName());



  /**
   *
   * @param strURL
   * @param doc
   * @return
   * @throws SystemError
   */
    public Document doPostURLConnection(String strURL, Document doc) throws SystemError{
		String result = null;
		BufferedInputStream in = null;
		BufferedOutputStream out = null;

        Document response =null;
		try {
			URL url = new URL(strURL);
			URLConnection con = url.openConnection();
            /*if (con instanceof HttpsURLConnection) {
				((HttpsURLConnection) con).setHostnameVerifier(new HostnameVerifier() {

                    //@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});
			}*/
			con.setUseCaches(false);
			con.setDoInput(true);
			con.setDoOutput(true);

            Element root = doc.getDocumentElement();
            String req = "load="+root.toString();
            //System.out.println(req);
            out = new BufferedOutputStream(con.getOutputStream());
			byte outBuf[] = req.getBytes(charset);
			out.write(outBuf);
			out.close();


            // write the content into output stream
           /* TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(con.getOutputStream());

            transformer.transform(source, streamResult);
*/



            //in = new BufferedInputStream(con.getInputStream());
            //result = ReadByteStream(in);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		    response = dBuilder.parse(con.getInputStream());
		    response.getDocumentElement().normalize();


		} catch (Exception ex) {

            log.error("Exception during URL Connection=  1" , ex);
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
		return response;
	}


   /**
    *
    * @param strURL
    * @param doc
    * @return
    * @throws SystemError
    */
        public Document doPostHTTPSURLConnection(String strURL, Document doc) throws SystemError{
            Document response = null;
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
                con.setRequestProperty("Content-Type","application/x-www- form-urlencoded");
                //con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows 98; DigExt)");

                /*Element root = doc.getDocumentElement();
                 String req = root.toString();
                 System.out.println(req);
                 out = new BufferedOutputStream(con.getOutputStream());
			     byte outBuf[] = req.getBytes(charset);
			     out.write(outBuf);
			     out.close();*/


            // write the content into output stream
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(con.getOutputStream());

            transformer.transform(source, streamResult);

                //System.out.println("Resp Code:"+con.getResponseCode());
                //System.out.println("Resp Message:"+ con.getResponseMessage());

                //in = new BufferedInputStream(con.getInputStream());
                //result = ReadByteStream(in);

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		        response = dBuilder.parse(con.getInputStream());
		        response.getDocumentElement().normalize();


            } catch (Exception ex) {

                log.error("Exception during URL Connection= 2 ====" + ex);
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
            if (response == null)
                return null;
            else
                return response;
        }



}
