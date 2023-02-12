package com.payment.repropay;

import com.directi.pg.*;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZCancelRequest;
import com.payment.request.PZCaptureRequest;
import com.payment.request.PZRefundRequest;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Admin on 12/3/2021.
 */
public class ReproPayPaymentProcess extends CommonPaymentProcess
{
    private TransactionLogger transactionLogger= new TransactionLogger(ReproPayPaymentProcess.class.getName());
    public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D){
        transactionLogger.error("inside  ReproPayPaymentProcess Form---");

        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" ;
        Iterator keys=response3D.getRequestMap().keySet().iterator();
        while (keys.hasNext())
        {
            String key= (String) keys.next();
            form+="<input type=\"hidden\" name=\""+key+"\"  value=\""+response3D.getRequestMap().get(key)+"\">";

        }
        form+="</form><script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        transactionLogger.error("ReproPayPaymentProcess Form---" + form.toString());
        return form.toString();
    }
    public void setRepropayRequestVO(CommRequestVO requestVO, String trackingId) throws PZDBViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO = new CommMerchantVO();
        TransactionManager transactionManager = new TransactionManager();
        TransactionDetailsVO transactionVO = transactionManager.getTransDetailFromCommon(trackingId);
        commTransactionDetailsVO.setPrevTransactionStatus(transactionVO.getStatus());

        commTransactionDetailsVO.setCurrency(transactionVO.getCurrency());
        commTransactionDetailsVO.setAmount(transactionVO.getAmount());
        commTransactionDetailsVO.setOrderId(transactionVO.getDescription());
        commTransactionDetailsVO.setOrderDesc(transactionVO.getOrderDescription());
        commTransactionDetailsVO.setPaymentId(transactionVO.getPaymentId());

        commTransactionDetailsVO.setToId(transactionVO.getToid());
        commTransactionDetailsVO.setPaymentType(transactionVO.getPaymodeId());
        commTransactionDetailsVO.setCardType(transactionVO.getCardTypeId());
        commTransactionDetailsVO.setRedirectUrl(transactionVO.getRedirectURL());
        commAddressDetailsVO.setCountry(transactionVO.getCountry());
        commAddressDetailsVO.setFirstname(transactionVO.getFirstName());
        commAddressDetailsVO.setLastname(transactionVO.getLastName());
        commAddressDetailsVO.setCustomerid(transactionVO.getCustomerId());

        commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(transactionVO.getCcnum()));
        commAddressDetailsVO.setTmpl_amount(transactionVO.getTemplateamount());
        commAddressDetailsVO.setTmpl_currency(transactionVO.getTemplatecurrency());

        commMerchantVO.setAccountId(transactionVO.getAccountId());
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
        requestVO.setCommMerchantVO(commMerchantVO);
    }
    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO)
    {
        transactionLogger.error("inside Repropay Process --->"+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        transactionLogger.error("inside Repropay getBankRedirectionUrl Process -->"+directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }


    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside  Repropay payment process---"+response3D.getUrlFor3DRedirect());

        Iterator keys   = response3D.getRequestMap().keySet().iterator();
        String key      ="";
        while (keys.hasNext())
        {
            key                 = (String)keys.next();
            asyncParameterVO    = new AsyncParameterVO();
            asyncParameterVO.setName(key);
            asyncParameterVO.setValue(response3D.getRequestMap().get(key));
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
        }
        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }
  
}

