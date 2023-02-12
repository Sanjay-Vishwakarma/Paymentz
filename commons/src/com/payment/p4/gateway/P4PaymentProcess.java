package com.payment.p4.gateway;

import com.directi.pg.Logger;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Admin on 23/10/2015.
 */
public class P4PaymentProcess extends CommonPaymentProcess
{
    private static Logger logger = new Logger(P4PaymentProcess.class.getName());

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        logger.debug("inside p4process===");
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        return directKitResponseVO;
    }
}
