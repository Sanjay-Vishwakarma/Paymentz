package com.manager.vo.merchantmonitoring.enums;

/**
 * Created by user on 5/18/2016.
 */
public enum MonitoringDeviation
{
    Higher("Higher"),
    Lower("Lower");

    public String monitoringDeviation;

    MonitoringDeviation(String monitoringDeviation)
    {
        this.monitoringDeviation = monitoringDeviation;
    }
    public String toString() {return monitoringDeviation;}
}
