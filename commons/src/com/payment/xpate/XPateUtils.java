package com.payment.xpate;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.codehaus.jettison.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * Created by jitendra on 12-Dec-19.
 */
public class XPateUtils
{

    public final static String charset = "UTF-8";
    private static Logger log = new Logger(XPateUtils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(XPateUtils.class.getName());

    public static String doPostHTTPSURLConnectionClient(String strURL, String req, String key) throws PZTechnicalViolationException
    {
        transactionLogger.error("inside doPostHTTPSURLConnectionClient --- ");
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();

            post.addRequestHeader("authorization", "Bearer" + " " + key);
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Accept", "application/json");
            post.addRequestHeader("Cache-Control", "no-cache");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException he)
        {
            // transactionLogger.error("-----Exception-----",e);
            PZExceptionHandler.raiseTechnicalViolationException("XPateUtils.java", "doPostHTTPSURLConnectionClient()", null, "common", "Http Exception:::", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException e)
        {
            // transactionLogger.error("-----Exception-----",e);
            PZExceptionHandler.raiseTechnicalViolationException("XPateUtils.java", "doPostHTTPSURLConnectionClient()", null, "common", "IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String doHttpPostConnection(String operationUrl,String req) throws PZTechnicalViolationException
    {
        InputStream inputStream;
        byte[] bytes=new byte[]{};
        try
        {
            URL url = new URL(null,operationUrl,new sun.net.www.protocol.https.Handler());
            // above first and last parameter in URL is used to handle ClassCalst Exception
            //com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl cannot be cast to javax.net.ssl.HttpsURLConnection
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //  conn.setRequestProperty("Authorization", type + "" + credentials);

            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
            dataOutputStream.writeBytes(req);
            dataOutputStream.flush();
            dataOutputStream.close();
            int responseCode = conn.getResponseCode();

            if (responseCode >= 400)
            {
                inputStream = conn.getErrorStream();
            }
            else
            {
                inputStream = conn.getInputStream();
            }
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
        }
        catch (IOException e)
        {
            // transactionLogger.error("-----Exception-----",e);
            PZExceptionHandler.raiseTechnicalViolationException("XPateUtils.java", "doHttpPostConnection()", null, "common", "IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return new String(bytes);
    }

    public static String doHttpPostConnectionForRefund(String operationUrl,String req,String type,String credentials) throws PZTechnicalViolationException
    {
        InputStream inputStream;
        byte[] bytes=new byte[]{};
        try
        {
            URL url = new URL(null,operationUrl,new sun.net.www.protocol.https.Handler());
            // above first and last parameter in URL is used to handle ClassCalst Exception
            //com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl cannot be cast to javax.net.ssl.HttpsURLConnection
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", type + " " + credentials);

            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
            dataOutputStream.writeBytes(req);
            dataOutputStream.flush();
            dataOutputStream.close();
            int responseCode = conn.getResponseCode();

            if (responseCode >= 400)
            {
                inputStream = conn.getErrorStream();
            }
            else
            {
                inputStream = conn.getInputStream();
            }
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
        }
        catch (IOException e)
        {
            // transactionLogger.error("-----Exception-----",e);
            PZExceptionHandler.raiseTechnicalViolationException("KortaPayUtils.java", "doPostHTTPSURLConnection()", null, "common", "IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return new String(bytes);
    }

    public static String doPostHTTPSURLConnectionInquiry(String strURL,String key) throws PZTechnicalViolationException
    {
        String result = "";

        GetMethod post = new GetMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();

            post.addRequestHeader("authorization", "Bearer" + " " + key);
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Accept", "application/json");
            post.addRequestHeader("Cache-Control", "no-cache");
            //post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
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

    public static String makeCurrencyConversion(String from_currency,String to_currency,String from_amount)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet=null;
        Double exchangeRate=null;
        Double converted_currency=null;
        try
        {
            conn = Database.getConnection();
            String query = "select exchange_rate from currency_exchange_rates where from_currency=? and to_currency=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,from_currency);
            stmt.setString(2,to_currency);
            resultSet=stmt.executeQuery();
            if(resultSet.next()){
                exchangeRate=resultSet.getDouble("exchange_rate");
                transactionLogger.error("Exchange rate-----"+exchangeRate);
                transactionLogger.error("Transaction request amount-----"+from_amount);
                transactionLogger.error("Transaction request currency-----"+from_currency);
            }
            converted_currency = Double.valueOf(from_amount) * exchangeRate;
        }
        catch(SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch(SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        return String.valueOf(converted_currency);
    }

    public static String getCentAmount(String amount)
    {
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;
        String amt = d.format(dObj2);
        return amt;
    }
    public static HashMap<String,String> getTerminalConversionDetails(String terminalID)
    {
        Connection con=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        HashMap<String,String> hashMap=null;

        try
        {
            con=Database.getConnection();
            String query="SELECT currency_conversion,conversion_currency FROM member_account_mapping WHERE terminalid=?";
            ps=con.prepareStatement(query);
            ps.setString(1,terminalID);
            rs=ps.executeQuery();
            transactionLogger.error("Query---"+ps);
            if(rs.next())
            {
                hashMap= new HashMap<>();
                hashMap.put("currency_conversion", rs.getString("currency_conversion"));
                hashMap.put("conversion_currency",rs.getString("conversion_currency"));
            }
        }catch (Exception e)
        {
            transactionLogger.error("Exception in getTerminalConversionDetails---",e);
        }
        finally
        {
            Database.closeConnection(con);
            Database.closePreparedStatement(ps);
            Database.closeResultSet(rs);
        }
        return hashMap;
    }
}
