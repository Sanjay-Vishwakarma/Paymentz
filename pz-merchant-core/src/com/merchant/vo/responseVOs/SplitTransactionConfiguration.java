package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="splitTransactionConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class SplitTransactionConfiguration
{

    @XmlElement(name="splitPaymentAllowed")
    private String splitPaymentAllowed;

    @XmlElement(name="splitPaymentType")
    private String splitPaymentType;

    public String getSplitPaymentAllowed()
    {
        return splitPaymentAllowed;
    }

    public void setSplitPaymentAllowed(String splitPaymentAllowed)
    {
        this.splitPaymentAllowed = splitPaymentAllowed;
    }

    public String getSplitPaymentType()
    {
        return splitPaymentType;
    }

    public void setSplitPaymentType(String splitPaymentType)
    {
        this.splitPaymentType = splitPaymentType;
    }

    @Override
    public String toString()
    {
        return "SplitTransactionConfiguration{" +
                "splitPaymentAllowed='" + splitPaymentAllowed + '\'' +
                ", splitPaymentType='" + splitPaymentType + '\'' +
                '}';
    }
}
