package com.payment.paysec;

/**
 * Created by Sandip on 7/27/2018.
 */
public class PaySecDepositResponse
{
    Header header;
    Body body;

    public Header getHeader()
    {
        return header;
    }

    public void setHeader(Header header)
    {
        this.header = header;
    }

    public Body getBody()
    {
        return body;
    }

    public void setBody(Body body)
    {
        this.body = body;
    }
}
