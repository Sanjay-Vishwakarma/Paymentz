package com.payment.skrill;

import com.payment.common.core.CommResponseVO;

/**
 * Created by Admin on 11/1/2017.
 */
public class SkrillResponseVO extends CommResponseVO
{
    private String fromEmail;
    private String customerCurrency;
    private String customerAmount;

    public String getFromEmail()
    {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail)
    {
        this.fromEmail = fromEmail;
    }

    public String getCustomerCurrency()
    {
        return customerCurrency;
    }

    public void setCustomerCurrency(String customerCurrency)
    {
        this.customerCurrency = customerCurrency;
    }

    public String getCustomerAmount()
    {
        return customerAmount;
    }

    public void setCustomerAmount(String customerAmount)
    {
        this.customerAmount = customerAmount;
    }
}
