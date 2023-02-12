package com.payment.payboutique;

import com.directi.pg.TransactionLogger;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Balaji on 02-Nov-19.
 */
public class PayBoutiquePaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(PayBoutiquePaymentProcess.class.getName());

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.debug("inside PayBoutiquePaymentProcess  "+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        transactionLogger.debug("inside PayBoutiquePaymentProcess "+directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }
}
