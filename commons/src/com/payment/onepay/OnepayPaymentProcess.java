package com.payment.onepay;

 import com.directi.pg.TransactionLogger;
 import com.manager.vo.AsyncParameterVO;
 import com.manager.vo.DirectKitResponseVO;
 import com.payment.common.core.Comm3DResponseVO;
 import com.payment.common.core.CommonPaymentProcess;
 import com.payment.payaidpayments.PayaidPaymentGateway;
 import com.payment.validators.vo.CommonValidatorVO;

 import java.util.Iterator;

/**
 * Created by Admin on 27/12/2021.
 */
public class OnepayPaymentProcess extends CommonPaymentProcess
{
    TransactionLogger transactionlogger = new TransactionLogger(OnepayPaymentProcess.class.getName());

    public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D)
    {

        transactionlogger.error("inside  OnepayPaymentProcess Form---");
        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\">";
        Iterator keys = response3D.getRequestMap().keySet().iterator();
        while (keys.hasNext())
        {
            String key = (String) keys.next();
            form += "<input type=\"hidden\" name=\"" + key + "\"  value=\"" + response3D.getRequestMap().get(key) + "\">";

        }
        form += "</form><script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        transactionlogger.error("OnepayPaymentProcess Form---" + form.toString());
        return form.toString();
    }

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionlogger.error("inside OnepayPaymentProcess --->" + directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        transactionlogger.error("inside OnepayPaymentProcess getBankRedirectionUrl Process -->" + directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }


    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D, CommonValidatorVO commonValidatorVO)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionlogger.debug("inside  OnepayPaymentProcess---" + response3D.getUrlFor3DRedirect());

        Iterator keys = response3D.getRequestMap().keySet().iterator();
        String key = "";
        while (keys.hasNext())
        {
            key = (String) keys.next();
            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName(key);
            asyncParameterVO.setValue(response3D.getRequestMap().get(key));
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        }

    }
}
