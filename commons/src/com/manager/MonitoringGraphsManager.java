package com.manager;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.dao.MonitoringGraphsDAO;
import com.manager.helper.RatioCalculationHelper;
import com.manager.vo.TerminalVO;
import com.manager.vo.TransactionSummaryVO;
import com.manager.vo.merchantmonitoring.DateVO;
import com.manager.vo.merchantmonitoring.MonitoringParameterVO;
import com.manager.vo.merchantmonitoring.enums.MonitoringAlertType;
import com.manager.vo.merchantmonitoring.enums.MonitoringCategory;
import com.manager.vo.merchantmonitoring.enums.MonitoringKeyword;
import com.manager.vo.merchantmonitoring.enums.MonitoringSubKeyword;
import com.payment.exceptionHandler.PZDBViolationException;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by admin on 8/31/2016.
 */
public class MonitoringGraphsManager
{
    public static Logger log=new Logger(MonitoringGraphsManager.class.getName());
    MonitoringGraphsDAO monitoringGraphsDAO =new MonitoringGraphsDAO();
    MerchantMonitoringManager merchantMonitoringManager = new MerchantMonitoringManager();
    RatioCalculationHelper ratioCalculationHelper = new RatioCalculationHelper();
    Functions functions=new Functions();
    public HashMap getBlacklistCountryCountFromTransaction(TerminalVO terminalVO, DateVO dateVO)throws PZDBViolationException
    {
        return monitoringGraphsDAO.getBlacklistCountryCountFromTransaction(terminalVO, dateVO);
    }
    public HashMap getBlacklistIPCountFromTransaction(TerminalVO terminalVO, DateVO dateVO)throws PZDBViolationException
    {
        return monitoringGraphsDAO.getBlacklistIPCountFromTransaction(terminalVO, dateVO);
    }
    public HashMap getBlacklistEmailCountFromTransaction(TerminalVO terminalVO, DateVO dateVO)throws PZDBViolationException
    {
        return monitoringGraphsDAO.getBlacklistEmailCountFromTransaction(terminalVO, dateVO);
    }
    public HashMap getBlacklistNameCountFromTransaction(TerminalVO terminalVO, DateVO dateVO)throws PZDBViolationException
    {
        return monitoringGraphsDAO.getBlacklistNameCountFromTransaction(terminalVO, dateVO);
    }
    public HashMap getBlacklistCardCountFromTransaction(TerminalVO terminalVO, DateVO dateVO)throws PZDBViolationException
    {
        return monitoringGraphsDAO.getBlacklistCardCountFromTransaction(terminalVO, dateVO);
    }
    public TransactionSummaryVO getDataForDataForDeclineAuthorization(TerminalVO terminalVO, String startDate, String endDate)throws Exception
    {
        return monitoringGraphsDAO.getDataForDataForDeclineAuthorization(terminalVO, startDate, endDate);
    }
    public TransactionSummaryVO getDataForDataForChargebackRatio(TerminalVO terminalVO, String startDate, String endDate)throws PZDBViolationException
    {
        return monitoringGraphsDAO.getDataForDataForChargebackRatio(terminalVO, startDate, endDate);
    }
    public TransactionSummaryVO getRefundAndChargebackDataByTimeStamp(TerminalVO terminalVO, String startDate, String endDate)throws PZDBViolationException
    {
        return monitoringGraphsDAO.getRefundAndChargebackDataByTimeStamp(terminalVO,startDate,endDate);
    }
    public HashMap getDataForDataForForeignSales(TerminalVO terminalVO, String startDate, String endDate)throws PZDBViolationException
    {
        return monitoringGraphsDAO.getDataForDataForForeignSales(terminalVO, startDate, endDate);
    }
    public long getMatureOperationTransactionsCount(TerminalVO terminalVO,DateVO dateVO,String status,int days) throws PZDBViolationException
    {
        return monitoringGraphsDAO.getMatureOperationTransactionsCount(terminalVO,dateVO,status,days);
    }
    public long getTransDetailsFromBlackListedCountry(TerminalVO terminalVO,DateVO dateVO, String country)throws PZDBViolationException
    {
        return  monitoringGraphsDAO.getTransDetailsFromBlackListedCountry(terminalVO,dateVO,country);
    }
    public long getTransDetailsFromBlackListedIP(TerminalVO terminalVO,DateVO dateVO, String requestedip)throws PZDBViolationException
    {
        return  monitoringGraphsDAO.getTransDetailsFromBlackListedIP(terminalVO, dateVO, requestedip);
    }
    public long getTransDetailsFromBlackListedCard(TerminalVO terminalVO,DateVO dateVO, String first_six, String last_four)throws PZDBViolationException
    {
        return  monitoringGraphsDAO.getTransDetailsFromBlackListedCard(terminalVO, dateVO, first_six, last_four);
    }
    public long getTransDetailsFromBlackListedEmails(TerminalVO terminalVO,DateVO dateVO, String emailAddress)throws PZDBViolationException
    {
        return  monitoringGraphsDAO.getTransDetailsFromBlackListedEmails(terminalVO, dateVO, emailAddress);
    }
    public long getTransDetailsFromBlackListedNames(TerminalVO terminalVO,DateVO dateVO, String firstName, String lastName)throws PZDBViolationException
    {
        return  monitoringGraphsDAO.getTransDetailsFromBlackListedNames(terminalVO, dateVO, firstName, lastName);
    }
    public List<String> getListOfBlacklistedCountry()throws PZDBViolationException
    {
        return  monitoringGraphsDAO.getListOfBlacklistedCountry();
    }
    public List<String> getListOfBlacklistedIP()throws PZDBViolationException
    {
        return  monitoringGraphsDAO.getListOfBlacklistedIP();
    }
    public List<String> getListOfBlacklistedCard()throws PZDBViolationException
    {
        return  monitoringGraphsDAO.getListOfBlacklistedCard();
    }
    public List<String> getListOfBlacklistedEmails()throws PZDBViolationException
    {
        return  monitoringGraphsDAO.getListOfBlacklistedEmails();
    }
    public List<String> getListOfBlacklistedNames()throws PZDBViolationException
    {
        return  monitoringGraphsDAO.getListOfBlacklistedNames();
    }
    public String getMerchantLastSubmission(TerminalVO terminalVO)throws PZDBViolationException
    {
        return monitoringGraphsDAO.getMerchantLastSubmission(terminalVO);
    }
    public String getMerchantFirstSubmission(TerminalVO terminalVO)throws PZDBViolationException
    {
        return monitoringGraphsDAO.getMerchantFirstSubmission(terminalVO);
    }
    public Hashtable getDataForPendingTransactions(TerminalVO terminalVO)throws PZDBViolationException
    {
        return monitoringGraphsDAO.getDataForPendingTransactions(terminalVO);
    }

