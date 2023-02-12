package com.payment.dvg_payment;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;

import java.sql.*;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 9/17/14
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class DVGAccount
{
    private static Logger log = new Logger(DVGAccount.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(DVGAccount.class.getName());
    public Hashtable getMidPassword(String accountid)
    {
        Connection conn = null;
        String passwd = "";
        String mId = "";
        Hashtable dataHash = new Hashtable();
        try
        {
            conn = Database.getConnection();
            String query = "select merchantid,passwd from gateway_accounts where accountid=?";
            PreparedStatement pstmt= conn.prepareStatement(query);
            pstmt.setString(1,accountid);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                dataHash.put("password",rs.getString("passwd"));
                dataHash.put("mid",rs.getString("merchantid"));

            }
        }
        catch (SystemError systemError)
        {
            log.error("Error in fatch values from DB",systemError);
            transactionLogger.error("Error in fatch values from DB",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException...",e);
            transactionLogger.error("SQLException...",e);
        }
        finally {
            Database.closeConnection(conn);
        }
        return dataHash;
    }

    public Hashtable refundData(String trackingId)
    {
        Hashtable refundHash = new Hashtable();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT ipaddress,name,ccnum,expdate,firstname,lastname,street,city,state,country,zip,emailaddr,telno FROM transaction_common WHERE trackingid=?";
            PreparedStatement pstmt= conn.prepareStatement(query);
            pstmt.setString(1,trackingId);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                refundHash.put("ipaddress",rs.getString("ipaddress"));
                refundHash.put("name",rs.getString("name"));
                refundHash.put("ccnum",rs.getString("ccnum"));
                refundHash.put("expdate",rs.getString("expdate"));
                refundHash.put("firstname",rs.getString("firstname"));
                refundHash.put("lastname",rs.getString("lastname"));
                refundHash.put("street",rs.getString("street"));
                refundHash.put("city",rs.getString("city"));
                refundHash.put("state",rs.getString("state"));
                refundHash.put("country",rs.getString("country"));
                refundHash.put("zip",rs.getString("zip"));
                refundHash.put("emailaddr",rs.getString("emailaddr"));
                refundHash.put("telno",rs.getString("telno"));
            }
        }
        catch (SystemError systemError)
        {
            log.error("Error in fatch values from DB",systemError);
            transactionLogger.error("Error in fatch values from DB",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException...",e);
            transactionLogger.error("SQLException...",e);
        }
        finally {
            Database.closeConnection(conn);
        }

        return refundHash;
    }

    /*public static void main(String[] args)
    {
        DVGAccount a = new DVGAccount();
        Hashtable data= a.refundData("10657");

        System.out.println("---"+data);
    }*/
}
