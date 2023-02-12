package com.manager.vo;

/**
 * Created by Supriya on 8/19/2016.
 */
public class ISOCommReportVO
{
    String bankName;
    String currency;
    String accountId;
    String startDate;
    String endDate;
    String isoWireId;
    String settledDate;
    Double amount;
    Double netFinalAmount;
    Double unpaidAmount;
    String status;
    String reportFilePath;
    String transactionFilePath;
    String actionExecutor;

    public String getBankName()
    {
        return bankName;
    }

    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

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

    public String getIsoWireId()
    {
        return isoWireId;
    }

    public void setIsoWireId(String isoWireId)
    {
        this.isoWireId = isoWireId;
    }

    public String getSettledDate()
    {
        return settledDate;
    }
    public void setSettledDate(String settledDate)
    {
       this.settledDate=settledDate;
    }
    public Double getAmount()
    {
        return  amount;
    }
    public void setAmount(double amount)
    {
        this.amount=amount;
    }
    public Double getNetfinalamount()
    {
        return netFinalAmount;
    }
    public void setNetfinalamount(double netFinalAmount)
    {
        this.netFinalAmount=netFinalAmount;
    }
    public Double getUnpaidAmount()
    {
        return unpaidAmount;
    }
    public void setUnpaidAmount(double unpaidAmount)
    {
        this.unpaidAmount=unpaidAmount;
    }
    public String getStatus()
    {
        return status;
    }
    public void setStatus(String status)
    {
        this.status=status;
    }
    public String getReportFilePath()
    {
        return reportFilePath;
    }
    public void setReportFilePath(String reportFilePath)
    {
        this.reportFilePath=reportFilePath;
    }
    public String getTransactionFilePath()
    {
        return transactionFilePath;
    }
    public void setTransactionFilePath(String transactionFilePath)
    {
        this.transactionFilePath=transactionFilePath;
    }
    public String getActionExecutor()
    {
        return  actionExecutor;
    }
    public void setActionExecutor(String actionExecutor)
    {
     this.actionExecutor=actionExecutor;
    }
}
