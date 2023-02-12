package com.payment.acqra;

import com.payment.AbstractPaymentProcess;
import com.payment.common.core.CommonPaymentProcess;

/**
 * Created by Admin on 12/20/2019.
 */
public class AcqraPaymentProcess extends CommonPaymentProcess
{
    public String getSpecificVirtualTerminalJSP()
    {
        return "payforasiaspecificfields.jsp";
    }
}
