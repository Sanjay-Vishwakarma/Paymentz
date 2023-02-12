package com.payment.p4.vos.p4MainVo;

import com.payment.p4.vos.queryBlock.Query;
import com.payment.p4.vos.transactionBlock.Transaction;

import javax.xml.bind.annotation.*;

/**
 * Created by admin on 10/3/2015.
 */
@XmlRootElement(name = "Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Response
{

    @XmlAttribute(name = "version")
    String version;

    @XmlElement(name = "Transaction")
    Transaction Transaction;

    @XmlElement(name = "Query")
    Query Query;

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public Transaction getTransaction()
    {
        return Transaction;
    }

    public void setTransaction(Transaction transaction)
    {
        Transaction = transaction;
    }

    public Query getQuery()
    {
        return Query;
    }

    public void setQuery(Query query)
    {
        Query = query;
    }
}
