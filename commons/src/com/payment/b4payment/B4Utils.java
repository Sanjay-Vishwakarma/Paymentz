package com.payment.b4payment;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.utils.SSLUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

/**
 * Created by Swamy on 18-01-2017.
 */
public class B4Utils
{
    private static Logger logger= new Logger(B4Utils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(B4Utils.class.getName());

    static String doHttpConnection(String sUrl, String data) throws PZTechnicalViolationException
    {
        StringBuffer strResponse = new StringBuffer();
        try
        {
            URL url = new URL(sUrl);
            transactionLogger.error("sUrl:::::" + sUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            OutputStreamWriter outSW = new OutputStreamWriter(connection.getOutputStream());
            outSW.write(data);
            outSW.flush();
            outSW.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String decodedString;
            while ((decodedString = in.readLine()) != null)
            {
                strResponse.append(decodedString);
            }
            in.close();
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException::" + e.getMessage());
        }
        return strResponse.toString();
    }

    static String doHttpConnectionHeader(String sUrl, String data, String token, String scope) throws PZTechnicalViolationException
    {
        StringBuffer strResponse = new StringBuffer();
        URL url = null;
        try
        {
            url = new URL(sUrl);
            transactionLogger.error("sUrl:::::" + sUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            // Set request headers for content type and length
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/vnd.b4payment-" + scope + "+json");
            connection.setRequestProperty("Authorization", "Bearer " + token + "");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            OutputStreamWriter outSW = new OutputStreamWriter(connection.getOutputStream());
            outSW.write(String.valueOf(data));
            outSW.flush();
            outSW.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String decodedString;
            while ((decodedString = in.readLine()) != null)
            {
                strResponse.append(decodedString);
            }
            in.close();
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException:::::" + e.getMessage());
        }
        return strResponse.toString();
    }

    public  static String doGetHTTPSURLConnectionClient(String url, String token, String scope) throws PZTechnicalViolationException
    {
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();

        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);

        String result = "";
        try
        {
            method.setRequestHeader("Accept", "application/vnd.b4payment-"+scope+"+json");
            method.setRequestHeader("Authorization", "Bearer " + token + "");
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK)
            {
                System.err.println("Method failed: " + method.getStatusLine());
            }

            byte[] response = method.getResponseBody();
            result = new String(response);

            method.releaseConnection();
        }
        catch (IOException e)
        {
            transactionLogger.debug("IOException::" + e.getMessage());
        }

        if (result == null)
            return "";
        else
            return result;
    }

    public static String generateToken(int size)
    {
        String tokenData = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int len = tokenData.length();
        Date date = new Date();
        Random rand = new Random(date.getTime());
        StringBuffer token = new StringBuffer();
        int index = -1;

        for (int i = 0; i < size; i++)
        {
            index = rand.nextInt(len);
            Collections.shuffle(Arrays.asList(token));
            token.append(tokenData.substring(index, index + 1));
        }
        return token.toString();
    }

    public static void main(String[] args)
    {
        String tokenData = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int len = tokenData.length();
        Date date = new Date();
        Random rand = new Random(date.getTime());
        StringBuffer token = new StringBuffer();
        int index = -1;

        for (int i = 0; i < 5 ; i++)
        {
            index = rand.nextInt(len);
            Collections.shuffle(Arrays.asList(token));
            token.append(tokenData.substring(index, index + 1));
        }
    }

    public CommRequestVO getRequestForB4SEPATransfer(CommonValidatorVO commonValidatorVO)
    {
        logger.debug("Inside b4Utils of getRequestForB4SEPATransfer");
        transactionLogger.debug("Inside b4Utils of getRequestForB4SEPATransfer");
        CommRequestVO commRequestVO = new CommRequestVO();

        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount()); //Amount * 100 according to the docs
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());

        commAddressDetailsVO.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());

        commAddressDetailsVO.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());

        commAddressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        commAddressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        commAddressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        commAddressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());

        commCardDetailsVO.setMandateId(commonValidatorVO.getCardDetailsVO().getMandateId());
        commCardDetailsVO.setBIC(commonValidatorVO.getCardDetailsVO().getBIC());
        commCardDetailsVO.setIBAN(commonValidatorVO.getCardDetailsVO().getIBAN());

        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        commRequestVO.setCardDetailsVO(commCardDetailsVO);

        return commRequestVO;
    }
}
