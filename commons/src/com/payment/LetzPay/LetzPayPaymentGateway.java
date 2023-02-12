package com.payment.LetzPay;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.google.gson.Gson;
import com.manager.PaymentManager;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.qikpay.QikPayPaymentProcess;
import com.payment.qikpay.QikPayUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * Created by Vivek on 3/16/2021.
 */
public class LetzPayPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "letzpay";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.letzpay");
    private static LetzPayGatewayLogger transactionLogger = new LetzPayGatewayLogger(LetzPayPaymentGateway.class.getName());

    public LetzPayPaymentGateway(String accountId){this.accountId=accountId;}

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("--- Inside sale method of LetzPayPaymentGateway ---");
        Comm3DResponseVO comm3DResponseVO=new Comm3DResponseVO();
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        Functions functions=new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String salt = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String encryptionKey = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String serverHashKey = GatewayAccountService.getGatewayAccount(accountId).getCHARGEBACK_FTP_PATH();
        boolean isTest = gatewayAccount.isTest();
        String paymentMode=GatewayAccountService.getPaymentMode(commTransactionDetailsVO.getPaymentType());
        String termUrl = "";
        String saleUrl = "";
        String amount=LetzPayUtils.getAmount(commTransactionDetailsVO.getAmount());
        String currencyCode=CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency());
        String email=commAddressDetailsVO.getEmail();
        String phone=commAddressDetailsVO.getPhone();
        if (functions.isValueNull(commAddressDetailsVO.getPhone()))
        {

            if (commAddressDetailsVO.getPhone().contains("-"))
            {
                phone = commAddressDetailsVO.getPhone().split("\\-")[1];
            }
            if (phone.length() > 10)
            {

                phone = phone.substring(phone.length() - 10);
            }
            else
            {
                phone = commAddressDetailsVO.getPhone();
            }

        }
        else
        {
            phone = "9999999999";
        }
        String name="";
        String country=commAddressDetailsVO.getCountry();
        String state=commAddressDetailsVO.getState();
        String street=commAddressDetailsVO.getStreet();
        String city=commAddressDetailsVO.getCity();
        String zip=commAddressDetailsVO.getZipCode();
        String paymentType="";
        String card="";
        String expDate="";
        String cvv="";
        String customerId="";
        String vpaAddress="";
        String paymentProcessor=commTransactionDetailsVO.getCardType();
        String MOP_TYPE="";
        try
        {
            if ("CC".equalsIgnoreCase(paymentMode) || "DC".equalsIgnoreCase(paymentMode))
            {
                paymentType = "CARD";
                card = commCardDetailsVO.getCardNum();
                expDate = commCardDetailsVO.getExpMonth() + commCardDetailsVO.getExpYear();
                cvv = commCardDetailsVO.getcVV();
            }
            else if ("NB".equalsIgnoreCase(paymentMode) || "NBI".equalsIgnoreCase(paymentMode))
            {
                paymentType = "NB";
                transactionLogger.error("paymentProcessor--->" + paymentProcessor);
                MOP_TYPE = paymentProcessor;

            }
            else if ("EWI".equalsIgnoreCase(paymentMode))
            {
                paymentType = "WL";
                MOP_TYPE = paymentProcessor;
            }
            else if ("UPI".equalsIgnoreCase(paymentMode))
            {
                paymentType = "UP";
                vpaAddress = commTransactionDetailsVO.getVpaAddress();
                customerId=vpaAddress;
            }
            else{
                comm3DResponseVO.setStatus("fail");
                comm3DResponseVO.setDescription("Incorrect Request");
                comm3DResponseVO.setRemark("Incorrect Request");
                return comm3DResponseVO ;
            }

            if (functions.isValueNull(encryptionKey))
                encryptionKey = LetzPayUtils.getHash(encryptionKey);
            if (functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname()))
                name = new String(commAddressDetailsVO.getFirstname().getBytes(),"UTF-8") + " " + new String(commAddressDetailsVO.getLastname().getBytes(),"UTF-8");
            else if (functions.isValueNull(commAddressDetailsVO.getFirstname()))
                name = new String(commAddressDetailsVO.getFirstname().getBytes(),"UTF-8");
            else if (functions.isValueNull(commAddressDetailsVO.getLastname()))
                name = new String(commAddressDetailsVO.getLastname().getBytes(),"UTF-8");


            if(functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                termUrl="https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL")+URLEncoder.encode("trackingId=","UTF-8")+trackingID;;
            }else
            {
                termUrl = RB.getString("TERM_URL")+URLEncoder.encode("trackingId=","UTF-8")+trackingID;
            }

            if (isTest)
            {
                saleUrl = RB.getString("TEST_SALE_URL");
            }
            else
            {
                saleUrl = RB.getString("LIVE_SALE_URL");
            }
            TreeMap<String, String> requestMap = new TreeMap();
            TreeMap<String,String> requestMapLog = new TreeMap();
            requestMap.put("PAY_ID", mId);
            requestMapLog.put("PAY_ID", mId);
            requestMap.put("ORDER_ID", trackingID);
            requestMapLog.put("ORDER_ID", trackingID);
            requestMap.put("RETURN_URL", termUrl);
            requestMapLog.put("RETURN_URL", termUrl);
            requestMap.put("CUST_NAME", name);
            requestMapLog.put("CUST_NAME", name);
            requestMap.put("CUST_PHONE", phone);
            requestMapLog.put("CUST_PHONE", phone);
            requestMap.put("CUST_EMAIL", email);
            requestMapLog.put("CUST_EMAIL", email);
            requestMap.put("AMOUNT", amount);
            requestMapLog.put("AMOUNT", amount);
            requestMap.put("CURRENCY_CODE", currencyCode);
            requestMapLog.put("CURRENCY_CODE", currencyCode);
            if (functions.isValueNull(street))
            {
                requestMap.put("CUST_STREET_ADDRESS1", street);
                requestMapLog.put("CUST_STREET_ADDRESS1", street);
            }
            if (functions.isValueNull(city))
            {
                requestMap.put("CUST_CITY", city);
                requestMapLog.put("CUST_CITY", city);
            }
            if (functions.isValueNull(state))
            {
                requestMap.put("CUST_STATE", state);
                requestMapLog.put("CUST_STATE", state);
            }
            if (functions.isValueNull(country))
            {
                requestMap.put("CUST_COUNTRY", country);
                requestMapLog.put("CUST_COUNTRY", country);
            }
            if (functions.isValueNull(zip))
            {
                requestMap.put("CUST_ZIP", zip);
                requestMapLog.put("CUST_ZIP", zip);
            }
            if (functions.isValueNull(card))
            {
                requestMap.put("CARD_NUMBER", card);
                requestMap.put("CARD_EXP_DT", expDate);
                requestMap.put("CARD_HOLDER_NAME", name);
                requestMap.put("CVV", cvv);

                requestMapLog.put("CARD_NUMBER", functions.maskingPan(card));
                requestMapLog.put("CARD_EXP_DT", functions.maskingNumber(expDate));
                requestMapLog.put("CVV", functions.maskingNumber(cvv));
            }
            if (functions.isValueNull(vpaAddress))
            {
                requestMap.put("PAYER_ADDRESS", vpaAddress);
                requestMapLog.put("PAYER_ADDRESS", vpaAddress);
            }
            if (functions.isValueNull(MOP_TYPE))
            {
                requestMap.put("MOP_TYPE", MOP_TYPE);
                requestMapLog.put("MOP_TYPE", MOP_TYPE);
            }
            requestMap.put("PAYMENT_TYPE", paymentType);
            requestMapLog.put("PAYMENT_TYPE", paymentType);
            String s = LetzPayUtils.convertMapToSpring(requestMap, salt, "");
            String hash = LetzPayUtils.hashSHA256(s);
            String request = LetzPayUtils.convertMapToSpring(requestMap, "", hash);
            String requestLog = LetzPayUtils.convertMapToSpring(requestMapLog, "", hash);
            transactionLogger.error("request after hash append--"+trackingID+"->" + requestLog);
            String requestHash = LetzPayUtils.encryptAES(request, encryptionKey);
            transactionLogger.error("requestHash--"+trackingID+"->" + requestHash);

            TreeMap finalrequestMap=new TreeMap();
            finalrequestMap.put("PAY_ID",mId);
            finalrequestMap.put("ENCDATA",requestHash);
            String finalrequest=LetzPayUtils.convertMapToSpring(finalrequestMap, serverHashKey, "");
            String finalHash=LetzPayUtils.hashSHA256(finalrequest);

            HashMap<String, String> formMap = new HashMap<>();
            formMap.put("PAY_ID", mId);
            formMap.put("ENCDATA", requestHash);
            formMap.put("HASH", finalHash);
            boolean isUpdate=false;
            comm3DResponseVO.setUrlFor3DRedirect(saleUrl);
            comm3DResponseVO.setStatus("pending3DConfirmation");
            comm3DResponseVO.setRemark("Transaction is pending");
            comm3DResponseVO.setDescription("Transaction is pending");

            comm3DResponseVO.setRequestMap(formMap);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException----->", e);
        }
        return comm3DResponseVO;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("IlixiumPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error(" ---- Autoredict in Letzpay ---- ");
        String html                         = "";
        PaymentManager paymentManager       = new PaymentManager();
        Comm3DResponseVO transRespDetails   = null;
        LetzPayUtils letzPayUtils =new LetzPayUtils();
        LetzPayPaymentProcess letzPayPaymentProcess=new LetzPayPaymentProcess();
        String paymentMode              = commonValidatorVO.getPaymentMode();

        CommRequestVO commRequestVO     = letzPayUtils.getCommRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        try
        {
            transactionLogger.error("isService Flag -----" + commonValidatorVO.getMerchantDetailsVO().getIsService());
            transactionLogger.error("autoredirect  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAutoRedirect());
            transactionLogger.error("addressdetail  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAddressDeatails());
            transactionLogger.error("terminal id  is -----" + commonValidatorVO.getTerminalId());
            transactionLogger.error("tracking id  is Letzpay -----" + commonValidatorVO.getTrackingid());

            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = letzPayPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionLogger.error("automatic redirect Letzpay form -- >>"+html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException in LetpayPaymentGateway---", e);
        }
        return html;
    }
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("Inside processQuery ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mid = gatewayAccount.getMerchantId();
        String salt = gatewayAccount.getFRAUD_FTP_USERNAME();
        boolean isTest = gatewayAccount.isTest();
        String amount=LetzPayUtils.getAmount(commTransactionDetailsVO.getAmount());
        String currencyCode=CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency());
        String inquiryUrl="";
        String bankTransactionDate="";
        String responseCode="";
        String rrn="";
        String transactionId="";
        String message="";
        String resStatus="";
        String amountRes="";
        String PG_TXN_MESSAGE="";
        try
        {
            if (isTest)
            {
                inquiryUrl = RB.getString("TEST_INQUIRY_URL");
            }
            else
            {
                inquiryUrl = RB.getString("LIVE_INQUIRY_URL");
            }
            TreeMap requestMap = new TreeMap();
            requestMap.put("PAY_ID", mid);
            requestMap.put("ORDER_ID", trackingID);
            requestMap.put("AMOUNT", amount);
            requestMap.put("TXNTYPE", "STATUS");
            requestMap.put("CURRENCY_CODE", currencyCode);
            String hashStr=LetzPayUtils.convertMapToSpring(requestMap,salt,"");
            String hash = LetzPayUtils.hashSHA256(hashStr);
            requestMap.put("HASH",hash);
            Gson gson=new Gson();
            String request=gson.toJson(requestMap);
            transactionLogger.error("Inquiry Request--"+trackingID+"-->"+request);
            String response=LetzPayUtils.doPostHTTPSURLConnectionClient(inquiryUrl,request);
            transactionLogger.error("Inquiry Response--"+trackingID+"-->"+response);
            if(functions.isValueNull(response))
            {
                JSONObject responseJson=new JSONObject(response);
                if(responseJson.has("RESPONSE_DATE_TIME"))
                    bankTransactionDate=responseJson.getString("RESPONSE_DATE_TIME");
                if(responseJson.has("RESPONSE_CODE"))
                    responseCode=responseJson.getString("RESPONSE_CODE");
                if(responseJson.has("TXN_ID"))
                    transactionId=responseJson.getString("TXN_ID");
                if(responseJson.has("RRN"))
                    rrn=responseJson.getString("RRN");
                if(responseJson.has("RESPONSE_MESSAGE"))
                    message=responseJson.getString("RESPONSE_MESSAGE");
                if(responseJson.has("PG_TXN_MESSAGE"))
                    PG_TXN_MESSAGE=responseJson.getString("PG_TXN_MESSAGE");
                if(responseJson.has("STATUS"))
                    resStatus=responseJson.getString("STATUS");
                if(responseJson.has("AMOUNT"))
                {
                    amountRes = responseJson.getString("AMOUNT");
                    transactionLogger.error("Inquiry Response amount--"+trackingID+"-->"+amountRes);
                    amountRes=String.format("%.2f", Double.parseDouble(amountRes)/100);
                    transactionLogger.error("Inquiry formated Response amount--"+trackingID+"-->"+amountRes);
                }

                if("000".equalsIgnoreCase(responseCode) && "Captured".equalsIgnoreCase(resStatus))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setRemark(PG_TXN_MESSAGE);
                    commResponseVO.setDescription(PG_TXN_MESSAGE);
                }else if("000".equalsIgnoreCase(responseCode) && "Sent to Bank".equalsIgnoreCase(resStatus))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    message=resStatus;
                    commResponseVO.setRemark(PG_TXN_MESSAGE);
                    commResponseVO.setDescription(PG_TXN_MESSAGE);
                }else if(PG_TXN_MESSAGE.contains("Response not received from acquirer")||"900".equalsIgnoreCase(responseCode)||"010".equalsIgnoreCase(responseCode)||"022".equalsIgnoreCase(responseCode)||"006".equalsIgnoreCase(responseCode) || "026".equalsIgnoreCase(responseCode)|| "032".equalsIgnoreCase(responseCode))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(PG_TXN_MESSAGE);
                    commResponseVO.setDescription(PG_TXN_MESSAGE);
                }
                else if("Error".equalsIgnoreCase(resStatus)||"Failed".equalsIgnoreCase(resStatus)||"Declined".equalsIgnoreCase(resStatus)){
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(PG_TXN_MESSAGE);
                    commResponseVO.setDescription(PG_TXN_MESSAGE);
                }
                else{
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    message="Your Transaction is Pending Please check the status after some time";
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                }
                commResponseVO.setBankTransactionDate(bankTransactionDate);
                commResponseVO.setTransactionId(transactionId);
                commResponseVO.setRrn(rrn);
                commResponseVO.setMerchantId(mid);
                commResponseVO.setTransactionType("Sale");
                commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
                if(!functions.isValueNull(amountRes))
                    amountRes=commTransactionDetailsVO.getAmount();
                commResponseVO.setAmount(amountRes);
                commResponseVO.setErrorCode(responseCode);
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending"); // set inquir
            }

        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---"+trackingID+"--->",e);
        }

        return commResponseVO;
    }
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error("Inside processRefund ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mid = gatewayAccount.getMerchantId();
        boolean isTest = gatewayAccount.isTest();
        String salt = gatewayAccount.getFRAUD_FTP_USERNAME();
        String amount=LetzPayUtils.getAmount(commTransactionDetailsVO.getAmount());
        String currencyCode=CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency());
        String inquiryUrl="";
        String bankTransactionDate="";
        String responseCode="";
        String rrn="";
        String transactionId="";
        String message="";
        String resStatus="";
        String PG_REF_NUM="";
        try
        {
            if (isTest)
            {
                inquiryUrl = RB.getString("TEST_INQUIRY_URL");
            }
            else
            {
                inquiryUrl = RB.getString("LIVE_INQUIRY_URL");
            }
            TreeMap requestMap = new TreeMap();
            requestMap.put("PAY_ID", mid);
            requestMap.put("PG_REF_NUM", commTransactionDetailsVO.getPaymentId());
            requestMap.put("ORDER_ID", trackingID);
            requestMap.put("AMOUNT", amount);
            requestMap.put("CURRENCY_CODE", currencyCode);
            requestMap.put("TXNTYPE", "REFUND");
            requestMap.put("REFUND_ORDER_ID", "RF_"+commRequestVO.getCount()+"_"+trackingID);
            String hashStr=LetzPayUtils.convertMapToSpring(requestMap, salt, "");
            transactionLogger.error("hashStr--->"+hashStr);
            String hash = LetzPayUtils.hashSHA256(hashStr);
            requestMap.put("HASH",hash);
            Gson gson=new Gson();
            String request=gson.toJson(requestMap);
            transactionLogger.error("Refund Request--"+trackingID+"-->"+request);
            String response=LetzPayUtils.doPostHTTPSURLConnectionClient(inquiryUrl,request);
            transactionLogger.error("Refund Response--"+trackingID+"-->"+response);
            if(functions.isValueNull(response))
            {
                JSONObject responseJson=new JSONObject(response);
                if(responseJson.has("RESPONSE_DATE_TIME"))
                    bankTransactionDate=responseJson.getString("RESPONSE_DATE_TIME");
                if(responseJson.has("RESPONSE_CODE"))
                    responseCode=responseJson.getString("RESPONSE_CODE");
                if(responseJson.has("TXN_ID"))
                    transactionId=responseJson.getString("TXN_ID");
                if(responseJson.has("RRN"))
                    rrn=responseJson.getString("RRN");
                if(responseJson.has("RESPONSE_MESSAGE"))
                    message=responseJson.getString("RESPONSE_MESSAGE");
                if(responseJson.has("STATUS"))
                    resStatus=responseJson.getString("STATUS");
                if(responseJson.has("PG_REF_NUM"))
                    PG_REF_NUM=responseJson.getString("PG_REF_NUM");

                if("000".equalsIgnoreCase(responseCode) && "Captured".equalsIgnoreCase(resStatus))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else if("006".equalsIgnoreCase(responseCode) || "026".equalsIgnoreCase(responseCode)|| "032".equalsIgnoreCase(responseCode))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                }else{
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                }
                commResponseVO.setBankTransactionDate(bankTransactionDate);
                commResponseVO.setTransactionId(transactionId);
                commResponseVO.setRemark(message);
                commResponseVO.setDescription(message);
                commResponseVO.setRrn(rrn);
                commResponseVO.setResponseHashInfo(PG_REF_NUM);
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---"+trackingID+"--->",e);
        }

        return commResponseVO;
    }
    public GenericResponseVO processPayout(String trackingID,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside letzpay process payout-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mid=gatewayAccount.getMerchantId();
        boolean isTest = gatewayAccount.isTest();
        String salt = gatewayAccount.getFRAUD_FTP_USERNAME();
        String amount=LetzPayUtils.getAmount(commTransactionDetailsVO.getAmount());
        String currencyCode=CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency());
        String payoutUrl="";
        String responseCode="";
        String transactionId="";
        String message="";
        String resMessage="";
        String resMsg="";
        String beneName="";
        String accountNo="";
        String ifscCode="";
        String ACQ_ID="";
        String RRN="";
        String PG_TXN_STATUS="";
        String STATUS="";
        String phone=commAddressDetailsVO.getPhone();
        String PURPOSE="Loyalty Points Redemption";
        if (functions.isValueNull(commTransactionDetailsVO.getCustomerBankAccountName()))
        {
            beneName = commTransactionDetailsVO.getCustomerBankAccountName();
        }
        if (functions.isValueNull(commTransactionDetailsVO.getBankAccountNo()))
        {
            accountNo = commTransactionDetailsVO.getBankAccountNo();
        }
        if (functions.isValueNull(commTransactionDetailsVO.getBankIfsc()))
        {
            ifscCode = commTransactionDetailsVO.getBankIfsc();
        }
        if(!functions.isValueNull(phone))
            phone="9999999999";

        if (isTest)
        {
            payoutUrl = RB.getString("TEST_PAYOUT_URL");
        }
        else
        {
            payoutUrl = RB.getString("LIVE_PAYOUT_URL");
        }
        try
        {
            TreeMap requestMap = new TreeMap();
            requestMap.put("PAY_ID", mid);
            requestMap.put("ORDER_ID", trackingID);
            requestMap.put("AMOUNT", amount);
            requestMap.put("CURRENCY_CODE", currencyCode);
            requestMap.put("BENE_NAME", beneName);
            requestMap.put("BENE_ACCOUNT_NO", accountNo);
            requestMap.put("IFSC_CODE", ifscCode);
            requestMap.put("PURPOSE", PURPOSE);
            requestMap.put("PHONE_NO", phone);
            String hashStr = LetzPayUtils.convertMapToSpring(requestMap, salt, "");
            transactionLogger.error("payoutUrl--->" + payoutUrl);
            transactionLogger.error("hashStr--->" + hashStr);
            String hash = LetzPayUtils.hashSHA256(hashStr);
            requestMap.put("HASH", hash);
            Gson gson = new Gson();
            String request = gson.toJson(requestMap);
            transactionLogger.error("Payout Request--" + trackingID + "-->" + request);
            String response = LetzPayUtils.doPostHTTPSURLConnectionClient(payoutUrl, request);
            transactionLogger.error("Payout Response--" + trackingID + "-->" + response);

            if (functions.isValueNull(response)&& response.contains("{"))
                    {
                JSONObject responseJson = new JSONObject(response);

            if(responseJson.has("RESPONSE_CODE"))
               responseCode=responseJson.getString("RESPONSE_CODE");
            if(responseJson.has("ACQ_ID"))
                ACQ_ID=responseJson.getString("ACQ_ID");
            if(responseJson.has("RRN"))
                RRN=responseJson.getString("RRN");
            if(responseJson.has("PG_TXN_STATUS"))
                PG_TXN_STATUS=responseJson.getString("PG_TXN_STATUS");
            if(responseJson.has("TXN_ID"))
               transactionId=responseJson.getString("TXN_ID");
            if(responseJson.has("PG_TXN_MESSAGE"))
               message=responseJson.getString("PG_TXN_MESSAGE");
            if(responseJson.has("RESPONSE_MESSAGE"))
               resMsg=responseJson.getString("RESPONSE_MESSAGE");
            if(responseJson.has("STATUS"))
                STATUS=responseJson.getString("STATUS");

                if("000".equalsIgnoreCase(responseCode)||"Captured".equalsIgnoreCase(STATUS))
                {   commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else if("Invalid at acquirer".equalsIgnoreCase(STATUS)){
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                }

                else if(("026".equalsIgnoreCase(responseCode) && "032".equalsIgnoreCase(responseCode))&& "Pending".equalsIgnoreCase(STATUS)){
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                }
                else {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                }

                commResponseVO.setTransactionId(transactionId);
                commResponseVO.setRemark(message);
                commResponseVO.setDescription(message);

            }
            else{
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("pending");
                commResponseVO.setDescription("pending");
            }

        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---"+trackingID+"--->",e);
        }

        return commResponseVO;
    }
    public GenericResponseVO processPayoutInquiry(String trackingID,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside Letzpay process payout inquiry------->");
        Functions functions=new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        HashMap<String,String> transactionMap=LetzPayUtils.getTransactionDetails(trackingID);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mid=gatewayAccount.getMerchantId();
        boolean isTest = gatewayAccount.isTest();
        String salt = gatewayAccount.getFRAUD_FTP_USERNAME();
        String amount=LetzPayUtils.getAmount(commTransactionDetailsVO.getAmount());
        String accountNo=transactionMap.get("bankaccount");
        if(!functions.isValueNull(amount))
            amount=LetzPayUtils.getAmount(transactionMap.get("amount"));
        transactionLogger.error("trackingId---------->" + trackingID);
        transactionLogger.error("isTest---------->" + isTest);
        String payoutInquiryURL = "";
        String responseCode="";
        String transactionId="";
        String message="";
        String resMessage="";
        String resStatus="";
        if (isTest)
        {
            payoutInquiryURL = RB.getString("PAYOUT_INQUIRY_TEST_URL");
        }
        else
        {
            payoutInquiryURL = RB.getString("PAYOUT_INQUIRY_LIVE_URL");

        }

        try
        {
            TreeMap requestMap = new TreeMap();
            requestMap.put("PAY_ID", mid);
            requestMap.put("ORDER_ID", trackingID);
            requestMap.put("AMOUNT", amount);
            requestMap.put("BENE_ACCOUNT_NO", accountNo);
            String hashStr = LetzPayUtils.convertMapToSpring(requestMap, salt, "");
            transactionLogger.error("hashStr--->" + hashStr);
            String hash = LetzPayUtils.hashSHA256(hashStr);
            requestMap.put("HASH", hash);
            Gson gson = new Gson();
            String request = gson.toJson(requestMap);
            transactionLogger.error("Payout Request--" + trackingID + "-->" + request);
            String response = LetzPayUtils.doPostHTTPSURLConnectionClient(payoutInquiryURL, request);
            transactionLogger.error("Payout Response--" + trackingID + "-->" + response);

            if (functions.isValueNull(response)&& response.contains("{"))
            {
                JSONObject responseJson = new JSONObject(response);

                if(responseJson.has("RESPONSE_CODE"))
                    responseCode=responseJson.getString("RESPONSE_CODE");
                if(responseJson.has("TXN_ID"))
                    transactionId=responseJson.getString("TXN_ID");
                if(responseJson.has("PG_TXN_MESSAGE"))
                    message=responseJson.getString("PG_TXN_MESSAGE");
                if(responseJson.has("STATUS"))
                    resStatus=responseJson.getString("STATUS");
                if(responseJson.has("RESPONSE_MESSAGE"))
                    resMessage=responseJson.getString("RESPONSE_MESSAGE");

                if("000".equalsIgnoreCase(responseCode) && "Captured".equalsIgnoreCase(resStatus))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else if("Declined".equalsIgnoreCase(resStatus)||"Rejected".equalsIgnoreCase(resStatus)||"Timeout".equalsIgnoreCase(resStatus)||"Invalid at acquirer".equalsIgnoreCase(resStatus)){
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                }

                else {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                }

                commResponseVO.setTransactionId(transactionId);
                commResponseVO.setRemark(resMessage);
                commResponseVO.setDescription(resMessage);

            }
            else{
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("pending");
                commResponseVO.setDescription("pending");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---"+trackingID+"--->",e);
        }

        return commResponseVO;
    }
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
