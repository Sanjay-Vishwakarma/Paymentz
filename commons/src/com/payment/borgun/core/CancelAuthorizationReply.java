package com.payment.borgun.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 10/2/13
 * Time: 3:24 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("cancelAuthorizationReply")
public class CancelAuthorizationReply
{
    @XStreamAlias("Version")
    String version;

    @XStreamAlias("Processor")
    int processor;

    @XStreamAlias("MerchantID")
    String merchantID;

    @XStreamAlias("TerminalID")
    int terminalID;

    @XStreamAlias("TransType")
    int transType;

    @XStreamAlias("TrAmount")
    String trAmount;

    @XStreamAlias("TrCurrency")
    int trCurrency;

    @XStreamAlias("CardAccName")
    String cardAccName;

    @XStreamAlias("DateAndTime")
    String dateAndTime;

    @XStreamAlias("RRN")
    String rrn;

    @XStreamAlias("AuthCode")
    String authCode;


    @XStreamAlias("ActionCode")
    String actionCode;

    @XStreamAlias("PAN")
    String pan;


    @XStreamAlias("Transaction")
    String transaction;

    @XStreamAlias("StoreTerminal")
    String storeTerminal;

    @XStreamAlias("CardType")
    String cardType;

    @XStreamAlias("CardAccId")
    String cardAccId;

    @XStreamAlias("Batch")
    String batch;

    @XStreamAlias("Message")
    String message;



    public String getTransaction()
    {
        return transaction;
    }

    public void setTransaction(String transaction)
    {
        this.transaction = transaction;
    }

    public String getCardAccName()
    {
        return cardAccName;
    }

    public void setCardAccName(String cardAccName)
    {
        this.cardAccName = cardAccName;
    }

    public String getStoreTerminal()
    {
        return storeTerminal;
    }

    public void setStoreTerminal(String storeTerminal)
    {
        this.storeTerminal = storeTerminal;
    }

    public String getCardType()
    {
        return cardType;
    }

    public void setCardType(String cardType)
    {
        this.cardType = cardType;
    }

    public String getCardAccId()
    {
        return cardAccId;
    }

    public void setCardAccId(String cardAccId)
    {
        this.cardAccId = cardAccId;
    }

    public String getBatch()
    {
        return batch;
    }

    public void setBatch(String batch)
    {
        this.batch = batch;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public int getProcessor()
    {
        return processor;
    }

    public void setProcessor(int processor)
    {
        this.processor = processor;
    }

    public String getMerchantID()
    {
        return merchantID;
    }

    public void setMerchantID(String merchantID)
    {
        this.merchantID = merchantID;
    }

    public int getTerminalID()
    {
        return terminalID;
    }

    public void setTerminalID(int terminalID)
    {
        this.terminalID = terminalID;
    }

    public int getTransType()
    {
        return transType;
    }

    public void setTransType(int transType)
    {
        this.transType = transType;
    }

    public String getTrAmount()
    {
        return trAmount;
    }

    public void setTrAmount(String trAmount)
    {
        this.trAmount = trAmount;
    }

    public int getTrCurrency()
    {
        return trCurrency;
    }

    public void setTrCurrency(int trCurrency)
    {
        this.trCurrency = trCurrency;
    }

    public String getDateAndTime()
    {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime)
    {
        this.dateAndTime = dateAndTime;
    }

    public String getRrn()
    {
        return rrn;
    }

    public void setRrn(String rrn)
    {
        this.rrn = rrn;
    }

    public String getAuthCode()
    {
        return authCode;
    }

    public void setAuthCode(String authCode)
    {
        this.authCode = authCode;
    }

    public String getActionCode()
    {
        return actionCode;
    }

    public void setActionCode(String actionCode)
    {
        this.actionCode = actionCode;
    }

}
