package com.merchant.vo.requestVOs;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 10-07-2017.
 */
@XmlRootElement(name="customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerVO
{


    @XmlElement(name="customerId")
    String customerId;


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


    @XmlElement(name="status")
    String status;


    @XmlElement(name="middleName")
    String middleName;


    @XmlElement(name="workPhone")
    String workPhone;


    @XmlElement(name="telnocc")
    String telnocc;


    @XmlElement(name="country")
    String country;


    @XmlElement(name="postcode")
    String postcode;

    @XmlElement(name="city")
    String city;

    @XmlElement(name="street")
    String street;

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
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

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getMiddleName()
    {
        return middleName;
    }

    public void setMiddleName(String middleName)
    {
        this.middleName = middleName;
    }

    public String getWorkPhone()
    {
        return workPhone;
    }

    public void setWorkPhone(String workPhone)
    {
        this.workPhone = workPhone;
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

    public String getPostcode()
    {
        return postcode;
    }

    public void setPostcode(String postcode)
    {
        this.postcode = postcode;
    }
}
