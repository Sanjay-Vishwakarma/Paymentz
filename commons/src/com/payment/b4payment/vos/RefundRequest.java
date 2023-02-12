package com.payment.b4payment.vos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 02-01-2017.
 */
@XmlRootElement(name="RefundRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class RefundRequest
{
    @XmlElement(name="transactionId")
    String transactionId;
    @XmlElement(name="currencyCode")
    String currencyCode;
    @XmlElement(name="amount")
    String amount;
    @XmlElement(name="softDescriptor")
    String softDescriptor;
    @XmlElement(name="orderId")
    String orderId;
    @XmlElement(name="b4pId")
    String b4pId;


    public String getTransactionId()
    {
        return transactionId;
    }

    public void setTransactionId(String transactionId)
    {
        this.transactionId = transactionId;
    }

    public String getCurrencyCode()
    {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode)
    {
        this.currencyCode = currencyCode;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getSoftDescriptor()
    {
        return softDescriptor;
    }

    public void setSoftDescriptor(String softDescriptor)
    {
        this.softDescriptor = softDescriptor;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getB4pId()
    {
        return b4pId;
    }

    public void setB4pId(String b4pId)
    {
        this.b4pId = b4pId;
    }
}
