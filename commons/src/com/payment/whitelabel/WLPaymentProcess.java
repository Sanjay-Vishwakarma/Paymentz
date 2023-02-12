package com.payment.whitelabel;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;

import java.util.HashMap;

/**
 * Created by Uday on 10/16/18.
 */
public class WLPaymentProcess extends CommonPaymentProcess
{
    TransactionLogger transactionLogger= new TransactionLogger(WLPaymentProcess.class.getName());


    @Override
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String html="";
        StringBuilder sb= new StringBuilder();
        String target = "target=_blank";
        HashMap map= (HashMap) response3D.getRequestMap();
        sb.append("<form name=\"launch\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\""+target+">");
        for(Object key : map.keySet()){
            sb.append("<input type=\"hidden\" name=\""+key.toString()+"\" value=\""+map.get(key.toString())+"\" >");
        }
        sb.append("</form>");
        sb.append("<script language=\"javascript\"> document.launch.submit(); </script>");
        transactionLogger.error("WLUtils Form -----"+html);
        return html;
    }

    @Override
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String html="";
        StringBuilder sb= new StringBuilder();
        HashMap map= (HashMap) response3D.getRequestMap();
        sb.append("<form name=\"launch\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\" >");
        for(Object key : map.keySet()){
            sb.append("<input type=\"hidden\" name=\""+key.toString()+"\" value=\""+map.get(key.toString())+"\" >");
        }
        sb.append("</form>");
        sb.append("<script language=\"javascript\"> document.launch.submit(); </script>");
        html=sb.toString();
        transactionLogger.error("WLUtils Form -----"+html);
        return html;
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside WLPaymentProcess  process---" + response3D.getUrlFor3DRedirect());
        if (response3D!=null)
        {
            HashMap hashMap = (HashMap) response3D.getRequestMap();
            for (Object key : hashMap.keySet())
            {
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName(key.toString());
                asyncParameterVO.setValue((String) hashMap.get(key.toString()));
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
            directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
        }
    }


}
