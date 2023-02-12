package com.payment.quickpayments;

import com.directi.pg.TransactionLogger;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Admin on 4/28/2021.
 */
public class QuickPaymentsPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(QuickPaymentsPaymentProcess.class.getName());

    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("---- Inside QuickPaymentsPaymentProcess ----");
        String form="<form name=\"launch3D\" method=\"GET\" action=\""+response3D.getUrlFor3DRedirect()+"\">"+
                "<input type=\"hidden\" name=\"mcTxId\" value=\""+response3D.getResponseHashInfo()+"\">"+
                "</form>"+ "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        return form;

    }
    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("Inside QuickPaymentsPaymentProcess---" + response3D.getUrlFor3DRedirect());

        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
        directKitResponseVO.setMethod("GET");

        asyncParameterVO=new AsyncParameterVO();
        asyncParameterVO.setName("mcTxId");
        asyncParameterVO.setValue(response3D.getResponseHashInfo());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO=new AsyncParameterVO();
        asyncParameterVO.setName("TermUrl");
        asyncParameterVO.setValue("");
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
    }
    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("Inside QuickPaymentsPaymentProcess Form REST NB---");
        transactionLogger.error("directKitResponseVO.getBankRedirectionUrl()---->"+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setMethod(directKitResponseVO.getMethod());
        return directKitResponseVO;
    }

}
