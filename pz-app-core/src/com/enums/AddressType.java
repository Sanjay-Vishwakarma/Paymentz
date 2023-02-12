package com.enums;

/**
 * Created by admin on 7/10/2017.
 */
public enum AddressType
{
    UtilityBill("UtilityBill"),
    License("License");

    private String Addresstype;

    AddressType(String idtype)
    {
        this.Addresstype=idtype;
    }

    public String toString()
    {
        return Addresstype;
    }
}
