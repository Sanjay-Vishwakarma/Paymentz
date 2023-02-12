package com.payment.cardpay;

import com.directi.pg.Functions;

import java.util.HashMap;

/**
 * Created by Admin on 9/11/18.
 */
public class CardPayErrorCode
{
    private static HashMap<String,String> errorCodes=new HashMap<>();
    static Functions functions = new Functions();

    static
    {
        errorCodes.put("01", "System malfunction");
        errorCodes.put("02", "Cancelled by customer");
        errorCodes.put("03", "Declined by Antifraudk");
        errorCodes.put("04", "Declined by 3-D Secure");
        errorCodes.put("05", "Only 3-D Secure transactions are allowed");
        errorCodes.put("06", "3-D Secure availability is unknown");
        errorCodes.put("07", "Limit reached");
        errorCodes.put("10", "Declined by bank (reason not specified)");
        errorCodes.put("11", "Common decline by bank");
        errorCodes.put("13", "Insufficient funds");
        errorCodes.put("14", "Card limit reached");
        errorCodes.put("15", "Incorrect card data");
        errorCodes.put("16", "Declined by bank’s antifraud");
        errorCodes.put("17", "Bank’s malfunction");
        errorCodes.put("18", "Connection problem");
    }

    public static String getDescription(String code){
        String description="";
        if (functions.isValueNull(code))
            description=errorCodes.get(code);
        return description;
    }
}
