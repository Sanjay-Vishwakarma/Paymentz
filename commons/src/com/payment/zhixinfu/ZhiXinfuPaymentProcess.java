package com.payment.zhixinfu;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;

/**
 * Created by Admin on 11/11/2020.
 */
public class ZhiXinfuPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(ZhiXinfuPaymentProcess.class.getName());

/*    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        Functions functions = new Functions();
        transactionLogger.error("3d page displayed ZhiXinfuPaymentProcess....." + response3D.getUrlFor3DRedirect());
        String target = "";
        StringBuffer form = new StringBuffer("<form name=\"launch3D\" method=\"post\" action=\"" + response3D.getUrlFor3DRedirect() + "\" " + target + ">");
        if (functions.isValueNull(response3D.getPaReq()))
        {
            form.append("<input type=\"hidden\" name=\"PaReq\" value=\"" + response3D.getPaReq() + "\">" +
                    "<input type=\"hidden\" name=\"TermUrl\" value=\"" + response3D.getTerURL() + "\">" +
                    "<input type=\"hidden\" name=\"MD\" value=\"" + response3D.getMd() + "\">");
        }
        form.append("</form>" +
                "<script language=\"javascript\"> document.launch3D.submit(); </script>");
        return form.toString();
    }*/

    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {

        String form =response3D.getUrlFor3DRedirect();
        transactionLogger.debug("form----"+form);
        return form;

          }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside Zhixinfu payment process---" + response3D.getUrlFor3DRedirect());

        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());

    }
}
