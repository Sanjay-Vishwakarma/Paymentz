package com.payment.europay.core;

import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/5/13
 * Time: 1:44 AM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("addresses")
public class EuroPayAddress
{
    @XStreamAlias("type")
    @XStreamAsAttribute
    String type;

    @XStreamAlias("country")
    @XStreamAsAttribute
    String country;

    @XStreamAlias("state")
    @XStreamAsAttribute
    String state;

    @XStreamAlias("street")
    @XStreamAsAttribute
    String street;

    @XStreamAlias("zip")
    @XStreamAsAttribute
    String zip;

    @XStreamAlias("city")
    @XStreamAsAttribute
    String city;



    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

}
