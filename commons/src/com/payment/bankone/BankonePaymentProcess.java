package com.payment.bankone;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;

/**
 * Created by Admin on 8/19/2017.
 */
public class BankonePaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(BankonePaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(BankonePaymentProcess.class.getName());

    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D){
        String target = "target=_blank";
        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+response3D.getTransactionId()+ "\""+target+">" +
                "<input type=hidden name=payId id=payId value=\"" +response3D.getTransactionId()+"\">"+
                "</form>" +
                "<script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        transactionLogger.error("BankonePaymentProcess Form---"+form.toString());
        return form.toString();
    }

    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D){

        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+response3D.getTransactionId()+ "\">" +
                "<input type=hidden name=payId id=payId value=\"" +response3D.getTransactionId()+"\">"+
                "</form>" +
                "<script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        transactionLogger.error("BankonePaymentProcess Form---"+form.toString());
        return form.toString();
    }

}
