package com.payment.zotapaygateway;

import com.directi.pg.TransactionLogger;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Balaji on 31-Jan-20.
 */
public class ZotaPayProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger=new TransactionLogger(ZotaPayProcess.class.getName());
    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.debug("inside ZotaPayProcess  ---==="+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        return directKitResponseVO;
    }
}
