package com.payment.neteller.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sneha on 3/6/2017.
 */
public class NotificationResponse
{
    private String id;
    private String key;
    private String mode;
    private String eventDate;
    private String eventType;
    private String attemptNumber;
    private List<Links> links = new ArrayList<Links>();

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public String getEventDate()
    {
        return eventDate;
    }

    public void setEventDate(String eventDate)
    {
        this.eventDate = eventDate;
    }

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public String getAttemptNumber()
    {
        return attemptNumber;
    }

    public void setAttemptNumber(String attemptNumber)
    {
        this.attemptNumber = attemptNumber;
    }

    public List<Links> getLinks()
    {
        return links;
    }

    public void setLinks(List<Links> links)
    {
        this.links = links;
    }
}
