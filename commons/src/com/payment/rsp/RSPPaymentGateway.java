package com.payment.rsp;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.manager.TerminalManager;
import com.manager.vo.TerminalVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by admin on 5/21/2018.
 */
public class RSPPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "rsp";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.rsp");
    private static TransactionLogger transactionLogger = new TransactionLogger(RSPPaymentGateway.class.getName());

    public RSPPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        String reqType = "CAPTURE";
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        RSPUtils rspUtils = new RSPUtils();
        Functions functions = new Functions();

        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO= commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();

        String isCurrencyConversion = commRequestVO.getCurrencyConversion();
        String conversionCurrency = commRequestVO.getConversionCurrency();
        String terminalId = transDetailsVO.getTerminalId();

        transactionLogger.error("isCurrencyConversion:" + isCurrencyConversion);
        transactionLogger.error("conversionCurrency:" + conversionCurrency);

        String merchantId = gatewayAccount.getMerchantId();
        String reqAccountId = gatewayAccount.getCHARGEBACK_FTP_PATH();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String secretKey = gatewayAccount.getFRAUD_FTP_PATH();

        String status = "";
        String remark = "";
        String descriptor = "";
        String responseData = "";
        String hash = "";
        transactionLogger.error("host url----"+commMerchantVO.getHostUrl());
        String termUrl = "";
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
            transactionLogger.error("from host url----"+termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL_3DS");
            transactionLogger.error("from RB----"+termUrl);
        }


        String transactionCurrency = transDetailsVO.getCurrency();
        String transactionAmount = transDetailsVO.getAmount();
        if ("Y".equals(isCurrencyConversion))
        {
            Double exchangeRate = null;
            if (functions.isValueNull(conversionCurrency))
            {
                exchangeRate = rspUtils.getExchangeRate(transactionCurrency, conversionCurrency);
            }
            else
            {
                transactionLogger.error("rejecting transaction because conversion currency has not defined");
                commResponseVO.setStatus("failed");
                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                commResponseVO.setDescription("Conversion currency has not defined");
                commResponseVO.setRemark("Conversion currency has not defined");
                commResponseVO.setCurrency(transDetailsVO.getCurrency());
                commResponseVO.setTmpl_Amount(addressDetailsVO.getTmpl_amount());
                commResponseVO.setTmpl_Currency(addressDetailsVO.getTmpl_currency());
                return commResponseVO;
            }
            if (exchangeRate != null)
            {
                transactionAmount = Functions.round(Double.valueOf(exchangeRate) * Double.valueOf(transDetailsVO.getAmount()), 2);
                transactionCurrency = conversionCurrency;
                TerminalManager terminalManager = new TerminalManager();
                if (functions.isValueNull(terminalId))
                {
                    TerminalVO terminalVO = terminalManager.getMemberTerminalfromTerminal(terminalId);
                    TerminalVO terminalVO1 = terminalManager.getMemberTerminalDetailsForTerminalChange(terminalVO.getMemberId(), terminalVO.getPaymodeId(), terminalVO.getCardTypeId(), transactionCurrency);
                    if (terminalVO1 != null)
                    {
                        GatewayAccount gatewayAccount1 = GatewayAccountService.getGatewayAccount(terminalVO1.getAccountId());
                        rspUtils.changeTerminalInfo(transactionAmount, transactionCurrency, String.valueOf(gatewayAccount1.getAccountId()), gatewayAccount1.getMerchantId(), transDetailsVO.getAmount(), transDetailsVO.getCurrency(), trackingID, terminalVO1.getTerminalId());
                        commResponseVO.setAmount(transactionAmount);
                    }
                    else
                    {
                        transactionLogger.error("rejecting transaction because " + transactionCurrency + " terminal not defined");
                        commResponseVO.setStatus("failed");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setDescription(transactionCurrency + " terminal not defined");
                        commResponseVO.setRemark(transactionCurrency + " terminal not defined");
                        commResponseVO.setCurrency(transDetailsVO.getCurrency());
                        commResponseVO.setTmpl_Amount(addressDetailsVO.getTmpl_amount());
                        commResponseVO.setTmpl_Currency(addressDetailsVO.getTmpl_currency());
                        return commResponseVO;
                    }
                }
                else
                {
                    transactionLogger.error("rejecting transaction because invalid terminal configuration ");
                    commResponseVO.setStatus("failed");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setDescription("Invalid terminal configuration");
                    commResponseVO.setRemark("Invalid terminal configuration");
                    return commResponseVO;
                }
            }
            else
            {
                transactionLogger.error("rejecting transaction because exchange rates not found");
                commResponseVO.setStatus("failed");
                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                commResponseVO.setDescription("Exchange rate has not been defined");
                commResponseVO.setRemark("Exchange rate has not been defined");
                commResponseVO.setCurrency(transDetailsVO.getCurrency());
                commResponseVO.setTmpl_Amount(addressDetailsVO.getTmpl_amount());
                commResponseVO.setTmpl_Currency(addressDetailsVO.getTmpl_currency());
                return commResponseVO;
            }
            transactionLogger.error("FromCurrency:" + transDetailsVO.getCurrency());
            transactionLogger.error("transactionCurrency:" + transactionCurrency);
            transactionLogger.error("ExChangeRate:" + exchangeRate);
            transactionLogger.error("transactionAmount:" + transactionAmount);
        }

        try
        {
            String cardType = rspUtils.getRSPCardTypeName(cardDetailsVO.getCardType());
            hash = rspUtils.getMD5HashForSale(reqType, merchantId, reqAccountId, userName, password, trackingID, secretKey);
            String saleRequest =
                    "req_type=" + reqType +
                            "&req_mid=" + merchantId +
                            "&req_accountid=" + reqAccountId +
                            "&req_username=" + userName +
                            "&req_password=" + password +
                            "&req_trackid=" + trackingID +
                            "&req_amount=" + transactionAmount +
                            "&req_currency=" + transactionCurrency +
                            "&req_cardnumber=" + cardDetailsVO.getCardNum() +
                            "&req_cardtype=" + cardType +
                            "&req_mm=" + cardDetailsVO.getExpMonth() +
                            "&req_yyyy=" + cardDetailsVO.getExpYear() +
                            "&req_cvv=" + cardDetailsVO.getcVV() +
                            "&req_firstname=" + addressDetailsVO.getFirstname() +
                            "&req_lastname=" + addressDetailsVO.getLastname() +
                            "&req_address=" + addressDetailsVO.getStreet() +
                            "&req_city=" + addressDetailsVO.getCity() +
                            "&req_statecode=" + addressDetailsVO.getState() +
                            "&req_countrycode=" + addressDetailsVO.getCountry() +
                            "&req_zipcode=" + addressDetailsVO.getZipCode() +
                            "&req_phone=" + addressDetailsVO.getPhone() +
                            "&req_email=" + addressDetailsVO.getEmail() +
                            "&req_ipaddress=" + addressDetailsVO.getCardHolderIpAddress() +
                            "&req_returnurl=" + termUrl+trackingID + "" +
                            "&req_signature=" + hash;
                    String saleRequestLog =
                            "req_type=" + reqType +
                            "&req_mid=" + merchantId +
                            "&req_accountid=" + reqAccountId +
                            "&req_username=" + userName +
                            "&req_password=" + password +
                            "&req_trackid=" + trackingID +
                            "&req_amount=" + transactionAmount +
                            "&req_currency=" + transactionCurrency +
                            "&req_cardnumber=" + functions.maskingPan(cardDetailsVO.getCardNum()) +
                            "&req_cardtype=" + cardType +
                            "&req_mm=" + functions.maskingNumber(cardDetailsVO.getExpMonth()) +
                            "&req_yyyy=" + functions.maskingNumber(cardDetailsVO.getExpYear()) +
                            "&req_cvv=" + functions.maskingNumber(cardDetailsVO.getcVV()) +
                            "&req_firstname=" + addressDetailsVO.getFirstname() +
                            "&req_lastname=" + addressDetailsVO.getLastname() +
                            "&req_address=" + addressDetailsVO.getStreet() +
                            "&req_city=" + addressDetailsVO.getCity() +
                            "&req_statecode=" + addressDetailsVO.getState() +
                            "&req_countrycode=" + addressDetailsVO.getCountry() +
                            "&req_zipcode=" + addressDetailsVO.getZipCode() +
                            "&req_phone=" + addressDetailsVO.getPhone() +
                            "&req_email=" + addressDetailsVO.getEmail() +
                            "&req_ipaddress=" + addressDetailsVO.getCardHolderIpAddress() +
                            "&req_returnurl=" + termUrl+trackingID + "" +
                            "&req_signature=" + hash;

            transactionLogger.error("-----sale request---"+trackingID+"--" + saleRequestLog);
            if (isTest)
            {
                responseData = rspUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), saleRequest);
            }
            else
            {
                responseData = rspUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), saleRequest);
            }

            responseData = URLDecoder.decode(responseData);
            transactionLogger.error("----- sale response---"+trackingID+"--" + responseData);
            HashMap hashMap = rspUtils.readResponse(responseData);
            if ("0".equals(hashMap.get("res_code")) && "Approved".equals(hashMap.get("res_message")))
            {
                status = "success";
                remark = (String) hashMap.get("res_message");
                descriptor = gatewayAccount.getDisplayName();
            }
            else if ("2".equals(hashMap.get("res_code")) && "Pending".equals(hashMap.get("res_message")))
            {
                status = "pending3DConfirmation";
                remark = "3D Authentication is pending";
                commResponseVO.setUrlFor3DRedirect((String) hashMap.get("res_redirecturl"));
                descriptor = gatewayAccount.getDisplayName();
            }
            else
            {
                status = "failed";
                remark = (String) hashMap.get("res_message");
            }
            commResponseVO.setMerchantOrderId(trackingID);
            commResponseVO.setTransactionId((String) hashMap.get("res_referenceid"));
            commResponseVO.setStatus(status);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
            commResponseVO.setResponseTime((String) hashMap.get("res_datetime"));
            commResponseVO.setCurrency(transDetailsVO.getCurrency());
            commResponseVO.setTmpl_Amount(addressDetailsVO.getTmpl_amount());
            commResponseVO.setTmpl_Currency(addressDetailsVO.getTmpl_currency());
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(RSPPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint(RSPPaymentGateway.class.getName(), "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        return null;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        String reqTypeFullRefund = "FULL REFUND";
        String reqTypePartialRefund = "PARTIAL REFUND";
        String reqType = reqTypeFullRefund;
        String reqAmount = "";

        RSPUtils rspUtils = new RSPUtils();
        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;

        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();

        String requestTime = dateFormat.format(date);

        boolean isTest = gatewayAccount.isTest();
        String merchantId = gatewayAccount.getMerchantId();
        String reqAccountId = gatewayAccount.getCHARGEBACK_FTP_PATH();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String secretKey = gatewayAccount.getFRAUD_FTP_PATH();

        if (Double.valueOf(commTransactionDetailsVO.getAmount()) < Double.valueOf(commTransactionDetailsVO.getPreviousTransactionAmount()))
        {
            reqType = reqTypePartialRefund;
        }

        if ("PARTIAL REFUND".equals(reqType))
        {
            reqAmount = "&req_amount=" + commTransactionDetailsVO.getAmount();
        }

        String status = "";
        String remark = "";
        String descriptor = "";
        String responseData = "";
        String hash = "";

        try
        {
            hash = rspUtils.getMD5HashForSale(reqType, merchantId, reqAccountId, userName, password, trackingID + "_" + requestTime, secretKey);
            String requestData =
                    "req_type=" + reqType +
                            "&req_mid=" + merchantId +
                            "&req_accountid=" + reqAccountId +
                            "&req_username=" + userName +
                            "&req_password=" + password +
                            "&req_trackid=" + trackingID + "_" + requestTime +
                            "&req_origreferenceid=" + commTransactionDetailsVO.getPreviousTransactionId() +
                            reqAmount +
                            "&req_signature=" + hash;

            transactionLogger.error("-----refund request-----" + requestData);
            if (isTest)
            {
                responseData = rspUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), requestData);
            }
            else
            {
                responseData = rspUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), requestData);
            }

            responseData = URLDecoder.decode(responseData);
            transactionLogger.error("-----refund response-----" + responseData);
            HashMap hashMap = rspUtils.readResponse(responseData);
            if ("0".equals(hashMap.get("res_code")) && "Approved".equals(hashMap.get("res_message")))
            {
                status = "success";
                remark = (String) hashMap.get("res_message");
                descriptor = gatewayAccount.getDisplayName();
            }
            else
            {
                status = "fail";
                remark = (String) hashMap.get("res_message");
            }
            commResponseVO.setMerchantOrderId(trackingID);
            commResponseVO.setTransactionId((String) hashMap.get("res_referenceid"));
            commResponseVO.setStatus(status);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
            commResponseVO.setResponseTime((String) hashMap.get("res_datetime"));
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(RSPPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while refunding transaction ", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        return null;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        String reqType = "SEARCH";
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        RSPUtils rspUtils = new RSPUtils();

        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String merchantId = gatewayAccount.getMerchantId();
        String reqAccountId = gatewayAccount.getCHARGEBACK_FTP_PATH();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String secretKey = gatewayAccount.getFRAUD_FTP_PATH();
        boolean isTest = gatewayAccount.isTest();

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();

        String requestTime = dateFormat.format(date);

        String status = "";
        String remark = "";
        String responseData = "";
        String hash = "";
        try
        {
            hash = rspUtils.getMD5HashForSale(reqType, merchantId, reqAccountId, userName, password, trackingID + "_" + requestTime, secretKey);
            String requestData =
                    "req_type=" + reqType +
                            "&req_mid=" + merchantId +
                            "&req_accountid=" + reqAccountId +
                            "&req_username=" + userName +
                            "&req_password=" + password +
                            "&req_trackid=" + trackingID + "_" + requestTime +
                            "&req_origtrackid=" + trackingID +
                            "&req_signature=" + hash;

            transactionLogger.error("----process query request-----" + requestData);
            if (isTest)
            {
                responseData = rspUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), requestData);
            }
            else
            {
                responseData = rspUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), requestData);
            }

            responseData = URLDecoder.decode(responseData);
            transactionLogger.error("-----process query response-----" + responseData);

            HashMap hashMap = rspUtils.readResponse(responseData);
            if ("0".equals(hashMap.get("res_code")) && "Approved".equals(hashMap.get("res_message")))
            {
                status = "success";
                remark = (String) hashMap.get("res_founddescription");
            }
            else
            {
                status = "fail";
                remark = (String) hashMap.get("res_founddescription");
            }

            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            commResponseVO.setMerchantId(merchantId);
            commResponseVO.setMerchantOrderId((String) hashMap.get("res_trackid"));
            commResponseVO.setTransactionId((String) hashMap.get("res_referenceid"));
            commResponseVO.setAuthCode("-");
            commResponseVO.setTransactionType((String) hashMap.get("res_foundtype"));
            commResponseVO.setTransactionStatus((String) hashMap.get("res_foundmessage"));
            commResponseVO.setStatus(status);
            commResponseVO.setAmount(commTransactionDetailsVO.getAmount());
            commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
            commResponseVO.setBankTransactionDate((String) hashMap.get("res_founddatetime"));
            commResponseVO.setErrorCode((String) hashMap.get("res_code"));
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(RSPPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while doing inquiry transaction ", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        String reqType = "SEARCH";
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        RSPUtils rspUtils = new RSPUtils();

        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String merchantId = gatewayAccount.getMerchantId();
        String reqAccountId = gatewayAccount.getCHARGEBACK_FTP_PATH();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String secretKey = gatewayAccount.getFRAUD_FTP_PATH();
        boolean isTest = gatewayAccount.isTest();

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();

        String requestTime = dateFormat.format(date);

        String status = "";
        String remark = "";
        String responseData = "";
        String hash = "";
        try
        {
            hash = rspUtils.getMD5HashForSale(reqType, merchantId, reqAccountId, userName, password, commTransactionDetailsVO.getOrderId() + "_" + requestTime, secretKey);
            String requestData =
                    "req_type=" + reqType +
                            "&req_mid=" + merchantId +
                            "&req_accountid=" + reqAccountId +
                            "&req_username=" + userName +
                            "&req_password=" + password +
                            "&req_trackid=" + commTransactionDetailsVO.getOrderId() + "_" + requestTime +
                            "&req_origtrackid=" + commTransactionDetailsVO.getOrderId() +
                            "&req_signature=" + hash;

            transactionLogger.error("----process query request-----" + requestData);
            if (isTest)
            {
                responseData = rspUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), requestData);
            }
            else
            {
                responseData = rspUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), requestData);
            }

            responseData = URLDecoder.decode(responseData);
            transactionLogger.error("-----process query response-----" + responseData);

            HashMap hashMap = rspUtils.readResponse(responseData);
            if ("0".equals(hashMap.get("res_code")) && "Approved".equals(hashMap.get("res_message")))
            {
                status = "success";
                remark = (String) hashMap.get("res_founddescription");
            }
            else
            {
                status = "fail";
                remark = (String) hashMap.get("res_founddescription");
            }

            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            commResponseVO.setMerchantId(merchantId);
            commResponseVO.setMerchantOrderId((String) hashMap.get("res_trackid"));
            commResponseVO.setTransactionId((String) hashMap.get("res_referenceid"));
            commResponseVO.setAuthCode("-");
            commResponseVO.setTransactionType((String) hashMap.get("res_foundtype"));
            commResponseVO.setTransactionStatus((String) hashMap.get("res_foundmessage"));
            commResponseVO.setStatus(status);
            commResponseVO.setAmount(commTransactionDetailsVO.getAmount());
            commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
            commResponseVO.setBankTransactionDate((String) hashMap.get("res_founddatetime"));
            commResponseVO.setErrorCode((String) hashMap.get("res_code"));
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(RSPPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while doing inquiry transaction ", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

}
