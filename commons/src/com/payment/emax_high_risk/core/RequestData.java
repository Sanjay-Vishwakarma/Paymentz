package com.payment.emax_high_risk.core;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 1/30/15
 * Time: 11:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class RequestData
{
    public Transaction transaction;
    public Transaction getTransaction()
    {
        return transaction;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
    public String message;
    public void setTransaction(Transaction transaction)
    {
        this.transaction = transaction;
    }

    public Response response;

    public Response getResponse()
    {
        return response;
    }

    public void setResponse(Response response)
    {
        this.response = response;
    }
}
