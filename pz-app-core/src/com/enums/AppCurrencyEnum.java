package com.enums;

/**
 * Created by admin on 7/26/2017.
 */
public enum AppCurrencyEnum
{
    USD("USD"),
    GBP("GBP"),
    EUR("EUR"),
    JPY("JPY"),
    PEN("PEN"),
    HKD("HKD"),
    AUD("AUD"),
    CAD("CAD"),
    DKK("DKK"),
    SEK("SEK"),
    NOK("NOK"),
    INR("INR");

    private String CurrencyEnum;
    AppCurrencyEnum(String idtype)
    {
        this.CurrencyEnum=idtype;
    }

    public String toString()
    {
        return CurrencyEnum;
    }
}
