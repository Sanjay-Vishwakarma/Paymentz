package com.payment.opx;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 4/6/15
 * Time: 9:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class OPXAccounts
{
    public String getServiceKey()
    {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey)
    {
        this.serviceKey = serviceKey;
    }

    public String getRoutingKey()
    {
        return RoutingKey;
    }

    public void setRoutingKey(String routingKey)
    {
        RoutingKey = routingKey;
    }

    private String serviceKey;
    private String RoutingKey;

    public String getAccountid()
    {
        return accountid;
    }

    public void setAccountid(String accountid)
    {
        this.accountid = accountid;
    }

    private String accountid;
    public OPXAccounts(ResultSet rs) throws SQLException
    {
        serviceKey = rs.getString("servicekey");
        RoutingKey = rs.getString("routingkey");
        accountid = rs.getString("accountid");
    }
}
