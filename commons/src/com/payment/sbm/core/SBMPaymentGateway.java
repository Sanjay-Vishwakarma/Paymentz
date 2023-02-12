package com.payment.sbm.core;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.google.gson.Gson;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
//import ru.bpc.Plugin;
import com.payment.sbm.core.Plugin;
import org.codehaus.jettison.json.JSONObject;
import ru.bpc.exception.RequestProcessingException;
import ru.bpc.exception.UnexpectedResponseException;
import ru.bpc.message.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: Niket
 * Date: 8/26/14
 * Time: 6:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class SBMPaymentGateway extends AbstractPaymentGateway
{
    private static Logger logger = new Logger(SBMPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(SBMPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "SBMGateway";
    Functions functions = new Functions();

    //orderStatusDescription MAP {number,Order description}
    static Map<Integer, String> orderRegistrationDesc = new HashMap<Integer, String>();

    //sbm Gate way specific plugin
    final static ResourceBundle RB2 = LoadProperties.getProperty("com.directi.pg.config");

    //private final static Plugin plugin =Plugin.newInstance(RB2.getString("path"));
    static
    {
        //order status description according to the oderStatus number
        orderRegistrationDesc.put(0, "The order was registered successfully. A payment wasn't received yet");
        orderRegistrationDesc.put(1, "The order has been successfully authorised");//Is not used anymore
        orderRegistrationDesc.put(2, "All the amount was successfully authorised");
        orderRegistrationDesc.put(3, "The authorisation was cancelled");
        orderRegistrationDesc.put(4, "The refund was made");
        orderRegistrationDesc.put(5, "The transaction was sent to ACS for authorisation");
        orderRegistrationDesc.put(6, "The authorisation was declined");
    }

    public SBMPaymentGateway(String accountId)
    {
        this.accountId = accountId;

    }

    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static void main(String args[]) throws PZTechnicalViolationException
    {
//        Plugin plugin =Plugin.newInstance(RB2.getString("path"));
//
//        RegisterAndPayRequest rr = new RegisterAndPayRequest();
//        rr.setCurrency("840");
//        rr.setMerchantOrderNumber("6547cc4");
//        rr.setAmount("100.20");
//        rr.setLanguage("en");
//        rr.setReturnUrl("http://unesistedurlmerchant.ru/finish.html");
//        rr.setDescription("Test Transaction");
//        rr.setPan("5436031030894768");
//        rr.setCvc("902");
//        rr.setExpiration("201906");
//        rr.setCardholder("Va Kee");
//
//        System.out.println("RR---"+rr);
//
//        RegisterAndPayResponse resp = plugin.registerAndPay(rr);
//        System.out.println("url---"+resp.getAcsUrl());
//        System.out.println("resp---"+resp.redirectToACS());
//        System.out.println("status---"+resp.getOrderStatus());
//        System.out.println("code---"+resp.getActionCode());
//        System.out.println("desc---"+resp.getActionCodeDescription());
//        Gson gson= new Gson();
//        String req=gson.toJson(resp);

        Plugin plugin = Plugin.newInstance(RB2.getString("path") + "config.properties");
        System.setProperty("https.protocols", "TLSv1.2");
        System.setProperty("jsse.enableSNIExtension", "false");
        Gson gson = new Gson();

        OrderStatusRequest orderStatusRequest = new OrderStatusRequest();
        orderStatusRequest.setLanguage("en");
        orderStatusRequest.setOrderId("202012014202");
        String request = gson.toJson(orderStatusRequest);
        System.out.println("QueryRequest 1 -----" + request);

        OrderStatusResponse orderStatusResponse = plugin.getOrderStatus(orderStatusRequest);
        String response = gson.toJson(orderStatusResponse);

        System.out.println("inQuiry response-----" + response);
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering processSale in SBMPaymentGateway");

        SBMResponseVO sbmResponseVO = new SBMResponseVO();
        System.setProperty("https.protocols", "TLSv1.2");
        //System.setProperty("jsse.enableSNIExtension", "false");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String propertyName = gatewayAccount.getFRAUD_FTP_USERNAME(); //propery file name
        if (!functions.isValueNull(propertyName))
            propertyName = "config.properties";

        Plugin plugin = Plugin.newInstance(RB2.getString("path") + propertyName);
        transactionLogger.debug("plugin --- " + RB2.getString("path") + propertyName);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String termUrl = "";
        transactionLogger.error("HOST_URL ---" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB2.getString("HOST_URL");
            transactionLogger.error("From HOST_URL----" + termUrl);
        }
        else
        {
            termUrl = RB2.getString("TERM_URL");
            transactionLogger.error("From RB TERM_URL ----" + termUrl);
        }

        try
        {
            RegisterAndPayRequest rr = new RegisterAndPayRequest();

            rr.setCurrency(currency);
            rr.setMerchantOrderNumber(trackingID);
            rr.setAmount(transDetailsVO.getAmount());
            rr.setLanguage("en");
            rr.setReturnUrl(termUrl + trackingID);
            rr.setDescription(transDetailsVO.getOrderDesc());
            rr.setPan(cardDetailsVO.getCardNum());
            rr.setCvc(cardDetailsVO.getcVV());
            rr.setExpiration(cardDetailsVO.getExpYear() + cardDetailsVO.getExpMonth());
            rr.setCardholder(addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname());

            RegisterAndPayRequest rrLog = new RegisterAndPayRequest();

            rrLog.setCurrency(currency);
            rrLog.setMerchantOrderNumber(trackingID);
            rrLog.setAmount(transDetailsVO.getAmount());
            rrLog.setLanguage("en");
            rrLog.setReturnUrl(termUrl + trackingID);
            rr.setDescription(transDetailsVO.getOrderDesc());
            rrLog.setPan(functions.maskingPan(cardDetailsVO.getCardNum()));
            rrLog.setCvc(functions.maskingNumber(cardDetailsVO.getcVV()));
            rrLog.setExpiration(functions.maskingNumber(cardDetailsVO.getExpYear()) + functions.maskingNumber(cardDetailsVO.getExpMonth()));
            rrLog.setCardholder(addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname());

            Gson gson = new Gson();
            String req = gson.toJson(rr);
            String reqLog = gson.toJson(rrLog);

            transactionLogger.error("Sale Request--for--" + trackingID + "--" + reqLog);

            RegisterAndPayResponse orderStatusResponse = plugin.registerAndPay(rr);
            String res = gson.toJson(orderStatusResponse);
            transactionLogger.error("Sale Response--for--" + trackingID + "--" + res);

            String status = "";

            if (orderStatusResponse.redirectToACS())
            {
                sbmResponseVO.setStatus("pending3DConfirmation");
                sbmResponseVO.setUrlFor3DRedirect(orderStatusResponse.getAcsUrl());
                sbmResponseVO.setPaReq(orderStatusResponse.getPaReq());
                sbmResponseVO.setMd(orderStatusResponse.getOrderId());
                sbmResponseVO.setTerURL(orderStatusResponse.getTermUrl());

            }
            else
            {
                if (orderStatusResponse.getOrderStatus() == 2 && orderStatusResponse.getActionCode().equals("0"))
                {
                    status = "success";
                    sbmResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else
                {
                    status = "fail";
                }
                sbmResponseVO.setStatus(status);
                sbmResponseVO.setAmount(orderStatusResponse.getDepositAmount());
                sbmResponseVO.setTransactionId(orderStatusResponse.getOrderId());
                sbmResponseVO.setRemark(orderRegistrationDesc.get(orderStatusResponse.getOrderStatus()));
                sbmResponseVO.setErrorCode(orderStatusResponse.getActionCode());
                sbmResponseVO.setDescription(orderStatusResponse.getActionCodeDescription());
                sbmResponseVO.setResponseTime(dateFormat.format(date));
                sbmResponseVO.setTransactionType(PZProcessType.SALE.toString());
                sbmResponseVO.setIpaddress(orderStatusResponse.getIp());

                //extra parameters from sbm other than common table
                sbmResponseVO.setEci(orderStatusResponse.getEci());
                sbmResponseVO.setCvvValid(orderStatusResponse.getCvvValidationResult());
                sbmResponseVO.setDepositAmount(orderStatusResponse.getDepositAmount());
                sbmResponseVO.setPaymentAuthCode(orderStatusResponse.getPaymentSystemAuthCode());
                sbmResponseVO.setAuthCode(String.valueOf(orderStatusResponse.getPaymentSystemAuthCode()));
                sbmResponseVO.setProcessingAuthCode(String.valueOf(orderStatusResponse.getProcessingSystemAuthCode()));
                sbmResponseVO.setErrorCode(String.valueOf(orderStatusResponse.getProcessingSystemAuthCode()));
                sbmResponseVO.setReferenceNumber(orderStatusResponse.getReferenceNumber());
                sbmResponseVO.setRrn(orderStatusResponse.getReferenceNumber());
                sbmResponseVO.setPostDate(orderStatusResponse.getPostDate());
            }

        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(SBMPaymentGateway.class.getName(), "processSale()", null, "common", "Bank Conection issue while Placing transaction", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, e.getMessage(), e.getCause());
        }
        return sbmResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering processRefund in SBMPaymentGateway");
        System.setProperty("https.protocols", "TLSv1.2");
        System.setProperty("jsse.enableSNIExtension", "false");
        Gson gson = new Gson();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String propertyName = gatewayAccount.getFRAUD_FTP_USERNAME(); //propery file name
        if (!functions.isValueNull(propertyName))
            propertyName = "config.properties";
        transactionLogger.error("processRefund propertyName---->" + propertyName);

        Plugin plugin = Plugin.newInstance(RB2.getString("path") + propertyName);
        transactionLogger.debug("plugin --- " + RB2.getString("path") + propertyName);
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        SBMResponseVO sbmResponseVO = null;

        try
        {
            RefundRequest refundRequest = new RefundRequest();
            refundRequest.setOrderId(transactionDetailsVO.getPreviousTransactionId());
            refundRequest.setAmount(transactionDetailsVO.getAmount());
            String request = gson.toJson(refundRequest);
            transactionLogger.error("processRefund Refund Request 1 --for--" + trackingID + "--" + request);

            RefundResponse refundResponse = null;
            refundResponse = plugin.refund(refundRequest);
            String response = gson.toJson(refundResponse);
            transactionLogger.error("processRefund Refund Response 1 --for--" + trackingID + "--" + response);

            OrderStatusRequest orderStatusRequest = new OrderStatusRequest();
            orderStatusRequest.setLanguage("en");
            orderStatusRequest.setOrderId(transactionDetailsVO.getPreviousTransactionId());
            String OrderRequest = gson.toJson(orderStatusRequest);
            transactionLogger.error("processRefund Refund OrderRequest 1 --for--" + trackingID + "--" + OrderRequest);

            OrderStatusResponse orderStatusResponse = plugin.getOrderStatus(orderStatusRequest);
            String orderResponse = gson.toJson(orderStatusResponse);
            transactionLogger.error("processRefund Refund OrderResponse 1 --for--" + trackingID + "--" + orderResponse);

            sbmResponseVO = new SBMResponseVO();
            String status = "";
            String amount = "";
            if (orderStatusResponse.getOrderStatus() == 4 || orderStatusResponse.getActionCode().equals("0"))
            {
                status = "success";
            }
            else
            {
                status = "fail";
            }
            //orderStatusResponse.getAmount()
            if (functions.isValueNull(orderStatusResponse.getAmount()))
            {
                amount = orderStatusResponse.getAmount();
            }

            sbmResponseVO.setStatus(status);
            sbmResponseVO.setAmount(orderStatusResponse.getDepositAmount());
            sbmResponseVO.setTransactionId(orderStatusResponse.getOrderId());
            sbmResponseVO.setRemark(orderRegistrationDesc.get(orderStatusResponse.getOrderStatus()));
            sbmResponseVO.setErrorCode(orderStatusResponse.getActionCode());
            sbmResponseVO.setDescription(orderStatusResponse.getActionCodeDescription());
            sbmResponseVO.setResponseTime(dateFormat.format(date));
            sbmResponseVO.setTransactionType(PZProcessType.REFUND.toString());
            sbmResponseVO.setIpaddress(orderStatusResponse.getIp());

            //extra parameters from sbm other than common table
            sbmResponseVO.setEci(orderStatusResponse.getEci());
            sbmResponseVO.setCvvValid(orderStatusResponse.getCvvValidationResult());
            sbmResponseVO.setDepositAmount(orderStatusResponse.getDepositAmount());
            sbmResponseVO.setPaymentAuthCode(orderStatusResponse.getPaymentSystemAuthCode());
            sbmResponseVO.setProcessingAuthCode(String.valueOf(orderStatusResponse.getProcessingSystemAuthCode()));
            sbmResponseVO.setReferenceNumber(orderStatusResponse.getReferenceNumber());
            sbmResponseVO.setPostDate(orderStatusResponse.getPostDate());
        }
        catch (RequestProcessingException e)
        {
            transactionLogger.error("SBM Refund RequestProcessingException--->", e);
            sbmResponseVO = (SBMResponseVO) processExceptionRefund(trackingID, requestVO);
        }
        catch (UnexpectedResponseException e)
        {
            transactionLogger.error("SBM Refund UnexpectedResponseException--->", e);
            PZExceptionHandler.raiseTechnicalViolationException(SBMPaymentGateway.class.getName(), "processRefund()", null, "common", "Bank Conection issue while Refunding transaction", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, e.getMessage(), e.getCause());
        }

        return sbmResponseVO;
    }

    public GenericResponseVO processExceptionRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering processExceptionRefund in SBMPaymentGateway");
        System.setProperty("https.protocols", "TLSv1.2");
        System.setProperty("jsse.enableSNIExtension", "false");
        Gson gson = new Gson();
        String propertyName = "config.properties";
        transactionLogger.error("processExceptionRefund propertyName---->" + propertyName);

        Plugin plugin = Plugin.newInstance(RB2.getString("path") + propertyName);
        transactionLogger.debug("plugin --- " + RB2.getString("path") + propertyName);
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        SBMResponseVO sbmResponseVO = null;

        try
        {
            RefundRequest refundRequest = new RefundRequest();
            refundRequest.setOrderId(transactionDetailsVO.getPreviousTransactionId());
            refundRequest.setAmount(transactionDetailsVO.getAmount());
            String request = gson.toJson(refundRequest);
            transactionLogger.error("processExceptionRefund Refund Exception Request 1 --for--" + trackingID + "--" + request);

            RefundResponse refundResponse = null;
            refundResponse = plugin.refund(refundRequest);
            String response = gson.toJson(refundResponse);
            transactionLogger.error("processExceptionRefund Refund Exception Response 1 --for--" + trackingID + "--" + response);

            OrderStatusRequest orderStatusRequest = new OrderStatusRequest();
            orderStatusRequest.setLanguage("en");
            orderStatusRequest.setOrderId(transactionDetailsVO.getPreviousTransactionId());
            String OrderRequest = gson.toJson(orderStatusRequest);
            transactionLogger.error("processExceptionRefund Refund Exception OrderRequest 1 --for--" + trackingID + "--" + OrderRequest);

            OrderStatusResponse orderStatusResponse = plugin.getOrderStatus(orderStatusRequest);
            String orderResponse = gson.toJson(orderStatusResponse);
            transactionLogger.error("processExceptionRefund Refund Exception OrderResponse 1 --for--" + trackingID + "--" + orderResponse);

            sbmResponseVO = new SBMResponseVO();
            String status = "";
            String amount = "";
            if (orderStatusResponse.getOrderStatus() == 4 || orderStatusResponse.getActionCode().equals("0"))
            {
                status = "success";
            }
            else
            {
                status = "fail";
            }
            //orderStatusResponse.getAmount()
            if (functions.isValueNull(orderStatusResponse.getAmount()))
            {
                amount = orderStatusResponse.getAmount();
            }

            sbmResponseVO.setStatus(status);
            sbmResponseVO.setAmount(orderStatusResponse.getDepositAmount());
            sbmResponseVO.setTransactionId(orderStatusResponse.getOrderId());
            sbmResponseVO.setRemark(orderRegistrationDesc.get(orderStatusResponse.getOrderStatus()));
            sbmResponseVO.setErrorCode(orderStatusResponse.getActionCode());
            sbmResponseVO.setDescription(orderStatusResponse.getActionCodeDescription());
            sbmResponseVO.setResponseTime(dateFormat.format(date));
            sbmResponseVO.setTransactionType(PZProcessType.REFUND.toString());
            sbmResponseVO.setIpaddress(orderStatusResponse.getIp());

            //extra parameters from sbm other than common table
            sbmResponseVO.setEci(orderStatusResponse.getEci());
            sbmResponseVO.setCvvValid(orderStatusResponse.getCvvValidationResult());
            sbmResponseVO.setDepositAmount(orderStatusResponse.getDepositAmount());
            sbmResponseVO.setPaymentAuthCode(orderStatusResponse.getPaymentSystemAuthCode());
            sbmResponseVO.setProcessingAuthCode(String.valueOf(orderStatusResponse.getProcessingSystemAuthCode()));
            sbmResponseVO.setReferenceNumber(orderStatusResponse.getReferenceNumber());
            sbmResponseVO.setPostDate(orderStatusResponse.getPostDate());
        }
        catch (RequestProcessingException e)
        {
            transactionLogger.error("SBM Exception Refund  RequestProcessingException--for--" + trackingID + "-->", e);
            PZExceptionHandler.raiseTechnicalViolationException(SBMPaymentGateway.class.getName(), "processRefund()", null, "common", "Bank Conection issue while Refunding transaction", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (UnexpectedResponseException e)
        {
            transactionLogger.error("SBM Exception Refund UnexpectedResponseException--for--" + trackingID + "-->", e);
            PZExceptionHandler.raiseTechnicalViolationException(SBMPaymentGateway.class.getName(), "processRefund()", null, "common", "Bank Conection issue while Refunding transaction", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, e.getMessage(), e.getCause());
        }

        return sbmResponseVO;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering processQuery/Inquiry");
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String propertyName = gatewayAccount.getFRAUD_FTP_USERNAME(); //propery file name
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        if (!functions.isValueNull(propertyName))
            propertyName = "config.properties";
        transactionLogger.error("propertyName-----------" + propertyName);

        Plugin plugin = Plugin.newInstance(RB2.getString("path") + propertyName);
        transactionLogger.debug("plugin --- " + RB2.getString("path") + propertyName);
        System.setProperty("https.protocols", "TLSv1.2");
        System.setProperty("jsse.enableSNIExtension", "false");
        SBMResponseVO sbmResponseVO = new SBMResponseVO();
        Gson gson = new Gson();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        try
        {
            String previousTransactionId = transactionDetailsVO.getPreviousTransactionId();

            OrderStatusRequest orderStatusRequest = new OrderStatusRequest();
            orderStatusRequest.setLanguage("en");
            orderStatusRequest.setOrderId(trackingID);
            String request = gson.toJson(orderStatusRequest);
            transactionLogger.error("QueryRequest 1 --for--" + trackingID + "--" + request);

            OrderStatusResponse orderStatusResponse = plugin.getOrderStatus(orderStatusRequest);
            String response = gson.toJson(orderStatusResponse);
            transactionLogger.error("QueryResponse 1 --for--" + trackingID + "--" + response);

//            Response Order Status
//            0 - The order was registered successfully. A payment wasn't received yet.
//            1 - is not used anymore
//            2 - All the amount was successfully authorised
//            3 - The authorisation was cancelled
//            4 - The refund was made
//            5 - The transaction was sent to ACS for authorisation
//            6 - The authorisation was declined

            String responseStatusCode = String.valueOf(orderStatusResponse.getOrderStatus());
            String actionCode =orderStatusResponse.getActionCode();
            transactionLogger.error("actionCode--->"+actionCode);
            String status = "";
            if (responseStatusCode.equals("0"))
            {
                status = "pending";
                sbmResponseVO.setAmount(commTransactionDetailsVO.getAmount());
            }
            else if (responseStatusCode.equals("2"))
            {
                status = "success";
                sbmResponseVO.setAmount(commTransactionDetailsVO.getAmount());
            }
            else if (responseStatusCode.equals("3") || responseStatusCode.equals("6") || (responseStatusCode.equals("5") && actionCode.equalsIgnoreCase("-100")))
            {
                status = "fail";
                sbmResponseVO.setAmount(commTransactionDetailsVO.getAmount());
            }
            else if (responseStatusCode.equals("4"))
            {
                status = "reversed";
                if(orderStatusResponse.getDepositAmount().equals("0")){
                    sbmResponseVO.setAmount(commTransactionDetailsVO.getAmount());
                }else{
                    double partialamount = (Double.parseDouble(commTransactionDetailsVO.getAmount()))- (Double.parseDouble(orderStatusResponse.getDepositAmount()));
                    sbmResponseVO.setAmount(Double.toString(partialamount));
                }
            }

            sbmResponseVO.setStatus(status);
            sbmResponseVO.setTransactionStatus(status);
            sbmResponseVO.setTransactionId(orderStatusResponse.getOrderId());
            sbmResponseVO.setRemark(orderRegistrationDesc.get(orderStatusResponse.getOrderStatus()));
            sbmResponseVO.setErrorCode(orderStatusResponse.getActionCode());
            sbmResponseVO.setDescription(orderStatusResponse.getActionCodeDescription());
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            sbmResponseVO.setResponseTime(dateFormat.format(date));
            sbmResponseVO.setDescriptor(GATEWAY_TYPE);
            sbmResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
            sbmResponseVO.setIpaddress(orderStatusResponse.getIp());
            sbmResponseVO.setAuthCode(String.valueOf(orderStatusResponse.getOrderStatus()));
            sbmResponseVO.setMerchantId(gatewayAccount.getMerchantId());
            sbmResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());

            //extra parameters from sbm other than common table
            sbmResponseVO.setEci(orderStatusResponse.getEci());
            sbmResponseVO.setCvvValid(orderStatusResponse.getCvvValidationResult());
            sbmResponseVO.setDepositAmount(orderStatusResponse.getDepositAmount());
            sbmResponseVO.setPaymentAuthCode(orderStatusResponse.getPaymentSystemAuthCode());
            sbmResponseVO.setProcessingAuthCode(String.valueOf(orderStatusResponse.getProcessingSystemAuthCode()));
            sbmResponseVO.setReferenceNumber(orderStatusResponse.getReferenceNumber());
            sbmResponseVO.setPostDate(orderStatusResponse.getPostDate());
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(SBMPaymentGateway.class.getName(), "processQuery()", null, "common", "Bank Conection issue while Refunding transaction", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, e.getMessage(), e.getCause());
        }

        return sbmResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering processAuthentication in SBMPaymentGateway");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        Gson gson = new Gson();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String propertyName = gatewayAccount.getFRAUD_FTP_USERNAME(); //propery file name
        if (!functions.isValueNull(propertyName))
            propertyName = "config.properties";

        Plugin plugin = Plugin.newInstance(RB2.getString("path") + propertyName);
        transactionLogger.debug("plugin --- " + RB2.getString("path") + propertyName);


        // Plugin plugin =Plugin.newInstance(RB2.getString("path"));
        System.setProperty("https.protocols", "TLSv1.2");
        System.setProperty("jsse.enableSNIExtension", "false");
        SBMResponseVO sbmResponseVO = new SBMResponseVO();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        String termUrl = "";
        transactionLogger.error("HOST_URL ---" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB2.getString("HOST_URL");
            transactionLogger.error("From HOST_URL----" + termUrl);
        }
        else
        {
            termUrl = RB2.getString("TERM_URL");
            transactionLogger.error("From RB TERM_URL ----" + termUrl);
        }

        try
        {
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setAmount(transDetailsVO.getAmount());
            registerRequest.setCurrency(currency);
            registerRequest.setLanguage("en");
            registerRequest.setMerchantOrderNumber(trackingID);
            registerRequest.setReturnUrl(termUrl + trackingID);
            registerRequest.setDescription(transDetailsVO.getOrderDesc());
            String request = gson.toJson(registerRequest);
            transactionLogger.error("Register Auth Request 1 -----" + request);

            RegisterResponse registerResponse = null;
            registerResponse = plugin.registerPreAuthRequest(registerRequest);
            String response = gson.toJson(registerResponse);
            transactionLogger.debug("Register Auth Respose 1----" + response);


            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setOrderId(registerResponse.getOrderId());
            paymentRequest.setPan(cardDetailsVO.getCardNum());
            paymentRequest.setCvc(cardDetailsVO.getcVV());
            paymentRequest.setExpiration(cardDetailsVO.getExpYear() + cardDetailsVO.getExpMonth());
            paymentRequest.setCardholder(addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname());
            paymentRequest.setLanguage("en");
            String paymentrequest = gson.toJson(paymentRequest);

            PaymentRequest paymentRequestLog = new PaymentRequest();
            paymentRequestLog.setOrderId(registerResponse.getOrderId());
            paymentRequestLog.setPan(functions.maskingPan(cardDetailsVO.getCardNum()));
            paymentRequestLog.setCvc(functions.maskingNumber(cardDetailsVO.getcVV()));
            paymentRequestLog.setExpiration(functions.maskingNumber(cardDetailsVO.getExpYear() + cardDetailsVO.getExpMonth()));
            paymentRequestLog.setCardholder(addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname());
            paymentRequestLog.setLanguage("en");
            String paymentrequestLog = gson.toJson(paymentRequest);
            transactionLogger.debug("processAuthentication PaymentRequest Auth Request 1--for--" + trackingID + "--" + paymentrequestLog);

            PaymentResponse paymentResponse = plugin.authorizePayment(paymentRequest);
            String paymentresponse = gson.toJson(paymentResponse);
            transactionLogger.debug("processAuthentication PaymentResponse Auth Response 1--for--" + trackingID + "--" + paymentresponse);

            OrderStatusRequest orderStatusRequest = new OrderStatusRequest();
            orderStatusRequest.setLanguage("en");
            orderStatusRequest.setOrderId(registerResponse.getOrderId());
            String OrderStatusrequest = gson.toJson(orderStatusRequest);
            transactionLogger.debug("processAuthentication OrderStatusRequest Auth request 1--for--" + trackingID + "--" + OrderStatusrequest);

            OrderStatusResponse orderStatusResponse = plugin.getOrderStatus(orderStatusRequest);
            String OrderStatusres = gson.toJson(orderStatusResponse);
            transactionLogger.debug("processAuthentication OrderStatusResponse Auth Response 1--for--" + trackingID + "--" + OrderStatusres);

            String status = "";
            if (orderStatusResponse.getOrderStatus() == 1 && orderStatusResponse.getActionCode().equals("0"))
            {
                status = "success";
            }
            else
            {
                status = "fail";
            }

            sbmResponseVO.setStatus(status);
            sbmResponseVO.setAmount(orderStatusResponse.getDepositAmount());
            sbmResponseVO.setTransactionId(orderStatusResponse.getOrderId());
            sbmResponseVO.setRemark(orderRegistrationDesc.get(orderStatusResponse.getOrderStatus()));
            sbmResponseVO.setErrorCode(orderStatusResponse.getActionCode());
            sbmResponseVO.setDescription(orderStatusResponse.getActionCodeDescription());
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            sbmResponseVO.setResponseTime(dateFormat.format(date));
            sbmResponseVO.setDescriptor(GATEWAY_TYPE);
            sbmResponseVO.setTransactionType(PZProcessType.AUTHORIZATION.toString());
            sbmResponseVO.setIpaddress(orderStatusResponse.getIp());

            //extra parameters from sbm other than common table
            sbmResponseVO.setEci(orderStatusResponse.getEci());
            sbmResponseVO.setCvvValid(orderStatusResponse.getCvvValidationResult());
            sbmResponseVO.setDepositAmount(orderStatusResponse.getDepositAmount());
            sbmResponseVO.setPaymentAuthCode(orderStatusResponse.getPaymentSystemAuthCode());
            sbmResponseVO.setAuthCode(String.valueOf(orderStatusResponse.getPaymentSystemAuthCode()));
            sbmResponseVO.setProcessingAuthCode(String.valueOf(orderStatusResponse.getProcessingSystemAuthCode()));
            sbmResponseVO.setErrorCode(String.valueOf(orderStatusResponse.getProcessingSystemAuthCode()));
            sbmResponseVO.setReferenceNumber(orderStatusResponse.getReferenceNumber());
            sbmResponseVO.setRrn(orderStatusResponse.getReferenceNumber());
            sbmResponseVO.setPostDate(orderStatusResponse.getPostDate());
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(SBMPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Bank Conection issue while Refunding transaction", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, e.getMessage(), e.getCause());
        }

        return sbmResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering processVoid in SBMPaymentGateway");
        System.setProperty("https.protocols", "TLSv1.2");
        System.setProperty("jsse.enableSNIExtension", "false");
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String propertyName = gatewayAccount.getFRAUD_FTP_USERNAME(); //propery file name
        if (!functions.isValueNull(propertyName))
            propertyName = "config.properties";

        Plugin plugin = Plugin.newInstance(RB2.getString("path") + propertyName);
        transactionLogger.debug("plugin --- " + RB2.getString("path") + propertyName);
        Gson gson = new Gson();
        SBMResponseVO sbmResponseVO = new SBMResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        try
        {
            ReversalRequest reversalRequest = new ReversalRequest();
            reversalRequest.setOrderId(transDetailsVO.getPreviousTransactionId());
            String ReverseReq = gson.toJson(reversalRequest);
            transactionLogger.debug("Cancel Reversal Request 1 --for--" + trackingID + "--" + ReverseReq);

            ReversalResponse reversalResponse = null;
            reversalResponse = plugin.reverse(reversalRequest);
            String ReverseRes = gson.toJson(reversalResponse);
            transactionLogger.debug("Cancel Reversal Response 1 --for--" + trackingID + "--" + ReverseRes);

            OrderStatusRequest orderStatusRequest = new OrderStatusRequest();
            orderStatusRequest.setLanguage("en");
            orderStatusRequest.setOrderId(transDetailsVO.getPreviousTransactionId());
            String OrderStatusReq = gson.toJson(orderStatusRequest);
            transactionLogger.debug("Cancel Order Status Req 1 --for--" + trackingID + "--" + OrderStatusReq);

            OrderStatusResponse orderStatusResponse = plugin.getOrderStatus(orderStatusRequest);
            String OrderStatusRes = gson.toJson(orderStatusResponse);
            transactionLogger.debug("Cancel Order Status Res 1 --for--" + trackingID + "--" + OrderStatusRes);

            String status = "";
            if (orderStatusResponse.getOrderStatus() == 3 || orderStatusResponse.getActionCode().equals("0"))
            {
                status = "success";
            }
            else
            {
                status = "fail";
            }

            sbmResponseVO.setStatus(status);
            sbmResponseVO.setAmount(orderStatusResponse.getDepositAmount());
            sbmResponseVO.setTransactionId(orderStatusResponse.getOrderId());
            sbmResponseVO.setRemark(orderRegistrationDesc.get(orderStatusResponse.getOrderStatus()));
            sbmResponseVO.setErrorCode(orderStatusResponse.getActionCode());
            sbmResponseVO.setDescription(orderStatusResponse.getActionCodeDescription());
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            sbmResponseVO.setResponseTime(dateFormat.format(date));
            sbmResponseVO.setDescriptor(GATEWAY_TYPE);
            sbmResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
            sbmResponseVO.setIpaddress(orderStatusResponse.getIp());

            //extra parameters from sbm other than common table
            sbmResponseVO.setEci(orderStatusResponse.getEci());
            sbmResponseVO.setCvvValid(orderStatusResponse.getCvvValidationResult());
            sbmResponseVO.setDepositAmount(orderStatusResponse.getDepositAmount());
            sbmResponseVO.setPaymentAuthCode(orderStatusResponse.getPaymentSystemAuthCode());
            sbmResponseVO.setProcessingAuthCode(String.valueOf(orderStatusResponse.getProcessingSystemAuthCode()));
            sbmResponseVO.setReferenceNumber(orderStatusResponse.getReferenceNumber());
            sbmResponseVO.setPostDate(orderStatusResponse.getPostDate());
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(SBMPaymentGateway.class.getName(), "processVoid()", null, "common", "Bank Conection issue while Refunding transaction", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, e.getMessage(), e.getCause());
        }

        return sbmResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering processCapture in SBMPaymentGateway");
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String propertyName = gatewayAccount.getFRAUD_FTP_USERNAME(); //propery file name
        if (!functions.isValueNull(propertyName))
            propertyName = "config.properties";

        Plugin plugin = Plugin.newInstance(RB2.getString("path") + propertyName);
        transactionLogger.debug("plugin --- " + RB2.getString("path") + propertyName);
        System.setProperty("https.protocols", "TLSv1.2");
        System.setProperty("jsse.enableSNIExtension", "false");
        SBMResponseVO sbmResponseVO = new SBMResponseVO();
        Gson gson = new Gson();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();

        try
        {
            CompletionRequest completionRequest = new CompletionRequest();
            completionRequest.setAmount(transDetailsVO.getAmount());
            completionRequest.setOrderId(transDetailsVO.getPreviousTransactionId());
            String compRequest = gson.toJson(completionRequest);
            transactionLogger.debug("processCapture completionRequest 1 --for--" + trackingID + "--" + compRequest);

            CompletionResponse completionResponse = null;
            completionResponse = plugin.complete(completionRequest);
            String compResponse = gson.toJson(completionResponse);
            transactionLogger.debug("processCapture completionResponse 1 --for--" + trackingID + "--" + compResponse);

            OrderStatusRequest orderStatusRequest = new OrderStatusRequest();
            orderStatusRequest.setLanguage("en");
            orderStatusRequest.setOrderId(transDetailsVO.getPreviousTransactionId());
            String OrderRes = gson.toJson(completionResponse);
            transactionLogger.debug("processCapture OrderRequest 1 --for--" + trackingID + "--" + OrderRes);

            OrderStatusResponse orderStatusResponse = plugin.getOrderStatus(orderStatusRequest);
            String orderStatusRes = gson.toJson(completionResponse);
            transactionLogger.debug("processCapture OrderResponse 1 --for--" + trackingID + "--" + orderStatusRes);

            String status = "";
            if (orderStatusResponse.getOrderStatus() == 0 || orderStatusResponse.getActionCode().equals("0"))
            {
                status = "success";
            }
            else
            {
                status = "fail";
            }

            sbmResponseVO.setStatus(status);
            sbmResponseVO.setAmount(orderStatusResponse.getDepositAmount());
            sbmResponseVO.setTransactionId(orderStatusResponse.getOrderId());
            sbmResponseVO.setRemark(orderRegistrationDesc.get(orderStatusResponse.getOrderStatus()));
            sbmResponseVO.setErrorCode(orderStatusResponse.getActionCode());
            sbmResponseVO.setDescription(orderStatusResponse.getActionCodeDescription());
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            sbmResponseVO.setResponseTime(dateFormat.format(date));
            sbmResponseVO.setDescriptor(GATEWAY_TYPE);
            sbmResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
            sbmResponseVO.setIpaddress(orderStatusResponse.getIp());

            //extra parameters from sbm other than common table
            sbmResponseVO.setEci(orderStatusResponse.getEci());
            sbmResponseVO.setCvvValid(orderStatusResponse.getCvvValidationResult());
            sbmResponseVO.setDepositAmount(orderStatusResponse.getDepositAmount());
            sbmResponseVO.setPaymentAuthCode(orderStatusResponse.getPaymentSystemAuthCode());
            sbmResponseVO.setProcessingAuthCode(String.valueOf(orderStatusResponse.getProcessingSystemAuthCode()));
            sbmResponseVO.setReferenceNumber(orderStatusResponse.getReferenceNumber());
            sbmResponseVO.setPostDate(orderStatusResponse.getPostDate());
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(SBMPaymentGateway.class.getName(), "processCapture()", null, "common", "Bank Conection issue while Refunding transaction", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, e.getMessage(), e.getCause());
        }
        return sbmResponseVO;
    }

    //extra feature of sbm
    public GenericResponseVO processCaptureVoid(String trackingid, GenericRequestVO commRequestVO) throws PZTechnicalViolationException
    {
        return processVoid(trackingid, commRequestVO);
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("SBMPaymentGateway", "processRebilling()", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    //todo sbm integration commented out till here for logger issue


}
