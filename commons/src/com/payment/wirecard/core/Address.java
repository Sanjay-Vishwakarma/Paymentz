package com.payment.wirecard.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/27/13
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("ADDRESS")
public class Address
{

    @XStreamAlias("FirstName")
    String firstName;

    @XStreamAlias("LastName")
    String lastName;

    @XStreamAlias("Address1")
    String address1;

    @XStreamAlias("Address2")
    String address2;

    @XStreamAlias("City")
    String city;

    @XStreamAlias("ZipCode")
    String zip;

    @XStreamAlias("Email")
    String email;

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    @XStreamAlias("State")
    String state;

    @XStreamAlias("Country")
    String country;

    @XStreamAlias("Phone")
    String phone;

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getAddress1()
    {
        return address1;
    }

    public void setAddress1(String address1)
    {
        this.address1 = address1;
    }

    public String getAddress2()
    {
        return address2;
    }

    public void setAddress2(String address2)
    {
        this.address2 = address2;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }
}
