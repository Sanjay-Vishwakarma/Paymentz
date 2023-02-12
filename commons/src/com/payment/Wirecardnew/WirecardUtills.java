package com.payment.Wirecardnew;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.net.ssl.SSLHandshakeException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 9/17/14
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class WirecardUtills
{
    public final static String charset = "UTF-8";
    private static Logger log = new Logger(WirecardUtills.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(WirecardUtills.class.getName());

    public static String doPostHTTPSURLConnectionClient(String strURL, String req, String authType, String encodedCredentials) throws PZTechnicalViolationException
    {
        transactionLogger.error("strURL-->" + strURL);
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();

          //  HostConfiguration hostConfiguration=httpClient.getHostConfiguration();
         //   hostConfiguration.setProxy("103.95.12.128",1949);
         //   httpClient.setHostConfiguration(hostConfiguration);

            post.addRequestHeader("Authorization", authType + " " + encodedCredentials);
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.addRequestHeader("Cache-Control", "no-cache");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException--" + he.getMessage());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException--"+io.getMessage());
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String doPostHTTPSURLConnection(String strURL, String req,String credentials) throws PZTechnicalViolationException
    {
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        URLConnection con = null;

        try
        {
            //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            //java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            URL url = new URL(strURL);
            try
            {
                con = url.openConnection();
                con.setConnectTimeout(12000);
                con.setReadTimeout(12000);
                con.setRequestProperty("Content-length",String.valueOf (req.length()));
                con.setRequestProperty("Content-Type","application/x-www- form-urlencoded");
                con.setRequestProperty("Authorization","Basic "+credentials);
                con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows 98; DigExt)");

            }
            catch (SSLHandshakeException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("WirecardUtills.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, io.getMessage(), io.getCause());
            }
            if (con instanceof HttpURLConnection)
            {
                //System.out.println("if---");
                ((HttpURLConnection) con).setRequestMethod("POST");
            }
            con.setDoInput(true);
            con.setDoOutput(true);

            out = new BufferedOutputStream(con.getOutputStream());
            byte outBuf[] = req.getBytes(charset);
            out.write(outBuf);

            out.close();

            in = new BufferedInputStream(con.getInputStream());
            result = ReadByteStream(in);
        }
        catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("WirecardUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON, null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("WirecardUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION, null, pe.getMessage(), pe.getCause());
        }
        catch (UnsupportedEncodingException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("WirecardUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, pe.getMessage(), pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("WirecardUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, ex.getMessage(), ex.getCause());
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
                    PZExceptionHandler.raiseTechnicalViolationException("WirecardUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("WirecardUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }

    private static String ReadByteStream(BufferedInputStream in) throws PZTechnicalViolationException
    {
        LinkedList<Wirecard> bufList = new LinkedList<Wirecard>();
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
                bufList.add(new Wirecard(buf, num));
            }
            while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<Wirecard> p = bufList.listIterator(); p.hasNext(); )
            {
                Wirecard b = p.next();
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
            PZExceptionHandler.raiseTechnicalViolationException("WirecardUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }

        return buffer;
    }

    public static Map<String, String> ReadSalesResponse(String str) throws PZTechnicalViolationException
    {
        Document doc = createDocumentFromString(str);

        Map<String, String> responseMap = new HashMap<String, String>();

        doc.getDocumentElement().getNodeName();
        NodeList nList = doc.getElementsByTagName("DIVITIA_BXML");
        nList = doc.getElementsByTagName("payment");
        responseMap.put("merchant-account-id", getTagValue("merchant-account-id", ((Element) nList.item(0))));
        responseMap.put("transaction-id", getTagValue("transaction-id", ((Element) nList.item(0))));
        responseMap.put("request-id", getTagValue("request-id", ((Element) nList.item(0))));
        responseMap.put("transaction-type", getTagValue("transaction-type", ((Element) nList.item(0))));
        responseMap.put("transaction-state", getTagValue("transaction-state", ((Element) nList.item(0))));
        responseMap.put("transaction-type", getTagValue("transaction-type", ((Element) nList.item(0))));
        responseMap.put("statuses", getTagValue("statuses", ((Element) nList.item(0))));
        responseMap.put("card-token", getTagValue("card-token", ((Element) nList.item(0))));
        responseMap.put("parent-transaction-id", getTagValue("parent-transaction-id", ((Element) nList.item(0))));
        responseMap.put("completion-time-stamp", getTagValue("completion-time-stamp", ((Element) nList.item(0))));
        responseMap.put("status code", getTagValue("status code", ((Element) nList.item(0))));
        responseMap.put("description", getTagValue("description", ((Element) nList.item(0))));
        responseMap.put("pareq", getTagValue("pareq", ((Element) nList.item(0))));
        responseMap.put("acs-url", getTagValue("acs-url", ((Element) nList.item(0))));
        responseMap.put("requested-amount", getTagValue("requested-amount", ((Element) nList.item(0))));


        responseMap.put("authorization-code", getTagValue("authorization-code", ((Element) nList.item(0))));

        for (Map.Entry<String,String> entry:responseMap.entrySet())
        {
            String key=entry.getKey();
            String value=entry.getValue();
            transactionLogger.debug("key line 246 :::::"+key);
            transactionLogger.debug("value line 247 :::::"+value);
        }

       /*for ( Map.Entry<String, Tab> entry : hash.entrySet()) {
    String key = entry.getKey();
    Tab tab = entry.getValue();
    // do something with key and/or tab
}*/

        return responseMap;
    }

    private static String getTagValue(String sTag, Element eElement)
    {
        NodeList nlList = null;
        String value = "";
        if (eElement != null && eElement.getElementsByTagName(sTag) != null && eElement.getElementsByTagName(sTag).item(0) != null)
        {
            nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        }
        if (nlList != null && nlList.item(0) != null)
        {
            Node nValue = (Node) nlList.item(0);
            value = nValue.getNodeValue();
        }
        return value;
    }

    private static String getAttributeValue(Element eElement, String sTag)
    {
        String value = "";
        if (eElement != null)
        {
            value = eElement.getAttribute(sTag);
        }
        return value;
    }

    public static Document createDocumentFromString(String xmlString) throws PZTechnicalViolationException
    {
        Document doc = null;
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            doc = docBuilder.parse(new InputSource(new StringReader(xmlString)));
        }
        catch (ParserConfigurationException pce)
        {
            log.error("Exception in createDocumentFromString of WirecardUtills", pce);
            transactionLogger.error("Exception in createDocumentFromString of WirecardUtills", pce);
            PZExceptionHandler.raiseTechnicalViolationException("WirecardUtills.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e)
        {
            // TODO Auto-generated catch block
            log.error("Exception in createDocumentFromString WirecardUtills", e);
            transactionLogger.error("Exception in createDocumentFromString WirecardUtills", e);
            PZExceptionHandler.raiseTechnicalViolationException("WirecardUtills.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.SAXEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            log.error("Exception in createDocumentFromString WirecardUtills", e);
            transactionLogger.error("Exception in createDocumentFromString WirecardUtills", e);
            PZExceptionHandler.raiseTechnicalViolationException("WirecardUtills.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return doc;
    }

    public static Map<String, String> ReadStatusResponse(String str) throws PZTechnicalViolationException
    {

        Map<String, String> responseMap = new HashMap<String, String>();


        try{
            Document doc = WirecardUtills.createDocumentFromString(str);

            XPathFactory xp = XPathFactory.newInstance();
            XPath x1 = xp.newXPath();
            XPathExpression ex = x1.compile("//statuses/status");
            NodeList n = (NodeList) ex.evaluate(doc, XPathConstants.NODESET);
           /* System.out.println("len---"+n.getLength());
            System.out.println("name---"+ n.item(0).getAttributes().getNamedItem("code").getNodeValue());
            System.out.println("name---"+ n.item(0).getAttributes().getNamedItem("description").getNodeValue());*/

            responseMap.put("code", n.item(0).getAttributes().getNamedItem("code").getNodeValue());
            responseMap.put("description",n.item(0).getAttributes().getNamedItem("description").getNodeValue());

        }
        catch (Exception e)
        {
            log.error("Exception in createDocumentFromString WirecardUtills", e);
            transactionLogger.error("Exception in createDocumentFromString WirecardUtills", e);
            PZExceptionHandler.raiseTechnicalViolationException("WirecardUtills.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return responseMap;

}

    public static String get3DConfirmationForm(Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\">" +
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "<input type=\"hidden\" name=\"MD\" value=\"merchant_account_id=0c9558df-a2e8-4fe9-ab10-f650deaeeb94&transaction_type=purchase&nonce3d=3d\">"+
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }

    static class Wirecard
    {
        public byte buf[];
        public int size;

        public Wirecard(byte b[], int s)
        {
            buf = b;
            size = s;
        }
    }


/*
 public static void main(String[] args) throws PZTechnicalViolationException
  {



      String ttt="<statuses>\n" +
              "<status code=\"201.0000\"\n" +
              " description=\"3d-acquirer:The resource was successfully created.\" severity=\"information\"/>\n" +
              " </statuses>";


      try{
          Document doc = WirecardUtills.createDocumentFromString(ttt);

          XPathFactory xp = XPathFactory.newInstance();
          XPath x1 = xp.newXPath();
          XPathExpression ex = x1.compile("//statuses/status");
          NodeList n = (NodeList) ex.evaluate(doc, XPathConstants.NODESET);
          System.out.println("len---"+n.getLength());
          System.out.println("name---"+ n.item(0).getAttributes().getNamedItem("code").getNodeValue());
          System.out.println("name---"+ n.item(0).getAttributes().getNamedItem("description").getNodeValue());

      }
      catch (Exception e)
      {

      }
*/






    /*  String readXml= new String("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
              "<payment xmlns=\"http://www.elastic-payments.com/schema/payment\" xmlns:ns2=\"http://www.elastic-payments.com/schema/epa/transaction\" self=\"https://api-test.wirecard.com/engine/rest/merchants/9105bb4f-ae68-4768-9c3b-3eda968f57ea/payments/a7877187-f55e-44c9-a873-1a53d43a5ac5\"><merchant-account-id ref=\"https://api-test.wirecard.com/engine/rest/config/merchants/9105bb4f-ae68-4768-9c3b-3eda968f57ea\">9105bb4f-ae68-4768-9c3b-3eda968f57ea</merchant-account-id>\n" +
              "<transaction-id>a7877187-f55e-44c9-a873-1a53d43a5ac5</transaction-id>\n" +
              "<request-id>1b3be510-a992-48aa-8af9-6ba4c368980777a0ac</request-id>\n" +
              "<transaction-type>authorization</transaction-type>\n" +
              "<transaction-state>success</transaction-state>\n" +
              "<completion-time-stamp>2017-02-01T11:02:14.000Z</completion-time-stamp>\n" +
              "<statuses><status code=\"201.0000\" description=\"3d-acquirer:The resource was successfully created.\" severity=\"information\"/></statuses>\n" +
              "<csc-code>P</csc-code>\n" +
              "<account-holder>\n" +
              "\t<first-name>John</first-name>\n" +
              "\t<last-name>Doe</last-name>\n" +
              "\t<email>john.doe@test.com</email>\n" +
              "\t<phone>9403043443</phone>\n" +
              "</account-holder>\n" +
              "<card-token>\n" +
              "\t<token-id>4304509873471003</token-id>\n" +
              "\t<masked-account-number>401200******1003</masked-account-number>\n" +
              "</card-token>\n" +
              "<descriptor></descriptor>\n" +
              "<authorization-code>721950</authorization-code>\n" +
              "<api-id>elastic-api</api-id></payment>");

        Document doc = createDocumentFromString(readXml);

      System.out.println("statuses---"+doc.getElementsByTagName("statuses"));

      NodeList list = doc.getElementsByTagName("statuses");
      System.out.println("val---"+getTagValue("statuses", ((Element) list.item(0))));
*/




      /*for (int i = 0; i < list.getLength(); i++)
      {
          Node node = list.item(i);
          if (node.getNodeType() == Node.ELEMENT_NODE)
          {
              Element element = (Element) node;
              System.out.println("s----"+getTagValue("status",element));

              NodeList n = doc.getElementsByTagName("ststus");
              System.out.println("s----"+n.item(0).getAttributes().getNamedItem("code").getNodeValue());
          }
      }*/

        /*Map<String, String> responseMap =  ReadSalesResponse(readXml.toString());
        System.out.println("map----"+responseMap);
        Set<String> keys = responseMap.keySet();
        System.out.println("  ");
        for(String key : keys)
        {
            System.out.println(key+"="+responseMap.get(key));

        }
        String status = "fail";

            if(responseMap.get("RESULT")!=null && responseMap.get("RESULT").equals("OK"))
            {
                status = "success";
            }


        System.out.println("status---"+status);*/
    }

