package com.payment.npayon;

import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.apache.commons.httpclient.HttpException;
import org.apache.tika.io.IOUtils;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Created by Sandip on 6/19/2018.
 */
public class NPayOnUtils
{
    public final static String charset = "UTF-8";
    private static TransactionLogger transactionLogger = new TransactionLogger(NPayOnUtils.class.getName());

    public static String doPostHTTPSURLConnectionClient(String strURL, String req) throws PZTechnicalViolationException
    {
        StringBuffer result = new StringBuffer();
        URL obj;
        HttpURLConnection con =null;
        InputStream is = null;
        try
        {
            System.out.println("inside npayon util--------------");
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
            transactionLogger.error("HttpException :::::::::",he);
            PZExceptionHandler.raiseTechnicalViolationException("RSPUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException :::::::::", io);
            PZExceptionHandler.raiseTechnicalViolationException("RSPUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
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
                    PZExceptionHandler.raiseTechnicalViolationException("WirecardUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                }
            }
            if(con != null)
            {
                con.disconnect();
            }
        }
        return result.toString();
    }

    public static String doGetHTTPSURLConnectionClient(String strURL) throws PZTechnicalViolationException
    {
        StringBuffer result = new StringBuffer();
        URL obj;
        HttpURLConnection con=null;
        try
        {
            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            System.setProperty("https.protocols", "TLSv1.2");

            obj = new URL(strURL);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            InputStream is;
            if (con.getResponseCode() >= 400) is = con.getErrorStream();
            else is = con.getInputStream();
            return IOUtils.toString(is);
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException :::::::",he);
            PZExceptionHandler.raiseTechnicalViolationException("RSPUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("HttpException :::::::",io);
            PZExceptionHandler.raiseTechnicalViolationException("RSPUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            if(con != null)
            {
                con.disconnect();
            }
        }
        return result.toString();
    }

    public String getNPayOnCardTypeName(String cardType)
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
}
