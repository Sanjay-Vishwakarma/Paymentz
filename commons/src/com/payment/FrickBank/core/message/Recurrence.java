package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/1/13
 * Time: 5:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class Recurrence
{
    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    @XStreamAsAttribute
    private String mode="";
}
