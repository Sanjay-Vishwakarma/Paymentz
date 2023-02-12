package com.payment.emax_high_risk.core;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 1/30/15
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class Transaction
{

    //public Customer customer;
    //public CreditCard credit_card;
    //public BillingAddress billing_address;
    //public BeProtectedVarification be_protected_verification;
    public Authorization authorization;
    public Payment payment;
    public String uid;
    public String status;
    public String amount;
    public String currency;
    public String description;
    public String type;
    public String tracking_id;
    public String message;
    public String test;
    public String created_at;
    public String updated_at;
    public String id;
    public String reason;
    public CreditCard credit_card;
    public BillingAddress billing_address;
    public Chargeback chargeback;

    /*public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    */

    /*

    public BeProtectedVarification getBeProtectedVarification()
    {
        return be_protected_verification;
    }

    public void setBeProtectedVarification(BeProtectedVarification beProtectedVarification)
    {
        this.be_protected_verification = beProtectedVarification;
    }*/

    public Authorization getAuthorization()
    {
        return authorization;
    }

    public void setAuthorization(Authorization authorization)
    {
        this.authorization = authorization;
    }

    public Payment getPayment()
    {
        return payment;
    }

    public void setPayment(Payment payment)
    {
        this.payment = payment;
    }

    public CreditCard getCreditCard()
    {
        return credit_card;
    }

    public void setCreditCard(CreditCard creditCard)
    {
        this.credit_card = creditCard;
    }

    public BillingAddress getBillingAddress()
    {
        return billing_address;
    }

    public void setBillingAddress(BillingAddress billingAddress)
    {
        this.billing_address = billingAddress;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

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

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getTracking_id()
    {
        return tracking_id;
    }

    public void setTracking_id(String tracking_id)
    {
        this.tracking_id = tracking_id;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getTest()
    {
        return test;
    }

    public void setTest(String test)
    {
        this.test = test;
    }

    public String getCreated_at()
    {
        return created_at;
    }

    public void setCreated_at(String created_at)
    {
        this.created_at = created_at;
    }

    public String getUpdated_at()
    {
        return updated_at;
    }

    public void setUpdated_at(String updated_at)
    {
        this.updated_at = updated_at;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public Chargeback getChargeback()
    {
        return chargeback;
    }

    public void setChargeback(Chargeback chargeback)
    {
        this.chargeback = chargeback;
    }
}
