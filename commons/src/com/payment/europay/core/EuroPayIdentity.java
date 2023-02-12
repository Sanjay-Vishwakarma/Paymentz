package com.payment.europay.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/5/13
 * Time: 1:40 AM
 * To change this template use File | Settings | File Templates.
 */

@XStreamAlias("identity")
public class EuroPayIdentity
{
    @XStreamAlias("ip")
    @XStreamAsAttribute
    String ip ;

    @XStreamAlias("email")
    @XStreamAsAttribute
    String email;

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

}
