package com.manager.vo;

/**
 * Created by IntelliJ IDEA.
 * User: Supriya
 * Date: 5/9/2016
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class WLPartnerInvoiceVO
{
    String partnerId;
    String pgtypeId;
    String startDate;
    String endDate;
    String settledDate;
    Double amount;
    Double netFinalAmount;
    Double unpaidAmount;
    String currency;
    String status;
    String reportFilePath;
    String transactionFilePath;
    String creationOn;
    String actionExecutor;
    String invoiceId;
    String gateway;

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

    public String getSettledDate()
    {
        return settledDate;
    }

    public void setSettledDate(String settledDate)
    {
        this.settledDate = settledDate;
    }

    public Double getAmount()
    {
        return amount;
    }

    public void setAmount(Double amount)
    {
        this.amount = amount;
    }

    public Double getNetFinalAmount()
    {
        return netFinalAmount;
    }

    public void setNetFinalAmount(Double netFinalAmount)
    {
        this.netFinalAmount = netFinalAmount;
    }

    public Double getUnpaidAmount()
    {
        return unpaidAmount;
    }

    public void setUnpaidAmount(Double unpaidAmount)
    {
        this.unpaidAmount = unpaidAmount;
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

    public String getReportFilePath()
    {
        return reportFilePath;
    }

    public void setReportFilePath(String reportFilePath)
    {
        this.reportFilePath = reportFilePath;
    }

    public String getTransactionFilePath()
    {
        return transactionFilePath;
    }

    public void setTransactionFilePath(String transactionFilePath)
    {
        this.transactionFilePath = transactionFilePath;
    }

    public String getCreationOn()
    {
        return creationOn;
    }

    public void setCreationOn(String creationOn)
    {
        this.creationOn = creationOn;
    }

    public String getActionExecutor()
    {
        return actionExecutor;
    }

    public void setActionExecutor(String actionExecutor)
    {
        this.actionExecutor = actionExecutor;
    }

    public String getInvoiceId()
    {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId)
    {
        this.invoiceId = invoiceId;
    }

    public String getGateway()
    {
        return gateway;
    }

    public void setGateway(String gateway)
    {
        this.gateway = gateway;
    }
}
