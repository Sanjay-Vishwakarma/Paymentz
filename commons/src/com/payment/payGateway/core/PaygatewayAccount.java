package com.payment.payGateway.core;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh Dani
 * Date: 12/23/13
 * Time: 4:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaygatewayAccount
{
    private int id;
    private String accountid;
    private String username;
    private String password;
    private String passphrase;

    public String getAccountid()
    {
        return accountid;
    }

    public void setAccountid(String accountid)
    {
        this.accountid = accountid;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassphrase()
    {
        return passphrase;
    }

    public void setPassphrase(String passphrase)
    {
        this.passphrase = passphrase;
    }

    public int getId()
    {

        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public PaygatewayAccount(ResultSet rs) throws SQLException
    {

        id = rs.getInt("id");
        accountid = rs.getString("accountid");
        username = rs.getString("username");
        password = rs.getString("password");
        passphrase=rs.getString("passphrase");

    }
}
