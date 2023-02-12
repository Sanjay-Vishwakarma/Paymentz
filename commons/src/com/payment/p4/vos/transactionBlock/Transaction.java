package com.payment.p4.vos.transactionBlock;

import com.payment.p4.vos.transactionBlock.authorizationBlock.Authorization;
import com.payment.p4.vos.transactionBlock.customerBlock.Customer;
import com.payment.p4.vos.transactionBlock.eventsBlock.Events;
import com.payment.p4.vos.transactionBlock.frontEndBlock.Frontend;
import com.payment.p4.vos.transactionBlock.identificationBlock.Identification;
import com.payment.p4.vos.transactionBlock.loginBlock.Login;
import com.payment.p4.vos.transactionBlock.mandateBlock.Mandate;
import com.payment.p4.vos.transactionBlock.paymentBlock.Payment;
import com.payment.p4.vos.transactionBlock.processingBlock.Processing;
import com.payment.p4.vos.transactionBlock.schedulingBlock.Scheduling;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by Niket on 10/2/2015.
 */
@XmlRootElement(name="Transaction")
@XmlAccessorType(XmlAccessType.FIELD)
public class Transaction
{
    @XmlAttribute(name="mode")
    String mode;

    @XmlAttribute(name = "response")
    String response;

    @XmlElement(name = "Login")
    Login Login;

    @XmlElement(name = "Payment")
    List<Payment> Payment;

    @XmlElement(name = "Customer")
    Customer Customer;

    @XmlElement(name = "Frontend")
    Frontend Frontend;

    @XmlElement(name = "Identification")
    Identification Identification;

    @XmlElement(name = "Scheduling")
    Scheduling Scheduling;


    //Response Extra
    @XmlElement(name = "Processing")
    Processing Processing;

    @XmlElement(name = "Events")
    Events Events;

    @XmlElement(name = "Mandate")
    Mandate Mandate;

    @XmlElement(name = "Authorization")
    Authorization Authorization;


    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }


    public String getResponse()
    {
        return response;
    }

    public void setResponse(String response)
    {
        this.response = response;
    }

    public com.payment.p4.vos.transactionBlock.loginBlock.Login getLogin()
    {
        return Login;
    }

    public void setLogin(Login login)
    {
        Login = login;
    }

    public Identification getIdentification()
    {
        return Identification;
    }

    public void setIdentification(Identification identification)
    {
        Identification = identification;
    }

    public Frontend getFrontend()
    {
        return Frontend;
    }

    public void setFrontend(Frontend frontend)
    {
        Frontend = frontend;
    }

    public List<com.payment.p4.vos.transactionBlock.paymentBlock.Payment> getPayment()
    {
        return Payment;
    }

    public void setPayment(List<com.payment.p4.vos.transactionBlock.paymentBlock.Payment> payment)
    {
        Payment = payment;
    }

    public Scheduling getScheduling()
    {
        return Scheduling;
    }

    public void setScheduling(Scheduling scheduling)
    {
        Scheduling = scheduling;
    }

    public Customer getCustomer()
    {
        return Customer;
    }

    public void setCustomer(Customer customer)
    {
        Customer = customer;
    }

    public Processing getProcessing()
    {
        return Processing;
    }

    public void setProcessing(Processing processing)
    {
        Processing = processing;
    }

    public com.payment.p4.vos.transactionBlock.eventsBlock.Events getEvents()
    {
        return Events;
    }

    public void setEvents(com.payment.p4.vos.transactionBlock.eventsBlock.Events events)
    {
        Events = events;
    }

    public com.payment.p4.vos.transactionBlock.mandateBlock.Mandate getMandate()
    {
        return Mandate;
    }

    public void setMandate(com.payment.p4.vos.transactionBlock.mandateBlock.Mandate mandate)
    {
        Mandate = mandate;
    }

    public com.payment.p4.vos.transactionBlock.authorizationBlock.Authorization getAuthorization()
    {
        return Authorization;
    }

    public void setAuthorization(com.payment.p4.vos.transactionBlock.authorizationBlock.Authorization authorization)
    {
        Authorization = authorization;
    }
}
