package com.payment.payon.core.message;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 9:05 AM
 * To change this template use File | Settings | File Templates.
 */

public class Request extends Message
{

    private String responseURL;

    public Request()
    {
        this.setTransaction(new RequestTransaction());
    }
}
