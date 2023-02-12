package com.payment.bankone;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Admin on 8/19/2017.
 */
public class BankonePaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "bankone";

    public static final String SALE = "1";
    public static final String PRE_AUTH = "4";
    public static final String CAPTURE = "5";
    public static final String VOID = "9";
    public static final String REFUND = "2";

    public static final String TRAN_URL = "http://test.upaywise.com/paymentgateway/payments/performXmlTransaction";
    public static final String LIVE_TRAN_URL = "https://secure.soft-connect.biz/DirectTransaction.aspx";

    private static Logger log = new Logger(BankonePaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(BankonePaymentGateway.class.getName());
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public BankonePaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.debug("inside processAuthentication BankonePaymentGateway---");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = null;

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO merchantAccountVO = new CommMerchantVO();

        String terminalId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String password = GatewayAccountService.getGatewayAccount(accountId).getPassword();
        String currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();
        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();

        BankoneUtils bankoneUtils = new BankoneUtils();

        validateForSale(trackingID, requestVO);

        String xmlRequest = "<?xml version='1.0' encoding='ISO-8859-1' ?>\n" +
                "<request>\n" +
                "<terminalid>"+terminalId+"</terminalid>\n" +
                "<password>"+password+"</password>\n" +
                "<action>"+PRE_AUTH+"</action>\n" +
                "<card>"+genericCardDetailsVO.getCardNum()+"</card>\n" +
                "<cvv2>"+genericCardDetailsVO.getcVV()+"</cvv2>\n" +
                "<expYear>"+genericCardDetailsVO.getExpYear()+"</expYear>\n" +
                "<expMonth>"+genericCardDetailsVO.getExpMonth()+"</expMonth>\n" +
                "<member>"+addressDetailsVO.getFirstname()+" "+addressDetailsVO.getLastname()+"</member>\n" +
                "<currencyCode>"+currency+"</currencyCode>\n" +
                "<address>"+addressDetailsVO.getStreet()+"</address>\n" +
                "<city>"+addressDetailsVO.getCity()+"</city>\n" +
                "<statecode>"+addressDetailsVO.getState()+"</statecode>\n" +
                "<zip>"+addressDetailsVO.getZipCode()+"</zip>\n" +
                "<CountryCode>"+addressDetailsVO.getCountry()+"</CountryCode>\n" +
                "<email>"+addressDetailsVO.getEmail()+"</email>\n" +
                "<amount>"+genericTransDetailsVO.getAmount()+"</amount>\n" +
                "<customerIp>"+addressDetailsVO.getCardHolderIpAddress()+"</customerIp>\n" +
                "<merchantIp>"+addressDetailsVO.getIp()+"</merchantIp>\n" +
                "<trackid>"+trackingID+"</trackid>\n" +
                "<udf1></udf1>\n" +
                "<udf2></udf2>\n" +
                "<udf3></udf3>\n" +
                "<udf4></udf4>\n" +
                "</request>";

        transactionLogger.error("sale request BankOne---"+xmlRequest);
        String response = "";

        if(isTest)
        {
            response = bankoneUtils.doPostHTTPSURLConnection(TRAN_URL, xmlRequest);
        }
        else
        {
            response = bankoneUtils.doPostHTTPSURLConnection(LIVE_TRAN_URL, xmlRequest);
        }
        transactionLogger.error("sale response BankOne---"+response);

        if(response!=null)
        {
            commResponseVO = bankoneUtils.read3DResponse(response);
            transactionLogger.error("url---"+commResponseVO.getUrlFor3DRedirect());
            transactionLogger.error("pay id---"+commResponseVO.getTransactionId());
            commResponseVO.setStatus("pending3DConfirmation");
        }

        return commResponseVO;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.debug("inside processSale BankonePaymentGateway---");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = null;

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO merchantAccountVO = new CommMerchantVO();

        String terminalId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String password = GatewayAccountService.getGatewayAccount(accountId).getPassword();
        String currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();
        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();

        BankoneUtils bankoneUtils = new BankoneUtils();

        validateForSale(trackingID, requestVO);

        String xmlRequest = "<?xml version='1.0' encoding='ISO-8859-1' ?>\n" +
                "<request>\n" +
                "<terminalid>"+terminalId+"</terminalid>\n" +
                "<password>"+password+"</password>\n" +
                "<action>"+SALE+"</action>\n" +
                "<card>"+genericCardDetailsVO.getCardNum()+"</card>\n" +
                "<cvv2>"+genericCardDetailsVO.getcVV()+"</cvv2>\n" +
                "<expYear>"+genericCardDetailsVO.getExpYear()+"</expYear>\n" +
                "<expMonth>"+genericCardDetailsVO.getExpMonth()+"</expMonth>\n" +
                "<member>"+addressDetailsVO.getFirstname()+" "+addressDetailsVO.getLastname()+"</member>\n" +
                "<currencyCode>"+currency+"</currencyCode>\n" +
                "<address>"+addressDetailsVO.getStreet()+"</address>\n" +
                "<city>"+addressDetailsVO.getCity()+"</city>\n" +
                "<statecode>"+addressDetailsVO.getState()+"</statecode>\n" +
                "<zip>"+addressDetailsVO.getZipCode()+"</zip>\n" +
                "<CountryCode>"+addressDetailsVO.getCountry()+"</CountryCode>\n" +
                "<email>"+addressDetailsVO.getEmail()+"</email>\n" +
                "<amount>"+genericTransDetailsVO.getAmount()+"</amount>\n" +
                "<customerIp>"+addressDetailsVO.getCardHolderIpAddress()+"</customerIp>\n" +
                "<merchantIp>"+addressDetailsVO.getIp()+"</merchantIp>\n" +
                "<trackid>"+trackingID+"</trackid>\n" +
                "<udf1></udf1>\n" +
                "<udf2></udf2>\n" +
                "<udf3></udf3>\n" +
                "<udf4></udf4>\n" +
                "</request>";

        transactionLogger.error("sale request---"+xmlRequest);

        String response = "";

        if(isTest)
        {
            response = bankoneUtils.doPostHTTPSURLConnection(TRAN_URL, xmlRequest);
        }
        else
        {
            response = bankoneUtils.doPostHTTPSURLConnection(LIVE_TRAN_URL, xmlRequest);
        }

        transactionLogger.debug("sale response---"+response);

        if(response!=null)
        {
            commResponseVO = bankoneUtils.read3DResponse(response);
            transactionLogger.debug("url---"+commResponseVO.getUrlFor3DRedirect());
            transactionLogger.debug("pay id---"+commResponseVO.getTransactionId());
        }

        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Enter in processRefund of BankOne---");
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        String terminalId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String password = GatewayAccountService.getGatewayAccount(accountId).getPassword();
        String currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();

        BankoneUtils bankoneUtils = new BankoneUtils();

        String xmlRequest = "<?xml version='1.0' encoding='ISO-8859-1' ?>\n" +
                "<request>\n" +
                "<terminalid>"+terminalId+"</terminalid>\n" +
                "<password>"+password+"</password>\n" +
                "<action>"+REFUND+"</action>\n" +
                "<currencyCode>"+currency+"</currencyCode>\n" +
                "<amount>"+commTransactionDetailsVO.getAmount()+"</amount>\n" +
                "<trackid>"+trackingID+"</trackid>\n" +
                "<transid>"+commTransactionDetailsVO.getPreviousTransactionId()+"</transid>\n" +
                "<merchantIp></merchantIp>\n" +
                "<customerIp></customerIp>\n" +
                "<udf1></udf1>\n" +
                "<udf2></udf2>\n" +
                "<udf3></udf3>\n" +
                "<udf4></udf4>\n" +
                "</request>";

        transactionLogger.debug("Refund request---"+xmlRequest);

        String response = bankoneUtils.doPostHTTPSURLConnection(TRAN_URL,xmlRequest);

        transactionLogger.debug("Refund response---"+response);

        if(response!=null)
        {
            commResponseVO = bankoneUtils.readResponse(response);
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(new Date())));
            commResponseVO.setTransactionType("refund");
        }

        return commResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Enter in processCapture of BankOne---");
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        String terminalId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String password = GatewayAccountService.getGatewayAccount(accountId).getPassword();
        String currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();

        BankoneUtils bankoneUtils = new BankoneUtils();

        String xmlRequest = "<?xml version='1.0' encoding='ISO-8859-1' ?>\n" +
                "<request>\n" +
                "<terminalid>"+terminalId+"</terminalid>\n" +
                "<password>"+password+"</password>\n" +
                "<action>"+CAPTURE+"</action>\n" +
                "<currencyCode>"+currency+"</currencyCode>\n" +
                "<amount>"+commTransactionDetailsVO.getAmount()+"</amount>\n" +
                "<trackid>"+trackingID+"</trackid>\n" +
                "<transid>"+commTransactionDetailsVO.getPreviousTransactionId()+"</transid>\n" +
                "<merchantIp></merchantIp>\n" +
                "<customerIp></customerIp>\n" +
                "<udf1></udf1>\n" +
                "<udf2></udf2>\n" +
                "<udf3></udf3>\n" +
                "<udf4></udf4>\n" +
                "</request>";

        transactionLogger.debug("Capture request---"+xmlRequest);

        String response = bankoneUtils.doPostHTTPSURLConnection(TRAN_URL,xmlRequest);

        transactionLogger.debug("Capture response---"+response);

        if(response!=null && !response.equals(""))
        {
            transactionLogger.debug("inside read gateway---");
            commResponseVO = bankoneUtils.readResponse(response);
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(new Date())));
            commResponseVO.setTransactionType("capture");
        }

        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Enter in processVoid of BankOne---");
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        String terminalId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String password = GatewayAccountService.getGatewayAccount(accountId).getPassword();
        String currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();

        BankoneUtils bankoneUtils = new BankoneUtils();

        String xmlRequest = "<?xml version='1.0' encoding='ISO-8859-1' ?>\n" +
                "<request>\n" +
                "<terminalid>"+terminalId+"</terminalid>\n" +
                "<password>"+password+"</password>\n" +
                "<action>"+VOID+"</action>\n" +
                "<currencyCode>"+currency+"</currencyCode>\n" +
                "<amount>"+commTransactionDetailsVO.getAmount()+"</amount>\n" +
                "<trackid>"+trackingID+"</trackid>\n" +
                "<transid>"+commTransactionDetailsVO.getPreviousTransactionId()+"</transid>\n" +
                "<merchantIp></merchantIp>\n" +
                "<customerIp></customerIp>\n" +
                "<udf1></udf1>\n" +
                "<udf2></udf2>\n" +
                "<udf3></udf3>\n" +
                "<udf4></udf4>\n" +
                "</request>";

        transactionLogger.debug("Refund request---"+xmlRequest);

        String response = bankoneUtils.doPostHTTPSURLConnection(TRAN_URL,xmlRequest);

        transactionLogger.debug("Refund response---"+response);

        if(response!=null)
        {
            commResponseVO = bankoneUtils.readResponse(response);
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(new Date())));
            commResponseVO.setTransactionType("cancel");
        }

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
                PZExceptionHandler.raiseConstraintViolationException(BankonePaymentGateway.class.getName(), "validateForSale()", null, "common", "Amount not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Amount not provided while placing transaction", new Throwable("Amount not provided while placing transaction"));
            }
            if (genericTransDetailsVO.getCurrency() == null || genericTransDetailsVO.getCurrency().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(BankonePaymentGateway.class.getName(), "validateForSale()", null, "common", "Currency not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Currency not provided while placing transaction", new Throwable("Currency not provided while placing transaction"));
            }
            if (genericTransDetailsVO.getOrderId() == null || genericTransDetailsVO.getOrderId().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(BankonePaymentGateway.class.getName(), "validateForSale()", null, "common", "Order ID not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Order ID not provided while placing transaction", new Throwable("Order ID not provided while placing transaction"));
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(BankonePaymentGateway.class.getName(), "validateForSale()", null, "common", "TransactionDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING, null, "TransactionDetails not provided while placing transaction", new Throwable("TransactionDetails  not provided while placing transaction"));
        }

        if (addressDetailsVO != null)
        {
            if (addressDetailsVO.getFirstname() == null || addressDetailsVO.getFirstname().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(BankonePaymentGateway.class.getName(), "validateForSale()", null, "common", "First Name not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "First Name not provided while placing transaction", new Throwable("First Name not provided while placing transaction"));
            }
            if (addressDetailsVO.getLastname() == null || addressDetailsVO.getLastname().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(BankonePaymentGateway.class.getName(), "validateForSale()", null, "common", "Last Name not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Last Name not provided while placing transaction", new Throwable("Last Name not provided while placing transaction"));
            }
            if (addressDetailsVO.getEmail() == null || addressDetailsVO.getEmail().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(BankonePaymentGateway.class.getName(), "validateForSale()", null, "common", "Email ID not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Email ID not provided while placing transaction", new Throwable("Email ID not provided while placing transaction"));
            }
            
            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            String addressValidation = account.getAddressValidation();
            if (addressValidation.equalsIgnoreCase("Y"))
            {
                if (addressDetailsVO.getCountry() == null || addressDetailsVO.getCountry().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(BankonePaymentGateway.class.getName(), "validateForSale()", null, "common", "Country not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Country not provided while placing transaction", new Throwable("Country not provided while placing transaction"));
                }
                if (addressDetailsVO.getState() == null || addressDetailsVO.getState().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(BankonePaymentGateway.class.getName(), "validateForSale()", null, "common", "State not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "State not provided while placing transaction", new Throwable("State not provided while placing transaction"));
                }
                if (addressDetailsVO.getCity() == null || addressDetailsVO.getCity().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(BankonePaymentGateway.class.getName(), "validateForSale()", null, "common", "City not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "City not provided while placing transaction", new Throwable("City not provided while placing transaction"));
                }
                if (addressDetailsVO.getStreet() == null || addressDetailsVO.getStreet().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(BankonePaymentGateway.class.getName(), "validateForSale()", null, "common", "Street not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Street not provided while placing transaction", new Throwable("Street not provided while placing transaction"));
                }
                if (addressDetailsVO.getZipCode() == null || addressDetailsVO.getZipCode().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(BankonePaymentGateway.class.getName(), "validateForSale()", null, "common", "Zip Code not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Zip Code not provided while placing transaction", new Throwable("Zip Code not provided while placing transaction"));
                }
                if (addressDetailsVO.getPhone() == null || addressDetailsVO.getPhone().equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException(BankonePaymentGateway.class.getName(), "validateForSale()", null, "common", "Phone NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Phone NO not provided while placing transaction", new Throwable("Phone NO not provided while placing transaction"));
                }
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(BankonePaymentGateway.class.getName(), "validateForSale()", null, "common", "AddressDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING, null, "AddressDetails  not provided while placing transaction", new Throwable("AddressDetails  not provided while placing transaction"));
        }

        if (genericCardDetailsVO != null)
        {
            if (genericCardDetailsVO.getCardNum() == null || genericCardDetailsVO.getCardNum().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(BankonePaymentGateway.class.getName(), "validateForSale()", null, "common", "Card NO not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Card NO not provided while placing transaction", new Throwable("Card NO not provided while placing transaction"));
            }
            if (genericCardDetailsVO.getExpMonth() == null || genericCardDetailsVO.getExpMonth().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(BankonePaymentGateway.class.getName(), "validateForSale()", null, "common", "Expiry Month not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Expiry Month not provided while placing transaction", new Throwable("Expiry Month not provided while placing transaction"));
            }
            if (genericCardDetailsVO.getExpYear() == null || genericCardDetailsVO.getExpYear().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(BankonePaymentGateway.class.getName(), "validateForSale()", null, "common", "Expiry Year not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Expiry Year not provided while placing transaction", new Throwable("Expiry Year not provided while placing transaction"));
            }
            if (genericCardDetailsVO.getcVV() == null || genericCardDetailsVO.getcVV().equals(""))
            {
                PZExceptionHandler.raiseConstraintViolationException(BankonePaymentGateway.class.getName(), "validateForSale()", null, "common", "CVV not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "CVV not provided while placing transaction", new Throwable("CVV not provided while placing transaction"));
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(BankonePaymentGateway.class.getName(), "validateForSale()", null, "common", "CardDetails  not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING, null, "CardDetails  not provided while placing transaction", new Throwable("CardDetails  not provided while placing transaction"));
        }

        if (trackingId == null || trackingId.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(BankonePaymentGateway.class.getName(), "validateForSale()", null, "common", "Tracking Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Tracking Id not provided while placing transaction", new Throwable("Tracking Id not provided while placing transaction"));
        }

    }
}
