package com.manager.vo;

/**
 * Created by Jitendra on 4/4/2018.
 */
public class ManualReconVO
{
    String trackingId;
    String amount;
    String paymentOrderNumber;
    String remark;
    String status;
    String desc;
    String dbPaymentNumber;
    String refundAmount;
    double chargebackAmount;
    String accountId;
    String time;
    String actionExecutorId;
    String actionExecutorName;
    String captureAmount;
    String ipAddress;

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getPaymentOrderNumber()
    {
        return paymentOrderNumber;
    }

    public void setPaymentOrderNumber(String paymentOrderNumber)
    {
        this.paymentOrderNumber = paymentOrderNumber;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public String getDbPaymentNumber()
    {
        return dbPaymentNumber;
    }

    public void setDbPaymentNumber(String dbPaymentNumber)
    {
        this.dbPaymentNumber = dbPaymentNumber;
    }

    public String getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount)
    {
        this.refundAmount = refundAmount;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public String getTrackingId()
    {
        return trackingId;
    }

    public void setTrackingId(String trackingId)
    {
        this.trackingId = trackingId;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getActionExecutorId()
    {
        return actionExecutorId;
    }

    public void setActionExecutorId(String actionExecutorId)
    {
        this.actionExecutorId = actionExecutorId;
    }

    public String getActionExecutorName()
    {
        return actionExecutorName;
    }

    public void setActionExecutorName(String actionExecutorName)
    {
        this.actionExecutorName = actionExecutorName;
    }

    public String getCaptureAmount()
    {
        return captureAmount;
    }

    public void setCaptureAmount(String captureAmount)
    {
        this.captureAmount = captureAmount;
    }

    public String getIpAddress(){ return ipAddress; }

    public void setIpAddress(String ipAddress){this.ipAddress = ipAddress; }

    public double getChargebackAmount()
    {
        return chargebackAmount;
    }

    public void setChargebackAmount(double chargebackAmount)
    {
        this.chargebackAmount = chargebackAmount;
    }
}
