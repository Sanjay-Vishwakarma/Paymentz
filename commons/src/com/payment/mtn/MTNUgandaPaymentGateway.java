package com.payment.mtn;

import com.directi.pg.Base64;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.dao.TransactionDAO;
import com.payment.Enum.PZProcessType;
import com.payment.airtel.AirtelUgandaUtils;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.safexpay.AsyncSafexPayPayoutQueryService;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ResourceBundle;
import java.util.UUID;

/**
 * Created by admin on 08-Mar-22.
 */
public class MTNUgandaPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "mtnuganda";
    private static TransactionLogger transactionLogger = new TransactionLogger(MTNUgandaPaymentGateway.class.getName());
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.MtnUganda");

    public MTNUgandaPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering into processSale of MTNPaymentGateway =====");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Functions functions = new Functions();
        TransactionDAO transactionDAO = new TransactionDAO();

        try
        {
            String authorization = getAuthtoken("collection");
            String callbackURL = RB.getString("NOTIFY_URL") + trackingID;
            String referenceId = UUID.randomUUID().toString();
            String targetEnvironment = gatewayAccount.getCHARGEBACK_FTP_PATH();
            String key = gatewayAccount.getFRAUD_FTP_SITE();
            String amount = transDetailsVO.getAmount();
            String currency = transDetailsVO.getCurrency();
            String externalId = trackingID;
            String partyIdType = "MSISDN";
            String telnocc = addressDetailsVO.getTelnocc();
            String msisdn = telnocc + addressDetailsVO.getPhone();
            String payerMessage = trackingID;
            String payeeNote = trackingID;

            transactionDAO.updateUUID(referenceId, trackingID);

/*            boolean referenceIdExists = true;

            while(referenceIdExists)
            {
                referenceId = UUID.randomUUID().toString();
                db call
                if()
                {
                    referenceIdExists = false;
                }
            }*/


            String saleURL = "";

            boolean isTest = gatewayAccount.isTest();

            if (isTest)
            {
                saleURL = RB.getString("TEST_COLLECTION_PAYMENT_URL");
            }
            else
            {
                saleURL = RB.getString("LIVE_COLLECTION_PAYMENT_URL");
            }


            transactionLogger.error("saleURL ===== " + saleURL);
            transactionLogger.error("referenceId ===== " + referenceId);

/*            transactionLogger.error("callbackURL ===== " + callbackURL);
            transactionLogger.error("targetEnvironment ===== " + targetEnvironment);
            transactionLogger.error("key ===== " + key);
            transactionLogger.error("amount ===== " + amount);
            transactionLogger.error("msisdn ===== " + msisdn);
            transactionLogger.error("telnocc ===== " + telnocc);
            transactionLogger.error("authorization ===== " + authorization);
            transactionLogger.error("currency ===== " + currency);*/

            JSONObject saleRequest = new JSONObject();
            JSONObject payer = new JSONObject();

            payer.put("partyIdType", partyIdType);
            payer.put("partyId", msisdn);

            saleRequest.put("amount", amount);
            saleRequest.put("currency", currency);
            saleRequest.put("externalId", externalId);
            saleRequest.put("payer", payer);
            saleRequest.put("payeeNote", payeeNote);
            saleRequest.put("payerMessage", payerMessage);

            transactionLogger.error("MTN Sale request---- " + trackingID + " -- " + saleRequest.toString());

            String response = MtnUgandaUtils.doPostHTTPSURLConnectionClient(saleURL, saleRequest.toString(), authorization, referenceId, targetEnvironment, key, callbackURL);

            transactionLogger.error("MTN Sale response ---- " + trackingID + " -- " + response);

            String CALL_EXECUTE_AFTER = RB.getString("CALL_EXECUTE_AFTER");
            String CALL_EXECUTE_INTERVAL = RB.getString("CALL_EXECUTE_INTERVAL");
            String MAX_EXECUTE_SEC = RB.getString("MAX_EXECUTE_SEC");
            String isThreadAllowed = RB.getString("THREAD_CALL");

            transactionLogger.error("isThreadAllowed ------->" + isThreadAllowed);
            transactionLogger.error("CALL_EXECUTE_AFTER ------->" + CALL_EXECUTE_AFTER);
            transactionLogger.error("CALL_EXECUTE_INTERVAL ------->" + CALL_EXECUTE_INTERVAL);
            transactionLogger.error("MAX_EXECUTE_SEC ------->" + MAX_EXECUTE_SEC);

            if("Y".equalsIgnoreCase(isThreadAllowed))
            {
                new AsyncMtNUgandaQueryService(trackingID,accountId,"",CALL_EXECUTE_AFTER,CALL_EXECUTE_INTERVAL,MAX_EXECUTE_SEC);
            }

            if (functions.isJSONValid(response))
            {
                String code = "";
                String message = "";

                JSONObject responseJsonObject = new JSONObject(response);

                if(responseJsonObject.has("message") && functions.isValueNull(responseJsonObject.getString("message")))
                    message = responseJsonObject.getString("message");

                if(responseJsonObject.has("code") && functions.isValueNull(responseJsonObject.getString("code")))
                    code = responseJsonObject.getString("code");

                if(functions.isValueNull(code) && functions.isValueNull(message))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(code);
                }

                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setTransactionStatus("pending");
                }
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("Transaction is pending");
                commResponseVO.setDescription("Transaction is pending");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException =====" , e);
        }

        return commResponseVO;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering into processQuery of MTNPaymentGateway =====");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();
        String dbStatus = commTransactionDetailsVO.getPrevTransactionStatus();

        try
        {
            String inquiryURL = "";
            String targetEnvironment = gatewayAccount.getCHARGEBACK_FTP_PATH();
            String key = gatewayAccount.getFRAUD_FTP_SITE();
            String referenceId = commTransactionDetailsVO.getSessionId();
            String authorization = getAuthtoken("collection");

            boolean isTest = gatewayAccount.isTest();

            if("reversed".equalsIgnoreCase(dbStatus) || "markedforreversal".equalsIgnoreCase(dbStatus))
            {
                if (isTest)
                {
                    inquiryURL = RB.getString("TEST_REFUND_INQUIRY_URL") + referenceId;
                }
                else
                {
                    inquiryURL = RB.getString("LIVE_REFUND_INQUIRY_URL") + referenceId;
                }
            }

            else
            {
                if (isTest)
                {
                    inquiryURL = RB.getString("TEST_INQUIRY_URL") + referenceId;
                }
                else
                {
                    inquiryURL = RB.getString("LIVE_INQUIRY_URL") + referenceId;
                }
            }
            transactionLogger.error("inquiryURL ===== " + inquiryURL);
            transactionLogger.error("dbStatus ===== " + dbStatus);
            transactionLogger.error("referenceId ===== " + referenceId);

            /* transactionLogger.error("targetEnvironment ===== " + targetEnvironment);
            transactionLogger.error("key ===== " + key);
            transactionLogger.error("authorization ===== " + authorization);
            transactionLogger.error("isTest ===== " + isTest);*/

            transactionLogger.error("MTN Inquiry request--for-- " + trackingID + " -- " + inquiryURL);

            String response = MtnUgandaUtils.doGetHTTPSURLConnectionClient(inquiryURL, targetEnvironment, key, authorization);

            transactionLogger.error("MTN Inquiry response--for-- " + trackingID +  "-- " + response);

            if (functions.isJSONValid(response))
            {
                JSONObject responseJson = new JSONObject(response);

                String financialTransactionId = "";
                String status = "";
                String amount = "";
                String currency = "";
                String code = "";
                String message = "";


                if(responseJson.has("reason") && responseJson.get("reason") != null && functions.isJSONValid(responseJson.get("reason").toString()))
                {
                    JSONObject reasonJson = responseJson.getJSONObject("reason");

                    if(reasonJson.has("code") && functions.isValueNull(reasonJson.getString("code")))
                        code = reasonJson.getString("code");

                    if(reasonJson.has("message") && functions.isValueNull(reasonJson.getString("message")))
                        message = reasonJson.getString("message");
                }
                else if(responseJson.has("reason") && functions.isValueNull(responseJson.getString("reason")))
                {
                    message = responseJson.getString("reason");
                }

                if(responseJson.has("code") && functions.isValueNull(responseJson.getString("code")))
                {
                    code = responseJson.getString("code");
                }

                if(responseJson.has("message") && functions.isValueNull(responseJson.getString("message")))
                {
                    message = responseJson.getString("message");
                }


                if (responseJson.has("financialTransactionId") && functions.isValueNull(responseJson.getString("financialTransactionId")))
                {
                    financialTransactionId = responseJson.getString("financialTransactionId");
                }

                if (responseJson.has("amount") && functions.isValueNull(responseJson.getString("amount")))
                {
                    amount = responseJson.getString("amount");
                }

                if (responseJson.has("currency") && functions.isValueNull(responseJson.getString("currency")))
                {
                    currency = responseJson.getString("currency");
                }

                if (responseJson.has("status") && functions.isValueNull(responseJson.getString("status")))
                {
                    status = responseJson.getString("status");

                    if(!functions.isValueNull(message))
                        message = status;
                }

                transactionLogger.error("financialTransactionId =====" + financialTransactionId);
                transactionLogger.error("amount =====" + amount);
                transactionLogger.error("status =====" + status);
                transactionLogger.error("currency =====" + currency);
                transactionLogger.error("code =====" + code);
                transactionLogger.error("message =====" + message);

                if("SUCCESSFUL".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("Success");
                    commResponseVO.setTransactionStatus("Success");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(code);
                    commResponseVO.setTransactionId(financialTransactionId);
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setAmount(amount);
                }
                else if("FAILED".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(code);
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setTransactionId(financialTransactionId);
                }

                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(code);
                    commResponseVO.setAmount(amount);
                }
            }

            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setTransactionStatus("pending");
            }

        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException =====", e);
        }

        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processRefund of MTNPaymentGateway =====");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Functions functions = new Functions();

        try
        {
            String authorization = getAuthtoken("collection");
            String callbackURL = RB.getString("NOTIFY_URL") + trackingID;
            String referenceId = UUID.randomUUID().toString();
            String targetEnvironment = gatewayAccount.getCHARGEBACK_FTP_PATH();
            String key = gatewayAccount.getFRAUD_FTP_SITE();
            String amount = transDetailsVO.getAmount();
            String currency = transDetailsVO.getCurrency();
            String externalId = trackingID;
            String partyIdType = "MSISDN";
            String telnocc = addressDetailsVO.getTelnocc();
            String msisdn = telnocc + addressDetailsVO.getPhone();
            String payerMessage = trackingID;
            String payeeNote = trackingID;

            String refundURL = "";

            boolean isTest = gatewayAccount.isTest();

            if (isTest)
            {
                refundURL = RB.getString("TEST_REFUND_URL");
            }
            else
            {
                refundURL = RB.getString("LIVE_REFUND_URL");
            }

            transactionLogger.error("referenceId ===== " + referenceId);
            transactionLogger.error("refundURL ===== " + refundURL);

/*            transactionLogger.error("callbackURL ===== " + callbackURL);
            transactionLogger.error("targetEnvironment ===== " + targetEnvironment);
            transactionLogger.error("key ===== " + key);
            transactionLogger.error("amount ===== " + amount);
            transactionLogger.error("msisdn ===== " + msisdn);
            transactionLogger.error("telnocc ===== " + telnocc);
            transactionLogger.error("authorization ===== " + authorization);
            transactionLogger.error("currency ===== " + currency);*/

            JSONObject saleRequest = new JSONObject();
            JSONObject payer = new JSONObject();

            payer.put("partyIdType", partyIdType);
            payer.put("partyId", msisdn);

            saleRequest.put("amount", amount);
            saleRequest.put("currency", currency);
            saleRequest.put("externalId", externalId);
            saleRequest.put("payer", payer);
            saleRequest.put("payeeNote", payeeNote);
            saleRequest.put("payerMessage", payerMessage);

            transactionLogger.error("MTN Refund request---- " + trackingID + " -- " + saleRequest.toString());

            String response = MtnUgandaUtils.doPostHTTPSURLConnectionClient(refundURL, saleRequest.toString(), authorization, referenceId, targetEnvironment, key, callbackURL);

            transactionLogger.error("MTN Refund response ---- " + trackingID + " -- " + response);



            String CALL_EXECUTE_AFTER = RB.getString("REFUND_INQUIRY_CALL_EXECUTE_AFTER");
            String CALL_EXECUTE_INTERVAL = RB.getString("REFUND_INQUIRY_CALL_EXECUTE_INTERVAL");
            String MAX_EXECUTE_SEC = RB.getString("REFUND_INQUIRY_MAX_EXECUTE_SEC");
            String isThreadAllowed = RB.getString("REFUND_THREAD_CALL");

            transactionLogger.error("isThreadAllowed ------->" + isThreadAllowed);
            transactionLogger.error("CALL_EXECUTE_AFTER ------->" + CALL_EXECUTE_AFTER);
            transactionLogger.error("CALL_EXECUTE_INTERVAL ------->" + CALL_EXECUTE_INTERVAL);
            transactionLogger.error("MAX_EXECUTE_SEC ------->" + MAX_EXECUTE_SEC);

            if("Y".equalsIgnoreCase(isThreadAllowed))
            {
                new AsyncMTNUgandaRefundQueryService(trackingID,accountId,"",CALL_EXECUTE_AFTER,CALL_EXECUTE_INTERVAL,MAX_EXECUTE_SEC);
            }


            if (functions.isJSONValid(response))
            {

                String code = "";
                String message = "";

                JSONObject responseJsonObject = new JSONObject(response);

                if(responseJsonObject.has("message") && functions.isValueNull(responseJsonObject.getString("message")))
                    message = responseJsonObject.getString("message");

                if(responseJsonObject.has("code") && functions.isValueNull(responseJsonObject.getString("code")))
                    code = responseJsonObject.getString("code");

                if(functions.isValueNull(code) && functions.isValueNull(message))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(code);
                }

                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setTransactionStatus("pending");
                }
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("Transaction is pending");
                commResponseVO.setDescription("Transaction is pending");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException =====" , e);
        }

        return commResponseVO;
    }

    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering into processPayout of MTNPaymentGateway =====");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Functions functions = new Functions();
        TransactionDAO transactionDAO = new TransactionDAO();

        try
        {
            String authorization = getAuthtoken("payout");
            String callbackURL = RB.getString("NOTIFY_URL") + trackingID;
            String referenceId = UUID.randomUUID().toString();
            String targetEnvironment = gatewayAccount.getCHARGEBACK_FTP_PATH();
            String key = gatewayAccount.getFRAUD_FTP_PATH();
            String amount = transDetailsVO.getAmount();
            String currency = transDetailsVO.getCurrency();
            String externalId = trackingID;
            String partyIdType = "MSISDN";
            String telnocc = addressDetailsVO.getTelnocc();
            String msisdn = addressDetailsVO.getPhone();
            String payerMessage = trackingID;
            String payeeNote = trackingID;

            String saleURL = "";

//            transactionDAO.updateUUID(referenceId, trackingID);
            MtnUgandaUtils.updatePayoutTransaction(trackingID, telnocc, msisdn, referenceId);

            msisdn = telnocc + addressDetailsVO.getPhone();

            boolean isTest = gatewayAccount.isTest();

            if (isTest)
            {
                saleURL = RB.getString("TEST_PAYOUT_URL");
            }
            else
            {
                saleURL = RB.getString("LIVE_PAYOUT_URL");
            }

            transactionLogger.error("referenceId ===== " + referenceId);
            transactionLogger.error("payoutURL ===== " + saleURL);

/*            transactionLogger.error("callbackURL ===== " + callbackURL);
            transactionLogger.error("targetEnvironment ===== " + targetEnvironment);
            transactionLogger.error("key ===== " + key);
            transactionLogger.error("amount ===== " + amount);
            transactionLogger.error("msisdn ===== " + msisdn);
            transactionLogger.error("telnocc ===== " + telnocc);
            transactionLogger.error("authorization ===== " + authorization);
            transactionLogger.error("currency ===== " + currency);*/

            JSONObject saleRequest = new JSONObject();
            JSONObject payee = new JSONObject();

            payee.put("partyIdType", partyIdType);
            payee.put("partyId", msisdn);

            saleRequest.put("amount", amount);
            saleRequest.put("currency", currency);
            saleRequest.put("externalId", externalId);
            saleRequest.put("payee", payee);
            saleRequest.put("payeeNote", payeeNote);
            saleRequest.put("payerMessage", payerMessage);

            transactionLogger.error("MTN Payout request---- " + trackingID + " -- " + saleRequest.toString());

            String response = MtnUgandaUtils.doPostHTTPSURLConnectionClient(saleURL, saleRequest.toString(), authorization, referenceId, targetEnvironment, key, callbackURL);

            transactionLogger.error("MTN Payout response ---- " + trackingID + " -- " + response);

            String CALL_EXECUTE_AFTER=RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_AFTER");
            String CALL_EXECUTE_INTERVAL=RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_INTERVAL");
            String MAX_EXECUTE_SEC=RB.getString("PAYOUT_INQUIRY_MAX_EXECUTE_SEC");
            String isThreadAllowed=RB.getString("PAYOUT_THREAD_CALL");

            transactionLogger.error("isThreadAllowed ------->"+isThreadAllowed);

            if("Y".equalsIgnoreCase(isThreadAllowed))
            {
                transactionLogger.error("isThreadAllowed ------->"+isThreadAllowed);
                new AsyncMTNUgandaPayoutQueryService(trackingID,accountId,"",CALL_EXECUTE_AFTER,CALL_EXECUTE_INTERVAL,MAX_EXECUTE_SEC);
            }

            if (functions.isJSONValid(response))
            {

                String code = "";
                String message = "";

                JSONObject responseJsonObject = new JSONObject(response);

                if(responseJsonObject.has("message") && functions.isValueNull(responseJsonObject.getString("message")))
                    message = responseJsonObject.getString("message");

                if(responseJsonObject.has("code") && functions.isValueNull(responseJsonObject.getString("code")))
                    code = responseJsonObject.getString("code");

                if(functions.isValueNull(code) && functions.isValueNull(message))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(code);
                }

                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setTransactionStatus("pending");
                }
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("Transaction is pending");
                commResponseVO.setDescription("Transaction is pending");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException =====" , e);
        }

        return commResponseVO;
    }

    public GenericResponseVO processPayoutInquiry(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering into processPayoutInquiry of MTNPaymentGateway =====");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Functions functions = new Functions();

        try
        {
            String inquiryURL = "";
            String targetEnvironment = gatewayAccount.getCHARGEBACK_FTP_PATH();
            String key = gatewayAccount.getFRAUD_FTP_PATH();
            String referenceId = commTransactionDetailsVO.getSessionId();

            boolean isTest = gatewayAccount.isTest();
            String authorization = getAuthtoken("payout");

            if (isTest)
            {
                inquiryURL = RB.getString("TEST_PAYOUT_INQUIRY_URL") + referenceId;
            }
            else
            {
                inquiryURL = RB.getString("LIVE_PAYOUT_INQUIRY_URL") + referenceId;
            }

            transactionLogger.error("payout inquiryURL ===== " + inquiryURL);
            transactionLogger.error("referenceId ===== " + referenceId);

          /*  transactionLogger.error("targetEnvironment ===== " + targetEnvironment);
            transactionLogger.error("key ===== " + key);
            transactionLogger.error("authorization ===== " + authorization);*/

            transactionLogger.error("MTN Payout Inquiry request--for-- " + trackingID + " -- " + inquiryURL);

            String response = MtnUgandaUtils.doGetHTTPSURLConnectionClient(inquiryURL, targetEnvironment, key, authorization);

            transactionLogger.error("MTN Payout Inquiry response--for-- " + trackingID +  "-- " + response);

            if (functions.isJSONValid(response))
            {
                JSONObject responseJson = new JSONObject(response);

                String financialTransactionId = "";
                String status = "";
                String amount = "";
                String currency = "";
                String code = "";
                String message = "";


                if(responseJson.has("reason") && responseJson.get("reason") != null && functions.isJSONValid(responseJson.get("reason").toString()))
                {
                    JSONObject reasonJson = responseJson.getJSONObject("reason");

                    if(reasonJson.has("code") && functions.isValueNull(reasonJson.getString("code")))
                        code = reasonJson.getString("code");

                    else if(responseJson.has("statusCode") && functions.isValueNull(responseJson.getString("statusCode")))
                    {
                        code = responseJson.getString("statusCode");
                    }

                    if(reasonJson.has("message") && functions.isValueNull(reasonJson.getString("message")))
                        message = reasonJson.getString("message");
                }
                else if(responseJson.has("reason") && functions.isValueNull(responseJson.getString("reason")))
                {
                    message = responseJson.getString("reason");
                }

                if(responseJson.has("code") && functions.isValueNull(responseJson.getString("code")))
                {
                    code = responseJson.getString("code");
                }
                else if(responseJson.has("statusCode") && functions.isValueNull(responseJson.getString("statusCode")))
                {
                    code = responseJson.getString("statusCode");
                }


                if(responseJson.has("message") && functions.isValueNull(responseJson.getString("message")))
                {
                    message = responseJson.getString("message");
                }


                if (responseJson.has("financialTransactionId") && functions.isValueNull(responseJson.getString("financialTransactionId")))
                {
                    financialTransactionId = responseJson.getString("financialTransactionId");
                }

                if (responseJson.has("amount") && functions.isValueNull(responseJson.getString("amount")))
                {
                    amount = responseJson.getString("amount");
                }

                if (responseJson.has("currency") && functions.isValueNull(responseJson.getString("currency")))
                {
                    currency = responseJson.getString("currency");
                }

                if (responseJson.has("status") && functions.isValueNull(responseJson.getString("status")))
                {
                    status = responseJson.getString("status");

                    if(!functions.isValueNull(message))
                        message = status;
                }

                transactionLogger.error("financialTransactionId =====" + financialTransactionId);
                transactionLogger.error("amount =====" + amount);
                transactionLogger.error("status =====" + status);
                transactionLogger.error("currency =====" + currency);
                transactionLogger.error("code =====" + code);
                transactionLogger.error("message =====" + message);

                if("SUCCESSFUL".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(code);
                    commResponseVO.setTransactionId(financialTransactionId);
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
                    commResponseVO.setAmount(amount);
                }
                else if("FAILED".equalsIgnoreCase(status) || functions.isValueNull(code))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(code);
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
                    commResponseVO.setAmount(amount);
                    commResponseVO.setTransactionId(financialTransactionId);
                }

                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(code);
                }
            }

            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setTransactionStatus("pending");
            }

        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException =====", e);
        }

        return commResponseVO;
    }

    private String getAuthtoken(String operation) throws PZTechnicalViolationException, JSONException
    {
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Functions functions = new Functions();
        String userId = "";
        String key = "";
        String subscriptionKey = "";
        if(functions.isValueNull(operation) && "collection".equals(operation))
            subscriptionKey = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_SITE();

        else if (functions.isValueNull(operation) && "payout".equals(operation))
            subscriptionKey = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH();

        String authTokenURL = "";
        String authToken = "";

        boolean isTest = gatewayAccount.isTest();

        if ("collection".equals(operation))
        {
            userId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
            key = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        }

        else if("payout".equals(operation))
        {
            userId = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FILE_SHORT_NAME();
            key = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        }

        if (isTest && "collection".equals(operation))
        {
            authTokenURL = RB.getString("TEST_AUTHTOKEN_URL");
        }
        else if (!isTest && "collection".equals(operation))
        {
            authTokenURL = RB.getString("LIVE_AUTHTOKEN_URL");
        }

        else if(isTest && "payout".equals(operation))
        {
            authTokenURL = RB.getString("TEST_PAYOUT_AUTHTOKEN_URL");
        }
        else if (!isTest && "payout".equals(operation))
        {
            authTokenURL = RB.getString("LIVE_PAYOUT_AUTHTOKEN_URL");
        }

        String plainCredentials = userId + ":" + key;
        String base64Credentials = new String(Base64.encode(plainCredentials.getBytes()));

        transactionLogger.error("inside getAuthtoken ===== ");
        transactionLogger.error("authTokenURL ===== " + authTokenURL);

/*        transactionLogger.error("userId ===== " + userId);
        transactionLogger.error("key ===== " + key);
        transactionLogger.error("base64Credentials ===== " + base64Credentials);
        transactionLogger.error("subscriptionKey ===== " + subscriptionKey);*/

        String response =  MtnUgandaUtils.doPostHTTPSURLConnectionAuthToken(authTokenURL, "Basic " + base64Credentials, subscriptionKey);

        if(functions.isValueNull(response) && functions.isJSONValid(response))
        {
            JSONObject responseJson = new JSONObject(response);

            if(responseJson.has("access_token") && functions.isValueNull(responseJson.getString("access_token")))
                authToken = responseJson.getString("access_token");
        }

        return authToken;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
