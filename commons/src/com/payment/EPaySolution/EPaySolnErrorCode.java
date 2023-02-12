package com.payment.EPaySolution;

import com.directi.pg.Functions;

import java.util.HashMap;

/**
 * Created by Admin on 27-Sep-18.
 */
class EPaySolnErrorCode
{
    private static HashMap<String,String> errorCodes=new HashMap<>();
    static Functions functions = new Functions();

    static
    {
        errorCodes.put("92001", "Merchant Number cannot be empty");
        errorCodes.put("92002", "Merchant Number format is incorrect");
        errorCodes.put("92003", "Order Number cannot be empty");
        errorCodes.put("92004", "Order Number cannot exceed 50 characters");
        errorCodes.put("92005", "Order Amount cannot be empty!");
        errorCodes.put("92006", "Order Amount format is incorrect");
        errorCodes.put("92007", "Order currency cannot be empty");
        errorCodes.put("92008", "Order currency is incorrect");
        errorCodes.put("92011", "Gateway Number cannot empty!");
        errorCodes.put("92013", "returnURL cannot be empty");
        errorCodes.put("92014", "returnURL cannot exceed 255 characters");
        errorCodes.put("92015", "email cannot be empty");
        errorCodes.put("92016", "email is incorrect");
        errorCodes.put("92017", "shipAddress cannot be empty");
        errorCodes.put("92018", "shipAddress cannot exceed 200 characters");
        errorCodes.put("92019", "shipCity cannot be empty");
        errorCodes.put("92020", "shipCity cannot exceed 100 characters");
        errorCodes.put("92021", "shipState cannot exceed 100 characters");
        errorCodes.put("92022", "shipCountry cannot be empty");
        errorCodes.put("92023", "shipCountry cannot exceed 100 characters");
        errorCodes.put("92024", "sFirstName cannot be empty");
        errorCodes.put("92025", "sFirstName cannot exceed 100 characters");
        errorCodes.put("92026", "sLastName cannot be empty");
        errorCodes.put("92027", "sLastName cannot exceed 100 characters");
        errorCodes.put("92028", "shipZip cannot be empty");
        errorCodes.put("92029", "shipZip cannot exceed 100 characters");
        errorCodes.put("92030", "signData cannot be empty");
        errorCodes.put("92031", "signData cannot exceed 64 characters");
        errorCodes.put("92032", "goodsList incorrect data");
        errorCodes.put("92033", "The length of cardNo is incorrect");
        errorCodes.put("92034", "AE card No is less than 15 numbers");
        errorCodes.put("92035", "Please input 3-digit Verification code");
        errorCodes.put("92036", "The cardExpireMonth should be between 01-12");
        errorCodes.put("92037", "Please input 2-digit only for cardExpireYear");
        errorCodes.put("92038", "Please input between 2-255 characters only for issuingBank");
        errorCodes.put("92039", "ip cannot be empty");
        errorCodes.put("92040", "broserType cannot be empty");
        errorCodes.put("92041", "broserLang cannot be empty");
        errorCodes.put("90005", "address can be submitted only in terms of https not http.");
        errorCodes.put("90006", "Incorrect merN");
        errorCodes.put("90018", "Incorrect gateway No");
        errorCodes.put("90007", "Invalid account");
        errorCodes.put("90008", "Unauthorized merchant");
        errorCodes.put("90009", "Order Amount must be greater than 0");
        errorCodes.put("90010", "The maximum value of payment from testing account cannot exceed 1dollar");
        errorCodes.put("90011", "Merchant website inquiry error");
        errorCodes.put("90012", "Unauthorized URL");
        errorCodes.put("90013", "signData verify failed");
        errorCodes.put("90014", "Duplicated merchant order No");
        errorCodes.put("90017", "Frequently payments at same IP");
        errorCodes.put("91018", "Master channel is unavailable");
        errorCodes.put("91019", "Visa channel is unavailable");
        errorCodes.put("91020", "JCB channel is unavailable");
        errorCodes.put("91021", "AE channel is unavailable");
        errorCodes.put("91022", "High risk");
        errorCodes.put("91023", "High risk");
        errorCodes.put("91024", "High risk");
        errorCodes.put("91025", "Channel is unavailable temporarily");
        errorCodes.put("91026", "High risk");
        errorCodes.put("91028", "High risk");
        errorCodes.put("91029", "Number limitation of successful payment");
        errorCodes.put("91030", "High risk");
        errorCodes.put("91031", "Blacklist  intercept");
        errorCodes.put("91032", "High risk");
        errorCodes.put("91033", "High risk");
        errorCodes.put("91034", "System exceptions");
        errorCodes.put("91035", "High risk");
        errorCodes.put("91036", "High risk");
        errorCodes.put("91037", "High risk");
        errorCodes.put("91038", "High risk");
        errorCodes.put("91040", "System exceptions");
        errorCodes.put("91043", "High risk");
        errorCodes.put("91044", "System exceptions");
        errorCodes.put("91045", "Bank connection failure");
    }

    public static String getDescription(String code)
    {
        String description="";
        if (functions.isValueNull(code))
            description=errorCodes.get(code);
        return description;
    }


}
