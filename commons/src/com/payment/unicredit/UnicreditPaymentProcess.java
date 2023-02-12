package com.payment.unicredit;

import com.directi.pg.Logger;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Trupti on 2/20/2018.
 */
public class UnicreditPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(UnicreditPaymentProcess.class.getName());
    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        log.debug("inside idealprocess===");
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        return directKitResponseVO;
    }

}
