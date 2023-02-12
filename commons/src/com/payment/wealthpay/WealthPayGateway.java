package com.payment.wealthpay;

import com.directi.pg.AuditTrailVO;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.TigerGatePayRequestVO;
import com.manager.PaymentManager;
import com.payment.Enum.PZProcessType;
import com.payment.Rubixpay.RubixpayPaymentProcess;
import com.payment.Rubixpay.RubixpayUtils;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.payneteasy.PayneteasyUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import com.payment.bhartiPay.BhartiPayUtils;
import com.payment.doku.DokuUtils;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.nestpay.NestPayPaymentGateway;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;
import qikpay.QikPayUtils;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Krishna on 15-Jul-21.
 */
public class WealthPayGateway extends AbstractPaymentGateway
{
    public WealthPayGateway(String accountId)
    {
        this.accountId = accountId;
    }

    private static TransactionLogger transactionlogger = new TransactionLogger(WealthPayGateway.class.getName());
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.wealthpay");
    public static final String GATEWAY_TYPE = "wealthpay";

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of WealthPayGateway ......");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        Functions functions = new Functions();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        transactionlogger.error("CardType from transactionDetailsVO is-------------------> " + transactionDetailsVO.getCardType());
        transactionlogger.error("payment_Card from transactionDetailsVO is-------------------> " + transactionDetailsVO.getPaymentType());
        String payment_Card = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        String cardType = GatewayAccountService.getCardType(transactionDetailsVO.getCardType());

        transactionlogger.error("payment_Card  is-------------------> "+payment_Card);
        transactionlogger.error("cardType  is-------------------> "+cardType);
        boolean isTest = gatewayAccount.isTest();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = new Date();

        String merchantCode = gatewayAccount.getMerchantId();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String apikey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String clientIP = commAddressDetailsVO.getIp();
        String memberId = trackingID;
        String currencyCode = transactionDetailsVO.getCurrency();
        String amount = transactionDetailsVO.getAmount();
        String note = trackingID;
        String redirectURL = RB.getString("TERM_URL") + trackingID;
        String callbackURL = RB.getString("NOTIFY_URL");
        String transactionTime = formatter.format(date);
        String depositType = "0";

        transactionlogger.error("before cardType ==== " + cardType);

        String depositChannel = WealthPayUtils.getPaymentType(cardType);
        String signature = "";
        String saleUrl = "";

        signature = WealthPayUtils.hashMac(merchantCode + trackingID + memberId + currencyCode + amount + note + redirectURL + callbackURL + clientIP + transactionTime + apikey);

        if (isTest)
        {
            saleUrl = RB.getString("TEST_SALE_URL");
        }
        else
        {
            saleUrl = RB.getString("LIVE_SALE_URL");
        }

        transactionlogger.error("merchantCode ==== " + merchantCode);
        transactionlogger.error("userName ==== " + userName);
        transactionlogger.error("apikey ==== " + apikey);
        transactionlogger.error("clientIP ==== " + clientIP);
        transactionlogger.error("memberId ==== " + memberId);
        transactionlogger.error("currencyCode ==== " + currencyCode);
        transactionlogger.error("amount ==== " + amount);
        transactionlogger.error("cardType ==== " + cardType);
        transactionlogger.error("note ==== " + note);
        transactionlogger.error("transactionTime ==== " + transactionTime);
        transactionlogger.error("depositType ==== " + depositType);
        transactionlogger.error("depositChannel ==== " + depositChannel);
        transactionlogger.error("signature ==== " + signature);
        transactionlogger.error("saleUrl ==== " + saleUrl);
        transactionlogger.error("redirectURL ==== " + redirectURL);
        transactionlogger.error("callbackURL ==== " + callbackURL);


