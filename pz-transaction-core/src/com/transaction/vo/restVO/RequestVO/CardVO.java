package com.transaction.vo.restVO.RequestVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Trupti on 2/8/2016.
 */
@XmlRootElement(name="card")
@XmlAccessorType(XmlAccessType.FIELD)
public class CardVO
{
    @XmlElement(name="number")
    String number;

    @XmlElement(name="expiryMonth")
    String expiryMonth;

    @XmlElement(name="expiryYear")
    String expiryYear;

    @XmlElement(name="cvv")
    String cvv;

    @XmlElement(name="cardholder")
    String cardholder;

    @XmlElement(name="firstsix")
    String firstsix;

    @XmlElement(name="lastfour")
    String lastfour;


    public String getNumber()
    {
        return number;
    }

    public void setNumber(String number)
    {
        this.number = number;
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

    public String getCvv()
    {
        return cvv;
    }

    public void setCvv(String cvv)
    {
        this.cvv = cvv;
    }

    public String getCardholder()
    {
        return cardholder;
    }

    public void setCardholder(String cardholder)
    {
        this.cardholder = cardholder;
    }

    public String getFirstsix()
    {
        return firstsix;
    }

    public void setFirstsix(String firstsix)
    {
        this.firstsix = firstsix;
    }

    public String getLastfour()
    {
        return lastfour;
    }

    public void setLastfour(String lastfour)
    {
        this.lastfour = lastfour;
    }
}
