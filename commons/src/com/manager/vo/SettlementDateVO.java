package com.manager.vo;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 8/1/14
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class SettlementDateVO
{
    String settlementcycleNumber;
    String settlementStartDate;
    String settlementEndDate;

    String declinedStartDate;
    String declinedEndDate;

    String reversedStartDate;
    String reversedEndDate;

    String chargebackStartDate;
    String chargebackEndDate;

    public String getDeclinedStartDate()
    {
        return declinedStartDate;
    }

    public void setDeclinedStartDate(String declinedStartDate)
    {
        this.declinedStartDate = declinedStartDate;
    }

    public String getDeclinedEndDate()
    {
        return declinedEndDate;
    }

    public void setDeclinedEndDate(String declinedEndDate)
    {
        this.declinedEndDate = declinedEndDate;
    }

    public String getReversedStartDate()
    {
        return reversedStartDate;
    }

    public void setReversedStartDate(String reversedStartDate)
    {
        this.reversedStartDate = reversedStartDate;
    }

    public String getReversedEndDate()
    {
        return reversedEndDate;
    }

    public void setReversedEndDate(String reversedEndDate)
    {
        this.reversedEndDate = reversedEndDate;
    }

    public String getChargebackStartDate()
    {
        return chargebackStartDate;
    }

    public void setChargebackStartDate(String chargebackStartDate)
    {
        this.chargebackStartDate = chargebackStartDate;
    }

    public String getChargebackEndDate()
    {
        return chargebackEndDate;
    }

    public void setChargebackEndDate(String chargebackEndDate)
    {
        this.chargebackEndDate = chargebackEndDate;
    }
    public String getSettlementcycleNumber()
    {
        return settlementcycleNumber;
    }
    public void setSettlementcycleNumber(String settlementcycleNumber)
    {
        this.settlementcycleNumber = settlementcycleNumber;
    }
    public String getSettlementStartDate()
    {
        return settlementStartDate;
    }
    public void setSettlementStartDate(String settlementStartDate)
    {
        this.settlementStartDate = settlementStartDate;
    }
    public String getSettlementEndDate()
    {
        return settlementEndDate;
    }
    public void setSettlementEndDate(String settlementEndtDate)
    {
        this.settlementEndDate = settlementEndtDate;
    }


}
