package com.manager.vo;

import com.payment.common.core.CommRequestVO;

/**
 * Created by ThinkPadT410 on 12/21/2015.
 */
public class ReserveField2VO extends CommRequestVO
{
    private String routingNumber;
    private String accountNumber;
    private String accountType;
    private String bankName;
    private String bankAddress;
    private String bankCity;
    private String bankState;
    private String bankZipcode;
    private String checkNumber;

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
    public String getAccountType()
    {
        return accountType;
    }
    public void setAccountType(String accountType)
    {
        this.accountType = accountType;
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

    public String getCheckNumber()
    {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber)
    {
        this.checkNumber = checkNumber;
    }
}