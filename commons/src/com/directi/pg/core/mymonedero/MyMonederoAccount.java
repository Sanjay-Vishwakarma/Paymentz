package com.directi.pg.core.mymonedero;

import com.directi.pg.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Mar 9, 2013
 * Time: 9:45:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class MyMonederoAccount
{

    private static Logger log = new Logger(MyMonederoAccount.class.getName());
    private int id;
    private int accountId;
    private String merchantid;
    private String merchantpass;
    private String accesskey;
    private String submerchantid;
    private String submerchantpass;
    private boolean live;
    private String endpoint;


      public MyMonederoAccount(ResultSet rs) throws SQLException
    {

        id = rs.getInt("id");
        accountId = rs.getInt("accountid");
        merchantid = rs.getString("merchantid");
        merchantpass = rs.getString("merchantpass");
        accesskey = rs.getString("accesskey");
        submerchantid = rs.getString("submerchantid");
        submerchantpass = rs.getString("submerchantpass");
        live = rs.getBoolean("isLive");
        endpoint = rs.getString("endpoint") ;

    }

    public static Logger getLog()
    {
        return log;
    }

    public static void setLog(Logger log)
    {
        MyMonederoAccount.log = log;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getAccountId()
    {
        return accountId;
    }

    public void setAccountId(int accountId)
    {
        this.accountId = accountId;
    }

    public String getMerchantid()
    {
        return merchantid;
    }

    public void setMerchantid(String merchantid)
    {
        this.merchantid = merchantid;
    }

    public String getMerchantpass()
    {
        return merchantpass;
    }

    public void setMerchantpass(String merchantpass)
    {
        this.merchantpass = merchantpass;
    }

    public String getAccesskey()
    {
        return accesskey;
    }

    public void setAccesskey(String accesskey)
    {
        this.accesskey = accesskey;
    }

    public String getSubmerchantid()
    {
        return submerchantid;
    }

    public void setSubmerchantid(String submerchantid)
    {
        this.submerchantid = submerchantid;
    }

    public String getSubmerchantpass()
    {
        return submerchantpass;
    }

    public void setSubmerchantpass(String submerchantpass)
    {
        this.submerchantpass = submerchantpass;
    }

    public boolean isLive()
    {
        return live;
    }

    public void setLive(boolean live)
    {
        this.live = live;
    }

    public String getEndpoint()
    {
        return endpoint;
    }

    public void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
    }


}
