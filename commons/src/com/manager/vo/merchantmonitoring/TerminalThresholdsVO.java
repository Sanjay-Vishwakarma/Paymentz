package com.manager.vo.merchantmonitoring;

/**
 * Created by admin on 3/31/2016.
 */
public class TerminalThresholdsVO
{
    double dailyApprovalRatio;
    double monthlyApprovalRatio;
    double weeklyApprovalRatio;

    double dailyCBRatio;
    double weeklyCBRatio;
    double monthlyCBRatio;
    double amountMonthlyCBRatio;

    double dailyCBRatioAmount;
    double weeklyCBRatioAmount;
    double monthlyCBRatioAmount;

    double priorMonthSalesVsCurrentMonthRefund;

    double dailyRFRatio;
    double weeklyRFRatio;
    double monthlyRFRatio;

    double dailyRFAmountRatio;
    double weeklyRFAmountRatio;
    double monthlyRFAmountRatio;

    double dailyAvgTicketThreshold;
    double weeklyAvgTicketThreshold;
    double monthlyAvgTicketThreshold;
    double dailyAvgTicketPercentageThreshold;
    double dailyVsQuarterlyAvgTicketThreshold;
    double monthly_avgticket_amount;

    double dailyCBRatioSuspension;
    double dailyCBAmountRatioSuspension;

    int inactivePeriodThreshold;
    int firstSubmissionThreshold;
    int sameCardConsequentlyThreshold;

    int suspendCBCountThreshold;
    int manualCaptureAlertThreshold;
    int sameCardSameAmountThreshold;
    int sameCardSameAmountConsequenceThreshold;
    int alertCBCountThreshold;
    int resumeProcessingAlert;

    public double getPriorMonthRFVsCurrentMonthSales()
    {
        return priorMonthSalesVsCurrentMonthRefund;
    }

    public void setPriorMonthSalesVsCurrentMonthRefund(double priorMonthSalesVsCurrentMonthRefund)
    {
        this.priorMonthSalesVsCurrentMonthRefund = priorMonthSalesVsCurrentMonthRefund;
    }

    public int getSameCardSameAmountThreshold()
    {
        return sameCardSameAmountThreshold;
    }

    public void setSameCardSameAmountThreshold(int sameCardSameAmountThreshold)
    {
        this.sameCardSameAmountThreshold = sameCardSameAmountThreshold;
    }
    public int getAlertCBCountThreshold()
    {
        return alertCBCountThreshold;
    }

    public void setAlertCBCountThreshold(int alertCBCountThreshold)
    {
        this.alertCBCountThreshold = alertCBCountThreshold;
    }

    public double getDailyVsQuarterlyAvgTicketThreshold()
    {
        return dailyVsQuarterlyAvgTicketThreshold;
    }

    public void setDailyVsQuarterlyAvgTicketThreshold(double dailyVsQuarterlyAvgTicketThreshold)
    {
        this.dailyVsQuarterlyAvgTicketThreshold = dailyVsQuarterlyAvgTicketThreshold;
    }

    public int getManualCaptureAlertThreshold()
    {
        return manualCaptureAlertThreshold;
    }

    public void setManualCaptureAlertThreshold(int manualCaptureAlertThreshold)
    {
        this.manualCaptureAlertThreshold = manualCaptureAlertThreshold;
    }

    public double getDailyApprovalRatio()
    {
        return dailyApprovalRatio;
    }

    public void setDailyApprovalRatio(double dailyApprovalRatio)
    {
        this.dailyApprovalRatio = dailyApprovalRatio;
    }

    public double getMonthlyApprovalRatio()
    {
        return monthlyApprovalRatio;
    }

    public void setMonthlyApprovalRatio(double monthlyApprovalRatio)
    {
        this.monthlyApprovalRatio = monthlyApprovalRatio;
    }

    public double getWeeklyApprovalRatio()
    {
        return weeklyApprovalRatio;
    }

    public void setWeeklyApprovalRatio(double weeklyApprovalRatio)
    {
        this.weeklyApprovalRatio = weeklyApprovalRatio;
    }

    public double getMonthlyCBRatio()
    {
        return monthlyCBRatio;
    }

    public void setMonthlyCBRatio(double monthlyCBRatio)
    {
        this.monthlyCBRatio = monthlyCBRatio;
    }

    public double getMonthlyRFRatio()
    {
        return monthlyRFRatio;
    }

    public void setMonthlyRFRatio(double monthlyRFRatio)
    {
        this.monthlyRFRatio = monthlyRFRatio;
    }

    public int getInactivePeriodThreshold()
    {
        return inactivePeriodThreshold;
    }

    public void setInactivePeriodThreshold(int inactivePeriodThreshold)
    {
        this.inactivePeriodThreshold = inactivePeriodThreshold;
    }

    public int getFirstSubmissionThreshold()
    {
        return firstSubmissionThreshold;
    }

    public void setFirstSubmissionThreshold(int firstSubmissionThreshold)
    {
        this.firstSubmissionThreshold = firstSubmissionThreshold;
    }

    public double getDailyCBRatio()
    {
        return dailyCBRatio;
    }

    public void setDailyCBRatio(double dailyCBRatio)
    {
        this.dailyCBRatio = dailyCBRatio;
    }

    public double getWeeklyCBRatio()
    {
        return weeklyCBRatio;
    }

    public void setWeeklyCBRatio(double weeklyCBRatio)
    {
        this.weeklyCBRatio = weeklyCBRatio;
    }

    public double getDailyRFRatio()
    {
        return dailyRFRatio;
    }

