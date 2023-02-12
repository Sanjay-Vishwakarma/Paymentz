package com.fraud.vo;

/**
 * Created by SurajT on 3/9/2018.
 */
public class PZCustomerDetailsVO
{
    String customer_registration_id;
    String cust_request_id;

    public String getCustomer_registration_id()
    {
        return customer_registration_id;
    }

    public void setCustomer_registration_id(String customer_registration_id)
    {
        this.customer_registration_id = customer_registration_id;
    }

    public String getCust_request_id()
    {
        return cust_request_id;
    }

    public void setCust_request_id(String cust_request_id)
    {
        this.cust_request_id = cust_request_id;
    }
}
