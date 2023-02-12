package com.directi.pg.core.paylineVoucher;

import com.directi.pg.Logger;
import com.directi.pg.core.paymentgateway.PayLineVoucherGateway;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Oct 8, 2012
 * Time: 9:33:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayVoucherAccount
{
    private static Logger log = new Logger(PayVoucherAccount.class.getName());
    private int id;
    private int accountId;
    private String version;
    private String token;
    private String sender;
    private String channel;
    private String login;
    private String pwd;
    private String brand;
    private boolean live;

     public PayVoucherAccount(ResultSet rs) throws SQLException
    {

        id = rs.getInt("id");
        accountId = rs.getInt("accountid");
        version = rs.getString("version");
        token = rs.getString("token");
        sender = rs.getString("sender");
        channel = rs.getString("channel");
        login = rs.getString("login");
        pwd = rs.getString("password");
        brand = rs.getString("brand");
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

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getSender()
    {
        return sender;
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getPwd()
    {
        return pwd;
    }

    public void setPwd(String pwd)
    {
        this.pwd = pwd;
    }

    public String getBrand()
    {
        return brand;
    }

    public void setBrand(String brand)
    {
        this.brand = brand;
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
