package com.payment.ninja;

import com.directi.pg.TransactionLogger;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Admin on 5/31/2019.
 */
public class NinjaWalletPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger= new TransactionLogger(NinjaWalletPaymentProcess.class.getName());

    @Override
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String target = "target=_blank";
        String form="<form name=\"launch3D\" method=\"GET\" action=\""+response3D.getUrlFor3DRedirect()+"\""+target+">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        return form;
    }

    @Override
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());

        String form="<form name=\"launch3D\" method=\"GET\" action=\""+response3D.getUrlFor3DRedirect()+"\">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        return form;
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        transactionLogger.error("Inside REST KIT");
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside NinjaWallet Rest 3d payment process---" + response3D.getUrlFor3DRedirect());
        transactionLogger.debug("inside NinjaWallet Rest 3d payment process---" + response3D.getMethod());
        transactionLogger.debug("inside NinjaWallet Rest 3d payment process---" + response3D.getTarget());
        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
        directKitResponseVO.setMethod(response3D.getMethod());

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("launch3D");
        asyncParameterVO.setValue(response3D.getUrlFor3DRedirect());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO= new AsyncParameterVO();
        asyncParameterVO.setName("");
        asyncParameterVO.setValue("");
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

    }
}
