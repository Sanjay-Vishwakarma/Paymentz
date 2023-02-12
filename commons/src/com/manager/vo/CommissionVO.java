package com.manager.vo;
import com.manager.vo.payoutVOs.ChargeMasterVO;

/**
 * Created by Admin on 10/10/2015.
 */
public class CommissionVO
{
    String commissionId;
    String memberId;
    String terminalId;
    String chargeId;
    double commissionValue;
    String startDate;
    String endDate;
    String sequenceNo;
    String isInputRequired;
    String id;
    String chargeName;
    String chargeValue;
    ChargeMasterVO chargeMasterVO;

    public String getCommissionId()
    {
        return commissionId;
    }
    public void setCommissionId(String commissionId)
    {
        this.commissionId = commissionId;
    }
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
    public String getChargeId()
    {
        return chargeId;
    }
    public void setChargeId(String chargeId)
    {
        this.chargeId = chargeId;
    }
    public double getCommissionValue()
    {
        return commissionValue;
    }
    public void setCommissionValue(double commissionValue)
    {
        this.commissionValue = commissionValue;
    }
    public String getStartDate()
    {
        return startDate;
    }
    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }
    public String getEndDate()
    {
        return endDate;
    }
    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }
    public String getSequenceNo()
    {
        return sequenceNo;
    }
    public void setSequenceNo(String sequenceNo)
    {
        this.sequenceNo = sequenceNo;
    }
    public ChargeMasterVO getChargeMasterVO()
    {
        return chargeMasterVO;
    }
    public void setChargeMasterVO(ChargeMasterVO chargeMasterVO)
    {
        this.chargeMasterVO = chargeMasterVO;
    }

    public String getIsInputRequired()
    {
        return isInputRequired;
    }

    public void setIsInputRequired(String isInputRequired)
    {
        this.isInputRequired = isInputRequired;
    }

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public String getChargeName() {return chargeName;}

    public void setChargeName(String chargeName) {this.chargeName = chargeName;}

    public String getChargeValue()
    {
        return chargeValue;
    }

    public void setChargeValue(String chargeValue)
    {
        this.chargeValue = chargeValue;
    }
}
