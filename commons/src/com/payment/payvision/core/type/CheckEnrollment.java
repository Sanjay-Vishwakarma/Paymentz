package com.payment.payvision.core.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Uday on 1/5/18.
 */

@XmlRootElement(name="CheckEnrollment")
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckEnrollment
{
    @XmlElement(name = "memberId",required=false)
    private long memberid;

    @XmlElement(name="memberGuid", required = false)
    private String memberGuid;

    @XmlElement(name="amount", required = false)
    private String amount;

    @XmlElement(name="currencyId", required = false)
    private int currencyId;

    @XmlElement(name="trackingMemberCode",required = false)
    private String trackingMemberCode;

    @XmlElement(name="cardNumber",required = false)
    private String cardNumber;

    @XmlElement(name="cardholder",required = true)
    private String cardholder;

    @XmlElement(name = "cardExpiryMonth",required =false)
    private int cardExpiryMonth;

    @XmlElement(name="cardExpiryYear",required = false)
    private int cardExpiryYear;

    @XmlElement(name="additionalInfo",required = true)
    private String additionalInfo;

    public long getMemberid()
    {
        return memberid;
    }

    public void setMemberid(long memberid)
    {
        this.memberid = memberid;
    }

    public String getMemberGuid()
    {
        return memberGuid;
    }

    public void setMemberGuid(String memberGuid)
    {
        this.memberGuid = memberGuid;
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

    public String getCardNumber()
    {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber)
    {
        this.cardNumber = cardNumber;
    }

    public String getCardholder()
    {
        return cardholder;
    }

    public void setCardholder(String cardholder)
    {
        this.cardholder = cardholder;
    }

    public int getCardExpiryMonth()
    {
        return cardExpiryMonth;
    }

    public void setCardExpiryMonth(int cardExpiryMonth)
    {
        this.cardExpiryMonth = cardExpiryMonth;
    }

    public int getCardExpiryYear()
    {
        return cardExpiryYear;
    }

    public void setCardExpiryYear(int cardExpiryYear)
    {
        this.cardExpiryYear = cardExpiryYear;
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
