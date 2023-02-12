package com.payment.p4.vos.transactionBlock.paymentBlock;

import com.payment.p4.vos.transactionBlock.paymentBlock.clearingBlock.Clearing;
import com.payment.p4.vos.transactionBlock.paymentBlock.presentationBlock.Presentation;

import javax.xml.bind.annotation.*;

/**
 * Created by Niket on 10/2/2015.
 */
@XmlRootElement(name = "Payment")
@XmlAccessorType(XmlAccessType.FIELD)
public class Payment
{
    @XmlAttribute(name = "code")
    String code;

    @XmlElement(name="Presentation")
    Presentation Presentation;

    @XmlElement(name = "Clearing")
    Clearing Clearing;

    @XmlElement(name = "Cleared")
    Clearing Cleared;

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public Presentation getPresentation()
    {
        return Presentation;
    }

    public void setPresentation(Presentation presentation)
    {
        Presentation = presentation;
    }

    public Clearing getClearing()
    {
        return Clearing;
    }

    public void setClearing(Clearing clearing)
    {
        Clearing = clearing;
    }

    public com.payment.p4.vos.transactionBlock.paymentBlock.clearingBlock.Clearing getCleared()
    {
        return Cleared;
    }

    public void setCleared(com.payment.p4.vos.transactionBlock.paymentBlock.clearingBlock.Clearing cleared)
    {
        Cleared = cleared;
    }
}
