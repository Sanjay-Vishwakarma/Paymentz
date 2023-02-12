package com.payment.Ecommpay;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZInquiryRequest;
import com.payment.request.PZPayoutRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Sagar on 6/19/2020.
 */
public class EcommpayPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger =new TransactionLogger(EcommpayPaymentProcess.class.getName());
    public String get3DConfirmationFormVT(String trackingId,String ctoken,Comm3DResponseVO response3D)
    {
        Functions functions=new Functions();
        transactionLogger.error("3d page displayed....." +response3D.getUrlFor3DRedirect());
        String target ="";
        StringBuffer form=new StringBuffer("<form name=\"launch3D\" method=\"post\" action=\""+response3D.getUrlFor3DRedirect()+"\" "+target+">");
        if(functions.isValueNull(response3D.getPaReq()))
        {
            form.append("<input type=\"hidden\" name=\"PaReq\" value=\"" + response3D.getPaReq() + "\">" +
                    "<input type=\"hidden\" name=\"TermUrl\" value=\"" + response3D.getTerURL() + "\">" +
                    "<input type=\"hidden\" name=\"MD\" value=\"" + response3D.getMd() + "\">");
        }
        form.append("</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>");
        return form.toString();
    }

    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        Functions functions=new Functions();
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String target = "target=\"\"";

        StringBuffer form=new StringBuffer("<form name=\"launch3D\" method=\"post\" action=\""+response3D.getUrlFor3DRedirect()+"\" "+target+">");
            if(functions.isValueNull(response3D.getPaReq()))
            {
                form.append("<input type=\"hidden\" name=\"PaReq\" value=\"" + response3D.getPaReq() + "\">" +
                        "<input type=\"hidden\" name=\"TermUrl\" value=\"" + response3D.getTerURL() + "\">" +
                        "<input type=\"hidden\" name=\"MD\" value=\"" + response3D.getMd() + "\">");
            }
                form.append("</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>");
        transactionLogger.error("3d form---->" + trackingId + " ---" + form);
        transactionLogger.error("PaReq---->" + response3D.getPaReq());
        transactionLogger.error("TermUrl---->" +response3D.getTerURL());
        transactionLogger.error("MD---->"+response3D.getMd());
        return form.toString();
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        Functions functions=new Functions();
        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());

        if(functions.isValueNull(response3D.getPaReq()))
        {
            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("PaReq");
            asyncParameterVO.setValue(response3D.getPaReq());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
        }
        if(functions.isValueNull(response3D.getTerURL()))
        {
            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("TermUrl");
            asyncParameterVO.setValue(response3D.getTerURL());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
        }
        if(functions.isValueNull(response3D.getMd()))
        {
            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("MD");
            asyncParameterVO.setValue(response3D.getMd());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
        }

       }
    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        CommAddressDetailsVO commAddressDetailsVO=requestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=requestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=requestVO.getCardDetailsVO();
        TransactionManager transactionManager=new TransactionManager();
        TransactionDetailsVO transactionDetailsVO=transactionManager.getTransDetailFromCommon(String.valueOf(payoutRequest.getTrackingId()));
        commAddressDetailsVO.setFirstname(transactionDetailsVO.getFirstName());
        commAddressDetailsVO.setLastname(transactionDetailsVO.getLastName());
        commAddressDetailsVO.setCountry(transactionDetailsVO.getCountry());
        commAddressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
        commAddressDetailsVO.setIp(payoutRequest.getIpAddress());
        commAddressDetailsVO.setCustomerid(transactionDetailsVO.getCustomerId());
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }
   }

