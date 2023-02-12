package com.transaction.vo;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: NIKET
 * Date: 6/4/15
 * Time: 4:23 PM
 * To change this template use File | Settings | File Templates.
 */
/*@XmlType(name = "DirectCancelResponse", propOrder = {"trackingId","description","status","statusDescription","newCheckSum","directTransactionErrorCodeVOs"})*/
@XmlRootElement(name="DirectCancelResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DirectCancelResponse
{
    @XmlElement(name = "trackingId")
    private String trackingId;

    @XmlElement(name = "description")
    private String description;

    @XmlElement(name = "status")
    private String status;

    @XmlElement(name = "statusDescription")
    private String statusDescription;

    @XmlElement(name = "newCheckSum")
    private String newCheckSum;

    @XmlElement(name = "directTransactionErrorCodeVOs")
    List<DirectTransactionErrorCode> directTransactionErrorCodeVOs;

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

    public String getNewCheckSum()
    {
        return newCheckSum;
    }

    public void setNewCheckSum(String newCheckSum)
    {
        this.newCheckSum = newCheckSum;
    }

    public List<DirectTransactionErrorCode> getDirectTransactionErrorCodeVOs()
    {
        return directTransactionErrorCodeVOs;
    }

    public void setDirectTransactionErrorCodeVOs(List<DirectTransactionErrorCode> directTransactionErrorCodeVOs)
    {
        this.directTransactionErrorCodeVOs = directTransactionErrorCodeVOs;
    }
}
