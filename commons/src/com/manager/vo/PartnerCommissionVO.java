package com.manager.vo;



/**
 * Created by sandip on 10/7/2015.
 */
public class PartnerCommissionVO extends CommissionVO
{
    String partnerId;

    String actionExecutorId;
    String actionExecutorName;

    public String getActionExecutorId()
    {
        return actionExecutorId;
    }

    public void setActionExecutorId(String actionExecutorId)
    {
        this.actionExecutorId = actionExecutorId;
    }

    public String getActionExecutorName()
    {
        return actionExecutorName;
    }

    public void setActionExecutorName(String actionExecutorName)
    {
        this.actionExecutorName = actionExecutorName;
    }

    public String getPartnerId()
    {
        return partnerId;
    }
    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }
}
