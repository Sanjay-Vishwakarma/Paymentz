package com.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Admin on 10/23/2017.
 */
@XmlRootElement(name="result")
@XmlAccessorType(XmlAccessType.FIELD)
public class Result
{
    @XmlElement(name="code")
    String code;

    @XmlElement(name="description")
    String description;

    @XmlElement(name="currency")
    String currency;

    @XmlElement(name="currencyList")
    List<String> currencyList;

    @XmlElement(name="mobilecc")
    String mobilecc;

    @XmlElement(name="mobilenumber")
    String mobilenumber;

    @XmlElement(name="LoginName")
    String LoginName;

    public String getMobilecc()
    {
        return mobilecc;
    }

    public void setMobilecc(String mobilecc)
    {
        this.mobilecc = mobilecc;
    }

    public String getMobilenumber()
    {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber)
    {
        this.mobilenumber = mobilenumber;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    @XmlElement(name="country")
    String country;

    public String getResultCode()
    {
        return code;
    }

    public void setResultCode(String resultCode)
    {
        this.code = resultCode;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public List getCurrencyList()
    {
        return currencyList;
    }

    public void setCurrencyList(List currencyList)
    {
        this.currencyList = currencyList;
    }

    public String getLoginName()
    {
        return LoginName;
    }

    public void setLoginName(String loginName)
    {
        LoginName = loginName;
    }
}


