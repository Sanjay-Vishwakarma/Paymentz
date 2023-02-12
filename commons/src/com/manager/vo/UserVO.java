package com.manager.vo;

/**
 * Created by Jinesh on 1/23/2016.
 */
public class UserVO
{
    String userid;
    String userLogin;
    String emailAddress;
    String userMerchantId;
    String userPaymodeId;
    String userCardTypeId;
    String userTerminalId;
    String userAccountId;

    public String getUserMerchantId()
    {
        return userMerchantId;
    }

    public void setUserMerchantId(String userMerchantId)
    {
        this.userMerchantId = userMerchantId;
    }

    public String getUserPaymodeId()
    {
        return userPaymodeId;
    }

    public void setUserPaymodeId(String userPaymodeId)
    {
        this.userPaymodeId = userPaymodeId;
    }

    public String getUserCardTypeId()
    {
        return userCardTypeId;
    }

    public void setUserCardTypeId(String userCardTypeId)
    {
        this.userCardTypeId = userCardTypeId;
    }

    public String getUserTerminalId()
    {
        return userTerminalId;
    }

    public void setUserTerminalId(String userTerminalId)
    {
        this.userTerminalId = userTerminalId;
    }

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    public String getUserLogin()
    {
        return userLogin;
    }

    public void setUserLogin(String userLogin)
    {
        this.userLogin = userLogin;
    }

    public String getEmailAddress()
    {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    public String getUserAccountId()
    {
        return userAccountId;
    }

    public void setUserAccountId(String userAccountId)
    {
        this.userAccountId = userAccountId;
    }
}
