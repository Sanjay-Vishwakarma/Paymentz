package com.payment.p4.vos.transactionBlock.customerBlock.contactBlock;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 10/2/2015.
 */

@XmlRootElement(name = "Contact")
@XmlAccessorType(XmlAccessType.FIELD)
public class Contact
{
    @XmlElement(name="Email")
    String Email;
    @XmlElement(name="Ip")
    String Ip;
    @XmlElement(name="Phone")
    String Phone;

    public String getEmail()
    {
        return Email;
    }

    public void setEmail(String email)
    {
        Email = email;
    }

    public String getIp()
    {
        return Ip;
    }

    public void setIp(String ip)
    {
        Ip = ip;
    }

    public String getPhone()
    {
        return Phone;
    }

    public void setPhone(String phone)
    {
        Phone = phone;
    }
}
