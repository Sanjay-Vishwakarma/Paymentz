package com.payment.airtel;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;

import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.dao.PaymentDAO;
import com.manager.dao.TransactionDAO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

/**
 * Created by admin on 28-Feb-22.
 */
public class AirtelUgandaPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "airteluganda";
    private static TransactionLogger transactionLogger = new TransactionLogger(AirtelUgandaPaymentGateway.class.getName());
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.airteluganda");

    public AirtelUgandaPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering into processSale of AirtelMoneyPaymentGateway =====");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Functions functions = new Functions();
        PaymentDAO paymentDAO = new PaymentDAO();

        try
        {
            String username = gatewayAccount.getFRAUD_FTP_USERNAME();
            String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
            String amount = transDetailsVO.getAmount();
            String msisdn = addressDetailsVO.getPhone();
            String reference = transactionDetailsVO.getOrderDesc();
            String country = commAddressDetailsVO.getCountry();
            String currency = transDetailsVO.getCurrency();
            String saleURL = "";
            String authToken = getAuthtoken();
            boolean isTest = gatewayAccount.isTest();

            if(functions.isValueNull(country) && country.length() == 3)
            {
                country = AirtelUgandaUtils.getCountryCodeHash(country);
            }

            if (isTest)
            {
                saleURL = RB.getString("TEST_COLLECTION_PAYMENT_URL");
            }
            else
            {
                saleURL = RB.getString("LIVE_COLLECTION_PAYMENT_URL");
            }

//            transactionLogger.error("username ===== " + username);
//            transactionLogger.error("password ===== " + password);
//            transactionLogger.error("amount ===== " + amount);
//            transactionLogger.error("msisdn ===== " + msisdn);
            transactionLogger.error("saleURL ===== " + saleURL);
            transactionLogger.error("country ===== " + country);
//            transactionLogger.error("reference ===== " + reference);
//            transactionLogger.error("authToken ===== " + authToken);
            transactionLogger.error("currency ===== " + currency);

            JSONObject saleRequest = new JSONObject();
            JSONObject subscriber = new JSONObject();
            JSONObject transaction = new JSONObject();

            subscriber.put("msisdn", msisdn);

            transaction.put("amount", amount);
            transaction.put("id", trackingID);

            saleRequest.put("reference", trackingID);
            saleRequest.put("subscriber", subscriber);
            saleRequest.put("transaction", transaction);

            transactionLogger.error("AirtelUganda Sale request---- " + trackingID + " -- " + saleRequest.toString());

            String response = AirtelUgandaUtils.doPostHTTPSURLConnectionClient(saleURL, saleRequest.toString(), country, currency, authToken);

            transactionLogger.error("AirtelUganda Sale response ---- " + trackingID + " -- " + response);

                if (functions.isJSONValid(response))
                {

                    JSONObject responseJsonObject = new JSONObject(response);

                    if(responseJsonObject.has("status"))
                    {
                        String message = "";
                        String code = "";
                        String responseCode = "";
                        String success = "";

                        JSONObject responseStatusObject = responseJsonObject.getJSONObject("status");

                        if(responseStatusObject.has("code") && functions.isValueNull(responseStatusObject.getString("code")))
                            code = responseStatusObject.getString("code");

                        if(responseStatusObject.has("response_code") && functions.isValueNull(responseStatusObject.getString("response_code")))
                            responseCode = responseStatusObject.getString("response_code");

                        if(responseStatusObject.has("success") && functions.isValueNull(responseStatusObject.getString("success")))
                            success = responseStatusObject.getString("success");

                        if(responseStatusObject.has("message") && functions.isValueNull(responseStatusObject.getString("message")))
                            message = responseStatusObject.getString("message");


                        transactionLogger.error("code ===== " + code);
                        transactionLogger.error("message ===== " + message);
                        transactionLogger.error("responseCode ===== " + responseCode);
                        transactionLogger.error("success ===== " + success);

                        if("DP00800001001".equalsIgnoreCase(responseCode))
                        {
                            commResponseVO.setDescription(message);
                            commResponseVO.setStatus("success");
                            commResponseVO.setRemark(message);
                            commResponseVO.setAmount(amount);
                            commResponseVO.setErrorCode(responseCode);
                            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        }
                        else if(!"DP00800001006".equalsIgnoreCase(responseCode) || "false".equalsIgnoreCase(success))
                        {
                            transactionLogger.error("transaction failed ==== +++++");
                            commResponseVO.setDescription(message);
                            commResponseVO.setStatus("fail");
                            commResponseVO.setErrorCode(responseCode);
                            commResponseVO.setRemark(message);
                        }
                        else
                        {
                            transactionLogger.error("pending ==== +++++");

                            commResponseVO.setStatus("pending");
                            commResponseVO.setTransactionStatus("pending");
                            commResponseVO.setRemark("Transaction is pending");
                            commResponseVO.setDescription("Transaction is pending");
                            commResponseVO.setErrorCode(responseCode);
                            AirtelUgandaUtils.updateTransaction(trackingID, responseCode);
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
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark("Transaction is pending");
                    commResponseVO.setDescription("Transaction is pending");
                }
            paymentDAO.updatePaymentTransactionModeforCommon("Non-3D", trackingID);
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException =====" , e);
        }

        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processRefund of AirtelMoney =====");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

//        TransactionDAO transactionDAO = new TransactionDAO();
//        TransactionDetailsVO transactionDetailsVO = transactionDAO.getDetailFromCommon(trackingID);

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        try
        {
            String country = commAddressDetailsVO.getCountry();
            String currency = commTransactionDetailsVO.getCurrency();
            String refundURL = "";
            String authToken = getAuthtoken();

            if (functions.isValueNull(country) && country.length() == 3)
            {
                country = AirtelUgandaUtils.getCountryCodeHash(country);
            }

            boolean isTest = gatewayAccount.isTest();

            if (isTest)
            {
                refundURL = RB.getString("TEST_REFUND_URL");
            }
            else
            {
                refundURL = RB.getString("LIVE_REFUND_URL");
            }

            transactionLogger.error("refundURL ===== " + refundURL);
            transactionLogger.error("country ===== " + country);
            transactionLogger.error("currency ===== " + currency);
//            transactionLogger.error("authToken ===== " + authToken);

            JSONObject request = new JSONObject();

            request.put("airtel_money_id", trackingID);

            transactionLogger.error("AirtelUganda Refund request ==== " + trackingID + " ===== " + request);

            String response = AirtelUgandaUtils.doPostHTTPSURLConnectionClient(refundURL, request.toString(), country, currency, authToken);


            transactionLogger.error("AirtelUganda Refund response ==== " + trackingID + " ===== " + response);

            if(functions.isJSONValid(response))
            {
                JSONObject responseJson = new JSONObject();

                String airtelMoneyId = "";
                String status = "";
                if (responseJson.has("data"))
                {

                    JSONObject dataJson = responseJson.getJSONObject("data");

                    if (dataJson.has("transaction"))
                    {
                        JSONObject transactionJson = dataJson.getJSONObject("transaction");

                        if (transactionJson.has("airtel_money_id") && functions.isValueNull(transactionJson.getString("airtel_money_id")))
                            airtelMoneyId = transactionJson.getString("airtel_money_id");

                        if (transactionJson.has("status") && functions.isValueNull(transactionJson.getString("status")))
                            status = transactionJson.getString("status");
                    }
                }

                if(responseJson.has("status"))
                {
                    String message = "";
                    String code = "";
                    String responseCode = "";
                    String success = "";
                    JSONObject responseStatusObject = responseJson.getJSONObject("status");

                    if(responseStatusObject.has("code") && functions.isValueNull(responseStatusObject.getString("code")))
                        code = responseStatusObject.getString("code");

                    if(responseStatusObject.has("message") && functions.isValueNull(responseStatusObject.getString("message")))
                        message = responseStatusObject.getString("message");

                    if(responseStatusObject.has("result_code") && functions.isValueNull(responseStatusObject.getString("result_code")))
                        responseCode = responseStatusObject.getString("result_code");

                    if(responseStatusObject.has("success") && functions.isValueNull(responseStatusObject.getString("success")))
                        success = responseStatusObject.getString("success");


                    transactionLogger.error("code ===== " + code);
                    transactionLogger.error("responseCode ===== " + responseCode);
                    transactionLogger.error("message ===== " + message);
                    transactionLogger.error("success ===== " + success);


                    if(functions.isValueNull(success) && "true".equals(success))
                    {
                        commResponseVO.setStatus("Success");
                        commResponseVO.setTransactionStatus("Success");
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription(message);
                        commResponseVO.setErrorCode(responseCode);
                        commResponseVO.setTransactionId(airtelMoneyId);
                    }

                    else //if(functions.isValueNull(responseCode) && "FF".equalsIgnoreCase(responseCode))
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionStatus("failed");
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription(message);
                        commResponseVO.setErrorCode(responseCode);
                    }

//                    else
//                    {
//                        commResponseVO.setStatus("pending");
//                        commResponseVO.setTransactionStatus("pending");
//                        commResponseVO.setRemark("Transaction is pending");
//                        commResponseVO.setDescription("Transaction is pending");
//                        commResponseVO.setErrorCode(responseCode);
//                    }
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
            transactionLogger.error("JSONException =====", e);
        }

        return comm3DResponseVO;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering into processQuery of AirtelMoneyPaymentGateway =====");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();

        try
        {
            String country = commAddressDetailsVO.getCountry();
            String currency = commTransactionDetailsVO.getCurrency();
            String authToken = getAuthtoken();
            String inquiryURL = "";


            if (functions.isValueNull(country) && country.length() == 3)
            {
                country = AirtelUgandaUtils.getCountryCodeHash(country);
            }

            boolean isTest = gatewayAccount.isTest();

            if (isTest)
            {
                inquiryURL = RB.getString("TEST_INQUIRY_URL") + trackingID;
            }
            else
            {
                inquiryURL = RB.getString("LIVE_INQUIRY_URL") + trackingID;
            }

            transactionLogger.error("inquiryURL ===== " + inquiryURL);
            transactionLogger.error("country ===== " + country);
            transactionLogger.error("currency ===== " + currency);
//            transactionLogger.error("authToken ===== " + authToken);


            transactionLogger.error("AirtelUganda Inquiry request--for-- " + trackingID + " -- " + inquiryURL);

            String response = AirtelUgandaUtils.doGetHTTPSURLConnectionClient(inquiryURL, country, currency, authToken);

            transactionLogger.error("AirtelUganda Inquiry response--for-- " + trackingID +  "-- " + response);

            if (functions.isJSONValid(response))
            {
                String airtelMoneyId = "";
                String id = "";
                String status = "";
                String resultCode = "";
                String message = "";
                String success = "";

                JSONObject responseJson = new JSONObject(response);

                if (responseJson.has("data"))
                {
                    JSONObject dataJson = responseJson.getJSONObject("data");

                    if (dataJson.has("transaction"))
                    {
                        JSONObject transactionJson = dataJson.getJSONObject("transaction");

                        if (transactionJson.has("airtel_money_id") && functions.isValueNull(transactionJson.getString("airtel_money_id")))
                            airtelMoneyId = transactionJson.getString("airtel_money_id");

                        if (transactionJson.has("id") && functions.isValueNull(transactionJson.getString("id")))
                            id = transactionJson.getString("id");

                        if (transactionJson.has("status") && functions.isValueNull(transactionJson.getString("status")))
                            status = transactionJson.getString("status");

                        if (transactionJson.has("message") && functions.isValueNull(transactionJson.getString("message")))
                            message = transactionJson.getString("message");
                    }
                }

                transactionLogger.error("transaction message =====" + message);

                if (responseJson.has("status"))
                {
                    JSONObject statusJson = responseJson.getJSONObject("status");

                    if (statusJson.has("message") && functions.isValueNull(statusJson.getString("message")))
                    {
                        if(!functions.isValueNull(message))
                            message = statusJson.getString("message");
                    }

                    if (statusJson.has("response_code") && functions.isValueNull(statusJson.getString("response_code")))
                        resultCode = statusJson.getString("response_code");

                    if (statusJson.has("success") && functions.isValueNull(statusJson.getString("success")))
                        success = statusJson.getString("success");

                }

                transactionLogger.error("airtelMoneyId =====" + airtelMoneyId);
                transactionLogger.error("id =====" + id);
                transactionLogger.error("status =====" + status);
                transactionLogger.error("success =====" + success);
                transactionLogger.error("resultCode =====" + resultCode);
                transactionLogger.error("status message =====" + message);

                    if("TS".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("Success");
                        commResponseVO.setTransactionStatus("Success");
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription(message);
                        commResponseVO.setErrorCode(resultCode);
//                        commResponseVO.setTransactionId(id);
                        commResponseVO.setTransactionId(airtelMoneyId);
                        commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                        commResponseVO.setAuthCode(resultCode);
                        commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
                        commResponseVO.setCurrency(currency);

                    }
                    else if("TF".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionStatus("failed");
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription(message);
                        commResponseVO.setErrorCode(resultCode);
//                        commResponseVO.setTransactionId(id);
                        commResponseVO.setTransactionId(airtelMoneyId);
                        commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                        commResponseVO.setAuthCode(resultCode);
                        commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
                        commResponseVO.setCurrency(currency);
                    }

                    else
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setTransactionStatus("pending");
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription(message);
                        commResponseVO.setErrorCode(resultCode);
                        commResponseVO.setAuthCode(resultCode);
                        commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
                        commResponseVO.setCurrency(currency);
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

    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("inside processPayout of AirtelMoney ===== ");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Functions functions = new Functions();

        try
        {
            String country = commAddressDetailsVO.getCountry();
            String currency = commTransactionDetailsVO.getCurrency();
            String authToken = getAuthtoken();
            String msisdn = addressDetailsVO.getPhone();
            String payoutURL = "";
            String pin = gatewayAccount.getFRAUD_FTP_PATH();
            String publicKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
            String amount = commTransactionDetailsVO.getAmount();
            pin = AirtelUgandaUtils.encrypt(pin, publicKey);
            String telnocc = "";

            if(functions.isValueNull(addressDetailsVO.getTelnocc()))
             telnocc = addressDetailsVO.getTelnocc();

            if (functions.isValueNull(country) && country.length() == 3)
            {
                country = AirtelUgandaUtils.getCountryCodeHash(country);
            }

            boolean isTest = gatewayAccount.isTest();

            if (isTest)
            {
                payoutURL = RB.getString("TEST_PAYOUT_URL");
            }
            else
            {
                payoutURL = RB.getString("LIVE_PAYOUT_URL");
            }

            transactionLogger.error("payoutURL ===== " + payoutURL);
//            transactionLogger.error("pin ===== " + pin);
//            transactionLogger.error("publicKey ===== " + publicKey);
            transactionLogger.error("country ===== " + country);
            transactionLogger.error("currency ===== " + currency);
//            transactionLogger.error("authToken ===== " + authToken);
//            transactionLogger.error("msisdn ===== " + msisdn);
//            transactionLogger.error("amount ===== " + amount);
//            transactionLogger.error("telnocc ===== " + telnocc);

            AirtelUgandaUtils.updatePayoutTransaction(trackingID, telnocc, msisdn, country);

            JSONObject request = new JSONObject();
            JSONObject payee = new JSONObject();
            JSONObject transaction = new JSONObject();

            payee.put("msisdn", msisdn);

            transaction.put("amount", amount);
            transaction.put("id", trackingID);

            request.put("reference", trackingID);
            request.put("pin", pin);
            request.put("payee", payee);
            request.put("transaction", transaction);

            transactionLogger.error("AirtelUganda Payout request ===== " + trackingID + " === " + request);

            String response = AirtelUgandaUtils.doPostHTTPSURLConnectionClient(payoutURL, request.toString(), country, currency, authToken);

            transactionLogger.error("AirtelUganda Payout response ---- " + trackingID + " -- " + response);

            if (functions.isJSONValid(response))
            {
                String airtelMoneyId = "";
                String referenceId = "";
                String status = "";
                String resultCode = "";
                String message = "";
                String success = "";

                JSONObject responseJson = new JSONObject(response);

                if(responseJson.has("status_message"))
                {
                    message = responseJson.getString("status_message");
                }

                if (responseJson.has("data"))
                {
                    JSONObject dataJson = responseJson.getJSONObject("data");

                    if (dataJson.has("transaction"))
                    {
                        JSONObject transactionJson = dataJson.getJSONObject("transaction");

                        if (transactionJson.has("airtel_money_id") && functions.isValueNull(transactionJson.getString("airtel_money_id")))
                            airtelMoneyId = transactionJson.getString("airtel_money_id");

                        if (transactionJson.has("reference_id") && functions.isValueNull(transactionJson.getString("reference_id")))
                            referenceId = transactionJson.getString("reference_id");

                    }
                }

                if (responseJson.has("status"))
                {
                    JSONObject statusJson = responseJson.getJSONObject("status");

                    if (statusJson.has("message") && functions.isValueNull(statusJson.getString("message")))
                        message = statusJson.getString("message");

                    if (statusJson.has("response_code") && functions.isValueNull(statusJson.getString("response_code")))
                        resultCode = statusJson.getString("response_code");

                    if (statusJson.has("success") && functions.isValueNull(statusJson.getString("success")))
                        success = statusJson.getString("success");
                }

                transactionLogger.error("airtelMoneyId =====" + airtelMoneyId);
                transactionLogger.error("referenceId =====" + referenceId);
                transactionLogger.error("success =====" + success);
                transactionLogger.error("resultCode =====" + resultCode);
                transactionLogger.error("message =====" + message);

                if("DP00900001001".equalsIgnoreCase(resultCode))
                {
                    commResponseVO.setStatus("Success");
                    commResponseVO.setTransactionStatus("Success");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(resultCode);
                    commResponseVO.setTransactionId(referenceId);
                    commResponseVO.setResponseHashInfo(airtelMoneyId);
                    commResponseVO.setTransactionType(PZProcessType.PAYOUT.toString());

                }
                else //if("TF".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(resultCode);
                    commResponseVO.setTransactionId(referenceId);
                    commResponseVO.setResponseHashInfo(airtelMoneyId);
                    commResponseVO.setTransactionType(PZProcessType.PAYOUT.toString());
                }

//                else
//                {
//                    commResponseVO.setStatus("pending");
//                    commResponseVO.setTransactionStatus("pending");
//                    commResponseVO.setRemark(message);
//                    commResponseVO.setDescription(message);
//                    commResponseVO.setErrorCode(resultCode);
//                }
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
        catch (IllegalBlockSizeException e)
        {
            transactionLogger.error("IllegalBlockSizeException =====", e);
        }
        catch (InvalidKeyException e)
        {
            transactionLogger.error("InvalidKeyException =====", e);
        }
        catch (NoSuchPaddingException e)
        {
            transactionLogger.error("NoSuchPaddingException =====", e);
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException =====", e);
        }
        catch (BadPaddingException e)
        {
            transactionLogger.error("BadPaddingException =====", e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processPayoutInquiry(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("In processPayoutInquiry of AirtelMoneyPaymentGateway =====");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();

        try
        {
            String country = commAddressDetailsVO.getCountry();
            String currency = commTransactionDetailsVO.getCurrency();
            String authToken = getAuthtoken();
            String payoutInquiryURL = "";


            if (functions.isValueNull(country) && country.length() == 3)
            {
                country = AirtelUgandaUtils.getCountryCodeHash(country);
            }

            boolean isTest = gatewayAccount.isTest();

            if (isTest)
            {
                payoutInquiryURL = RB.getString("TEST_PAYOUT_INQUIRY_URL") + trackingID;
            }
            else
            {
                payoutInquiryURL = RB.getString("LIVE_PAYOUT_INQUIRY_URL") + trackingID;
            }

            transactionLogger.error("payoutInquiryURL ===== " + payoutInquiryURL);
            transactionLogger.error("country ===== " + country);
            transactionLogger.error("currency ===== " + currency);
//            transactionLogger.error("authToken ===== " + authToken);


            transactionLogger.error("AirtelUganda Payout Inquiry request--for-- " + trackingID + " -- " + payoutInquiryURL);

            String response = AirtelUgandaUtils.doGetHTTPSURLConnectionClient(payoutInquiryURL, country, currency, authToken);

            transactionLogger.error("AirtelUganda Payout Inquiry response--for-- " + trackingID +  "-- " + response);

            if (functions.isJSONValid(response))
            {
                String id = "";
                String status = "";
                String resultCode = "";
                String message = "";
                String success = "";

                JSONObject responseJson = new JSONObject(response);

                if (responseJson.has("data"))
                {
                    JSONObject dataJson = responseJson.getJSONObject("data");

                    if (dataJson.has("transaction"))
                    {
                        JSONObject transactionJson = dataJson.getJSONObject("transaction");

                        if (transactionJson.has("id") && functions.isValueNull(transactionJson.getString("id")))
                            id = transactionJson.getString("id");

                        if (transactionJson.has("status") && functions.isValueNull(transactionJson.getString("status")))
                            status = transactionJson.getString("status");
                    }
                }

                if (responseJson.has("status"))
                {
                    JSONObject statusJson = responseJson.getJSONObject("status");

                    if (statusJson.has("message") && functions.isValueNull(statusJson.getString("message")))
                        message = statusJson.getString("message");

                    if (statusJson.has("response_code") && functions.isValueNull(statusJson.getString("response_code")))
                        resultCode = statusJson.getString("response_code");

                    if (statusJson.has("success") && functions.isValueNull(statusJson.getString("success")))
                        success = statusJson.getString("success");

                }

                transactionLogger.error("id =====" + id);
                transactionLogger.error("status =====" + status);
                transactionLogger.error("success =====" + success);
                transactionLogger.error("resultCode =====" + resultCode);
                transactionLogger.error("message =====" + message);

                if("TS".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("Success");
                    commResponseVO.setTransactionStatus("Success");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(resultCode);
                    commResponseVO.setTransactionId(id);
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    commResponseVO.setAuthCode(resultCode);
                    commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
                    commResponseVO.setCurrency(currency);

                }
                else if("TF".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(resultCode);
                    commResponseVO.setTransactionId(id);
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    commResponseVO.setAuthCode(resultCode);
                    commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
                    commResponseVO.setCurrency(currency);
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(resultCode);
                    commResponseVO.setAuthCode(resultCode);
                    commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
                    commResponseVO.setCurrency(currency);
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

    private String getAuthtoken() throws PZTechnicalViolationException, JSONException
    {
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String clientId = gatewayAccount.getMerchantId();
        String clientSecret = gatewayAccount.getFRAUD_FTP_SITE();
        String grantType = gatewayAccount.getFRAUD_FILE_SHORT_NAME();
        String authTokenURL = "";

        boolean isTest = gatewayAccount.isTest();

        if (isTest)
        {
            authTokenURL = RB.getString("TEST_AUTHTOKEN_URL");
        }
        else
        {
            authTokenURL = RB.getString("LIVE_AUTHTOKEN_URL");
        }

        transactionLogger.error("inside getAuthtoken ===== ");
//        transactionLogger.error("clientId ===== " + clientId);
//        transactionLogger.error("clientSecret ===== " + clientSecret);
//        transactionLogger.error("grantType ===== " + grantType);
//        transactionLogger.error("authTokenURL ===== " + authTokenURL);

        return AirtelUgandaUtils.getAuthToken(authTokenURL, clientId, clientSecret, grantType);
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
