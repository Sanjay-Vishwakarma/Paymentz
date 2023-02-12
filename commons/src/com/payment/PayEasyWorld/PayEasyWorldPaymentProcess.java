package com.payment.PayEasyWorld;

import com.directi.pg.TransactionLogger;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;

/**
 * Created by Admin on 5/24/2021.
 */
public class PayEasyWorldPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(PayEasyWorldPaymentProcess.class.getName());
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String target = "target=\"_blank\"";
        String form="<form name=\"launch3D\" method=\"post\" action=\""+response3D.getUrlFor3DRedirect()+"\""+target+">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        return form;
    }

    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String target = "target=\"\"";

        String form="<form name=\"launch3D\" method=\"post\" action=\""+response3D.getUrlFor3DRedirect()+"\" "+target+">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        return form;
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;

        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }
    public String getSpecificVirtualTerminalJSP()
    {
        return "payforasiaspecificfields.jsp";
    }
}
