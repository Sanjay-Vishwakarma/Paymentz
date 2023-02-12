package com.merchant.vo.responseVOs;

import com.manager.vo.AsyncParameterVO;
import com.manager.vo.TerminalVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Admin on 4/28/2017.
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

    @XmlElement(name="address")
    String address;

    @XmlElement(name="city")
    String city;

    @XmlElement(name="state")
    String state;

    @XmlElement(name="mobilenumber")
    String mobilenumber;

    @XmlElement(name="zip")
    String zip;

    @XmlElement(name="email")
    String email;

    @XmlElement(name = "merchantThemeList")
    List<AsyncParameterVO> merchantThemeList;

     @XmlElement(name = "memberTerminalList")
     List<TerminalVO> memberTerminalList;



    public List<AsyncParameterVO> getMerchantThemeList()
    {
        return merchantThemeList;
    }

    public void setMerchantThemeList(List<AsyncParameterVO> merchantThemeList)
    {
        this.merchantThemeList = merchantThemeList;
    }

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


    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
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

    public List<TerminalVO> getMemberTerminalList()
    {
        return memberTerminalList;
    }

    public void setMemberTerminalList(List<TerminalVO> memberTerminalList)
    {
        this.memberTerminalList = memberTerminalList;
    }
}
