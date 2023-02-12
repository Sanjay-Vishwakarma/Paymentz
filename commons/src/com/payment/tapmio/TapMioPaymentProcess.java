package com.payment.tapmio;

import com.directi.pg.TransactionLogger;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.validators.vo.CommonValidatorVO;

import java.util.Iterator;

/**
 * Created by Admin on 5/15/2020.
 */
public class TapMioPaymentProcess extends CommonPaymentProcess
{


    private static TransactionLogger transactionLogger=new TransactionLogger(TapMioPaymentProcess.class.getName());
    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.debug("inside TapMioPaymentProcess  ---==="+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        return directKitResponseVO;
    }
    public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D){
        transactionLogger.error("inside  TapMioPaymentProcess Form---");

        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" ;
        form+="<input type=\"hidden\" name=\"TermUrl\"  value=\"\">";
        form+="</form><script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        transactionLogger.error("TapMioPaymentProcess Form---"+form.toString());
        return form.toString();
    }

}
