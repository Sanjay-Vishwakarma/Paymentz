package com.directi.pg.core.payworld;

import com.directi.pg.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/24/13
 * Time: 2:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayWorldAccount
{
    private static Logger log = new Logger(PayWorldAccount.class.getName());

    private int id;
    private int merchantaccno;
    private String guid;
    private String password;
    private String RS;
    private String accountid;
    public String getAccountid()
    {
        return accountid;
    }

    public void setAccountid(String accountid)
    {
        this.accountid = accountid;
    }


    public String getRS()
    {
        return RS;
    }

    public void setRS(String rs)
    {
        this.RS = rs;
    }


    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getMerchantaccno()
    {
        return merchantaccno;
    }

    public void setMerchantaccno(int merchantaccno)
    {
        this.merchantaccno = merchantaccno;
    }

    public String getGuid()
    {
        return guid;
    }

    public void setGuid(String guid)
    {
        this.guid = guid;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }




    public PayWorldAccount(ResultSet rs) throws SQLException
    {

        id = rs.getInt("id");
        merchantaccno = rs.getInt("merchantaccno");
        guid = rs.getString("guid");
        password = rs.getString("password");
        RS=rs.getString("str");
        accountid=rs.getString("accountid");
    }
}
