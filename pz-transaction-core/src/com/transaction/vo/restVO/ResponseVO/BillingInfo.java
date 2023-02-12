package com.transaction.vo.restVO.ResponseVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 4/18/2020.
 */
@XmlRootElement(name = "billinginfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillingInfo
{
    @XmlElement(name="bill_address")
    String bill_address;

    @XmlElement(name="bill_city")
    String bill_city;

    @XmlElement(name="bill_country")
    String bill_country;

    @XmlElement(name="bill_first_name")
    String bill_first_name;

    @XmlElement(name="bill_last_name")
    String bill_last_name;

    @XmlElement(name="bill_region")
    String bill_region;

    @XmlElement(name="bill_zip")
    String bill_zip;

    public String getBill_address()
    {
        return bill_address;
    }

    public void setBill_address(String bill_address)
    {
        this.bill_address = bill_address;
    }

    public String getBill_city()
    {
        return bill_city;
    }

    public void setBill_city(String bill_city)
    {
        this.bill_city = bill_city;
    }

    public String getBill_country()
    {
        return bill_country;
    }

    public void setBill_country(String bill_country)
    {
        this.bill_country = bill_country;
    }

    public String getBill_first_name()
    {
        return bill_first_name;
    }

    public void setBill_first_name(String bill_first_name)
    {
        this.bill_first_name = bill_first_name;
    }

    public String getBill_last_name()
    {
        return bill_last_name;
    }

    public void setBill_last_name(String bill_last_name)
    {
        this.bill_last_name = bill_last_name;
    }

    public String getBill_region()
    {
        return bill_region;
    }

    public void setBill_region(String bill_region)
    {
        this.bill_region = bill_region;
    }

    public String getBill_zip()
    {
        return bill_zip;
    }

    public void setBill_zip(String bill_zip)
    {
        this.bill_zip = bill_zip;
    }
}
