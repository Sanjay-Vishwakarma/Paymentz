package com.manager.vo;

import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by IntelliJ IDEA.
 * User: sandip
 * Date: 4/3/15
 * Time: 03:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class TokenResponseVO
{
    String tokenId;
    String token;
    String status;
    String statusMsg;
    String creationOn;
    int validDays;
    String registrationToken;
    String memberId;
    String partnerId;
    String customerId;
    TokenDetailsVO tokenDetailsVO;

    public String getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
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
    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatusMsg()
    {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg)
    {
        this.statusMsg = statusMsg;
    }

    public String getCreationOn()
    {
        return creationOn;
    }

    public void setCreationOn(String creationOn)
    {
        this.creationOn = creationOn;
    }

    public int getValidDays()
    {
        return validDays;
    }
    public void setValidDays(int validDays)
    {
        this.validDays = validDays;
    }

    public TokenDetailsVO getTokenDetailsVO()
    {
        return tokenDetailsVO;
    }

    public void setTokenDetailsVO(TokenDetailsVO tokenDetailsVO)
    {
        this.tokenDetailsVO = tokenDetailsVO;
    }

    public String getRegistrationToken()
    {
        return registrationToken;
    }

    public void setRegistrationToken(String registrationToken)
    {
        this.registrationToken = registrationToken;
    }
}
