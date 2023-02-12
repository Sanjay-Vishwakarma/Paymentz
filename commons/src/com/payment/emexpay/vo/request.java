package com.payment.emexpay.vo;

/**
 * Created by Admin on 2/24/2018.
 */
public class request
{
    String amount;
    String currency;
    String description;
    String tracking_id;
    String language;
    String return_url;
    boolean test;
    billing_address billing_address;
    credit_card credit_card;
    customer customer;

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public billing_address getBillingAddress()
    {
        return billing_address;
    }

    public void setBillingAddress(billing_address billingAddress)
    {
        this.billing_address = billingAddress;
    }

    public credit_card getCreditCard()
    {
        return credit_card;
    }

    public void setCreditCard(credit_card creditCard)
    {
        this.credit_card = creditCard;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getTracking_id()
    {
        return tracking_id;
    }

    public void setTracking_id(String tracking_id)
    {
        this.tracking_id = tracking_id;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public boolean isTest()
    {
        return test;
    }

    public void setTest(boolean test)
    {
        this.test = test;
    }

    public customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(customer customer)
    {
        this.customer = customer;
    }

    public String getReturn_url()
    {
        return return_url;
    }

    public void setReturn_url(String return_url)
    {
        this.return_url = return_url;
    }
}
