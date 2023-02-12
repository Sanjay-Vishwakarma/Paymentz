package com.manager.vo;

/**
 * Created by IntelliJ IDEA.
 * User: sandip
 * Date: 12/11/15
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class CardholderResponseVO
{
    String cardholderId;
    String status;
    String statusDescription;
    String memberId;
    String partnerId;

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    public String getCardholderId()
    {
        return cardholderId;
    }

    public void setCardholderId(String cardholderId)
    {
        this.cardholderId = cardholderId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatusDescription()
    {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription)
    {
        this.statusDescription = statusDescription;
    }
}
