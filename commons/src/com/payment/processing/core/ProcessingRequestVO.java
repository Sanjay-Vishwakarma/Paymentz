package com.payment.processing.core;

import com.payment.common.core.CommRequestVO;

/**
 * Created by Roshan on 2/8/2018.
 */
public class ProcessingRequestVO extends CommRequestVO {
    public String PARes;
    public String MD;

    public String getPARes()
    {
        return PARes;
    }

    public void setPARes(String PARes)
    {
        this.PARes = PARes;
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
