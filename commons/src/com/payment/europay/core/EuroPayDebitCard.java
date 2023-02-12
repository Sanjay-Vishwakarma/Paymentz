package com.payment.europay.core;

import com.payment.common.core.CommCardDetailsVO;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/5/13
 * Time: 10:36 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("debitCardClassic")
public class EuroPayDebitCard
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

    @XStreamAlias("number")
    @XStreamAsAttribute
    String number;

    @XStreamAlias("brand")
    @XStreamAsAttribute
    String brand;

    @XStreamAlias("bankName")
    @XStreamAsAttribute
    String bankName;

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

    public String getBankName()
    {
        return bankName;
    }

    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

    public String getBankCode()
    {
        return bankCode;
    }

    public void setBankCode(String bankCode)
    {
        this.bankCode = bankCode;
    }

    @XStreamAlias("bankCode")
    @XStreamAsAttribute
    String bankCode;



}
