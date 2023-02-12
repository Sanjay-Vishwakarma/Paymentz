package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="merchantNotificationCallback")
@XmlAccessorType(XmlAccessType.FIELD)
public class MerchantNotificationCallback
{
    @XmlElement(name="reconciliation")
    private String reconciliation;

    @XmlElement(name="transactions")
    private String transactions;

    @XmlElement(name="refundNotification")
    private String refundNotification;

    @XmlElement(name="chargebackNotification")
    private String chargebackNotification;

    @XmlElement(name="payoutNotification")
    private String payoutNotification;

    @XmlElement(name="inquiryNotification")
    private String inquiryNotification;

    public String getReconciliation()
    {
        return reconciliation;
    }

    public void setReconciliation(String reconciliation)
    {
        this.reconciliation = reconciliation;
    }

    public String getTransactions()
    {
        return transactions;
    }

    public void setTransactions(String transactions)
    {
        this.transactions = transactions;
    }

    public String getRefundNotification()
    {
        return refundNotification;
    }

    public void setRefundNotification(String refundNotification)
    {
        this.refundNotification = refundNotification;
    }

    public String getChargebackNotification()
    {
        return chargebackNotification;
    }

    public void setChargebackNotification(String chargebackNotification)
    {
        this.chargebackNotification = chargebackNotification;
    }

    public String getPayoutNotification()
    {
        return payoutNotification;
    }

    public void setPayoutNotification(String payoutNotification)
    {
        this.payoutNotification = payoutNotification;
    }

    public String getInquiryNotification()
    {
        return inquiryNotification;
    }

    public void setInquiryNotification(String inquiryNotification)
    {
        this.inquiryNotification = inquiryNotification;
    }

    @Override
    public String toString()
    {
        return "MerchantNotificationCallback{" +
                "reconciliation='" + reconciliation + '\'' +
                ", transactions='" + transactions + '\'' +
                ", refundNotification='" + refundNotification + '\'' +
                ", chargebackNotification='" + chargebackNotification + '\'' +
                ", payoutNotification='" + payoutNotification + '\'' +
                ", inquiryNotification='" + inquiryNotification + '\'' +
                '}';
    }
}
