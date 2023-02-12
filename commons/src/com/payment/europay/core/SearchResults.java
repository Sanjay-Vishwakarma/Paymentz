package com.payment.europay.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 8/17/13
 * Time: 5:52 PM
 * To change this template use File | Settings | File Templates.
 */

@XStreamAlias("searchResults")
public class SearchResults
{

    @XStreamAlias("terminalName")
    @XStreamAsAttribute
    String terminalName;

    @XStreamAlias("partnerName")
    @XStreamAsAttribute
    String partnerName;

    @XStreamAlias("merchantName")
    @XStreamAsAttribute
    String merchantName;

    @XStreamAlias("currency")
    @XStreamAsAttribute
    String currency;

    @XStreamAlias("creditAmountFloat")
    @XStreamAsAttribute
    String creditAmountFloat;

    @XStreamAlias("debitAmountFloat")
    @XStreamAsAttribute
    String debitAmountFloat;

    @XStreamAlias("debitAmount")
    @XStreamAsAttribute
    String creditAmount;

    @XStreamAlias("debitAmount")
    @XStreamAsAttribute
    String debitAmount;

    @XStreamAlias("channelName")
    @XStreamAsAttribute
    String channelName;

    @XStreamAlias("message")
    @XStreamAsAttribute
    String message;

    @XStreamAlias("reference")
    @XStreamAsAttribute
    String reference;

    @XStreamAlias("code")
    @XStreamAsAttribute
    String code;

    @XStreamAlias("endStamp")
    @XStreamAsAttribute
    String endStamp;

    @XStreamAlias("startStamp")
    @XStreamAsAttribute
    String startStamp;

    @XStreamAlias("type")
    @XStreamAsAttribute
    String type;

    @XStreamAlias("method")
    @XStreamAsAttribute
    String method;

    @XStreamAlias("destination")
    @XStreamAsAttribute
    String destination;

    @XStreamAlias("mcTxId")
    @XStreamAsAttribute
    String mcTxId;

    @XStreamAlias("txId")
    @XStreamAsAttribute
    String txId;

    @XStreamAlias("authorization")
    @XStreamAsAttribute
    Authorization authorization;

    @XStreamAlias("token")
    @XStreamAsAttribute
    Token token;



    public String getTerminalName()
    {
        return terminalName;
    }

    public void setTerminalName(String terminalName)
    {
        this.terminalName = terminalName;
    }

    public String getPartnerName()
    {
        return partnerName;
    }

    public void setPartnerName(String partnerName)
    {
        this.partnerName = partnerName;
    }

    public String getMerchantName()
    {
        return merchantName;
    }

    public void setMerchantName(String merchantName)
    {
        this.merchantName = merchantName;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getCreditAmountFloat()
    {
        return creditAmountFloat;
    }

    public void setCreditAmountFloat(String creditAmountFloat)
    {
        this.creditAmountFloat = creditAmountFloat;
    }

    public String getDebitAmountFloat()
    {
        return debitAmountFloat;
    }

    public void setDebitAmountFloat(String debitAmountFloat)
    {
        this.debitAmountFloat = debitAmountFloat;
    }

    public String getCreditAmount()
    {
        return creditAmount;
    }

    public void setCreditAmount(String creditAmount)
    {
        this.creditAmount = creditAmount;
    }

    public String getDebitAmount()
    {
        return debitAmount;
    }

    public void setDebitAmount(String debitAmount)
    {
        this.debitAmount = debitAmount;
    }

    public String getChannelName()
    {
        return channelName;
    }

    public void setChannelName(String channelName)
    {
        this.channelName = channelName;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getReference()
    {
        return reference;
    }

    public void setReference(String reference)
    {
        this.reference = reference;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getEndStamp()
    {
        return endStamp;
    }

    public void setEndStamp(String endStamp)
    {
        this.endStamp = endStamp;
    }

    public String getStartStamp()
    {
        return startStamp;
    }

    public void setStartStamp(String startStamp)
    {
        this.startStamp = startStamp;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public String getDestination()
    {
        return destination;
    }

    public void setDestination(String destination)
    {
        this.destination = destination;
    }

    public String getMcTxId()
    {
        return mcTxId;
    }

    public void setMcTxId(String mcTxId)
    {
        this.mcTxId = mcTxId;
    }

    public String getTxId()
    {
        return txId;
    }

    public void setTxId(String txId)
    {
        this.txId = txId;
    }

    public Authorization getAuthorization()
    {
        return authorization;
    }

    public void setAuthorization(Authorization authorization)
    {
        this.authorization = authorization;
    }

    public Token getToken()
    {
        return token;
    }

    public void setToken(Token token)
    {
        this.token = token;
    }

//    public List<TransactionLog> getTransactionLog()
//    {
//
//        return transactionLog;
//    }
//
//    public void setTransactionLog(List<TransactionLog> transactionLog)
//    {
//        this.transactionLog = transactionLog;
//    }
//
//    @XStreamAlias("transactionLog")
//    List<TransactionLog> transactionLog;
}
