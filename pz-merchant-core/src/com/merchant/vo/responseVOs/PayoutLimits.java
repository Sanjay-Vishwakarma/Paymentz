package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="payoutLimits")
@XmlAccessorType(XmlAccessType.FIELD)
public class PayoutLimits
{
    @XmlElement(name="payoutBankAccountNoLimitCheck")
    private String payoutBankAccountNoLimitCheck;

    @XmlElement(name="payoutBankAccountNoAmountLimitCheck")
    private String payoutBankAccountNoAmountLimitCheck;

    public String getPayoutBankAccountNoLimitCheck()
    {
        return payoutBankAccountNoLimitCheck;
    }

    public void setPayoutBankAccountNoLimitCheck(String payoutBankAccountNoLimitCheck)
    {
        this.payoutBankAccountNoLimitCheck = payoutBankAccountNoLimitCheck;
    }

    public String getPayoutBankAccountNoAmountLimitCheck()
    {
        return payoutBankAccountNoAmountLimitCheck;
    }

    public void setPayoutBankAccountNoAmountLimitCheck(String payoutBankAccountNoAmountLimitCheck)
    {
        this.payoutBankAccountNoAmountLimitCheck = payoutBankAccountNoAmountLimitCheck;
    }

    @Override
    public String toString()
    {
        return "PayoutLimits{" +
                "payoutBankAccountNoLimitCheck='" + payoutBankAccountNoLimitCheck + '\'' +
                ", payoutBankAccountNoAmountLimitCheck='" + payoutBankAccountNoAmountLimitCheck + '\'' +
                '}';
    }
}
