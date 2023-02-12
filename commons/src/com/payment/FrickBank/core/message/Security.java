package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/21/13
 * Time: 1:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class Security
{
    public String getSender()
    {
        return sender;
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    @XStreamAsAttribute
    private String sender;
}
