package com.payment.request;

/**
 * Created with IntelliJ IDEA.
 * User: Chandan
 * Date: 2/16/13
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class PZCancelRequest extends PZRequest
{
    private String cancelReason;
    private String cancelAmount;
    private String notificationURL;

    public String getNotificationURL()
    {
        return notificationURL;
    }

    public void setNotificationURL(String notificationURL)
    {
        this.notificationURL = notificationURL;
    }

    public String getCancelReason()
    {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason)
    {
        this.cancelReason = cancelReason;
    }

    public String getAmount()
    {
        return cancelAmount;
    }

    public void setAmount(String amount)
    {
        this.cancelAmount = amount;
    }
}
