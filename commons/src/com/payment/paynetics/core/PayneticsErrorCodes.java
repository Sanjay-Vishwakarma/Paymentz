package com.payment.paynetics.core;

import com.directi.pg.Functions;

import java.util.HashMap;

/**
 * Created by Sandip on 10/14/2017.
 */
public class PayneticsErrorCodes
{
    private static HashMap<String, String> errorCode = new HashMap();
    private static Functions functions=new Functions();
    static
    {
        errorCode.put("00","Approved or completed successfully");
        errorCode.put("02","Call Voice-authorization number;Initialization Data");
        errorCode.put("03","Invalid merchant number");
        errorCode.put("04","Retain card");
        errorCode.put("05","Declined by issuer");
        errorCode.put("06","Sequence- generation- number error - diagnostics necessary; the POS Terminal must carry out reconciliation with a 0800 message");
        errorCode.put("09","The value 09 has special significance as it indicates a wait message and the POSSystem should expect to wait at most 30 seconds more. The host may send several of these wait messages until the true reply is ready. A wait message contains only the following fields: Message Type, Bit Map, PAN, System Trace Audit number, POS Terminal ID Code, Response Code, and Additional Response Data.");
        errorCode.put("10","Partial approval");
        errorCode.put("12","Invalid transaction");
        errorCode.put("13","Invalid amount");
        errorCode.put("14","invalid card");
        errorCode.put("21","No action taken");
        errorCode.put("30","Format Error");
        errorCode.put("33","Card expired");
        errorCode.put("34","Suspicion of Manipulation");
        errorCode.put("40","Requested function not supported");
        errorCode.put("43","Stolen Card, pick up");
        errorCode.put("55","Incorrect personal identification number");
        errorCode.put("56","Card not in authorizer's database");
        errorCode.put("57","referencing transaction (e.g. reversal, Booking pre-authorization ...) was not carried out with the card which was used for the original transaction.");
        errorCode.put("58","Terminal ID unknown");
        errorCode.put("62","Restricted Card");
        errorCode.put("64","The transaction amount of the referencing transaction is higher than the transaction amount of the original transaction");
        errorCode.put("65","Contactless request declined – retry in contact mode");
        errorCode.put("75","PIN entered incorrectly too often");
        errorCode.put("77","PIN entry necessary");
        errorCode.put("78","Stop payment order (for forwarding the Visa response code R0 of the Visa BASE I interface): the transaction was declined or returned because the cardholder requested that payment of a specific recurring or installment payment transaction be stopped.");
        errorCode.put("79","Revocation of authorization order (for forwarding the Visa response codes R1 or R3 of the Visa BASE I interface): the transaction was declined or returned because the cardholder requested that payment of all recurring or installment payment transactions for a specific merchant account be stopped.");
        errorCode.put("80","Amount no longer available");
        errorCode.put("81","Message-flow error85: Cash back declined – pls. retry purchase only");
        errorCode.put("91","Card issuer temporarily not reachable");
        errorCode.put("92","The card type is not processed by the authorization center");
        errorCode.put("96"," Processing temporarily not possible");
        errorCode.put("97"," Security breach - MAC check indicates error condition");
        errorCode.put("98"," Date and time not plausible - The POS Terminal must set itself to the date and time of the response message");
        errorCode.put("99"," Error in PAC encryption detected Any other code sent by the Authorization Host = General decline");
    }
    public static String getErrorCode(String errorCode){
        String errorDescription= PayneticsErrorCodes.errorCode.get(errorCode);
        if(functions.isValueNull(errorDescription)){return errorDescription;}
        else{return "Transaction declined";}
    }
}
