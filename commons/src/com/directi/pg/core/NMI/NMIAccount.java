package com.directi.pg.core.NMI;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 8/6/13
 * Time: 8:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class NMIAccount
{
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    private int id;
    private String accountid;
    private String username;
    private String password;



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

    public NMIAccount(ResultSet rs) throws SQLException
    {

        id = rs.getInt("id");
        accountid = rs.getString("accountid");
        username = rs.getString("username");
        password = rs.getString("password");

    }
}
