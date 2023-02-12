package com.directi.pg.core.valueObjects;

import com.payment.common.core.Comm3DResponseVO;

/**
 * Created by IntelliJ IDEA.
 * User: saurabh.b
 * Date: 9/20/13
 * Time: 6:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class SafeChargeResponseVO extends Comm3DResponseVO
{
    String AuthCode;
    String AVSCode;
    String ExErrCode;
    String token;
    String paReq;
    String merchantID;
    String ACSUrl;
    String threeDFlow;
    String requestId;
    String result;
    String creditTypeId;

    public String getAuthCode()
    {
        return AuthCode;
    }

    public void setAuthCode(String authCode)
    {
        this.AuthCode = authCode;
    }

    public String getAVSCode()
    {
        return AVSCode;
    }

    public void setAVSCode(String AVSCode)
    {
        this.AVSCode = AVSCode;
    }

    public String getExErrCode()
    {
        return ExErrCode;
    }

    public void setExErrCode(String exErrCode)
    {
        this.ExErrCode = exErrCode;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getPaReq()
    {
        return paReq;
    }

    public void setPaReq(String paReq)
    {
        this.paReq = paReq;
    }

    public String getMerchantID()
    {
        return merchantID;
    }

    public void setMerchantID(String merchantID)
    {
        this.merchantID = merchantID;
    }

    public String getACSUrl()
    {
        return ACSUrl;
    }

    public void setACSUrl(String ACSUrl)
    {
        this.ACSUrl = ACSUrl;
    }

    public String getThreeDFlow()
    {
        return threeDFlow;
    }

    public void setThreeDFlow(String threeDFlow)
    {
        this.threeDFlow = threeDFlow;
    }

    public String getRequestId()
    {
        return requestId;
    }

    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public String getCreditTypeId() {return creditTypeId;}

    public void setCreditTypeId(String creditTypeId) {this.creditTypeId = creditTypeId;}
}
