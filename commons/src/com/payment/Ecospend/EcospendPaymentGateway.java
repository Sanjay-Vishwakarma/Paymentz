package com.payment.Ecospend;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.PaymentManager;
import com.manager.vo.MerchantDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.appletree.AppleTreeCellulantUtils;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.json.JSONArray;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.UUID;
import java.util.*;

/**
 * Created by Admin on 31-Aug-21.
 */
public class EcospendPaymentGateway extends AbstractPaymentGateway
{

    public static final String GATEWAY_TYPE             = "ecospend";
    private static ResourceBundle RB                    = LoadProperties.getProperty("com.directi.pg.ecospend");
    private static TransactionLogger transactionLogger  = new TransactionLogger(EcospendPaymentGateway.class.getName());

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public EcospendPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale (String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Inside processSale-----");
        EcospendRequestVo commRequestVO                     = (EcospendRequestVo) requestVO;
        EcospendResponseVO commResponseVO                   = new EcospendResponseVO();
        CommMerchantVO commMerchantVO                       = commRequestVO.getCommMerchantVO();
        EcospendUtils ecospendUtils                         = new EcospendUtils();
        Functions functions                                 = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        EcospendGatewayAccountVO gatewayAccountVO           = ecospendUtils.getAccountDetails(accountId);

        String getRefund    = "false";
        String forPayout    = "false";
        String payment_type = "Auto";
        String SaleUrl      = "";
        String AccessToken  = commRequestVO.getAccessTken();
        boolean isTest      = gatewayAccount.isTest();
        if(isTest){
            SaleUrl = RB.getString("TEST_INIT_URL");
        }else{
            SaleUrl = RB.getString("LIVE_INIT_URL");
        }
        transactionLogger.error("authURL--------------for--trackingid::::" + trackingID + "--" + SaleUrl);

        String redirect_url="";
        transactionLogger.error("commRequestVO.getHostUrl()  "+commRequestVO.getHostUrl());
        transactionLogger.error("commMerchantVO.getHostUrl()  "+commMerchantVO.getHostUrl());
        if (functions.isValueNull(commRequestVO.getHostUrl()))
        {
            redirect_url = "https://" + commRequestVO.getHostUrl() + RB.getString("REDIRECT_URL");
            transactionLogger.error("From HOST_URL----" + redirect_url);
        }
        else if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            redirect_url = "https://" + commMerchantVO.getHostUrl() + RB.getString("REDIRECT_URL");
            transactionLogger.error("else if From HOST_URL----" + redirect_url);
        }
        else
        {
            redirect_url = RB.getString("TERM_URL");
            transactionLogger.error("From RB TERM_URL ----" + redirect_url);
        }

        String amount       = "";
        String currency     = "";
        String description  = commTransactionDetailsVO.getOrderDesc();
        amount              = commTransactionDetailsVO.getAmount();
        currency            = commTransactionDetailsVO.getCurrency();

        String bankid       = commRequestVO.getBankid();

        String creditortype     = gatewayAccountVO.getCreditor_account_type();
        String creditorID       = gatewayAccountVO.getCreditor_account_identification();
        String creditorName     = gatewayAccountVO.getCreditor_account_owner_name();
        String CreditorBic      = gatewayAccountVO.getCreditor_account_bic();
        String debtortype       = commRequestVO.getDebtortype();
        String debtorID         = commRequestVO.getDebtorID();
        String debtorName           = commRequestVO.getDebtorName();
        String debtorBic            = commRequestVO.getDebtorBic();
        String psuid                = gatewayAccountVO.getPsu_id();
        String paymentrails         = "FasterPayments";
        String scheduled_for_date   = commRequestVO.getScheduledForDate();

         String payment_url     = "";
         String status          = "";
         String Respreference   = "";
         String rescrtype       = "";
         String respdrtype      = "";
         String Respamount      = "";
         String respcrowner_name= "";
         String respdrowner_name= "";
         String resdrcurrency   = "";
         String rescrcurrency   = "";
         String respcridentification    ="";
         String resdridentification     ="";
         String rescrbic    = "";
         String respdrbic   = "";
         String psu_id      = "";
         String payment_rails   = "";
         String is_refund       = "";
         String bank_id         = "";
         String id              = "";
        String scheduled_for    = "";
        String error            = "";
        String errordescription = "";
         String transactionStatus   = "";
        String merchant_id          = commTransactionDetailsVO.getToId();
        try
        {
            if(commRequestVO.getIsRefundAllow() != null){
                getRefund    = commRequestVO.getIsRefundAllow();
            }

            StringBuffer paymentRequest = new StringBuffer();
            String paymentResponse      = "";

            paymentRequest.append("{" +
                    "\"bank_id\":\"" + bankid + "\"," +
                    "\"amount\":\"" + amount + "\"," +
                    "\"currency\":\"" + currency + "\"," +
                    "\"description\":\"" + description + "\"," +
                    "\"reference\":\"" + trackingID + "\"," +
                    "\"redirect_url\":\"" + redirect_url + "\"," +
                    "\"merchant_id\":\"" + merchant_id + "\"," +
                    "\"merchant_user_id\":\"" + merchant_id + "\"" +
                    "," +
                    "\"creditor_account\":{" +
                    "\"type\":\"" + creditortype + "\"," +
                    "\"identification\":\"" + creditorID + "\"," +
                    "\"owner_name\":\""+creditorName+"\"," +
                    "\"currency\":\"" + currency + "\",");
            if(functions.isValueNull(CreditorBic))
            {
                paymentRequest.append("\"bic\":\"" + CreditorBic + "\"" +
                        "},");
            }else{
                paymentRequest.append("\"bic\":null" +
                        "},");
            }
            if(functions.isValueNull(debtortype))
            {
                paymentRequest.append(
                        "\"debtor_account\":{" +
                                "\"type\":\"" + debtortype + "\"," +
                                "\"identification\":\"" + debtorID + "\"," +
                                "\"owner_name\":\"" + debtorName + "\"," +
                                "\"currency\":\"" + currency + "\"," );
                if(functions.isValueNull(debtorBic))
                {
                    paymentRequest.append("\"bic\":\"" + debtorBic + "\"" +
                            "},");
                }else{
                    paymentRequest.append("\"bic\":null" +
                            "},");
                }
            }

            paymentRequest.append("\"payment_option\":{" +
                            "\"get_refund_info\":"+getRefund+"," +
                            "\"for_payout\":"+forPayout+"," );
            if(functions.isValueNull(scheduled_for_date) /*&& paymentMethod.equalsIgnoreCase("Scheduled Payment")*/)
            {
                paymentRequest.append("\"scheduled_for\":\""+scheduled_for_date+"\"," );
            }
            paymentRequest.append(
                            "\"psu_id\":\""+psuid+"\"," +
                            "\"payment_rails\":\""+paymentrails+"\"" +
                            "}," +
                            "\"payment_type\":\"" + payment_type + "\"" +
                            "}");
            transactionLogger.error("Paymentrequest =====" + trackingID + " -- " + paymentRequest);
            paymentResponse = EcospendUtils.doPostHTTPSURLConnectionClient(SaleUrl, String.valueOf(paymentRequest),AccessToken);
            transactionLogger.error("Paymentresponse for ===== " + trackingID + " -- " +paymentResponse);
            if (functions.isValueNull(paymentResponse) && paymentResponse.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(paymentResponse);
                if (jsonObject != null)
                {
                    if (jsonObject.has("id"))
                    {
                        id = jsonObject.getString("id");
                    }
                    if (jsonObject.has("payment_url"))
                    {
                        payment_url = jsonObject.getString("payment_url");
                    }
                    if (jsonObject.has("bank_id"))
                    {
                        bank_id = jsonObject.getString("bank_id");
                    }
                    if (jsonObject.has("status"))
                    {
                        status = jsonObject.getString("status");
                    }
                    if (jsonObject.has("reference"))
                    {
                        Respreference = jsonObject.getString("reference");
                    }
                    if (jsonObject.has("is_refund"))
                    {
                        is_refund = jsonObject.getString("is_refund");
                    }
                    if (jsonObject.has("amount"))
                    {
                        Respamount = jsonObject.getString("amount");
                    }
                    if (jsonObject.has("scheduled_for"))
                    {

                        error = jsonObject.getString("scheduled_for");
                    }
                    if (jsonObject.has("description"))
                    {
                        errordescription = jsonObject.getString("description");
                    }
                    if (jsonObject.has("scheduled_for"))
                    {

                        scheduled_for = jsonObject.getString("scheduled_for");
                    }
                    if (jsonObject.has("debtor_account") && functions.isValueNull(jsonObject.getString("debtor_account")))
                    {
                        if(functions.isValueNull(jsonObject.getJSONObject("debtor_account").getString("type")))
                        {
                            respdrtype = jsonObject.getJSONObject("debtor_account").getString("type");
                        }
                        if(functions.isValueNull(jsonObject.getJSONObject("debtor_account").getString("identification")))
                        {
                            resdridentification = jsonObject.getJSONObject("debtor_account").getString("identification");
                        }
                        if(functions.isValueNull(jsonObject.getJSONObject("debtor_account").getString("owner_name")))
                        {
                            respdrowner_name = jsonObject.getJSONObject("debtor_account").getString("owner_name");
                        }
                        if(functions.isValueNull(jsonObject.getJSONObject("debtor_account").getString("currency")))
                        {
                            resdrcurrency = jsonObject.getJSONObject("debtor_account").getString("currency");
                        }
                        if(jsonObject.getJSONObject("debtor_account").has("bic") && functions.isValueNull(jsonObject.getJSONObject("debtor_account").getString("bic")) )
                        {
                            rescrbic    = jsonObject.getJSONObject("debtor_account").getString("bic");
                        }
                    }
                    if (jsonObject.has("creditor_account") && functions.isValueNull(jsonObject.getString("creditor_account")))
                    {
                        if(functions.isValueNull(jsonObject.getJSONObject("creditor_account").getString("type")))
                        {
                            rescrtype = jsonObject.getJSONObject("creditor_account").getString("type");
                        }
                        if(functions.isValueNull(jsonObject.getJSONObject("creditor_account").getString("identification")))
                        {
                            respcridentification = jsonObject.getJSONObject("creditor_account").getString("identification");
                        }
                        if(functions.isValueNull(jsonObject.getJSONObject("creditor_account").getString("owner_name")))
                        {
                            respcrowner_name = jsonObject.getJSONObject("creditor_account").getString("owner_name");
                        }
                        if(functions.isValueNull(jsonObject.getJSONObject("creditor_account").getString("currency")))
                        {
                            rescrcurrency = jsonObject.getJSONObject("creditor_account").getString("currency");
                        }
                        if(jsonObject.getJSONObject("creditor_account").has("bic") && functions.isValueNull(jsonObject.getJSONObject("creditor_account").getString("bic")) )
                        {
                            rescrbic = jsonObject.getJSONObject("creditor_account").getString("bic");
                        }
                    }
                    if (jsonObject.has("payment_option") && functions.isValueNull(jsonObject.getString("payment_option")))
                    {
                        if(functions.isValueNull(jsonObject.getJSONObject("payment_option").getString("psu_id")))
                        {
                            psu_id = jsonObject.getJSONObject("payment_option").getString("psu_id");
                        }
                        if(functions.isValueNull(jsonObject.getJSONObject("payment_option").getString("payment_rails")))
                        {
                             payment_rails = jsonObject.getJSONObject("payment_option").getString("payment_rails");
                        }
                    }

                    if(status.equalsIgnoreCase("AwaitingAuthorization")){
                        transactionStatus="pending";
                        commResponseVO.setStatus("pending");
                        commResponseVO.setTransactionId(id);
                        commResponseVO.setBank_id(bank_id);
                        commResponseVO.setCreditortype(rescrtype);
                        commResponseVO.setCreditorName(respcrowner_name);
                        commResponseVO.setCreditorID(respcridentification);
                        commResponseVO.setCreditorCurrency(rescrcurrency);
                        commResponseVO.setCreditorBic(rescrbic);
                        commResponseVO.setDebtortype(respdrtype);
                        commResponseVO.setDebtorName(respdrowner_name);
                        commResponseVO.setDebtorID(resdridentification);
                        commResponseVO.setDebtorCurrency(resdrcurrency);
                        commResponseVO.setDebtorBic(respdrbic);
                        commResponseVO.setPsuid(psu_id);
                        commResponseVO.setPaymentrails(payment_rails);
                        commResponseVO.setGetrefundinfo(is_refund);
                        commResponseVO.setReference(Respreference);
                        commResponseVO.setScheduled_for(scheduled_for);
                        commResponseVO.setRedirectUrl(payment_url);
                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                        if(functions.isValueNull(error))
                        {
                            commResponseVO.setDescription(error);
                            commResponseVO.setRemark(errordescription);
                        }else{
                            commResponseVO.setDescription("Transaction Declined");
                            commResponseVO.setRemark("Transaction Declined");
                            }
                    }
                }
                else
                {
                    transactionLogger.error("Transaction Failed due to null json");
                    commResponseVO.setStatus("failed");
                    commResponseVO.setRemark("Transaction Declined");
                    commResponseVO.setDescription("Transaction Declined");
                }
            }
            else
            {
                transactionLogger.error("Transaction Failed due to null response");
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Invalid Response");
                commResponseVO.setDescription("Invalid Response");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("JSONException---" + trackingID + "-->", e);
        }
        return commResponseVO;
    }


    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("EcospendPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public GenericResponseVO processInitialSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Inside processInitialSale-----");
        EcospendResponseVO commResponseVO = new EcospendResponseVO();
        Functions functions             = new Functions();
        CommRequestVO commRequestVO     = (EcospendRequestVo) requestVO;
        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        EcospendUtils ecospendUtils     = new EcospendUtils();
        EcospendGatewayAccountVO ecospendGatewayAccountVO = ecospendUtils.getAccountDetails(accountId);


