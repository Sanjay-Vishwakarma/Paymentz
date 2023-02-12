package com.payment.europay.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/5/13
 * Time: 2:22 AM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("customer")
public class EuroPayCustomer
{

    @XStreamAlias("birthMonth")
    @XStreamAsAttribute
    String birthMonth;

    @XStreamAlias("birthDay")
    @XStreamAsAttribute
    String birthDay;

    @XStreamAlias("birthYear")
    @XStreamAsAttribute
    String birthYear;

    @XStreamAlias("salutation")
    @XStreamAsAttribute
    String salutation;

    @XStreamAlias("lastName")
    @XStreamAsAttribute
    String lastName;


    @XStreamAlias("firstName")
    @XStreamAsAttribute
    String firstName;

    @XStreamAlias("addresses")
    EuroPayAddress euroPayAddress;

    @XStreamAlias("title")
    @XStreamAsAttribute
    EuroPayAddress title;


    public EuroPayAddress getTitle()
    {
        return title;
    }

    public void setTitle(EuroPayAddress title)
    {
        this.title = title;
    }

    public String getBirthMonth()
    {
        return birthMonth;
    }

    public void setBirthMonth(String birthMonth)
    {
        this.birthMonth = birthMonth;
    }

    public String getBirthDay()
    {
        return birthDay;
    }

    public void setBirthDay(String birthDay)
    {
        this.birthDay = birthDay;
    }

    public String getBirthYear()
    {
        return birthYear;
    }

    public void setBirthYear(String birthYear)
    {
        this.birthYear = birthYear;
    }

    public String getSalutation()
    {
        return salutation;
    }

    public void setSalutation(String salutation)
    {
        this.salutation = salutation;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public EuroPayAddress getEuroPayAddress()
    {
        return euroPayAddress;
    }

    public void setEuroPayAddress(EuroPayAddress euroPayAddress)
    {
        this.euroPayAddress = euroPayAddress;
    }

    public EuroPayIdentity getEuroPayIdentity()
    {
        return euroPayIdentity;
    }

    public void setEuroPayIdentity(EuroPayIdentity euroPayIdentity)
    {
        this.euroPayIdentity = euroPayIdentity;
    }

    @XStreamAlias("identity")
    EuroPayIdentity euroPayIdentity;





}


