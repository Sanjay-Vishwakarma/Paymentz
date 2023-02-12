package com.payment.response;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 2/16/13
 * Time: 10:46 AM
 * To change this template use File | Settings | File Templates.
 */


public class PZResponse
{
    private String trackingId;
    private PZResponseStatus status;
    private String responseDesceiption;
    private String dbStatus;
    private String remark;
    private String refundAmount;
    private String paymentURL;

    public String getPaymentURL()
    {
        return paymentURL;
    }

    public void setPaymentURL(String paymentURL)
    {
        this.paymentURL = paymentURL;
    }

    public String getTrackingId()
    {
        return trackingId;
    }

    public void setTrackingId(String trackingId)
    {
        this.trackingId = trackingId;
    }

    public PZResponseStatus getStatus()
    {
        return status;
    }

    public void setStatus(PZResponseStatus status)
    {
        this.status = status;
    }

    public String getResponseDesceiption()
    {
        return responseDesceiption;
    }

    public void setResponseDesceiption(String responseDesceiption)
    {
        this.responseDesceiption = responseDesceiption;
    }

    public String getDbStatus()
    {
        return dbStatus;
    }

    public void setDbStatus(String dbStatus)
    {
        this.dbStatus = dbStatus;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }
}
