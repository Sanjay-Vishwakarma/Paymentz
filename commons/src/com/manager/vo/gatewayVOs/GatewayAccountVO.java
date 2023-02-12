package com.manager.vo.gatewayVOs;

import com.directi.pg.core.GatewayAccount;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 9/1/14
 * Time: 12:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class GatewayAccountVO
{
    String accountId;
    GatewayAccount gatewayAccount;

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public GatewayAccount getGatewayAccount()
    {
        return gatewayAccount;
    }

    public void setGatewayAccount(GatewayAccount gatewayAccount)
    {
        this.gatewayAccount = gatewayAccount;
    }
}
