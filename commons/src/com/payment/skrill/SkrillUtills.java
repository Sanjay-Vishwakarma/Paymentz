package com.payment.skrill;

import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
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
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by admin on 03-05-2017.
 */
public class SkrillUtills
{
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.skrill");
    private final static String charset = "UTF-8";
    private static Logger log = new Logger(SkrillUtills.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(SkrillUtills.class.getName());

    public static String doPostHTTPSURLConnection(String strURL, String request) throws PZTechnicalViolationException
    {
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        URLConnection conn = null;
        try
        {
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            URL url = new URL(strURL);
            try
            {
                conn = url.openConnection();
                conn.setConnectTimeout(120000);
                conn.setReadTimeout(120000);
            }
            catch (SSLHandshakeException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("SKrillUtills.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, io.getMessage(), io.getCause());
            }
            if (conn instanceof HttpURLConnection)
            {
                ((HttpURLConnection) conn).setRequestMethod("POST");
            }
            assert conn != null;
            conn.setDoInput(true);
            conn.setDoOutput(true);
            out = new BufferedOutputStream(conn.getOutputStream());
            byte outBuf[] = request.getBytes(charset);
            out.write(outBuf);
            out.close();
            in = new BufferedInputStream(conn.getInputStream());
            result = ReadByteStream(in);
        }
        catch (UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SkrillUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ue.getMessage(), ue.getCause());
        }
        catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SkrillUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON, null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SkrillUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION, null, pe.getMessage(), pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SkrillUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, ex.getMessage(), ex.getCause());
        }
        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("PZConstraintViolationException--->",e);
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
                    PZExceptionHandler.raiseTechnicalViolationException("SkrillUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
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
                    PZExceptionHandler.raiseTechnicalViolationException("SkrillUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }

    private static String ReadByteStream(BufferedInputStream in) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        LinkedList<Skrill> bufList = new LinkedList<Skrill>();
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
                bufList.add(new Skrill(buf, num));
            }
            while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<Skrill> p = bufList.listIterator(); p.hasNext(); )
            {
                Skrill b = p.next();
                for (int i = 0; i < b.size; )
                {
                    buf[pos] = b.buf[i];
                    i++;
                    pos++;
                }
            }
            buffer = new String(buf, charset);
        }
        catch (UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SKrillUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ue.getMessage(), ue.getCause());
        }
        catch (IOException ie)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SKrillUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ie.getMessage(), ie.getCause());
        }
        return buffer;
    }

    public static Map<String, String> getQueryMap(String query)
    {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String[] p = param.split("=");
            String name = p[0];
            if (p.length > 1)
            {
                String value = p[1];
                map.put(name, value);
                transactionLogger.debug(name+":::"+value);
            }
        }

        return map;
    }

    public static void main(String[] args)
    {
        String s = "merchant_id=90392517&\n" +
                "transaction_id=55284&\n" +
                "mb_transaction_id=2233754255&\n" +
                "mb_amount=75.375&\n" +
                "mb_currency=INR&\n" +
                "status=2&\n" +
                "md5sig=C599CBF89F9BA6BB490B5ED96307B14C&\n" +
                "amount=1.00&\n" +
                "pay_from_email=trupti.joshi%40pz.com&\n" +
                "pay_to_email=pranav.d%40pz.com&\n" +
                "currency=EUR";

        /*Map m = getQueryMap(s);
        System.out.println(m.get("pay_from_email"));*/

        try
        {
            transactionLogger.debug(URLDecoder.decode("trupti.joshi%40pz.com"));
            transactionLogger.debug(URLDecoder.decode("trupti.joshi%40pz.com","UTF-8"));
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception--->",e);
        }
    }

    public static Map<String, String> readStepOneResponse(String str) throws PZTechnicalViolationException
    {
        Document doc = createDocumentFromString(str);
        Map<String, String> responseMap = new HashMap<String, String>();
        doc.getDocumentElement().getNodeName();
        NodeList nList = null;
        nList = doc.getElementsByTagName("response");
        responseMap.put("error", getTagValue("error", ((Element) nList.item(0))));
        responseMap.put("error_msg", getTagValue("error_msg", ((Element) nList.item(0))));
        responseMap.put("sid", getTagValue("sid", ((Element) nList.item(0))));
        return responseMap;
    }

    public static Map<String, String> readStepTwoResponse(String str) throws PZTechnicalViolationException
    {
        Document doc = createDocumentFromString(str);
        Map<String, String> responseMap = new HashMap<String, String>();
        doc.getDocumentElement().getNodeName();
        NodeList nList = null;
        nList = doc.getElementsByTagName("response");
        responseMap.put("transaction", getTagValue("transaction", ((Element) nList.item(0))));
        responseMap.put("amount", getTagValue("amount", ((Element) nList.item(0))));
        responseMap.put("currency", getTagValue("currency", ((Element) nList.item(0))));
        responseMap.put("id", getTagValue("id", ((Element) nList.item(0))));
        responseMap.put("status", getTagValue("status", ((Element) nList.item(0))));
        responseMap.put("status_msg", getTagValue("status_msg", ((Element) nList.item(0))));
        return responseMap;
    }

    public static Map<String, String> readStepTworefundResponse(String str) throws PZTechnicalViolationException
    {
        Document doc = createDocumentFromString(str);
        Map<String, String> responseMap = new HashMap<String, String>();
        doc.getDocumentElement().getNodeName();
        NodeList nList = null;
        nList = doc.getElementsByTagName("response");
        responseMap.put("transaction_id", getTagValue("transaction_id", ((Element) nList.item(0))));
        responseMap.put("mb_amount", getTagValue("mb_amount", ((Element) nList.item(0))));
        responseMap.put("mb_transaction_id", getTagValue("mb_transaction_id", ((Element) nList.item(0))));
        responseMap.put("status", getTagValue("status", ((Element) nList.item(0))));
        responseMap.put("status_msg", getTagValue("status_msg", ((Element) nList.item(0))));
        return responseMap;
    }

    public static Map<String, String> readStoreCardResponse(String str) throws PZTechnicalViolationException
    {
        Document doc = createDocumentFromString(str);
        Map<String, String> responseMap = new HashMap<String, String>();
        doc.getDocumentElement().getNodeName();
        NodeList nList = null;
        nList = doc.getElementsByTagName("wallet");
        responseMap.put("error", getTagValue("error", ((Element) nList.item(0))));
        responseMap.put("errormessage", getTagValue("errormessage", ((Element) nList.item(0))));
        responseMap.put("sessionid", getTagValue("sessionid", ((Element) nList.item(0))));
        responseMap.put("customercardid", getTagValue("customercardid", ((Element) nList.item(0))));
        return responseMap;
    }

    private static String getTagValue(String Tag, Element eElement)
    {
        NodeList nlList = null;
        String value = "";
        if (eElement != null && eElement.getElementsByTagName(Tag) != null && eElement.getElementsByTagName(Tag).item(0) != null)
        {
            nlList = eElement.getElementsByTagName(Tag).item(0).getChildNodes();
        }
        if (nlList != null && nlList.item(0) != null)
        {
            Node nValue = nlList.item(0);
            value = nValue.getNodeValue();
        }
        return value;
    }

    public static Document createDocumentFromString(String xmlString) throws PZTechnicalViolationException
    {
        Document document = null;
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            document = docBuilder.parse(new InputSource(new StringReader(xmlString)));
        }
        catch (ParserConfigurationException pce)
        {
            transactionLogger.error("Exception in createDocumentFromString of SKrillUtill", pce);
            PZExceptionHandler.raiseTechnicalViolationException("SkrillUtills.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e)
        {
            transactionLogger.error("Exception in createDocumentFromString SKrillUtill", e);
            PZExceptionHandler.raiseTechnicalViolationException("SkrillUtills.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.SAXEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            transactionLogger.error("Exception in createDocumentFromString SKrillUtill", e);
            PZExceptionHandler.raiseTechnicalViolationException("SkrillUtills.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return document;
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
            for (byte b : theDigest)
            {
                formatter.format("%02x", b);
            }
            encryptedString = formatter.toString().toLowerCase();
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(SkrillUtills.class.getName(), "getMD5HashVal()", null, "common", "UnSupportedEncoding Exception while conecting to InPay", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(SkrillUtills.class.getName(), "getMD5HashVal()", null, "common", "NoSuchAlgorithm Exception while conecting to InPay", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return encryptedString;
    }

    public static String generateAutoSubmitForm(String sid, CommonValidatorVO commonValidatorVO)
    {
       /* StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append("" + RB.getString("SALEURL") + "?sid=" + sid + "").append("\" method=\"POST\">\n");
        html.append("</form>");*/

        String accountId = commonValidatorVO.getMerchantDetailsVO().getAccountId();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String merchantId = gatewayAccount.getMerchantId();
        StringBuilder html = new StringBuilder();

        String status_url = RB.getString("STATUSURL");
        String email = commonValidatorVO.getAddressDetailsVO().getEmail();
        String amount = commonValidatorVO.getTransDetailsVO().getAmount();
        String currency = commonValidatorVO.getTransDetailsVO().getCurrency();
        String trackingID = commonValidatorVO.getTrackingid();
        String return_url = RB.getString("RETURNURL");
        try
        {
            html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>");
            html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append("" + RB.getString("SALEURL") + "?sid=" + sid + "").append("\" method=\"post\">\n");
            html.append("<input type=\"hidden\" name=\"merchant_id\" id=\"merchant_id\" value=\"" + merchantId + "\">\n");
            html.append("<input type=\"hidden\" name=\"status_url\" id=\"status_url\" value=\"" + status_url + "\">\n");
            html.append("<input type=\"hidden\" name=\"pay_from_email\" id=\"pay_from_email\" value=\"" + email + "\">\n");
            html.append("<input type=\"hidden\" name=\"amount\" id=\"amount\" value=\"" + amount + "\">\n");
            html.append("<input type=\"hidden\" name=\"currency\" id=\"currency\" value=\"" + currency + "\">\n");
            html.append("<input type=\"hidden\" name=\"transaction_id\" id=\"transaction_id\" value=\"" + trackingID+ "\">\n");
            html.append("<input type=\"hidden\" name=\"return_url\" id=\"return_url\" value=\"" + return_url + "\">\n");
            html.append("</form>");

            transactionLogger.debug("form----"+html);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception---->",e);
        }

        return html.toString();
    }

    public CommRequestVO getSkrillRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();

        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String merctId = account.getMerchantId();
        String username = account.getFRAUD_FTP_USERNAME();
        String password = account.getFRAUD_FTP_PASSWORD();
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        transDetailsVO.setRedirectUrl(commonValidatorVO.getTransDetailsVO().getRedirectUrl());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount()); //Amount * 100 according to the docs
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTrackingid());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderId());

        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        merchantAccountVO.setMerchantId(merctId);
        merchantAccountVO.setPassword(password);
        merchantAccountVO.setMerchantUsername(username);
        merchantAccountVO.setDisplayName(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        merchantAccountVO.setAliasName(commonValidatorVO.getMerchantDetailsVO().getSiteName());

        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);

        return commRequestVO;
    }

    static class Skrill
    {
        public byte buf[];
        public int size;

        public Skrill(byte b[], int s)
        {
            buf = b;
            size = s;
        }
    }

}


