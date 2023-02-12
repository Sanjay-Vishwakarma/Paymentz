package com.directi.pg.core.paymentgateway;

import com.directi.pg.SystemError;
import com.directi.pg.core.handler.AbstractKitHandler;

/**
 * Created by IntelliJ IDEA.
 * User: Alpesh.s
 * Date: Feb 27, 2011
 * Time: 3:24:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class SBMVBVPaymentGateway extends SBMPaymentGateway
{
    //public static final String GATEWAY_TYPE = "sbmvbv";

    public SBMVBVPaymentGateway(String accountId) throws SystemError
    {
        super(accountId);
    }

    public String getMaxWaitDays()
    {
        return "3.5";
    }
}
