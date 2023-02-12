package com.payment.clearsettle;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import java.io.*;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Sneha on 1/11/2017.
 */

public class ClearSettlePaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "clrsettle";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.clearsettle");
    private static TransactionLogger transactionLogger = new TransactionLogger(ClearSettlePaymentGateway.class.getName());
    private static Functions functions = new Functions();
    private static ClearSettleUtills clearSettleUtills = new ClearSettleUtills();

    public ClearSettlePaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        String countryCode = addressDetailsVO.getCountry();

        String isOnly3dSecureAccount = "no";
        String returnUrl = "";
        String attemptThreeD = "";
        String status = "";
        String descriptor = "";
        String response = "";


        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String apiKey = gatewayAccount.getMerchantId();
        String is3dSecureAccount = gatewayAccount.get_3DSupportAccount();

        SimpleDateFormat inputDate = new SimpleDateFormat("yyyymmdd");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");

        if (countryCode.length() > 2)
        {
            countryCode = countryCode.substring(0, 2);
        }

        if (functions.isValueNull(commRequestVO.getAttemptThreeD()))
        {
            attemptThreeD = commRequestVO.getAttemptThreeD();
        }

        if ("Y".equals(is3dSecureAccount) && !("Direct".equalsIgnoreCase(attemptThreeD)))
        {
            is3dSecureAccount = "yes";
            returnUrl = RB.getString("CLEAR_SETTLE_FRONTEND");
            if ("Only3D".equalsIgnoreCase(attemptThreeD))
            {
                isOnly3dSecureAccount = "yes";
            }
        }
        else
        {
            is3dSecureAccount = "no";
        }

        try
        {
            String birthDate = formatter.format(inputDate.parse(addressDetailsVO.getBirthdate()));
            String request =
                    "{" +
                            "\"apiKey\":\"" + apiKey + "\",\n" +
                            "\"amount\":\"" + getCentAmount(transDetailsVO.getAmount()) + "\",\n" +
                            "\"currency\":\"" + transDetailsVO.getCurrency() + "\",\n" +
                            "\"referenceNo\":\"" + trackingID + "\",\n" +
                            "\"description\":\"" + transDetailsVO.getOrderDesc() + "\",\n" +
                            "\"isPreAuth\":\"yes\",\n" +
                            "\"number\":\"" + cardDetailsVO.getCardNum() + "\",\n" +
                            "\"expiryMonth\":\"" + cardDetailsVO.getExpMonth() + "\",\n" +
                            "\"expiryYear\":\"" + cardDetailsVO.getExpYear() + "\",\n" +
                            "\"cvv\":\"" + cardDetailsVO.getcVV() + "\",\n" +
                            "\"email\":\"" + addressDetailsVO.getEmail() + "\",\n" +
                            "\"birthday\":\"" + birthDate + "\",\n" +
                            "\"billingFirstName\":\"" + addressDetailsVO.getFirstname() + "\",\n" +
                            "\"billingLastName\":\"" + addressDetailsVO.getLastname() + "\",\n" +
                            "\"billingAddress1\":\"" + addressDetailsVO.getStreet() + "\",\n" +
                            "\"billingCity\":\"" + addressDetailsVO.getCity() + "\",\n" +
                            "\"billingPostcode\":\"" + addressDetailsVO.getZipCode() + "\",\n" +
                            "\"billingCountry\":\"" + countryCode + "\",\n" +
                            "\"customerIp\":\"" + addressDetailsVO.getCardHolderIpAddress() + "\",\n" +
                            "\"customerUserAgent\":\"" + "Mozilla/5.0 (Windows NT 6.1)AppleWebKit/537.36" + "\"," +
                            "\"returnUrl\":\"" + returnUrl + "\",\n" +
                            "\"only3d\":\"" + isOnly3dSecureAccount + "\",\n" +
                            "\"is3d\":\"" + is3dSecureAccount + "\"}";

            transactionLogger.error("-----auth request-----" + request);
            if (isTest)
            {
                response = clearSettleUtills.doPostHTTPSURLConnectionClient(RB.getString("TEST_SALE_URL"), request);
            }
            else
            {
                response = clearSettleUtills.doPostHTTPSURLConnectionClient(RB.getString("LIVE_SALE_URL"), request);
            }
            transactionLogger.error("-----auth response-----" + response);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ClearSettleResponseVO clearSettleResponseVO = objectMapper.readValue(response, ClearSettleResponseVO.class);
            if ("00".equals(clearSettleResponseVO.getCode()))
            {
                status = "success";
                if(functions.isValueNull(clearSettleResponseVO.getDescriptor())){
                    descriptor = clearSettleResponseVO.getDescriptor();
                }
                else
                {
                    descriptor=gatewayAccount.getDisplayName();
                }
            }
            else if ("283".equals(clearSettleResponseVO.getCode()))
            {
                if ("true".equals(clearSettleResponseVO.getIs3d()) && "WAITING".equals(clearSettleResponseVO.getStatus()))
                {
                    status = "pending3DConfirmation";
                    commResponseVO.setStatus(status);
                    commResponseVO.setUrlFor3DRedirect(URLDecoder.decode(clearSettleResponseVO.getForm3d()));
                    if (functions.isValueNull(clearSettleResponseVO.getDescriptor()))
                    {
                        descriptor = clearSettleResponseVO.getDescriptor();
                    }
                    else{
                        descriptor=gatewayAccount.getDisplayName();
                    }
                }

            }
            commResponseVO.setTransactionId(clearSettleResponseVO.getTransactionId());
            commResponseVO.setStatus(status);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(clearSettleResponseVO.getMessage());
            commResponseVO.setRemark(clearSettleResponseVO.getMessage());
            commResponseVO.setResponseTime(clearSettleResponseVO.getDate());
            commResponseVO.setTransactionType("Auth");
        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettlePaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettlePaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Parsing while placing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettlePaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (ParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettlePaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        String countryCode = addressDetailsVO.getCountry();

        String isOnly3dSecureAccount = "no";
        String returnUrl = "";
        String attemptThreeD = "";
        String status = "";
        String descriptor = "";
        String response = "";


        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String apiKey = gatewayAccount.getMerchantId();
        String is3dSecureAccount = gatewayAccount.get_3DSupportAccount();

        SimpleDateFormat inputDate = new SimpleDateFormat("yyyymmdd");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");

        if (countryCode.length() > 2)
        {
            countryCode = countryCode.substring(0, 2);
        }

        if (functions.isValueNull(commRequestVO.getAttemptThreeD()))
        {
            attemptThreeD = commRequestVO.getAttemptThreeD();
        }

        if ("Y".equals(is3dSecureAccount) && !("Direct".equalsIgnoreCase(attemptThreeD)))
        {
            is3dSecureAccount = "yes";
            returnUrl = RB.getString("CLEAR_SETTLE_FRONTEND");
            if ("Only3D".equalsIgnoreCase(attemptThreeD))
            {
                isOnly3dSecureAccount = "yes";
            }
        }
        else
        {
            is3dSecureAccount = "no";
        }

        try
        {
            String birthDate = formatter.format(inputDate.parse(addressDetailsVO.getBirthdate()));
            String request =
                    "{" +
                            "\"apiKey\":\"" + apiKey + "\",\n" +
                            "\"amount\":\"" + getCentAmount(transDetailsVO.getAmount()) + "\",\n" +
                            "\"currency\":\"" + transDetailsVO.getCurrency() + "\",\n" +
                            "\"referenceNo\":\"" + trackingID + "\",\n" +
                            "\"description\":\"" + transDetailsVO.getOrderDesc() + "\",\n" +
                            "\"number\":\"" + cardDetailsVO.getCardNum() + "\",\n" +
                            "\"expiryMonth\":\"" + cardDetailsVO.getExpMonth() + "\",\n" +
                            "\"expiryYear\":\"" + cardDetailsVO.getExpYear() + "\",\n" +
                            "\"cvv\":\"" + cardDetailsVO.getcVV() + "\",\n" +
                            "\"email\":\"" + addressDetailsVO.getEmail() + "\",\n" +
                            "\"birthday\":\"" + birthDate + "\",\n" +
                            "\"billingFirstName\":\"" + addressDetailsVO.getFirstname() + "\",\n" +
                            "\"billingLastName\":\"" + addressDetailsVO.getLastname() + "\",\n" +
                            "\"billingAddress1\":\"" + addressDetailsVO.getStreet() + "\",\n" +
                            "\"billingCity\":\"" + addressDetailsVO.getCity() + "\",\n" +
                            "\"billingPostcode\":\"" + addressDetailsVO.getZipCode() + "\",\n" +
                            "\"billingCountry\":\"" + countryCode + "\",\n" +
                            "\"customerIp\":\"" + addressDetailsVO.getCardHolderIpAddress() + "\",\n" +
                            "\"customerUserAgent\":\"" + "Mozilla/5.0 (Windows NT 6.1)AppleWebKit/537.36" + "\"," +
                            "\"returnUrl\":\"" + returnUrl + "\",\n" +
                            "\"only3d\":\"" + isOnly3dSecureAccount + "\",\n" +
                            "\"is3d\":\"" + is3dSecureAccount + "\"}";

            transactionLogger.error("-----sale request-----" + request);
            if (isTest)
            {
                response = clearSettleUtills.doPostHTTPSURLConnectionClient(RB.getString("TEST_SALE_URL"), request);
            }
            else
            {
                response = clearSettleUtills.doPostHTTPSURLConnectionClient(RB.getString("LIVE_SALE_URL"), request);
            }
            transactionLogger.error("-----sale response-----" + response);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ClearSettleResponseVO clearSettleResponseVO = objectMapper.readValue(response, ClearSettleResponseVO.class);
            if ("00".equals(clearSettleResponseVO.getCode()))
            {
                status = "success";
                if (functions.isValueNull(clearSettleResponseVO.getDescriptor()))
                {
                    descriptor = clearSettleResponseVO.getDescriptor();
                }
                else
                {
                    descriptor = gatewayAccount.getDisplayName();
                }
            }
            else if ("283".equals(clearSettleResponseVO.getCode()))
            {
                if ("true".equals(clearSettleResponseVO.getIs3d()) && "WAITING".equals(clearSettleResponseVO.getStatus()))
                {
                    status = "pending3DConfirmation";
                    commResponseVO.setStatus(status);
                    commResponseVO.setUrlFor3DRedirect(URLDecoder.decode(clearSettleResponseVO.getForm3d()));
                    if (functions.isValueNull(clearSettleResponseVO.getDescriptor()))
                    {
                        descriptor = clearSettleResponseVO.getDescriptor();
                    }
                    else
                    {
                        descriptor = gatewayAccount.getDisplayName();
                    }
                }

            }
            commResponseVO.setTransactionId(clearSettleResponseVO.getTransactionId());
            commResponseVO.setStatus(status);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(clearSettleResponseVO.getMessage());
            commResponseVO.setRemark(clearSettleResponseVO.getMessage());
            commResponseVO.setResponseTime(clearSettleResponseVO.getDate());
            commResponseVO.setTransactionType("Sale");
        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettlePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettlePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Parsing while placing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettlePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (ParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettlePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws  PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        ClearSettleResponseVO responseVO = new ClearSettleResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String apiKey = gatewayAccount.getMerchantId();

        String status = "";
        String descriptor = "";
        String response = "";
        try
        {
            String request =
                    "{\n" +
                            "\"apiKey\":\"" + apiKey + "\",\n" +
                            "\"referenceNo\":\"" + trackingID + "\",\n" +
                            "\"transactionId\":\"" + transactionDetailsVO.getPreviousTransactionId() + "\"}";

            transactionLogger.error("-----cancel request-----" + request);
            if (isTest)
            {
                response = clearSettleUtills.doPostHTTPSURLConnectionClient(RB.getString("TEST_CANCEL_URL"), request);
            }
            else
            {
                response = clearSettleUtills.doPostHTTPSURLConnectionClient(RB.getString("LIVE_CANCEL_URL"), request);
            }
            transactionLogger.error("-----cancel response-----" + response);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            responseVO = objectMapper.readValue(response, ClearSettleResponseVO.class);
            if ("00".equals(responseVO.getCode()))
            {
                status = "success";
            }
            responseVO.setStatus(status);
            responseVO.setDescriptor(descriptor);
            responseVO.setDescription(responseVO.getMessage());
        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettlePaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while cancelling the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettlePaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Parsing while cancelling the transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettlePaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while cancelling the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return responseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        ClearSettleResponseVO responseVO = new ClearSettleResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String apiKey = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();

        String status = "failed";
        String descriptor = "";
        String response = "";

        try
        {
            String request = "{" +
                    "\"apiKey\":\"" + apiKey + "\" ,\n" +
                    "\"referenceNo\":\"" + trackingID + "\" ,\n" +
                    "\"transactionId\" : \"" + transactionDetailsVO.getPreviousTransactionId() + "\"}";

            transactionLogger.error("-----refund request-----" + request);
            if (isTest)
            {
                response = clearSettleUtills.doPostHTTPSURLConnectionClient(RB.getString("TEST_REFUND_URL"), request);
            }
            else
            {
                response = clearSettleUtills.doPostHTTPSURLConnectionClient(RB.getString("LIVE_REFUND_URL"), request);
            }
            transactionLogger.error("-----refund response-----" + response);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            responseVO = objectMapper.readValue(response, ClearSettleResponseVO.class);
            if ("00".equals(responseVO.getCode()))
            {
                status = "success";
            }
            responseVO.setStatus(status);
            responseVO.setDescriptor(descriptor);
            responseVO.setDescription(responseVO.getMessage());
        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettlePaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while refunding the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettlePaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Parsing while refunding the transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettlePaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while refunding the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return responseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        ClearSettleResponseVO responseVO = new ClearSettleResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String apiKey = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String status = "failed";
        String descriptor = "";
        String responseData = "";

        try
        {
            String request =
                    "{" +
                            "\"apiKey\":\"" + apiKey + "\",\n" +
                            "\"referenceNo\":\"" + trackingID + "\",\n" +
                            "\"transactionId\":\"" + transactionDetailsVO.getPreviousTransactionId() + "\"}"; //todo have to handel the partial capture at bank's end
//                        "\"amount\":\"" + getCentAmount(transactionDetailsVO.getAmount()) + "\",\n" +
//                        "\"currency\":\"" + transactionDetailsVO.getCurrency() + "\"\n" +

            transactionLogger.error("-----capture request-----" + request);
            if (isTest)
            {
                responseData = clearSettleUtills.doPostHTTPSURLConnectionClient(RB.getString("TEST_CAPTURE_URL"), request);
            }
            else
            {
                responseData = clearSettleUtills.doPostHTTPSURLConnectionClient(RB.getString("LIVE_CAPTURE_URL"), request);
            }

            transactionLogger.error("-----capture response-----" + request);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            responseVO = objectMapper.readValue(responseData, ClearSettleResponseVO.class);
            if ("00".equals(responseVO.getCode()))
            {
                status = "success";
            }
            if (!functions.isValueNull(responseVO.getTransactionId()))
            {
                responseVO.setTransactionId(transactionDetailsVO.getPreviousTransactionId());
            }
            responseVO.setStatus(status);
            responseVO.setErrorCode(responseVO.getCode());
            responseVO.setDescriptor(descriptor);
            responseVO.setDescription(responseVO.getMessage());
            responseVO.setRemark(responseVO.getMessage());
        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettlePaymentGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while capturing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettlePaymentGateway.class.getName(), "processCapture()", null, "common", "Technical Parsing while capturing the transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettlePaymentGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while capturing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return responseVO;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        ClearSettleResponseVO responseVO = new ClearSettleResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String mid = gatewayAccount.getMerchantId();
        String apiKey = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();

        String descriptor = "";
        String response = "";
        String merchantOrderId = "";
        String transactionId = "";
        String authCode = "";
        String transactionStatus = "";
        String transactionType = "";
        String amount = "";
        String currency = "";
        String remark = "";
        String status = "";
        String transactionDate = "";
        String errorCode = "";

        try
        {
            String request =
                    "{\n" +
                            "\"apiKey\":\"" + apiKey + "\" ,\n" +
                            "\"referenceNo\":\"" + transactionDetailsVO.getOrderId() + "\" ,\n" +
                            "\"transactionId\":\"" + transactionDetailsVO.getPreviousTransactionId() + "\"}";

            transactionLogger.error("-----inquiry request-----" + request);
            if (isTest)
            {
                response = clearSettleUtills.doPostHTTPSURLConnectionClient(RB.getString("TEST_INQUIRY_URL"), request);
            }
            else
            {
                response = clearSettleUtills.doPostHTTPSURLConnectionClient(RB.getString("LIVE_INQUIRY_URL"), request);
            }
            transactionLogger.error("-----inquiry response-----" + response);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            responseVO = objectMapper.readValue(response, ClearSettleResponseVO.class);
            transactionType = responseVO.getType();
            transactionStatus = responseVO.getStatus();
            if ("0".equals(responseVO.getCode()) && "APPROVED".equals(responseVO.getStatus()))
            {
                status = "success";
                remark = "Transaction Successful";
            }
            else if ("0".equals(responseVO.getCode()) && "WAITING".equals(responseVO.getStatus()))
            {
                status = "pending";
                remark = "Transaction Waiting";
            }
            else if ("0".equals(responseVO.getCode()) && "DECLINED".equals(responseVO.getStatus()))
            {
                status = "failed";
                remark = responseVO.getMessage();
            }
            else if ("0".equals(responseVO.getCode()) && "ERROR".equals(responseVO.getStatus()))
            {
                status = "error";
                remark = responseVO.getMessage();
            }
            else
            {
                status = "failed";
                remark = responseVO.getMessage();
            }

            merchantOrderId = transactionDetailsVO.getOrderId();
            transactionId = transactionDetailsVO.getPreviousTransactionId();
            amount = transactionDetailsVO.getAmount();
            currency = transactionDetailsVO.getCurrency();

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            responseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            responseVO.setMerchantId(mid);
            responseVO.setMerchantOrderId(merchantOrderId);
            responseVO.setTransactionId(transactionId);
            responseVO.setAuthCode(authCode);
            responseVO.setTransactionType(transactionType);
            responseVO.setTransactionStatus(transactionStatus);
            responseVO.setStatus(status);
            responseVO.setAmount(amount);
            responseVO.setCurrency(currency);
            responseVO.setBankTransactionDate(transactionDate);
            responseVO.setErrorCode(errorCode);
            responseVO.setDescription(remark);
            responseVO.setRemark(remark);
        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettlePaymentGateway.class.getName(), "processInquiry()", null, "common", "Technical Exception while process inquiry", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettlePaymentGateway.class.getName(), "processInquiry()", null, "common", "Technical Parsing while process inquiry", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ClearSettlePaymentGateway.class.getName(), "processInquiry()", null, "common", "Technical Exception while process inquiry", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return responseVO;
    }

    public String getCentAmount(String amount)
    {
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;
        Integer newAmount = dObj2.intValue();

        return newAmount.toString();
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
