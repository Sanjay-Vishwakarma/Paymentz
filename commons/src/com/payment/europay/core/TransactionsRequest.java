package com.payment.europay.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/6/13
 * Time: 3:53 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("transactions_request")
public class TransactionsRequest
{
    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    @XStreamAsAttribute
    @XStreamAlias("mode")
    String mode;

    @XStreamAsAttribute
    @XStreamAlias("accountId")
    String accountId;

    public EuroPayTransactions getEuroPayTransactions()
    {
        return euroPayTransactions;
    }

    public void setEuroPayTransactions(EuroPayTransactions euroPayTransactions)
    {
        this.euroPayTransactions = euroPayTransactions;
    }

    @XStreamAlias("transactions")
    EuroPayTransactions euroPayTransactions;

}

