package com.payment.Rubixpay;

import com.directi.pg.TransactionLogger;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Admin on 5/27/2020.
 */
public class RubixpayPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger=new TransactionLogger(RubixpayPaymentProcess.class.getName());
    public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D){
        transactionLogger.error("inside  RubixpayPaymentProcess Form---");

        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" ;
        form+="<input type=\"hidden\" name=\"TermUrl\"  value=\"\">";
        form+="</form><script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        transactionLogger.error("RubixpayPaymentProcess Form---"+form.toString());
        return form.toString();
    }
}
