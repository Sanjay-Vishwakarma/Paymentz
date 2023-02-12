package com.transaction.vo.restVO.ResponseVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Sneha on 2/6/2017.
 */
@XmlRootElement(name="invoiceDetails")
@XmlAccessorType(XmlAccessType.FIELD)
public class Invoice
{
    @XmlElement(name="amount")
    String amount;

    @XmlElement(name="currency")
    String currency;

    @XmlElement(name = "expirationPeriod")
    String expirationPeriod;

    @XmlElement(name = "createdDate")
    String date;

    @XmlElement(name = "createdTime")
    String time;

    @XmlElement(name = "merchantInvoiceId")
    String merchantInvoiceId;

    @XmlElement(name = "customerEmail")
    String emailaddr;

    @XmlElement(name="descriptor")
    String descriptor;

    @XmlElement(name="systemInvoiceId")
    String invoiceId;

    @XmlElement(name="transactionUrl")
    String transactionUrl;

    @XmlElement(name="invoiceStatus")
    String invoiceStatus;

    @XmlElement(name="transactionStatus")
    String transactionStatus;

    @XmlElement(name="paymentId")
    String paymentId;

    public String getPaymentId()
    {
        return paymentId;
    }

    public void setPaymentId(String paymentId)
    {
        this.paymentId = paymentId;
    }

    public String getInvoiceStatus()
    {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus)
    {
        this.invoiceStatus = invoiceStatus;
    }

    public String getTransactionStatus()
    {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus)
    {
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionUrl()
    {
        return transactionUrl;
    }

    public void setTransactionUrl(String transactionUrl)
    {
        this.transactionUrl = transactionUrl;
    }

    public String getExpirationPeriod()
    {
        return expirationPeriod;
    }

    public void setExpirationPeriod(String expirationPeriod)
    {
        this.expirationPeriod = expirationPeriod;
    }

    public String getInvoiceId()
    {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId)
    {
        this.invoiceId = invoiceId;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getMerchantInvoiceId()
    {
        return merchantInvoiceId;
    }

    public void setMerchantInvoiceId(String merchantTransactionId)
    {
        this.merchantInvoiceId = merchantTransactionId;
    }

    public String getEmailaddr()
    {
        return emailaddr;
    }

    public void setEmailaddr(String emailaddr)
    {
        this.emailaddr = emailaddr;
    }

    public String getDescriptor()
    {
        return descriptor;
    }

    public void setDescriptor(String descriptor)
    {
        this.descriptor = descriptor;
    }
}
