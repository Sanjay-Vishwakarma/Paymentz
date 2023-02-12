package com.manager.vo.merchantmonitoring.enums;

/**
 * Created by admin on 5/11/16.
 */
public enum MonitoringCategory
{
    Success("Success"),
    Failure("Failure"),
    Others("Others");

    private String monitoringCategory;

    MonitoringCategory(String monitoringCategory)
    {
        this.monitoringCategory = monitoringCategory;
    }

    public String toString()
    {
        return monitoringCategory;
    }
}
