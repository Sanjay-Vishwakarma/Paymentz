package com.payment.decta.core;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;

/**
 * Created by Trupti on 5/19/2017.
 */
public class DectaPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(DectaPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(DectaPaymentProcess.class.getName());

    @Override
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D){
        log.debug("3d page displayed....."+response3D.getUrlFor3DRedirect());
        String target = "target=_blank";

        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\" "+target+">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }

    @Override
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D){
        log.debug("3d page displayed....."+response3D.getUrlFor3DRedirect());

        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }


    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        log.debug("inside decta payment process---"+response3D.getUrlFor3DRedirect());
        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }


}
