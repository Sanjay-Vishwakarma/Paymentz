package com.payment.alliedwalled.core;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.Enum.PZProcessType;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created by Admin on 4/14/18.
 */
public class AlliedWalletUtils
{
    private static Logger log = new Logger(AlliedWalletUtils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(AlliedWalletUtils.class.getName());

    public static String doPostHTTPSURLConnectionClient(String strURL, String authorizationToken,String req) throws PZTechnicalViolationException
    {
        //System.out.println("strURL:::::" + strURL);
        //System.out.println("authorizationToken:::::" + authorizationToken);
        String result = "";
        try
        {
            HttpClient httpClient = new HttpClient();
            PostMethod post = new PostMethod(strURL);
            post.addRequestHeader("Authorization","Bearer "+authorizationToken+"");
            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException In doPostHTTPSURLConnectionClient AlliedWalletUtils ",he);
        }
        catch (IOException io){
            transactionLogger.error("IOException In doPostHTTPSURLConnectionClient AlliedWalletUtils ",io);
        }
        return result;
    }

    public static String GETHTTPSURLConnectionClient(String strURL, String authorizationToken) throws PZTechnicalViolationException
    {
        //System.out.println("strURL:::::" + strURL);
       // System.out.println("authorizationToken:::::" + authorizationToken);
        String result = "";
        try
        {
            HttpClient httpClient = new HttpClient();
            GetMethod get = new GetMethod(strURL);
            get.addRequestHeader("Authorization","Bearer "+authorizationToken+"");
            get.addRequestHeader("Content-Type", "application/json");
            httpClient.executeMethod(get);
            String response = new String(get.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException In GETHTTPSURLConnectionClient AlliedWalletUtils ",he);
        }
        catch (IOException io){
            transactionLogger.error("IOException In GETHTTPSURLConnectionClient AlliedWalletUtils ", io);
        }
        return result;
    }

    public static String getToken(String accountid)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        String Token = null;
        try
        {
            conn = Database.getConnection();
            String query = "select token from gateway_accounts_allied_wallet where accountid=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,accountid);
            ResultSet rs = stmt.executeQuery();
            if(rs.next())
            {
               Token=rs.getString("token");
            }
        }
        catch (SystemError se)
        {
            log.error("SystemError::::::",se);
            transactionLogger.error("SystemError::::::",se);
        }
        catch (SQLException e)
        {
            log.error("SQLException::::::",e);
            transactionLogger.error("SQLException::::::",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return Token;
    }

    public static String transType(String transaType){
        switch (transaType){
            case "Authorize":
                return PZProcessType.AUTH.toString();
            case "Sale":
                return PZProcessType.SALE.toString();
            case "Capture":
                return PZProcessType.CAPTURE.toString();
            case "Void":
                return PZProcessType.CANCEL.toString();
            case "Refund":
                return PZProcessType.REFUND.toString();
            default:
                return PZProcessType.INQUIRY.toString();
        }
    }
}
