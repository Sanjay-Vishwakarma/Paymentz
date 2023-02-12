package com.payment.europay.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/5/13
 * Time: 10:24 PM
 * To change this template use File | Settings | File Templates.
 */

@XStreamAlias("payment")
public class EuroPayPayment
{

    @XStreamAlias("usageL2")
    @XStreamAsAttribute
    String usageL2;

    @XStreamAlias("usageL1")
    @XStreamAsAttribute
    String usageL1;

    @XStreamAlias("amount")
    @XStreamAsAttribute
    String amount;

    @XStreamAlias("currency")
    @XStreamAsAttribute
    String currency;

    @XStreamAlias("method")
    @XStreamAsAttribute
    String method;

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public String getUsageL2()
    {
        return usageL2;
    }

    public void setUsageL2(String usageL2)
    {
        this.usageL2 = usageL2;
    }

    public String getUsageL1()
    {
        return usageL1;
    }

    public void setUsageL1(String usageL1)
    {
        this.usageL1 = usageL1;
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

}
