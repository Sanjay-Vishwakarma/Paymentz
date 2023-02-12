package com.payment.beekash.vos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 2/22/2019.
 */
@XmlRootElement(name="shipping")
@XmlAccessorType(XmlAccessType.FIELD)
public class ShippingVO
{
    @XmlElement(name = "zip_code")
    String zip_code;
    @XmlElement(name = "address")
    String address ;
    @XmlElement(name = "city")
    String city;
    @XmlElement(name = "state")
    String state;
    @XmlElement(name = "country")
    String country;

    public String getZip_code()
    {
        return zip_code;
    }

    public void setZip_code(String zip_code)
    {
        this.zip_code = zip_code;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getCountry() {return country;}

    public void setCountry(String country)
    {
        this.country = country;
    }
}
