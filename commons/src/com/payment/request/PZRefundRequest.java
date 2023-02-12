package com.payment.request;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 2/16/13
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class PZRefundRequest extends PZRequest
{
    public String getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount)
    {
        this.refundAmount = refundAmount;
    }

    private String refundAmount;
    private String notificationURL;
    private String refundReason;
    private String reversedAmount;
    private String requestedReversedAmount;

    public String getCaptureAmount()
    {
        return captureAmount;
    }

    public void setCaptureAmount(String captureAmount)
    {
        this.captureAmount = captureAmount;
    }

    public String getReversedAmount()
    {
        return reversedAmount;
    }

    public void setReversedAmount(String reversedAmount)
    {
        this.reversedAmount = reversedAmount;
    }

    private String captureAmount;
    private String requestedCaptureAmount;


    boolean isFraud;
    boolean isAdmin;
    private String transactionStatus;
    private String bankId;

    public String getBankId()
    {
        return bankId;
    }

    public void setBankId(String bankId)
    {
        this.bankId = bankId;
    }

    public boolean isAdmin()
    {
        return isAdmin;
    }

    public void setAdmin(boolean admin)
    {
        isAdmin = admin;
    }

    public boolean isFraud()
    {
        return isFraud;
    }

    public void setFraud(boolean fraud)
    {
        isFraud = fraud;
    }

    public String getRefundReason()
    {
        return refundReason;
    }

    public void setRefundReason(String refundReason)
    {
        this.refundReason = refundReason;
    }

    public String getTransactionStatus()
    {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus)
    {
        this.transactionStatus = transactionStatus;
    }

    public String getRequestedReversedAmount()
    {
        return requestedReversedAmount;
    }

    public void setRequestedReversedAmount(String requestedReversedAmount)
    {
        this.requestedReversedAmount = requestedReversedAmount;
    }

    public String getRequestedCaptureAmount()
    {
        return requestedCaptureAmount;
    }

    public void setRequestedCaptureAmount(String requestedCaptureAmount)
    {
        this.requestedCaptureAmount = requestedCaptureAmount;
    }

    public String getNotificationURL()
    {
        return notificationURL;
    }

    public void setNotificationURL(String notificationURL)
    {
        this.notificationURL = notificationURL;
    }
}
