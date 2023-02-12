package com.payment.Wirecardnew;

import com.payment.common.core.CommRequestVO;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/27/13
 * Time: 9:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class WireCardRequestVO extends CommRequestVO
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
