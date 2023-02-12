package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 10:10 AM
 * To change this template use File | Settings | File Templates.
 */

@XStreamAlias("Response")
public class Response extends Message
{

    public ResponseTransaction getResponseTransaction()
    {
        return responseTransaction;
    }

    public void setResponseTransaction(ResponseTransaction responseTransaction)
    {
        this.responseTransaction = responseTransaction;
    }

    @XStreamAlias("Transaction")
    public ResponseTransaction responseTransaction;


}
