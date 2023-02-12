package com.payment.kotakbank.core;

import com.payment.common.core.CommResponseVO;

/**
 * Created by Nikita on 3/20/2017.
 */
public class KotakResponseVO extends CommResponseVO
{
    String batchNumber="";
    String cavv="";
    String eci="";
    String authStatus="";
    String enrolled="";
    String authCode="";
    String retRefNumber="";
    String responseCode="";
    String remark="";

    public String getBatchNumber()
    {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber)
    {
        this.batchNumber = batchNumber;
    }

    public String getCavv()
    {
        return cavv;
    }

    public void setCavv(String cavv)
    {
        this.cavv = cavv;
    }

    public String getEci()
    {
        return eci;
    }

    public void setEci(String eci)
    {
        this.eci = eci;
    }

    public String getAuthStatus()
    {
        return authStatus;
    }

    public void setAuthStatus(String authStatus)
    {
        this.authStatus = authStatus;
    }

    public String getEnrolled()
    {
        return enrolled;
    }

    public void setEnrolled(String enrolled)
    {
        this.enrolled = enrolled;
    }

    public String getAuthCode()
    {
        return authCode;
    }

    public void setAuthCode(String authCode)
    {
        this.authCode = authCode;
    }

    public String getRetRefNumber()
    {
        return retRefNumber;
    }

    public void setRetRefNumber(String retRefNumber)
    {
        this.retRefNumber = retRefNumber;
    }

    public String getResponseCode()
    {
        return responseCode;
    }

    public void setResponseCode(String responseCode)
    {
        this.responseCode = responseCode;
    }

    @Override
    public String getRemark()
    {
        return remark;
    }

    @Override
    public void setRemark(String remark)
    {
        this.remark = remark;
    }
}
