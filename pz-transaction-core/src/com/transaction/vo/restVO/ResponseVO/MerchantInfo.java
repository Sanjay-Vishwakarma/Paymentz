package com.transaction.vo.restVO.ResponseVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 4/18/2020.
 */

@XmlRootElement(name = "merchantinfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class MerchantInfo
{

    @XmlElement(name="acquirer_bin")
    String acquirer_bin;

    @XmlElement(name="address")
    String address;

    @XmlElement(name="bank_mid")
    String bank_mid;

    @XmlElement(name="bank_tid")
    String bank_tid;

    @XmlElement(name="card_acceptor_id")
    String card_acceptor_id;

    @XmlElement(name="descriptor")
    String descriptor;

    @XmlElement(name="email")
    String email;

    @XmlElement(name="city")
    String city;

    @XmlElement(name="country")
    String country;

    @XmlElement(name="is_test")
    String is_test;

    public String getRegion()
    {
        return region;
    }

    public void setRegion(String region)
    {
        this.region = region;
    }

    @XmlElement(name="region")

    String region;

    @XmlElement(name="name")
    String name;

    @XmlElement(name="status")
    String status;

    public String getAcquirer_bin()
    {
        return acquirer_bin;
    }

    public void setAcquirer_bin(String acquirer_bin)
    {
        this.acquirer_bin = acquirer_bin;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getBank_mid()
    {
        return bank_mid;
    }

    public void setBank_mid(String bank_mid)
    {
        this.bank_mid = bank_mid;
    }

    public String getBank_tid()
    {
        return bank_tid;
    }

    public void setBank_tid(String bank_tid)
    {
        this.bank_tid = bank_tid;
    }

    public String getCard_acceptor_id()
    {
        return card_acceptor_id;
    }

    public void setCard_acceptor_id(String card_acceptor_id)
    {
        this.card_acceptor_id = card_acceptor_id;
    }

    public String getDescriptor()
    {
        return descriptor;
    }

    public void setDescriptor(String descriptor)
    {
        this.descriptor = descriptor;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
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

    public String getIs_test()
    {
        return is_test;
    }

    public void setIs_test(String is_test)
    {
        this.is_test = is_test;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
