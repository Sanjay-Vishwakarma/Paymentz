package com.manager.vo.merchantmonitoring.enums;

/**
 * Created by admin on 5/11/16.
 */
public enum MonitoringAlertType
{
    Ratio("Ratio"),
    SalesDeviation("SalesDeviation"),
    AvgTicketDeviation("AvgTicketDeviation"),
    RefundDeviation("RefundDeviation"),
    ProcessingSetup("ProcessingSetup"),
    Others("Others");


    private String monitoringAlertType;

    MonitoringAlertType(String monitoringAlertType)
    {
        this.monitoringAlertType = monitoringAlertType;
    }

    public String toString()
    {
        return monitoringAlertType;
    }
}
