package com.payment.apcoFastpay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.apco.core.ApcoPayUtills;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.common.core.CurrencyCodeISO4217;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.ResourceBundle;

/**
 * Created by Admin on 5/21/18.
 */
public class ApcoFastpayPaymentProcess extends CommonPaymentProcess
{
    TransactionLogger transactionLogger = new TransactionLogger(ApcoFastpayPaymentProcess.class.getName());
    ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.ApcoPayServlet");

    public static String getMD5HashVal(String str) throws PZTechnicalViolationException
    {
        String encryptedString = null;
        byte[] bytesToBeEncrypted;
        try
        {
            // convert string to bytes using a encoding scheme
            bytesToBeEncrypted = str.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] theDigest = md.digest(bytesToBeEncrypted);
            // convert each byte to a hexadecimal digit
            Formatter formatter = new Formatter();
            for (byte b : theDigest)
            {
                formatter.format("%02x", b);
            }
            encryptedString = formatter.toString().toLowerCase();
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "getMD5HashVal()", null, "Transaction", "UnsupportedEncodingException raised::::", PZTechnicalExceptionEnum.UNSUPPORTING_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("ApcoPayUtills.java", "getMD5HashVal()", null, "Transaction", "UnsupportedEncodingException raised::::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return encryptedString;
    }


    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D, CommonValidatorVO commonValidatorVO)
    {
        Functions functions = new Functions();
        ApcoPayUtills apcoPayUtills = new ApcoPayUtills();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String profileId = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretWord = gatewayAccount.getFRAUD_FTP_PATH();
        String birthday = "";

        boolean isTest = gatewayAccount.isTest();
        String testString = "";
        String Card="";

        String amount = commonValidatorVO.getTransDetailsVO().getAmount();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(commonValidatorVO.getTransDetailsVO().getCurrency());
        Integer currencyCode = Integer.parseInt(currency);
        String trackingId = commonValidatorVO.getTrackingid();
        String phoneNo = commonValidatorVO.getAddressDetailsVO().getPhone();
        String emailId = commonValidatorVO.getAddressDetailsVO().getEmail();
        String language = "";
        String UDF1=commonValidatorVO.getTransDetailsVO().getOrderId();
        transactionLogger.debug("UDF1-------"+UDF1);

        if(functions.isValueNull(language)){
            language=commonValidatorVO.getAddressDetailsVO().getLanguage();
        }
        String name = commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname();
        String address="";
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getStreet()))
        {
        address=commonValidatorVO.getAddressDetailsVO().getStreet().replaceAll(","," ");
        }
        String street = address + "," + address + "," + commonValidatorVO.getAddressDetailsVO().getCity() + "," + commonValidatorVO.getAddressDetailsVO().getZipCode() + "," + commonValidatorVO.getAddressDetailsVO().getState();
        String country = commonValidatorVO.getAddressDetailsVO().getCountry();


        String redirectURLSuccess = "";
        String redirectURLFailed = "";
        String statusURL = "";

        if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getHostUrl()))
        {
            redirectURLSuccess = "https://" + commonValidatorVO.getMerchantDetailsVO().getHostUrl() + "/transaction/ApcoPayFrontEndServlet";
            redirectURLFailed = "https://" + commonValidatorVO.getMerchantDetailsVO().getHostUrl() + "/transaction/ApcoPayFrontEndServlet";
            statusURL = "https://" + commonValidatorVO.getMerchantDetailsVO().getHostUrl() + "/transaction/ApcoPayBackEndServlet";

        }
        else
        {
            redirectURLSuccess = rb.getString("APCOPAY_FRONTEND");
            redirectURLFailed = rb.getString("APCOPAY_FRONTEND");
            statusURL = rb.getString("APCOPAY_BACKEND");
        }
        String actionType = "1";
        String cardTypeId = commonValidatorVO.getCardType();
        String fromType = gatewayAccount.getGateway();
        String apmName="";
        if(fromType.equals("fastpay"))
        {
           apmName=gatewayAccount.getCHARGEBACK_FTP_PATH();
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getBirthdate()))
                birthday = apcoPayUtills.getBirthday(commonValidatorVO.getAddressDetailsVO().getBirthdate(), rb.getString(apmName));
        }
        else
        {
            apmName=apcoPayUtills.getAPMName(cardTypeId, fromType);
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getBirthdate()))
                birthday = apcoPayUtills.getBirthday(commonValidatorVO.getAddressDetailsVO().getBirthdate(),null);
        }
        transactionLogger.error("apmName---"+apmName);
        //String apmName = getAPMName(cardTypeId, fromType);
        String addressXML = "";


        if (isTest)
        {
            testString = "<TEST />";
            Card="<TESTCARD />";
        }else{
            Card="<ForcePayment>" + apmName + "</ForcePayment>";
        }
        transactionLogger.error("Card-----"+Card);
        try
        {
            if ("ALDRAPAY".equalsIgnoreCase(apmName) || ("ALDRAPAY".equalsIgnoreCase(apmName)))
            {
                addressXML = "<RegName>" + name + "</RegName>" +
                        "<Address>" + street + "</Address>" +
                        "<RegCountry>" + country + "</RegCountry>" +
                        "<DOB>" + birthday + "</DOB>";
            }
            else if ("PVISA".equalsIgnoreCase(apmName) || ("RAVEDIRECT".equalsIgnoreCase(apmName)))
            {
                addressXML = "<RegName>" + name + "</RegName>" +
                        "<Address>" + street + "</Address>" +
                        "<RegCountry>" + country + "</RegCountry>" +
                        "<DOB>" + birthday + "</DOB>";
            }
            else if (fromType.equals("fastpay"))
            {
                addressXML = "<RegName>" + name + "</RegName>" +
                        "<Address>" + street + "</Address>" +
                        "<RegCountry>" + country + "</RegCountry>" +
                        "<DOB>" + birthday + "</DOB>";
            }

            //generate md5 hash from xml
            String xmlToPostForMD5 = "<Transaction hash=\"" + secretWord + "\">" +
                    "<ProfileID>" + profileId + "</ProfileID>" +
                    "<Value>" + amount + "</Value>" +
                    "<Curr>" + currencyCode + "</Curr>" +
                    "<Lang>" + language + "</Lang>" +
                    "<ORef>" + trackingId + "</ORef>" +
                    "<ClientAcc></ClientAcc>" +
                    "<MobileNo>" + phoneNo + "</MobileNo>" +
                    "<Email>" + emailId + "</Email>" +
                    "<CIP>"+commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()+"</CIP>"+
                    "<RedirectionURL>" + redirectURLSuccess + "</RedirectionURL>" +
                    "<FailedRedirectionURL>" + redirectURLFailed + "</FailedRedirectionURL>" +
                    "<UDF1>"+UDF1+"</UDF1>" +
                    "<UDF2 />" +
                    "<UDF3 />" +
                    "<FastPay>" +
                    "<ListAllCards></ListAllCards>" +
                    "<NewCard1Try />" +
                    "<NewCardOnFail />" +
                    "<PromptCVV />" +
                    "<PromptExpiry />" +
                    "</FastPay>" +
                    "<ActionType>" + actionType + "</ActionType>" +
                    "<status_url>" + statusURL + "</status_url>" +
                    "<HideSSLLogo></HideSSLLogo>" +
                    "<AntiFraud>" +
                    "<Provider></Provider>" +
                    "</AntiFraud>" +
                    "" + testString + "" +
                    "<return_pspid></return_pspid>" +
                    "<TicketNo>" + response3D.getResponseHashInfo() + "</TicketNo>" +
                    Card+
                    addressXML +
                    "</Transaction>";
            transactionLogger.error("xmlToPostForMD5:::" + xmlToPostForMD5);
            String hash = getMD5HashVal(xmlToPostForMD5);
            transactionLogger.error("hash:::" + hash);
            //use generated hash in below xml
            String xmlToPost = "<Transaction hash='" + hash + "'>" +
                    "<ProfileID>" + profileId + "</ProfileID>" +
                    "<Value>" + amount + "</Value>" +
                    "<Curr>" + currencyCode + "</Curr>" +
                    "<Lang>" + language + "</Lang>" +
                    "<ORef>" + trackingId + "</ORef>" +
                    "<ClientAcc></ClientAcc>" +
                    "<MobileNo>" + phoneNo + "</MobileNo>" +
                    "<Email>" + emailId + "</Email>" +
                    "<CIP>"+commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()+"</CIP>"+
                    "<RedirectionURL>" + redirectURLSuccess + "</RedirectionURL>" +
                    "<FailedRedirectionURL>" + redirectURLFailed + "</FailedRedirectionURL>" +
                    "<UDF1>"+UDF1+"</UDF1>" +
                    "<UDF2 />" +
                    "<UDF3 />" +
                    "<FastPay>" +
                    "<ListAllCards></ListAllCards>" +
                    "<NewCard1Try />" +
                    "<NewCardOnFail />" +
                    "<PromptCVV />" +
                    "<PromptExpiry />" +
                    "</FastPay>" +
                    "<ActionType>" + actionType + "</ActionType>" +
                    "<status_url>" + statusURL + "</status_url>" +
                    "<HideSSLLogo></HideSSLLogo>" +
                    "<AntiFraud>" +
                    "<Provider></Provider>" +
                    "</AntiFraud>" +
                    "" + testString + "" +
                    "<return_pspid></return_pspid>" +
                    "<TicketNo>" + response3D.getResponseHashInfo() + "</TicketNo>" +
                    Card+
                    addressXML +
                    "</Transaction>";

            transactionLogger.debug("xmlToPost-----" + xmlToPost);

            AsyncParameterVO asyncParameterVO = null;

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("params");
            asyncParameterVO.setValue(xmlToPost);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("");
            asyncParameterVO.setValue("");
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            directKitResponseVO.setBankRedirectionUrl(rb.getString("URL"));

            transactionLogger.error("xmlToPost:::" + xmlToPost);
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException-----", e);
        }
    }



    public String getSpecificVirtualTerminalJSP()
    {
        return "clearsettlespecificfields.jsp";
    }
}