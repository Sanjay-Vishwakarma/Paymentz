package com.payment.p4.vos.transactionBlock.eventsBlock;

import com.payment.p4.vos.transactionBlock.eventsBlock.eventBlock.Event;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Admin on 4/11/2015.
 */
@XmlRootElement(name = "Events")
@XmlAccessorType(XmlAccessType.FIELD)
public class Events
{
    @XmlElement(name = "Event")
    List<Event> Event;

    public List<Event> getEvent()
    {
        return Event;
    }

    public void setEvent(List<Event> event)
    {
        Event = event;
    }
}
