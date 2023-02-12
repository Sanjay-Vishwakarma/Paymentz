package com.payment.p4.vos.queryBlock.resultSetBlock.resultBlock;

import com.payment.p4.vos.transactionBlock.Transaction;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 4/11/2015.
 */
@XmlRootElement(name = "Result")
@XmlAccessorType(XmlAccessType.FIELD)
public class Result
{
    @XmlElement(name = "Transaction")
    private Transaction Transaction;

    public com.payment.p4.vos.transactionBlock.Transaction getTransaction()
    {
        return Transaction;
    }

    public void setTransaction(com.payment.p4.vos.transactionBlock.Transaction transaction)
    {
        Transaction = transaction;
    }
}
