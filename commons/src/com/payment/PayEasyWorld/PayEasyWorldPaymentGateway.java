package com.payment.PayEasyWorld;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;

import java.util.ResourceBundle;

/**
 * Created by Vivek on 5/22/2021.
 */
public class PayEasyWorldPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "payeasyworld";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.payeasyworld");
    private static TransactionLogger transactionLogger = new TransactionLogger(PayEasyWorldPaymentGateway.class.getName());
    private static Logger log = new Logger(PayEasyWorldPaymentGateway.class.getName());

    public PayEasyWorldPaymentGateway(String accountId){this.accountId=accountId;}
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint(PayEasyWorldPaymentGateway.class.getName(), "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error(":::Inside processSale:::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mid=gatewayAccount.getMerchantId();
        String terNo=gatewayAccount.getFRAUD_FTP_USERNAME();
        String signKey=gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest=gatewayAccount.isTest();
        String saleUrl="";
        if (isTest)
        {
            saleUrl = RB.getString("TEST_SALE_URL");
        }
        else
        {
            saleUrl = RB.getString("LIVE_SALE_URL");
        }
        String firstName=addressDetailsVO.getFirstname();
        String lastName=addressDetailsVO.getLastname();
        String amount=transDetailsVO.getAmount();
        String currency=transDetailsVO.getCurrency();
        String street="";
        String city="";
        String country="";
        String state="";
        String phone="";
        if(functions.isValueNull(addressDetailsVO.getStreet()))
            street = addressDetailsVO.getStreet();
        if(functions.isValueNull(addressDetailsVO.getCity()))
            city = addressDetailsVO.getCity();
        if(functions.isValueNull(addressDetailsVO.getCountry()))
            country = addressDetailsVO.getCountry();
        if(functions.isValueNull(addressDetailsVO.getState()))
            state = addressDetailsVO.getState();
        if(functions.isValueNull(addressDetailsVO.getPhone()))
            phone=addressDetailsVO.getPhone();
        String termUrl="";
        String descriptor="";
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL")+trackingID;
            transactionLogger.error("From HOST_URL----" + termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL")+trackingID;
            transactionLogger.error("From RB TERM_URL ----" + termUrl);
        }
        String notifyUrl=RB.getString("NOTIFY_URL")+trackingID;
        try
        {
            firstName= ESAPI.encoder().encodeForURL(firstName);
            lastName= ESAPI.encoder().encodeForURL(lastName);
            street= ESAPI.encoder().encodeForURL(street);
            city= ESAPI.encoder().encodeForURL(city);
            state= ESAPI.encoder().encodeForURL(state);
            country= ESAPI.encoder().encodeForURL(country);
            StringBuffer request = new StringBuffer("");
            request.append("orderNo=" + trackingID +
                            "&merNo=" + mid +
                            "&terNo=" + terNo +
                            "&orderAmount=" + amount +
                            "&orderCurrency=" + currency +
                            "&signInfo=" + PayEasyWorldUtils.SHA256forSales(mid.trim()+ terNo.trim()+ trackingID.trim()+ currency.trim()+amount.trim()+ cardDetailsVO.getCardNum().trim()+ cardDetailsVO.getExpYear().trim()+ cardDetailsVO.getExpMonth().trim()+ cardDetailsVO.getcVV().trim()+ signKey) +
                            "&billFirstName=" + firstName +
                            "&billLastName=" + lastName);
                if(functions.isValueNull(street))
                    request.append("&billAddress=" + street);
                if(functions.isValueNull(city))
                    request.append("&billCity=" + city);
                if(functions.isValueNull(state))
                    request.append("&billState=" + state);
                if(functions.isValueNull(country))
                    request.append("&billCountry=" + country);
                if(functions.isValueNull(addressDetailsVO.getZipCode()))
                    request.append("&billZip=" + addressDetailsVO.getZipCode());
                if(functions.isValueNull(phone))
                    request.append("&billPhone=" + phone );
                if(functions.isValueNull(addressDetailsVO.getEmail()))
                    request.append("&billEmail=" + addressDetailsVO.getEmail() );
            request.append("&cardNo=" + cardDetailsVO.getCardNum() +
                            "&cardSecurityCode=" + cardDetailsVO.getcVV() +
                            "&cardExpireMonth=" + cardDetailsVO.getExpMonth() +
                            "&cardExpireYear=" + cardDetailsVO.getExpYear() +
                            "&website=" + commMerchantVO.getSitename() +
                            "&returnUrl=" + termUrl +
                            "&notifyUrl=" + notifyUrl+
                            "&ip="+addressDetailsVO.getCardHolderIpAddress()
            );
            StringBuffer requestLog = new StringBuffer("");
            requestLog.append("orderNo=" + trackingID +
                            "&merNo=" + mid +
                            "&terNo=" + terNo +
                            "&orderAmount=" + amount +
                            "&orderCurrency=" + currency +
                            "&signInfo=" + PayEasyWorldUtils.SHA256forSales(mid.trim()+ terNo.trim()+ trackingID.trim()+ currency.trim()+amount.trim()+ cardDetailsVO.getCardNum().trim()+ cardDetailsVO.getExpYear().trim()+ cardDetailsVO.getExpMonth().trim()+ cardDetailsVO.getcVV().trim()+ signKey) +
                            "&billFirstName=" + firstName +
                            "&billLastName=" + lastName);
                if(functions.isValueNull(street))
                    requestLog.append("&billAddress=" + street);
                if(functions.isValueNull(city))
                    requestLog.append("&billCity=" + city);
                if(functions.isValueNull(state))
                    requestLog.append("&billState=" + state);
                if(functions.isValueNull(country))
                    requestLog.append("&billCountry=" + country);
                if(functions.isValueNull(addressDetailsVO.getZipCode()))
                    requestLog.append("&billZip=" + addressDetailsVO.getZipCode());
                if(functions.isValueNull(phone))
                    requestLog.append("&billPhone=" + phone );
                if(functions.isValueNull(addressDetailsVO.getEmail()))
                    requestLog.append("&billEmail=" + addressDetailsVO.getEmail() );
            requestLog.append("&cardNo=" + functions.maskingPan(cardDetailsVO.getCardNum()) +
                            "&cardSecurityCode=" + functions.maskingNumber(cardDetailsVO.getcVV()) +
                            "&cardExpireMonth=" + functions.maskingNumber(cardDetailsVO.getExpMonth()) +
                            "&cardExpireYear=" + functions.maskingNumber(cardDetailsVO.getExpYear()) +
                            "&website=" + commMerchantVO.getSitename() +
                            "&returnUrl=" + termUrl +
                            "&notifyUrl=" + notifyUrl+
                            "&ip="+addressDetailsVO.getCardHolderIpAddress()
            );
            transactionLogger.error("Sale request--" + trackingID + "-->" + requestLog.toString());
            String response = PayEasyWorldUtils.doPostHTTPSURLConnection(saleUrl, request.toString());
            transactionLogger.error("Sale response--" + trackingID + "-->" + response);

            if (functions.isValueNull(response))
            {
                JSONObject responseJSON = new JSONObject(response);
                String status = "";
                String orderSucceed="";
                String bankDescriptor="";
                String tradeNo="";
                String orderAmount="";
                String orderResult="";
                String redirectURL="";
                if(responseJSON.has("orderSucceed"))
                    orderSucceed = responseJSON.getString("orderSucceed");
                if(responseJSON.has("orderStatus"))
                    bankDescriptor = responseJSON.getString("orderStatus");
                if(responseJSON.has("tradeNo"))
                    tradeNo = responseJSON.getString("tradeNo");
                if(responseJSON.has("orderAmount"))
                    orderAmount = responseJSON.getString("orderAmount");
                if(responseJSON.has("orderResult"))
                    orderResult = responseJSON.getString("orderResult");
                if(responseJSON.has("redirectURL"))
                    redirectURL = responseJSON.getString("redirectURL");

                if (functions.isValueNull(orderSucceed))
                {
                    if (orderSucceed.equals("1"))
                    {
                        status = "success";
                        if (functions.isValueNull(bankDescriptor) && "Y".equalsIgnoreCase(gatewayAccount.getIsDynamicDescriptor()))
                        {
                            descriptor = bankDescriptor;
                        }else{
                            descriptor=gatewayAccount.getDisplayName();
                        }
                        commResponseVO.setDescriptor(descriptor);
                    }
                    else if (orderSucceed.equals("0"))
                    {
                        status = "fail";
                    }
                    else if (orderSucceed.equals("-1") || orderSucceed.equals("-2"))
                    {
                        status = "Pending";
                        if(functions.isValueNull(redirectURL))
                        {
                            status="pending3DConfirmation";
                            commResponseVO.setUrlFor3DRedirect(redirectURL);
                        }
                    }
                }

                commResponseVO.setTransactionId(tradeNo);
                commResponseVO.setAmount(orderAmount);
                commResponseVO.setTransactionStatus(status);
                commResponseVO.setErrorCode(orderSucceed);
                commResponseVO.setDescription(orderResult);
                commResponseVO.setRemark(orderResult);
                commResponseVO.setStatus(status);
                commResponseVO.setTransactionType("sale");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException--"+trackingID+"-->",e);
        }
        catch (EncodingException e)
        {
            transactionLogger.error("EncodingException--" + trackingID + "-->", e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processQuery(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processInquiry :::");
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        Functions functions=new Functions();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String mid=gatewayAccount.getMerchantId();
        String terNo=gatewayAccount.getFRAUD_FTP_USERNAME();
        String signKey=gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest=gatewayAccount.isTest();
        String transactionId="";
        String transactionStatus="";
        String amount="";
        String currency="";
        String remark="";
        String status="";
        String errorCode="";
        String inquiryUrl="";
        String orderSucceed="";
        String transDate="";
        if(isTest)
        {
            inquiryUrl=RB.getString("TEST_INQUIRY_URL");
        }else{
            inquiryUrl=RB.getString("LIVE_INQUIRY_URL");
        }
        try
        {
            String request = "{\"merNo\":\"" + mid + "\"," +
                    "\"terNo\":\"" + terNo + "\"," +
                    "\"queryType\":\"single\"," +
                    "\"orderNo\":\"" + trackingId + "\"," +
                    "\"signInfo\":\"" + PayEasyWorldUtils.SHA256forSales(mid.trim() + terNo.trim() + "single" + trackingId.trim() + signKey) + "\"}";
            transactionLogger.error("Inquiry Request----" + trackingId + "-->" + request);
            String response = PayEasyWorldUtils.doPostHTTPSURLConnectionClient(inquiryUrl, request.toString());
            transactionLogger.error("Inquiry Response----" + trackingId + "-->" + response);
            if (functions.isValueNull(response))
            {
                JSONObject responseJSON = new JSONObject(response);
                if(responseJSON.has("orderList"))
                {
                    JSONArray responseJSONArray=responseJSON.getJSONArray("orderList");
                    if(responseJSONArray!=null && responseJSONArray.length()==1)
                    {
                        JSONObject jsonObject = responseJSONArray.getJSONObject(0);
                        if (jsonObject.has("orderAmount"))
                            amount = jsonObject.getString("orderAmount");
                        if (jsonObject.has("orderCurrency"))
                            currency = jsonObject.getString("orderCurrency");
                        if (jsonObject.has("orderResult"))
                            remark = jsonObject.getString("orderResult");
                        if (jsonObject.has("orderSucceed"))
                            orderSucceed = jsonObject.getString("orderSucceed");
                        if (jsonObject.has("tradeNo"))
                            transactionId = jsonObject.getString("tradeNo");
                        if (jsonObject.has("transDate"))
                            transDate = jsonObject.getString("transDate");
                        if(functions.isValueNull(amount))
                        {
                            amount=String.format("%.2f",Double.parseDouble(amount));
                        }

                    }

                }
                if (functions.isValueNull(orderSucceed))
                {
                    status = "success";
                    if (orderSucceed.equals("0")) //Transaction Declined.
                    {
                        transactionStatus = "failed";
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }
                    else if (orderSucceed.equals("1")) //Transaction Approved.
                    {
                        transactionStatus = "success";
                    }else if (orderSucceed.equals("-1")) //Transaction Approved.
                    {
                        transactionStatus = "pending";
                        remark = "Transaction Pending";
                    }
                    else if (orderSucceed.equals("2"))  //Transaction not found.
                    {
                        transactionStatus = "pending";
                        remark = "Transaction Not Found";
                    }
                }
                else
                {
                    status = "pending";
                    transactionStatus = "pending";
                    remark = "Internal error while processing your request.";
                }

            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException----"+trackingId+"-->",e);
        }
        commResponseVO.setMerchantId(mid);
        commResponseVO.setMerchantOrderId(trackingId);
        commResponseVO.setTransactionId(transactionId);
        commResponseVO.setTransactionType("Sale");
        commResponseVO.setTransactionStatus(transactionStatus);
        commResponseVO.setStatus(status);
        commResponseVO.setAmount(amount);
        commResponseVO.setCurrency(currency);
        commResponseVO.setBankTransactionDate(transDate);
        commResponseVO.setErrorCode(errorCode);
        commResponseVO.setDescription(remark);
        commResponseVO.setRemark(remark);
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
        String mid=gatewayAccount.getMerchantId();
        String terNo=gatewayAccount.getFRAUD_FTP_USERNAME();
        String signKey=gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest=gatewayAccount.isTest();
        String refundUrl="";
        String responseCode="";
        String tradeNo="";
        String message="";
        String refundReason=commTransactionDetailsVO.getOrderDesc();
        if(!functions.isValueNull(refundReason))
        {
            refundReason="Refund";
        }
        if(isTest)
        {
            refundUrl=RB.getString("TEST_REFUND_URL");
        }else{
            refundUrl=RB.getString("LIVE_REFUND_URL");
        }
        try{
            String signInfo=PayEasyWorldUtils.SHA256forSales(mid.trim()+terNo.trim()+commTransactionDetailsVO.getPreviousTransactionId().trim()+commTransactionDetailsVO.getAmount().trim()+commTransactionDetailsVO.getCurrency().trim()+signKey.trim());
            String request="{\"merNo\":\""+mid+"\"," +
                    "\"terNo\":\""+terNo+"\"," +
                    "\"tradeNo\":\""+commTransactionDetailsVO.getPreviousTransactionId()+"\"," +
                    "\"refundAmount\":\""+commTransactionDetailsVO.getAmount()+"\"," +
                    "\"refundCurrency\":\""+commTransactionDetailsVO.getCurrency()+"\"," +
                    "\"refundReason\":\""+refundReason+"\"," +
                    "\"signInfo\":\""+signInfo+"\"" +
                    "}";
            transactionLogger.error("Refund request--"+trackingID+"--->"+request);
            String response=PayEasyWorldUtils.doPostHTTPSURLConnectionClient(refundUrl,request);
            transactionLogger.error("Refund response--"+trackingID+"--->"+response);
            if(functions.isValueNull(response))
            {
                JSONObject responseJSON=new JSONObject(response);
                if(responseJSON.has("respCode"))
                    responseCode=responseJSON.getString("respCode");
                if(responseJSON.has("tradeNo"))
                    tradeNo=responseJSON.getString("tradeNo");
                if("0000".equalsIgnoreCase(responseCode))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else{
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                }
                commResponseVO.setTransactionId(tradeNo);
                message=PayEasyWorldUtils.getErrorMessage(responseCode);
                commResponseVO.setRemark(message);
                commResponseVO.setDescription(message);
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
