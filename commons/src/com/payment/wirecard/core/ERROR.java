package com.payment.wirecard.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/31/13
 * Time: 12:35 AM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("ERROR")
public class ERROR
{
    @XStreamAlias("Type")
    String type;

    @XStreamAlias("Number")
    String number;

    @XStreamAlias("Message")
    String message;

    @XStreamAlias("Advice")
    String advice;

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getNumber()
    {
        return number;
    }

    public void setNumber(String number)
    {
        this.number = number;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getAdvice()
    {
        return advice;
    }

    public void setAdvice(String advice)
    {
        this.advice = advice;
    }
}
