package com.payment.response;

/**
 * Created by ThinkPadT410 on 7/5/2016.
 */
public class PZChargebackRecord
{
    private String amount;
    private String trackingid;
    private String paymentid;
    private String chargebackAmount;
    private String rrn;
    private String chargebackReason;
    private String currency;
    private String date;
    private String isBlacklist;
    private String isRefund;
    private String chargebackDate;


    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getAmount()
    {
        return amount;
    }

    public String getTrackingid()
    {
        return trackingid;
    }

    public void setTrackingid(String trackingid)
    {
        this.trackingid = trackingid;
    }

    public String getPaymentid()
    {
        return paymentid;
    }

    public void setPaymentid(String paymentid)
    {
        this.paymentid = paymentid;
    }

    public String getChargebackAmount()
    {
        return chargebackAmount;
    }

    public void setChargebackAmount(String chargebackAmount)
    {
        this.chargebackAmount = chargebackAmount;
    }

    public String getRrn()
    {
        return rrn;
    }

    public void setRrn(String rrn)
    {
        this.rrn = rrn;
    }

    public String getChargebackReason()
    {
        return chargebackReason;
    }

    public void setChargebackReason(String chargebackReason)
    {
        this.chargebackReason = chargebackReason;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date) {this.date = date;}

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency) {this.currency = currency;}

    public String getIsBlacklist()
    {
        return isBlacklist;
    }

    public void setIsBlacklist(String isBlacklist)
    {
        this.isBlacklist = isBlacklist;
    }

    public String getIsRefund()
    {
        return isRefund;
    }

    public void setIsRefund(String isRefund)
    {
        this.isRefund = isRefund;
    }

    public String getChargebackDate()
    {
        return chargebackDate;
    }

    public void setChargebackDate(String chargebackDate)
    {
        this.chargebackDate = chargebackDate;
    }
}