package com.directi.pg.core.lpb;

import com.payment.common.core.Comm3DResponseVO;

/**
 * Created by Roshan on 4/10/2018.
 */
public class LPBResponseVO extends Comm3DResponseVO
{
    private String PaReq;
    private String ACSUrl;
    private String md;
    private String enrolled;


    public String getPaReq()
    {
        return PaReq;
    }

    public void setPaReq(String paReq)
    {
        PaReq = paReq;
    }

    public String getACSUrl()
    {
        return ACSUrl;
    }

    public void setACSUrl(String ACSUrl)
    {
        this.ACSUrl = ACSUrl;
    }

    public String getEnrolled()
    {
        return enrolled;
    }

    public void setEnrolled(String enrolled)
    {
        this.enrolled = enrolled;
    }

    public String getMd()
    {
        return md;
    }

    public void setMd(String md)
    {
        this.md = md;
    }

}
