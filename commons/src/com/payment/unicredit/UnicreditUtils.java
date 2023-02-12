package com.payment.unicredit;

import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpException;
import org.apache.http.ssl.SSLContexts;
import sun.misc.BASE64Decoder;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by Admin on 1/2/2018.
 */
public class UnicreditUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(UnicreditUtils.class.getName());
    private static String KEYSTORE_TYPE_P12 = "PKCS12";

    ResourceBundle RB=null;// LoadProperties.getProperty("com.directi.pg.unicredit");

    //public String keyStore = RB.getString("Test_Merchant_JKS");
    public String p12 ="";// RB.getString("P12");

    public String doGetHTTPSURLConnectionClient(String strURL) throws PZTechnicalViolationException
    {
        String result = "";
        URL obj;
        HttpURLConnection con=null;
        try
        {
            TrustManager trm = new X509TrustManager()
            {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                //@Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException
                {
                }

                //@Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException
                {
                }
            };

            String CERT_P12 = "";
            String PWD_P12 = "";

            CERT_P12 = p12;
            PWD_P12 = "changeit";

            /*if(isTest)
            {
                CERT_JKS = keyStore;
                CERT_P12 = p12;

                PWD_JKS = "changeit";
                PWD_P12 = "changeit";
            }
            else
            {
                CERT_JKS = CERT_JKS_PATH+certMap.get("merchantJKS");
                CERT_P12 = CERT_P12_PATH+certMap.get("merchantP12");

                PWD_JKS = (String) certMap.get("passwordJKS");
                PWD_P12 = (String) certMap.get("passwordP12");
            }*/

            // Trusted CA keystore (JKS)
            /*KeyStore tks = KeyStore.getInstance(KEYSTORE_TYPE_JKS);
            tks.load(new FileInputStream(CERT_JKS),PWD_JKS.toCharArray());*/

            // Client keystore
            transactionLogger.error("CERT_P12---"+CERT_P12+"-PWD_P12-"+PWD_P12);
            KeyStore cks = KeyStore.getInstance(KEYSTORE_TYPE_P12);
            cks.load(new FileInputStream(CERT_P12), PWD_P12.toCharArray());

            //SSLContext context = SSLContext.getInstance("TLS");
            SSLContext context = SSLContexts.custom()
                    .loadKeyMaterial(cks, PWD_P12.toCharArray()) // load client certificate
                    .build();

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(cks,PWD_P12.toCharArray());

            context.init(keyManagerFactory.getKeyManagers(), new TrustManager[] { trm }, new SecureRandom());
            SSLContext.setDefault(context);

            obj = new URL(strURL);
             con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            //int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }
            in.close();

            result = response.toString();
        }
        catch (HttpException e)
        {
            transactionLogger.error("CertificateException----", e);
        }
        catch (IOException e)
        {
            transactionLogger.error("CertificateException----", e);
        }
        catch (CertificateException e)
        {
            transactionLogger.error("CertificateException----", e);
            PZExceptionHandler.raiseTechnicalViolationException("EMSUtils.java", "doPostHTTPSURLConnectionClient()", null, "Common", "Certificate Exception while placing transaction", PZTechnicalExceptionEnum.CERTIFICATE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnrecoverableKeyException e)
        {
            transactionLogger.error("UnrecoverableKeyException----", e);
            PZExceptionHandler.raiseTechnicalViolationException("EMSUtils.java", "processRequest()", null, "Common", "UnrecoverableKey Exception while placing transaction", PZTechnicalExceptionEnum.UNRECOVERABLE_KEY_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException----", e);
            PZExceptionHandler.raiseTechnicalViolationException("EMSUtils.java","doPostHTTPSURLConnectionClient()",null,"Common","NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (KeyManagementException e)
        {
            transactionLogger.error("KeyManagementException----", e);
            PZExceptionHandler.raiseTechnicalViolationException("EMSUtils.java", "doPostHTTPSURLConnectionClient()", null, "Common", "KeyManagement Exception while placing transaction", PZTechnicalExceptionEnum.KEY_MANAGEMENT_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (KeyStoreException e)
        {
            transactionLogger.error("KeyStoreException----", e);
            PZExceptionHandler.raiseTechnicalViolationException("EMSUtils.java", "doPostHTTPSURLConnectionClient()", null, "Common", "KeyStore Exception while placing transaction", PZTechnicalExceptionEnum.KEY_STORE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            if (con !=null)
            {
                con.disconnect();
            }
        }
        return result;
    }

    public CommResponseVO decodeBOResp(String rMsg)
    {
        CommResponseVO commResponseVO = new CommResponseVO();
        String status = "fail";

        try
        {
            rMsg = URLDecoder.decode(rMsg, "windows-1252");
            BASE64Decoder decoder = new BASE64Decoder();
            rMsg = new String(decoder.decodeBuffer(rMsg), StandardCharsets.ISO_8859_1);

            String transCode = rMsg.substring(0, 2);
            transactionLogger.error("Response.TransactonCode = " + transCode);
            String transTime = rMsg.substring(2, 14);
            transactionLogger.error("Response.TransactionTime = " + transTime);
            String amount = rMsg.substring(16, 28);
            transactionLogger.error("Response.Amount = " + amount);
            String termID = rMsg.substring(28, 36);
            transactionLogger.error("Response.TID = " + termID);
            String orderID = rMsg.substring(36, 51);
            transactionLogger.error("Response.OredrID = " + orderID);
            String respCode = rMsg.substring(51, 53);
            transactionLogger.error("Response.ResponseCode = " + respCode);
            String protVer = rMsg.substring(53, 56);
            transactionLogger.error("Response.ProtocolVersion = " + protVer);

            if(respCode.equals("00"))
            {
                status = "success";
            }

            commResponseVO.setStatus(status);
            commResponseVO.setTransactionType(transCode);
            commResponseVO.setResponseTime(transTime);
            commResponseVO.setAmount(amount);
            commResponseVO.setMerchantOrderId(orderID);
            commResponseVO.setErrorCode(respCode);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception ::::",e);
        }
        return commResponseVO;
    }

    public CommRequestVO getUnicreditRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = null;

        //CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        //CommMerchantVO merchantAccountVO = new CommMerchantVO();

        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());

        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        /*commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);*/
        commRequestVO.setTransDetailsVO(transDetailsVO);

        return commRequestVO;
    }

    public static String generateAutoSubmitForm(Comm3DResponseVO commResponseVO)
    {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(commResponseVO.getUrlFor3DRedirect()).append("\" method=\"POST\">\n");
        html.append("</form>\n");
        return html.toString();
    }
}
