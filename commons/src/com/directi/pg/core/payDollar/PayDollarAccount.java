package com.directi.pg.core.payDollar;

import com.directi.pg.Logger;
import com.directi.pg.core.ugspay.UGSPayAccount;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Jun 15, 2013
 * Time: 7:28:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayDollarAccount
{

    private static Logger log = new Logger(PayDollarAccount.class.getName());
    private int id;
    private int accountId;
    private String merchantid;
    private String loginId;
    private String password;
    private String authUrl;
    private String apiUrl;


     public PayDollarAccount(ResultSet rs) throws SQLException
    {

        id = rs.getInt("id");
        accountId = rs.getInt("accountid");
        merchantid = rs.getString("merchantid");
        loginId = rs.getString("loginId");
        password = rs.getString("password");
        authUrl = rs.getString("authUrl");
        apiUrl = rs.getString("apiUrl");


     }

    public String getMerchantid()
    {
        return merchantid;
    }

    public void setMerchantid(String merchantid)
    {
        this.merchantid = merchantid;
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

    public String getLoginId()
    {
        return loginId;
    }

    public void setLoginId(String loginId)
    {
        this.loginId = loginId;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getAuthUrl()
    {
        return authUrl;
    }

    public void setAuthUrl(String authUrl)
    {
        this.authUrl = authUrl;
    }

    public String getApiUrl()
    {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl)
    {
        this.apiUrl = apiUrl;
    }

}
