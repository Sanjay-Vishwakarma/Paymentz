package com.payment.FrickBank.core;

import com.payment.common.core.CommResponseVO;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/2/13
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class FrickBankResponseVO extends CommResponseVO
{
    public String getShortID()
    {
        return ShortID;
    }

    public void setShortID(String shortID)
    {
        ShortID = shortID;
    }

    public String getUniqueID()
    {
        return UniqueID;
    }

    public void setUniqueID(String uniqueID)
    {
        UniqueID = uniqueID;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getResult()
    {
        return Result;
    }

    public void setResult(String result)
    {
        Result = result;
    }

    public String getReturn()
    {
        return Return;
    }

    public void setReturn(String aReturn)
    {
        Return = aReturn;
    }

    public String getSecurityHash()
    {
        return securityHash;
    }

    public void setSecurityHash(String securityHash)
    {
        this.securityHash = securityHash;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    private String ShortID;
    private String UniqueID;
    private String timestamp;
    private String Result;
    private String Return;
    private String securityHash;
    private String amount;
    private String currency;

    public String getResponsecode()
    {
        return responsecode;
    }

    public void setResponsecode(String responsecode)
    {
        this.responsecode = responsecode;
    }

    private String responsecode;

    public String getReferanceid()
    {
        return referanceid;
    }

    public void setReferanceid(String referanceid)
    {
        this.referanceid = referanceid;
    }

    public String getFxdate()
    {
        return fxdate;
    }

    public void setFxdate(String fxdate)
    {
        this.fxdate = fxdate;
    }

    private String referanceid;
    private String fxdate;
}
