package com.payment.allPay88;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.manager.vo.ReserveField2VO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpStatus;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by admin on 03-12-2018.
 */
public class AllPay88Utils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(AllPay88Utils.class.getName());

    public static String doPostHTTPSURLConnectionClient(String strURL, String req) throws PZTechnicalViolationException
    {
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();

           // post.addRequestHeader("Authorization", authType + " " + encodedCredentials);
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Cache-Control", "no-cache");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he)
        {
            transactionLogger.debug("HttpException--" + he.getMessage());
        }
        catch (IOException io)
        {
            transactionLogger.debug("IOException--"+io.getMessage());
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
    public  static String doGetHTTPSURLConnectionClient(String url) throws PZTechnicalViolationException
    {
       transactionLogger.debug("url--->" + url);
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();

        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        String result = "";
        try
        {
            method.setRequestHeader("Accept", "application/json");
          //  method.setRequestHeader("Authorization", authType+ " " +encodedCredentials);
            method.setRequestHeader("Cache-Control", "no-cache");

            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK)
            {
                transactionLogger.debug("Method failed: " + method.getStatusLine());
            }

            byte[] response = method.getResponseBody();
           transactionLogger.debug("Response-----" + response.toString());
            result = new String(response);

            method.releaseConnection();
        }
        catch (HttpException he)
        {
          transactionLogger.debug("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("NetellerUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
          transactionLogger.debug("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("NetellerUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }

        if (result == null)
            return "";
        else
            return result;
    }

    public static  String doPostHTTPSURLConnectionClient1(String strURL,String sign) throws PZTechnicalViolationException
    {
     transactionLogger.debug(" strURL:::"+strURL);
        StringBuffer result = new StringBuffer();
        try
        {
            URL obj = new URL(strURL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Content-Hmac", sign);
            // Send post request
            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
           // wr.write(req);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                result.append(inputLine);
            }
            in.close();
        }
        catch (HttpException he)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PaySpaceUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PaySpaceUtils.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }
        return result.toString();
    }

    public static String doPostHTTPSURLConnection(String strURL, String req) throws PZTechnicalViolationException
    {
        StringBuffer result = new StringBuffer();
        Functions functions=new Functions();
        URL obj;
        HttpURLConnection con=null;
        BufferedReader in = null;
        try
        {
            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            System.setProperty("http.agent", "");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            System.setProperty("https.protocols", "TLSv1.2");

            obj = new URL(strURL);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(req);
            wr.flush();
            wr.close();

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                result.append(inputLine);
            }

        }

        catch (HttpException he)
        {
            PZExceptionHandler.raiseTechnicalViolationException("EPaySolutionUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("EPaySolutionUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            if(in !=null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    transactionLogger.error("IOException In  AllPay88Utils --",e);
                }
            }

        }
        return result.toString();
    }

    public CommRequestVO getAllPay88RequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        ReserveField2VO reserveField2VO= new ReserveField2VO();

        String paymentType = GatewayAccountService.getPaymentTypes(commonValidatorVO.getPaymentType());
        String cardType = GatewayAccountService.getCardType(commonValidatorVO.getCardType());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        transDetailsVO.setRedirectUrl(commonValidatorVO.getTransDetailsVO().getRedirectUrl());
        transDetailsVO.setPaymentType(paymentType.toLowerCase()); //value parameter in paymentMethods
        transDetailsVO.setCardType(cardType.toLowerCase()); //type parameter in paymentMethods
        transDetailsVO.setCustomerId(commonValidatorVO.getCustomerId());
        addressDetailsVO.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());

        reserveField2VO.setBankName(commonValidatorVO.getReserveField2VO().getBankName());


        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        merchantAccountVO.setDisplayName(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        merchantAccountVO.setAliasName(commonValidatorVO.getMerchantDetailsVO().getSiteName());

        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setReserveField2VO(reserveField2VO);
        return commRequestVO;
    }

    public static  String getSignature(String Reqparameter,String apiKey)
    {
        String result="";
        try
        {
            String HMAC_SHA1_ALGORITHM = "HmacSHA1";
            SecretKeySpec signingKey = new SecretKeySpec(apiKey.getBytes(), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(Reqparameter.getBytes());
            result = com.directi.pg.Base64.encode(rawHmac);

        }catch (NoSuchAlgorithmException e){
          transactionLogger.debug("NoSuchAlgorithmException-----"+e);
        }catch (InvalidKeyException k){
           transactionLogger.debug("InvalidKeyException-----"+k);
        }

        return result;

    }


}
