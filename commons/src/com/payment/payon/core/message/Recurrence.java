package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */

public class Recurrence
{

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    @XStreamAsAttribute
    private String mode;

    public String getMode()
    {
        return mode;
    }
}
