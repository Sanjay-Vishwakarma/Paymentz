package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="refundConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class RefundConfiguration
{
    @XmlElement(name="isRefund")
    private String isRefund;

    @XmlElement(name="isMultipleRefund")
    private String isMultipleRefund;

    @XmlElement(name="isPartialRefund")
    private String isPartialRefund;

    public String getIsRefund()
    {
        return isRefund;
    }

    public void setIsRefund(String isRefund)
    {
        this.isRefund = isRefund;
    }

    public String getIsMultipleRefund()
    {
        return isMultipleRefund;
    }

    public void setIsMultipleRefund(String isMultipleRefund)
    {
        this.isMultipleRefund = isMultipleRefund;
    }

    public String getIsPartialRefund()
    {
        return isPartialRefund;
    }

    public void setIsPartialRefund(String isPartialRefund)
    {
        this.isPartialRefund = isPartialRefund;
    }

    @Override
    public String toString()
    {
        return "RefundConfiguration{" +
                "isRefund='" + isRefund + '\'' +
                ", isMultipleRefund='" + isMultipleRefund + '\'' +
                ", isPartialRefund='" + isPartialRefund + '\'' +
                '}';
    }
}
