package com.payment.Enum;

/**
 * Created by Admin on 8/19/2020.
 */
public enum EU_Country
{
    AUT("AUT"),
    BEL("BEL"),
    BGR("BGR"),
    HRV("HRV"),
    CYP("CYP"),
    CZE("CZE"),
    DNK("DNK"),
    EST("EST"),
    FIN("FIN"),
    FRA("FRA"),
    DEU("DEU"),
    GRC("GRC"),
    HUN("HUN"),
    IRL("IRL"),
    ITA("ITA"),
    LVA("LVA"),
    LTU("LTU"),
    LUX("LUX"),
    MLT("MLT"),
    NLD("NLD"),
    POL("POL"),
    PRT("PRT"),
    ROU("ROU"),
    SVK("SVK"),
    SVN("SVN"),
    ESP("ESP"),
    SWE("SWE");

    private String country;


    EU_Country(String country)
    {
        this.country=country;
    }
    public static EU_Country getEnum(String value)
    {
        try
        {
            return EU_Country.valueOf(value);
        } catch (IllegalArgumentException iae)
        {
            return null;
        }
    }

    public String toString()
    {
        return country;
    }


}
