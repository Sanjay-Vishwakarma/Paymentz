package com.payment.onlinepay;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
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

import java.io.IOException;

/**
 * Created by Rihen on 5/29/2019.
 */
public class OnlinePayUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(OnlinePayUtils.class.getName());


    public static String doPostHTTPSURLConnectionClient(String strURL,String req,String key) throws PZTechnicalViolationException
    {
        transactionLogger.error("strURL:::::"+ strURL);
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Authorization", "Bearer" + " " + key);
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Accept", "application/json");
            post.addRequestHeader("Cache-Control", "no-cache");
            post.setRequestBody(req);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException :::::::::::::",he);
        }
        catch (IOException io){
            transactionLogger.error("IOException :::::::::::::", io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }


    public static String doPostHTTPSURLConnectionClient2(String strURL,String req) throws PZTechnicalViolationException
    {
        transactionLogger.error("strURL:::::"+ strURL);
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.addRequestHeader("Cache-Control", "no-cache");
            post.setRequestBody(req);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException :::::::::::::", he);
        }
        catch (IOException io){
            transactionLogger.error("IOException :::::::::::::", io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public  static String doGetHTTPSURLConnectionClient(String url, String secretkey) throws PZTechnicalViolationException
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
            method.setRequestHeader("Authorization", "Bearer "+secretkey);
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
            PZExceptionHandler.raiseTechnicalViolationException("DectaNewUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null,he.getMessage(),he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.debug("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("DectaNewUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }

        if (result == null)
            return "";
        else
            return result;
    }


    public static String getRedirectForm(CommResponseVO response3D)
    {
        transactionLogger.error("redirect url---" + response3D.getRedirectUrl());
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.onlinePayForm.submit();}</script>\n");
//        html.append("<iframe id=\"myframe\" name=\"myframe\" width=\"100%\" height=\"500px\" ></iframe>");
        html.append("<form id=\"onlinePayForm\" name=\"onlinePayForm\" action=\"").append(response3D.getRedirectUrl()).append("\" method=\"post\"> </form>");


        transactionLogger.error("form----"+html.toString());
        return html.toString();
    }


    public static CommRequestVO getCommRequestFromUtils(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("----- inside getCommRequestFromOnlinePayUtils -----");
        Functions functions = new Functions();

        CommRequestVO commRequestVO = new CommRequestVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());

        if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderDesc()))
        {
            transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        }
        if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderId()))
        {
            transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        }

        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }




}