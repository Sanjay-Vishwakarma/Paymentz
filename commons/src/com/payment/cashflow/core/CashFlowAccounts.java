package com.payment.cashflow.core;


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
 * Date: 10/2/14
 * Time: 6:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class CashFlowAccounts
{
    private static Logger log = new Logger(CashFlowAccounts.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(CashFlowAccounts.class.getName());
    public Hashtable getMidPassword(String accountid)
    {
        Connection conn = null;
        String passwd = "";
        String mId = "";
        Hashtable dataHash = new Hashtable();
        try
        {
            conn = Database.getConnection();
            String query = "select merchantid,passwd,istest from gateway_accounts where accountid=?";
            PreparedStatement pstmt= conn.prepareStatement(query);
            pstmt.setString(1,accountid);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            {
                dataHash.put("password",rs.getString("passwd"));
                dataHash.put("mid",rs.getString("merchantid"));
                dataHash.put("istest",rs.getString("istest"));
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
        CashFlowAccounts a = new CashFlowAccounts();
        Hashtable data= a.getMidPassword("410");

        System.out.println("---"+data.get("password")+"---"+data.get("mid")+"---"+data.get("istest"));
    }    
}
