package com.payment.payvision.core.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 1/9/18.
 */
@XmlRootElement(name="RetrieveTransactionResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class Inquiry
{
    @XmlElement(name="memberId",required=false)
    private long memberId;

    @XmlElement(name="memberGuid", required=false)
    private String memberGuid;

    @XmlElement(name="trackingMemberCode",required = false)
    private String trackingMemberCode;

    @XmlElement(name="transactionDate" ,required = false)
    private String transactionDate;

    public long getMemberId()
    {
        return memberId;
    }

    public void setMemberId(long memberId)
    {
        this.memberId = memberId;
    }

    public String getMemberGuid()
    {
        return memberGuid;
    }

    public void setMemberGuid(String memberGuid)
    {
        this.memberGuid = memberGuid;
    }

    public String getTrackingMemberCode()
    {
        return trackingMemberCode;
    }

    public void setTrackingMemberCode(String trackingMemberCode)
    {
        this.trackingMemberCode = trackingMemberCode;
    }

    public String getTransactionDate()
    {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate)
    {
        this.transactionDate = transactionDate;
    }
}
