package com.payment.phoneix;

import com.directi.pg.TransactionLogger;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import sun.misc.BASE64Encoder;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;

/**
 * Created by Vivek on 9/10/2019.
 */
public class PhoneixUtils
{
    private static TransactionLogger transactionLogger=new TransactionLogger(PhoneixUtils.class.getName());
    private static HashMap<String, String> currencyCodeHash = new HashMap<String, String>();
    private static HashMap<String, String> errorCodes = new HashMap<String, String>();
    private static HashMap<String, String> transType = new HashMap<String, String>();
    static {
        currencyCodeHash.put("ILS","0");
        currencyCodeHash.put("USD","1");
        currencyCodeHash.put("EUR","2");
        currencyCodeHash.put("GBP","3");
        currencyCodeHash.put("AUD","4");
        currencyCodeHash.put("CAD","5");
        currencyCodeHash.put("JPY","6");
        currencyCodeHash.put("NOK","7");
        currencyCodeHash.put("PLN","8");
        currencyCodeHash.put("MXN","9");
        currencyCodeHash.put("ZAR","10");
        currencyCodeHash.put("RUB","11");
        currencyCodeHash.put("TRY","12");
        currencyCodeHash.put("CHF","13");
        currencyCodeHash.put("INR","14");
        currencyCodeHash.put("DKK","15");
        currencyCodeHash.put("SEK","16");
        currencyCodeHash.put("CNY","17");
        currencyCodeHash.put("HUF","18");
        currencyCodeHash.put("NZD","19");
        currencyCodeHash.put("HKD","20");
        currencyCodeHash.put("KRW","21");
        currencyCodeHash.put("SGD","22");
        currencyCodeHash.put("THB","23");
        currencyCodeHash.put("BSD","24");

        errorCodes.put("000", "The transaction has completed successfully");
        errorCodes.put("001", "Transaction is pending authorization");
        errorCodes.put("002", "No Details");
        errorCodes.put("1001","Test Environment Only - Soft Decline (Call)");
        errorCodes.put("1002","Test Environment Only - Insufficient Funds");
        errorCodes.put("500", "Merchant company number or signature is invalid");
        errorCodes.put("501", "Merchant is not activated");
        errorCodes.put("502", "Merchant is unauthorized to use this service");
        errorCodes.put("503", "Merchant unauthorized to process that type of credit card in that currency");
        errorCodes.put("504", "Merchant \"authorization only\" option is inactive");
        errorCodes.put("505", "Charge amount not in allowed range !");
        errorCodes.put("506", "Data is missing, check CardNum, Amount, Currency, Payments, Expiration Date and TypeCredit");
        errorCodes.put("507", "Credit card number is invalid");
        errorCodes.put("508", "Invalid data range - currency, typeCredit, data length");
        errorCodes.put("509", "Credit card number is blocked");
        errorCodes.put("510", "Credit card is expired");
        errorCodes.put("511", "Missing card holder Name");
        errorCodes.put("512", "Missing or incorrect length of card verification number (cvv2)");
        errorCodes.put("513", "Missing government issued id number");
        errorCodes.put("514", "Missing card holder phone number");
        errorCodes.put("515", "Missing or invalid card holder email address");
        errorCodes.put("516", "Missing client ip");
        errorCodes.put("517", "Full name is invalid");
        errorCodes.put("518", "TermCode is missing, invalid or not configured");
        errorCodes.put("519", "This card is not certified");
        errorCodes.put("520", "Internal error");
        errorCodes.put("521", "Unable to complete transaction (communication failure)");
        errorCodes.put("522", "System blocked transaction (duplicate transaction within the specified timeframe)");
        errorCodes.put("523", "System Error");
        errorCodes.put("524", "Processing System Error");
        errorCodes.put("525", "Daily volume limit exceeded");
        errorCodes.put("526", "Invalid request source");
        errorCodes.put("527", "Service not allowed from your IP Address");
        errorCodes.put("528", "Missing or incorrect MD5 signature");
        errorCodes.put("529", "Duplicate transaction");
        errorCodes.put("530", "Transaction amount exceeded system`s limit");
        errorCodes.put("531", "Currency is invalid or not setup for processing");
        errorCodes.put("532", "RefTransID - can't find original transaction");
        errorCodes.put("533", "Unable to refund more then the original transaction`s Amount");
        errorCodes.put("534", "The request must be sent over secure socket layer (HTTPS)");
        errorCodes.put("535", "Invalid RefTransID parameter");
        errorCodes.put("536", "Initial pre-auth transaction not found: check TransApprovalID and Currency");
        errorCodes.put("537", "Silent Post with credit card details is not allowed");
        errorCodes.put("538", "Captured amount is higher that authorized");
        errorCodes.put("539", "Transaction amount is invalid");
        errorCodes.put("540", "Billing address - missing address");
        errorCodes.put("541", "Billing address - missing city name");
        errorCodes.put("542", "Billing address - missing or invalid zip code");
        errorCodes.put("543", "Billing address - missing or invalid state");
        errorCodes.put("544", "Billing address - missing or invalid country");
        errorCodes.put("545", "Request length limit exceeded");
        errorCodes.put("546", "Previous transaction with this card is still in process, please try again after response is sent");
        errorCodes.put("547", "Too many cards for single email or too many emails for single card.");
        errorCodes.put("548", "Declined by external processor");
        errorCodes.put("549", "Processing stored card - merchant is unauthorized to use this service");
        errorCodes.put("550", "Recurring transactions - merchant is unauthorized to use this service");
        errorCodes.put("551", "Recurring transactions - data is missing");
        errorCodes.put("552", "Transaction is pending for SMS code");
        errorCodes.put("553", "3D Secure Redirection is needed");
        errorCodes.put("554", "Bank Transfer, redirection is needed");
        errorCodes.put("555", "Recurring limit exceeded");
        errorCodes.put("556", "Waiting for SMS message containing the code");
        errorCodes.put("557", "Billing address - invalid zip code");
        errorCodes.put("558", "ClientIP parameter is required but missing from the request");
        errorCodes.put("559", "Wrong CVV length, please use 3 digit number from back of the card");
        errorCodes.put("560", "Not Exist in PPC List");
        errorCodes.put("561", "This credit card has been temporarily blocked.");
        errorCodes.put("562", "Cardholder data is blacklisted");
        errorCodes.put("563", "This credit card has been temporarily blocked.");
        errorCodes.put("564", "Only one refund permitted per transaction");
        errorCodes.put("565", "Cannot proceed - One or more cart products are out of stock");
        errorCodes.put("578", "Charge amount higher than maximum allowed by merchant");
        errorCodes.put("579", "Charge amount lower than minimum allowed by merchant");
        errorCodes.put("580", "Did not pass fraud detection test");
        errorCodes.put("581", "Negativ Country List Block");
        errorCodes.put("582", "Country IP is blocked");
        errorCodes.put("583", "Weekly charge count limit reached for this credit card");
        errorCodes.put("584", "Weekly charge amount limit reached for this credit card");
        errorCodes.put("585", "Charge count limit reached for this credit card");
        errorCodes.put("586", "Charge amount limit reached for this credit card");
        errorCodes.put("587", "Reached daily limit of failed charges");
        errorCodes.put("588", "Incorrect charge amount");
        errorCodes.put("589", "Blocked group of credit cards");
        errorCodes.put("590", "Input parameter is out of range");
        errorCodes.put("591", "Input parameter is too long");
        errorCodes.put("592", "Processor is not available");
        errorCodes.put("593", "Monthly charge count limit reached for this credit card");
        errorCodes.put("594", "Monthly charge amount limit reached for this credit card");
        errorCodes.put("595", "Integration mode - incorrect credit card number");
        errorCodes.put("596", "Integration mode - incorrect charge amount");
        errorCodes.put("597", "Daily charge count limit reached for this credit card");
        errorCodes.put("598", "Daily charge amount limit reached for this credit card");
        errorCodes.put("599", "Declined by issuing bank");
        errorCodes.put("600", "The customer has closed the payment page window");
        errorCodes.put("601", "Gateway connection problem");
        errorCodes.put("603", "Non 3D transaction not allowed");
        errorCodes.put("0", "Approved Transaction ");
        errorCodes.put("1", "Refer to card issuer");
        errorCodes.put("2", "Refer to special conditions of card issuer");
        errorCodes.put("3", "Invalid merchant");
        errorCodes.put("4", "Pick-up card");
        errorCodes.put("5", "Do not honor");
        errorCodes.put("6", "Error");
        errorCodes.put("7", "Pick-up card - special condition");
        errorCodes.put("8", "Honor with identification (Approved maybe more ID)");
        errorCodes.put("9", "Request in progress (duplicate)");
        errorCodes.put("01", "Refer to card issuer");
        errorCodes.put("02", "Refer to special conditions of card issuer");
        errorCodes.put("03", "Invalid merchant");
        errorCodes.put("04", "Pick-up card");
        errorCodes.put("05", "Do not honor");
        errorCodes.put("06", "Error");
        errorCodes.put("07", "Pick-up card - special condition");
        errorCodes.put("08", "Honor with identification (Approved maybe more ID)");
        errorCodes.put("09", "Request in progress (duplicate)");
        errorCodes.put("12", "Invalid transaction");
        errorCodes.put("13", "Invalid amount");
        errorCodes.put("14", "Invalid card number (no such number)");
        errorCodes.put("15", "No such issuer");
        errorCodes.put("30", "Format error");
        errorCodes.put("31", "Bank not supported by switch");
        errorCodes.put("33", "Expired card");
        errorCodes.put("34", "Suspected fraud");
        errorCodes.put("35", "Card acceptor contact acquirer");
        errorCodes.put("36", "Restricted card");
        errorCodes.put("37", "Card acceptor call acquirer secutiry");
        errorCodes.put("38", "Allowable PIN tries exceeded");
        errorCodes.put("39", "No credit account");
        errorCodes.put("41", "Lost card");
        errorCodes.put("43", "Stolen card - pick-up");
        errorCodes.put("51", "Not sufficient funds");
        errorCodes.put("54", "Expired card");
        errorCodes.put("55", "Incorrect personal identification number");
        errorCodes.put("56", "No card record");
        errorCodes.put("57", "Transaction not permitted to cardholder");
        errorCodes.put("58", "Transaction not permitted to terminal");
        errorCodes.put("61", "Exceeds withdrawal amount  limit");
        errorCodes.put("62", "Restricted card");
        errorCodes.put("65", "Exceeds withdrawal frequency limit");
        errorCodes.put("68", "Response received too late");
        errorCodes.put("75", "Allowable number of PIN tries exceeded");
        errorCodes.put("82", "Reserved for private use or No security module");
        errorCodes.put("83", "Reserved for private use or No accounts");
        errorCodes.put("84", "Reserved for private use or No PBF");
        errorCodes.put("85", "Reserved for private use or PBF update error");
        errorCodes.put("86", "Reserved for private use or invalid authorization type");
        errorCodes.put("87", "Reserved for private use or Bad track data");
        errorCodes.put("88", "Reserved for private use or PTLF error");
        errorCodes.put("89", "Reserved for private use or invalid route service");
        errorCodes.put("90", "Cut off is in process - a switch is ending business for a day and starting the next (transaction can be sent again in a few minutes)");
        errorCodes.put("91", "Issuer or switch is inoperative");
        errorCodes.put("92", "Financial institution or intermediate network facility cannot be found for routing");
        errorCodes.put("94", "Duplicate transmission");
        errorCodes.put("A001", "Data Validation Error");
        errorCodes.put("A002", "Missing data like cvv2 or pan (if manual refund) or track2 (if card refund)");
        errorCodes.put("A004", "Invalid affiliation number");
        errorCodes.put("A006", "Invalid request encryption");
        errorCodes.put("A007", "Transaction Not Found");
        errorCodes.put("A008", "Invalid request merchant");
        errorCodes.put("A009", "Not Approved Transaction");
        errorCodes.put("A010", "Transaction already refunded");
        errorCodes.put("A011", "Authnum does not match");
        errorCodes.put("A012", "EMV request not present and no fallback flag was set");
        errorCodes.put("A013", "Fallback flag was set but emvtags are present");
        errorCodes.put("A014", "Incomplete EMV Tags");
        errorCodes.put("A015", "Exceeded EMV Tags");
        errorCodes.put("A016", "EMV Tags length exceeded");
        errorCodes.put("A017", "Limit reached for the day");
        errorCodes.put("A018", "Limit for transaction");
        errorCodes.put("P001", "No manual sales are allowed");
        errorCodes.put("P002", "No card sales allowed");
        errorCodes.put("P003", "No deferral payments allowed");
        errorCodes.put("P004", "No tipping allowed");
        errorCodes.put("P005", "No manual refunds allowed");
        errorCodes.put("P006", "No Card refunds allowed");
        errorCodes.put("P007", "No manual reversals allowed");
        errorCodes.put("P008", "No Card reversals allowed");
        errorCodes.put("S001", "Null Access Token");
        errorCodes.put("S002", "Null Request");
        errorCodes.put("S003", "Invalid Access Token Format");
        errorCodes.put("S004", "Invalid Encription");
        errorCodes.put("S005", "Invalid Signature");

        transType.put("0","Sale");
        transType.put("1","Auth");
        transType.put("2","Capture");
        transType.put("3","Charge");
    }
    public static String doHttpPostConnection(String operationUrl,String req) throws PZTechnicalViolationException
    {
        InputStream inputStream;
        byte[] bytes=new byte[]{};
        try
        {
            URL url = new URL(null,operationUrl,new sun.net.www.protocol.https.Handler());
            // above first and last parameter in URL is used to handle ClassCalst Exception
            //com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl cannot be cast to javax.net.ssl.HttpsURLConnection
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
            dataOutputStream.writeBytes(req);
            dataOutputStream.flush();
            dataOutputStream.close();
            int responseCode = conn.getResponseCode();

            if (responseCode >= 400)
            {
                inputStream = conn.getErrorStream();
            }
            else
            {
                inputStream = conn.getInputStream();
            }
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException ---->",e);
            PZExceptionHandler.raiseTechnicalViolationException(PhoneixUtils.class.getName(), "doPostHTTPSURLConnection()", null, "common", "IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return new String(bytes);
    }
    public static String doHttpGetConnection(String operationUrl,String req) throws PZTechnicalViolationException
    {
        InputStream inputStream;
        byte[] bytes=new byte[]{};
        try
        {
            URL url = new URL(null,operationUrl,new sun.net.www.protocol.https.Handler());
            // above first and last parameter in URL is used to handle ClassCalst Exception
            //com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl cannot be cast to javax.net.ssl.HttpsURLConnection
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
            dataOutputStream.writeBytes(req);
            dataOutputStream.flush();
            dataOutputStream.close();
            int responseCode = conn.getResponseCode();

            if (responseCode >= 400)
            {
                inputStream = conn.getErrorStream();
            }
            else
            {
                inputStream = conn.getInputStream();
            }
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException---->",e);
            PZExceptionHandler.raiseTechnicalViolationException("KortaPayUtils.java", "doPostHTTPSURLConnection()", null, "common", "IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return new String(bytes);
    }
    public static Comm3DResponseVO readQueryStringResponse(String response)
    {
        Comm3DResponseVO comm3DResponseVO=new Comm3DResponseVO();
        String[] resArray=response.split("&");
        String key="";
        String value="";
        for (String res:resArray)
        {
            String[] resArr=res.split("=");
            key=resArr[0];
            if(resArr.length==2)
            value=resArr[1];
            if(key.equalsIgnoreCase("Reply"))
            {
                if("000".equals(value))
                    comm3DResponseVO.setStatus("success");
                else if("553".equals(value))
                    comm3DResponseVO.setStatus("pending3DConfirmation");
                else
                    comm3DResponseVO.setStatus("failed");
                comm3DResponseVO.setErrorCode(value);
                comm3DResponseVO.setBankCode(value);
            }
            if(key.equalsIgnoreCase("ReplyDesc"))
            {
                comm3DResponseVO.setRemark(value);
                comm3DResponseVO.setDescription(value);
            }
            if(key.equalsIgnoreCase("TransID"))
                comm3DResponseVO.setTransactionId(value);
            if(key.equalsIgnoreCase("Date"))
                comm3DResponseVO.setResponseTime(value);
            /*if(key.equalsIgnoreCase("Descriptor"))
                comm3DResponseVO.setDescriptor(value);*/
            if(key.equalsIgnoreCase("ConfirmationNum"))
                comm3DResponseVO.setResponseHashInfo(value);
            if(key.equalsIgnoreCase("D3Redirect"))
                comm3DResponseVO.setRedirectUrl(value);
        }
        return comm3DResponseVO;
    }
    public static String getCurrencyCode(String currency)
    {
        String currencyCode="";
        currencyCode=currencyCodeHash.get(currency);
        return currencyCode;
    }
    public static String getDescription(String errorcode)
    {
        String description="";
        description=errorCodes.get(errorcode);
        return description;
    }
    public static String getTransactionType(String transCode) throws Exception
    {
        String type="";
        type=transType.get(transCode);
        return type;
    }
    public static String hashSignature(String stringaMac) throws Exception
    {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        BASE64Encoder encoder = new BASE64Encoder();
        byte[] in = digest.digest(stringaMac.getBytes());

        String builder = "";
        builder=encoder.encode(in);
        return builder.toString();
    }
}
