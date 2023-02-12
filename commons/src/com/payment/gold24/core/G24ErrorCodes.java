package com.payment.gold24.core;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 2/20/13
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class G24ErrorCodes
{
    private static HashMap<String, String> errorCodes = new HashMap<String, String>();

    static
    {
        errorCodes.put("100000", "Transaction successful	");
        errorCodes.put("100701", "Transaction successfull - S3D error	");
        errorCodes.put("000004", "Invalid card	");
        errorCodes.put("000012", "Invalid Transaction	");
        errorCodes.put("000013", "Invalid Amount sent	");
        errorCodes.put("000014", "Invalid card	");
        errorCodes.put("000033", "Card expired	");
        errorCodes.put("000051", "Insufficient funds on the card	");
        errorCodes.put("000062", "Restricted card or prohibited	");
        errorCodes.put("000064", "The transaction amount sent is higher than the transaction amount of the original transaction	");
        errorCodes.put("000094", "Duplicated requests sent	");
        errorCodes.put("000300", "Missing field(s)	");
        errorCodes.put("000304", "Merchant verification failed, wrong websiteID or password	");
        errorCodes.put("000306", "Amount must be a positive integer	");
        errorCodes.put("000307", "Invalid card number format	");
        errorCodes.put("000308", "Expire year must have the following pattern 2008	");
        errorCodes.put("000309", "Expire month must have this pattern 07	");
        errorCodes.put("000310", "Card verification code(CVV) must be 3/4 digits	");
        errorCodes.put("000312", "The transaction ID does not exists or wrong websiteID	");
        errorCodes.put("000313", "The order ID must be unique and not duplicated	");
        errorCodes.put("000410", "Transaction rejected attempting to capture an non-AUTH transaction	");
        errorCodes.put("000411", "Transaction rejected attempting to capture an unsuccessful AUTH transaction	");
        errorCodes.put("000412", "Transaction rejected attempting to capture an already captured AUTH transaction	");
        errorCodes.put("000413", "Transaction rejected attempting to capture a too large amount	");
        errorCodes.put("000414", "Transaction rejected attempting to capture a cancelled AUTH transaction	");
        errorCodes.put("000420", "Transaction rejected attempting to cancel an non-AUTH transaction	");
        errorCodes.put("000421", "Transaction rejected attempting to cancel an unsuccessful AUTH transaction	");
        errorCodes.put("000422", "Transaction rejected attempting to cancel an already cancelled AUTH transaction	");
        errorCodes.put("000423", "Transaction rejected attempting to cancel an captured AUTH transaction	");
        errorCodes.put("000430", "Transaction declined bad credit or refund on non-captured transaction	");
        errorCodes.put("000431", "Transaction rejected attempting to perform credit or refund on unsuccessful capture transaction	");
        errorCodes.put("000432", "Transaction rejected attempting to perform refund on an already refunded transaction	");
        errorCodes.put("000433", "Transaction rejected attempting to perform refund with a too large amount	");
        errorCodes.put("000440", "Transaction rejected attempting to rebill a non-captured transaction	");
        errorCodes.put("000441", "Transaction rejected attempting to rebill an unsuccessful capture transaction	");
        errorCodes.put("000520", "Secure 3D verification not allowed	");
        errorCodes.put("000530", "The defined amount for the terminal is higher than the sent amount	");
        errorCodes.put("000601", "Card holder full address must be provided in order to perform AVS check with the selected policy	");
        errorCodes.put("000651", "Invalid S3D data sent	");
        errorCodes.put("000655", "S3D verification required	");
        errorCodes.put("000656", "3D secure not allowed for this terminal, please proceed with an ordinary sale or authorize transaction	");
        errorCodes.put("000700", "Bad credit card	");
        errorCodes.put("000702", "Invalid card number format	");
        errorCodes.put("000704", "Bad card Holder Name	");
        errorCodes.put("000705", "Bad card Holder Address	");
        errorCodes.put("000706", "Bad card Holder State	");
        errorCodes.put("000707", "Bad card Holder City	");
        errorCodes.put("000708", "Bad card Expire Year	");
        errorCodes.put("000709", "Bad card Expire Month	");
        errorCodes.put("000710", "Bad card Holder Country Code	");
        errorCodes.put("000711", "Bad card Holder Zipcode	");
        errorCodes.put("000797", "Merchant IP verification failed	");
        errorCodes.put("000798", "Bank not ready or Connection error	");
        errorCodes.put("000906", "Bad currency or card type	");
        errorCodes.put("000997", "Multi-terminal is not defined for this transaction	");
        errorCodes.put("000998", "Unknown error	");


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
