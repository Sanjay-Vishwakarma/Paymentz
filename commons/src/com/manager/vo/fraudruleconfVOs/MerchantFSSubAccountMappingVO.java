package com.manager.vo.fraudruleconfVOs;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/11/15
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantFSSubAccountMappingVO
{
    String merchantFraudServiceId;
    String memberId;
    String fsSubAccountId;
    String isActive;
    String creationOn;
    String lastUpdated;

    public String getMerchantFraudServiceId()
    {
        return merchantFraudServiceId;
    }

    public void setMerchantFraudServiceId(String merchantFraudServiceId)
    {
        this.merchantFraudServiceId = merchantFraudServiceId;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public String getFsSubAccountId()
    {
        return fsSubAccountId;
    }

    public void setFsSubAccountId(String fsSubAccountId)
    {
        this.fsSubAccountId = fsSubAccountId;
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
