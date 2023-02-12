package com.directi.pg.core.ugspay;

import com.directi.pg.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Mar 11, 2013
 * Time: 8:43:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class UGSPayAccount
{

    private static Logger log = new Logger(UGSPayAccount.class.getName());
    private int id;
    private int accountId;
    private int websiteid;
    private String password;
    private String websiteurl;
    private boolean live;


     public UGSPayAccount(ResultSet rs) throws SQLException
    {

        id = rs.getInt("id");
        accountId = rs.getInt("accountid");
        websiteid = rs.getInt("websiteid");
        password = rs.getString("password");
        websiteurl = rs.getString("websiteurl");
        live = rs.getBoolean("isLive");

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

    public int getWebsiteid()
    {
        return websiteid;
    }

    public void setWebsiteid(int websiteid)
    {
        this.websiteid = websiteid;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getWebsiteurl()
    {
        return websiteurl;
    }

    public void setWebsiteurl(String websiteurl)
    {
        this.websiteurl = websiteurl;
    }

    public boolean isLive()
    {
        return live;
    }

    public void setLive(boolean live)
    {
        this.live = live;
    }
}
