package com.payment.sofort;

import com.directi.pg.ActionEntry;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 3/2/15
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class SofortPaymentProcess  extends CommonPaymentProcess
{
    private static Logger log = new Logger(SofortPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(SofortPaymentProcess.class.getName());
    private static ActionEntry actionEntry=new ActionEntry();

    public int actionEntryExtension
            (
                    int newDetailId, String
                    trackingId, String
                    amount, String
                    action, String
                    status, CommResponseVO
                    responseVO, CommRequestVO
                    commRequestVO)
    {

        int results = 0;

        try
        {

            results = actionEntry.detailActionEntryForSofort(newDetailId,trackingId,amount,action,status,responseVO,commRequestVO);

        }
        catch (PZDBViolationException dve)
        {
            PZExceptionHandler.handleDBCVEException(dve, null, "actionEntryExtension");
        }



        return results;
    }

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        log.debug("inside sofortprocess===");
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        return directKitResponseVO;
    }


}
