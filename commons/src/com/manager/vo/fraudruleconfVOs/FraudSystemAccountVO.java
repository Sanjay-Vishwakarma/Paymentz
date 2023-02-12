package com.manager.vo.fraudruleconfVOs;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/11/15
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class FraudSystemAccountVO
{
    String fraudSystemAccountId;
    String accountName;
    String userName;
    String password;
    String fraudSystemId;
    String isTest;
    String creationOn;
    String lastUpdated;
    String fraudSystemMerchantId;
    String contactName;
    String contactEmail;

    public String getContactName()
    {
        return contactName;
    }

    public String getContactEmail()
    {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail)
    {
        this.contactEmail = contactEmail;
    }

    public void setContactName(String contactName)
    {
        this.contactName = contactName;
    }

    public String getFraudSystemAccountId()
    {
        return fraudSystemAccountId;
    }

    public void setFraudSystemAccountId(String fraudSystemAccountId)
    {
        this.fraudSystemAccountId = fraudSystemAccountId;
    }

    public String getAccountName()
    {
        return accountName;
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getFraudSystemId()
    {
        return fraudSystemId;
    }

    public void setFraudSystemId(String fraudSystemId)
    {
        this.fraudSystemId = fraudSystemId;
    }

    public String getIsTest()
    {
        return isTest;
    }

    public void setIsTest(String isTest)
    {
        this.isTest = isTest;
    }

    public String getCreationOn()
    {
        return creationOn;
    }

    public void setCreationOn(String creationOn)
    {
        this.creationOn = creationOn;
    }

    public String getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }

    public String getFraudSystemMerchantId()
    {
        return fraudSystemMerchantId;
    }

    public FraudSystemAccountVO()
    {

    }
    public FraudSystemAccountVO(ResultSet rs)throws SQLException
    {
        fraudSystemId=rs.getString("fsid");
        fraudSystemAccountId=rs.getString("fsaccountid");
        fraudSystemMerchantId=rs.getString("accountname");
        accountName=rs.getString("accountname");
        userName=rs.getString("username");
        password=rs.getString("password");
        isTest=rs.getString("isTest");
    }

}
