package com.payment.kotakbank.core;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.apco.core.ApcoPayUtills;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZTechnicalViolationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by ThinkPadT410 on 3/6/2017.
 */
public class KotakAllPayCardPaymentGateway extends AbstractPaymentGateway
{
    public static Logger logger = new Logger(KotakAllPayCardPaymentGateway.class.getName());
    public static TransactionLogger transactionLogger = new TransactionLogger(KotakAllPayCardPaymentGateway.class.getName());

    public static final String GATEWAY_TYPE = "kotak";

    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.KotakServlet");

    boolean useProxy = false;	//change to 'true' is using a proxy server

    public String getMaxWaitDays()
    {
        return "3.5";
    }

    public  KotakAllPayCardPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public final static String TRANSACTIONTYPE = "TxnType";
    public final static String ORDERID = "TxnRefNo"; //A unique value created by the merchant.
    public final static String MERCHANTID = "MerchantId";
    public final static String PASSCODE = "PassCode";
    public final static String ORDERINFO = "OrderInfo";
    public final static String AMOUNT = "Amount";
    public final static String TERMINALID = "TerminalId";
    public final static String MCC = "MCC"; //Merchant Category Code
    public final static String NAME = "MerchantName";
    public final static String CITY = "MerchantCity";
    public final static String STATE = "MerchantState";
    public final static String ZIP = "MerchPostalCode";
    public final static String PHONE = "MerchPhone";
    public final static String RETURNURL = "ReturnURL";
    public final static String CARDNUMBER = "CardNumber";
    public final static String EXPIRYDATE = "ExpiryDate";
    public final static String CVV = "CardSecurityCode";
    //public final static String A = "SecureHash"; //secure secret
    public final static String ENCDATA = "EncData"; //The encrypted value of the request parameters and the secure hash is sent in this field

