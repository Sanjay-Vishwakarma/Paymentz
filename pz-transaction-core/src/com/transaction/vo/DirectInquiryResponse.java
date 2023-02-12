package com.transaction.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Admin on 23/6/15.
 */
@XmlRootElement(name="DirectInquiryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DirectInquiryResponse
{
    @XmlElement(name = "trackingId")
    private String trackingId;

    @XmlElement(name = "description")
    private String description;

    @XmlElement(name = "status")
    private String status;

    @XmlElement(name = "statusDescription")
    private String statusDescription;

    @XmlElement(name = "authAmount")
    private String authAmount;

    @XmlElement(name = "capturedAmount")
    private String capturedAmount;

    @XmlElement(name = "directTransactionErrorCodeVOs")
    List<DirectTransactionErrorCode> directTransactionErrorCodeVOs;

    @XmlElement(name = "remark")
    private String remark;

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

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatusDescription()
    {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription)
    {
        this.statusDescription = statusDescription;
    }

    public String getAuthAmount()
    {
        return authAmount;
    }

    public void setAuthAmount(String authAmount)
    {
        this.authAmount = authAmount;
    }

    public String getCapturedAmount()
    {
        return capturedAmount;
    }

    public void setCapturedAmount(String capturedAmount)
    {
        this.capturedAmount = capturedAmount;
    }

    public List<DirectTransactionErrorCode> getDirectTransactionErrorCodeVOs()
    {
        return directTransactionErrorCodeVOs;
    }

    public void setDirectTransactionErrorCodeVOs(List<DirectTransactionErrorCode> directTransactionErrorCodeVOs)
    {
        this.directTransactionErrorCodeVOs = directTransactionErrorCodeVOs;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }
}
