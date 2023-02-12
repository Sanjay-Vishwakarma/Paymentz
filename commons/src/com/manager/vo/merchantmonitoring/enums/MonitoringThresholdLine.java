package com.manager.vo.merchantmonitoring.enums;

/**
 * Created by sandip on 5/17/2016.
 */
public enum MonitoringThresholdLine
{
    Higher("Higher"),
    Lower("Failure");

    private String monitoringThresholdLine;

    MonitoringThresholdLine(String monitoringThresholdLine)
    {
        this.monitoringThresholdLine = monitoringThresholdLine;
    }

    public String toString()
    {
        return monitoringThresholdLine;
    }
}
