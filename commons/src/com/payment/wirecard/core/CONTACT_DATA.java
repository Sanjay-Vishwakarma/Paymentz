package com.payment.wirecard.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/27/13
 * Time: 5:14 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("CONTACT_DATA")
public class CONTACT_DATA
{
    @XStreamAlias("IPAddress")
    String IPAddress;

    public String getIPAddress()
    {
        return IPAddress;
    }

    public void setIPAddress(String IPAddress)
    {
        this.IPAddress = IPAddress;
    }
}
