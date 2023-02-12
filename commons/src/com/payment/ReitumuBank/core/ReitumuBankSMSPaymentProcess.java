package com.payment.ReitumuBank.core;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 2/10/15
 * Time: 8:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReitumuBankSMSPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(ReitumuBankSMSPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(ReitumuBankSMSPaymentProcess.class.getName());

    /*@Override
    public String getSpecificVirtualTerminalJSP()
    {
        return "reitumuCreditPage.jsp";    //To change body of overridden methods use File | Settings | File Templates.
    }*/

    @Override
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        String target = "target=_blank";
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\" "+target+">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }

    @Override
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }

}
