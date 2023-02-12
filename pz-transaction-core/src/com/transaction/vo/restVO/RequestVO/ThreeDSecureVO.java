package com.transaction.vo.restVO.RequestVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Sneha on 2/8/2016.
 */
@XmlRootElement(name="threeDSecure")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThreeDSecureVO
{
    @XmlElement(name="eci")
    String eci;

    @XmlElement(name="verificationId")
    String verificationId;

    @XmlElement(name="xid")
    String xid;

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
}
