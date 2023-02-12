package com.payment.emax_high_risk.core;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 2/18/15
 * Time: 6:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreditCard
{
    public CreditCard creditCard;
    public String holder;
    public String stamp;
    public String token;

    /*public CreditCard getCreditCard()
    {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard)
    {
        this.creditCard = creditCard;
    }*/

    public String getHolder()
    {
        return holder;
    }

    public void setHolder(String holder)
    {
        this.holder = holder;
    }

    public String getStamp()
    {
        return stamp;
    }

    public void setStamp(String stamp)
    {
        this.stamp = stamp;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }
}
