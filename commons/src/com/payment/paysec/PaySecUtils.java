package com.payment.paysec;

import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Admin on 4/1/2016.
 */
public class PaySecUtils
{
    final static ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.PaysecServlet");
    private static Logger logger = new Logger(PaySecUtils.class.getName());
    private static String CALLBACK_URL = rb.getString("CALLBACK_URL");

    /*public String getPaySecRequest(CommonValidatorVO commonValidatorVO) throws NoSuchAlgorithmException
    {
        System.out.println("in side Paysec Utils");
        final String VERSION = "version";
        final  String CURRENCY = "v_currency";
        final  String AMOUNT = "v_amount";
        final  String FIRSTNAME = "v_firstname";
        final  String LASTNAME = "v_lastname";
        final  String EMAIL = "v_billemail";
        //final  String STATE = "v_shipstate";
        //final  String POST = "v_shippost";

        final  String BSTREET = "v_billstreet";
        final  String BCITY = "v_billcity";
        final  String BSTATE = "v_billstate";
        final  String BPOST = "v_billpost";
        final  String BCOUNTRY = "v_billcountry";
        final  String BPHONE = "v_billphone";
        final  String MERCHANTID = "CID";
        final  String CARTID = "v_CartID";
        final  String SIGNATURE = "signature";
        final  String BCALLBACKURL = "v_callbackurl";
        final  String PRODUCT_CODE = "v_productcode";
        //final  String MERCHANT_KEY = "merchantKey";

        //final  String TESTURL = "https://merchant.chopstickpay.com/API/cpCheckCompany.asp";
        final  String TESTURL = "http://203.112.226.178/GUX/GPost";
        String redirectHTML = "";
        try
        {


            GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
            //GatewayAccount signaturekey = GatewayAccountService.getGatewayAccount(account.getFRAUD_FTP_PATH());
            String signaturekey = account.getPassword();

            PaySecUtils paySecUtils = new PaySecUtils();
            Map authmap = new TreeMap();

            authmap.put(VERSION, "1.0");
            authmap.put(CURRENCY, URLEncoder.encode(account.getCurrency(),"UTF-8"));
            authmap.put(AMOUNT, URLEncoder.encode(commonValidatorVO.getTransDetailsVO().getAmount(),"UTF-8"));
            authmap.put(FIRSTNAME, URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getFirstname(),"UTF-8"));
            authmap.put(LASTNAME, URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getLastname(),"UTF-8"));
            authmap.put(EMAIL, URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getEmail(),"UTF-8"));
            //authmap.put(STATE, commonValidatorVO.getAddressDetailsVO().getState());
            //authmap.put(POST, commonValidatorVO.getAddressDetailsVO().getZipCode());

            authmap.put(BSTREET, URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getStreet(),"UTF-8"));
            authmap.put(BCITY, URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getCity(),"UTF-8"));
            authmap.put(BSTATE, commonValidatorVO.getAddressDetailsVO().getState());
            authmap.put(BPOST, commonValidatorVO.getAddressDetailsVO().getZipCode());
            authmap.put(BCOUNTRY, URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getCountry(),"UTF-8"));
            authmap.put(BPHONE, URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getPhone(),"UTF-8"));
            authmap.put(MERCHANTID, account.getMerchantId());
            authmap.put(CARTID, URLEncoder.encode(commonValidatorVO.getTrackingid(),"UTF-8"));
            authmap.put(PRODUCT_CODE, URLEncoder.encode(commonValidatorVO.getTransDetailsVO().getOrderId(),"UTF-8"));
            //authmap.put(MERCHANT_KEY, signaturekey);

            authmap.put(SIGNATURE, URLEncoder.encode(paySecUtils.generateMD5ChecksumDirectKit(signaturekey.toUpperCase(), account.getMerchantId().toUpperCase(), commonValidatorVO.getTrackingid(), paySecUtils.getAmount(commonValidatorVO.getTransDetailsVO().getAmount()), account.getCurrency().toUpperCase()),"UTF-8"));
            authmap.put(BCALLBACKURL, URLEncoder.encode("http://staging.<hostname>.com/transaction/PaysecBackendNotification","UTF-8"));

            logger.debug("authmap---paysec---" + authmap);
            logger.debug("Signature for Paysec------" + SIGNATURE);

            String reqParams = joinMapValue(authmap,'&');
            logger.debug("reqParams---"+reqParams);

            URL url = new URL(TESTURL);
            //URLConnection connection = url.openConnection();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            *//*if (connection instanceof HttpURLConnection)
            {
                ((HttpURLConnection) connection).setRequestMethod("POST");
            }*//*

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            //connection.setRequestProperty("Content-length", String.valueOf(TESTURL.length()));

            OutputStreamWriter outSW = new OutputStreamWriter(
                    connection.getOutputStream());
            outSW.write(reqParams.toString());


            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String strResponse = "";
            String decodedString;
            while ((decodedString = in.readLine()) != null)
            {
                strResponse = strResponse + decodedString;
            }
            in.close();
            outSW.close();

            logger.debug("response---"+strResponse);

            *//*String[] strToken = strResponse.split(":");

            String token = strToken[1].replace("}", "");


            logger.debug("response token---"+token);
            logger.debug("response ---"+strToken[1]);*//*

            //authmap.put("token",token);
            redirectHTML = generateAutoSubmitForm(TESTURL,authmap);
        }
        catch (Exception e)
        {
            logger.error("paysec exception-=--",e);
        }
        //log.debug("chksum string---"+singleCallPaymentDAO.getMD5HashVal(checksumString));
        return redirectHTML;
    }*/

