package com.directi.pg.core.paymentgateway;

import com.directi.pg.SystemError;
import com.directi.pg.core.handler.AbstractKitHandler;

/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * Date: Mar 1, 2007
 * Time: 3:24:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class SBMPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "sbm";

    public SBMPaymentGateway(String accountId) throws SystemError
    {
        this.accountId = accountId;
        gatewayHandler = AbstractKitHandler.getHandler(GATEWAY_TYPE);
    }

    public String getMaxWaitDays()
    {
        return "3.5";
    }
}
