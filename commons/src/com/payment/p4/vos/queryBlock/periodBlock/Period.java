package com.payment.p4.vos.queryBlock.periodBlock;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 28/10/2015.
 */
@XmlRootElement(name = "Period")
@XmlAccessorType(XmlAccessType.FIELD)
public class Period
{
    @XmlAttribute(name = "strict")
    String strict;

    @XmlAttribute(name = "from")
    String from;

    @XmlAttribute(name = "to")
    String to;

    public String getStrict()
    {
        return strict;
    }

    public void setStrict(String strict)
    {
        this.strict = strict;
    }

    public String getFrom()
    {
        return from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public String getTo()
    {
        return to;
    }

    public void setTo(String to)
    {
        this.to = to;
    }
}
