package com.payment.billdesk;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Admin on 8/10/2017.
 */
public class BillDeskPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(BillDeskPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(BillDeskPaymentProcess.class.getName());

    //final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.PbsServlet");

    public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D){
        Functions functions=new Functions();
        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" +
                    "<input type=hidden name=msg id=msg value=\""+response3D.getPaReq()+"\">"+
                    "<input type=hidden name=cnumber id=cnumber value=\""+commonValidatorVO.getCardDetailsVO().getCardNum()+"\">"+
                    "<input type=hidden name=expmon id=expmon value=\""+commonValidatorVO.getCardDetailsVO().getExpMonth()+"\">"+
                    "<input type=hidden name=expyr id=expyr value=\"" +commonValidatorVO.getCardDetailsVO().getExpYear()+"\">"+
                    "<input type=hidden name=cvv2 id=cvv2 value=\""+commonValidatorVO.getCardDetailsVO().getcVV() +"\">"+
                    "<input type=hidden name=cardType id=cardType value=\"NA\">"+
                    "<input type=hidden name=cname2 id=cname2 value=\""+commonValidatorVO.getAddressDetailsVO().getFirstname()+" "+commonValidatorVO.getAddressDetailsVO().getLastname()+"\">"+
                "</form>" +
                "<script language=\"javascript\">document.creditcard_checkout.submit();</script>";
        String formLog = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" +
                    "<input type=hidden name=msg id=msg value=\""+response3D.getPaReq()+"\">"+
                    "<input type=hidden name=cnumber id=cnumber value=\""+functions.maskingPan(commonValidatorVO.getCardDetailsVO().getCardNum())+"\">"+
                    "<input type=hidden name=expmon id=expmon value=\""+functions.maskingNumber(commonValidatorVO.getCardDetailsVO().getExpMonth())+"\">"+
                    "<input type=hidden name=expyr id=expyr value=\"" +functions.maskingNumber(commonValidatorVO.getCardDetailsVO().getExpYear())+"\">"+
                    "<input type=hidden name=cvv2 id=cvv2 value=\""+functions.maskingNumber(commonValidatorVO.getCardDetailsVO().getcVV()) +"\">"+
                    "<input type=hidden name=cardType id=cardType value=\"NA\">"+
                    "<input type=hidden name=cname2 id=cname2 value=\""+commonValidatorVO.getAddressDetailsVO().getFirstname()+" "+commonValidatorVO.getAddressDetailsVO().getLastname()+"\">"+
                "</form>" +
                "<script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        transactionLogger.error("BillDeskPaymentProcess Form---"+formLog.toString());
        return form.toString();
    }
    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.debug("inside billdesk Process --->"+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        transactionLogger.debug("inside getBankRedirectionUrl Process -->"+directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D, CommonValidatorVO commonValidatorVO)
    {
        AsyncParameterVO asyncParameterVO = null;
        log.debug("inside bd payment process---"+response3D.getUrlFor3DRedirect());

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("msg");
        asyncParameterVO.setValue(response3D.getPaReq());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("cnumber");
        asyncParameterVO.setValue(commonValidatorVO.getCardDetailsVO().getCardNum());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("expmon");
        asyncParameterVO.setValue(commonValidatorVO.getCardDetailsVO().getExpMonth());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("expyr");
        asyncParameterVO.setValue(commonValidatorVO.getCardDetailsVO().getExpYear());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("cvv2");
        asyncParameterVO.setValue(commonValidatorVO.getCardDetailsVO().getcVV());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("cardType");
        asyncParameterVO.setValue("NA");
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("cname2");
        asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getFirstname()+" "+commonValidatorVO.getAddressDetailsVO().getLastname());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("creditcard_checkout");
        asyncParameterVO.setValue(response3D.getUrlFor3DRedirect());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }

}