package com.payment.procesosmc;

import com.payment.common.core.CommRequestVO;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 5/19/15
 * Time: 05:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcesosMCRequestVO extends CommRequestVO
{
    String authCode;
    String refNumber;
    String bankTransDate;
    String bankTransTime;

    public String getAuthCode()
    {
        return authCode;
    }

    public void setAuthCode(String authCode)
    {
        this.authCode = authCode;
    }

    public String getRefNumber()
    {
        return refNumber;
    }

    public void setRefNumber(String refNumber)
    {
        this.refNumber = refNumber;
    }

    public String getBankTransDate()
    {
        return bankTransDate;
    }

    public void setBankTransDate(String bankTransDate)
    {
        this.bankTransDate = bankTransDate;
    }

    public String getBankTransTime()
    {
        return bankTransTime;
    }

    public void setBankTransTime(String bankTransTime)
    {
        this.bankTransTime = bankTransTime;
    }
}
