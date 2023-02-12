package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/1/13
 * Time: 3:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Address
{
    @XStreamAlias("Country")
    private String country;
    @XStreamAlias("City")
    private String city;
    @XStreamAlias("State")
    private String state;
    @XStreamAlias("Street")
    private String street;

    public String getCountry()
    {
        return country;
    }

    public String getCity()
    {
        return city;
    }

    public String getState()
    {
        return state;
    }

    public String getStreet()
    {
        return street;
    }

    public String getZip()
    {
        return zip;
    }

    @XStreamAlias("Zip")
    private String zip;

    public void setCountry(String country)
    {
        this.country = country;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }
}
