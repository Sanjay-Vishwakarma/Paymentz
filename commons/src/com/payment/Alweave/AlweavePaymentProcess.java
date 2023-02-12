package com.payment.Alweave;

import com.directi.pg.TransactionLogger;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Admin on 11/2/2020.
 */
public class AlweavePaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionlogger = new TransactionLogger(AlweavePaymentProcess.class.getName());
    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionlogger.error("inside Process --->"+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        transactionlogger.error("inside getBankRedirectionUrl Process -->"+directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }
}
