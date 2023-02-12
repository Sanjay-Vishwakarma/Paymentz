package com.manager.enums;

/**
 * Created by admin on 10/9/2015.
 */
public enum InputNameEnum
{
    USERID("USERID"),
    TOID("TOID"),
    AUTOREDIRECT("AUTOREDIRECT"),
    TESTMODE("TESTMODE"),
    CURRENCY("CURRENCY"),
    ONLINEPROCESSINGURL("ONLINEPROCESSINGURL"),
    OFFLINEPROCESSINGURL("OFFLINEPROCESSINGURL"),
    TOTYPE("TOTYPE"),
    AMOUNT("AMOUNT"),
    ORDERDESCRIPTION("ORDERDESCRIPTION"),
    DESCRIPTION("DESCRIPTION"),
    REDIRECTURL("REDIRECTURL"),
    COUNTRYCODE("COUNTRYCODE"),
    CITY("CITY"),
    STATE("STATE"),
    ZIP("ZIP"),
    ADDRESS("ADDRESS"),
    TELEPHONENO("TELEPHONENO"),
    EMAILADDRESS("EMAILADDRESS"),
    TELEPHONECC("TELEPHONECC"),
    FIRSTNAME("FIRSTNAME"),
    LASTNAME("LASTNAME"),
    CARDNUMBER("CARDNUMBER"),
    FIRSTSIX_CARDNUMBER("FIRSTSIX_CARDNUMBER"),
    LASTFOUR_CARDNUMBER("LASTFOUR_CARDNUMBER"),
    CVV("CVV"),
    EXPIRYMONTH("LASTNAME"),
    EXPIRYYEAR("LASTNAME"),
    CARDHOLDERFIRSTNAME("CARDHOLDERFIRSTNAME"),
    CARDHOLDERLASTNAME("CARDHOLDERLASTNAME"),
    BOILEDNAME("BOILEDNAME"),
    TERMINALID("TERMINALID"),
    PAYMENTTYPE("PAYMENTTYPE"),
    CARDTYPE("CARDTYPE");

    private String inputName;

    InputNameEnum(String inputName)
    {
        this.inputName=inputName;
    }

    public String toString()
    {
        return inputName;
    }

    public static InputNameEnum getEnum(String value)
    {
        try
        {
            return InputNameEnum.valueOf(value);
        } catch (IllegalArgumentException iae)
        {
            return null;
        }
    }
}
