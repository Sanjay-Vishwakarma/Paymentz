package com.payment.OneRoadPayments;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.Enum.PZProcessType;
import com.payment.billdesk.BillDeskUtils;
import com.payment.billdesk.TestHMac;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.tojika.TojikaUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Jeet Gupta on 01-04-2019.
 */
public class OneRoadPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "OneRoad";
    final static TransactionLogger transactionlogeer = new TransactionLogger(OneRoadPaymentGateway.class.getName());
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.OneRoadPayment");

    public OneRoadPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogeer.error("Inside Process Sale------");
        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String merchantID = "";
        String clientID = "";
        String orderID = "";
        String amount = "";
        String currency = "";
        String cardType = "";
        String key ="";
        String calsign="";
        String signature="";
        boolean isTest=gatewayAccount.isTest();

        merchantID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        clientID = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        orderID  = trackingID;
        amount = transactionDetailsVO.getAmount();
        cardType =GatewayAccountService.getCardType(transactionDetailsVO.getCardType()); //transactionDetailsVO.getCardType()
        currency =transactionDetailsVO.getCurrency();
        key = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();//;//1434240847//Test.getMd5("1434240847").toString()
        key = OneRoadUtils.getMd5(key.trim()).toString();//generate md5 using input key..
        calsign = clientID+orderID+key+amount+currency;
        String signatureVersion = "1.0";
        String termUrl = "";
        transactionlogeer.debug("---host url----" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            transactionlogeer.error("---from HOST_URL is --" + termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            transactionlogeer.error("---from TERM_URL is ---" + termUrl);
        }
        calsign = calsign+termUrl+trackingID;
        signature = OneRoadUtils.getMd5(calsign).toString();
        String url = "";
        if (isTest)
        {
            url = RB.getString("TEST_SALE_URL");

        }
        else
        {
            url = RB.getString("LIVE_SALE_URL");
        }

        try
        {
            Map<String, String> fields = new HashMap<String, String>();
            fields.put("merchantID", merchantID.trim());
            fields.put("clientID", clientID);
            fields.put("orderID", trackingID);
            fields.put("amount", amount);
            fields.put("cardType", cardType);
            fields.put("currency", currency);
            fields.put("key", key); //md5hash generate of key.
            fields.put("signature", signature);
            fields.put("signatureVersion",signatureVersion);
            fields.put("replyURL",termUrl+trackingID);

            if (fields != null)
            {
                for (Map.Entry<String, String> entry : fields.entrySet())
                {
                    String Key = entry.getKey();
                    String value = entry.getValue();
                    transactionlogeer.error("key line :::::" + Key);
                    transactionlogeer.error("value line :::::" + value);
                }
            }
            commResponseVO.setStatus("pending");
            commResponseVO.setRequestMap(fields);
            commResponseVO.setRedirectUrl(url);
        }
        catch (Exception e)
        {
            transactionlogeer.error("Exception-----", e);
        }

        return commResponseVO;
    }


    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogeer.error("processAutoRedirect in OneRoadPaymentGateway ----");
        String html="";
        OneRoadUtils oneRoadUtils=new OneRoadUtils();
        CommRequestVO commRequestVO = null;
        Comm3DResponseVO transRespDetails = null;
        commRequestVO = oneRoadUtils.getOneRoadUtils(commonValidatorVO);

        try
        {
            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
            {
                html = oneRoadUtils.getRedirectForm(commonValidatorVO.getTrackingid(),transRespDetails);
                transactionlogeer.error("Html in processAutoRedirect -------" + html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogeer.error("PZGenericConstraintViolationException in OneRoadPaymentGateway ---",e);
        }

        return html;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogeer.error("Inside processInquiry of OneRoadPaymentGateway---");
        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        OneRoadUtils oneRoadUtils = new OneRoadUtils();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        Functions functions =  new Functions();
        try
        {
            String merchantID="";
            String orderID="";
            String inquiryResp = "";
            String inquiryReq="";

            merchantID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
            orderID = commTransactionDetailsVO.getOrderId();

            inquiryReq="merchantID ="+merchantID+
                    "&orderID ="+orderID;
            transactionlogeer.error("inquiryReq is-----------" + inquiryReq);

            if (isTest)
            {
                transactionlogeer.error("inside test_sale_url -------" + RB.getString("INQUIRY_TEST_URL"));
                inquiryResp = OneRoadUtils.doGetHTTPSURLConnectionClient(RB.getString("INQUIRY_TEST_URL")+orderID);
            }
            else
            {
                transactionlogeer.error("inside live_sale_url -------" + RB.getString("INQUIRY_LIVE_URL"));
                inquiryResp = OneRoadUtils.doGetHTTPSURLConnectionClient(RB.getString("INQUIRY_LIVE_URL")+orderID);
            }
            transactionlogeer.error("Inquiry response---" + inquiryResp);

            try
            {
                if (functions.isValueNull(inquiryResp) && inquiryResp.contains("{"))
                {
                    String response="";
                    String  statusCode="";
                    String message="";
                    String reply="";
                    String amount="";
                    String balance="";
                    String clientID="";
                    String currency="";
                    String date="";
                    String merchant="";
                    merchantID="";
                    orderID="";
                    String settlement="";
                    String signature="";
                    String success="";
                    String url="";


                    JSONObject jsonObject = new JSONObject(inquiryResp);
                    if (jsonObject != null)
                    {
                        JSONObject jsonObject1 =jsonObject.getJSONObject("response");
                        if (jsonObject1.has("statusCode"))
                        {
                            statusCode=jsonObject1.getString("statusCode");
                            transactionlogeer.error("statuscode is jsonobj ======"+statusCode);
                        }
                        if (jsonObject1.has("message"))
                        {
                            message = jsonObject1.getString("message");
                            transactionlogeer.error("message is -----"+message);
                        }

                        JSONObject jsonObject2 = jsonObject.getJSONObject("reply");
                        if (jsonObject2.has("amount"))
                        {
                            amount = jsonObject2.getString("amount");
                            transactionlogeer.error("amount jsonresponse1 is -----" + amount);
                        }
                        if (jsonObject2.has("balance"))
                        {
                            balance = jsonObject2.getString("balance");
                        }
                        if (jsonObject2.has("clientID"))
                        {
                            clientID = jsonObject2.getString("clientID");
                        }
                        if (jsonObject2.has("currency"))
                        {
                            currency = jsonObject2.getString("currency");
                        }
                        if (jsonObject2.has("date"))
                        {
                            date = jsonObject2.getString("date");
                        }
                        if (jsonObject2.has("merchant"))
                        {
                            merchant = jsonObject2.getString("merchant");
                        }
                        if (jsonObject2.has("merchantID"))
                        {
                            merchantID = jsonObject2.getString("merchantID");
                        }
                        if (jsonObject2.has("orderID"))
                        {
                            orderID = jsonObject2.getString("orderID");
                        }
                        if (jsonObject2.has("settlement"))
                        {
                            settlement = jsonObject2.getString("settlement");
                        }
                        if (jsonObject.has("signature"))
                        {
                            signature = jsonObject2.getString("signature");
                        }
                        if (jsonObject2.has("success"))
                        {
                            success = jsonObject2.getString("success");
                            transactionlogeer.debug("success is------" + success);
                        }
                        if (jsonObject2.has("url"))
                        {
                            url = jsonObject2.getString("url");
                        }

                        if (statusCode.equals("200")&& message.equalsIgnoreCase("OK"))
                        {
                            commResponseVO.setStatus("Success");
                            commResponseVO.setDescription(message);
                            commResponseVO.setTransactionStatus("Success");
                        }
                        else
                        {
                            commResponseVO.setStatus("Failed");
                            commResponseVO.setDescription("Failed");
                            commResponseVO.setTransactionStatus("Failed");
                        }
                    }
                    commResponseVO.setAmount(amount);
                    commResponseVO.setMerchantId(merchantID);
                    commResponseVO.setTransactionId(orderID);
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setAuthCode(statusCode);
                    commResponseVO.setBankTransactionDate(date);
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                }
                else
                {
                    commResponseVO.setStatus("Failed");
                    commResponseVO.setDescription("Inquiry response not found");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("Inquiry response not found"); // set inquire response not found
                }
            }
            catch (JSONException e)
            {
                transactionlogeer.error("JSONException ::::::::: ",e);
            }
        }
        catch (IllegalFormatException e)
        {
            transactionlogeer.error("IllegalFormatException ::::::::: ", e);
        }

        return commResponseVO;
    }

    @Override
    public String getMaxWaitDays() {   return null;   }
}
