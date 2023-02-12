package com.payment.europay.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/5/13
 * Time: 10:28 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("transactions")
public class EuroPayTransactions
{
    @XStreamAlias("type")
    @XStreamAsAttribute
    String type;

    @XStreamAlias("method")
    @XStreamAsAttribute
    String method;

    @XStreamAlias("mcTxDate")
    @XStreamAsAttribute
    String mcTxDate;

    @XStreamAlias("mcTxId")
    @XStreamAsAttribute
    String mcTxId;

    @XStreamAlias("payment")
    @XStreamAsAttribute
    EuroPayPayment euroPayPayment;

    @XStreamAlias("customer")
    @XStreamAsAttribute
    EuroPayCustomer euroPayCustomer;

    @XStreamAlias("creditCard")
    @XStreamAsAttribute
    EuroPayCard euroPayCard;

    @XStreamAlias("debitCardClassic")
    @XStreamAsAttribute
    EuroPayDebitCard euroPayDebitCard;

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

    public String getMcTxDate()
    {
        return mcTxDate;
    }

    public void setMcTxDate(String mcTxDate)
    {
        this.mcTxDate = mcTxDate;
    }

    public String getMcTxId()
    {
        return mcTxId;
    }

    public void setMcTxId(String mcTxId)
    {
        this.mcTxId = mcTxId;
    }

    public EuroPayPayment getEuroPayPayment()
    {
        return euroPayPayment;
    }

    public void setEuroPayPayment(EuroPayPayment euroPayPayment)
    {
        this.euroPayPayment = euroPayPayment;
    }

    public EuroPayCustomer getEuroPayCustomer()
    {
        return euroPayCustomer;
    }

    public void setEuroPayCustomer(EuroPayCustomer euroPayCustomer)
    {
        this.euroPayCustomer = euroPayCustomer;
    }

    public EuroPayCard getEuroPayCard()
    {
        return euroPayCard;
    }

    public void setEuroPayCard(EuroPayCard euroPayCard)
    {
        this.euroPayCard = euroPayCard;
    }

    public EuroPayDebitCard getEuroPayDebitCard()
    {
        return euroPayDebitCard;
    }

    public void setEuroPayDebitCard(EuroPayDebitCard euroPayDebitCard)
    {
        this.euroPayDebitCard = euroPayDebitCard;
    }

}

