package com.payment.wealthpay;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZPayoutRequest;
import com.payment.validators.vo.CommonValidatorVO;

import java.util.Iterator;

/**
 * Created by admin on 26-Jul-21.
 */
public class WealthPayPaymentProcess  extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger=new TransactionLogger(WealthPayPaymentProcess.class.getName());

    public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D)
    {
        transactionLogger.error("inside  WealthPayPaymentProcess Form---");

        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" ;
        form+="<input type=\"hidden\" name=\"TermUrl\"  value=\"\">";
        form+="</form><script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        transactionLogger.error("WealthPayPaymentProcess Form --- "+form.toString());
        return form.toString();
    }

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("inside setNBResponseVO Process ---> "+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        transactionLogger.error("inside WealthPayPaymentProcess getBankRedirectionUrl Process --> "+directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }


    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D, CommonValidatorVO commonValidatorVO)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside  WealthPayPaymentProcess --- "+response3D.getUrlFor3DRedirect());



        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }
    public void setPayoutVOParamsextension(CommRequestVO commRequestVO,PZPayoutRequest payoutRequest) throws PZDBViolationException
    {

        transactionLogger.error("<=============INSIDE PAYOUT REQUESTVO PARAMS EXTENSION ==============>");

        WealthPayRequestVO wealthPayRequestVO= (WealthPayRequestVO) commRequestVO;
        CommAddressDetailsVO commAddressDetailsVO=wealthPayRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=wealthPayRequestVO.getTransDetailsVO();

        TransactionManager transactionManager=new TransactionManager();
        Functions functions=new Functions();
        TransactionDetailsVO transactionDetailsVO=transactionManager.getTransDetailFromCommon(String.valueOf(payoutRequest.getTrackingId()));

        if(functions.isValueNull(transactionDetailsVO.getIpAddress())){
        commAddressDetailsVO.setIp(transactionDetailsVO.getIpAddress());
        }
        commTransactionDetailsVO.setCustomerBankAccountName(payoutRequest.getCustomerBankAccountName());
        commTransactionDetailsVO.setBankIfsc(payoutRequest.getBankIfsc());
        commTransactionDetailsVO.setBankAccountNo(payoutRequest.getBankAccountNo());
        commTransactionDetailsVO.setBankTransferType(payoutRequest.getBankTransferType());
       // commTransactionDetailsVO.setCustomerBankCode(payoutRequest.getBankCode());
        wealthPayRequestVO.setBankCode(payoutRequest.getBankCode());
        wealthPayRequestVO.setBranchName(payoutRequest.getBranchName());
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);


    }
}
