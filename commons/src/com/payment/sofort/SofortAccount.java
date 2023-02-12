package com.payment.sofort;

import com.directi.pg.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 15/1/15
 * Time: 9:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class SofortAccount
{
    private static Logger log = new Logger(SofortAccount.class.getName());
    private int accountId;
    private int customerId ;
    private int projectId ;
    private String apiKey ;
    
    private String projectPass;
    
    private String notificationPass;

    private boolean live;

    private String accountHolderName;
    private String bankName;
    private String iban;
    private String bic;



    public SofortAccount(ResultSet rs) throws SQLException
    {

        customerId = rs.getInt("customerid");
        accountId = rs.getInt("accountid");
        projectId = rs.getInt("projectid");
        apiKey = rs.getString("apikey");
        live = rs.getBoolean("isLive");
        projectPass=rs.getString("projectPass");
        notificationPass=rs.getString("notificationPass");
        accountHolderName=rs.getString("accountHolderName");
        bankName = rs.getString("bankName");
        iban = rs.getString("iban");
        bic = rs.getString("bic");


    }

    public int getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(int customerId)
    {
        this.customerId = customerId;
    }

    public int getProjectId()
    {
        return projectId;
    }

    public void setProjectId(int projectId)
    {
        this.projectId = projectId;
    }

    public String getApiKey()
    {
        return apiKey;
    }

    public void setApiKey(String apiKey)
    {
        this.apiKey = apiKey;
    }

    public static Logger getLog()
    {
        return log;
    }

    public static void setLog(Logger log)
    {
        SofortAccount.log = log;
    }

    public int getAccountId()
    {
        return accountId;
    }

    public void setAccountId(int accountId)
    {
        this.accountId = accountId;
    }

    public boolean isLive()
    {
        return live;
    }

    public void setLive(boolean live)
    {
        this.live = live;
    }

    public String getProjectPass()
    {
        return projectPass;
    }

    public void setProjectPass(String projectPass)
    {
        this.projectPass = projectPass;
    }

    public String getNotificationPass()
    {
        return notificationPass;
    }

    public void setNotificationPass(String notificationPass)
    {
        this.notificationPass = notificationPass;
    }

    public String getAccountHolderName()
    {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName)
    {
        this.accountHolderName = accountHolderName;
    }

    public String getBankName()
    {
        return bankName;
    }

    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

    public String getIban()
    {
        return iban;
    }

    public void setIban(String iban)
    {
        this.iban = iban;
    }

    public String getBic()
    {
        return bic;
    }

    public void setBic(String bic)
    {
        this.bic = bic;
    }
}
