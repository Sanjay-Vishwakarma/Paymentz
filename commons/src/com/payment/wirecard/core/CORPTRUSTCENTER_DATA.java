package com.payment.wirecard.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/27/13
 * Time: 5:27 PM
 * To change this template use File | Settings | File Templates.
 */

@XStreamAlias("CORPTRUSTCENTER_DATA")
public class CORPTRUSTCENTER_DATA
{
    public Address getAddress()
    {
        return address;
    }

    public void setAddress(Address address)
    {
        this.address = address;
    }

    @XStreamAlias("ADDRESS")
    Address address;

    public PERSONINFO getPersoninfo()
    {
        return personinfo;
    }

    public void setPersoninfo(PERSONINFO personinfo)
    {
        this.personinfo = personinfo;
    }

    @XStreamAlias("PERSONINFO")
    PERSONINFO personinfo;
}
