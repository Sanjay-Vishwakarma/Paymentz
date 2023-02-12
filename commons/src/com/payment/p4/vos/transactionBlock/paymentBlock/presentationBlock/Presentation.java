package com.payment.p4.vos.transactionBlock.paymentBlock.presentationBlock;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Niket 10/2/2015.
 */
@XmlRootElement(name="Presentation")
@XmlAccessorType(XmlAccessType.FIELD)
public class Presentation
{
    @XmlElement(name = "Amount")
    String Amount;
    @XmlElement(name = "Currency")
    String Currency;
    @XmlElement(name = "Usage")
    String Usage;

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