        StringBuffer request    = new StringBuffer();
        boolean isTest          = gatewayAccount.isTest();
        UUID uuid1              = UUID.randomUUID();
        String RequestId        = uuid1.toString();
        String client_id        = gatewayAccount.getMerchantId();
        String client_secret    = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String grant_type       = gatewayAccount.getCHARGEBACK_FTP_PATH();
        String purpose  = "";
        String scope    = "";
        String authURL  = "";
        String banksURL = "";
        String bankid   = null;
        String bankname = null;
        String bankLogo = null;
        String refund_supported = null;
        String countryCode      = null;


        try
        {

            if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                countryCode = commAddressDetailsVO.getCountry();
            }
            if(functions.isValueNull(countryCode) && countryCode.length()==3){
                countryCode = AppleTreeCellulantUtils.getCountryTwoDigit(countryCode);
            }

            String cardtypeid   = commTransactionDetailsVO.getCardType();
            String cardtype     = GatewayAccountService.getCardType(cardtypeid);

            if ("INSTANTPAYMENT".equalsIgnoreCase(cardtype))
            {
                purpose = "DomesticPayment";
            }
            else if ("STANDINGORDERS".equalsIgnoreCase(cardtype))
            {
                purpose = "DomesticStandingOrder";
            }
            else if ("SCHEDULEPAYMENTS".equalsIgnoreCase(cardtype))
            {
                purpose = "DomesticScheduledPayment";
            }
            else
            {
                purpose = "DomesticPayment";
            }

            if(isTest)
            {
                authURL    = RB.getString("TEST_AUTH_URL");
                banksURL   = RB.getString("TEST_BANKS_URL");
            }else{
                authURL    = RB.getString("LIVE_AUTH_URL");
                banksURL   = RB.getString("LIVE_BANKS_URL");
            }
            transactionLogger.error("authURL--------------for--trackingid::::" + trackingID + "--" + authURL);

            StringBuffer authRequest    = new StringBuffer();
            String authResponse         = "";

            authRequest.append("grant_type=" + grant_type+ "&client_id=" + client_id + "&client_secret=" + client_secret /*+ "&scope="+scope*/);
            transactionLogger.error("authRequest--------------for--trackingid::::" + trackingID + "--" + authRequest);

            authResponse = EcospendUtils.getAccessToken(authURL, authRequest.toString(), RequestId);
            transactionLogger.error("authResponse--------------for--trackingid::::" + trackingID + "--" + authResponse);

