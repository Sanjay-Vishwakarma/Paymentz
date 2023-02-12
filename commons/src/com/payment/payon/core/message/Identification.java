package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 8:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class Identification
{
    @XStreamAlias("ShortID")
    private String shortID;
    private String UUID;
    private String orderId;
    private String shopperId;


    public String getShortID()
    {
        return shortID;
    }

    public String getUUID()
    {
        return UUID;
    }

    public String getOrderId()
    {
        return orderId;
    }



    public String getShopperId()
    {
        return shopperId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public void setShopperId(String shopperId)
    {
        this.shopperId = shopperId;
    }

    public void setShortID(String shortID)
    {
        this.shortID = shortID;
    }

    public void setUUID(String UUID)
    {
        this.UUID = UUID;
    }
}
