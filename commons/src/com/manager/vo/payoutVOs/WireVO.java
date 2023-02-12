package com.manager.vo.payoutVOs;

import com.manager.vo.SettlementDateVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.WireAmountVO;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 8/8/14
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class WireVO extends WireAmountVO
{
    String settleId;
    String settleDate;
    String firstDate;
    String lastDate;
    String currency;
    String status;
    String memberId;
    String terminalId;
    String settlementReportFilePath;
    String settledTransactionFilePath;
    String wireTransferConfirmationImage;
    String markForDeletion;
    String isRollingReserveIncluded;
    String reserveReleasedUptoDate;

    String declinedcoverdateupto;
    String reversedcoverdateupto;
    String chargebackcoverdateupto;
    String paymodeId;
    String cardTypeId;
    String rollingReserveFilePath;
    String timestamp;


    String settlementCycleNo;
    String parent_settlementCycleNo;
    double wireAmount;
    double netFinalAmount;
    String dateOfReportGeneration;

    TerminalVO terminalVO;
    SettlementDateVO settlementDateVO;
    int reportid;

    public int getReportid()
    {
        return reportid;
    }

    public void setReportid(int reportid)
    {
        this.reportid = reportid;
    }
    public String getDeclinedcoverdateupto()
    {
        return declinedcoverdateupto;
    }

    public void setDeclinedcoverdateupto(String declinedcoverdateupto)
    {
        this.declinedcoverdateupto = declinedcoverdateupto;
    }

    public String getReversedcoverdateupto()
    {
        return reversedcoverdateupto;
    }

    public void setReversedcoverdateupto(String reversedcoverdateupto)
    {
        this.reversedcoverdateupto = reversedcoverdateupto;
    }

    public String getChargebackcoverdateupto()
    {
        return chargebackcoverdateupto;
    }

    public void setChargebackcoverdateupto(String chargebackcoverdateupto)
    {
        this.chargebackcoverdateupto = chargebackcoverdateupto;
    }


    public String getSettlementCycleNo()
    {
        return settlementCycleNo;
    }

    public void setSettlementCycleNo(String settlementCycleNo)
    {
        this.settlementCycleNo = settlementCycleNo;
    }
    public TerminalVO getTerminalVO()
    {
        return terminalVO;
    }

    public void setTerminalVO(TerminalVO terminalVO)
    {
        this.terminalVO = terminalVO;
    }
    public String getReserveReleasedUptoDate()
    {
        return reserveReleasedUptoDate;
    }

    public void setReserveReleasedUptoDate(String reserveReleasedUptoDate)
    {
        this.reserveReleasedUptoDate = reserveReleasedUptoDate;
    }

    public SettlementDateVO getSettlementDateVO()
    {
        return settlementDateVO;
    }

    public void setSettlementDateVO(SettlementDateVO settlementDateVO)
    {
        this.settlementDateVO = settlementDateVO;
    }

    public String getSettleId()
    {
        return settleId;
    }

    public void setSettleId(String settleId)
    {
        this.settleId = settleId;
    }

    public String getSettleDate()
    {
        return settleDate;
    }

    public void setSettleDate(String settleDate)
    {
        this.settleDate = settleDate;
    }

    public String getFirstDate()
    {
        return firstDate;
    }

    public void setFirstDate(String firstDate)
    {
        this.firstDate = firstDate;
    }

    public String getLastDate()
    {
        return lastDate;
    }

    public void setLastDate(String lastDate)
    {
        this.lastDate = lastDate;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getSettlementReportFilePath()
    {
        return settlementReportFilePath;
    }

    public void setSettlementReportFilePath(String settlementReportFilePath)
    {
        this.settlementReportFilePath = settlementReportFilePath;
    }

    public String getSettledTransactionFilePath()
    {
        return settledTransactionFilePath;
    }

    public void setSettledTransactionFilePath(String settledTransactionFilePath)
    {
        this.settledTransactionFilePath = settledTransactionFilePath;
    }

    public String getMarkForDeletion()
    {
        return markForDeletion;
    }

    public void setMarkForDeletion(String markForDeletion)
    {
        this.markForDeletion = markForDeletion;
    }

    public String getRollingReserveIncluded()
    {
        return isRollingReserveIncluded;
    }

    public void setRollingReserveIncluded(String rollingReserveIncluded)
    {
        isRollingReserveIncluded = rollingReserveIncluded;
    }

    public double getWireAmount()
    {
        return wireAmount;
    }

    public void setWireAmount(double wireAmount)
    {
        this.wireAmount = wireAmount;
    }

    public double getNetFinalAmount()
    {
        return netFinalAmount;
    }

    public void setNetFinalAmount(double netFinalAmount)
    {
        this.netFinalAmount = netFinalAmount;
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

    public String getWireTransferConfirmationImage()
    {
        return wireTransferConfirmationImage;
    }

    public void setWireTransferConfirmationImage(String wireTransferConfirmationImage)
    {
        this.wireTransferConfirmationImage = wireTransferConfirmationImage;
    }

    public String getPaymodeId()
    {
        return paymodeId;
    }

    public void setPaymodeId(String paymodeId)
    {
        this.paymodeId = paymodeId;
    }

    public String getCardTypeId()
    {
        return cardTypeId;
    }

    public void setCardTypeId(String cardTypeId)
    {
        this.cardTypeId = cardTypeId;
    }

    public String getIsRollingReserveIncluded()
    {
        return isRollingReserveIncluded;
    }

    public void setIsRollingReserveIncluded(String isRollingReserveIncluded)
    {
        this.isRollingReserveIncluded = isRollingReserveIncluded;
    }

    public String getRollingReserveFilePath()
    {
        return rollingReserveFilePath;
    }

    public void setRollingReserveFilePath(String rollingReserveFilePath)
    {
        this.rollingReserveFilePath = rollingReserveFilePath;
    }

    public String getDateOfReportGeneration()
    {
        return dateOfReportGeneration;
    }

    public void setDateOfReportGeneration(String dateOfReportGeneration)
    {
        this.dateOfReportGeneration = dateOfReportGeneration;
    }

    public String getParent_settlementCycleNo()
    {
        return parent_settlementCycleNo;
    }

    public void setParent_settlementCycleNo(String parent_settlementCycleNo)
    {
        this.parent_settlementCycleNo = parent_settlementCycleNo;
    }
    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }
}
