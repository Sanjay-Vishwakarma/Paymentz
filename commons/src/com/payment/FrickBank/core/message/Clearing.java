package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/2/13
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class Clearing
{
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

    public String getDescriptor()
    {
        return Descriptor;
    }

    public void setDescriptor(String descriptor)
    {
        Descriptor = descriptor;
    }


    @XStreamAlias("Amount")
    private String Amount="";
    @XStreamAlias("Currency")
    private String Currency="";
    @XStreamAlias("Descriptor")
    private String Descriptor="";

    public String getFxRate()
    {
        return FxRate;
    }

    public void setFxRate(String fxRate)
    {
        FxRate = fxRate;
    }

    public String getFxSource()
    {
        return FxSource;
    }

    public void setFxSource(String fxSource)
    {
        FxSource = fxSource;
    }

    public String getFxDate()
    {
        return FxDate;
    }

    public void setFxDate(String fxDate)
    {
        FxDate = fxDate;
    }

    @XStreamAlias("FxRate")
    private String FxRate="";
    @XStreamAlias("FxSource")
    private String FxSource="";
    @XStreamAlias("FxDate")
    private String FxDate="";
}
