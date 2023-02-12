package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/1/13
 * Time: 4:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class Transaction
{
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

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }



    public Identification getIdentification()
    {
        return identification;
    }

    public void setIdentification(Identification identification)
    {
        this.identification = identification;
    }

    public Payment getPayment()
    {
        return payment;
    }

    public void setPayment(Payment payment)
    {
        this.payment = payment;
    }

    public Recurrence getRecurrence()
    {
        return recurrence;
    }

    public void setRecurrence(Recurrence recurrence)
    {
        this.recurrence = recurrence;
    }

    public Account getAccount()
    {
        return account;
    }

    public void setAccount(Account account)
    {
        this.account = account;
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public Analysis getAnalysis()
    {
        return analysis;
    }

    public void setAnalysis(Analysis analysis)
    {
        this.analysis = analysis;
    }

    @XStreamAsAttribute
    private String mode="";
    @XStreamAsAttribute
    private String response="SYNC";
    @XStreamAsAttribute
    private String channel="";

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    @XStreamAlias("User")
    private User user;
    @XStreamAlias("Identification")
    private Identification identification;
    @XStreamAlias("Payment")
    private Payment payment;
    @XStreamAlias("Recurrence")
    private Recurrence recurrence;
    @XStreamAlias("Account")
    private Account account;
    @XStreamAlias("Customer")
    private Customer customer;
    @XStreamAlias("Analysis")
    private Analysis analysis;

    public Processing getProcessing()
    {
        return processing;
    }

    public void setProcessing(Processing processing)
    {
        this.processing = processing;
    }



    @XStreamAlias("processing")
    private Processing processing;

}
