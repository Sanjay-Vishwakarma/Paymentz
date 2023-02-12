package com.transaction.vo.restVO.RequestVO;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Sneha on 2/8/2016.
 */
@XmlRootElement(name="customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class Customer
{
    @FormParam("customer.customerId")
    String customerId;

    @FormParam("customer.givenName")
    String givenName;

    @FormParam("customer.surname")
    String surname;

    @FormParam("customer.sex")
    String sex;

    @FormParam("customer.birthDate")
    String birthDate;

    @FormParam("customer.phone")
    String phone;

    @FormParam("customer.mobile")
    String mobile;

    @FormParam("customer.email")
    String email;

    @FormParam("customer.companyName")
    String companyName;

    @FormParam("customer.ip")
    String ip;

    @FormParam("customer.telnocc")
    String telnocc;

    @FormParam("customer.country")
    String country;

    @FormParam("customer.city")
    String city;

    @FormParam("customer.postcode")
    String postcode;

    @FormParam("customer.street")
    String street;

    @FormParam("customer.bankId")
    String customerBankId;

    @FormParam("customer.id")
    String id;

    @FormParam("customer.language")
    String language;

    @FormParam("customer.walletId")
    String walletId;

    @FormParam("customer.walletAmount")
    String walletAmount;

    @FormParam("customer.walletCurrency")
    String walletCurrency;

    @FormParam("customer.smsOtp")
    String smsOtp;

    @FormParam("customer.emailOtp")
    String emailOTP;

    public String getSmsOtp()
    {
        return smsOtp;
    }

    public void setSmsOtp(String smsOtp)
    {
        this.smsOtp = smsOtp;
    }

    public String getEmailOTP()
    {
        return emailOTP;
    }

    public void setEmailOTP(String emailOTP)
    {
        this.emailOTP = emailOTP;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }



    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getTelnocc()
    {
        return telnocc;
    }

    public void setTelnocc(String telnocc)
    {
        this.telnocc = telnocc;
    }

    public String getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
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

    public String getPostcode()
    {
        return postcode;
    }

    public void setPostcode(String postcode)
    {
        this.postcode = postcode;
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

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public String getWalletId()
    {
        return walletId;
    }

    public void setWalletId(String walletId)
    {
        this.walletId = walletId;
    }

    public String getWalletAmount()
    {
        return walletAmount;
    }

    public void setWalletAmount(String walletAmount)
    {
        this.walletAmount = walletAmount;
    }

    public String getWalletCurrency()
    {
        return walletCurrency;
    }

    public void setWalletCurrency(String walletCurrency)
    {
        this.walletCurrency = walletCurrency;
    }
}