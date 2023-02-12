package com.payment.europay.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 8/17/13
 * Time: 1:35 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("response")
public class Response
{

    @XStreamAlias("operation")
    String operation;

    @XStreamAlias("resultCode")
    String resultCode;

    @XStreamAlias("merNo")
    String merNo;

    @XStreamAlias("billNo")
    String  billNo;

    @XStreamAlias("currency")
    String currency;

    @XStreamAlias("amount")
    String amount;

    @XStreamAlias("dateTime")
    String dateTime;

    @XStreamAlias("paymentOrderNo")
    String paymentOrderNo;

    @XStreamAlias("remark")
    String remark;

    @XStreamAlias("md5Info")
    String md5Info;

    public String getOperation()
    {
        return operation;
    }

    public void setOperation(String operation)
    {
        this.operation = operation;
    }

    public String getResultCode()
    {
        return resultCode;
    }

    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }

    public String getMerNo()
    {
        return merNo;
    }

    public void setMerNo(String merNo)
    {
        this.merNo = merNo;
    }

    public String getBillNo()
    {
        return billNo;
    }

    public void setBillNo(String billNo)
    {
        this.billNo = billNo;
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

    public String getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(String dateTime)
    {
        this.dateTime = dateTime;
    }

    public String getPaymentOrderNo()
    {
        return paymentOrderNo;
    }

    public void setPaymentOrderNo(String paymentOrderNo)
    {
        this.paymentOrderNo = paymentOrderNo;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getMd5Info()
    {
        return md5Info;
    }

    public void setMd5Info(String md5Info)
    {
        this.md5Info = md5Info;
    }

    public String getBillingDescriptor()
    {
        return billingDescriptor;
    }

    public void setBillingDescriptor(String billingDescriptor)
    {
        this.billingDescriptor = billingDescriptor;
    }

    @XStreamAlias("billingDescriptor")
    String  billingDescriptor;
}
