package com.transaction.vo.restVO.ResponseVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 4/20/2020.
 */
@XmlRootElement(name = "ShippingInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ShippingInfo
{
    @XmlElement(name="delivery_date")
    String delivery_date;

    @XmlElement(name="ship_address")
    String ship_address;

    @XmlElement(name="ship_city")
    String ship_city;

    @XmlElement(name="ship_country")
    String ship_country;

    @XmlElement(name="ship_first_name")
    String ship_first_name;

    @XmlElement(name="ship_last_name")
    String ship_last_name;

    @XmlElement(name="ship_region")
    String ship_region;

    @XmlElement(name="ship_zip")
    String ship_zip;

    @XmlElement(name="shipped_date")
    String shipped_date;

    @XmlElement(name="shipping_type")
    String shipping_type;

    @XmlElement(name="tracking_number")
    String tracking_number;

    @XmlElement(name="shipping_amount")
    String shipping_amount;

    public String getDelivery_date()
    {
        return delivery_date;
    }

    public void setDelivery_date(String delivery_date)
    {
        this.delivery_date = delivery_date;
    }

    public String getShip_address()
    {
        return ship_address;
    }

    public void setShip_address(String ship_address)
    {
        this.ship_address = ship_address;
    }

    public String getShip_city()
    {
        return ship_city;
    }

    public void setShip_city(String ship_city)
    {
        this.ship_city = ship_city;
    }

    public String getShip_country()
    {
        return ship_country;
    }

    public void setShip_country(String ship_country)
    {
        this.ship_country = ship_country;
    }

    public String getShip_first_name()
    {
        return ship_first_name;
    }

    public void setShip_first_name(String ship_first_name)
    {
        this.ship_first_name = ship_first_name;
    }

    public String getShip_last_name()
    {
        return ship_last_name;
    }

    public void setShip_last_name(String ship_last_name)
    {
        this.ship_last_name = ship_last_name;
    }

    public String getShip_region()
    {
        return ship_region;
    }

    public void setShip_region(String ship_region)
    {
        this.ship_region = ship_region;
    }

    public String getShip_zip()
    {
        return ship_zip;
    }

    public void setShip_zip(String ship_zip)
    {
        this.ship_zip = ship_zip;
    }

    public String getShipped_date()
    {
        return shipped_date;
    }

    public void setShipped_date(String shipped_date)
    {
        this.shipped_date = shipped_date;
    }

    public String getShipping_type()
    {
        return shipping_type;
    }

    public void setShipping_type(String shipping_type)
    {
        this.shipping_type = shipping_type;
    }

    public String getTracking_number()
    {
        return tracking_number;
    }

    public void setTracking_number(String tracking_number)
    {
        this.tracking_number = tracking_number;
    }

    public String getShipping_amount()
    {
        return shipping_amount;
    }

    public void setShipping_amount(String shipping_amount)
    {
        this.shipping_amount = shipping_amount;
    }
}
