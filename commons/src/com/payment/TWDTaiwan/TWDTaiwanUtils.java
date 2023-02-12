package com.payment.TWDTaiwan;

import com.directi.pg.TransactionLogger;
import com.payment.common.core.*;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Created by Admin on 12/23/2020.
 */
public class TWDTaiwanUtils
{
    private static TransactionLogger transactionLogger=new TransactionLogger(TWDTaiwanUtils.class.getName());
    private static HashMap<String,String> cardTypeMap=new HashMap<>();
    private static HashMap<String,String> errorCodeMap=new HashMap<>();
    static {
        cardTypeMap.put("VISA","visa");
        cardTypeMap.put("MC","mastercard");
        cardTypeMap.put("AMEX","amex");
        cardTypeMap.put("JCB","jcb");
        cardTypeMap.put("UPI","upi");

        errorCodeMap.put("MPG01001","Incorrect setting (SID)");
        errorCodeMap.put("MPG01002","Incorrect timestamp");
        errorCodeMap.put("MPG01008","Incorrect setting (recurring)");
        errorCodeMap.put("MPG01009","SID is null");
        errorCodeMap.put("MPG01012","Incorrect setting (TID)");
        errorCodeMap.put("MPG01013","Incorrect email");
        errorCodeMap.put("MPG01014","Incorrect URL");
        errorCodeMap.put("MPG01015","Incorrect amount");
        errorCodeMap.put("MPG01017","Incorrect item_desc");
        errorCodeMap.put("MPG01018","Incorrect Expire Date");
        errorCodeMap.put("MPG01023","Incorrect hash");
        errorCodeMap.put("MPG01024","Incorrect hash");
        errorCodeMap.put("MPG02001","Incorrect hash");
        errorCodeMap.put("MPG02002","Incorrect setting");
        errorCodeMap.put("MPG02003","Does not allow this Payment method, contact support.");
        errorCodeMap.put("MPG02004","FX rate updated, please start a new transaction");
        errorCodeMap.put("MPG02005","System Error");
        errorCodeMap.put("MPG02006","System Error from acquiring bank");
        errorCodeMap.put("MPG03001","System Error");
        errorCodeMap.put("MPG03002","Blacklisted IP");
        errorCodeMap.put("MPG03003","Reach transaction limit(N transaction in M minutes)");
        errorCodeMap.put("MPG03004","Merchant account is suspended or closed");
        errorCodeMap.put("MPG03005","System error");
        errorCodeMap.put("MPG03005","System error");
        errorCodeMap.put("MPG03006","System error");
        errorCodeMap.put("MPG03007","Cannot find this transaction");
        errorCodeMap.put("MPG03008","Tracking Id already exist");
        errorCodeMap.put("MPG03009","Transaction Fail");
        errorCodeMap.put("MPG05001","Verify card data fail");
        errorCodeMap.put("MPG05002","Do not accept this card type, please use another card.");
        errorCodeMap.put("MPG05003","Reach credit limit.Card does not support recurring payment. /Do not accept rewards points as part of payment.");
        errorCodeMap.put("MPG05004","Card does not support recurring payment");
        errorCodeMap.put("00","Authorized transaction is successful");
        errorCodeMap.put("08","Authorized transaction is successful");
        errorCodeMap.put("11","Authorized transaction is successful");
        errorCodeMap.put("01","Please contact the card issuing bank");
        errorCodeMap.put("02","Please contact the card issuing bank");
        errorCodeMap.put("P1","Please contact the card issuing bank");
        errorCodeMap.put("P4","Please contact the card issuing bank");
        errorCodeMap.put("P6","Please contact the card issuing bank");
        errorCodeMap.put("P9","Please contact the card issuing bank");
        errorCodeMap.put("T4","Please contact the card issuing bank");
        errorCodeMap.put("03","Declined Transaction");
        errorCodeMap.put("05","Declined Transaction");
        errorCodeMap.put("06","Declined Transaction");
        errorCodeMap.put("09","Declined Transaction");
        errorCodeMap.put("12","Declined Transaction");
        errorCodeMap.put("13","Declined Transaction");
        errorCodeMap.put("14","Declined Transaction");
        errorCodeMap.put("15","Declined Transaction");
        errorCodeMap.put("30","Declined Transaction");
        errorCodeMap.put("31","Declined Transaction");
        errorCodeMap.put("39","Declined Transaction");
        errorCodeMap.put("51","Declined Transaction");
        errorCodeMap.put("55","Declined Transaction");
        errorCodeMap.put("56","Declined Transaction");
        errorCodeMap.put("57","Declined Transaction");
        errorCodeMap.put("58","Declined Transaction");
        errorCodeMap.put("61","Declined Transaction");
        errorCodeMap.put("62","Declined Transaction");
        errorCodeMap.put("63","Declined Transaction");
        errorCodeMap.put("65","Declined Transaction");
        errorCodeMap.put("68","Declined Transaction");
        errorCodeMap.put("75","Declined Transaction");
        errorCodeMap.put("76","Declined Transaction");
        errorCodeMap.put("77","Declined Transaction");
        errorCodeMap.put("78","Declined Transaction");
        errorCodeMap.put("79","Declined Transaction");
        errorCodeMap.put("80","Declined Transaction");
        errorCodeMap.put("81","Declined Transaction");
        errorCodeMap.put("82","Declined Transaction");
        errorCodeMap.put("83","Declined Transaction");
        errorCodeMap.put("84","Declined Transaction");
        errorCodeMap.put("85","Declined Transaction");
        errorCodeMap.put("86","Declined Transaction");
        errorCodeMap.put("87","Declined Transaction");
        errorCodeMap.put("88","Declined Transaction");
        errorCodeMap.put("89","Declined Transaction");
        errorCodeMap.put("90","Declined Transaction");
        errorCodeMap.put("91","Declined Transaction");
        errorCodeMap.put("92","Declined Transaction");
        errorCodeMap.put("94","Declined Transaction");
        errorCodeMap.put("96","Declined Transaction");
        errorCodeMap.put("N1","Declined Transaction");
        errorCodeMap.put("N2","Declined Transaction");
        errorCodeMap.put("N3","Declined Transaction");
        errorCodeMap.put("N4","Declined Transaction");
        errorCodeMap.put("N5","Declined Transaction");
        errorCodeMap.put("N6","Declined Transaction");
        errorCodeMap.put("N7","Declined Transaction");
        errorCodeMap.put("N8","Declined Transaction");
        errorCodeMap.put("N9","Declined Transaction");
        errorCodeMap.put("G6","Declined Transaction");
        errorCodeMap.put("O0","Declined Transaction");
        errorCodeMap.put("O1","Declined Transaction");
        errorCodeMap.put("O2","Declined Transaction");
        errorCodeMap.put("O3","Declined Transaction");
        errorCodeMap.put("O4","Declined Transaction");
        errorCodeMap.put("O5","Declined Transaction");
        errorCodeMap.put("O6","Declined Transaction");
        errorCodeMap.put("O7","Declined Transaction");
        errorCodeMap.put("O8","Declined Transaction");
        errorCodeMap.put("O9","Declined Transaction");
        errorCodeMap.put("P0","Declined Transaction");
        errorCodeMap.put("P2","Declined Transaction");
        errorCodeMap.put("P3","Declined Transaction");
        errorCodeMap.put("P5","Declined Transaction");
        errorCodeMap.put("P7","Declined Transaction");
        errorCodeMap.put("P8","Declined Transaction");
        errorCodeMap.put("Q0","Declined Transaction");
        errorCodeMap.put("Q1","Declined Transaction");
        errorCodeMap.put("Q2","Declined Transaction");
        errorCodeMap.put("Q3","Declined Transaction");
        errorCodeMap.put("Q4","Declined Transaction");
        errorCodeMap.put("Q6","Declined Transaction");
        errorCodeMap.put("Q7","Declined Transaction");
        errorCodeMap.put("Q8","Declined Transaction");
        errorCodeMap.put("Q9","Declined Transaction");
        errorCodeMap.put("R0","Declined Transaction");
        errorCodeMap.put("R1","Declined Transaction");
        errorCodeMap.put("R2","Declined Transaction");
        errorCodeMap.put("R3","Declined Transaction");
        errorCodeMap.put("R4","Declined Transaction");
        errorCodeMap.put("R5","Declined Transaction");
        errorCodeMap.put("R6","Declined Transaction");
        errorCodeMap.put("R7","Declined Transaction");
        errorCodeMap.put("R8","Declined Transaction");
        errorCodeMap.put("S4","Declined Transaction");
        errorCodeMap.put("S5","Declined Transaction");
        errorCodeMap.put("S6","Declined Transaction");
        errorCodeMap.put("S7","Declined Transaction");
        errorCodeMap.put("S8","Declined Transaction");
        errorCodeMap.put("S9","Declined Transaction");
        errorCodeMap.put("T5","Declined Transaction");
        errorCodeMap.put("A3","Declined Transaction");
        errorCodeMap.put("A9","Declined Transaction");
        errorCodeMap.put("04","Card Confiscated");
        errorCodeMap.put("07","Card Confiscated");
        errorCodeMap.put("34","Card Confiscated");
        errorCodeMap.put("35","Card Confiscated");
        errorCodeMap.put("36","Card Confiscated");
        errorCodeMap.put("37","Card Confiscated");
        errorCodeMap.put("38","Card Confiscated");
        errorCodeMap.put("41","Card Confiscated");
        errorCodeMap.put("43","Card Confiscated");
        errorCodeMap.put("Q5","Card Confiscated");
        errorCodeMap.put("33","Card Expired");
        errorCodeMap.put("54","Card Expired");
        errorCodeMap.put("T2","Wrong transaction date");
        errorCodeMap.put("AY","Transaction timeout");
        errorCodeMap.put("A9","Failure to read risk card number or installment bonus data ");
        errorCodeMap.put("AD","the number of installments for installment transactions is incorrect");
        errorCodeMap.put("AE","The installment information in the reply from the issuing bank is incorrect");
        errorCodeMap.put("AI","The bonus information returned by the issuing bank is incorrect ");
        errorCodeMap.put("AH","special shop for installment transactions for this card category has expired");
        errorCodeMap.put("AG","special shop has no installment transaction function for this card");
        errorCodeMap.put("AK","the special store’s bonus transaction for this card has expired");
        errorCodeMap.put("AL","the special store has no bonus transaction function for this card");
        errorCodeMap.put("801","Transaction failed");
        errorCodeMap.put("802","Transaction failed");
        errorCodeMap.put("803","Refund failed");
        errorCodeMap.put("804","Transaction failed");
        errorCodeMap.put("805","Transaction failed");
        errorCodeMap.put("901","specific merchant code format error");
        errorCodeMap.put("902","The format of the terminal code is wrong");
        errorCodeMap.put("903","Incorrect order number format");
        errorCodeMap.put("904","Incorrect transaction amount format");
        errorCodeMap.put("905","Response URL format error");
        errorCodeMap.put("906","Installment transactionerrorCodeMap.put please enter the montly period of installments");
        errorCodeMap.put("907","Transaction mode input error!! '0': general transaction '1': installment transaction '2': bonus discount transaction");
        errorCodeMap.put("908","Specific merchant code: does not exist");
        errorCodeMap.put("909","This merchant code has been cancelled");
        errorCodeMap.put("910","The website of  merchant is different from the registered website from our centererrorCodeMap.put please find out before using iterrorCodeMap.put thank you");
        errorCodeMap.put("911","Duplicate order number");
        errorCodeMap.put("912","Terminal code: does not exist");
        errorCodeMap.put("913","Terminal code has been disabled");
        errorCodeMap.put("914","Database AUTH_RECORD failed to add");
        errorCodeMap.put("915","Database AUTH_RECORD failed to read");
        errorCodeMap.put("916","Failed to connect to authorized host");
        errorCodeMap.put("917","HPPRequest does not support this call method (only GET/POST is supported)");
        errorCodeMap.put("918","Transaction timed out [Session data is inconsistent]");
        errorCodeMap.put("919","Transaction timed out [Session=null]");
        errorCodeMap.put("920","Wrong card number");
        errorCodeMap.put("922","3DS transaction authentication error");
        errorCodeMap.put("923","Card expiration date format error");
        errorCodeMap.put("924","Terminal does not support HPP function");
        errorCodeMap.put("926","Terminal does not support this function");
        errorCodeMap.put("929","Terminal has not been filed yet");
        errorCodeMap.put("930","Specific merchant reply Plug=In data check error");
        errorCodeMap.put("931","Transaction failed");
        errorCodeMap.put("932","Order No. Repeated Insertion");
        errorCodeMap.put("933","Order number does not exist");
        errorCodeMap.put("934","Issuing banks 3DS certification digital signature verification failed");
        errorCodeMap.put("935","3DS authentication host of the issuing bank responded with an authentication error");
        errorCodeMap.put("936","Exceed the monthly limit of specific merchant set");
        errorCodeMap.put("937","single transaction amount cannot exceed 100errorCodeMap.put 000 yuan");
        errorCodeMap.put("938","Wrong Banks details");
        errorCodeMap.put("939","Terminal card is disabled");
        errorCodeMap.put("940","wrong cardholder IP address ");
        errorCodeMap.put("941","Cancellation of transaction cannot exceed the UnionPay Cutoff date (after 23:00 on the same day)");
        errorCodeMap.put("942","Order number of the card holder format is wrongerrorCodeMap.put total of the card number  cannot be less than 8 digits or more than 32 digits");
        errorCodeMap.put("954","The four digits on the upper right or upper left of the card number have not been entered (the AE card needs to enter 4DBC)");
        errorCodeMap.put("589","Blocked BIN");
        errorCodeMap.put("970","Please use Avon co-branded card");
        errorCodeMap.put("991","Signature error");
        errorCodeMap.put("996","System maintenance errorCodeMap.put please try again later");
        errorCodeMap.put("998","Passing to issuing bank for 3DS verification");
        errorCodeMap.put("999","System error");



    }
    public static String getCardType(String cardType)
    {
        return cardTypeMap.get(cardType);
    }
    public static String getErrorMessage(String errorCode)
    {
        return errorCodeMap.get(errorCode);
    }
    public static String doHttpPostConnection(String url, String request)
    {
        String result = "";
        PostMethod post = new PostMethod(url);
        try
        {
            transactionLogger.error("URL---->>" + url);
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException e)
        {
            transactionLogger.error("HttpException --->" , e);
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException --->" , e);
        }catch (Exception e)
        {
            transactionLogger.error("Exception --->" , e);
        }
        return result;
    }
    public static String generateHash(String str)
    {
        transactionLogger.error("hash String-->"+str);
        String generatedHash="";
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            generatedHash = getString(messageDigest.digest(str.getBytes()));
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException--->", e);
        }
        return generatedHash;
    }
    private static String getString(byte buf[])
    {
        StringBuffer sb = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++)
        {
            int h = (buf[i] & 0xf0) >> 4;
            int l = (buf[i] & 0x0f);
            sb.append(new Character((char) ((h > 9) ? 'a' + h - 10 : '0' + h)));
            sb.append(new Character((char) ((l > 9) ? 'a' + l - 10 : '0' + l)));
        }
        return sb.toString();
    }
    public static CommRequestVO getTaiwanRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = new CommRequestVO();
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());

        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        addressDetailsVO.setCustomerid(commonValidatorVO.getAddressDetailsVO().getCustomerid());
        addressDetailsVO.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());

        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getCardType());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());

        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        return commRequestVO;
    }
}
