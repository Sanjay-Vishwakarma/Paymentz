package com.payment.payFluid;

import com.directi.pg.TransactionLogger;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Admin on 12/3/2020.
 */
public class PayFluidPaymentProcess  extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger=new TransactionLogger(PayFluidPaymentProcess.class.getName());
    public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D){
        transactionLogger.error("inside  PayFluidPaymentProcess Form---");

        String form = "<form name=\"creditcard_checkout\" method=\"GET\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" ;

        form+="</form><script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        transactionLogger.error("VervePaymentProcess Form---"+form.toString());
        return form.toString();
    }

}
