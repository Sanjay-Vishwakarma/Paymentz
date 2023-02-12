package com.payment.payspace;

/**
 * Created by admin on 7/22/2017.
 */
public class RedirectParams
{
    private String PaReq;
    private String MD;
    private String TermUrl;

    public String getPaReq()
    {
        return PaReq;
    }

    public void setPaReq(String paReq)
    {
        PaReq = paReq;
    }

    public String getMD()
    {
        return MD;
    }

    public void setMD(String MD)
    {
        this.MD = MD;
    }

    public String getTermUrl()
    {
        return TermUrl;
    }

    public void setTermUrl(String termUrl)
    {
        TermUrl = termUrl;
    }
}
