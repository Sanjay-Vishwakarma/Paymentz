package com.manager.vo.merchantmonitoring.enums;

/**
 * Created by admin on 5/11/16.
 */
public enum MonitoringSubKeyword
{
    Amount("Amount"),
    Count("Count"),
    Day("Day"),
    AvgTicketAmount("AvgTicketAmount");

    private String monitoringSubKeyword;


    MonitoringSubKeyword(String monitoringSubKeyword)
    {
        this.monitoringSubKeyword = monitoringSubKeyword;
    }

    public String toString()
    {
        return monitoringSubKeyword;
    }
}
