package com.payment.npayon;

/**
 * Created by Admin on 25-Jun-18.
 */
public class ThreeDSecure
{
    String eci;
    String verificationId;
    String xid;
    String paRes;

    public String getEci()
    {
        return eci;
    }

    public void setEci(String eci)
    {
        this.eci = eci;
    }

    public String getVerificationId()
    {
        return verificationId;
    }

    public void setVerificationId(String verificationId)
    {
        this.verificationId = verificationId;
    }

    public String getXid()
    {
        return xid;
    }

    public void setXid(String xid)
    {
        this.xid = xid;
    }

    public String getPaRes()
    {
        return paRes;
    }

    public void setPaRes(String paRes)
    {
        this.paRes = paRes;
    }
}
