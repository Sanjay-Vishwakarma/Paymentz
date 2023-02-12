package com.directi.pg.core.paymentgateway;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.qwipi.QwipiUtils;
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
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Admin on 8/7/2017.
 */
public class QpPaymentGateway extends AbstractPaymentGateway
{

    private static TransactionLogger transactionLogger = new TransactionLogger(QpPaymentGateway.class.getName());
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.QwipiServlet");
    public static final String GATEWAY_TYPE = "QwipiN";

    //card details
    private final static String FIELD_CARD_NUMBER = "cardNum";
    private final static String FIELD_EXPIRY_MONTH = "month";
    private final static String FIELD_EXPIRY_YEAR = "year";
    private final static String FIELD_CVV = "cvv2";
    private final static String FIELD_IP = "cardHolderIp";
    private final static String FIELD_DATE_TIME = "dateTime";
    private final static String FIELD_BILLNO = "billNo";
    private final static String FIELD_AMT = "amount";
    private final static String FIELD_CURRENCYCODE = "currency";
    private final static String FIELD_LANGUAGE = "language";
    private final static String FIELD_MD5INFO = "md5Info";
    private final static String FIELD_resType= "resType";

    //customer detail
    private final static String FIELD_FIRST_NAME = "firstName";
    private final static String FIELD_LAST_NAME = "lastName";
    private final static String FIELD_EMAIL = "email";
    private final static String FIELD_ZIP = "zipCode";
    private final static String FIELD_PHONE = "phone";
    private final static String FIELD_ADDR = "address";
    private final static String FIELD_CITY = "city";
    private final static String FIELD_STATE = "state";
    private final static String FIELD_COUNTRY = "country";
    private final static String FIELD_PRODUCTS = "products";
    private final static String FIELD_REMARK = "remark";
    private final static String FIELD_BIRTHDAY = "dob";
    private final static String FIELD_SSN = "ssn";
    //private final static String FIELD_MIDDLENAME="middleName";

    //return fields
    private final static String FIELD_OPERATION = "operation";
    private final static String FIELD_PAYMENT_ORDER_NO = "orderId";
    private final static String FIELD_MERNO = "merNo";
    private final static String FIELD_AMOUNT = "amount";
    private final static String FIELD_RF_RETURN_URL = "returnUrl";
    private final static String FIELD_RF_MD5INFO = "md5Info";
    private final static String FIELD_REFUND_AMOUNT = "amountRefund";

    //Constants
    private final static String SALE = "1";
    private final static String AUTH = "2";
    private final static String CAPTURE = "3";
    private final static String VOID = "4";
    private final static String REFUND = "5";

    private final static String PAYQWIPI_REFUND_URL = "https://secure.qwipi.com/universalS2S/refund";
    private final static String PAYQWIPI_URL = "https://secure.qwipi.com/universalS2S/payment";
    private final static String PAYQWIPI_INQUIRY_URL = "https://secure.qwipi.com/universalS2S/query";

    @Override
    public String getMaxWaitDays()
    {
        return "3.5";
    }

