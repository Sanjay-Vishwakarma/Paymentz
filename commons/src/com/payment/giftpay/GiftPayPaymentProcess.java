package com.payment.giftpay;

import com.directi.pg.TransactionLogger;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Admin on 4/3/2021.
 */
public class GiftPayPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(GiftPayPaymentProcess.class.getName());

    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        //1st way
     /*   String form =response3D.getUrlFor3DRedirect();
        transactionLogger.debug("form----"+form);
        return form;*/


        String form="<form name=\"launch3D\" method=\"GET\" action=\""+response3D.getUrlFor3DRedirect()+"\">"+
        "</form>"+ "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        transactionLogger.debug("form----"+form);
        return form;

    }
    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("Inside GiftPayPaymentProcess Form REST NB---");
        transactionLogger.error("directKitResponseVO.getBankRedirectionUrl()---->"+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setMethod(directKitResponseVO.getMethod());
        return directKitResponseVO;
    }
/*
    public String get3DConfirmationFormVT(String trackingId,String ctoken,Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed.....get3DConfirmationFormVT..." +response3D.getUrlFor3DRedirect());

        StringBuffer form=new StringBuffer("<form name=\"launch3D\" method=\"GET\" action=\""+response3D.getUrlFor3DRedirect()+"\">");

        form.append("</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>");
        return form.toString();
    }
*/


    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("Inside giftpay payment process---" + response3D.getUrlFor3DRedirect());

        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
        directKitResponseVO.setMethod("GET");

      /*  asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("PaReq");
        asyncParameterVO.setValue(response3D.getPaReq());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("TermUrl");
        asyncParameterVO.setValue(response3D.getTerURL());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("MD");
        asyncParameterVO.setValue(response3D.getMd());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("connector");
        asyncParameterVO.setValue(response3D.getConnector());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);


        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("threeDSSessionData");
        asyncParameterVO.setValue(response3D.getThreeDSSessionData());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("threeDSMethodData");
        asyncParameterVO.setValue(response3D.getThreeDSMethodData());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("creq");
        asyncParameterVO.setValue(response3D.getCreq());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

*/

    }

}
