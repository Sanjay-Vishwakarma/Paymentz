package com.payment.emax_high_risk.core;

import com.payment.common.core.CommResponseVO;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 2/18/15
 * Time: 6:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class EmaxResponseVO extends CommResponseVO
{
    String trackingId;
    String token;
    String stamp;
    String brand;
    String address_details;
    String card_holder;

    public String getTrackingId()
    {
        return trackingId;
    }

    public void setTrackingId(String trackingId)
    {
        this.trackingId = trackingId;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getStamp()
    {
        return stamp;
    }

    public void setStamp(String stamp)
    {
        this.stamp = stamp;
    }

    public String getBrand()
    {
        return brand;
    }

    public void setBrand(String brand)
    {
        this.brand = brand;
    }

    public String getAddress_details()
    {
        return address_details;
    }

    public void setAddress_details(String address_details)
    {
        this.address_details = address_details;
    }

    public String getCard_holder()
    {
        return card_holder;
    }

    public void setCard_holder(String card_holder)
    {
        this.card_holder = card_holder;
    }
}

