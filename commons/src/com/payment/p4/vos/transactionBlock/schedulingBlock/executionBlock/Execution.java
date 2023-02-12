package com.payment.p4.vos.transactionBlock.schedulingBlock.executionBlock;

import javax.xml.bind.annotation.*;

/**
 * Created by Niket on 10/2/2015.
 */
@XmlRootElement(name="Execution")
@XmlAccessorType(XmlAccessType.FIELD)
public class Execution
{
    @XmlElement(name = "Hour")
    String Hour;

    @XmlAttribute(name="entity")
    String entity;

    @XmlElement(name = "Day")
    String Day;

    public String getHour()
    {
        return Hour;
    }

    public void setHour(String hour)
    {
        Hour = hour;
    }


    public String getEntity()
    {
        return entity;
    }

    public void setEntity(String entity)
    {
        this.entity = entity;
    }

    public String getDay()
    {
        return Day;
    }

    public void setDay(String day)
    {
        Day = day;
    }
}
