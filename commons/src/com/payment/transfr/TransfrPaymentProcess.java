package com.payment.transfr;

import com.directi.pg.TransactionLogger;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by admin on 28-Apr-22.
 */
public class TransfrPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger=new TransactionLogger(TransfrPaymentProcess.class.getName());

    public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D)
    {
        transactionLogger.error("inside  TransfrPaymentProcess get3DConfirmationForm---");

        String form = "<form name=\"creditcard_checkout\" method=\"GET\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" ;
        form+="<input type=\"hidden\" name=\"TermUrl\"  value=\"\">";
        form+="</form><script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        transactionLogger.error("TransfrPaymentProcess Form --- "+form.toString());
        return form.toString();
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        transactionLogger.error("inside  TransfrPaymentProcess set3DResponseVO ---");

        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside  TransfrPaymentProcess --- "+response3D.getUrlFor3DRedirect());

        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
        directKitResponseVO.setMethod("GET");
    }

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("inside setNBResponseVO Process ---> "+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        transactionLogger.error("inside TransfrPaymentProcess getBankRedirectionUrl Process --> "+directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }
}
