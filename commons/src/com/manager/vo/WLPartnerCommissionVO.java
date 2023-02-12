package com.manager.vo;

/**
 * Created by Naushad on 11/26/2016.
 */
public class WLPartnerCommissionVO extends CommissionVO
{
    String pgtypeId;
    String creationOn;
    String partnerId;

    String actionExecutorId;
    String actionExecutorName;

    public String getPartnerId()
    {
        return partnerId;
    }
    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }
    public String getPgtypeId()
    {
        return pgtypeId;
    }
    public void setPgtypeId(String pgtypeId)
    {
        this.pgtypeId = pgtypeId;
    }
    public String getCreationOn()
    {
        return creationOn;
    }
    public void setCreationOn(String creationOn)
    {
        this.creationOn = creationOn;
    }

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
}
