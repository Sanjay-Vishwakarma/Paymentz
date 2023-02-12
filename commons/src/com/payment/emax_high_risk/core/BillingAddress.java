package com.payment.emax_high_risk.core;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 2/18/15
 * Time: 6:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class BillingAddress
{
    public String address;
    public String city;
    public String state;
    public String zip;
    public String country;

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

    public String getZip()
    {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }
}
