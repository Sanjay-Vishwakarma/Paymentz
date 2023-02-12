package com.payment.neteller.response;

/**
 * Created by Sneha on 2/21/2017.
 */
public class Redirects
{
    private String rel;
    private String uri;

    public String getRel()
    {
        return rel;
    }

    public void setRel(String rel)
    {
        this.rel = rel;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }
}
