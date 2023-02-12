package com.payment.borgun.core;

import com.directi.pg.Transaction;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Created by Jinesh on 7/22/2016.
 */
@XStreamAlias("TransactionList")
public class InquiryAuthorizationReply
{
    @XStreamAlias("Version")
    String version;

    @XStreamAlias("Processor")
    String processor;

    @XStreamAlias("MerchantID")
    String merchantID;

    @XStreamAlias("ActionCode")
    String actionCode;

    @XStreamAlias("Transaction")
    InquiryTransaction transaction;

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getProcessor()
    {
        return processor;
    }

    public void setProcessor(String processor)
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

    public String getActionCode()
    {
        return actionCode;
    }

    public void setActionCode(String actionCode)
    {
        this.actionCode = actionCode;
    }

    public InquiryTransaction getTransaction()
    {
        return transaction;
    }

    public void setTransaction(InquiryTransaction transaction)
    {
        this.transaction = transaction;
    }
}
