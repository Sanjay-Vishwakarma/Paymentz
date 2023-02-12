package com.manager.vo.payoutVOs;

import com.manager.vo.CommissionVO;

/**
 * Created by Naushad on 11/26/2016.
 */
public class WLPartnerCommissionVO extends CommissionVO
{
    String pgTypeId;
    String creationOn;
    String partnerId;
    String partnerName;
    String gateway;
    String chargeName;
    String currency;
    String isActive;
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

    public String getIsActive()
    {
        return isActive;
    }

    public void setIsActive(String isActive)
    {
        this.isActive = isActive;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getPartnerName()
    {
        return partnerName;
    }
    public void setPartnerName(String partnerName)
    {
        this.partnerName = partnerName;
    }
    public String getGateway()
    {
        return gateway;
    }
    public void setGateway(String gateway)
    {
        this.gateway = gateway;
    }
    public String getPartnerId()
    {
        return partnerId;
    }
    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }
    public String getCreationOn()
    {
        return creationOn;
    }
    public void setCreationOn(String creationOn)
    {
        this.creationOn = creationOn;
    }
    public String getChargeName()
    {
        return chargeName;
    }
    public void setChargeName(String chargeName)
    {
        this.chargeName = chargeName;
    }
    public String getPgTypeId()
    {
        return pgTypeId;
    }
    public void setPgTypeId(String pgTypeId)
    {
        this.pgTypeId = pgTypeId;
    }
}

