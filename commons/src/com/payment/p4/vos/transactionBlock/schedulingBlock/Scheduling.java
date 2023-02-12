package com.payment.p4.vos.transactionBlock.schedulingBlock;

import com.payment.p4.vos.transactionBlock.schedulingBlock.executionBlock.Execution;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Niket on 10/2/2015.
 */

@XmlRootElement(name="Scheduling")
@XmlAccessorType(XmlAccessType.FIELD)
public class Scheduling
{
    @XmlElement(name = "StartDate")
    String StartDate;

    @XmlElement(name = "EndDate")
    String EndDate;

    @XmlElement(name = "")
    Execution Execution;

    public String getStartDate()
    {
        return StartDate;
    }

    public void setStartDate(String startDate)
    {
        StartDate = startDate;
    }

    public String getEndDate()
    {
        return EndDate;
    }

    public void setEndDate(String endDate)
    {
        EndDate = endDate;
    }

    public Execution getExecution()
    {
        return Execution;
    }

    public void setExecution(Execution execution)
    {
        Execution = execution;
    }
}
