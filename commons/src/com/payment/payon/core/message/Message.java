package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 10:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class Message
{
    public void setTransaction(Transaction transaction)
    {
        this.transaction = transaction;
    }

    public void setVersion(String version)
    {

        this.version = version;
    }


    @XStreamAsAttribute
    private String version="1.0";
    @XStreamAlias("Transaction")
    private Transaction transaction = new Transaction();

}
