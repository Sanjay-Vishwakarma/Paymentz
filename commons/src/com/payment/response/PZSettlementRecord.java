package com.payment.response;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/26/13
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class PZSettlementRecord extends PZResponse
{

    private String SDateTime;
    private String Paymentid;
    private String statusDetail;
    private String amount;
    private String merchantid;
    private String merchantTXNid;
    private String txnCODE;

    private String successfulAmount;
    private String refundAmount;
    private String chargebackAmount;
    private String transactionTime;

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

    public String getSuccessfulAmount()
    {
        return successfulAmount;
    }

    public void setSuccessfulAmount(String successfulAmount)
    {
        this.successfulAmount = successfulAmount;
    }

    public String getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount)
    {
        this.refundAmount = refundAmount;
    }

    public String getChargebackAmount()
    {
        return chargebackAmount;
    }

    public void setChargebackAmount(String chargebackAmount)
    {
        this.chargebackAmount = chargebackAmount;
    }

    public String getTransactionTime()
    {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime)
    {
        this.transactionTime = transactionTime;
    }
}
