package com.payment.safechargeV2;

import com.directi.pg.Database;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Admin on 9/7/2020.
 */
public class SafeChargeV2Utils
{
    private static TransactionLogger transactionLogger=new TransactionLogger(SafeChargeV2Utils.class.getName());
    public static String generateChecksum(String checksumStr)
    {
        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(checksumStr.toString().getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1)
                {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException--->",e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException--->", e);
        }
        return hexString.toString();
    }
    public static String doPostHTTPSURLConnectionClient(String strURL,String req) throws PZTechnicalViolationException
    {
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {

            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException he--->",he);
            PZExceptionHandler.raiseTechnicalViolationException(SafeChargeV2Utils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io){
            transactionLogger.error("IOException io--->", io);
            PZExceptionHandler.raiseTechnicalViolationException(SafeChargeV2Utils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
    public static boolean insertSessionToken(String trackingId,String sessionToken,String type)
    {
        Connection con=null;
        PreparedStatement ps=null;
        boolean isRecordInserted=false;
        try
        {
            con= Database.getConnection();
            String query="insert into transaction_safechargev2_details (trackingid,sessionToken,type) values (?,?,?)";
            ps=con.prepareStatement(query);
            ps.setString(1,trackingId);
            ps.setString(2,sessionToken);
            ps.setString(3,type);
            transactionLogger.error("insertSessionToken ---->"+ps);
            int i=ps.executeUpdate();
            if(i>0)
                isRecordInserted=true;
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError ---->",systemError);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException ---->", e);
        }finally
        {
            Database.closeConnection(con);
        }
        return isRecordInserted;
    }
    public static boolean updateDetailTable(String trackingId,String orderid,String authCode)
    {
        Connection con=null;
        PreparedStatement ps=null;
        boolean isRecordInserted=false;
        try
        {
            con= Database.getConnection();
            String query="update transaction_safechargev2_details set orderId=?,authCode=? where trackingId=?";
            ps=con.prepareStatement(query);
            ps.setString(1,orderid);
            ps.setString(2,authCode);
            ps.setString(3,trackingId);
            transactionLogger.error("updateDetailTable ---->"+ps);
            int i=ps.executeUpdate();
            if(i>0)
                isRecordInserted=true;
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("updateDetailTable SystemError ---->",systemError);
        }
        catch (SQLException e)
        {
            transactionLogger.error("updateDetailTable SQLException ---->", e);
        }finally
        {
            Database.closeConnection(con);
        }
        return isRecordInserted;
    }
    public static HashMap<String,String> getSessionToken(String trackingId)
    {
        Connection con=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        HashMap<String,String> hashMap=new HashMap<>();
        try
        {
            con= Database.getConnection();
            String query="select sessionToken,orderId from  transaction_safechargev2_details where trackingId=?";
            ps=con.prepareStatement(query);
            ps.setString(1,trackingId);
            transactionLogger.error("getSessionToken--->"+ps);
            rs=ps.executeQuery();
            if(rs.next())
            {
                hashMap.put("sessionToken",rs.getString("sessionToken"));
                hashMap.put("orderId",rs.getString("orderId"));
            }
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("updateDetailTable SystemError ---->",systemError);
        }
        catch (SQLException e)
        {
            transactionLogger.error("updateDetailTable SQLException ---->", e);
        }finally
        {
            Database.closeConnection(con);
        }
        return hashMap;
    }
}
