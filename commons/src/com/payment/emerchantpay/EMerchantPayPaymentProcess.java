package com.payment.emerchantpay;

import com.directi.pg.TransactionLogger;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZPayoutRequest;

/**
 * Created by Admin on 1/29/2020.
 */
public class EMerchantPayPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger=new TransactionLogger(EMerchantPayPaymentProcess.class.getName());
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());

        String form="<form name=\"launch3D\" method=\"GET\" action=\""+response3D.getUrlFor3DRedirect()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\"\">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        return form;
    }
    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        transactionLogger.error("inside bd payment process---"+response3D.getUrlFor3DRedirect());

        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
        directKitResponseVO.setMethod(response3D.getMethod());
    }
    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        CommAddressDetailsVO commAddressDetailsVO=requestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=requestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=requestVO.getCardDetailsVO();
        TransactionManager transactionManager=new TransactionManager();
        TransactionDetailsVO transactionDetailsVO=transactionManager.getTransDetailFromCommon(String.valueOf(payoutRequest.getTrackingId()));
        // commTransactionDetailsVO.setAmount(transactionDetailsVO.getAmount());
        commAddressDetailsVO.setFirstname(transactionDetailsVO.getFirstName());
        commAddressDetailsVO.setLastname(transactionDetailsVO.getLastName());
        commAddressDetailsVO.setStreet(transactionDetailsVO.getStreet());
        commAddressDetailsVO.setZipCode(transactionDetailsVO.getZip());
        commAddressDetailsVO.setCity(transactionDetailsVO.getCity());
        commAddressDetailsVO.setState(transactionDetailsVO.getState());
        commAddressDetailsVO.setCountry(transactionDetailsVO.getCountry());
        //commTransactionDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
        commTransactionDetailsVO.setPreviousTransactionId(transactionDetailsVO.getPaymentId());
        commAddressDetailsVO.setPhone(transactionDetailsVO.getTelno());
        commAddressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
        commAddressDetailsVO.setIp(payoutRequest.getIpAddress());
        //commAddressDetailsVO.setLanguage(transactionDetailsVO.getLanguage());
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }
}
