package com.payment.wonderland.core;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.CommonPaymentProcess;

/**
 * Created by ThinkPadT410 on 9/2/2016.
 */
public class WonderlandPayPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(WonderlandPayPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(WonderlandPayPaymentProcess.class.getName());

    public String getSpecificVirtualTerminalJSP()
    {
        return "payforasiaspecificfields.jsp";
    }

}
