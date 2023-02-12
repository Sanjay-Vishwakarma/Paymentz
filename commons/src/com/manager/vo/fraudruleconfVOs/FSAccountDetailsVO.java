package com.manager.vo.fraudruleconfVOs;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/11/15
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class FSAccountDetailsVO
{
    String fsAccountId;
    String accountName;
    String userName;
    String password;
    String fsid;
    String isTest;
    String creationOn;
    String lastUpdated;

    public String getFsAccountId()
    {
        return fsAccountId;
    }

    public void setFsAccountId(String fsAccountId)
    {
        this.fsAccountId = fsAccountId;
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

    public String getFsid()
    {
        return fsid;
    }

    public void setFsid(String fsid)
    {
        this.fsid = fsid;
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
}