    public HashMap getTransactionCountBasedOnBinCategory(TerminalVO terminalVO, DateVO dateVO,int[] categoryArray1,int[] categoryArray2,int[] categoryArray3,int[] categoryArray4) throws PZDBViolationException
    {
        return monitoringGraphsDAO.getTransactionCountBasedOnBinCategory(terminalVO, dateVO,categoryArray1,categoryArray2,categoryArray3,categoryArray4);
    }

    public HashMap getTransactionCountBasedOnAmountCategory(TerminalVO terminalVO, DateVO dateVO,int[] categoryArray1,int[] categoryArray2,int[] categoryArray3,int[] categoryArray4)throws PZDBViolationException
    {
        return monitoringGraphsDAO.getTransactionCountBasedOnAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
    }

    public HashMap getTransactionCountBasedOnBinUsedDays(TerminalVO terminalVO,DateVO dateVO,int[] categoryArray1,int[] categoryArray2,int[] categoryArray3,int[] categoryArray4)throws PZDBViolationException
    {
        return monitoringGraphsDAO.getTransactionCountBasedOnBinUsedDays(terminalVO,dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
    }
    public double monitoringLineChartHelper(TransactionSummaryVO transactionSummaryVO, MonitoringParameterVO monitoringParameterVO)
    {
        double plotData = 0.00;

        if (MonitoringCategory.Failure.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            plotData = ratioCalculationHelper.calculateDeclineRatioCount((int) transactionSummaryVO.getTotalProcessingCount(), (int) transactionSummaryVO.getCountOfAuthfailed());
        }
        else if (MonitoringCategory.Failure.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            plotData = ratioCalculationHelper.calculateDeclineRatioAmount(transactionSummaryVO.getTotalProcessingAmount(), transactionSummaryVO.getAuthfailedAmount());
        }
        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.AuthSuccessful.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            plotData = ratioCalculationHelper.calculateCBRatioCount((int) transactionSummaryVO.getTotalProcessingCount(), (int) transactionSummaryVO.getAuthSuccessCount());
        }
        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.AuthSuccessful.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            plotData = ratioCalculationHelper.calculateCBRatioAmount(transactionSummaryVO.getTotalProcessingAmount(), transactionSummaryVO.getAuthSuccessAmount());
        }
        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            plotData = ratioCalculationHelper.calculateCBRatioCount((int) transactionSummaryVO.getTotalProcessingCount(), (int) transactionSummaryVO.getCountOfChargeback());
        }
        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            plotData = ratioCalculationHelper.calculateCBRatioAmount(transactionSummaryVO.getTotalProcessingAmount(), transactionSummaryVO.getChargebackAmount());
        }
        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Reversed.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            plotData = ratioCalculationHelper.calculateRFRatioCount((int) transactionSummaryVO.getTotalProcessingCount(), (int) transactionSummaryVO.getCountOfReversed());
        }
        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Reversed.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
        {
            plotData = ratioCalculationHelper.calculateRFRatioAmount(transactionSummaryVO.getTotalProcessingAmount(), transactionSummaryVO.getReversedAmount());
        }

        return plotData;
    }
    public double calculateTransactionHigherLowerTicket(double actualAvgTicketAmount,double contractedAvgTicketAmount)
    {
        double plotData=0.00;
        if (contractedAvgTicketAmount > 0)
        {
            plotData = (actualAvgTicketAmount / contractedAvgTicketAmount) * 100;
            plotData = plotData - 100;
        }
        return Functions.roundDBL(plotData, 2);
    }
    public void appendData(StringBuffer data1,StringBuffer data2,StringBuffer data3,StringBuffer data4)
    {
        if (data1.length() > 0)
        {
            data1.append(",");
        }
        if (data2.length() > 0)
        {
            data2.append(",");
        }
        if (data3.length() > 0)
        {
            data3.append(",");
        }
        if (data4.length() > 0)
        {
            data4.append(",");
        }
    }
    public String prepareLineChartJSON(StringBuffer data1,StringBuffer data2,StringBuffer data3,StringBuffer data4,String displayRuleName,MonitoringParameterVO monitoringParameterVO,double alertThreshold,double suspensionThreshold)
    {

        String dailyJsonStr="";
        if(MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()))
        {
            dailyJsonStr = "{\"labels\":[" + data4.toString() + "],\"datasets\":[{\"label\":\"" + displayRuleName + "\",\"data\":[" + data1.toString() + "],\"fillColor\":\"#5DADE2\",\"borderColor\":\"#5DADE2\"}]}";
        }
        else
        {
            dailyJsonStr = "{\"labels\":[" + data4.toString() + "],\"datasets\":[{\"label\":\"" + displayRuleName + "\",\"data\":[" + data1.toString() + "],\"fillColor\":\"#5DADE2\",\"borderColor\":\"#5DADE2\"},{\"label\":\"Alert Threshold - " + Functions.round(alertThreshold, 2) + "%\",\"data\":[" + data2.toString() + "],\"lineTension\":0,\"fill\":false,\"backgroundColor\":\"#FF8C00\",\"borderColor\":\"#FF8C00\"},{\"label\":\"Suspension Threshold - " + Functions.round(suspensionThreshold, 2) + "%\",\"data\":[" + data3.toString() + "],\"lineTension\":0,\"fill\":false,\"backgroundColor\":\"#DC143C\",\"borderColor\":\"#DC143C\"}]}";
        }
        return dailyJsonStr;
    }
    public StringBuffer prepareLineChartTable(StringBuffer data1,StringBuffer data2,StringBuffer data3,StringBuffer data4,String displayRuleName,String monitoringUnit,MonitoringParameterVO monitoringParameterVO,String ruleFrequency,String tableHeaderFreq)
    {
        StringBuffer dailyTableData = new StringBuffer();

        String labelNameArr[] = data4.toString().split(",");
        String alertThresholdArr[] = data2.toString().split(",");
        String suspensionThresholdArr[] = data3.toString().split(",");
        String actualRatioArr[] = data1.toString().split(",");

        dailyTableData.append("<table border=\"1\" style=\"width:90%; height:130px;\">");
        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" class=\"th0\" colspan=\""+labelNameArr.length +1+"\">" + displayRuleName + " "+tableHeaderFreq+"" + "</td>");
        dailyTableData.append("</tr>");
        dailyTableData.append("<tr>");
        dailyTableData.append("<td class=\"th0\"></td>");

        for (int i = 0; i < labelNameArr.length; i++)
        {
            dailyTableData.append("<td align=\"center\" class=\"th0\">" + labelNameArr[i].replace("\"", "") + "</td>");
        }

        dailyTableData.append("</tr>");
        dailyTableData.append("<tr>");
        if(MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()))
        {
            dailyTableData.append("<td align=\"center\" class=\"th0\">"+ruleFrequency+" Actual Count [" + monitoringUnit + "] </td>");
        }
        else
        {
            dailyTableData.append("<td align=\"center\" class=\"th0\">"+ruleFrequency+" Actual Ratio [" + monitoringUnit + "] </td>");
        }
        for (int i = 0; i < actualRatioArr.length; i++)
        {
            try
            {
                dailyTableData.append("<td align=\"center\">" + Integer.parseInt(actualRatioArr[i]) + "</td>");
            }
            catch (Exception e)
            {
                dailyTableData.append("<td align=\"center\">" + Functions.round(Double.valueOf(actualRatioArr[i]), 2) + "</td>");
            }
        }
        dailyTableData.append("</tr>");

        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" class=\"th0\">"+ruleFrequency+" Alert Threshold [" + monitoringUnit + "] </td>");
        for (int i = 0; i < alertThresholdArr.length; i++)
        {
            try
            {
                dailyTableData.append("<td align=\"center\" bgcolor=\"#D5DBDB\">" + Integer.parseInt(alertThresholdArr[i]) + "</td>");
            }
            catch (Exception e)
            {
                dailyTableData.append("<td align=\"center\" bgcolor=\"#D5DBDB\">" + Functions.round(Double.valueOf(alertThresholdArr[i]), 2) + "</td>");
            }
        }
        dailyTableData.append("</tr>");

        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" class=\"th0\">"+ruleFrequency+" Suspension Threshold [" + monitoringUnit + "] </td>");
        for (int i = 0; i < suspensionThresholdArr.length; i++)
        {
            try
            {
                dailyTableData.append("<td align=\"center\" bgcolor=\"#D5DBDB\">" + Integer.parseInt(suspensionThresholdArr[i]) + "</td>");
            }
            catch (Exception e)
            {
                dailyTableData.append("<td align=\"center\" bgcolor=\"#D5DBDB\">" + Functions.round(Double.valueOf(suspensionThresholdArr[i]), 2) + "</td>");
            }
        }
        dailyTableData.append("</tr>");
        dailyTableData.append("</table>");

        return dailyTableData;
    }

    public StringBuffer prepareLineChartTableForPartner(StringBuffer data1, StringBuffer data2, StringBuffer data3, StringBuffer data4, String displayRuleName, String monitoringUnit, MonitoringParameterVO monitoringParameterVO, String ruleFrequency, String tableHeaderFreq)
    {
        StringBuffer dailyTableData = new StringBuffer();

        String labelNameArr[] = data4.toString().split(",");
        String alertThresholdArr[] = data2.toString().split(",");
        String suspensionThresholdArr[] = data3.toString().split(",");
        String actualRatioArr[] = data1.toString().split(",");


        dailyTableData.append("<table align=center width=\"50%\" class=\"display table table table-striped table-bordered table-hover dataTable\" style=\"font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;\">");
        //dailyTableData.append("<table border=\"1\" style=\"width:90%; height:130px;\">");
        dailyTableData.append("<thead>");
        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white;\" colspan=\"" + labelNameArr.length + 1 + "\">" + displayRuleName + " " + tableHeaderFreq + "" + "</td>");
        dailyTableData.append("</tr>");
        dailyTableData.append("<tr>");
        dailyTableData.append("<td style=\"background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;\"></td>");
        for (int i = 0; i < labelNameArr.length; i++)
        {
            dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white;\">" + labelNameArr[i].replace("\"", "") + "</td>");
        }

        dailyTableData.append("</tr>");
        dailyTableData.append("</thead>");
        dailyTableData.append("<tr>");
        if (MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()))
        {
            dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white;\" >" + ruleFrequency + " Actual Count </td>");
        }
        else
        {
            dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white;\" >" + ruleFrequency + " Actual Ratio [" + monitoringUnit + "] </td>");
        }
        for (int i = 0; i < actualRatioArr.length; i++)
        {
            try
            {
                dailyTableData.append("<td align=\"center\" >" + Integer.parseInt(actualRatioArr[i]) + "</td>");
            }
            catch (Exception e)
            {
                dailyTableData.append("<td align=\"center\">" + Functions.round(Double.valueOf(actualRatioArr[i]), 2) + "</td>");
            }
        }
        dailyTableData.append("</tr>");

        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white;\">" + ruleFrequency + " Alert Threshold [" + monitoringUnit + "] </td>");
        for (int i = 0; i < alertThresholdArr.length; i++)
        {
            try
            {
                dailyTableData.append("<td align=\"center\">" + Integer.parseInt(alertThresholdArr[i]) + "</td>");
            }
            catch (Exception e)
            {
                dailyTableData.append("<td align=\"center\">" + Functions.round(Double.valueOf(alertThresholdArr[i]), 2) + "</td>");
            }
        }
        dailyTableData.append("</tr>");

        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white;\">" + ruleFrequency + " Suspension Threshold [" + monitoringUnit + "] </td>");
        for (int i = 0; i < suspensionThresholdArr.length; i++)
        {
            try
            {
                dailyTableData.append("<td align=\"center\">" + Integer.parseInt(suspensionThresholdArr[i]) + "</td>");
            }
            catch (Exception e)
            {
                dailyTableData.append("<td align=\"center\">" + Functions.round(Double.valueOf(suspensionThresholdArr[i]), 2) + "</td>");
            }
        }
        dailyTableData.append("</tr>");
        dailyTableData.append("</table>");

        return dailyTableData;
    }

    public StringBuffer prepareDoughnutTableForPartner(StringBuffer data1, StringBuffer data2, String merchantCountry, String monitoringUnit, String tableHeader)
    {
        String countryName[] = data2.toString().split(",");
        String countryValue[] = data1.toString().split(",");
        StringBuffer dailyTableData = new StringBuffer();
        dailyTableData.append("<table align=center width=\"50%\" class=\"display table table table-striped table-bordered table-hover dataTable\" style=\"font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;\">");
        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \"colspan=\"2\">" + tableHeader + "</td>");
        dailyTableData.append("</tr>");
        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \"colspan=\"2\">Domestic Country - " + merchantCountry + "</td>");
        dailyTableData.append("</tr>");
        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white;\">Country</td>");
        dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white;\">Sales (" + monitoringUnit + ")</td>");
        dailyTableData.append("</tr>");
        for (int i = 0; i < countryName.length; i++)
        {
            dailyTableData.append("<tr>");
            dailyTableData.append("<td align=\"center\">" + countryName[i].replace("\"", "") + "</td>");
            dailyTableData.append("<td align=\"center\">" + countryValue[i] + "%" + "</td>");
            dailyTableData.append("</tr>");
        }
        dailyTableData.append("</table>");
        return dailyTableData;
    }
    public String prepareDoughnutChartJSON(StringBuffer data1,StringBuffer data2,StringBuffer data3)
    {
        return  "\"datasets\":[{\"data\":[" + data1.toString() + "],\"backgroundColor\":[" + data3.toString() + "]}],\"labels\":[" + data2.toString() + "]";
    }
    public StringBuffer prepareDoughnutTable(StringBuffer data1,StringBuffer data2,String merchantCountry,String monitoringUnit,String tableHeader)
    {
        String countryName[] = data2.toString().split(",");
        String countryValue[] = data1.toString().split(",");
        StringBuffer dailyTableData = new StringBuffer();
        dailyTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"2\">"+tableHeader+"</td>");
        dailyTableData.append("</tr>");
        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"2\">Domestic Country - " + merchantCountry + "</td>");
        dailyTableData.append("</tr>");
        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" class=\"th0\">Country</td>");
        dailyTableData.append("<td align=\"center\" class=\"th0\">Sales (" + monitoringUnit + ")</td>");
        dailyTableData.append("</tr>");
        for (int i = 0; i < countryName.length; i++)
        {
            dailyTableData.append("<tr>");
            dailyTableData.append("<td align=\"center\">" + countryName[i].replace("\"", "") + "</td>");
            dailyTableData.append("<td align=\"center\">" + countryValue[i] + "%" + "</td>");
            dailyTableData.append("</tr>");
        }
        dailyTableData.append("</table>");
        return dailyTableData;
    }
    public void appendDataForStackBarChart(StringBuffer catagoryData1,StringBuffer catagoryData2,StringBuffer catagoryData3,StringBuffer catagoryData4,StringBuffer label)
    {
        if (label.length() > 0)
        {
            label.append(",");
        }
        if (catagoryData1.length() > 0)
        {
            catagoryData1.append(",");
        }
        if (catagoryData2.length() > 0)
        {
            catagoryData2.append(",");
        }
        if (catagoryData3.length() > 0)
        {
            catagoryData3.append(",");
        }
        if (catagoryData4.length() > 0)
        {
            catagoryData4.append(",");
        }
    }

    public StringBuffer prepareStackBarChartTableForPartner(StringBuffer catagoryData1, StringBuffer catagoryData2, StringBuffer catagoryData3, StringBuffer catagoryData4, StringBuffer label, String category1, String category2, String category3, String category4, long data01, long data02, long data03, long data04, String displayRuleName, String tableHeaderFrequency, MonitoringParameterVO monitoringParameterVO)
    {
        String dateLabel[] = label.toString().split(",");
        String catagoryDataArr1[] = catagoryData1.toString().split(",");
        String catagoryDataArr2[] = catagoryData2.toString().split(",");
        String catagoryDataArr3[] = catagoryData3.toString().split(",");
        String catagoryDataArr4[] = catagoryData4.toString().split(",");

        StringBuffer dailyTableData = new StringBuffer();
        dailyTableData.append("<table align=center width=\"50%\" class=\"display table table table-striped table-bordered table-hover dataTable\" style=\"font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;\">");
        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \" colspan=\"" + dateLabel.length + +2 + "\">" + displayRuleName + " [" + tableHeaderFrequency + "]" + "</td>");
        dailyTableData.append("</tr>");

        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">&nbsp;</td>");
        for (int i = 0; i < dateLabel.length; i++)
        {
            dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + dateLabel[i].replace("\"", "") + "</td>");
        }
        dailyTableData.append("</tr>");

        dailyTableData.append("<tr>");
        if(MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && (MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword())) || MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()))
        {
            dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + category1 + " times" + "</td>");
        }
        else
        {
            dailyTableData.append("<td align=\"center\"style=\"background-color: #7eccad !important;color: white; \">" + category1 + "</td>");
        }
        for (int i = 0; i < catagoryDataArr1.length; i++)
        {
            dailyTableData.append("<td align=\"center\">" + catagoryDataArr1[i] + "</td>");
        }
        dailyTableData.append("</tr>");

        dailyTableData.append("<tr>");
        if(MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && (MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword())) || MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()))
        {
            dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + category2 + " times" + "</td>");
        }
        else
        {
            dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + category2 + "</td>");
        }
        for (int i = 0; i < catagoryDataArr2.length; i++)
        {
            dailyTableData.append("<td align=\"center\">" + catagoryDataArr2[i] + "</td>");
        }
        dailyTableData.append("</tr>");

        dailyTableData.append("<tr>");
        if(MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && (MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword())) || MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()))
        {
            dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + category3 + " times" + "</td>");
        }
        else
        {
            dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + category3 + "</td>");
        }
        for (int i = 0; i < catagoryDataArr3.length; i++)
        {
            dailyTableData.append("<td align=\"center\">" + catagoryDataArr3[i] + "</td>");
        }
        dailyTableData.append("</tr>");

        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + category4 + "</td>");
        for (int i = 0; i < catagoryDataArr4.length; i++)
        {
            dailyTableData.append("<td align=\"center\">" + catagoryDataArr4[i] + "</td>");
        }
        dailyTableData.append("</tr>");
        dailyTableData.append("</table>");

        return dailyTableData;
    }
    public StringBuffer prepareStackBarChartTable(StringBuffer catagoryData1,StringBuffer catagoryData2,StringBuffer catagoryData3,StringBuffer catagoryData4,StringBuffer label,String category1,String category2,String category3,String category4,long data01,long data02,long data03,long data04,String displayRuleName,String tableHeaderFrequency, MonitoringParameterVO monitoringParameterVO)
    {
        String dateLabel[] = label.toString().split(",");
        String catagoryDataArr1[] = catagoryData1.toString().split(",");
        String catagoryDataArr2[] = catagoryData2.toString().split(",");
        String catagoryDataArr3[] = catagoryData3.toString().split(",");
        String catagoryDataArr4[] = catagoryData4.toString().split(",");

        StringBuffer dailyTableData = new StringBuffer();
        dailyTableData.append("<table border=\"1\" style=\"width:90%; height:140px;\">");
        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" class=\"th0\" colspan=\""+ dateLabel.length + +2+"\">" + displayRuleName + " ["+tableHeaderFrequency+"]" + "</td>");
        dailyTableData.append("</tr>");

        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
        for (int i = 0; i < dateLabel.length; i++)
        {
            dailyTableData.append("<td align=\"center\" class=\"th0\">" + dateLabel[i].replace("\"", "") + "</td>");
        }
        dailyTableData.append("<td align=\"center\" class=\"th0\"> Total </td>");
        dailyTableData.append("</tr>");

        dailyTableData.append("<tr>");
        if(MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()))
        {
            dailyTableData.append("<td align=\"center\" class=\"th0\">" + category1 + " times"+ "</td>");
        }
        else
        {
            dailyTableData.append("<td align=\"center\" class=\"th0\">" + category1 + "</td>");
        }
        for (int i = 0; i < catagoryDataArr1.length; i++)
        {
            dailyTableData.append("<td align=\"center\">"+ catagoryDataArr1[i] +"</td>");
        }
        dailyTableData.append("<td align=\"center\">"+ data01 +"</td>");
        dailyTableData.append("</tr>");

        dailyTableData.append("<tr>");
        if(MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()))
        {
            dailyTableData.append("<td align=\"center\" class=\"th0\">" + category2 + " times" + "</td>");
        }
        else
        {
            dailyTableData.append("<td align=\"center\" class=\"th0\">" + category2 + "</td>");
        }
        for (int i = 0; i < catagoryDataArr2.length; i++)
        {
            dailyTableData.append("<td align=\"center\">"+ catagoryDataArr2[i] +"</td>");
        }
        dailyTableData.append("<td align=\"center\">"+ data02 +"</td>");
        dailyTableData.append("</tr>");

        dailyTableData.append("<tr>");
        if(MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()))
        {
            dailyTableData.append("<td align=\"center\" class=\"th0\">" + category3 + " times" + "</td>");
        }
        else
        {
            dailyTableData.append("<td align=\"center\" class=\"th0\">" + category3 + "</td>");
        }
        for(int i = 0; i < catagoryDataArr3.length; i++)
        {
            dailyTableData.append("<td align=\"center\">"+ catagoryDataArr3[i] +"</td>");
        }
        dailyTableData.append("<td align=\"center\">"+ data03 +"</td>");
        dailyTableData.append("</tr>");

        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" class=\"th0\">" + category4 + "</td>");
        for (int i = 0; i < catagoryDataArr4.length; i++)
        {
            dailyTableData.append("<td align=\"center\">"+ catagoryDataArr4[i] +"</td>");
        }
        dailyTableData.append("<td align=\"center\">"+ data04 +"</td>");
        dailyTableData.append("</tr>");
        dailyTableData.append("</table>");

        return dailyTableData;
    }
    public StringBuffer prepareProgressBarChartTable(int actualInactiveDays,int alertThresholdOrange,int suspensionThresholdRed,String displayName,String header,String monitoringUnit)
    {
        StringBuffer dailyTableData = new StringBuffer();
        dailyTableData.append("<table border=\"1\" style=\"width:90%; height:110px;\">");
        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"3\">"+header+"</td>");
        dailyTableData.append("</tr>");
        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" class=\"th0\">Alert Threshold <br> ("+monitoringUnit+")</td>");
        dailyTableData.append("<td align=\"center\" class=\"th0\">Suspension Threshold <br> ("+monitoringUnit+")</td>");
        dailyTableData.append("<td align=\"center\" class=\"th0\">"+displayName+"<br> ("+monitoringUnit+")</td>");
        dailyTableData.append("</tr>");
        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" bgcolor=\"#D5DBDB\">" + alertThresholdOrange + "</td>");
        dailyTableData.append("<td align=\"center\" bgcolor=\"#D5DBDB\">" + suspensionThresholdRed + "</td>");
        dailyTableData.append("<td align=\"center\">" + actualInactiveDays + "</td>");
        dailyTableData.append("</tr>");
        dailyTableData.append("</table>");
        return dailyTableData;
    }
    public List<int[]> getCategoryByThreshold(double alertThreshold,double suspensionThreshold)
    {
        List<int[]> ints=new LinkedList();
        int[] categoryArray1 = new int[2];
        int[] categoryArray2 = new int[2];
        int[] categoryArray3 = new int[2];
        int[] categoryArray4 = new int[2];
        int start1 = 0;
        int end1 = (int)alertThreshold;
        categoryArray1[0] = start1;
        categoryArray1[1] = end1;
        int start2 = end1 + 1;
        int end2 = start2 + (int) suspensionThreshold;
        categoryArray2[0] = start2;
        categoryArray2[1] = end2;
        int start3 = end2 + 1;
        int end3 = start3 + (int) suspensionThreshold;
        categoryArray3[0] = start3;
        categoryArray3[1] = end3;
        int start4 = end3 + 1;
        categoryArray4[0] = start4;

        ints.add(categoryArray1);
        ints.add(categoryArray2);
        ints.add(categoryArray3);
        ints.add(categoryArray4);
        return ints;
    }
    public void appendData(StringBuffer data1,StringBuffer data2,StringBuffer data3)
    {
        if (data1.length() > 0)
        {
            data1.append(",");
        }
        if (data2.length() > 0)
        {
            data2.append(",");
        }
        if (data3.length() > 0)
        {
            data3.append(",");
        }
    }

    public StringBuffer prepareProgressBarTableForPartner(int actualInactiveDays, int alertThresholdOrange, int suspensionThresholdRed, String displayName, String header, String monitoringUnit)
    {
        StringBuffer dailyTableData = new StringBuffer();
        dailyTableData.append("<table align=center width=\"50%\" class=\"display table table table-striped table-bordered table-hover dataTable\" style=\"font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;\">");
        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \"colspan=\"3\">" + header + "</td>");
        dailyTableData.append("</tr>");
        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white;\">Alert Threshold <br> (" + monitoringUnit + ")</td>");
        dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white;\">Suspension Threshold <br> (" + monitoringUnit + ")</td>");
        dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white;\">" + displayName + "<br> (" + monitoringUnit + ")</td>");
        dailyTableData.append("</tr>");
        dailyTableData.append("<tr>");
        dailyTableData.append("<td align=\"center\">" + alertThresholdOrange + "</td>");
        dailyTableData.append("<td align=\"center\">" + suspensionThresholdRed + "</td>");
        dailyTableData.append("<td align=\"center\">" + actualInactiveDays + "</td>");
        dailyTableData.append("</tr>");
        dailyTableData.append("</table>");
        return dailyTableData;
    }
    public double[] getMinMaxTransactionAmountByDate(TerminalVO terminalVO,DateVO dateVO)throws PZDBViolationException
    {
      return monitoringGraphsDAO.getMinMaxTransactionAmountByDate(terminalVO,dateVO);
    }
    public HashMap getCardCountBasedOnBinAndAmountCategory(TerminalVO terminalVO, DateVO dateVO,int[] categoryArray1,int[] categoryArray2,int[] categoryArray3,int[] categoryArray4)throws PZDBViolationException
    {
        return monitoringGraphsDAO.getCardCountBasedOnBinAndAmountCategory(terminalVO,dateVO,categoryArray1,categoryArray2,categoryArray3,categoryArray4);
    }
}
