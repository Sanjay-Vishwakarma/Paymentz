package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 11:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class VirtualAccount

{
    public String getBrand()
    {
        return brand;
    }

    public String getID()
    {
        return ID;
    }

    @XStreamAlias("Brand")
    private String brand;

    public void setBrand(String brand)
    {
        this.brand = brand;
    }

    public void setID(String ID)
    {
        this.ID = ID;
    }

    @XStreamAlias("Id")
    private String ID;

}
