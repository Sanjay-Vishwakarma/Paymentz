package com.invoice.vo;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Sneha on 12/20/16.
 */

@XmlRootElement(name="billing")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillingAddress
{
    @FormParam("billing.givenName")
    String givenName;

    @FormParam("billing.surname")
    String surname;

    @FormParam("billing.street")
    String street;

    @FormParam("billing.city")
    String city;

    @FormParam("billing.state")
    String state;

    @FormParam("billing.postcode")
    String postcode;

    @FormParam("billing.country")
    String country;

    @FormParam("billing.language")
    String language;

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public String getGivenName()
    {
        return givenName;
    }

    public void setGivenName(String givenName)
    {
        this.givenName = givenName;
    }

    public String getSurname()
    {
        return surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
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

    public String getPostcode()
    {
        return postcode;
    }

    public void setPostcode(String postcode)
    {
        this.postcode = postcode;
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
