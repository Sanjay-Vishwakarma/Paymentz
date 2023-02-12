package com.payment.payvision.core.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 7/17/2018.
 */

@XmlRootElement(name="additionalInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class AdditionalInfo
{
    @XmlElement(name = "Recipient",required = true)
    private Recipient Recipient;

    public Recipient getRecipient()
    {
        return Recipient;
    }

    public void setRecipient(Recipient recipient)
    {
        this.Recipient = recipient;
    }
}
