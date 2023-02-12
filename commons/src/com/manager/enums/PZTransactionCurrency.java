package com.manager.enums;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 2/6/15
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
public enum PZTransactionCurrency
{

    AUD("AUD"),
    CAD("CAD"),
    EUR("EUR"),
    GBP("GBP"),
    INR("INR"),
    JPY("JPY"),
    PEN("PEN"),
    TRY("TRY"),
    USD("USD");

    private String pzTransactionCurrency;

    PZTransactionCurrency(String pzCurrency)
    {
        this.pzTransactionCurrency=pzCurrency;
    }

    public String toString()
    {
        return pzTransactionCurrency;
    }
}
