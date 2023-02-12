package qikpay;

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
 * Created by Admin on 3/8/2021.
 */
public class QikPayPaymentProcess  extends CommonPaymentProcess
{
    private static TransactionLogger transactionlogger = new TransactionLogger(QikPayPaymentProcess.class.getName());

    public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D){
        transactionlogger.error("inside  QikPayPaymentProcess Form---");

        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" ;
        Iterator keys=response3D.getRequestMap().keySet().iterator();
        while (keys.hasNext())
        {
            String key= (String) keys.next();
            form+="<input type=\"hidden\" name=\""+key+"\"  value=\""+response3D.getRequestMap().get(key)+"\">";

        }
        form+="</form><script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        transactionlogger.error("BhartiPayPaymentProcess Form---"+form.toString());
        return form.toString();
    }

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionlogger.error("inside bhartipay Process --->"+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        transactionlogger.error("inside getBankRedirectionUrl Process -->"+directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }


    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D, CommonValidatorVO commonValidatorVO)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionlogger.debug("inside bd payment process---"+response3D.getUrlFor3DRedirect());

        Iterator keys = response3D.getRequestMap().keySet().iterator();
        String key ="";
        while (keys.hasNext())
        {
            key = (String)keys.next();
            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName(key);
            asyncParameterVO.setValue(response3D.getRequestMap().get(key));
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        }

        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }
    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        transactionlogger.error("inside payout vo params etension -- >");
        CommAddressDetailsVO commAddressDetailsVO           = requestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = requestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO                 = requestVO.getCardDetailsVO();
        TransactionManager transactionManager               = new TransactionManager();
        TransactionDetailsVO transactionDetailsVO           = transactionManager.getTransDetailFromCommon(String.valueOf(payoutRequest.getTrackingId()));

        commAddressDetailsVO.setStreet(transactionDetailsVO.getStreet());
        commAddressDetailsVO.setZipCode(transactionDetailsVO.getZip());
        commAddressDetailsVO.setCity(transactionDetailsVO.getCity());
        commAddressDetailsVO.setState(transactionDetailsVO.getState());
        commAddressDetailsVO.setCountry(transactionDetailsVO.getCountry());
        commAddressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
        //commTransactionDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
        commAddressDetailsVO.setPhone(transactionDetailsVO.getTelno());

        commTransactionDetailsVO.setCustomerBankAccountName(payoutRequest.getCustomerBankAccountName());
        commTransactionDetailsVO.setBankIfsc(payoutRequest.getBankIfsc());
        commTransactionDetailsVO.setBankAccountNo(payoutRequest.getBankAccountNo());
        commTransactionDetailsVO.setBankTransferType(payoutRequest.getBankTransferType());


        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        //requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }

}
