package com.manager.vo.fraudruleconfVOs;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 8/13/15
 * Time: 7:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class FraudAccountDetailsVO
{
    String fraudSystemId;
    String fraudSystemMerchantId;
    String password;
    String subMerchantId;
    String userName;
    String userNumber;
    String isTest;
    String isOnlineFraudCheck;
    String submerchantUsername;
    String submerchantPassword;

    public String getFraudSystemMerchantId()
    {
        return fraudSystemMerchantId;
    }
    public void setFraudSystemMerchantId(String fraudSystemMerchantId)
    {
        this.fraudSystemMerchantId = fraudSystemMerchantId;
    }
    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
    public String getSubMerchantId()
    {
        return subMerchantId;
    }
    public void setSubMerchantId(String subMerchantId)
    {
        this.subMerchantId = subMerchantId;
    }
    public String getUserName()
    {
        return userName;
    }
    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    public String getUserNumber()
    {
        return userNumber;
    }
    public void setUserNumber(String userNumber)
    {
        this.userNumber = userNumber;
    }
    public String getIsTest()
    {
        return isTest;
    }
    public void setIsTest(String isTest)
    {
        this.isTest = isTest;
    }

    public String getFraudSystemId()
    {
        return fraudSystemId;
    }

    public void setFraudSystemId(String fraudSystemId)
    {
        this.fraudSystemId = fraudSystemId;
    }

    public String getIsOnlineFraudCheck()
    {
        return isOnlineFraudCheck;
    }

    public void setIsOnlineFraudCheck(String isOnlineFraudCheck)
    {
        this.isOnlineFraudCheck = isOnlineFraudCheck;
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
}
