package com.payment.cybersource;

import com.directi.pg.PzEncryptor;
import com.directi.pg.TransactionLogger;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZPayoutRequest;

/**
 * Created by Admin on 12/5/18.
 */
public class CyberSourcePaymentProcess extends CommonPaymentProcess
{
    TransactionLogger transactionLogger= new TransactionLogger(CyberSourcePaymentProcess.class.getName());
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String target = "target=_blank";
        String form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\""+target+">"+
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        return form;
    }

    @Override
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());

        String form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\">"+
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        return form;
    }


    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside CyberSourcePaymentProcess Rest 3d payment process---"+response3D.getUrlFor3DRedirect());
        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("launch3D");
        asyncParameterVO.setValue(response3D.getUrlFor3DRedirect());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("PaReq");
        asyncParameterVO.setValue(response3D.getPaReq());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("TermUrl");
        asyncParameterVO.setValue(response3D.getTerURL());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("MD");
        asyncParameterVO.setValue(response3D.getMd());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
    }

    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        transactionLogger.error(":::::enter into setPayoutVOParamsextension:::::");
        TransactionManager transactionManager = new TransactionManager();
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();

        TransactionDetailsVO transVo = transactionManager.getTransDetailFromCommon(String.valueOf(payoutRequest.getTrackingId()));
        if (transVo != null)
        {
            commAddressDetailsVO.setFirstname(transVo.getFirstName());
            commAddressDetailsVO.setLastname(transVo.getLastName());
            commAddressDetailsVO.setCity(transVo.getCity());
            commAddressDetailsVO.setCountry(transVo.getCountry());
            commAddressDetailsVO.setEmail(transVo.getEmailaddr());
            commAddressDetailsVO.setPhone(transVo.getTelno());
            commAddressDetailsVO.setCardHolderIpAddress(transVo.getIpAddress());
            commAddressDetailsVO.setZipCode(transVo.getZip());
            commAddressDetailsVO.setState(transVo.getState());
            commAddressDetailsVO.setStreet(transVo.getStreet());
            commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(transVo.getCcnum()));
            String expdate = transVo.getExpdate();
            if (expdate != null)
            {
                String eDate = PzEncryptor.decryptExpiryDate(expdate);
                String expMonth = eDate.split("\\/")[0];
                String expYear = eDate.split("\\/")[1];
                commCardDetailsVO.setExpMonth(expMonth);
                commCardDetailsVO.setExpYear(expYear);
            }
            commCardDetailsVO.setCardType(transVo.getCardtype());
            commTransactionDetailsVO.setCardType(transVo.getCardTypeId());
            commTransactionDetailsVO.setAmount(payoutRequest.getPayoutAmount());
            commTransactionDetailsVO.setCurrency(transVo.getCurrency());

            requestVO.setCardDetailsVO(commCardDetailsVO);
            requestVO.setAddressDetailsVO(commAddressDetailsVO);
            requestVO.setTransDetailsVO(commTransactionDetailsVO);

        }
    }
}
