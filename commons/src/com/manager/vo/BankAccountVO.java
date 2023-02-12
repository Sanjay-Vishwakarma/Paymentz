package com.manager.vo;

/**
 * Created by Sneha on 3/5/2016.
 */
public class BankAccountVO
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

    public String getBankCode()
    {
        return bankCode;
    }

    public void setBankCode(String bankCode)
    {
        this.bankCode = bankCode;
    }

    public String getBankaccountMandateId()
    {
        return bankaccountMandateId;
    }

    public void setBankaccountMandateId(String bankaccountMandateId)
    {
        this.bankaccountMandateId = bankaccountMandateId;
    }

    public String getBankaccountMandateDateOfSignature()
    {
        return bankaccountMandateDateOfSignature;
    }

    public void setBankaccountMandateDateOfSignature(String bankaccountMandateDateOfSignature)
    {
        this.bankaccountMandateDateOfSignature = bankaccountMandateDateOfSignature;
    }

    public String getTransactionDueDate()
    {
        return transactionDueDate;
    }

    public void setTransactionDueDate(String transactionDueDate)
    {
        this.transactionDueDate = transactionDueDate;
    }

    private String holder;
    private String country;
    private String Number;
    private String IBAN;
    private String Bank;
    private String BIC;
    private String BankName;
    private String bankCode;
    private String bankaccountMandateId;
    private String bankaccountMandateDateOfSignature;
    private String transactionDueDate;
}
