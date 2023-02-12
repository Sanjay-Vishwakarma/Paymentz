package com.payment.acqra;

import com.directi.pg.TransactionLogger;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

/**
 * Created by Admin on 12/19/2019.
 */
public class AcqraUtils
{
    private static TransactionLogger transactionLogger=new TransactionLogger(AcqraUtils.class.getName());
    private static Hashtable statusHash=new Hashtable();
    static {
        statusHash.put("50000", "Payment Success");
        statusHash.put("51111","Payment failure");
        statusHash.put("50004","Email error");
        statusHash.put("50006","security_code error");
        statusHash.put("50008","card_holder_first_name is not correct");
        statusHash.put("50010","card_holder_last_name is not correct");
        statusHash.put("50016","Credit card info is not correct");
        statusHash.put("50018","exp_month is not correct");
        statusHash.put("50020","exp_year is not correct");
        statusHash.put("50024","Amount is not correct");
        statusHash.put("50025","Transaction amount exceeds the limit");
        statusHash.put("50026","Credit card number is not correct");
        statusHash.put("50027","IP is not legitimate");
        statusHash.put("50029","phone is not correct");
        statusHash.put("50030","Country of issuing bank is not correct");
        statusHash.put("51002","website is error");
        statusHash.put("51003","Merchant is limited (Country, state, IP, etc.)");
        statusHash.put("51004","Amount error, only support 2 decimal point");
        statusHash.put("51005","Payment amount must be positive number and cannot contain other characters");
        statusHash.put("51006","The amount of payment cannot be more than 1");
        statusHash.put("51007","The order number exceeds the number of transactions.");
        statusHash.put("51008","The order number is repeated within the specified time.");
        statusHash.put("51009","The gateway are not open");
        statusHash.put("51010","The merchant have not open the direct interface");
        statusHash.put("51011","Website is limited for credit card merchant");
        statusHash.put("51013","Merchant payment times are limited");
        statusHash.put("51014","High risk card");
        statusHash.put("51015","National Issuing Bank Limited");
        statusHash.put("51016","Gateway value is not correct");
        statusHash.put("51017","Error merchant IP captured");
        statusHash.put("51018","Merchant IP blocked");
        statusHash.put("51019","Payment currency is not correct");
        statusHash.put("52001","No maximum limit (for each transaction)");
        statusHash.put("52002","No maximum limit (for daily transaction)");
        statusHash.put("52003","No maximum limit (for monthly transaction)");
        statusHash.put("52004","Payment amount reached a limit (for each transaction)");
        statusHash.put("52005","Payment amount reached a limit (for daily transaction)");
        statusHash.put("52006","Payment amount reached a limit (for monthly transaction)");
        statusHash.put("52007","Merchant transaction amount error");
        statusHash.put("52008","Temporarily unavailable payment service");
        statusHash.put("52009","There is no maximum amount limit (weekly trading)");
        statusHash.put("52010","Payment amount reached the limit (weekly transaction)");
        statusHash.put("52011","The payment amount has reached the channel limit");
        statusHash.put("52012","Payment amount reached the channel daily limit");
        statusHash.put("52013","The payment amount reached the channel weekly limit");
        statusHash.put("52014","The payment amount has reached the limit of 2 weeks per channel");
        statusHash.put("52015","The payment amount has reached the limit of 3 weeks per channel");
        statusHash.put("52016","The payment amount reached the channel monthly limit");
        statusHash.put("52017","Channel not configured (automatic route - amount) or channel used");
        statusHash.put("52018","The transaction amount cannot be less than 1 USD");
        statusHash.put("53001","Gateway temporarily not available");
        statusHash.put("53002","The merchant payment gateway did not establish currency");
        statusHash.put("53003","Mastercard payment service are temporarily not available, welcome to use Visa payment");
        statusHash.put("53004","Visa payment service are temporarily not available, welcome to use Mastercard payment");
        statusHash.put("53005","Gateway address error");
        statusHash.put("53006","Gateway have not been opened");
        statusHash.put("53007","Query exchange rate error");
        statusHash.put("53008","Card number encryption error");
        statusHash.put("53009","Due to potential risks, payment is blocked");
        statusHash.put("53010","Network is busy");
        statusHash.put("53011","Payment submission error");
        statusHash.put("53012","Connection timed out");
        statusHash.put("53013","Payment amount may be tampered");
        statusHash.put("53014","Bankid is not match");
        statusHash.put("53015","Gateway is not match");
        statusHash.put("53016","Abnormal payment");
        statusHash.put("53017","Currency of gateway have not been opened");
        statusHash.put("53018","Payment success, but failed to send a message to the cardholder");
        statusHash.put("53020","Business card blocking");
        statusHash.put("53022","High risk trading");
        statusHash.put("53023","website is not allowed");
        statusHash.put("53024","Not white list card number");
        statusHash.put("53025","Fraudulent transactions");

        statusHash.put("60000","Refund Submitted");
        statusHash.put("60010","Submit Success");
        statusHash.put("60011","Submit Fail");
        statusHash.put("60013","Already Submitted");
        statusHash.put("61004","Amount illegal");
        statusHash.put("61005","Order not exist");
        statusHash.put("61006","Refund amount more than transaction amount");
        statusHash.put("61007","Failed order cannot refunded");
        statusHash.put("61008","Merchant number not exist");
        statusHash.put("61010","Chargeback cannot refund");
        statusHash.put("61012","Refund amount is greater than the refundable amount");
        statusHash.put("61013","Merchant refund request ID is invalid");
        statusHash.put("61014","Merchant refund request ID is duplicated");
        statusHash.put("40000","Invalid API Key or API Key not found");
        statusHash.put("40001","Invalid mid or mid not found");
        statusHash.put("40002","Invalid Order Reference");
        statusHash.put("40003","Order Reference already exist");
        statusHash.put("40004","Amount is missing / invalid");
        statusHash.put("40005","Invalid Amount");
        statusHash.put("40006","Invalid Version");
        statusHash.put("40007","Currency is missing / invalid");
        statusHash.put("40008","Invalid currency");
        statusHash.put("40009","Invalid language");
        statusHash.put("40010","Invalid Phone Number");
        statusHash.put("40011","Invalid transaction ID");
        statusHash.put("40012","Transaction ID does not exist");
        statusHash.put("40015","Invalid hash value");
        statusHash.put("40016","Email format is invalid");
        statusHash.put("40017","Credit card number is missing / invalid");
        statusHash.put("40018","Credit card number is invalid");
        statusHash.put("40019","CVV is missing / invalid");
        statusHash.put("40020","CVV is invalid");
        statusHash.put("40021","Credit Card expiry month is missing / invalid");
        statusHash.put("40022","Credit Card expiry month is invalid");
        statusHash.put("40023","Credit Card expiry year is missing / invalid");
        statusHash.put("40024","Credit Card expiry year is invalid or expired");
        statusHash.put("40025","Cardholder first name is missing / invalid");
        statusHash.put("40026","Cardholder last name is missing / invalid");
        statusHash.put("40027","Billing address is missing / invalid");
        statusHash.put("40028","Billing city is missing / invalid");
        statusHash.put("40029","Billing state is missing / invalid");
        statusHash.put("40030","Billing zip code is missing / invalid");
        statusHash.put("40031","Billing country is missing / invalid");
        statusHash.put("40041","Buyer IP address is missing / invalid");
        statusHash.put("40042","Buyer browser user agent is missing / invalid");
        statusHash.put("40043","Buyer browser accept language is missing / invalid");
        statusHash.put("40046","Settlement reference is missing / invalid");
        statusHash.put("40050","Order Reference does not exist");
        statusHash.put("40051","Transaction amount exceed or below limit");
        statusHash.put("40054","Mid or API Key is invalid");
        statusHash.put("41006","Invalid card or card does not support card-not-present transaction");
        statusHash.put("41010","Card type is invalid");
        statusHash.put("41011","Insufficient fund in the account");
        statusHash.put("41012","Invalid card number");
        statusHash.put("41013","Expired card");
        statusHash.put("42000","Error connecting to Payment Channel");
        statusHash.put("44001","Invalid Input / Method");
        statusHash.put("45000","Attempted to perform 3DS, but either the cardholder or issuer was not participating");
    }
    public static String getStatusDescription(String responseCode)
    {
        String description="";
        statusHash.get(responseCode);
        return description;
    }
    public static String doHttpPostConnection(String url,String request)
    {
        String result="";
        PostMethod post=new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response=new String(post.getResponseBody());
            result=response;
        }
        catch (HttpException e)
        {
            transactionLogger.error("HttpException --->",e);
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException --->",e);
        }
        return result;
    }
    public static String hashSHA256(String plainText)
    {
        StringBuffer cipherText=new StringBuffer();
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(plainText.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) cipherText.append('0');
                cipherText.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException --->",e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException --->", e);
        }
        return cipherText.toString();
    }
}
