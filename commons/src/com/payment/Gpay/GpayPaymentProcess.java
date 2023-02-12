package com.payment.Gpay;

import com.directi.pg.*;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.PZCancelRequest;
import com.payment.request.PZCaptureRequest;
import com.payment.request.PZPayoutRequest;
import com.payment.request.PZRefundRequest;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created by Admin on 2/22/2022.
 */
public class GpayPaymentProcess extends CommonPaymentProcess
{

    private static TransactionLogger transactionLogger=new TransactionLogger(GpayPaymentProcess.class.getName());

    public String get3DConfirmationForm(String trackingid, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("inside  GpayPaymentProcess Form---");

        /*String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" ;
        *//*Iterator keys=response3D.getRequestMap().keySet().iterator();
        while (keys.hasNext())
        {
            String key= (String) keys.next();
            form+="<input type=\"hidden\" name=\""+key+"\"  value=\""+response3D.getRequestMap().get(key)+"\">";

        }*//*
        form+="</form><script language=\"javascript\">document.creditcard_checkout.submit();</script>";*/


        transactionLogger.error("Gpay 3d page displayed....." + response3D.getUrlFor3DRedirect());

        String form = "<form name=\"launch3D\" method=\"GET\" action=\"" + response3D.getUrlFor3DRedirect() + "\">";
        form += "</form><script language=\"javascript\">window.location=\"" + response3D.getUrlFor3DRedirect() + "\";</script>";


        transactionLogger.error("form for "+trackingid+"---->"+ ">>>>>>>>> "+form);
        transactionLogger.error("GpayPaymentProcess Form --- "+form.toString());
        return form.toString();
    }

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("inside setNBResponseVO Process ---> "+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        transactionLogger.error("inside GpayPaymentProcess getBankRedirectionUrl Process --> "+directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }



    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside  GpayPaymentProcess --- "+response3D.getUrlFor3DRedirect());
        directKitResponseVO.setMethod("GET");


        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }

