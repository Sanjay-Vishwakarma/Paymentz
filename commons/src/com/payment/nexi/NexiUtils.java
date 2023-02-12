package com.payment.nexi;

/**
 * Created by Admin on 5/23/2019.
 */

import com.directi.pg.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.apache.commons.httpclient.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.security.MessageDigest;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Vivek
 * Date: 5/23/19
 * Time: 4:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class NexiUtils
{
    public final static String charset = "UTF-8";
    private static Logger log = new Logger(NexiUtils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(NexiUtils.class.getName());

    public static String doPostHTTPSURLConnectionClient(String strURL, String req) throws PZTechnicalViolationException
    {
        transactionLogger.error("----inside Connection-----" + strURL);
        String result = "";
        System.setProperty("jsse.enableSNIExtension", "false");

        CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
        HttpPost httpPost = new HttpPost(strURL);

        try
        {
            System.setProperty("https.protocols", "TLSv1.2");

            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            StringEntity jsonRequest = new StringEntity(req, "UTF-8");
            httpPost.addHeader("content-type", "application/json");
            httpPost.setEntity(jsonRequest);

            HttpResponse response = httpClient.execute(httpPost);
            result = EntityUtils.toString(response.getEntity(), "UTF-8");

        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException in NexiPaymentGateway---", he);
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException in NexiPaymentGateway---", io);
        }
        finally
        {
            httpPost.releaseConnection();
        }
        transactionLogger.error("result-----" + result);
        return result;
    }

    public static String hashMac(String stringaMac) throws Exception
    {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] in = digest.digest(stringaMac.getBytes());

        final StringBuilder builder = new StringBuilder();
        for (byte b : in)
        {
            builder.append(String.format("%02x", b)); // converting to hexa decimal
        }
        return builder.toString();
    }

    public static Hashtable getValuesFromDb(String accountid)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        Hashtable dataHash = new Hashtable();
        Functions functions = new Functions();
        try
        {
            conn = Database.getConnection();
            String query = "select * from gateway_accounts_nexi where accountid=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, accountid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                if (functions.isValueNull(rs.getString("3d_api_key")))
                    dataHash.put("3d_api_key", rs.getString("3d_api_key"));
                if (functions.isValueNull(rs.getString("3d_mac")))
                    dataHash.put("3d_mac", rs.getString("3d_mac"));
                if (functions.isValueNull(rs.getString("non3d_api_key")))
                    dataHash.put("non3d_api_key", rs.getString("non3d_api_key"));
                if (functions.isValueNull(rs.getString("non3d_mac")))
                    dataHash.put("non3d_mac", rs.getString("non3d_mac"));
            }

        }
        catch (SystemError se)
        {
            log.error("SystemError::::::", se);
            transactionLogger.error("SystemError::::::", se);
        }
        catch (SQLException e)
        {
            log.error("SQLException::::::", e);
            transactionLogger.error("SQLException::::::", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return dataHash;
    }

    public static String getTransactionType(String trackingid)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        Hashtable dataHash = new Hashtable();
        Functions functions = new Functions();
        String transType = "";
        try
        {
            conn = Database.getConnection();
            String query = "select transType from transaction_common_details where trackingid=? and status='capturesuccess'";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, trackingid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                transType = rs.getString("transType");
            }

        }
        catch (SystemError se)
        {
            log.error("SystemError::::::", se);
            transactionLogger.error("SystemError::::::", se);
        }
        catch (SQLException e)
        {
            log.error("SQLException::::::", e);
            transactionLogger.error("SQLException::::::", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transType;
    }


    public String getnum(String dates)
    {
        String currentDate = "";
        try
        {
            Date date1 = null;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            date1 = new Date();
            currentDate = simpleDateFormat.format(date1);

            Date reqdate = simpleDateFormat.parse(dates);

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -30);
            Timestamp d2 = new Timestamp(c.getTime().getTime());


            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -60);
            Timestamp d3 = new Timestamp(cal.getTime().getTime());


            if (dates.equals(""))
            {
                return "01";
            }
            else if (dates.equals(currentDate))
            {
                return "02";
            }
            else if (d2.before(reqdate))
            {
                return "03";
            }
            else if (d2.after(reqdate) && d3.before(reqdate))
            {
                return "04";
            }
            else if (d3.after(reqdate))
            {
                return "05";
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception :::::::::",e);
        }
        return "01";
    }

    public String getnumber(String dates)
    {

        String currentDate = "";
        try
        {
            Date date1 = null;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            date1 = new Date();
            currentDate = simpleDateFormat.format(date1);

            Date reqdate = simpleDateFormat.parse(dates);

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -30);
            Timestamp d2 = new Timestamp(c.getTime().getTime());


            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -60);
            Timestamp d3 = new Timestamp(cal.getTime().getTime());

            if (dates.equals(currentDate))
            {
                return "01";
            }
            else if (d2.before(reqdate))
            {
                return "02";
            }
            else if (d2.after(reqdate) && d3.before(reqdate))
            {
                return "03";
            }
            else if (d3.after(reqdate))
            {
                return "04";
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception :::::: ",e);
        }
        return "01";
    }

    public HashMap getTransactionCount(String mid)
    {
        HashMap tCount = new HashMap();
        PreparedStatement stmt = null;
        Connection conn = null;
        String yCount = "";
        String mCount = "";
        String dCount = "";
        Functions functions = new Functions();
        try
        {
            conn = Database.getConnection();
            String query = "select COUNT(*) as yCount FROM transaction_common WHERE TIMESTAMP >= NOW()-INTERVAL 12 MONTH AND fromid=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, mid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                yCount = rs.getString("yCount");
            }
            tCount.put("year",yCount);//12 month
            rs=null;
            query = "select COUNT(*) as mCount FROM transaction_common WHERE TIMESTAMP >= NOW()-INTERVAL 6 MONTH AND fromid=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, mid);
            rs = stmt.executeQuery();
            if (rs.next())
            {
                mCount = rs.getString("mCount");
            }
            tCount.put("month",mCount);//6 month
            rs=null;
            query = "select COUNT(*) as dCount FROM transaction_common WHERE TIMESTAMP >= NOW()-INTERVAL 1 DAY AND fromid=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, mid);
            rs = stmt.executeQuery();
            if (rs.next())
            {
                dCount = rs.getString("dCount");
            }
            tCount.put("day",dCount);//1 day
        }
        catch (SystemError se)
        {
            log.error("SystemError::::::", se);
            transactionLogger.error("SystemError::::::", se);
        }
        catch (SQLException e)
        {
            log.error("SQLException::::::", e);
            transactionLogger.error("SQLException::::::", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return tCount;
    }

}


