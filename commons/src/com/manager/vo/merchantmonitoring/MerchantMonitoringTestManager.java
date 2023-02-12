package com.manager.vo.merchantmonitoring;

import com.manager.MerchantMonitoringManager;

/**
 * Created by admin on 4/8/2016.
 */
public class MerchantMonitoringTestManager
{
    public static void main(String args[])throws Exception
    {
     //System.out.printf("welcome in programming");

     MerchantMonitoringManager merchantMonitoringManager=new MerchantMonitoringManager();

        //merchantMonitoringManager.dailyDeclinedRatioOnCountAlert();   //point 1 done

        //merchantMonitoringManager.dailyCBRatioOnCountAlert();         //point 2 done

        //merchantMonitoringManager.dailyCBRatioOnAmountAlert();        //point 2 done

        //merchantMonitoringManager.monthlySuspensionOrAlertOnCBCountThreshold();//extra point

        //merchantMonitoringManager.lastMonthsSalesVsCurrentMonthRefunds();//extra point.

        //merchantMonitoringManager.dailyNewTerminalFirstSubmissionAlert();       //point 4

        //merchantMonitoringManager.dailyNewTerminalsResumeProcessingAlert();     //point 5

        //merchantMonitoringManager.monthlySalesVSContractedSalesAlert(); //point 6 & 12 removed

        //merchantMonitoringManager.weeklySalesVSContractedSalesAlert(); //point 6 & 12

        //merchantMonitoringManager.dailyTerminalSuspensionOnInactivityPeriodActionAlert();//point 7

        //merchantMonitoringManager.dailyAvgTicketAmountAlert();//point 8 & 9

        //merchantMonitoringManager.dailyRFRatioOnAmountAlert();//point 10   daily

        //merchantMonitoringManager.dailyManualCaptureAlert();//point 14

        //merchantMonitoringManager.dailyDayCB180DayThresholdAlert();//Extra point

        //merchantMonitoringManager.dailySameCardSameAmountAlert();//19

        //merchantMonitoringManager.dailySameCardSameAmountConsequenceAlert();

        //merchantMonitoringManager.dailySameCardConsequentlyAlert();//23

//     merchantMonitoringManager.monthlyRFRatioOnAmountAlert();// point 10  monthly ..Need to be pick transaction received on same month

        //merchantMonitoringManager.dailyVsLastThreeMonthAvgTicketAlert();//point 8 & 9
     //Not is used.

     //merchantMonitoringManager.monthlyApprovalRatioOnCountAlert(); //not in use.

     //merchantMonitoringManager.lastTwoMonthsChargebacksVsCurrentMonthSales();

     //merchantMonitoringManager.lastTwoMonthsRefundsVsCurrentMonthSales();

     //merchantMonitoringManager.lastThreeMonthAvgTicketAmountExceedsByCurrentMonth();

     //merchantMonitoringManager.monthlyAvgTicketAmountAlert();

     //merchantMonitoringManager.dailyAvgTicketAmountAlert();


        //merchantMonitoringManager.monthlyCBRatioOnCountAlert();       //point 2 done need to be check about calculations

        // merchantMonitoringManager.monthlyCBRatioOnAmountAlert();      //point 2 done

    }
}