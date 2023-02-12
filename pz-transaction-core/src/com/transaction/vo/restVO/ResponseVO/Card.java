package com.transaction.vo.restVO.ResponseVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Sneha on 2/11/2016.
 */
@XmlRootElement(name = "card")
@XmlAccessorType(XmlAccessType.FIELD)
public class Card
{
    @XmlElement(name="registrationId")
    String registrationId;

    @XmlElement(name="registrationStatus")
    String registrationStatus;

    @XmlElement(name="cardType")
    String cardType;

    @XmlElement(name="bin")
    String bin;

    @XmlElement(name="last4Digits")
    String lastFourDigits;

    @XmlElement(name="lastFourDigits")
    String last4Digits;

    @XmlElement(name="holder")
    String holder;

    @XmlElement(name="expiryMonth")
    String expiryMonth;

    @XmlElement(name="expiryYear")
    String expiryYear;

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

    public String getCardType()
    {
        return cardType;
    }

    public void setCardType(String cardType)
    {
        this.cardType = cardType;
    }

    public String getHolder()
    {
        return holder;
    }

    public void setHolder(String holder)
    {
        this.holder = holder;
    }

    public String getExpiryMonth()
    {
        return expiryMonth;
    }

    public void setExpiryMonth(String expiryMonth)
    {
        this.expiryMonth = expiryMonth;
    }

    public String getExpiryYear()
    {
        return expiryYear;
    }

    public void setExpiryYear(String expiryYear)
    {
        this.expiryYear = expiryYear;
    }

    public String getBin()
    {
        return bin;
    }

    public void setBin(String bin)
    {
        this.bin = bin;
    }

    public String getLastFourDigits()
    {
        return lastFourDigits;
    }

    public void setLastFourDigits(String lastFourDigits)
    {
        this.lastFourDigits = lastFourDigits;
    }

    public String getLast4Digits()
    {
        return last4Digits;
    }

    public void setLast4Digits(String last4Digits)
    {
        this.last4Digits = last4Digits;
    }
}
