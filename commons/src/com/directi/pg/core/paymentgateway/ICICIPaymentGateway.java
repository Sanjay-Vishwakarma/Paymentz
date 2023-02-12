package com.directi.pg.core.paymentgateway;

import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.handler.AbstractKitHandler;

/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * Date: Mar 1, 2007
 * Time: 3:24:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class ICICIPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "icici";
    public static final String GATEWAY_TYPE_NEW = "icici_new";
    public static final String GATEWAY_TYPE_OLD = "icici_old";

    public ICICIPaymentGateway(String accountId) throws SystemError
    {
        this.accountId = accountId;
        String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        if (merchantId.equals("000")) // some way to get old merchant Id's and compare it
        {
            gatewayHandler = AbstractKitHandler.getHandler(GATEWAY_TYPE_OLD);
        }
        else
        {
            gatewayHandler = AbstractKitHandler.getHandler(GATEWAY_TYPE_NEW);
        }
    }


    public String getMaxWaitDays()
    {
        return "13";
    }
}
