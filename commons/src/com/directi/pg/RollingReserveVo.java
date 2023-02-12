package com.directi.pg;

/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 5/29/14
 * Time: 3:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class RollingReserveVo
{


    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public String getTrackingId()
    {
        return trackingId;
    }

    public void setTrackingId(String trackingId)
    {
        this.trackingId = trackingId;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getRrAmount()
    {
        return rrAmount;
    }

    public void setRrAmount(String rrAmount)
    {
        this.rrAmount = rrAmount;
    }

    private String description;
    private String accountId;
    private String trackingId;
    private String amount;
    private String rrAmount;

}
