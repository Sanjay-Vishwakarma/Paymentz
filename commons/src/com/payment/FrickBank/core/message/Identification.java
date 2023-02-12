package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/1/13
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class Identification
{
    public String getTransactionID()
    {
        return TransactionID;
    }

    public void setTransactionID(String transactionID)
    {
        TransactionID = transactionID;
    }
    @XStreamAlias("TransactionID")
    private String TransactionID="";

    public String getReferenceID()
    {
        return ReferenceID;
    }

    public void setReferenceID(String referenceID)
    {
        ReferenceID = referenceID;
    }

    @XStreamAlias("ReferenceID")
    private String ReferenceID="";
}
