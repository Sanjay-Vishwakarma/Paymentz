package com.vo.applicationManagerVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Swamy on 8/9/2017.
 */
@XmlRootElement(name="AddressIdentificationVO")
@XmlAccessorType(XmlAccessType.FIELD)
public class AddressIdentificationVO
{

    @XmlElement(name="applicationId")
    String applicationId;

    @XmlElement(name="address")
    String address;

    @XmlElement(name="city")
    String city;

    @XmlElement(name="state")
    String state;

    @XmlElement(name="street")
    String street;

    @XmlElement(name="zipcode")
    String zipcode;

    @XmlElement(name="country")
    String country;

    @XmlElement(name="addressId")
    String addressId;

    @XmlElement(name="addressProof")
    String addressProof;

    @XmlElement(name="vatidentification")
    String vatidentification;

    @XmlElement(name="federalTaxId")
    String federalTaxId;

    @XmlElement(name="identificationtypeselect")
    String identificationtypeselect;

    @XmlElement(name="identificationtype")
    String identificationtype;

    @XmlElement(name="nationality")
    String nationality;

    @XmlElement(name="passportissuedate")
    String passportissuedate;

    @XmlElement(name="passportexpirydate")
    String passportexpirydate;

    @XmlElement(name="type")
    String type;

    @XmlElement(name="company_name")
    String company_name;

    @XmlElement(name="registration_number")
    String registration_number;

    @XmlElement(name="date_of_registration")
    String date_of_registration;

    @XmlElement(name="phone_cc")
    String phone_cc;

    @XmlElement(name="phone_number")
    String phone_number;

    @XmlElement(name="fax")
    String fax;

    @XmlElement(name="email_id")
    String email_id;

    @XmlElement(name="registred_directors")
    String registred_directors;


    public String getApplicationId()
    {
        return applicationId;
    }

    public void setApplicationId(String applicationId)
    {
        this.applicationId = applicationId;
    }

    public String getVatidentification()
    {
        return vatidentification;
    }

    public void setVatidentification(String vatidentification)
    {
        this.vatidentification = vatidentification;
    }

    public String getFederalTaxId()
    {
        return federalTaxId;
    }

    public void setFederalTaxId(String federalTaxId)
    {
        this.federalTaxId = federalTaxId;
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

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getZipcode()
    {
        return zipcode;
    }

    public void setZipcode(String zipcode)
    {
        this.zipcode = zipcode;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getAddressId()
    {
        return addressId;
    }

    public void setAddressId(String addressId)
    {
        this.addressId = addressId;
    }

    public String getAddressProof()
    {
        return addressProof;
    }

    public void setAddressProof(String addressProof)
    {
        this.addressProof = addressProof;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getIdentificationtypeselect()
    {
        return identificationtypeselect;
    }

    public void setIdentificationtypeselect(String identificationtypeselect)
    {
        this.identificationtypeselect = identificationtypeselect;
    }

    public String getIdentificationtype()
    {
        return identificationtype;
    }

    public void setIdentificationtype(String identificationtype)
    {
        this.identificationtype = identificationtype;
    }

    public String getNationality()
    {
        return nationality;
    }

    public void setNationality(String nationality)
    {
        this.nationality = nationality;
    }

    public String getPassportissuedate()
    {
        return passportissuedate;
    }

    public void setPassportissuedate(String passportissuedate)
    {
        this.passportissuedate = passportissuedate;
    }

    public String getPassportexpirydate()
    {
        return passportexpirydate;
    }

    public void setPassportexpirydate(String passportexpirydate)
    {
        this.passportexpirydate = passportexpirydate;
    }

    public String getCompany_name()
    {
        return company_name;
    }

    public void setCompany_name(String company_name)
    {
        this.company_name = company_name;
    }

    public String getRegistration_number()
    {
        return registration_number;
    }

    public void setRegistration_number(String registration_number)
    {
        this.registration_number = registration_number;
    }

    public String getDate_of_registration()
    {
        return date_of_registration;
    }

    public void setDate_of_registration(String date_of_registration)
    {
        this.date_of_registration = date_of_registration;
    }

    public String getPhone_cc()
    {
        return phone_cc;
    }

    public void setPhone_cc(String phone_cc)
    {
        this.phone_cc = phone_cc;
    }

    public String getPhone_number()
    {
        return phone_number;
    }

    public void setPhone_number(String phone_number)
    {
        this.phone_number = phone_number;
    }

    public String getFax()
    {
        return fax;
    }

    public void setFax(String fax)
    {
        this.fax = fax;
    }

    public String getEmail_id()
    {
        return email_id;
    }

    public void setEmail_id(String email_id)
    {
        this.email_id = email_id;
    }

    public String getRegistred_directors()
    {
        return registred_directors;
    }

    public void setRegistred_directors(String registred_directors)
    {
        this.registred_directors = registred_directors;
    }
}