            if (functions.isValueNull(authResponse) && authResponse.contains("{"))
            {
                String access_token = "";
                String expires_in   = "";
                String token_type   = "";
                String scopeResp    = "";

                JSONObject jsonObject = new JSONObject(authResponse);

                if (jsonObject.has("access_token"))
                {
                    access_token = jsonObject.getString("access_token");
                    commResponseVO.setAccessTken(access_token);
                }
                if (jsonObject.has("expires_in"))
                {
                    expires_in = jsonObject.getString("expires_in");
                }
                if (jsonObject.has("token_type"))
                {
                    token_type = jsonObject.getString("token_type");
                }
                if (jsonObject.has("scope"))
                {
                    scopeResp = jsonObject.getString("scope");
                }


                StringBuffer banksRequest = new StringBuffer();
                String banksResponse = "";

                if (functions.isValueNull(access_token))
                {

                    banksRequest.append("purpose=" + purpose + "" +
                            "&country_iso_code=" + countryCode);
                    transactionLogger.error("banksRequest--------------for--trackingid::::" + trackingID + "--" + banksRequest);

                    banksResponse = EcospendUtils.GETHTTPSURLConnectionClientBanks(banksURL, String.valueOf(request), access_token);
                    transactionLogger.error("banksResponse--------------for--trackingid::::" + trackingID + "--" + banksResponse);

                    JSONObject jsonObjectbank = new JSONObject(banksResponse);
                    if (jsonObjectbank.has("data")&& functions.isValueNull(jsonObjectbank.getString("data")))
                    {
                        JSONArray jsonArray = jsonObjectbank.getJSONArray("data");
                        int length = jsonArray.length();
                        TreeMap<String, String> Bankid = new TreeMap<>();
                        for (int i = 0; i < length; i++)
                        {
                                bankid              = jsonArray.getJSONObject(i).getString("bank_id");
                                bankname            = jsonArray.getJSONObject(i).getString("name");
                                bankLogo            = jsonArray.getJSONObject(i).getString("logo");
                                refund_supported    = jsonArray.getJSONObject(i).getString("refund_supported");
                                Bankid.put(bankid, bankname+"_"+bankLogo+"_"+refund_supported);
                        }


                        commResponseVO.setBankid(Bankid);
                        commResponseVO.setAccessTken(access_token);
                        commResponseVO.setCreditorID(ecospendGatewayAccountVO.getCreditor_account_identification().substring(6));
                        commResponseVO.setCreditorName(ecospendGatewayAccountVO.getCreditor_account_owner_name());
                        commResponseVO.setCreditorBic(ecospendGatewayAccountVO.getCreditor_account_identification().substring(0,6));
                      /*  commRequestVO.setAccessTken(transRespDetails.getAccessTken());

                        commRequestVO.setCreditorID(transRespDetails.getCreditorID());
                        commRequestVO.setCreditorName(transRespDetails.getCreditorName());
                        commRequestVO.setCreditorBic(transRespDetails.getCreditorBic());*/
                    }
                }
            }

        }
        catch (Exception e)
        {
            transactionLogger.error("JSONException---"+trackingID+"-->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions             = new Functions();
        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                  = gatewayAccount.isTest();
        UUID uuid1                      = UUID.randomUUID();
        String RequestId                = uuid1.toString();
        String client_id        = gatewayAccount.getMerchantId();
        String client_secret    = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String grant_type       = gatewayAccount.getCHARGEBACK_FTP_PATH();
        String scope        = "";
        String inquiryUrl   = "";
        String standingOrdersUrl   = "";
        String paylinkUrl   = "";
        String authURL      = "";
        String url          = "";
        String paymentId     =  commTransactionDetailsVO.getPreviousTransactionId();
        StringBuffer authRequest = new StringBuffer();
        String authResponse     = "";
        try
        {
            if(isTest)
            {
                authURL                   = RB.getString("TEST_AUTH_URL");
                inquiryUrl                = RB.getString("TEST_INQUIRY_URL");
                standingOrdersUrl         = RB.getString("TEST_STANDING_ORDERS_URL");
                paylinkUrl                = RB.getString("TEST_PAYLINK_URL");
            }
            else
            {
                authURL                   = RB.getString("LIVE_AUTH_URL");
                inquiryUrl                = RB.getString("LIVE_INQUIRY_URL");
                standingOrdersUrl         = RB.getString("LIVE_STANDING_ORDERS_URL");
                paylinkUrl                = RB.getString("LIVE_PAYLINK_URL");
            }

            authRequest.append("grant_type=" + grant_type+ "&client_id=" + client_id + "&client_secret=" + client_secret /*+ "&scope="+scope*/);
            transactionLogger.error("Inquiry authRequest " + trackingID + " " + authRequest);

            authResponse = EcospendUtils.getAccessToken(authURL, authRequest.toString(), RequestId);
            transactionLogger.error("Inquiry authResponse " + trackingID + " " + authResponse);

            if (functions.isValueNull(authResponse) && EcospendUtils.isJSONValid(authResponse))
            {
                String access_token = "";

                JSONObject jsonObject = new JSONObject(authResponse);

                if (jsonObject.has("access_token"))
                {
                    access_token = jsonObject.getString("access_token");
                }

                String cardtypeid   = commTransactionDetailsVO.getCardType();
                String cardtype     = GatewayAccountService.getCardType(cardtypeid);

                if (functions.isValueNull(access_token))
                {
                    if ("STANDINGORDERS".equalsIgnoreCase(cardtype))
                    {
                        url =    standingOrdersUrl+"/"+paymentId;
                    }
                    else if ("PAYBYLINK".equalsIgnoreCase(cardtype))
                    {
                        url =    paylinkUrl+"/"+paymentId+"/payments";
                    }
                    else
                    {
                        url =    inquiryUrl+paymentId+"?MerchantId="+ trackingID;
                    }
                    transactionLogger.error("Inquiry Request " + trackingID + "--" + url);
                    String InquiryResponse = EcospendUtils.doGetHTTPSURLConnectionClient(url, access_token);
                    transactionLogger.error("Inquiry Response " + trackingID + "--" + InquiryResponse);

                    String transactionStatus    = "";
                    String respId               = "";
                    String bank_reference_id    = "";
                    String amount               = "";
                    String currency             = "";
                    String date_created         = "";
                    JSONObject refund_account   = null;

                    if(functions.isValueNull(InquiryResponse) && InquiryResponse.contains("[") && InquiryResponse.indexOf("[")== 0 ){
                        JSONArray jsonArray = new JSONArray(InquiryResponse);

                        if(jsonArray.length() >0 ){
                            InquiryResponse = jsonArray.getJSONObject(0).toString();
                        }
                    }

                    if(functions.isValueNull(InquiryResponse) && EcospendUtils.isJSONValid(InquiryResponse)){

                        JSONObject inquiryObject = new JSONObject(InquiryResponse);

                        if(inquiryObject.has("status")){
                            transactionStatus = inquiryObject.getString("status");
                        }
                        if(inquiryObject.has("id")){
                            respId = inquiryObject.getString("id");
                        }

                        if(inquiryObject.has("bank_reference_id")){
                            bank_reference_id = inquiryObject.getString("bank_reference_id");
                        }
                        if(inquiryObject.has("amount")){
                            amount = inquiryObject.getString("amount");
                        }
                        if(inquiryObject.has("currency")){
                            currency = inquiryObject.getString("currency");
                        }
                        if(inquiryObject.has("date_created")){
                            date_created = inquiryObject.getString("date_created");
                        }
                        if(inquiryObject.has("refund_account")){
                            refund_account = inquiryObject.getJSONObject("refund_account");
                            if(refund_account != null){
                                String type             = "";
                                String identification   = "";
                                String owner_name       = "";

                                if(refund_account.has("type")){
                                    type = refund_account.getString("type");
                                }
                                if(refund_account.has("identification")){
                                    identification = refund_account.getString("identification");
                                }
                                if(refund_account.has("owner_name")){
                                    owner_name = refund_account.getString("owner_name");
                                }
                                if(functions.isValueNull(type) && functions.isValueNull(identification) && functions.isValueNull(owner_name)){
                                    EcospendUtils.updateRefundAccountTransaction(trackingID,type,identification,owner_name);
                                }

                            }
                        }


                        if(transactionStatus.equalsIgnoreCase("Completed")|| transactionStatus.equalsIgnoreCase("Verified")){
                            commResponseVO.setStatus("success");
                            commResponseVO.setTransactionStatus("success");
                            commResponseVO.setTransactionId(respId);
                            commResponseVO.setDescription(transactionStatus);
                            commResponseVO.setRemark(transactionStatus);
                            commResponseVO.setAmount(amount);
                            commResponseVO.setRrn(bank_reference_id);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setResponseTime(date_created);
                        }else if(transactionStatus.equalsIgnoreCase("Canceled") || transactionStatus.equalsIgnoreCase("Rejected")
                            || transactionStatus.equalsIgnoreCase("Failed")){
                            commResponseVO.setStatus("failed");
                            commResponseVO.setTransactionStatus("failed");
                            commResponseVO.setTransactionId(respId);
                            commResponseVO.setDescription(transactionStatus);
                            commResponseVO.setRemark(transactionStatus);
                            commResponseVO.setAmount(amount);
                            commResponseVO.setRrn(bank_reference_id);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setResponseTime(date_created);
                        }
                        else{
                            commResponseVO.setStatus("pending");
                            commResponseVO.setTransactionStatus("pending");
                            commResponseVO.setTransactionId(respId);
                            commResponseVO.setDescription(transactionStatus);
                            commResponseVO.setRemark(transactionStatus);
                            commResponseVO.setAmount(amount);
                            commResponseVO.setRrn(bank_reference_id);
                            commResponseVO.setCurrency(currency);
                        }

                    }else{
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark("Invalid Access Token");
                        commResponseVO.setDescription("Invalid Access Token");
                    }
                }
                else
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setRemark("Invalid Access Token");
                    commResponseVO.setDescription("Invalid Access Token");

                }
                commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
                commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
            }else{
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Invalid Access Token");
                commResponseVO.setDescription("Invalid Access Token");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("JSONException---"+trackingID+"-->",e);
        }
        return commResponseVO;
    }
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error("Inside processRefund-----");
       /* CommRequestVO commRequestVO = (CommRequestVO) requestVO;*/
        Comm3DResponseVO commResponseVO     = new Comm3DResponseVO();
        EcospendRequestVo commRequestVO     = (EcospendRequestVo) requestVO;
        CommMerchantVO commMerchantVO       = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        PaymentManager paymentManager   = new PaymentManager();
        Functions functions             = new Functions();
        Hashtable details               = null;
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest      = gatewayAccount.isTest();
        UUID uuid1          = UUID.randomUUID();
        String RequestId    = uuid1.toString();
        String client_id    = gatewayAccount.getMerchantId();
        String client_secret    = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String grant_type       = gatewayAccount.getCHARGEBACK_FTP_PATH();;
        String paymentid        = commTransactionDetailsVO.getPreviousTransactionId();
        String scope        = "";
        String RefundUrl    = "";
        String authURL      = "";

        String amount       ="";
        String currency     ="";
        String bankid       = "";
        String reference    = "";
        String creditortype = "";
        String creditorID   = "";
        String creditorCurrency = "";
        String creditorName     = "";
        String creditorBic      = "";
        String description      = commTransactionDetailsVO.getOrderDesc();
        amount                  = commTransactionDetailsVO.getAmount();
        currency                = commTransactionDetailsVO.getCurrency();


        String refundResponse       = "";
        StringBuffer authRequest    = new StringBuffer();
        String authResponse         = "";

        try
        {


            if (isTest)
            {
                authURL     = RB.getString("TEST_AUTH_URL");
                RefundUrl   = RB.getString("TEST_REFUND_URL")+paymentid+"/refund";
            }
            else
            {
                authURL     = RB.getString("LIVE_AUTH_URL");
                RefundUrl   = RB.getString("LIVE_REFUND_URL")+paymentid+"/refund";
            }

            String redirect_url="";
            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                redirect_url    = "https://" + commMerchantVO.getHostUrl() + RB.getString("REDIRECT_URL");
                transactionLogger.error("From HOST_URL----" + redirect_url);
            }
            else
            {
                redirect_url    = RB.getString("TERM_URL");
                transactionLogger.error("From RB TERM_URL ----" + redirect_url);
            }

            transactionLogger.error("redirect_url " + trackingID+" "+ redirect_url);
            transactionLogger.error("authURL " + trackingID+" "+ authURL);
            transactionLogger.error("RefundUrl " + trackingID+" "+ RefundUrl);


            authRequest.append("grant_type=" + grant_type + "&client_id=" + client_id + "&client_secret=" + client_secret /*+ "&scope=" + scope*/);
            transactionLogger.error("Inquiry authRequest--------------for--trackingid::::" + trackingID + "--" + authRequest);

            authResponse = EcospendUtils.getAccessToken(authURL, authRequest.toString(), RequestId);
            transactionLogger.error("Inquiry authResponse--------------for--trackingid::::" + trackingID + "--" + authResponse);

            if (functions.isValueNull(authResponse) && EcospendUtils.isJSONValid(authResponse))
            {
                String access_token = "";

                JSONObject jsonObject = new JSONObject(authResponse);

                if (jsonObject.has("access_token"))
                {
                    access_token = jsonObject.getString("access_token");
                }

                if (functions.isValueNull(access_token))
                {
                    JSONObject requestJSON      = new JSONObject();
                    JSONObject refund_account   = new JSONObject();

                    bankid          = commRequestVO.getBankid();
                    reference       =  commRequestVO.getReference();
                    creditortype    = commRequestVO.getCreditortype();
                    creditorID      = commRequestVO.getCreditorID();
                    creditorCurrency= commRequestVO.getCreditorCurrency();
                    creditorName    = commRequestVO.getCreditorName();
                    if(functions.isValueNull(commRequestVO.getCreditorBic())){
                        creditorBic     = commRequestVO.getCreditorBic();
                    }else{
                        creditorBic = null;
                    }

                    requestJSON.put("bank_id",bankid);
                    requestJSON.put("amount",amount);
                    requestJSON.put("currency",currency);
                    requestJSON.put("description",description);
                    requestJSON.put("reference",reference);
                    requestJSON.put("reference",reference);
                    requestJSON.put("redirect_url",redirect_url);

                    refund_account.put("type",creditortype);
                    refund_account.put("identification",creditorID);
                    refund_account.put("owner_name",creditorName);
                    refund_account.put("currency",creditorCurrency);
                    refund_account.put("bic",creditorBic);

                    requestJSON.put("refund_account",refund_account);
                    transactionLogger.error("Refund Request " + trackingID + "--" + requestJSON.toString());

                    refundResponse = EcospendUtils.doPostHTTPSURLConnectionClient(RefundUrl, requestJSON.toString(), access_token);
                    transactionLogger.error("Refund Response--------------for--trackingid::::" + trackingID + "--" + refundResponse);

                    String transaction_Id     = "";
                    String bank_reference_id  = "";
                    String payment_url        = "";
                    String status             = "";
                    String is_refund          = "";
                    String respAmount         = "";
                    String date_created       = "";
                    String error             = "";
                    String respDescription        = "";

                    if(functions.isValueNull(refundResponse) && EcospendUtils.isJSONValid(refundResponse)){
                        JSONObject responseJSON = new JSONObject(refundResponse);

                        if(responseJSON.has("id")){
                            transaction_Id =  responseJSON.getString("id");
                        }
                        if(responseJSON.has("bank_reference_id")){
                            bank_reference_id =  responseJSON.getString("bank_reference_id");
                        }
                        if(responseJSON.has("payment_url")){
                            payment_url =  responseJSON.getString("payment_url");
                        }
                        if(responseJSON.has("status")){
                            status =  responseJSON.getString("status");
                        }
                        if(responseJSON.has("is_refund")){
                            is_refund =  responseJSON.getString("is_refund");
                        }
                        if(responseJSON.has("amount")){
                            respAmount =  responseJSON.getString("amount");
                        }
                        if(responseJSON.has("date_created")){
                            date_created =  responseJSON.getString("date_created");
                        }
                        if(responseJSON.has("error")){
                            error =  responseJSON.getString("error");
                        }
                        if(responseJSON.has("description")){
                            respDescription =  responseJSON.getString("description");
                        }
                        if(functions.isValueNull(transaction_Id)){
                            EcospendUtils.updateMainPaymentIdEntry(transaction_Id,trackingID);
                        }

                        if (status.equalsIgnoreCase("AwaitingAuthorization"))
                        {
                            commResponseVO.setStatus("pending");
                            commResponseVO.setTransactionStatus("pending");
                            commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                            commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                            commResponseVO.setTransactionId(transaction_Id);
                            commResponseVO.setRrn(bank_reference_id);
                            commResponseVO.setResponseTime(date_created);
                            commResponseVO.setPaymentURL(payment_url);

                        }else if(functions.isValueNull(error) && functions.isValueNull(respDescription)){
                            commResponseVO.setStatus("failed");
                            commResponseVO.setRemark(error);
                            commResponseVO.setDescription(error);
                        }
                        else{
                            commResponseVO.setStatus("pending");
                            commResponseVO.setTransactionStatus("pending");
                            commResponseVO.setRemark("pending");
                            commResponseVO.setDescription("pending");
                        }
                    }else{
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark("Invalid Response");
                        commResponseVO.setDescription("Invalid Response");
                    }
                }
                else
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setRemark("Invalid Access Token");
                    commResponseVO.setDescription("Invalid Access Token");
                }
                commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
                commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
            }else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Invalid Access Token");
                commResponseVO.setDescription("Invalid Access Token");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("JSONException---" + trackingID + "-->", e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID,GenericRequestVO requestVO) throws  PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {

        transactionLogger.error("Inside processCapture-----");
        EcospendRequestVo commRequestVO     = (EcospendRequestVo) requestVO;
        EcospendResponseVO commResponseVO   = new EcospendResponseVO();
        CommMerchantVO commMerchantVO       = commRequestVO.getCommMerchantVO();
        EcospendUtils ecospendUtils         = new EcospendUtils();
        Functions functions = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO               = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String getRefund = "false";
        String forPayout = "false";
        EcospendGatewayAccountVO gatewayAccountVO=ecospendUtils.getAccountDetails(accountId);

        String PaylinkUrl   ="";
        String AccessToken  = commRequestVO.getAccessTken();
        boolean isTest      = gatewayAccount.isTest();

        if(isTest){
            PaylinkUrl = RB.getString("TEST_PAYLINK_URL");
        }else{
            PaylinkUrl = RB.getString("LIVE_PAYLINK_URL");
        }

        transactionLogger.error("paylink url--------------for--trackingid::::" + trackingID + "--" + PaylinkUrl);


        String redirect_url="";
        transactionLogger.error("commRequestVO.getHostUrl()  "+commRequestVO.getHostUrl());
        transactionLogger.error("commMerchantVO.getHostUrl()  "+commMerchantVO.getHostUrl());
        if (functions.isValueNull(commRequestVO.getHostUrl()))
        {
            redirect_url = "https://" + commRequestVO.getHostUrl() + RB.getString("REDIRECT_URL");
            transactionLogger.error("From HOST_URL----" + redirect_url);
        }

        else if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            redirect_url = "https://" + commMerchantVO.getHostUrl() + RB.getString("REDIRECT_URL");
            transactionLogger.error("else if From HOST_URL----" + redirect_url);
        }
        else
        {
            redirect_url = RB.getString("TERM_URL");
            transactionLogger.error("From RB TERM_URL ----" + redirect_url);
        }

        String amount       = "";
        String description  = commTransactionDetailsVO.getOrderDesc();
        amount              = commTransactionDetailsVO.getAmount();
        String bankid       = commRequestVO.getBankid();
        //String reference=gatewayAccountVO.getReference();
        String currency     = commTransactionDetailsVO.getCurrency();
        String creditortype = gatewayAccountVO.getCreditor_account_type();
        String creditorID   = gatewayAccountVO.getCreditor_account_identification();
        //String creditorCurrency=gatewayAccountVO.getCreditor_account_currency();
        String creditorName = gatewayAccountVO.getCreditor_account_owner_name();
        /*String reference=commRequestVO.getReference();
        String creditortype=commRequestVO.getCreditortype();
        String creditorID=commRequestVO.getCreditorID();
        String creditorCurrency=commRequestVO.getCreditorCurrency();
        String creditorName=commRequestVO.getCreditorName();*/
        String debtortype       = commRequestVO.getDebtortype();
        String debtorID         = commRequestVO.getDebtorID();
        //String debtorCurrency=commRequestVO.getDebtorCurrency();
        String debtorName       = commRequestVO.getDebtorName();
        //String Debtoraccount=commRequestVO.getDebtoraccount();
        String paymentrails     = "FasterPayments";
        String auto_redirect    = "true";
        String send_sms_notification    = "false";

        String email                    = addressDetailsVO.getEmail();
        String send_email_notification  = "true";
        String phone_number             = addressDetailsVO.getPhone();
        //String request_tip="true";
        //String is_required="true";
        String allow_partial_payments   = "false";
        String generate_qr_code         = "false";

        String unique_id="";
        String url      ="";
        String qr_code  ="";
        String status   ="";
        String error    ="";
        String errordescription="";
        String merchant_id = commTransactionDetailsVO.getToId();

        try
        {


            if(commRequestVO.getIsRefundAllow() != null){
                getRefund = commRequestVO.getIsRefundAllow();
            }

            StringBuffer paymentRequest = new StringBuffer();
            String paymentResponse      = "";


            paymentRequest.append("{" +
                    "\"amount\":" + amount + "," +
                    "\"reference\":\"" + trackingID + "\"," +
                    "\"description\":\"" + description + "\"," +
                    "\"redirect_url\":\"" + redirect_url + "\"," +
                    "\"bank_id\":\"" + bankid + "\"," +
                    "\"merchant_id\":\"" + merchant_id + "\"," +
                    "\"merchant_user_id\":\"" + merchant_id + "\"," +
                    "\"creditor_account\":{" +
                    "\"type\":\"" + creditortype + "\"," +
                    "\"identification\":\"" + creditorID + "\"," +
                    "\"name\":\"" + creditorName + "\"," +
                    "\"currency\":\"" + currency + "\"," +
                    "}," );
            if(functions.isValueNull(debtortype))
            {
                paymentRequest.append(
                        "\"debtor_account\":{" +
                                "\"type\":\"" + debtortype + "\"," +
                                "\"identification\":\"" + debtorID + "\"," +
                                "\"name\":\"" + debtorName + "\"," +
                                "\"currency\":\"" + currency + "\"," +
                                "},");
            }
            paymentRequest.append(
                    "\"paylink_options\":{" +
                    "\"auto_redirect\":" + auto_redirect + "," +
                    "\"generate_qr_code\":" + generate_qr_code + "," +
                    "\"allow_partial_payments\":\"" + allow_partial_payments + "\"," +
                    /*"\"tip\":{" +
                    "\"request_tip\":" + request_tip + "," +
                    "\"title\":\"" + identification + "\"," +
                    "\"text\":\"" + title + "\"," +
                    "\"is_required\":" + is_required + "," +
                    "\"options\":[{" +
                    "\"type\":\"" + 1 + "\"," +
                    "\"value\":\"" + 1 + "\"," +
                    "}]," +
                    "}," +*/
                    "}," +
                    "\"notification_options\":{" +
                    "\"send_email_notification\":" + send_email_notification + "," +
                    "\"email\":\"" + email + "\"," +
                    "\"send_sms_notification\":" + send_sms_notification + "," +
                    "\"phone_number\":\"" + phone_number + "\"," +
                    "}," +
                    "\"payment_options\":{" +
                    "\"payment_rails\":\"" + paymentrails + "\"," +
                    "\"get_refund_info\":" + getRefund + "," +
                    "\"for_payout\":" + forPayout + "" +
                    "}," +
                    /*"\"limit_options\":{" +
                    "\"count\":" + 1 + "," +
                    "\"amount\":" + 2000 + "," +
                    "\"date\":\"" + date + "\"" +
                    "}," +*/
                    "}");
            transactionLogger.error("Paylinkrequest =====" + trackingID + " -- " + paymentRequest);

            paymentResponse= EcospendUtils.doPostHTTPSURLConnectionClient(PaylinkUrl, String.valueOf(paymentRequest),AccessToken);
            transactionLogger.error("Paylink URL response for ===== " + trackingID + " -- " +paymentResponse);



            if (functions.isValueNull(paymentResponse) && paymentResponse.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(paymentResponse);
                if (jsonObject != null)
                {
                    if (jsonObject.has("unique_id"))
                    {

                        unique_id = jsonObject.getString("unique_id");

                    }
                    if (jsonObject.has("url"))
                    {

                        url = jsonObject.getString("url");
                    }
                    if (jsonObject.has("qr_code"))
                    {

                        qr_code = jsonObject.getString("qr_code");
                    }

                   /* if (functions.isValueNull(url))
                    {
                        paylinkResponse= EcospendUtils.doGetHTTPSURLConnectionClient(PaylinkUrl + "/" +unique_id, AccessToken);
                        transactionLogger.error("Paylinkresponse for ===== " + trackingID + " -- " +paylinkResponse);

                    }
*/
                /*if(status.equalsIgnoreCase("AwaitingAuthorization"))
                {*/
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionId(unique_id);
                    commResponseVO.setRedirectUrl(url);
               // }
                /*else
                {
                    commResponseVO.setStatus("failed");
                    if(functions.isValueNull(error))
                    {
                        commResponseVO.setDescription(error);
                        commResponseVO.setRemark(errordescription);
                    }else{
                        commResponseVO.setDescription("Transaction Declined");
                        commResponseVO.setRemark("Transaction Declined");
                    }
                }*/

            }
            else
            {
                transactionLogger.error("Transaction Failed due to null json");
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Transaction Declined");
                commResponseVO.setDescription("Transaction Declined");
            }
        }
        else
        {
            transactionLogger.error("Transaction Failed due to null response");
            commResponseVO.setStatus("failed");
            commResponseVO.setRemark("Invalid Response");
            commResponseVO.setDescription("Invalid Response");
        }


        }
        catch (Exception e)
        {
            transactionLogger.error("JSONException---" + trackingID + "-->", e);
        }

            return commResponseVO;
    }

    @Override
    public GenericResponseVO processRebilling (String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Inside processSale-----");
        EcospendRequestVo commRequestVO         = (EcospendRequestVo) requestVO;
        EcospendResponseVO commResponseVO       = new EcospendResponseVO();
        CommMerchantVO commMerchantVO           = commRequestVO.getCommMerchantVO();
        EcospendUtils ecospendUtils             = new EcospendUtils();
        Functions functions                     = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        EcospendGatewayAccountVO gatewayAccountVO           = ecospendUtils.getAccountDetails(accountId);

        String getRefund    = "false";
        String payment_type = "Auto";

        String SaleUrl      = "";
        String AccessToken  = commRequestVO.getAccessTken();
        boolean isTest      = gatewayAccount.isTest();
        if(isTest){
            SaleUrl = RB.getString("TEST_STANDING_ORDERS_URL");
        }else{
            SaleUrl = RB.getString("LIVE_STANDING_ORDERS_URL");
        }
        transactionLogger.error("authURL--------------for--trackingid::::" + trackingID + "--" + SaleUrl);

        String redirect_url="";
        transactionLogger.error("commRequestVO.getHostUrl()  "+commRequestVO.getHostUrl());
        transactionLogger.error("commMerchantVO.getHostUrl()  "+commMerchantVO.getHostUrl());
        if (functions.isValueNull(commRequestVO.getHostUrl()))
        {
            redirect_url = "https://" + commRequestVO.getHostUrl() + RB.getString("REDIRECT_URL");
            transactionLogger.error("From HOST_URL----" + redirect_url);
        }

        else if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            redirect_url = "https://" + commMerchantVO.getHostUrl() + RB.getString("REDIRECT_URL");
            transactionLogger.error("else if From HOST_URL----" + redirect_url);
        }
        else
        {
            redirect_url = RB.getString("TERM_URL");
            transactionLogger.error("From RB TERM_URL ----" + redirect_url);
        }

        String amount       ="";
        String currency     ="";
        String description  = commTransactionDetailsVO.getOrderDesc();
        amount              = commTransactionDetailsVO.getAmount();
        currency            = commTransactionDetailsVO.getCurrency();
        //description=commTransactionDetailsVO.getOrderDesc();
        String bankid       = commRequestVO.getBankid();
        /*String reference=commRequestVO.getReference();
        String creditortype=commRequestVO.getCreditortype();
        String creditorID=commRequestVO.getCreditorID();
        String creditorName=commRequestVO.getCreditorName();*/
        String creditortype = gatewayAccountVO.getCreditor_account_type();
        String creditorID   = gatewayAccountVO.getCreditor_account_identification();
        //String creditorCurrency=gatewayAccountVO.getCreditor_account_currency();
        String creditorName = gatewayAccountVO.getCreditor_account_owner_name();
        String CreditorBic  = gatewayAccountVO.getCreditor_account_bic();
        //String reference=gatewayAccountVO.getReference();
        String debtortype   = commRequestVO.getDebtortype();
        String debtorID     = commRequestVO.getDebtorID();
        String debtorName   = commRequestVO.getDebtorName();
        String debtorBic    = commRequestVO.getDebtorBic();
        String first_payment_date   = commRequestVO.getFirst_payment_date();
       // String first_payment_date="11/11/2021 7:56:58 PM +00:00";
        String number_of_payments   = commRequestVO.getNumber_of_payments();
        String period               = commRequestVO.getPeriod();
        String firstPaymentAmount   = commRequestVO.getFirstPaymentAmount();
        String lastPaymentAmount    = commRequestVO.getLastPaymentAmount();

        String payment_url  ="";
        String status       ="";
        String Respreference="";
        String rescrtype    ="";
        String respdrtype   ="";
        String Respamount   ="";
        String respcrowner_name ="";
        String respdrowner_name ="";
        String resdrcurrency    ="";
        String rescrcurrency    ="";
        String respcridentification ="";
        String resdridentification  ="";
        String is_refund    ="";
        String bank_id      ="";
        String id           ="";
        String rescrbic     ="";
        String respdrbic    ="";
        String resp_First_payment_date="";
        String standing_order_type="";
        String resp_number_of_payments="";
        String respPeriod="";
        String error ="";
        String errordescription="";
        String transactionStatus="";
        String merchant_id = commTransactionDetailsVO.getToId();
        try
        {
            if(commRequestVO.getIsRefundAllow() != null){
                getRefund    = commRequestVO.getIsRefundAllow();
            }

            StringBuffer paymentRequest = new StringBuffer();
            String paymentResponse      = "";

            paymentRequest.append("{" +
                    "\"redirect_url\":\"" + redirect_url + "\"," +
                    "\"bank_id\":\"" + bankid + "\"," +
                    "\"currency\":\"" + currency + "\"," +
                    "\"description\":\"" + description + "\"," +
                    "\"reference\":\"" + trackingID + "\"," +
                    "\"merchant_id\":\"" + merchant_id + "\"," +
                    "\"merchant_user_id\":\"" + merchant_id + "\"," +
                    "\"first_payment_date\":\"" + first_payment_date + "\"," +
                    "\"amount\":" + amount + "," +
                    "\"number_of_payments\":" + number_of_payments + "," +
                    "\"period\":\"" + period + "\"," +
                    "\"standing_order_type\":\"" + payment_type + "\","+

                    "\"standing_order_option\":{" +
                            "\"get_refund_info\":" + getRefund
            );
            if(functions.isValueNull(firstPaymentAmount))
            {
                paymentRequest.append(
                             ","+
                            "\"first_payment_amount\":" + firstPaymentAmount  );
            }
            if(functions.isValueNull(lastPaymentAmount))
            {
                paymentRequest.append(
                            ","+
                            "\"last_payment_amount\":" + lastPaymentAmount + ""
                );
            }
            paymentRequest.append(
                    "},"+
                    "\"creditor_account\":{" +
                    "\"type\":\"" + creditortype + "\"," +
                    "\"identification\":\"" + creditorID + "\"," +
                    "\"owner_name\":\""+creditorName+"\"," +
                    "\"currency\":\"" + currency + "\",");
            if(functions.isValueNull(CreditorBic))
            {
                paymentRequest.append("\"bic\":\"" + CreditorBic + "\"" +
                        "},");
            }else{
                paymentRequest.append("\"bic\":null" +
                        "},");
            }
            if(functions.isValueNull(debtortype))
            {
                paymentRequest.append(
                        "\"debtor_account\":{" +
                                "\"type\":\"" + debtortype + "\"," +
                                "\"identification\":\"" + debtorID + "\"," +
                                "\"owner_name\":\"" + debtorName + "\"," +
                                "\"currency\":\"" + currency + "\"," );
                if(functions.isValueNull(debtorBic))
                {
                    paymentRequest.append("\"bic\":\"" + debtorBic + "\"" +
                            "},");
                }else{
                    paymentRequest.append("\"bic\":null" +
                            "},");
                }
            }
            paymentRequest.append(
                            "}");
            transactionLogger.error("Paymentrequest =====" + trackingID + " -- " + paymentRequest);

            paymentResponse = EcospendUtils.doPostHTTPSURLConnectionClient(SaleUrl, String.valueOf(paymentRequest),AccessToken);

            transactionLogger.error("Paymentresponse for ===== " + trackingID + " -- " +paymentResponse);

            if (functions.isValueNull(paymentResponse) && paymentResponse.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(paymentResponse);
                if (jsonObject != null)
                {
                    if (jsonObject.has("id"))
                    {
                        id = jsonObject.getString("id");
                    }
                    if (jsonObject.has("status"))
                    {
                        status = jsonObject.getString("status");
                    }
                    if (jsonObject.has("standing_order_url"))
                    {
                        payment_url = jsonObject.getString("standing_order_url");
                    }
                    if (jsonObject.has("bank_id"))
                    {
                        bank_id = jsonObject.getString("bank_id");
                    }
                    if (jsonObject.has("description"))
                    {
                        errordescription = jsonObject.getString("description");
                    }
                    if (jsonObject.has("reference"))
                    {
                        Respreference = jsonObject.getString("reference");
                    }
                    if (jsonObject.has("first_payment_date"))
                    {
                        resp_First_payment_date = jsonObject.getString("first_payment_date");
                    }
                    if (jsonObject.has("amount"))
                    {
                        Respamount = jsonObject.getString("amount");
                    }
                    if (jsonObject.has("number_of_payments"))
                    {
                        resp_number_of_payments = jsonObject.getString("number_of_payments");
                    }

                    if (jsonObject.has("period"))
                    {
                        respPeriod = jsonObject.getString("period");
                    }
                    if (jsonObject.has("standing_order_type"))
                    {
                        standing_order_type = jsonObject.getString("standing_order_type");
                    }
                    if (jsonObject.has("standing_order_option") && functions.isValueNull(jsonObject.getString("standing_order_option")))
                    {
                        if(functions.isValueNull(jsonObject.getJSONObject("standing_order_option").getString("get_refund_info")))
                        {
                            is_refund = jsonObject.getJSONObject("standing_order_option").getString("get_refund_info");
                        }
                    }
                    if (jsonObject.has("debtor_account") && functions.isValueNull(jsonObject.getString("debtor_account")))
                    {
                        if(functions.isValueNull(jsonObject.getJSONObject("debtor_account").getString("type")))
                        {
                            respdrtype = jsonObject.getJSONObject("debtor_account").getString("type");
                        }
                        if(functions.isValueNull(jsonObject.getJSONObject("debtor_account").getString("identification")))
                        {
                            resdridentification = jsonObject.getJSONObject("debtor_account").getString("identification");
                        }
                        if(functions.isValueNull(jsonObject.getJSONObject("debtor_account").getString("owner_name")))
                        {
                            respdrowner_name = jsonObject.getJSONObject("debtor_account").getString("owner_name");
                        }
                        if(functions.isValueNull(jsonObject.getJSONObject("debtor_account").getString("currency")))
                        {
                            resdrcurrency = jsonObject.getJSONObject("debtor_account").getString("currency");
                        }
                        if(jsonObject.getJSONObject("debtor_account").has("bic") && functions.isValueNull(jsonObject.getJSONObject("debtor_account").getString("bic")) )
                        {
                            respdrbic = jsonObject.getJSONObject("debtor_account").getString("bic");
                        }
                    }
                    if (jsonObject.has("creditor_account") && functions.isValueNull(jsonObject.getString("creditor_account")))
                    {
                        if(functions.isValueNull(jsonObject.getJSONObject("creditor_account").getString("type")))
                        {
                            rescrtype = jsonObject.getJSONObject("creditor_account").getString("type");
                        }
                        if(functions.isValueNull(jsonObject.getJSONObject("creditor_account").getString("identification")))
                        {
                            respcridentification = jsonObject.getJSONObject("creditor_account").getString("identification");
                        }
                        if(functions.isValueNull(jsonObject.getJSONObject("creditor_account").getString("owner_name")))
                        {
                            respcrowner_name = jsonObject.getJSONObject("creditor_account").getString("owner_name");
                        }
                        if(functions.isValueNull(jsonObject.getJSONObject("creditor_account").getString("currency")))
                        {
                            rescrcurrency = jsonObject.getJSONObject("creditor_account").getString("currency");
                        }
                        if(jsonObject.getJSONObject("creditor_account").has("bic") && functions.isValueNull(jsonObject.getJSONObject("creditor_account").getString("bic")) )
                        {
                        rescrbic = jsonObject.getJSONObject("creditor_account").getString("bic");
                        }
                    }

                    if(status.equalsIgnoreCase("AwaitingAuthorization")){
                        transactionStatus="pending";
                        commResponseVO.setStatus("pending");
                        commResponseVO.setTransactionId(id);
                        commResponseVO.setBank_id(bank_id);
                        commResponseVO.setCreditortype(rescrtype);
                        commResponseVO.setCreditorName(respcrowner_name);
                        commResponseVO.setCreditorID(respcridentification);
                        commResponseVO.setCreditorCurrency(rescrcurrency);
                        commResponseVO.setCreditorBic(rescrbic);
                        commResponseVO.setDebtortype(respdrtype);
                        commResponseVO.setDebtorName(respdrowner_name);
                        commResponseVO.setDebtorID(resdridentification);
                        commResponseVO.setDebtorCurrency(resdrcurrency);
                        commResponseVO.setDebtorBic(respdrbic);
                        commResponseVO.setFirst_payment_date(resp_First_payment_date);
                        commResponseVO.setStanding_order_type(standing_order_type);
                        commResponseVO.setNumber_of_payments(resp_number_of_payments);
                        commResponseVO.setPeriod(respPeriod);
                        commResponseVO.setRedirectUrl(payment_url);
                        commResponseVO.setGetrefundinfo(is_refund);
                        commResponseVO.setReference(Respreference);
                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                        if(functions.isValueNull(error))
                        {
                            commResponseVO.setDescription(error);
                            commResponseVO.setRemark(errordescription);
                        }else{
                            commResponseVO.setDescription("Transaction Declined");
                            commResponseVO.setRemark("Transaction Declined");
                        }
                    }

                }
                else
                {
                    transactionLogger.error("Transaction Failed due to null json");
                    commResponseVO.setStatus("failed");
                    commResponseVO.setRemark("Transaction Declined");
                    commResponseVO.setDescription("Transaction Declined");
                }
            }
            else
            {
                transactionLogger.error("Transaction Failed due to null response");
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Invalid Response");
                commResponseVO.setDescription("Invalid Response");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("JSONException---" + trackingID + "-->", e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processPaybyLink(String trackingID,GenericRequestVO requestVO) throws  PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {

        transactionLogger.error("Inside processCapture-----");
        EcospendRequestVo commRequestVO     = (EcospendRequestVo) requestVO;
        EcospendResponseVO commResponseVO   = new EcospendResponseVO();
        CommMerchantVO commMerchantVO       = commRequestVO.getCommMerchantVO();
        EcospendUtils ecospendUtils         = new EcospendUtils();
        Functions functions                 = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO               = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String getRefund = "false";
        String forPayout = "false";
        EcospendGatewayAccountVO gatewayAccountVO   = ecospendUtils.getAccountDetails(accountId);
        UUID uuid1              = UUID.randomUUID();
        String RequestId        = uuid1.toString();
        String client_id        = gatewayAccount.getMerchantId();
        String client_secret    = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String grant_type       = gatewayAccount.getCHARGEBACK_FTP_PATH();


        String PaylinkUrl   = "";
        String authURL      = "";
       // String AccessToken = commRequestVO.getAccessTken();
        boolean isTest  = gatewayAccount.isTest();
        if(isTest){
            PaylinkUrl  = RB.getString("TEST_PAYLINK_URL");
            authURL     = RB.getString("TEST_AUTH_URL");

        }else{
            PaylinkUrl = RB.getString("LIVE_PAYLINK_URL");
            authURL = RB.getString("LIVE_AUTH_URL");

        }
        transactionLogger.error("authURL--------------for--trackingid::::" + trackingID + "--" + authURL);
        transactionLogger.error("PaylinkUrl--------------for--trackingid::::" + trackingID + "--" + PaylinkUrl);

        String redirect_url = "";

        transactionLogger.error("commRequestVO.getHostUrl()  "+commRequestVO.getHostUrl());
        transactionLogger.error("commMerchantVO.getHostUrl()  "+commMerchantVO.getHostUrl());
        if (functions.isValueNull(commRequestVO.getHostUrl()))
        {
            redirect_url = "https://" + commRequestVO.getHostUrl() + RB.getString("REDIRECT_URL");
            transactionLogger.error("From HOST_URL----" + redirect_url);
        }

        else if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            redirect_url = "https://" + commMerchantVO.getHostUrl() + RB.getString("REDIRECT_URL");
            transactionLogger.error("else if From HOST_URL----" + redirect_url);
        }
        else
        {
            redirect_url = RB.getString("TERM_URL");
            transactionLogger.error("From RB TERM_URL ----" + redirect_url);
        }

        String amount="";
        String description   = commTransactionDetailsVO.getOrderDesc();
        amount               = commTransactionDetailsVO.getAmount();
        String bankid        = commRequestVO.getBankid();
        String currency      = commTransactionDetailsVO.getCurrency();
        String creditortype  = gatewayAccountVO.getCreditor_account_type();
        String creditorID    = gatewayAccountVO.getCreditor_account_identification();
        String creditorName  = addressDetailsVO.getFirstname();
        String debtortype    = commRequestVO.getDebtortype();
        String debtorID      = commRequestVO.getDebtorID();
        String debtorName    = commRequestVO.getDebtorName();
        String Debtoraccount = commRequestVO.getDebtoraccount();
        String paymentrails             = "FasterPayments";
        String auto_redirect            = "false";
        String send_sms_notification    = "false";

        /*String debtorCurrency=commRequestVO.getDebtorCurrency();
        String reference=gatewayAccountVO.getReference();
        //String creditorCurrency=gatewayAccountVO.getCreditor_account_currency();
        String reference=commRequestVO.getReference();
        String creditortype=commRequestVO.getCreditortype();
        String creditorID=commRequestVO.getCreditorID();
        String creditorCurrency=commRequestVO.getCreditorCurrency();
        String creditorName=commRequestVO.getCreditorName();*/

        String email                    = addressDetailsVO.getEmail();
        String send_email_notification  = "true";
        String phone_number             = addressDetailsVO.getPhone();
        //String request_tip="true";
        //String is_required="true";
        String allow_partial_payments   = "false";
        String generate_qr_code         = "false";

        String unique_id= "";
        String url      = "";
        String qr_code  = "";
        String status   = "";
        String error    = "";
        String errordescription = "";
        String merchant_id = commTransactionDetailsVO.getToId();
        try
        {

            if(commRequestVO.getIsRefundAllow() !=null){
                getRefund   = commRequestVO.getIsRefundAllow();
            }

            StringBuffer paymentRequest = new StringBuffer();
            String paymentResponse      = "";
            String paylinkResponse      = "";
            String scope = "";
            StringBuffer authRequest = new StringBuffer();
            String authResponse     = "";
            String access_token     = "";


            authRequest.append("grant_type=" + grant_type + "&client_id=" + client_id + "&client_secret=" + client_secret /*+ "&scope=" + scope*/);
            transactionLogger.error("authRequest authRequest--------------for--trackingid::::" + trackingID + "--" + authRequest);

            authResponse = EcospendUtils.getAccessToken(authURL, authRequest.toString(), RequestId);
            transactionLogger.error("authRequest authResponse--------------for--trackingid::::" + trackingID + "--" + authResponse);

            if (functions.isValueNull(authResponse) && authResponse.contains("{"))
            {
                String expires_in = "";
                String token_type = "";
                String scopeResp = "";

                JSONObject jsonObject = new JSONObject(authResponse);

                if (jsonObject.has("access_token"))
                {
                    access_token = jsonObject.getString("access_token");
                }
                if (functions.isValueNull(access_token))
                {
                    paymentRequest.append("{" +
                            "\"amount\":" + amount + "," +
                            "\"reference\":\"" + trackingID + "\"," +
                            "\"description\":\"" + description + "\"," +
                            "\"redirect_url\":\"" + redirect_url + "\"," +
                            //          "\"bank_id\":\"" + bankid + "\"," +
                            "\"merchant_id\":\"" + merchant_id + "\"," +
                            "\"merchant_user_id\":\"" + merchant_id + "\"," +
                            "\"creditor_account\":{" +
                            "\"type\":\"" + creditortype + "\"," +
                            "\"identification\":\"" + creditorID + "\"," +
                            "\"name\":\"" + creditorName + "\"," +
                            "\"currency\":\"" + currency + "\"," +
                            "},");
                    if (functions.isValueNull(debtortype))
                    {
                        paymentRequest.append(
                                "\"debtor_account\":{" +
                                        "\"type\":\"" + debtortype + "\"," +
                                        "\"identification\":\"" + debtorID + "\"," +
                                        "\"name\":\"" + debtorName + "\"," +
                                        "\"currency\":\"" + currency + "\"," +
                                        "},");
                    }
                    paymentRequest.append(
                            "\"paylink_options\":{" +
                                    "\"auto_redirect\":" + auto_redirect + "," +
                                    "\"generate_qr_code\":" + generate_qr_code + "," +
                                    "\"allow_partial_payments\":\"" + allow_partial_payments + "\"," +
                    /*"\"tip\":{" +
                    "\"request_tip\":" + request_tip + "," +
                    "\"title\":\"" + identification + "\"," +
                    "\"text\":\"" + title + "\"," +
                    "\"is_required\":" + is_required + "," +
                    "\"options\":[{" +
                    "\"type\":\"" + 1 + "\"," +
                    "\"value\":\"" + 1 + "\"," +
                    "}]," +
                    "}," +*/
                                    "}," +
                                    "\"notification_options\":{" +
                                    "\"send_email_notification\":" + send_email_notification + "," +
                                    "\"email\":\"" + email + "\"," +
                                    "\"send_sms_notification\":" + send_sms_notification + "," +
                                    "\"phone_number\":\"" + phone_number + "\"," +
                                    "}," +
                                    "\"payment_options\":{" +
                                    "\"payment_rails\":\"" + paymentrails + "\"," +
                                    "\"get_refund_info\":" + getRefund + "," +
                                    "\"for_payout\":" + forPayout + "" +
                                    "}," +
                    /*"\"limit_options\":{" +
                    "\"count\":" + 1 + "," +
                    "\"amount\":" + 2000 + "," +
                    "\"date\":\"" + date + "\"" +
                    "}," +*/
                                    "}");
                }
            }
            transactionLogger.error("Merchant Paylinkrequest =====" + trackingID + " -- " + paymentRequest);

            paymentResponse= EcospendUtils.doPostHTTPSURLConnectionClient(PaylinkUrl, String.valueOf(paymentRequest),access_token);
            transactionLogger.error("Merchant Paylink URL response for ===== " + trackingID + " -- " +paymentResponse);



            if (functions.isValueNull(paymentResponse) && paymentResponse.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(paymentResponse);
                if (jsonObject != null)
                {
                    if (jsonObject.has("unique_id"))
                    {

                        unique_id = jsonObject.getString("unique_id");

                    }
                    if (jsonObject.has("url"))
                    {

                        url = jsonObject.getString("url");
                    }
                    if (jsonObject.has("qr_code"))
                    {

                        qr_code = jsonObject.getString("qr_code");
                    }

                   /* if (functions.isValueNull(url))
                    {
                        paylinkResponse= EcospendUtils.doGetHTTPSURLConnectionClient(PaylinkUrl + "/" +unique_id, AccessToken);
                        transactionLogger.error("Paylinkresponse for ===== " + trackingID + " -- " +paylinkResponse);

                    }
*/
                /*if(status.equalsIgnoreCase("AwaitingAuthorization"))
                {*/
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionId(unique_id);
                    commResponseVO.setRedirectUrl(url);
                    // }
                /*else
                {
                    commResponseVO.setStatus("failed");
                    if(functions.isValueNull(error))
                    {
                        commResponseVO.setDescription(error);
                        commResponseVO.setRemark(errordescription);
                    }else{
                        commResponseVO.setDescription("Transaction Declined");
                        commResponseVO.setRemark("Transaction Declined");
                    }
                }*/

                }
                else
                {
                    transactionLogger.error("Transaction Failed due to null json");
                    commResponseVO.setStatus("failed");
                    commResponseVO.setRemark("Transaction Declined");
                    commResponseVO.setDescription("Transaction Declined");
                }
            }
            else
            {
                transactionLogger.error("Transaction Failed due to null response");
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Invalid Response");
                commResponseVO.setDescription("Invalid Response");
            }


        }
        catch (Exception e)
        {
            transactionLogger.error("JSONException---" + trackingID + "-->", e);
        }

        return commResponseVO;
    }


    public GenericResponseVO processPayout(String trackingID,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside Ecospend process payout-----");
        //CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        EcospendRequestVo commRequestVO                         = (EcospendRequestVo) requestVO;
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions                                 = new Functions();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                      = gatewayAccount.isTest();

        UUID uuid1              = UUID.randomUUID();
        String RequestId        = uuid1.toString();
        String client_id        = gatewayAccount.getMerchantId();
        String client_secret    = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String grant_type       = gatewayAccount.getCHARGEBACK_FTP_PATH();

        String REQUEST_URL      = "";
        String AUTHTOKEN_URL    = "";
        String responseString   = "";
        StringBuffer authRequest = new StringBuffer();
        String authTokenResponse = "";
        SimpleDateFormat dt        = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try
        {
            if (isTest)
            {
                REQUEST_URL  = RB.getString("PAYOUT_TEST_URL");
                AUTHTOKEN_URL  = RB.getString("TEST_AUTH_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("PAYOUT_LIVE_URL");
                AUTHTOKEN_URL = RB.getString("LIVE_AUTH_URL");
            }

            transactionLogger.error("AUTHTOKEN_URL " + trackingID + " " + AUTHTOKEN_URL);
            transactionLogger.error("REQUEST_URL " + trackingID + " " + REQUEST_URL);

            authRequest.append("grant_type=" + grant_type + "&client_id=" + client_id + "&client_secret=" + client_secret /*+ "&scope=" + scope*/);
            transactionLogger.error("Payout authTokenRequest " + trackingID + " " + authRequest);

            authTokenResponse = EcospendUtils.getAccessToken(AUTHTOKEN_URL, authRequest.toString(), RequestId);
            transactionLogger.error("Payout authTokenResponse " + trackingID + " " + authTokenResponse);
            String access_token = "";
            String token_type   = "";
            String scopeResp    = "";

            if (functions.isValueNull(authTokenResponse) && EcospendUtils.isJSONValid(authTokenResponse))
            {
                JSONObject jsonObject = new JSONObject(authTokenResponse);

                if (jsonObject.has("access_token"))
                {
                    access_token = jsonObject.getString("access_token");
                }
            }

            if (functions.isValueNull(access_token))
            {

                String original_payment_id  = commTransactionDetailsVO.getPaymentId();
                String beneficiary_type     = "I";
                String bank_id              = commRequestVO.getBankid();
                String amount               = commTransactionDetailsVO.getAmount();
                String reference            = trackingID;
                String reason               = "Paying for service";
                String merchant_id          = trackingID;
                String merchant_user_id     = trackingID;

                //beneficiary_account
                String type             = commRequestVO.getCreditortype();
                String identification   = commRequestVO.getCreditorID();
                String owner_name       = commRequestVO.getCreditorName();
                String currency         = commRequestVO.getCreditorCurrency();

                JSONObject requestJSON          = new JSONObject();
                JSONObject beneficiary_account  = new JSONObject();

                requestJSON.put("original_payment_id","");
                requestJSON.put("beneficiary_type",beneficiary_type);
                requestJSON.put("bank_id",bank_id);
                requestJSON.put("amount",amount);
                requestJSON.put("reference",reference);
                requestJSON.put("reason",reason);
                requestJSON.put("payout_date",dt.format(new Date()));
                requestJSON.put("merchant_id",merchant_id);
                requestJSON.put("merchant_user_id",merchant_user_id);

                beneficiary_account.put("type",type);
                beneficiary_account.put("identification",identification);
                beneficiary_account.put("owner_name",owner_name);
                beneficiary_account.put("currency",currency);

                requestJSON.put("beneficiary_account",beneficiary_account);


                transactionLogger.error("Payout Request " + trackingID + " " + requestJSON.toString());
                responseString =  EcospendUtils.doPostHTTPSURLConnectionClient(REQUEST_URL, requestJSON.toString(), access_token);
                transactionLogger.error("Payout Response " + trackingID + " " + responseString);
            }


            String transaction_Id        = "";
            String reference_id          = "";
            String status                = "";
            String payout_date          = "";
            String original_payment_id  = "";
            String bank_id              = "";
            String amount               = "";
            String currency             = "";
            String error             = "";

            if(functions.isValueNull(responseString) && EcospendUtils.isJSONValid(responseString))
            {
                JSONObject payoutResponse = new JSONObject(responseString);

                if(payoutResponse.has("status")){
                    status = payoutResponse.getString("status");
                }
                if(payoutResponse.has("error")){
                    error = payoutResponse.getString("error");
                }
                if(payoutResponse.has("id")){
                    transaction_Id = payoutResponse.getString("id");
                }
                if(payoutResponse.has("amount")){
                    amount = payoutResponse.getString("amount");
                }
                if(payoutResponse.has("payout_date")){
                    payout_date = payoutResponse.getString("payout_date");
                }


                if (status.equalsIgnoreCase("Completed"))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setRemark(status);
                    commResponseVO.setDescription(status);
                    commResponseVO.setTransactionId(transaction_Id);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setResponseTime(payout_date);

                }
                else if(status.equalsIgnoreCase("Initial") || status.equalsIgnoreCase("AwaitingAuthorization")
                        || status.equalsIgnoreCase("Authorised") || status.equalsIgnoreCase("Verified") ){

                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(status);
                    commResponseVO.setDescription(status);
                }
                else if (status.equalsIgnoreCase("Canceled") || status.equalsIgnoreCase("Failed") ||
                        status.equalsIgnoreCase("Rejected") || functions.isValueNull(error) )
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(status);
                    commResponseVO.setDescription(status);
                    commResponseVO.setDescription(status);
                    commResponseVO.setTransactionId(transaction_Id);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setResponseTime(payout_date);
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark("pending");
                    commResponseVO.setDescription("pending");

                }
            }
            else
            {
                transactionLogger.error("Transaction Failed due to null response");
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Invalid Response");
                commResponseVO.setDescription("Invalid Response");
            }


        }
        catch (Exception e)
        {
            transactionLogger.error("processPayout ----trackingid---->"+trackingID+"--->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processPayoutInquiry(String trackingID,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside Ecopsend processPayout inquiry------->");

        CommRequestVO commRequestVO       = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO   = new Comm3DResponseVO();
        GatewayAccount gatewayAccount     = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                    = gatewayAccount.isTest();
        Functions functions               = new Functions();

        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        UUID uuid1              = UUID.randomUUID();
        String RequestId        = uuid1.toString();
        String client_id        = gatewayAccount.getMerchantId();
        String client_secret    = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String grant_type       = gatewayAccount.getCHARGEBACK_FTP_PATH();

        String REQUEST_URL          = "";
        String AUTHTOKEN_URL        = "";
        StringBuffer authRequest    = new StringBuffer();
        String authTokenResponse    = "";
        String responeString        = "";
        try
        {
            transactionLogger.error("isTest---------->" + isTest);
            if (isTest)
            {
                REQUEST_URL     = RB.getString("PAYOUT_INQUIRY_TEST_URL") + commTransactionDetailsVO.getPreviousTransactionId();
                AUTHTOKEN_URL   = RB.getString("TEST_AUTH_URL");
            }
            else
            {
                REQUEST_URL     = RB.getString("PAYOUT_INQUIRY_LIVE_URL") + commTransactionDetailsVO.getPreviousTransactionId();
                AUTHTOKEN_URL   = RB.getString("LIVE_AUTH_URL");
            }

            authRequest.append("grant_type=" + grant_type + "&client_id=" + client_id + "&client_secret=" + client_secret /*+ "&scope=" + scope*/);
            transactionLogger.error("Payout authTokenRequest " + trackingID + " " + authRequest);

            authTokenResponse = EcospendUtils.getAccessToken(AUTHTOKEN_URL, authRequest.toString(), RequestId);

            transactionLogger.error("Payout authTokenResponse " + trackingID + " " + authTokenResponse);

            String access_token = "";
            String token_type   = "";
            String scopeResp    = "";

            if (functions.isValueNull(authTokenResponse) && EcospendUtils.isJSONValid(authTokenResponse))
            {
                JSONObject authToken = new JSONObject(authTokenResponse);

                if (authToken.has("access_token"))
                {
                    access_token = authToken.getString("access_token");
                }
            }

            if (functions.isValueNull(access_token))
            {
                transactionLogger.error("payoutInquiryRequest------> "+ trackingID+" " +REQUEST_URL);
                responeString    = EcospendUtils.doGetHTTPSURLConnectionClient(REQUEST_URL, access_token);
            }
            transactionLogger.error("payoutInquiryResponse------> "+ trackingID +" "+ responeString);

            String transaction_Id        = "";
            String reference_id          = "";
            String status                = "";
            String payout_date          = "";
            String original_payment_id  = "";
            String bank_id              = "";
            String amount               = "";
            String currency             = "";
            String error                = "";

            if(functions.isValueNull(responeString) && EcospendUtils.isJSONValid(responeString) )
            {
                JSONObject payoutResponse   = new JSONObject(responeString);
                JSONArray jsonArray          = null;
                JSONObject jsonArrayResponse = new JSONObject();

                if(payoutResponse.has("data") && payoutResponse.getJSONArray("data") != null){
                    jsonArray           =  payoutResponse.getJSONArray("data");
                    jsonArrayResponse   = jsonArray.getJSONObject(0);

                }

                if(jsonArrayResponse != null){
                    if(jsonArrayResponse.has("status")){
                        status = jsonArrayResponse.getString("status");
                    }
                    if(jsonArrayResponse.has("id")){
                        transaction_Id = jsonArrayResponse.getString("id");
                    }
                    if(jsonArrayResponse.has("amount")){
                        amount = jsonArrayResponse.getString("amount");
                    }
                    if(jsonArrayResponse.has("payout_date")){
                        payout_date = jsonArrayResponse.getString("payout_date");
                    }
                }


                if (status.equalsIgnoreCase("Completed"))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setRemark(status);
                    commResponseVO.setDescription(status);
                    commResponseVO.setTransactionId(transaction_Id);
                    commResponseVO.setResponseHashInfo(transaction_Id);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setResponseTime(payout_date);
                    commResponseVO.setAuthCode(status);
                    commResponseVO.setErrorCode(status);
                    commResponseVO.setMerchantId(client_id);
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                }else if(status.equalsIgnoreCase("Initial") || status.equalsIgnoreCase("AwaitingAuthorization")
                        || status.equalsIgnoreCase("Authorised") || status.equalsIgnoreCase("Verified") ){
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(status);
                    commResponseVO.setDescription(status);
                }
                else if (status.equalsIgnoreCase("Canceled") || status.equalsIgnoreCase("Failed") ||
                        status.equalsIgnoreCase("Rejected"))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(status);
                    commResponseVO.setDescription(status);
                    commResponseVO.setTransactionId(transaction_Id);
                    commResponseVO.setResponseHashInfo(transaction_Id);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setResponseTime(payout_date);
                    commResponseVO.setAuthCode(status);
                    commResponseVO.setErrorCode(status);
                    commResponseVO.setMerchantId(client_id);
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());

                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                }

            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Invalid Response");
                commResponseVO.setDescription("Invalid Response");
            }

        }
        catch (Exception e)
        {
            transactionLogger.error("processPayoutInquiry JSONException-->" ,e );
        }

        return commResponseVO;

    }
}
