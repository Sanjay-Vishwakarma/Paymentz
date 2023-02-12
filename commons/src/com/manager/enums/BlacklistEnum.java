package com.manager.enums;

/**
 * Created by Admin on 8/17/2020.
 */
public enum BlacklistEnum
{
    Chargeback_Received("Chargeback Received"),
    Fraud_Received("Fraud Received"),
    Stolen_Card("Stolen Card");

    private String blacklistEnum;

    BlacklistEnum (String blacklistEnum)
    {
        this.blacklistEnum=blacklistEnum;
    }
    public String toString()
    {
        return blacklistEnum;
    }

    public static BlacklistEnum getEnum(String value)
    {
        try
        {
            return BlacklistEnum.valueOf(value);
        } catch (IllegalArgumentException iae)
        {
            return null;
        }
    }

}
