package com.manager.vo.fraudruleconfVOs;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/11/15


 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */

public class MerchantFraudAccountVO
{
    String merchantFraudAccountId;
    String memberId;
    String fsSubAccountId;
    String isActive;
    String creationOn;
    String lastUpdated;
    String isVisible;
    String subAccountName;
    String isOnlineFraudCheck;
    String isAPIUser;
    String partnerid;
    String fsaccountid;
    String pisActive;
    String submerchantUsername;
    String submerchantPassword;


    public String getMerchantFraudAccountId()
    {
        return merchantFraudAccountId;
    }
    public void setMerchantFraudAccountId(String merchantFraudAccountId)
    {
        this.merchantFraudAccountId = merchantFraudAccountId;
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
    public String getIsVisible() {return isVisible;}
    public void setIsVisible(String isVisible) {this.isVisible = isVisible;}

    public String getSubAccountName()
    {
        return subAccountName;
    }

    public void setSubAccountName(String subAccountName)
    {
        this.subAccountName = subAccountName;
    }

    public String getIsAPIUser()
    {
        return isAPIUser;
    }

    public void setIsAPIUser(String isAPIUser)
    {
        this.isAPIUser = isAPIUser;
    }

    public String getIsOnlineFraudCheck()
    {
        return isOnlineFraudCheck;
    }

    public void setIsOnlineFraudCheck(String isOnlineFraudCheck)
    {
        this.isOnlineFraudCheck = isOnlineFraudCheck;
    }

    public String getPartnerid()
    {
        return partnerid;
    }

    public void setPartnerid(String partnerid)
    {
        this.partnerid = partnerid;
    }

    public String getFsaccountid()
    {
        return fsaccountid;
    }

    public void setFsaccountid(String fsaccountid)
    {
        this.fsaccountid = fsaccountid;
    }

    public String getPisActive()
    {
        return pisActive;
    }

    public void setPisActive(String pisActive)
    {
        this.pisActive = pisActive;
    }

    public String getSubmerchantUsername() {return submerchantUsername;}

    public void setSubmerchantUsername(String submerchantUsername) {this.submerchantUsername = submerchantUsername;}

    public String getSubmerchantPassword()
    {
        return submerchantPassword;
    }

    public void setSubmerchantPassword(String submerchantPassword)
    {
        this.submerchantPassword = submerchantPassword;
    }
}
