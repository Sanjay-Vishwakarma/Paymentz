package com.payment.FrickBank.core;

import com.directi.pg.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/2/13
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class FrickBankAccount
{
    private static Logger log = new Logger(FrickBankAccount.class.getName());

    private int id;
    private String accountid;
    private String login;
    private String pwd;
    private String sender;
    private String channel;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getAccountid()
    {
        return accountid;
    }

    public void setAccountid(String accountid)
    {
        this.accountid = accountid;
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

    public FrickBankAccount(ResultSet rs) throws SQLException
    {
        id = rs.getInt("id");
        login = rs.getString("login");
        pwd = rs.getString("pwd");
        accountid=rs.getString("accountid");
        sender = rs.getString("sender");
        channel=rs.getString("channel");
    }
}
