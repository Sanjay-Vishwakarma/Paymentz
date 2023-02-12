package com.payment.b4payment.vos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 02-01-2017.
 */
@XmlRootElement(name="MandateRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class MandateRequest
{
    @XmlElement(name ="emailAddress")
    String emailAddress;

    @XmlElement(name ="languageCode")
    String languageCode;

    @XmlElement(name ="b4pId")
    String b4pId;


    public String getEmailAddress()
    {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    public String getLanguageCode()
    {
        return languageCode;
    }

    public void setLanguageCode(String languageCode)
    {
        this.languageCode = languageCode;
    }

    public String getB4pId()
    {
        return b4pId;
    }

    public void setB4pId(String b4pId)
    {
        this.b4pId = b4pId;
    }
}
