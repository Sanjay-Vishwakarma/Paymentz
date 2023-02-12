package com.payment.b4payment.vos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 18-01-2017.
 */
@XmlRootElement(name="result")
@XmlAccessorType(XmlAccessType.FIELD)
public class Result
{
    @XmlElement(name="reconciliationEntryId")
    String reconciliationEntryId ;

    @XmlElement(name="settlementAccount")
    String settlementAccount ;

    @XmlElement(name="end2endId")
    String end2endId ;

    @XmlElement(name="source")
    String source ;

    @XmlElement(name="bookingDate")
    String bookingDate ;

    @XmlElement(name="amount")
    String amount;

    @XmlElement(name="bankStatementAmount")
    String bankStatementAmount ;

    @XmlElement(name="mandateDate")
    String mandateDate;

    @XmlElement(name="thirdPartyFees")
    String thirdPartyFees ;

    @XmlElement(name="currencyCode")
    String currencyCode ;

    @XmlElement(name="info")
    String info ;

    @XmlElement(name="transactionId")
    String transactionId ;

    @XmlElement(name="state")
    String state ;

    @XmlElement(name="b4pId")
    String b4pId ;

    @XmlElement(name="reasonCode")
    String reasonCode ;

    @XmlElement(name="trigger")
    String trigger ;

    @XmlElement(name="token")
    String token ;


    public String getReconciliationEntryId()
    {
        return reconciliationEntryId;
    }

    public void setReconciliationEntryId(String reconciliationEntryId)
    {
        this.reconciliationEntryId = reconciliationEntryId;
    }

    public String getSettlementAccount()
    {
        return settlementAccount;
    }

    public void setSettlementAccount(String settlementAccount)
    {
        this.settlementAccount = settlementAccount;
    }

    public String getEnd2endId()
    {
        return end2endId;
    }

    public void setEnd2endId(String end2endId)
    {
        this.end2endId = end2endId;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getBookingDate()
    {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate)
    {
        this.bookingDate = bookingDate;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getBankStatementAmount()
    {
        return bankStatementAmount;
    }

    public void setBankStatementAmount(String bankStatementAmount)
    {
        this.bankStatementAmount = bankStatementAmount;
    }

    public String getMandateDate()
    {
        return mandateDate;
    }

    public void setMandateDate(String mandateDate)
    {
        this.mandateDate = mandateDate;
    }

    public String getThirdPartyFees()
    {
        return thirdPartyFees;
    }

    public void setThirdPartyFees(String thirdPartyFees)
    {
        this.thirdPartyFees = thirdPartyFees;
    }

    public String getCurrencyCode()
    {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode)
    {
        this.currencyCode = currencyCode;
    }

    public String getInfo()
    {
        return info;
    }

    public void setInfo(String info)
    {
        this.info = info;
    }

    public String getTransactionId()
    {
        return transactionId;
    }

    public void setTransactionId(String transactionId)
    {
        this.transactionId = transactionId;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getB4pId()
    {
        return b4pId;
    }

    public void setB4pId(String b4pId)
    {
        this.b4pId = b4pId;
    }

    public String getReasonCode()
    {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode)
    {
        this.reasonCode = reasonCode;
    }

    public String getTrigger()
    {
        return trigger;
    }

    public void setTrigger(String trigger)
    {
        this.trigger = trigger;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }
}
