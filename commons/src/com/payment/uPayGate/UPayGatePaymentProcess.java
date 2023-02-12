package com.payment.uPayGate;

import com.payment.common.core.CommonPaymentProcess;

/**
 * Created by Admin on 7/28/2017.
 */
public class UPayGatePaymentProcess extends CommonPaymentProcess
{
    public String getSpecificVirtualTerminalJSP()
    {
        return "upaygatespecificfields.jsp";    //To change body of overridden methods use File | Settings | File Templates.
    }
}
