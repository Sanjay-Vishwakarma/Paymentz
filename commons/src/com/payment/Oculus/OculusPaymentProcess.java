package com.payment.Oculus;

import com.directi.pg.TransactionLogger;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;

/**
 * Created by Admin on 7/28/2021.
 */
public class OculusPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger =new TransactionLogger(OculusPaymentProcess.class.getName());

    public String get3DConfirmationFormVT(String trackingId,String ctoken,Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." +response3D.getUrlFor3DRedirect());
        String target = "target=\"_blank\"";
        String form="<form name=\"datos\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\""+target+">"+
                "<input type=\"hidden\" name=\"creq\" value=\""+response3D.getCreq()+ "\">"+
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getCreq()+ "\">"+
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+ "\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+ "\">"+
                "</form>"+
                "<script language=\"javascript\"> document.datos.submit(); </script>";
        transactionLogger.error("form....." +form);
        return form;
    }

    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String target = "target=\"\"";
        String form="<form name=\"datos\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\""+target+">"+
                "<input type=\"hidden\" name=\"creq\" value=\""+response3D.getCreq()+ "\">"+
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+ "\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+ "\">"+
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getCreq()+ "\">"+
                "</form>"+
                "<script language=\"javascript\"> document.datos.submit(); </script>";
        transactionLogger.error("form....." +form);
        return form;
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;

        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("MD");
        asyncParameterVO.setValue(response3D.getMd());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("TermUrl");
        asyncParameterVO.setValue(response3D.getTerURL());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("PaReq");
        asyncParameterVO.setValue(response3D.getCreq());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("creq");
        asyncParameterVO.setValue(response3D.getCreq());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
    }
}
