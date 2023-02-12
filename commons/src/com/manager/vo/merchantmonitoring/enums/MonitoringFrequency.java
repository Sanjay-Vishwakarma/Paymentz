package com.manager.vo.merchantmonitoring.enums;

/**
 * Created by admin on 5/11/16.
 */
public enum MonitoringFrequency
{
    Daily("Daily"),
    Weekly("Weekly"),
    Monthly("Monthly"),
    Yearly("Yearly"),
    Hoursly("Hoursly");

    private String monitoringFrequency;

    MonitoringFrequency(String monitoringFrequency)
    {
        this.monitoringFrequency = monitoringFrequency;
    }

    public String toString()
    {
        return monitoringFrequency;
    }
}
