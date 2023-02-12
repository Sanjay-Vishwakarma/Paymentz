package com.payment.cashfree;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.fraud.fourstop.MultipartUtility;
import com.manager.PaymentManager;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.qikpay.QikPayPaymentProcess;
import com.payment.qikpay.QikPayUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * Created by Admin on 5/15/2021.
 */
public class CashFreePaymentGateway extends AbstractPaymentGateway
{

    private static TransactionLogger transactionlogger = new TransactionLogger(CashFreePaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "cashfree";
    public static final String CARD = "CARD";
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.cashfree");
    CommonValidatorVO commonValidatorVO = new CommonValidatorVO();

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public CashFreePaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of CashFreePaymentGateway......");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CashFreeUtils cashFreeUtils = new CashFreeUtils();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String payment_Card = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());

        boolean isTest = gatewayAccount.isTest();
        String appId = gatewayAccount.getMerchantId();
        String orderId = trackingID;
        String orderAmount = transactionDetailsVO.getAmount();
        String orderNote = trackingID; 
        String customerName = "";
        String card_holder = "";
        String paymentUrl = "";
        String paymentOption = "";
        String paymentCode = "";
        String orderCurrency = "INR";
        String vpa=commRequestVO.getCustomerId();
        String payment_brand  = transactionDetailsVO.getCardType();
        transactionlogger.error("cashfree gateway vpa is----------->"+vpa);
        if (functions.isValueNull(commAddressDetailsVO.getFirstname()))
        {
            if (functions.isValueNull(commAddressDetailsVO.getLastname()))
            {
                customerName = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();
            }
            else
            {
                customerName = commAddressDetailsVO.getFirstname();
            }
        }
        else
        {
            customerName = "";
        }
        card_holder = customerName;
        String customerPhone = "";
        if (functions.isValueNull(commAddressDetailsVO.getPhone()))
        {
            customerPhone = commAddressDetailsVO.getPhone();
        }
        else
        {
            customerPhone = "9999999999";
        }
        String customerEmail = "";
        if (functions.isValueNull(commAddressDetailsVO.getEmail()))
        {
            customerEmail = commAddressDetailsVO.getEmail();
        }
        /*String returnUrl = RB.getString("ReturnUrl")+trackingID;
        String notifyUrl = RB.getString("ReturnUrl")+trackingID;  //backend*/

        String returnUrl = "";
        String notifyUrl = "";  //backend

        if(functions.isValueNull(gatewayAccount.getFRAUD_FTP_SITE())){
            returnUrl = "https://checkout." + gatewayAccount.getFRAUD_FTP_SITE() + RB.getString("HOSTURL")+trackingID;
          //  returnUrl = RB.getString("ReturnUrl")+trackingID;
        }else{
            returnUrl = RB.getString("ReturnUrl")+trackingID;
        }
        if(functions.isValueNull(gatewayAccount.getFRAUD_FTP_SITE())){
            notifyUrl = "https://checkout." + gatewayAccount.getFRAUD_FTP_SITE() + RB.getString("HOSTURL")+trackingID;
          //  notifyUrl = RB.getString("ReturnUrl")+trackingID;
        }else{
            notifyUrl = RB.getString("ReturnUrl")+trackingID;
        }
        transactionlogger.error("returnUrl live-->" + returnUrl);
        transactionlogger.error("notifyUrl live-->" + notifyUrl);


        String signature = "";
        String secretKey = gatewayAccount.getFRAUD_FTP_PASSWORD(); //"d21ed9fab44af276472e783cc2c0162f89941fcf";
        paymentOption    = cashFreeUtils.getPaymentTypeS2S(payment_Card);
        //card
        String card_number = commCardDetailsVO.getCardNum();
        ;
        String card_expiryMonth = commCardDetailsVO.getExpMonth();
        String card_expiryYear = commCardDetailsVO.getExpYear();
        String card_cvv = commCardDetailsVO.getcVV();


        if (isTest)
        {
            transactionlogger.error("Inside isTest");

            paymentUrl = RB.getString("TEST_SALE_URL");
        }
        else
        {
            paymentUrl = RB.getString("LIVE_SALE_URL");
        }
        transactionlogger.error("paymentUrl live-->" + paymentUrl);

        try
        {

            Map<String, String> postData = new HashMap<String, String>();

            postData.put("appId", appId);
            postData.put("orderId", orderId);
            postData.put("orderAmount", orderAmount);
            postData.put("orderCurrency", orderCurrency);
            postData.put("orderNote", orderNote);
            postData.put("customerName", customerName);
            postData.put("customerEmail", customerEmail);
            postData.put("customerPhone", customerPhone);
            postData.put("returnUrl", returnUrl);
            postData.put("notifyUrl", notifyUrl);
            postData.put("paymentOption", paymentOption);
            if(paymentOption.equalsIgnoreCase("card"))
            {
                postData.put("card_number", card_number);
                postData.put("card_expiryMonth", card_expiryMonth);
                postData.put("card_expiryYear", card_expiryYear);
                postData.put("card_cvv", card_cvv);
                postData.put("card_holder", card_holder);
            }
            else if(paymentOption.equalsIgnoreCase("nb"))
            {
                postData.put("paymentCode", payment_brand);
            }
            else if(paymentOption.equalsIgnoreCase("upi"))
            {
                postData.put("upi_vpa", vpa);
            }
            else{
                commResponseVO.setStatus("fail");
                commResponseVO.setDescription("Incorrect request");
                commResponseVO.setRemark("Incorrect request");
                return commResponseVO ;
            }
            signature = cashFreeUtils.getSignature(postData, secretKey);
            transactionlogger.error("signature is-------------->" + signature);
            commResponseVO.setStatus("pending3DConfirmation");
            commResponseVO.setUrlFor3DRedirect(paymentUrl);
            commResponseVO.setRequestMap(postData);
            commResponseVO.setPaReq(signature);


        }
        catch (Exception e)
        {
            transactionlogger.error("Exception cashfree gateway ---------------> " + e);
        }

        return commResponseVO;
    }

