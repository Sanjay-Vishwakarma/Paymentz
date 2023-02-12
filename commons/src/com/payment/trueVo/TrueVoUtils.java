package com.payment.trueVo;

import com.directi.pg.TransactionLogger;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.tika.io.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by admin on 13-Nov-19.
 */
public class TrueVoUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(TrueVoUtils.class.getName());
    public static String doPostHTTPSURLConnectionClient(String strURL, String req) throws PZTechnicalViolationException
    {
        StringBuffer result = new StringBuffer();
        URL obj;
        HttpURLConnection con =null;
        InputStream is = null;
        try
        {
            System.out.println("inside TrueVo util--------------");
            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            System.setProperty("https.protocols", "TLSv1.2");

            obj=new URL(strURL);
            con=(HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(req);
            wr.flush();
            wr.close();

            if (con.getResponseCode() >= 400) is = con.getErrorStream();
            else is = con.getInputStream();
            return IOUtils.toString(is);
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException---->",he);
            PZExceptionHandler.raiseTechnicalViolationException("TrueVoUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException---->", io);
            PZExceptionHandler.raiseTechnicalViolationException("TrueVoUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("TrueVoUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                }
            }
            if(con != null)
            {
                con.disconnect();
            }
        }
        return result.toString();
    }

    public static String doPostHTTPSURLConnectionClient(String strURL, String authorizationToken,String req) throws PZTechnicalViolationException
    {
        transactionLogger.error("TrueVoUtils:: Inside doPostHTTPSURLConnectionClient() of TrueVoUtils.....!!!"+strURL);
        String result = "";
        try
        {
            HttpClient httpClient = new HttpClient();
            PostMethod post = new PostMethod(strURL);
            post.addRequestHeader("Authorization","Bearer "+authorizationToken+"");
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.addRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("TrueVoUtils:: HttpException-----"+he);
        }
        catch (IOException io){
            transactionLogger.error("TrueVoUtils:: IOException-----"+io);
        }
        return result;
    }

    public String getTrueVoCardTypeName(String cardType)
    {
        String value = "";
        if ("VISA".equals(cardType))
        {
            value = "VISA";
        }
        else if ("MC".equals(cardType))
        {
            value = "MASTER";
        }
        else if ("JCB".equals(cardType))
        {
            value = "JCB";
        }
        else if ("AMEX".equals(cardType))
        {
            value = "AMEX";
        }
        else if ("DISC".equals(cardType))
        {
            value = "DISCOVER";
        }
        else if ("DINER".equals(cardType))
        {
            value = "DINERS";
        }
        return value;
    }
    public static String doHttpPostConnection(String operationUrl, String credentials) throws PZTechnicalViolationException
    {
        InputStream is=null;
        try{
//            URL url = new URL("https://test.truevo.eu/v1/payments/8ac7a49f6e690614016e69a0aa934e67?entityId=8ac7a4ca6e6530e4016e68889f740552");
            URL url = new URL(operationUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + credentials);
            int responseCode = conn.getResponseCode();
            if (responseCode >= 400) is = conn.getErrorStream();
            else is = conn.getInputStream();

           return IOUtils.toString(is);
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception---->",e);
        }
        return is.toString();
    }

    public HashMap getTransactionStatus( String trackingId)
    {
        HashMap<String,String> hp= new HashMap();
        String dbStatus="";
        String transactionTime="";
        TransactionManager transactionManager = new TransactionManager();
        TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
        if (transactionDetailsVO != null)
        {
            dbStatus = transactionDetailsVO.getStatus();
            transactionTime=transactionDetailsVO.getTransactionTime();
        }
        hp.put("dbStatus",dbStatus);
        hp.put("transactionTime",transactionTime);

        return hp;
    }
}
