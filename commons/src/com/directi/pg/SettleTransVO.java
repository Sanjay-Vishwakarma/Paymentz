package com.directi.pg;

/**
 * Created by IntelliJ IDEA.
 * User: Jimmy Mehta
 * Date: Dec 24, 2012
 * Time: 10:22:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class SettleTransVO
{
    private String paymentOrderId;
    private String merchantOrderId;
    private String trackingId;
    private boolean pendingApproval;
    private boolean status;
    private String amount;
    private boolean refund;
    private boolean chargeback;

    public String getPaymentOrderId()
    {
        return paymentOrderId;
    }

    public void setPaymentOrderId(String paymentOrderId)
    {
        this.paymentOrderId = paymentOrderId;
    }

    public String getMerchantOrderId()
    {
        return merchantOrderId;
    }

    public void setMerchantOrderId(String merchantOrderId)
    {
        this.merchantOrderId = merchantOrderId;
    }

    public boolean isStatus()
    {
        return status;
    }

    public void setStatus(boolean status)
    {
        this.status = status;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public boolean isRefund()
    {
        return refund;
    }

    public void setRefund(boolean refund)
    {
        this.refund = refund;
    }

    public boolean isChargeback()
    {
        return chargeback;
    }

    public void setChargeback(boolean chargeback)
    {
        this.chargeback = chargeback;
    }
    public String getTrackingId()
    {
        return trackingId;
    }

    public void setTrackingId(String trackingId)
    {
        this.trackingId = trackingId;
    }

    public boolean isPendingApproval()
    {
        return pendingApproval;
    }

    public void setPendingApproval(boolean pendingApproval)
    {
        this.pendingApproval = pendingApproval;
    }


}
