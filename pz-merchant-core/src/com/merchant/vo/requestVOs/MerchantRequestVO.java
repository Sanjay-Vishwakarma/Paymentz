package com.merchant.vo.requestVOs;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Sneha on 18-01-2022.
 */
@XmlRootElement(name="merchantRequestVO")
@XmlAccessorType(XmlAccessType.FIELD)
public class MerchantRequestVO
{
    @XmlElement(name="authToken")
    String authToken;

    @XmlElement(name="merchant")
    MerchantVO merchant;

    @XmlElement(name="authentication")
    AuthenticationVO authentication;

    @XmlElement(name="paymentBrand")
    String paymentBrand;

    @XmlElement(name="paymentMode")
    String paymentMode;

    @XmlElement(name="currency")
    String currency;

    public String getAuthToken()
    {
        return authToken;
    }

    public void setAuthToken(String authToken)
    {
        this.authToken = authToken;
    }

    public MerchantVO getMerchant()
    {
        return merchant;
    }

    public void setMerchant(MerchantVO merchant)
    {
        this.merchant = merchant;
    }

    public AuthenticationVO getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(AuthenticationVO authentication)
    {
        this.authentication = authentication;
    }

    public String getPaymentBrand()
    {
        return paymentBrand;
    }

    public void setPaymentBrand(String paymentBrand)
    {
        this.paymentBrand = paymentBrand;
    }

    public String getPaymentMode()
    {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode)
    {
        this.paymentMode = paymentMode;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    @Override
    public String toString()
    {
        return "MerchantRequestVO{" +
                "authToken='" + authToken + '\'' +
                ", email='" + merchant.getEmail() + '\'' +
                ", mobileNo='" + merchant.getMobilenumber() + '\'' +
                ", memberId='" + authentication.getMemberId() + '\'' +
                ", smsOtp='" + merchant.getSmsotp() + '\'' +
                ", emailOtp='" + merchant.getEmailotp() + '\'' +
                '}';
    }
}
