package com.vo.requestVOs;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 10/30/2017.
 */

@XmlRootElement(name="merchant")
@XmlAccessorType(XmlAccessType.FIELD)
public class Merchant
{


    @XmlElement(name="username")
    String loginName;

    @XmlElement(name="password")
    String newPassword;

    @XmlElement(name="conPassword")
    String conPassword;

    @XmlElement(name="companyName")
    String companyName;

    @XmlElement(name="website")
    String website;


    @XmlElement(name="contactName")
    String contactName;


    @XmlElement(name="givenName")
    String givenName;

    @XmlElement(name="surname")
    String surname;

    @XmlElement(name="sex")
    String sex;

    @XmlElement(name="email")
    String email;

    @XmlElement(name="phone")
    String phone;

    @XmlElement(name="country")
    String country;

    @XmlElement(name="postcode")
    String postcode;

    @XmlElement(name="birthDate")
    String birthDate;

    @XmlElement(name="mobilecountrycode")
    String mobilecountrycode;

    @XmlElement(name="mobilenumber")
    String mobilenumber;

    @XmlElement(name="otp")
    String otp;

    @XmlElement(name="telcc")
    String telcc;


    public String getTelcc()
    {
        return telcc;
    }

    public void setTelcc(String telcc)
    {
        this.telcc = telcc;
    }

    public String getOtp()
    {
        return otp;
    }

    public void setOtp(String otp)
    {
        this.otp = otp;
    }

    public String getMobilenumber()
    {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber)
    {
        this.mobilenumber = mobilenumber;
    }

    public String getMobilecountrycode()
    {
        return mobilecountrycode;
    }

    public void setMobilecountrycode(String mobilecountrycode)
    {
        this.mobilecountrycode = mobilecountrycode;
    }

    public String getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate(String birthDate)
    {
        this.birthDate = birthDate;
    }

    public String getPostcode()
    {
        return postcode;
    }

    public void setPostcode(String postcode)
    {
        this.postcode = postcode;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getSurname()
    {
        return surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public String getGivenName()
    {
        return givenName;
    }

    public void setGivenName(String givenName)
    {
        this.givenName = givenName;
    }

    public String getContactName()
    {
        return contactName;
    }

    public void setContactName(String contactName)
    {
        this.contactName = contactName;
    }

    public String getWebsite()
    {
        return website;
    }

    public void setWebsite(String website)
    {
        this.website = website;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public String getConPassword()
    {
        return conPassword;
    }

    public void setConPassword(String conPassword)
    {
        this.conPassword = conPassword;
    }

    public String getNewPassword()
    {
        return newPassword;
    }

    public void setNewPassword(String newPassword)
    {
        this.newPassword = newPassword;
    }

    public String getLoginName()
    {
        return loginName;
    }

    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
}
