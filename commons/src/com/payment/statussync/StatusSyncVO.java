package com.payment.statussync;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 11/5/14
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatusSyncVO
{
    public String getTrackingId()
    {
        return trackingId;
    }

    public void setTrackingId(String trackingId)
    {
        this.trackingId = trackingId;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCardNumber()
    {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber)
    {
        this.cardNumber = cardNumber;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    private String trackingId;
    private String accountId;
    private String email;
    private String name;
    private String cardNumber;
    private String status;
}
