package com.payment.request;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 10/30/13
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class PZReconcilationRequest extends PZRequest
{

    public String getPaymentid()
    {
        return paymentid;
    }

    public void setPaymentid(String paymentid)
    {
        this.paymentid = paymentid;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getPZstatus()
    {
        return PZstatus;
    }

    public void setPZstatus(String PZstatus)
    {
        this.PZstatus = PZstatus;
    }

    private String paymentid;
    private String amount;
    private String PZstatus;
}
