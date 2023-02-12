package com.manager.vo.merchantmonitoring;

import com.manager.vo.TerminalVO;

/**
 * Created by admin on 3/8/2016.
 */
public class TerminalProcessingDetailsVO
{
    int salesCount;
    int chargebackCount;
    int refundCount;
    int declinedCount;
    int preAuthCount;
    int oldTransRefundCount;

    double salesAmount;
    double preAuthAmount;
    double chargebackAmount;
    double refundAmount;
    double declinedAmount;
    double refundPercentage;
    double chargebackPercentage;
    double declinedPercentage;
    double salesPercentage;
    double oldTransRefundAmount;

    TerminalVO terminalVO;
    MerchantRiskParameterVO riskParameterVO;

    String firstTransactionDate;
    String lastTransactionDate;
    String currentMonthStartDate;
    String currentMonthEndDate;
    String activationDate;
    String bankName;
    String currency;
    int totalProcessingCount;
    double totalProcessingAmount;

    public String getFirstTransactionDate()
    {
        return firstTransactionDate;
    }

    public void setFirstTransactionDate(String firstTransactionDate)
    {
        this.firstTransactionDate = firstTransactionDate;
    }

    public String getLastTransactionDate()
    {
        return lastTransactionDate;
    }

    public void setLastTransactionDate(String lastTransactionDate)
    {
        this.lastTransactionDate = lastTransactionDate;
    }

    public int getSalesCount()
    {
        return salesCount;
    }

    public void setSalesCount(int salesCount)
    {
        this.salesCount = salesCount;
    }

    public int getChargebackCount()
    {
        return chargebackCount;
    }

    public void setChargebackCount(int chargebackCount)
    {
        this.chargebackCount = chargebackCount;
    }

    public int getRefundCount()
    {
        return refundCount;
    }

    public void setRefundCount(int refundCount)
    {
        this.refundCount = refundCount;
    }

    public int getDeclinedCount()
    {
        return declinedCount;
    }

    public void setDeclinedCount(int declinedCount)
    {
        this.declinedCount = declinedCount;
    }

    public double getSalesAmount()
    {
        return salesAmount;
    }

    public void setSalesAmount(double salesAmount)
    {
        this.salesAmount = salesAmount;
    }

    public double getChargebackAmount()
    {
        return chargebackAmount;
    }

    public void setChargebackAmount(double chargebackAmount)
    {
        this.chargebackAmount = chargebackAmount;
    }

    public double getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount)
    {
        this.refundAmount = refundAmount;
    }

    public double getDeclinedAmount()
    {
        return declinedAmount;
    }

    public void setDeclinedAmount(double declinedAmount)
    {
        this.declinedAmount = declinedAmount;
    }

    public String getActivationDate()
    {
        return activationDate;
    }

    public void setActivationDate(String activationDate)
    {
        this.activationDate = activationDate;
    }

    public TerminalVO getTerminalVO()
    {
        return terminalVO;
    }

    public void setTerminalVO(TerminalVO terminalVO)
    {
        this.terminalVO = terminalVO;
    }

    public MerchantRiskParameterVO getRiskParameterVO()
    {
        return riskParameterVO;
    }

    public void setRiskParameterVO(MerchantRiskParameterVO riskParameterVO)
    {
        this.riskParameterVO = riskParameterVO;
    }

    public double getRefundPercentage()
    {
        return refundPercentage;
    }

    public void setRefundPercentage(double refundPercentage)
    {
        this.refundPercentage = refundPercentage;
    }

    public double getChargebackPercentage()
    {
        return chargebackPercentage;
    }

    public void setChargebackPercentage(double chargebackPercentage)
    {
        this.chargebackPercentage = chargebackPercentage;
    }

    public double getDeclinedPercentage()
    {
        return declinedPercentage;
    }

    public void setDeclinedPercentage(double declinedPercentage)
    {
        this.declinedPercentage = declinedPercentage;
    }

    public String getBankName()
    {
        return bankName;
    }

    public void setBankName(String bankName)
    {
        this.bankName=bankName;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency=currency;
    }

    public double getSalesPercentage()
    {
        return salesPercentage;
    }

    public void setSalesPercentage(double salesPercentage)
    {
        this.salesPercentage = salesPercentage;
    }

    public String getCurrentMonthStartDate()
    {
        return currentMonthStartDate;
    }

    public void setCurrentMonthStartDate(String currentMonthStartDate)
    {
        this.currentMonthStartDate = currentMonthStartDate;
    }

    public String getCurrentMonthEndDate()
    {
        return currentMonthEndDate;
    }

    public void setCurrentMonthEndDate(String currentMonthEndDate)
    {
        this.currentMonthEndDate = currentMonthEndDate;
    }

    public int getTotalProcessingCount()
    {
        return totalProcessingCount;
    }

    public void setTotalProcessingCount(int totalProcessingCount)
    {
        this.totalProcessingCount = totalProcessingCount;
    }

    public double getTotalProcessingAmount()
    {
        return totalProcessingAmount;
    }

    public void setTotalProcessingAmount(double totalProcessingAmount)
    {
        this.totalProcessingAmount = totalProcessingAmount;
    }

    public int getPreAuthCount()
    {
        return preAuthCount;
    }

    public void setPreAuthCount(int preAuthCount)
    {
        this.preAuthCount = preAuthCount;
    }

    public double getPreAuthAmount()
    {
        return preAuthAmount;
    }

    public void setPreAuthAmount(double preAuthAmount)
    {
        this.preAuthAmount = preAuthAmount;
    }

    public int getOldTransRefundCount()
    {
        return oldTransRefundCount;
    }

    public void setOldTransRefundCount(int oldTransRefundCount)
    {
        this.oldTransRefundCount = oldTransRefundCount;
    }

    public double getOldTransRefundAmount()
    {
        return oldTransRefundAmount;
    }

    public void setOldTransRefundAmount(double oldTransRefundAmount)
    {
        this.oldTransRefundAmount = oldTransRefundAmount;
    }
}
