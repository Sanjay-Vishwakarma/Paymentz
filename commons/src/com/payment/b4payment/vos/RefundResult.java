package com.payment.b4payment.vos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 21-01-2017.
 */
@XmlRootElement(name="result")
@XmlAccessorType(XmlAccessType.FIELD)
public class RefundResult
{
    @XmlElement(name = "transactionId")
    String transactionId;

    @XmlElement(name = "orderId")
    String orderId;

    @XmlElement(name = "amount")
    String amount;

    @XmlElement(name = "softDescriptor")
    String softDescriptor;

    @XmlElement(name = "currencyCode")
    String currencyCode;

    public String getTransactionId()
    {
        return transactionId;
    }

    public void setTransactionId(String transactionId)
    {
        this.transactionId = transactionId;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
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

    public String getCurrencyCode()
    {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode)
    {
        this.currencyCode = currencyCode;
    }
}
