package com.payment.TWDTaiwan;

import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.*;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import java.util.Iterator;
import java.util.ResourceBundle;

/**
 * Created by Admin on 12/23/2020.
 */
public class TWDTaiwanPaymentProccess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(TWDTaiwanPaymentProccess.class.getName());
    private  static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.TWDTaiwan");
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        String form="<form name=\"paymentForm\" action=\""+response3D.getUrlFor3DRedirect()+"\" method=\"post\" target=\"_target\">";
        try
        {
            JSONObject jsonObject = new JSONObject(response3D.getPaReq());
            Iterator i = jsonObject.keys();
            while (i.hasNext())
            {
                String key= (String) i.next();
                String value=jsonObject.getString(key);
                form+="<input type=\"hidden\" name=\""+key+"\" value=\""+value+"\">";
            }
        }catch (JSONException e)
        {
            transactionLogger.error("JSONEXcepton--->",e);
        }
        form+="</form><script>document.paymentForm.submit();</script>";
        return form;
    }

    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        String form="<form name=\"paymentForm\" action=\""+response3D.getUrlFor3DRedirect()+"\" method=\"post\">";
        try
        {
            JSONObject jsonObject = new JSONObject(response3D.getPaReq());
            Iterator i = jsonObject.keys();
            while (i.hasNext())
            {
                String key= (String) i.next();
                String value=jsonObject.getString(key);
                form+="<input type=\"hidden\" name=\""+key+"\" value=\""+value+"\">";
            }
        }catch (JSONException e)
        {
            transactionLogger.error("JSONEXcepton--->",e);
        }
        form+="</form><script>document.paymentForm.submit();</script>";
        return form;
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        try
        {
            AsyncParameterVO asyncParameterVO=null;
            directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
            JSONObject jsonObject = new JSONObject(response3D.getPaReq());
            Iterator i=jsonObject.keys();
            while (i.hasNext())
            {
                String key= (String) i.next();
                String value=jsonObject.getString(key);
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName(key);
                asyncParameterVO.setValue(value);
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONEXcepton--->",e);
        }
    }
    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("inside Process --->"+directKitResponseVO.getBankRedirectionUrl());
        try
        {
            AsyncParameterVO asyncParameterVO=null;
            directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
            JSONObject jsonObject = new JSONObject(directKitResponseVO.getPaReq());
            Iterator i=jsonObject.keys();
            while (i.hasNext())
            {
                String key= (String) i.next();
                String value=jsonObject.getString(key);
                asyncParameterVO = new AsyncParameterVO();
                asyncParameterVO.setName(key);
                asyncParameterVO.setValue(value);
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONEXcepton--->",e);
        }

        return directKitResponseVO;
    }
}
