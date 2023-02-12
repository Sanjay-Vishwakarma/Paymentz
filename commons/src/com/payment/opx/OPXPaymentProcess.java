package com.payment.opx;

import com.payment.common.core.CommonPaymentProcess;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 4/6/15
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class OPXPaymentProcess extends CommonPaymentProcess
{
    @Override
    public String getSpecificVirtualTerminalJSP()
    {
        return "opxspecificfields.jsp";    //To change body of overridden methods use File | Settings | File Templates.
    }
}
