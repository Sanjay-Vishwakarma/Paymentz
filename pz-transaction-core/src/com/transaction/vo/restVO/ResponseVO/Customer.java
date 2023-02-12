package com.transaction.vo.restVO.ResponseVO;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 11/3/2017.
 */
@XmlRootElement(name="customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class Customer
{
    @XmlElement(name="givenName")
    String givenName;

    @XmlElement(name="surname")
    String surname;

    @XmlElement(name="sex")
    String sex;

    @XmlElement(name="birthDate")
    String birthDate;

    @XmlElement(name="phone")
    String phone;

    @XmlElement(name="mobile")
    String mobile;

    @XmlElement(name="email")
    String email;

    @XmlElement(name="companyName")
    String companyName;

    @XmlElement(name="ip")
    String ip;

    @XmlElement(name="telnocc")
    String telnocc;

    @XmlElement(name="country")
    String country;

    @XmlElement(name="city")
    String city;

    @XmlElement(name="postcode")
    String postcode;

    @XmlElement(name="street")
    String street;

    @XmlElement(name="bankId")
    String customerBankId;

    @XmlElement(name="id")
    String id;

    @XmlElement(name="isMobileNoVerified")
    String isMobileNoVerified;

    @XmlElement(name="isEmailVerified")
    String isEmailVerified;

    public String getGivenName()
    {
        return givenName;
    }

    public void setGivenName(String givenName)
    {
        this.givenName = givenName;
    }

    public String getSurname()
    {
        return surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate(String birthDate)
    {
        this.birthDate = birthDate;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getTelnocc()
    {
        return telnocc;
    }

    public void setTelnocc(String telnocc)
    {
        this.telnocc = telnocc;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getPostcode()
    {
        return postcode;
    }

    public void setPostcode(String postcode)
    {
        this.postcode = postcode;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getCustomerBankId()
    {
        return customerBankId;
    }

    public void setCustomerBankId(String customerBankId)
    {
        this.customerBankId = customerBankId;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getIsMobileNoVerified()
    {
        return isMobileNoVerified;
    }

    public void setIsMobileNoVerified(String isMobileNoVerified)
    {
        this.isMobileNoVerified = isMobileNoVerified;
    }

    public String getIsEmailVerified()
    {
        return isEmailVerified;
    }

    public void setIsEmailVerified(String isEmailVerified)
    {
        this.isEmailVerified = isEmailVerified;
    }
}
