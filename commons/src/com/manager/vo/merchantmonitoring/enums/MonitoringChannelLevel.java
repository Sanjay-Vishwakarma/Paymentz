package com.manager.vo.merchantmonitoring.enums;

/**
 * Created by admin on 5/11/16.
 */
public enum MonitoringChannelLevel
{
    New("New"),
    Old("Old"),
    All("All");

    private String monitoringChannelLevel;

    MonitoringChannelLevel(String monitoringChannelLevel)
    {
        this.monitoringChannelLevel = monitoringChannelLevel;
    }

    public String toString()
    {
        return monitoringChannelLevel;
    }
}
