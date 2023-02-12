package com.payment.nexi;

import com.directi.pg.Logger;
import com.directi.pg.PaymentzEncryptor;
import com.directi.pg.TransactionLogger;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Vivek on 5/24/2019.
 */
public class NexiPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(NexiPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(NexiPaymentProcess.class.getName());

    @Override
    public String get3DConfirmationForm(String trackingid, String ctoken, Comm3DResponseVO response3D){

        String form = response3D.getUrlFor3DRedirect();

        return form.toString();
    }
    @Override
    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        log.debug("inside bd payment process---"+response3D.getUrlFor3DRedirect());

        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }
    public void setNexiRequestVO(CommRequestVO requestVO,String trackingId,String amount,String currency,String xpayNonce) throws PZDBViolationException
    {
        transactionLogger.error("Inside ICard Payment Process Set RequestVO-----");
        CommAddressDetailsVO commAddressDetailsVO=new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO=new CommCardDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();
        commTransactionDetailsVO.setAmount(amount);
        commTransactionDetailsVO.setCurrency(currency);
        commTransactionDetailsVO.setPreviousTransactionId(xpayNonce);
        //commMerchantVO.setAccountId(accountid);
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
        requestVO.setCommMerchantVO(commMerchantVO);

        transactionLogger.error("dbStatus:::::::"+commTransactionDetailsVO.getPrevTransactionStatus());
    }

}
