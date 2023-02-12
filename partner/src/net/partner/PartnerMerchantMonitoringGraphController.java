package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.DateManager;
import com.manager.MerchantMonitoringManager;
import com.manager.MonitoringGraphsManager;
import com.manager.TerminalManager;
import com.manager.dao.MerchantDAO;
import com.manager.helper.RatioCalculationHelper;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.TransactionSummaryVO;
import com.manager.vo.TransactionVO;
import com.manager.vo.merchantmonitoring.*;
import com.manager.vo.merchantmonitoring.enums.*;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by sandip on 3/16/2017.
 */
public class PartnerMerchantMonitoringGraphController extends HttpServlet
{
    static Logger logger = new Logger(PartnerMerchantMonitoringGraphController.class.getName());
    private static final ResourceBundle countryName = LoadProperties.getProperty("com.directi.pg.countrycodenamepairlist");

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            doProcess(request, response);
        }
        catch (Exception e)
        {

            logger.error("Exception:::::" + e);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            doProcess(request, response);
        }
        catch (Exception e)
        {

            logger.error("Exception:::::" + e);
        }
    }

    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner = new PartnerFunctions();
        Functions functions = new Functions();

        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        RequestDispatcher rd = null;
        String error = "";

        error = error + validateOptionalParameters(request);
        if (functions.isValueNull(error))
        {
            request.setAttribute("error",error);
            rd = request.getRequestDispatcher("/partnerRiskRuleGraph.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }

        String memberId = request.getParameter("memberid");
        String terminalId = request.getParameter("terminalid");
        String ruleId = request.getParameter("ruleid");
        String partnerid = request.getParameter("partnerid");
        try
        {
            if (!partner.isPartnerMemberMapped(memberId, partnerid))
            {
                rd = request.getRequestDispatcher("/partnerRiskRuleGraph.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
        }
        catch (Exception e)
        {
        
            logger.debug("Exception::::::" + e);
        }
        String hourlyFrequency = request.getParameter("ishourslyexecution");
        String dailyFrequency = request.getParameter("isdailyexecution");
        String weeklyFrequency = request.getParameter("isweeklyexecution");
        String monthlyFrequency = request.getParameter("ismonthlyexecution");

        String dailyJsonStr = "";
        String weeklyJsonStr = "";
        String weeklyNewJsonStr = "";
        String monthlyJsonStr = "";

        String monitoringUnit = "";

        StringBuffer dailyTableData = null;
        StringBuffer weeklyTableData = null;
        StringBuffer weeklyNewTableData = null;
        StringBuffer monthlyTableData = null;

        StringBuffer data1 = null;
        StringBuffer data2 = null;
        StringBuffer data3 = null;
        StringBuffer data4 = null;

        StringBuffer dailyTableDataToggle = null;
        StringBuffer weeklyTableDataToggle = null;
        StringBuffer monthlyTableDataToggle = null;
        DateManager dateManager = new DateManager();
        MerchantMonitoringManager merchantMonitoringManager = new MerchantMonitoringManager();
        MonitoringGraphsManager monitoringGraphsManager = new MonitoringGraphsManager();
        RatioCalculationHelper ratioCalculationHelper = new RatioCalculationHelper();
        TerminalManager terminalManager = new TerminalManager();
        MerchantDAO merchantDAO = new MerchantDAO();

        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date1 = new Date();
        logger.debug("before calling getMonitoringParameterDetails::::::" + date1.getTime());
        MonitoringParameterVO monitoringParameterVO = merchantMonitoringManager.getMonitoringParameterDetails(ruleId);
        logger.debug("After calling getMonitoringParameterDetails::::" + new Date().getTime());
        logger.debug("After calling this getMonitoringParameterDetails difference time::::" + (new Date().getTime() - date1.getTime()));

        Date date2 = new Date();
        logger.debug("before calling getMonitoringParameterDetailsFromMapping::::::" + date2.getTime());
        MonitoringParameterMappingVO monitoringParameterMappingVO = merchantMonitoringManager.getMonitoringParameterDetailsFromMapping(ruleId, memberId, terminalId);
        logger.debug("After calling getMonitoringParameterDetailsFromMapping::::" + new Date().getTime());
        logger.debug("After calling this getMonitoringParameterDetailsFromMapping difference time::::" + (new Date().getTime() - date2.getTime()));

        if (monitoringParameterMappingVO == null)
        {
            monitoringParameterMappingVO = merchantMonitoringManager.getMonitoringParameterFromMaster(ruleId);
        }
        if (monitoringParameterMappingVO == null)
        {
            rd = request.getRequestDispatcher("/partnerRiskRuleGraph.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }

        MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(memberId);
        String merchantCountry = merchantDetailsVO.getCountry();
        TerminalVO terminalVO = terminalManager.getActiveInActiveTerminalInfo(terminalId);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
        TerminalLimitsVO terminalLimitsVO = terminalManager.getMemberTerminalProcessingLimitVO(memberId, terminalId);

        Date date3 = new Date();
        logger.debug("before calling getMerchantFirstSubmission::::::" + date3.getTime());
        String firstTransactionDate = monitoringGraphsManager.getMerchantFirstSubmission(terminalVO);
        logger.debug("After calling getMerchantFirstSubmission::::" + new Date().getTime());
        logger.debug("After calling this getMerchantFirstSubmission difference time::::" + (new Date().getTime() - date3.getTime()));


        String currency = gatewayAccount.getCurrency();

        long merchantProcessingDays = 0;

        String isDailyExecution = monitoringParameterMappingVO.getIsDailyExecution();
        String isWeeklyExecution = monitoringParameterMappingVO.getIsWeeklyExecution();
        String isMonthlyExecution = monitoringParameterMappingVO.getIsMonthlyExecution();
        String displayChartType = monitoringParameterMappingVO.getDisplayChartType();
        String displayRuleName = monitoringParameterVO.getMonitoringParameterName();

        double alertThreshold = 0.00;
        double suspensionThreshold = 0.00;
        double lastThreeMonthActualAvgTicketAmount = 0.00;
        double currentDayActualAvgTicketAmount = 0.00;

        long data01 = 0;
        long data02 = 0;
        long data03 = 0;
        long data04 = 0;

        String todayDate = targetFormat.format(new Date());
        if (functions.isValueNull(firstTransactionDate))
        {
            merchantProcessingDays = Functions.DATEDIFF(firstTransactionDate, todayDate);
        }
        if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()) || MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.AuthSuccessful.toString().equals(monitoringParameterVO.getMonitoingKeyword())){
            if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()) || MonitoringKeyword.AuthSuccessful.toString().equals(monitoringParameterVO.getMonitoingKeyword())){
                monitoringUnit = "%";
            }
            else{
                monitoringUnit = currency;
            }
        }
        else{
            if (MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword())){
                monitoringUnit = " in Days";
            }
            else if (MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword())){
                monitoringUnit = "in Count";
            }
        }
        logger.debug("displayChartType::::" + displayChartType);
        if ("LineChart".equals(displayChartType))
        {
            rd = request.getRequestDispatcher("/partnerRiskRuleGraph.jsp?ctoken=" + user.getCSRFToken());
            String ruleFrequency = "";
            String tableHeaderFreq = "";
            String lables = "'actual', 'alert', 'suspension'";
            if ("hoursly".equals(hourlyFrequency))
            {
                StringBuffer dailyLineChartData = new StringBuffer();
                boolean isValid = true;
                tableHeaderFreq = " [today]";
                ruleFrequency = "Daily";
                alertThreshold = monitoringParameterMappingVO.getAlertThreshold();
                suspensionThreshold = monitoringParameterMappingVO.getSuspensionThreshold();
                if ("Y".equals(isDailyExecution))
                {
                    data1 = new StringBuffer();
                    data2 = new StringBuffer();
                    data3 = new StringBuffer();
                    data4 = new StringBuffer();

                    List<DateVO> list = dateManager.getTodaysLineChartQuarter(4);
                    TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO = new MerchantMonitoringManager().getLastThreeMonthProcessingDetailsAsPerCalendarMonth(terminalVO);
                    if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()) && MonitoringDeviation.Lower.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
                    {
                        data1.append("100.00");
                        data2.append(alertThreshold);
                        data3.append(suspensionThreshold);
                    }
                    else if (MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()))
                    {
                        data1.append("0");
                        data2.append(Double.valueOf(alertThreshold).intValue());
                        data3.append(Double.valueOf(suspensionThreshold).intValue());
                    }
                    else
                    {
                        data1.append("0.0");
                        data2.append(alertThreshold);
                        data3.append(suspensionThreshold);
                    }
                    data4.append("\"00:00\"");
                    int i = 1;
                    for (DateVO dateVO : list)
                    {
                        String startDate = dateVO.getStartDate();
                        String endDate = dateVO.getEndDate();
                        String labelName = dateVO.getDateLabel();
                        String graphLable = endDate;
                        double plotData = 0.00;
                        TransactionSummaryVO transactionSummaryVO = monitoringGraphsManager.getDataForDataForChargebackRatio(terminalVO, startDate, endDate);
                        if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Reversed.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                        {
                            TransactionSummaryVO refundAndChargebackTransactionSummaryVO = monitoringGraphsManager.getRefundAndChargebackDataByTimeStamp(terminalVO, startDate, endDate);
                            TerminalProcessingDetailsVO priorMonthProcessingDetailsVO = new MerchantMonitoringManager().getLastMonthProcessingDetailsAsPerCalendarMonth(terminalVO);
                            plotData = ratioCalculationHelper.calculateRefundDeviation(priorMonthProcessingDetailsVO.getSalesAmount(), refundAndChargebackTransactionSummaryVO.getReversedAmount());
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()) && MonitoringDeviation.Higher.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
                        {
                            if (merchantProcessingDays < 90)
                            {
                                double dailyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount() + transactionSummaryVO.getAuthfailedAmount();
                                double dailyTargetSalesAmount = terminalLimitsVO.getDailyAmountLimit();
                                if (dailyTargetSalesAmount > 0)
                                {
                                    plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(dailyActualSalesAmount, dailyTargetSalesAmount);
                                    if (plotData < 0)
                                    {
                                        plotData = 0.00;
                                    }
                                }
                            }
                            else
                            {
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()) && MonitoringDeviation.Lower.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
                        {
                            if (merchantProcessingDays < 90)
                            {
                                double dailyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount() + transactionSummaryVO.getAuthfailedAmount();
                                double dailyTargetSalesAmount = terminalLimitsVO.getDailyAmountLimit();
                                if (dailyTargetSalesAmount > 0)
                                {
                                    plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(dailyActualSalesAmount, dailyTargetSalesAmount);
                                    if (plotData < 0)
                                    {
                                        plotData = plotData * (-1);
                                    }
                                    else
                                    {
                                        plotData = 0.00;
                                    }
                                }
                            }
                            else
                            {
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                        {
                            if (merchantProcessingDays > 90)
                            {
                                double dailyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount();
                                double dailyTargetSalesAmount = lastThreeMonthProcessingDetailsVO.getSalesAmount() / 3;
                                if (dailyTargetSalesAmount > 0)
                                {
                                    plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(dailyActualSalesAmount, dailyTargetSalesAmount);
                                    if (plotData < 0)
                                    {
                                        plotData = 0.00;
                                    }
                                }
                            }
                            else
                            {
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                        {
                            if (merchantProcessingDays > 90)
                            {
                                lastThreeMonthActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(lastThreeMonthProcessingDetailsVO.getSalesAmount(), lastThreeMonthProcessingDetailsVO.getSalesCount());
                                currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(transactionSummaryVO.getTotalProcessingAmount(), (int) transactionSummaryVO.getTotalProcessingCount());
                                if (lastThreeMonthActualAvgTicketAmount > 0)
                                {
                                    plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(currentDayActualAvgTicketAmount, lastThreeMonthActualAvgTicketAmount);
                                    if (plotData < 0)
                                    {
                                        plotData = 0.00;
                                    }
                                }
                            }
                            else
                            {
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                        {
                            if (merchantProcessingDays < 90)
                            {
                                transactionSummaryVO = monitoringGraphsManager.getDataForDataForChargebackRatio(terminalVO, startDate, endDate);
                                double contractedAvgTicketAmount = terminalLimitsVO.getDailyAvgTicketAmount();
                                double actualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(transactionSummaryVO.getTotalProcessingAmount(), (int) transactionSummaryVO.getTotalProcessingCount());
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(actualAvgTicketAmount, contractedAvgTicketAmount);
                                if (plotData < 0)
                                {
                                    plotData = 0.00;
                                }
                            }
                            else
                            {
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                        {
                            plotData = monitoringGraphsManager.getMatureOperationTransactionsCount(terminalVO, dateVO, "reversed", (int) alertThreshold);
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                        {
                            plotData = monitoringGraphsManager.getMatureOperationTransactionsCount(terminalVO, dateVO, "chargeback", (int) alertThreshold);
                        }
                        else
                        {
                            plotData = monitoringGraphsManager.monitoringLineChartHelper(transactionSummaryVO, monitoringParameterVO);
                        }
                        monitoringGraphsManager.appendData(data1, data2, data3, data4);
                        if (MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()))
                        {
                            data1.append(Double.valueOf(plotData).intValue());
                            data2.append(Double.valueOf(alertThreshold).intValue());
                            data3.append(Double.valueOf(suspensionThreshold).intValue());
                        }
                        else
                        {
                            data1.append(plotData);
                            data2.append(alertThreshold);
                            data3.append(suspensionThreshold);
                        }
                        data4.append("\"" + labelName + "\"");
                        if (i == 1)
                        {
                            dailyLineChartData.append("{\"date\":\"" + startDate + "\",\"actual\":\"00.00\",\"alert\":\"" + alertThreshold + "\",\"suspension\": \"" + suspensionThreshold + "\"},");
                        }
                        dailyLineChartData.append("{\"date\":\"" + graphLable + "\",\"actual\":\"" + plotData + "\",\"alert\":\"" + alertThreshold + "\",\"suspension\": \"" + suspensionThreshold + "\"}");
                        if (i != list.size())
                        {
                            dailyLineChartData.append(",");
                        }
                        i = i + 1;
                    }
                    if (data1.length() > 0 && data2.length() > 0 && data3.length() > 0 && data4.length() > 0 && isValid)
                    {
                        dailyTableData = monitoringGraphsManager.prepareLineChartTableForPartner(data1, data2, data3, data4, displayRuleName, monitoringUnit, monitoringParameterVO, ruleFrequency, tableHeaderFreq);
                    }
                    dailyJsonStr = "{\"data\":[" + dailyLineChartData + "]}";
                }
                else
                {
                    logger.error("Frequency is not activated");
                }
            }
            if ("daily".equals(dailyFrequency))
            {
                boolean isValid = true;
                tableHeaderFreq = " [in current week]";
                ruleFrequency = "Daily";
                alertThreshold = monitoringParameterMappingVO.getAlertThreshold();
                suspensionThreshold = monitoringParameterMappingVO.getSuspensionThreshold();
                if ("Y".equals(isDailyExecution))
                {
                    StringBuffer weeklyLineChartData = new StringBuffer();
                    String startDate = "";
                    String endDate = "";
                    String labelName = "";

                    data1 = new StringBuffer();
                    data2 = new StringBuffer();
                    data3 = new StringBuffer();
                    data4 = new StringBuffer();

                    List<DateVO> list = dateManager.getWeeklyLineChartQuarter();
                    int i = 1;
                    for (DateVO dateVO : list)
                    {
                        startDate = dateVO.getStartDate();
                        endDate = dateVO.getEndDate();
                        labelName = dateVO.getDateLabel();
                        String graphLable = endDate.split(" ")[0];
                        double plotData = 0.00;
                        TransactionSummaryVO transactionSummaryVO = monitoringGraphsManager.getDataForDataForChargebackRatio(terminalVO, startDate, endDate);
                        if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Reversed.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                        {
                            TransactionSummaryVO refundAndChargebackTransactionSummaryVO = monitoringGraphsManager.getRefundAndChargebackDataByTimeStamp(terminalVO, startDate, endDate);
                            TerminalProcessingDetailsVO priorMonthProcessingDetailsVO = new MerchantMonitoringManager().getLastMonthProcessingDetailsAsPerCalendarMonth(terminalVO);
                            plotData = ratioCalculationHelper.calculateRefundDeviation(priorMonthProcessingDetailsVO.getSalesAmount(), refundAndChargebackTransactionSummaryVO.getReversedAmount());
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()) && MonitoringDeviation.Higher.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
                        {
                            if (merchantProcessingDays < 90)
                            {
                                double weeklyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount();
                                double weeklyTargetSalesAmount = terminalLimitsVO.getDailyAmountLimit();
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(weeklyActualSalesAmount, weeklyTargetSalesAmount);
                                if (plotData < 0)
                                {
                                    plotData = 0.00;
                                }
                            }
                            else
                            {
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()) && MonitoringDeviation.Lower.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
                        {
                            if (merchantProcessingDays < 90)
                            {
                                double weeklyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount();
                                double weeklyTargetSalesAmount = terminalLimitsVO.getDailyAmountLimit();
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(weeklyActualSalesAmount, weeklyTargetSalesAmount);
                                if (plotData < 0)
                                {
                                    plotData = plotData * (-1);
                                }
                                else
                                {
                                    plotData = 0.00;
                                }
                            }
                            else
                            {
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                        {
                            if (merchantProcessingDays > 90)
                            {
                                double weeklyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount();
                                double weeklyTargetSalesAmount = terminalLimitsVO.getDailyAmountLimit();
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(weeklyActualSalesAmount, weeklyTargetSalesAmount);
                                if (plotData < 0)
                                {
                                    plotData = 0.00;
                                }
                            }
                            else
                            {
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                        {
                            if (merchantProcessingDays > 90)
                            {
                                TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO = new MerchantMonitoringManager().getLastThreeMonthProcessingDetailsAsPerCalendarMonth(terminalVO);
                                lastThreeMonthActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(lastThreeMonthProcessingDetailsVO.getSalesAmount(), lastThreeMonthProcessingDetailsVO.getSalesCount());
                                currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(transactionSummaryVO.getTotalProcessingAmount(), (int) transactionSummaryVO.getTotalProcessingCount());
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(currentDayActualAvgTicketAmount, lastThreeMonthActualAvgTicketAmount);
                                if (plotData < 0)
                                {
                                    plotData = 0.00;
                                }
                            }
                            else
                            {
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                        {
                            if (merchantProcessingDays < 90)
                            {
                                double contractedAvgTicketAmount = terminalLimitsVO.getDailyAvgTicketAmount();
                                currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(transactionSummaryVO.getTotalProcessingAmount(), (int) transactionSummaryVO.getTotalProcessingCount());
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(currentDayActualAvgTicketAmount, contractedAvgTicketAmount);
                                if (plotData < 0)
                                {
                                    plotData = 0.00;
                                }
                            }
                            else
                            {
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                        {
                            plotData = monitoringGraphsManager.getMatureOperationTransactionsCount(terminalVO, dateVO, "reversed", (int) alertThreshold);
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                        {
                            plotData = monitoringGraphsManager.getMatureOperationTransactionsCount(terminalVO, dateVO, "chargeback", (int) alertThreshold);
                        }
                        else
                        {
                            plotData = monitoringGraphsManager.monitoringLineChartHelper(transactionSummaryVO, monitoringParameterVO);
                        }
                        monitoringGraphsManager.appendData(data1, data2, data3, data4);
                        if (MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()))
                        {
                            data1.append(Double.valueOf(plotData).intValue());
                            data2.append(Double.valueOf(alertThreshold).intValue());
                            data3.append(Double.valueOf(suspensionThreshold).intValue());
                        }
                        else
                        {
                            data1.append(plotData);
                            data2.append(alertThreshold);
                            data3.append(suspensionThreshold);
                        }
                        data4.append("\"" + labelName + "\"");
                        weeklyLineChartData.append("{\"date\":\"" + graphLable + "\",\"actual\":\"" + plotData + "\",\"alert\":\"" + alertThreshold + "\",\"suspension\": \"" + suspensionThreshold + "\"}");
                        if (i != list.size())
                        {
                            weeklyLineChartData.append(",");
                        }
                        i = i + 1;
                    }
                    if (data1.length() > 0 && data2.length() > 0 && data3.length() > 0 && data4.length() > 0 && isValid)
                    {
                        weeklyTableData = monitoringGraphsManager.prepareLineChartTableForPartner(data1, data2, data3, data4, displayRuleName, monitoringUnit, monitoringParameterVO, ruleFrequency, tableHeaderFreq);
                    }
                    else
                    {
                        logger.debug("Data Not Found");
                    }
                    weeklyJsonStr = "{\"data\":[" + weeklyLineChartData + "]}";
                }
            }
            if ("weekly".equals(weeklyFrequency))
            {
                boolean isValid = true;
                tableHeaderFreq = " [in current month]";
                ruleFrequency = "Weekly";
                if ("Y".equals(isWeeklyExecution))
                {
                    StringBuffer weeklyNewLineChartData = new StringBuffer();
                    String startDate = "";
                    String endDate = "";
                    String labelName = "";

                    data1 = new StringBuffer();
                    data2 = new StringBuffer();
                    data3 = new StringBuffer();
                    data4 = new StringBuffer();

                    alertThreshold = monitoringParameterMappingVO.getWeeklyAlertThreshold();
                    suspensionThreshold = monitoringParameterMappingVO.getWeeklySuspensionThreshold();
                    List<DateVO> list = dateManager.getWeeksOfMonthNew();
                    int i = 1;
                    for (DateVO dateVO : list)
                    {

                        startDate = dateVO.getStartDate();
                        endDate = dateVO.getEndDate();
                        labelName = dateVO.getDateLabel();
                        String graphLable = endDate.split(" ")[0];
                        double plotData = 0.00;
                        TransactionSummaryVO transactionSummaryVO = monitoringGraphsManager.getDataForDataForChargebackRatio(terminalVO, startDate, endDate);
                        if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Reversed.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                        {
                            TransactionSummaryVO refundAndChargebackTransactionSummaryVO = monitoringGraphsManager.getRefundAndChargebackDataByTimeStamp(terminalVO, startDate, endDate);
                            TerminalProcessingDetailsVO priorMonthProcessingDetailsVO = new MerchantMonitoringManager().getLastMonthProcessingDetailsAsPerCalendarMonth(terminalVO, dateVO);
                            plotData = ratioCalculationHelper.calculateRefundDeviation(priorMonthProcessingDetailsVO.getSalesAmount(), refundAndChargebackTransactionSummaryVO.getReversedAmount());
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()) && MonitoringDeviation.Higher.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
                        {
                            if (merchantProcessingDays < 90)
                            {
                                double monthlyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount();
                                double monthlyTargetSalesAmount = terminalLimitsVO.getWeeklyAmountLimit();
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(monthlyActualSalesAmount, monthlyTargetSalesAmount);
                                if (plotData < 0)
                                {
                                    plotData = 0.00;
                                }
                            }
                            else
                            {
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()) && MonitoringDeviation.Lower.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
                        {
                            if (merchantProcessingDays < 90)
                            {
                                double monthlyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount();
                                double monthlyTargetSalesAmount = terminalLimitsVO.getWeeklyAmountLimit();
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(monthlyActualSalesAmount, monthlyTargetSalesAmount);
                                if (plotData < 0)
                                {
                                    plotData = plotData * (-1);
                                }
                                else
                                {
                                    plotData = 0.00;
                                }
                            }
                            else
                            {
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                        {
                            if (merchantProcessingDays > 90)
                            {
                                double monthlyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount();
                                double monthlyTargetSalesAmount = terminalLimitsVO.getWeeklyAmountLimit();
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(monthlyActualSalesAmount, monthlyTargetSalesAmount);
                                if (plotData < 0)
                                {
                                    plotData = 0.00;
                                }
                            }
                            else
                            {
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                        {
                            if (merchantProcessingDays > 90)
                            {
                                TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO = new MerchantMonitoringManager().getLastThreeMonthProcessingDetailsAsPerCalendarMonth(terminalVO);
                                lastThreeMonthActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(lastThreeMonthProcessingDetailsVO.getSalesAmount(), lastThreeMonthProcessingDetailsVO.getSalesCount());
                                currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(transactionSummaryVO.getTotalProcessingAmount(), (int) transactionSummaryVO.getTotalProcessingCount());
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(currentDayActualAvgTicketAmount, lastThreeMonthActualAvgTicketAmount);
                                if (plotData < 0)
                                {
                                    plotData = 0.00;
                                }
                            }
                            else
                            {
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                        {
                            if (merchantProcessingDays < 90)
                            {
                                double contractedAvgTicketAmount = terminalLimitsVO.getWeeklyAvgTicketAmount();
                                currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(transactionSummaryVO.getTotalProcessingAmount(), (int) transactionSummaryVO.getTotalProcessingCount());
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(currentDayActualAvgTicketAmount, contractedAvgTicketAmount);
                                if (plotData < 0)
                                {
                                    plotData = 0.00;
                                }
                            }
                            else
                            {
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                        {
                            plotData = monitoringGraphsManager.getMatureOperationTransactionsCount(terminalVO, dateVO, "reversed", (int) alertThreshold);
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                        {
                            plotData = monitoringGraphsManager.getMatureOperationTransactionsCount(terminalVO, dateVO, "chargeback", (int) alertThreshold);
                        }
                        else
                        {
                            plotData = monitoringGraphsManager.monitoringLineChartHelper(transactionSummaryVO, monitoringParameterVO);
                        }
                        monitoringGraphsManager.appendData(data1, data2, data3, data4);
                        if (MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()))
                        {
                            data1.append(Double.valueOf(plotData).intValue());
                            data2.append(Double.valueOf(alertThreshold).intValue());
                            data3.append(Double.valueOf(suspensionThreshold).intValue());
                        }
                        else
                        {
                            data1.append(plotData);
                            data2.append(alertThreshold);
                            data3.append(suspensionThreshold);
                        }
                        data4.append("\"" + labelName + "\"");
                        weeklyNewLineChartData.append("{\"date\":\"" + graphLable + "\",\"actual\":\"" + plotData + "\",\"alert\":\"" + alertThreshold + "\",\"suspension\": \"" + suspensionThreshold + "\"}");
                        if (i != list.size())
                        {
                            weeklyNewLineChartData.append(",");
                        }
                        i = i + 1;
                    }
                    if (data1.length() > 0 && data2.length() > 0 && data3.length() > 0 && data4.length() > 0 && isValid)
                    {
                        weeklyNewTableData = monitoringGraphsManager.prepareLineChartTableForPartner(data1, data2, data3, data4, displayRuleName, monitoringUnit, monitoringParameterVO, ruleFrequency, tableHeaderFreq);
                    }
                    else
                    {
                        logger.debug("Data Not Found");
                    }
                    weeklyNewJsonStr = "{\"data\":[" + weeklyNewLineChartData + "]}";
                }
            }
            if ("monthly".equals(monthlyFrequency))
            {
                boolean isValid = true;
                ruleFrequency = "Monthly";
                tableHeaderFreq = " [In last six months]";
                if ("Y".equals(isMonthlyExecution))
                {
                    StringBuffer lineChartData = new StringBuffer();
                    String startDate = "";
                    String endDate = "";
                    String labelName = "";

                    data1 = new StringBuffer();
                    data2 = new StringBuffer();
                    data3 = new StringBuffer();
                    data4 = new StringBuffer();

                    alertThreshold = monitoringParameterMappingVO.getMonthlyAlertThreshold();
                    suspensionThreshold = monitoringParameterMappingVO.getMonthlySuspensionThreshold();

                    int i = 1;
                    List<DateVO> list = dateManager.getMonthlyLineChartQuarter();
                    for (DateVO dateVO : list)
                    {
                        startDate = dateVO.getStartDate();
                        endDate = dateVO.getEndDate();
                        labelName = dateVO.getDateLabel();
                        String graphLable = startDate.split(" ")[0];
                        double plotData = 0.00;

                        TransactionSummaryVO transactionSummaryVO = monitoringGraphsManager.getDataForDataForChargebackRatio(terminalVO, startDate, endDate);
                        if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Reversed.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                        {
                            TransactionSummaryVO refundAndChargebackTransactionSummaryVO = monitoringGraphsManager.getRefundAndChargebackDataByTimeStamp(terminalVO, startDate, endDate);
                            TerminalProcessingDetailsVO priorMonthProcessingDetailsVO = new MerchantMonitoringManager().getLastMonthProcessingDetailsAsPerCalendarMonth(terminalVO, dateVO);
                            plotData = ratioCalculationHelper.calculateRefundDeviation(priorMonthProcessingDetailsVO.getSalesAmount(), refundAndChargebackTransactionSummaryVO.getReversedAmount());
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()) && MonitoringDeviation.Higher.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
                        {
                            if (merchantProcessingDays < 90)
                            {
                                double monthlyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount();
                                double monthlyTargetSalesAmount = terminalLimitsVO.getMonthlyAmountLimit();
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(monthlyActualSalesAmount, monthlyTargetSalesAmount);
                                if (plotData < 0)
                                {
                                    plotData = 0.00;
                                }
                            }
                            else
                            {
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()) && MonitoringDeviation.Lower.toString().equals(monitoringParameterVO.getMonitoingDeviation()))
                        {
                            if (merchantProcessingDays < 90)
                            {
                                double monthlyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount();
                                double monthlyTargetSalesAmount = terminalLimitsVO.getMonthlyAmountLimit();
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(monthlyActualSalesAmount, monthlyTargetSalesAmount);
                                if (plotData < 0)
                                {
                                    plotData = plotData * (-1);
                                }
                                else
                                {
                                    plotData = 0.00;
                                }
                            }
                            else
                            {
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                        {
                            if (merchantProcessingDays > 90)
                            {
                                double monthlyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount();
                                double monthlyTargetSalesAmount = terminalLimitsVO.getMonthlyAmountLimit();
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(monthlyActualSalesAmount, monthlyTargetSalesAmount);
                                if (plotData < 0)
                                {
                                    plotData = 0.00;
                                }
                            }
                            else
                            {
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                        {
                            if (merchantProcessingDays > 90)
                            {
                                TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO = new MerchantMonitoringManager().getLastThreeMonthProcessingDetailsAsPerCalendarMonth(terminalVO, dateVO);
                                lastThreeMonthActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(lastThreeMonthProcessingDetailsVO.getSalesAmount(), lastThreeMonthProcessingDetailsVO.getSalesCount());
                                currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(transactionSummaryVO.getTotalProcessingAmount(), (int) transactionSummaryVO.getTotalProcessingCount());
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(currentDayActualAvgTicketAmount, lastThreeMonthActualAvgTicketAmount);
                                if (plotData < 0)
                                {
                                    plotData = 0.00;
                                }
                            }
                            else
                            {
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                        {
                            if (merchantProcessingDays < 90)
                            {
                                double contractedAvgTicketAmount = terminalLimitsVO.getMonthlyAvgTicketAmount();
                                currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(transactionSummaryVO.getTotalProcessingAmount(), (int) transactionSummaryVO.getTotalProcessingCount());
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(currentDayActualAvgTicketAmount, contractedAvgTicketAmount);
                                if (plotData < 0)
                                {
                                    plotData = 0.00;
                                }
                            }
                            else
                            {
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                        {
                            plotData = monitoringGraphsManager.getMatureOperationTransactionsCount(terminalVO, dateVO, "reversed", (int) alertThreshold);
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                        {
                            plotData = monitoringGraphsManager.getMatureOperationTransactionsCount(terminalVO, dateVO, "chargeback", (int) alertThreshold);
                        }
                        else
                        {
                            plotData = monitoringGraphsManager.monitoringLineChartHelper(transactionSummaryVO, monitoringParameterVO);
                        }
                        monitoringGraphsManager.appendData(data1, data2, data3, data4);
                        if (MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()))
                        {
                            data1.append(Double.valueOf(plotData).intValue());
                            data2.append(Double.valueOf(alertThreshold).intValue());
                            data3.append(Double.valueOf(suspensionThreshold).intValue());

                            lables = "'actual'";
                            lineChartData.append("{\"date\":\"" + graphLable + "\",\"actual\":\"" + plotData + "\"}");
                            if (i != list.size())
                            {
                                lineChartData.append(",");
                            }
                            i = i + 1;
                        }
                        else
                        {
                            data1.append(plotData);
                            data2.append(alertThreshold);
                            data3.append(suspensionThreshold);
                            lineChartData.append("{\"date\":\"" + graphLable + "\",\"actual\":\"" + plotData + "\",\"alert\":\"" + alertThreshold + "\",\"suspension\": \"" + suspensionThreshold + "\"}");
                            if (i != list.size())
                            {
                                lineChartData.append(",");
                            }
                            i = i + 1;
                        }
                        data4.append("\"" + labelName + "\"");
                    }
                    if (data1.length() > 0 && data2.length() > 0 && data3.length() > 0 && data4.length() > 0 && isValid)
                    {
                        monthlyTableData = monitoringGraphsManager.prepareLineChartTableForPartner(data1, data2, data3, data4, displayRuleName, monitoringUnit, monitoringParameterVO, ruleFrequency, tableHeaderFreq);
                    }
                    monthlyJsonStr = "{\"data\":[" + lineChartData + "]}";
                }
            }
            if (dailyTableData != null)
            {
                request.setAttribute("dailyTableData", dailyTableData.toString());
            }
            if (weeklyTableData != null)
            {
                request.setAttribute("weeklyTableData", weeklyTableData.toString());
            }
            if (weeklyNewTableData != null)
            {
                request.setAttribute("weeklyNewTableData", weeklyNewTableData.toString());
            }
            if (monthlyTableData != null)
            {
                request.setAttribute("monthlyTableData", monthlyTableData.toString());
            }
            request.setAttribute("displayRuleName", displayRuleName);
            request.setAttribute("dailyJsonStr", dailyJsonStr);
            request.setAttribute("weeklyJsonStr", weeklyJsonStr);
            request.setAttribute("weeklyNewJsonStr", weeklyNewJsonStr);
            request.setAttribute("monthlyJsonStr", monthlyJsonStr);
            request.setAttribute("monitoringUnit", monitoringUnit);
            request.setAttribute("lables", lables);
            rd.forward(request, response);
            return;
        }
        else if ("DoughnutChart".equals(displayChartType))
        {
            rd = request.getRequestDispatcher("/partnerRiskRuleDonutGraph.jsp?ctoken=" + user.getCSRFToken());
            String tableHeader = "";
            StringBuffer donutChartData = null;
            if ("daily".equals(dailyFrequency))
            {
                if ("Y".equals(isDailyExecution))
                {
                    donutChartData = new StringBuffer();
                    data1 = new StringBuffer();
                    data2 = new StringBuffer();
                    data3 = new StringBuffer();
                    DateVO dateVO = dateManager.getCurrentDayDateRange();
                    String startDate = dateVO.getStartDate();
                    String endDate = dateVO.getEndDate();
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.ForeignSales.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        HashMap<String, Double> foreignSales = monitoringGraphsManager.getDataForDataForForeignSales(terminalVO, startDate, endDate);
                        double totalProcessingAmount = foreignSales.get("totalAmount");
                        foreignSales.remove("totalAmount");
                        foreignSales.remove("totalCount");
                        tableHeader = "Foreign sales [today]";
                        if (totalProcessingAmount > 0)
                        {
                            int c = 0;
                            int i = 1;
                            Iterator iterator = foreignSales.keySet().iterator();
                            while (iterator.hasNext())
                            {
                                String countryLabel = (String) iterator.next();
                                String country = countryName.getString(countryLabel);
                                double amt = foreignSales.get(countryLabel);
                                double ratio = ((amt / totalProcessingAmount) * 100);
                                monitoringGraphsManager.appendData(data1, data2, data3);
                                data1.append(Functions.round(ratio, 2));
                                data2.append(country);
                                donutChartData.append("{value: " + Functions.round(ratio, 2) + ", label: '" + countryLabel + "'}");
                                if (i != foreignSales.size())
                                {
                                    donutChartData.append(",");
                                }
                                i++;
                            }
                        }
                    }
                    if ((data1.length() > 0) && (data2.length() > 0) /*&& (data3.length() > 0)*/)
                    {
                        dailyTableData = monitoringGraphsManager.prepareDoughnutTableForPartner(data1, data2, merchantCountry, monitoringUnit, tableHeader);
                        dailyJsonStr = "data: [" + donutChartData.toString() + "],";
                    }
                }
            }
            if ("weekly".equals(weeklyFrequency))
            {
                if ("Y".equals(isWeeklyExecution))
                {
                    donutChartData = new StringBuffer();
                    data1 = new StringBuffer();
                    data2 = new StringBuffer();
                    data3 = new StringBuffer();
                    DateVO dateVO = dateManager.getCalendarCurrentWeekDateRange();
                    String startDate = dateVO.getStartDate();
                    String endDate = dateVO.getEndDate();
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.ForeignSales.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        HashMap<String, Double> foreignSales = monitoringGraphsManager.getDataForDataForForeignSales(terminalVO, startDate, endDate);
                        double totalProcessingAmount = foreignSales.get("totalAmount");
                        foreignSales.remove("totalAmount");
                        foreignSales.remove("totalCount");
                        tableHeader = "Foreign sales [in current week]";
                        if (totalProcessingAmount > 0)
                        {
                            int c = 0;
                            int i = 1;
                            Iterator iterator = foreignSales.keySet().iterator();
                            while (iterator.hasNext())
                            {
                                String countryLabel = (String) iterator.next();
                                String country = countryName.getString(countryLabel);
                                double amt = foreignSales.get(countryLabel);
                                double ratio = ((amt / totalProcessingAmount) * 100);
                                monitoringGraphsManager.appendData(data1, data2, data3);
                                data1.append(Functions.round(ratio, 2));
                                data2.append(country);
                                donutChartData.append("{value: " + Functions.round(ratio, 2) + ", label: '" + countryLabel + "'}");
                                if (i != foreignSales.size())
                                {
                                    donutChartData.append(",");
                                }
                                i++;
                            }
                        }
                    }
                    if (data1.length() > 0 /*&& data3.length() > 0*/ && data2.length() > 0)
                    {
                        weeklyTableData = monitoringGraphsManager.prepareDoughnutTableForPartner(data1, data2, merchantCountry, monitoringUnit, tableHeader);
                        weeklyJsonStr = "data: [" + donutChartData.toString() + "],";
                    }
                }
            }
            if ("monthly".equals(monthlyFrequency))
            {
                if ("Y".equals(isMonthlyExecution))
                {
                    donutChartData = new StringBuffer();
                    data1 = new StringBuffer();
                    data2 = new StringBuffer();
                    data3 = new StringBuffer();
                    DateVO dateVO = dateManager.getCurrentMonthDateRange();
                    String startDate = dateVO.getStartDate();
                    String endDate = dateVO.getEndDate();
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.ForeignSales.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        HashMap<String, Double> foreignSales = monitoringGraphsManager.getDataForDataForForeignSales(terminalVO, startDate, endDate);
                        double totalProcessingAmount = foreignSales.get("totalAmount");
                        foreignSales.remove("totalAmount");
                        foreignSales.remove("totalCount");
                        tableHeader = "Foreign sales [in current month]";
                        if (totalProcessingAmount > 0)
                        {
                            int c = 0;
                            int i = 1;
                            Iterator iterator = foreignSales.keySet().iterator();
                            while (iterator.hasNext())
                            {
                                String countryLabel = (String) iterator.next();
                                String country = countryName.getString(countryLabel);
                                double amt = foreignSales.get(countryLabel);
                                double ratio = ((amt / totalProcessingAmount) * 100);
                                monitoringGraphsManager.appendData(data1, data2, data3);
                                data1.append(Functions.round(ratio, 2));
                                data2.append(country);
                                donutChartData.append("{value: " + Functions.round(ratio, 2) + ", label: '" + countryLabel + "'}");
                                if (i != foreignSales.size())
                                {
                                    donutChartData.append(",");
                                }
                                i++;
                            }
                        }
                    }
                    if (data1.length() > 0 && data2.length() > 0 /*&& data3.length() > 0*/)
                    {
                        monthlyTableData = monitoringGraphsManager.prepareDoughnutTableForPartner(data1, data2, merchantCountry, monitoringUnit, tableHeader);
                        monthlyJsonStr = "data: [" + donutChartData.toString() + "],";
                    }
                }
            }
            request.setAttribute("displayRuleName", displayRuleName);
            request.setAttribute("dailyJsonStr", dailyJsonStr);
            request.setAttribute("weeklyJsonStr", weeklyJsonStr);
            request.setAttribute("monthlyJsonStr", monthlyJsonStr);
            if (dailyTableData != null)
            {
                request.setAttribute("dailyTableData", dailyTableData.toString());
            }
            if (weeklyTableData != null)
            {
                request.setAttribute("weeklyTableData", weeklyTableData.toString());
            }
            if (monthlyTableData != null)
            {
                request.setAttribute("monthlyTableData", monthlyTableData.toString());
            }
            rd.forward(request, response);
            return;
        }
        else if ("BarChart".equals(displayChartType))
        {
            long maxRatioCompositeHoursly = 0;
            long maxRatioDetailHoursly = 0;
            long maxRatioCompositeDaily = 0;
            long maxRatioDetailDaily = 0;
            long maxRatioCompositeWeekly = 0;
            long maxRatioDetailWeekly = 0;
            long maxRatioCompositeMonthly = 0;
            long maxRatioDetailMonthly = 0;
            long totalData = 0;
            rd = request.getRequestDispatcher("/partnerRiskRuleBarChartGraph.jsp?ctoken=" + user.getCSRFToken());
            StringBuffer morrisDailyLabel = new StringBuffer();
            if ("hoursly".equals(hourlyFrequency))
            {
                String tableHeaderFreq = " today";
                if ("Y".equals(isDailyExecution))
                {
                    StringBuffer dailyBarChartData = new StringBuffer();
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        alertThreshold = monitoringParameterMappingVO.getAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getSuspensionThreshold();
                        List<int[]> ints = monitoringGraphsManager.getCategoryByThreshold(alertThreshold, suspensionThreshold);

                        int[] categoryArray1 = ints.get(0);
                        int[] categoryArray2 = ints.get(1);
                        int[] categoryArray3 = ints.get(2);
                        int[] categoryArray4 = ints.get(3);

                        String category1 = categoryArray1[0] + " - " + categoryArray1[1] + ".99";
                        String category2 = categoryArray2[0] + " - " + categoryArray2[1] + ".99";
                        String category3 = categoryArray3[0] + " - " + categoryArray3[1] + ".99";
                        String category4 = categoryArray4[0] + " and above";

                        StringBuffer label = new StringBuffer();
                        StringBuffer categoryData1 = new StringBuffer();
                        StringBuffer categoryData2 = new StringBuffer();
                        StringBuffer categoryData3 = new StringBuffer();
                        StringBuffer categoryData4 = new StringBuffer();

                        List<DateVO> list = dateManager.getTodaysTimeForStackedBarChart(5);
                        int k = 1;
                        for (DateVO dateVO : list)
                        {
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailHoursly)
                            {
                                maxRatioDetailHoursly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(categoryData1, categoryData2, categoryData3, categoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            categoryData1.append(data01);
                            categoryData2.append(data02);
                            categoryData3.append(data03);
                            categoryData4.append(data04);

                            dailyBarChartData.append("{'x': '" + labelName + "', '" + category1 + "':'" + data01 + "','" + category2 + "':'" + data02 + "','" + category3 + "':'" + data03 + "','" + category4 + "':'" + data04 + "' }");
                            if (list.size() != k)
                            {
                                dailyBarChartData.append(",");
                            }
                            k = k + 1;
                        }
                        dailyJsonStr = "data: [" + dailyBarChartData + "],";
                        morrisDailyLabel.append("'" + category1 + "','" + category2 + "','" + category3 + "','" + category4 + "'");
                        dailyTableData = monitoringGraphsManager.prepareStackBarChartTableForPartner(categoryData1, categoryData2, categoryData3, categoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName, tableHeaderFreq, monitoringParameterVO);
                        /*DateVO dateVO = dateManager.getCurrentDayDateRange();
                        HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                        if (hashMap != null){
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            if(data01 > maxRatioCompositeHoursly){
                                maxRatioCompositeHoursly = data01;
                            }
                            if(data02 > maxRatioCompositeHoursly){
                                maxRatioCompositeHoursly = data02;
                            }
                            if(data03 > maxRatioCompositeHoursly){
                                maxRatioCompositeHoursly = data03;
                            }
                            if(data04 > maxRatioCompositeHoursly){
                                maxRatioCompositeHoursly = data04;
                            }
                            //dailyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"##Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }*/
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardSameAmountConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        alertThreshold = monitoringParameterMappingVO.getAlertThreshold();
                        alertThreshold = monitoringParameterMappingVO.getAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getSuspensionThreshold();
                        DateVO compositDateVO = dateManager.getCurrentDayDateRange();
                        double[] doubles = monitoringGraphsManager.getMinMaxTransactionAmountByDate(terminalVO, compositDateVO);
                        List<int[]> ints = monitoringGraphsManager.getCategoryByThreshold(doubles[0], doubles[1]);
                        int[] categoryArray1 = ints.get(0);
                        int[] categoryArray2 = ints.get(1);
                        int[] categoryArray3 = ints.get(2);
                        int[] categoryArray4 = ints.get(3);

                        String category1 = categoryArray1[0] + " - " + categoryArray1[1] + ".99";
                        String category2 = categoryArray2[0] + " - " + categoryArray2[1] + ".99";
                        String category3 = categoryArray3[0] + " - " + categoryArray3[1] + ".99";
                        String category4 = categoryArray4[0] + " and above";

                        StringBuffer label = new StringBuffer();
                        StringBuffer categoryData1 = new StringBuffer();
                        StringBuffer categoryData2 = new StringBuffer();
                        StringBuffer catagoryData3 = new StringBuffer();
                        StringBuffer catagoryData4 = new StringBuffer();

                        List<DateVO> list = dateManager.getTodaysTimeForStackedBarChart(5);
                        int k = 1;
                        for (DateVO dateVO : list)
                        {
                            HashMap hashMap = monitoringGraphsManager.getCardCountBasedOnBinAndAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");

                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailHoursly)
                            {
                                maxRatioDetailHoursly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(categoryData1, categoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            categoryData1.append(data01);
                            categoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);

                            dailyBarChartData.append("{'x': '" + labelName + "', '" + category1 + "':'" + data01 + "','" + category2 + "':'" + data02 + "','" + category3 + "':'" + data03 + "','" + category4 + "':'" + data04 + "' }");
                            if (list.size() != k)
                            {
                                dailyBarChartData.append(",");
                            }
                            k = k + 1;
                        }
                        dailyJsonStr = "data: [" + dailyBarChartData + "],";
                        morrisDailyLabel.append("'" + category1 + "','" + category2 + "','" + category3 + "','" + category4 + "'");
                        dailyTableData = monitoringGraphsManager.prepareStackBarChartTableForPartner(categoryData1, categoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName, tableHeaderFreq, monitoringParameterVO);
                        /*HashMap hashMap = monitoringGraphsManager.getCardCountBasedOnBinAndAmountCategory(terminalVO, compositDateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                        if (hashMap != null){
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            if (data01 > maxRatioCompositeHoursly){
                                maxRatioCompositeHoursly = data01;
                            }
                            if (data02 > maxRatioCompositeHoursly){
                                maxRatioCompositeHoursly = data02;
                            }
                            if (data03 > maxRatioCompositeHoursly){
                                maxRatioCompositeHoursly = data03;
                            }
                            if (data04 > maxRatioCompositeHoursly){
                                maxRatioCompositeHoursly = data04;
                            }
                            //dailyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }*/
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        alertThreshold = monitoringParameterMappingVO.getAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getSuspensionThreshold();
                        List<int[]> ints = monitoringGraphsManager.getCategoryByThreshold(alertThreshold, suspensionThreshold);

                        int[] categoryArray1 = ints.get(0);
                        int[] categoryArray2 = ints.get(1);
                        int[] categoryArray3 = ints.get(2);
                        int[] categoryArray4 = ints.get(3);

                        String category1 = categoryArray1[0] + " - " + categoryArray1[1];
                        String category2 = categoryArray2[0] + " - " + categoryArray2[1];
                        String category3 = categoryArray3[0] + " - " + categoryArray3[1];
                        String category4 = categoryArray4[0] + " times and above";

                        StringBuffer label = new StringBuffer();
                        StringBuffer catagoryData1 = new StringBuffer();
                        StringBuffer catagoryData2 = new StringBuffer();
                        StringBuffer catagoryData3 = new StringBuffer();
                        StringBuffer catagoryData4 = new StringBuffer();

                        List<DateVO> list = dateManager.getTodaysTimeForStackedBarChart(5);
                        int k = 1;
                        for (DateVO dateVO : list)
                        {
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");

                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailHoursly)
                            {
                                maxRatioDetailHoursly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);

                            dailyBarChartData.append("{'x': '" + labelName + "', '" + category1 + "':'" + data01 + "','" + category2 + "':'" + data02 + "','" + category3 + "':'" + data03 + "','" + category4 + "':'" + data04 + "' }");
                            if (list.size() != k)
                            {
                                dailyBarChartData.append(",");
                            }
                            k = k + 1;
                        }
                        dailyJsonStr = "data: [" + dailyBarChartData + "],";
                        morrisDailyLabel.append("'" + category1 + "','" + category2 + "','" + category3 + "','" + category4 + "'");
                        dailyTableData = monitoringGraphsManager.prepareStackBarChartTableForPartner(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName, tableHeaderFreq, monitoringParameterVO);
                        /*DateVO dateVO = dateManager.getCurrentDayDateRange();
                        HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                        if (hashMap != null){
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            if (data01 > maxRatioCompositeHoursly){
                                maxRatioCompositeHoursly = data01;
                            }
                            if (data02 > maxRatioCompositeHoursly){
                                maxRatioCompositeHoursly = data02;
                            }
                            if (data03 > maxRatioCompositeHoursly){
                                maxRatioCompositeHoursly = data03;
                            }
                            if (data04 > maxRatioCompositeHoursly){
                                maxRatioCompositeHoursly = data04;
                            }
                            //dailyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }*/
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        alertThreshold = monitoringParameterMappingVO.getAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getSuspensionThreshold();
                        List<int[]> ints = monitoringGraphsManager.getCategoryByThreshold(alertThreshold, suspensionThreshold);
                        int[] categoryArray1 = ints.get(0);
                        int[] categoryArray2 = ints.get(1);
                        int[] categoryArray3 = ints.get(2);
                        int[] categoryArray4 = ints.get(3);

                        String category1 = categoryArray1[0] + " - " + categoryArray1[1];
                        String category2 = categoryArray2[0] + " - " + categoryArray2[1];
                        String category3 = categoryArray3[0] + " - " + categoryArray3[1];
                        String category4 = categoryArray4[0] + " times and above";

                        StringBuffer label = new StringBuffer();
                        StringBuffer catagoryData1 = new StringBuffer();
                        StringBuffer catagoryData2 = new StringBuffer();
                        StringBuffer catagoryData3 = new StringBuffer();
                        StringBuffer catagoryData4 = new StringBuffer();

                        List<DateVO> list = dateManager.getTodaysTimeForStackedBarChart(5);
                        int k = 1;
                        for (DateVO dateVO : list)
                        {
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinUsedDays(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");

                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailHoursly)
                            {
                                maxRatioDetailHoursly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);

                            dailyBarChartData.append("{'x': '" + labelName + "', '" + category1 + "':'" + data01 + "','" + category2 + "':'" + data02 + "','" + category3 + "':'" + data03 + "','" + category4 + "':'" + data04 + "' }");

                            if (list.size() != k)
                            {
                                dailyBarChartData.append(",");
                            }
                            k = k + 1;
                        }
                        dailyJsonStr = "data: [" + dailyBarChartData + "],";
                        morrisDailyLabel.append("'" + category1 + "','" + category2 + "','" + category3 + "','" + category4 + "'");
                        dailyTableData = monitoringGraphsManager.prepareStackBarChartTableForPartner(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName, tableHeaderFreq, monitoringParameterVO);
                       /* DateVO dateVO = dateManager.getCurrentDayDateRange();
                        HashMap hashMap =monitoringGraphsManager.getTransactionCountBasedOnBinUsedDays(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                        if (hashMap != null){
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            if (data01 > maxRatioCompositeHoursly){
                                maxRatioCompositeHoursly = data01;
                            }
                            if (data02 > maxRatioCompositeHoursly){
                                maxRatioCompositeHoursly = data02;
                            }
                            if (data03 > maxRatioCompositeHoursly){
                                maxRatioCompositeHoursly = data03;
                            }
                            if (data04 > maxRatioCompositeHoursly){
                                maxRatioCompositeHoursly = data04;
                            }
                            //dailyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }*/
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedCountry.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        HashMap hashMap=new HashMap();
                        data1=new StringBuffer();
                        boolean isData = true;
                        List<DateVO> list1 = dateManager.getTodaysLineChartQuarter(5);
                        HashMap hashMap2 = new HashMap();
                        HashMap hashMap3=null;
                        List<String> countryList = new ArrayList();
                        for (DateVO dateVO : list1)
                        {
                            StringBuffer sb = new StringBuffer();
                            hashMap3 = monitoringGraphsManager.getBlacklistCountryCountFromTransaction(terminalVO, dateVO);
                            if(isData) {
                                if(data1.length() > 0) {
                                    data1.append(",");
                                }
                                data1.append("\"" +dateVO.getDateLabel()+ "\"");
                            }
                            Iterator iterator = hashMap3.keySet().iterator();
                            while (iterator.hasNext())
                            {
                                String key=(String)iterator.next();
                                sb.append("'" + key + "':'" + hashMap3.get(key) + "'");
                                sb.append(",");
                                if(!countryList.contains(key))
                                {
                                    countryList.add(key);
                                }
                                hashMap.put(dateVO.getDateLabel()+"-"+key,hashMap3.get(key));
                            }
                            hashMap2.put(dateVO.getDateLabel(), sb.toString());
                        }
                        if (data1.length()>0 && hashMap.size() > 0)
                        {
                            String dailyHrs[] = data1.toString().split(",");
                            dailyTableData = new StringBuffer();
                            dailyTableData.append("<table align=center width=\"50%\" class=\"display table table table-striped table-bordered table-hover dataTable\" style=\"font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;\">");
                            dailyTableData.append("<tr>");
                            dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \" colspan=\"" + dailyHrs.length + 1 + "\">" + displayRuleName + " [today]" + "</td>");
                            dailyTableData.append("</tr>");
                            dailyTableData.append("<tr>");
                            dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \" >&nbsp;</td>");
                            int j = 1;
                            for (int i = 0; i < dailyHrs.length; i++)
                            {
                                dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + dailyHrs[i].replace("\"", "") + "</td>");
                                String data=(String)hashMap2.get(dailyHrs[i].replace("\"", ""));
                                if(!functions.isValueNull(data)){
                                    data="'':'0'";
                                }
                                dailyBarChartData.append("{ 'x':'" + dailyHrs[i].replace("\"", "") + "'," + data + "}");
                                if (j != dailyHrs.length)
                                {
                                    dailyBarChartData.append(",");
                                }
                                j = j + 1;
                            }
                            dailyTableData.append("</tr>");
                            int k = 1;
                            for(String country : countryList)
                            {
                                dailyTableData.append("<tr>");
                                dailyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + country + "</td>");
                                morrisDailyLabel.append("'" + country + "'");
                                if (hashMap.size() != k)
                                {
                                    morrisDailyLabel.append(",");
                                }
                                for (DateVO dateVO : list1)
                                {
                                    if(hashMap.containsKey(dateVO.getDateLabel()+"-"+country))
                                    {
                                        k = k + 1;
                                        String labelValue = (String) hashMap.get(dateVO.getDateLabel()+"-"+country);
                                        dailyTableData.append("<td align=\"center\">" + labelValue + "</td>");
                                    }else {
                                        dailyTableData.append("<td align=\"center\">0</td>");
                                    }
                                }
                                dailyTableData.append("</tr>");
                            }
                            dailyTableData.append("</table>");
                        }
                        dailyJsonStr = "data:[" + dailyBarChartData + "],";
                    }
                }
            }
            StringBuffer morrisWeeklyLabel = new StringBuffer();
            if ("daily".equals(dailyFrequency))
            {
                String tableHeaderFreq = " in current week";
                if ("Y".equals(isDailyExecution))
                {
                    StringBuffer weeklyBarChartData = new StringBuffer();
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        alertThreshold = monitoringParameterMappingVO.getAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getSuspensionThreshold();
                        List<int[]> ints = monitoringGraphsManager.getCategoryByThreshold(alertThreshold, suspensionThreshold);

                        int[] categoryArray1 = ints.get(0);
                        int[] categoryArray2 = ints.get(1);
                        int[] categoryArray3 = ints.get(2);
                        int[] categoryArray4 = ints.get(3);

                        String category1 = categoryArray1[0] + " - " + categoryArray1[1] + ".99";
                        String category2 = categoryArray2[0] + " - " + categoryArray2[1] + ".99";
                        String category3 = categoryArray3[0] + " - " + categoryArray3[1] + ".99";
                        String category4 = categoryArray4[0] + " and above";

                        StringBuffer label = new StringBuffer();
                        StringBuffer catagoryData1 = new StringBuffer();
                        StringBuffer catagoryData2 = new StringBuffer();
                        StringBuffer catagoryData3 = new StringBuffer();
                        StringBuffer catagoryData4 = new StringBuffer();

                        List<DateVO> list = dateManager.getWeeklyLineChartQuarter();
                        int k = 1;
                        for (DateVO dateVO : list)
                        {
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailDaily)
                            {
                                maxRatioDetailDaily = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);

                            weeklyBarChartData.append("{'x': '" + dateVO.getEndDate().split(" ")[0] + "', '" + category1 + "':'" + data01 + "','" + category2 + "':'" + data02 + "','" + category3 + "':'" + data03 + "','" + category4 + "':'" + data04 + "' }");
                            if (list.size() != k)
                            {
                                weeklyBarChartData.append(",");
                            }
                            k = k + 1;
                        }
                        weeklyJsonStr = "data:[" + weeklyBarChartData + "],";
                        morrisWeeklyLabel.append("'" + category1 + "','" + category2 + "','" + category3 + "','" + category4 + "'");
                        weeklyTableData = monitoringGraphsManager.prepareStackBarChartTableForPartner(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName, tableHeaderFreq, monitoringParameterVO);

                        /*DateVO dateVO = dateManager.getCalendarCurrentWeekDateRange();
                        HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                        if (hashMap != null){
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            if (data01 > maxRatioCompositeDaily){
                                maxRatioCompositeDaily = data01;
                            }
                            if (data02 > maxRatioCompositeDaily){
                                maxRatioCompositeDaily = data02;
                            }
                            if (data03 > maxRatioCompositeDaily){
                                maxRatioCompositeDaily = data03;
                            }
                            if (data04 > maxRatioCompositeDaily){
                                maxRatioCompositeDaily = data04;
                            }
                            //weeklyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }*/
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardSameAmountConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        alertThreshold = monitoringParameterMappingVO.getAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getSuspensionThreshold();
                        DateVO compositDateVO = dateManager.getCalendarCurrentWeekDateRange();
                        double[] doubles = monitoringGraphsManager.getMinMaxTransactionAmountByDate(terminalVO, compositDateVO);
                        List<int[]> ints = monitoringGraphsManager.getCategoryByThreshold(doubles[0], doubles[1]);
                        int[] categoryArray1 = ints.get(0);
                        int[] categoryArray2 = ints.get(1);
                        int[] categoryArray3 = ints.get(2);
                        int[] categoryArray4 = ints.get(3);

                        String category1 = categoryArray1[0] + " - " + categoryArray1[1] + ".99";
                        String category2 = categoryArray2[0] + " - " + categoryArray2[1] + ".99";
                        String category3 = categoryArray3[0] + " - " + categoryArray3[1] + ".99";
                        String category4 = categoryArray4[0] + " and above";

                        StringBuffer label = new StringBuffer();
                        StringBuffer catagoryData1 = new StringBuffer();
                        StringBuffer catagoryData2 = new StringBuffer();
                        StringBuffer catagoryData3 = new StringBuffer();
                        StringBuffer catagoryData4 = new StringBuffer();

                        List<DateVO> list = dateManager.getWeeklyLineChartQuarter();
                        int k = 1;
                        for (DateVO dateVO : list)
                        {
                            HashMap hashMap = monitoringGraphsManager.getCardCountBasedOnBinAndAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");

                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailDaily)
                            {
                                maxRatioDetailDaily = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);

                            weeklyBarChartData.append("{'x': '" + dateVO.getEndDate().split(" ")[0] + "', '" + category1 + "':'" + data01 + "','" + category2 + "':'" + data02 + "','" + category3 + "':'" + data03 + "','" + category4 + "':'" + data04 + "' }");
                            if (list.size() != k)
                            {
                                weeklyBarChartData.append(",");
                            }
                            k = k + 1;
                        }
                        weeklyJsonStr = "data:[" + weeklyBarChartData + "],";
                        morrisWeeklyLabel.append("'" + category1 + "','" + category2 + "','" + category3 + "','" + category4 + "'");
                        weeklyTableData = monitoringGraphsManager.prepareStackBarChartTableForPartner(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName, tableHeaderFreq, monitoringParameterVO);

                        /*HashMap hashMap = monitoringGraphsManager.getCardCountBasedOnBinAndAmountCategory(terminalVO, compositDateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                        if (hashMap != null){
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            if (data01 > maxRatioCompositeDaily){
                                maxRatioCompositeDaily = data01;
                            }
                            if (data02 > maxRatioCompositeDaily){
                                maxRatioCompositeDaily = data02;
                            }
                            if (data03 > maxRatioCompositeDaily){
                                maxRatioCompositeDaily = data03;
                            }
                            if (data04 > maxRatioCompositeDaily){
                                maxRatioCompositeDaily = data04;
                            }
                            //weeklyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }*/
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        alertThreshold = monitoringParameterMappingVO.getAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getSuspensionThreshold();

                        List<int[]> ints = monitoringGraphsManager.getCategoryByThreshold(alertThreshold, suspensionThreshold);
                        int[] categoryArray1 = ints.get(0);
                        int[] categoryArray2 = ints.get(1);
                        int[] categoryArray3 = ints.get(2);
                        int[] categoryArray4 = ints.get(3);

                        String category1 = categoryArray1[0] + " - " + categoryArray1[1];
                        String category2 = categoryArray2[0] + " - " + categoryArray2[1];
                        String category3 = categoryArray3[0] + " - " + categoryArray3[1];
                        String category4 = categoryArray4[0] + " times and above";

                        StringBuffer label = new StringBuffer();
                        StringBuffer catagoryData1 = new StringBuffer();
                        StringBuffer catagoryData2 = new StringBuffer();
                        StringBuffer catagoryData3 = new StringBuffer();
                        StringBuffer catagoryData4 = new StringBuffer();

                        List<DateVO> list = dateManager.getWeeklyLineChartQuarter();
                        int k = 1;
                        for (DateVO dateVO : list)
                        {
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");

                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailDaily)
                            {
                                maxRatioDetailDaily = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);

                            weeklyBarChartData.append("{'x': '" + dateVO.getEndDate().split(" ")[0] + "', '" + category1 + "':'" + data01 + "','" + category2 + "':'" + data02 + "','" + category3 + "':'" + data03 + "','" + category4 + "':'" + data04 + "' }");
                            if (list.size() != k)
                            {
                                weeklyBarChartData.append(",");
                            }
                            k = k + 1;
                        }
                        weeklyJsonStr = "data:[" + weeklyBarChartData + "],";
                        morrisWeeklyLabel.append("'" + category1 + "','" + category2 + "','" + category3 + "','" + category4 + "'");
                        weeklyTableData = monitoringGraphsManager.prepareStackBarChartTableForPartner(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName, tableHeaderFreq, monitoringParameterVO);
                        /*DateVO dateVO = dateManager.getCalendarCurrentWeekDateRange();
                        HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                        if (hashMap != null){
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            if(data01 > maxRatioCompositeDaily){
                                maxRatioCompositeDaily = data01;
                            }
                            if(data02 > maxRatioCompositeDaily){
                                maxRatioCompositeDaily = data02;
                            }
                            if (data03 > maxRatioCompositeDaily){
                                maxRatioCompositeDaily = data03;
                            }
                            if (data04 > maxRatioCompositeDaily){
                                maxRatioCompositeDaily = data04;
                            }
                            //weeklyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }*/
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        alertThreshold = monitoringParameterMappingVO.getAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getSuspensionThreshold();
                        List<int[]> ints = monitoringGraphsManager.getCategoryByThreshold(alertThreshold, suspensionThreshold);
                        int[] categoryArray1 = ints.get(0);
                        int[] categoryArray2 = ints.get(1);
                        int[] categoryArray3 = ints.get(2);
                        int[] categoryArray4 = ints.get(3);

                        String category1 = categoryArray1[0] + " - " + categoryArray1[1];
                        String category2 = categoryArray2[0] + " - " + categoryArray2[1];
                        String category3 = categoryArray3[0] + " - " + categoryArray3[1];
                        String category4 = categoryArray4[0] + " times and above";

                        StringBuffer label = new StringBuffer();
                        StringBuffer catagoryData1 = new StringBuffer();
                        StringBuffer catagoryData2 = new StringBuffer();
                        StringBuffer catagoryData3 = new StringBuffer();
                        StringBuffer catagoryData4 = new StringBuffer();

                        List<DateVO> list = dateManager.getWeeklyLineChartQuarter();
                        int k = 1;
                        for (DateVO dateVO : list)
                        {
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinUsedDays(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailDaily)
                            {
                                maxRatioDetailDaily = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);

                            weeklyBarChartData.append("{'x': '" + dateVO.getEndDate().split(" ")[0] + "', '" + category1 + "':'" + data01 + "','" + category2 + "':'" + data02 + "','" + category3 + "':'" + data03 + "','" + category4 + "':'" + data04 + "' }");
                            if (list.size() != k)
                            {
                                weeklyBarChartData.append(",");
                            }
                            k = k + 1;
                        }
                        weeklyJsonStr = "data:[" + weeklyBarChartData + "],";
                        morrisWeeklyLabel.append("'" + category1 + "','" + category2 + "','" + category3 + "','" + category4 + "'");
                        weeklyTableData = monitoringGraphsManager.prepareStackBarChartTableForPartner(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName, tableHeaderFreq, monitoringParameterVO);
                        /*DateVO dateVO = dateManager.getCalendarCurrentWeekDateRange();
                        HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinUsedDays(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                        if (hashMap != null){
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            if (data01 > maxRatioCompositeDaily){
                                maxRatioCompositeDaily = data01;
                            }
                            if (data02 > maxRatioCompositeDaily){
                                maxRatioCompositeDaily = data02;
                            }
                            if (data03 > maxRatioCompositeDaily){
                                maxRatioCompositeDaily = data03;
                            }
                            if (data04 > maxRatioCompositeDaily){
                                maxRatioCompositeDaily = data04;
                            }
                            //weeklyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }*/
                    }
                    data1 = new StringBuffer();
                    boolean isData = true;
                    HashMap hashMap = new HashMap();

                    HashMap dateHashMap = new HashMap();
                    List<DateVO> list11 = dateManager.getWeeklyLineChartQuarter();
                    for (DateVO dateVO : list11)
                    {
                        dateHashMap.put(dateVO.getDateLabel(), dateVO);
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedCountry.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        List<DateVO> list = dateManager.getWeeklyLineChartQuarter();
                        HashMap hashMap1 = new HashMap();
                        HashMap hashMap2 = null;
                        StringBuffer sb = null;
                        List<String> countryList = new ArrayList();
                        for (DateVO dateVO : list)
                        {
                            sb=new StringBuffer();
                            hashMap2 = monitoringGraphsManager.getBlacklistCountryCountFromTransaction(terminalVO, dateVO);
                            if(isData) {
                                if(data1.length() > 0) {
                                    data1.append(",");
                                }
                                data1.append("\"" +dateVO.getDateLabel()+ "\"");
                            }
                            Iterator iterator = hashMap2.keySet().iterator();
                            while (iterator.hasNext())
                            {
                                String key=(String)iterator.next();
                                sb.append("'" + key + "':'" + hashMap2.get(key) + "'");
                                sb.append(",");
                                if(!countryList.contains(key))
                                {
                                    countryList.add(key);
                                }
                                hashMap.put(dateVO.getDateLabel()+"-"+key,hashMap2.get(key));
                            }
                            hashMap1.put(dateVO.getDateLabel(), sb.toString());
                        }
                        if (data1.length() > 0 && hashMap.size() > 0)
                        {
                            String weekDays[] = data1.toString().split(",");
                            weeklyTableData = new StringBuffer();
                            weeklyTableData.append("<table align=center width=\"50%\" class=\"display table table table-striped table-bordered table-hover dataTable\" style=\"font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;\">");
                            weeklyTableData.append("<tr>");
                            weeklyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \" colspan=\"" + weekDays.length + 1 + "\">" + displayRuleName + " [In Current Week]" + "</td>");
                            weeklyTableData.append("</tr>");
                            weeklyTableData.append("<tr>");
                            weeklyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">&nbsp;</td>");
                            int j = 1;
                            for (int i = 0; i < weekDays.length; i++)
                            {
                                weeklyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + weekDays[i].replace("\"", "") + "</td>");

                                DateVO dateVO = (DateVO) dateHashMap.get(weekDays[i].replace("\"", ""));
                                String weeklyLabel = dateVO.getEndDate().split(" ")[0];
                                String data=(String)hashMap1.get(weekDays[i].replace("\"", ""));
                                if(!functions.isValueNull(data)){
                                    data="'':'0'";
                                }
                                weeklyBarChartData.append("{ 'x':'" + weeklyLabel + "'," + data + "}");
                                if (j != weekDays.length)
                                {
                                    weeklyBarChartData.append(",");
                                }
                                j = j + 1;
                            }
                            weeklyTableData.append("</tr>");
                            int k = 1;
                            for(String country : countryList)
                            {
                                weeklyTableData.append("<tr>");
                                weeklyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + country + "</td>");
                                morrisWeeklyLabel.append("'" + country + "'");
                                if (hashMap.size() != k)
                                {
                                    morrisWeeklyLabel.append(",");
                                }
                                for (DateVO dateVO : list)
                                {
                                    if(hashMap.containsKey(dateVO.getDateLabel()+"-"+country))
                                    {
                                        k = k + 1;
                                        String labelValue = (String) hashMap.get(dateVO.getDateLabel()+"-"+country);
                                        weeklyTableData.append("<td align=\"center\">" + labelValue + "</td>");
                                    }else {
                                        weeklyTableData.append("<td align=\"center\">0</td>");
                                    }
                                }
                                weeklyTableData.append("</tr>");
                            }
                            weeklyTableData.append("</table>");
                        }
                        weeklyJsonStr = "data:[" + weeklyBarChartData + "],";
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedIP.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        List<DateVO> list = dateManager.getWeeklyLineChartQuarter();
                        HashMap hashMap1 = new HashMap();
                        HashMap hashMap2 = null;
                        StringBuffer sb = null;
                        List<String> ipList = new ArrayList();
                        for (DateVO dateVO : list)
                        {
                            sb=new StringBuffer();
                            hashMap2 = monitoringGraphsManager.getBlacklistIPCountFromTransaction(terminalVO, dateVO);
                            if (isData)
                            {
                                if (data1.length() > 0)
                                {
                                    data1.append(",");
                                }
                                data1.append("\"" + dateVO.getDateLabel() + "\"");
                            }
                            Iterator iterator = hashMap2.keySet().iterator();
                            while (iterator.hasNext())
                            {
                                String key=(String)iterator.next();
                                sb.append("'" + key + "':'" + hashMap2.get(key) + "'");
                                sb.append(",");
                                if(!ipList.contains(key))
                                {
                                    ipList.add(key);
                                }
                                hashMap.put(dateVO.getDateLabel()+"-"+key, hashMap2.get(key));
                            }
                            hashMap1.put(dateVO.getDateLabel(), sb.toString());
                        }

                        if (data1.length() > 0 && hashMap.size() > 0)
                        {
                            String weekDays[] = data1.toString().split(",");
                            weeklyTableData = new StringBuffer();
                            weeklyTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            weeklyTableData.append("<tr>");
                            weeklyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + weekDays.length + 1 + "\">" + displayRuleName + " [In Current Week]" + "</td>");
                            weeklyTableData.append("</tr>");
                            weeklyTableData.append("<tr>");
                            weeklyTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            int j = 1;
                            for (int i = 0; i < weekDays.length; i++)
                            {
                                weeklyTableData.append("<td align=\"center\" class=\"th0\">" + weekDays[i].replace("\"", "") + "</td>");

                                DateVO dateVO = (DateVO) dateHashMap.get(weekDays[i].replace("\"", ""));
                                String weeklyLabel = dateVO.getEndDate().split(" ")[0];
                                String data=(String)hashMap1.get(weekDays[i].replace("\"", ""));
                                if(!functions.isValueNull(data)){
                                    data="'':'0'";
                                }
                                weeklyBarChartData.append("{ 'x':'" + weeklyLabel + "'," + data + "}");
                                if (j != weekDays.length)
                                {
                                    weeklyBarChartData.append(",");
                                }
                                j = j + 1;
                            }
                            weeklyTableData.append("</tr>");
                            int k = 1;
                            for(String country : ipList)
                            {
                                weeklyTableData.append("<tr>");
                                weeklyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + country + "</td>");
                                morrisWeeklyLabel.append("'" + country + "'");
                                if (hashMap.size() != k)
                                {
                                    morrisWeeklyLabel.append(",");
                                }
                                for (DateVO dateVO : list)
                                {
                                    if(hashMap.containsKey(dateVO.getDateLabel()+"-"+country))
                                    {
                                        k = k + 1;
                                        String labelValue = (String) hashMap.get(dateVO.getDateLabel()+"-"+country);
                                        weeklyTableData.append("<td align=\"center\">" + labelValue + "</td>");
                                    }else {
                                        weeklyTableData.append("<td align=\"center\">0</td>");
                                    }
                                }
                                weeklyTableData.append("</tr>");
                            }
                            weeklyTableData.append("</table>");
                        }
                        weeklyJsonStr = "data:[" + weeklyBarChartData + "],";
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedCard.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        List<DateVO> list = dateManager.getWeeklyLineChartQuarter();
                        HashMap hashMap1 = new HashMap();
                        HashMap hashMap2 = null;
                        StringBuffer sb = null;
                        List<String> cardList = new ArrayList();
                        for (DateVO dateVO : list)
                        {
                            sb=new StringBuffer();
                            hashMap2 = monitoringGraphsManager.getBlacklistCardCountFromTransaction(terminalVO, dateVO);
                            if (isData)
                            {
                                if (data1.length() > 0)
                                {
                                    data1.append(",");
                                }
                                data1.append("\"" + dateVO.getDateLabel() + "\"");
                            }
                            Iterator iterator = hashMap2.keySet().iterator();
                            while (iterator.hasNext())
                            {
                                String key=(String)iterator.next();
                                sb.append("'" + key + "':'" + hashMap2.get(key) + "'");
                                sb.append(",");
                                if(!cardList.contains(key))
                                {
                                    cardList.add(key);
                                }
                                hashMap.put(dateVO.getDateLabel()+"-"+key, hashMap2.get(key));
                            }
                            hashMap1.put(dateVO.getDateLabel(), sb.toString());
                        }

                        if (data1.length() > 0 && hashMap.size() > 0)
                        {
                            String weekDays[] = data1.toString().split(",");
                            weeklyTableData = new StringBuffer();
                            weeklyTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            weeklyTableData.append("<tr>");
                            weeklyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + weekDays.length + 1 + "\">" + displayRuleName + " [In Current Week]" + "</td>");
                            weeklyTableData.append("</tr>");
                            weeklyTableData.append("<tr>");
                            weeklyTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            int j = 1;
                            for (int i = 0; i < weekDays.length; i++)
                            {
                                weeklyTableData.append("<td align=\"center\" class=\"th0\">" + weekDays[i].replace("\"", "") + "</td>");

                                DateVO dateVO = (DateVO) dateHashMap.get(weekDays[i].replace("\"", ""));
                                String weeklyLabel = dateVO.getEndDate().split(" ")[0];
                                String data=(String)hashMap1.get(weekDays[i].replace("\"", ""));
                                if(!functions.isValueNull(data)){
                                    data="'':'0'";
                                }
                                weeklyBarChartData.append("{ 'x':'" + weeklyLabel + "'," + data + "}");
                                if (j != weekDays.length)
                                {
                                    weeklyBarChartData.append(",");
                                }
                                j = j + 1;
                            }
                            weeklyTableData.append("</tr>");
                            int k = 1;
                            for(String country : cardList)
                            {
                                weeklyTableData.append("<tr>");
                                weeklyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + country + "</td>");
                                morrisWeeklyLabel.append("'" + country + "'");
                                if (hashMap.size() != k)
                                {
                                    morrisWeeklyLabel.append(",");
                                }
                                for (DateVO dateVO : list)
                                {
                                    if(hashMap.containsKey(dateVO.getDateLabel()+"-"+country))
                                    {
                                        k = k + 1;
                                        String labelValue = (String) hashMap.get(dateVO.getDateLabel()+"-"+country);
                                        weeklyTableData.append("<td align=\"center\">" + labelValue + "</td>");
                                    }else {
                                        weeklyTableData.append("<td align=\"center\">0</td>");
                                    }
                                }
                                weeklyTableData.append("</tr>");
                            }
                            weeklyTableData.append("</table>");
                        }
                        weeklyJsonStr = "data:[" + weeklyBarChartData + "],";
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedEmail.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        List<DateVO> list = dateManager.getWeeklyLineChartQuarter();
                        HashMap hashMap1 = new HashMap();
                        HashMap hashMap2 = null;
                        StringBuffer sb = null;
                        List<String> emailList = new ArrayList();
                        for (DateVO dateVO : list)
                        {
                            sb=new StringBuffer();
                            hashMap2 = monitoringGraphsManager.getBlacklistEmailCountFromTransaction(terminalVO, dateVO);
                            if (isData) {
                                if (data1.length() > 0) {
                                    data1.append(",");
                                }
                                data1.append("\"" + dateVO.getDateLabel() + "\"");
                            }
                            Iterator iterator = hashMap2.keySet().iterator();
                            while (iterator.hasNext())
                            {
                                String key=(String)iterator.next();
                                sb.append("'" + key + "':'" + hashMap2.get(key) + "'");
                                sb.append(",");
                                if(!emailList.contains(key))
                                {
                                    emailList.add(key);
                                }
                                hashMap.put(dateVO.getDateLabel()+"-"+key, hashMap2.get(key));
                            }
                            hashMap1.put(dateVO.getDateLabel(), sb.toString());
                        }

                        if (data1.length() > 0 && hashMap.size() > 0)
                        {
                            String weekDays[] = data1.toString().split(",");
                            weeklyTableData = new StringBuffer();
                            weeklyTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            weeklyTableData.append("<tr>");
                            weeklyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + weekDays.length + 1 + "\">" + displayRuleName + " [In Current Week]" + "</td>");
                            weeklyTableData.append("</tr>");
                            weeklyTableData.append("<tr>");
                            weeklyTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            int j = 1;
                            for (int i = 0; i < weekDays.length; i++)
                            {
                                weeklyTableData.append("<td align=\"center\" class=\"th0\">" + weekDays[i].replace("\"", "") + "</td>");

                                DateVO dateVO = (DateVO) dateHashMap.get(weekDays[i].replace("\"", ""));
                                String weeklyLabel = dateVO.getEndDate().split(" ")[0];
                                String data=(String)hashMap1.get(weekDays[i].replace("\"", ""));
                                if(!functions.isValueNull(data)){
                                    data="'':'0'";
                                }
                                weeklyBarChartData.append("{ 'x':'" + weeklyLabel + "'," + data + "}");
                                if (j != weekDays.length)
                                {
                                    weeklyBarChartData.append(",");
                                }
                                j = j + 1;
                            }
                            weeklyTableData.append("</tr>");
                            int k = 1;
                            for(String country : emailList)
                            {
                                weeklyTableData.append("<tr>");
                                weeklyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + country + "</td>");
                                morrisWeeklyLabel.append("'" + country  + "'");
                                if (hashMap.size() != k)
                                {
                                    morrisWeeklyLabel.append(",");
                                }
                                for (DateVO dateVO : list)
                                {
                                    if(hashMap.containsKey(dateVO.getDateLabel()+"-"+country))
                                    {
                                        k = k + 1;
                                        String labelValue = (String) hashMap.get(dateVO.getDateLabel()+"-"+country);
                                        weeklyTableData.append("<td align=\"center\">" + labelValue + "</td>");
                                    }else {
                                        weeklyTableData.append("<td align=\"center\">0</td>");
                                    }
                                }
                                weeklyTableData.append("</tr>");
                            }
                            weeklyTableData.append("</table>");
                        }
                        weeklyJsonStr = "data:[" + weeklyBarChartData + "],";
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedName.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        List<DateVO> list = dateManager.getWeeklyLineChartQuarter();
                        HashMap hashMap1 = new HashMap();
                        HashMap hashMap2 = null;
                        StringBuffer sb = null;
                        List<String> nameList = new ArrayList();
                        for (DateVO dateVO : list)
                        {
                            sb=new StringBuffer();
                            hashMap2 = monitoringGraphsManager.getBlacklistNameCountFromTransaction(terminalVO, dateVO);
                            if (isData)
                            {
                                if (data1.length() > 0)
                                {
                                    data1.append(",");
                                }
                                data1.append("\"" + dateVO.getDateLabel() + "\"");
                            }
                            Iterator iterator = hashMap2.keySet().iterator();
                            while (iterator.hasNext())
                            {
                                String key=(String)iterator.next();
                                sb.append("'" + key + "':'" + hashMap2.get(key) + "'");
                                sb.append(",");
                                if(!nameList.contains(key))
                                {
                                    nameList.add(key);
                                }
                                hashMap.put(dateVO.getDateLabel()+"-"+key, hashMap2.get(key));
                            }
                            hashMap1.put(dateVO.getDateLabel(), sb.toString());
                        }
                        if (data1.length() > 0 && hashMap.size() > 0)
                        {
                            String weekDays[] = data1.toString().split(",");
                            weeklyTableData = new StringBuffer();
                            weeklyTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            weeklyTableData.append("<tr>");
                            weeklyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + weekDays.length + 1 + "\">" + displayRuleName + " [In Current Week]" + "</td>");
                            weeklyTableData.append("</tr>");
                            weeklyTableData.append("<tr>");
                            weeklyTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            int j = 1;
                            for (int i = 0; i < weekDays.length; i++)
                            {
                                weeklyTableData.append("<td align=\"center\" class=\"th0\">" + weekDays[i].replace("\"", "") + "</td>");

                                DateVO dateVO = (DateVO) dateHashMap.get(weekDays[i].replace("\"", ""));
                                String weeklyLabel = dateVO.getEndDate().split(" ")[0];
                                String data=(String)hashMap1.get(weekDays[i].replace("\"", ""));
                                if(!functions.isValueNull(data)){
                                    data="'':'0'";
                                }
                                weeklyBarChartData.append("{ 'x':'" + weeklyLabel + "'," + data + "}");
                                if (j != weekDays.length)
                                {
                                    weeklyBarChartData.append(",");
                                }
                                j = j + 1;
                            }
                            weeklyTableData.append("</tr>");
                            int k = 1;
                            for(String country : nameList)
                            {
                                weeklyTableData.append("<tr>");
                                weeklyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + country + "</td>");
                                morrisWeeklyLabel.append("'" + country + "'");
                                if (hashMap.size() != k)
                                {
                                    morrisWeeklyLabel.append(",");
                                }
                                for (DateVO dateVO : list)
                                {
                                    if(hashMap.containsKey(dateVO.getDateLabel()+"-"+country))
                                    {
                                        k = k + 1;
                                        String labelValue = (String) hashMap.get(dateVO.getDateLabel()+"-"+country);
                                        weeklyTableData.append("<td align=\"center\">" + labelValue + "</td>");
                                    }else {
                                        weeklyTableData.append("<td align=\"center\">0</td>");
                                    }
                                }
                                weeklyTableData.append("</tr>");
                            }
                            weeklyTableData.append("</table>");
                        }
                        weeklyJsonStr = "data:[" + weeklyBarChartData + "],";
                    }
                }
            }
            StringBuffer morrisWeeklyNewLabel = new StringBuffer();
            if ("weekly".equals(weeklyFrequency))
            {
                String tableHeaderFreq = " in current month";
                if ("Y".equals(isWeeklyExecution))
                {
                    StringBuffer weeklyNewBarChartData = new StringBuffer();
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        alertThreshold = monitoringParameterMappingVO.getWeeklyAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getWeeklySuspensionThreshold();

                        List<int[]> ints = monitoringGraphsManager.getCategoryByThreshold(alertThreshold, suspensionThreshold);
                        int[] categoryArray1 = ints.get(0);
                        int[] categoryArray2 = ints.get(1);
                        int[] categoryArray3 = ints.get(2);
                        int[] categoryArray4 = ints.get(3);

                        String category1 = categoryArray1[0] + " - " + categoryArray1[1] + ".99";
                        String category2 = categoryArray2[0] + " - " + categoryArray2[1] + ".99";
                        String category3 = categoryArray3[0] + " - " + categoryArray3[1] + ".99";
                        String category4 = categoryArray4[0] + " and above";

                        StringBuffer label = new StringBuffer();
                        StringBuffer catagoryData1 = new StringBuffer();
                        StringBuffer catagoryData2 = new StringBuffer();
                        StringBuffer catagoryData3 = new StringBuffer();
                        StringBuffer catagoryData4 = new StringBuffer();
                        List<DateVO> list = dateManager.getWeeksOfMonthNew();
                        int k = 1;
                        for (DateVO dateVO : list)
                        {
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");

                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailWeekly)
                            {
                                maxRatioDetailWeekly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);

                            weeklyNewBarChartData.append("{'x': '" + dateVO.getEndDate().split(" ")[0] + "', '" + category1 + "':'" + data01 + "','" + category2 + "':'" + data02 + "','" + category3 + "':'" + data03 + "','" + category4 + "':'" + data04 + "' }");
                            if (list.size() != k)
                            {
                                weeklyNewBarChartData.append(",");
                            }
                            k = k + 1;
                        }
                        weeklyNewJsonStr = "data: [" + weeklyNewBarChartData + "],";
                        morrisWeeklyNewLabel.append("'" + category1 + "','" + category2 + "','" + category3 + "','" + category4 + "'");
                        weeklyNewTableData = monitoringGraphsManager.prepareStackBarChartTableForPartner(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName, tableHeaderFreq, monitoringParameterVO);

                        /*DateVO dateVO = dateManager.getCurrentMonthDateRange();
                        HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                        data01 = 0;
                        data02 = 0;
                        data03 = 0;
                        data04 = 0;
                        if (hashMap != null){
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            if (data01 > maxRatioCompositeWeekly){
                                maxRatioCompositeWeekly = data01;
                            }
                            if (data02 > maxRatioCompositeWeekly){
                                maxRatioCompositeWeekly = data02;
                            }
                            if (data03 > maxRatioCompositeWeekly){
                                maxRatioCompositeWeekly = data03;
                            }
                            if (data04 > maxRatioCompositeWeekly){
                                maxRatioCompositeWeekly = data04;
                            }
                            //weeklyNewJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }*/
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardSameAmountConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        alertThreshold = monitoringParameterMappingVO.getWeeklyAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getWeeklySuspensionThreshold();
                        DateVO compositDateVO = dateManager.getCurrentMonthDateRange();
                        double[] doubles = monitoringGraphsManager.getMinMaxTransactionAmountByDate(terminalVO, compositDateVO);
                        List<int[]> ints = monitoringGraphsManager.getCategoryByThreshold(doubles[0], doubles[1]);

                        int[] categoryArray1 = ints.get(0);
                        int[] categoryArray2 = ints.get(1);
                        int[] categoryArray3 = ints.get(2);
                        int[] categoryArray4 = ints.get(3);

                        String category1 = categoryArray1[0] + " - " + categoryArray1[1] + ".99";
                        String category2 = categoryArray2[0] + " - " + categoryArray2[1] + ".99";
                        String category3 = categoryArray3[0] + " - " + categoryArray3[1] + ".99";
                        String category4 = categoryArray4[0] + " and above";

                        StringBuffer label = new StringBuffer();
                        StringBuffer catagoryData1 = new StringBuffer();
                        StringBuffer catagoryData2 = new StringBuffer();
                        StringBuffer catagoryData3 = new StringBuffer();
                        StringBuffer catagoryData4 = new StringBuffer();

                        List<DateVO> list = dateManager.getWeeksOfMonthNew();
                        int k = 1;
                        for (DateVO dateVO : list)
                        {
                            HashMap hashMap = monitoringGraphsManager.getCardCountBasedOnBinAndAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailWeekly)
                            {
                                maxRatioDetailWeekly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);

                            weeklyNewBarChartData.append("{'x': '" + dateVO.getEndDate().split(" ")[0] + "', '" + category1 + "':'" + data01 + "','" + category2 + "':'" + data02 + "','" + category3 + "':'" + data03 + "','" + category4 + "':'" + data04 + "' }");
                            if (list.size() != k)
                            {
                                weeklyNewBarChartData.append(",");
                            }
                            k = k + 1;
                        }
                        weeklyNewJsonStr = "data: [" + weeklyNewBarChartData + "],";
                        morrisWeeklyNewLabel.append("'" + category1 + "','" + category2 + "','" + category3 + "','" + category4 + "'");
                        weeklyNewTableData = monitoringGraphsManager.prepareStackBarChartTableForPartner(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName, tableHeaderFreq, monitoringParameterVO);

                        /*HashMap hashMap = monitoringGraphsManager.getCardCountBasedOnBinAndAmountCategory(terminalVO, compositDateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                        if (hashMap != null){
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            if (data01 > maxRatioCompositeWeekly){
                                maxRatioCompositeWeekly = data01;
                            }
                            if (data02 > maxRatioCompositeWeekly){
                                maxRatioCompositeWeekly = data02;
                            }
                            if (data03 > maxRatioCompositeWeekly){
                                maxRatioCompositeWeekly = data03;
                            }
                            if (data04 > maxRatioCompositeWeekly){
                                maxRatioCompositeWeekly = data04;
                            }
                            //weeklyNewJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }*/
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        alertThreshold = monitoringParameterMappingVO.getWeeklyAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getWeeklySuspensionThreshold();
                        List<int[]> ints = monitoringGraphsManager.getCategoryByThreshold(alertThreshold, suspensionThreshold);
                        int[] categoryArray1 = ints.get(0);
                        int[] categoryArray2 = ints.get(1);
                        int[] categoryArray3 = ints.get(2);
                        int[] categoryArray4 = ints.get(3);

                        String category1 = categoryArray1[0] + " - " + categoryArray1[1];
                        String category2 = categoryArray2[0] + " - " + categoryArray2[1];
                        String category3 = categoryArray3[0] + " - " + categoryArray3[1];
                        String category4 = categoryArray4[0] + " times and above";

                        StringBuffer label = new StringBuffer();
                        StringBuffer catagoryData1 = new StringBuffer();
                        StringBuffer catagoryData2 = new StringBuffer();
                        StringBuffer catagoryData3 = new StringBuffer();
                        StringBuffer catagoryData4 = new StringBuffer();
                        List<DateVO> list = dateManager.getWeeksOfMonthNew();
                        int k = 1;
                        for (DateVO dateVO : list)
                        {
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailWeekly)
                            {
                                maxRatioDetailWeekly = totalData;
                            }

                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);

                            weeklyNewBarChartData.append("{x: '" + dateVO.getEndDate().split(" ")[0] + "', '" + category1 + "':" + data01 + ",'" + category2 + "':" + data02 + ",'" + category3 + "':" + data03 + ",'" + category4 + "':" + data04 + " }");
                            if (list.size() != k)
                            {
                                weeklyNewBarChartData.append(",");
                            }
                            k = k + 1;
                        }
                        weeklyNewJsonStr = "data: [" + weeklyNewBarChartData + "],";
                        morrisWeeklyNewLabel.append("'" + category1 + "','" + category2 + "','" + category3 + "','" + category4 + "'");
                        weeklyNewTableData = monitoringGraphsManager.prepareStackBarChartTableForPartner(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName, tableHeaderFreq, monitoringParameterVO);

                        /*DateVO dateVO = dateManager.getCurrentMonthDateRange();
                        HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                        if (hashMap != null){
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            if (data01 > maxRatioCompositeWeekly){
                                maxRatioCompositeWeekly = data01;
                            }
                            if (data02 > maxRatioCompositeWeekly){
                                maxRatioCompositeWeekly = data02;
                            }
                            if (data03 > maxRatioCompositeWeekly){
                                maxRatioCompositeWeekly = data03;
                            }
                            if (data04 > maxRatioCompositeWeekly){
                                maxRatioCompositeWeekly = data04;
                            }
                            //weeklyNewJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }*/
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        alertThreshold = monitoringParameterMappingVO.getWeeklyAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getWeeklySuspensionThreshold();
                        List<int[]> ints = monitoringGraphsManager.getCategoryByThreshold(alertThreshold, suspensionThreshold);
                        int[] categoryArray1 = ints.get(0);
                        int[] categoryArray2 = ints.get(1);
                        int[] categoryArray3 = ints.get(2);
                        int[] categoryArray4 = ints.get(3);

                        String category1 = categoryArray1[0] + " - " + categoryArray1[1];
                        String category2 = categoryArray2[0] + " - " + categoryArray2[1];
                        String category3 = categoryArray3[0] + " - " + categoryArray3[1];
                        String category4 = categoryArray4[0] + " times and above";

                        StringBuffer label = new StringBuffer();
                        StringBuffer catagoryData1 = new StringBuffer();
                        StringBuffer catagoryData2 = new StringBuffer();
                        StringBuffer catagoryData3 = new StringBuffer();
                        StringBuffer catagoryData4 = new StringBuffer();
                        List<DateVO> list = dateManager.getWeeksOfMonthNew();
                        int k = 1;
                        for (DateVO dateVO : list)
                        {
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinUsedDays(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailWeekly)
                            {
                                maxRatioDetailWeekly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);

                            weeklyNewBarChartData.append("{'x': '" + dateVO.getEndDate().split(" ")[0] + "', '" + category1 + "':'" + data01 + "','" + category2 + "':'" + data02 + "','" + category3 + "':'" + data03 + "','" + category4 + "':'" + data04 + "' }");
                            if (list.size() != k)
                            {
                                weeklyNewBarChartData.append(",");
                            }
                            k = k + 1;
                        }
                        weeklyNewJsonStr = "data: [" + weeklyNewBarChartData + "],";
                        morrisWeeklyNewLabel.append("'" + category1 + "','" + category2 + "','" + category3 + "','" + category4 + "'");
                        weeklyNewTableData = monitoringGraphsManager.prepareStackBarChartTableForPartner(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName, tableHeaderFreq, monitoringParameterVO);

                        /*DateVO dateVO = dateManager.getCurrentMonthDateRange();
                        HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinUsedDays(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                        if (hashMap != null)
                        {
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            if (data01 > maxRatioCompositeWeekly){
                                maxRatioCompositeWeekly = data01;
                            }
                            if (data02 > maxRatioCompositeWeekly){
                                maxRatioCompositeWeekly = data02;
                            }
                            if (data03 > maxRatioCompositeWeekly){
                                maxRatioCompositeWeekly = data03;
                            }
                            if (data04 > maxRatioCompositeWeekly){
                                maxRatioCompositeWeekly = data04;
                            }
                            //weeklyNewJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }*/
                    }
                    data1 = new StringBuffer();
                    boolean isData = true;
                    HashMap hashMap = new HashMap();
                    HashMap dateHashMap = new HashMap();
                    List<DateVO> list11 = dateManager.getWeeksOfMonthNew();
                    for (DateVO dateVO : list11)
                    {
                        dateHashMap.put(dateVO.getDateLabel(), dateVO);
                    }

                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedCountry.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        List<DateVO> list = dateManager.getWeeksOfMonthNew();
                        HashMap hashMap1 = new HashMap();
                        HashMap hashMap2 = null;
                        StringBuffer sb = null;
                        List<String> countryList = new ArrayList();
                        for (DateVO dateVO : list)
                        {
                            sb=new StringBuffer();
                            hashMap2 = monitoringGraphsManager.getBlacklistCountryCountFromTransaction(terminalVO, dateVO);
                            if(isData)
                            {
                                if(data1.length() > 0)
                                {
                                    data1.append(",");
                                }
                                data1.append("\"" +dateVO.getDateLabel()+ "\"");
                            }
                            Iterator iterator = hashMap2.keySet().iterator();
                            while (iterator.hasNext())
                            {
                                String key=(String)iterator.next();
                                sb.append("'" + key + "':'" + hashMap2.get(key) + "'");
                                sb.append(",");
                                if(!countryList.contains(key))
                                {
                                    countryList.add(key);
                                }
                                hashMap.put(dateVO.getDateLabel()+"-"+key, hashMap2.get(key));
                            }
                            hashMap1.put(dateVO.getDateLabel(), sb.toString());
                        }
                        if (data1.length() > 0 && hashMap.size() > 0)
                        {
                            weeklyNewTableData = new StringBuffer();
                            String month[] = data1.toString().split(",");
                            weeklyNewTableData.append("<table align=center width=\"50%\" class=\"display table table table-striped table-bordered table-hover dataTable\" style=\"font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;\">");
                            weeklyNewTableData.append("<tr>");
                            weeklyNewTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \" colspan=\"" + month.length + 1 + "\">" + displayRuleName + " [In Current Month]" + "</td>");
                            weeklyNewTableData.append("</tr>");
                            weeklyNewTableData.append("<tr>");
                            weeklyNewTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">&nbsp;</td>");
                            int j = 1;
                            for (int i = 0; i < month.length; i++)
                            {
                                weeklyNewTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + month[i].replace("\"", "") + "</td>");

                                DateVO dateVO = (DateVO) dateHashMap.get(month[i].replace("\"", ""));
                                String weeklyLabel = dateVO.getEndDate().split(" ")[0];
                                String data=(String)hashMap1.get(month[i].replace("\"", ""));
                                if(!functions.isValueNull(data)){
                                    data="'':'0'";
                                }
                                weeklyNewBarChartData.append("{ 'x':'" + weeklyLabel + "'," + data + "}");
                                if (j != month.length)
                                {
                                    weeklyNewBarChartData.append(",");
                                }
                                j = j + 1;
                            }
                            weeklyNewTableData.append("</tr>");
                            int k = 1;
                            for(String country : countryList)
                            {
                                weeklyNewTableData.append("<tr>");
                                weeklyNewTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + country + "</td>");
                                morrisWeeklyNewLabel.append("'" + country + "'");
                                if (hashMap.size() != k)
                                {
                                    morrisWeeklyNewLabel.append(",");
                                }
                                for (DateVO dateVO : list)
                                {
                                    if(hashMap.containsKey(dateVO.getDateLabel()+"-"+country))
                                    {
                                        k = k + 1;
                                        String labelValue = (String) hashMap.get(dateVO.getDateLabel()+"-"+country);
                                        weeklyNewTableData.append("<td align=\"center\">" + labelValue + "</td>");
                                    }else {
                                        weeklyNewTableData.append("<td align=\"center\">0</td>");
                                    }
                                }
                                weeklyNewTableData.append("</tr>");
                            }
                            weeklyNewTableData.append("</table>");
                        }
                        weeklyNewJsonStr = "data: [" + weeklyNewBarChartData + "],";
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedIP.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        StringBuffer sb = null;
                        List<DateVO> list = dateManager.getWeeksOfMonthNew();
                        HashMap hashMap1 = new HashMap();
                        HashMap hashMap2 = null;
                        List<String> ipList = new ArrayList();
                        for (DateVO dateVO : list)
                        {
                            sb=new StringBuffer();
                            hashMap2 = monitoringGraphsManager.getBlacklistIPCountFromTransaction(terminalVO, dateVO);
                            if (isData)
                            {
                                if (data1.length() > 0)
                                {
                                    data1.append(",");
                                }
                                data1.append("\"" + dateVO.getDateLabel() + "\"");
                            }
                            Iterator iterator = hashMap2.keySet().iterator();
                            while (iterator.hasNext())
                            {
                                String key=(String)iterator.next();
                                sb.append("'" + key + "':'" + hashMap2.get(key) + "'");
                                sb.append(",");
                                if(!ipList.contains(key))
                                {
                                    ipList.add(key);
                                }
                                hashMap.put(dateVO.getDateLabel()+"-"+key, hashMap2.get(key));
                            }
                            hashMap1.put(dateVO.getDateLabel(), sb.toString());
                        }

                        if (data1.length() > 0 && hashMap.size() > 0)
                        {
                            weeklyNewTableData = new StringBuffer();
                            String month[] = data1.toString().split(",");
                            weeklyNewTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            weeklyNewTableData.append("<tr>");
                            weeklyNewTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + month.length + 1 + "\">" + displayRuleName + " [In Current Month]" + "</td>");
                            weeklyNewTableData.append("</tr>");
                            weeklyNewTableData.append("<tr>");
                            weeklyNewTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            int j = 1;
                            for (int i = 0; i < month.length; i++)
                            {
                                weeklyNewTableData.append("<td align=\"center\" class=\"th0\">" + month[i].replace("\"", "") + "</td>");

                                DateVO dateVO = (DateVO) dateHashMap.get(month[i].replace("\"", ""));
                                String weeklyLabel = dateVO.getEndDate().split(" ")[0];

                                String data=(String)hashMap1.get(month[i].replace("\"", ""));
                                if(!functions.isValueNull(data)){
                                    data="'':'0'";
                                }
                                weeklyNewBarChartData.append("{ 'x':'" + weeklyLabel + "'," + data + "}");
                                if (j != month.length)
                                {
                                    weeklyNewBarChartData.append(",");
                                }
                                j = j + 1;
                            }
                            weeklyNewTableData.append("</tr>");
                            int k = 1;
                            for(String country : ipList)
                            {
                                weeklyNewTableData.append("<tr>");
                                weeklyNewTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + country + "</td>");
                                morrisWeeklyNewLabel.append("'" + country + "'");
                                if (hashMap.size() != k)
                                {
                                    morrisWeeklyNewLabel.append(",");
                                }
                                for (DateVO dateVO : list)
                                {
                                    if(hashMap.containsKey(dateVO.getDateLabel()+"-"+country))
                                    {
                                        k = k + 1;
                                        String labelValue = (String) hashMap.get(dateVO.getDateLabel()+"-"+country);
                                        weeklyNewTableData.append("<td align=\"center\">" + labelValue + "</td>");
                                    }else {
                                        weeklyNewTableData.append("<td align=\"center\">0</td>");
                                    }
                                }
                                weeklyNewTableData.append("</tr>");
                            }
                            weeklyNewTableData.append("</table>");
                        }
                        weeklyNewJsonStr = "data: [" + weeklyNewBarChartData + "],";
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedCard.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        List<DateVO> list = dateManager.getWeeksOfMonthNew();
                        HashMap hashMap1 = new HashMap();
                        HashMap hashMap2 = null;
                        StringBuffer sb = null;
                        List<String> cardList = new ArrayList();
                        for (DateVO dateVO : list)
                        {
                            sb=new StringBuffer();
                            hashMap2 = monitoringGraphsManager.getBlacklistCardCountFromTransaction(terminalVO, dateVO);
                            if (isData)
                            {
                                if (data1.length() > 0)
                                {
                                    data1.append(",");
                                }
                                data1.append("\"" + dateVO.getDateLabel() + "\"");
                            }
                            Iterator iterator = hashMap2.keySet().iterator();
                            while (iterator.hasNext())
                            {
                                String key=(String)iterator.next();
                                sb.append("'" + key + "':'" + hashMap2.get(key) + "'");
                                sb.append(",");
                                if(!cardList.contains(key))
                                {
                                    cardList.add(key);
                                }
                                hashMap.put(dateVO.getDateLabel()+"-"+key, hashMap2.get(key));
                            }
                            hashMap1.put(dateVO.getDateLabel(), sb.toString());
                        }

                        if (data1.length() > 0 && hashMap.size() > 0)
                        {
                            weeklyNewTableData = new StringBuffer();
                            String month[] = data1.toString().split(",");
                            weeklyNewTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            weeklyNewTableData.append("<tr>");
                            weeklyNewTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + month.length + 1 + "\">" + displayRuleName + " [In Current Month]" + "</td>");
                            weeklyNewTableData.append("</tr>");
                            weeklyNewTableData.append("<tr>");
                            weeklyNewTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            int j = 1;
                            for (int i = 0; i < month.length; i++)
                            {
                                weeklyNewTableData.append("<td align=\"center\" class=\"th0\">" + month[i].replace("\"", "") + "</td>");

                                DateVO dateVO = (DateVO) dateHashMap.get(month[i].replace("\"", ""));
                                String weeklyLabel = dateVO.getEndDate().split(" ")[0];
                                String data=(String)hashMap1.get(month[i].replace("\"", ""));
                                if(!functions.isValueNull(data)){
                                    data="'':'0'";
                                }
                                weeklyNewBarChartData.append("{ 'x':'" + weeklyLabel + "'," + data + "}");
                                if (j != month.length)
                                {
                                    weeklyNewBarChartData.append(",");
                                }
                                j = j + 1;
                            }
                            weeklyNewTableData.append("</tr>");
                            int k = 1;
                            for(String country : cardList)
                            {
                                weeklyNewTableData.append("<tr>");
                                weeklyNewTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + country + "</td>");
                                morrisWeeklyNewLabel.append("'" + country  + "'");
                                if (hashMap.size() != k)
                                {
                                    morrisWeeklyNewLabel.append(",");
                                }
                                for (DateVO dateVO : list)
                                {
                                    if(hashMap.containsKey(dateVO.getDateLabel()+"-"+country))
                                    {
                                        k = k + 1;
                                        String labelValue = (String) hashMap.get(dateVO.getDateLabel()+"-"+country);
                                        weeklyNewTableData.append("<td align=\"center\">" + labelValue + "</td>");
                                    }else {
                                        weeklyNewTableData.append("<td align=\"center\">0</td>");
                                    }
                                }
                                weeklyNewTableData.append("</tr>");
                            }
                            weeklyNewTableData.append("</table>");
                        }
                        weeklyNewJsonStr = "data: [" + weeklyNewBarChartData + "],";
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedEmail.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        List<DateVO> list = dateManager.getWeeksOfMonthNew();
                        HashMap hashMap1 = new HashMap();
                        HashMap hashMap2 = null;
                        StringBuffer sb = null;
                        List<String> emaiList = new ArrayList();
                        for (DateVO dateVO : list)
                        {
                            sb=new StringBuffer();
                            hashMap2 = monitoringGraphsManager.getBlacklistEmailCountFromTransaction(terminalVO, dateVO);
                            if (isData) {
                                if (data1.length() > 0)
                                {
                                    data1.append(",");
                                }
                                data1.append("\"" + dateVO.getDateLabel() + "\"");
                            }
                            Iterator iterator = hashMap2.keySet().iterator();
                            while (iterator.hasNext())
                            {
                                String key=(String)iterator.next();
                                sb.append("'" + key + "':'" + hashMap2.get(key) + "'");
                                sb.append(",");
                                if(!emaiList.contains(key))
                                {
                                    emaiList.add(key);
                                }
                                hashMap.put(dateVO.getDateLabel()+"-"+key, hashMap2.get(key));
                            }
                            hashMap1.put(dateVO.getDateLabel(), sb.toString());
                        }

                        if (data1.length() > 0 && hashMap.size() > 0)
                        {
                            weeklyNewTableData = new StringBuffer();
                            String month[] = data1.toString().split(",");
                            weeklyNewTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            weeklyNewTableData.append("<tr>");
                            weeklyNewTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + month.length + 1 + "\">" + displayRuleName + " [In Current Month]" + "</td>");
                            weeklyNewTableData.append("</tr>");
                            weeklyNewTableData.append("<tr>");
                            weeklyNewTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            int j = 1;
                            for (int i = 0; i < month.length; i++)
                            {
                                weeklyNewTableData.append("<td align=\"center\" class=\"th0\">" + month[i].replace("\"", "") + "</td>");
                                DateVO dateVO = (DateVO) dateHashMap.get(month[i].replace("\"", ""));
                                String weeklyLabel = dateVO.getEndDate().split(" ")[0];
                                String data=(String)hashMap1.get(month[i].replace("\"", ""));
                                if(!functions.isValueNull(data)){
                                    data="'':'0'";
                                }
                                weeklyNewBarChartData.append("{ 'x':'" + weeklyLabel + "'," + data + "}");
                                if (j != month.length)
                                {
                                    weeklyNewBarChartData.append(",");
                                }
                                j = j + 1;
                            }
                            weeklyNewTableData.append("</tr>");
                            int k = 1;
                            for(String country : emaiList)
                            {
                                weeklyNewTableData.append("<tr>");
                                weeklyNewTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + country + "</td>");
                                morrisWeeklyNewLabel.append("'" + country + "'");
                                if (hashMap.size() != k)
                                {
                                    morrisWeeklyNewLabel.append(",");
                                }
                                for (DateVO dateVO : list)
                                {
                                    if(hashMap.containsKey(dateVO.getDateLabel()+"-"+country))
                                    {
                                        k = k + 1;
                                        String labelValue = (String) hashMap.get(dateVO.getDateLabel()+"-"+country);
                                        weeklyNewTableData.append("<td align=\"center\">" + labelValue + "</td>");
                                    }else {
                                        weeklyNewTableData.append("<td align=\"center\">0</td>");
                                    }
                                }
                                weeklyNewTableData.append("</tr>");
                            }
                            weeklyNewTableData.append("</table>");
                        }
                        weeklyNewJsonStr = "data: [" + weeklyNewBarChartData + "],";
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedName.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        List<DateVO> list = dateManager.getWeeksOfMonthNew();
                        HashMap hashMap1 = new HashMap();
                        HashMap hashMap2 = null;
                        StringBuffer sb = null;
                        List<String> nameList = new ArrayList();
                        for (DateVO dateVO : list)
                        {
                            sb=new StringBuffer();
                            hashMap2 = monitoringGraphsManager.getBlacklistNameCountFromTransaction(terminalVO, dateVO);
                            if (isData)
                            {
                                if (data1.length() > 0)
                                {
                                    data1.append(",");
                                }
                                data1.append("\"" + dateVO.getDateLabel() + "\"");
                            }
                            Iterator iterator = hashMap2.keySet().iterator();
                            while (iterator.hasNext())
                            {
                                String key=(String)iterator.next();
                                sb.append("'" + key + "':'" + hashMap2.get(key) + "'");
                                sb.append(",");
                                if(!nameList.contains(key))
                                {
                                    nameList.add(key);
                                }
                                hashMap.put(dateVO.getDateLabel()+"-"+key, hashMap2.get(key));
                            }
                            hashMap1.put(dateVO.getDateLabel(), sb.toString());
                        }

                        if (data1.length() > 0 && hashMap.size() > 0)
                        {
                            weeklyNewTableData = new StringBuffer();
                            String month[] = data1.toString().split(",");
                            weeklyNewTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            weeklyNewTableData.append("<tr>");
                            weeklyNewTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + month.length + 1 + "\">" + displayRuleName + " [In Current Month]" + "</td>");
                            weeklyNewTableData.append("</tr>");
                            weeklyNewTableData.append("<tr>");
                            weeklyNewTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            int j = 1;
                            for (int i = 0; i < month.length; i++)
                            {
                                weeklyNewTableData.append("<td align=\"center\" class=\"th0\">" + month[i].replace("\"", "") + "</td>");
                                DateVO dateVO = (DateVO) dateHashMap.get(month[i].replace("\"", ""));
                                String weeklyLabel = dateVO.getEndDate().split(" ")[0];
                                String data=(String)hashMap1.get(month[i].replace("\"", ""));
                                if(!functions.isValueNull(data)){
                                    data="'':'0'";
                                }

                                weeklyNewBarChartData.append("{ 'x':'" + weeklyLabel + "'," + data + "}");
                                if (j != month.length)
                                {
                                    weeklyNewBarChartData.append(",");
                                }
                                j = j + 1;
                            }
                            weeklyNewTableData.append("</tr>");
                            int k = 1;
                            for(String country : nameList)
                            {
                                weeklyNewTableData.append("<tr>");
                                weeklyNewTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + country + "</td>");
                                morrisWeeklyNewLabel.append("'" + country + "'");
                                if (hashMap.size() != k)
                                {
                                    morrisWeeklyNewLabel.append(",");
                                }
                                for (DateVO dateVO : list)
                                {
                                    if(hashMap.containsKey(dateVO.getDateLabel()+"-"+country))
                                    {
                                        k = k + 1;
                                        String labelValue = (String) hashMap.get(dateVO.getDateLabel()+"-"+country);
                                        weeklyNewTableData.append("<td align=\"center\">" + labelValue + "</td>");
                                    }else {
                                        weeklyNewTableData.append("<td align=\"center\">0</td>");
                                    }
                                }
                                weeklyNewTableData.append("</tr>");
                            }
                            weeklyNewTableData.append("</table>");
                        }
                        weeklyNewJsonStr = "data: [" + weeklyNewBarChartData + "],";
                    }
                }
                else
                {
                    logger.debug("Data Not Found");
                }
            }
            StringBuffer morrisMonthlyLabel = new StringBuffer();
            if ("monthly".equals(monthlyFrequency))
            {
                String tableHeaderFreq = " in last six month";
                if ("Y".equals(isMonthlyExecution))
                {
                    StringBuffer monthlyLineChartData = new StringBuffer();
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        alertThreshold = monitoringParameterMappingVO.getMonthlyAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getMonthlySuspensionThreshold();
                        List<int[]> ints = monitoringGraphsManager.getCategoryByThreshold(alertThreshold, suspensionThreshold);

                        int[] categoryArray1 = ints.get(0);
                        int[] categoryArray2 = ints.get(1);
                        int[] categoryArray3 = ints.get(2);
                        int[] categoryArray4 = ints.get(3);

                        String category1 = categoryArray1[0] + " - " + categoryArray1[1] + ".99";
                        String category2 = categoryArray2[0] + " - " + categoryArray2[1] + ".99";
                        String category3 = categoryArray3[0] + " - " + categoryArray3[1] + ".99";
                        String category4 = categoryArray4[0] + " and above";

                        StringBuffer label = new StringBuffer();
                        StringBuffer catagoryData1 = new StringBuffer();
                        StringBuffer catagoryData2 = new StringBuffer();
                        StringBuffer catagoryData3 = new StringBuffer();
                        StringBuffer catagoryData4 = new StringBuffer();

                        List<DateVO> list = dateManager.getMonthlyLineChartQuarter();
                        int k = 1;
                        for (DateVO dateVO : list)
                        {
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailMonthly)
                            {
                                maxRatioDetailMonthly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);

                            String monthLabel[] = dateVO.getStartDate().split(" ")[0].split("-");
                            String barChartMonthLabel = monthLabel[0] + "-" + monthLabel[1];
                            monthlyLineChartData.append("{'x': '" + barChartMonthLabel + "', '" + category1 + "':'" + data01 + "','" + category2 + "':'" + data02 + "','" + category3 + "':'" + data03 + "','" + category4 + "':'" + data04 + "' }");
                            if (list.size() != k)
                            {
                                monthlyLineChartData.append(",");
                            }
                            k = k + 1;

                        }
                        monthlyJsonStr = "data: [" + monthlyLineChartData + "],";
                        morrisMonthlyLabel.append("'" + category1 + "','" + category2 + "','" + category3 + "','" + category4 + "'");
                        monthlyTableData = monitoringGraphsManager.prepareStackBarChartTableForPartner(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName, tableHeaderFreq, monitoringParameterVO);

                       /* DateVO dateVO = dateManager.getLastSixCalendarMonthDateRangeForGraph();
                        HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                        if (hashMap != null){
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            if (data01 > maxRatioCompositeMonthly){
                                maxRatioCompositeMonthly = data01;
                            }
                            if (data02 > maxRatioCompositeMonthly){
                                maxRatioCompositeMonthly = data02;
                            }
                            if (data03 > maxRatioCompositeMonthly){
                                maxRatioCompositeMonthly = data03;
                            }
                            if (data04 > maxRatioCompositeMonthly){
                                maxRatioCompositeMonthly = data04;
                            }
                            //monthlyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }*/
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardSameAmountConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        alertThreshold = monitoringParameterMappingVO.getMonthlyAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getMonthlySuspensionThreshold();
                        DateVO compositDateVO = dateManager.getLastSixCalendarMonthDateRangeForGraph();
                        double[] doubles = monitoringGraphsManager.getMinMaxTransactionAmountByDate(terminalVO, compositDateVO);
                        List<int[]> ints = monitoringGraphsManager.getCategoryByThreshold(doubles[0], doubles[1]);

                        int[] categoryArray1 = ints.get(0);
                        int[] categoryArray2 = ints.get(1);
                        int[] categoryArray3 = ints.get(2);
                        int[] categoryArray4 = ints.get(3);

                        String category1 = categoryArray1[0] + " - " + categoryArray1[1] + ".99";
                        String category2 = categoryArray2[0] + " - " + categoryArray2[1] + ".99";
                        String category3 = categoryArray3[0] + " - " + categoryArray3[1] + ".99";
                        String category4 = categoryArray4[0] + " and above";

                        StringBuffer label = new StringBuffer();
                        StringBuffer catagoryData1 = new StringBuffer();
                        StringBuffer catagoryData2 = new StringBuffer();
                        StringBuffer catagoryData3 = new StringBuffer();
                        StringBuffer catagoryData4 = new StringBuffer();

                        List<DateVO> list = dateManager.getMonthlyLineChartQuarter();
                        int k = 1;
                        for (DateVO dateVO : list)
                        {
                            HashMap hashMap = monitoringGraphsManager.getCardCountBasedOnBinAndAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailMonthly)
                            {
                                maxRatioDetailMonthly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);

                            String monthLabel[] = dateVO.getStartDate().split(" ")[0].split("-");
                            String barChartMonthLabel = monthLabel[0] + "-" + monthLabel[1];
                            monthlyLineChartData.append("{'x': '" + barChartMonthLabel + "', '" + category1 + "':'" + data01 + "','" + category2 + "':'" + data02 + "','" + category3 + "':'" + data03 + "','" + category4 + "':'" + data04 + "' }");
                            if (list.size() != k)
                            {
                                monthlyLineChartData.append(",");
                            }
                            k = k + 1;
                        }
                        monthlyJsonStr = "data: [" + monthlyLineChartData + "],";
                        morrisMonthlyLabel.append("'" + category1 + "','" + category2 + "','" + category3 + "','" + category4 + "'");
                        monthlyTableData = monitoringGraphsManager.prepareStackBarChartTableForPartner(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName, tableHeaderFreq, monitoringParameterVO);

                       /* HashMap hashMap = monitoringGraphsManager.getCardCountBasedOnBinAndAmountCategory(terminalVO, compositDateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                        if (hashMap != null){
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            if (data01 > maxRatioCompositeMonthly){
                                maxRatioCompositeMonthly = data01;
                            }
                            if (data02 > maxRatioCompositeMonthly){
                                maxRatioCompositeMonthly = data02;
                            }
                            if (data03 > maxRatioCompositeMonthly){
                                maxRatioCompositeMonthly = data03;
                            }
                            if (data04 > maxRatioCompositeMonthly){
                                maxRatioCompositeMonthly = data04;
                            }
                            //monthlyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }*/
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        alertThreshold = monitoringParameterMappingVO.getMonthlyAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getMonthlySuspensionThreshold();
                        List<int[]> ints = monitoringGraphsManager.getCategoryByThreshold(alertThreshold, suspensionThreshold);

                        int[] categoryArray1 = ints.get(0);
                        int[] categoryArray2 = ints.get(1);
                        int[] categoryArray3 = ints.get(2);
                        int[] categoryArray4 = ints.get(3);

                        String category1 = categoryArray1[0] + " - " + categoryArray1[1];
                        String category2 = categoryArray2[0] + " - " + categoryArray2[1];
                        String category3 = categoryArray3[0] + " - " + categoryArray3[1];
                        String category4 = categoryArray4[0] + " times and above";

                        StringBuffer label = new StringBuffer();
                        StringBuffer catagoryData1 = new StringBuffer();
                        StringBuffer catagoryData2 = new StringBuffer();
                        StringBuffer catagoryData3 = new StringBuffer();
                        StringBuffer catagoryData4 = new StringBuffer();

                        List<DateVO> list = dateManager.getMonthlyLineChartQuarter();
                        int k = 1;
                        for (DateVO dateVO : list)
                        {
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailMonthly)
                            {
                                maxRatioDetailMonthly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);

                            String monthLabel[] = dateVO.getStartDate().split(" ")[0].split("-");
                            String barChartMonthLabel = monthLabel[0] + "-" + monthLabel[1];

                            monthlyLineChartData.append("{'x': '" + barChartMonthLabel + "', '" + category1 + "':'" + data01 + "','" + category2 + "':'" + data02 + "','" + category3 + "':'" + data03 + "','" + category4 + "':'" + data04 + "'}");
                            if (list.size() != k)
                            {
                                monthlyLineChartData.append(",");
                            }
                            k = k + 1;
                        }
                        monthlyJsonStr = "data: [" + monthlyLineChartData + "],";
                        morrisMonthlyLabel.append("'" + category1 + "','" + category2 + "','" + category3 + "','" + category4 + "'");
                        monthlyTableData = monitoringGraphsManager.prepareStackBarChartTableForPartner(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName, tableHeaderFreq, monitoringParameterVO);

                        /*DateVO dateVO = dateManager.getLastSixCalendarMonthDateRangeForGraph();
                        HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                        if (hashMap != null){
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");

                            if (data01 > maxRatioCompositeMonthly){
                                maxRatioCompositeMonthly = data01;
                            }
                            if (data02 > maxRatioCompositeMonthly){
                                maxRatioCompositeMonthly = data02;
                            }
                            if (data03 > maxRatioCompositeMonthly){
                                maxRatioCompositeMonthly = data03;
                            }
                            if (data04 > maxRatioCompositeMonthly){
                                maxRatioCompositeMonthly = data04;
                            }
                            //monthlyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }*/
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        alertThreshold = monitoringParameterMappingVO.getMonthlyAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getMonthlySuspensionThreshold();
                        List<int[]> ints = monitoringGraphsManager.getCategoryByThreshold(alertThreshold, suspensionThreshold);

                        int[] categoryArray1 = ints.get(0);
                        int[] categoryArray2 = ints.get(1);
                        int[] categoryArray3 = ints.get(2);
                        int[] categoryArray4 = ints.get(3);

                        String category1 = categoryArray1[0] + " - " + categoryArray1[1];
                        String category2 = categoryArray2[0] + " - " + categoryArray2[1];
                        String category3 = categoryArray3[0] + " - " + categoryArray3[1];
                        String category4 = categoryArray4[0] + " times and above";

                        StringBuffer label = new StringBuffer();
                        StringBuffer catagoryData1 = new StringBuffer();
                        StringBuffer catagoryData2 = new StringBuffer();
                        StringBuffer catagoryData3 = new StringBuffer();
                        StringBuffer catagoryData4 = new StringBuffer();
                        List<DateVO> list = dateManager.getMonthlyLineChartQuarter();
                        int k = 1;
                        for (DateVO dateVO : list)
                        {
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinUsedDays(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailMonthly)
                            {
                                maxRatioDetailMonthly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);

                            String monthLabel[] = dateVO.getStartDate().split(" ")[0].split("-");
                            String barChartMonthLabel = monthLabel[0] + "-" + monthLabel[1];

                            monthlyLineChartData.append("{'x': '" + barChartMonthLabel + "', '" + category1 + "':'" + data01 + "','" + category2 + "':'" + data02 + "','" + category3 + "':'" + data03 + "','" + category4 + "':'" + data04 + " '}");
                            if (list.size() != k)
                            {
                                monthlyLineChartData.append(",");
                            }
                            k = k + 1;
                        }
                        monthlyJsonStr = "data: [" + monthlyLineChartData + "],";
                        morrisMonthlyLabel.append("'" + category1 + "','" + category2 + "','" + category3 + "','" + category4 + "'");
                        monthlyTableData = monitoringGraphsManager.prepareStackBarChartTableForPartner(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName, tableHeaderFreq, monitoringParameterVO);

                        /*DateVO dateVO = dateManager.getLastSixMonthDateRange();
                        HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinUsedDays(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                        if (hashMap != null){
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");

                            if (data01 > maxRatioCompositeMonthly){
                                maxRatioCompositeMonthly = data01;
                            }
                            if (data02 > maxRatioCompositeMonthly){
                                maxRatioCompositeMonthly = data02;
                            }
                            if (data03 > maxRatioCompositeMonthly){
                                maxRatioCompositeMonthly = data03;
                            }
                            if (data04 > maxRatioCompositeMonthly){
                                maxRatioCompositeMonthly = data04;
                            }
                            //monthlyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }*/
                    }
                    data1 = new StringBuffer();
                    boolean isData = true;
                    HashMap hashMap = new HashMap();
                    HashMap dateHashMap = new HashMap();
                    List<DateVO> list11 = dateManager.getMonthlyLineChartQuarter();
                    for (DateVO dateVO : list11)
                    {
                        dateHashMap.put(dateVO.getDateLabel(), dateVO);
                    }

                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedCountry.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        List<DateVO> list = dateManager.getMonthlyLineChartQuarter();
                        HashMap hashMap1 = new HashMap();
                        HashMap hashMap2 = null;
                        StringBuffer sb=null;
                        List<String> countryList = new ArrayList();
                        for (DateVO dateVO : list)
                        {
                            sb=new StringBuffer();
                            hashMap2 = monitoringGraphsManager.getBlacklistCountryCountFromTransaction(terminalVO, dateVO);
                            if(isData)
                            {
                                if(data1.length() > 0)
                                {
                                    data1.append(",");
                                }
                                data1.append("\"" +dateVO.getDateLabel()+ "\"");
                            }
                            Iterator iterator = hashMap2.keySet().iterator();
                            while (iterator.hasNext())
                            {
                                String key=(String)iterator.next();
                                sb.append("'" + key + "':'" + hashMap2.get(key) + "'");
                                sb.append(",");
                                if(!countryList.contains(key))
                                {
                                    countryList.add(key);
                                }
                                hashMap.put(dateVO.getDateLabel()+"-"+key, hashMap2.get(key));
                            }
                            hashMap1.put(dateVO.getDateLabel(), sb.toString());
                        }

                        if (data1.length() > 0 && hashMap.size() > 0)
                        {
                            monthlyTableData = new StringBuffer();
                            String month[] = data1.toString().split(",");
                            monthlyTableData.append("<table align=center width=\"50%\" class=\"display table table table-striped table-bordered table-hover dataTable\" style=\"font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;\">");
                            monthlyTableData.append("<tr>");
                            monthlyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \" colspan=\"" + month.length + 1 + "\">" + displayRuleName + " [Last Six Months]" + "</td>");
                            monthlyTableData.append("</tr>");
                            monthlyTableData.append("<tr>");
                            monthlyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">&nbsp;</td>");
                            int j = 1;
                            for (int i = 0; i < month.length; i++)
                            {
                                monthlyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + month[i].replace("\"", "") + "</td>");
                                DateVO dateVO = (DateVO) dateHashMap.get(month[i].replace("\"", ""));
                                String monthLabel[] = dateVO.getStartDate().split(" ")[0].split("-");
                                String barChartMonthLabel = monthLabel[0] + "-" + monthLabel[1];
                                String data=(String)hashMap1.get(month[i].replace("\"", ""));
                                if(!functions.isValueNull(data)){
                                    data="'':'0'";
                                }
                                monthlyLineChartData.append("{ 'x':'" + barChartMonthLabel + "'," + data + "}");
                                if (j != month.length)
                                {
                                    monthlyLineChartData.append(",");
                                }
                                j = j + 1;
                            }
                            monthlyTableData.append("</tr>");
                            int k = 1;
                            for(String country : countryList)
                            {
                                monthlyTableData.append("<tr>");
                                monthlyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + country + "</td>");
                                morrisMonthlyLabel.append("'" + country + "'");
                                if (hashMap.size() != k)
                                {
                                    morrisMonthlyLabel.append(",");
                                }
                                for (DateVO dateVO : list)
                                {
                                    if(hashMap.containsKey(dateVO.getDateLabel()+"-"+country))
                                    {
                                        k = k + 1;
                                        String labelValue = (String) hashMap.get(dateVO.getDateLabel()+"-"+country);
                                        monthlyTableData.append("<td align=\"center\">" + labelValue + "</td>");
                                    }else {
                                        monthlyTableData.append("<td align=\"center\">0</td>");
                                    }
                                }
                                monthlyTableData.append("</tr>");
                            }
                            monthlyTableData.append("</table>");
                        }
                        monthlyJsonStr = "data:[" + monthlyLineChartData + "],";
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedIP.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        StringBuffer sb = new StringBuffer();
                        HashMap hashMap1 = new HashMap();
                        HashMap hashMap2 = null;
                        List<String> ipList = new ArrayList();
                        List<DateVO> list = dateManager.getMonthlyLineChartQuarter();
                        for (DateVO dateVO : list)
                        {
                            hashMap2 = monitoringGraphsManager.getBlacklistIPCountFromTransaction(terminalVO, dateVO);
                            if (isData)
                            {
                                if (data1.length() > 0)
                                {
                                    data1.append(",");
                                }
                                data1.append("\"" + dateVO.getDateLabel() + "\"");
                            }
                            Iterator iterator = hashMap2.keySet().iterator();
                            while (iterator.hasNext())
                            {
                                String key=(String)iterator.next();
                                sb.append("'" + key + "':'" + hashMap2.get(key) + "'");
                                sb.append(",");
                                if(!ipList.contains(key))
                                {
                                    ipList.add(key);
                                }
                                hashMap.put(dateVO.getDateLabel()+"-"+key, hashMap2.get(key));
                            }
                            hashMap1.put(dateVO.getDateLabel(), sb.toString());
                        }

                        if (data1.length() > 0 && hashMap.size() > 0)
                        {
                            monthlyTableData = new StringBuffer();
                            String month[] = data1.toString().split(",");
                            monthlyTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            monthlyTableData.append("<tr>");
                            monthlyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + month.length + 1 + "\">" + displayRuleName + " [Last Six Months]" + "</td>");
                            monthlyTableData.append("</tr>");
                            monthlyTableData.append("<tr>");
                            monthlyTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            int j = 1;
                            for (int i = 0; i < month.length; i++)
                            {
                                monthlyTableData.append("<td align=\"center\" class=\"th0\">" + month[i].replace("\"", "") + "</td>");

                                DateVO dateVO = (DateVO) dateHashMap.get(month[i].replace("\"", ""));
                                String monthLabel[] = dateVO.getStartDate().split(" ")[0].split("-");
                                String barChartMonthLabel = monthLabel[0] + "-" + monthLabel[1];
                                String data=(String)hashMap1.get(month[i].replace("\"", "")) ;
                                if(!functions.isValueNull(data)){
                                    data="'':'0'";
                                }
                                monthlyLineChartData.append("{ 'x':'" + barChartMonthLabel + "'," + data + "}");
                                if (j != month.length)
                                {
                                    monthlyLineChartData.append(",");
                                }
                                j = j + 1;
                            }
                            monthlyTableData.append("</tr>");
                            int k = 1;
                            for(String country : ipList)
                            {
                                monthlyTableData.append("<tr>");
                                monthlyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + country + "</td>");
                                morrisMonthlyLabel.append("'" + country + "'");
                                if (hashMap.size() != k)
                                {
                                    morrisMonthlyLabel.append(",");
                                }
                                for (DateVO dateVO : list)
                                {
                                    if(hashMap.containsKey(dateVO.getDateLabel()+"-"+country))
                                    {
                                        k = k + 1;
                                        String labelValue = (String) hashMap.get(dateVO.getDateLabel()+"-"+country);
                                        monthlyTableData.append("<td align=\"center\">" + labelValue + "</td>");
                                    }else {
                                        monthlyTableData.append("<td align=\"center\">0</td>");
                                    }
                                }
                                monthlyTableData.append("</tr>");
                            }
                            monthlyTableData.append("</table>");
                        }
                        monthlyJsonStr = "data:[" + monthlyLineChartData + "],";
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedCard.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        List<DateVO> list = dateManager.getMonthlyLineChartQuarter();
                        HashMap hashMap1 = new HashMap();
                        HashMap hashMap2 = null;
                        StringBuffer sb = null;
                        List<String> cardList = new ArrayList();
                        for (DateVO dateVO : list)
                        {
                            sb=new StringBuffer();
                            hashMap2 = monitoringGraphsManager.getBlacklistCardCountFromTransaction(terminalVO, dateVO);
                            if (isData)
                            {
                                if (data1.length() > 0)
                                {
                                    data1.append(",");
                                }
                                data1.append("\"" + dateVO.getDateLabel() + "\"");
                            }
                            Iterator iterator = hashMap2.keySet().iterator();
                            while (iterator.hasNext())
                            {
                                String key=(String)iterator.next();
                                sb.append("'" + key + "':'" + hashMap2.get(key) + "'");
                                sb.append(",");
                                if(!cardList.contains(key))
                                {
                                    cardList.add(key);
                                }
                                hashMap.put(dateVO.getDateLabel()+"-"+key, hashMap2.get(key));
                            }
                            hashMap1.put(dateVO.getDateLabel(), sb.toString());
                        }
                        if (data1.length() > 0 && hashMap.size() > 0)
                        {
                            monthlyTableData = new StringBuffer();
                            String month[] = data1.toString().split(",");
                            monthlyTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            monthlyTableData.append("<tr>");
                            monthlyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + month.length + 1 + "\">" + displayRuleName + " [Last Six Months]" + "</td>");
                            monthlyTableData.append("</tr>");
                            monthlyTableData.append("<tr>");
                            monthlyTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            int j = 1;
                            for (int i = 0; i < month.length; i++)
                            {
                                monthlyTableData.append("<td align=\"center\" class=\"th0\">" + month[i].replace("\"", "") + "</td>");

                                DateVO dateVO = (DateVO) dateHashMap.get(month[i].replace("\"", ""));
                                String monthLabel[] = dateVO.getStartDate().split(" ")[0].split("-");
                                String barChartMonthLabel = monthLabel[0] + "-" + monthLabel[1];
                                String data=(String)hashMap1.get(month[i].replace("\"", ""));
                                if(!functions.isValueNull(data)){
                                    data="'':'0'";
                                }
                                monthlyLineChartData.append("{ 'x':'" + barChartMonthLabel + "'," + data + "}");
                                if (j != month.length)
                                {
                                    monthlyLineChartData.append(",");
                                }
                                j = j + 1;
                            }
                            monthlyTableData.append("</tr>");
                            int k = 1;
                            for(String country : cardList)
                            {
                                monthlyTableData.append("<tr>");
                                monthlyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + country + "</td>");
                                morrisMonthlyLabel.append("'" + country + "'");
                                if (hashMap.size() != k)
                                {
                                    morrisMonthlyLabel.append(",");
                                }
                                for (DateVO dateVO : list)
                                {
                                    if(hashMap.containsKey(dateVO.getDateLabel()+"-"+country))
                                    {
                                        k = k + 1;
                                        String labelValue = (String) hashMap.get(dateVO.getDateLabel()+"-"+country);
                                        monthlyTableData.append("<td align=\"center\">" + labelValue + "</td>");
                                    }else {
                                        monthlyTableData.append("<td align=\"center\">0</td>");
                                    }
                                }
                                monthlyTableData.append("</tr>");
                            }
                            monthlyTableData.append("</table>");
                        }
                        monthlyJsonStr = "data:[" + monthlyLineChartData + "],";

                        logger.error("CardMonthly LineChart:::::"+monthlyJsonStr);
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedEmail.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        List<DateVO> list = dateManager.getMonthlyLineChartQuarter();
                        HashMap hashMap1 = new HashMap();
                        HashMap hashMap2 = null;
                        StringBuffer sb = null;
                        List<String> emailList = new ArrayList();
                        for (DateVO dateVO : list)
                        {
                            sb=new StringBuffer();
                            hashMap2 = monitoringGraphsManager.getBlacklistEmailCountFromTransaction(terminalVO, dateVO);
                            if (isData)
                            {
                                if (data1.length() > 0)
                                {
                                    data1.append(",");
                                }
                                data1.append("\"" + dateVO.getDateLabel() + "\"");
                            }
                            Iterator iterator = hashMap2.keySet().iterator();
                            while (iterator.hasNext())
                            {
                                String key=(String)iterator.next();
                                sb.append("'" + key + "':'" + hashMap2.get(key) + "'");
                                sb.append(",");
                                if(!emailList.contains(key))
                                {
                                    emailList.add(key);
                                }
                                hashMap.put(dateVO.getDateLabel()+"-"+key, hashMap2.get(key));
                            }
                            hashMap1.put(dateVO.getDateLabel(), sb.toString());
                        }
                        if (data1.length() > 0 && hashMap.size() > 0)
                        {
                            monthlyTableData = new StringBuffer();
                            String month[] = data1.toString().split(",");
                            monthlyTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            monthlyTableData.append("<tr>");
                            monthlyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + month.length + 1 + "\">" + displayRuleName + " [Last Six Months]" + "</td>");
                            monthlyTableData.append("</tr>");
                            monthlyTableData.append("<tr>");
                            monthlyTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            int j = 1;
                            for (int i = 0; i < month.length; i++)
                            {
                                monthlyTableData.append("<td align=\"center\" class=\"th0\">" + month[i].replace("\"", "") + "</td>");

                                DateVO dateVO = (DateVO) dateHashMap.get(month[i].replace("\"", ""));
                                String monthLabel[] = dateVO.getStartDate().split(" ")[0].split("-");
                                String barChartMonthLabel = monthLabel[0] + "-" + monthLabel[1];
                                String data=(String)hashMap1.get(month[i].replace("\"", ""));
                                if(!functions.isValueNull(data)){
                                    data="'':'0'";
                                }
                                monthlyLineChartData.append("{ 'x':'" + barChartMonthLabel + "'," + data + " }");
                                if (j != month.length)
                                {
                                    monthlyLineChartData.append(",");
                                }
                                j = j + 1;
                            }
                            monthlyTableData.append("</tr>");
                            int k = 1;
                            for(String country : emailList)
                            {
                                monthlyTableData.append("<tr>");
                                monthlyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + country + "</td>");
                                morrisMonthlyLabel.append("'" + country + "'");
                                if (hashMap.size() != k)
                                {
                                    morrisMonthlyLabel.append(",");
                                }
                                for (DateVO dateVO : list)
                                {
                                    if(hashMap.containsKey(dateVO.getDateLabel()+"-"+country))
                                    {
                                        k = k + 1;
                                        String labelValue = (String) hashMap.get(dateVO.getDateLabel()+"-"+country);
                                        monthlyTableData.append("<td align=\"center\">" + labelValue + "</td>");
                                    }else {
                                        monthlyTableData.append("<td align=\"center\">0</td>");
                                    }
                                }
                                monthlyTableData.append("</tr>");
                            }
                            monthlyTableData.append("</table>");
                        }
                        monthlyJsonStr = "data:[" + monthlyLineChartData + "],";
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedName.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()))
                    {
                        List<DateVO> list = dateManager.getMonthlyLineChartQuarter();
                        HashMap hashMap1 = new HashMap();
                        HashMap hashMap2 = null;
                        StringBuffer sb = null;
                        List<String> nameList = new ArrayList();
                        for (DateVO dateVO : list)
                        {
                            sb=new StringBuffer();
                            hashMap2 = monitoringGraphsManager.getBlacklistNameCountFromTransaction(terminalVO, dateVO);
                            if (isData)
                            {
                                if (data1.length() > 0)
                                {
                                    data1.append(",");
                                }
                                data1.append("\"" + dateVO.getDateLabel() + "\"");
                            }
                            Iterator iterator = hashMap2.keySet().iterator();
                            while (iterator.hasNext())
                            {
                                String key=(String)iterator.next();
                                sb.append("'" + key + "':'" + hashMap2.get(key) + "'");
                                sb.append(",");
                                if(!nameList.contains(key))
                                {
                                    nameList.add(key);
                                }
                                hashMap.put(dateVO.getDateLabel()+"-"+key, hashMap2.get(key));
                            }
                            hashMap1.put(dateVO.getDateLabel(), sb.toString());
                        }

                        if (data1.length() > 0 && hashMap.size() > 0)
                        {
                            monthlyTableData = new StringBuffer();
                            String month[] = data1.toString().split(",");
                            monthlyTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            monthlyTableData.append("<tr>");
                            monthlyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + month.length + 1 + "\">" + displayRuleName + " [Last Six Months]" + "</td>");
                            monthlyTableData.append("</tr>");
                            monthlyTableData.append("<tr>");
                            monthlyTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            int j = 1;
                            for (int i = 0; i < month.length; i++)
                            {
                                monthlyTableData.append("<td align=\"center\" class=\"th0\">" + month[i].replace("\"", "") + "</td>");

                                DateVO dateVO = (DateVO) dateHashMap.get(month[i].replace("\"", ""));
                                String monthLabel[] = dateVO.getStartDate().split(" ")[0].split("-");
                                String barChartMonthLabel = monthLabel[0] + "-" + monthLabel[1];
                                String data=(String)hashMap1.get(month[i].replace("\"", ""));
                                if(!functions.isValueNull(data)){
                                    data="'':'0'";
                                }
                                monthlyLineChartData.append("{ 'x':'" + barChartMonthLabel + "'," + data + "}");
                                if (j != month.length)
                                {
                                    monthlyLineChartData.append(",");
                                }
                                j = j + 1;
                            }
                            monthlyTableData.append("</tr>");
                            int k = 1;
                            for(String country : nameList)
                            {
                                monthlyTableData.append("<tr>");
                                monthlyTableData.append("<td align=\"center\" style=\"background-color: #7eccad !important;color: white; \">" + country + "</td>");
                                morrisMonthlyLabel.append("'" + country + "'");
                                if (hashMap.size() != k)
                                {
                                    morrisMonthlyLabel.append(",");
                                }
                                for (DateVO dateVO : list)
                                {
                                    if(hashMap.containsKey(dateVO.getDateLabel()+"-"+country))
                                    {
                                        k = k + 1;
                                        String labelValue = (String) hashMap.get(dateVO.getDateLabel()+"-"+country);
                                        monthlyTableData.append("<td align=\"center\">" + labelValue + "</td>");
                                    }else {
                                        monthlyTableData.append("<td align=\"center\">0</td>");
                                    }
                                }
                                monthlyTableData.append("</tr>");
                            }
                            monthlyTableData.append("</table>");
                        }
                        monthlyJsonStr = "data:[" + monthlyLineChartData + "],";
                    }
                }
            }
            request.setAttribute("displayRuleName", displayRuleName);
            request.setAttribute("dailyJsonStr", dailyJsonStr);
            request.setAttribute("weeklyJsonStr", weeklyJsonStr);
            request.setAttribute("weeklyNewJsonStr", weeklyNewJsonStr);
            request.setAttribute("monthlyJsonStr", monthlyJsonStr);
            request.setAttribute("morrisMonthlyLabel", morrisMonthlyLabel.toString());
            request.setAttribute("morrisWeeklyNewLabel", morrisWeeklyNewLabel.toString());
            request.setAttribute("morrisWeeklyLabel", morrisWeeklyLabel.toString());
            request.setAttribute("morrisDailyLabel", morrisDailyLabel.toString());
            request.setAttribute("monthlyJsonStr", monthlyJsonStr);
            request.setAttribute("monitoringUnit", monitoringUnit);
            //request.setAttribute("dailyJsonStr1", dailyJsonStr1);
            //request.setAttribute("weeklyJsonStr1", weeklyJsonStr1);
            //request.setAttribute("weeklyNewJsonStr1", weeklyNewJsonStr1);
            //request.setAttribute("monthlyJsonStr1", monthlyJsonStr1);
           /* request.setAttribute("maxRatioCompositeHoursly", maxRatioCompositeHoursly);
            request.setAttribute("maxRatioCompositeDaily", maxRatioCompositeDaily);
            request.setAttribute("maxRatioCompositeWeekly", maxRatioCompositeWeekly);
            request.setAttribute("maxRatioCompositeMonthly", maxRatioCompositeMonthly);
            request.setAttribute("maxRatioDetailHoursly", maxRatioDetailHoursly);
            request.setAttribute("maxRatioDetailDaily", maxRatioDetailDaily);
            request.setAttribute("maxRatioDetailWeekly", maxRatioDetailWeekly);
            request.setAttribute("maxRatioDetailMonthly", maxRatioDetailMonthly);*/

            if (dailyTableData != null)
            {
                request.setAttribute("dailyTableData", dailyTableData.toString());
            }
            if (weeklyTableData != null)
            {
                request.setAttribute("weeklyTableData", weeklyTableData.toString());
            }
            if (weeklyNewTableData != null)
            {
                request.setAttribute("weeklyNewTableData", weeklyNewTableData.toString());
            }
            if (monthlyTableData != null)
            {
                request.setAttribute("monthlyTableData", monthlyTableData.toString());
            }
            if (dailyTableDataToggle != null)
            {
                request.setAttribute("dailyTableDataToggle", dailyTableDataToggle.toString());
            }
            if (weeklyTableDataToggle != null)
            {
                request.setAttribute("weeklyTableDataToggle", weeklyTableDataToggle.toString());
            }
            if (monthlyTableDataToggle != null)
            {
                request.setAttribute("monthlyTableDataToggle", monthlyTableDataToggle.toString());
            }
            rd.forward(request, response);
            return;
        }
        else if ("ProgressBarChart".equals(displayChartType)){
            RequestDispatcher rd1=null;
            int actualInactiveDays = 0;
            int alertThresholdOrange = 0;
            int suspensionThresholdRed = 0;
            int weeklyActualInactiveDays = 0;
            int weeklyAlertThresholdOrange = 0;
            int weeklySuspensionThresholdRed = 0;
            int monthlyActualInactiveDays = 0;
            int monthlyAlertThresholdOrange = 0;
            int monthlySuspensionThresholdRed = 0;
            String message = "";
            String tableHeader = "";
            String tableHeaderUnit = "";
            String weeklyMessage = "";
            String monthlyMessage = "";
            if("daily".equals(dailyFrequency)){
                if ("Y".equals(isDailyExecution)){
                    if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                        message = "Total number of chargeback-[today]";
                        tableHeader = "Total chargeback and thereshold[today]";
                        tableHeaderUnit="count";
                        DateVO dateVO = dateManager.getCurrentDayDateRange();
                        List<TransactionVO> todaysCBTransactionVOsList = merchantMonitoringManager.getTransactionListByTimestamp(terminalVO, dateVO, "chargeback");
                        actualInactiveDays = todaysCBTransactionVOsList.size();
                        alertThresholdOrange = (int) monitoringParameterMappingVO.getAlertThreshold();
                        suspensionThresholdRed = (int) monitoringParameterMappingVO.getSuspensionThreshold();
                        dailyTableData = monitoringGraphsManager.prepareProgressBarTableForPartner(actualInactiveDays, alertThresholdOrange, suspensionThresholdRed, "Total chargebacks", tableHeader, tableHeaderUnit);
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.InactivityPeriod.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.ProcessingSetup.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                        message = "Current inactive period-[new merchant]";
                        tableHeader = "Inactive period and threshold";
                        tableHeaderUnit="days";
                        String lastTransactionDate = monitoringGraphsManager.getMerchantLastSubmission(terminalVO);
                        if (merchantProcessingDays < 90){
                            actualInactiveDays = (int) Functions.DATEDIFF(lastTransactionDate, todayDate);
                            alertThresholdOrange = (int) monitoringParameterMappingVO.getAlertThreshold();
                            suspensionThresholdRed = (int) monitoringParameterMappingVO.getSuspensionThreshold();
                            dailyTableData = monitoringGraphsManager.prepareProgressBarTableForPartner(actualInactiveDays, alertThresholdOrange, suspensionThresholdRed, "Actual inactive period", tableHeader, tableHeaderUnit);
                        }
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.FirstSubmission.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.ProcessingSetup.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                        message = "Days without first transaction-[new merchant]";
                        tableHeader = "Inactive period from account activation-[new merchant]";
                        tableHeaderUnit="days";
                        String firstTransactionSubmission = monitoringGraphsManager.getMerchantFirstSubmission(terminalVO);
                        if (!functions.isValueNull(firstTransactionSubmission)){
                            String terminalActivationDate = terminalVO.getActivationDate();
                            actualInactiveDays = (int) Functions.DATEDIFF(terminalActivationDate, todayDate);
                            alertThresholdOrange = (int) monitoringParameterMappingVO.getAlertThreshold();
                            suspensionThresholdRed = (int) monitoringParameterMappingVO.getSuspensionThreshold();
                            dailyTableData = monitoringGraphsManager.prepareProgressBarTableForPartner(actualInactiveDays, alertThresholdOrange, suspensionThresholdRed, "Actual inactive period", tableHeader, tableHeaderUnit);
                        }
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.InactivityPeriod.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.ProcessingSetup.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                        message = "Current inactive period-[old merchant]";
                        tableHeader = "Inactive period and threshold";
                        tableHeaderUnit="days";
                        String lastTransactionDate = monitoringGraphsManager.getMerchantLastSubmission(terminalVO);
                        if (merchantProcessingDays > 90){
                            actualInactiveDays = (int) Functions.DATEDIFF(lastTransactionDate, todayDate);
                            alertThresholdOrange = (int) monitoringParameterMappingVO.getAlertThreshold();
                            suspensionThresholdRed = (int) monitoringParameterMappingVO.getSuspensionThreshold();
                            dailyTableData = monitoringGraphsManager.prepareProgressBarTableForPartner(actualInactiveDays, alertThresholdOrange, suspensionThresholdRed, "Actual inactive period", tableHeader, tableHeaderUnit);
                        }
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.StuckTransaction.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                        Hashtable hashtable = monitoringGraphsManager.getDataForPendingTransactions(terminalVO);
                        Hashtable hashtable1 = new Hashtable();
                        if (hashtable != null){
                            Set set = hashtable.keySet();
                            Iterator iterator = set.iterator();
                            while (iterator.hasNext()){
                                String status = (String) iterator.next();
                                actualInactiveDays = Integer.parseInt((String) hashtable.get(status));
                                alertThresholdOrange = (int) monitoringParameterMappingVO.getAlertThreshold();
                                suspensionThresholdRed = (int) monitoringParameterMappingVO.getSuspensionThreshold();
                                tableHeader = "Pending transactions(" + status + ") and threshold";
                                tableHeaderUnit="count";
                                dailyTableData = monitoringGraphsManager.prepareProgressBarTableForPartner(actualInactiveDays, alertThresholdOrange, suspensionThresholdRed, "Transactions", tableHeader, tableHeaderUnit);
                                hashtable1.put(status, dailyTableData.toString());
                            }
                        }
                        request.setAttribute("hashtable", hashtable);
                        request.setAttribute("hashtable1", hashtable1);
                        request.setAttribute("displayRuleName", displayRuleName);
                        request.setAttribute("actualinactivedays", actualInactiveDays);
                        request.setAttribute("alertThresholdOrange", alertThresholdOrange);
                        request.setAttribute("suspensionThresholdRed", suspensionThresholdRed);
                        request.setAttribute("monitoringParameterVO", monitoringParameterVO);
                        if (dailyTableData != null){
                            request.setAttribute("dailyTableData", dailyTableData.toString());
                        }
                        RequestDispatcher rd2 = request.getRequestDispatcher("/partnerRiskRulePendingTransactions.jsp?ctoken=" + user.getCSRFToken());
                        rd2.forward(request, response);
                        return;
                    }
                }
            }
            if ("weekly".equals(weeklyFrequency)){
                if ("Y".equals(isWeeklyExecution)){
                    if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                        weeklyMessage = "Total number of chargeback-[in current week]";
                        tableHeader = "Total chargeback and thereshold [in current week]";
                        tableHeaderUnit="count";
                        DateVO dateVO = dateManager.getCalendarCurrentWeekDateRange();
                        List<TransactionVO> todaysCBTransactionVOsList = merchantMonitoringManager.getTransactionListByTimestamp(terminalVO, dateVO, "chargeback");
                        weeklyActualInactiveDays = todaysCBTransactionVOsList.size();
                        weeklyAlertThresholdOrange = monitoringParameterMappingVO.getWeeklyAlertThreshold().intValue();
                        weeklySuspensionThresholdRed = monitoringParameterMappingVO.getWeeklySuspensionThreshold().intValue();
                        weeklyTableData = monitoringGraphsManager.prepareProgressBarTableForPartner(weeklyActualInactiveDays, weeklyAlertThresholdOrange, weeklySuspensionThresholdRed, "Total Chargebacks", tableHeader, tableHeaderUnit);
                    }
                }
            }
            if ("monthly".equals(monthlyFrequency)){
                if ("Y".equals(isMonthlyExecution)){
                    if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                        monthlyMessage = "Total number of chargebacks [in current month]";
                        tableHeader = "Total chargeback and thereshold [in current month]";
                        tableHeaderUnit="count";
                        DateVO dateVO = dateManager.getCurrentMonthDateRange();
                        List<TransactionVO> todaysCBTransactionVOsList = merchantMonitoringManager.getTransactionListByTimestamp(terminalVO, dateVO, "chargeback");
                        monthlyActualInactiveDays = todaysCBTransactionVOsList.size();
                        monthlyAlertThresholdOrange = monitoringParameterMappingVO.getMonthlyAlertThreshold().intValue();
                        monthlySuspensionThresholdRed = monitoringParameterMappingVO.getMonthlySuspensionThreshold().intValue();
                        monthlyTableData = monitoringGraphsManager.prepareProgressBarTableForPartner(monthlyActualInactiveDays, monthlyAlertThresholdOrange, monthlySuspensionThresholdRed, "Total Chargebacks", tableHeader, tableHeaderUnit);
                    }
                }
            }
            request.setAttribute("displayRuleName", displayRuleName);
            request.setAttribute("monitoringParameterVO", monitoringParameterVO);
            request.setAttribute("actualinactivedays", actualInactiveDays);
            request.setAttribute("alertThresholdOrange", alertThresholdOrange);
            request.setAttribute("suspensionThresholdRed", suspensionThresholdRed);
            request.setAttribute("weeklyActualInactiveDays", weeklyActualInactiveDays);
            request.setAttribute("weeklyAlertThresholdOrange", weeklyAlertThresholdOrange);
            request.setAttribute("weeklySuspensionThresholdRed", weeklySuspensionThresholdRed);
            request.setAttribute("monthlyActualInactiveDays", monthlyActualInactiveDays);
            request.setAttribute("monthlyAlertThresholdOrange", monthlyAlertThresholdOrange);
            request.setAttribute("monthlySuspensionThresholdRed", monthlySuspensionThresholdRed);
            request.setAttribute("message", message);
            request.setAttribute("weeklyMessage", weeklyMessage);
            request.setAttribute("monthlyMessage", monthlyMessage);
            if (dailyTableData != null){
                request.setAttribute("dailyTableData", dailyTableData.toString());
            }
            if (weeklyTableData != null){
                request.setAttribute("weeklyTableData", weeklyTableData.toString());
            }
            if (monthlyTableData != null){
                request.setAttribute("monthlyTableData", monthlyTableData.toString());
            }
            rd1 = request.getRequestDispatcher("/partnerRiskRuleProgressBarChart.jsp?ctoken=" + user.getCSRFToken());
            rd1.forward(request, response);
            return;
        }
    }

    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MEMBERID);
        inputFieldsListOptional.add(InputFields.TERMINALID);
        inputFieldsListOptional.add(InputFields.RISKRULEID);
        /*inputFieldsListOptional.add(InputFields.FDATE);
        inputFieldsListOptional.add(InputFields.TDATE);*/
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }
}
