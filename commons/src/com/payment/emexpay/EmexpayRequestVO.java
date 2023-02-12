package com.payment.emexpay;

import com.payment.common.core.CommRequestVO;

/**
 * Created by Admin on 2/6/18.
 */
public class EmexpayRequestVO extends CommRequestVO
{
    private String paRes;
    private String pa_res_url;
    private String MD;

    public String getPaRes()
    {
        return paRes;
    }

    public void setPaRes(String paRes)
    {
        this.paRes = paRes;
    }

    public String getPa_res_url()
    {
        return pa_res_url;
    }

    public void setPa_res_url(String pa_res_url)
    {
        this.pa_res_url = pa_res_url;
    }

    public String getMD()
    {
        return MD;
    }

    public void setMD(String MD)
    {
        this.MD = MD;
    }
}
