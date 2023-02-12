package com.payment.beekash.vos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 2/20/2019.
 */
@XmlRootElement(name="customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerVO
{
    @XmlElement(name = "first_name")
    String first_name;

    @XmlElement(name = "last_name")
    String last_name;

    @XmlElement(name = "email_id")
    String email_id;

    @XmlElement(name = "phone_number")
    String phone_number;

    @XmlElement(name = "address")
    String address;

    @XmlElement(name = "city")
    String city;

    @XmlElement(name = "state")
    String state;

    @XmlElement(name = "country_code")
    String country_code;

    @XmlElement(name = "zip_code")
    String zip_code;

    public String getFirst_name() {return first_name;}
    public void setFirst_name(String first_name) {this.first_name = first_name;}

    public String getLast_name() {return last_name;}
    public void setLast_name(String last_name) {this.last_name = last_name;}

    public String getEmail_id() {return email_id;}
    public void setEmail_id(String email_id) {this.email_id = email_id;}

    public String getPhone_number() {return phone_number;}
    public void setPhone_number(String phone_number) {this.phone_number = phone_number;}

    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}

    public String getCity() {return city;}
    public void setCity(String city) {this.city = city;}

    public String getState() {return state;}
    public void setState(String state) {this.state = state;}

    public String getCountry_code() {return country_code;}
    public void setCountry_code(String country_code) {this.country_code = country_code;}

    public String getZip_code() {return zip_code;}
    public void setZip_code(String zip_code) {this.zip_code = zip_code;}
}