    @Override
    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredict in Qikpay ---- ");
        String html = "";
        Comm3DResponseVO transRespDetails = null;
        CashFreeUtils cashFreeUtils = new CashFreeUtils();
        CashFreePaymentProcess cashFreePaymentProcess = new CashFreePaymentProcess();
        CommRequestVO commRequestVO = cashFreeUtils.getCashFreeRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        try
        {
            transactionlogger.error("isService Flag -----" + commonValidatorVO.getMerchantDetailsVO().getIsService());
            transactionlogger.error("autoredirect  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAutoRedirect());
            transactionlogger.error("addressdetail  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAddressDeatails());
            transactionlogger.error("tracking id  is -----" + commonValidatorVO.getTrackingid());
            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = cashFreePaymentProcess.get3DConfirmationForm(commonValidatorVO, transRespDetails);
                transactionlogger.error("automatic redirect Cashfree form -- >>" + html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in CashFreePaymentGateway---", e);
        }
        return html;
    }

    public GenericResponseVO processQuery1(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside processInquiry of CashFree Gateway---");
        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        Functions functions = new Functions();
        String PAY_ID = "";
        String ORDER_ID = trackingId;
        String appId = gatewayAccount.getMerchantId();
        String secretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String orderStatus = "";
        String orderAmount = "";
        String status = "";
        String txStatus = "";
        String utr = "";
        String txMsg = "";
        String referenceId = "";
        String paymentMode = "";
        String orderCurrency = "";
        String orderId = "";
        String paymentDetailsPaymentMode = "";
        String cardNumber = "";
        String cardCountry = "";
        String cardScheme = "";
        String authIdCode = "";
        List<String> response = null;
        String inquiry_res = "";
        try
        {
            transactionlogger.error("isTest  --- >" + isTest);
            transactionlogger.error("PAY_ID is --- >" + PAY_ID);
            transactionlogger.error("ORDER_ID is --- >" + ORDER_ID);
            String inquiryReqestData = "";

            String url = "";

            if (isTest)
            {
                url=RB.getString("INQUIRY_TEST_URL");
                transactionlogger.error("inside test req of inquiry is -- >" + RB.getString("INQUIRY_TEST_URL"));

            }
            else
            {
                url=RB.getString("INQUIRY_LIVE_URL");
                transactionlogger.error("inside live req of inquiry is -- >" + RB.getString("INQUIRY_LIVE_URL"));

            }
            MultipartUtility multipartUtility=new MultipartUtility(url,"UTF-8");
            multipartUtility.addHeaderField("Content-Type","multipart/form-data");
            multipartUtility.addFormField("appId",appId);
            multipartUtility.addFormField("orderId",trackingId);
            multipartUtility.addFormField("secretKey",secretKey);
            response=  multipartUtility.finish();

            transactionlogger.error("inquiry response is -->" + response);

            String resString = "";
            JSONArray responseArray = new JSONArray(response);
            transactionlogger.error("array length  is -->" + responseArray.length());
            for (int i = 0; i < responseArray.length(); i++)
            {
                resString = responseArray.getString(i);
                transactionlogger.error("resString is -->" + resString);
                JSONObject jsonres=new JSONObject(resString);
                JSONObject paymentDetails=jsonres.getJSONObject("paymentDetails");
                if (jsonres.has("orderStatus"))
                   orderStatus = jsonres.getString("orderStatus");
                if (jsonres.has("orderAmount"))
                   orderAmount = jsonres.getString("orderAmount");
                if (jsonres.has("txStatus"))
                   txStatus = jsonres.getString("txStatus");
                if (jsonres.has("referenceId"))
                   referenceId = jsonres.getString("referenceId");
                if (paymentDetails.has("utr"))
                   utr = paymentDetails.getString("utr");

            }
            transactionlogger.error("inquiry res is -- >" + inquiry_res);


            if (functions.isValueNull(txStatus))
            {
                transactionlogger.error("inside if cashfree inquiry response trackingId-->" + trackingId+"response "+inquiry_res);

                if (txStatus.equalsIgnoreCase("SUCCESS") && orderStatus.equalsIgnoreCase("PAID"))
                {
                    commResponseVO.setStatus("Success");
                    commResponseVO.setDescription("Success");
                    commResponseVO.setRemark(txStatus);
                    commResponseVO.setTransactionStatus("Success");
                    commResponseVO.setAmount(orderAmount);
                    commResponseVO.setTransactionId(referenceId);
                    commResponseVO.setResponseHashInfo(utr);
                    commResponseVO.setCurrency(orderCurrency);
                }

                else if (txStatus.equalsIgnoreCase("FAILED")||txStatus.equalsIgnoreCase("USER_DROPPED"))
                {
                    commResponseVO.setStatus("Failed");
                    commResponseVO.setDescription("Failed");
                    commResponseVO.setRemark(txStatus);
                    commResponseVO.setTransactionStatus("Failed");
                    commResponseVO.setAmount(orderAmount);
                    commResponseVO.setTransactionId(referenceId);
                    commResponseVO.setResponseHashInfo(utr);
                    commResponseVO.setCurrency(orderCurrency);

                }

                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("pending");
                    commResponseVO.setRemark(txMsg);
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setAmount(orderAmount);
                    commResponseVO.setTransactionId(referenceId);
                    commResponseVO.setCurrency(orderCurrency);
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
        catch (JSONException e)
        {
            transactionlogger.error("JSONException cashfree inquiry-->", e);
            commResponseVO.setStatus("pending");
            commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            commResponseVO.setTransactionStatus("pending");
        }
        catch (IOException e)
        {
            transactionlogger.error("IOException cashfree inquiry-->",e);
            commResponseVO.setStatus("pending");
            commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            commResponseVO.setTransactionStatus("pending");

        }
        return commResponseVO;
    }
    public GenericResponseVO processQuery(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside processInquiry of CashFree Gateway---");
        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        Functions functions = new Functions();
        String PAY_ID = "";
        String ORDER_ID = trackingId;
        String appId = gatewayAccount.getMerchantId();
        String secretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String orderStatus = "";
        String orderAmount = "";
        String status = "";
        String txStatus = "";
        String utr = "";
        String txMsg = "";
        String referenceId = "";
        String paymentMode = "";
        String orderCurrency = "";
        String orderId = "";
        String paymentDetailsPaymentMode = "";
        String cardNumber = "";
        String cardCountry = "";
        String cardScheme = "";
        String authIdCode = "";
       // List<String> response = null;
        String response = null;
        String inquiry_res = "";
        try
        {
            transactionlogger.error("isTest  --- >" + isTest);
            transactionlogger.error("PAY_ID is --- >" + PAY_ID);
            transactionlogger.error("ORDER_ID is --- >" + ORDER_ID);
            String inquiryReqestData = "";

            String REQUEST_URL = "";

            if (isTest)
            {
                REQUEST_URL =   RB.getString("INQUIRY_TEST_URL")+trackingId;
                transactionlogger.error("inside test req of inquiry is -- >" + REQUEST_URL);

            }
            else
            {
                REQUEST_URL = RB.getString("INQUIRY_LIVE_URL")+trackingId;
                transactionlogger.error("inside live req of inquiry is -- >" + REQUEST_URL);

            }

            response=  CashFreeUtils.doGetHttpUrlConnection(REQUEST_URL,appId,secretKey);

            transactionlogger.error("inquiry response is -->" + response);

            String resString = "";
            /*JSONArray responseArray = new JSONArray(response);
            transactionlogger.error("array length  is -->" + responseArray.length());
            for (int i = 0; i < responseArray.length(); i++)
            {
                resString = responseArray.getString(i);
                transactionlogger.error("resString is -->" + resString);
                JSONObject jsonres=new JSONObject(resString);
                JSONObject paymentDetails=jsonres.getJSONObject("paymentDetails");
                if (jsonres.has("orderStatus"))
                   orderStatus = jsonres.getString("orderStatus");
                if (jsonres.has("orderAmount"))
                   orderAmount = jsonres.getString("orderAmount");
                if (jsonres.has("txStatus"))
                   txStatus = jsonres.getString("txStatus");
                if (jsonres.has("referenceId"))
                   referenceId = jsonres.getString("referenceId");
                if (paymentDetails.has("utr"))
                   utr = paymentDetails.getString("utr");

            }*/
            //transactionlogger.error("inquiry res is -- >" + inquiry_res);

            if(functions.isValueNull(response) && functions.isJSONValid(response)){

                JSONObject responseJSON = new JSONObject(response);
                String order_amount     = "";
                String order_currency   = "";
                String order_status     = "";
                String cf_order_id      = "";
                String created_at       = "";

                if(responseJSON.has("order_amount")){
                    order_amount = responseJSON.getString("order_amount");
                }
                if(responseJSON.has("order_currency")){
                    order_currency = responseJSON.getString("order_currency");
                }
                if(responseJSON.has("order_status")){
                    order_status = responseJSON.getString("order_status");
                }
                if(responseJSON.has("cf_order_id")){
                    cf_order_id = responseJSON.getString("cf_order_id");
                }
                if(responseJSON.has("created_at")){
                    created_at = responseJSON.getString("created_at");
                }

                transactionlogger.error("inside if cashfree inquiry response trackingId-->" + trackingId+"response "+inquiry_res);

                if (order_status.equalsIgnoreCase("PAID"))
                {
                    commResponseVO.setStatus("Success");
                    commResponseVO.setTransactionStatus("Success");
                    commResponseVO.setDescription(order_status);
                    commResponseVO.setRemark(order_status);
                    commResponseVO.setAmount(order_amount);
                    commResponseVO.setTransactionId(cf_order_id);
                    commResponseVO.setResponseHashInfo(cf_order_id);
                    commResponseVO.setCurrency(order_currency);
                    commResponseVO.setResponseTime(created_at);
                    commResponseVO.setMerchantId(appId);
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                }
                else if (order_status.equalsIgnoreCase("FAILED") || order_status.equalsIgnoreCase("USER_DROPPED"))
                {
                    commResponseVO.setStatus("Failed");
                    commResponseVO.setTransactionStatus("Failed");
                    commResponseVO.setDescription(order_status);
                    commResponseVO.setRemark(order_status);
                    commResponseVO.setAmount(order_amount);
                    commResponseVO.setTransactionId(cf_order_id);
                    commResponseVO.setResponseHashInfo(cf_order_id);
                    commResponseVO.setCurrency(order_currency);
                    commResponseVO.setResponseTime(created_at);
                    commResponseVO.setMerchantId(appId);
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());

                }
                else if (order_status.equalsIgnoreCase("ACTIVE"))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setDescription(order_status);
                    commResponseVO.setRemark(order_status);
                    commResponseVO.setAmount(order_amount);
                    commResponseVO.setTransactionId(cf_order_id);
                    commResponseVO.setResponseHashInfo(cf_order_id);
                    commResponseVO.setCurrency(order_currency);
                    commResponseVO.setMerchantId(appId);
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
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
            transactionlogger.error("JSONException cashfree inquiry-->", e);
            commResponseVO.setStatus("pending");
            commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            commResponseVO.setTransactionStatus("pending");
        }
        catch (IOException e)
        {
            transactionlogger.error("IOException cashfree inquiry-->",e);
            commResponseVO.setStatus("pending");
            commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            commResponseVO.setTransactionStatus("pending");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return commResponseVO;
    }

}