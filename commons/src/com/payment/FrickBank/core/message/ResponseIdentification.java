package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/21/13
 * Time: 5:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResponseIdentification
{
    public String getTransactionID()
    {
        return TransactionID;
    }

    public void setTransactionID(String transactionID)
    {
        TransactionID = transactionID;
    }

    public String getUniqueID()
    {
        return UniqueID;
    }

    public void setUniqueID(String uniqueID)
    {
        UniqueID = uniqueID;
    }

    public String getShortID()
    {
        return ShortID;
    }

    public void setShortID(String shortID)
    {
        ShortID = shortID;
    }

    @XStreamAlias("TransactionID")
    private String TransactionID="";
    @XStreamAlias("UniqueID")
    private String UniqueID="";
    @XStreamAlias("ShortID")
    private String ShortID="";

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
