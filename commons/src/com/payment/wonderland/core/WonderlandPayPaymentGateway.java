package com.payment.wonderland.core;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;

import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.payforasia.core.PayforasiaAccount;
import com.payment.payforasia.core.PayforasiaUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.codehaus.jettison.json.JSONArray;

/**
 * Created by ThinkPadT410 on 9/1/2016.
 */

public class WonderlandPayPaymentGateway extends AbstractPaymentGateway
{
    private static Logger logger = new Logger(WonderlandPayPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(WonderlandPayPaymentGateway.class.getName());

    public static final String GATEWAY_TYPE = "wland";
    private final static String TESTURL = "https://pay.wonderlandpay.com/TestTPInterface";
    private final static String REFUND_URL="http://mer.wonderlandpay.com/RefundInterface";
    private final static String INQUIRY_URL="http://mer.wonderlandpay.com/customerCheck";
    private final static String LIVE_URL="https://pay.wonderlandpay.com/TPInterface";

    public String getMaxWaitDays()
    {
        return null;
    }

    public WonderlandPayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public final static String ACCOUNTID = "accountId";
    public final static String MERCHANTID = "merNo";
    public final static String GATEWAYNO = "gatewayNo";
    public final static String ORDERNO = "orderNo";
    public final static String ORDERCURRENCY = "orderCurrency";

    public final static String AMOUNT = "orderAmount";
    // public final static String PAYMENT_METHOD = "paymentMethod";

    public final static String CARDNO = "cardNo";
    public final static String EXPIRY_MONTH = "cardExpireMonth";
    public final static String EXPIRY_YEAR = "cardExpireYear";
    public final static String CVV = "cardSecurityCode";
    public final static String ISSUING_BANK = "issuingBank";
    public final static String CUSTOMER_ID = "customerID";
    public final static String FIRST_NAME = "firstName";
    public final static String LAST_NAME = "lastName";
    public final static String EMAIL = "email";
    public final static String PHONE = "phone";
    public final static String COUNTRY = "country";
    public final static String STATE = "state";
    public final static String CITY = "city";
    public final static String ADDRESS = "address";
    public final static String ZIP = "zip";
    public final static String SIGN_INFO = "signInfo";
    public final static String SIGN_KEY = "08D2024j";
    public final static String TRADE_NO = "tradeNo";
    public final static String REFUND_TYPE = "refundType";
    public final static String TRADE_AMOUNT = "tradeAmount";
    public final static String REFUND_AMOUNT = "refundAmount";
    public final static String CURRENCY = "currency";
    public final static String REFUND_REASON = "refundReason";
    public final static String REFUND_ORDERS ="refundOrders";
    public final static String WEBSITE = "webSite";
    public final static String UNIQUEID = "uniqueId";
    public final static String DEVICENO = "deviceNo";

    public final static String REMARK = "remark";
    public final static String IPADDRESS = "ip";


    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("WonderlandPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public static String SHA256forSales(String merNo, String gatewayNo, String orderNo, String orderCurrency, String orderAmount, String cardNo, String expiryYear, String expiryMonth, String cvv, String signKey) throws PZTechnicalViolationException
    {
        String sha = merNo.trim() + gatewayNo.trim() + orderNo.trim() + orderCurrency.trim() + orderAmount.trim() + cardNo.trim() + expiryYear.trim() + expiryMonth.trim() + cvv.trim() + signKey;

        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(WonderlandPayPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(WonderlandPayPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return hexString.toString().trim();
    }

    public static String SHA256forRefund(String merchantNo, String gatewayNo, String tradeNo, String signKey) throws PZTechnicalViolationException
    {
        String sha = merchantNo.trim() + gatewayNo.trim() + tradeNo.trim()  + signKey;

        StringBuffer hexString = new StringBuffer();
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(WonderlandPayPaymentGateway.class.getName(), "SHA256forRefund()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(WonderlandPayPaymentGateway.class.getName(), "SHA256forRefund()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null, e.getMessage(), e.getCause());
        }

        return hexString.toString().toLowerCase().trim();
    }

    public static String SHA256forInquiry(String merchantNo, String gatewayNo, String signKey) throws PZTechnicalViolationException
    {
        String sha = merchantNo.trim() + gatewayNo.trim() + signKey;

        StringBuffer hexString = new StringBuffer();
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(WonderlandPayPaymentGateway.class.getName(), "SHA256forRefund()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(WonderlandPayPaymentGateway.class.getName(), "SHA256forRefund()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null, e.getMessage(), e.getCause());
        }

        return hexString.toString().toLowerCase().trim();
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVo) throws PZTechnicalViolationException
    {
        logger.debug("Entering processSale of Wonderlandpaymentgateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVo;
        CommResponseVO commResponseVO = new CommResponseVO();
        Functions functions = new Functions();
        WonderlandPayUtils wonderlandPayUtils = new WonderlandPayUtils();

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO merchantAccountVO = commRequestVO.getCommMerchantVO();

        String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
        String accountid = GatewayAccountService.getGatewayAccount(accountId).toString();

        PayforasiaAccount payforasiaAccount = new PayforasiaAccount();
        Hashtable dataHash = payforasiaAccount.getValuesFromDb(accountId);

        //parameters required for SHA256
        String merNo = (String) dataHash.get("mid");
        String gatewayNo = (String) dataHash.get("gatewayno");
        String orderNo = trackingID;
        String orderCurrency = genericTransDetailsVO.getCurrency();
        String amount = genericTransDetailsVO.getAmount();
        String cardNo = genericCardDetailsVO.getCardNum();
        String expiryYear = genericCardDetailsVO.getExpYear();
        String expiryMonth = genericCardDetailsVO.getExpMonth();
        String cvv = genericCardDetailsVO.getcVV();
        String signKey = (String) dataHash.get("signkey");
        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        String merchantWebsite = merchantAccountVO.getSitename();
        String customerIp = addressDetailsVO.getCardHolderIpAddress();

        String customerIpCountryCode = functions.getIPCountryShort(customerIp);
        String binCountryCode = genericCardDetailsVO.getCountry_code_A2();
        transactionLogger.error("Bin Country---"+binCountryCode+"--Ip Country---"+customerIpCountryCode);

        if(functions.isValueNull(binCountryCode) && !binCountryCode.equalsIgnoreCase(customerIpCountryCode))
        {
            customerIp = wonderlandPayUtils.getCardholderIp(cardNo);

            if("0".equalsIgnoreCase(customerIp))
                customerIp = addressDetailsVO.getCardHolderIpAddress();
        }

        Map saleMap = new TreeMap();

        saleMap.put(MERCHANTID, merNo.trim());
        saleMap.put(GATEWAYNO, gatewayNo.trim());
        saleMap.put(ORDERNO, orderNo.trim());
        saleMap.put(ORDERCURRENCY, orderCurrency.trim());
        saleMap.put(AMOUNT, amount.trim());
        saleMap.put(CARDNO, genericCardDetailsVO.getCardNum().trim());
        saleMap.put(EXPIRY_MONTH, genericCardDetailsVO.getExpMonth().trim());
        saleMap.put(EXPIRY_YEAR, genericCardDetailsVO.getExpYear().trim());
        saleMap.put(CVV, genericCardDetailsVO.getcVV().trim());
        saleMap.put(ISSUING_BANK, "ISSUING BANK");
        saleMap.put(FIRST_NAME, addressDetailsVO.getFirstname().trim());
        saleMap.put(LAST_NAME, addressDetailsVO.getLastname().trim());
        saleMap.put(EMAIL, addressDetailsVO.getEmail().trim());
        saleMap.put(IPADDRESS, customerIp);
        saleMap.put(PHONE, addressDetailsVO.getPhone());
        saleMap.put(COUNTRY, addressDetailsVO.getCountry());
        saleMap.put(STATE, addressDetailsVO.getState());
        saleMap.put(CITY, addressDetailsVO.getCity());
        saleMap.put(ADDRESS, addressDetailsVO.getStreet());
        saleMap.put(ZIP, addressDetailsVO.getZipCode());
        saleMap.put(SIGN_INFO, SHA256forSales(merNo, gatewayNo, orderNo, orderCurrency, amount, cardNo, expiryYear, expiryMonth, cvv, signKey).toString());
//        saleMap.put(WEBSITE,merchantWebsite);
        saleMap.put(WEBSITE,GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_SITE());
        saleMap.put(UNIQUEID,commRequestVO.getUniqueId());
        saleMap.put(DEVICENO,commRequestVO.getDeviceId());

        String strRequest = WonderlandPayUtils.joinMapValue(saleMap, '&');

        String logRequest = WonderlandPayUtils.joinMapValueForLogging(saleMap, '&');
        logger.error("------ sale request----"+"trackingid"+trackingID + logRequest);

        String response = "";
        if(isTest)
        {
            response = WonderlandPayUtils.doPostHTTPSURLConnection(TESTURL, strRequest);
        }
        else
        {
            response = WonderlandPayUtils.doPostHTTPSURLConnection(LIVE_URL, strRequest);
        }
        logger.error("Sale Response-----" + response);
        transactionLogger.error("Sale Response-----" + response);

        Map readResponse = WonderlandPayUtils.ReadSalesResponse(response);

        if (readResponse != null && !readResponse.equals(""))
        {
            String status = "";
            String orderStatus = String.valueOf(readResponse.get("orderStatus"));
            String bankDescriptor = String.valueOf(readResponse.get("billAddress"));

            if (orderStatus != null && !orderStatus.equals("") && !orderStatus.equals("null"))
            {
                if (orderStatus.equals("1"))
                {
                    status = "success";
                    transactionLogger.error("in side order status = success" + orderStatus);
                }
                else if (orderStatus.equals("0"))
                {
                    status = "fail";
                    transactionLogger.error("in side order status = fail" + orderStatus);
                }
                else if (orderStatus.equals("-1"))
                {
                    status = "Pending";
                    transactionLogger.error("in side order status = Pending" + orderStatus);
                }
                else if (orderStatus.equals("-2"))
                {
                    status = "To be confirmed";
                    transactionLogger.error("in side order status = To be confirmed" + orderStatus);
                }
                if (bankDescriptor != null)
                {
                    descriptor = bankDescriptor;
                }
            }

            commResponseVO.setTransactionId((String) readResponse.get("tradeNo"));
            commResponseVO.setAmount((String) readResponse.get("orderAmount"));
            commResponseVO.setTransactionStatus(status);
            commResponseVO.setErrorCode(orderStatus);
            commResponseVO.setDescription((String) readResponse.get("orderInfo"));
            commResponseVO.setStatus(status);
            commResponseVO.setTransactionType("sale");
            commResponseVO.setDescriptor(descriptor);

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        }
        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        logger.debug("Entering processRefund of wonderland...");
        transactionLogger.debug("Entering processRefund of Wonderland...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();

        String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
        String accountid = GatewayAccountService.getGatewayAccount(accountId).toString();

        PayforasiaAccount payforasiaAccount = new PayforasiaAccount();
        Hashtable dataHash = payforasiaAccount.getValuesFromDb(accountId);
        transactionLogger.error("datahash---"+dataHash);

        String merNo = (String) dataHash.get("mid");
        String gatewayNo = (String) dataHash.get("gatewayno");
        String tradeNo = genericTransDetailsVO.getPreviousTransactionId();
        String signKey = (String) dataHash.get("signkey");

        transactionLogger.error("merNo---"+merNo+ "gatewayNo----"+gatewayNo+ "tradeNo---"+tradeNo+ "signkey---"+signKey);

        try
        {
            String refundRequest =
                    "{\"merNo\":" + merNo + ",\n" +
                            "\"gatewayNo\":\"" + gatewayNo + "\",\n" +
                            "\"signInfo\":\"" + SHA256forRefund(merNo, gatewayNo, tradeNo, signKey) + "\",\n" +
                            "\"refundOrders\":[{\n" +
                            "\"tradeNo\":\"" + tradeNo + "\",\n" +
                            "\"orderNo\":\"" + trackingID + "\",\n" +
                            "\"currency\":\"" + genericTransDetailsVO.getCurrency() + "\",\n" +
                            "\"tradeAmount\":\"" + genericTransDetailsVO.getPreviousTransactionAmount() + "\",\n" +
                            "\"refundAmount\":\"" + genericTransDetailsVO.getAmount() + "\",\n" +
                            "\"refundReason\":\"" + genericTransDetailsVO.getOrderDesc() + "\"\n" +
                            "}]\n" +
                            "}\n" +
                            "}\n";


            logger.error("Refund Req----" + refundRequest);
            transactionLogger.error("Refund Req----" + refundRequest);

            String response = WonderlandPayUtils.doPostHTTPSURLConnection(REFUND_URL, refundRequest);
            logger.error("Refund Res----" + response);
            transactionLogger.error("Response Res----" + response);

            JSONObject jsonObject = new JSONObject(response);
            transactionLogger.error("jsonobj res---"+jsonObject);

            JSONArray readArr = new JSONArray(jsonObject.getString("refundOrders"));
            transactionLogger.error("read array from json----"+readArr);
            //System.out.println("bankInfo.length()---"+readArr.length());

            org.codehaus.jettison.json.JSONObject jsonProductObject = readArr.getJSONObject(0);

            jsonProductObject.getString("refundStatus");

            String status="fail";
            if(jsonObject.getString("errorStatus").equals("1") && jsonProductObject.getString("refundStatus").equals("1"))
            {
                status="success";

            }
            commResponseVO.setStatus(status);
            commResponseVO.setTransactionId(jsonProductObject.getString("tradeNo"));
            commResponseVO.setTransactionType("refund");
            commResponseVO.setErrorCode(jsonObject.getString("errorStatus"));
            commResponseVO.setDescription(jsonProductObject.getString("refundInfo"));
            commResponseVO.setDescriptor(descriptor);

        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(WonderlandPayPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (org.codehaus.jettison.json.JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(WonderlandPayPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return commResponseVO;
    }

    public GenericResponseVO processInquiry(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Inside processInquiry :::");

        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();

        //String accountid = GatewayAccountService.getGatewayAccount(accountId).toString();
        PayforasiaAccount payforasiaAccount = new PayforasiaAccount();
        Hashtable dataHash = payforasiaAccount.getValuesFromDb(accountId);

        String merNo = (String) dataHash.get("mid");
        String gatewayNo = (String) dataHash.get("gatewayno");
        String signKey = (String) dataHash.get("signkey");

        Map inquiryMap = new TreeMap();

        inquiryMap.put(MERCHANTID, merNo.trim());
        inquiryMap.put(GATEWAYNO, gatewayNo.trim());
        inquiryMap.put(SIGN_INFO, SHA256forInquiry(merNo, gatewayNo, signKey));
        inquiryMap.put(ORDERNO, trackingID);

        String inquiryParameters = WonderlandPayUtils.joinMapValue(inquiryMap, '&');
        logger.debug("===inquiry request===" + inquiryParameters);
        transactionLogger.debug("===inquiry request===" + inquiryParameters);

        String response = WonderlandPayUtils.doPostHTTPSURLConnection(INQUIRY_URL, inquiryParameters);
        logger.debug("===inquiry response===" + response);
        transactionLogger.debug("===inquiry response===" + response);

        Map<String, String> responseInquiry = WonderlandPayUtils.ReadInquiryResponse(response);

        logger.debug("Inquiry read response-----" + responseInquiry);

        String status = "fail";
        if(responseInquiry != null && !responseInquiry.equals(""))
        {
            String statusCode = String.valueOf(responseInquiry.get("queryResult"));
            String statusDescription="";
            if (statusCode != null && !statusCode.equals(""))
            {
                if (statusCode.equals("1"))
                {
                    status = "success";
                    statusDescription = "Transaction Successful";
                }
                else if (statusCode.equals("0"))
                {
                    status = "fail";
                    statusDescription = "Transaction Failed";
                }
                else if (statusCode.equals("2"))
                {
                    status = "fail";
                    statusDescription = "Transaction not found";
                }
                else if (statusCode.equals("-2") || statusCode.equals("-1"))
                {
                    status = "fail";
                    statusDescription = "To be confirmed/processing";
                }
            }
            else
            {
                status = "fail";
            }

            commResponseVO.setMerchantOrderId(responseInquiry.get("orderNo"));
            commResponseVO.setTransactionId(responseInquiry.get("tradeNo"));

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

            commResponseVO.setAmount(responseInquiry.get("tradeAmount"));
            commResponseVO.setErrorCode(responseInquiry.get("queryResult"));
            commResponseVO.setStatus(status);
            commResponseVO.setDescription(statusDescription);
            commResponseVO.setTransactionType("inquiry");
        }
        return commResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("WonderlandPayPaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }


    public static void main(String[] args) throws PZTechnicalViolationException
    {
       String r = "{\"merNo\":18076,\n" +
               "\"gatewayNo\":\"18076001\",\n" +
               "\"signInfo\":\"9371fd293ca5739e5868da80feb58aa6bf5b7fb7cec404e5f3aee53a58f47477\",\n" +
               "\"refundOrders\":[{\n" +
               "\"tradeNo\":\"T018070917264499756569\",\n" +
               "\"orderNo\":\"73906\",\n" +
               "\"currency\":\"EUR\",\n" +
               "\"tradeamount\":\"T018070917264499756569\",\n" +
               "\"refundAmount\":\"50.00\",\n" +
               "\"refundReason\":\"wewqewqewe\"\n" +
               "}]\n" +
               "}\n" +
               "}";

        String response = WonderlandPayUtils.doPostHTTPSURLConnection(REFUND_URL, r);
        System.out.println("Refund Res----" + response);
    }
}

