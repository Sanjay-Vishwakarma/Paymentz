package com.manager.helper;

import com.directi.pg.Functions;

/**
 * Created by admin on 5/17/2016.
 */
public class RatioCalculationHelper
{
    public double calculateApprovalRatioCount(int salesCount, int declineCount)
    {
        int totalProcessingCount;
        double declineRatioCount = 0.00;
        totalProcessingCount = salesCount + declineCount;
        if (totalProcessingCount>0)
        {
            declineRatioCount = ((double)salesCount/(double)totalProcessingCount) * 100;
        }
        return Functions.roundDBL(declineRatioCount,2);
    }
    public double calculateDeclineRatioCount(int salesCount, int declineCount)
    {
        int totalProcessingCount;
        double declineRatioCount = 0.00;
        totalProcessingCount = salesCount + declineCount;
        if(totalProcessingCount > 0)
        {
            declineRatioCount = ((double)declineCount / (double)totalProcessingCount) * 100;
        }
        return Functions.roundDBL(declineRatioCount,2);
    }
    public double calculateCBRatioCount(int salesCount, int cbCount)
    {
        double cbRatioCount = 0.00;
        if(salesCount>0)
        {
            cbRatioCount = ((double)cbCount/(double)salesCount) * 100;
        }
        return Functions.roundDBL(cbRatioCount,2);
    }
    public double calculateRFRatioCount(int salesCount, int rfCount)
    {
        double rfRatioCount = 0.00;
        if (salesCount > 0)
        {
            rfRatioCount = ((double)rfCount / (double)salesCount) * 100;
        }
        return Functions.roundDBL(rfRatioCount,2);
    }

    public double calculateDeclineRatioAmount(double salesAmount, double declineAmount)
    {
        double totalProcessingAmount;
        double declineRatioAmt = 0.00;
        totalProcessingAmount = salesAmount + declineAmount;
        if(totalProcessingAmount > 0)
        {
            declineRatioAmt = (declineAmount/totalProcessingAmount) * 100;
        }
        return Functions.roundDBL(declineRatioAmt,2);
    }
    public double calculateApprovalRatioAmount(double salesAmount, double declineAmount)
    {
        double totalProcessingAmount;
        double approvalRatioAmt = 0.00;
        totalProcessingAmount = salesAmount + declineAmount;
        if (totalProcessingAmount > 0)
        {
            approvalRatioAmt = (salesAmount/totalProcessingAmount) * 100;
        }
        return Functions.roundDBL(approvalRatioAmt,2);
    }
    public double calculateCBRatioAmount(double salesAmount, double cbAmount)
    {
        double cbRatioAmt = 0.00;
        if (salesAmount > 0)
        {
            cbRatioAmt = (cbAmount / salesAmount) * 100;
        }
        return Functions.roundDBL(cbRatioAmt,2);
    }
    public double calculateRFRatioAmount(double salesAmount, double rfAmount)
    {
        double rfRatioAmt = 0.00;
        if (salesAmount > 0)
        {
            rfRatioAmt = (rfAmount / salesAmount) * 100;
        }
        return Functions.roundDBL(rfRatioAmt,2);
    }

    public double calculateRefundDeviation(double salesAmount, double rfAmount)
    {
        double rfDeviationAmtRatio = 0.00;
        if (salesAmount > 0)
        {
            rfDeviationAmtRatio = (rfAmount / salesAmount) * 100;
        }
        else if(rfAmount>0)
        {
            rfDeviationAmtRatio=rfAmount/100*100;
        }
        return Functions.roundDBL(rfDeviationAmtRatio, 2);
    }
    public double calculateAvgTicketAmount(double salesAmount, int salesCount)
    {
        double AvgTicketAmount = 0.00;
        if (salesAmount > 0)
        {
            AvgTicketAmount = (salesAmount / salesCount);
        }
        return Functions.roundDBL(AvgTicketAmount, 2);
    }

}
