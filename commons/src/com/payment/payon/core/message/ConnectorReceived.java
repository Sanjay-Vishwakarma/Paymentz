package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 11:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConnectorReceived
{
    @XStreamAsAttribute
    private String timestamp;
    @XStreamAlias("Returned")
    private Returned returned;
    @XStreamAlias("Body")
    private String body;

    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public Returned getReturned()
    {
        return returned;
    }

    public void setReturned(Returned returned)
    {
        this.returned = returned;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }
}
