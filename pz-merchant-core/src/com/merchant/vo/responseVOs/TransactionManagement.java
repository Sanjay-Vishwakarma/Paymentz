package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="transactionManagement")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionManagement
{
    @XmlElement(name="transactions")
    private String transactions;

    @XmlElement(name="capture")
    private String capture;

    @XmlElement(name="reversal")
    private String reversal;

    @XmlElement(name="payout")
    private String payout;

    @XmlElement(name="payoutTransaction")
    private String payoutTransaction;

    public String getTransactions()
    {
        return transactions;
    }

    public void setTransactions(String transactions)
    {
        this.transactions = transactions;
    }

    public String getCapture()
    {
        return capture;
    }

    public void setCapture(String capture)
    {
        this.capture = capture;
    }

    public String getReversal()
    {
        return reversal;
    }

    public void setReversal(String reversal)
    {
        this.reversal = reversal;
    }

    public String getPayout()
    {
        return payout;
    }

    public void setPayout(String payout)
    {
        this.payout = payout;
    }

    public String getPayoutTransaction()
    {
        return payoutTransaction;
    }

    public void setPayoutTransaction(String payoutTransaction)
    {
        this.payoutTransaction = payoutTransaction;
    }

    @Override
    public String toString()
    {
        return "TransactionManagement{" +
                "transactions='" + transactions + '\'' +
                ", capture='" + capture + '\'' +
                ", reversal='" + reversal + '\'' +
                ", payout='" + payout + '\'' +
                ", payoutTransaction='" + payoutTransaction + '\'' +
                '}';
    }
}
