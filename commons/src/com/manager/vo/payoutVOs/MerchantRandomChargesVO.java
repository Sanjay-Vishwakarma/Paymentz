package com.manager.vo.payoutVOs;

/**
 * Created by Kiran on 23/7/15.
 */
public class MerchantRandomChargesVO
{

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public String getTerminalId()
    {
        return terminalId;
    }

    public void setTerminalId(String terminalId)
    {
        this.terminalId = terminalId;
    }

    public String getChargeName()
    {
        return chargeName;
    }

    public void setChargeName(String chargeName)
    {
        this.chargeName = chargeName;
    }

    public Double getChargeRate()
    {
        return chargeRate;
    }

    public void setChargeRate(Double chargeRate)
    {
        this.chargeRate = chargeRate;
    }

    public int getChargeCounter()
    {
        return chargeCounter;
    }

    public void setChargeCounter(int chargeCounter)
    {
        this.chargeCounter = chargeCounter;
    }

    public Double getChargeAmount()
    {
        return chargeAmount;
    }

    public void setChargeAmount(Double chargeAmount)
    {
        this.chargeAmount = chargeAmount;
    }

    public Double getChargeValue()
    {
        return chargeValue;
    }

    public void setChargeValue(Double chargeValue)
    {
        this.chargeValue = chargeValue;
    }

    public String getChargeRemark()
    {
        return chargeRemark;
    }

    public void setChargeRemark(String chargeRemark)
    {
        this.chargeRemark = chargeRemark;
    }

    public String getChargeValueType()
    {
        return chargeValueType;
    }

    public void setChargeValueType(String chargeValueType)
    {
        this.chargeValueType = chargeValueType;
    }

    public String getMerchantRdmChargeId()
    {
        return merchantRdmChargeId;
    }

    public void setMerchantRdmChargeId(String merchantRdmChargeId)
    {
        this.merchantRdmChargeId = merchantRdmChargeId;
    }

    public String getBankWireId(){return bankWireId;}

    public void setBankWireId(String bankWireId){this.bankWireId = bankWireId;}

    public String getChargeType(){return chargeType;}

    public void setChargeType(String chargeType){this.chargeType = chargeType;}

    public String getAccountid()
    {
        return accountid;
    }

    public void setAccountid(String accountid)
    {
        this.accountid = accountid;
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

    public Double getTotal()
    {
        return Total;
    }

    public void setTotal(Double total)
    {
        Total = total;
    }

    String memberId;
    String terminalId;
    String chargeName;
    Double chargeRate;
    int chargeCounter;
    Double chargeAmount;
    Double chargeValue;
    String chargeRemark;
    String chargeValueType;
    String  merchantRdmChargeId;
    String bankWireId;
    String chargeType;
    String accountid;
    String actionExecutorId;
    String actionExecutorName;
    Double Total;


}
