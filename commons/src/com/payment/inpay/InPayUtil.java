package com.payment.inpay;

import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayAccountService;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 8/5/14
 * Time: 3:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class InPayUtil
{
    public final static String charset = "UTF-8";
    final static ResourceBundle RB1 = LoadProperties.getProperty("com.directi.pg.InPayServlet");

    private static Logger log = new Logger(InPayUtil.class.getName());


    public static Map<String,String> ReadOrderStatusResponse(String str)
    {
        Document doc = createDocumentFromString(str);

        Map<String, String> responseMap  = new HashMap<String, String>();
        try
        {
            doc.getDocumentElement().getNodeName();
            NodeList nList = doc.getElementsByTagName("result");
                nList = doc.getElementsByTagName("invoice");
                    responseMap.put("status",getTagValue("status",((Element) nList.item(0))));
                    responseMap.put("received-sum",getTagValue("received-sum",((Element) nList.item(0))));
                    responseMap.put("received-currency",getTagValue("received-currency",((Element) nList.item(0))));
        }
        catch (Exception e)
        {
            log.error("Exception in ReadRefundResponse",e);
        }
        return responseMap;
    }

    public static Map<String,String> ReadRefundResponse(String str)
    {
        Document doc = createDocumentFromString(str);

        Map<String, String> responseMap  = new HashMap<String, String>();
        try
        {
            doc.getDocumentElement().getNodeName();
            NodeList nList = doc.getElementsByTagName("error");
            responseMap.put("error",getTagValue("error",((Element) nList.item(0))));
            responseMap.put("message",getTagValue("message",((Element) nList.item(0))));
            responseMap.put("http-code",getTagValue("http-code",((Element) nList.item(0))));
        }
        catch (Exception e)
        {
            log.error("Exception in ReadRefundResponse",e);
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

    private static String getAttributeValue(Element eElement, String sTag)
    {
        String value  ="";
        if(eElement!=null)
        {
            value =	eElement.getAttribute(sTag);
        }
        return value;
    }

    public static Document createDocumentFromString(String xmlString ) {

        Document doc = null;
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            doc = docBuilder.parse(new InputSource(new StringReader( xmlString ) ));
        }
        catch (ParserConfigurationException pce)
        {
            log.error("Exception in createDocumentFromString of PayForAsiaUtils",pce);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            log.error("Exception in createDocumentFromString PayForAsiaUtils",e);
        }
        return doc;
    }

    public static String doPostHTTPSURLConnection(String strURL, String req) throws PZTechnicalViolationException
    {
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        URLConnection con = null;

        try
        {
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            URL url = new URL(strURL);
            try
            {
                con = url.openConnection();
            }
            catch (IOException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("InPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE,null, io.getMessage(), io.getCause());
            }
            if(con instanceof HttpURLConnection)
            {
                ((HttpURLConnection)con).setRequestMethod("POST");
            }
            con.setDoInput(true);
            con.setDoOutput(true);


            con.setRequestProperty("Content-length",String.valueOf (req.length()));
            con.setRequestProperty("Content-Type","application/x-www- form-urlencoded");
            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows 98; DigExt)");

            out = new BufferedOutputStream(con.getOutputStream());
            byte outBuf[] = req.getBytes(charset);
            out.write(outBuf);

            out.close();

            in = new BufferedInputStream(con.getInputStream());
            result = ReadByteStream(in);
        }
        catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("InPayUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null,me.getMessage(),me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("InPayUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (UnsupportedEncodingException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("InPayUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("InPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, ex.getMessage(), ex.getCause());
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
                    PZExceptionHandler.raiseTechnicalViolationException("InPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                } catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("InPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
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
        LinkedList<Inpay> bufList = new LinkedList<Inpay>();
        int size = 0;
        String buffer = null;
        byte buf[];
        try
        {
            do {
                buf = new byte[128];
                int num = in.read(buf);
                if (num == -1)
                    break;
                size += num;
                bufList.add(new Inpay(buf, num));
            } while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<Inpay> p = bufList.listIterator(); p.hasNext();)
            {
                Inpay b = p.next();
                for (int i = 0; i < b.size;) {
                    buf[pos] = b.buf[i];
                    i++;
                    pos++;
                }

            }
            buffer = new String(buf,charset);
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("InPayUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, io.getMessage(), io.getCause());
        }

        return buffer;
    }

    public static void main(String[] a)
    {
        StringBuffer readXml = new StringBuffer();

        StringBuffer refundXml = new StringBuffer();

        readXml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");

        readXml.append("<result>");
        readXml.append("<invoice>");
        readXml.append("<status>created</status>");
        readXml.append("<received-sum>50.00</received-sum>");
        readXml.append("<received-currency>INR</received-currency>");
        readXml.append("</invoice>");
        readXml.append("</result>");

        Document doc =null;

        //Map<String, String> responseMap =  ReadRefundResponse(refundXml.toString());
        Map<String, String> responseMap =  ReadOrderStatusResponse(readXml.toString());

        Set<String> keys = responseMap.keySet();
        //System.out.println("  ");
        for(String key : keys)
        {
            log.debug("  "+key+"="+responseMap.get(key));
        }

    }

    public static String getMD5HashVal(String str) throws PZTechnicalViolationException
    {
        String encryptedString = null;
        byte[] bytesToBeEncrypted;
        try
        {
            // convert string to bytes using a encoding scheme
            bytesToBeEncrypted = str.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] theDigest = md.digest(bytesToBeEncrypted);
            // convert each byte to a hexadecimal digit
            Formatter formatter = new Formatter();
            for (byte b : theDigest) {
                formatter.format("%02x", b);
            }
            encryptedString = formatter.toString().toLowerCase();
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("InPayUtil.java","getMD5HashVal()",null,"commons","UnsupportedEncodingException raised::::", PZTechnicalExceptionEnum.UNSUPPORTING_ENCOADING_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("InPayUtil.java","getMD5HashVal()",null,"commons","UnsupportedEncodingException raised::::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        return encryptedString;
    }

    public static String generateAutoSubmitForm(String actionUrl, Map<String, String> paramMap)
    {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(actionUrl).append("\" method=\"post\">\n");

        for (String key : paramMap.keySet())
        {
            html.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + paramMap.get(key) + "\">\n");
        }
        html.append("</form>\n");
        return html.toString();
    }

    public String getInPayRequest(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZTechnicalViolationException
    {
        final String MERCHANTID = "merchant_id";
        final String ORDERID = "order_id";
        final String AMOUNT = "amount";
        final String CURRENCY = "currency";
        final String ORDERTEXT = "order_text";
        final String FLOWLAYOUT = "flow_layout";
        final String RETURNURL = "return_url";
        final String EMAILID = "buyer_email";
        final String CHECKSUM = "checksum";
        final String COUNTRY = "country";
        final String NAME = "buyer_name";
        final String ADDRESS = "buyer_address";
        final String PENDINGURL = "pending_url";
        final String CANCELURL = "cancel_url";
        final String NOTIFYURL = "notify_url";
        final String testURL = "https://test-secure.inpay.com";
        final String livetURL = "https://secure.inpay.com";
        String URL = "";

        boolean isTest = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).isTest();

        if (isTest)
        {
            URL = testURL;
        }
        else
        {
            URL = livetURL;
        }
        String flowLayout = "multi_page";
        InPayAccount inPayAccount = new InPayAccount();
        Hashtable dataHash = new Hashtable();

        dataHash = inPayAccount.getMidAndSecretKey(commonValidatorVO.getMerchantDetailsVO().getAccountId());

        String merchantid = (String) dataHash.get("mid");
        String secratKey = (String) dataHash.get("secretkey");
        String cMerchantid = URLEncoder.encode(merchantid);
        String cOrderid = URLEncoder.encode(commonValidatorVO.getTrackingid());
        String cAmount = URLEncoder.encode(commonValidatorVO.getTransDetailsVO().getAmount());
        String cCurrency = URLEncoder.encode(commonValidatorVO.getTransDetailsVO().getCurrency());
        String cOrderText = URLEncoder.encode(commonValidatorVO.getTransDetailsVO().getOrderId());
        String cFlowLayout = URLEncoder.encode(flowLayout);
        String cSecretKey = URLEncoder.encode(secratKey);
        String address=commonValidatorVO.getAddressDetailsVO().getStreet()+","+commonValidatorVO.getAddressDetailsVO().getCity()+","+commonValidatorVO.getAddressDetailsVO().getState()+","+commonValidatorVO.getAddressDetailsVO().getCountry();
        String checksumString = "merchant_id="+cMerchantid+"&order_id="+cOrderid+"&amount="+cAmount+"&currency="+cCurrency+"&order_text="+cOrderText+
                "&flow_layout="+cFlowLayout+"&secret_key="+cSecretKey;

        log.debug("checksum combination ---"+checksumString);
        //log.debug("chksum string---"+checksumString);

        Map saleMap = new TreeMap();

        saleMap.put(MERCHANTID,merchantid);
        saleMap.put(ORDERID,commonValidatorVO.getTrackingid());
        saleMap.put(AMOUNT,commonValidatorVO.getTransDetailsVO().getAmount());
        saleMap.put(CURRENCY,commonValidatorVO.getTransDetailsVO().getCurrency());
        saleMap.put(COUNTRY,commonValidatorVO.getAddressDetailsVO().getCountry());
        saleMap.put(ORDERTEXT,commonValidatorVO.getTransDetailsVO().getOrderId());
        saleMap.put(FLOWLAYOUT,flowLayout);
        saleMap.put(RETURNURL,RB1.getString("FRONTEND"));
        saleMap.put(EMAILID,commonValidatorVO.getAddressDetailsVO().getEmail());
        saleMap.put(ADDRESS,address);
        saleMap.put(NAME,commonValidatorVO.getAddressDetailsVO().getFirstname()+" "+commonValidatorVO.getAddressDetailsVO().getLastname());
        saleMap.put(CHECKSUM, getMD5HashVal(checksumString));

        String redirectHTML = generateAutoSubmitForm(URL,saleMap);
        log.debug("inpay form---"+redirectHTML.toString());
        //log.debug("chksum string---"+singleCallPaymentDAO.getMD5HashVal(checksumString));
        return redirectHTML;
    }

    static class Inpay
    {
        public byte buf[];
        public int size;

        public Inpay(byte b[], int s)
        {
            buf = b;
            size = s;
        }
    }
}
