package com.manager.vo.merchantmonitoring;

/**
 * Created by admin on 3/9/2016.
 */
public class MerchantRiskParameterVO
{
    int merchantFirstSubmissionInDays;
    String firstSubmissionInMonthDaysFormat;

    int merchantLastSubmissionInDays;
    String lastSubmissionInMonthDaysFormat;

    public int getMerchantFirstSubmissionInDays()
    {
        return merchantFirstSubmissionInDays;
    }

    public void setMerchantFirstSubmissionInDays(int merchantFirstSubmissionInDays)
    {
        this.merchantFirstSubmissionInDays = merchantFirstSubmissionInDays;
    }

    public int getMerchantLastSubmissionInDays()
    {
        return merchantLastSubmissionInDays;
    }

    public void setMerchantLastSubmissionInDays(int merchantLastSubmissionInDays)
    {
        this.merchantLastSubmissionInDays = merchantLastSubmissionInDays;
    }

    public String getFirstSubmissionInMonthDaysFormat()
    {
        return firstSubmissionInMonthDaysFormat;
    }

    public void setFirstSubmissionInMonthDaysFormat(String firstSubmissionInMonthDaysFormat)
    {
        this.firstSubmissionInMonthDaysFormat = firstSubmissionInMonthDaysFormat;
    }

    public String getLastSubmissionInMonthDaysFormat()
    {
        return lastSubmissionInMonthDaysFormat;
    }

    public void setLastSubmissionInMonthDaysFormat(String lastSubmissionInMonthDaysFormat)
    {
        this.lastSubmissionInMonthDaysFormat = lastSubmissionInMonthDaysFormat;
    }
}
