package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="fraudDetermined")
@XmlAccessorType(XmlAccessType.FIELD)
public class FraudDetermined
{
    @XmlElement(name="refund")
    private String refund;

    @XmlElement(name="blacklist")
    private String blacklist;

    public String getRefund()
    {
        return refund;
    }

    public void setRefund(String refund)
    {
        this.refund = refund;
    }

    public String getBlacklist()
    {
        return blacklist;
    }

    public void setBlacklist(String blacklist)
    {
        this.blacklist = blacklist;
    }

    @Override
    public String toString()
    {
        return "FraudDetermined{" +
                "refund='" + refund + '\'' +
                ", blacklist='" + blacklist + '\'' +
                '}';
    }
}
