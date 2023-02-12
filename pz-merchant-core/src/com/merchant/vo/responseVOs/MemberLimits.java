package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="memberLimits")
@XmlAccessorType(XmlAccessType.FIELD)
public class MemberLimits
{
    @XmlElement(name="cardLimitCheck")
    private String cardLimitCheck;

    @XmlElement(name="cardAmountLimitCheck")
    private String cardAmountLimitCheck;

    @XmlElement(name="amountLimitCheck")
    private String amountLimitCheck;

    @XmlElement(name="cardVelocityCheck")
    private String cardVelocityCheck;

    @XmlElement(name="limitRouting")
    private String limitRouting;

    @XmlElement(name="payoutAmountLimitCheck")
    private String payoutAmountLimitCheck;

    @XmlElement(name="payoutRouting")
    private String payoutRouting;

    public String getCardLimitCheck()
    {
        return cardLimitCheck;
    }

    public void setCardLimitCheck(String cardLimitCheck)
    {
        this.cardLimitCheck = cardLimitCheck;
    }

    public String getCardAmountLimitCheck()
    {
        return cardAmountLimitCheck;
    }

    public void setCardAmountLimitCheck(String cardAmountLimitCheck)
    {
        this.cardAmountLimitCheck = cardAmountLimitCheck;
    }

    public String getAmountLimitCheck()
    {
        return amountLimitCheck;
    }

    public void setAmountLimitCheck(String amountLimitCheck)
    {
        this.amountLimitCheck = amountLimitCheck;
    }

    public String getCardVelocityCheck()
    {
        return cardVelocityCheck;
    }

    public void setCardVelocityCheck(String cardVelocityCheck)
    {
        this.cardVelocityCheck = cardVelocityCheck;
    }

    public String getLimitRouting()
    {
        return limitRouting;
    }

    public void setLimitRouting(String limitRouting)
    {
        this.limitRouting = limitRouting;
    }

    public String getPayoutAmountLimitCheck()
    {
        return payoutAmountLimitCheck;
    }

    public void setPayoutAmountLimitCheck(String payoutAmountLimitCheck)
    {
        this.payoutAmountLimitCheck = payoutAmountLimitCheck;
    }

    public String getPayoutRouting()
    {
        return payoutRouting;
    }

    public void setPayoutRouting(String payoutRouting)
    {
        this.payoutRouting = payoutRouting;
    }

    @Override
    public String toString()
    {
        return "MemberLimits{" +
                "cardLimitCheck='" + cardLimitCheck + '\'' +
                ", cardAmountLimitCheck='" + cardAmountLimitCheck + '\'' +
                ", amountLimitCheck='" + amountLimitCheck + '\'' +
                ", cardVelocityCheck='" + cardVelocityCheck + '\'' +
                ", limitRouting='" + limitRouting + '\'' +
                ", payoutAmountLimitCheck='" + payoutAmountLimitCheck + '\'' +
                ", payoutRouting='" + payoutRouting + '\'' +
                '}';
    }
}
