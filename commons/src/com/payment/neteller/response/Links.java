package com.payment.neteller.response;

/**
 * Created by Sneha on 2/21/2017.
 */
public class Links
{
    private String url;
    private String rel;
    private String method;

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getRel()
    {
        return rel;
    }

    public void setRel(String rel)
    {
        this.rel = rel;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }
}
