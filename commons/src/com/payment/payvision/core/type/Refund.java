package com.payment.payvision.core.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 5/15/16.
 */
@XmlRootElement(name="Capture")
@XmlAccessorType(XmlAccessType.FIELD)
public class Refund
{
    @XmlElement(name = "memberId",required = true)
    private long memberId;

    @XmlElement(name = "memberGuid",required = true)
    private String memberGuid;

    @XmlElement(name = "transactionId",required = true)
    private long transactionId;

    @XmlElement(name = "transactionGuid",required = true)
    private String transactionGuid;

    @XmlElement(name = "amount",required = true)
    private String amount;

    @XmlElement(name = "currencyId",required = true)
    private int currencyId;

    @XmlElement(name = "trackingMemberCode",required = true)
    private String trackingMemberCode;

    @XmlElement(name = "additionalInfo",required = true)
    private String additionalInfo;

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

    public long getTransactionId()
    {
        return transactionId;
    }

    public void setTransactionId(long transactionId)
    {
        this.transactionId = transactionId;
    }

    public String getTransactionGuid()
    {
        return transactionGuid;
    }

    public void setTransactionGuid(String transactionGuid)
    {
        this.transactionGuid = transactionGuid;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public int getCurrencyId()
    {
        return currencyId;
    }

    public void setCurrencyId(int currencyId)
    {
        this.currencyId = currencyId;
    }

    public String getTrackingMemberCode()
    {
        return trackingMemberCode;
    }

    public void setTrackingMemberCode(String trackingMemberCode)
    {
        this.trackingMemberCode = trackingMemberCode;
    }

    public String getAdditionalInfo()
    {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo)
    {
        this.additionalInfo = additionalInfo;
    }
}
