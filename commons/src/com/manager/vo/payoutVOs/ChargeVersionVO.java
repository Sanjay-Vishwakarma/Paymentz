package com.manager.vo.payoutVOs;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 10/11/14
 * Time: 6:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChargeVersionVO
{
    int chargeversionId;
    int accounts_charges_mapping_id;

    double chargeValue;
    double agentCommision;
    double partnerCommision;

    String effectiveStartDate;
    String effectiveEndDate;



    public int getChargeversionId()
    {
        return chargeversionId;
    }

    public void setChargeversionId(int chargeversionId)
    {
        this.chargeversionId = chargeversionId;
    }

    public double getChargeValue()
    {
        return chargeValue;
    }

    public void setChargeValue(double chargeValue)
    {
        this.chargeValue = chargeValue;
    }

    public double getAgentCommision()
    {
        return agentCommision;
    }

    public void setAgentCommision(double agentCommision)
    {
        this.agentCommision = agentCommision;
    }

    public double getPartnerCommision()
    {
        return partnerCommision;
    }

    public void setPartnerCommision(double partnerCommision)
    {
        this.partnerCommision = partnerCommision;
    }

    public String getEffectiveStartDate()
    {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(String effectiveStartDate)
    {
        this.effectiveStartDate = effectiveStartDate;
    }

    public String getEffectiveEndDate()
    {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(String effectiveEndDate)
    {
        this.effectiveEndDate = effectiveEndDate;
    }

    public int getAccounts_charges_mapping_id()
    {
        return accounts_charges_mapping_id;
    }

    public void setAccounts_charges_mapping_id(int accounts_charges_mapping_id)
    {
        this.accounts_charges_mapping_id = accounts_charges_mapping_id;
    }
}
