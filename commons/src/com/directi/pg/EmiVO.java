package com.directi.pg;

/**
 * Created by Admin on 11/14/2018.
 */
public class EmiVO
{
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String emiPeriod;
    private String id;
    private String isActive;

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

    public String getEmiPeriod()
    {
        return emiPeriod;
    }

    public void setEmiPeriod(String emiPeriod)
    {
        this.emiPeriod = emiPeriod;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    public String getIsActive()
    {
        return isActive;
    }

    public void setIsActive(String isActive)
    {
        this.isActive = isActive;
    }
}
