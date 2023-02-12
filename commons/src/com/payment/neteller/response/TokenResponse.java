package com.payment.neteller.response;

/**
 * Created by Sneha on 2/22/2017.
 */
public class TokenResponse
{
    private String accessToken;
    private String tokenType;
    private String expiresIn;
    private String error;

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public void setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
    }

    public String getTokenType()
    {
        return tokenType;
    }

    public void setTokenType(String tokenType)
    {
        this.tokenType = tokenType;
    }

    public String getExpiresIn()
    {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn)
    {
        this.expiresIn = expiresIn;
    }
}
