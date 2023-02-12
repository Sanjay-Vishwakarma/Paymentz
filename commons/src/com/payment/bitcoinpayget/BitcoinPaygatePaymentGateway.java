package com.payment.bitcoinpayget;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.PayMitco.core.PayMitcoResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.math.BigDecimal;
import java.util.ResourceBundle;

/**
 * Created by Jitendra on 19-Jun-19.
 */
public class BitcoinPaygatePaymentGateway extends AbstractPaymentGateway
{
    public static final  String GATEWAY_TYPE="bitcoinpg";
    final  static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.bitcoinpg");
    TransactionLogger transactionLogger=new TransactionLogger(BitcoinPaygatePaymentGateway.class.getName());

    public BitcoinPaygatePaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processSale ---");
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        Functions functions= new Functions();
        CommResponseVO commResponseVO= new CommResponseVO();
        CommTransactionDetailsVO transDetailsVO=commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String apiKeyName=gatewayAccount.getFRAUD_FTP_USERNAME();
        String apiKeySecret=gatewayAccount.getFRAUD_FTP_PASSWORD();
        String keySecret=apiKeyName+":"+apiKeySecret;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(keySecret.getBytes()));
        transactionLogger.error("encodedCredentials -------------" + encodedCredentials);
        String currency = "";
        if (functions.isValueNull(transDetailsVO.getCurrency()))
        {
            currency = transDetailsVO.getCurrency();
            transactionLogger.debug("currency ---"+currency);
        }

        String termUrl = "";
        String notificationUrl="";
        transactionLogger.error("HOST_URL ---" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            notificationUrl="https://" + commMerchantVO.getHostUrl() + RB.getString("NOTIFICATION_HOST_URL");
            transactionLogger.error("From HOST_URL----" + termUrl);
            transactionLogger.error("From HOST_URL notificationUrl ----" + notificationUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            notificationUrl = RB.getString("NOTIFICATION_TERM_URL");
            transactionLogger.error("From RB TERM_URL ----" + termUrl);
            transactionLogger.error("From RB notificationUrl ----" + notificationUrl);
        }

        try
        {
            JSONObject saleRequest = new JSONObject();
            saleRequest.put("priceAmount",transDetailsVO.getAmount());
            saleRequest.put("priceCurrency",transDetailsVO.getCurrency());
            saleRequest.put("transferCurrency","BTC");
            saleRequest.put("notificationUrl",notificationUrl+trackingId);
            saleRequest.put("externalPaymentId", trackingId);
            saleRequest.put("notificationUrl",notificationUrl+trackingId+"&notification=anomaly");
            saleRequest.put("walletMessage",trackingId);

            JSONObject orderProduct = new JSONObject();
            orderProduct.put("expired",termUrl+trackingId+"&status=expired");
            orderProduct.put("confirmed",termUrl+trackingId+"&status=confirmed");
            orderProduct.put("invalid",termUrl+trackingId+"&status=invalid");
            orderProduct.put("unconfirmed",termUrl+trackingId+"&status=unconfirmed");

            saleRequest.put("redirectUrls",orderProduct);

            transactionLogger.error(" sale request ----"+trackingId+"--->"+saleRequest);
            String saleResponse="";
            if(isTest)
            {
                saleResponse= BitcoinPaygateUtils.doPostHTTPSURLConnectionClient(RB.getString("SALE_TEST_URL"), saleRequest.toString(), encodedCredentials);
            }else
            {
                saleResponse= BitcoinPaygateUtils.doPostHTTPSURLConnectionClient(RB.getString("SALE_LIVE_URL"), saleRequest.toString(), encodedCredentials);
            }
            transactionLogger.error("sale response ----"+trackingId+"--->"+saleResponse);

            String address="";
            String respAmount="";
            String exchangeRate="";
            String paymentPageUrl="";
            String transactionId="";
            String paymentId="";
            String respAmountNew = "";
            String errorType = "";
            String message = "";
            if (saleResponse != null)
            {
                if (saleResponse.trim().startsWith("[") && saleResponse.trim().endsWith("]"))
                {
                    transactionLogger.error("inside error message start with [ ");
                    String new_saleResponse = "";
                    String message_from_response="";
                    new_saleResponse = saleResponse.replace("[", "");
                    new_saleResponse = new_saleResponse.replace("]", "");
                    new_saleResponse = new_saleResponse.replace("\n", "");
                    transactionLogger.error("new_saleResponse ----- " + new_saleResponse);
                    JSONObject jsonObjectnew = new JSONObject(new_saleResponse);
                    if (jsonObjectnew.has("message"))
                    {
                         message_from_response = jsonObjectnew.getString("message");
                        transactionLogger.error("message_from_response --- " + message_from_response);

                    }
                    commResponseVO.setStatus("failed");
                    commResponseVO.setRemark(message_from_response);
                    commResponseVO.setDescription(message_from_response);
                    return  commResponseVO;
                }
                else
                {
                    JSONObject jsonObject = new JSONObject(saleResponse);
                    if (jsonObject != null)
                    {
                        if (jsonObject.has("address"))
                        {
                            address = jsonObject.getString("address");
                            transactionLogger.error("address ---" + address);
                        }
                        if (jsonObject.has("transferAmount"))
                        {
                            respAmount = jsonObject.getString("transferAmount");
                            respAmountNew = new BigDecimal(respAmount).toPlainString();
                            transactionLogger.error("transferAmount ---" + respAmount);
                        }
                        if (jsonObject.has("exchangeRate"))
                        {
                            exchangeRate = jsonObject.getString("exchangeRate");
                            transactionLogger.error("exchangeRate ---" + exchangeRate);
                        }
                        if (jsonObject.has("paymentPageUrl"))
                        {
                            paymentPageUrl = jsonObject.getString("paymentPageUrl");
                            transactionLogger.error("paymentPageUrl ---" + paymentPageUrl);
                        }
                        if (jsonObject.has("transactionId"))
                        {
                            transactionId = jsonObject.getString("transactionId");
                            transactionLogger.error("transactionId ---" + transactionId);
                        }
                        if (jsonObject.has("paymentId"))
                        {
                            paymentId = jsonObject.getString("paymentId");
                            transactionLogger.error("paymentId ---" + paymentId);
                        }
                        if (jsonObject.has("errorType"))
                        {
                            errorType = jsonObject.getString("errorType");
                            transactionLogger.error("errorType ---" + errorType);
                        }
                        if (jsonObject.has("message"))
                        {
                            message = jsonObject.getString("message");
                            transactionLogger.error("message ---" + message);
                        }

                        if(functions.isValueNull(errorType))
                        {
                            commResponseVO.setStatus("failed");
                            commResponseVO.setRemark(message);
                            commResponseVO.setDescription(message);
                        }else
                        {
                            commResponseVO.setStatus("pending");
                        }
                        commResponseVO.setRedirectUrl(paymentPageUrl);
                        commResponseVO.setTransactionId(paymentId);
                        commResponseVO.setResponseHashInfo(address);
                        if (functions.isValueNull(respAmountNew))
                        {
                            commResponseVO.setWalletAmount(respAmountNew);
                        }
                        commResponseVO.setWalletCurrecny("BTC");
                        commResponseVO.setCurrency(currency);
                        transactionLogger.error("transactionId -------------" + transactionId);
                    }
                }
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("response null");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException----"+trackingId+"--->", e);
            PZExceptionHandler.raiseTechnicalViolationException(BitcoinPaygatePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception----"+trackingId+"--->", e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("BitcoinPaygatePaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by PerfectMoney gateway. Please contact your Tech. support Team:::",null);
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("---Entering into processInquery---");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        PayMitcoResponseVO commResponseVO = new PayMitcoResponseVO();
        Functions functions = new Functions();
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        TransactionManager transactionManager =new TransactionManager();
        TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(transDetailsVO.getOrderId());
        transactionLogger.error("status is -----"+transactionDetailsVO.getStatus());
        String trackingId=transDetailsVO.getOrderId();
        String transaction_status=transactionDetailsVO.getStatus();
        String apiKeyName=gatewayAccount.getFRAUD_FTP_USERNAME();
        String apiKeySecret=gatewayAccount.getFRAUD_FTP_PASSWORD();
        String keySecret=apiKeyName+":"+apiKeySecret;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(keySecret.getBytes()));
        transactionLogger.error("encodedCredentials -------------" + encodedCredentials);

        try
        {
            String inquireReq =RB.getString("INQUIRY_TEST_URL")+commRequestVO.getTransDetailsVO().getPreviousTransactionId();
            transactionLogger.error("---inquireReq---"+transDetailsVO.getOrderId()+"--->" + inquireReq);

            String inquireRes = BitcoinPaygateUtils.doGetHttpConnection(inquireReq,encodedCredentials);
            transactionLogger.error("---inquiryRes---"+transDetailsVO.getOrderId()+"--->" + inquireRes);

            String inquiryCurrency ="";
            String inquiryTransactionId ="";
            String inquiryPaymentId ="";
            String inquiryStatus ="";
            String remainingToPayBtc ="";
            String remainingToPay ="";
            String paymentTime ="";
            String paid ="";
            String inquiryMessage ="";

            if ((functions.isValueNull(inquireRes)) && inquireRes.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(inquireRes);
                if (jsonObject.has("transactionId"))
                {
                    inquiryTransactionId = jsonObject.getString("transactionId");
                    transactionLogger.error("---inquiryTransactionId---" + inquiryTransactionId);
                }
                if (jsonObject.has("paymentId"))
                {
                    inquiryPaymentId = jsonObject.getString("paymentId");
                    transactionLogger.error("---inquiryPaymentId---" + inquiryPaymentId);
                }
                if (jsonObject.has("status"))
                {
                    inquiryStatus = jsonObject.getString("status");
                    transactionLogger.error("---inquiryStatus---" + inquiryStatus);
                }
                if (jsonObject.has("pendingPaidTransferAmount"))
                {
                    remainingToPayBtc = jsonObject.getString("pendingPaidTransferAmount");
                    transactionLogger.error("---remainingToPayBtc---" + remainingToPayBtc);
                }
                if (jsonObject.has("pendingPaidPriceAmount"))
                {
                    remainingToPay = jsonObject.getString("pendingPaidPriceAmount");
                    transactionLogger.error("---remainingToPay---" + remainingToPay);
                }
                if (jsonObject.has("paymentTime"))
                {
                    paymentTime = jsonObject.getString("paymentTime");
                    transactionLogger.error("---paymentTime---" + paymentTime);
                }
                if (jsonObject.has("priceCurrency"))
                {
                    inquiryCurrency = jsonObject.getString("priceCurrency");
                    transactionLogger.error("---inquiryCurrency---" + inquiryCurrency);
                }
                if (jsonObject.has("paidPriceAmount"))
                {
                    paid = jsonObject.getString("paidPriceAmount");
                    transactionLogger.error("---paid---" + paid);
                }
                if (jsonObject.has("message"))
                {
                    inquiryMessage = jsonObject.getString("message");
                    transactionLogger.error("---inquiryMessage---" + inquiryMessage);
                }

                if ("CONFIRMED".equalsIgnoreCase(inquiryStatus))
                {
                    transactionLogger.error("---inside sucess part-----");
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else if ("INVALID".equalsIgnoreCase(inquiryStatus) || "UNDERPAID".equalsIgnoreCase(inquiryStatus))
                {
                    transactionLogger.error("---inside INVALID and UNDERPAID part-----");
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else if("NEW".equalsIgnoreCase(inquiryStatus) || "UNCONFIRMED".equalsIgnoreCase(inquiryStatus))
                {
                    transactionLogger.error("---inside pending-----");
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setTransactionStatus("fail");
                }
                commResponseVO.setAmount(paid);
                commResponseVO.setBankTransactionDate(paymentTime);
                commResponseVO.setCurrency(inquiryCurrency);
                commResponseVO.setDescription(inquiryMessage);
                commResponseVO.setResponseTime(paymentTime);
                commResponseVO.setTransactionId(inquiryPaymentId);
                if ("UNDERPAID".equalsIgnoreCase(inquiryStatus))
                {
                    commResponseVO.setDescription("remaining to pay: " + remainingToPay);
                    commResponseVO.setRemark("remaining to pay: " + remainingToPay);
                }
                if("INVALID".equalsIgnoreCase(inquiryStatus))
                {
                    commResponseVO.setDescription("overpaid amount: " + remainingToPay.substring(1)); // substring used to remove "-" sign
                    commResponseVO.setRemark("overpaid amount: " + remainingToPay.substring(1)); // substring used to remove "-" sign
                }
                else
                {
                    commResponseVO.setDescription(inquiryStatus);
                    commResponseVO.setRemark(inquiryStatus);
                }
                commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
                commResponseVO.setAuthCode("-");
                if (transaction_status.equalsIgnoreCase("capturesuccess"))
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                else if (transaction_status.equalsIgnoreCase("markedforreversal")||transaction_status.equalsIgnoreCase("reversed"))
                    commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                else
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException----"+trackingId+"--->", e);
            PZExceptionHandler.raiseTechnicalViolationException(BitcoinPaygatePaymentGateway.class.getName(), "processInquiry()", null, "common", "Technical Exception while Inquiry", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception---"+trackingId+"--->", e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Inside processPayout :::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Functions functions=new Functions();
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        String customerBitcoinAddress ="";
        if (functions.isValueNull(commTransactionDetailsVO.getWalletId()))
        {
            customerBitcoinAddress =commTransactionDetailsVO.getWalletId();
        }
        transactionLogger.debug("customerBitcoinAddress ----"+customerBitcoinAddress);
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
      //  TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(transDetailsVO.getOrderId());
       // transactionLogger.error("status is -----"+transactionDetailsVO.getStatus());
       // String transaction_status=transactionDetailsVO.getStatus();
        transactionLogger.debug("currency for payout -----" + commTransactionDetailsVO.getCurrency());
        transactionLogger.debug("amount for payout -----" + commTransactionDetailsVO.getAmount());
        String payoutRequest_Amount=commTransactionDetailsVO.getAmount();
        String apiKeyName=gatewayAccount.getFRAUD_FTP_USERNAME();
        String apiKeySecret=gatewayAccount.getFRAUD_FTP_PASSWORD();
        String keySecret=apiKeyName+":"+apiKeySecret;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(keySecret.getBytes()));
        transactionLogger.error("encodedCredentials -------------" + encodedCredentials);
        String address=commTransactionDetailsVO.getResponseHashInfo();
       transactionLogger.error("address"+address);
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        transactionLogger.error("HOST_URL ---" + commMerchantVO.getHostUrl());
        String notificationUrl="";

        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            notificationUrl="https://" + commMerchantVO.getHostUrl() + RB.getString("NOTIFICATION_HOST_URL");
            transactionLogger.error("From HOST_URL notificationUrl ----" + notificationUrl);
        }
        else
        {
            notificationUrl = RB.getString("NOTIFICATION_TERM_URL");
            transactionLogger.error("From RB notificationUrl ----" + notificationUrl);
        }

        try
        {
     //STEP 1::::::::::::: Limit Check
            String payoutLimit_Response="";
            if (isTest)
            {
                transactionLogger.error("Inside Is Test ---" + RB.getString("PAYOUT_LIMIT_TEST_URL")+commTransactionDetailsVO.getCurrency());
                payoutLimit_Response=BitcoinPaygateUtils.doGetHttpConnection(RB.getString("PAYOUT_LIMIT_TEST_URL")+commTransactionDetailsVO.getCurrency(), encodedCredentials);
            }
            else
            {
                transactionLogger.error("Inside Is Live ---" + RB.getString("PAYOUT_LIMIT_LIVE_URL")+commTransactionDetailsVO.getCurrency());
                payoutLimit_Response=BitcoinPaygateUtils.doGetHttpConnection(RB.getString("PAYOUT_LIMIT_LIVE_URL")+commTransactionDetailsVO.getCurrency(), encodedCredentials);
            }
            transactionLogger.error("payoutLimit_Response ----------"+trackingId+"--->" + payoutLimit_Response);

            if (payoutLimit_Response!=null)
            {
                JSONObject jsonObjectLimit = new JSONObject(payoutLimit_Response);
                if (jsonObjectLimit!=null)
                {
                    String available = jsonObjectLimit.getString("available");
                    transactionLogger.error("available --- " + available);

                    String minPayoutAmount = jsonObjectLimit.getString("minPayoutAmount");
                    transactionLogger.error("minPayoutAmount --- " + minPayoutAmount);

                    String used = jsonObjectLimit.getString("used");
                    transactionLogger.error("used --- "+used);

                    BigDecimal bigDecimal_available=new BigDecimal(available);
                    BigDecimal bigDecimal_minPayoutAmount=new BigDecimal(minPayoutAmount);
                    BigDecimal bigDecimal_payoutRequest_Amount=new BigDecimal(payoutRequest_Amount);

                    transactionLogger.error("bigDecimal_available ---" + bigDecimal_available);
                    transactionLogger.error("bigDecimal_minPayoutAmount ---" + bigDecimal_minPayoutAmount);
                    transactionLogger.error("bigDecimal_payoutRequest_Amount ---" + bigDecimal_payoutRequest_Amount);

                    int compareResult1=bigDecimal_payoutRequest_Amount.compareTo(bigDecimal_minPayoutAmount);
                    int compareResilt2=bigDecimal_payoutRequest_Amount.compareTo(bigDecimal_available);
        // compareResult1== 1 means if payout request amount is greater than min payout amount || compareResult1==0 means  payout request amount is equal to  min payout amount
        // compareResult2== -1 means if payout request amount is smaller than available payout amount || compareResult10=0 means  payout request amount is equal to  available payout amount

                    if (compareResult1==1 || compareResult1==0)
                    {
                        if(compareResilt2==-1 || compareResilt2==0)
                        {
    //STEP 2::::::::::::: Request Payout Offer
                            JSONObject payout_offer_request = new JSONObject();
                            payout_offer_request.put("priceAmount",payoutRequest_Amount);
                            payout_offer_request.put("priceCurrency",commTransactionDetailsVO.getCurrency());
                            payout_offer_request.put("transferCurrency","BTC");
                            payout_offer_request.put("address",customerBitcoinAddress);
                            payout_offer_request.put("notificationUrl",notificationUrl+trackingId);

                            transactionLogger.error("payout_offer_request ---"+trackingId+"--->"+payout_offer_request.toString());

                            String payout_offer_responce="";
                            if (isTest)
                            {
                                transactionLogger.error("Inside Is Test ---" + RB.getString("PAYOUT_OFFER_REQUEST_TEST_URL"));
                                payout_offer_responce=BitcoinPaygateUtils.doPostHTTPSURLConnectionClient(RB.getString("PAYOUT_OFFER_REQUEST_TEST_URL"), payout_offer_request.toString(),encodedCredentials);
                            }
                            else
                            {
                                transactionLogger.error("Inside Is Live ---" + RB.getString("PAYOUT_OFFER_REQUEST_LIVE_URL")+commTransactionDetailsVO.getCurrency());
                                payout_offer_responce=BitcoinPaygateUtils.doGetHttpConnection(RB.getString("PAYOUT_OFFER_REQUEST_LIVE_URL"), encodedCredentials);
                            }
                            transactionLogger.error("payout_offer_response ----------"+trackingId+"--->" + payout_offer_responce);

                            if (payout_offer_responce!=null)
                            {
                                JSONObject jsonObjectOffer = new JSONObject(payout_offer_responce);
                                if (jsonObjectOffer!=null)
                                {
                                    String  payoutOffer_address="";
                                    String  payoutOffer_btcAmount="";
                                    String  payoutOffer_createTime="";
                                    String  payoutOffer_currency="";
                                    String  payoutOffer_fiatAmount="";
                                    String  payoutOffer_payoutId="";
                                    String  payoutOffer_errorType="";
                                    String  payoutOffer_message="";
                                    if(jsonObjectOffer.has("address"))
                                        payoutOffer_address = jsonObjectOffer.getString("address");
                                    if(jsonObjectOffer.has("transferAmount"))
                                        payoutOffer_btcAmount = jsonObjectOffer.getString("transferAmount");
                                    if(jsonObjectOffer.has("createTime"))
                                        payoutOffer_createTime = jsonObjectOffer.getString("createTime");
                                    if(jsonObjectOffer.has("priceCurrency"))
                                        payoutOffer_currency = jsonObjectOffer.getString("priceCurrency");
                                    if(jsonObjectOffer.has("priceAmount"))
                                        payoutOffer_fiatAmount = jsonObjectOffer.getString("priceAmount");
                                    if(jsonObjectOffer.has("payoutId"))
                                        payoutOffer_payoutId = jsonObjectOffer.getString("payoutId");
                                    if(jsonObjectOffer.has("errorType"))
                                        payoutOffer_errorType = jsonObjectOffer.getString("errorType");
                                    if(jsonObjectOffer.has("message"))
                                        payoutOffer_message = jsonObjectOffer.getString("message");
                                    transactionLogger.debug("payoutOffer_payoutId ---"+payoutOffer_payoutId);
                                    if(functions.isValueNull(payoutOffer_errorType))
                                    {
                                        commResponseVO.setErrorCode(payoutOffer_errorType);
                                        commResponseVO.setDescription(payoutOffer_message);
                                        commResponseVO.setRemark(payoutOffer_message);
                                        return commResponseVO;
                                    }

                                    String jsonObjectApprove_address="";
                                    String jsonObjectApprove_approvalTime="";
                                    String jsonObjectApprove_approvedBy="";
                                    String jsonObjectApprove_fiatAmount="";
                                    String jsonObjectApprove_fiatTotalAmount="";
                                    String jsonObjectApprove_currency="";
                                    String jsonObjectApprove_payoutId="";
                                    String jsonObjectApprove_status="";

                                    String  payoutOffer_status=jsonObjectOffer.getString("status"); // this will be new always as per swagger doc and said by support member
                                    transactionLogger.error("payoutOffer_status ---" + payoutOffer_status);

                                    if (functions.isValueNull(payoutOffer_payoutId) && (payoutOffer_status.equalsIgnoreCase("NEW") || payoutOffer_status.equalsIgnoreCase("QUEUED")))
                                    {
    //STEP 3::::::::::::: Approve Payout Offfer
                                        JSONObject payout_offer_approve_Request = new JSONObject();
                                        payout_offer_approve_Request.put("id",payoutOffer_payoutId);
                                        transactionLogger.error("payout_offer_approve_Request ---"+trackingId+"--->" +payout_offer_approve_Request.toString());
                                        String payout_offer_approve_Responce="";
                                        if (isTest)
                                        {
                                            transactionLogger.error("Inside Is Test ---"+trackingId+"--->" + RB.getString("PAYOUT_OFFER_APPROVE_TEST_URL"));
                                            payout_offer_approve_Responce=BitcoinPaygateUtils.doPostHTTPSURLConnectionClient(RB.getString("PAYOUT_OFFER_APPROVE_TEST_URL")+payoutOffer_payoutId+"/approve", payout_offer_approve_Request.toString() ,encodedCredentials);
                                        }
                                        else
                                        {
                                            transactionLogger.error("Inside Is Live ---" + RB.getString("PAYOUT_OFFER_APPROVE_LIVE_URL")+commTransactionDetailsVO.getCurrency());
                                            payout_offer_approve_Responce=BitcoinPaygateUtils.doPostHTTPSURLConnectionClient(RB.getString("PAYOUT_OFFER_APPROVE_LIVE_URL") + payoutOffer_payoutId + "/approve", payout_offer_approve_Request.toString(), encodedCredentials);
                                        }
                                        transactionLogger.error("payout_offer_approve_Response ----------" + payout_offer_approve_Responce);

                                        if(functions.isValueNull(payout_offer_approve_Responce))
                                        {
                                            JSONObject jsonObjectApprove = new JSONObject(payout_offer_approve_Responce);
                                            if (jsonObjectApprove!=null)
                                            {
                                                if(jsonObjectApprove.has("address"))
                                                    jsonObjectApprove_address=jsonObjectApprove.getString("address");
                                                if(jsonObjectApprove.has("sentTime"))
                                                    jsonObjectApprove_approvalTime=jsonObjectApprove.getString("sentTime");
                                                if(jsonObjectApprove.has("approvedBy"))
                                                    jsonObjectApprove_approvedBy=jsonObjectApprove.getString("approvedBy");
                                                if(jsonObjectApprove.has("priceAmount"))
                                                    jsonObjectApprove_fiatAmount=jsonObjectApprove.getString("priceAmount");
                                                if(jsonObjectApprove.has("fiatTotalAmount"))
                                                    jsonObjectApprove_fiatTotalAmount=jsonObjectApprove.getString("fiatTotalAmount");
                                                if(jsonObjectApprove.has("priceCurrency"))
                                                    jsonObjectApprove_currency=jsonObjectApprove.getString("priceCurrency");
                                                if(jsonObjectApprove.has("payoutId"))
                                                    jsonObjectApprove_payoutId=jsonObjectApprove.getString("payoutId");
                                                if(jsonObjectApprove.has("status"))
                                                    jsonObjectApprove_status=jsonObjectApprove.getString("status");

                                                if (jsonObjectApprove_status.equalsIgnoreCase("NEW") || jsonObjectApprove_status.equalsIgnoreCase("QUEUED"))
                                                {
                                                    transactionLogger.error("inside jsonObjectApprove new and queued condition");
                                                    commResponseVO.setStatus("pending");
                                                    commResponseVO.setRemark("SYS:Payout is Pending bcz it is in " + jsonObjectApprove_status + " state.");
                                                    commResponseVO.setDescription("SYS:Payout is Pending bcz it is in " + jsonObjectApprove_status + " state.");
                                                }
                                                else if (jsonObjectApprove_status.equalsIgnoreCase("SENT"))
                                                {
                                                    commResponseVO.setStatus("success");
                                                    commResponseVO.setRemark("Payout success");
                                                    commResponseVO.setDescription("Payout success");
                                                }
                                                else
                                                {
                                                    commResponseVO.setStatus("fail");
                                                    commResponseVO.setRemark("SYS:payout fail");
                                                    commResponseVO.setDescription("SYS:payout fail");
                                                }
                                            }
                                        }
                                    }
                                    else if(functions.isValueNull(payoutOffer_payoutId) && payoutOffer_status.equalsIgnoreCase("SENT"))
                                    {
                                        commResponseVO.setStatus("success");
                                        commResponseVO.setRemark("payout status is currently" + payoutOffer_status);
                                        commResponseVO.setDescription("payout status is currently" + payoutOffer_status);
                                    }
                                    else
                                    {
                                        commResponseVO.setStatus("fail");
                                        commResponseVO.setRemark("payout status is currently" + payoutOffer_status);
                                        commResponseVO.setDescription("payout status is currently" + payoutOffer_status);
                                    }
                                    commResponseVO.setTransactionId(jsonObjectApprove_payoutId);
                                    commResponseVO.setResponseHashInfo(jsonObjectApprove_address);
                                    commResponseVO.setCurrency(jsonObjectApprove_currency);
                                    commResponseVO.setAmount(jsonObjectApprove_fiatAmount);
                                    commResponseVO.setBankTransactionDate(jsonObjectApprove_approvalTime);
                                }
                                else
                                {
                                    commResponseVO.setStatus("fail");
                                    commResponseVO.setRemark("SYS:Issue with payout offer response");
                                    commResponseVO.setDescription("SYS:Issue with payout offer response");
                                    return  commResponseVO;
                                }
                            }
                            else
                            {
                                commResponseVO.setStatus("fail");
                                commResponseVO.setRemark("SYS:Issue with payout offer response");
                                commResponseVO.setDescription("SYS:Issue with payout offer response");
                                return  commResponseVO;
                            }
                        }
                        else
                        {
                          commResponseVO.setStatus("fail");
                          commResponseVO.setRemark("SYS:payout request amount may be greater than daily available payout amount "+available);
                          commResponseVO.setDescription("SYS:payout request amount may be greater than daily available payout amount "+available);
                           return commResponseVO;
                        }
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setRemark("SYS:Payout request amount is less than daily minimum payout amount "+minPayoutAmount);
                        commResponseVO.setDescription("SYS:Payout request amount is less than daily minimum payout amount "+minPayoutAmount);
                        return  commResponseVO;
                    }
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("SYS:Issue with payout Limit Json Response");
                    commResponseVO.setDescription("SYS:Issue with payout Limit Json Response");
                    return  commResponseVO;
                }
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Issue With Payout Limit Response");
                commResponseVO.setDescription("Issue With Payout Limit Response");
                return commResponseVO;
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException----"+trackingId+"--->", e);
            PZExceptionHandler.raiseTechnicalViolationException(BitcoinPaygatePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception----"+trackingId+"--->", e);
        }

        return commResponseVO;
    }


    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("processAutoRedirect in BitcoinPaygetPaymentGateway ----");
        String html="";
        BitcoinPaygateUtils bitcoinPaygateUtils =new BitcoinPaygateUtils();
        PaymentManager paymentManager = new PaymentManager();
        CommRequestVO commRequestVO = null;
        CommResponseVO transRespDetails = null;
        commRequestVO = bitcoinPaygateUtils.getCommRequestFromUtils(commonValidatorVO);

        try
        {
            transRespDetails = (CommResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
            {
                transactionLogger.debug("status -------------------"+transRespDetails.getStatus());
                paymentManager.updatePaymentIdForCommon(transRespDetails, commonValidatorVO.getTrackingid());
                BitcoinPaygateUtils.updateResponseHashInfo(transRespDetails, commonValidatorVO.getTrackingid());
                html = bitcoinPaygateUtils.getRedirectForm(commonValidatorVO.getTrackingid(),transRespDetails);
                transactionLogger.error("Html in processAutoRedirect -------" + html);
            }
            else if ("failed".equalsIgnoreCase(transRespDetails.getStatus()))
            {
                html="failed";
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException ---"+commonValidatorVO.getTrackingid()+"--->",e);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception----"+commonValidatorVO.getTrackingid()+"--->", e);
        }
        return html;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
