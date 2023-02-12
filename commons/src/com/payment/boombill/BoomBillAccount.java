package com.payment.boombill;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import java.sql.*;
import java.util.Hashtable;
/**
 * Created by Admin on 5/7/2022.
 */
public class BoomBillAccount
{
    private static Logger log = new Logger(BoomBillAccount.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(BoomBillAccount.class.getName());

    private int id;
    private int mid;
    private int account_id;
    private String Apikey;
    private String headertoken;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getMid()
    {
        return mid;
    }

    public void setMid(int mid)
    {
        this.mid = mid;
    }

    public int getAccount_id()
    {
        return account_id;
    }

    public void setAccount_id(int account_id)
    {
        this.account_id = account_id;
    }

    public String getApikey()
    {
        return Apikey;
    }

    public void setApikey(String apikey)
    {
        Apikey = apikey;
    }

    public String getHeadertoken()
    {
        return headertoken;
    }

    public void setHeadertoken(String headertoken)
    {
        this.headertoken = headertoken;
    }

    public Hashtable getValuesFromDb(String accountid)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        Hashtable dataHash = new Hashtable();
        try
        {
            conn = Database.getConnection();
            String query = "select * from gateway_accounts_boombill where accountid=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,accountid);
            ResultSet rs = stmt.executeQuery();
            if(rs.next())
            {
                dataHash.put("accountid",rs.getString("accountid"));
                dataHash.put("3D-Key",rs.getString("3D-Key"));
                dataHash.put("3D-HeaderToken", rs.getString("3D-HeaderToken"));
                dataHash.put("Non3D-Key",rs.getString("Non3D-Key"));
                dataHash.put("Non3D-HeaderToken",rs.getString("Non3D-HeaderToken"));
                //dataHash.put("AppKey",rs.getString("AppKey"));

            }
            transactionLogger.error("BoomBillQuery is =========> "+stmt);


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
