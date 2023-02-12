package com.payment.p4.vos.transactionBlock.eventsBlock.eventBlock;

import com.payment.p4.vos.transactionBlock.identificationBlock.Identification;
import com.payment.p4.vos.transactionBlock.loginBlock.Login;
import com.payment.p4.vos.transactionBlock.paymentBlock.Payment;
import com.payment.p4.vos.transactionBlock.processingBlock.Processing;

import javax.xml.bind.annotation.*;

/**
 * Created by Admin on 4/11/2015.
 */
@XmlRootElement(name = "Event")
@XmlAccessorType(XmlAccessType.FIELD)
public class Event
{
    @XmlAttribute(name = "sequence")
    String sequence;

    @XmlAttribute(name = "source")
    String source;

    @XmlElement(name = "Login")
    private Login Login;

    @XmlElement(name = "Identification")
    private Identification Identification;

    @XmlElement(name = "Processing")
    private Processing Processing;

    @XmlElement(name = "Payment")
    private Payment Payment;

    public String getSequence()
    {
        return sequence;
    }

    public void setSequence(String sequence)
    {
        this.sequence = sequence;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public com.payment.p4.vos.transactionBlock.loginBlock.Login getLogin()
    {
        return Login;
    }

    public void setLogin(com.payment.p4.vos.transactionBlock.loginBlock.Login login)
    {
        Login = login;
    }

    public com.payment.p4.vos.transactionBlock.identificationBlock.Identification getIdentification()
    {
        return Identification;
    }

    public void setIdentification(com.payment.p4.vos.transactionBlock.identificationBlock.Identification identification)
    {
        Identification = identification;
    }

    public com.payment.p4.vos.transactionBlock.processingBlock.Processing getProcessing()
    {
        return Processing;
    }

    public void setProcessing(com.payment.p4.vos.transactionBlock.processingBlock.Processing processing)
    {
        Processing = processing;
    }

    public com.payment.p4.vos.transactionBlock.paymentBlock.Payment getPayment()
    {
        return Payment;
    }

    public void setPayment(com.payment.p4.vos.transactionBlock.paymentBlock.Payment payment)
    {
        Payment = payment;
    }
}
