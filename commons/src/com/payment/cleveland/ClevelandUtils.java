package com.payment.cleveland;

import com.directi.pg.Database;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Admin on 10/15/2021.
 */
public class ClevelandUtils
{
    private static TransactionLogger transactionlogger      = new TransactionLogger(ClevelandUtils.class.getName());
    public static String doGetHTTPSURLConnectionClient(String url,String request) throws PZTechnicalViolationException
    {
        String result   = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.setRequestHeader("Content-Type", "application/json");

            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he)
        {
            transactionlogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("ClevelandUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionlogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("ClevelandUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {
            post.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }

    public static String generateAutoSubmitForm(Comm3DResponseVO commResponseVO)
    {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(commResponseVO.getUrlFor3DRedirect()).append("\" method=\"POST\">\n");
        html.append("</form>\n");
        return html.toString();
    }


    public  static String doGetHTTPSURLConnectionClient(String url) throws PZTechnicalViolationException
    {
        transactionlogger.debug("url--->" + url);
        GetMethod method = new GetMethod(url);
        String result = "";
        try
        {
            HttpClient client = new HttpClient();
            method.setRequestHeader("Content-Type", "application/json");

            //method.setRequestBody(request);
            client.executeMethod(method);
            String response = new String(method.getResponseBody());
            result          = response;
            transactionlogger.error("Response for INQUERY IN UTILS==========>"+result);

        }
        catch (HttpException he)
        {
            transactionlogger.debug("HttpException----" + he);
            PZExceptionHandler.raiseTechnicalViolationException("Cleveland.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null,he.getMessage(),he.getCause());
        }
        catch (IOException io)
        {
            transactionlogger.debug("IOException----" + io);
            PZExceptionHandler.raiseTechnicalViolationException("Cleveland.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {
            method.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }
    public void updatePayGTransctionId(String transctionId,String trackingid)
    {
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET paymentid=? WHERE trackingid=?";
            PreparedStatement ps2   = connection.prepareStatement(updateQuery1);
            ps2.setString(1, transctionId);
            ps2.setString(2, trackingid);
            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            transactionlogger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            transactionlogger.error("SystemError---", s);
        }
        finally
        {
            if(connection != null){
                Database.closeConnection(connection);
            }
        }
    }

}
