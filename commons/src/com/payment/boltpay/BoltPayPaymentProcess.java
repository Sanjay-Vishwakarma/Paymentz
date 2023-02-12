package com.payment.boltpay;

import com.directi.pg.TransactionLogger;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by admin on 26-Jul-21.
 */
public class BoltPayPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger=new TransactionLogger(BoltPayPaymentProcess.class.getName());

    public String get3DConfirmationForm(String trackingid, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("inside  BoltPayPaymentProcess Form---");

        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" ;
        form+="<input type=\"hidden\" name=\"TermUrl\"  value=\"\">";
        form+="</form><script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        transactionLogger.error("BoltPayPaymentProcess Form --- "+form.toString());
        return form.toString();
    }

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("inside setNBResponseVO Process ---> "+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        transactionLogger.error("inside BoltPayPaymentProcess getBankRedirectionUrl Process --> "+directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }



    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside  BoltPayPaymentProcess --- "+response3D.getUrlFor3DRedirect());



        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }
}
