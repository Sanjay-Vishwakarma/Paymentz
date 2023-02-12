package com.payment.paynetics.core;

import com.payment.common.core.CommRequestVO;

/**
 * Created by admin on 12/6/2017.
 */
public class PayneticsRequestVO extends CommRequestVO
{
    public String PARes;

    public String getPARes()
    {
        return PARes;
    }

    public void setPARes(String PARes)
    {
        this.PARes = PARes;
    }
}
