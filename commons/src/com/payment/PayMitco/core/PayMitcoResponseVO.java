package com.payment.PayMitco.core;

import com.payment.common.core.CommResponseVO;

/**
 * Created by ThinkPadT410 on 12/31/2015.
 */
public class PayMitcoResponseVO extends CommResponseVO
{
     String paymentType;
     String paymitcoTransactionType;
     String accountType;
     String accountNumber;
     String routingNumber;
     String customerId;
     String checknumber;
     String transactionID;

     String bankName;
     String bankAddress;
     String bankCity;
     String bankState;
     String bankZipcode;




    public String getPaymitcoTransactionType()
    {
        return paymitcoTransactionType;
    }

    public void setPaymitcoTransactionType(String paymitcoTransactionType)
    {
        this.paymitcoTransactionType = paymitcoTransactionType;
    }

    public String getChecknumber()
    {
        return checknumber;
    }

    public void setChecknumber(String checknumber)
    {
        this.checknumber = checknumber;
    }

    public String getCustomerId()
    {

        return customerId;
    }

    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }

    public String getRoutingNumber()
    {

        return routingNumber;
    }

    public void setRoutingNumber(String routingNumber)
    {
        this.routingNumber = routingNumber;
    }

    public String getAccountNumber()
    {

        return accountNumber;
    }

    public void setAccountNumber(String accountNumber)
    {
        this.accountNumber = accountNumber;
    }

    public String getTransactionID()
    {
        return transactionID;
    }

    public void setTransactionID(String transactionID)
    {
        this.transactionID = transactionID;
    }

    public String getAccountType()
    {

        return accountType;

    }

    public void setAccountType(String accountType)
    {
        this.accountType = accountType;
    }

    public String getPaymentType()
    {
        return paymentType;
    }

    public void setPaymentType(String paymentType)
    {
        this.paymentType = paymentType;
    }

    public String getBankName()
    {
        return bankName;
    }

    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

    public String getBankAddress()
    {
        return bankAddress;
    }

    public void setBankAddress(String bankAddress)
    {
        this.bankAddress = bankAddress;
    }

    public String getBankCity()
    {
        return bankCity;
    }

    public void setBankCity(String bankCity)
    {
        this.bankCity = bankCity;
    }

    public String getBankState()
    {
        return bankState;
    }

    public void setBankState(String bankState)
    {
        this.bankState = bankState;
    }

    public String getBankZipcode()
    {
        return bankZipcode;
    }

    public void setBankZipcode(String bankZipcode)
    {
        this.bankZipcode = bankZipcode;
    }
}
