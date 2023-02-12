package com.payment.icici;

import com.payment.common.core.CommResponseVO;

/**
 * Created by Kiran on 27/6/15.
 */
public class INICICIResponseVO extends CommResponseVO
{
    String authCode;
    String RRNumber;
    String RTSRNumber;

    public String getAuthCode()
    {
        return authCode;
    }

    public void setAuthCode(String authCode)
    {
        this.authCode = authCode;
    }

    public String getRRNumber()
    {
        return RRNumber;
    }

    public void setRRNumber(String RRNumber)
    {
        this.RRNumber = RRNumber;
    }

    public String getRTSRNumber()
    {
        return RTSRNumber;
    }

    public void setRTSRNumber(String RTSRNumber)
    {
        this.RTSRNumber = RTSRNumber;
    }
}
