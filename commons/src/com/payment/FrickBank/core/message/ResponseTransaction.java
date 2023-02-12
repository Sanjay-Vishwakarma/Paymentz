package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/22/13
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResponseTransaction
{
    public Processing getProcessing()
    {
        return processing;
    }

    public void setProcessing(Processing processing)
    {
        this.processing = processing;
    }
    @XStreamAlias("Processing")
    private Processing processing;

    public ResponseIdentification getIdentification()
    {
        return identification;
    }

    public void setIdentification(ResponseIdentification identification)
    {
        this.identification = identification;
    }

    @XStreamAlias("Identification")
    private ResponseIdentification identification;

    public ResponsePayment getPayment()
    {
        return payment;
    }

    public void setPayment(ResponsePayment payment)
    {
        this.payment = payment;
    }

    @XStreamAlias("Payment")
    private ResponsePayment payment;
    public String getResponse()
    {
        return response;
    }

    public void setResponse(String response)
    {
        this.response = response;
    }

    @XStreamAsAttribute
    private String response="SYNC";
}
