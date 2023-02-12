package com.payment.emax_high_risk.core;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 1/30/15
 * Time: 11:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class Payment
{
    public Payment getPayment()
    {
        return payment;
    }

    public void setPayment(Payment payment)
    {
        this.payment = payment;
    }

    public Payment payment;
    public String auth_code;
    public String bank_code;
    public String rrn;
    public String ref_id;
    public String message;
    public String gateway_id;
    public String billing_descriptor;

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String status;

    public String getAuth_code()
    {
        return auth_code;
    }

    public void setAuth_code(String auth_code)
    {
        this.auth_code = auth_code;
    }

    public String getBank_code()
    {
        return bank_code;
    }

    public void setBank_code(String bank_code)
    {
        this.bank_code = bank_code;
    }

    public String getRrn()
    {
        return rrn;
    }

    public void setRrn(String rrn)
    {
        this.rrn = rrn;
    }

    public String getRef_id()
    {
        return ref_id;
    }

    public void setRef_id(String ref_id)
    {
        this.ref_id = ref_id;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getGateway_id()
    {
        return gateway_id;
    }

    public void setGateway_id(String gateway_id)
    {
        this.gateway_id = gateway_id;
    }

    public String getBilling_descriptor()
    {
        return billing_descriptor;
    }

    public void setBilling_descriptor(String billing_descriptor)
    {
        this.billing_descriptor = billing_descriptor;
    }




}
