package com.payment.p4.vos.p4MainVo;

import com.payment.p4.vos.transactionBlock.identificationBlock.Identification;
import com.payment.p4.vos.transactionBlock.mandateBlock.Mandate;
import com.payment.p4.vos.transactionBlock.processingBlock.Processing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 7/11/2015.
 */
@XmlRootElement(name = "Notification")
@XmlAccessorType(XmlAccessType.FIELD)
public class Notification
{
    @XmlElement(name = "Processing")
    Processing Processing;

    @XmlElement(name = "Identification")
    Identification Identification;

    @XmlElement(name = "Mandate")
    Mandate Mandate;

    public Processing getProcessing()
    {
        return Processing;
    }

    public void setProcessing(Processing processing)
    {
        Processing = processing;
    }

    public Identification getIdentification()
    {
        return Identification;
    }

    public void setIdentification(Identification identification)
    {
        Identification = identification;
    }

    public Mandate getMandate()
    {
        return Mandate;
    }

    public void setMandate(Mandate mandate)
    {
        Mandate = mandate;
    }
}
