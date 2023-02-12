package com.transaction.vo.restVO.RequestVO;

import com.manager.vo.MarketPlaceVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 4/16/2019.
 */
@XmlRootElement(name="marketplace")
@XmlAccessorType(XmlAccessType.FIELD)
public class MarketPlace
{
    @XmlElement(name="trackingid")
    String trackingid;

    @XmlElement(name="amount")
    String amount;

    @XmlElement(name="memberid")
    String memberid;

    @XmlElement(name="orderid")
    String orderid;

    @XmlElement(name="order_Description")
    String order_Description;


    public String getTrackingid()
    {
        return trackingid;
    }

    public void setTrackingid(String trackingid)
    {
        this.trackingid = trackingid;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

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

    public String getOrder_Description()
    {
        return order_Description;
    }

    public void setOrder_Description(String order_Description)
    {
        this.order_Description = order_Description;
    }
}
