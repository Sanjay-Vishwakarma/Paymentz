package com.payment.p4.vos.transactionBlock.processingBlock.historyBlock;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Admin on 20/10/2015.
 */
@XmlRootElement(name = "History")
@XmlAccessorType(XmlAccessType.FIELD)
public class History
{
    @XmlElement(name = "State")
    List State;

    @XmlPath("State/@modified")
    String modified;

    //For Query
    @XmlElement(name = "Status")
    String Status;

    @XmlPath("Status/@modified")
    String modified1;

    public List getState()
    {
        return State;
    }

    public void setState(List state)
    {
        State = state;
    }

    public String getModified()
    {
        return modified;
    }

    public void setModified(String modified)
    {
        this.modified = modified;
    }

    public String getStatus()
    {
        return Status;
    }

    public void setStatus(String status)
    {
        Status = status;
    }

    public String getModified1()
    {
        return modified1;
    }

    public void setModified1(String modified1)
    {
        this.modified1 = modified1;
    }
}
