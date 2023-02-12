package com.payment.response;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 16/1/14
 * Time: 9:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class PZReconcilationRecord extends PZResponse
{

    private String SDateTime;
    private String Paymentid;
    private String statusDetail;
    private String amount;
    private String merchantid;
    private String merchantTXNid;
    private String txnCODE;
    public String getTxnCODE()
    {
        return txnCODE;
    }

    public void setTxnCODE(String txnCODE)
    {
        this.txnCODE = txnCODE;
    }


    public String getSDateTime()
    {
        return SDateTime;
    }

    public void setSDateTime(String SDateTime)
    {
        this.SDateTime = SDateTime;
    }

    public String getPaymentid()
    {
        return Paymentid;
    }

    public void setPaymentid(String paymentid)
    {
        Paymentid = paymentid;
    }

    public String getStatusDetail()
    {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail)
    {
        this.statusDetail = statusDetail;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getMerchantid()
    {
        return merchantid;
    }

    public void setMerchantid(String merchantid)
    {
        this.merchantid = merchantid;
    }

    public String getMerchantTXNid()
    {
        return merchantTXNid;
    }

    public void setMerchantTXNid(String merchantTXNid)
    {
        this.merchantTXNid = merchantTXNid;
    }


}
