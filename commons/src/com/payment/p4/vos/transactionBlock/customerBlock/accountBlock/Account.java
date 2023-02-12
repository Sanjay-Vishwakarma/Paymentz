package com.payment.p4.vos.transactionBlock.customerBlock.accountBlock;

import com.payment.p4.vos.transactionBlock.customerBlock.accountBlock.expiryBlock.Expiry;

import javax.xml.bind.annotation.*;

/**
 * Created by Niket on 10/2/2015.
 */
@XmlRootElement(name="Account")
@XmlAccessorType(XmlAccessType.FIELD)
public class Account
{
    @XmlElement(name = "Bank")
    String Bank ;

    @XmlElement(name = "Country")
    String Country ;

    @XmlElement(name = "Number")
    String Number ;

    @XmlElement(name = "Holder")
    String Holder ;

    //For Net banking
    @XmlElement(name = "IBAN")
    String IBAN;

    @XmlElement(name = "BIC")
    String BIC;

    @XmlElement(name = "BankName")
    String BankName;

    //For Online Bank Transfer

    @XmlElement(name = "Brand")
    String Brand;

    //For Credit Card

    @XmlElement(name = "Expiry")
    Expiry Expiry;

    @XmlElement(name = "Verification")
    String Verification;

    @XmlAttribute(name = "type")
    String type;

    public String getBank()
    {
        return Bank;
    }

    public void setBank(String bank)
    {
        Bank = bank;
    }

    public String getCountry()
    {
        return Country;
    }

    public void setCountry(String country)
    {
        Country = country;
    }

    public String getNumber()
    {
        return Number;
    }

    public void setNumber(String number)
    {
        Number = number;
    }

    public String getHolder()
    {
        return Holder;
    }

    public void setHolder(String holder)
    {
        Holder = holder;
    }

    public String getIBAN()
    {
        return IBAN;
    }

    public void setIBAN(String IBAN)
    {
        this.IBAN = IBAN;
    }

    public String getBIC()
    {
        return BIC;
    }

    public void setBIC(String BIC)
    {
        this.BIC = BIC;
    }

    public String getBankName()
    {
        return BankName;
    }

    public void setBankName(String bankName)
    {
        BankName = bankName;
    }

    public String getBrand()
    {
        return Brand;
    }

    public void setBrand(String brand)
    {
        Brand = brand;
    }

    public Expiry getExpiry()
    {
        return Expiry;
    }

    public void setExpiry(Expiry expiry)
    {
        Expiry = expiry;
    }

    public String getVerification()
    {
        return Verification;
    }

    public void setVerification(String verification)
    {
        Verification = verification;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
