package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 8:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class Transaction
{
    public String getEntity()
    {
        return entity;
    }

    public void setEntity(String entity)
    {
        this.entity = entity;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    @XStreamAsAttribute
    private String entity;

    @XStreamAsAttribute
    private String mode="TEST";

    @XStreamAlias("Identification")
    private Identification identification =new Identification();


    public void setIdentification(Identification identification)
    {
        this.identification = identification;
    }
}
