package com.payment.clearsettle;

import com.directi.pg.TransactionLogger;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Admin on 24-Jul-18.
 */
public class ClearSettleHPPPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(ClearSettleHPPPaymentProcess.class.getName());
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getRedirectUrl());
        String form = "<form name=\"launch3D\" method=\"GET\" action=\"" + response3D.getRedirectUrl() + "\">" +
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }
    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        return directKitResponseVO;
    }
}