    public static String generateAutoSubmitForm(String actionUrl, Map<String, String> paramMap)
    {
        //System.out.println("urll---"+actionUrl);
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

    private static String getString(byte buf[])
    {
        StringBuffer sb = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++)
        {
            int h = (buf[i] & 0xf0) >> 4;
            int l = (buf[i] & 0x0f);
            sb.append(new Character((char) ((h > 9) ? 'a' + h - 10 : '0' + h)));
            sb.append(new Character((char) ((l > 9) ? 'a' + l - 10 : '0' + l)));
        }
        return sb.toString();
    }

    public static String generateMD5ChecksumDirectKit(String merchantkey, String merchantid, String cartid, int amount, String currency) throws NoSuchAlgorithmException
    {
        String str = merchantkey + ";" + merchantid + ";" + cartid + ";" + amount + ";" + currency;
        logger.error("STR Combination---"+str);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        return generatedCheckSum;
    }

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
            if (cnt < map.size())
            {
                b.append(connector);
            }
        }
        return b.toString();
    }

    static String hash256(String sampleStr) throws NoSuchAlgorithmException
    {
        String sha256hex = DigestUtils.sha256Hex(sampleStr);
        logger.error("sha256Hex : " + sha256hex);
        return sha256hex;
    }

    public static String generateHash(String plainText, String key)
    {
        String hashValue = BCrypt.hashpw(plainText, key);
        hashValue = hashValue.replace(key, "");
        return (hashValue);
    }

    public static String generateAutoSubmitForm(Comm3DResponseVO commResponseVO)
    {
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" + commResponseVO.getUrlFor3DRedirect() + "\">" +
                "<input type=\"hidden\" name=\"token\" value=\"" + commResponseVO.getResponseHashInfo() + "\">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }

    public String getPaySecRequest(CommonValidatorVO commonValidatorVO) throws NoSuchAlgorithmException
    {
        //String url = "http://203.112.226.178/GUX/GPost";
        String url = "https://pay.paysec.com/GUX/GPost";
        StringBuffer stringParams = new StringBuffer();
        //String v_callbackurl = CALLBACK_URL;

        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        //GatewayAccount signaturekey = GatewayAccountService.getGatewayAccount(account.getFRAUD_FTP_PATH());
        String signaturekey = account.getPassword();
        Map authmap = new TreeMap();
        String redirectHTML = "";
        JSONObject json = null;
        Float fAmount = Float.parseFloat(commonValidatorVO.getTransDetailsVO().getAmount());
        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);
        logger.debug("two decimal amount paysec---" + df.format(fAmount));
        try
        {
            //stringParams.append("version").append("=").append(URLEncoder.encode("1.0", "UTF-8")).append("&");
            stringParams.append("CID").append("=").append(URLEncoder.encode(account.getMerchantId(), "UTF-8")).append("&");
            stringParams.append("v_CartID").append("=").append(URLEncoder.encode(commonValidatorVO.getTrackingid(), "UTF-8")).append("&");
            stringParams.append("v_currency").append("=").append(URLEncoder.encode(account.getCurrency(), "UTF-8")).append("&");
            stringParams.append("v_amount").append("=").append(URLEncoder.encode(df.format(fAmount), "UTF-8")).append("&");
            stringParams.append("v_firstname").append("=").append(URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getFirstname(), "UTF-8")).append("&");
            stringParams.append("v_lastname").append("=").append(URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getLastname(), "UTF-8")).append("&");
            stringParams.append("v_billemail").append("=").append(URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getEmail(), "UTF-8")).append("&");
            stringParams.append("v_billstreet").append("=").append(URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getStreet(), "UTF-8")).append("&");
            stringParams.append("v_billcity").append("=").append(URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getCity(), "UTF-8")).append("&");
            stringParams.append("v_billstate").append("=").append(URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getState(), "UTF-8")).append("&");
            stringParams.append("v_billpost").append("=").append(URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getZipCode(), "UTF-8")).append("&");
            stringParams.append("v_billcountry").append("=").append(URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getCountry(), "UTF-8")).append("&");
            stringParams.append("v_billphone").append("=").append(URLEncoder.encode(commonValidatorVO.getAddressDetailsVO().getPhone(), "UTF-8")).append("&");
            stringParams.append("v_productcode").append("=").append(URLEncoder.encode(commonValidatorVO.getTransDetailsVO().getOrderId(), "UTF-8")).append("&");
            //stringParams.append("v_callbackurl").append("=").append(URLEncoder.encode(v_callbackurl, "UTF-8")).append("&");

            String signature = generateMD5ChecksumDirectKit(signaturekey.toUpperCase(), account.getMerchantId().toUpperCase(), commonValidatorVO.getTrackingid(), getAmount(commonValidatorVO.getTransDetailsVO().getAmount()), account.getCurrency().toUpperCase());
            stringParams.append("signature=").append(URLEncoder.encode(signature, "UTF-8"));
            String urlParameters = stringParams.toString();

            logger.error("buffer values---" + stringParams.toString());
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            // Send post request
            System.setProperty("jsse.enableSNIExtension", "false");
            System.setProperty("https.protocols", "TLSv1.2");
            doTrustToCertificates();
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            // add request header
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            StringBuffer response;

            wr.write(postData);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            logger.error("response code---" + con.getResponseCode() + "-" + con.getResponseMessage());
            /*if (responseCode >= 500) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                StringBuffer r = new StringBuffer();
                String inputLine = "";
                while ((inputLine = in.readLine()) != null) {
                    r.append(inputLine);
                }
            }*/
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }
            in.close();
            logger.error("token in paysec---" + response.toString());

            json = new JSONObject(response.toString());
            Iterator i = json.keys();
            String key = (String) i.next();

            /*String[] strToken = response.toString().split(":");

            String token = strToken[1].replace("}", "");*/
            authmap.put("token", json.get(key));
            redirectHTML = generateAutoSubmitForm("https://pay.paysec.com/GUX/GPay", authmap);
            logger.error("paysec html form---" + redirectHTML);
        }
        catch (Exception e)
        {
            logger.error("Exception in PaySecUtils---", e);
        }
        return redirectHTML;
    }

    public int getAmount(String amount)
    {
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;
        Integer newAmount = dObj2.intValue();

        return newAmount;
    }

    public void doTrustToCertificates() throws Exception {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType)
            {
                return;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType)  {
                return;
            }
        } };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
                    /*System.out.println("Warning: URL host '" + urlHostName + "' is different to SSLSession host '"
                            + session.getPeerHost() + "'.");*/
                }
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

    public String doPostHTTPSURLConnectionClient(String strURL, String req) throws PZTechnicalViolationException
    {
        logger.error("PaySecUtils Server URL:::" + strURL);
        StringBuffer result = new StringBuffer();
        try
        {
            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            System.setProperty("https.protocols", "TLSv1.2");

            URL obj = new URL(strURL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("content-type", "application/json");

            // Send post request
            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(req);
            wr.flush();
            wr.close();

            if (con.getResponseCode() != 500)
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                {
                    result.append(inputLine);
                }
                in.close();
            }
            else
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                {
                    result.append(inputLine);
                }
                in.close();
            }
        }
        catch (HttpException he)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PaySecUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PaySecUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        return result.toString();
    }

    public CommRequestVO getCommRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = new CommRequestVO();

        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO = new CommMerchantVO();
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());

        commAddressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        commAddressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());

        commAddressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        commAddressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        commAddressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        commAddressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        commAddressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        commAddressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());

        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        return commRequestVO;
    }
}