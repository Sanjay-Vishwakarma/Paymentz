package com.payment.octapay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;
import java.util.Iterator;
import java.util.ResourceBundle;
/**
 * Created by Admin on 12/4/2020.
 */
public class OctapayPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE ="octapay";
    private TransactionLogger transactionLogger = new TransactionLogger(OctapayPaymentGateway.class.getName());
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.octapay");

    public OctapayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }
    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Inside OctapayPaymentGateway processSale");
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Functions functions = new Functions();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String merId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String api_key = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String first_name ="";
        String last_name ="";
        String address = "";
        String country = "";
        String state = "";
        String city = "";
        String zip = "";
        String ip_address = addressDetailsVO.getCardHolderIpAddress();
        String birth_date = addressDetailsVO.getBirthdate(); //"06/12/1990";
        String email = addressDetailsVO.getEmail();
        String phone_no = addressDetailsVO.getPhone();
        String card_type = GatewayAccountService.getCardType(transDetailsVO.getCardType());
        if("VISA".equalsIgnoreCase(card_type))
        {
            card_type="2";
        }
        else if("AMEX".equalsIgnoreCase(card_type))
        {
            card_type="1";
        }
        else if("MC".equalsIgnoreCase(card_type))
        {
            card_type="3";
        }
        else if("DISC".equalsIgnoreCase(card_type))
        {
            card_type="4";
        }
        else
        {
            card_type="";
        }
        transactionLogger.error("card_type----------------->"+card_type);

        String amount = transDetailsVO.getAmount();
        String currency = transDetailsVO.getCurrency();
        String card_no = cardDetailsVO.getCardNum();
        String ccExpiryMonth = cardDetailsVO.getExpMonth();
        String ccExpiryYear = cardDetailsVO.getExpYear();
        String cvvNumber = cardDetailsVO.getcVV();
        String response_url = "";
        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        String webhook_url = RB.getString("NOTIFY_URL")+trackingID;
        String termUrl = "";
        String saleURL = "";
        String response="";

        boolean isTest = gatewayAccount.isTest();
        try
        {
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getFirstname())))
            {
                first_name = ESAPI.encoder().encodeForURL(addressDetailsVO.getFirstname());
            }
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getLastname())))
            {
                last_name = ESAPI.encoder().encodeForURL(addressDetailsVO.getLastname());
            }
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getStreet())))
            {
                address = ESAPI.encoder().encodeForURL(addressDetailsVO.getStreet());
            }
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getCountry())))
            {
                country = ESAPI.encoder().encodeForURL(addressDetailsVO.getCountry());
            }
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getState())))
            {
                state = ESAPI.encoder().encodeForURL(addressDetailsVO.getState());
            }
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getCity())))
            {
                city = ESAPI.encoder().encodeForURL(addressDetailsVO.getCity());
            }
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getZipCode())))
            {
                zip = ESAPI.encoder().encodeForURL(addressDetailsVO.getZipCode());
            }

            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL") + trackingID;
                transactionLogger.error("From HOST_URL----" + termUrl);
            }
            else
            {
            termUrl = RB.getString("TERM_URL") + trackingID;
            transactionLogger.error("From RB TERM_URL ----" + termUrl);
            }


                if (isTest)
                {
                    saleURL = RB.getString("TEST_URL");
                }
                else
                {
                    saleURL = RB.getString("LIVE_URL");
                }
                StringBuffer request = new StringBuffer();
                request.append("api_key=" + api_key + "&first_name=" + first_name + "&last_name=" + last_name + "&address=" + address + "&customer_order_id=" + trackingID + "&country=" + country + "&state=" + state +
                        "&city=" + city + "&zip=" + zip + "&ip_address=" + ip_address + "&birth_date=" + birth_date + "&email=" + email + "&phone_no=" + phone_no + "&card_type=" + card_type + "&amount=" + amount +
                        "&currency=" + currency + "&card_no=" + card_no + "&ccExpiryMonth=" + ccExpiryMonth + "&ccExpiryYear=" + ccExpiryYear + "&cvvNumber=" + cvvNumber + "&response_url=" + termUrl +
                        "&webhook_url=" + webhook_url);

                StringBuffer requestlog = new StringBuffer();
                requestlog.append("api_key=" + api_key + "&first_name=" + first_name + "&last_name=" + last_name + "&address=" + address + "&customer_order_id=" + trackingID + "&country=" + country + "&state=" + state +
                        "&city=" + city + "&zip=" + zip + "&ip_address=" + ip_address + "&birth_date=" + birth_date + "&email=" + email + "&phone_no=" + phone_no + "&card_type=" + card_type + "&amount=" + amount +
                        "&currency=" + currency + "&card_no=" + functions.maskingPan(card_no) + "&ccExpiryMonth=" + functions.maskingNumber(ccExpiryMonth) + "&ccExpiryYear=" + functions.maskingNumber(ccExpiryYear) + "&cvvNumber=" + functions.maskingNumber(cvvNumber) + "&response_url=" + termUrl +
                        "&webhook_url=" + webhook_url);
                transactionLogger.error("Octapay Sale request---->" + trackingID + "---" + requestlog);
                response = OctapayUtils.doHttpPostConnection(saleURL, request.toString());
                transactionLogger.error("Octapay Sale response---->" + trackingID + "---" + response);




            if (functions.isValueNull(response))
            {
                JSONObject responseJSON = new JSONObject(response);
                String status = "";
                String message = "";
                String order_id = "";
                String reason = "";
                String transaction_status = "";
                String redirect_3ds_url = "";
                String transaction_date = "";
                String error="";
                if (responseJSON != null)
                {
                    if (responseJSON.has("status"))
                    {
                        status = responseJSON.getString("status");
                    }
                    if (responseJSON.has("reason"))
                    {
                        reason = responseJSON.getString("reason");
                    }
                    if (responseJSON.has("message"))
                    {
                        message = responseJSON.getString("message");
                    }
                    if (responseJSON.has("order_id"))
                    {
                        order_id = responseJSON.getString("order_id");
                    }
                    if (responseJSON.has("redirect_3ds_url"))
                    {
                        redirect_3ds_url = responseJSON.getString("redirect_3ds_url");
                    }
                    if(responseJSON.has("errors")&&!responseJSON.isNull("errors"))
                    {
                        JSONObject errorsjson=responseJSON.getJSONObject("errors");
                        Iterator key=errorsjson.keys();
                        error="Invalid Parameter ";
                        while(key.hasNext())
                        {
                            error+=key.next();
                        }

                    }

                    transactionLogger.error("Status---->"+status);
                    if ("success".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription(message);
                        commResponseVO.setTransactionId(order_id);
                        commResponseVO.setResponseTime(transaction_date);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }
                    else if ("3d_redirect".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setUrlFor3DRedirect(redirect_3ds_url);
                        commResponseVO.setTransactionId(order_id);
                        commResponseVO.setDescription(reason);
                        commResponseVO.setRemark(reason);
                        commResponseVO.setResponseTime(transaction_date);
                        commResponseVO.setMethod("GET");

                    }
                    else if ("fail".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionId(order_id);
                        commResponseVO.setResponseTime(transaction_date);

                        if(functions.isValueNull(error))
                        {
                            commResponseVO.setRemark(error);
                            commResponseVO.setDescription(error);

                        }
                        else
                        {
                           commResponseVO.setRemark(message);
                           commResponseVO.setDescription(message);
                        }
                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription(message);
                    }
                }
            }
        }

        catch (JSONException e)
        {
            transactionLogger.error("JSONException---->" + trackingID + "---", e);

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception---->" + trackingID + "---", e);
        }

        return commResponseVO;

    }
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO)
    {
        transactionLogger.error("OctapayPaymentGateway :: Inside processRefund");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Functions functions = new Functions();
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String refundUrl = "";
        String api_key=GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String customer_order_id=trackingID;
        String refund_reason=transactionDetailsVO.getOrderDesc();

        boolean isTest = gatewayAccount.isTest();
        try
        {
            if (isTest)
            {
                refundUrl = RB.getString("REFUND_URL");
            }
            else
            {
                refundUrl = RB.getString("REFUND_URL");
            }
            StringBuffer request = new StringBuffer();
            request.append("api_key=" + api_key + "&refund_reason=" + refund_reason + "&customer_order_id=" + customer_order_id);
            transactionLogger.error("Octapay Refund Request-->" + request);

            String response = OctapayUtils.doHttpPostConnection(refundUrl, request.toString());
            transactionLogger.error("Octapay Refund Response-->" + response);

            if (functions.isValueNull(response))
            {
                JSONObject responseJSON = new JSONObject(response);
                String status = "";
                String message = "";
                String transaction_status="";
                if(responseJSON!=null)
                {
                    if (responseJSON.has("status"))
                    {
                        status = responseJSON.getString("status");
                    }
                    if (responseJSON.has("message"))
                    {
                        message = responseJSON.getString("message");
                    }

                    if ("success".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription(message);

                    }
                    else if ("fail".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription(message);
                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription(message);
                    }
                }
            }
        }

        catch (JSONException e)
        {
            transactionLogger.error("JSONException---->" + trackingID + "---", e);
        }
        return commResponseVO;
    }


    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("OctapayPaymentGateway :: Inside processQuery ");
        Functions functions = new Functions();
        CommResponseVO commResponseVO = new CommResponseVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String inquiryUrl = "";
        String order_id="";
        String api_key=GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String merId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String customer_order_id=trackingID; //Atleast one parameter from the above two parameters is required.

        boolean isTest = gatewayAccount.isTest();

        try
        {
            if (isTest)
            {
                inquiryUrl = RB.getString("INQUIRY_URL");
            }
            else
            {
                inquiryUrl = RB.getString("INQUIRY_URL");
            }
            StringBuffer request = new StringBuffer();
            request.append("api_key="+api_key+"&order_id="+order_id+"&customer_order_id="+customer_order_id);
            transactionLogger.error("Octapay inquiry  Request-->" + trackingID+"-----"+request);

            String response = OctapayUtils.doHttpPostConnection(inquiryUrl, request.toString());

            transactionLogger.error("Octapay inquiry Response---->" + trackingID + "---" + response);
            if (functions.isValueNull(response))
            {
                    JSONObject responseJSON = new JSONObject(response);
                    String status = "";
                    String message = "";
                    String transaction_status = "";
                    String currency="";
                    String amount="";
                    String transaction_date="";
                    String test="";
                    String reason="";
                if(responseJSON!=null)
                {

                    if(responseJSON.has("transaction"))
                    {
                        JSONObject transaction = responseJSON.getJSONObject("transaction");

                        if(transaction.has("order_id"))
                        {
                            order_id=transaction.getString("order_id");
                        }
                        if(transaction.has("transaction_status"))
                        {
                            transaction_status=transaction.getString("transaction_status");
                        }
                        if(transaction.has("currency"))
                        {
                            currency=transaction.getString("currency");
                        }
                        if(transaction.has("amount"))
                        {
                            amount=transaction.getString("amount");
                        }
                        if(transaction.has("transaction_date"))
                        {
                            transaction_date=transaction.getString("transaction_date");
                        }
                        if(transaction.has("test"))
                        {
                            test=transaction.getString("test");
                        }
                        if(transaction.has("reason"))
                        {
                            reason=transaction.getString("reason");
                        }else
                        {
                            reason=transaction_status;
                        }

                    }

                    if ("success".equalsIgnoreCase(transaction_status))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark(reason);
                        commResponseVO.setDescription(reason);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setAmount(amount);
                        commResponseVO.setTransactionId(order_id);
                        commResponseVO.setBankTransactionDate(transaction_date);
                        commResponseVO.setTransactionStatus(transaction_status);
                        commResponseVO.setMerchantId(merId);

                    }
                    else if ("3d_redirect".equalsIgnoreCase(transaction_status))
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setTransactionId(order_id);
                        commResponseVO.setDescription(reason);
                        commResponseVO.setRemark(reason);
                        commResponseVO.setResponseTime(transaction_date);

                    }
                    else if ("fail".equalsIgnoreCase(transaction_status))
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark(reason);
                        commResponseVO.setDescription(reason);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setAmount(amount);
                        commResponseVO.setTransactionId(order_id);
                        commResponseVO.setBankTransactionDate(transaction_date);
                        commResponseVO.setTransactionStatus(transaction_status);
                        commResponseVO.setMerchantId(merId);
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setRemark(reason);
                        commResponseVO.setDescription(reason);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setAmount(amount);
                        commResponseVO.setTransactionId(order_id);
                        commResponseVO.setBankTransactionDate(transaction_date);
                        commResponseVO.setTransactionStatus(transaction_status);
                        commResponseVO.setMerchantId(merId);
                    }

                }
            }
        }


        catch (JSONException e)
        {
            transactionLogger.error("JSONException---->" + trackingID + "---", e);
        }
        return commResponseVO;
    }
   /* public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {

        transactionLogger.error(":::::Entered into processAutoRedirect for Octapay:::::");
        String form = "";

        Comm3DResponseVO comm3DResponseVO = null;
        OctapayPaymentProcess octapayPaymentProcess = new OctapayPaymentProcess();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommRequestVO commRequestVO = null;
        commRequestVO = OctapayUtils.getOctapayRequestVO(commonValidatorVO);
        Functions functions = new Functions();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);


        String merId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String api_key = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String first_name = "";
        String last_name = "";
        String address = "";
        String country = "";
        String state = "";
        String city = "";
        String zip = "";
        String ip_address = addressDetailsVO.getCardHolderIpAddress();
        String birth_date = addressDetailsVO.getBirthdate(); //"06/12/1990";
        String email = addressDetailsVO.getEmail();
        String phone_no = addressDetailsVO.getPhone();
        String trackingID = commonValidatorVO.getTrackingid();

        String amount = transDetailsVO.getAmount();
        String currency = transDetailsVO.getCurrency();
        String card_no = cardDetailsVO.getCardNum();
        String ccExpiryMonth = cardDetailsVO.getExpMonth();
        String ccExpiryYear = cardDetailsVO.getExpYear();
        String cvvNumber = cardDetailsVO.getcVV();
        String response_url = "";
        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        String webhook_url = RB.getString("NOTIFY_URL") + trackingID;
        String termUrl = "";
        String saleURL = "";
        String response = "";

     *//*   if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL") + trackingID;
            transactionLogger.error("From HOST_URL----" + termUrl);
        }
        else
        {*//*
            termUrl = RB.getString("TERM_URL") + trackingID;
            transactionLogger.error("From RB TERM_URL ----" + termUrl);
       // }

        boolean isTest = gatewayAccount.isTest();
        try
        {
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getFirstname())))
            {
                first_name = ESAPI.encoder().encodeForURL(addressDetailsVO.getFirstname());
            }
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getLastname())))
            {
                last_name = ESAPI.encoder().encodeForURL(addressDetailsVO.getLastname());
            }
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getStreet())))
            {
                address = ESAPI.encoder().encodeForURL(addressDetailsVO.getStreet());
            }
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getCountry())))
            {
                country = ESAPI.encoder().encodeForURL(addressDetailsVO.getCountry());
            }
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getState())))
            {
                state = ESAPI.encoder().encodeForURL(addressDetailsVO.getState());
            }
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getCity())))
            {
                city = ESAPI.encoder().encodeForURL(addressDetailsVO.getCity());
            }
            if (functions.isValueNull(ESAPI.encoder().encodeForURL(addressDetailsVO.getZipCode())))
            {
                zip = ESAPI.encoder().encodeForURL(addressDetailsVO.getZipCode());
            }

            if (isTest)
            {
                saleURL = RB.getString("HOSTED_PAGE_API_URL");
            }
            else
            {
                saleURL = RB.getString("HOSTED_PAGE_API_URL");
            }
            StringBuffer request = new StringBuffer();
            request.append("api_key=" + api_key + "&first_name=" + first_name + "&last_name=" + last_name + "&address=" + address + "&customer_order_id=" + trackingID + "&country=" + country + "&state=" + state +
                    "&city=" + city + "&zip=" + zip + "&ip_address=" + ip_address + "&birth_date=" + birth_date + "&email=" + email + "&phone_no=" + phone_no + "&amount=" + amount +
                    "&currency=" + currency + "&response_url=" + termUrl +
                    "&webhook_url=" + webhook_url);

            StringBuffer requestlog = new StringBuffer();
            requestlog.append("api_key=" + api_key + "&first_name=" + first_name + "&last_name=" + last_name + "&address=" + address + "&customer_order_id=" + trackingID + "&country=" + country + "&state=" + state +
                    "&city=" + city + "&zip=" + zip + "&ip_address=" + ip_address + "&birth_date=" + birth_date + "&email=" + email + "&phone_no=" + phone_no + "&amount=" + amount +
                    "&currency=" + currency + "&response_url=" + termUrl +
                    "&webhook_url=" + webhook_url);
            transactionLogger.error("::::HOSTED_PAGE_API Request-Response::::");
            transactionLogger.error("Octapay Sale request---->" + trackingID + "---" + requestlog);
            response = OctapayUtils.doHttpPostConnection(saleURL, request.toString());
            transactionLogger.error("Octapay Sale response---->" + trackingID + "---" + response);

            if (functions.isValueNull(response))
            {
                JSONObject responseJSON = new JSONObject(response);
                String status = "";
                String message = "";
                String order_id = "";
                String reason = "";
                String transaction_status = "";
                String redirect_3ds_url = "";
                String transaction_date = "";
                String error="";
                if (responseJSON != null)
                {
                    if (responseJSON.has("status"))
                    {
                        status = responseJSON.getString("status");
                    }
                    if (responseJSON.has("reason"))
                    {
                        reason = responseJSON.getString("reason");
                    }
                    if (responseJSON.has("message"))
                    {
                        message = responseJSON.getString("message");
                    }
                    if (responseJSON.has("order_id"))
                    {
                        order_id = responseJSON.getString("order_id");
                    }
                    if (responseJSON.has("redirect_3ds_url"))
                    {
                        redirect_3ds_url = responseJSON.getString("redirect_3ds_url");
                    }
                    if(responseJSON.has("errors")&&!responseJSON.isNull("errors"))
                    {
                        JSONObject errorsjson=responseJSON.getJSONObject("errors");
                        Iterator key=errorsjson.keys();
                        error="Invalid Parameter ";
                        while(key.hasNext())
                        {
                            error+=key.next();
                        }

                    }

                    transactionLogger.error("Status---->"+status);
                    if ("success".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription(message);
                        commResponseVO.setTransactionId(order_id);
                        commResponseVO.setResponseTime(transaction_date);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }
                    else if ("3d_redirect".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setUrlFor3DRedirect(redirect_3ds_url);
                        commResponseVO.setTransactionId(order_id);
                        commResponseVO.setDescription(reason);
                        commResponseVO.setRemark(reason);
                        commResponseVO.setResponseTime(transaction_date);
                        commResponseVO.setMethod("GET");

                    }
                    else if ("fail".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionId(order_id);
                        commResponseVO.setResponseTime(transaction_date);

                        if(functions.isValueNull(error))
                        {
                            commResponseVO.setRemark(error);
                            commResponseVO.setDescription(error);

                        }
                        else
                        {
                            commResponseVO.setRemark(message);
                            commResponseVO.setDescription(message);
                        }
                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription(message);
                    }
                }
            }


            if ("pending".equalsIgnoreCase(commResponseVO.getStatus()) || "pending3DConfirmation".equalsIgnoreCase(commResponseVO.getStatus()))
            {
                form = octapayPaymentProcess.get3DConfirmationForm(commonValidatorVO.getTrackingid(),"", commResponseVO);
                transactionLogger.debug("form---->" + form);
            }

        }
        catch (EncodingException e)
        {

            transactionLogger.error("EncodingException---->" +trackingID +"-------",e);
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---->" +trackingID +"-------",e);

        }
        return form;

        }
*/
   public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
   {
       ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
       ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
       errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
       PZGenericConstraint genConstraint = new PZGenericConstraint("OctapayPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
       throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
   }

    public String getMaxWaitDays()
    {
        return null;
    }
}
