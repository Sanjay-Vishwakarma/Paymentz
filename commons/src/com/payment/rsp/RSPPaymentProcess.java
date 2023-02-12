package com.payment.rsp;

import com.directi.pg.TransactionLogger;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;

/**
 * Created by admin on 5/21/2018.
 */
public class RSPPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(RSPPaymentProcess.class.getName());

    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }

    public String getSpecificVirtualTerminalJSP()
    {
        return "payforasiaspecificfields.jsp";
    }

    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        String target = "target=_blank";
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\"" + target + ">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }
}
