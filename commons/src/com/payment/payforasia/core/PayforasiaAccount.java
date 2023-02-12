package com.payment.payforasia.core;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;

import java.sql.*;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 7/4/14
 * Time: 8:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayforasiaAccount
{
    private static Logger log = new Logger(PayforasiaAccount.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayforasiaAccount.class.getName());

    private int id;
    private int mid;
    private int gatewayno;
    private String signkey;
    private int accountid;
    private String is3Dsec;

    public int getMid()
    {
        return mid;
    }

    public void setMid(int mid)
    {
        this.mid = mid;
    }

    public int getGatewayno()
    {
        return gatewayno;
    }

    public void setGatewayno(int gatewayno)
    {
        this.gatewayno = gatewayno;
    }

    public String getSignkey()
    {
        return signkey;
    }

    public void setSignkey(String signkey)
    {
        this.signkey = signkey;
    }

    public int getAccountid()
    {
        return accountid;
    }

    public void setAccountid(int accountid)
    {
        this.accountid = accountid;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getIs3Dsec()
    {
        return is3Dsec;
    }

    public void setIs3Dsec(String is3Dsec)
    {
        this.is3Dsec = is3Dsec;
    }

    public Hashtable getValuesFromDb(String accountid)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        Hashtable dataHash = new Hashtable();
        try
        {
            conn = Database.getConnection();
            String query = "select accountid,mid,gatewayno,signkey,isThreeDSec from gateway_accounts_payforasia where accountid=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,accountid);
            ResultSet rs = stmt.executeQuery();
            if(rs.next())
            {
                dataHash.put("accountid",rs.getString("accountid"));
                dataHash.put("mid",rs.getString("mid"));
                dataHash.put("gatewayno",rs.getString("gatewayno"));
                dataHash.put("signkey",rs.getString("signkey"));
                dataHash.put("isThreeDSec",rs.getString("isThreeDSec"));
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
        return dataHash;
    }

    /*public static void main(String[] args)
    {

        Hashtable resHash = new Hashtable();
        resHash = getValuesFromDb("404");

        System.out.println("resHash---"+resHash.get("mid")+"\n"+resHash.get("gatewayno")+"\n"+resHash.get("signkey"));
    }*/


}
