package com.manager.vo;

/**
 * Created by IntelliJ IDEA.
 * User: sandip
 * Date: 5/13/15
 * Time: 01:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class TokenTransactionDetailsVO extends TransactionDetailsVO
{
    String tokenId;
    String token;
    String tokenTransDate;
    String registrationId;

    public String getRegistrationId()
    {
        return registrationId;
    }

    public void setRegistrationId(String registrationId)
    {
        this.registrationId = registrationId;
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

    public String getTokenTransDate()
    {
        return tokenTransDate;
    }

    public void setTokenTransDate(String tokenTransDate)
    {
        this.tokenTransDate = tokenTransDate;
    }
}