        String request = ""
                + "MerchantCode=" + merchantCode
                + "&TransactionID=" + trackingID
                + "&MemberID=" + memberId
                + "&CurrencyCode=" + currencyCode
                + "&Amount=" + amount
                + "&Note=" + note
                + "&RedirectURL=" + redirectURL
                + "&CallbackURL=" + callbackURL
                + "&ClientIP=" + clientIP
                + "&TransactionTime=" + transactionTime
                + "&DepositType=" + depositType
                + "&DepositChannel=" + depositChannel
                + "&Signature=" + signature;

        transactionlogger.error("request =====" + trackingID + " -- " + request);

        String response= WealthPayUtils.doHttpPostConnection(saleUrl,request);
        transactionlogger.error("response for ===== " + trackingID + " -- " +response);

        commRequestVO.getTransDetailsVO().setTransactionDate(transactionTime);

        try
        {

            String authcode = "";
            String status   = "";
            String message   = "";

            if(functions.isValueNull(response) && response.contains("{"))
            {
                JSONObject jsonobj =new JSONObject(response);

                if (jsonobj.has("RedirectURL")){
                    redirectURL = jsonobj.getString("RedirectURL");
                }

                if(jsonobj.has("Status")){
                    status = jsonobj.getString("Status");
                }

                if(jsonobj.has("Message") && functions.isValueNull(jsonobj.getString("Message"))){
                    message = jsonobj.getString("Message");
                }

                 if("0000".equalsIgnoreCase(status) && functions.isValueNull(redirectURL))
                 {
                     commResponseVO.setStatus("pending3DConfirmation");
                       commResponseVO.setUrlFor3DRedirect(redirectURL);
                     commResponseVO.setRedirectUrl(redirectURL);
                     transactionlogger.error("redirecturl----------------------------> " + redirectURL);
                 }
                else
                 {
                     commResponseVO.setStatus("fail");
                     commResponseVO.setDescription(message);
                     commResponseVO.setErrorCode(status);
                     commResponseVO.setRemark(message);
                 }
            }else{
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }


        }
        catch (JSONException e)
        {
            transactionlogger.error("JSONException-------------------------> ",e);
        }

        return commResponseVO;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Inside wealthay processQuery ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String merchantCode = gatewayAccount.getMerchantId();
        boolean isTest = gatewayAccount.isTest();
        String currencyCode = commTransactionDetailsVO.getCurrency();
        String apikey = gatewayAccount.getFRAUD_FTP_PASSWORD().trim();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String transactionTime = commTransactionDetailsVO.getResponsetime();

