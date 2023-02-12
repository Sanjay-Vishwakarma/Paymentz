package com.payment.borgun.core;

import com.directi.pg.Transaction;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by ThinkPadT410 on 8/8/2016.
 */
@XStreamAlias("Transaction")
public class InquiryTransaction
{
    @XStreamAlias("TransactionType")
    String transactionType;

    @XStreamAlias("TransactionNumber")
    String transactionNumber;

    @XStreamAlias("BatchNumber")
    String batchNumber;

    @XStreamAlias("TransactionDate")
    String transactionDate;

    @XStreamAlias("PAN")
    String pan;

    @XStreamAlias("RRN")
    String rrn;

    @XStreamAlias("ActionCode")
    String actionCode;

    @XStreamAlias("AuthorizationCode")
    String authorizationCode;

    @XStreamAlias("TrAmount")
    String trAmount;

    @XStreamAlias("TrCurrency")
    String trCurrency;

    @XStreamAlias("Voided")
    String voided;

    @XStreamAlias("Status")
    String status;

    @XStreamAlias("TerminalNr")
    String terminalNr;

    @XStreamAlias("Credit")
    String credit;

    public String getTransactionType()
    {
        return transactionType;
    }

    public void setTransactionType(String transactionType)
    {
        this.transactionType = transactionType;
    }

    public String getTransactionNumber()
    {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber)
    {
        this.transactionNumber = transactionNumber;
    }

    public String getBatchNumber()
    {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber)
    {
        this.batchNumber = batchNumber;
    }

    public String getTransactionDate()
    {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate)
    {
        this.transactionDate = transactionDate;
    }

    public String getPan()
    {
        return pan;
    }

    public void setPan(String pan)
    {
        this.pan = pan;
    }

    public String getRrn()
    {
        return rrn;
    }

    public void setRrn(String rrn)
    {
        this.rrn = rrn;
    }

    public String getActionCode()
    {
        return actionCode;
    }

    public void setActionCode(String actionCode)
    {
        this.actionCode = actionCode;
    }

    public String getAuthorizationCode()
    {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode)
    {
        this.authorizationCode = authorizationCode;
    }

    public String getTrAmount()
    {
        return trAmount;
    }

    public void setTrAmount(String trAmount)
    {
        this.trAmount = trAmount;
    }

    public String getTrCurrency()
    {
        return trCurrency;
    }

    public void setTrCurrency(String trCurrency)
    {
        this.trCurrency = trCurrency;
    }

    public String getVoided()
    {
        return voided;
    }

    public void setVoided(String voided)
    {
        this.voided = voided;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getTerminalNr()
    {
        return terminalNr;
    }

    public void setTerminalNr(String terminalNr)
    {
        this.terminalNr = terminalNr;
    }

    public String getCredit()
    {
        return credit;
    }

    public void setCredit(String credit)
    {
        this.credit = credit;
    }
}