    public final static String TRASACTIONID = "RetRefNo";
    public final static String REFUNDAMOUNT = "RefundAmount";
    public final static String CAPTUREAMOUNT = "CaptureAmount";
    public final static String REASON = "RefCancelID";

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO genericRequestVO)
    {
        logger.debug("Entering processAuthentication in KotakAllPayCardPaymentGateway:::");
        transactionLogger.debug("Entering processAuthentication in KotakAllPayCardPaymentGateway:::");

        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommRequestVO commRequestVO  =    (CommRequestVO)genericRequestVO;
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO= commRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO= commRequestVO.getCardDetailsVO();
        KotakAccount kotakAccount = new KotakAccount();
        KotakEncryptionDecryptionUtils kotakEncryptionDecryptionUtils = new KotakEncryptionDecryptionUtils();

        String kotakMid = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();

        Hashtable dataHash = kotakAccount.getAccountValuesFromDb(accountId);
        //String encKey = "3a5c1c4e81d7eb133a5c1c4e81d7eb13";
        String encKey = (String) dataHash.get("enckey");

        Map fields = new HashMap();
        Map fieldslog = new HashMap();
        Functions functions = new Functions();
        try
        {
            fields.put(CARDNUMBER, genericCardDetailsVO.getCardNum());
            fields.put(EXPIRYDATE, genericCardDetailsVO.getExpMonth() + "" + genericCardDetailsVO.getExpYear());
            fields.put(TRANSACTIONTYPE, "02");
            fields.put(AMOUNT, genericTransDetailsVO.getAmount().replace(".", ""));
            fields.put(ORDERINFO, genericTransDetailsVO.getOrderId());
            fields.put(RETURNURL,RB.getString("KOTAK_NOTIFICATION"));
            fields.put(MERCHANTID, kotakMid);
            fields.put(MCC, dataHash.get("mcc"));
            fields.put(STATE, genericAddressDetailsVO.getState());
            fields.put(CVV, genericCardDetailsVO.getcVV());
            fields.put(CITY, genericAddressDetailsVO.getCity());
            fields.put(PASSCODE, dataHash.get("passcode"));
            fields.put(ZIP, genericAddressDetailsVO.getZipCode());
            fields.put(NAME, dataHash.get("merchantname"));
            fields.put(TERMINALID, dataHash.get("terminalid"));
            fields.put(PHONE, genericAddressDetailsVO.getPhone());
            fields.put(ORDERID, trackingID);

            fieldslog.put(CARDNUMBER, functions.maskingPan(genericCardDetailsVO.getCardNum()));
            fieldslog.put(EXPIRYDATE, functions.maskingExpiry(genericCardDetailsVO.getExpMonth() + "" + genericCardDetailsVO.getExpYear()));
            fieldslog.put(TRANSACTIONTYPE, "02");
            fieldslog.put(AMOUNT, genericTransDetailsVO.getAmount().replace(".", ""));
            fieldslog.put(ORDERINFO, genericTransDetailsVO.getOrderId());
            fieldslog.put(RETURNURL,RB.getString("KOTAK_NOTIFICATION"));
            fieldslog.put(MERCHANTID, kotakMid);
            fieldslog.put(MCC, dataHash.get("mcc"));
            fieldslog.put(STATE, genericAddressDetailsVO.getState());
            fieldslog.put(CVV, functions.maskingNumber(genericCardDetailsVO.getcVV()));
            fieldslog.put(CITY, genericAddressDetailsVO.getCity());
            fieldslog.put(PASSCODE, dataHash.get("passcode"));
            fieldslog.put(ZIP, genericAddressDetailsVO.getZipCode());
            fieldslog.put(NAME, dataHash.get("merchantname"));
            fieldslog.put(TERMINALID, dataHash.get("terminalid"));
            fieldslog.put(PHONE, genericAddressDetailsVO.getPhone());
            fieldslog.put(ORDERID, trackingID);

            logger.debug("fields----" + trackingID + "--" + fieldslog);

            String secureHash = kotakEncryptionDecryptionUtils.hashAllFields(fields);
            logger.debug("secureHash----" + trackingID + "--" + secureHash);

            fields.put("SecureHash", secureHash);
            String encData = kotakEncryptionDecryptionUtils.createPostDataForSale(fields);

            logger.debug("encData---" + encData);

            String encHash = kotakEncryptionDecryptionUtils.encrypt(encData, encKey);
            logger.debug("encHash----" + encHash);

            fields.put(ENCDATA, encHash);
            logger.debug("saleMap in KotakAllPayCardUtills-----" + fields);

            commResponseVO.setStatus("pending3DConfirmation");
            if(isTest)
            {
                commResponseVO.setUrlFor3DRedirect(RB.getString("TESTURL_AUTHSALE"));
            }
            else
            {
                commResponseVO.setUrlFor3DRedirect(RB.getString("LIVE_AUTHSALE"));
            }
            commResponseVO.setRequestMap(fields);
        }
        catch (Exception e)
        {

        }
        return commResponseVO;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO genericRequestVO)
    {
        logger.debug("Entering processSale in KotakAllPayCardPaymentGateway:::");
        transactionLogger.debug("Entering processSale in KotakAllPayCardPaymentGateway:::");

        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO)genericRequestVO;
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO= commRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO= commRequestVO.getCardDetailsVO();

        KotakAccount kotakAccount = new KotakAccount();
        KotakEncryptionDecryptionUtils kotakEncryptionDecryptionUtils = new KotakEncryptionDecryptionUtils();

        String kotakMid = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();

        Hashtable dataHash = kotakAccount.getAccountValuesFromDb(accountId);
        //String encKey = "3a5c1c4e81d7eb133a5c1c4e81d7eb13";
        String encKey = (String) dataHash.get("enckey");
        Functions functions = new Functions();
        Map fields = new HashMap();
        Map fieldslog = new HashMap();
        try
        {
            fields.put(CARDNUMBER, genericCardDetailsVO.getCardNum());
            fields.put(EXPIRYDATE, genericCardDetailsVO.getExpMonth() + "" + genericCardDetailsVO.getExpYear());
            fields.put(TRANSACTIONTYPE, "01");
            fields.put(AMOUNT, genericTransDetailsVO.getAmount().replace(".", ""));
            fields.put(ORDERINFO, genericTransDetailsVO.getOrderId());
            fields.put(RETURNURL,RB.getString("KOTAK_NOTIFICATION"));
            fields.put(MERCHANTID, kotakMid);
            fields.put(MCC, dataHash.get("mcc"));
            fields.put(STATE, genericAddressDetailsVO.getState());
            fields.put(CVV, genericCardDetailsVO.getcVV());
            fields.put(CITY, genericAddressDetailsVO.getCity());
            fields.put(PASSCODE, dataHash.get("passcode"));
            fields.put(ZIP, genericAddressDetailsVO.getZipCode());
            fields.put(NAME, dataHash.get("merchantname"));
            fields.put(TERMINALID, dataHash.get("terminalid"));
            fields.put(PHONE, genericAddressDetailsVO.getPhone());
            fields.put(ORDERID, trackingID);

            fieldslog.put(CARDNUMBER, functions.maskingPan(genericCardDetailsVO.getCardNum()));
            fieldslog.put(EXPIRYDATE, functions.maskingExpiry(genericCardDetailsVO.getExpMonth() + "" + genericCardDetailsVO.getExpYear()));
            fieldslog.put(TRANSACTIONTYPE, "01");
            fieldslog.put(AMOUNT, genericTransDetailsVO.getAmount().replace(".", ""));
            fieldslog.put(ORDERINFO, genericTransDetailsVO.getOrderId());
            fieldslog.put(RETURNURL,RB.getString("KOTAK_NOTIFICATION"));
            fieldslog.put(MERCHANTID, kotakMid);
            fieldslog.put(MCC, dataHash.get("mcc"));
            fieldslog.put(STATE, genericAddressDetailsVO.getState());
            fieldslog.put(CVV, functions.maskingNumber(genericCardDetailsVO.getcVV()));
            fieldslog.put(CITY, genericAddressDetailsVO.getCity());
            fieldslog.put(PASSCODE, dataHash.get("passcode"));
            fieldslog.put(ZIP, genericAddressDetailsVO.getZipCode());
            fieldslog.put(NAME, dataHash.get("merchantname"));
            fieldslog.put(TERMINALID, dataHash.get("terminalid"));
            fieldslog.put(PHONE, genericAddressDetailsVO.getPhone());
            fieldslog.put(ORDERID, trackingID);

            logger.debug("fields----" + trackingID + "--" + fieldslog);

            String secureHash = kotakEncryptionDecryptionUtils.hashAllFields(fields);
            logger.debug("secureHash----" + secureHash);

            fields.put("SecureHash", secureHash);
            String encData = kotakEncryptionDecryptionUtils.createPostDataForSale(fields);

            logger.debug("encData---" + encData);

            String encHash = kotakEncryptionDecryptionUtils.encrypt(encData, encKey);
            logger.debug("encHash----" + encHash);

            fields.put(ENCDATA, encHash);
            logger.debug("saleMap in KotakAllPayCardUtills-----" + fields);

            commResponseVO.setStatus("pending3DConfirmation");
            if(isTest)
            {
                commResponseVO.setUrlFor3DRedirect(RB.getString("TESTURL_AUTHSALE"));
            }
            else
            {
                commResponseVO.setUrlFor3DRedirect(RB.getString("LIVE_AUTHSALE"));
            }
            commResponseVO.setRequestMap(fields);
        }
        catch (Exception e)
        {

        }
        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO genericRequestVO)
    {
        logger.debug("Entering processRefund in KotakPaymentGateway-----");
        transactionLogger.debug("Entering processRefund in KotakPaymentGateway-----");

        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO reqVO = (CommRequestVO) genericRequestVO;
        CommTransactionDetailsVO transDetailsVO = reqVO.getTransDetailsVO();
        //GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        String refundUrl = "";

        KotakEncryptionDecryptionUtils kotakEncryptionDecryptionUtils = new KotakEncryptionDecryptionUtils();
        if(isTest)
        {
            refundUrl = RB.getString("TEST_REFUNDCAPTURE_URL");
        }
        else
        {
            refundUrl = RB.getString("LIVE_REFUNDCAPTURE_URL");
        }

        KotakAccount kotakAccount = new KotakAccount();
        Hashtable dataHash = kotakAccount.getAccountValuesFromDb(accountId);
        String secureSecret = (String) dataHash.get("securesecret");

        logger.debug("amount-----" +transDetailsVO.getAmount());
        logger.debug("amount1-----" +transDetailsVO.getPreviousTransactionId());

        String proxyHost = RB.getString("PROXYHOST");
        String proxyPort = RB.getString("PROXYPORT");

        Map refundMap = new HashMap();
        refundMap.put(ORDERID,trackingID);
        refundMap.put(TRANSACTIONTYPE,"04");
        refundMap.put(MERCHANTID,merchantId);
        refundMap.put(PASSCODE,dataHash.get("passcode"));
        refundMap.put(AMOUNT,transDetailsVO.getAmount().replace(".", ""));
        refundMap.put(TERMINALID,dataHash.get("terminalid"));
        refundMap.put(TRASACTIONID, transDetailsVO.getPreviousTransactionId());
        refundMap.put(REFUNDAMOUNT, transDetailsVO.getAmount().replace(".", ""));

        logger.debug("refundMap----" + trackingID + "--" + refundMap);
        refundMap.remove("SubButL");
        String secureHash = null;
        if (secureSecret != null && secureSecret.length() > 0)
        {
            secureHash = kotakEncryptionDecryptionUtils.hashAllFields(refundMap);
            logger.debug("secureHash-----"+secureHash);
        }
        refundMap.put("SecureHash", secureHash);
        logger.debug("secureHash 1-----"+refundMap.get("SecureHash"));

        String postData = kotakEncryptionDecryptionUtils.createPostDataFromMap(refundMap);
        logger.debug("postData----"+postData);
        String resQS = "";

        try
        {
            //create a URL connection to the Payment Gateway
            resQS = kotakEncryptionDecryptionUtils.doPost(refundUrl, postData, useProxy, proxyHost, Integer.valueOf(proxyPort));
            logger.debug("resQS----"+resQS);
        }
        catch (UnknownHostException ex)
        {
            logger.error("UnknownHostException---",ex);
        }
        catch (IOException ex)
        {
            logger.error("IOException---",ex);
        }

        //extract the fields from response
        Map responseFields = kotakEncryptionDecryptionUtils.createMapFromResponse(resQS);
        String TxnRefNo = null2unknown("TxnRefNo", responseFields);
        String MerchantId = null2unknown("MerchantId", responseFields);
        String Amount = null2unknown("Amount", responseFields);
        String TerminalId = null2unknown("TerminalId", responseFields);
        String Status = null2unknown("Status", responseFields);
        String TxnType = null2unknown("TxnType", responseFields);
        String BatchNo = null2unknown("BatchNo", responseFields);
        String RetRefNo = null2unknown("RetRefNo", responseFields);
        String RefundAmount = null2unknown("RefundAmount", responseFields);
        String CancellationId = null2unknown("RefCancelID", responseFields);
        String IsgSecureHash = null2unknown("SecureHash", responseFields);
        responseFields.remove("SecureHash");
        String hashValidated = null;
        boolean errorExists = false;
        logger.debug("responseFields----"+responseFields);

        String merchantSecureHash = kotakEncryptionDecryptionUtils.hashAllFields(responseFields);
        if (IsgSecureHash.equalsIgnoreCase(merchantSecureHash))
        {
            hashValidated = "<font color='#00AA00'><strong>CORRECT</strong></font>";
        } else
        {
            errorExists = true;
            hashValidated = "<font color='#FF0066'><strong>INVALID HASH</strong></font>";
        }
        /*response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        response.setDateHeader("Expires", -1);
        response.setDateHeader("Last-Modified", 0);*/

        String status = "fail";
        if(responseFields!=null)
        {
            if (responseFields.get("Status").toString().equalsIgnoreCase("success"))
            {
                status = "success";
                commResponseVO.setMerchantOrderId(TxnRefNo);
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                commResponseVO.setDescription("Transaction Approved " + status);
                commResponseVO.setTransactionId(RetRefNo);
                commResponseVO.setRemark(Status);
            }
            else
            {
                status = "fail";
                commResponseVO.setRemark(Status);
                commResponseVO.setDescription("Transaction Failed " + Status);
            }
        }

        commResponseVO.setStatus(status);
        commResponseVO.setTransactionType("refund");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        commResponseVO.setAmount(RefundAmount);
        commResponseVO.setResponseHashInfo(BatchNo);

        return commResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO genericRequestVO)
    {
        logger.debug("Entering processCapture in KotakAllPayCardPaymentGateway-----");
        transactionLogger.debug("Entering processCapture in KotakAllPayCardPaymentGateway-----");

        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO)genericRequestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        //GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();

        KotakEncryptionDecryptionUtils kotakEncryptionDecryptionUtils = new KotakEncryptionDecryptionUtils();

        KotakAccount kotakAccount = new KotakAccount();
        Hashtable dataHash = kotakAccount.getAccountValuesFromDb(accountId);
        String secureSecret = (String) dataHash.get("securesecret");
        logger.debug("amt---"+commTransactionDetailsVO.getAmount());
        logger.debug("previous tid---"+commTransactionDetailsVO.getPreviousTransactionId());
        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        String captureUrl = "";
        if(isTest)
        {
            captureUrl = RB.getString("TEST_REFUNDCAPTURE_URL");
        }
        else
        {
            captureUrl = RB.getString("LIVE_REFUNDCAPTURE_URL");
        }

        String proxyHost = RB.getString("PROXYHOST");
        String proxyPort = RB.getString("PROXYPORT");

        Map captureMap = new HashMap();
        captureMap.put(ORDERID,trackingID);
        captureMap.put(TRANSACTIONTYPE,"03");
        captureMap.put(MERCHANTID,merchantId);
        captureMap.put(PASSCODE,dataHash.get("passcode"));
        //captureMap.put(AMOUNT,commTransactionDetailsVO.getAmount().replace(".", ""));
        captureMap.put(AMOUNT,"500");
        captureMap.put(TERMINALID,dataHash.get("terminalid"));
        captureMap.put(TRASACTIONID,commTransactionDetailsVO.getPreviousTransactionId());
        //captureMap.put(CAPTUREAMOUNT,commTransactionDetailsVO.getAmount().replace(".", ""));
        captureMap.put(CAPTUREAMOUNT,"500");

        logger.debug("captureMap----"+trackingID + "--" + captureMap);

        captureMap.remove("SubButL");
        String secureHash = null;
        if (secureSecret != null && secureSecret.length() > 0)
        {
            secureHash = kotakEncryptionDecryptionUtils.hashAllFields(captureMap);
            captureMap.put("SecureHash", secureHash);
            logger.debug("secureHash-----"+secureHash);
            logger.debug("secureHash 1-----"+captureMap.get("SecureHash"));
        }

        String postData = kotakEncryptionDecryptionUtils.createPostDataFromMap(captureMap);
        logger.debug("postData----"+postData);
        String resQS = "";
        String message = "";
        try
        {
            //create a URL connection to the Payment Gateway
            resQS = kotakEncryptionDecryptionUtils.doPost(captureUrl, postData, useProxy, proxyHost, Integer.valueOf(proxyPort));
        }
        catch (Exception ex)
        {
            logger.error("IOException---",ex);
        }

        //extract the fields from response
        Map responseFields = kotakEncryptionDecryptionUtils.createMapFromResponse(resQS);
        String TxnRefNo = null2unknown("TxnRefNo", responseFields);
        String MerchantId = null2unknown("MerchantId", responseFields);
        String OrderInfo = null2unknown("OrderInfo", responseFields);
        String Amount = null2unknown("Amount", responseFields);
        String TerminalId = null2unknown("TerminalId", responseFields);
        String Status = null2unknown("Status", responseFields);
        String TxnType = null2unknown("TxnType", responseFields);
        String BatchNo = null2unknown("BatchNo", responseFields);
        String RetRefNo = null2unknown("RetRefNo", responseFields);
        String AuthCode = null2unknown("AuthCode", responseFields);
        String CapturedAmount = null2unknown("CapturedAmount", responseFields);
        String IsgSecureHash = null2unknown("SecureHash", responseFields);
        responseFields.remove("SecureHash");
        String hashValidated = null;
        boolean errorExists = false;
        logger.debug("responseFields----"+responseFields);

        String merchantSecureHash = kotakEncryptionDecryptionUtils.hashAllFields(responseFields);
        if (IsgSecureHash.equalsIgnoreCase(merchantSecureHash)) {
            hashValidated = "<font color='#00AA00'><strong>CORRECT</strong></font>";
        } else {
            errorExists = true;
            hashValidated = "<font color='#FF0066'><strong>INVALID HASH</strong></font>";
        }

        String status = "fail";
        if(responseFields!=null)
        {
            if (responseFields.get("Status").toString().equalsIgnoreCase("Success"))
            {
                status = "success";
                commResponseVO.setMerchantOrderId(TxnRefNo);
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                commResponseVO.setDescription("Transaction Approved " + status);
                commResponseVO.setTransactionId(RetRefNo);
                commResponseVO.setRemark(Status);
            }
            else
            {
                status = "fail";
                commResponseVO.setRemark(Status);
                commResponseVO.setDescription("Transaction Failed " + Status);
            }
        }

        commResponseVO.setStatus(status);
        commResponseVO.setTransactionType("capture (" + TxnType + ")");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        commResponseVO.setAmount(CapturedAmount);
        commResponseVO.setResponseHashInfo(AuthCode);
        commResponseVO.setTransactionStatus(OrderInfo);

        return commResponseVO;
    }

    public GenericResponseVO processInquiry(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        logger.debug("Inside processInquiry Kotak:::");

        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO)requestVO;
        //GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        //String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        //String merchantId = GatewayAccountService.getGatewayAccount("2663").getMerchantId();

        String proxyHost = RB.getString("PROXYHOST");
        String proxyPort = RB.getString("PROXYPORT");

        KotakEncryptionDecryptionUtils kotakEncryptionDecryptionUtils = new KotakEncryptionDecryptionUtils();

        KotakAccount kotakAccount = new KotakAccount();

        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        String statusUrl = "";
        if(isTest)
        {
            statusUrl = RB.getString("TEST_STATUS_URL");
        }
        else
        {
            statusUrl = RB.getString("LIVE_STATUS_URL");
        }

        Hashtable dataHash = kotakAccount.getAccountValuesFromDb(accountId);
        String secureSecret = (String) dataHash.get("securesecret");
        Map statusMap = new HashMap();
        statusMap.put(ORDERID,trackingID);
        statusMap.put(TRANSACTIONTYPE,"05");
        statusMap.put(MERCHANTID,"TESTPYMNTZMERCH");
        statusMap.put(PASSCODE,"OXEY8157");
        statusMap.put(TERMINALID,"20001493");
        //statusMap.put(PASSCODE,dataHash.get("passcode"));
        //statusMap.put(TERMINALID, dataHash.get("terminalid"));

        //System.out.println("statusMap----"+statusMap);
        logger.debug("statusMap----"+trackingID + "--" + statusMap);

        statusMap.remove("SubButL");
        String secureHash = null;
        if (secureSecret != null && secureSecret.length() > 0)
        {
            secureHash = kotakEncryptionDecryptionUtils.hashAllFields(statusMap);
            statusMap.put("SecureHash", secureHash);
            //System.out.println("secureHash-----" + secureHash);
            logger.debug("secureHash-----" + trackingID + "--" + secureHash);
        }

        String postData = kotakEncryptionDecryptionUtils.createPostDataFromMap(statusMap);
        logger.debug("postData----"+postData);
        String resQS = "";
        String message = "";
        try {

            //create a URL connection to the Payment Gateway
            resQS = kotakEncryptionDecryptionUtils.doPost(statusUrl, postData,  useProxy, proxyHost, Integer.valueOf(proxyPort));

            //System.out.println("status response----"+resQS);
            logger.debug("status response----" + resQS);

            Map responseFields = kotakEncryptionDecryptionUtils.createMapFromResponse(resQS);

            String status = "";
            String amount = null2unknown("Amount", responseFields);
            String trxRefNo = null2unknown("TxnRefNo", responseFields);
            String transactionStatus = null2unknown("Message", responseFields);
            String retRefNo = null2unknown("RetRefNo", responseFields);
            String responseCode = null2unknown("ResponseCode", responseFields);
            if (responseFields.get("ResponseCode").equals("00"))
            {
                status = "success";
            }
            else
            {
                status = "fail";
            }

            //commResponseVO.setMerchantOrderId(responseInquiry.get("orderNo"));
            //commResponseVO.setTransactionId(responseInquiry.get("tradeNo"));

            commResponseVO.setStatus(status);
            commResponseVO.setDescription(transactionStatus);
            commResponseVO.setAmount(amount);
            commResponseVO.setTransactionType("inquiry");
            commResponseVO.setErrorCode(responseCode);
        }
        catch (Exception ex)
        {
            logger.error("Kotak Inquiry Exception---",ex);
        }
        return commResponseVO;
    }

    public static void main(String[] args) throws PZTechnicalViolationException
    {

        KotakAllPayCardPaymentGateway k = new KotakAllPayCardPaymentGateway("2663");

        //CommResponseVO c = (CommResponseVO)k.processInquiry("40344",null);
        String SECURE_SECRET = "F3C63F6FFFC176E4AAD530C59833719D";

        boolean useProxy = false;	//change to 'true' is using a proxy server
        String proxyHost = "";	//add your proxy server IP here
        int proxyPort = 443;	//add your proxy server port here


        String merchantId = "TESTPYMNTZMERCH";


        KotakEncryptionDecryptionUtils kotakEncryptionDecryptionUtils = new KotakEncryptionDecryptionUtils();

        Map statusMap = new HashMap();
        statusMap.put(ORDERID,"40344");
        statusMap.put(TRANSACTIONTYPE,"05");
        statusMap.put(MERCHANTID,merchantId);
        statusMap.put(PASSCODE,"OXEY8157");
        statusMap.put(TERMINALID,"20001493");


        //System.out.println("statusMap----"+statusMap);

        statusMap.remove("SubButL");
        String secureHash = null;
        if (SECURE_SECRET != null && SECURE_SECRET.length() > 0)
        {
            secureHash = kotakEncryptionDecryptionUtils.hashAllFields(statusMap);
            statusMap.put("SecureHash", secureHash);
            //System.out.println("secureHash-----" + secureHash);

        }

        String postData = kotakEncryptionDecryptionUtils.createPostDataFromMap(statusMap);
        logger.debug("postData----"+postData);
        String resQS = "";
        String message = "";
        try
        {
            //create a URL connection to the Payment Gateway
            resQS = kotakEncryptionDecryptionUtils.doPost(RB.getString("STATUS_URL"), postData,  useProxy, proxyHost, proxyPort);
        }
        catch (Exception ex)
        {

        }

    }

    private static String null2unknown(String in, Map responseFields) {
        if (in == null || in.length() == 0 || (String)responseFields.get(in) == null) {
            return "No Value Returned";
        } else {
            return (String)responseFields.get(in);
        }
    }

}
