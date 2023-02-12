package com.payment.beekash.vos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 12/29/2015.
 */
@XmlRootElement(name = "Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Response
{
    @XmlElement(name = "resultcode")
    private String resultcode;
    @XmlElement(name = "merchantid")
    private String merchantid;
    @XmlElement(name = "ordernumber")
    private String ordernumber;
    @XmlElement(name = "name")
    private String currency;
    @XmlElement(name = "amount")
    private String amount;
    @XmlElement(name = "datetime")
    private String datetime;
    @XmlElement(name = "paymentordernumber")
    private String paymentordernumber;
    @XmlElement(name = "remark")
    private String remark;
    @XmlElement(name = "billingDescriptor")
    private String billingDescriptor;

    public String getResultcode()
    {
        return resultcode;
    }

    public void setResultcode(String resultcode)
    {
        this.resultcode = resultcode;
    }

    public String getMerchantid()
    {
        return merchantid;
    }

    public void setMerchantid(String merchantid)
    {
        this.merchantid = merchantid;
    }

    public String getOrdernumber()
    {
        return ordernumber;
    }

    public void setOrdernumber(String ordernumber)
    {
        this.ordernumber = ordernumber;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getDatetime()
    {
        return datetime;
    }

    public void setDatetime(String datetime)
    {
        this.datetime = datetime;
    }

    public String getPaymentordernumber()
    {
        return paymentordernumber;
    }

    public void setPaymentordernumber(String paymentordernumber)
    {
        this.paymentordernumber = paymentordernumber;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getBillingDescriptor()
    {
        return billingDescriptor;
    }

    public void setBillingDescriptor(String billingDescriptor)
    {
        this.billingDescriptor = billingDescriptor;
    }
}
