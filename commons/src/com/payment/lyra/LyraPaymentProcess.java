package com.payment.lyra;

import com.directi.pg.TransactionLogger;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.validators.vo.CommonValidatorVO;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 12/02/2022.
 */
public class LyraPaymentProcess extends CommonPaymentProcess
{
    TransactionLogger transactionlogger = new TransactionLogger(LyraPaymentProcess.class.getName());
    public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D)
    {
        transactionlogger.error("inside  Lyra PaymentProcess Form---");
        StringBuffer form               = new StringBuffer("<form name=\"creditcard_checkout\" action=\""+response3D.getUrlFor3DRedirect()+"\" method=\"POST\">");
        HashMap<String,String> formMap  = (HashMap<String, String>) response3D.getRequestMap();
        if(formMap!=null)
        {
            for (Map.Entry<String, String> entry : formMap.entrySet())
            {
                form.append("<input type=\"hidden\" name=\""+entry.getKey()+"\" value=\""+entry.getValue()+"\">");
            }
        }
        form.append("</form><script>document.creditcard_checkout.submit();</script>");
        transactionlogger.error("form--->"+form);
        return form.toString();
    }
}
