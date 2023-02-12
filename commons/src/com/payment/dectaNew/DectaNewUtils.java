package com.payment.dectaNew;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.CommRequestVO;
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
public class DectaNewUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(DectaNewUtils.class.getName());

    public static String doPostFormHTTPSURLConnectionClient(String strURL,String req) throws PZTechnicalViolationException
    {
        transactionLogger.error("strURL:::::"+ strURL);
        String result = "";

        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strURL);
        try
        {
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(req);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException In DectaNewUtils :::::",he);
        }
        catch (IOException io){
            transactionLogger.error("IOException In DectaNewUtils :::::", io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }


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
            transactionLogger.error("HttpException In DectaNewUtils :::::", he);
        }
        catch (IOException io){
            transactionLogger.error("IOException In DectaNewUtils :::::", io);
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




    public static String getCardPaymentForm(CommonValidatorVO commonValidatorVO,String url )
    {
        transactionLogger.error("Card form Decta Utils....." );
        Functions function = new Functions();

        String form="<form name=\"cardPayment\" method=\"POST\" action=\""+url+"\">"+
                "<input type=\"hidden\" name=\"cardholder_name\" value=\""+commonValidatorVO.getAddressDetailsVO().getFirstname()+" "+ commonValidatorVO.getAddressDetailsVO().getLastname() +"\">"+
                "<input type=\"hidden\" name=\"number\" value=\""+commonValidatorVO.getCardDetailsVO().getCardNum()+"\">"+
                "<input type=\"hidden\" name=\"exp_month\" value=\""+commonValidatorVO.getCardDetailsVO().getExpMonth()+"\">"+
                "<input type=\"hidden\" name=\"exp_year\" value=\""+commonValidatorVO.getCardDetailsVO().getExpYear().substring(2)+"\">"+
                "<input type=\"hidden\" name=\"csc\" value=\""+commonValidatorVO.getCardDetailsVO().getcVV()+"\">"+
                "</form>"+
                "<script language=\"javascript\"> document.cardPayment.submit(); </script>";

        String formLogs="<form name=\"cardPayment\" method=\"POST\" action=\""+url+"\">"+
                "<input type=\"hidden\" name=\"cardholder_name\" value=\""+commonValidatorVO.getAddressDetailsVO().getFirstname()+" "+ commonValidatorVO.getAddressDetailsVO().getLastname() +"\">"+
                "<input type=\"hidden\" name=\"number\" value=\""+function.maskingPan(commonValidatorVO.getCardDetailsVO().getCardNum())+"\">"+
                "<input type=\"hidden\" name=\"exp_month\" value=\""+function.maskingNumber(commonValidatorVO.getCardDetailsVO().getExpMonth())+"\">"+
                "<input type=\"hidden\" name=\"exp_year\" value=\""+function.maskingNumber(commonValidatorVO.getCardDetailsVO().getExpYear().substring(2))+"\">"+
                "<input type=\"hidden\" name=\"csc\" value=\""+function.maskingNumber(commonValidatorVO.getCardDetailsVO().getcVV())+"\">"+
                "</form>"+
                "<script language=\"javascript\"> document.cardPayment.submit(); </script>";

        transactionLogger.error("Decta Card form =="+formLogs);
        return form;

    }

    public static String getTransactionType(String transType){

        if("hold".equalsIgnoreCase(transType))
        {
            transType= PZProcessType.AUTH.toString();
        }
        else if("cancelled".equalsIgnoreCase(transType))
        {
            transType=PZProcessType.CANCEL.toString();
        }
        else
        {
            transType=PZProcessType.INQUIRY.toString();
        }

        return transType;
    }


}