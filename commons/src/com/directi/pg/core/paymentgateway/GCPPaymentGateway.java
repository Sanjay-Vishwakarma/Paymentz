package com.directi.pg.core.paymentgateway;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.PzEncryptor;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.payforasia.core.PayforasiaAccount;
import com.payment.payforasia.core.PayforasiaUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.NewMemberSignUp
 * User: Admin
 * Date: 5/29/14
 * Time: 6:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class GCPPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "gcp";
    public final static String ACCOUNTID = "accountId";
    public final static String MERCHANTID = "merNo";
    public final static String GATEWAYNO = "gatewayNo";
    public final static String ORDERNO = "orderNo";
    public final static String ORDERCURRENCY = "orderCurrency";

    //private final static String URL = "https://safer2connect.com/DirectInterface";
    public final static String AMOUNT = "orderAmount";
    //public final static String RETURN_URL = "returnUrl";
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
    public final static String SIGN_KEY = "signkey";
    public final static String TRADE_NO = "tradeNo";
    public final static String REFUND_TYPE = "refundType";
    public final static String TRADE_AMOUNT = "tradeAmount";
    public final static String REFUND_AMOUNT = "refundAmount";
    public final static String CURRENCY = "currency";
    public final static String REFUND_REASON = "refundReason";
    public final static String REMARK = "remark";
    public final static String IPADDRESS = "ip";
    public final static String CSID = "csid";
    public final static String RETURN_URL = "returnUrl";
    private final static String LIVEURL = "https://online-safest.com/TPInterface";//https://online-safest.com/TPInterface";
    private final static String TESTURL = "https://online-safest.com/TestTPInterface";
    private final static String REFUND_URL = "https://check.payforasia.com/servlet/ApplyRefund";
    //private final static String INQUIRY_URL = "https://check.payforasia.com/services/customerCheckWS?WSDL";
    private final static String INQUIRY_URL = "https://check.safer2connect.com/servlet/NormalCustomerCheck";

    //private static Logger log = new Logger(PayforasiaPaymentGateway.class.getName());
    private static TransactionLogger transactionlogger = new TransactionLogger(GCPPaymentGateway.class.getName());
    private static Logger logger = new Logger(GCPPaymentGateway.class.getName());


    public GCPPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static String SHA256forSales(String merchantNo, String gatewayNo, String orderNo, String orderCurrency, String orderAmount, String firstName, String lastName, String cardNo, String expiryYear, String expiryMonth, String cvv, String email, String signKey) throws PZTechnicalViolationException
    {
        String sha = merchantNo.trim() + gatewayNo.trim() + orderNo.trim() + orderCurrency.trim() + orderAmount.trim() + firstName.trim() + lastName.trim() + cardNo.trim() + expiryYear.trim() + expiryMonth.trim() + cvv.trim() + email.trim() + signKey;
        sha.trim();
       // transactionlogger.error("sha256 combination---" + sha);

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
            PZExceptionHandler.raiseTechnicalViolationException(GCPPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(GCPPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return hexString.toString().trim();

    }

    public static String SHA256forRefund(String merchantNo, String gatewayNo, String tradeNo, String refundType, String signKey) throws PZTechnicalViolationException
    {
        String sha = merchantNo.trim() + gatewayNo.trim() + tradeNo.trim() + refundType.trim() + signKey;
        sha.trim();

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
            PZExceptionHandler.raiseTechnicalViolationException(GCPPaymentGateway.class.getName(), "SHA256forRefund()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(GCPPaymentGateway.class.getName(), "SHA256forRefund()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return hexString.toString().toLowerCase().trim();

    }

    public static String SHA256forInquiry(String merchantNo, String gatewayNo, String signKey) throws PZTechnicalViolationException
    {
        String sha = merchantNo.trim() + gatewayNo.trim() + signKey;
        sha.trim();

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
            PZExceptionHandler.raiseTechnicalViolationException(GCPPaymentGateway.class.getName(), "SHA256forInquiry()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(GCPPaymentGateway.class.getName(), "SHA256forInquiry()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return hexString.toString().toLowerCase().trim();

    }

    public static void main(String[] args)
    {
        /*String ExpiryDate="ETLbewAAAVQonAStABRBRVMvQ0JDL1BLQ1M1UGFkZGluZwEAABAAEPzIfZZm6et5mSRV38wntMcAAAAQsqbOPSuuHpAk44+fOdmxGgAUwHN4h+XI4ORzEv+8fjfkVwDjc3o=";
        //System.out.println("exp date----"+ExpiryDate);
        String EXP= Encryptor.decryptExpiryDate(ExpiryDate);
        String dateArr[] = EXP.split("/");
        // System.out.println("datearr---"+dateArr);
        String expiryYear = dateArr[1].substring(2,4);
        //System.out.println("expiryyear----"+expiryYear);*/

        //String testURL = "https://check.safer2connect.com/servlet/NormalCustomerCheck";

        try
        {
            Map<String, String> inquiryMap = new HashMap<String, String>();

            /*inquiryMap.put(MERCHANTID, "20543");
            inquiryMap.put(GATEWAYNO, "20543001");
            inquiryMap.put(SIGN_INFO, SHA256forInquiry("20543", "20543001", "fNl6hZh6"));
            inquiryMap.put(ORDERNO, "32050");*/

            /*inquiryMap.put(MERCHANTID, "21006");
            inquiryMap.put(GATEWAYNO, "21006002");
            inquiryMap.put(SIGN_INFO, SHA256forInquiry("21006", "21006002", "4080b08l"));
            inquiryMap.put(ORDERNO, "903677");

            String strRequest = PayforasiaUtils.joinMapValue(inquiryMap, '&');
            System.out.println("request for inquiry---" + strRequest);

            String responseInq = PayforasiaUtils.doPostHTTPSURLConnection(INQUIRY_URL, strRequest);
            System.out.println("response for inquiry---" + responseInq);
            Map<String, String> responseMap =  PayforasiaUtils.ReadInquiryResponse(responseInq.toString());
            System.out.println(responseMap);*/

            Map<String, String> refundMap = new HashMap<String, String>();

            /*refundMap.put(MERCHANTID, "20928");
            refundMap.put(GATEWAYNO, "20928001");
            refundMap.put(SIGN_INFO, SHA256forInquiry("20928", "20928001", "H46024jf"));*/
            refundMap.put(MERCHANTID, "21006");
            refundMap.put(GATEWAYNO, "21006002");
            refundMap.put(SIGN_INFO, SHA256forRefund("21006", "21006002", "2017050116205662826053","1","4080b08l"));
            refundMap.put(TRADE_NO,"2017050116205662826053");
            refundMap.put(REFUND_TYPE,"1");
            refundMap.put(TRADE_AMOUNT,"323.00");
            refundMap.put(REFUND_AMOUNT,"323.00");
            refundMap.put(CURRENCY,"USD");
            refundMap.put(REFUND_REASON,"test");
            refundMap.put(REMARK, "Refund Remark");

            String refundParameters = PayforasiaUtils.joinMapValue(refundMap, '&');
            transactionlogger.error("===refund request===" + refundParameters);
            String response = PayforasiaUtils.doPostHTTPSURLConnection(REFUND_URL, refundParameters);
            transactionlogger.error("===refund response===" + response);
        }
        catch (PZTechnicalViolationException e)
        {
        }
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {

        transactionlogger.error("Entering processSale of GCPPaymentGateway...");

        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String descriptor = gatewayAccount.getDisplayName();
        //String accountid = GatewayAccountService.getGatewayAccount(accountId).toString();
        boolean isTest = gatewayAccount.isTest();
        transactionlogger.error("Entering processSale of GCPPaymentGateway...for "+trackingID+ "---" + genericTransDetailsVO.getResponseOrderNumber());
        PayforasiaAccount payforasiaAccount = new PayforasiaAccount();
        Hashtable dataHash = payforasiaAccount.getValuesFromDb(accountId);

        validateForSale(trackingID, requestVO);

        //parameters required for SHA256
        String merNo = (String) dataHash.get("mid");
        String gatewayNo = (String) dataHash.get("gatewayno");
        String orderNo = trackingID;
        String orderCurrency = genericTransDetailsVO.getCurrency();
        String amount = genericTransDetailsVO.getAmount();
        String firstName = addressDetailsVO.getFirstname();
        String lastName = addressDetailsVO.getLastname();
        String cardNo = genericCardDetailsVO.getCardNum();
        String expiryYear = genericCardDetailsVO.getExpYear();
        String expiryMonth = genericCardDetailsVO.getExpMonth();
        String cvv = genericCardDetailsVO.getcVV();
        String email = addressDetailsVO.getEmail();
        String signKey = (String) dataHash.get("signkey");

        Map saleMap = new TreeMap();

        saleMap.put(MERCHANTID, merNo.trim());
        saleMap.put(GATEWAYNO, gatewayNo.trim());
        saleMap.put(SIGN_KEY, signKey.trim());
        saleMap.put(ORDERNO, trackingID.trim());
        saleMap.put(ORDERCURRENCY, genericTransDetailsVO.getCurrency().trim());
        saleMap.put(AMOUNT, genericTransDetailsVO.getAmount().trim());
        saleMap.put(FIRST_NAME, addressDetailsVO.getFirstname().trim());
        saleMap.put(LAST_NAME, addressDetailsVO.getLastname().trim());
        saleMap.put(CARDNO, genericCardDetailsVO.getCardNum().trim());
        saleMap.put(EXPIRY_YEAR, genericCardDetailsVO.getExpYear().trim());
        saleMap.put(EXPIRY_MONTH, genericCardDetailsVO.getExpMonth().trim());
        saleMap.put(CVV, genericCardDetailsVO.getcVV().trim());
        saleMap.put(EMAIL, addressDetailsVO.getEmail().trim());
        saleMap.put(ISSUING_BANK, "ISSUING BANK");
        saleMap.put(PHONE, addressDetailsVO.getPhone());
        saleMap.put(IPADDRESS, addressDetailsVO.getCardHolderIpAddress());
        saleMap.put(COUNTRY, addressDetailsVO.getCountry());
        saleMap.put(STATE, addressDetailsVO.getState());
        saleMap.put(CITY, addressDetailsVO.getCity());
        saleMap.put(ADDRESS, addressDetailsVO.getStreet());
        saleMap.put(ZIP, addressDetailsVO.getZipCode());
        saleMap.put(CSID, genericTransDetailsVO.getResponseOrderNumber());
        saleMap.put(RETURN_URL, "");
        saleMap.put(SIGN_INFO, SHA256forSales(merNo, gatewayNo, orderNo, orderCurrency, amount, firstName, lastName, cardNo, expiryYear, expiryMonth, cvv, email, signKey).toString());

        Map saleMaplog = new TreeMap();

        saleMaplog.put(MERCHANTID, merNo.trim());
        saleMaplog.put(GATEWAYNO, gatewayNo.trim());
        saleMaplog.put(SIGN_KEY, signKey.trim());
        saleMaplog.put(ORDERNO, trackingID.trim());
        saleMaplog.put(ORDERCURRENCY, genericTransDetailsVO.getCurrency().trim());
        saleMaplog.put(AMOUNT, genericTransDetailsVO.getAmount().trim());
        saleMaplog.put(FIRST_NAME, addressDetailsVO.getFirstname().trim());
        saleMaplog.put(LAST_NAME, addressDetailsVO.getLastname().trim());
        saleMaplog.put(CARDNO, functions.maskingPan(genericCardDetailsVO.getCardNum().trim()));
        saleMaplog.put(EXPIRY_YEAR, functions.maskingNumber(genericCardDetailsVO.getExpYear().trim()));
        saleMaplog.put(EXPIRY_MONTH, functions.maskingNumber(genericCardDetailsVO.getExpMonth().trim()));
        saleMaplog.put(CVV, functions.maskingNumber(genericCardDetailsVO.getcVV().trim()));
        saleMaplog.put(EMAIL, addressDetailsVO.getEmail().trim());
        saleMaplog.put(ISSUING_BANK, "ISSUING BANK");
        saleMaplog.put(PHONE, addressDetailsVO.getPhone());
        saleMaplog.put(IPADDRESS, addressDetailsVO.getCardHolderIpAddress());
        saleMaplog.put(COUNTRY, addressDetailsVO.getCountry());
        saleMaplog.put(STATE, addressDetailsVO.getState());
        saleMaplog.put(CITY, addressDetailsVO.getCity());
        saleMaplog.put(ADDRESS, addressDetailsVO.getStreet());
        saleMaplog.put(ZIP, addressDetailsVO.getZipCode());
        saleMaplog.put(CSID, genericTransDetailsVO.getResponseOrderNumber());
        saleMaplog.put(RETURN_URL, "");
        saleMaplog.put(SIGN_INFO, SHA256forSales(merNo, gatewayNo, orderNo, orderCurrency, amount, firstName, lastName, cardNo, expiryYear, expiryMonth, cvv, email, signKey).toString());


        String strRequest = PayforasiaUtils.joinMapValue(saleMap, '&');
        String strRequestlog = PayforasiaUtils.joinMapValue(saleMaplog, '&');

        String logRequest=PayforasiaUtils.joinMapValueForLogging(saleMap,'&');
        String logRequestlog=PayforasiaUtils.joinMapValueForLogging(saleMaplog,'&');
        transactionlogger.error("------request----for "+trackingID+ "---" + logRequestlog);
        //transactionlogger.error("payforasia request----" + strRequest);

        String response = "";
        if (isTest)
        {
            response = PayforasiaUtils.doPostHTTPSURLConnection(TESTURL, strRequest);
        }
        else
        {
            response = PayforasiaUtils.doPostHTTPSURLConnection(LIVEURL, strRequest);
        }

        transactionlogger.error("------sale response------for "+trackingID+ "---" + response);

        Map responseParameter = PayforasiaUtils.ReadSalesResponse(response);

        if (responseParameter != null && !responseParameter.equals(""))
        {
            String status = "";
            String orderStatus = String.valueOf(responseParameter.get("orderStatus"));
            String bankDescriptor = String.valueOf(responseParameter.get("billAddress"));

            if (orderStatus != null && !orderStatus.equals("") && !orderStatus.equals("null"))
            {
                if (orderStatus.equals("1"))
                {
                    status = "success";
                    transactionlogger.error("in side order status = success" + orderStatus);
                }
                else if (orderStatus.equals("0"))
                {
                    status = "fail";
                    transactionlogger.error("in side order status = fail" + orderStatus);
                }
                else if (orderStatus.equals("-1"))
                {
                    status = "Pending";
                    transactionlogger.error("in side order status = Pending" + orderStatus);
                }
                else if (orderStatus.equals("-2"))
                {
                    status = "To be confirmed";
                    transactionlogger.error("in side order status = To be confirmed" + orderStatus);
                }
                if (bankDescriptor != null)
                {
                    descriptor = bankDescriptor;
                }
            }

            commResponseVO.setTransactionId((String) responseParameter.get("tradeNo"));
            commResponseVO.setAmount((String) responseParameter.get("orderAmount"));
            commResponseVO.setTransactionStatus(status);
            commResponseVO.setErrorCode(orderStatus);
            commResponseVO.setDescription((String) responseParameter.get("orderInfo"));
            commResponseVO.setStatus(status);
            commResponseVO.setTransactionType("sale");
            commResponseVO.setDescriptor(descriptor);
            //commResponseVO.setResponseHashInfo((String) responseParameter.get("riskInfo"));

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

        }
        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        System.setProperty("https.protocols", "TLSv1");
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();

        transactionlogger.debug("Enter in processRefund of PayForAsia");

        //String accountid = GatewayAccountService.getGatewayAccount(accountId).toString();
        PayforasiaAccount payforasiaAccount = new PayforasiaAccount();
        Hashtable dataHash = payforasiaAccount.getValuesFromDb(accountId);

        String merNo = (String) dataHash.get("mid");
        String gatewayNo = (String) dataHash.get("gatewayno");
        String tradeNo = commTransactionDetailsVO.getPreviousTransactionId();
        String refundType = "1";
        String signKey = (String) dataHash.get("signkey");
        transactionlogger.error("commTransactionDetailsVO.getPreviousTransactionAmount()--->for "+trackingID+ "---" +commTransactionDetailsVO.getPreviousTransactionAmount());
        transactionlogger.error("commTransactionDetailsVO.getAmount()--->for "+trackingID+ "---" +commTransactionDetailsVO.getAmount());
        if(Double.parseDouble(commTransactionDetailsVO.getPreviousTransactionAmount())!=Double.parseDouble(commTransactionDetailsVO.getAmount())){
            refundType = "2";
        }
        Map refundMap = new TreeMap();

        refundMap.put(MERCHANTID, merNo.trim());
        refundMap.put(GATEWAYNO, gatewayNo.trim());
        refundMap.put(SIGN_INFO, SHA256forRefund(merNo, gatewayNo, tradeNo, refundType, signKey));
        refundMap.put(TRADE_NO, tradeNo.trim());
        refundMap.put(REFUND_TYPE, refundType.trim());
        refundMap.put(TRADE_AMOUNT, commTransactionDetailsVO.getPreviousTransactionAmount());
        refundMap.put(REFUND_AMOUNT, commTransactionDetailsVO.getAmount());
        refundMap.put(CURRENCY, commTransactionDetailsVO.getCurrency());
        refundMap.put(REFUND_REASON, commTransactionDetailsVO.getOrderDesc());
        refundMap.put(REMARK, "Refund Remark");

        String refundParameters = PayforasiaUtils.joinMapValue(refundMap, '&');
        transactionlogger.error("===refund request===for "+trackingID+ "---" + refundParameters);
        String response = PayforasiaUtils.doPostHTTPSURLConnection(REFUND_URL, refundParameters);
        transactionlogger.error("===refund response===for "+trackingID+ "---"+ response);

        Map<String, String> responseRefund = PayforasiaUtils.ReadRefundResponse(response);
        if (responseRefund != null && !responseRefund.equals(""))
        {
            String status = "";
            if (responseRefund != null && !responseRefund.equals(""))
            {
                String statusCode = String.valueOf(responseRefund.get("code"));

                if (statusCode != null && !statusCode.equals(""))
                {
                    if (statusCode.equals("00"))
                    {
                        status = "success";
                    }
                    else
                    {
                        status = "fail";
                    }
                }
                else
                {
                    status = "fail";
                }
            }

            commResponseVO.setTransactionId(responseRefund.get("tradeNo"));
            commResponseVO.setErrorCode(responseRefund.get("code"));
            commResponseVO.setDescription(responseRefund.get("description"));
            commResponseVO.setResponseHashInfo(responseRefund.get("batchNo"));
            commResponseVO.setStatus(status);
            commResponseVO.setTransactionType("refund");

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        }


        return commResponseVO;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.debug("Inside processInquiry :::");
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO=reqVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();

        PayforasiaAccount payforasiaAccount = new PayforasiaAccount();
        Hashtable dataHash = payforasiaAccount.getValuesFromDb(accountId);

        String merNo = (String) dataHash.get("mid");
        String gatewayNo = (String) dataHash.get("gatewayno");
        String signKey = (String) dataHash.get("signkey");

        Map inquiryMap = new TreeMap();

        inquiryMap.put(MERCHANTID, merNo.trim());
        inquiryMap.put(GATEWAYNO, gatewayNo.trim());
        inquiryMap.put(SIGN_INFO, SHA256forInquiry(merNo, gatewayNo, signKey));
        inquiryMap.put(ORDERNO, transactionDetailsVO.getOrderId());

        String inquiryParameters = PayforasiaUtils.joinMapValue(inquiryMap, '&');
        transactionlogger.debug("===inquiry request===" + inquiryParameters);

        String response = PayforasiaUtils.doPostHTTPSURLConnection(INQUIRY_URL, inquiryParameters);
        transactionlogger.debug("===inquiry response===" + response);

        Map<String, String> responseInquiry = PayforasiaUtils.ReadInquiryResponse(response);

        String mid="";
        String merchantOrderId="";
        String transactionId="";
        String authCode="";
        String transactionStatus="";
        String transactionType="";
        String amount="";
        String currency="";
        String remark="";
        String transactionDate="";
        String status="";
        String errorCode="";

        if(responseInquiry != null && !responseInquiry.isEmpty())
        {
            String statusCode=String.valueOf(responseInquiry.get("queryResult"));
            if(statusCode != null && !statusCode.equals("")){
                status="success";
                if (statusCode.equals("0")) //Transaction Declined.
                {
                    transactionStatus="Failed";
                    remark="Transaction Failed";
                }
                else if(statusCode.equals("1")) //Transaction Approved.
                {
                    transactionStatus="Successful";
                    remark="Transaction Successful";
                }
                else if(statusCode.equals("2"))  //Transaction not found.
                {
                    transactionStatus="N/A";
                    remark="Transaction Not Found";
                }
            }
            else{
                status="fail";
                transactionStatus="N/A";
                remark="Internal error while processing your request.";
            }

            mid=responseInquiry.get("merNo");
            merchantOrderId=responseInquiry.get("orderNo");
            transactionId=responseInquiry.get("tradeNo");
            amount=responseInquiry.get("tradeAmount");
            currency=responseInquiry.get("tradeCurrency");
            errorCode=responseInquiry.get("queryResult");

        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        commResponseVO.setMerchantId(mid);
        commResponseVO.setMerchantOrderId(merchantOrderId);
        commResponseVO.setTransactionId(transactionId);
        commResponseVO.setAuthCode(authCode);
        commResponseVO.setTransactionType(transactionType);
        commResponseVO.setTransactionStatus(transactionStatus);
        commResponseVO.setStatus(status);
        commResponseVO.setAmount(amount);
        commResponseVO.setCurrency(currency);
        commResponseVO.setBankTransactionDate(transactionDate);
        commResponseVO.setErrorCode(errorCode);
        commResponseVO.setDescription(remark);
        commResponseVO.setRemark(remark);
        return commResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.info("  Inside   GCPPaymentGateway  ::::::::");
        transactionlogger.info("  Inside   processRebilling  ::::::::");
        validateForRebill(trackingID, requestVO);
        Functions functions = new Functions();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        PayforasiaAccount payforasiaAccount = new PayforasiaAccount();
        /*RecurringBillingVO recurringBillingVO = commRequestVO.getRecurringBillingVO();

        if (!functions.isValueNull(recurringBillingVO.getRbid()))
        {
            PZExceptionHandler.raiseConstraintViolationException(PayforasiaPaymentGateway.class.getName(), "processRebilling()", null, "common", "RBID not provided while rebilling transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "RBID not provided while rebilling transaction", new Throwable("RBID not provided while rebilling transaction"));
        }
*/
        boolean isTest = account.isTest();
        if (commRequestVO.getTransDetailsVO() != null)
        {

            if (!functions.isValueNull(commRequestVO.getTransDetailsVO().getAmount()))
            {
                PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "processRebilling()", null, "common", "Amount not provided while rebilling transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Amount not provided while rebilling transaction", new Throwable("Amount not provided while rebilling transaction"));
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "processRebilling()", null, "common", "TransactionDetails not provided while rebilling transaction", PZConstraintExceptionEnum.VO_MISSING, null, "TransactionDetails not provided while rebilling transaction", new Throwable("TransactionDetails not provided while rebilling transaction"));
        }

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();//casting
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        Hashtable hash = payforasiaAccount.getValuesFromDb(accountId);

        String amount = "";
        /*String username = commMerchantVO.getMerchantUsername();
        String password = commMerchantVO.getPassword();*/

        String cardNo = PzEncryptor.decryptPAN(genericCardDetailsVO.getCardNum());

        String EXP= PzEncryptor.decryptExpiryDate(genericCardDetailsVO.getExpMonth());
        String dateArr[]=EXP.split("/");

        //transactionlogger.debug("expiryyear----" + dateArr[0]);
        //transactionlogger.debug("expiryyear----" + dateArr[1]);
        //transactionlogger.debug("card num----" + cardNo);
        String expiryMonth = dateArr[0];

        String expiryYear = dateArr[1];

        String merNum = (String) hash.get("mid");
        String gatewayNum = (String) hash.get("gatewayno");
        String orderNo = trackingID;
        String orderCurrency = genericTransDetailsVO.getCurrency();
        amount = genericTransDetailsVO.getAmount();
        String firstName = addressDetailsVO.getFirstname();
        String lastName = addressDetailsVO.getLastname();
        //String cardNo = genericCardDetailsVO.getCardNum();
        //String expiryYear = genericCardDetailsVO.getExpYear();
        //String expiryMonth = genericCardDetailsVO.getExpMonth();
        String cvv = "";
        String email = addressDetailsVO.getEmail();
        String signKey = (String) hash.get("signkey");

        Map rebillMap = new TreeMap();
        Map rebillMaplog = new TreeMap();

        rebillMap.put(MERCHANTID, merNum.trim());
        rebillMap.put(GATEWAYNO, gatewayNum.trim());
        rebillMap.put(ORDERNO, trackingID.trim());
        rebillMap.put(ORDERCURRENCY, genericTransDetailsVO.getCurrency().trim());
        rebillMap.put(AMOUNT, genericTransDetailsVO.getAmount().trim());
        rebillMap.put(CARDNO, cardNo.trim());
        rebillMap.put(EXPIRY_YEAR, expiryYear.trim());
        rebillMap.put(EXPIRY_MONTH, expiryMonth.trim());

        rebillMap.put(ISSUING_BANK, "ISSUING BANK");
        rebillMap.put(FIRST_NAME, addressDetailsVO.getFirstname().trim());
        rebillMap.put(LAST_NAME, addressDetailsVO.getLastname().trim());
        rebillMap.put(EMAIL, addressDetailsVO.getEmail().trim());
        rebillMap.put(IPADDRESS, addressDetailsVO.getIp());
        rebillMap.put(PHONE, addressDetailsVO.getPhone());
        rebillMap.put(COUNTRY, addressDetailsVO.getCountry());
        rebillMap.put(STATE, addressDetailsVO.getState());
        rebillMap.put(CITY, addressDetailsVO.getCity());
        rebillMap.put(ADDRESS, addressDetailsVO.getStreet());
        rebillMap.put(ZIP, addressDetailsVO.getZipCode());
        rebillMap.put(CSID, "");
        rebillMap.put(RETURN_URL, "");
        rebillMap.put(SIGN_INFO, SHA256forSales(merNum, gatewayNum, orderNo, orderCurrency, amount, firstName, lastName, cardNo, expiryYear, expiryMonth, cvv, email, signKey).toString());

        String strRequest = PayforasiaUtils.joinMapValue(rebillMap, '&');
        String logRequest = PayforasiaUtils.joinMapValueForLogging(rebillMap, '&');
        transactionlogger.error("------- rebill request----for "+trackingID+ "---" + logRequest);

        String response = "";
        if (isTest)
        {
            response = PayforasiaUtils.doPostHTTPSURLConnection(TESTURL, strRequest);
        }
        else
        {
            response = PayforasiaUtils.doPostHTTPSURLConnection(LIVEURL, strRequest);
        }
        transactionlogger.error("-------rebill response------for "+trackingID+ "---" + response);

        return commResponseVO;
    }

    private void validateForSale(String trackingId, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();


        if (genericTransDetailsVO != null)
        {
            if (genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "Amount not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Amount not provided while placing transaction", new Throwable("Amount not provided while placing transaction"));
            }
            if (genericTransDetailsVO.getCurrency() == null || genericTransDetailsVO.getCurrency().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "Currency not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Currency not provided while placing transaction", new Throwable("Currency not provided while placing transaction"));
            }
            if (genericTransDetailsVO.getOrderId() == null || genericTransDetailsVO.getOrderId().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "Order ID not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Order ID not provided while placing transaction", new Throwable("Order ID not provided while placing transaction"));
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "TransactionDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING, null, "TransactionDetails  not provided while placing transaction", new Throwable("TransactionDetails  not provided while placing transaction"));
        }

        if (addressDetailsVO != null)
        {
            if (addressDetailsVO.getFirstname() == null || addressDetailsVO.getFirstname().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "First Name not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "First Name not provided while placing transaction", new Throwable("First Name not provided while placing transaction"));
            }
            if (addressDetailsVO.getLastname() == null || addressDetailsVO.getLastname().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "Last Name not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Last Name not provided while placing transaction", new Throwable("Last Name not provided while placing transaction"));
            }
            if (addressDetailsVO.getEmail() == null || addressDetailsVO.getEmail().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "Email ID not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Email ID not provided while placing transaction", new Throwable("Email ID not provided while placing transaction"));
            }
            if (addressDetailsVO.getIp() == null || addressDetailsVO.getIp().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "IP Address not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "IP Address not provided while placing transaction", new Throwable("IP Address not provided while placing transaction"));
            }

            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            String addressValidation = account.getAddressValidation();
            if (addressValidation.equalsIgnoreCase("Y"))
            {
                if (addressDetailsVO.getCountry() == null || addressDetailsVO.getCountry().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "Country not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Country not provided while placing transaction", new Throwable("Country not provided while placing transaction"));
                }
                if (addressDetailsVO.getState() == null || addressDetailsVO.getState().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "State not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "State not provided while placing transaction", new Throwable("State not provided while placing transaction"));
                }
                if (addressDetailsVO.getCity() == null || addressDetailsVO.getCity().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "City not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "City not provided while placing transaction", new Throwable("City not provided while placing transaction"));
                }
                if (addressDetailsVO.getStreet() == null || addressDetailsVO.getStreet().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "Street not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Street not provided while placing transaction", new Throwable("Street not provided while placing transaction"));
                }
                if (addressDetailsVO.getZipCode() == null || addressDetailsVO.getZipCode().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "Zip Code not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Zip Code not provided while placing transaction", new Throwable("Zip Code not provided while placing transaction"));
                }
                if (addressDetailsVO.getPhone() == null || addressDetailsVO.getPhone().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "Phone NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Phone NO not provided while placing transaction", new Throwable("Phone NO not provided while placing transaction"));
                }
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "AddressDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING, null, "AddressDetails  not provided while placing transaction", new Throwable("AddressDetails  not provided while placing transaction"));
        }

        if (genericCardDetailsVO != null)
        {
            if (genericCardDetailsVO.getCardNum() == null || genericCardDetailsVO.getCardNum().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "Card NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Card NO not provided while placing transaction", new Throwable("Card NO not provided while placing transaction"));
            }
            if (genericCardDetailsVO.getExpMonth() == null || genericCardDetailsVO.getExpMonth().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "Expiry Month not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Expiry Month not provided while placing transaction", new Throwable("Expiry Month not provided while placing transaction"));
            }
            if (genericCardDetailsVO.getExpYear() == null || genericCardDetailsVO.getExpYear().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "Expiry Year not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Expiry Year not provided while placing transaction", new Throwable("Expiry Year not provided while placing transaction"));
            }
            if (genericCardDetailsVO.getcVV() == null || genericCardDetailsVO.getcVV().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "CVV not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "CVV not provided while placing transaction", new Throwable("CVV not provided while placing transaction"));
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "CardDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING, null, "CardDetails  not provided while placing transaction", new Throwable("CardDetails  not provided while placing transaction"));
        }

        if (trackingId == null || trackingId.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForSale()", null, "common", "Tracking Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Tracking Id not provided while placing transaction", new Throwable("Tracking Id not provided while placing transaction"));
        }

    }


  /*  public static void main(String[] args) throws PZTechnicalViolationException
    {
        Map saleMap = new TreeMap();

        saleMap.put(MERCHANTID,"20000");
        saleMap.put(GATEWAYNO,"20000002");
        saleMap.put(ORDERNO,"$num");
        saleMap.put(ORDERCURRENCY,"EUR");
        saleMap.put(AMOUNT,"0.01");
        saleMap.put(CARDNO,"4111111111111129");
        saleMap.put(EXPIRY_YEAR,"2014");
        saleMap.put(EXPIRY_MONTH,"04");
        saleMap.put(ISSUING_BANK, "ISSUING BANK");
        saleMap.put(FIRST_NAME, "tan");
        saleMap.put(LAST_NAME,"");
        saleMap.put(EMAIL,"test@google.com");
        saleMap.put(IPADDRESS,"127.0.0.1");
        saleMap.put(PHONE,"1233123");
        saleMap.put(COUNTRY,"aaaaa");
        saleMap.put(STATE,"bbbbb");
        saleMap.put(CITY,"cccccc");
        saleMap.put(ADDRESS,"ddddd");
        saleMap.put(ZIP,"123423");
        saleMap.put(CSID,"");
        saleMap.put(RETURN_URL,"");
        saleMap.put(SIGN_INFO,SHA256forSales(MERCHANTID,GATEWAYNO,ORDERNO,ORDERCURRENCY,AMOUNT,FIRST_NAME,LAST_NAME,CARDNO,EXPIRY_YEAR,EXPIRY_MONTH,EMAIL,).toString());

    }

}*/


    /*public static void main(String[] args)
    {
        *//*String ExpiryDate="ETLbewAAAVQonAStABRBRVMvQ0JDL1BLQ1M1UGFkZGluZwEAABAAEPzIfZZm6et5mSRV38wntMcAAAAQsqbOPSuuHpAk44+fOdmxGgAUwHN4h+XI4ORzEv+8fjfkVwDjc3o=";
        //System.out.println("exp date----"+ExpiryDate);
        String EXP= Encryptor.decryptExpiryDate(ExpiryDate);
        String dateArr[] = EXP.split("/");
        // System.out.println("datearr---"+dateArr);
        String expiryYear = dateArr[1].substring(2,4);
        //System.out.println("expiryyear----"+expiryYear);*//*

        //String testURL = "https://check.safer2connect.com/servlet/NormalCustomerCheck";

        try
        {
            Map<String, String> inquiryMap = new HashMap<String, String>();

            inquiryMap.put(MERCHANTID, "20543");
            inquiryMap.put(GATEWAYNO, "20543001");
            inquiryMap.put(SIGN_INFO, SHA256forInquiry("20543", "20543001", "fNl6hZh6"));

            *//*inquiryMap.put(MERCHANTID, "20928");
            inquiryMap.put(GATEWAYNO, "20928001");
            inquiryMap.put(SIGN_INFO, SHA256forInquiry("20928", "20928001", "H46024jf"));*//*

            //inquiryMap.put(ORDERNO, "2015012314540871502599");
            inquiryMap.put(ORDERNO, "14759");

            String strRequest = PayforasiaUtils.joinMapValue(inquiryMap, '&');
            System.out.println("request for inquiry---"+strRequest);

            String response = PayforasiaUtils.doPostHTTPSURLConnection(INQUIRY_URL, strRequest);
            System.out.println("response for inquiry---"+response);
            Map<String, String> responseMap =  PayforasiaUtils.ReadInquiryResponse(response.toString());
            System.out.println(responseMap);

        }
        catch (PZTechnicalViolationException e)
        {
            e.printStackTrace();
        }
    }*/

    private void validateForRebill(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        if (trackingID == null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForRebill()", null, "common", "Tracking Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Tracking Id not provided while placing transaction", new Throwable("Tracking Id not provided while placing transaction"));
        }


        CommRequestVO commRequestVO = (CommRequestVO) requestVO;

        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        if (commTransactionDetailsVO == null)
        {
            PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForRebill()", null, "common", "TransactionDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING, null, "TransactionDetails  not provided while placing transaction", new Throwable("TransactionDetails  not provided while placing transaction"));
        }
        if (commTransactionDetailsVO.getAmount() == null || commTransactionDetailsVO.getAmount().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(GCPPaymentGateway.class.getName(), "validateForRebill()", null, "common", "Amount not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Amount not provided while placing transaction", new Throwable("Amount not provided while placing transaction"));
        }
    }
}