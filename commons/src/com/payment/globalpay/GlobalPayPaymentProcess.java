package com.payment.globalpay;

import com.directi.pg.TransactionLogger;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Admin on 1/8/2021.
 */
public class GlobalPayPaymentProcess extends CommonPaymentProcess
{

    private static TransactionLogger transactionLogger=new TransactionLogger(GlobalPayPaymentProcess.class.getName());
    public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D){
        transactionLogger.error("inside  GlobalPayPaymentProcess Form---");

        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" ;
        form+="</form><script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        transactionLogger.error("GlobalPayPaymentProcess Form---"+form.toString());
        return form.toString();
    }
}
