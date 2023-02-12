package com.payment.p4.vos.transactionBlock.customerBlock.addressBlock;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 10/2/2015.
 */
@XmlRootElement(name="Name")
@XmlAccessorType(XmlAccessType.FIELD)
public class Address
{

    @XmlElement(name="Street")
    String Street;
    @XmlElement(name="Zip")
    String Zip;
    @XmlElement(name="City")
    String City;
    @XmlElement(name="State")
    String State;
    @XmlElement(name="Country")
    String Country;

    public String getZip()
    {
        return Zip;
    }

    public void setZip(String zip)
    {
        Zip = zip;
    }

    public String getStreet()
    {
        return Street;
    }

    public void setStreet(String street)
    {
        Street = street;
    }

    public String getCity()
    {
        return City;
    }

    public void setCity(String city)
    {
        City = city;
    }

    public String getState()
    {
        return State;
    }

    public void setState(String state)
    {
        State = state;
    }

    public String getCountry()
    {
        return Country;
    }

    public void setCountry(String country)
    {
        Country = country;
    }
}
