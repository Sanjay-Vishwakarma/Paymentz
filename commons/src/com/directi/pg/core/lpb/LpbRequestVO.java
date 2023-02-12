package com.directi.pg.core.lpb;

import com.payment.common.core.CommRequestVO;

/**
 * Created by Roshan on 4/12/2018.
 */
public class LpbRequestVO extends CommRequestVO
{
    public String PaRes;

    public String getPaRes()
    {
        return PaRes;
    }

    public void setPaRes(String PaRes)
    {
        this.PaRes = PaRes;
    }
}
