package com.payment.octapay;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;

/**
 * Created by Admin on 12/5/2020.
 */
public class OctapayPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(OctapayPaymentProcess.class.getName());

/*
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        Functions functions = new Functions();
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());

        StringBuffer html = new StringBuffer(response3D.getUrlFor3DRedirect());

        transactionLogger.debug("form----"+html.toString());
        return html.toString();
    }
*/
public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
{
    Functions functions=new Functions();
    transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
    String target = "target=\"\"";
    String transactionId="";
    String url="";


    if(functions.isValueNull(response3D.getUrlFor3DRedirect())&&response3D.getUrlFor3DRedirect().contains("?"))
    { url=response3D.getUrlFor3DRedirect().split("\\?")[0];
        transactionId=response3D.getUrlFor3DRedirect().split("\\?")[1];
        transactionId=transactionId.split("=")[1];
    }
    else {
        url=response3D.getUrlFor3DRedirect();
    }

    StringBuffer form=new StringBuffer("<form name=\"launch3D\" method=\"get\" action=\""+url+"\">");
    form.append("<input type=\"hidden\" name=\"transaction_id\" value=\"" +transactionId + "\">");
    form.append("</form>"+
            "<script language=\"javascript\"> document.launch3D.submit(); </script>");
    transactionLogger.error("form---->"+form);
    return form.toString();
}
    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        Functions functions = new Functions();
        String transactionId="";
        String url="";


        if(functions.isValueNull(response3D.getUrlFor3DRedirect())&&response3D.getUrlFor3DRedirect().contains("?"))
        { url=response3D.getUrlFor3DRedirect().split("\\?")[0];
            transactionId=response3D.getUrlFor3DRedirect().split("\\?")[1];
            transactionId=transactionId.split("=")[1];
        }
        else {
            url=response3D.getUrlFor3DRedirect();
        }

        AsyncParameterVO asyncParameterVO = null;
        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("transaction_id");
        asyncParameterVO.setValue(transactionId);
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("transType");
        asyncParameterVO.setValue("");
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
        transactionLogger.debug("inside Octapay payment process---" + url);

        directKitResponseVO.setBankRedirectionUrl(url);
        directKitResponseVO.setMethod(response3D.getMethod());

    }
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        Functions functions=new Functions();
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String target = "target=\"\"";
        String transactionId="";
        String url="";


        if(functions.isValueNull(response3D.getUrlFor3DRedirect())&&response3D.getUrlFor3DRedirect().contains("?"))
        { url=response3D.getUrlFor3DRedirect().split("\\?")[0];
            transactionId=response3D.getUrlFor3DRedirect().split("\\?")[1];
            transactionId=transactionId.split("=")[1];
        }
        else {
            url=response3D.getUrlFor3DRedirect();
        }

        StringBuffer form=new StringBuffer("<form name=\"launch3D\" method=\"get\" action=\""+url+"\">");
        form.append("<input type=\"hidden\" name=\"transaction_id\" value=\"" +transactionId + "\">");
        form.append("</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>");
        transactionLogger.error("form---->"+form);
        return form.toString();
    }


}
