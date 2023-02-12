package com.payment.phoneix;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZRefundRequest;

/**
 * Created by Vivek on 9/12/2019.
 */
public class PhoneixPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(PhoneixPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PhoneixPaymentProcess.class.getName());
    @Override
    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {

        int trackingid=refundRequest.getTrackingId();
        CommAddressDetailsVO commAddressDetailsVO=requestVO.getAddressDetailsVO();
        TransactionManager transactionManager= new TransactionManager();
        TransactionDetailsVO transactionVO =transactionManager.getTransDetailFromCommon(String.valueOf(trackingid));
        transactionLogger.debug("Street-----"+transactionVO.getStreet());
        transactionLogger.debug("Country-----"+transactionVO.getCountry());
        commAddressDetailsVO.setStreet(transactionVO.getStreet());
        commAddressDetailsVO.setCountry(transactionVO.getCountry());
        requestVO.setAddressDetailsVO(commAddressDetailsVO);

    }
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        String target = "target=_blank";
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\""+target+">" +
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }
    @Override
    public String get3DConfirmationForm(String trackingid, String ctoken, Comm3DResponseVO response3D){

        transactionLogger.error("3d page displayed ---" + response3D.getUrlFor3DRedirect());

        String form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        return form;
    }
    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside bd payment process---"+response3D.getUrlFor3DRedirect());

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("");
        asyncParameterVO.setValue("");
        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
    }
}
