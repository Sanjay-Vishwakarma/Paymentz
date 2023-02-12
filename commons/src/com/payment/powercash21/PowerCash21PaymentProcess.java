package com.payment.powercash21;

import com.directi.pg.TransactionLogger;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZPayoutRequest;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Admin on 1/10/2022.
 */
public class PowerCash21PaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger=new TransactionLogger(PowerCash21PaymentProcess.class.getName());

    public String get3DConfirmationForm(String trackingid, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("inside  PowerCash21PaymentProcess Form---");

        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" ;
        form+="<input type=\"hidden\" name=\"TermUrl\"  value=\"\">";
        form+="</form><script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        transactionLogger.error("PowerCash21PaymentProcess Form --- "+form.toString());
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
        transactionLogger.debug("inside  PowerCash21PaymentProcess --- "+response3D.getUrlFor3DRedirect());



        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }

    public void setPayoutVOParamsextension(CommRequestVO commRequestVO,PZPayoutRequest payoutRequest) throws PZDBViolationException{

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
}
