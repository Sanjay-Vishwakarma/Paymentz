package com.payment.response;

/**
 * Created with IntelliJ IDEA.
 * User: Chandan
 * Date: 2/16/13
 * Time: 10:46 AM
 * To change this template use File | Settings | File Templates.
 */
public final class PZCancelResponse extends PZResponse
{
    String bankStatus;
    String resultCode;
    String resultDescription;

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

}
