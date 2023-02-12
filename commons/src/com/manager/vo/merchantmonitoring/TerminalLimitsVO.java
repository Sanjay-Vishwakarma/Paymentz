package com.manager.vo.merchantmonitoring;

/**
 * Created by admin on 3/31/2016.
 */
public class TerminalLimitsVO
{
    double dailyAmountLimit;
    double weeklyAmountLimit;
    double monthlyAmountLimit;

    double dailyCardAmountLimit;
    double weeklyCardAmountLimit;
    double monthlyCardAmountLimit;

    double dailyCardLimit;
    double weeklyCardLimit;
    double monthlyCardLimit;

    double minTransactionAmount;
    double maxTransactionAmount;

    double dailyAvgTicketAmount;
    double WeeklyAvgTicketAmount;
    double MonthlyAvgTicketAmount;

    public double getDailyAmountLimit()
    {
        return dailyAmountLimit;
    }

    public void setDailyAmountLimit(double dailyAmountLimit)
    {
        this.dailyAmountLimit = dailyAmountLimit;
    }

    public double getWeeklyAmountLimit()
    {
        return weeklyAmountLimit;
    }

    public void setWeeklyAmountLimit(double weeklyAmountLimit)
    {
        this.weeklyAmountLimit = weeklyAmountLimit;
    }

    public double getMonthlyAmountLimit()
    {
        return monthlyAmountLimit;
    }

    public void setMonthlyAmountLimit(double monthlyAmountLimit)
    {
        this.monthlyAmountLimit = monthlyAmountLimit;
    }

    public double getDailyCardAmountLimit()
    {
        return dailyCardAmountLimit;
    }

    public void setDailyCardAmountLimit(double dailyCardAmountLimit)
    {
        this.dailyCardAmountLimit = dailyCardAmountLimit;
    }

    public double getWeeklyCardAmountLimit()
    {
        return weeklyCardAmountLimit;
    }

    public void setWeeklyCardAmountLimit(double weeklyCardAmountLimit)
    {
        this.weeklyCardAmountLimit = weeklyCardAmountLimit;
    }

    public double getMonthlyCardAmountLimit()
    {
        return monthlyCardAmountLimit;
    }

    public void setMonthlyCardAmountLimit(double monthlyCardAmountLimit)
    {
        this.monthlyCardAmountLimit = monthlyCardAmountLimit;
    }

    public double getDailyCardLimit()
    {
        return dailyCardLimit;
    }

    public void setDailyCardLimit(double dailyCardLimit)
    {
        this.dailyCardLimit = dailyCardLimit;
    }

    public double getWeeklyCardLimit()
    {
        return weeklyCardLimit;
    }

    public void setWeeklyCardLimit(double weeklyCardLimit)
    {
        this.weeklyCardLimit = weeklyCardLimit;
    }

    public double getMonthlyCardLimit()
    {
        return monthlyCardLimit;
    }

    public void setMonthlyCardLimit(double monthlyCardLimit)
    {
        this.monthlyCardLimit = monthlyCardLimit;
    }

    public double getMinTransactionAmount()
    {
        return minTransactionAmount;
    }

    public void setMinTransactionAmount(double minTransactionAmount)
    {
        this.minTransactionAmount = minTransactionAmount;
    }

    public double getMaxTransactionAmount()
    {
        return maxTransactionAmount;
    }

    public void setMaxTransactionAmount(double maxTransactionAmount)
    {
        this.maxTransactionAmount = maxTransactionAmount;
    }

    public double getDailyAvgTicketAmount()
    {
        return dailyAvgTicketAmount;
    }

    public void setDailyAvgTicketAmount(double dailyAvgTicketAmount)
    {
        this.dailyAvgTicketAmount = dailyAvgTicketAmount;
    }

    public double getWeeklyAvgTicketAmount()
    {
        return WeeklyAvgTicketAmount;
    }

    public void setWeeklyAvgTicketAmount(double weeklyAvgTicketAmount)
    {
        WeeklyAvgTicketAmount = weeklyAvgTicketAmount;
    }

    public double getMonthlyAvgTicketAmount()
    {
        return MonthlyAvgTicketAmount;
    }

    public void setMonthlyAvgTicketAmount(double monthlyAvgTicketAmount)
    {
        MonthlyAvgTicketAmount = monthlyAvgTicketAmount;
    }
}
