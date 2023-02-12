package com.merchant.vo.requestVOs;

import com.sun.jersey.api.core.InjectParam;

import javax.ws.rs.FormParam;

/**
 * Created by admin on 18-01-2022.
 */
public class MerchantRequest
{
    @FormParam("authToken")
    String authToken;

    @InjectParam("merchant")
    Merchant merchant;

    @InjectParam("authentication")
    Authentication authentication;

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

    public Merchant getMerchant()
    {
        return merchant;
    }

    public void setMerchant(Merchant merchant)
    {
        this.merchant = merchant;
    }

    public String getAuthToken()
    {
        return authToken;
    }

    public void setAuthToken(String authToken)
    {
        this.authToken = authToken;
    }

    @Override
    public String toString()
    {
        return "MerchantRequest{" +
                "authToken='" + authToken + '\'' +
                ", email='" + merchant.getEmail() + '\'' +
                ", mobileNo='" + merchant.getMobilenumber() + '\'' +
                ", memberId='" + authentication.getMemberId() + '\'' +
                ", smsOtp='" + merchant.getSmsotp() + '\'' +
                ", emailOtp='" + merchant.getEmailotp() + '\'' +
                '}';
    }
}
