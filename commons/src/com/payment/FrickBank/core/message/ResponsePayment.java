package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/21/13
 * Time: 5:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResponsePayment
{
    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    @XStreamAsAttribute
    private String code="";

    public Clearing getClearing()
    {
        return clearing;
    }

    public void setClearing(Clearing clearing)
    {
        this.clearing = clearing;
    }

    @XStreamAlias("Clearing")
    private Clearing clearing;
}
