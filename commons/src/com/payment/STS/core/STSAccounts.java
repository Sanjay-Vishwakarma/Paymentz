package com.payment.STS.core;


import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.payment.exceptionHandler.PZDBViolationException;

import java.security.PrivateKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 12/6/14
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class STSAccounts
{

    private static Logger log = new Logger(STSAccounts.class.getName());
    public Hashtable getDataHash(String accountid)
    {
        Hashtable dataHash = new Hashtable();
        String mid = "";
        String password = "";
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query = "select merchantid,passwd from gateway_accounts where accountid=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,accountid);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            {
                dataHash.put("password",rs.getString("passwd"));
                dataHash.put("mid",rs.getString("merchantid"));
            }
        }
        catch (SystemError systemError)
        {
            log.error("Error in fatch values from DB",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException...",e);
        }

        finally {
            Database.closeConnection(con);
        }

        return dataHash;
    }

    public static void main(String[] args)
    {
        Hashtable h = new Hashtable();
        STSAccounts sa = new STSAccounts();

        h = sa.getDataHash("416");
        /*System.out.println("-----h----"+h);
        System.out.println("---mid---"+h.get("mid")+"---pwd---"+h.get("password"));*/

    }
}
