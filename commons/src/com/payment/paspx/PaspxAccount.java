package com.payment.paspx;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 10/30/14
 * Time: 7:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaspxAccount
{
    private static Logger log = new Logger(PaspxAccount.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PaspxAccount.class.getName());

    public Hashtable getUserNamePassword(String accountid)
    {
        Connection conn = null;
        String passwd = "";
        String uName = "";
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
                //dataHash.put("username",rs.getString("username"));
                dataHash.put("mid",rs.getString("merchantid"));
                dataHash.put("password",rs.getString("passwd"));
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

    public static void main(String[] args)
    {
        PaspxAccount a = new PaspxAccount();
        Hashtable data= a.getUserNamePassword("414");

        System.out.println("---"+data.get("password")+"---"+data.get("mid"));
    }
}
