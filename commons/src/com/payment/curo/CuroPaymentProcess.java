package com.payment.curo;

import com.directi.pg.TransactionLogger;
import com.manager.vo.DirectKitResponseVO;
import com.payment.AbstractPaymentProcess;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Admin on 1/6/2020.
 */
public class CuroPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger=new TransactionLogger(CuroPaymentProcess.class.getName());
    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.debug("inside CuroPaymentProcess  ---==="+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        transactionLogger.debug("inside CuroPaymentProcess  ---==="+directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }
}
