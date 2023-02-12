package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/1/13
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Presentation
{
    @XStreamAlias("Amount")
    private String Amount="Amount";
    @XStreamAlias("Currency")
    private String Currency="Currency";
    @XStreamAlias("Usage")
    private String Usage="Usage";

    public String getAmount()
    {
        return Amount;
    }

    public void setAmount(String amount)
    {
        Amount = amount;
    }

    public String getCurrency()
    {
        return Currency;
    }

    public void setCurrency(String currency)
    {
        Currency = currency;
    }

    public String getUsage()
    {
        return Usage;
    }

    public void setUsage(String usage)
    {
        Usage = usage;
    }


}
