package com.payment.iMerchantPay;

import com.payment.common.core.CommonPaymentProcess;

/**
 * Created by Admin on 7/6/18.
 */
public class iMerchantPaymentProcess extends CommonPaymentProcess
{
    public String getSpecificVirtualTerminalJSP()
    {
        return "payforasiaspecificfields.jsp";
    }
}
