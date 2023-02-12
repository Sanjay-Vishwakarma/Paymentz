package com.merchant.vo.requestVOs;

import com.sun.jersey.api.core.InjectParam;

import javax.ws.rs.FormParam;

/**
 * Created by admin on 10-07-2017.
 */
public class MerchantServiceRequest
{

    @InjectParam("authentication")
    Authentication authentication;

    @InjectParam("customer")
    Customer customer;

    @InjectParam("merchant")
    Merchant merchant;

    @FormParam("id")
    String id;

    @FormParam("authToken")
    String authToken;

    @FormParam("paymentId")
    String paymentId;


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

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getAuthToken()
    {
        return authToken;
    }

    public void setAuthToken(String authToken)
    {
        this.authToken = authToken;
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
