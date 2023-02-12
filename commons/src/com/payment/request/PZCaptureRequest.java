package com.payment.request;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 2/16/13
 * Time: 11:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class PZCaptureRequest extends PZRequest
{
    private Double captureAmount;
    private String pod;
    private String notificationUrl;

    public String getPodBatch()
    {
        return podBatch;
    }

    public void setPodBatch(String podBatch)
    {
        this.podBatch = podBatch;
    }

    private String podBatch;

    public Double getAmount()
    {
        return captureAmount;
    }

    public void setAmount(Double captureAmount)
    {
        this.captureAmount = captureAmount;
    }

    public String getPod()
    {
        return pod;
    }

    public String getNotificationUrl()
    {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl)
    {
        this.notificationUrl = notificationUrl;
    }

    public void setPod(String pod)
    {
        this.pod = pod;
    }
}
