package com.payment.cybersource;

import java.util.HashMap;

/**
 * Created by Jitendra on 04-Apr-19.
 */
public class CyberSourceErrorCode
{
    private static HashMap<String,String> errorCodes=new HashMap<>();

    static
    {
        errorCodes.put("100","Successful transaction.");
        errorCodes.put("101","The request is missing one or more required fields.");
        errorCodes.put("102","One or more fields in the request contains invalid data.");
        errorCodes.put("104","The merchant reference code for this authorization request matches the merchant reference code of another authorization request that you sent within the past 15 minutes.");
        errorCodes.put("110","Only a partial amount was approved.");
        errorCodes.put("150","General system failure.");
        errorCodes.put("151","The request was received but there was a server timeout. This error does not include timeouts between the client and the server.");
        errorCodes.put("152","The request was received, but a service did not finish running in time.");
        errorCodes.put("200","The authorization request was approved by the issuing bank but declined by CyberSource because it did not pass the Address Verification System (AVS) check.");
        errorCodes.put("201","The issuing bank has questions about the request. You do not receive an authorization code programmatically, but you might receive one verbally by calling the processor.");
        errorCodes.put("202","Expired card. You might also receive this value if the expiration date you provided does not match the date the issuing bank has on file.");
        errorCodes.put("203","General decline of the card. No other information was provided by the issuing bank.");
        errorCodes.put("204","Insufficient funds in the account.");
        errorCodes.put("205","Stolen or lost card.");
        errorCodes.put("207","Issuing bank unavailable.");
        errorCodes.put("208","Inactive card or card not authorized for card-not-present transactions.");
        errorCodes.put("209","CVN did not match.");
        errorCodes.put("210","The card has reached the credit limit.");
        errorCodes.put("211","Invalid CVN.");
        errorCodes.put("230","The authorization request was approved by the issuing bank but declined by CyberSource because it did not pass the CVN check.");
        errorCodes.put("231","Invalid account number.");
        errorCodes.put("232","The card type is not accepted by the payment processor.");
        errorCodes.put("233","General decline by the processor.");
        errorCodes.put("234","There is a problem with the information in your CyberSource account.");
        errorCodes.put("235","The requested capture amount exceeds the originally authorized amount.");
        errorCodes.put("236","Processor failure.");
        errorCodes.put("237","The authorization has already been reversed.");
        errorCodes.put("238","The authorization has already been captured.");
        errorCodes.put("239","The requested transaction amount must match the previous transaction amount.");
        errorCodes.put("240","The card type sent is invalid or does not correlate with the credit card number.");
        errorCodes.put("241","The request ID is invalid.");
        errorCodes.put("242","There is no corresponding, unused authorization record. Occurs if there was not a\n"+
                "previously successful authorization request or if the previously successful authorization has already been used by another capture request.");
        errorCodes.put("243","The transaction has already been settled or reversed.");
        errorCodes.put("246","The capture or credit is not voidable because the capture or credit information\n" +
                "has already been submitted to your processor Or You requested a void for a type of transaction that cannot be voided.");
        errorCodes.put("247","You requested a credit for a capture that was previously voided.");
        errorCodes.put("250","The request was received, but there was a timeout at the payment processor.");
        errorCodes.put("254","Stand-alone credits are not allowed.");
    }

    public static String getDescription(String code)
    {
        String description=errorCodes.get(code);
        return description;
    }
}
