package com.transaction.vo.restVO.ResponseVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 4/20/2020.
 */

@XmlRootElement(name = "refund_info")
@XmlAccessorType(XmlAccessType.FIELD)
public class RefundInfo
{
    @XmlElement(name="refund_amount")
    String refund_amount;

    @XmlElement(name="refund_arn")
    String refund_arn;

    @XmlElement(name="refund_currency")
    String refund_currency;

    @XmlElement(name="refund_datetime")
    String refund_datetime;

    @XmlElement(name="refund_exists")
    String refund_exists;

    @XmlElement(name="refund_purchase_identifier")
    String refund_purchase_identifier;

    public String getRefund_amount()
    {
        return refund_amount;
    }

    public void setRefund_amount(String refund_amount)
    {
        this.refund_amount = refund_amount;
    }

    public String getRefund_arn()
    {
        return refund_arn;
    }

    public void setRefund_arn(String refund_arn)
    {
        this.refund_arn = refund_arn;
    }

    public String getRefund_currency()
    {
        return refund_currency;
    }

    public void setRefund_currency(String refund_currency)
    {
        this.refund_currency = refund_currency;
    }

    public String getRefund_datetime()
    {
        return refund_datetime;
    }

    public void setRefund_datetime(String refund_datetime)
    {
        this.refund_datetime = refund_datetime;
    }

    public String getRefund_exists()
    {
        return refund_exists;
    }

    public void setRefund_exists(String refund_exists)
    {
        this.refund_exists = refund_exists;
    }

    public String getRefund_purchase_identifier()
    {
        return refund_purchase_identifier;
    }

    public void setRefund_purchase_identifier(String refund_purchase_identifier)
    {
        this.refund_purchase_identifier = refund_purchase_identifier;
    }
}
