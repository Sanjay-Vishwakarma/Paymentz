package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 8:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class Authentication
{
    public String getEci()
    {
        return eci;
    }

    public String getVerification()
    {
        return verification;
    }

    public String getXid()
    {
        return xid;
    }

    @XStreamAlias("Eci")
    private String eci;
    @XStreamAlias("Verification")
    private String verification;
    @XStreamAlias("Xid")
    private String xid;

    public void setEci(String eci)
    {
        this.eci = eci;
    }

    public void setVerification(String verification)
    {
        this.verification = verification;
    }

    public void setXid(String xid)
    {
        this.xid = xid;
    }
}
