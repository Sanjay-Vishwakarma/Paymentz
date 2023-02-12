package com.payment.PBSAmex;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.payforasia.core.PayforasiaUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Jitendra on 09-Apr-19.
 */
public class PBSPaymentGatewayAmex  extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "PBSAmex";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.PBSAmex");
    private static TransactionLogger transactionLogger = new TransactionLogger(PBSPaymentGatewayAmex.class.getName());
    private static Logger log = new Logger(PBSPaymentGatewayAmex.class.getName());

    public PBSPaymentGatewayAmex(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processSale ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO=new CommResponseVO();
        Functions functions = new Functions();

        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String paymentType = GatewayAccountService.getPaymentTypes(transDetailsVO.getPaymentType());
        boolean isTest = gatewayAccount.isTest();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        String termUrl = "";
        transactionLogger.error("host url----" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            transactionLogger.error("From HOST_URL----" + termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("From TERM_URL----" + termUrl);
        }
        String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(transDetailsVO.getCurrency())) {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount())) {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency())) {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }

        String ip="";
        if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress()))
        {
            ip=addressDetailsVO.getCardHolderIpAddress();
        }else
        {
            ip=addressDetailsVO.getIp();
        }


        String signKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String w_mer_no = gatewayAccount.getMerchantId();;
        String w_gateway_no = gatewayAccount.getFRAUD_FTP_USERNAME();
        String w_order_no = trackingID;
        String w_order_currency = transDetailsVO.getCurrency();
        String w_order_amount = transDetailsVO.getAmount();
        String w_return_url =termUrl+trackingID;
        String w_payment_method = paymentType;
        String w_card_no = cardDetailsVO.getCardNum();
        String w_card_expire_month = cardDetailsVO.getExpMonth();
        String w_card_expire_year = cardDetailsVO.getExpYear();
        String w_card_security_code = cardDetailsVO.getcVV();
        String w_issuing_bank = "issuing_bank";
        String w_first_name = addressDetailsVO.getFirstname();
        String w_last_name =addressDetailsVO.getLastname();
        String w_email = addressDetailsVO.getEmail();
        String w_ip =ip;
        String w_phone = addressDetailsVO.getPhone();
        String w_country = addressDetailsVO.getCountry();
        String w_city = addressDetailsVO.getCity();
        String w_address =addressDetailsVO.getCity();
        String w_zip = addressDetailsVO.getZipCode();
        String sinInfo = PayforasiaUtils.SHA256forSales(w_mer_no, w_gateway_no, w_order_no, w_order_currency, w_order_amount, w_first_name, w_last_name, w_card_no, w_card_expire_year, w_card_expire_month, w_card_security_code, w_email, signKey);
        try
        {
            Map saleMap = new TreeMap();

            saleMap.put("w_mer_no", w_mer_no);
            saleMap.put("w_gateway_no", w_gateway_no);
            saleMap.put("w_order_no", w_order_no);
            saleMap.put("w_order_currency", w_order_currency);
            saleMap.put("w_order_amount", w_order_amount);
           // saleMap.put("w_return_url", w_return_url);
            saleMap.put("w_payment_method", w_payment_method);
            // Card Details
            saleMap.put("w_card_no", w_card_no);
            saleMap.put("w_card_expire_month", w_card_expire_month);
            saleMap.put("w_card_expire_year", w_card_expire_year);
            saleMap.put("w_card_security_code", w_card_security_code);
            saleMap.put("w_issuing_bank", w_issuing_bank);
            saleMap.put("csid", "");
            //Personal Details
            saleMap.put("w_first_name",w_first_name );
            saleMap.put("w_last_name", w_last_name);
            saleMap.put("w_email", w_email);
            saleMap.put("w_ip", w_ip);
            saleMap.put("w_phone", w_phone);
            saleMap.put("w_country", w_country);
            saleMap.put("w_city", w_city);
            saleMap.put("w_address", w_address);
            saleMap.put("w_zip", w_zip);
            saleMap.put("w_sign_info", sinInfo);

            String saleRequest = PayforasiaUtils.joinMapValue(saleMap, '&');
            String saleResponce="";
            transactionLogger.error("saleRequest ---" + saleRequest);

            if (isTest)
            {
                transactionLogger.error("inside isTest ----" + RB.getString("TEST_SALE_URL"));
                saleResponce = PayforasiaUtils.doPostHTTPSURLConnection(RB.getString("TEST_SALE_URL"), saleRequest);
            }
            else
            {
                transactionLogger.error("inside isLive ----" + RB.getString("LIVE_SALE_URL"));
                saleResponce =PayforasiaUtils.doPostHTTPSURLConnection(RB.getString("LIVE_SALE_URL"), saleRequest);;
            }
            transactionLogger.error("saleResponce ----" + saleResponce);

            Map responseParameter = PayforasiaUtils.ReadSalesResponse(saleResponce);
            if (responseParameter != null && !responseParameter.equals(""))
            {
                String status = "";
                String merNo = (String) responseParameter.get("w_mer_no");
                String gatewayNo = (String) responseParameter.get("w_gateway_no");
                String tradeNo = (String) responseParameter.get("w_trade_no");
                String orderNumber = (String) responseParameter.get("w_order_no");
                String orderAmount = (String) responseParameter.get("w_order_amount");
                String orderCurrency = (String) responseParameter.get("w_order_currency");
                String orderStatus = String.valueOf(responseParameter.get("w_order_status"));
                String orderInfo = String.valueOf(responseParameter.get("w_order_info"));
                String signInfo = String.valueOf(responseParameter.get("w_sign_info"));
                String remark = String.valueOf(responseParameter.get("w_remark"));
                String bankDescriptor = String.valueOf(responseParameter.get("billAddress"));
                if (functions.isValueNull(orderStatus))
                {
                    if (functions.isValueNull(bankDescriptor))
                    {
                        descriptor = bankDescriptor;
                    }
                    if (orderStatus.equals("1"))
                    {
                        status = "success";
                        commResponseVO.setDescriptor(descriptor);
                    }
                    else if (orderStatus.equals("0"))
                    {
                        status = "fail";
                    }
                    else if (orderStatus.equals("-1"))
                    {
                        status = "Pending";
                    }
                    else if (orderStatus.equals("-2"))
                    {
                        status = "To be confirmed";
                    }

                    commResponseVO.setStatus(status);
                    commResponseVO.setTransactionId(tradeNo);
                    commResponseVO.setAmount(orderAmount);
                    commResponseVO.setCurrency(orderCurrency);
                    commResponseVO.setTmpl_Currency(tmpl_currency);
                    commResponseVO.setTmpl_Amount(tmpl_amount);
                    commResponseVO.setIpaddress(ip);
                    commResponseVO.setMerchantId(merNo);
                    commResponseVO.setRemark(status + "/" + orderInfo);
                    commResponseVO.setDescription(orderInfo);
                    commResponseVO.setErrorCode(orderStatus);
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("order status not found");
                    commResponseVO.setDescription("order status not found");
                }
            }
            else
            {
                commResponseVO.setStatus("Fail");
                commResponseVO.setRemark("Response Parameters Not Found");
                commResponseVO.setDescription("Response Parameters Not Found");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception While Sale Transaction ----", e);
        }
        return  commResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint(PBSPaymentGatewayAmex.class.getName(), "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech support Team:::", null);
    }
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Inside Inquiry ---");
        CommRequestVO reqVO= (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO=reqVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();
        Functions functions = new Functions();

        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        String orderId=commTransactionDetailsVO.getOrderId();
        String merNo = gatewayAccount.getMerchantId();
        String signKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String gatewayNo = gatewayAccount.getFRAUD_FTP_USERNAME();
        String sinInfo = PayforasiaUtils.SHA256forInquiry(merNo,gatewayNo,signKey);

        try
        {
            Map inquiryMap = new TreeMap();

            inquiryMap.put("merNo", merNo);
            inquiryMap.put("gatewayNo", gatewayNo);
            inquiryMap.put("orderNo", orderId);
            inquiryMap.put("signInfo", sinInfo);

            String inquiryRequest = PayforasiaUtils.joinMapValue(inquiryMap, '&');
            transactionLogger.error("inquiry request ----" + inquiryRequest);

            String inquiryResponse = " ";
            if (isTest)
            {
                transactionLogger.error("inside isTest ----" + RB.getString("TEST_INQUIRY_URL"));
                inquiryResponse = PayforasiaUtils.doPostHTTPSURLConnection(RB.getString("TEST_INQUIRY_URL"), inquiryRequest);
            }
            else
            {
                transactionLogger.error("inside isLive ----" + RB.getString("LIVE_INQUIRY_URL"));
                inquiryResponse = PayforasiaUtils.doPostHTTPSURLConnection(RB.getString("LIVE_INQUIRY_URL"), inquiryRequest);

            }
            transactionLogger.error("inquiryResponse -----" + inquiryResponse);

            Map<String, String> responseInquiry = PayforasiaUtils.ReadInquiryResponse(inquiryResponse);

            if (responseInquiry != null && !responseInquiry.isEmpty())
            {
                String inquiry_QueryResult = responseInquiry.get("queryResult");
                String inquiry_MerNo = responseInquiry.get("merNo");
                String inquiry_TradeNo = responseInquiry.get("tradeNo");
                String inquiry_Amount = responseInquiry.get("tradeAmount");
                String inquiry_Curency = responseInquiry.get("tradeCurrency");
                String inquiry_OrderNo = responseInquiry.get("orderNo");
                String status="";


                if (functions.isValueNull(inquiry_QueryResult))
                {
                    if (inquiry_QueryResult.equals("1"))
                    {
                        status="success";
                        commResponseVO.setStatus(status);
                        commResponseVO.setRemark("SYS:Transaction Successful");
                        commResponseVO.setDescription("SYS:Transaction Successful");
                    }
                    else
                    {
                        status="fail";
                        commResponseVO.setStatus(status);
                        commResponseVO.setRemark("SYS:Transaction Failed");
                        commResponseVO.setDescription("SYS:Transaction Failed");
                    }

                    commResponseVO.setAmount(inquiry_Amount);
                    commResponseVO.setAuthCode(inquiry_QueryResult);
                    commResponseVO.setBankTransactionDate(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setCurrency(inquiry_Curency);
                    commResponseVO.setMerchantId(inquiry_MerNo);
                    commResponseVO.setMerchantOrderId(inquiry_OrderNo);
                    commResponseVO.setTransactionId(inquiry_TradeNo);
                    commResponseVO.setTransactionStatus(status);
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("Query Result Not Found");
                    commResponseVO.setDescription("Query Result Not Found");
                }
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Inquiry Response Not Fund");
                commResponseVO.setDescription("Inquiry Response Not Fund");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception While Inquiry ---",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Inside processRefund ---");
        Functions functions=new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String merNo = gatewayAccount.getMerchantId();
        String signKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String gatewayNo = gatewayAccount.getFRAUD_FTP_USERNAME();
        CommRequestVO reqVO= (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO=reqVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();
        String tradeNo = commTransactionDetailsVO.getPreviousTransactionId();
        transactionLogger.debug("tradeNo ---"+tradeNo);
        String tradeAmount = commTransactionDetailsVO.getPreviousTransactionAmount();
        String refundAmount = commTransactionDetailsVO.getAmount();
        String refundReason = commTransactionDetailsVO.getOrderDesc();
        String status = "";

        String currency="";
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency=commTransactionDetailsVO.getCurrency();
        }

        String refundType = "1";
        if(!commTransactionDetailsVO.getPreviousTransactionAmount().equals(commTransactionDetailsVO.getAmount()))
        {
            refundType = "2";
        }

        String sinInfo = PayforasiaUtils.SHA256forRefund(merNo, gatewayNo, tradeNo, refundType, signKey);

        try
        {
            Map refundMap = new TreeMap();

            refundMap.put("merNo", merNo);
            refundMap.put("gatewayNo", gatewayNo);
            refundMap.put("signInfo", sinInfo);
            refundMap.put("tradeNo", tradeNo);
            refundMap.put("refundType", refundType);
            refundMap.put("tradeAmount", tradeAmount);
            refundMap.put("refundAmount", refundAmount);
            refundMap.put("currency", currency);
            refundMap.put("refundReason", refundReason);
            refundMap.put("remark", "Refund Remark");

            String refundParameters = PayforasiaUtils.joinMapValue(refundMap, '&');

            transactionLogger.error("refund request ---" + refundParameters);
            String response = PayforasiaUtils.doPostHTTPSURLConnection(RB.getString("LIVE_REFUND_URL"), refundParameters);
            transactionLogger.error("refund response ---" + response);

            String refund_MerNo="";
            String refund_GatewayNo="";
            String refund_SignInfo ="";
            String refund_TradeNo ="";
            String refund_Code ="";
            String refund_Desc ="";
            String refund_BatchNo ="";
            String redund_Remark ="";

            Map<String, String> responseRefund = PayforasiaUtils.ReadRefundResponse(response);

            if (responseRefund != null && !responseRefund.equals(""))
            {
                 refund_MerNo = responseRefund.get("merNo");
                 refund_GatewayNo = responseRefund.get("gatewayNo");
                 refund_SignInfo = responseRefund.get("signInfo");
                 refund_TradeNo = responseRefund.get("tradeNo");
                 refund_Code = responseRefund.get("code");
                 refund_Desc = responseRefund.get("description");
                 refund_BatchNo = responseRefund.get("batchNo");
                 redund_Remark = responseRefund.get("remark");

                if (functions.isValueNull(refund_Code))
                {
                    if (refund_Code.equals("00"))
                    {
                        status = "success";
                    }
                    else
                    {
                        status = "fail";
                    }
                }
                else
                {
                    status = "fail";
                    commResponseVO.setRemark("RefundCode Not Not Found");
                    commResponseVO.setDescription("RefundCode Not Not Found");
                }
            }
            else
            {
                status = "fail";
                commResponseVO.setRemark("Refund Response Not Found");
                commResponseVO.setDescription("Refund Response Not Found");
            }
            commResponseVO.setStatus(status);
            commResponseVO.setMerchantId(refund_MerNo);
            commResponseVO.setTransactionId(refund_TradeNo);
            commResponseVO.setAuthCode(refund_Code);
            commResponseVO.setRemark(status+"/"+redund_Remark);
            commResponseVO.setDescription(redund_Remark);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception While Inquiry ---",e);
        }

        return commResponseVO;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
