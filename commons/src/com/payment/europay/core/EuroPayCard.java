package com.payment.europay.core;

import com.payment.common.core.CommCardDetailsVO;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/5/13
 * Time: 2:01 AM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("creditCard")
public class EuroPayCard
{
    @XStreamAlias("ownerLastName")
    @XStreamAsAttribute
    String ownerLastName;

    @XStreamAlias("ownerFirstName")
    @XStreamAsAttribute
    String ownerFirstName;

    @XStreamAlias("expireMonth")
    @XStreamAsAttribute
    String expireMonth;

    @XStreamAlias("expireYear")
    @XStreamAsAttribute
    String expireYear;

    @XStreamAlias("verification")
    @XStreamAsAttribute
    String verification;

    @XStreamAlias("number")
    @XStreamAsAttribute
    String number;

    @XStreamAlias("brand")
    @XStreamAsAttribute
    String brand;


    public String getOwnerLastName()
    {
        return ownerLastName;
    }

    public void setOwnerLastName(String ownerLastName)
    {
        this.ownerLastName = ownerLastName;
    }

    public String getOwnerFirstName()
    {
        return ownerFirstName;
    }

    public void setOwnerFirstName(String ownerFirstName)
    {
        this.ownerFirstName = ownerFirstName;
    }

    public String getExpireMonth()
    {
        return expireMonth;
    }

    public void setExpireMonth(String expireMonth)
    {
        this.expireMonth = expireMonth;
    }

    public String getExpireYear()
    {
        return expireYear;
    }

    public void setExpireYear(String expireYear)
    {
        this.expireYear = expireYear;
    }

    public String getVerification()
    {
        return verification;
    }

    public void setVerification(String verification)
    {
        this.verification = verification;
    }

    public String getNumber()
    {
        return number;
    }

    public void setNumber(String number)
    {
        this.number = number;
    }

    public String getBrand()
    {
        return brand;
    }

    public void setBrand(String brand)
    {
        this.brand = brand;
    }
}
