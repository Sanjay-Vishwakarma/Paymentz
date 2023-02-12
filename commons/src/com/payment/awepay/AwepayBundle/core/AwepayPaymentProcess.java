package com.payment.awepay.AwepayBundle.core;

import com.directi.pg.TransactionLogger;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;

/**
 * Created by Admin on 11/29/2018.
 */
public class AwepayPaymentProcess extends CommonPaymentProcess
{
    TransactionLogger transactionLogger= new TransactionLogger(AwepayPaymentProcess.class.getName());

    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String form = response3D.getUrlFor3DRedirect();
        return form;
    }
}