    public QpPaymentGateway(String accountId)
    {
        this.accountId = accountId;

    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    public GenericResponseVO processSale(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.debug("Entering processSale qwipi---");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO merchantAccountVO = new CommMerchantVO();

        QwipiUtils qwipiUtils = new QwipiUtils();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        String tredtime = dateFormat.format(calendar.getTime());
        String response = "";
        String md5Hash = "";

        String currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();
        Hashtable qwipiMidDetails = qwipiUtils.getMidKeyForQwipi(accountId);
        Map<String, String> authMap = new TreeMap<String, String>();
        Map<String, String> authMapLog = new TreeMap<String, String>();

        String merNo = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String billNo = trackingID;
        String amount = genericTransDetailsVO.getAmount();

        validateForSale(trackingID, requestVO);

        authMap.put(FIELD_CURRENCYCODE, currency);
        authMap.put(FIELD_AMT,amount);
        authMap.put(FIELD_BILLNO, trackingID);
        authMap.put(FIELD_MERNO, merNo);
        authMap.put(FIELD_BIRTHDAY,addressDetailsVO.getBirthdate());
        //authMap.put(FIELD_SSN,addressDetailsVO.getSsn());
        authMap.put(FIELD_FIRST_NAME,addressDetailsVO.getFirstname());
        authMap.put(FIELD_DATE_TIME,tredtime);
        authMap.put(FIELD_LAST_NAME, addressDetailsVO.getLastname());
        authMap.put(FIELD_ADDR, addressDetailsVO.getStreet());
        authMap.put(FIELD_CITY, addressDetailsVO.getCity());
        authMap.put(FIELD_ZIP, addressDetailsVO.getZipCode());
        authMap.put(FIELD_STATE, addressDetailsVO.getState());
        authMap.put(FIELD_COUNTRY, addressDetailsVO.getCountry());
        authMap.put(FIELD_PHONE, addressDetailsVO.getPhone());
        authMap.put(FIELD_EMAIL,addressDetailsVO.getEmail());
        authMap.put(FIELD_LANGUAGE,addressDetailsVO.getLanguage());

        try
        {
            md5Hash = Functions.convertmd5(merNo.trim() + billNo.trim() + currency.trim() + amount + tredtime.trim() + qwipiMidDetails.get("midkey"));
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception---->",e);
        }
        authMap.put(FIELD_MD5INFO,md5Hash);
        authMap.put(FIELD_IP,addressDetailsVO.getIp());
        authMap.put(FIELD_CARD_NUMBER, genericCardDetailsVO.getCardNum());
        authMap.put(FIELD_EXPIRY_MONTH,genericCardDetailsVO.getExpMonth());
        authMap.put(FIELD_EXPIRY_YEAR, genericCardDetailsVO.getExpYear());
        authMap.put(FIELD_CVV, genericCardDetailsVO.getcVV());

        authMapLog.put(FIELD_CURRENCYCODE, currency);
        authMapLog.put(FIELD_AMT,amount);
        authMapLog.put(FIELD_BILLNO, trackingID);
        authMapLog.put(FIELD_MERNO, merNo);
        authMapLog.put(FIELD_BIRTHDAY,addressDetailsVO.getBirthdate());
        //authMapLog.put(FIELD_SSN,addressDetailsVO.getSsn());
        authMapLog.put(FIELD_FIRST_NAME,addressDetailsVO.getFirstname());
        authMapLog.put(FIELD_DATE_TIME,tredtime);
        authMapLog.put(FIELD_LAST_NAME, addressDetailsVO.getLastname());
        authMapLog.put(FIELD_ADDR, addressDetailsVO.getStreet());
        authMapLog.put(FIELD_CITY, addressDetailsVO.getCity());
        authMapLog.put(FIELD_ZIP, addressDetailsVO.getZipCode());
        authMapLog.put(FIELD_STATE, addressDetailsVO.getState());
        authMapLog.put(FIELD_COUNTRY, addressDetailsVO.getCountry());
        authMapLog.put(FIELD_PHONE, addressDetailsVO.getPhone());
        authMapLog.put(FIELD_EMAIL,addressDetailsVO.getEmail());
        authMapLog.put(FIELD_LANGUAGE,addressDetailsVO.getLanguage());
        authMapLog.put(FIELD_MD5INFO,md5Hash);
        authMapLog.put(FIELD_IP,addressDetailsVO.getIp());
        authMapLog.put(FIELD_CARD_NUMBER, genericCardDetailsVO.getCardNum());
        authMapLog.put(FIELD_EXPIRY_MONTH,genericCardDetailsVO.getExpMonth());
        authMapLog.put(FIELD_EXPIRY_YEAR, genericCardDetailsVO.getExpYear());
        authMapLog.put(FIELD_CVV, genericCardDetailsVO.getcVV());
        //authMap.put(FIELD_resType,"XML");

        transactionLogger.debug("qwipi map---"+authMap);
        String reqParameters = qwipiUtils.joinMapValue(authMap, '&');
        String reqParametersLog = qwipiUtils.joinMapValue(authMapLog, '&');

        transactionLogger.error("-------sale request---"+trackingID+"---"+reqParametersLog);
        response = qwipiUtils.doPostHTTPSURLConnection(PAYQWIPI_URL,reqParameters);

        transactionLogger.error("-------sale response---"+trackingID+"---"+response);

        commResponseVO = qwipiUtils.readJsonResponse(response);

        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.debug("Entering processRefund---");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();
        QwipiUtils qwipiUtils = new QwipiUtils();

        Map<String, String> authMap = new TreeMap<String, String>();
        Hashtable qwipiMidDetails = qwipiUtils.getMidKeyForQwipi(accountId);

        String qwipi_paymentordernumber = commTransactionDetailsVO.getPreviousTransactionId();
        String amount = commTransactionDetailsVO.getAmount();
        String refundAmount = commTransactionDetailsVO.getAmount();

        authMap.put(FIELD_PAYMENT_ORDER_NO,qwipi_paymentordernumber);
        authMap.put(FIELD_BILLNO, trackingID);
        authMap.put(FIELD_AMOUNT,amount);
        authMap.put(FIELD_REFUND_AMOUNT,refundAmount);
        String md5Info = "";
        try
        {
            md5Info = Functions.convertmd5(qwipi_paymentordernumber + trackingID + amount + refundAmount + qwipiMidDetails.get("midkey"));
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception---->",e);
        }
        authMap.put(FIELD_RF_MD5INFO, md5Info);
        authMap.put(FIELD_OPERATION, "02");
        authMap.put(FIELD_RF_RETURN_URL, RB.getString("REFUND_RETURN_URL"));

        transactionLogger.debug("refund request---"+authMap);

        String response =null;

        String reqParameters = qwipiUtils.joinMapValue(authMap, '&');
        transactionLogger.error("------refund request------" + reqParameters);
        response = qwipiUtils.doPostHTTPSURLConnection(PAYQWIPI_REFUND_URL,reqParameters);
        transactionLogger.error("-------- refund response-------"+response);

        commResponseVO = qwipiUtils.readJsonResponse(response);

        return commResponseVO;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        transactionLogger.debug("Entering processInquiry---");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();
        QwipiUtils qwipiUtils = new QwipiUtils();

        Map<String, String> authMap = new TreeMap<String, String>();
        Hashtable qwipiMidDetails = qwipiUtils.getMidKeyForQwipi(accountId);
        String merNo = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String billNo = commTransactionDetailsVO.getOrderId();
        String md5Info = "";
        authMap.put(FIELD_BILLNO, billNo);
        authMap.put(FIELD_MERNO, merNo);
        try
        {
            md5Info = Functions.convertmd5(merNo + billNo + qwipiMidDetails.get("midkey"));
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception--->",e);
        }
        authMap.put(FIELD_MD5INFO,md5Info);

        String reqParameters = qwipiUtils.joinMapValue(authMap, '&');
        String response = qwipiUtils.doPostHTTPSURLConnection(PAYQWIPI_INQUIRY_URL,reqParameters);

        transactionLogger.debug("qwipi inquiry request---"+reqParameters);
        transactionLogger.debug("qwipi inquiry response---"+response);

        commResponseVO = qwipiUtils.readInquiryJsonResponse(response);
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
                PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "Amount not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Amount not provided while placing transaction", new Throwable("Amount not provided while placing transaction"));
            }
            if (genericTransDetailsVO.getCurrency() == null || genericTransDetailsVO.getCurrency().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "Currency not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Currency not provided while placing transaction", new Throwable("Currency not provided while placing transaction"));
            }
            if (genericTransDetailsVO.getOrderId() == null || genericTransDetailsVO.getOrderId().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "Order ID not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Order ID not provided while placing transaction", new Throwable("Order ID not provided while placing transaction"));
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "TransactionDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING, null, "TransactionDetails not provided while placing transaction", new Throwable("TransactionDetails  not provided while placing transaction"));
        }

        if (addressDetailsVO != null)
        {
            if (addressDetailsVO.getFirstname() == null || addressDetailsVO.getFirstname().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "First Name not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "First Name not provided while placing transaction", new Throwable("First Name not provided while placing transaction"));
            }
            if (addressDetailsVO.getLastname() == null || addressDetailsVO.getLastname().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "Last Name not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Last Name not provided while placing transaction", new Throwable("Last Name not provided while placing transaction"));
            }
            if (addressDetailsVO.getEmail() == null || addressDetailsVO.getEmail().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "Email ID not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Email ID not provided while placing transaction", new Throwable("Email ID not provided while placing transaction"));
            }
            if (addressDetailsVO.getIp() == null || addressDetailsVO.getIp().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "IP Address not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "IP Address not provided while placing transaction", new Throwable("IP Address not provided while placing transaction"));
            }
            if (addressDetailsVO.getBirthdate() == null || addressDetailsVO.getBirthdate().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "Birth Date not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Birth date not provided while placing transaction", new Throwable("Birth date not provided while placing transaction"));
            }
            if (addressDetailsVO.getLanguage() == null || addressDetailsVO.getLanguage().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "Language not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Language not provided while placing transaction", new Throwable("Language not provided while placing transaction"));
            }

            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            String addressValidation = account.getAddressValidation();
            if (addressValidation.equalsIgnoreCase("Y"))
            {
                if (addressDetailsVO.getCountry() == null || addressDetailsVO.getCountry().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "Country not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Country not provided while placing transaction", new Throwable("Country not provided while placing transaction"));
                }
                if (addressDetailsVO.getState() == null || addressDetailsVO.getState().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "State not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "State not provided while placing transaction", new Throwable("State not provided while placing transaction"));
                }
                if (addressDetailsVO.getCity() == null || addressDetailsVO.getCity().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "City not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "City not provided while placing transaction", new Throwable("City not provided while placing transaction"));
                }
                if (addressDetailsVO.getStreet() == null || addressDetailsVO.getStreet().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "Street not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Street not provided while placing transaction", new Throwable("Street not provided while placing transaction"));
                }
                if (addressDetailsVO.getZipCode() == null || addressDetailsVO.getZipCode().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "Zip Code not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Zip Code not provided while placing transaction", new Throwable("Zip Code not provided while placing transaction"));
                }
                if (addressDetailsVO.getPhone() == null || addressDetailsVO.getPhone().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "Phone NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Phone NO not provided while placing transaction", new Throwable("Phone NO not provided while placing transaction"));
                }
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "AddressDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING, null, "AddressDetails  not provided while placing transaction", new Throwable("AddressDetails  not provided while placing transaction"));
        }

        if (genericCardDetailsVO != null)
        {
            if (genericCardDetailsVO.getCardNum() == null || genericCardDetailsVO.getCardNum().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "Card NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Card NO not provided while placing transaction", new Throwable("Card NO not provided while placing transaction"));
            }
            if (genericCardDetailsVO.getExpMonth() == null || genericCardDetailsVO.getExpMonth().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "Expiry Month not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Expiry Month not provided while placing transaction", new Throwable("Expiry Month not provided while placing transaction"));
            }
            if (genericCardDetailsVO.getExpYear() == null || genericCardDetailsVO.getExpYear().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "Expiry Year not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Expiry Year not provided while placing transaction", new Throwable("Expiry Year not provided while placing transaction"));
            }
            if (genericCardDetailsVO.getcVV() == null || genericCardDetailsVO.getcVV().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "CVV not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "CVV not provided while placing transaction", new Throwable("CVV not provided while placing transaction"));
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "CardDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING, null, "CardDetails  not provided while placing transaction", new Throwable("CardDetails  not provided while placing transaction"));
        }

        if (trackingId == null || trackingId.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(QpPaymentGateway.class.getName(), "validateForSale()", null, "common", "Tracking Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Tracking Id not provided while placing transaction", new Throwable("Tracking Id not provided while placing transaction"));
        }

    }

}
