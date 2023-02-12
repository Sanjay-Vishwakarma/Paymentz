package com.payment.boltpay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;

import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.codehaus.jettison.json.JSONException;
//import org.codehaus.jettison.json.JsonObject;
import com.google.gson.JsonObject;
import org.codehaus.jettison.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

/**
 * Created by admin on 17-Nov-21.
 */
public class BoltPayPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "boltpay";
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.boltpay");

    private static TransactionLogger transactionLogger = new TransactionLogger(BoltPayPaymentGateway.class.getName());

    public BoltPayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processSale of Boltpay -----");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String merchant_token = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String key = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        boolean isTest = gatewayAccount.isTest();
        String is3dSupported= gatewayAccount.get_3DSupportAccount();
        String saleUrl = "";

        transactionLogger.error("isTest in processSale  ===== " + isTest);
        transactionLogger.error("merchant_token in processSale  ===== " + merchant_token);
        transactionLogger.error("key in processSale  ===== " + key);

        String amount = transDetailsVO.getAmount();
        String currency = transDetailsVO.getCurrency();
        String customer_name = addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname();
        String customer_email = addressDetailsVO.getEmail();
        String customer_phone = addressDetailsVO.getPhone();
        String cardnumber = cardDetailsVO.getCardNum();
        String expiry_month = cardDetailsVO.getExpMonth();
        String expiry_year = cardDetailsVO.getExpYear();
        String cvv = cardDetailsVO.getcVV();
        String txref = trackingID;
        String narration = transDetailsVO.getOrderDesc();
        String notifyURL = "";

        if (isTest)
        {
            saleUrl = RB.getString("TEST_SALE_URL");
        }
        else
        {
            saleUrl = RB.getString("LIVE_SALE_URL");
        }

        transactionLogger.error("saleUrl in processSale  ==== " + saleUrl);

        notifyURL = RB.getString("NOTIFY_URL") + trackingID;
        transactionLogger.error("notifyURL in processSale  ----" + notifyURL);
        transactionLogger.error("is3dSupported  ----" + is3dSupported);

        JsonObject jsonRequest  = new JsonObject();
        JsonObject jsonRequestLog  = new JsonObject();
        try
        {
            jsonRequest.addProperty("merchant_token", merchant_token);
            jsonRequest.addProperty("customer_email", BoltPayUtils.harden(customer_email, key));
            jsonRequest.addProperty("customer_phone", BoltPayUtils.harden(customer_phone, key));
            jsonRequest.addProperty("customer_name", BoltPayUtils.harden(customer_name, key));
            jsonRequest.addProperty("amount", BoltPayUtils.harden(amount, key));
            jsonRequest.addProperty("currency", BoltPayUtils.harden(currency, key));
            jsonRequest.addProperty("cardnumber", BoltPayUtils.harden(cardnumber, key));
            jsonRequest.addProperty("cvv", BoltPayUtils.harden(cvv, key));
            jsonRequest.addProperty("expiry_month", BoltPayUtils.harden(expiry_month, key));
            jsonRequest.addProperty("expiry_year", BoltPayUtils.harden(expiry_year, key));
            jsonRequest.addProperty("narration", BoltPayUtils.harden(narration, key));
            jsonRequest.addProperty("txref", BoltPayUtils.harden(txref, key));

            jsonRequestLog.addProperty("merchant_token", merchant_token);
            jsonRequestLog.addProperty("customer_email", customer_email);
            jsonRequestLog.addProperty("customer_phone", customer_phone);
            jsonRequestLog.addProperty("customer_name", functions.maskingFirstName(addressDetailsVO.getFirstname()) + " " + functions.maskingLastName(addressDetailsVO.getLastname()));
            jsonRequestLog.addProperty("amount", amount);
            jsonRequestLog.addProperty("currency", currency);
            jsonRequestLog.addProperty("cardnumber", functions.maskingPan(cardnumber));
            jsonRequestLog.addProperty("cvv", functions.maskingNumber(cvv));
            jsonRequestLog.addProperty("expiry_month", functions.maskingNumber(expiry_month));
            jsonRequestLog.addProperty("expiry_year", functions.maskingNumber(expiry_year));
            jsonRequestLog.addProperty("narration", narration);
            jsonRequestLog.addProperty("txref", txref);

            if(is3dSupported.equalsIgnoreCase("Y"))
            {
                jsonRequest.addProperty("responseurl", BoltPayUtils.harden(notifyURL, key));
                jsonRequestLog.addProperty("responseurl", notifyURL);
            }

            transactionLogger.error("BoltPay Sale UnEncryptedRequest ===== " + trackingID + " ===== " + jsonRequestLog);
            transactionLogger.error("BoltPay Sale EncryptedRequest ===== " + trackingID + " ===== " + jsonRequest);

            String response = BoltPayUtils.doPostHTTPSURLConnectionClient(saleUrl, jsonRequest.toString());
            transactionLogger.error("BoltPay Sale Response ===== " + trackingID + " ===== " + response);

            JSONObject jsonResponse = null;
            String responseCode = "";
            String responseMessage = "";
            String responseHtml = "";
            String paymentReference = "";
            String redirectURL = "";

            if(functions.isValueNull(response) && response.contains("{"))
            {
                jsonResponse = new JSONObject(response);

                if(jsonResponse.has("data") && jsonResponse.getJSONObject("data") != null)
                {
                    JSONObject jsonData =  jsonResponse.getJSONObject("data");

                    if(jsonData.has("responsecode") && functions.isValueNull(jsonData.getString("responsecode")))
                    {
                        responseCode = jsonData.getString("responsecode");
                    }

                    if(jsonData.has("responsehtml") && functions.isValueNull(jsonData.getString("responsehtml")))
                    {
                        responseHtml = jsonData.getString("responsehtml");
                    }

                    if(jsonData.has("responsemessage") && functions.isValueNull(jsonData.getString("responsemessage")))
                    {
                        responseMessage = jsonData.getString("responsemessage");
                    }

                    if(jsonData.has("payment_reference") && functions.isValueNull(jsonData.getString("payment_reference")))
                    {
                        paymentReference = jsonData.getString("payment_reference");
                    }

                    if(jsonData.has("responsehtml") && functions.isValueNull(jsonData.getString("responsehtml")))
                    {
                        redirectURL = jsonData.getString("responsehtml");
                    }

                    transactionLogger.error("responseCode ===== " + responseCode);
                    transactionLogger.error("responseMessage ===== " + responseMessage);
                    transactionLogger.error("responseHtml ===== " + responseHtml);
                    transactionLogger.error("paymentReference ===== " + paymentReference);
                    transactionLogger.error("redirectURL ===== " + redirectURL);
                }


                if(functions.isValueNull(responseCode) && "00".equalsIgnoreCase(responseCode))
                {
                    commResponseVO.setStatus("Success");
                    commResponseVO.setTransactionStatus("Success");
                    commResponseVO.setRemark(responseMessage);
                    commResponseVO.setDescription(responseMessage);
                    commResponseVO.setErrorCode(responseCode);
                    commResponseVO.setTransactionId(paymentReference);
                }

                else if(functions.isValueNull(responseCode) && "03".equalsIgnoreCase(responseCode))
                {
                    commResponseVO.setStatus("pending3DConfirmation");
                    commResponseVO.setUrlFor3DRedirect(redirectURL);
                    commResponseVO.setRedirectUrl(redirectURL);
                    commResponseVO.setTransactionId(paymentReference);
                    BoltPayUtils.updateTransaction(trackingID, responseCode);
                }

                else if(functions.isValueNull(responseCode) && ("RR".equalsIgnoreCase(responseCode) || "EE".equalsIgnoreCase(responseCode) ||  "FF".equalsIgnoreCase(responseCode)))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(responseMessage);
                    commResponseVO.setDescription(responseMessage);
                    commResponseVO.setErrorCode(responseCode);
                    commResponseVO.setTransactionId(paymentReference);
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark("Transaction is pending");
                    commResponseVO.setDescription("Transaction is pending");
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
            transactionLogger.error("JSONException ===== ", e);
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException ===== ", e);
        }
        catch (InvalidKeyException e)
        {
            transactionLogger.error("InvalidKeyException ===== ", e);
        }
        catch (NoSuchPaddingException e)
        {
            transactionLogger.error("NoSuchPaddingException ===== ", e);
        }
        catch (BadPaddingException e)
        {
            transactionLogger.error("BadPaddingException ===== ", e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException ===== ", e);
        }
        catch (IllegalBlockSizeException e)
        {
            transactionLogger.error("IllegalBlockSizeException ===== ", e);
        }

        return commResponseVO;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Inside processQuery of Boltpay ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String sessionCode = commTransactionDetailsVO.getSessionCode();
        String sessionId = commTransactionDetailsVO.getSessionId();
        String transactionMode = commTransactionDetailsVO.getTransactionmode();

        boolean isTest = gatewayAccount.isTest();
        String is3dSupported= gatewayAccount.get_3DSupportAccount();
        String merchantToken = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String inquiryUrl = "";
        String reference = commRequestVO.getTransDetailsVO().getPreviousTransactionId();
        String key = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();

        transactionLogger.error("sessionCode in processQuery ===== " + sessionCode);
        transactionLogger.error("sessionId in processQuery ===== " + sessionId);
        transactionLogger.error("reference in processQuery ===== " + reference);
        transactionLogger.error("merchantToken in processQuery ===== " + merchantToken);
        transactionLogger.error("transactionMode in processQuery ===== " + transactionMode);

        if(is3dSupported.equalsIgnoreCase("Y") && !"Non-3D".equals(transactionMode))
        {
            if (isTest)
            {
                inquiryUrl = RB.getString("TEST_3DINQUIRY_URL");
            }
            else
            {
                inquiryUrl = RB.getString("LIVE_3DINQUIRY_URL");
            }
        }

        else
        {
            if (isTest)
            {
                inquiryUrl = RB.getString("TEST_INQUIRY_URL");
            }
            else
            {
                inquiryUrl = RB.getString("LIVE_INQUIRY_URL");
            }
        }

        transactionLogger.error("inquiryUrl in processQuery ===== " + inquiryUrl);

        JSONObject jsonRequest  = new JSONObject();
        JSONObject jsonRequestLog  = new JSONObject();

        try
        {
            jsonRequest.put("merchant_token", merchantToken);
            jsonRequest.put("reference", BoltPayUtils.harden(reference, key));

            jsonRequestLog.put("merchant_token", merchantToken);
            jsonRequestLog.put("reference", reference);

            if(is3dSupported.equalsIgnoreCase("Y") && functions.isValueNull(transactionMode) && !"Non-3D".equals(transactionMode))
            {
                jsonRequest.put("sessionid", BoltPayUtils.harden(sessionId, key));
                jsonRequest.put("sessioncode", BoltPayUtils.harden(sessionCode, key));

                jsonRequestLog.put("sessionid", sessionId);
                jsonRequestLog.put("sessioncode", sessionCode);
            }


            transactionLogger.error("Boltpay Inquiry Request ===== " + trackingID + "----" + jsonRequestLog);

            String response = BoltPayUtils.doPostHTTPSURLConnectionClient(inquiryUrl, jsonRequest.toString());
            transactionLogger.error("Boltpay Inquiry Response ===== " + trackingID + "----" + response);

            JSONObject jsonResponse = null;
            String paymentReference = "";
            String responseCode = "";
            String responseMessage = "";
            String transactionDate = "";
            String amount = "";
            String currency = "";

            if(functions.isValueNull(response) && response.contains("{"))
            {
                jsonResponse = new JSONObject(response);

                if(jsonResponse.has("data")&& jsonResponse.getJSONObject("data") != null)
                {
                    JSONObject jsonData = jsonResponse.getJSONObject("data");

                    if(jsonData.has("payment_reference") && functions.isValueNull(jsonData.getString("payment_reference")))
                    {
                        paymentReference = jsonData.getString("payment_reference");
                    }

                    if(jsonData.has("responsecode") && functions.isValueNull(jsonData.getString("responsecode")))
                    {
                        responseCode = jsonData.getString("responsecode");
                    }

                    if(jsonData.has("responsemessage") && functions.isValueNull(jsonData.getString("responsemessage")))
                    {
                        responseMessage = jsonData.getString("responsemessage");
                    }

                    if(jsonData.has("transaction_date") && functions.isValueNull(jsonData.getString("transaction_date")))
                    {
                        transactionDate = jsonData.getString("transaction_date");
                    }

                    if(jsonData.has("currency") && functions.isValueNull(jsonData.getString("currency")))
                    {
                        currency = jsonData.getString("currency");
                    }

                    if(jsonData.has("amount") && functions.isValueNull(jsonData.getString("amount")))
                    {
                        amount = jsonData.getString("amount");
                    }

                    transactionLogger.error("paymentReference ===== " + paymentReference);
                    transactionLogger.error("responseCode ===== " + responseCode);
                    transactionLogger.error("responseMessage ===== " + responseMessage);
                    transactionLogger.error("transactionDate ===== " + transactionDate);
                    transactionLogger.error("currency ===== " + currency);
                    transactionLogger.error("amount ===== " + amount);
                }

                if(functions.isValueNull(responseCode) && "00".equals(responseCode))
                {
                    commResponseVO.setStatus("Success");
                    commResponseVO.setTransactionStatus("Success");
                    commResponseVO.setRemark(responseMessage);
                    commResponseVO.setDescription(responseMessage);
                    commResponseVO.setErrorCode(responseCode);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setTransactionId(paymentReference);
                    commResponseVO.setAuthCode(responseCode);
                    commResponseVO.setMerchantId(merchantToken);
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());

                    if(functions.isValueNull(transactionDate))
                        commResponseVO.setBankTransactionDate(transactionDate);
                }

                else if(functions.isValueNull(responseCode) && ("RR".equals(responseCode) || "EE".equals(responseCode) || "FF".equals(responseCode)))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(responseMessage);
                    commResponseVO.setDescription(responseMessage);
                    commResponseVO.setErrorCode(responseCode);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setAuthCode(responseCode);
                    commResponseVO.setMerchantId(merchantToken);
                }

                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark("Transaction is pending");
                    commResponseVO.setDescription("Transaction is pending");
                    commResponseVO.setErrorCode(responseCode);
                    commResponseVO.setAmount(amount);
                }

                commResponseVO.setTransactionType(PZProcessType.SALE.toString());
            }

            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("Transaction is pending");
                commResponseVO.setDescription("Transaction is pending");
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException ===== ", e);
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException ===== ", e);
        }
        catch (InvalidKeyException e)
        {
            transactionLogger.error("InvalidKeyException ===== ", e);
        }
        catch (NoSuchPaddingException e)
        {
            transactionLogger.error("NoSuchPaddingException ===== ", e);
        }
        catch (BadPaddingException e)
        {
            transactionLogger.error("BadPaddingException ===== ", e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException ===== ", e);
        }
        catch (IllegalBlockSizeException e)
        {
            transactionLogger.error("IllegalBlockSizeException ===== ", e);
        }

        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processRefund of Boltpay =====");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        String merchantToken = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String refundUrl = "";
        String reference = commRequestVO.getTransDetailsVO().getPreviousTransactionId();
        String key = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String amount = commTransactionDetailsVO.getAmount();

        transactionLogger.error("isTest ===== " + isTest);
        transactionLogger.error("merchantToken ===== " + merchantToken);
        transactionLogger.error("reference ===== " + reference);
        transactionLogger.error("key ===== " + key);
        transactionLogger.error("amount ===== " + amount);

        if (isTest)
        {
            refundUrl = RB.getString("TEST_REFUND_URL");
        }
        else
        {
            refundUrl = RB.getString("LIVE_REFUND_URL");
        }

        transactionLogger.error("refundURL in processRefund ===== " + refundUrl);


        JSONObject request = new JSONObject();
        JSONObject requestLog = new JSONObject();
        try
        {
            request.put("merchant_token", merchantToken);
            request.put("reference", BoltPayUtils.harden(reference, key));
            request.put("amount", BoltPayUtils.harden(amount, key));

            requestLog.put("merchant_token", merchantToken);
            requestLog.put("reference", reference);
            requestLog.put("amount", amount);

            transactionLogger.error("BoltPay Refund Request ===== " + trackingID+ "----" + requestLog);

            String response = BoltPayUtils.doPostHTTPSURLConnectionClient(refundUrl, request.toString());
            transactionLogger.error("BoltPay Refund Response ===== " + trackingID+ "----" + response);

            String paymentReference = "";
            String responseCode = "";
            String responseMessage = "";
            String currency = "";
            JSONObject jsonResponse = null;

            if(functions.isValueNull(response) && response.contains("{"))
            {
                jsonResponse = new JSONObject(response);

                if(jsonResponse.has("data")&& jsonResponse.getJSONObject("data") != null)
                {
                    JSONObject jsonData = jsonResponse.getJSONObject("data");

                    if(jsonData.has("payment_reference") && functions.isValueNull(jsonData.getString("payment_reference")))
                    {
                        paymentReference = jsonData.getString("payment_reference");
                    }

                    if(jsonData.has("responsecode") && functions.isValueNull(jsonData.getString("responsecode")))
                    {
                        responseCode = jsonData.getString("responsecode");
                    }

                    if(jsonData.has("responsemessage") && functions.isValueNull(jsonData.getString("responsemessage")))
                    {
                        responseMessage = jsonData.getString("responsemessage");
                    }

                    if(jsonData.has("currency") && functions.isValueNull(jsonData.getString("currency")))
                    {
                        currency = jsonData.getString("currency");
                    }

                    if(jsonData.has("amount") && functions.isValueNull(jsonData.getString("amount")))
                    {
                        amount = jsonData.getString("amount");
                    }

                    transactionLogger.error("paymentReference ===== " + paymentReference);
                    transactionLogger.error("responseCode ===== " + responseCode);
                    transactionLogger.error("responseMessage ===== " + responseMessage);
                    transactionLogger.error("currency ===== " + currency);
                    transactionLogger.error("amount ===== " + amount);
                }

                if(functions.isValueNull(responseCode) && "00".equals(responseCode))
                {
                    commResponseVO.setStatus("Success");
                    commResponseVO.setTransactionStatus("Success");
                    commResponseVO.setRemark(responseMessage);
                    commResponseVO.setDescription(responseMessage);
                    commResponseVO.setErrorCode(responseCode);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setTransactionId(paymentReference);
                }

                else if(functions.isValueNull(responseCode) && ("RR".equalsIgnoreCase(responseCode) || "EE".equalsIgnoreCase(responseCode) ||  "FF".equalsIgnoreCase(responseCode)))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(responseMessage);
                    commResponseVO.setDescription(responseMessage);
                    commResponseVO.setErrorCode(responseCode);
                    commResponseVO.setTransactionId(paymentReference);
                }

                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark("Transaction is pending");
                    commResponseVO.setDescription("Transaction is pending");
                    commResponseVO.setErrorCode(responseCode);
                }

            }

            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setRemark("Transaction is pending");
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException ===== ", e);
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException ===== ", e);
        }
        catch (InvalidKeyException e)
        {
            transactionLogger.error("InvalidKeyException ===== ", e);
        }
        catch (NoSuchPaddingException e)
        {
            transactionLogger.error("NoSuchPaddingException ===== ", e);
        }
        catch (BadPaddingException e)
        {
            transactionLogger.error("BadPaddingException ===== ", e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException ===== ", e);
        }
        catch (IllegalBlockSizeException e)
        {
            transactionLogger.error("IllegalBlockSizeException ===== ", e);
        }

        return commResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("Boltpay","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

}
