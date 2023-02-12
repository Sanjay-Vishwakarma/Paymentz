package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/2/13
 * Time: 2:43 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("Response")
public class Response
{
    public ResponseTransaction getTransaction()
    {
        return transaction;
    }

    public void setTransaction(ResponseTransaction transaction)
    {
        this.transaction = transaction;
    }

    @XStreamAlias("Transaction")
    private ResponseTransaction transaction;


}
