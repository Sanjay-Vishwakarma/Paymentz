package com.payment.cupUPI;

import java.util.HashMap;

/**
 * Created by Jitendra on 16-Jul-19.
 */
public class UnionPayInternationalErrorCode
{
    private static HashMap<String,String> errorCodes=new HashMap<>();

    static
    {
        errorCodes.put("00","Success");
        errorCodes.put("A6","Partial success");
        errorCodes.put("01","Transaction failed. For details please inquire overseas service hotline.");
        errorCodes.put("02","System is not started or temporarily down, please try again later");
        errorCodes.put("03","Transaction communication time out, please initiate inquiry transaction");
        errorCodes.put("05","Transaction has been accepted, please inquire about transaction result shortly");
        errorCodes.put("06","System is busy, please retry it later.");

        errorCodes.put("10","Message format error");
        errorCodes.put("11","Verify signature error");
        errorCodes.put("12","Repeat transaction");
        errorCodes.put("13","Message transaction key element missing");
        errorCodes.put("30","Transaction failed, please try using other UnionPay card for payment or contact overseas service hotline");
        errorCodes.put("31","Merchant state incorrect. The payment is not completed within the order timeout");
        errorCodes.put("32","No such transaction right");
        errorCodes.put("33","Transaction amount exceeds limit");
        errorCodes.put("34","Could not find this transaction");
        errorCodes.put("35","Original transaction does not exist or state is incorrect");
        errorCodes.put("36","Does not match original transaction information");
        errorCodes.put("37","Max number of inquiries exceeded or too frequent operations");
        errorCodes.put("38","UnionPay risk constraint");
        errorCodes.put("39","Transaction is not within the acceptance time range");
        errorCodes.put("42","Balance deduction successful but transaction exceeded payment time limit");
        errorCodes.put("43","Business not allowed, please contact overseas service hotline for help.");
        errorCodes.put("44","Wrong number entered or business not opened, please contact overseas service hotline for help");
        errorCodes.put("45","The original transaction has been refunded or cancelled successfully");
        errorCodes.put("60","Transaction failure, for details, please inquire with your issuer");
        errorCodes.put("61","Card number entered is invalid, please double check and enter");
        errorCodes.put("62","Transaction failed, issuer does not support this merchant, please change to another bank card");
        errorCodes.put("63","Card state is incorrect");
        errorCodes.put("64","Card balance is insufficient");
        errorCodes.put("65","Error with PIN, expiration date, or CVN2 entered, transaction failure");
        errorCodes.put("66","Cardholder identity information or mobile number entered are incorrect,verification failure");
        errorCodes.put("67","Limit on number of PIN entry attempts exceeded");
        errorCodes.put("68","Your bank card currently does not support this business, please inquire with business, please inquire with your bank or overseas service hotline for help");
        errorCodes.put("69","Time limit on entry exceeded, transaction failure");
        errorCodes.put("70","Transaction has been redirected, waiting for cardholder input");
        errorCodes.put("71","Dynamic password or SMS verification code validation failure");
        errorCodes.put("72","You have not signed up for UnionPay card-not-present payment service at the bank counter or on your personal online bank, please go to a bank counter or\n" +
                "access your online banking to activate it or contact overseas service hotline for help.");
        errorCodes.put("73","Payment card has exceeded expiration date");
        errorCodes.put("76","Requires encryption verification for activation");
        errorCodes.put("77","Bank card has not been activated for authenticated payment");
        errorCodes.put("78","Issuer transaction rights limited, for details please contact your issuer");
        errorCodes.put("79","The bank card is valid, but issuer does not support SMS verification");
        errorCodes.put("80","Transaction failed and the token has expired");
        errorCodes.put("81","Monthly accumulated transaction counter (amount) exceeded");
        errorCodes.put("82","PIN needs to be verified");
        errorCodes.put("84","PIN is required but not submitted");
        errorCodes.put("85","Transaction failed, the marketing rules are not met");
        errorCodes.put("86","QRC status error");
        errorCodes.put("88","QRC not found");
        errorCodes.put("89","No Token found, invalid TR status or invalid Token status");
        errorCodes.put("98","File does not exist");
        errorCodes.put("99","General error");
    }

    public static String getDescription(String code)
    {
        String description=errorCodes.get(code);
        return description;
    }
}
