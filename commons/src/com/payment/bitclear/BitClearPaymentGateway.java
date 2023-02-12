package com.payment.bitclear;

import com.directi.pg.Base64;
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
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Admin on 3/8/2021.
 */
public class BitClearPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(BitClearPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE             = "bitclear";
    private final static ResourceBundle RB              = LoadProperties.getProperty("com.directi.pg.bitclear");

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("BitClearPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public BitClearPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processSale ---");
        CommRequestVO commRequestVO                 = (CommRequestVO) requestVO;
        Functions functions                         = new Functions();
        Comm3DResponseVO commResponseVO             = new Comm3DResponseVO();
        CommTransactionDetailsVO transDetailsVO     = commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO               = commRequestVO.getCommMerchantVO();
        CommAddressDetailsVO addressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount               = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest              = gatewayAccount.isTest();
        String apiKeyName           = gatewayAccount.getFRAUD_FTP_USERNAME();
        String apiKeySecret         = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String currency             = "";

        String RETURN_URL           = "";
        String notificationUrl      = "";
        JSONObject saleRequest      = new JSONObject();
        String REQUEST_URL          = "";

        try
        {
            String keySecret            = apiKeyName + ":" + apiKeySecret;
            String encodedCredentials   = new String(Base64.encode(keySecret.getBytes("utf-8")));
            transactionLogger.error("encodedCredentials -------------" + encodedCredentials);

            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                RETURN_URL      = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
                notificationUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("NOTIFIY_URL");
            }
            else
            {
                RETURN_URL      = RB.getString("RETURN_URL");
                notificationUrl = RB.getString("NOTIFICATION_URL");
            }

            transactionLogger.error("From HOST_URL----" + RETURN_URL);
            transactionLogger.error("From HOST_URL notificationUrl ----" + notificationUrl);

            if(isTest)
            {
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }else
            {
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }

            if (functions.isValueNull(transDetailsVO.getCurrency()))
            {
                currency = transDetailsVO.getCurrency();
            }

            saleRequest.put("priceAmount",transDetailsVO.getAmount());
            saleRequest.put("priceCurrency",transDetailsVO.getCurrency());
            saleRequest.put("transferCurrency","BTC");
            saleRequest.put("notificationUrl",notificationUrl + trackingId + "&opt=deposit");
            saleRequest.put("externalPaymentId", trackingId);
            //saleRequest.put("notificationUrl",notificationUrl+trackingId+"&notification=anomaly");
            saleRequest.put("walletMessage",trackingId);

            JSONObject redirectUrls = new JSONObject();
            redirectUrls.put("expired",RETURN_URL+trackingId+"&status=expired");
            redirectUrls.put("confirmed",RETURN_URL+trackingId+"&status=confirmed");
            redirectUrls.put("invalid",RETURN_URL+trackingId+"&status=invalid");
            redirectUrls.put("unconfirmed",RETURN_URL+trackingId+"&status=unconfirmed");

            saleRequest.put("redirectUrls",redirectUrls);

            transactionLogger.error(" sale request ----"+trackingId+"--->"+saleRequest);

            String saleResponse = BitClearUtils.doPostHTTPSURLConnectionClient(REQUEST_URL, saleRequest.toString(), encodedCredentials);

            transactionLogger.error("sale response ----"+trackingId+"--->"+saleResponse);

            String address      ="";
            String respAmount   ="";
            String exchangeRate ="";
            String paymentPageUrl   ="";
            String transactionId    ="";
            String paymentId        ="";
            String respAmountNew    = "";
            String errorType        = "";
            String message          = "";
            String status           = "";
            if (functions.isValueNull(saleResponse))
            {
                if (saleResponse.trim().startsWith("[") && saleResponse.trim().endsWith("]"))
                {
                    transactionLogger.error("inside error message start with [ ");
                    String new_saleResponse = "";
                    String message_from_response="";
                    new_saleResponse    = saleResponse.replace("[", "");
                    new_saleResponse    = new_saleResponse.replace("]", "");
                    new_saleResponse    = new_saleResponse.replace("\n", "");
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

                    if (functions.isValueNull(saleResponse) && functions.isJSONValid(saleResponse))
                    {
                        JSONObject jsonObject = new JSONObject(saleResponse);
                        if (jsonObject.has("address"))
                        {
                            address = jsonObject.getString("address");
                        }
                        if (jsonObject.has("status"))
                        {
                            status = jsonObject.getString("status");
                        }
                        if (jsonObject.has("transferAmount"))
                        {
                            respAmount = jsonObject.getString("transferAmount");
                            respAmountNew = new BigDecimal(respAmount).toPlainString();
                        }
                        if (jsonObject.has("exchangeRate"))
                        {
                            exchangeRate = jsonObject.getString("exchangeRate");
                        }
                        if (jsonObject.has("paymentPageUrl"))
                        {
                            paymentPageUrl = jsonObject.getString("paymentPageUrl");
                        }
                        if (jsonObject.has("transactionId"))
                        {
                            transactionId = jsonObject.getString("transactionId");
                        }
                        if (jsonObject.has("paymentId"))
                        {
                            paymentId = jsonObject.getString("paymentId");
                        }
                        if (jsonObject.has("errorType"))
                        {
                            errorType = jsonObject.getString("errorType");
                        }
                        if (jsonObject.has("message"))
                        {
                            message = jsonObject.getString("message");
                        }

                        if(functions.isValueNull(errorType))
                        {
                            commResponseVO.setStatus("failed");
                            commResponseVO.setRemark(message);
                            commResponseVO.setDescription(message);
                        }else
                        {
                            commResponseVO.setStatus("pending");
                            commResponseVO.setUrlFor3DRedirect(paymentPageUrl);
                            commResponseVO.setTransactionId(paymentId);
                            commResponseVO.setWalletId(address);
                            if (functions.isValueNull(respAmountNew))
                            {
                                commResponseVO.setWalletAmount(respAmountNew);
                            }
                            commResponseVO.setWalletCurrecny("BTC");
                            commResponseVO.setCurrency(currency);
                        }
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
        catch (Exception e)
        {
            transactionLogger.error("Exception----"+trackingId+"--->", e);
            PZExceptionHandler.raiseTechnicalViolationException(BitClearPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error(" ---- Process Auto Redirect ---- ");
        String html                                 = "";
        Comm3DResponseVO transRespDetails           = null;
        BitClearUtils bitClearUtils                 = new BitClearUtils();
        BitClearPaymentProcess payGPaymentProcess   = new BitClearPaymentProcess();
        CommRequestVO commRequestVO                 = bitClearUtils.getBitClearGRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount               = GatewayAccountService.getGatewayAccount(accountId);
        PaymentManager paymentManager = new PaymentManager();

        try
        {
            transactionLogger.error("isService Flag -----" + commonValidatorVO.getMerchantDetailsVO().getIsService());
            transactionLogger.error("autoredirect  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAutoRedirect());
            transactionLogger.error("addressdetail  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAddressDeatails());
            transactionLogger.error("terminal id  is -----" + commonValidatorVO.getTerminalId());
            transactionLogger.error("tracking id  is -----" + commonValidatorVO.getTrackingid());

            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails.getStatus().equalsIgnoreCase("pending"))
            {
                paymentManager.updatePaymentIdForCommon(transRespDetails, commonValidatorVO.getTrackingid());
                BitClearUtils.updateResponseHashInfo(transRespDetails, commonValidatorVO.getTrackingid());
                html = payGPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionLogger.error("automatic redirect PayG form -- >>"+commonValidatorVO.getTrackingid() +" " +html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException in BitClearPaymentGateway---", e);
        }
        return html;
    }

    public GenericResponseVO processQuery(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("---Entering into processInquery---");

        CommRequestVO commRequestVO               = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO             = new CommResponseVO();
        Functions functions                       = new Functions();
        GenericTransDetailsVO transDetailsVO      = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount             = GatewayAccountService.getGatewayAccount(accountId);
        TransactionManager transactionManager     = new TransactionManager();
        //TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(transDetailsVO.getOrderId());

        String transaction_status   = "";
        String apiKeyName           = gatewayAccount.getFRAUD_FTP_USERNAME();
        String apiKeySecret         = gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest              = gatewayAccount.isTest();

        String REQUEST_URL = "";
        try
        {
            String keySecret            = apiKeyName + ":" + apiKeySecret;
            String encodedCredentials   = new String(Base64.encode(keySecret.getBytes("utf-8")));

            if(isTest)
            {
                REQUEST_URL = RB.getString("INQUIRY_TEST_URL");
            }else
            {
                REQUEST_URL = RB.getString("INQUIRY_LIVE_URL");
            }

            String inquireReq = REQUEST_URL + commRequestVO.getTransDetailsVO().getPreviousTransactionId();

            transactionLogger.error("Inquiry Request---"+ trackingId +"--->" + inquireReq);

            String inquireRes = BitClearUtils.doGetHttpConnection(inquireReq,encodedCredentials);

            transactionLogger.error("Inquiry Response ---> "+ trackingId+" " + inquireRes);

            String inquiryCurrency      ="";
            String inquiryTransactionId ="";
            String inquiryPaymentId     ="";
            String inquiryStatus        ="";
            String remainingToPayBtc    ="";
            String remainingToPay       ="";
            String paymentTime          ="";
            String paid                 ="";
            String inquiryMessage       ="";
            String acceptedTime         = "";

            if (functions.isValueNull(inquireRes) && functions.isJSONValid(inquireRes))
            {
                JSONObject jsonObject = new JSONObject(inquireRes);
                if (jsonObject.has("transactionId"))
                {
                    inquiryTransactionId = jsonObject.getString("transactionId");
                }
                if (jsonObject.has("paymentId"))
                {
                    inquiryPaymentId = jsonObject.getString("paymentId");
                }
                if (jsonObject.has("status"))
                {
                    inquiryStatus = jsonObject.getString("status");
                }
                if (jsonObject.has("pendingPaidTransferAmount"))
                {
                    remainingToPayBtc = jsonObject.getString("pendingPaidTransferAmount");
                }
                if (jsonObject.has("pendingPaidPriceAmount"))
                {
                    remainingToPay = jsonObject.getString("pendingPaidPriceAmount");
                }
                if (jsonObject.has("paymentTime"))
                {
                    paymentTime = jsonObject.getString("paymentTime");
                }
                if (jsonObject.has("priceCurrency"))
                {
                    inquiryCurrency = jsonObject.getString("priceCurrency");
                }
                if (jsonObject.has("paidPriceAmount"))
                {
                    paid = jsonObject.getString("paidPriceAmount");
                }
                if (jsonObject.has("message"))
                {
                    inquiryMessage = jsonObject.getString("message");
                }
                if (jsonObject.has("transactions"))
                {
                    JSONArray transactionsArrary = jsonObject.getJSONArray("transactions");
                    if(transactionsArrary != null && transactionsArrary.length() > 0){
                        JSONObject arrObj = transactionsArrary.getJSONObject(0);
                        if(arrObj.has("")){
                            acceptedTime = arrObj.getString("acceptedTime");
                        }
                    }
                }

                transactionLogger.error("inquiryStatus >>>>>>>>>> "+trackingId+" "+inquiryStatus);

                if ("CONFIRMED".equalsIgnoreCase(inquiryStatus))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setDescription(inquiryStatus);
                    commResponseVO.setRemark(inquiryStatus);

                }else if ("INVALID".equalsIgnoreCase(inquiryStatus) || "UNDERPAID".equalsIgnoreCase(inquiryStatus))
                {
                    transactionLogger.error("---inside INVALID and UNDERPAID part-----");
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());

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
                }
                else if("NEW".equalsIgnoreCase(inquiryStatus) || "UNCONFIRMED".equalsIgnoreCase(inquiryStatus))
                {
                    transactionLogger.error("---inside pending-----");
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setDescription(inquiryStatus);
                    commResponseVO.setRemark(inquiryStatus);
                }else if("EXPIRED".equalsIgnoreCase(inquiryStatus)){
                    commResponseVO.setStatus("fail");
                    commResponseVO.setTransactionStatus("fail");
                    commResponseVO.setDescription(inquiryStatus);
                    commResponseVO.setRemark(inquiryStatus);
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }

                commResponseVO.setAmount(paid);
                commResponseVO.setBankTransactionDate(acceptedTime);
                commResponseVO.setCurrency(inquiryCurrency);
                //commResponseVO.setDescription(inquiryMessage);
                commResponseVO.setResponseTime(acceptedTime);
                commResponseVO.setTransactionId(inquiryPaymentId);
                commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
                commResponseVO.setAuthCode("-");

               /* if (transaction_status.equalsIgnoreCase("capturesuccess")){
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                }else if (transaction_status.equalsIgnoreCase("markedforreversal")||transaction_status.equalsIgnoreCase("reversed")){
                    commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                }else{
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                }*/
            }else{
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
            }
            commResponseVO.setTransactionType(PZProcessType.SALE.toString());
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception---"+trackingId+"--->", e);
            PZExceptionHandler.raiseTechnicalViolationException(BitClearPaymentGateway.class.getName(), "processInquiry()", null, "common", "Technical Exception while Inquiry", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Inside processPayout :::::");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Functions functions                                 = new Functions();
        CommResponseVO commResponseVO                       = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        String customerBitcoinAddress                       = "";

        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                  = gatewayAccount.isTest();
        String payoutRequestedAmount    = commTransactionDetailsVO.getAmount();
        String apiKeyName               = gatewayAccount.getFRAUD_FTP_USERNAME();
        String apiKeySecret             = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String address                  = commTransactionDetailsVO.getResponseHashInfo();
        CommMerchantVO commMerchantVO   = commRequestVO.getCommMerchantVO();

        String notificationUrl      = "";
        String LIMIT_REQUEST_URL    = "";
        String PAYOUT_REQUEST_URL   = "";
        String PAYOUT_APPROVE_URL   = "";

        String currency = "";
        try
        {
            String keySecret            = apiKeyName + ":" + apiKeySecret;
            String encodedCredentials   = new String(Base64.encode(keySecret.getBytes("utf-8")));

            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                notificationUrl="https://" + commMerchantVO.getHostUrl() + RB.getString("NOTIFIY_URL");
            }
            else
            {
                notificationUrl = RB.getString("NOTIFICATION_URL");
            }

            if(functions.isValueNull(commTransactionDetailsVO.getCurrency())){
                currency = commTransactionDetailsVO.getCurrency();
            }
            if(functions.isValueNull(commTransactionDetailsVO.getResponseHashInfo())){
                address = commTransactionDetailsVO.getResponseHashInfo();
            }

            if (isTest)
            {
                LIMIT_REQUEST_URL   = RB.getString("PAYOUT_LIMIT_TEST_URL") + currency;
                PAYOUT_REQUEST_URL  = RB.getString("PAYOUT_TEST_URL");
                PAYOUT_APPROVE_URL  = RB.getString("APPROVE_PAYOUT_TEST_URL");
            }
            else
            {
                LIMIT_REQUEST_URL   = RB.getString("PAYOUT_LIMIT_LIVE_URL") + currency;
                PAYOUT_REQUEST_URL  = RB.getString("PAYOUT_LIVE_URL");
                PAYOUT_APPROVE_URL  = RB.getString("APPROVE_PAYOUT_LIVE_URL");
            }

            if (functions.isValueNull(commTransactionDetailsVO.getWalletId()))
            {
                customerBitcoinAddress = commTransactionDetailsVO.getWalletId();
            }

            transactionLogger.debug("customerBitcoinAddress ----> "+trackingId+" "+customerBitcoinAddress);
            transactionLogger.debug("notificationUrl ----> " + trackingId + " " + notificationUrl);
            transactionLogger.debug("LIMIT_REQUEST_URL ----> " + trackingId + " " + LIMIT_REQUEST_URL);
            transactionLogger.debug("PAYOUT_REQUEST_URL ----> " + trackingId + " " + PAYOUT_REQUEST_URL);


            //STEP 1 -----------> Payout Limit Check
            transactionLogger.error("Payout LIMIT Start " + trackingId);
            transactionLogger.error("LIMIT_REQUEST_URL " + trackingId+" " + LIMIT_REQUEST_URL);

            String payoutLimit_Response = BitClearUtils.doGetHttpConnection(LIMIT_REQUEST_URL, encodedCredentials);

            transactionLogger.error("payoutLimit_Response ----------"+trackingId+"--->" + payoutLimit_Response);
            transactionLogger.error("Payout LIMIT End " + trackingId);

            String available        = "";
            String minPayoutAmount  = "";
            String used             = "";

            if (functions.isValueNull(payoutLimit_Response) && functions.isJSONValid(payoutLimit_Response))
            {
                JSONObject jsonObjectLimit = new JSONObject(payoutLimit_Response);

                if (jsonObjectLimit != null)
                {
                    if(jsonObjectLimit.has("available")){
                        available = jsonObjectLimit.getString("available");
                    }
                    if(jsonObjectLimit.has("minPayoutAmount")){
                        minPayoutAmount = jsonObjectLimit.getString("minPayoutAmount");
                    }
                    if(jsonObjectLimit.has("used")){
                        used = jsonObjectLimit.getString("used");
                    }

                    BigDecimal bigDecimal_available             = new BigDecimal(available);
                    BigDecimal bigDecimalMinPayoutAmount       = new BigDecimal(minPayoutAmount);// minmum payou amount can be requested
                    BigDecimal bigDecimal_payoutRequest_Amount  = new BigDecimal(payoutRequestedAmount);

                    transactionLogger.error("bigDecimal_available ---" + bigDecimal_available);
                    transactionLogger.error("bigDecimal_minPayoutAmount ---" + bigDecimalMinPayoutAmount);
                    transactionLogger.error("bigDecimal_payoutRequest_Amount ---" + bigDecimal_payoutRequest_Amount);

                    int compareResult1  = bigDecimal_payoutRequest_Amount.compareTo(bigDecimalMinPayoutAmount);
                    int compareResilt2  = bigDecimal_payoutRequest_Amount.compareTo(bigDecimal_available);
                    // compareResult1== 1 means if payout request amount is greater than min payout amount || compareResult1==0 means  payout request amount is equal to  min payout amount
                    // compareResult2== -1 means if payout request amount is smaller than available payout amount || compareResult10=0 means  payout request amount is equal to  available payout amount

                    if (compareResult1 == 1 || compareResult1 == 0)
                    {
                        if(compareResilt2 == -1 || compareResilt2 == 0)
                        {
                            transactionLogger.error("payout Request Start " + trackingId);
                            //STEP 2 ---------> Payout Request  Offer
                            JSONObject payoutRequest = new JSONObject();

                            payoutRequest.put("priceAmount",payoutRequestedAmount);
                            payoutRequest.put("priceCurrency",commTransactionDetailsVO.getCurrency());
                            payoutRequest.put("transferCurrency","BTC");
                            payoutRequest.put("address",customerBitcoinAddress);
                            payoutRequest.put("notificationUrl",notificationUrl + trackingId + "&opt=payout" );
                            payoutRequest.put("externalPayoutId",trackingId);

                            transactionLogger.error("payoutRequest ---> " + trackingId + " " + payoutRequest.toString());

                            String payoutResponce = BitClearUtils.doPostHTTPSURLConnectionClient(PAYOUT_REQUEST_URL, payoutRequest.toString(),encodedCredentials);

                            transactionLogger.error("payoutResponce ---> "+trackingId+" " + payoutResponce.toString());
                            transactionLogger.error("payout Request End " + trackingId);

                            if (payoutResponce !=null && functions.isJSONValid(payoutResponce) )
                            {
                                JSONObject jsonObjectOffer = new JSONObject(payoutResponce);
                                if (jsonObjectOffer!=null)
                                {
                                    String  payoutOffer_address     ="";
                                    String  payoutOffer_btcAmount   ="";
                                    String  payoutOffer_createTime  ="";
                                    String  payoutOffer_currency    ="";
                                    String  payoutOffer_fiatAmount  ="";
                                    String  payoutOffer_payoutId    ="";
                                    String  payoutOffer_errorType   ="";
                                    String  payoutOffer_message     ="";

                                    if(jsonObjectOffer.has("address")){
                                        payoutOffer_address = jsonObjectOffer.getString("address");
                                    }

                                    if(jsonObjectOffer.has("transferAmount")){
                                        payoutOffer_btcAmount = jsonObjectOffer.getString("transferAmount");
                                    }

                                    if(jsonObjectOffer.has("createTime")){
                                        payoutOffer_createTime = jsonObjectOffer.getString("createTime");
                                    }

                                    if(jsonObjectOffer.has("priceCurrency")){
                                        payoutOffer_currency = jsonObjectOffer.getString("priceCurrency");
                                    }

                                    if(jsonObjectOffer.has("priceAmount")){
                                        payoutOffer_fiatAmount = jsonObjectOffer.getString("priceAmount");
                                    }

                                    if(jsonObjectOffer.has("payoutId")){
                                        payoutOffer_payoutId = jsonObjectOffer.getString("payoutId");
                                    }

                                    if(jsonObjectOffer.has("errorType")){
                                        payoutOffer_errorType = jsonObjectOffer.getString("errorType");
                                    }

                                    if(jsonObjectOffer.has("message")){
                                        payoutOffer_message = jsonObjectOffer.getString("message");
                                    }
                                    if(functions.isValueNull(payoutOffer_payoutId)){
                                        BitClearUtils.updatePaymentId(payoutOffer_payoutId,trackingId);
                                    }

                                    transactionLogger.debug("payoutOffer_payoutId ---"+trackingId+" "+payoutOffer_payoutId);

                                    if(functions.isValueNull(payoutOffer_errorType))
                                    {
                                        commResponseVO.setErrorCode(payoutOffer_errorType);
                                        commResponseVO.setDescription(payoutOffer_message);
                                        commResponseVO.setRemark(payoutOffer_message);
                                        return commResponseVO;
                                    }

                                    String jsonObjectApprove_address            = "";
                                    String jsonObjectApprove_approvalTime       = "";
                                    String jsonObjectApprove_approvedBy         = "";
                                    String jsonObjectApprove_fiatAmount         = "";
                                    String jsonObjectApprove_fiatTotalAmount    = "";
                                    String jsonObjectApprove_currency   = "";
                                    String jsonObjectApprove_payoutId   = "";
                                    String jsonObjectApprove_status     = "";

                                    String  payoutOffer_status  = jsonObjectOffer.getString("status"); // this will be new always as per swagger doc and said by support member
                                    transactionLogger.error("payoutOffer_status ---> " +trackingId +" " + payoutOffer_status);

                                    if (functions.isValueNull(payoutOffer_payoutId) && (payoutOffer_status.equalsIgnoreCase("NEW") || payoutOffer_status.equalsIgnoreCase("QUEUED")))
                                    {
                                        transactionLogger.error("Payout Approve Request Start ---> "+trackingId);
                                        //STEP 3::::::::::::: Approve Payout Offfer
                                        JSONObject payoutApproveRequest = new JSONObject();
                                        payoutApproveRequest.put("id",payoutOffer_payoutId);

                                        PAYOUT_APPROVE_URL = PAYOUT_APPROVE_URL +payoutOffer_payoutId +"/approve";

                                        transactionLogger.error("PAYOUT_APPROVE_URL ---> "+trackingId+"--->" +PAYOUT_APPROVE_URL);
                                        transactionLogger.error("payoutApproveRequest ---> "+trackingId+"--->" +payoutApproveRequest.toString());

                                        String payoutApproveResponse = BitClearUtils.doPostHTTPSURLConnectionClient(PAYOUT_APPROVE_URL, encodedCredentials);

                                        transactionLogger.error("payout_offer_approve_Response ----------" + payoutApproveResponse);
                                        transactionLogger.error("Payout Approve Request End ---> "+trackingId);

                                        if(functions.isValueNull(payoutApproveResponse) && functions.isJSONValid(payoutApproveResponse))
                                        {
                                            JSONObject jsonObjectApprove = new JSONObject(payoutApproveResponse);

                                            if (jsonObjectApprove != null)
                                            {
                                                if(jsonObjectApprove.has("address")){
                                                    jsonObjectApprove_address   = jsonObjectApprove.getString("address");
                                                }

                                                if(jsonObjectApprove.has("sentTime")){
                                                    jsonObjectApprove_approvalTime  = jsonObjectApprove.getString("sentTime");
                                                }

                                                if(jsonObjectApprove.has("approvedBy")){
                                                    jsonObjectApprove_approvedBy    = jsonObjectApprove.getString("approvedBy");
                                                }

                                                if(jsonObjectApprove.has("priceAmount")){
                                                    jsonObjectApprove_fiatAmount=jsonObjectApprove.getString("priceAmount");
                                                }

                                                if(jsonObjectApprove.has("fiatTotalAmount")){
                                                    jsonObjectApprove_fiatTotalAmount=jsonObjectApprove.getString("fiatTotalAmount");
                                                }

                                                if(jsonObjectApprove.has("priceCurrency")){
                                                    jsonObjectApprove_currency=jsonObjectApprove.getString("priceCurrency");
                                                }

                                                if(jsonObjectApprove.has("payoutId")){
                                                    jsonObjectApprove_payoutId=jsonObjectApprove.getString("payoutId");
                                                }

                                                if(jsonObjectApprove.has("status")){
                                                    jsonObjectApprove_status=jsonObjectApprove.getString("status");
                                                }


                                                if (jsonObjectApprove_status.equalsIgnoreCase("NEW") || jsonObjectApprove_status.equalsIgnoreCase("QUEUED"))
                                                {
                                                    transactionLogger.error("inside jsonObjectApprove new and queued condition");
                                                    commResponseVO.setStatus("pending");
                                                    commResponseVO.setTransactionStatus("pending");
                                                    commResponseVO.setRemark("SYS:Payout is Pending bcz it is in " + jsonObjectApprove_status + " state.");
                                                    commResponseVO.setDescription("SYS:Payout is Pending bcz it is in " + jsonObjectApprove_status + " state.");
                                                }
                                                else if (jsonObjectApprove_status.equalsIgnoreCase("SENT"))
                                                {
                                                    commResponseVO.setStatus("success");
                                                    commResponseVO.setTransactionStatus("success");
                                                    commResponseVO.setRemark("Payout success");
                                                    commResponseVO.setDescription("Payout success");
                                                }
                                                else
                                                {
                                                    commResponseVO.setStatus("pending");
                                                    commResponseVO.setTransactionStatus("pending");
                                                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                                                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
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
        catch (Exception e)
        {
            transactionLogger.error("Exception----"+trackingId+"--->", e);
            PZExceptionHandler.raiseTechnicalViolationException(BitClearPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return commResponseVO;
    }

    public GenericResponseVO processPayoutInquiry(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processPayout inquiry------->");

        CommRequestVO commRequestVO       = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO   = new Comm3DResponseVO();
        GatewayAccount gatewayAccount     = GatewayAccountService.getGatewayAccount(accountId);
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        String REQUEST_URL          = "";
        Functions functions         = new Functions();
        BitClearUtils bitClearUtils = new BitClearUtils();

        boolean isTest                  = gatewayAccount.isTest();
        String apiKeyName               = gatewayAccount.getFRAUD_FTP_USERNAME();
        String apiKeySecret             = gatewayAccount.getFRAUD_FTP_PASSWORD();

        String payOutKeyId      = commTransactionDetailsVO.getPreviousTransactionId();
        JSONObject jsonObject   = new JSONObject();

        try
        {
            String base64Credentials = apiKeyName + ":" + apiKeySecret;
            String authentication    = new String(Base64.encode(base64Credentials.getBytes("utf-8")));

            if (isTest)
            {
                REQUEST_URL = RB.getString("PAYOUT_INQUIRY_TEST_URL") + payOutKeyId ;
            }
            else
            {
                REQUEST_URL = RB.getString("PAYOUT_INQUIRY_LIVE_URL") + payOutKeyId;
            }

            transactionLogger.error("payoutInquiryRequest------> "+ trackingId+" " +REQUEST_URL);

            String responeString    = BitClearUtils.doGetHttpConnection(REQUEST_URL, authentication);

            transactionLogger.error("payoutInquiryResponse------> "+ trackingId +" "+ responeString);

            String address          = "";
            String comment          = "";
            String createdTime      = "";
            String expirationTime   = "";
            String externalPayoutId = "";
            String payoutId         = "";
            String priceAmount      = "";
            String priceCurrency    = "";
            String status           = "";
            String transferAmount   = "";
            String transactionId   = "";
            String transferCurrency = "";
            String sentTime = "";

            if(functions.isValueNull(responeString) && functions.isJSONValid(responeString)){
                JSONObject responseJOSN = new JSONObject(responeString);

                if(responseJOSN.has("address")){
                    address = responseJOSN.getString("address");
                }
                if(responseJOSN.has("comment")){
                    comment = responseJOSN.getString("comment");
                }
                if(responseJOSN.has("createdTime")){
                    createdTime = responseJOSN.getString("createdTime");
                }
                if(responseJOSN.has("expirationTime")){
                    expirationTime = responseJOSN.getString("expirationTime");
                }
                if(responseJOSN.has("externalPayoutId")){
                    externalPayoutId = responseJOSN.getString("externalPayoutId");
                }
                if(responseJOSN.has("payoutId")){
                    payoutId = responseJOSN.getString("payoutId");
                }
                if(responseJOSN.has("priceAmount")){
                    priceAmount = responseJOSN.getString("priceAmount");
                }
                if(responseJOSN.has("priceCurrency")){
                    priceCurrency = responseJOSN.getString("priceCurrency");
                }
                if(responseJOSN.has("status")){
                    status = responseJOSN.getString("status");
                }
                if(responseJOSN.has("transferAmount")){
                    transferAmount = responseJOSN.getString("transferAmount");
                }
                if(responseJOSN.has("transferCurrency")){
                    transferCurrency = responseJOSN.getString("transferCurrency");
                }
                if(responseJOSN.has("transactionId")){
                    transactionId = responseJOSN.getString("transactionId");
                }
                if(responseJOSN.has("sentTime")){
                    sentTime = responseJOSN.getString("sentTime");
                }

                if("SENT".equalsIgnoreCase(status)){
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setRemark(status);
                    commResponseVO.setAmount(priceAmount);
                    if(functions.isValueNull(comment)){
                        commResponseVO.setDescription(comment);
                    }else{
                        commResponseVO.setDescription(status);
                    }

                    commResponseVO.setTransactionId(payoutId);
                    commResponseVO.setCurrency(priceCurrency);
                    commResponseVO.setResponseHashInfo(transactionId);
                    commResponseVO.setMerchantId(apiKeyName);
                    commResponseVO.setResponseTime(createdTime);
                    commResponseVO.setBankTransactionDate(createdTime);
                }else if("EXPIRED".equalsIgnoreCase(status) || "FAILED".equalsIgnoreCase(status)){
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setAmount(priceAmount);
                    commResponseVO.setRemark(status);
                    if(functions.isValueNull(comment)){
                        commResponseVO.setDescription(comment);
                    }else{
                        commResponseVO.setDescription(status);
                    }
                    commResponseVO.setTransactionId(payoutId);
                    commResponseVO.setCurrency(priceCurrency);
                    commResponseVO.setResponseHashInfo(transactionId);
                    commResponseVO.setMerchantId(apiKeyName);
                    commResponseVO.setResponseTime(createdTime);
                    commResponseVO.setBankTransactionDate(createdTime);
                }else if("NEW".equalsIgnoreCase(status) || "QUEUED".equalsIgnoreCase(status)){
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    if(functions.isValueNull(comment)){
                        commResponseVO.setDescription(comment);
                    }else{
                        commResponseVO.setDescription(status);
                    }
                    commResponseVO.setRemark(status);
                    commResponseVO.setAmount(priceAmount);
                    commResponseVO.setTransactionId(payoutId);
                    commResponseVO.setCurrency(priceCurrency);
                    commResponseVO.setResponseHashInfo(transactionId);
                    commResponseVO.setMerchantId(apiKeyName);
                    commResponseVO.setResponseTime(createdTime);
                    commResponseVO.setBankTransactionDate(createdTime);
                }else{
                    commResponseVO.setCurrency(priceCurrency);
                    commResponseVO.setMerchantId(apiKeyName);
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                }

            }else {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
            }
            commResponseVO.setTransactionType(PZProcessType.PAYOUT.toString());


        }
        catch (Exception e)
        {
            transactionLogger.error("processPayoutInquiry Exception----"+trackingId+" ---> ", e);
            PZExceptionHandler.raiseTechnicalViolationException(BitClearPaymentGateway.class.getName(), "processPayoutInquiry()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return commResponseVO;

    }

}