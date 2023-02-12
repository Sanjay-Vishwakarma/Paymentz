package com.manager.vo.payoutVOs;

/**
 * Created by Sandip on 6/30/2017.
 */
public class SettlementCycleVO
{
    int settlementCycleId;
    String accountId;
    String startDate;
    String startTime;
    String endDate;
    String endTime;
    String nextCycleDays;
    String status;
    String partnerId;
    String isTransactionFileAvailable;
    String finalAmount;
    String unpaidAmount;
    String settlementReportFilePath;
    String settledTransactionFilePath;
    String memberId;
    String terminalId;
    String paymodeId;
    String cardTypeId;
    String currency;
    String rollingReserveDate;
    String markForDeletion;
    String rollingReserveIncluded;
    String amount;
    String balanceAmount;
    String moneyRecivedDate;

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
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

    public String getNextCycleDays()
    {
        return nextCycleDays;
    }

    public void setNextCycleDays(String nextCycleDays)
    {
        this.nextCycleDays = nextCycleDays;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    public int getSettlementCycleId()
    {
        return settlementCycleId;
    }

    public void setSettlementCycleId(int settlementCycleId)
    {
        this.settlementCycleId = settlementCycleId;
    }

    public String getIsTransactionFileAvailable()
    {
        return isTransactionFileAvailable;
    }

    public void setIsTransactionFileAvailable(String isTransactionFileAvailable)
    {
        this.isTransactionFileAvailable = isTransactionFileAvailable;
    }

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    public String getFinalAmount()
    {
        return finalAmount;
    }

    public void setFinalAmount(String finalAmount)
    {
        this.finalAmount = finalAmount;
    }

    public String getUnpaidAmount()
    {
        return unpaidAmount;
    }

    public void setUnpaidAmount(String unpaidAmount)
    {
        this.unpaidAmount = unpaidAmount;
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

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getRollingReserveDate()
    {
        return rollingReserveDate;
    }

    public void setRollingReserveDate(String rollingReserveDate)
    {
        this.rollingReserveDate = rollingReserveDate;
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
        return rollingReserveIncluded;
    }

    public void setRollingReserveIncluded(String rollingReserveIncluded)
    {
        this.rollingReserveIncluded = rollingReserveIncluded;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getBalanceAmount()
    {
        return balanceAmount;
    }

    public void setBalanceAmount(String balanceAmount)
    {
        this.balanceAmount = balanceAmount;
    }

    public String getMoneyRecivedDate()
    {
        return moneyRecivedDate;
    }

    public void setMoneyRecivedDate(String moneyRecivedDate)
    {
        this.moneyRecivedDate = moneyRecivedDate;
    }
}
