package com.directi.pg.core.valueObjects;

import com.payment.common.core.CommRequestVO;

/**
 * Created by IntelliJ IDEA.
 * User: saurabh.b
 * Date: 9/20/13
 * Time: 5:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class SafeChargeRequestVO  extends CommRequestVO
{

    public String AuthCode;
    public String PARes;
    public String WEbsite;

    public String getAuthCode()
    {
        return AuthCode;
    }

    public void setAuthCode(String authCode)
    {
        this.AuthCode = authCode;
    }

    public String getWEbsite()
    {
        return WEbsite;
    }

    public void setWEbsite(String WEbsite)
    {
        this.WEbsite = WEbsite;
    }

    public String getPARes()
    {
        return PARes;
    }

    public void setPARes(String PARes)
    {
        this.PARes = PARes;
    }
}
