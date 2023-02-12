package com.payment.euroeximbank;

import com.directi.pg.TransactionLogger;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.payaidpayments.PayaidPaymentGateway;
import com.payment.validators.vo.CommonValidatorVO;

import java.util.Iterator;

/**
 * Created by Admin on 12/28/2021.
 */
public class EuroEximBankPaymentProcess extends CommonPaymentProcess
{
    public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D){
        TransactionLogger transactionlogger= new TransactionLogger(PayaidPaymentGateway.class.getName());
        transactionlogger.error("inside  PayAidPaymentProcess Form---");
        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" ;
        Iterator keys=response3D.getRequestMap().keySet().iterator();
        while (keys.hasNext())
        {
            String key= (String) keys.next();
            form+="<input type=\"hidden\" name=\""+key+"\"  value=\""+response3D.getRequestMap().get(key)+"\">";

        }
        form+="</form><script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        transactionlogger.error("PayAidPaymentProcess Form---"+form.toString());
        return form.toString();
    }

}
