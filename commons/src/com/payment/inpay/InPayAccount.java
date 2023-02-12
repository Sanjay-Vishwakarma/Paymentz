package com.payment.inpay;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 8/21/14
 * Time: 6:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class InPayAccount
{
    private static Logger log = new Logger(InPayAccount.class.getName());
    public Hashtable getMidAndSecretKey(String accountid)
    {
        Connection conn = null;
        Statement stmt = null;
        Hashtable dataHash = new Hashtable();
        try
        {
            conn = Database.getConnection();
            String query = "select * from gateway_accounts_inpay";
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if(rs.next())
            {
                dataHash.put("accountid",rs.getString("accountid"));
                dataHash.put("mid",rs.getString("mid"));
                dataHash.put("secretkey",rs.getString("secretkey"));
            }

        }
        catch (SystemError se)
        {
            log.error("Error in fatch values from DB",se);
            //PZExceptionHandler.raiseDBViolationException("InPayAccount.java","getMidAndSecretKey()",null,"Common/payment","SQL Exception Thrown::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            log.error("SQLException...",e);
            //PZExceptionHandler.raiseDBViolationException("InPayAccount.java","getMidAndSecretKey()",null,"Common/payment","SQL Exception Thrown::::", PZDBExceptionEnum.SQL_EXCEPTION,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return dataHash;
    }

    /*public static void main(String[] args)
    {
        Hashtable data = new Hashtable();
        data = getMidAndSecretKey("406");
        System.out.println("Mid "+data.get("mid") +"\n"+"Accountid "+data.get("accountid")+"\n"+"secret key "+data.get("secretkey"));
    }*/
}
