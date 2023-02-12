package com.enums;

/**
 * Created by Pradeep on 22/06/2015.
 */
public enum BankApplicationStatus
{
    GENERATED("GENERATED"),
    INVALIDATED("INVALIDATED"),
    VERIFIED("VERIFIED");

    private String bankApplicationStatus;

    BankApplicationStatus(String bankApplicationStatus)
    {
        this.bankApplicationStatus=bankApplicationStatus;
    }

    public String toString()
    {
        return bankApplicationStatus;
    }
}
