package com.payment.zotapay;

import com.directi.pg.TransactionLogger;
import com.payment.Enum.PZProcessType;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.payforasia.core.PayforasiaPaymentGateway;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 7/11/2018.
 */
public class ZotaPayUtils
{
    private static TransactionLogger transactionLogger= new TransactionLogger(ZotaPayUtils.class.getName());


    public static String doPostHTTPSURLConnectionClient(String strURL,String req) throws PZTechnicalViolationException
    {
        transactionLogger.error("strURL:::::"+ strURL);
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

    public static String getControl(String input) throws PZTechnicalViolationException
    {
        String sha = input;
        sha.trim();
        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            byte[] hash = digest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayforasiaPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayforasiaPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return hexString.toString().trim();
    }

    public static String EndPointCurrency(String currency,String is3DSupported,boolean isTest)
    {
        String endpoint = "";

        try
        {
            transactionLogger.debug("isTest----------" + isTest);
            if(isTest)
            {
                transactionLogger.debug("EndPointCurrency----------" + currency + is3DSupported);
                if (currency.equals("USD") && "Y".equalsIgnoreCase(is3DSupported))
                {
                    endpoint = "4043";
                }
                else if (currency.equals("EUR") && "Y".equalsIgnoreCase(is3DSupported))
                {
                    endpoint = "4044";
                }
                else if (currency.equals("GBP") && "Y".equalsIgnoreCase(is3DSupported))
                {
                    endpoint = "4045";
                }
                else if (currency.equals("USD") && "N".equalsIgnoreCase(is3DSupported))
                {
                    endpoint = "4190";
                }
                else if (currency.equalsIgnoreCase("EUR") && "N".equalsIgnoreCase(is3DSupported))
                {
                    endpoint = "4207";
                }
                else if (currency.equalsIgnoreCase("GBP") && "N".equalsIgnoreCase(is3DSupported))
                {
                    endpoint = "4208";
                }
            }else{
                transactionLogger.debug("EndPointCurrency----------" + currency + is3DSupported);
                if (currency.equals("USD") && "Y".equalsIgnoreCase(is3DSupported))
                {
                    endpoint = "4398";
                }
                else if (currency.equals("EUR") && "Y".equalsIgnoreCase(is3DSupported))
                {
                    endpoint = "4399";
                }
                else if (currency.equals("GBP") && "Y".equalsIgnoreCase(is3DSupported))
                {
                    endpoint = "4400";
                }
                else if (currency.equals("USD") && "N".equalsIgnoreCase(is3DSupported))
                {
                    endpoint = "6565";
                }
                else if (currency.equalsIgnoreCase("EUR") && "N".equalsIgnoreCase(is3DSupported))
                {
                    endpoint = "6566";
                }
                else if (currency.equalsIgnoreCase("GBP") && "N".equalsIgnoreCase(is3DSupported))
                {
                    endpoint = "6567";
                }

            }

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
        return endpoint;
    }

    public static String getTransactionType(String transType){

        if("sale".equalsIgnoreCase(transType)){
            transType= PZProcessType.SALE.toString();
        }else if("preauth".equalsIgnoreCase(transType)){
                transType=PZProcessType.AUTH.toString();
        }else if("capture".equalsIgnoreCase(transType)){
                transType=PZProcessType.CAPTURE.toString();
        }else if("reversal".equalsIgnoreCase(transType)){
            transType=PZProcessType.REFUND.toString();
        }else if("void".equalsIgnoreCase(transType)){
            transType=PZProcessType.CANCEL.toString();
        }
        return transType;
    }

    public static String getAmount(String amount)
    {
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;

        Integer newAmount = dObj2.intValue();

        return newAmount.toString();
    }

    public static Map<String, String> getQueryMap(String query)
    {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String[] p = param.split("=",2);
            String name = p[0];
            if (p.length > 1)
            {
                String value = p[1];
                map.put(name.trim(), value.trim());
                transactionLogger.debug(name+":::"+value);
            }
        }
        return map;
    }
}