    public void setPayoutVOParamsextension(CommRequestVO commRequestVO,PZPayoutRequest payoutRequest) throws PZDBViolationException
    {

        transactionLogger.error("<=============INSIDE PAYOUT REQUESTVO PARAMS EXTENSION ==============>");
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        TransactionManager transactionManager=new TransactionManager();
        TransactionDetailsVO transactionDetailsVO=transactionManager.getTransDetailFromCommon(String.valueOf(payoutRequest.getTrackingId()));

        commAddressDetailsVO.setStreet(transactionDetailsVO.getStreet());
        commAddressDetailsVO.setCity(transactionDetailsVO.getCity());
        commAddressDetailsVO.setState(transactionDetailsVO.getState());
        commAddressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
        commAddressDetailsVO.setCountry(transactionDetailsVO.getCountry());
        commAddressDetailsVO.setPhone(transactionDetailsVO.getTelno());
        commAddressDetailsVO.setZipCode(transactionDetailsVO.getZip());
        commTransactionDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
        commTransactionDetailsVO.setCustomerBankAccountName(payoutRequest.getCustomerBankAccountName());
        commTransactionDetailsVO.setBankIfsc(payoutRequest.getBankIfsc());
        commTransactionDetailsVO.setBankAccountNo(payoutRequest.getBankAccountNo());
        commTransactionDetailsVO.setBankTransferType(payoutRequest.getBankTransferType());

        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);


    }
    public void setCancelVOParamsExtension(CommRequestVO requestVO, PZCancelRequest cancelRequest) throws PZDBViolationException
    {
        CommAddressDetailsVO commAddressDetailsVO=null;
        CommTransactionDetailsVO commTransactionDetailsVO=null;



        if(requestVO.getTransDetailsVO()!=null){
            commTransactionDetailsVO=requestVO.getTransDetailsVO();
        }else{
            commTransactionDetailsVO=new CommTransactionDetailsVO();
        }
        if(requestVO.getAddressDetailsVO()!=null){
            commAddressDetailsVO=requestVO.getAddressDetailsVO();
        }else{
            commAddressDetailsVO=new CommAddressDetailsVO();

        }

        TransactionManager transactionManager=new TransactionManager();
        TransactionDetailsVO transactionDetailsVO=transactionManager.getTransDetailFromCommon(String.valueOf(cancelRequest.getTrackingId()));


        commAddressDetailsVO.setStreet(transactionDetailsVO.getStreet());
        commAddressDetailsVO.setCity(transactionDetailsVO.getCity());
        commAddressDetailsVO.setState(transactionDetailsVO.getState());
        commAddressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
        commAddressDetailsVO.setCountry(transactionDetailsVO.getCountry());
        commAddressDetailsVO.setPhone(transactionDetailsVO.getTelno());
        commAddressDetailsVO.setZipCode(transactionDetailsVO.getZip());
        commAddressDetailsVO.setFirstname(transactionDetailsVO.getFirstName());
        commAddressDetailsVO.setLastname(transactionDetailsVO.getLastName());
        commTransactionDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
        commTransactionDetailsVO.setMerchantOrderId(transactionDetailsVO.getOrderDescription());



        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);




    }
    public void setCaptureVOParamsExtension(CommRequestVO requestVO, PZCaptureRequest captureRequest) throws PZDBViolationException
    {
        CommAddressDetailsVO commAddressDetailsVO=null;
        CommTransactionDetailsVO commTransactionDetailsVO=null;



        if(requestVO.getTransDetailsVO()!=null){
            commTransactionDetailsVO=requestVO.getTransDetailsVO();
        }else{
            commTransactionDetailsVO=new CommTransactionDetailsVO();
        }
        if(requestVO.getAddressDetailsVO()!=null){
            commAddressDetailsVO=requestVO.getAddressDetailsVO();
        }else{
            commAddressDetailsVO=new CommAddressDetailsVO();

        }

        TransactionManager transactionManager=new TransactionManager();
        TransactionDetailsVO transactionDetailsVO=transactionManager.getTransDetailFromCommon(String.valueOf(captureRequest.getTrackingId()));


        commAddressDetailsVO.setStreet(transactionDetailsVO.getStreet());
        commAddressDetailsVO.setCity(transactionDetailsVO.getCity());
        commAddressDetailsVO.setState(transactionDetailsVO.getState());
        commAddressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
        commAddressDetailsVO.setCountry(transactionDetailsVO.getCountry());
        commAddressDetailsVO.setPhone(transactionDetailsVO.getTelno());
        commAddressDetailsVO.setZipCode(transactionDetailsVO.getZip());
        commAddressDetailsVO.setFirstname(transactionDetailsVO.getFirstName());
        commAddressDetailsVO.setLastname(transactionDetailsVO.getLastName());
        commTransactionDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
        commTransactionDetailsVO.setMerchantOrderId(transactionDetailsVO.getOrderDescription());

        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);

    }
    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        transactionLogger.error("Entered into setRefundVOParamsextension");
        CommAddressDetailsVO commAddressDetailsVO=null;
        CommTransactionDetailsVO commTransactionDetailsVO=null;



        if(requestVO.getTransDetailsVO()!=null){
            commTransactionDetailsVO=requestVO.getTransDetailsVO();
        }else{
            commTransactionDetailsVO=new CommTransactionDetailsVO();
        }
        if(requestVO.getAddressDetailsVO()!=null){
            commAddressDetailsVO=requestVO.getAddressDetailsVO();
        }else{
            commAddressDetailsVO=new CommAddressDetailsVO();

        }

        TransactionManager transactionManager=new TransactionManager();
        TransactionDetailsVO transactionDetailsVO=transactionManager.getTransDetailFromCommon(String.valueOf(refundRequest.getTrackingId()));


        commAddressDetailsVO.setStreet(transactionDetailsVO.getStreet());
        commAddressDetailsVO.setCity(transactionDetailsVO.getCity());
        commAddressDetailsVO.setState(transactionDetailsVO.getState());
        commAddressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
        commAddressDetailsVO.setCountry(transactionDetailsVO.getCountry());
        commAddressDetailsVO.setPhone(transactionDetailsVO.getTelno());
        commAddressDetailsVO.setZipCode(transactionDetailsVO.getZip());
        commAddressDetailsVO.setFirstname(transactionDetailsVO.getFirstName());
        commAddressDetailsVO.setLastname(transactionDetailsVO.getLastName());
        commTransactionDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
        commTransactionDetailsVO.setMerchantOrderId(transactionDetailsVO.getOrderDescription());

        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);

    }


}
