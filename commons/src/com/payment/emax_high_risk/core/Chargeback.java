package com.payment.emax_high_risk.core;

/**
 * Created with IntelliJ IDEA.
 * User: abc
 * Date: 2/6/15
 * Time: 5:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class Chargeback
{
    public Chargeback chargeback;
    public String message;
    public String ref_id;
    public String gateway_id;
    public String status;

    public void setChargeback(Chargeback chargeback)
    {
        this.chargeback = chargeback;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {

        return status;
    }

    public void setGateway_id(String gateway_id)
    {

        this.gateway_id = gateway_id;
    }

    public String getGateway_id()
    {

        return gateway_id;
    }

    public void setRef_id(String ref_id)
    {

        this.ref_id = ref_id;
    }

    public String getRef_id()
    {

        return ref_id;
    }

    public void setMessage(String message)
    {

        this.message = message;
    }

    public String getMessage()
    {

        return message;
    }

    public Chargeback getChargeback()
    {

        return chargeback;
    }
}
