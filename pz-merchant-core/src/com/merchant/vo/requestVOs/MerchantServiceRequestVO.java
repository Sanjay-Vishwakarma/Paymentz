package com.merchant.vo.requestVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Sneha on 9/1/2016.
 */
@XmlRootElement(name="merchantServiceRequestVO")
@XmlAccessorType(XmlAccessType.FIELD)
public class MerchantServiceRequestVO
{

    @XmlElement(name="authentication")
    AuthenticationVO authentication;


    @XmlElement(name="customer")
    CustomerVO customer;


    @XmlElement(name="merchant")
    MerchantVO merchant;

    @XmlElement(name="billingAddressVO")
    BillingAddressVO billingAddressVO;

    @XmlElement(name="authToken")
    String authToken;

    @XmlElement(name="paymentId")
    String paymentId;

    public AuthenticationVO getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(AuthenticationVO authentication)
    {
        this.authentication = authentication;
    }

    public CustomerVO getCustomer()
    {
        return customer;
    }

    public void setCustomer(CustomerVO customer)
    {
        this.customer = customer;
    }

    public MerchantVO getMerchant()
    {
        return merchant;
    }

    public void setMerchant(MerchantVO merchant)
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


    public BillingAddressVO getBillingAddressVO()
    {
        return billingAddressVO;
    }

    public void setBillingAddressVO(BillingAddressVO billingAddressVO)
    {
        this.billingAddressVO = billingAddressVO;
    }

    public String getPaymentId()
    {
        return paymentId;
    }

    public void setPaymentId(String paymentId)
    {
        this.paymentId = paymentId;
    }
}
