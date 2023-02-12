package com.payment.pbs.core;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
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
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.payforasia.core.PayforasiaAccount;
import com.payment.payforasia.core.PayforasiaUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Admin on 28/3/2016.
 */
public class PbsPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "pbs";
    public final static String ACCOUNTID = "accountId";
    public final static String MERCHANTID = "merNo";
    public final static String GATEWAYNO = "gatewayNo";
    public final static String ORDERNO = "orderNo";
    public final static String ORDERCURRENCY = "orderCurrency";
    public final static String AMOUNT = "orderAmount";
    public final static String PAYMENT_METHOD = "paymentMethod";
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
    private final static String TESTURL = "https://co.onlineb2cmall.com/TestTPInterface";
    private final static String LIVE_URL = "https://co.onlineb2cmall.com/TPInterface";
    private final static String threeDURL = "https://co.onlineb2cmall.com/DirectInterface";
    private final static String REFUND_URL = "https://check.onlinesslmall.com/servlet/ApplyRefund";
    private final static String INQUIRY_URL = "https://check.onlinesslmall.com/servlet/TestCustomerCheck";
    private final static String LIVE_INQUIRY_URL = "https://check.onlinesslmall.com/servlet/NormalCustomerCheck";
    //private static Logger transactionLogger = new Logger(PbsPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PbsPaymentGateway.class.getName());
    public PbsPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static String SHA256forSales(String merchantNo, String gatewayNo, String orderNo, String orderCurrency, String orderAmount, String firstName, String lastName, String cardNo, String expiryYear, String expiryMonth, String cvv, String email, String signKey) throws PZTechnicalViolationException
    {
        String sha = merchantNo.trim() + gatewayNo.trim() + orderNo.trim() + orderCurrency.trim() + orderAmount.trim() + firstName.trim() + lastName.trim() + cardNo.trim() + expiryYear.trim() + expiryMonth.trim() + cvv.trim() + email.trim() + signKey;
        sha.trim();
        //transactionLogger.error("sha256 combination---" + sha);

        StringBuffer hexString = new StringBuffer();
        try
        {
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
            PZExceptionHandler.raiseTechnicalViolationException(PbsPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PbsPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null, e.getMessage(), e.getCause());
        }

        return hexString.toString().trim();
    }

    public static String SHA256forRefund(String merchantNo, String gatewayNo, String tradeNo, String refundType, String signKey) throws PZTechnicalViolationException
    {
        String sha = merchantNo.trim() + gatewayNo.trim() + tradeNo.trim() + refundType.trim() + signKey;
        sha.trim();

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
            PZExceptionHandler.raiseTechnicalViolationException(PbsPaymentGateway.class.getName(), "SHA256forRefund()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PbsPaymentGateway.class.getName(), "SHA256forRefund()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null, e.getMessage(), e.getCause());
        }

        return hexString.toString().toLowerCase().trim();
    }

    public static String SHA256forInquiry(String merchantNo, String gatewayNo, String signKey) throws PZTechnicalViolationException
    {
        String sha = merchantNo.trim() + gatewayNo.trim() + signKey;
        sha.trim();

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
            PZExceptionHandler.raiseTechnicalViolationException(PbsPaymentGateway.class.getName(), "SHA256forInquiry()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PbsPaymentGateway.class.getName(), "SHA256forInquiry()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null, e.getMessage(), e.getCause());
        }

        return hexString.toString().toLowerCase().trim();
    }

    public String getMaxWaitDays()
    {
        return null;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("PbsPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        Functions functions=new Functions();
        transactionLogger.debug("Entering processSale of PbsPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        //CommResponseVO commResponseVO = new CommResponseVO();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();


        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        String accountid = GatewayAccountService.getGatewayAccount(accountId).toString();

        PayforasiaAccount payforasiaAccount = new PayforasiaAccount();
        Hashtable dataHash = payforasiaAccount.getValuesFromDb(accountId);

        validateForSale(trackingID,requestVO);

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
        String isThreedSec = (String) dataHash.get("isThreeDSec");
        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
        if (functions.isValueNull(genericTransDetailsVO.getCurrency()))
        {
            currency=genericTransDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=addressDetailsVO.getTmpl_currency();
        }

        Map saleMap = new TreeMap();

        saleMap.put(MERCHANTID,merNo.trim());
        saleMap.put(GATEWAYNO,gatewayNo.trim());
        saleMap.put(ORDERNO,trackingID.trim());
        saleMap.put(ORDERCURRENCY,genericTransDetailsVO.getCurrency().trim());
        saleMap.put(AMOUNT,genericTransDetailsVO.getAmount().trim());
        saleMap.put(CARDNO,genericCardDetailsVO.getCardNum().trim());
        saleMap.put(EXPIRY_YEAR,genericCardDetailsVO.getExpYear().trim());
        saleMap.put(EXPIRY_MONTH,genericCardDetailsVO.getExpMonth().trim());
        saleMap.put(CVV,genericCardDetailsVO.getcVV().trim());
        saleMap.put(ISSUING_BANK, "ISSUING BANK");
        saleMap.put(CSID, "");

        saleMap.put(PAYMENT_METHOD,"Credit Card");

        saleMap.put(FIRST_NAME, addressDetailsVO.getFirstname().trim());
        saleMap.put(LAST_NAME,addressDetailsVO.getLastname().trim());
        saleMap.put(EMAIL,addressDetailsVO.getEmail().trim());

        saleMap.put(PHONE,addressDetailsVO.getPhone());
        saleMap.put(COUNTRY,addressDetailsVO.getCountry());
        saleMap.put(STATE,addressDetailsVO.getState());
        saleMap.put(CITY,addressDetailsVO.getCity());
        saleMap.put(ADDRESS,addressDetailsVO.getStreet());
        saleMap.put(ZIP,addressDetailsVO.getZipCode());
        saleMap.put(IPADDRESS,addressDetailsVO.getCardHolderIpAddress());
        if(isTest){
            saleMap.put(RETURN_URL, "https://staging.pz.com/transaction/PBSFrontendServlet");
        }
        else {
            saleMap.put(RETURN_URL, "https://secure.theflyingmerchant.com/transaction/PBSFrontendServlet");
        }

        saleMap.put(SIGN_INFO,SHA256forSales(merNo,gatewayNo,orderNo,orderCurrency,amount,firstName,lastName,cardNo,expiryYear,expiryMonth,cvv,email,signKey).toString());

        String strRequest = PayforasiaUtils.joinMapValue(saleMap, '&');

        String logRequest=PayforasiaUtils.joinMapValueForLogging(saleMap,'&');
        transactionLogger.error("------ sale request----" + logRequest);

        //Date date104=new Date();
        //transactionLogger.debug("PbsPaymentGateway gatewayCall start time 104########" + date104.getTime());

        String response = " ";
        if("N".equals(isThreedSec))
        {
            if(isTest)
            {
                response = PayforasiaUtils.doPostHTTPSURLConnection(TESTURL, strRequest);
                transactionLogger.error("--------sale 3d response----" + TESTURL);
            }
            else
            {
                response = PayforasiaUtils.doPostHTTPSURLConnection(LIVE_URL, strRequest);
                transactionLogger.error("--------sale live response----" + LIVE_URL);
            }


            transactionLogger.error("--------sale response----" + response);
            //transactionLogger.debug("PbsPaymentGateway gatewayCall end time 104########" + new Date().getTime());
            //transactionLogger.debug("PbsPaymentGateway gatewayCall diff time 104########" + (new Date().getTime() - date104.getTime()));

            Map responseParameter = PayforasiaUtils.ReadSalesResponse(response);

            if (responseParameter != null && !responseParameter.equals(""))
            {
                String status = "";
                String orderStatus = String.valueOf(responseParameter.get("orderStatus"));
                String bankDescriptor = String.valueOf(responseParameter.get("billAddress"));
                if (orderStatus != null && !orderStatus.equals("") && !orderStatus.equals("null"))
                {
                    if (bankDescriptor != null)
                    {
                        descriptor = bankDescriptor;
                    }
                    if (orderStatus.equals("1"))
                    {
                        status = "success";
                        transactionLogger.error("in side order status = success" + orderStatus);
                        commResponseVO.setDescriptor(descriptor);
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

                }
                commResponseVO.setTransactionId((String) responseParameter.get("tradeNo"));
                commResponseVO.setAmount((String) responseParameter.get("orderAmount"));
                commResponseVO.setTransactionStatus(status);
                commResponseVO.setErrorCode(orderStatus);
                commResponseVO.setDescription((String) responseParameter.get("orderInfo"));
                commResponseVO.setStatus(status);
                commResponseVO.setTransactionType("sale");

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }
        }
        else if("Y".equalsIgnoreCase(isThreedSec))
        {
            commResponseVO.setUrlFor3DRedirect(threeDURL);
            commResponseVO.setStatus("pending3DConfirmation");
            commResponseVO.setPaReq(merNo);//set merchantno
            commResponseVO.setMd(gatewayNo);//set gateway
            commResponseVO.setRemark(signKey);
        }
        commResponseVO.setCurrency(currency);
        commResponseVO.setTmpl_Amount(tmpl_amount);
        commResponseVO.setTmpl_Currency(tmpl_currency);
        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        Functions functions=new Functions();
        System.setProperty("https.protocols", "TLSv1");
        CommRequestVO reqVO= (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO=reqVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();

        transactionLogger.debug("Enter in processRefund of PbsPaymentGateway");

        String accountid = GatewayAccountService.getGatewayAccount(accountId).toString();
        PayforasiaAccount payforasiaAccount = new PayforasiaAccount();
        Hashtable dataHash = payforasiaAccount.getValuesFromDb(accountId);

        String merNo = (String) dataHash.get("mid");
        String currency="";
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency=commTransactionDetailsVO.getCurrency();
        }
        //transactionLogger.debug("mid----" + dataHash.get("mid"));

        String gatewayNo = (String) dataHash.get("gatewayno");
        String tradeNo = commTransactionDetailsVO.getPreviousTransactionId();
        String refundType = "1";
        String signKey = (String) dataHash.get("signkey");
        if(!commTransactionDetailsVO.getPreviousTransactionAmount().equals(commTransactionDetailsVO.getAmount())){
            refundType = "2";
        }
        Map refundMap = new TreeMap();

        refundMap.put(MERCHANTID,merNo.trim());
        refundMap.put(GATEWAYNO,gatewayNo.trim());
        refundMap.put(SIGN_INFO,SHA256forRefund(merNo,gatewayNo,tradeNo,refundType,signKey));
        refundMap.put(TRADE_NO,tradeNo.trim());
        refundMap.put(REFUND_TYPE,refundType.trim());
        refundMap.put(TRADE_AMOUNT,commTransactionDetailsVO.getPreviousTransactionAmount());
        refundMap.put(REFUND_AMOUNT,commTransactionDetailsVO.getAmount());
        refundMap.put(CURRENCY,commTransactionDetailsVO.getCurrency());
        refundMap.put(REFUND_REASON,commTransactionDetailsVO.getOrderDesc());
        refundMap.put(REMARK,"Refund Remark");

        String refundParameters = PayforasiaUtils.joinMapValue(refundMap, '&');

        transactionLogger.error("===refund request===" + refundParameters);
        String response = PayforasiaUtils.doPostHTTPSURLConnection(REFUND_URL,refundParameters);
        transactionLogger.error("===refund response===" + response);

        Map<String,String> responseRefund = PayforasiaUtils.ReadRefundResponse(response);

        if (responseRefund != null && !responseRefund.equals(""))
        {
            String status = "";
            if (responseRefund != null && !responseRefund.equals(""))
            {
                String statusCode = String.valueOf(responseRefund.get("code"));

                if(statusCode != null && !statusCode.equals(""))
                {
                    if(statusCode.equals("00"))
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
            commResponseVO.setStatus(status);
            commResponseVO.setTransactionType("refund");
            commResponseVO.setCurrency(currency);

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        }
        return commResponseVO;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        System.setProperty("https.protocols", "TLSv1");
        CommRequestVO reqVO= (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO=reqVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();

        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        PayforasiaAccount payforasiaAccount = new PayforasiaAccount();
        Hashtable dataHash = payforasiaAccount.getValuesFromDb(accountId);

        String merNo = (String) dataHash.get("mid");
        String gatewayNo = (String) dataHash.get("gatewayno");
        String signKey = (String) dataHash.get("signkey");

        Map inquiryMap = new TreeMap();

        inquiryMap.put(MERCHANTID,merNo.trim());
        inquiryMap.put(GATEWAYNO,gatewayNo.trim());
        inquiryMap.put(SIGN_INFO,SHA256forInquiry(merNo,gatewayNo,signKey));
        inquiryMap.put(ORDERNO, commTransactionDetailsVO.getOrderId());

        String inquiryParameters = PayforasiaUtils.joinMapValue(inquiryMap, '&');
        transactionLogger.error("-----inquiry request----"+inquiryParameters);

        String response = " ";
        if (isTest)
        {
            response = PayforasiaUtils.doPostHTTPSURLConnection(INQUIRY_URL,inquiryParameters);
        }
        else
        {
            response = PayforasiaUtils.doPostHTTPSURLConnection(LIVE_INQUIRY_URL, inquiryParameters);
        }

        transactionLogger.error("-----inquiry response----"+response);
        Map<String, String> responseInquiry = PayforasiaUtils.ReadInquiryResponse(response);

        String mid = "";
        String merchantOrderId = "";
        String transactionId = "";
        String authCode = "";
        String transactionStatus = "";
        String transactionType = "";
        String amount = "";
        String currency = "";
        String remark = "";
        String transactionDate = "";
        String status = "";
        String errorCode = "";

        if (responseInquiry != null && !responseInquiry.isEmpty())
        {
            String statusCode = String.valueOf(responseInquiry.get("queryResult"));
            if (statusCode != null && !statusCode.equals(""))
            {
                status = "success";
                if (statusCode.equals("0")) //Transaction Declined.
                {
                    transactionStatus = "Failed";
                    remark = "Transaction Failed";
                }
                else if (statusCode.equals("1")) //Transaction Approved.
                {
                    transactionStatus = "Successful";
                    remark = "Transaction Successful";
                }
                else if (statusCode.equals("2"))  //Transaction not found.
                {
                    transactionStatus = "N/A";
                    remark = "Transaction Not Found";
                }
            }
            else
            {
                status = "fail";
                transactionStatus = "N/A";
                remark = "Internal error while processing your request.";
            }
            mid = responseInquiry.get("merNo");
            merchantOrderId = responseInquiry.get("orderNo");
            transactionId = responseInquiry.get("tradeNo");
            amount = responseInquiry.get("tradeAmount");
            currency = responseInquiry.get("tradeCurrency");
            errorCode = responseInquiry.get("queryResult");
            transactionDate = responseInquiry.get("tradeDate");
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

    private void validateForSale(String trackingId, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        GenericTransDetailsVO genericTransDetailsVO=commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO=commRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO=commRequestVO.getCardDetailsVO();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

        if(genericTransDetailsVO != null)
        {
            if(genericTransDetailsVO.getAmount()==null || genericTransDetailsVO.getAmount().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(),"validateForSale()",null,"common",errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while placing transaction",new Throwable("Amount not provided while placing transaction"));
            }
            if(genericTransDetailsVO.getCurrency()==null || genericTransDetailsVO.getCurrency().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CURRENCY);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(),"validateForSale()",null,"common",errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Currency not provided while placing transaction",new Throwable("Currency not provided while placing transaction"));
            }
            if(genericTransDetailsVO.getOrderId()==null || genericTransDetailsVO.getOrderId().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ORDER_DESCRIPTION);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(),"validateForSale()",null,"common",errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Order ID not provided while placing transaction",new Throwable("Order ID not provided while placing transaction"));
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(),"validateForSale()",null,"common",errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while placing transaction",new Throwable("TransactionDetails  not provided while placing transaction"));
        }


        if(addressDetailsVO != null)
        {
            if(addressDetailsVO.getFirstname()==null || addressDetailsVO.getFirstname().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(),"validateForSale()",null,"common",errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"First Name not provided while placing transaction",new Throwable("First Name not provided while placing transaction"));
            }
            if(addressDetailsVO.getLastname()==null || addressDetailsVO.getLastname().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(),"validateForSale()",null,"common",errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Last Name not provided while placing transaction",new Throwable("Last Name not provided while placing transaction"));
            }
            if(addressDetailsVO.getEmail()==null || addressDetailsVO.getEmail().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(),"validateForSale()",null,"common",errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Email ID not provided while placing transaction",new Throwable("Email ID not provided while placing transaction"));
            }
            /*if(addressDetailsVO.getIp()==null || addressDetailsVO.getIp().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_IPADDRESS);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(),"validateForSale()",null,"common",errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"IP Address not provided while placing transaction",new Throwable("IP Address not provided while placing transaction"));
            }*/

            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            String addressValidation = account.getAddressValidation();
            if (addressValidation.equalsIgnoreCase("Y"))
            {
                if (addressDetailsVO.getCountry() == null || addressDetailsVO.getCountry().equals(""))
                {
                    errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
                    if (errorCodeListVO.getListOfError().isEmpty())
                        errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(), "validateForSale()", null, "common", errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Country not provided while placing transaction", new Throwable("Country not provided while placing transaction"));
                }
                if (addressDetailsVO.getState() == null || addressDetailsVO.getState().equals(""))
                {
                    errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
                    if (errorCodeListVO.getListOfError().isEmpty())
                        errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(), "validateForSale()", null, "common", errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "State not provided while placing transaction", new Throwable("State not provided while placing transaction"));
                }
                if (addressDetailsVO.getCity() == null || addressDetailsVO.getCity().equals(""))
                {
                    errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
                    if (errorCodeListVO.getListOfError().isEmpty())
                        errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(), "validateForSale()", null, "common", errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "City not provided while placing transaction", new Throwable("City not provided while placing transaction"));
                }
                if (addressDetailsVO.getStreet() == null || addressDetailsVO.getStreet().equals(""))
                {
                    errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
                    if (errorCodeListVO.getListOfError().isEmpty())
                        errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(), "validateForSale()", null, "common", errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Street not provided while placing transaction", new Throwable("Street not provided while placing transaction"));
                }
                if (addressDetailsVO.getZipCode() == null || addressDetailsVO.getZipCode().equals(""))
                {
                    errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
                    if (errorCodeListVO.getListOfError().isEmpty())
                        errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(), "validateForSale()", null, "common",errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Zip Code not provided while placing transaction", new Throwable("Zip Code not provided while placing transaction"));
                }
                if(addressDetailsVO.getPhone()==null || addressDetailsVO.getPhone().equals(""))
                {
                    errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
                    if (errorCodeListVO.getListOfError().isEmpty())
                        errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(),"validateForSale()",null,"common",errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Phone NO not provided while placing transaction",new Throwable("Phone NO not provided while placing transaction"));
                }
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(),"validateForSale()",null,"common",errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.VO_MISSING,null,"AddressDetails  not provided while placing transaction",new Throwable("AddressDetails  not provided while placing transaction"));
        }

        if(genericCardDetailsVO != null)
        {
            if(genericCardDetailsVO.getCardNum()==null || genericCardDetailsVO.getCardNum().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAN);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(),"validateForSale()",null,"common",errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Card NO not provided while placing transaction",new Throwable("Card NO not provided while placing transaction"));
            }
            if(genericCardDetailsVO.getExpMonth()==null || genericCardDetailsVO.getExpMonth().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_MONTH);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(),"validateForSale()",null,"common",errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Month not provided while placing transaction",new Throwable("Expiry Month not provided while placing transaction"));
            }
            if(genericCardDetailsVO.getExpYear()==null || genericCardDetailsVO.getExpYear().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_YEAR);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(),"validateForSale()",null,"common",errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Expiry Year not provided while placing transaction",new Throwable("Expiry Year not provided while placing transaction"));
            }
            if(genericCardDetailsVO.getcVV()==null || genericCardDetailsVO.getcVV().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CVV);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(),"validateForSale()",null,"common",errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"CVV not provided while placing transaction",new Throwable("CVV not provided while placing transaction"));
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(),"validateForSale()",null,"common",errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.VO_MISSING,null,"CardDetails  not provided while placing transaction",new Throwable("CardDetails  not provided while placing transaction"));
        }

        if(trackingId==null || trackingId.equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TRACKINGID);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PbsPaymentGateway.class.getName(),"validateForSale()",null,"common",errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while placing transaction",new Throwable("Tracking Id not provided while placing transaction"));
        }
    }

}
