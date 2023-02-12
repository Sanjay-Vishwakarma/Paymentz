package com.manager.vo;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Admin on 3/23/2019.
 */
public class MarketPlaceVO
{
    String memberid;
    String orderid;
    String amount;
    String orderDesc;
    String trackingid;
    String parentTrackingid;
    String reversedAmount;
    String capturedAmount;
    String refundAmount;
    String refundReason;
    String status;
    private MerchantDetailsVO merchantDetailsVO;
    boolean isFraud;
    double totalAmount;

    public String getMemberid()
    {
        return memberid;
    }

    public void setMemberid(String memberid)
    {
        this.memberid = memberid;
    }

    public String getOrderid()
    {
        return orderid;
    }

    public void setOrderid(String orderid)
    {
        this.orderid = orderid;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getOrderDesc()
    {
        return orderDesc;
    }

    public void setOrderDesc(String orderDesc)
    {
        this.orderDesc = orderDesc;
    }

    public String getTrackingid()
    {
        return trackingid;
    }

    public void setTrackingid(String trackingid)
    {
        this.trackingid = trackingid;
    }

    public double getTotalAmount()
    {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public String getReversedAmount()
    {
        return reversedAmount;
    }

    public void setReversedAmount(String reversedAmount)
    {
        this.reversedAmount = reversedAmount;
    }

    public String getCapturedAmount()
    {
        return capturedAmount;
    }

    public void setCapturedAmount(String capturedAmount)
    {
        this.capturedAmount = capturedAmount;
    }

    public String getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount)
    {
        this.refundAmount = refundAmount;
    }

    public boolean isFraud()
    {
        return isFraud;
    }

    public void setFraud(boolean isFraud)
    {
        this.isFraud = isFraud;
    }

    public String getRefundReason()
    {
        return refundReason;
    }

    public void setRefundReason(String refundReason)
    {
        this.refundReason = refundReason;
    }

    public String getParentTrackingid()
    {
        return parentTrackingid;
    }

    public void setParentTrackingid(String parentTrackingid)
    {
        this.parentTrackingid = parentTrackingid;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public MerchantDetailsVO getMerchantDetailsVO()
    {
        return merchantDetailsVO;
    }

    public void setMerchantDetailsVO(MerchantDetailsVO merchantDetailsVO)
    {
        this.merchantDetailsVO = merchantDetailsVO;
    }
}
