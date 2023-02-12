package com.payment.OneRoadPayments;

import com.directi.pg.TransactionLogger;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import com.payment.validators.vo.CommonValidatorVO;
import com.sun.java.util.jar.pack.*;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by Jeet Gupta on 01-04-2019.
 */
public class OneRoadUtils
{
    private static final  TransactionLogger transactionLogger = new TransactionLogger(OneRoadUtils.class.getName());

    public String getRedirectForm(String trackingId, Comm3DResponseVO response3D)
    {
        transactionLogger.error("redirect url---" + response3D.getRedirectUrl());
        transactionLogger.error("trackingid inside OneRoad Utils -------"+trackingId);

        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(response3D.getRedirectUrl()).append("\" method=\"post\">\n");
        html.append("<input type=\"hidden\" name=\"merchantID\" value=\"" + response3D.getRequestMap().get("merchantID") + "\">");
        html.append("<input type=\"hidden\" name=\"clientID\" value=\"" + response3D.getRequestMap().get("clientID") + "\">");
        html.append("<input type=\"hidden\" name=\"orderID\" value=\"" +trackingId+ "\">");
        html.append("<input type=\"hidden\" name=\"amount\" value=\"" +response3D.getRequestMap().get("amount")+ "\">");
        html.append("<input type=\"hidden\" name=\"cardType\" value=\"" +response3D.getRequestMap().get("cardType")+"\">");
        html.append("<input type=\"hidden\" name=\"currency\" value=\""+response3D.getRequestMap().get("currency")+"\">");//
        html.append("<input type=\"hidden\" name=\"signature\" value=\""+response3D.getRequestMap().get("signature")+"\">");
        html.append("<input type=\"hidden\" name=\"signatureVersion\" value=\"" +response3D.getRequestMap().get("signatureVersion")+"\">");
        html.append("<input type=\"hidden\" name=\"replyURL\" value=\"" +response3D.getRequestMap().get("replyURL")+ "\">");
        html.append("<input type=\"hidden\" name=\"encoding\" value=\"utf-8\">");
        html.append("</form>\n");

        transactionLogger.error("form----"+html.toString());
        return html.toString();
    }


    public static CommRequestVO getOneRoadUtils(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("Inside OneRoadUtils getOneRoadUtils -----");
        CommRequestVO commRequestVO = new CommRequestVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setCardType(commonValidatorVO.getCardType());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        commMerchantVO.setMerchantId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }

    public static String getMd5(String input)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            String hashtext = no.toString(16);
            while (hashtext.length() < 32)
            {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }

    }
    public  static String doGetHTTPSURLConnectionClient(String url) throws PZTechnicalViolationException
    {
        transactionLogger.error("url--->" + url);
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
            transactionLogger.error("Response-----" + response.toString());
            result = new String(response);

        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("OneRoadUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("OneRoadUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {
            method.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }

}
