package com.payment.b4payment.vos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 02-01-2017.
 */
@XmlRootElement(name="RebillRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class RebillRequest
{
    @XmlElement(name="userContractId")
    String userContractId;

    @XmlElement(name="transactionId")
    String transactionId;

    @XmlElement(name="iban")
    String iban;

    @XmlElement(name="bic")
    String bic;

    @XmlElement(name="givenName")
    String givenName;

    @XmlElement(name="familyName")
    String familyName;

    @XmlElement(name="currencyCode")
    String currencyCode;

    @XmlElement(name="amount")
    String amount;

    @XmlElement(name="mandateId")
    String mandateId;

    @XmlElement(name="mandateDate")
    String mandateDate;

    @XmlElement(name="softDescriptor")
    String softDescriptor;

    @XmlElement(name="orderId")
    String orderId;

    public String getTransactionId()
    {
        return transactionId;
    }

    public void setTransactionId(String transactionId)
    {
        this.transactionId = transactionId;
    }

    public String getIban()
    {
        return iban;
    }

    public void setIban(String iban)
    {
        this.iban = iban;
    }

    public String getBic()
    {
        return bic;
    }

    public void setBic(String bic)
    {
        this.bic = bic;
    }

    public String getGivenName()
    {
        return givenName;
    }

    public void setGivenName(String givenName)
    {
        this.givenName = givenName;
    }

    public String getFamilyName()
    {
        return familyName;
    }

    public void setFamilyName(String familyName)
    {
        this.familyName = familyName;
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

    public String getMandateId()
    {
        return mandateId;
    }

    public void setMandateId(String mandateId)
    {
        this.mandateId = mandateId;
    }

    public String getMandateDate()
    {
        return mandateDate;
    }

    public void setMandateDate(String mandateDate)
    {
        this.mandateDate = mandateDate;
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

    String b4pId;


}

