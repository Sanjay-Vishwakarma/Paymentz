package com.transaction.vo.restVO.ResponseVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Sneha on 3/9/2016.
 */
@XmlRootElement(name="bankAccount")
@XmlAccessorType(XmlAccessType.FIELD)

public class BankAccount
{
    @XmlElement(name="registrationId")
    String registrationId;

    @XmlElement(name="registrationStatus")
    String registrationStatus;

    @XmlElement(name="holder")
    String holder;

    @XmlElement(name="iban")
    String iban;

    @XmlElement(name="bic")
    String bic;

    @XmlElement(name="country")
    String country;

    @XmlElement(name="accountNumber")
    String accountNumber;

    @XmlElement(name="accountType")
    String accountType;

    @XmlElement(name="routingNumber")
    String routingNumber;

    @XmlElement(name = "customerId")
    String customerId;

    public String getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }

    public String getRegistrationId()
    {
        return registrationId;
    }

    public void setRegistrationId(String registrationId)
    {
        this.registrationId = registrationId;
    }

    public String getRegistrationStatus()
    {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus)
    {
        this.registrationStatus = registrationStatus;
    }

    public String getAccountNumber()
    {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber)
    {
        this.accountNumber = accountNumber;
    }

    public String getAccountType()
    {
        return accountType;
    }

    public void setAccountType(String accountType)
    {
        this.accountType = accountType;
    }

    public String getRoutingNumber()
    {
        return routingNumber;
    }

    public void setRoutingNumber(String routingNumber)
    {
        this.routingNumber = routingNumber;
    }

    public String getHolder()
    {
        return holder;
    }

    public void setHolder(String holder)
    {
        this.holder = holder;
    }

    public String getIban()
    {
        return iban;
    }

    public void setIban(String iban)
    {
        this.iban = iban;
    }

    public String getBic()
    {
        return bic;
    }

    public void setBic(String bic)
    {
        this.bic = bic;
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
