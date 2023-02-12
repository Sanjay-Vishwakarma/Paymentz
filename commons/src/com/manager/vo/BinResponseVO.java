package com.manager.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 6/9/2017.
 */

@XmlRootElement(name="bindetail")
@XmlAccessorType(XmlAccessType.FIELD)
public class BinResponseVO
{

    /*@XmlElement(name="binbaseid")
    String binbaseid;*/

    @XmlElement(name="firstsix")
    String firstsix;

    @XmlElement(name="brand")
    String brand;

    @XmlElement(name="subbrand")
    String subbrand;

    @XmlElement(name="countrycode")
    String countrycode;

    @XmlElement(name="countryname")
    String countryname;

    @XmlElement(name="countrycodeA2")
    String countrycodeA2;

    @XmlElement(name="countrycodeA3")
    String countrycodeA3;

    @XmlElement(name="countryisonumber")
    String countryisonumber;

    @XmlElement(name="bank")
    String bank;

    @XmlElement(name="bankphoneno")
    String bankphoneno;

    @XmlElement(name="bankcity")
    String bankcity;

    @XmlElement(name="bankwebsite")
    String bankwebsite;

    @XmlElement(name="cardtype")
    String cardtype;

    @XmlElement(name="cardcategory")
    String cardcategory;

    @XmlElement(name="latitude")
    String latitude;

    @XmlElement(name="longitude")
    String longitude;

    @XmlElement(name="prepaid")
    String prepaid;

    @XmlElement(name="usagetype")
    String usagetype;

    @XmlElement(name="transtype")
    String transtype;

    @XmlElement(name="AuthToken")
    String AuthToken;

    /*public String getBinbaseid()
    {
        return binbaseid;
    }*/

   /* public void setBinbaseid(String binbaseid)
    {
        this.binbaseid = binbaseid;
    }*/

    public String getFirstsix()
    {
        return firstsix;
    }

    public void setFirstsix(String firstsix)
    {
        this.firstsix = firstsix;
    }

    public String getCountrycodeA2()
    {
        return countrycodeA2;
    }

    public void setCountrycodeA2(String countrycodeA2)
    {
        this.countrycodeA2 = countrycodeA2;
    }

    public String getCountrycodeA3()
    {
        return countrycodeA3;
    }

    public void setCountrycodeA3(String countrycodeA3)
    {
        this.countrycodeA3 = countrycodeA3;
    }

    public String getCountryisonumber()
    {
        return countryisonumber;
    }

    public void setCountryisonumber(String countryisonumber)
    {
        this.countryisonumber = countryisonumber;
    }

    public String getLatitude()
    {
        return latitude;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

    public String getPrepaid()
    {
        return prepaid;
    }

    public void setPrepaid(String prepaid)
    {
        this.prepaid = prepaid;
    }

    public String getBrand()
    {
        return brand;
    }

    public void setBrand(String brand)
    {
        this.brand = brand;
    }

    public String getSubbrand()
    {
        return subbrand;
    }

    public void setSubbrand(String subbrand)
    {
        this.subbrand = subbrand;
    }

    public String getCountrycode()
    {
        return countrycode;
    }

    public void setCountrycode(String countrycode)
    {
        this.countrycode = countrycode;
    }

    public String getCountryname()
    {
        return countryname;
    }

    public void setCountryname(String countryname)
    {
        this.countryname = countryname;
    }

    public String getBank()
    {
        return bank;
    }

    public void setBank(String bank)
    {
        this.bank = bank;
    }

    public String getBankphoneno()
    {
        return bankphoneno;
    }

    public void setBankphoneno(String bankphoneno)
    {
        this.bankphoneno = bankphoneno;
    }

    public String getBankcity()
    {
        return bankcity;
    }

    public void setBankcity(String bankcity)
    {
        this.bankcity = bankcity;
    }

    public String getBankwebsite()
    {
        return bankwebsite;
    }

    public void setBankwebsite(String bankwebsite)
    {
        this.bankwebsite = bankwebsite;
    }

    public String getCardtype()
    {
        return cardtype;
    }

    public void setCardtype(String cardtype)
    {
        this.cardtype = cardtype;
    }

    public String getCardcategory()
    {
        return cardcategory;
    }

    public void setCardcategory(String cardcategory)
    {
        this.cardcategory = cardcategory;
    }

    public String getUsagetype()
    {
        return usagetype;
    }

    public void setUsagetype(String usagetype)
    {
        this.usagetype = usagetype;
    }

    public String getTranstype()
    {
        return transtype;
    }

    public void setTranstype(String transtype)
    {
        this.transtype = transtype;
    }

    public String getAuthToken()
    {
        return AuthToken;
    }

    public void setAuthToken(String authToken)
    {
        AuthToken = authToken;
    }
}
