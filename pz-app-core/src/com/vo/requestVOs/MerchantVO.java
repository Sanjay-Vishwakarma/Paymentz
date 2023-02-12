package com.vo.requestVOs;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 10/17/2017.
 */
@XmlRootElement(name="merchant")
@XmlAccessorType(XmlAccessType.FIELD)
public class MerchantVO
{
    @FormParam("merchant.username")
    String loginName;

    @FormParam("merchant.password")
    String newPassword;

    @FormParam("merchant.conPassword")
    String conPassword;

    @FormParam("merchant.companyName")
    String companyName;

    @FormParam("merchant.website")
    String website;

    @FormParam("merchant.contactName")
    String contactName;

    @FormParam("merchant.givenName")
    String givenName;

    @FormParam("merchant.surname")
    String surname;

    @FormParam("merchant.sex")
    String sex;

    @FormParam("merchant.email")
    String email;

    @FormParam("merchant.phone")
    String phone;

    @FormParam("merchant.country")
    String country;

    @FormParam("merchant.postcode")
    String postcode;

    @FormParam("merchant.birthDate")
    String birthDate;

    @FormParam("merchant.mobilecountrycode")
    String mobilecountrycode;

    @FormParam("merchant.mobilenumber")
    String mobilenumber;

    @FormParam("merchant.otp")
    String otp;

    @FormParam("merchant.telcc")
    String telcc;


    public String getLoginName()
    {
        return loginName;
    }

    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    public String getNewPassword()
    {
        return newPassword;
    }

    public void setNewPassword(String newPassword)
    {
        this.newPassword = newPassword;
    }

    public String getConPassword()
    {
        return conPassword;
    }

    public void setConPassword(String conPassword)
    {
        this.conPassword = conPassword;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public String getWebsite()
    {
        return website;
    }

    public void setWebsite(String website)
    {
        this.website = website;
    }

    public String getContactName()
    {
        return contactName;
    }

    public void setContactName(String contactName)
    {
        this.contactName = contactName;
    }

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

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getPostcode()
    {
        return postcode;
    }

    public void setPostcode(String postcode)
    {
        this.postcode = postcode;
    }

    public String getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate(String birthDate)
    {
        this.birthDate = birthDate;
    }

    public String getMobilecountrycode()
    {
        return mobilecountrycode;
    }

    public void setMobilecountrycode(String mobilecountrycode)
    {
        this.mobilecountrycode = mobilecountrycode;
    }

    public String getMobilenumber()
    {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber)
    {
        this.mobilenumber = mobilenumber;
    }

    public String getOtp()
    {
        return otp;
    }

    public void setOtp(String otp)
    {
        this.otp = otp;
    }

    public String getTelcc()
    {
        return telcc;
    }

    public void setTelcc(String telcc)
    {
        this.telcc = telcc;
    }
}
