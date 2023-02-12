package com.payment.whitelabel;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.vo.DirectKitResponseVO;
import com.payment.Enum.PZProcessType;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Created by Uday on 8/22/18.
 */
public class WLUtils
{
    static TransactionLogger transactionLogger= new TransactionLogger(WLUtils.class.getName());

    public static String doPostHTTPSURLConnectionClient(String strURL,String req) throws PZTechnicalViolationException
    {
        transactionLogger.error("strURL:::::" + strURL);
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();

            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException-->",he);
        }
        catch (IOException io){
            transactionLogger.error("IOException-->", io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
    public static String doPostHTTPSURLConnectionClient(String strURL, String token,String req) throws PZTechnicalViolationException
    {
        transactionLogger.error("strURL:::::" + strURL);
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();

            post.addRequestHeader("AuthToken",token);
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException-->", he);
        }
        catch (IOException io){
            transactionLogger.error("IOException-->", io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String getCheckSum(String merchantId,String key,String orderId,String amount)
    {
        String generatedCheckSum="";
        String values=merchantId+"|"+key+"|"+orderId+"|"+amount;
        MessageDigest messageDigest = null;
        try
        {
            messageDigest = MessageDigest.getInstance("MD5");
            generatedCheckSum = getString(messageDigest.digest(values.getBytes()));
        }
        catch (NoSuchAlgorithmException e)
        {
           transactionLogger.error("NoSuchAlgorithmException-----",e);
        }
        return generatedCheckSum;
    }

    public static String getCheckSum(String merchantId,String key,String orderId)
    {
        String generatedCheckSum="";
        String values=merchantId+"|"+key+"|"+orderId;
        MessageDigest messageDigest = null;
        try
        {
            messageDigest = MessageDigest.getInstance("MD5");
            generatedCheckSum = getString(messageDigest.digest(values.getBytes()));
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException-----",e);
        }
        return generatedCheckSum;
    }

    public static String getCheckSumPayout(String merchantId,String orderId,String amount,String key)
    {
        String generatedCheckSum="";
        String values=merchantId+"|"+orderId+"|"+amount+"|"+key;
        MessageDigest messageDigest = null;
        try
        {
            messageDigest = MessageDigest.getInstance("MD5");
            generatedCheckSum = getString(messageDigest.digest(values.getBytes()));
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException-----",e);
        }
        return generatedCheckSum;
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

    public String getForm(Comm3DResponseVO comm3DResponseVO){
        transactionLogger.error("-----inside getForm-----");
        String html="";
        StringBuilder sb= new StringBuilder();
        HashMap map= (HashMap) comm3DResponseVO.getRequestMap();
        sb.append("<form name=\"launch\" action=\""+comm3DResponseVO.getUrlFor3DRedirect()+"\" method=\"POST\">");
        for(Object key : map.keySet()){
            sb.append("<input type=\"hidden\" name=\""+key.toString()+"\" value=\""+map.get(key.toString())+"\"");
        }
        sb.append("</form>");
        sb.append("<script language=\"javascript\"> document.launch.submit(); </script>");

        html=sb.toString();
        transactionLogger.error("-----form-----"+html);
        return html;
    }

    public static String getTranstype(String status){
        String transType="";

        switch (status){
            case "authsuccessful":
                return transType=PZProcessType.AUTH.toString();
            case "capturesuccess":
                return transType=PZProcessType.SALE.toString();
            case "reversed":
                return transType=PZProcessType.REFUND.toString();
            case "authcancelled":
                return transType=PZProcessType.CANCEL.toString();
            case "payoutsuccessful":
                return transType=PZProcessType.PAYOUT.toString();
            default:
                transType=PZProcessType.INQUIRY.toString();

        }


        return transType;
    }
}
