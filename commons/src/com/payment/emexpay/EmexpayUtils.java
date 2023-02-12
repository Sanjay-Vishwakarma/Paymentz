package com.payment.emexpay;

import com.directi.pg.Database;
import com.directi.pg.EmexpayLogger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.trustly.api.commons.exceptions.TrustlyConnectionException;
import com.payment.utils.SSLUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Admin on 12/8/2017.
 */
public class EmexpayUtils
{
    private  static TransactionLogger transactionLogger= new TransactionLogger(EmexpayUtils.class.getName());
    //EmexpayLogger emexpayLogger=new EmexpayLogger(EmexpayPaymentGateway.class.getName());

    public String doPostHTTPSURLConnectionClient(String strURL, String req, String authType, String encodedCredentials) throws PZTechnicalViolationException
    {
        transactionLogger.error("strURL-->" + strURL);
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Authorization", authType + " " + encodedCredentials);
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Cache-Control", "no-cache");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException--" + he.getMessage());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException--"+io.getMessage());
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public String newHttpPost(String url,String request, String authType, String encodedCredentials)throws PZTechnicalViolationException
    {
        String fResult = "";
        try
        {
            final CloseableHttpClient httpClient = HttpClients.createDefault();
            final HttpPost httpPost = new HttpPost(url);

            final StringEntity jsonRequest = new StringEntity(request, "UTF-8");
            httpPost.addHeader("content-type", "application/json");
            httpPost.addHeader("Authorization", authType + " " + encodedCredentials);
            httpPost.setEntity(jsonRequest);

            final HttpResponse result = httpClient.execute(httpPost);
            fResult = EntityUtils.toString(result.getEntity(), "UTF-8");

            //transactionLogger.error("fResult in newHttpPost---"+fResult);
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException in newHttpPost----",io);
            PZExceptionHandler.raiseTechnicalViolationException("EmexpayUtils.java","newHttpGet()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }
        return fResult;
    }


    public  String doGetHTTPSURLConnectionClient(String url, String authType, String encodedCredentials) throws PZTechnicalViolationException
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
            method.setRequestHeader("Authorization", authType+ " " +encodedCredentials);
            method.setRequestHeader("Cache-Control", "no-cache");

            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK)
            {
                transactionLogger.debug("Method failed: " + method.getStatusLine());
            }

            byte[] response = method.getResponseBody();
            //emexpayLogger.debug("Response in EmexUtils-----" + response.toString());
            result = new String(response);

            method.releaseConnection();
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException----",he);
            PZExceptionHandler.raiseTechnicalViolationException("NetellerUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException----",io);
            PZExceptionHandler.raiseTechnicalViolationException("NetellerUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }

        if (result == null)
            return "";
        else
            return result;
    }


    public String newHttpGet(String url, String authType, String encodedCredentials)throws PZTechnicalViolationException
    {
        String fResult = "";
        try
        {
            final CloseableHttpClient httpClient = HttpClients.createDefault();
            final HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Authorization", authType + " " + encodedCredentials);

            final HttpResponse result = httpClient.execute(httpGet);
            fResult =  EntityUtils.toString(result.getEntity(), "UTF-8");

            //transactionLogger.error("fResult in newHttpGet---"+fResult);
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException in newHttpGet----",io);
            PZExceptionHandler.raiseTechnicalViolationException("EmexpayUtils.java","newHttpGet()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }
        return fResult;
    }

    public Double getExchangeRate(String fromCurrency, String toCurrency)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        Double exchangeRate = null;
        try
        {
            conn = Database.getConnection();
            String query = "select exchange_rate from currency_exchange_rates where from_currency=? and to_currency=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, fromCurrency);
            stmt.setString(2, toCurrency);
            resultSet = stmt.executeQuery();
            if (resultSet.next())
            {
                exchangeRate = resultSet.getDouble("exchange_rate");
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError::::::", se);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException::::::", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return exchangeRate;
    }

    public boolean changeTerminalInfo(String amount, String currency, String accountId, String fromId, String templateAmount, String templateCurrency, String trackingId, String terminalId)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        int k = 0;
        boolean result = false;
        try
        {
            conn = Database.getConnection();
            String query = "update transaction_common set amount=?,currency=?,accountid=?,fromid=?,templateamount=?,templatecurrency=?,terminalId=? where trackingid=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, amount);
            stmt.setString(2, currency);
            stmt.setString(3, accountId);
            stmt.setString(4, fromId);
            stmt.setString(5, templateAmount);
            stmt.setString(6, templateCurrency);
            stmt.setString(7, terminalId);
            stmt.setString(8, trackingId);
            k = stmt.executeUpdate();
            if (k > 0)
            {
                result = true;
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError::::::", se);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException::::::", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return result;
    }
}
