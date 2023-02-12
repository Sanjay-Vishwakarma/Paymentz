package com.directi.pg.core.lpb;

import com.directi.pg.PzEncryptor;
import com.directi.pg.TransactionLogger;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;

/**
 * Created by Admin on 4/11/2018.
 */
public class LpbPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(LpbPaymentProcess.class.getName());

    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());

        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\">" +
                "<input type=\"hidden\" name=\"PaReq\" value=\"" + response3D.getPaReq() + "\">" +
                "<input type=\"hidden\" name=\"TermUrl\" value=\"" + response3D.getTerURL() + "\">" +
                "<input type=\"hidden\" name=\"MD\" value=\"" + response3D.getMd() + "\">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }

    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());

        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\">" +
                "<input type=\"hidden\" name=\"PaReq\" value=\"" + response3D.getPaReq() + "\">" +
                "<input type=\"hidden\" name=\"TermUrl\" value=\"" + response3D.getTerURL() + "\">" +
                "<input type=\"hidden\" name=\"MD\" value=\"" + response3D.getMd() + "\">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside bd payment process---"+response3D.getUrlFor3DRedirect());

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

        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }


    public void setLpbRequestVO(CommRequestVO requestVO, String trackingId, String PaRes) throws PZDBViolationException
    {
        LpbRequestVO lpbRequestVO = (LpbRequestVO) requestVO;
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO = new CommMerchantVO();
        TransactionManager transactionManager = new TransactionManager();
        TransactionDetailsVO transactionVO = transactionManager.getTransDetailFromCommon(trackingId);

        commCardDetailsVO.setCardHolderName(transactionVO.getFirstName() + " " + transactionVO.getLastName());
        commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(transactionVO.getCcnum()));
        commCardDetailsVO.setCardType(transactionVO.getCardtype());
        String expdate = transactionVO.getExpdate();
        if (expdate != null)
        {
            String eDate = PzEncryptor.decryptExpiryDate(expdate);
            String expMonth = eDate.split("\\/")[0];
            String expYear = eDate.split("\\/")[1];
            commCardDetailsVO.setExpMonth(expMonth);
            commCardDetailsVO.setExpYear(expYear);
        }
        lpbRequestVO.setPaRes(PaRes);
        commTransactionDetailsVO.setCurrency(transactionVO.getCurrency());
        commTransactionDetailsVO.setAmount(transactionVO.getAmount());
        commTransactionDetailsVO.setOrderId(transactionVO.getDescription());
        commTransactionDetailsVO.setOrderDesc(transactionVO.getOrderDescription());
        commTransactionDetailsVO.setPrevTransactionStatus(transactionVO.getStatus());

        commTransactionDetailsVO.setToId(transactionVO.getToid());
        commTransactionDetailsVO.setPaymentType(transactionVO.getPaymodeId());
        commTransactionDetailsVO.setCardType(transactionVO.getCardTypeId());
        commTransactionDetailsVO.setRedirectUrl(transactionVO.getRedirectURL());
        commTransactionDetailsVO.setPreviousTransactionId(transactionVO.getPaymentId());
        commTransactionDetailsVO.setCustomerId(transactionVO.getCustomerId());


        commAddressDetailsVO.setFirstname(transactionVO.getFirstName());
        commAddressDetailsVO.setLastname(transactionVO.getLastName());
        commAddressDetailsVO.setCountry(transactionVO.getCountry());
        commAddressDetailsVO.setCity(transactionVO.getCity());
        commAddressDetailsVO.setEmail(transactionVO.getEmailaddr());
        commAddressDetailsVO.setIp(transactionVO.getIpAddress());
        commAddressDetailsVO.setPhone(transactionVO.getTelno());
        commAddressDetailsVO.setZipCode(transactionVO.getZip());
        commAddressDetailsVO.setState(transactionVO.getState());
        commAddressDetailsVO.setStreet(transactionVO.getStreet());
        commAddressDetailsVO.setTmpl_amount(transactionVO.getTemplateamount());
        commAddressDetailsVO.setTmpl_currency(transactionVO.getTemplatecurrency());

        commMerchantVO.setAccountId(transactionVO.getAccountId());
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
        requestVO.setCommMerchantVO(commMerchantVO);

    }
}
