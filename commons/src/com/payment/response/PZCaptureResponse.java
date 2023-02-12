package com.payment.response;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 2/16/13
 * Time: 10:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class PZCaptureResponse extends PZResponse
{
    String bankStatus;
    String resultCode;
    String resultDescription;
    String captureCode;

    public String getBankStatus()
    {
        return bankStatus;
    }

    public void setBankStatus(String bankStatus)
    {
        this.bankStatus = bankStatus;
    }

    public String getResultCode()
    {
        return resultCode;
    }

    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }

    public String getResultDescription()
    {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription)
    {
        this.resultDescription = resultDescription;
    }

    public String getCaptureCode()
    {
        return captureCode;
    }

    public void setCaptureCode(String captureCode)
    {
        this.captureCode = captureCode;
    }
}
