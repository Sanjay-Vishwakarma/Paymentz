package com.payment.ems.core;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CurrencyCodeISO4217;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.ssl.SSLContexts;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by Admin on 1/24/2018.
 */
public class EMSUtils
{
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.ems");
    private static TransactionLogger transactionLogger = new TransactionLogger(EMSUtils.class.getName());
    private static String KEYSTORE_TYPE_JKS = "JKS";
    private static String KEYSTORE_TYPE_P12 = "PKCS12";

    public  static String doPostHTTPSURLConnectionClient(String encodedCredentials,String request,String accountId,boolean isTest) throws PZTechnicalViolationException
    {
        String CERT_JKS_PATH = RB.getString("CERT_PATH");
        String CERT_P12_PATH = RB.getString("CERT_PATH");
        String TEST_URL = RB.getString("TEST_URL");
        String LIVE_URL = RB.getString("LIVE_URL");
        String url = "";

        EMSUtils emsUtils = new EMSUtils();
        transactionLogger.debug("url--->" + url);
        String response = "";
        //System.out.println("encodedCredentials---"+encodedCredentials);
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

            HashMap certMap = emsUtils.getCertDetails(accountId);
            transactionLogger.error("cred map---"+certMap);

            String CERT_JKS = "";
            String CERT_P12 = "";
            String PWD_JKS = "";
            String PWD_P12 = "";

            if(isTest)
            {
                CERT_JKS = CERT_JKS_PATH+certMap.get("merchantJKS");
                CERT_P12 = CERT_P12_PATH+certMap.get("merchantP12");
                /*TEST_CERT_JKS = TEST_CERT_JKS_PATH+"WS230995073._.1.ks";
                TEST_CERT_P12 = TEST_CERT_P12_PATH+"WS230995073._.1.p12";*/

                PWD_JKS = (String) certMap.get("passwordJKS");
                PWD_P12 = (String) certMap.get("passwordP12");

                url = TEST_URL;
            }
            else
            {
                CERT_JKS = CERT_JKS_PATH+certMap.get("merchantJKS");
                CERT_P12 = CERT_P12_PATH+certMap.get("merchantP12");

                PWD_JKS = (String) certMap.get("passwordJKS");
                PWD_P12 = (String) certMap.get("passwordP12");

                url = LIVE_URL;
            }




            // Trusted CA keystore (JKS)
            KeyStore tks = KeyStore.getInstance(KEYSTORE_TYPE_JKS);
            //System.out.println(CERT_JKS);
            tks.load(new FileInputStream(CERT_JKS),PWD_JKS.toCharArray());

            // Client keystore
            KeyStore cks = KeyStore.getInstance(KEYSTORE_TYPE_P12);
            //System.out.println(CERT_P12);
            cks.load(new FileInputStream(CERT_P12), PWD_P12.toCharArray());

            //SSLContext context = SSLContext.getInstance("TLS");
            SSLContext context = SSLContexts.custom()
                    .loadKeyMaterial(cks, PWD_P12.toCharArray()) // load client certificate
                    .build();

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(cks,PWD_P12.toCharArray());

            context.init(keyManagerFactory.getKeyManagers(), new TrustManager[] { trm }, new SecureRandom());
            SSLContext.setDefault(context);

            HttpClient httpClient = new HttpClient();
            PostMethod post = new PostMethod(url);
            post.addRequestHeader("Authorization", "Basic "+encodedCredentials);
            post.addRequestHeader("Content-Type", "text/xml;charset=UTF-8");
            post.addRequestHeader("Connection", "keep-alive");
            post.addRequestHeader("Cache-Control", "no-cache");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            response = new String(post.getResponseBody());
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException----", he);
            PZExceptionHandler.raiseTechnicalViolationException("EMSUtils.java", "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException----", io);
            PZExceptionHandler.raiseTechnicalViolationException("EMSUtils.java", "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        catch (CertificateException e)
        {
            transactionLogger.error("CertificateException----", e);
            PZExceptionHandler.raiseTechnicalViolationException("EMSUtils.java","doPostHTTPSURLConnectionClient()",null,"Common","Certificate Exception while placing transaction", PZTechnicalExceptionEnum.CERTIFICATE_EXCEPTION,null,e.getMessage(),e.getCause());
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
            PZExceptionHandler.raiseTechnicalViolationException("EMSUtils.java","doPostHTTPSURLConnectionClient()", null, "Common", "KeyManagement Exception while placing transaction", PZTechnicalExceptionEnum.KEY_MANAGEMENT_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (KeyStoreException e)
        {
            transactionLogger.error("KeyStoreException----", e);
            PZExceptionHandler.raiseTechnicalViolationException("EMSUtils.java", "doPostHTTPSURLConnectionClient()", null, "Common", "KeyStore Exception while placing transaction", PZTechnicalExceptionEnum.KEY_STORE_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        return response;
    }

    private static InputStream fullStream ( String fname ) throws IOException
    {
        FileInputStream fis = new FileInputStream(fname);
        DataInputStream dis = new DataInputStream(fis);
        byte[] bytes = new byte[dis.available()];
        dis.readFully(bytes);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        return bais;
    }

    public static void main(String[] args)
    {
        try
        {
            String res1 = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><ipgapi:IPGApiOrderResponse xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\" xmlns:a1=\"http://ipg-online.com/ipgapi/schemas/a1\" xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\"><ipgapi:ApprovalCode>Y:829447:4515452216:PPXU:686123</ipgapi:ApprovalCode><ipgapi:AVSResponse>PPX</ipgapi:AVSResponse><ipgapi:Brand>MASTERCARD</ipgapi:Brand><ipgapi:CommercialServiceProvider>EMS</ipgapi:CommercialServiceProvider><ipgapi:OrderId>A-b8ac18f1-291f-4b09-8ef9-d4ebc3d13975</ipgapi:OrderId><ipgapi:IpgTransactionId>84515452216</ipgapi:IpgTransactionId><ipgapi:PaymentType>CREDITCARD</ipgapi:PaymentType><ipgapi:ProcessorApprovalCode>829447</ipgapi:ProcessorApprovalCode><ipgapi:ProcessorCCVResponse>U</ipgapi:ProcessorCCVResponse><ipgapi:ProcessorReferenceNumber>802605686123</ipgapi:ProcessorReferenceNumber><ipgapi:ProcessorResponseCode>00</ipgapi:ProcessorResponseCode><ipgapi:ProcessorResponseMessage>Function performed error-free</ipgapi:ProcessorResponseMessage><ipgapi:TDate>1516944451</ipgapi:TDate><ipgapi:TDateFormatted>2018.01.26 06:27:31 (CET)</ipgapi:TDateFormatted><ipgapi:TerminalID>80006232</ipgapi:TerminalID><ipgapi:TransactionResult>APPROVED</ipgapi:TransactionResult><ipgapi:TransactionTime>1516944451</ipgapi:TransactionTime></ipgapi:IPGApiOrderResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
            String res2 = "<SOAP-ENV:Envelope\n" +
                    "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "    <SOAP-ENV:Header/>\n" +
                    "    <SOAP-ENV:Body>\n" +
                    "        <ipgapi:IPGApiActionResponse\n" +
                    "            xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\"\n" +
                    "            xmlns:a1=\"http://ipg-online.com/ipgapi/schemas/a1\"\n" +
                    "            xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\">\n" +
                    "            <ipgapi:successfully>true</ipgapi:successfully>\n" +
                    "            <a1:TransactionValues>\n" +
                    "                <v1:CreditCardTxType>\n" +
                    "                    <v1:Type>sale</v1:Type>\n" +
                    "                </v1:CreditCardTxType>\n" +
                    "                <v1:CreditCardData>\n" +
                    "                    <v1:CardNumber>545301...0789</v1:CardNumber>\n" +
                    "                    <v1:ExpMonth>12</v1:ExpMonth>\n" +
                    "                    <v1:ExpYear>20</v1:ExpYear>\n" +
                    "                    <v1:Brand>MASTERCARD</v1:Brand>\n" +
                    "                </v1:CreditCardData>\n" +
                    "                <v1:Payment>\n" +
                    "                    <v1:ChargeTotal>50</v1:ChargeTotal>\n" +
                    "                    <v1:Currency>978</v1:Currency>\n" +
                    "                </v1:Payment>\n" +
                    "                <v1:TransactionDetails>\n" +
                    "                    <v1:OrderId>A-1a337de8-7c47-4539-8b22-70cd0e4142b8</v1:OrderId>\n" +
                    "                    <v1:MerchantTransactionId>54090</v1:MerchantTransactionId>\n" +
                    "                    <v1:TDate>1516947936</v1:TDate>\n" +
                    "                </v1:TransactionDetails>\n" +
                    "                <ipgapi:IPGApiOrderResponse>\n" +
                    "                    <ipgapi:ApprovalCode>Y:961564:4515452552:PPXU:686131</ipgapi:ApprovalCode>\n" +
                    "                    <ipgapi:AVSResponse>PPX</ipgapi:AVSResponse>\n" +
                    "                    <ipgapi:Brand>MASTERCARD</ipgapi:Brand>\n" +
                    "                    <ipgapi:OrderId>A-1a337de8-7c47-4539-8b22-70cd0e4142b8</ipgapi:OrderId>\n" +
                    "                    <ipgapi:IpgTransactionId>84515452552</ipgapi:IpgTransactionId>\n" +
                    "                    <ipgapi:PaymentType>CREDITCARD</ipgapi:PaymentType>\n" +
                    "                    <ipgapi:ProcessorApprovalCode>961564</ipgapi:ProcessorApprovalCode>\n" +
                    "                    <ipgapi:ProcessorCCVResponse>U</ipgapi:ProcessorCCVResponse>\n" +
                    "                    <ipgapi:ReferencedTDate>1516947936</ipgapi:ReferencedTDate>\n" +
                    "                    <ipgapi:TDate>1516947936</ipgapi:TDate>\n" +
                    "                    <ipgapi:TDateFormatted>2018.01.26 07:25:36 (CET)</ipgapi:TDateFormatted>\n" +
                    "                    <ipgapi:TerminalID>80006232</ipgapi:TerminalID>\n" +
                    "                </ipgapi:IPGApiOrderResponse>\n" +
                    "                <a1:TraceNumber>686131</a1:TraceNumber>\n" +
                    "                <a1:Brand>MASTERCARD</a1:Brand>\n" +
                    "                <a1:TransactionType>SALE</a1:TransactionType>\n" +
                    "                <a1:TransactionState>SETTLED</a1:TransactionState>\n" +
                    "                <a1:UserID>1</a1:UserID>\n" +
                    "                <a1:SubmissionComponent>API</a1:SubmissionComponent>\n" +
                    "            </a1:TransactionValues>\n" +
                    "        </ipgapi:IPGApiActionResponse>\n" +
                    "    </SOAP-ENV:Body>\n" +
                    "</SOAP-ENV:Envelope>";
            String res3 ="<SOAP-ENV:Envelope\n" +
                    "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "    <SOAP-ENV:Header/>\n" +
                    "    <SOAP-ENV:Body>\n" +
                    "        <ipgapi:IPGApiActionResponse\n" +
                    "            xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\"\n" +
                    "            xmlns:a1=\"http://ipg-online.com/ipgapi/schemas/a1\"\n" +
                    "            xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\">\n" +
                    "            <ipgapi:successfully>true</ipgapi:successfully>\n" +
                    "            <ipgapi:OrderId>A-3c151072-11f5-46ac-962e-58f3a1f7e8e6</ipgapi:OrderId>\n" +
                    "            <v1:Billing/>\n" +
                    "            <v1:Shipping/>\n" +
                    "            <a1:TransactionValues>\n" +
                    "                <v1:CreditCardTxType>\n" +
                    "                    <v1:Type>sale</v1:Type>\n" +
                    "                </v1:CreditCardTxType>\n" +
                    "                <v1:CreditCardData>\n" +
                    "                    <v1:CardNumber>545301...0789</v1:CardNumber>\n" +
                    "                    <v1:ExpMonth>12</v1:ExpMonth>\n" +
                    "                    <v1:ExpYear>20</v1:ExpYear>\n" +
                    "                    <v1:Brand>MASTERCARD</v1:Brand>\n" +
                    "                </v1:CreditCardData>\n" +
                    "                <v1:Payment>\n" +
                    "                    <v1:ChargeTotal>19</v1:ChargeTotal>\n" +
                    "                    <v1:Currency>978</v1:Currency>\n" +
                    "                </v1:Payment>\n" +
                    "                <v1:TransactionDetails>\n" +
                    "                    <v1:OrderId>A-3c151072-11f5-46ac-962e-58f3a1f7e8e6</v1:OrderId>\n" +
                    "                    <v1:TDate>1516801185</v1:TDate>\n" +
                    "                    <v1:TransactionOrigin>ECI</v1:TransactionOrigin>\n" +
                    "                </v1:TransactionDetails>\n" +
                    "                <ipgapi:IPGApiOrderResponse>\n" +
                    "                    <ipgapi:ApprovalCode>Y:469944:4515440579:PPXU:683545</ipgapi:ApprovalCode>\n" +
                    "                    <ipgapi:AVSResponse>PPX</ipgapi:AVSResponse>\n" +
                    "                    <ipgapi:Brand>MASTERCARD</ipgapi:Brand>\n" +
                    "                    <ipgapi:OrderId>A-3c151072-11f5-46ac-962e-58f3a1f7e8e6</ipgapi:OrderId>\n" +
                    "                    <ipgapi:IpgTransactionId>84515440579</ipgapi:IpgTransactionId>\n" +
                    "                    <ipgapi:PaymentType>CREDITCARD</ipgapi:PaymentType>\n" +
                    "                    <ipgapi:ProcessorApprovalCode>469944</ipgapi:ProcessorApprovalCode>\n" +
                    "                    <ipgapi:ProcessorCCVResponse>U</ipgapi:ProcessorCCVResponse>\n" +
                    "                    <ipgapi:ReferencedTDate>1516801185</ipgapi:ReferencedTDate>\n" +
                    "                    <ipgapi:TDate>1516801185</ipgapi:TDate>\n" +
                    "                    <ipgapi:TDateFormatted>2018.01.24 14:39:45 (CET)</ipgapi:TDateFormatted>\n" +
                    "                    <ipgapi:TerminalID>80006232</ipgapi:TerminalID>\n" +
                    "                </ipgapi:IPGApiOrderResponse>\n" +
                    "                <a1:TraceNumber>683545</a1:TraceNumber>\n" +
                    "                <a1:Brand>MASTERCARD</a1:Brand>\n" +
                    "                <a1:TransactionType>SALE</a1:TransactionType>\n" +
                    "                <a1:TransactionState>SETTLED</a1:TransactionState>\n" +
                    "                <a1:UserID>1</a1:UserID>\n" +
                    "                <a1:SubmissionComponent>API</a1:SubmissionComponent>\n" +
                    "            </a1:TransactionValues>\n" +
                    "            <a1:TransactionValues>\n" +
                    "                <v1:CreditCardTxType>\n" +
                    "                    <v1:Type>return</v1:Type>\n" +
                    "                </v1:CreditCardTxType>\n" +
                    "                <v1:CreditCardData>\n" +
                    "                    <v1:CardNumber>545301...0789</v1:CardNumber>\n" +
                    "                    <v1:ExpMonth>12</v1:ExpMonth>\n" +
                    "                    <v1:ExpYear>20</v1:ExpYear>\n" +
                    "                    <v1:Brand>MASTERCARD</v1:Brand>\n" +
                    "                </v1:CreditCardData>\n" +
                    "                <v1:Payment>\n" +
                    "                    <v1:ChargeTotal>19</v1:ChargeTotal>\n" +
                    "                    <v1:Currency>978</v1:Currency>\n" +
                    "                </v1:Payment>\n" +
                    "                <v1:TransactionDetails>\n" +
                    "                    <v1:OrderId>A-3c151072-11f5-46ac-962e-58f3a1f7e8e6</v1:OrderId>\n" +
                    "                    <v1:TDate>1516951111</v1:TDate>\n" +
                    "                    <v1:TransactionOrigin>ECI</v1:TransactionOrigin>\n" +
                    "                </v1:TransactionDetails>\n" +
                    "                <ipgapi:IPGApiOrderResponse>\n" +
                    "                    <ipgapi:ApprovalCode>Y:000000:4515452633:PPX :0000</ipgapi:ApprovalCode>\n" +
                    "                    <ipgapi:AVSResponse>PPX</ipgapi:AVSResponse>\n" +
                    "                    <ipgapi:Brand>MASTERCARD</ipgapi:Brand>\n" +
                    "                    <ipgapi:OrderId>A-3c151072-11f5-46ac-962e-58f3a1f7e8e6</ipgapi:OrderId>\n" +
                    "                    <ipgapi:IpgTransactionId>84515452633</ipgapi:IpgTransactionId>\n" +
                    "                    <ipgapi:PaymentType>CREDITCARD</ipgapi:PaymentType>\n" +
                    "                    <ipgapi:ProcessorApprovalCode>000000</ipgapi:ProcessorApprovalCode>\n" +
                    "                    <ipgapi:ProcessorCCVResponse></ipgapi:ProcessorCCVResponse>\n" +
                    "                    <ipgapi:ReferencedTDate>1516951111</ipgapi:ReferencedTDate>\n" +
                    "                    <ipgapi:TDate>1516951111</ipgapi:TDate>\n" +
                    "                    <ipgapi:TDateFormatted>2018.01.26 08:18:31 (CET)</ipgapi:TDateFormatted>\n" +
                    "                    <ipgapi:TerminalID>80006232</ipgapi:TerminalID>\n" +
                    "                </ipgapi:IPGApiOrderResponse>\n" +
                    "                <a1:TraceNumber>0000</a1:TraceNumber>\n" +
                    "                <a1:Brand>MASTERCARD</a1:Brand>\n" +
                    "                <a1:TransactionType>RETURN</a1:TransactionType>\n" +
                    "                <a1:TransactionState>SETTLED</a1:TransactionState>\n" +
                    "                <a1:UserID>1</a1:UserID>\n" +
                    "                <a1:SubmissionComponent>API</a1:SubmissionComponent>\n" +
                    "            </a1:TransactionValues>\n" +
                    "        </ipgapi:IPGApiActionResponse>\n" +
                    "    </SOAP-ENV:Body>\n" +
                    "</SOAP-ENV:Envelope>";

            String res4="<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><ipgapi:IPGApiActionResponse xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\" xmlns:a1=\"http://ipg-online.com/ipgapi/schemas/a1\" xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\"><ipgapi:successfully>true</ipgapi:successfully><ipgapi:OrderId>A-ad0e2009-f801-403b-a724-a56f9edb24e0</ipgapi:OrderId><v1:Billing/><v1:Shipping/><a1:TransactionValues><v1:CreditCardTxType><v1:Type>sale</v1:Type></v1:CreditCardTxType><v1:CreditCardData><v1:CardNumber>545301...0789</v1:CardNumber><v1:ExpMonth>12</v1:ExpMonth><v1:ExpYear>20</v1:ExpYear><v1:Brand>MASTERCARD</v1:Brand></v1:CreditCardData><v1:Payment><v1:ChargeTotal>60</v1:ChargeTotal><v1:Currency>978</v1:Currency></v1:Payment><v1:TransactionDetails><v1:OrderId>A-ad0e2009-f801-403b-a724-a56f9edb24e0</v1:OrderId><v1:MerchantTransactionId>62733</v1:MerchantTransactionId><v1:TDate>1517388226</v1:TDate><v1:TransactionOrigin>ECI</v1:TransactionOrigin></v1:TransactionDetails><ipgapi:IPGApiOrderResponse><ipgapi:ApprovalCode>Y:390876:4515478236:PPXU:693873</ipgapi:ApprovalCode><ipgapi:AVSResponse>PPX</ipgapi:AVSResponse><ipgapi:Brand>MASTERCARD</ipgapi:Brand><ipgapi:OrderId>A-ad0e2009-f801-403b-a724-a56f9edb24e0</ipgapi:OrderId><ipgapi:IpgTransactionId>84515478236</ipgapi:IpgTransactionId><ipgapi:PaymentType>CREDITCARD</ipgapi:PaymentType><ipgapi:ProcessorApprovalCode>390876</ipgapi:ProcessorApprovalCode><ipgapi:ProcessorCCVResponse>U</ipgapi:ProcessorCCVResponse><ipgapi:ReferencedTDate>1517388226</ipgapi:ReferencedTDate><ipgapi:TDate>1517388226</ipgapi:TDate><ipgapi:TDateFormatted>2018.01.31 09:43:46 (CET)</ipgapi:TDateFormatted><ipgapi:TerminalID>80006232</ipgapi:TerminalID></ipgapi:IPGApiOrderResponse><a1:TraceNumber>693873</a1:TraceNumber><a1:Brand>MASTERCARD</a1:Brand><a1:TransactionType>SALE</a1:TransactionType><a1:TransactionState>SETTLED</a1:TransactionState><a1:UserID>1</a1:UserID><a1:SubmissionComponent>API</a1:SubmissionComponent></a1:TransactionValues><a1:TransactionValues><v1:CreditCardTxType><v1:Type>return</v1:Type></v1:CreditCardTxType><v1:CreditCardData><v1:CardNumber>545301...0789</v1:CardNumber><v1:ExpMonth>12</v1:ExpMonth><v1:ExpYear>20</v1:ExpYear><v1:Brand>MASTERCARD</v1:Brand></v1:CreditCardData><v1:Payment><v1:ChargeTotal>60</v1:ChargeTotal><v1:Currency>978</v1:Currency></v1:Payment><v1:TransactionDetails><v1:OrderId>A-ad0e2009-f801-403b-a724-a56f9edb24e0</v1:OrderId><v1:TDate>1517388298</v1:TDate><v1:TransactionOrigin>ECI</v1:TransactionOrigin></v1:TransactionDetails><ipgapi:IPGApiOrderResponse><ipgapi:ApprovalCode>Y:000000:4515478237:PPX :0000</ipgapi:ApprovalCode><ipgapi:AVSResponse>PPX</ipgapi:AVSResponse><ipgapi:Brand>MASTERCARD</ipgapi:Brand><ipgapi:OrderId>A-ad0e2009-f801-403b-a724-a56f9edb24e0</ipgapi:OrderId><ipgapi:IpgTransactionId>84515478237</ipgapi:IpgTransactionId><ipgapi:PaymentType>CREDITCARD</ipgapi:PaymentType><ipgapi:ProcessorApprovalCode>000000</ipgapi:ProcessorApprovalCode><ipgapi:ProcessorCCVResponse> </ipgapi:ProcessorCCVResponse><ipgapi:ReferencedTDate>1517388298</ipgapi:ReferencedTDate><ipgapi:TDate>1517388298</ipgapi:TDate><ipgapi:TDateFormatted>2018.01.31 09:44:58 (CET)</ipgapi:TDateFormatted><ipgapi:TerminalID>80006232</ipgapi:TerminalID></ipgapi:IPGApiOrderResponse><a1:TraceNumber>0000</a1:TraceNumber><a1:Brand>MASTERCARD</a1:Brand><a1:TransactionType>RETURN</a1:TransactionType><a1:TransactionState>SETTLED</a1:TransactionState><a1:UserID>1</a1:UserID><a1:SubmissionComponent>API</a1:SubmissionComponent></a1:TransactionValues></ipgapi:IPGApiActionResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";

            EMSUtils emsUtils = new EMSUtils();
            //System.out.println("ree4----"+res4.replaceAll("\n", "").toString());
            EMSResponseVO e = emsUtils.readSoapResponse(res4);
           /* System.out.println("code---"+e.getDescription());
            System.out.println("code---"+e.getStatus());
            System.out.println("code---"+e.getSuccessfully());
            System.out.println("code---"+e.getTransactionType());
            System.out.println("code---"+e.getpApproveCode());*/

        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
    }

    private static String getTagValue(String trackID, Element eElement)
    {
        NodeList nlList = null;
        String value  ="";
        if(eElement!=null && eElement.getElementsByTagName(trackID)!=null && eElement.getElementsByTagName(trackID).item(0)!=null)
        {
            nlList =  eElement.getElementsByTagName(trackID).item(0).getChildNodes();
        }
        if(nlList!=null && nlList.item(0)!=null)
        {
            Node nValue = (Node) nlList.item(0);
            value =	nValue.getNodeValue();
        }
        return value;
    }

    public static Document createDocumentFromString(String xmlString ) throws PZTechnicalViolationException
    {
        Document document = null;
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            document = docBuilder.parse(new InputSource(new StringReader( xmlString ) ));
        }
        catch (ParserConfigurationException pce)
        {
            transactionLogger.error("Exception in createDocumentFromString of BorgunUtill",pce);
            PZExceptionHandler.raiseTechnicalViolationException("BorgunUtills.java","createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,pce.getMessage(),pce.getCause());
        }
        catch (SAXException e)
        {
            transactionLogger.error("Exception in createDocumentFromString BorgunUtill",e);
            PZExceptionHandler.raiseTechnicalViolationException("BorgunUtills.java","createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.SAXEXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (IOException e)
        {
            transactionLogger.error("Exception in createDocumentFromString BorgunUtill",e);
            PZExceptionHandler.raiseTechnicalViolationException("BorgunUtills.java","createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }
        return document;
    }

    public static String getCardType(String  cardType){
        String emsCardType = "";
        Functions functions = new Functions();
        if(functions.isValueNull(cardType))
        {

            if(cardType.equals("VISA"))
            {
                emsCardType="VISA" ;
            }
            else if(cardType.equals("MC"))
            {
                emsCardType="MASTERCARD" ;
            }
            else if(cardType.equals("AMEX"))
            {
                emsCardType="AMEX";
            }
            else if(cardType.equals("DINER"))
            {
                emsCardType="DINER";
            }
            else if(cardType.equals("DISC"))
            {
                emsCardType="DISC";
            }
            else if(cardType.equals("JCB")){

                emsCardType="JCB";
            }
        }
        return emsCardType;
    }
    public String getCardExpiry(String cardExpiryMonth,String cardExpiryYear){
        return cardExpiryYear.substring(2,4)+cardExpiryMonth;
    }

    public String direct3DsConfirmationRequest(CommRequestVO commRequestVO,String trackingid,String storeId)
    {
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        String cardNo = genericCardDetailsVO.getCardNum();
        String expMonth = genericCardDetailsVO.getExpMonth();
        String expYear = genericCardDetailsVO.getExpYear().substring(genericCardDetailsVO.getExpYear().length() - 2);
        String cvv = genericCardDetailsVO.getcVV();
        String amount = genericTransDetailsVO.getAmount();

        String customerid = commRequestVO.getCustomerId();
        String customerName = addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname();
        String address1 = addressDetailsVO.getStreet();
        String city = addressDetailsVO.getCity();
        String state = addressDetailsVO.getState();
        String country = addressDetailsVO.getCountry();
        String phone = addressDetailsVO.getPhone();
        String email = addressDetailsVO.getEmail();
        String zip = addressDetailsVO.getZipCode();
        int currencyId = Integer.parseInt(CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency()));

        String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "<SOAP-ENV:Header />\n" +
                "<SOAP-ENV:Body>\n" +
                "<ipgapi:IPGApiOrderRequest xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\" xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\">\n" +
                "<v1:Transaction>\n" +
                "<v1:CreditCardTxType>\n" +
                "<v1:StoreId>"+storeId+"</v1:StoreId>\n" +
                "<v1:Type>sale</v1:Type>\n" +
                "</v1:CreditCardTxType>\n" +
                "<v1:CreditCardData>\n" +
                "<v1:CardNumber>"+cardNo+"</v1:CardNumber>\n" +//5453010000070789
                "<v1:ExpMonth>"+expMonth+"</v1:ExpMonth>\n" + //Dec = 12
                "<v1:ExpYear>"+expYear+"</v1:ExpYear>\n" + //2020 = 20
                "<v1:CardCodeValue>"+cvv+"</v1:CardCodeValue>\n" +
                "<v1:Brand>"+EMSUtils.getCardType(genericCardDetailsVO.getCardType())+"</v1:Brand>\n" +
                "</v1:CreditCardData>\n" +
                "<v1:CreditCard3DSecure>\n" +
                "<v1:VerificationResponse>Y</v1:VerificationResponse>\n" +
                "<v1:PayerAuthenticationResponse>Y</v1:PayerAuthenticationResponse>\n" +
                "<v1:AuthenticationValue>"+genericTransDetailsVO.getVerificationId()+"</v1:AuthenticationValue>\n" +//AAACAjYYkBgIAggAFhiQAAAAAAA=
                "<v1:XID>"+genericTransDetailsVO.getXid()+"</v1:XID>\n" +
                "</v1:CreditCard3DSecure>\n" +
                "<v1:Payment>\n" +
                "<v1:ChargeTotal>"+amount+"</v1:ChargeTotal>\n" +
                "<v1:Currency>"+String.valueOf(currencyId)+"</v1:Currency>\n" +
                "</v1:Payment>\n" +
                "<v1:TransactionDetails>\n" +
                "<v1:MerchantTransactionId>"+trackingid+"</v1:MerchantTransactionId>\n" +
                "</v1:TransactionDetails>\n" +
                "<v1:Billing>\n" +
                "<v1:Name>"+customerName+"</v1:Name>\n" +
                "<v1:CustomerID>" + customerid + "</v1:CustomerID>\n" +
                "<v1:Address1>"+address1+"</v1:Address1>\n" +
                "<v1:City>"+city+"</v1:City>\n" +
                "<v1:State>"+state+"</v1:State>\n" +
                "<v1:Zip>"+zip+"</v1:Zip>\n" +
                "<v1:Country>"+country+"</v1:Country>\n" +
                "<v1:Phone>"+phone+"</v1:Phone>\n" +
                "<v1:Email>"+email+"</v1:Email>\n" +
                "</v1:Billing>\n" +
                "</v1:Transaction>\n" +
                "</ipgapi:IPGApiOrderRequest>\n" +
                "</SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>\n";

        return xmlRequest;
    }

    public String getCentAmount(String amount)
    {
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;
        Integer newAmount = dObj2.intValue();

        return newAmount.toString();
    }

    public EMSResponseVO readSoapResponse(String soapRequest) throws PZTechnicalViolationException
    {
        EMSResponseVO commResponseVO = new EMSResponseVO();


            Document doc = createDocumentFromString(soapRequest);
            Element rootElement = doc.getDocumentElement();
            NodeList nList = doc.getElementsByTagName("SOAP-ENV:Body");

            commResponseVO.setSuccessfully(getTagValue("ipgapi:successfully", ((Element) nList.item(0))));// use for inquiry response
            commResponseVO.setAuthCode(getTagValue("ipgapi:ApprovalCode", ((Element) nList.item(0))));//ApprovalCode
            commResponseVO.setDescriptor(getTagValue("ipgapi:CommercialServiceProvider", ((Element) nList.item(0))));//CommercialServiceProvider
            commResponseVO.setTransactionId(getTagValue("ipgapi:OrderId", ((Element) nList.item(0))));//OrderId
            commResponseVO.setMerchantOrderId(getTagValue("ipgapi:IpgTransactionId", ((Element) nList.item(0))));//IpgTransactionId
            commResponseVO.setDescription(getTagValue("ipgapi:TransactionResult", ((Element) nList.item(0))));//TransactionResult
            commResponseVO.setErrorCode(getTagValue("ipgapi:ProcessorResponseCode", ((Element) nList.item(0))));//:ProcessorResponseCode
            commResponseVO.setRemark(getTagValue("ipgapi:ProcessorResponseMessage", ((Element) nList.item(0))));//ProcessorResponseMessage
            commResponseVO.setTransactionType(getTagValue("a1:TransactionType", ((Element) nList.item(0))));//transactionType for inquiry method
            commResponseVO.setTransactionStatus(getTagValue("a1:TransactionState", ((Element) nList.item(0))));//TransactionState for inquiry method
            commResponseVO.setAvsResponse(getTagValue("ipgapi:AVSResponse", ((Element) nList.item(0))));//AVSResponse
            commResponseVO.setBrand(getTagValue("ipgapi:Brand", ((Element) nList.item(0))));//Brand
            commResponseVO.setServiceProvider(getTagValue("ipgapi:CommercialServiceProvider", ((Element) nList.item(0))));//CommercialServiceProvider
            commResponseVO.setPaymentType(getTagValue("ipgapi:PaymentType", ((Element) nList.item(0))));//PaymentType
            commResponseVO.setpApproveCode(getTagValue("ipgapi:ProcessorApprovalCode", ((Element) nList.item(0))));//ProcessorApprovalCode
            commResponseVO.setpCCVResponse(getTagValue("ipgapi:ProcessorCCVResponse", ((Element) nList.item(0))));//ProcessorCCVResponse
            commResponseVO.setArn(getTagValue("ipgapi:ProcessorReferenceNumber", ((Element) nList.item(0))));//ProcessorReferenceNumber
            commResponseVO.setpRespCode(getTagValue("ipgapi:ProcessorResponseCode", ((Element) nList.item(0))));//ProcessorResponseCode
            commResponseVO.settDate(getTagValue("ipgapi:TDate", ((Element) nList.item(0))));//TDate
            commResponseVO.setBankTransactionDate(getTagValue("ipgapi:TDateFormatted", ((Element) nList.item(0))));//TDateFormatted
            commResponseVO.settTime(getTagValue("ipgapi:TransactionTime", ((Element) nList.item(0))));//TransactionTime

            if (commResponseVO.getDescription().equalsIgnoreCase("APPROVED"))
                commResponseVO.setStatus("success");
            else
                commResponseVO.setStatus("fail");

            NodeList nList1 = doc.getElementsByTagName("a1:TransactionValues");

            //System.out.println("nList1.getLength()-----" + nList1.getLength());

            for (int i = 0; i < nList1.getLength(); i++)
            {
                commResponseVO.setAuthCode(getTagValue("ipgapi:ApprovalCode", ((Element) nList1.item(i))));//ApprovalCode
                commResponseVO.setDescriptor(getTagValue("ipgapi:CommercialServiceProvider", ((Element) nList1.item(i))));//CommercialServiceProvider
                commResponseVO.setTransactionId(getTagValue("ipgapi:OrderId", ((Element) nList1.item(i))));//OrderId
                commResponseVO.setMerchantOrderId(getTagValue("ipgapi:IpgTransactionId", ((Element) nList1.item(i))));//IpgTransactionId
                commResponseVO.setDescription(getTagValue("ipgapi:TransactionResult", ((Element) nList1.item(i))));//TransactionResult
                commResponseVO.setErrorCode(getTagValue("ipgapi:ProcessorResponseCode", ((Element) nList1.item(i))));//:ProcessorResponseCode
                commResponseVO.setRemark(getTagValue("ipgapi:ProcessorResponseMessage", ((Element) nList1.item(i))));//ProcessorResponseMessage
                commResponseVO.setTransactionType(getTagValue("a1:TransactionType", ((Element) nList1.item(i))));//transactionType for inquiry method
                commResponseVO.setTransactionStatus(getTagValue("a1:TransactionState", ((Element) nList1.item(i))));//TransactionState for inquiry method
                commResponseVO.setAvsResponse(getTagValue("ipgapi:AVSResponse", ((Element) nList1.item(i))));//AVSResponse
                commResponseVO.setBrand(getTagValue("ipgapi:Brand", ((Element) nList1.item(i))));//Brand
                commResponseVO.setServiceProvider(getTagValue("ipgapi:CommercialServiceProvider", ((Element) nList1.item(i))));//CommercialServiceProvider
                commResponseVO.setPaymentType(getTagValue("ipgapi:PaymentType", ((Element) nList1.item(i))));//PaymentType
                commResponseVO.setpApproveCode(getTagValue("ipgapi:ProcessorApprovalCode", ((Element) nList1.item(i))));//ProcessorApprovalCode
                commResponseVO.setpCCVResponse(getTagValue("ipgapi:ProcessorCCVResponse", ((Element) nList1.item(i))));//ProcessorCCVResponse
                commResponseVO.setArn(getTagValue("ipgapi:ProcessorReferenceNumber", ((Element) nList1.item(i))));//ProcessorReferenceNumber
                commResponseVO.setpRespCode(getTagValue("ipgapi:ProcessorResponseCode", ((Element) nList1.item(i))));//ProcessorResponseCode
                commResponseVO.settDate(getTagValue("ipgapi:TDate", ((Element) nList1.item(i))));//TDate
                commResponseVO.setBankTransactionDate(getTagValue("ipgapi:TDateFormatted", ((Element) nList1.item(i))));//TDateFormatted
                commResponseVO.settTime(getTagValue("ipgapi:TransactionTime", ((Element) nList1.item(i))));//TransactionTime

                transactionLogger.error("AVSResponse------" + getTagValue("ipgapi:AVSResponse", ((Element) nList1.item(i))));
                transactionLogger.error("IpgTransactionId------" + getTagValue("ipgapi:IpgTransactionId", ((Element) nList1.item(i))));
                transactionLogger.error("TDateFormatted------" + getTagValue("ipgapi:TDateFormatted", ((Element) nList1.item(i))));
                transactionLogger.error("TransactionState------" + getTagValue("a1:TransactionState", ((Element) nList1.item(i))));
                transactionLogger.error("TransactionType------" + getTagValue("a1:TransactionType", ((Element) nList1.item(i))));

                    if ( commResponseVO.getSuccessfully().equalsIgnoreCase("true") && commResponseVO.getTransactionStatus().equalsIgnoreCase("AUTHORIZED") ||commResponseVO.getTransactionStatus().equalsIgnoreCase("SETTLED") || commResponseVO.getTransactionStatus().equalsIgnoreCase("CAPTURED"))
                        commResponseVO.setStatus("success");
                    else
                        commResponseVO.setStatus("fail");
                }

        return commResponseVO;
    }

    public HashMap getCertDetails(String accountID)
    {
        HashMap dataHash = new HashMap();
        Connection conn = null;
        PreparedStatement stmt = null;
        try
        {
            conn = Database.getConnection();
            String query = "select merchantJKS,passwordJKS,merchantP12,passwordP12 from gateway_accounts_ems where accountId=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,accountID);
            ResultSet rs = stmt.executeQuery();
            if(rs.next())
            {
                dataHash.put("merchantJKS",rs.getString("merchantJKS"));
                dataHash.put("passwordJKS",rs.getString("passwordJKS"));
                dataHash.put("merchantP12",rs.getString("merchantP12"));
                dataHash.put("passwordP12",rs.getString("passwordP12"));
            }
            transactionLogger.error("ems data---"+stmt);
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError::::::",se);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException::::::",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return dataHash;
    }
}
