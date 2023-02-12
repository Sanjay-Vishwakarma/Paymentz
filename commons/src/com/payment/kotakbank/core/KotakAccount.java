package com.payment.kotakbank.core;

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
 * Created by Nikita on 3/21/2017.
 */
public class KotakAccount
{
    private static Logger log = new Logger(KotakAccount.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(KotakAccount.class.getName());

    private int id;
    private String merchantname;
    private int terminalid;
    private String passcode;
    private String securesecret;
    private String enckey;
    private int accountid;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getMerchantname()
    {
        return merchantname;
    }

    public void setMerchantname(String merchantname)
    {
        this.merchantname = merchantname;
    }

    public int getTerminalid()
    {
        return terminalid;
    }

    public void setTerminalid(int terminalid)
    {
        this.terminalid = terminalid;
    }

    public String getPasscode()
    {
        return passcode;
    }

    public void setPasscode(String passcode)
    {
        this.passcode = passcode;
    }

    public String getSecuresecret()
    {
        return securesecret;
    }

    public void setSecuresecret(String securesecret)
    {
        this.securesecret = securesecret;
    }

    public String getEnckey()
    {
        return enckey;
    }

    public void setEnckey(String enckey)
    {
        this.enckey = enckey;
    }

    public int getAccountid()
    {
        return accountid;
    }

    public void setAccountid(int accountid)
    {
        this.accountid = accountid;
    }

    public Hashtable getAccountValuesFromDb(String accountid)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        Hashtable dataHash = new Hashtable();
        try
        {
            conn = Database.getConnection();
            String query = "select accountid,merchantname,terminalid,passcode,securesecret,enckey,mcc from gateway_accounts_kotak where accountid=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,accountid);
            ResultSet rs = stmt.executeQuery();
            if(rs.next())
            {
                dataHash.put("accountid",rs.getString("accountid"));
                dataHash.put("merchantname",rs.getString("merchantname"));
                dataHash.put("terminalid",rs.getString("terminalid"));
                dataHash.put("passcode",rs.getString("passcode"));
                dataHash.put("securesecret",rs.getString("securesecret"));
                dataHash.put("enckey",rs.getString("enckey"));
                dataHash.put("mcc",rs.getString("mcc"));
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
}
