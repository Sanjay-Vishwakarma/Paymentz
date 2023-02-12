package com.payment.p4.vos.transactionBlock.customerBlock.accountBlock.expiryBlock;

import javax.xml.bind.annotation.*;

/**
 * Created by Admin on 26/10/2015.
 */

@XmlRootElement(name = "Expiry")
@XmlAccessorType(XmlAccessType.FIELD)
public class Expiry
{
    @XmlAttribute(name = "month")
    String month;

    @XmlAttribute(name = "year")
    String year;

    public String getMonth()
    {
        return month;
    }

    public void setMonth(String month)
    {
        this.month = month;
    }

    public String getYear()
    {
        return year;
    }

    public void setYear(String year)
    {
        this.year = year;
    }
}