    public void setDailyRFRatio(double dailyRFRatio)
    {
        this.dailyRFRatio = dailyRFRatio;
    }

    public double getWeeklyRFRatio()
    {
        return weeklyRFRatio;
    }

    public void setWeeklyRFRatio(double weeklyRFRatio)
    {
        this.weeklyRFRatio = weeklyRFRatio;
    }

    public double getAmountMonthlyCBRatio()
    {
        return amountMonthlyCBRatio;
    }

    public void setAmountMonthlyCBRatio(double amountMonthlyCBRatio)
    {
        this.amountMonthlyCBRatio = amountMonthlyCBRatio;
    }

    public int getSuspendCBCountThreshold()
    {
        return suspendCBCountThreshold;
    }

    public void setSuspendCBCountThreshold(int suspendCBCountThreshold)
    {
        this.suspendCBCountThreshold = suspendCBCountThreshold;
    }

    public double getDailyAvgTicketThreshold()
    {
        return dailyAvgTicketThreshold;
    }

    public void setDailyAvgTicketThreshold(double dailyAvgTicketThreshold)
    {
        this.dailyAvgTicketThreshold = dailyAvgTicketThreshold;
    }

    public double getWeeklyAvgTicketThreshold()
    {
        return weeklyAvgTicketThreshold;
    }

    public void setWeeklyAvgTicketThreshold(double weeklyAvgTicketThreshold)
    {
        this.weeklyAvgTicketThreshold = weeklyAvgTicketThreshold;
    }

    public double getMonthlyAvgTicketThreshold()
    {
        return monthlyAvgTicketThreshold;
    }

    public void setMonthlyAvgTicketThreshold(double monthlyAvgTicketThreshold)
    {
        this.monthlyAvgTicketThreshold = monthlyAvgTicketThreshold;
    }

   /* public double getMonthly_avgticket_amount()
    {
        return monthly_avgticket_amount;
    }

    public void setMonthly_avgticket_amount(double monthly_avgticket_amount)
    {
        this.monthly_avgticket_amount = monthly_avgticket_amount;
    }*/

    public double getDailyCBRatioAmount()
    {
        return dailyCBRatioAmount;
    }

    public void setDailyCBRatioAmount(double dailyCBRatioAmount)
    {
        this.dailyCBRatioAmount = dailyCBRatioAmount;
    }

    public double getWeeklyCBRatioAmount()
    {
        return weeklyCBRatioAmount;
    }

    public void setWeeklyCBRatioAmount(double weeklyCBRatioAmount)
    {
        this.weeklyCBRatioAmount = weeklyCBRatioAmount;
    }

    public double getDailyRFAmountRatio()
    {
        return dailyRFAmountRatio;
    }

    public void setDailyRFAmountRatio(double dailyRFAmountRatio)
    {
        this.dailyRFAmountRatio = dailyRFAmountRatio;
    }

    public double getWeeklyRFAmountRatio()
    {
        return weeklyRFAmountRatio;
    }

    public void setWeeklyRFAmountRatio(double weeklyRFAmountRatio)
    {
        this.weeklyRFAmountRatio = weeklyRFAmountRatio;
    }

    public double getMonthlyRFAmountRatio()
    {
        return monthlyRFAmountRatio;
    }

    public void setMonthlyRFAmountRatio(double monthlyRFAmountRatio)
    {
        this.monthlyRFAmountRatio = monthlyRFAmountRatio;
    }

    public double getMonthlyCBRatioAmount()
    {
        return monthlyCBRatioAmount;
    }

    public void setMonthlyCBRatioAmount(double monthlyCBRatioAmount)
    {
        this.monthlyCBRatioAmount = monthlyCBRatioAmount;
    }

    public int getResumeProcessingAlert()
    {
        return resumeProcessingAlert;
    }

    public void setResumeProcessingAlert(int resumeProcessingAlert)
    {
        this.resumeProcessingAlert = resumeProcessingAlert;
    }

    public double getDailyAvgTicketPercentageThreshold()
    {
        return dailyAvgTicketPercentageThreshold;
    }

    public void setDailyAvgTicketPercentageThreshold(double dailyAvgTicketPercentageThreshold)
    {
        this.dailyAvgTicketPercentageThreshold = dailyAvgTicketPercentageThreshold;
    }

    public double getDailyCBRatioSuspension()
    {
        return dailyCBRatioSuspension;
    }

    public void setDailyCBRatioSuspension(double dailyCBRatioSuspension)
    {
        this.dailyCBRatioSuspension = dailyCBRatioSuspension;
    }

    public double getDailyCBAmountRatioSuspension()
    {
        return dailyCBAmountRatioSuspension;
    }

    public void setDailyCBAmountRatioSuspension(double dailyCBAmountRatioSuspension)
    {
        this.dailyCBAmountRatioSuspension = dailyCBAmountRatioSuspension;
    }

    public int getSameCardSameAmountConsequenceThreshold()
    {
        return sameCardSameAmountConsequenceThreshold;
    }

    public void setSameCardSameAmountConsequenceThreshold(int sameCardSameAmountConsequenceThreshold)
    {
        this.sameCardSameAmountConsequenceThreshold = sameCardSameAmountConsequenceThreshold;
    }

    public int getSameCardConsequentlyThreshold()
    {
        return sameCardConsequentlyThreshold;
    }

    public void setSameCardConsequentlyThreshold(int sameCardConsequentlyThreshold)
    {
        this.sameCardConsequentlyThreshold = sameCardConsequentlyThreshold;
    }
}