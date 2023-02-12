package com.payment.sms;

import com.directi.pg.Logger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 9/5/15
 * Time: 4:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class SMSGateway
{

    public final static String charset = "UTF-8";
    private static Logger logger = new Logger(SMSGateway.class.getName());

    public static String doPostHTTPURLConnection(String strURL, String msg) throws PZTechnicalViolationException
    {
        String result = null;
        try
        {
            String encodedURL = strURL + URLEncoder.encode(msg, "UTF-8");
            URL url = new URL(encodedURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            result = conn.getResponseMessage();
            logger.debug("encodedURL---"+encodedURL);
            if ("OK".equals(result))
            {
                logger.debug("sms sent successfully" + result);
            }
            else
            {
                logger.debug("sms sending failed");
            }
        }
        catch (MalformedURLException me)
        {
            logger.error("MalformedURLException:::::" ,me);
            PZExceptionHandler.raiseTechnicalViolationException("SMSGateway.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON, null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe)
        {
            logger.error("ProtocolException:::::" , pe);
            PZExceptionHandler.raiseTechnicalViolationException("SMSGateway.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION, null, pe.getMessage(), pe.getCause());
        }
        catch (UnsupportedEncodingException pe)
        {
            logger.error("UnsupportedEncodingException:::::" , pe);
            PZExceptionHandler.raiseTechnicalViolationException("SMSGateway.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION, null, pe.getMessage(), pe.getCause());
        }
        catch (IOException ex)
        {
            logger.error("IOException:::::" , ex);
            PZExceptionHandler.raiseTechnicalViolationException("SMSGateway.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, ex.getMessage(), ex.getCause());
        }
        catch (Exception ex)
        {
            logger.error("IOException:::::" , ex);
            PZExceptionHandler.raiseTechnicalViolationException("SMSGateway.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, ex.getMessage(), ex.getCause());
        }
        return result;
    }

    public static void main(String args[]) throws Exception
    {
        String strUrl = "http://203.212.70.200/smpp/sendsms?username=tc2015&password=tc2015&to=9730490101&from=TRANSE&text=";
        String result = SMSGateway.doPostHTTPURLConnection(strUrl, "Its a test message for checking SMSGateway is working or not with the below url \"www.google.com\"");
    }


    public static String doPostJSONHTTPURLConnection(String strURL, String data,String authorization) throws PZTechnicalViolationException
    {
        String result   = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.setRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("authorization",authorization);

            post.setRequestBody(data);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (MalformedURLException me)
        {
            logger.error("MalformedURLException:::::" ,me);
            PZExceptionHandler.raiseTechnicalViolationException("SMSGateway.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON, null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe)
        {
            logger.error("ProtocolException:::::" , pe);
            PZExceptionHandler.raiseTechnicalViolationException("SMSGateway.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION, null, pe.getMessage(), pe.getCause());
        }
        catch (UnsupportedEncodingException pe)
        {
            logger.error("UnsupportedEncodingException:::::" , pe);
            PZExceptionHandler.raiseTechnicalViolationException("SMSGateway.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION, null, pe.getMessage(), pe.getCause());
        }
        catch (IOException ex)
        {
            logger.error("IOException:::::" , ex);
            PZExceptionHandler.raiseTechnicalViolationException("SMSGateway.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, ex.getMessage(), ex.getCause());
        }
        catch (Exception ex)
        {
            logger.error("IOException:::::" , ex);
            PZExceptionHandler.raiseTechnicalViolationException("SMSGateway.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, ex.getMessage(), ex.getCause());
        }
        return result;
    }
}
