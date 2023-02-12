package com.payment.cybersource2;

import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.trustly.api.commons.exceptions.TrustlySignatureException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import sun.misc.BASE64Encoder;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.ResourceBundle;

/**
 * Created by Admin on 11/19/2020.
 */
public class CyberSource2Utils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(CyberSource2Utils.class.getName());

    public static String getPaymentMethod(String cardType)
    {
        String paymentMethod = "";

        if ("VISA".equalsIgnoreCase(cardType))
            paymentMethod = "00";
        else if ("MC".equalsIgnoreCase(cardType))
            paymentMethod = "01";

        return paymentMethod;
    }

    public static String doPostHTTPSURLConnectionClient(String strURL, String request,String authentication) throws PZTechnicalViolationException
    {
        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
            post.addRequestHeader("Signature", authentication);
            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("CyberSource2Utils:: HttpException-----", he);
            PZExceptionHandler.raiseTechnicalViolationException(CyberSource2Utils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("CyberSource2Utils:: IOException-----", io);
            PZExceptionHandler.raiseTechnicalViolationException(CyberSource2Utils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
    public static String doPostHTTPSURLConnectionClient(String strURL, String request) throws PZTechnicalViolationException
    {
        String result = "";
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("CyberSource2Utils:: HttpException-----", he);
            PZExceptionHandler.raiseTechnicalViolationException(CyberSource2Utils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("CyberSource2Utils:: IOException-----", io);
            PZExceptionHandler.raiseTechnicalViolationException(CyberSource2Utils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
    public static String getSignature(String plainText,String privatePath) {
        try {
            transactionLogger.error("plainText---->"+plainText);
            File file = new File(privatePath);

            BufferedReader br = new BufferedReader(new FileReader(file));

            String privateKey = "", st;

            while ((st = br.readLine()) != null) {
                privateKey += st;
            }
            String realPK = privateKey.replaceAll("-----BEGIN RSA PRIVATE KEY-----", "")
                    .replaceAll("-----END RSA PRIVATE KEY-----", "")
                    .replaceAll("\\r\\n|\\r|\\n", "");

            byte[] keyBytes = Base64.getDecoder().decode(realPK);
            PKCS1EncodedKeySpec spec = new PKCS1EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(kf.generatePrivate(spec.getKeySpec()));
            signature.update(plainText.getBytes("UTF-8"));
            byte[] signedBytes = signature.sign();

            return Base64.getEncoder().encodeToString(signedBytes);
        } catch (Exception ex) {
            transactionLogger.error("Exception CyberSource2Utils :::::::::::",ex);
        }
        return "";
    }
}
