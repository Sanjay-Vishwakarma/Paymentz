package com.manager.vo.fraudruleconfVOs;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/11/15
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class FSSubAccountDetailsVO
{
    String fsSubAccountId;
    String fsAccountId;
    String subAccountName;
    String subUserName;
    String subPWD;
    String isActive;
    String creationOn;
    String lastUpdated;

    public String getFsSubAccountId()
    {
        return fsSubAccountId;
    }

    public void setFsSubAccountId(String fsSubAccountId)
    {
        this.fsSubAccountId = fsSubAccountId;
    }

    public String getFsAccountId()
    {
        return fsAccountId;
    }

    public void setFsAccountId(String fsAccountId)
    {
        this.fsAccountId = fsAccountId;
    }

    public String getSubAccountName()
    {
        return subAccountName;
    }

    public void setSubAccountName(String subAccountName)
    {
        this.subAccountName = subAccountName;
    }

    public String getSubUserName()
    {
        return subUserName;
    }

    public void setSubUserName(String subUserName)
    {
        this.subUserName = subUserName;
    }

    public String getSubPWD()
    {
        return subPWD;
    }

    public void setSubPWD(String subPWD)
    {
        this.subPWD = subPWD;
    }

    public String getIsActive()
    {
        return isActive;
    }

    public void setIsActive(String isActive)
    {
        this.isActive = isActive;
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
