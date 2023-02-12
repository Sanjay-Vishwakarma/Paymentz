package com.manager.vo.merchantmonitoring;

import java.util.Date;

/**
 * Created by admin on 4/6/2016.
 */
public class DateVO
{
    String startDate;
    String endDate;
    String dateLabel;
    String dateLabel1;
    String previousMonthStartDate;
    String previousMonthEndDate;

    String lastThreeMonthStartDate;
    String lastThreeMonthEndDate;

    Date startDate1;
    Date endDate1;

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

    public Date getStartDate1()
    {
        return startDate1;
    }

    public void setStartDate1(Date startDate1)
    {
        this.startDate1 = startDate1;
    }

    public Date getEndDate1()
    {
        return endDate1;
    }

    public void setEndDate1(Date endDate1)
    {
        this.endDate1 = endDate1;
    }

    public String getDateLabel()
    {
        return dateLabel;
    }

    public void setDateLabel(String dateLabel)
    {
        this.dateLabel = dateLabel;
    }

    public String getDateLabel1()
    {
        return dateLabel1;
    }

    public void setDateLabel1(String dateLabel1)
    {
        this.dateLabel1 = dateLabel1;
    }

    public String getPreviousMonthStartDate()
    {
        return previousMonthStartDate;
    }

    public void setPreviousMonthStartDate(String previousMonthStartDate)
    {
        this.previousMonthStartDate = previousMonthStartDate;
    }

    public String getPreviousMonthEndDate()
    {
        return previousMonthEndDate;
    }

    public void setPreviousMonthEndDate(String previousMonthEndDate)
    {
        this.previousMonthEndDate = previousMonthEndDate;
    }

    public String getLastThreeMonthStartDate()
    {
        return lastThreeMonthStartDate;
    }

    public void setLastThreeMonthStartDate(String lastThreeMonthStartDate)
    {
        this.lastThreeMonthStartDate = lastThreeMonthStartDate;
    }

    public String getLastThreeMonthEndDate()
    {
        return lastThreeMonthEndDate;
    }
    public void setLastThreeMonthEndDate(String lastThreeMonthEndDate)
    {
        this.lastThreeMonthEndDate = lastThreeMonthEndDate;
    }
}
