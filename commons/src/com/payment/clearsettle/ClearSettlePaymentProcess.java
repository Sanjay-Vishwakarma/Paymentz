package com.payment.clearsettle;

import com.directi.pg.TransactionLogger;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;

/**
 * Created by Sneha on 1/18/2017.
 */
public class ClearSettlePaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(ClearSettlePaymentProcess.class.getName());
    public String getSpecificVirtualTerminalJSP()
    {
        return "clearsettlespecificfields.jsp";    //To change body of overridden methods use File | Settings | File Templates.
    }

    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        String target = "target=_blank";
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\""+target+">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }

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
}
