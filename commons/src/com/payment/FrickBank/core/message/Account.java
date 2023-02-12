package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/1/13
 * Time: 3:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class Account
{
    @XStreamAlias("Holder")
    private String Holder="Holder";
    @XStreamAlias("Number")
    private String Number="Number";
    @XStreamAlias("Brand")
    private String Brand="Brand";


    public Expire getExpire()
    {
        return expire;
    }

    public void setExpire(Expire expire)
    {
        this.expire = expire;
    }

    @XStreamAlias("Expiry")
    private Expire expire;
    @XStreamAlias("Verification")
    private String Verification="Verification";


    public String getHolder()
    {
        return Holder;
    }

    public void setHolder(String holder)
    {
        Holder = holder;
    }

    public String getNumber()
    {
        return Number;
    }

    public void setNumber(String number)
    {
        Number = number;
    }

    public String getBrand()
    {
        return Brand;
    }

    public void setBrand(String brand)
    {
        Brand = brand;
    }


    public String getVerification()
    {
        return Verification;
    }

    public void setVerification(String verification)
    {
        Verification = verification;
    }

}
