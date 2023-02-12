package com.payment.transactium.psp.ps.v1003;

import java.util.HashMap;

/**
 * Created by Uday on 3/5/18.
 */
public class TransactiumErrorCode
{
   private static HashMap<String,String> errorCodes=new HashMap<>();

    static
    {
        errorCodes.put("00", "Successful approval/completion or that VIP PIN verification is valid");
        errorCodes.put("01", "Refer to card issuer");
        errorCodes.put("02", "Refer to card issuer, special condition");
        errorCodes.put("03", "Invalid merchant or service provider");
        errorCodes.put("04", "Pickup");
        errorCodes.put("05", "Do not honor");
        errorCodes.put("06", "General error");
        errorCodes.put("07", "Pickup card, special condition (other than lost/stolen card)");
        errorCodes.put("08", "Honor with identification");
        errorCodes.put("09", "Request in progress");
        errorCodes.put("10", "Partial approval");
        errorCodes.put("11", "VIP approval");
        errorCodes.put("12", "Invalid transaction");
        errorCodes.put("13", "Invalid amount (currency conversion field overflow) or amount exceeds maximum for card program");
        errorCodes.put("14", "Invalid account number (no such number)");
        errorCodes.put("15", "No such issuer");
        errorCodes.put("16", "Insufficient funds");
        errorCodes.put("17", "Customer cancellation");
        errorCodes.put("19", "Re-enter transaction");
        errorCodes.put("20", "Invalid response");
        errorCodes.put("21", "No action taken (unable to back out prior transaction)");
        errorCodes.put("22", "Suspected Malfunction");
        errorCodes.put("25", "Unable to locate record in file, or account number is missing from the inquiry");
        errorCodes.put("28", "File is temporarily unavailable");
        errorCodes.put("30", "Format error");
        errorCodes.put("41", "Merchant should retain card (card reported lost)");
        errorCodes.put("43", "Merchant should retain card (card reported stolen)");
        errorCodes.put("51", "Insufficient funds");
        errorCodes.put("52", "No checking account");
        errorCodes.put("53", "No savings account");
        errorCodes.put("54", "Expired card");
        errorCodes.put("55", "Incorrect PIN");
        errorCodes.put("57", "Transaction not permitted to cardholder");
        errorCodes.put("58", "Transaction not allowed at terminal");
        errorCodes.put("59", "Suspected fraud");
        errorCodes.put("61", "Activity amount limit exceeded");
        errorCodes.put("62", "Restricted card (for example, in country exclusion table)");
        errorCodes.put("63", "Security violation");
        errorCodes.put("65", "Activity count limit exceeded");
        errorCodes.put("68", "Response received too late");
        errorCodes.put("75", "Allowable number of PIN-entry tries exceeded");
        errorCodes.put("76", "Unable to locate previous message (no match on retrieval reference number)");
        errorCodes.put("77", "Previous message located for a repeat or reversal, but repeat or reversal data are inconsistent with original message");
        errorCodes.put("78", "’Blocked, first used’—The transaction is from a new cardholder, and the card has not been properly unblocked.");
        errorCodes.put("80", "Visa transactions: credit issuer unavailable. Private label and check acceptance: Invalid date");
        errorCodes.put("81", "PIN cryptographic error found (error found by VIC security module during PIN decryption)");
        errorCodes.put("82", "Negative CAM, dCVV, iCVV, or CVV results");
        errorCodes.put("83", "Unable to verify PIN");
        errorCodes.put("85", "No reason to decline a request for account number verification, address verification, CVV2 verification; or a credit voucher or merchandise return");
        errorCodes.put("91", "Issuer unavailable or switch inoperative (STIP not applicable or available for this transaction)");
        errorCodes.put("92", "Destination cannot be found for routing");
        errorCodes.put("93", "Transaction cannot be completed, violation of law");
        errorCodes.put("94", "Duplicate transmission");
        errorCodes.put("95", "Reconcile error");
        errorCodes.put("96", "System malfunction, System malfunction or certain field error conditions");
        errorCodes.put("B1", "Surcharge amount not permitted on Visa cards (U.S. acquirers only)");
        errorCodes.put("N0", "Force STIP");
        errorCodes.put("N3", "Cash service not available");
        errorCodes.put("N4", "Cashback request exceeds issuer limit");
        errorCodes.put("N7", "Decline for CVV2 failure");
        errorCodes.put("P2", "Invalid biller information");
        errorCodes.put("P5", "PIN change/unblock request declined");
        errorCodes.put("P6", "Unsafe PIN");
        errorCodes.put("Q1", "Card authentication failed");
        errorCodes.put("R0", "Stop payment order");
        errorCodes.put("R1", "Revocation of authorization order");
        errorCodes.put("R3", "Revocation of all authorizations order");
        errorCodes.put("XA", "Forward to issuer");
        errorCodes.put("XD", "Forward to issuer");
        errorCodes.put("Z3", "Unable to go online");

    }

    public static String getDescription(String code){
        String description=errorCodes.get(code);
        return description;
    }

}
