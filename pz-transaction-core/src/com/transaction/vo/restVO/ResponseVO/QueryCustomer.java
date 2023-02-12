package com.transaction.vo.restVO.ResponseVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 4/20/2020.
 */

@XmlRootElement(name="Customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryCustomer
{

    @XmlElement(name="email")
    String email;

    @XmlElement(name="ip")
    String ip;

    @XmlElement(name="phone")
    String phone;

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }
}
