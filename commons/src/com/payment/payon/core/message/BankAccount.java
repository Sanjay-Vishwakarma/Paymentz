package com.payment.payon.core.message;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 10:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class BankAccount
{
    public String getHolder()
    {
        return holder;
    }

    public void setHolder(String holder)
    {
        this.holder = holder;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getNumber()
    {
        return Number;
    }

    public void setNumber(String number)
    {
        Number = number;
    }

    public String getIBAN()
    {
        return IBAN;
    }

    public void setIBAN(String IBAN)
    {
        this.IBAN = IBAN;
    }

    public String getBank()
    {
        return Bank;
    }

    public void setBank(String bank)
    {
        Bank = bank;
    }

    public String getBIC()
    {
        return BIC;
    }

    public void setBIC(String BIC)
    {
        this.BIC = BIC;
    }

    public String getBankName()
    {
        return BankName;
    }

    public void setBankName(String bankName)
    {
        BankName = bankName;
    }

    private String holder;
    private String country;
    private String Number;
    private String IBAN;
    private String Bank;
    private String BIC;
    private String BankName;



}
