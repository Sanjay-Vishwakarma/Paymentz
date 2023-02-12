package com.payment.europay.core;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/4/13
 * Time: 1:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class EuroPayErrorCode
{
    private static HashMap<String, String> errorCodes = new HashMap<String, String>();

    static
    {
        errorCodes.put("0", "Unknown error	");
        errorCodes.put("0","REQUEST_SUCCESS");
        errorCodes.put("1","REQUEST_FAILURE");
        errorCodes.put("2","REQUEST_PARTIAL_SUCCESS");
        errorCodes.put("3","SERVICE_OFFLINE");
        errorCodes.put("4","ACCESS_DENIED");
        errorCodes.put("5","LOGIN_EXPIRED");
        errorCodes.put("6","LOGIN_DISABLED");
        errorCodes.put("7","LOGIN_INVALID");
        errorCodes.put("8","DEVICE_NOT_ACTIVATED");
        errorCodes.put("100","ERROR_REQ_NO_KEY");
        errorCodes.put("101","ERROR_REQ_DEAD_KEY");
        errorCodes.put("102","ERROR_STRUCTURE");
        errorCodes.put("103","ERROR_VALIDATION");
        errorCodes.put("104","ERROR_INCOMPLETE_DATA");
        errorCodes.put("105","ERROR_EXPIRED");
        errorCodes.put("106","ERROR_DUPLICATION");
        errorCodes.put("107","ERROR_ENCRYPTION");
        errorCodes.put("108","ERROR_CREDITCARD");
        errorCodes.put("109","ERROR_CREDITCARD_VERIFICATION");
        errorCodes.put("110","ERROR_TRANSACTION");
        errorCodes.put("116","ERROR_UNKNOWN");
        errorCodes.put("117","ERROR_NO_MATCH");
        errorCodes.put("118","ERROR_KEYGENERATION");
        errorCodes.put("119","ERROR_MAC");
        errorCodes.put("120","ERROR_AUTHENTICATION");
        errorCodes.put("200","TX_DECLINE_RISK");
        errorCodes.put("201","TX_DECLINE_SETTINGS");
        errorCodes.put("202","TX_DECLINE_ACQUIRER");
        errorCodes.put("203","TX_DECLINE_ISSUER");
        errorCodes.put("204","TX_DECLINE_CARD_VERIFICATION");
        errorCodes.put("205","TX_STATE_IN_REFUND");
    }


    public static String getDescritopn(String code)
    {
        String errorDescription =  errorCodes.get(code);
        if(errorDescription==null)
        {
            errorDescription = code +"-Error processing transaction";
        }

        return errorDescription;



    }
}
