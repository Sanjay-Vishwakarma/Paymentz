package com.transaction.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 23/6/15.
 */
@XmlRootElement(name = "DirectInquiryRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class DirectInquiryRequest
{
    @XmlElement(name = "toId",required = true)
    private String toId;

    @XmlElement(name = "checkSum",required = true)
    private String checkSum;

    @XmlElement(name = "trackingId",required = false)
    private String trackingId;

    @XmlElement(name = "description",required = false)
    private String description;

    public String getToId()
    {
        return toId;
    }

    public void setToId(String toId)
    {
        this.toId = toId;
    }

    public String getCheckSum()
    {
        return checkSum;
    }

    public void setCheckSum(String checkSum)
    {
        this.checkSum = checkSum;
    }

    public String getTrackingId()
    {
        return trackingId;
    }

    public void setTrackingId(String trackingId)
    {
        this.trackingId = trackingId;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
