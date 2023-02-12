package com.payment.p4.vos.transactionBlock.paymentBlock.clearingBlock;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 10/3/2015.
 */
@XmlRootElement(name="Clearing")
@XmlAccessorType(XmlAccessType.FIELD)
public class Clearing
{
    @XmlElement(name = "Amount")
    String Amount ;
    @XmlElement(name = "Currency")
    String Currency;
    @XmlElement(name = "Descriptor")
    String Descriptor ;
    @XmlElement(name = "Date")
    String Date ;

    @XmlElement(name = "Usage")
    String Usage ;

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

    public String getDate()
    {
        return Date;
    }

    public void setDate(String date)
    {
        Date = date;
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
