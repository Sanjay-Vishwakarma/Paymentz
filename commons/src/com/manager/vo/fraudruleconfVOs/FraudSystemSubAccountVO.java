package com.manager.vo.fraudruleconfVOs;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/11/15
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class FraudSystemSubAccountVO
{
    String fraudSystemSubAccountId;
    String fraudSystemAccountId;
    String subAccountName;
    String userName;
    String password;
    String isActive;
    String creationOn;
    String lastUpdated;
    String submerchantUsername;
    String submerchantPassword;
    String accountruleid;
    String ruleid;

    MerchantFraudAccountVO merchantFraudAccountVO;

    public String getFraudSystemSubAccountId()
    {
        return fraudSystemSubAccountId;
    }

    public void setFraudSystemSubAccountId(String fraudSystemSubAccountId)
    {
        this.fraudSystemSubAccountId = fraudSystemSubAccountId;
    }

    public String getFraudSystemAccountId()
    {
        return fraudSystemAccountId;
    }

    public void setFraudSystemAccountId(String fraudSystemAccountId)
    {
        this.fraudSystemAccountId = fraudSystemAccountId;
    }

    public String getSubAccountName()
    {
        return subAccountName;
    }

    public void setSubAccountName(String subAccountName)
    {
        this.subAccountName = subAccountName;
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

    public MerchantFraudAccountVO getMerchantFraudAccountVO()
    {
        return merchantFraudAccountVO;
    }

    public void setMerchantFraudAccountVO(MerchantFraudAccountVO merchantFraudAccountVO)
    {
        this.merchantFraudAccountVO = merchantFraudAccountVO;
    }

    public String getSubmerchantUsername()
    {
        return submerchantUsername;
    }

    public void setSubmerchantUsername(String submerchantUsername)
    {
        this.submerchantUsername = submerchantUsername;
    }

    public String getSubmerchantPassword()
    {
        return submerchantPassword;
    }

    public void setSubmerchantPassword(String submerchantPassword)
    {
        this.submerchantPassword = submerchantPassword;
    }

    public String getAccountruleid()
    {
        return accountruleid;
    }

    public void setAccountruleid(String accountruleid)
    {
        this.accountruleid = accountruleid;
    }

    public String getRuleid()
    {
        return ruleid;
    }

    public void setRuleid(String ruleid)
    {
        this.ruleid = ruleid;
    }
}
