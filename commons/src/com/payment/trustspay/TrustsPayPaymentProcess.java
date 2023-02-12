package com.payment.trustspay;

import com.payment.common.core.CommonPaymentProcess;

/**
 * Created by admin on 5/23/2016.
 */
public class TrustsPayPaymentProcess extends CommonPaymentProcess
{
    public String getSpecificVirtualTerminalJSP()
    {
        return "payforasiaspecificfields.jsp";    //To change body of overridden methods use File | Settings | File Templates.
    }
}
