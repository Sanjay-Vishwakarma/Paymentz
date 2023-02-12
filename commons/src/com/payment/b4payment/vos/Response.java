package com.payment.b4payment.vos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 17-01-2017.
 */
@XmlRootElement(name = "Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Response
{
    @XmlElement(name = "end2EndId")
    String end2EndId;

    @XmlElement(name = "message")
    String message;

    public String getEnd2EndId()
    {
        return end2EndId;
    }

    public void setEnd2EndId(String end2EndId)
    {
        this.end2EndId = end2EndId;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
