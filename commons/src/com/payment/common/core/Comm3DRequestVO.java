package com.payment.common.core;

/**
 * Created by Admin on 9/7/18.
 */
public class Comm3DRequestVO extends CommRequestVO
{
    private String paRes;
    private String md;
    private String authCode;
    private String urlFor3DRedirect;
    private String paReq;
    private String OptionalCode;


    public String getPaReq()
    {
        return paReq;
    }

    public void setPaReq(String paReq)
    {
        this.paReq = paReq;
    }

    public String getUrlFor3DRedirect()
    {
        return urlFor3DRedirect;
    }

    public void setUrlFor3DRedirect(String urlFor3DRedirect)
    {
        this.urlFor3DRedirect = urlFor3DRedirect;
    }

    public String getOptionalCode()
    {
        return OptionalCode;
    }

    public void setOptionalCode(String optionalCode)
    {
        OptionalCode = optionalCode;
    }

    public String getPaRes()
    {
        return paRes;
    }

    public void setPaRes(String paRes)
    {
        this.paRes = paRes;
    }

    public String getMd()
    {
        return md;
    }

    public void setMd(String md)
    {
        this.md = md;
    }

    public String getAuthCode()
    {
        return authCode;
    }

    public void setAuthCode(String authCode)
    {
        this.authCode = authCode;
    }
}
