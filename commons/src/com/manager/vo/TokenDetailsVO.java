package com.manager.vo;

/**
 * Created by IntelliJ IDEA.
 * User: sandip
 * Date: 4/6/15
 * Time: 03:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class TokenDetailsVO extends TokenRequestVO
{
    String tokenId;
    String token;
    String creationOn;
    String isActive;
    String isActiveReg;
    int tokenValidDays;
    String isAddrDetailsRequired;
    String bankAccountId;
    String currency;
    String registrationId;
    String status;
    PaginationVO paginationVO;

    public PaginationVO getPaginationVO()
    {
        return paginationVO;
    }

    public void setPaginationVO(PaginationVO paginationVO)
    {
        this.paginationVO = paginationVO;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getRegistrationId()
    {
        return registrationId;
    }

    public void setRegistrationId(String registrationId)
    {
        this.registrationId = registrationId;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getBankAccountId()
    {
        return bankAccountId;
    }

    public void setBankAccountId(String bankAccountId)
    {
        this.bankAccountId = bankAccountId;
    }

    public String getIsActiveReg()
    {
        return isActiveReg;
    }

    public void setIsActiveReg(String isActiveReg)
    {
        this.isActiveReg = isActiveReg;
    }

    public String getTokenId()
    {
        return tokenId;
    }
    public void setTokenId(String tokenId)
    {
        this.tokenId = tokenId;
    }
    public String getToken()
    {
        return token;
    }
    public void setToken(String token)
    {
        this.token = token;
    }

    public String getCreationOn()
    {
        return creationOn;
    }

    public void setCreationOn(String creationOn)
    {
        this.creationOn = creationOn;
    }

    public String getIsActive()
    {
        return isActive;
    }

    public void setIsActive(String isActive)
    {
        this.isActive = isActive;
    }

    public int getTokenValidDays()
    {
        return tokenValidDays;
    }
    public void setTokenValidDays(int tokenValidDays)
    {
        this.tokenValidDays = tokenValidDays;
    }

    public String getIsAddrDetailsRequired()
    {
        return isAddrDetailsRequired;
    }

    public void setIsAddrDetailsRequired(String isAddrDetailsRequired)
    {
        this.isAddrDetailsRequired = isAddrDetailsRequired;
    }
}