        try
        {

            Date date = formatter.parse(transactionTime);
            transactionTime = formatter.format(date);



        transactionlogger.error("transactionTime === " + transactionTime);
        transactionlogger.error("currencyCode === " + currencyCode);
        transactionlogger.error("apikey === " + apikey);
        transactionlogger.error("merchantCode === " + merchantCode);
        transactionlogger.error("trackingID === " + trackingID);

        String signature = "";
        String inquiryUrl = "";


            signature = WealthPayUtils.hashMac(merchantCode + currencyCode + trackingID + transactionTime + apikey);

        if (isTest)
        {
            inquiryUrl = RB.getString("TEST_INQUIRY_URL");
        }
        else
        {
            inquiryUrl = RB.getString("LIVE_INQUIRY_URL");
        }

        transactionlogger.error("inquiryUrl in processQuery ===== " + inquiryUrl);

            String request = ""
                    + "MerchantCode=" + merchantCode
                    + "&TransactionID=" + trackingID
                    + "&CurrencyCode=" + currencyCode
                    + "&TransactionTime=" + transactionTime
                    + "&Signature=" + signature;

        transactionlogger.error("WealthPayGateway Inquiry Request ===== " + trackingID + " ---- " + request);

        String response = WealthPayUtils.doHttpPostConnection(inquiryUrl, request);
        transactionlogger.error("WealthPayGateway Inquiry Response ===== " + trackingID + " ---- " + response);

            if (functions.isValueNull(response) && response.contains("{"))
            {
                String status = "";
                String amount = "";
                String message = "";
                String currency = "";
                String transactionID = "";

                JSONObject jsonObject = new JSONObject(response);

                if(jsonObject != null)
                {
                    if (jsonObject.has("Status") && functions.isValueNull(jsonObject.getString("Status")))
                    {
                        status = jsonObject.getString("Status");
                    }

                    if (jsonObject.has("Amount") && functions.isValueNull(jsonObject.getString("Amount")))
                    {
                        amount = jsonObject.getString("Amount");
                    }

                    if (jsonObject.has("Message") && functions.isValueNull(jsonObject.getString("Message")))
                    {
                        message = jsonObject.getString("Message");
                    }

                    if (jsonObject.has("Currency") && functions.isValueNull(jsonObject.getString("Currency")))
                    {
                        currency = jsonObject.getString("Currency");
                    }
                     if (jsonObject.has("TransactionID") && functions.isValueNull(jsonObject.getString("TransactionID")))
                    {
                        transactionID = jsonObject.getString("TransactionID");
                    }

                    if("0000".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("Success");
                        commResponseVO.setTransactionStatus("Success");
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription("Transaction Successful");
                        commResponseVO.setAmount(amount);
                        commResponseVO.setTransactionId(transactionID);
                        commResponseVO.setErrorCode(status);
                        commResponseVO.setAuthCode(status);
                        commResponseVO.setMerchantId(merchantCode);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    }
                    else if(status.equalsIgnoreCase("1003"))
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("pending");
                        commResponseVO.setTransactionStatus("pending");
                        commResponseVO.setRemark(message);
                        commResponseVO.setAmount(amount);
                        commResponseVO.setTransactionId(transactionID);
                        commResponseVO.setErrorCode(status);
                        commResponseVO.setAuthCode(status);
                        commResponseVO.setMerchantId(merchantCode);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    }
                    else if(status.equalsIgnoreCase("1001") || status.equalsIgnoreCase("1002") || status.equalsIgnoreCase("2001")|| status.equalsIgnoreCase("2002") || status.equalsIgnoreCase("2003")|| status.equalsIgnoreCase("2004") || status.equalsIgnoreCase("2005") || status.equalsIgnoreCase("2100"))
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setDescription("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(message);
                        commResponseVO.setAmount(amount);
                        commResponseVO.setTransactionId(transactionID);
                        commResponseVO.setErrorCode(status);
                        commResponseVO.setAuthCode(status);
                        commResponseVO.setMerchantId(merchantCode);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    }
                    else
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }
                    commResponseVO.setMerchantId(merchantCode);
                    commResponseVO.setTransactionId(transactionID);
                    commResponseVO.setAuthCode(status);
                    commResponseVO.setTransactionStatus(message);
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setDescription(commResponseVO.getDescription());
                    commResponseVO.setRemark(message);
                    commResponseVO.setResponseTime(transactionTime);
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setTransactionId(transactionID);
                    commResponseVO.setErrorCode(status);

                }
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }

        }
        catch (Exception e)
        {
            transactionlogger.error("Error in creating control in processQuery ===== ", e);
        }
        return commResponseVO;
    }public GenericResponseVO processPayoutInquiry(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Inside wealthay processPayoutInquiry ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String merchantCode = gatewayAccount.getMerchantId();

        boolean isTest = gatewayAccount.isTest();
        String currencyCode = commTransactionDetailsVO.getCurrency();
        String apikey = gatewayAccount.getFRAUD_FTP_PASSWORD().trim();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String transactionTime = commTransactionDetailsVO.getResponsetime();

        try {

            Date date = formatter.parse(transactionTime);
            transactionTime = formatter.format(date);



        transactionlogger.error("transactionTime === " + transactionTime);
        transactionlogger.error("currencyCode === " + currencyCode);
        transactionlogger.error("apikey === " + apikey);
        transactionlogger.error("merchantCode === " + merchantCode);
        transactionlogger.error("trackingID === " + trackingID);

        String signature = "";
        String inquiryUrl = "";

            signature = WealthPayUtils.hashMac(merchantCode + currencyCode + trackingID + transactionTime + apikey);

        if (isTest)
        {
            inquiryUrl = RB.getString("TEST_PAYOUT_INQUIRY_URL");
        }
        else
        {
            inquiryUrl = RB.getString("LIVE_PAYOUT_INQUIRY_URL");
        }

        transactionlogger.error("inquiryUrl in processQuery ===== " + inquiryUrl);



            String request = ""
                    + "MerchantCode=" + merchantCode
                    + "&TransactionID=" + trackingID
                    + "&CurrencyCode=" + currencyCode
                    + "&TransactionTime=" + transactionTime
                    + "&Signature=" + signature;

        transactionlogger.error("WealthPayGateway Inquiry Request ===== " + trackingID + " ---- " + request);

        String response = WealthPayUtils.doHttpPostConnection(inquiryUrl, request);
        transactionlogger.error("WealthPayGateway Inquiry Response ===== " + trackingID + " ---- " + response);

            if (functions.isValueNull(response) && response.contains("{"))
            {
                String status = "";
                String amount = "";
                String message = "";
                String currency = "";
                String transactionID = "";

                JSONObject jsonObject = new JSONObject(response);

                if(jsonObject != null)
                {
                    if (jsonObject.has("Status") && functions.isValueNull(jsonObject.getString("Status")))
                    {
                        status = jsonObject.getString("Status");
                    }

                    if (jsonObject.has("Amount") && functions.isValueNull(jsonObject.getString("Amount")))
                    {
                        amount = jsonObject.getString("Amount");
                    }

                    if (jsonObject.has("Message") && functions.isValueNull(jsonObject.getString("Message")))
                    {
                        message = jsonObject.getString("Message");
                    }

                    if (jsonObject.has("Currency") && functions.isValueNull(jsonObject.getString("Currency")))
                    {
                        currency = jsonObject.getString("Currency");
                    }
                     if (jsonObject.has("TransactionID") && functions.isValueNull(jsonObject.getString("TransactionID")))
                    {
                        transactionID = jsonObject.getString("TransactionID");
                    }
                    if("0000".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("Success");
                        commResponseVO.setTransactionStatus("Success");
                        commResponseVO.setRemark("Transaction Successful");
                        commResponseVO.setDescription("Transaction Successful");
                        commResponseVO.setAmount(amount);
                        commResponseVO.setTransactionId(transactionID);
                        commResponseVO.setErrorCode(status);
                    }
                    else if (status.equalsIgnoreCase("1003"))
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("pending");
                        commResponseVO.setTransactionStatus("pending");
                        commResponseVO.setRemark(status);
                        commResponseVO.setAmount(amount);
                        commResponseVO.setTransactionId(transactionID);
                        commResponseVO.setErrorCode(status);
                    }
                    else if(status.equalsIgnoreCase("1001") || status.equalsIgnoreCase("1002") || status.equalsIgnoreCase("2001")|| status.equalsIgnoreCase("2002") || status.equalsIgnoreCase("2003")|| status.equalsIgnoreCase("2004") || status.equalsIgnoreCase("2005") || status.equalsIgnoreCase("2100"))
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setDescription("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(status);
                        commResponseVO.setAmount(amount);
                        commResponseVO.setTransactionId(transactionID);
                        commResponseVO.setErrorCode(status);
                    }
                    else
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }
                    commResponseVO.setMerchantId(merchantCode);
                    commResponseVO.setTransactionId(transactionID);
                    commResponseVO.setAuthCode(status);
                    commResponseVO.setTransactionStatus(message);
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setDescription(commResponseVO.getDescription());
                    commResponseVO.setRemark(message);
                    commResponseVO.setResponseTime(transactionTime);
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setTransactionId(transactionID);
                }
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }

        }
        catch (Exception e)
        {
            transactionlogger.error("Error in creating control in processQuery ===== ", e);
        }
        return commResponseVO;
    }

    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Wealthpay Autoredict in processAutoRedirect ---- ");
        String html = "";;
        Comm3DResponseVO transRespDetails = null;
        WealthPayUtils wealthPayUtils =new WealthPayUtils();
        CommRequestVO commRequestVO = wealthPayUtils.getWealthRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        WealthPayPaymentProcess wealthPayPaymentProcess = new WealthPayPaymentProcess();
        PaymentManager paymentManager = new PaymentManager();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        auditTrailVO.setActionExecutorId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        auditTrailVO.setActionExecutorName("CustomerCheckout");

        boolean isTest = gatewayAccount.isTest();
        try
        {
            transactionlogger.error("isService Flag ----- " + commonValidatorVO.getMerchantDetailsVO().getIsService());
            transactionlogger.error("autoredirect  flag----- " + commonValidatorVO.getMerchantDetailsVO().getAutoRedirect());
            transactionlogger.error("addressdetail  flag----- " + commonValidatorVO.getMerchantDetailsVO().getAddressDeatails());
            transactionlogger.error("terminal id  is ----- " + commonValidatorVO.getTerminalId());
            transactionlogger.error("tracking id  is ----- " + commonValidatorVO.getTrackingid());

            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = wealthPayPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("automatic redirect wealthpay form -- >> "+html);
            }
            else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("fail"))
            {
                paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", commonValidatorVO.getTrackingid(), auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                html="failed";
            }

        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in TapMioPaymentGateway--- ", e);
        }
        return html;
    }

    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside wealthay process payout-----");
        WealthPayRequestVO commRequestVO                         = (WealthPayRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions                                 = new Functions();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                      = gatewayAccount.isTest();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        String hostURL          = "";
        String merchantCode     = gatewayAccount.getMerchantId();
        String transactionID    = trackingId;
        String memberID         = trackingId;
        String branchName       = "";
        String Client_id        = trackingId;
        String beneficiaryName  = "";
        String accountNumber    = "";
        String type             = "";
        String remittanceAmount = "0.00";
        String bankCode         = "";
        String api_token        = gatewayAccount.getFRAUD_FTP_PATH();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String transactionTime = formatter.format(date);
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String apikey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String clientIP = commAddressDetailsVO.getIp();
        String currencyCode = commTransactionDetailsVO.getCurrency();
        String amount = commTransactionDetailsVO.getAmount();
        String note = trackingId;
        String callbackURL = RB.getString("NOTIFY_URL")+trackingId;
        String signature = "";
        JSONObject jsonObject   = new JSONObject();
        try
        {
            if (isTest)
            {
                hostURL         = RB.getString("PAYOUT_TEST_URL");
            }
            else
            {
                hostURL = RB.getString("PAYOUT_LIVE_URL");
            }

            if (functions.isValueNull(commTransactionDetailsVO.getCustomerBankAccountName()))
            {
                beneficiaryName = commTransactionDetailsVO.getCustomerBankAccountName();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankAccountNo()))
            {
                accountNumber = commTransactionDetailsVO.getBankAccountNo();
            }
            if (functions.isValueNull(commRequestVO.getBankCode()))
            {
                bankCode = commRequestVO.getBankCode();
            }
            if (functions.isValueNull(commRequestVO.getBranchName()))
            {
                branchName = commRequestVO.getBranchName();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getAmount()))
            {
                remittanceAmount = commTransactionDetailsVO.getAmount();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankTransferType()))
            {
                type = commTransactionDetailsVO.getBankTransferType();
            }

            transactionlogger.error("beneficiaryName---> " + beneficiaryName);
            transactionlogger.error("accountNo---> " + accountNumber);
            transactionlogger.error("bankCode---> " + bankCode);
            transactionlogger.error("branch name---> " + branchName);
            transactionlogger.error("amount---> " + remittanceAmount);
            transactionlogger.error("transferType---> " + type);
            transactionlogger.error("hostURL---> " + hostURL);
            transactionlogger.error("merchantCode ==== " + merchantCode);
            transactionlogger.error("apikey ==== " + apikey);
            transactionlogger.error("clientIP ==== " + clientIP);
            transactionlogger.error("memberId ==== " + memberID);
            transactionlogger.error("currencyCode ==== " + currencyCode);
            transactionlogger.error("amount ==== " + amount);
            transactionlogger.error("note ==== " + note);
            transactionlogger.error("transactionTime ==== " + transactionTime);
            transactionlogger.error("signature ==== " + signature);
            transactionlogger.error("payoutUrl ==== " + hostURL);
            transactionlogger.error("callbackURL ==== " + callbackURL);


            signature = WealthPayUtils.hashMac(merchantCode + trackingId + memberID + currencyCode +bankCode + accountNumber + beneficiaryName +branchName + amount + note  + callbackURL + clientIP+ transactionTime  + apikey);

            String request = ""
                    + "MerchantCode=" + merchantCode
                    + "&TransactionID=" + trackingId
                    + "&MemberID=" + memberID
                    + "&CurrencyCode=" + currencyCode
                    + "&BankCode=" + bankCode
                    + "&ToAccountNumber=" + accountNumber
                    + "&ToAccountName=" + beneficiaryName
                    + "&ToBranch=" + branchName
                    + "&Amount=" + amount
                    + "&Note=" + note
                    + "&CallbackURL=" + callbackURL
                    + "&ClientIP=" + clientIP
                    + "&TransactionTime=" + transactionTime
                    + "&Signature=" + signature;

            transactionlogger.error("Wealthpay payout request ------ " + trackingId + " ---- " + request);

            String response= WealthPayUtils.doHttpPostConnection(hostURL,request);
            transactionlogger.error("Payout response for ===== " + trackingId + " -- " +response);


            String rate                 = "";
            String resamount            = "";
            String status               = "";
            String submissionID         = "";
            String restransactionID     = "";
            String message              = "";

            if(functions.isValueNull(response)){
                if(functions.isValueNull(response) && functions.isJSONValid(response))
                {
                    JSONObject payoutResponse   = new JSONObject(response);
                    if(payoutResponse.has("rate") && functions.isValueNull(payoutResponse.getString("rate"))){
                        rate=payoutResponse.getString("rate");
                    }

                    if(payoutResponse.has("amount") && functions.isValueNull(payoutResponse.getString("amount"))){
                        resamount=payoutResponse.getString("amount");
                    }
                    if(payoutResponse.has("status") && functions.isValueNull(payoutResponse.getString("status"))){
                        status=payoutResponse.getString("status");
                    }
                    if(payoutResponse.has("submissionID") && functions.isValueNull(payoutResponse.getString("submissionID"))){
                        submissionID=payoutResponse.getString("submissionID");
                    }
                    if(payoutResponse.has("transactionID") && functions.isValueNull(payoutResponse.getString("transactionID"))){
                        restransactionID=payoutResponse.getString("transactionID");
                    }
                    if(payoutResponse.has("message") && functions.isValueNull(payoutResponse.getString("message"))){
                        message=payoutResponse.getString("message");
                    }
                }

                if (status.equalsIgnoreCase("0000"))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(status);

                }
                else if ( status.equalsIgnoreCase("1003"))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(status);

                }
                else if(status.equalsIgnoreCase("1001") || status.equalsIgnoreCase("1002") || status.equalsIgnoreCase("2001")|| status.equalsIgnoreCase("2002") || status.equalsIgnoreCase("2003")|| status.equalsIgnoreCase("2004") || status.equalsIgnoreCase("2005") || status.equalsIgnoreCase("2100"))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setErrorCode(status);

                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }
                transactionlogger.error("---transactionId---> " + restransactionID + " ------ " + " paymentId---> " + restransactionID);
                commResponseVO.setTransactionId(restransactionID);
                commResponseVO.setResponseHashInfo(status);
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }


        }
        catch (JSONException e)
        {
            transactionlogger.error("JSONException----trackingid---->"+trackingId+"--->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("WealthPay","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

}
