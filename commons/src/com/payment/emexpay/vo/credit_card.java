package com.payment.emexpay.vo;

/**
 * Created by Admin on 2/24/2018.
 */
public class credit_card
{
    String number;
    String verification_value;
    String holder;
    String exp_month;
    String exp_year;
    String token;


    public String getNumber()
    {
        return number;
    }

    public void setNumber(String number)
    {
        this.number = number;
    }

    public String getVerification_value()
    {
        return verification_value;
    }

    public void setVerification_value(String verification_value)
    {
        this.verification_value = verification_value;
    }

    public String getHolder()
    {
        return holder;
    }

    public void setHolder(String holder)
    {
        this.holder = holder;
    }

    public String getExp_month()
    {
        return exp_month;
    }

    public void setExp_month(String exp_month)
    {
        this.exp_month = exp_month;
    }

    public String getExp_year()
    {
        return exp_year;
    }

    public void setExp_year(String exp_year)
    {
        this.exp_year = exp_year;
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
