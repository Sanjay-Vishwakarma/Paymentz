package com.payment.p4.vos.transactionBlock.mandateBlock;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 16/11/2015.
 */
@XmlRootElement(name = "Mandate")
@XmlAccessorType(XmlAccessType.FIELD)
public class Mandate
{
    @XmlElement(name = "Type")
    String Type;

    @XmlElement(name = "Recurrence")
    String Recurrence;

    @XmlElement(name = "Reference")
    String Reference;

    @XmlElement(name = "Issued")
    String Issued;

    public String getType()
    {
        return Type;
    }

    public void setType(String type)
    {
        Type = type;
    }

    public String getRecurrence()
    {
        return Recurrence;
    }

    public void setRecurrence(String recurrence)
    {
        Recurrence = recurrence;
    }

    public String getReference()
    {
        return Reference;
    }

    public void setReference(String reference)
    {
        Reference = reference;
    }

    public String getIssued()
    {
        return Issued;
    }

    public void setIssued(String issued)
    {
        Issued = issued;
    }
}
