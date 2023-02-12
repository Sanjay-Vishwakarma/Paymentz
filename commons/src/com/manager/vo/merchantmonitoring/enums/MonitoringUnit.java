package com.manager.vo.merchantmonitoring.enums;

/**
 * Created by user on 5/20/2016.
 */
public enum MonitoringUnit
{
    FlatThreshold("FlatThreshold"),
    Percentage("Percentage");

    private String unit;

    MonitoringUnit(String unit)
    {
        this.unit=unit;
    }

    public String toString()
    {
        return unit;
    }
}
