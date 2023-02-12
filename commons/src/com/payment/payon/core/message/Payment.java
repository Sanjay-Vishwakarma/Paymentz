package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 8:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class Payment
{
    @XStreamAsAttribute
    private String type;

    public void setPreviousAmount(String previousAmount)
    {
        this.previousAmount = previousAmount;
    }

    private String previousAmount;
    @XStreamAlias("Amount")
    private String amount;

    public String getType()
    {
        return type;
    }

    public String getPreviousAmount()
    {
        return previousAmount;
    }

    public String getAmount()
    {
        return amount;
    }

    public String getCurrency()
    {
        return currency;
    }

    public String getDescriptor()
    {
        return descriptor;
    }

    @XStreamAlias("Currency")
    private String currency;
    @XStreamAlias("Description")
    private String descriptor;

    public void setType(String type)
    {
        this.type = type;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public void setDescriptor(String descriptor)
    {
        this.descriptor = descriptor;
    }
}
