package com.transaction.utils;

import com.directi.pg.FraudDefenderLogger;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.response.PZResponseStatus;
import com.payment.validators.vo.CommonValidatorVO;

import com.transaction.vo.restVO.ResponseVO.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Admin on 4/20/2020.
 */
public class WriteDirectFraudDefenderResponse
{


    private ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
    private Functions functions = new Functions();
    private Logger logger = new Logger(WriteDirectTransactionResponse.class.getName());
    private FraudDefenderLogger fraudDefenderLogger = new FraudDefenderLogger(WriteDirectFraudDefenderResponse.class.getName());

    public void setRestFraudDefenderResponseForError(FraudDefender response, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO, PZResponseStatus status, String remark)
    {

        Result result = null;
        if (response != null)
        {
            if (commonValidatorVO != null)
            {

            }
            if (errorCodeListVO != null && errorCodeListVO.getListOfError() != null)
            {
                StringBuffer errorCode = new StringBuffer();
                StringBuffer errorDesc = new StringBuffer();
                for (ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
                {
                    result = new Result();
                    if (errorCode.length() > 0)
                    {
                        errorCode.append(" | ");
                        errorDesc.append(" | ");
                    }
                    errorCode.append(errorCodeVO.getApiCode());
                    errorDesc.append(errorCodeVO.getApiDescription());

                    result.setResultCode(errorCode.toString());
                    result.setDescription(errorDesc.toString());
                    logger.debug("error code---" + errorCodeVO.getApiCode() + "-" + errorCodeVO.getApiDescription());
                    response.setResult(result);

                }
            }
        }
    }

    public ErrorCodeVO formSystemErrorCodeVO(ErrorName errorName, String reason)
    {
        ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCodeFromName(errorName);
        if (errorCodeVO != null)
            errorCodeVO.setErrorReason(reason);

        return errorCodeVO;
    }

    public ErrorCodeVO formSystemErrorCodeVO(ErrorName errorName)
    {
        ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCodeFromName(errorName);
        return errorCodeVO;
    }





    public void setSuccessResponseQueryDefender(FraudDefender response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        TransactionDetailsVO transactionDetailsVO=commonValidatorVO.getTransactionDetailsVO();
        Transaction transaction = null;
        Customer customer=null;
        Card card=null;
        ChargebackInfo chargebackInfo=null;
        MerchantInfo merchantInfo=null;
        ProductInfo productInfo=null;
        PurchaseInfo purchaseInfo=null;
        BillingInfo billingInfo=null;
        QueryCustomer queryCustomer=null;
        ShippingInfo  shippingInfo=null;
        transaction = new Transaction();
        RefundInfo refundInfo=new RefundInfo();

        card=new Card();
        chargebackInfo=new ChargebackInfo();
        merchantInfo=new MerchantInfo();
        productInfo=new ProductInfo();
        purchaseInfo=new PurchaseInfo();
        billingInfo=new BillingInfo();
        queryCustomer=new QueryCustomer();
        shippingInfo=new ShippingInfo();
        if(functions.isValueNull(transactionDetailsVO.getChargebackAmount()) && !"0.00".equalsIgnoreCase(transactionDetailsVO.getChargebackAmount()))
        {
            chargebackInfo.setCb_amount(transactionDetailsVO.getChargebackAmount());
            chargebackInfo.setCb_currency(transactionDetailsVO.getCurrency());
        }else
        {
            chargebackInfo.setCb_amount("");
            chargebackInfo.setCb_currency("");
        }
        chargebackInfo.setCb_arn("");
        chargebackInfo.setCb_case_number("");
        chargebackInfo.setCb_datetime("");
        chargebackInfo.setCb_exists("");
        chargebackInfo.setCb_purchase_identifier("");

        merchantInfo.setAcquirer_bin("");
        merchantInfo.setAddress("");
        merchantInfo.setBank_mid(transactionDetailsVO.getFromid());
        merchantInfo.setBank_tid("");
        merchantInfo.setCard_acceptor_id("");
        merchantInfo.setCity("");
        merchantInfo.setCountry("");
        merchantInfo.setDescriptor("");
        merchantInfo.setEmail("");
        merchantInfo.setStatus("");
        merchantInfo.setIs_test(transactionDetailsVO.getRemark());
        merchantInfo.setRegion("");

        productInfo.setProduct_contact_phone("");
        productInfo.setProduct_cost(transactionDetailsVO.getAmount());
        productInfo.setProduct_name(transactionDetailsVO.getOrderDescription());
        productInfo.setProduct_quantity("");

        if(functions.isValueNull(transactionDetailsVO.getPaymentId()))
            purchaseInfo.setBank_order_number(transactionDetailsVO.getPaymentId());
        else
            purchaseInfo.setBank_order_number("");
        purchaseInfo.setClient_transaction_id(transactionDetailsVO.getTrackingid());

        billingInfo.setBill_address(transactionDetailsVO.getStreet());
        billingInfo.setBill_city(transactionDetailsVO.getCity());
        billingInfo.setBill_country(transactionDetailsVO.getCountry());
        billingInfo.setBill_first_name(transactionDetailsVO.getFirstName());
        billingInfo.setBill_last_name(transactionDetailsVO.getLastName());
        billingInfo.setBill_zip(transactionDetailsVO.getZip());

        queryCustomer.setEmail(transactionDetailsVO.getEmailaddr());
        queryCustomer.setIp(transactionDetailsVO.getIpAddress());
        queryCustomer.setPhone(transactionDetailsVO.getTelno());

        shippingInfo.setDelivery_date("");
        shippingInfo.setShip_address("");
        shippingInfo.setShip_city("");
        shippingInfo.setShip_country("");
        shippingInfo.setShip_first_name("");
        shippingInfo.setShip_last_name("");
        shippingInfo.setShip_region("");
        shippingInfo.setShip_zip("");
        shippingInfo.setShipped_date("");
        shippingInfo.setShipping_amount("");
        shippingInfo.setShipping_type("");
        shippingInfo.setTracking_number("");

        if(functions.isValueNull(transactionDetailsVO.getRefundAmount()) && !"0.00".equalsIgnoreCase(transactionDetailsVO.getRefundAmount()))
        {
            refundInfo.setRefund_amount(transactionDetailsVO.getRefundAmount());
            refundInfo.setRefund_currency(transactionDetailsVO.getCurrency());
        }else
        {
            refundInfo.setRefund_amount("");
            refundInfo.setRefund_currency("");
        }
        refundInfo.setRefund_arn("");
        refundInfo.setRefund_datetime("");
        refundInfo.setRefund_exists(transactionDetailsVO.getRefund_exists());
        refundInfo.setRefund_purchase_identifier("");


       /* transaction.setSystemPaymentId(transactionDetailsVO.getPaymentId());
        transaction.setAmount(transactionDetailsVO.getAmount());
        transaction.setTransactionStatus(transactionDetailsVO.getStatus());
        transaction.setCaptureamount(transactionDetailsVO.getCaptureAmount());
        transaction.setRefundamount(transactionDetailsVO.getRefundAmount());
        // transaction.setChargebackamount(TransactionDetailsVO.getChargeBackInfo());
        transaction.setDate(transactionDetailsVO.getOrderDescription());
        transaction.setDescriptor(transactionDetailsVO.getOrderDescription());
        transaction.setRemark(transactionDetailsVO.getRemark());
        transaction.setCurrency(transactionDetailsVO.getCurrency());*/





        if(functions.isValueNull(transactionDetailsVO.getFirstSix()))
            card.setBin(transactionDetailsVO.getFirstSix());
        else
            card.setBin("");
        if(functions.isValueNull(transactionDetailsVO.getLastFour()))
            card.setLast4Digits(transactionDetailsVO.getLastFour());
        else
            card.setLast4Digits("");


        response.setChargebackInfo(chargebackInfo);
        response.setMerchantInfo(merchantInfo);
        response.setProductInfo(productInfo);
        response.setPurchaseInfo(purchaseInfo);
        response.setBillingInfo(billingInfo);
        response.setBin_number(transactionDetailsVO.getFirstSix());
        response.setCurrency_iso_number("");
        response.setQueryCustomer(queryCustomer);
        response.setCvv("");
        response.setDescription(transactionDetailsVO.getDescription());
        response.setLast_four(transactionDetailsVO.getLastFour());
        response.setMessage("");
        response.setOrder_number(transactionDetailsVO.getTrackingid());
        response.setRecurring("");
        response.setShippingInfo(shippingInfo);
        response.setRefundInfo(refundInfo);


    }




    public void setSuccessResponseRefundDefender(FraudDefender response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        TransactionDetailsVO transactionDetailsVO=commonValidatorVO.getTransactionDetailsVO();

        Transaction transaction = null;

        RefundInfo refundInfo=new RefundInfo();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        logger.error("refund   amount----------------------------------->"+directKitResponseVO.getRefundAmount());

        if("success".equalsIgnoreCase(directKitResponseVO.getStatus())){
            refundInfo.setRefund_amount(directKitResponseVO.getRefundAmount());
            refundInfo.setRefund_arn(commonValidatorVO.getTransDetailsVO().getClientTransactionId());
            refundInfo.setRefund_currency(commonValidatorVO.getTransDetailsVO().getRefundCurrency());
            refundInfo.setRefund_datetime(String.valueOf(dateFormat.format(date)));
            refundInfo.setRefund_exists("true");
            refundInfo.setRefund_purchase_identifier(commonValidatorVO.getTransDetailsVO().getPaymentid());
            response.setRefundInfo(refundInfo);
        }
       else{
            refundInfo.setRefund_amount("");
            refundInfo.setRefund_arn("");
            refundInfo.setRefund_currency("");
            refundInfo.setRefund_datetime("");
            refundInfo.setRefund_exists("false");
            refundInfo.setRefund_purchase_identifier("");
            response.setRefundInfo(refundInfo);

        }


    }

    public void setFailAuthTokenResponse(FraudDefender response)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if (response != null)
        {
            result = new Result();

            errorCodeVO = errorCodeUtils.getSystemErrorCode(ErrorName.SYS_AUTHTOKEN_FAILED);
            result.setResultCode(errorCodeVO.getApiCode());
            result.setDescription(errorCodeVO.getApiDescription());
            response.setResult(result);

        }
    }

}
