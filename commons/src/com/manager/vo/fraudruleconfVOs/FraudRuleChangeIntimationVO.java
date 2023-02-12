package com.manager.vo.fraudruleconfVOs;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 8/8/15
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class FraudRuleChangeIntimationVO
{
    String changeIntimationId;
    String fraudSystemId;
    String fsAccountId;
    String fsSubAccountId;
    String partnerId;
    String memberId;
    String status;
    String creationDate;
    String lastUpdated;

    public String getChangeIntimationId()
    {
        return changeIntimationId;
    }
    public void setChangeIntimationId(String changeIntimationId)
    {
        this.changeIntimationId = changeIntimationId;
    }
    public String getFraudSystemId()
    {
        return fraudSystemId;
    }
    public void setFraudSystemId(String fraudSystemId)
    {
        this.fraudSystemId = fraudSystemId;
    }
    public String getFsAccountId()
    {
        return fsAccountId;
    }
    public void setFsAccountId(String fsAccountId)
    {
        this.fsAccountId = fsAccountId;
    }
    public String getFsSubAccountId()
    {
        return fsSubAccountId;
    }
    public void setFsSubAccountId(String fsSubAccountId)
    {
        this.fsSubAccountId = fsSubAccountId;
    }
    public String getPartnerId()
    {
        return partnerId;
    }
    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }
    public String getMemberId()
    {
        return memberId;
    }
    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }
    public String getStatus()
    {
        return status;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }
    public String getCreationDate()
    {
        return creationDate;
    }
    public void setCreationDate(String creationDate)
    {
        this.creationDate = creationDate;
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