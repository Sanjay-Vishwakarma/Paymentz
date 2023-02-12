package com.directi.pg.core.swiffpay;

import com.directi.pg.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 7/15/13
 * Time: 2:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class SwiffpayAccount
{
    private static Logger log = new Logger(SwiffpayAccount.class.getName());

    private int id;
    private String mid;
    private String merchantpin;
    private String accountid;



    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getMid()
    {
        return mid;
    }

    public void setMid(String mid)
    {
        this.mid = mid;
    }

    public String getMerchantpin()
    {
        return merchantpin;
    }

    public void setMerchantpin(String merchantpin)
    {
        this.merchantpin = merchantpin;
    }

    public String getAccountid()
    {
        return accountid;
    }

    public void setAccountid(String accountid)
    {
        this.accountid = accountid;
    }

    public SwiffpayAccount(ResultSet rs) throws SQLException
    {

        id = rs.getInt("id");
        mid = rs.getString("mid");
        merchantpin = rs.getString("merchantpin");
        accountid=rs.getString("accountid");
    }
}
