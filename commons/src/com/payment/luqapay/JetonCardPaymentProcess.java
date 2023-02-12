package com.payment.luqapay;

import com.directi.pg.TransactionLogger;
import com.manager.TransactionManager;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZPayoutRequest;

/**
 * Created by Vivek on 2/20/2020.
 */
public class JetonCardPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger=new TransactionLogger(JetonCardPaymentProcess.class.getName());
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
        commAddressDetailsVO.setFirstname(transactionDetailsVO.getFirstName());
        commAddressDetailsVO.setLastname(transactionDetailsVO.getLastName());
        commAddressDetailsVO.setCountry(transactionDetailsVO.getCountry());
        commAddressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }
}
