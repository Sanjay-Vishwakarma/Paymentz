import com.directi.pg.Admin;
import com.directi.pg.Functions;
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
 * Created by IntelliJ IDEA.
 * User: Sandip
 * Date: 1/9/16
 * Time: 4:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class MonitoringGraphController extends HttpServlet
{
    static Logger logger=new Logger(MonitoringGraphController.class.getName());
    //String colorArray[] = {"\"#3CC2C2\"", "\"#2FD69A\"", "\"#95D63F\"", "\"#EB7F57\"", "\"#F8C471\"", "\"#AF7AC5\"", "\"#D4AC0D\"", "\"#5DADE2\"", "\"#F5B7B1\""};
    String colorArray[] = {"\"#68c39f\"", "\"#edce8c\"", "\"#Abb7b7\"", "\"#4a525f\"", "\"#b4c0c0\"", "\"#e5d493\"", "\"#4a525f\"", "\"#7bC0AA\"", "\"#b4c0c0\"", "\"#e5d493\"", "\"#4a525f\"", "\"#7bC0AA\"", "\"#b4c0c0\"", "\"#e5d493\"", "\"#4a525f\"", "\"#b4c0c0\"","\"#b4c0c0\"", "\"#e5d493\"", "\"#4a525f\"", "\"#7bC0AA\"", "\"#b4c0c0\"", "\"#e5d493\"", "\"#4a525f\"", "\"#7bC0AA\"", "\"#b4c0c0\"", "\"#e5d493\"", "\"#4a525f\"", "\"#7bC0AA\"", "\"rgba(180,192,192,0.1)\"", "\"rgba(229,212,147,0.1)\"", "\"rgba(74,82,95,0.1)\"", "\"rgba(123,192,170,0.1)\"", "\"rgba(11,98,164,0.1)\"", "\"rgba(11,98,164,0.1)\"", "\"rgba(11,98,164,0.1)\"", "\"rgba(11,98,164,0.1)\"", "\"rgba(11,98,164,0.7)\"", "\"rgba(11,98,164,0.7)\"", "\"rgba(11,98,164,0.7)\"","\"rgba(11,98,164,0.7)\"", "\"rgba(11,98,164,0.7)\"", "\"rgba(11,98,164,0.7)\"", "\"rgba(11,98,164,0.7)\"", "\"rgba(11,98,164,0.7)\"", "\"rgba(11,98,164,0.7)\"", "\"rgba(11,98,164,0.7)\"", "\"rgba(11,98,164,0.7)\"", "\"rgba(11,98,164,0.7)\"", "\"rgba(11,98,164,0.7)\"", "\"rgba(11,98,164,0.7)\"", "\"rgba(11,98,164,0.7)\"", "\"rgba(11,98,164,0.7)\""};
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        try
        {
            doProcess(request, response);
        }
        catch(Exception e)
        {

        }
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException
    {
        try
        {
            doProcess(request, response);
        }
        catch(Exception e)
        {

        }
    }

    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();
        if (!Admin.isLoggedIn(session))
        {
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }

        String errormsg = "";
        errormsg = errormsg + validateParameters(request);
        if (functions.isValueNull(errormsg))
        {
            request.setAttribute("error", errormsg);
            RequestDispatcher rd = request.getRequestDispatcher("/riskRuleGraph.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }

        String memberId = request.getParameter("memberid");
        String terminalId = request.getParameter("terminalid");
        String ruleId = request.getParameter("ruleid");
        String frequency = request.getParameter("frequency");

        String dailyJsonStr = "";
        String weeklyJsonStr = "";
        String weeklyNewJsonStr = "";
        String monthlyJsonStr = "";
        String dailyJsonStr1 = "";
        String weeklyJsonStr1 = "";
        String weeklyNewJsonStr1 = "";
        String monthlyJsonStr1 = "";

        String monitoringUnit = "";

        StringBuffer dailyTableData = null;
        StringBuffer weeklyTableData = null;
        StringBuffer weeklyNewTableData = null;
        StringBuffer monthlyTableData = null;
        StringBuffer dailyTableDataToggle = null;
        StringBuffer weeklyTableDataToggle = null;
        StringBuffer monthlyTableDataToggle = null;
        StringBuffer data1 = null;
        StringBuffer data2 = null;
        StringBuffer data3 = null;
        StringBuffer data4 = null;
        long data01 = 0;
        long data02 = 0;
        long data03 = 0;
        long data04 = 0;

        DateManager dateManager = new DateManager();
        MerchantMonitoringManager merchantMonitoringManager = new MerchantMonitoringManager();
        MonitoringGraphsManager monitoringGraphsManager = new MonitoringGraphsManager();
        RatioCalculationHelper ratioCalculationHelper = new RatioCalculationHelper();
        TerminalManager terminalManager = new TerminalManager();
        MerchantDAO merchantDAO = new MerchantDAO();

        MonitoringParameterVO monitoringParameterVO = merchantMonitoringManager.getMonitoringParameterDetails(ruleId);
        MonitoringParameterMappingVO monitoringParameterMappingVO = merchantMonitoringManager.getMonitoringParameterDetailsFromMapping(ruleId, memberId, terminalId);

        if (monitoringParameterMappingVO == null){
            monitoringParameterMappingVO = merchantMonitoringManager.getMonitoringParameterFromMaster(ruleId);
        }
        if (monitoringParameterMappingVO == null){
            RequestDispatcher rd = request.getRequestDispatcher("/riskRuleGraph.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(memberId);
        String merchantCountry = merchantDetailsVO.getCountry();
        TerminalVO terminalVO = terminalManager.getActiveInActiveTerminalInfo(terminalId);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
        TerminalLimitsVO terminalLimitsVO = terminalManager.getMemberTerminalProcessingLimitVO(memberId, terminalId);
        String firstTransactionDate = monitoringGraphsManager.getMerchantFirstSubmission(terminalVO);
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

        String todayDate = targetFormat.format(new Date());
        if(functions.isValueNull(firstTransactionDate))
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
        if ("LineChart".equals(displayChartType)){
            double maxRatioCompositeHoursly = 0;
            double maxRatioCompositeDaily = 0;
            double maxRatioCompositeWeekly = 0;
            double maxRatioCompositeMonthly = 0;
            String ruleFrequency="";
            String tableHeaderFreq="";
            RequestDispatcher rd = request.getRequestDispatcher("/riskRuleGraph.jsp?ctoken=" + user.getCSRFToken());
            if ("all".equals(frequency) || "hoursly".equals(frequency)){
                boolean isValid = true;
                tableHeaderFreq="[today]";
                ruleFrequency="Daily";
                alertThreshold = monitoringParameterMappingVO.getAlertThreshold();
                suspensionThreshold = monitoringParameterMappingVO.getSuspensionThreshold();
                if ("Y".equals(isDailyExecution)){
                    data1 = new StringBuffer();
                    data2 = new StringBuffer();
                    data3 = new StringBuffer();
                    data4 = new StringBuffer();

                    List<DateVO> list = dateManager.getTodaysLineChartQuarter(5);
                    TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO = new MerchantMonitoringManager().getLastThreeMonthProcessingDetailsAsPerCalendarMonth(terminalVO);
                    if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel())&& MonitoringDeviation.Lower.toString().equals(monitoringParameterVO.getMonitoingDeviation())){
                        data1.append("100.00");
                        data2.append(alertThreshold);
                        data3.append(suspensionThreshold);
                    }
                    else if (MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword())){
                        data1.append("0");
                        data2.append(Double.valueOf(alertThreshold).intValue());
                        data3.append(Double.valueOf(suspensionThreshold).intValue());
                    }
                    else{
                        data1.append("0.0");
                        data2.append(alertThreshold);
                        data3.append(suspensionThreshold);
                    }
                    data4.append("\"00:00\"");
                    for(DateVO dateVO : list){
                        String startDate = dateVO.getStartDate();
                        String endDate = dateVO.getEndDate();
                        String labelName = dateVO.getDateLabel();
                        double plotData = 0.00;
                        TransactionSummaryVO transactionSummaryVO = monitoringGraphsManager.getDataForDataForChargebackRatio(terminalVO, startDate, endDate);
                        if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Reversed.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                            TransactionSummaryVO refundAndChargebackTransactionSummaryVO = monitoringGraphsManager.getRefundAndChargebackDataByTimeStamp(terminalVO, startDate, endDate);
                            TerminalProcessingDetailsVO priorMonthProcessingDetailsVO = new MerchantMonitoringManager().getLastMonthProcessingDetailsAsPerCalendarMonth(terminalVO);
                            plotData = ratioCalculationHelper.calculateRefundDeviation(priorMonthProcessingDetailsVO.getSalesAmount(), refundAndChargebackTransactionSummaryVO.getReversedAmount());
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel())&& MonitoringDeviation.Higher.toString().equals(monitoringParameterVO.getMonitoingDeviation())){
                            if (merchantProcessingDays < 90){
                                double dailyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount() + transactionSummaryVO.getAuthfailedAmount();
                                double dailyTargetSalesAmount = terminalLimitsVO.getDailyAmountLimit();
                                if (dailyTargetSalesAmount > 0){
                                    plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(dailyActualSalesAmount, dailyTargetSalesAmount);
                                    if(plotData<0){
                                        plotData=0.00;
                                    }
                                }
                            }
                            else{
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel())&& MonitoringDeviation.Lower.toString().equals(monitoringParameterVO.getMonitoingDeviation())){
                            if (merchantProcessingDays < 90){
                                double dailyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount() + transactionSummaryVO.getAuthfailedAmount();
                                double dailyTargetSalesAmount = terminalLimitsVO.getDailyAmountLimit();
                                if (dailyTargetSalesAmount > 0){
                                    plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(dailyActualSalesAmount, dailyTargetSalesAmount);
                                    if(plotData<0){
                                        plotData=plotData*(-1);
                                    }
                                    else{
                                        plotData=0.00;
                                    }
                                }
                            }
                            else{
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                            if (merchantProcessingDays > 90){
                                double dailyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount();
                                double dailyTargetSalesAmount = lastThreeMonthProcessingDetailsVO.getSalesAmount() / 3;
                                if (dailyTargetSalesAmount > 0){
                                    plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(dailyActualSalesAmount, dailyTargetSalesAmount);
                                    if(plotData<0){
                                        plotData=0.00;
                                    }
                                }
                            }
                            else{
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                            if (merchantProcessingDays > 90){
                                lastThreeMonthActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(lastThreeMonthProcessingDetailsVO.getSalesAmount(), lastThreeMonthProcessingDetailsVO.getSalesCount());
                                currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(transactionSummaryVO.getTotalProcessingAmount(), (int) transactionSummaryVO.getTotalProcessingCount());
                                if (lastThreeMonthActualAvgTicketAmount > 0){
                                    plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(currentDayActualAvgTicketAmount, lastThreeMonthActualAvgTicketAmount);
                                    if(plotData<0){
                                        plotData=0.00;
                                    }
                                }
                            }
                            else{
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                            if (merchantProcessingDays < 90){
                                transactionSummaryVO = monitoringGraphsManager.getDataForDataForChargebackRatio(terminalVO, startDate, endDate);
                                double contractedAvgTicketAmount = terminalLimitsVO.getDailyAvgTicketAmount();
                                double actualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(transactionSummaryVO.getTotalProcessingAmount(), (int) transactionSummaryVO.getTotalProcessingCount());
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(actualAvgTicketAmount, contractedAvgTicketAmount);
                                if(plotData<0){
                                    plotData=0.00;
                                }
                            }
                            else{
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                            plotData = monitoringGraphsManager.getMatureOperationTransactionsCount(terminalVO, dateVO, "reversed", (int) alertThreshold);
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                            plotData = monitoringGraphsManager.getMatureOperationTransactionsCount(terminalVO, dateVO, "chargeback", (int) alertThreshold);
                        }
                        else
                        {
                            plotData = monitoringGraphsManager.monitoringLineChartHelper(transactionSummaryVO, monitoringParameterVO);
                        }
                        if (plotData > maxRatioCompositeHoursly){
                            maxRatioCompositeHoursly = plotData;
                        }
                        if (alertThreshold > maxRatioCompositeHoursly){
                            maxRatioCompositeHoursly = alertThreshold;
                        }
                        if (suspensionThreshold > maxRatioCompositeHoursly){
                            maxRatioCompositeHoursly = suspensionThreshold;
                        }
                        monitoringGraphsManager.appendData(data1, data2, data3, data4);
                        if (MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword())){
                            data1.append(Double.valueOf(plotData).intValue());
                            data2.append(Double.valueOf(alertThreshold).intValue());
                            data3.append(Double.valueOf(suspensionThreshold).intValue());
                        }
                        else{
                            data1.append(plotData);
                            data2.append(alertThreshold);
                            data3.append(suspensionThreshold);
                        }
                        data4.append("\"" + labelName + "\"");
                    }
                    if (data1.length() > 0 && data2.length() > 0 && data3.length() > 0 && data4.length() > 0 && isValid){
                        dailyJsonStr = monitoringGraphsManager.prepareLineChartJSON(data1, data2, data3, data4, displayRuleName, monitoringParameterVO, alertThreshold, suspensionThreshold);
                        dailyTableData = monitoringGraphsManager.prepareLineChartTable(data1, data2, data3, data4, displayRuleName, monitoringUnit, monitoringParameterVO,ruleFrequency,tableHeaderFreq);
                    }
                }
                else{
                    logger.error("Frequency is not activated");
                }            }
            if("all".equals(frequency) || "daily".equals(frequency)){
                boolean isValid = true;
                tableHeaderFreq="[in current week]";
                ruleFrequency="Daily";
                alertThreshold = monitoringParameterMappingVO.getAlertThreshold();
                suspensionThreshold = monitoringParameterMappingVO.getSuspensionThreshold();
                if ("Y".equals(isDailyExecution)){
                    String startDate = "";
                    String endDate = "";
                    String labelName = "";

                    data1 = new StringBuffer();
                    data2 = new StringBuffer();
                    data3 = new StringBuffer();
                    data4 = new StringBuffer();

                    List<DateVO> list = dateManager.getWeeklyLineChartQuarter();
                    for (DateVO dateVO : list){
                        startDate = dateVO.getStartDate();
                        endDate = dateVO.getEndDate();
                        labelName = dateVO.getDateLabel();

                        double plotData = 0.00;
                        TransactionSummaryVO transactionSummaryVO = monitoringGraphsManager.getDataForDataForChargebackRatio(terminalVO, startDate, endDate);
                        if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Reversed.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                            TransactionSummaryVO refundAndChargebackTransactionSummaryVO = monitoringGraphsManager.getRefundAndChargebackDataByTimeStamp(terminalVO, startDate, endDate);
                            TerminalProcessingDetailsVO priorMonthProcessingDetailsVO = new MerchantMonitoringManager().getLastMonthProcessingDetailsAsPerCalendarMonth(terminalVO);
                            plotData = ratioCalculationHelper.calculateRefundDeviation(priorMonthProcessingDetailsVO.getSalesAmount(), refundAndChargebackTransactionSummaryVO.getReversedAmount());
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel())&& MonitoringDeviation.Higher.toString().equals(monitoringParameterVO.getMonitoingDeviation())){
                            if(merchantProcessingDays < 90){
                                double weeklyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount();
                                double weeklyTargetSalesAmount = terminalLimitsVO.getDailyAmountLimit();
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(weeklyActualSalesAmount, weeklyTargetSalesAmount);
                                if(plotData<0){
                                    plotData=0.00;
                                }
                            }
                            else{
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel())&& MonitoringDeviation.Lower.toString().equals(monitoringParameterVO.getMonitoingDeviation())){
                            if(merchantProcessingDays < 90){
                                double weeklyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount();
                                double weeklyTargetSalesAmount = terminalLimitsVO.getDailyAmountLimit();
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(weeklyActualSalesAmount, weeklyTargetSalesAmount);
                                if(plotData<0){
                                    plotData=plotData*(-1);
                                }
                                else{
                                    plotData=0.00;
                                }
                            }
                            else{
                                isValid = false;
                            }
                        }
                        else if(MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                            if(merchantProcessingDays > 90){
                                double weeklyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount();
                                double weeklyTargetSalesAmount = terminalLimitsVO.getDailyAmountLimit();
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(weeklyActualSalesAmount, weeklyTargetSalesAmount);
                                if(plotData<0){
                                    plotData=0.00;
                                }
                            }
                            else{
                                isValid = false;
                            }
                        }
                        else if(MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                            if(merchantProcessingDays > 90){
                                TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO = new MerchantMonitoringManager().getLastThreeMonthProcessingDetailsAsPerCalendarMonth(terminalVO);
                                lastThreeMonthActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(lastThreeMonthProcessingDetailsVO.getSalesAmount(), lastThreeMonthProcessingDetailsVO.getSalesCount());
                                currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(transactionSummaryVO.getTotalProcessingAmount(), (int) transactionSummaryVO.getTotalProcessingCount());
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(currentDayActualAvgTicketAmount, lastThreeMonthActualAvgTicketAmount);
                                if(plotData<0){
                                    plotData=0.00;
                                }
                            }
                            else{
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                            if (merchantProcessingDays < 90){
                                double contractedAvgTicketAmount = terminalLimitsVO.getDailyAvgTicketAmount();
                                currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(transactionSummaryVO.getTotalProcessingAmount(), (int) transactionSummaryVO.getTotalProcessingCount());
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(currentDayActualAvgTicketAmount, contractedAvgTicketAmount);
                                if(plotData<0){
                                    plotData=0.00;
                                }
                            }
                            else{
                                isValid = false;
                            }
                        }
                        else if(MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                            plotData = monitoringGraphsManager.getMatureOperationTransactionsCount(terminalVO, dateVO, "reversed", (int) alertThreshold);
                        }
                        else if(MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                            plotData = monitoringGraphsManager.getMatureOperationTransactionsCount(terminalVO, dateVO, "chargeback", (int) alertThreshold);
                        }
                        else{
                            plotData = monitoringGraphsManager.monitoringLineChartHelper(transactionSummaryVO, monitoringParameterVO);
                        }
                        if (plotData > maxRatioCompositeDaily){
                            maxRatioCompositeDaily = plotData;
                        }
                        if (alertThreshold > maxRatioCompositeDaily){
                            maxRatioCompositeDaily = alertThreshold;
                        }
                        if (suspensionThreshold > maxRatioCompositeDaily){
                            maxRatioCompositeDaily = suspensionThreshold;
                        }
                        monitoringGraphsManager.appendData(data1, data2, data3, data4);
                        if (MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword())){
                            data1.append(Double.valueOf(plotData).intValue());
                            data2.append(Double.valueOf(alertThreshold).intValue());
                            data3.append(Double.valueOf(suspensionThreshold).intValue());
                        }
                        else{
                            data1.append(plotData);
                            data2.append(alertThreshold);
                            data3.append(suspensionThreshold);
                        }
                        data4.append("\"" + labelName + "\"");
                    }
                    if (data1.length() > 0 && data2.length() > 0 && data3.length() > 0 && data4.length() > 0 && isValid){
                        weeklyJsonStr = monitoringGraphsManager.prepareLineChartJSON(data1, data2, data3, data4, displayRuleName, monitoringParameterVO, alertThreshold, suspensionThreshold);
                        weeklyTableData = monitoringGraphsManager.prepareLineChartTable(data1, data2, data3, data4, displayRuleName, monitoringUnit, monitoringParameterVO,ruleFrequency,tableHeaderFreq);
                    }
                    else{
                        logger.debug("Data Not Found");
                    }
                }
            }
            if ("all".equals(frequency) || "weekly".equals(frequency)){
                boolean isValid = true;
                tableHeaderFreq="[in current month]";
                ruleFrequency="Weekly";
                if ("Y".equals(isWeeklyExecution)){
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
                    for (DateVO dateVO : list){
                        startDate = dateVO.getStartDate();
                        endDate = dateVO.getEndDate();
                        labelName = dateVO.getDateLabel();
                        double plotData = 0.00;

                        TransactionSummaryVO transactionSummaryVO = monitoringGraphsManager.getDataForDataForChargebackRatio(terminalVO, startDate, endDate);
                        if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Reversed.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                            TransactionSummaryVO refundAndChargebackTransactionSummaryVO = monitoringGraphsManager.getRefundAndChargebackDataByTimeStamp(terminalVO, startDate, endDate);
                            TerminalProcessingDetailsVO priorMonthProcessingDetailsVO = new MerchantMonitoringManager().getLastMonthProcessingDetailsAsPerCalendarMonth(terminalVO, dateVO);
                            plotData = ratioCalculationHelper.calculateRefundDeviation(priorMonthProcessingDetailsVO.getSalesAmount(), refundAndChargebackTransactionSummaryVO.getReversedAmount());
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel())&& MonitoringDeviation.Higher.toString().equals(monitoringParameterVO.getMonitoingDeviation())){
                            if (merchantProcessingDays < 90){
                                double monthlyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount();
                                double monthlyTargetSalesAmount = terminalLimitsVO.getWeeklyAmountLimit();
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(monthlyActualSalesAmount, monthlyTargetSalesAmount);
                                if(plotData<0){
                                    plotData=0.00;
                                }
                            }
                            else{
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()) && MonitoringDeviation.Lower.toString().equals(monitoringParameterVO.getMonitoingDeviation())){
                            if (merchantProcessingDays < 90){
                                double monthlyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount();
                                double monthlyTargetSalesAmount = terminalLimitsVO.getWeeklyAmountLimit();
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(monthlyActualSalesAmount, monthlyTargetSalesAmount);
                                if(plotData<0){
                                    plotData=plotData*(-1);
                                }
                                else{
                                    plotData=0.00;
                                }
                            }
                            else{
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                            if (merchantProcessingDays > 90){
                                double monthlyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount();
                                double monthlyTargetSalesAmount = terminalLimitsVO.getWeeklyAmountLimit();
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(monthlyActualSalesAmount, monthlyTargetSalesAmount);
                                if(plotData<0){
                                    plotData=0.00;
                                }
                            }
                            else{
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                            if (merchantProcessingDays > 90){
                                TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO = new MerchantMonitoringManager().getLastThreeMonthProcessingDetailsAsPerCalendarMonth(terminalVO);
                                lastThreeMonthActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(lastThreeMonthProcessingDetailsVO.getSalesAmount(), lastThreeMonthProcessingDetailsVO.getSalesCount());
                                currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(transactionSummaryVO.getTotalProcessingAmount(), (int) transactionSummaryVO.getTotalProcessingCount());
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(currentDayActualAvgTicketAmount, lastThreeMonthActualAvgTicketAmount);
                                if(plotData<0){
                                    plotData=0.00;
                                }
                            }
                            else{
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                            if (merchantProcessingDays < 90){
                                double contractedAvgTicketAmount = terminalLimitsVO.getWeeklyAvgTicketAmount();
                                currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(transactionSummaryVO.getTotalProcessingAmount(), (int) transactionSummaryVO.getTotalProcessingCount());
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(currentDayActualAvgTicketAmount, contractedAvgTicketAmount);
                                if(plotData<0){
                                    plotData=0.00;
                                }
                            }
                            else{
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                            plotData = monitoringGraphsManager.getMatureOperationTransactionsCount(terminalVO, dateVO, "reversed", (int) alertThreshold);
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                            plotData = monitoringGraphsManager.getMatureOperationTransactionsCount(terminalVO, dateVO, "chargeback", (int) alertThreshold);
                        }
                        else{
                            //System.out.println("in else-----");
                            plotData = monitoringGraphsManager.monitoringLineChartHelper(transactionSummaryVO, monitoringParameterVO);
                        }
                        if (plotData > maxRatioCompositeWeekly){
                            maxRatioCompositeWeekly = plotData;
                        }
                        if (alertThreshold > maxRatioCompositeWeekly){
                            maxRatioCompositeWeekly = alertThreshold;
                        }
                        if (suspensionThreshold > maxRatioCompositeWeekly){
                            maxRatioCompositeWeekly = suspensionThreshold;
                        }
                        monitoringGraphsManager.appendData(data1, data2, data3, data4);
                        if (MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword())){
                            data1.append(Double.valueOf(plotData).intValue());
                            data2.append(Double.valueOf(alertThreshold).intValue());
                            data3.append(Double.valueOf(suspensionThreshold).intValue());
                        }
                        else{
                            data1.append(plotData);
                            data2.append(alertThreshold);
                            data3.append(suspensionThreshold);
                        }
                        data4.append("\"" + labelName + "\"");
                    }
                    if (data1.length() > 0 && data2.length() > 0 && data3.length() > 0 && data4.length() > 0 && isValid){
                        weeklyNewJsonStr = monitoringGraphsManager.prepareLineChartJSON(data1, data2, data3, data4, displayRuleName, monitoringParameterVO, alertThreshold, suspensionThreshold);
                        weeklyNewTableData = monitoringGraphsManager.prepareLineChartTable(data1, data2, data3, data4, displayRuleName, monitoringUnit, monitoringParameterVO,ruleFrequency,tableHeaderFreq);
                    }
                    else{
                        logger.debug("Data Not Found");
                    }
                }
            }
            if ("all".equals(frequency) || "monthly".equals(frequency)){
                boolean isValid = true;
                ruleFrequency="Monthly";
                tableHeaderFreq="[last six months]";
                if ("Y".equals(isMonthlyExecution)){
                    String startDate = "";
                    String endDate = "";
                    String labelName = "";

                    data1 = new StringBuffer();
                    data2 = new StringBuffer();
                    data3 = new StringBuffer();
                    data4 = new StringBuffer();

                    alertThreshold = monitoringParameterMappingVO.getMonthlyAlertThreshold();
                    suspensionThreshold = monitoringParameterMappingVO.getMonthlySuspensionThreshold();

                    List<DateVO> list = dateManager.getMonthlyLineChartQuarter();
                    for (DateVO dateVO : list){
                        startDate = dateVO.getStartDate();
                        endDate = dateVO.getEndDate();
                        labelName = dateVO.getDateLabel();
                        double plotData = 0.00;

                        TransactionSummaryVO transactionSummaryVO = monitoringGraphsManager.getDataForDataForChargebackRatio(terminalVO, startDate, endDate);
                        if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Reversed.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                            TransactionSummaryVO refundAndChargebackTransactionSummaryVO = monitoringGraphsManager.getRefundAndChargebackDataByTimeStamp(terminalVO, startDate, endDate);
                            TerminalProcessingDetailsVO priorMonthProcessingDetailsVO = new MerchantMonitoringManager().getLastMonthProcessingDetailsAsPerCalendarMonth(terminalVO, dateVO);
                            plotData = ratioCalculationHelper.calculateRefundDeviation(priorMonthProcessingDetailsVO.getSalesAmount(), refundAndChargebackTransactionSummaryVO.getReversedAmount());
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel())&& MonitoringDeviation.Higher.toString().equals(monitoringParameterVO.getMonitoingDeviation())){
                            if (merchantProcessingDays < 90){
                                double monthlyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount();
                                double monthlyTargetSalesAmount = terminalLimitsVO.getMonthlyAmountLimit();
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(monthlyActualSalesAmount, monthlyTargetSalesAmount);
                                if(plotData<0){
                                    plotData=0.00;
                                }
                            }
                            else{
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel()) && MonitoringDeviation.Lower.toString().equals(monitoringParameterVO.getMonitoingDeviation())){
                            if (merchantProcessingDays < 90){
                                double monthlyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount();
                                double monthlyTargetSalesAmount = terminalLimitsVO.getMonthlyAmountLimit();
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(monthlyActualSalesAmount, monthlyTargetSalesAmount);
                                if(plotData<0){
                                    plotData=plotData*(-1);
                                }
                                else{
                                    plotData=0.00;
                                }
                            }
                            else{
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.SalesDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                            if (merchantProcessingDays > 90){
                                double monthlyActualSalesAmount = transactionSummaryVO.getTotalProcessingAmount();
                                double monthlyTargetSalesAmount = terminalLimitsVO.getMonthlyAmountLimit();
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(monthlyActualSalesAmount, monthlyTargetSalesAmount);
                                if(plotData<0){
                                    plotData=0.00;
                                }
                            }
                            else{
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.Old.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                            if (merchantProcessingDays > 90){
                                TerminalProcessingDetailsVO lastThreeMonthProcessingDetailsVO = new MerchantMonitoringManager().getLastThreeMonthProcessingDetailsAsPerCalendarMonth(terminalVO, dateVO);
                                lastThreeMonthActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(lastThreeMonthProcessingDetailsVO.getSalesAmount(), lastThreeMonthProcessingDetailsVO.getSalesCount());
                                currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(transactionSummaryVO.getTotalProcessingAmount(), (int) transactionSummaryVO.getTotalProcessingCount());
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(currentDayActualAvgTicketAmount, lastThreeMonthActualAvgTicketAmount);
                                if(plotData<0){
                                    plotData=0.00;
                                }
                            }
                            else{
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Total.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.AvgTicketAmount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.AvgTicketDeviation.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.New.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                            if (merchantProcessingDays < 90){
                                double contractedAvgTicketAmount = terminalLimitsVO.getMonthlyAvgTicketAmount();
                                currentDayActualAvgTicketAmount = ratioCalculationHelper.calculateAvgTicketAmount(transactionSummaryVO.getTotalProcessingAmount(), (int) transactionSummaryVO.getTotalProcessingCount());
                                plotData = monitoringGraphsManager.calculateTransactionHigherLowerTicket(currentDayActualAvgTicketAmount, contractedAvgTicketAmount);
                                if(plotData<0){
                                    plotData=0.00;
                                }
                            }
                            else{
                                isValid = false;
                            }
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                            plotData = monitoringGraphsManager.getMatureOperationTransactionsCount(terminalVO, dateVO, "reversed", (int) alertThreshold);
                        }
                        else if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                            plotData = monitoringGraphsManager.getMatureOperationTransactionsCount(terminalVO, dateVO, "chargeback", (int) alertThreshold);
                        }
                        else{
                            plotData = monitoringGraphsManager.monitoringLineChartHelper(transactionSummaryVO, monitoringParameterVO);
                        }
                        if (plotData > maxRatioCompositeMonthly){
                            maxRatioCompositeMonthly = plotData;
                        }
                        if (alertThreshold > maxRatioCompositeMonthly){
                            maxRatioCompositeMonthly = alertThreshold;
                        }
                        if (suspensionThreshold > maxRatioCompositeMonthly){
                            maxRatioCompositeMonthly = suspensionThreshold;
                        }
                        monitoringGraphsManager.appendData(data1, data2, data3, data4);
                        if (MonitoringKeyword.MatureRefund.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.MatureChargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword())){
                            data1.append(Double.valueOf(plotData).intValue());
                            data2.append(Double.valueOf(alertThreshold).intValue());
                            data3.append(Double.valueOf(suspensionThreshold).intValue());
                        }
                        else{
                            data1.append(plotData);
                            data2.append(alertThreshold);
                            data3.append(suspensionThreshold);
                        }
                        data4.append("\"" + labelName + "\"");
                    }
                    if (data1.length() > 0 && data2.length() > 0 && data3.length() > 0 && data4.length() > 0 && isValid){
                        monthlyJsonStr = monitoringGraphsManager.prepareLineChartJSON(data1, data2, data3, data4, displayRuleName, monitoringParameterVO, alertThreshold, suspensionThreshold);
                        monthlyTableData = monitoringGraphsManager.prepareLineChartTable(data1, data2, data3, data4, displayRuleName, monitoringUnit, monitoringParameterVO,ruleFrequency,tableHeaderFreq);
                    }
                }
            }
            if (dailyTableData != null){
                request.setAttribute("dailyTableData", dailyTableData.toString());
            }
            if (weeklyTableData != null){
                request.setAttribute("weeklyTableData", weeklyTableData.toString());
            }
            if (weeklyNewTableData != null){
                request.setAttribute("weeklyNewTableData", weeklyNewTableData.toString());
            }
            if (monthlyTableData != null){
                request.setAttribute("monthlyTableData", monthlyTableData.toString());
            }
            request.setAttribute("displayRuleName", displayRuleName);
            request.setAttribute("dailyJsonStr", dailyJsonStr);
            request.setAttribute("weeklyJsonStr", weeklyJsonStr);
            request.setAttribute("weeklyNewJsonStr", weeklyNewJsonStr);
            request.setAttribute("monthlyJsonStr", monthlyJsonStr);
            request.setAttribute("monitoringUnit", monitoringUnit);
            request.setAttribute("maxRatioCompositeHoursly", maxRatioCompositeHoursly);
            request.setAttribute("maxRatioCompositeDaily", maxRatioCompositeDaily);
            request.setAttribute("maxRatioCompositeWeekly", maxRatioCompositeWeekly);
            request.setAttribute("maxRatioCompositeMonthly", maxRatioCompositeMonthly);
            rd.forward(request, response);
            return;
        }
        else if ("DoughnutChart".equals(displayChartType)){
            RequestDispatcher rd = request.getRequestDispatcher("/riskRuleDoughnutChart.jsp?ctoken=" + user.getCSRFToken());
            String tableHeader="";
            if ("all".equals(frequency) || "daily".equals(frequency)){
                if ("Y".equals(isDailyExecution)){
                    data1 = new StringBuffer();
                    data2 = new StringBuffer();
                    data3 = new StringBuffer();
                    DateVO dateVO = dateManager.getCurrentDayDateRange();
                    String startDate = dateVO.getStartDate();
                    String endDate = dateVO.getEndDate();
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.ForeignSales.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        HashMap<String, Double> foreignSales = monitoringGraphsManager.getDataForDataForForeignSales(terminalVO, startDate, endDate);
                        double totalProcessingAmount = foreignSales.get("totalAmount");
                        foreignSales.remove("totalAmount");
                        foreignSales.remove("totalCount");
                        tableHeader="Foreign sales [today]";
                        if (totalProcessingAmount > 0){
                            int c = 0;
                            Iterator iterator = foreignSales.keySet().iterator();
                            while (iterator.hasNext()){
                                String countryLabel = (String) iterator.next();
                                double amt = foreignSales.get(countryLabel);
                                double ratio = ((amt / totalProcessingAmount) * 100);
                                countryLabel = "\"" + countryLabel + "\"";
                                monitoringGraphsManager.appendData(data1, data2, data3);
                                data1.append(Functions.round(ratio, 2));
                                data2.append(countryLabel);
                                data3.append(colorArray[c]);
                                c = c + 1;
                            }
                        }
                    }
                    if ((data1.length() > 0) && (data2.length() > 0) && (data3.length() > 0)){
                        dailyTableData = monitoringGraphsManager.prepareDoughnutTable(data1, data2, merchantCountry, monitoringUnit,tableHeader);
                        dailyJsonStr = monitoringGraphsManager.prepareDoughnutChartJSON(data1, data2, data3);
                    }
                }
            }
            if ("all".equals(frequency) || "weekly".equals(frequency)){
                if ("Y".equals(isWeeklyExecution)){
                    data1 = new StringBuffer();
                    data2 = new StringBuffer();
                    data3 = new StringBuffer();
                    DateVO dateVO = dateManager.getCalendarCurrentWeekDateRange();
                    String startDate = dateVO.getStartDate();
                    String endDate = dateVO.getEndDate();
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.ForeignSales.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        HashMap<String, Double> foreignSales = monitoringGraphsManager.getDataForDataForForeignSales(terminalVO, startDate, endDate);
                        double totalProcessingAmount = foreignSales.get("totalAmount");
                        foreignSales.remove("totalAmount");
                        foreignSales.remove("totalCount");
                        tableHeader="Foreign sales [in current week]";
                        if (totalProcessingAmount > 0){
                            int c = 0;
                            Iterator iterator = foreignSales.keySet().iterator();
                            while (iterator.hasNext()){
                                String countryLabel = (String) iterator.next();
                                double amt = foreignSales.get(countryLabel);
                                double ratio = ((amt / totalProcessingAmount) * 100);
                                countryLabel = "\"" + countryLabel + "\"";
                                monitoringGraphsManager.appendData(data1, data2, data3);
                                data1.append(Functions.round(ratio, 2));
                                data2.append(countryLabel);
                                data3.append(colorArray[c]);
                                c = c + 1;
                            }
                        }
                    }
                    if (data1.length() > 0 && data3.length() > 0 && data2.length() > 0){
                        weeklyTableData = monitoringGraphsManager.prepareDoughnutTable(data1, data2, merchantCountry, monitoringUnit,tableHeader);
                        weeklyJsonStr = monitoringGraphsManager.prepareDoughnutChartJSON(data1, data2, data3);
                    }
                }
            }
            if ("all".equals(frequency) || "monthly".equals(frequency)){
                if ("Y".equals(isMonthlyExecution)){
                    data1 = new StringBuffer();
                    data2 = new StringBuffer();
                    data3 = new StringBuffer();
                    DateVO dateVO = dateManager.getCurrentMonthDateRange();
                    String startDate = dateVO.getStartDate();
                    String endDate = dateVO.getEndDate();
                    if(MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.ForeignSales.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Ratio.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        HashMap<String,Double> foreignSales = monitoringGraphsManager.getDataForDataForForeignSales(terminalVO,startDate,endDate);
                        double totalProcessingAmount = foreignSales.get("totalAmount");
                        foreignSales.remove("totalAmount");
                        foreignSales.remove("totalCount");
                        tableHeader="Foreign sales [in current month]";
                        if (totalProcessingAmount > 0){
                            int c = 0;
                            Iterator iterator = foreignSales.keySet().iterator();
                            while (iterator.hasNext()){
                                String countryLabel = (String) iterator.next();
                                double amt = foreignSales.get(countryLabel);
                                double ratio = ((amt / totalProcessingAmount) * 100);
                                countryLabel = "\"" + countryLabel + "\"";
                                monitoringGraphsManager.appendData(data1, data2, data3);
                                data1.append(Functions.round(ratio, 2));
                                data2.append(countryLabel);
                                data3.append(colorArray[c]);
                                c = c + 1;
                            }
                        }
                    }
                    if (data1.length() > 0 && data2.length() > 0 && data3.length() > 0){
                        monthlyTableData = monitoringGraphsManager.prepareDoughnutTable(data1, data2, merchantCountry, monitoringUnit,tableHeader);
                        monthlyJsonStr = monitoringGraphsManager.prepareDoughnutChartJSON(data1, data2, data3);
                    }
                }
            }
            request.setAttribute("displayRuleName", displayRuleName);
            request.setAttribute("dailyJsonStr", dailyJsonStr);
            request.setAttribute("weeklyJsonStr", weeklyJsonStr);
            request.setAttribute("monthlyJsonStr", monthlyJsonStr);
            if(dailyTableData != null){
                request.setAttribute("dailyTableData", dailyTableData.toString());
            }
            if(weeklyTableData != null){
                request.setAttribute("weeklyTableData", weeklyTableData.toString());
            }
            if(monthlyTableData != null){

                request.setAttribute("monthlyTableData", monthlyTableData.toString());
            }
            rd.forward(request, response);
            return;
        }
        else if("BarChart".equals(displayChartType)){
            RequestDispatcher rd=null;
            long maxRatioCompositeHoursly = 0;
            long maxRatioDetailHoursly = 0;
            long maxRatioCompositeDaily = 0;
            long maxRatioDetailDaily = 0;
            long maxRatioCompositeWeekly = 0;
            long maxRatioDetailWeekly = 0;
            long maxRatioCompositeMonthly = 0;
            long maxRatioDetailMonthly = 0;
            long totalData = 0;
            if("all".equals(frequency) || "hoursly".equals(frequency)){
                String tableHeaderFreq="today";
                if ("Y".equals(isDailyExecution)){
                    if(MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
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
                        for (DateVO dateVO : list){
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailHoursly){
                                maxRatioDetailHoursly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(categoryData1, categoryData2, categoryData3, categoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            categoryData1.append(data01);
                            categoryData2.append(data02);
                            categoryData3.append(data03);
                            categoryData4.append(data04);
                        }
                        dailyJsonStr = "{\"labels\":[" + label + "],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + categoryData1 + "]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[" + categoryData2 + "]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[" + categoryData3 + "]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[" + categoryData4 + "]}]}";
                        DateVO dateVO = dateManager.getCurrentDayDateRange();
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
                            dailyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"##Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }
                        dailyTableData = monitoringGraphsManager.prepareStackBarChartTable(categoryData1, categoryData2, categoryData3, categoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName,tableHeaderFreq, monitoringParameterVO);
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardSameAmountConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){                 alertThreshold = monitoringParameterMappingVO.getAlertThreshold();
                        alertThreshold=monitoringParameterMappingVO.getAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getSuspensionThreshold();
                        DateVO compositDateVO = dateManager.getCurrentDayDateRange();
                        double[] doubles=monitoringGraphsManager.getMinMaxTransactionAmountByDate(terminalVO,compositDateVO);
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
                        for (DateVO dateVO : list){
                            HashMap hashMap = monitoringGraphsManager.getCardCountBasedOnBinAndAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");

                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailHoursly){
                                maxRatioDetailHoursly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(categoryData1, categoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            categoryData1.append(data01);
                            categoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);
                        }
                        dailyJsonStr = "{\"labels\":[" + label + "],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + categoryData1 + "]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[" + categoryData2 + "]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[" + catagoryData3 + "]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[" + catagoryData4 + "]}]}";
                        HashMap hashMap = monitoringGraphsManager.getCardCountBasedOnBinAndAmountCategory(terminalVO, compositDateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
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
                            dailyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }
                        dailyTableData = monitoringGraphsManager.prepareStackBarChartTable(categoryData1, categoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName,tableHeaderFreq, monitoringParameterVO);
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
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
                        for (DateVO dateVO : list){
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");

                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailHoursly){
                                maxRatioDetailHoursly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);
                        }
                        dailyJsonStr = "{\"labels\":[" + label + "],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + catagoryData1 + "]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[" + catagoryData2 + "]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[" + catagoryData3 + "]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[" + catagoryData4 + "]}]}";
                        DateVO dateVO = dateManager.getCurrentDayDateRange();
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
                            dailyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }
                        dailyTableData = monitoringGraphsManager.prepareStackBarChartTable(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName,tableHeaderFreq, monitoringParameterVO);
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
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
                        String category4 = categoryArray4[0] + " and above";

                        StringBuffer label = new StringBuffer();
                        StringBuffer catagoryData1 = new StringBuffer();
                        StringBuffer catagoryData2 = new StringBuffer();
                        StringBuffer catagoryData3 = new StringBuffer();
                        StringBuffer catagoryData4 = new StringBuffer();

                        List<DateVO> list = dateManager.getTodaysTimeForStackedBarChart(5);
                        for (DateVO dateVO : list){
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinUsedDays(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");

                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailHoursly){
                                maxRatioDetailHoursly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);
                        }
                        dailyJsonStr = "{\"labels\":[" + label + "],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + catagoryData1 + "]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[" + catagoryData2 + "]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[" + catagoryData3 + "]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[" + catagoryData4 + "]}]}";
                        DateVO dateVO = dateManager.getCurrentDayDateRange();
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
                            dailyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }
                        dailyTableData = monitoringGraphsManager.prepareStackBarChartTable(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName,tableHeaderFreq, monitoringParameterVO);
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedCountry.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        data1 = new StringBuffer();
                        boolean isData = true;
                        HashMap hashMap = new HashMap();
                        List<String> countryList = monitoringGraphsManager.getListOfBlacklistedCountry();
                        Iterator iterator = countryList.iterator();
                        while (iterator.hasNext()){
                            StringBuffer data = new StringBuffer();
                            String country = (String) iterator.next();
                            List<DateVO> list = dateManager.getTodaysTimeForStackedBarChart(5);
                            for (DateVO dateVO : list){
                                long countryCount = monitoringGraphsManager.getTransDetailsFromBlackListedCountry(terminalVO, dateVO, country);
                                if (isData)
                                {
                                    if (data1.length() > 0){
                                        data1.append(",");
                                    }
                                    data1.append("\"" + dateVO.getDateLabel() + "\"");
                                }
                                if (data.length() > 0){
                                    data.append(",");
                                }
                                data.append(Double.valueOf(countryCount).intValue());
                            }
                            hashMap.put(country, data.toString());
                            isData = false;
                        }
                        StringBuffer stringBuffer = new StringBuffer();
                        Iterator iterator1 = hashMap.keySet().iterator();
                        int colorId = 0;
                        while (iterator1.hasNext()){
                            String label = (String) iterator1.next();
                            String labelValue = (String) hashMap.get(label);

                            String labelStr = "\"label\":\"" + label + "\"";
                            String labelStr1 = "\"backgroundColor\":" + colorArray[colorId];
                            String data = "\"data\":[" + labelValue + "]";
                            if (stringBuffer.length() > 0){
                                stringBuffer.append(",");
                            }
                            stringBuffer.append("{" + labelStr + "," + labelStr1 + "," + data + "}");
                            colorId = colorId + 1;
                        }
                        if (data1.length() > 0 && hashMap.size() > 0){
                            dailyJsonStr = "{\"labels\":[" + data1.toString() + "],\"datasets\":[" + stringBuffer.toString() + "]}";
                            String dailyHrs[] = data1.toString().split(",");
                            dailyTableData = new StringBuffer();
                            dailyTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            dailyTableData.append("<tr>");
                            dailyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + dailyHrs.length + 1 + "\">" + displayRuleName + " [today]" + "</td>");
                            dailyTableData.append("</tr>");
                            dailyTableData.append("<tr>");
                            dailyTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            for (int i = 0; i < dailyHrs.length; i++){
                                dailyTableData.append("<td align=\"center\" class=\"th0\">" + dailyHrs[i].replace("\"", "") + "</td>");
                            }
                            dailyTableData.append("</tr>");
                            Iterator iterator2 = hashMap.keySet().iterator();
                            while (iterator2.hasNext()){
                                String label = (String) iterator2.next();
                                String labelValue = (String) hashMap.get(label);
                                String dataArr[] = labelValue.split(",");
                                dailyTableData.append("<tr>");
                                dailyTableData.append("<td align=\"center\" class=\"th0\">" + label + "</td>");
                                for (String data : dataArr){
                                    dailyTableData.append("<td align=\"center\">" + data + "</td>");
                                }
                                dailyTableData.append("</tr>");
                            }
                            dailyTableData.append("</table>");
                        }
                    }
                }
            }
            if ("all".equals(frequency) || "daily".equals(frequency)){
                String tableHeaderFreq="in current week";
                if ("Y".equals(isDailyExecution)){
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
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
                        for (DateVO dateVO : list){
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailDaily){
                                maxRatioDetailDaily = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);
                        }
                        weeklyJsonStr = "{\"labels\":[" + label + "],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + catagoryData1 + "]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[" + catagoryData2 + "]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[" + catagoryData3 + "]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[" + catagoryData4 + "]}]}";
                        DateVO dateVO = dateManager.getCalendarCurrentWeekDateRange();
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
                            weeklyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }
                        weeklyTableData = monitoringGraphsManager.prepareStackBarChartTable(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName,tableHeaderFreq, monitoringParameterVO);
                    }
                    if(MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardSameAmountConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        alertThreshold = monitoringParameterMappingVO.getAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getSuspensionThreshold();
                        DateVO compositDateVO = dateManager.getCalendarCurrentWeekDateRange();
                        double[] doubles=monitoringGraphsManager.getMinMaxTransactionAmountByDate(terminalVO,compositDateVO);
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
                        for (DateVO dateVO : list){
                            HashMap hashMap = monitoringGraphsManager.getCardCountBasedOnBinAndAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");

                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailDaily){
                                maxRatioDetailDaily = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);
                        }
                        weeklyJsonStr = "{\"labels\":[" + label + "],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + catagoryData1 + "]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[" + catagoryData2 + "]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[" + catagoryData3 + "]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[" + catagoryData4 + "]}]}";
                        HashMap hashMap = monitoringGraphsManager.getCardCountBasedOnBinAndAmountCategory(terminalVO, compositDateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
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
                            weeklyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }
                        weeklyTableData = monitoringGraphsManager.prepareStackBarChartTable(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName,tableHeaderFreq, monitoringParameterVO);
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
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
                        for (DateVO dateVO : list){
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");

                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailDaily){
                                maxRatioDetailDaily = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);
                        }
                        weeklyJsonStr = "{\"labels\":[" + label + "],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + catagoryData1 + "]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[" + catagoryData2 + "]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[" + catagoryData3 + "]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[" + catagoryData4 + "]}]}";
                        DateVO dateVO = dateManager.getCalendarCurrentWeekDateRange();
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
                            weeklyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }
                        weeklyTableData = monitoringGraphsManager.prepareStackBarChartTable(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName,tableHeaderFreq, monitoringParameterVO);
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
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
                        String category4 = categoryArray4[0] + " and above";

                        StringBuffer label = new StringBuffer();
                        StringBuffer catagoryData1 = new StringBuffer();
                        StringBuffer catagoryData2 = new StringBuffer();
                        StringBuffer catagoryData3 = new StringBuffer();
                        StringBuffer catagoryData4 = new StringBuffer();

                        List<DateVO> list = dateManager.getWeeklyLineChartQuarter();
                        for (DateVO dateVO : list){
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinUsedDays(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailDaily){
                                maxRatioDetailDaily = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);
                        }
                        weeklyJsonStr = "{\"labels\":[" + label + "],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + catagoryData1 + "]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[" + catagoryData2 + "]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[" + catagoryData3 + "]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[" + catagoryData4 + "]}]}";
                        DateVO dateVO = dateManager.getCalendarCurrentWeekDateRange();
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
                            weeklyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }
                        weeklyTableData = monitoringGraphsManager.prepareStackBarChartTable(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName,tableHeaderFreq, monitoringParameterVO);
                    }
                    data1 = new StringBuffer();
                    boolean isData = true;
                    HashMap hashMap = new HashMap();
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedCountry.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        List<String> countryList = monitoringGraphsManager.getListOfBlacklistedCountry();
                        Iterator iterator = countryList.iterator();
                        while (iterator.hasNext()){
                            StringBuffer data = new StringBuffer();
                            String country = (String) iterator.next();
                            List<DateVO> list = dateManager.getWeeklyLineChartQuarter();
                            for (DateVO dateVO : list){
                                long countryCount = monitoringGraphsManager.getTransDetailsFromBlackListedCountry(terminalVO, dateVO, country);
                                if (countryCount > maxRatioCompositeDaily){
                                    maxRatioCompositeDaily = countryCount;
                                }
                                if (isData)
                                {
                                    if (data1.length() > 0){
                                        data1.append(",");
                                    }
                                    data1.append("\"" + dateVO.getDateLabel() + "\"");
                                }
                                if (data.length() > 0){
                                    data.append(",");
                                }
                                data.append(Double.valueOf(countryCount).intValue());
                            }
                            hashMap.put(country, data.toString());
                            isData = false;
                        }
                        StringBuffer stringBuffer = new StringBuffer();
                        Iterator iterator1 = hashMap.keySet().iterator();
                        int colorId = 0;
                        while (iterator1.hasNext()){
                            String label = (String) iterator1.next();
                            String labelValue = (String) hashMap.get(label);

                            String labelStr = "\"label\":\"" + label + "\"";
                            String labelStr1 = "\"backgroundColor\":" + colorArray[colorId];
                            String data = "\"data\":[" + labelValue + "]";
                            if (stringBuffer.length() > 0){
                                stringBuffer.append(",");
                            }
                            stringBuffer.append("{" + labelStr + "," + labelStr1 + "," + data + "}");
                            colorId = colorId + 1;
                        }
                        if (data1.length() > 0 && hashMap.size() > 0){
                            weeklyJsonStr = "{\"labels\":[" + data1.toString() + "],\"datasets\":[" + stringBuffer.toString() + "]}";
                            String weekDays[] = data1.toString().split(",");
                            weeklyTableData = new StringBuffer();
                            weeklyTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            weeklyTableData.append("<tr>");
                            weeklyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + weekDays.length + 1 + "\">" + displayRuleName + " [In Current Week]" + "</td>");
                            weeklyTableData.append("</tr>");
                            weeklyTableData.append("<tr>");
                            weeklyTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            for (int i = 0; i < weekDays.length; i++){
                                weeklyTableData.append("<td align=\"center\" class=\"th0\">" + weekDays[i].replace("\"", "") + "</td>");
                            }
                            weeklyTableData.append("</tr>");
                            Iterator iterator2 = hashMap.keySet().iterator();
                            while (iterator2.hasNext()){
                                String label = (String) iterator2.next();
                                String labelValue = (String) hashMap.get(label);
                                String dataArr[] = labelValue.split(",");
                                weeklyTableData.append("<tr>");
                                weeklyTableData.append("<td align=\"center\" class=\"th0\">" + label + "</td>");
                                for (String data : dataArr){
                                    weeklyTableData.append("<td align=\"center\">" + data + "</td>");
                                }
                                weeklyTableData.append("</tr>");
                            }
                            weeklyTableData.append("</table>");
                        }
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedIP.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        List<String> ipList = monitoringGraphsManager.getListOfBlacklistedIP();
                        Iterator iterator = ipList.iterator();
                        while (iterator.hasNext()){
                            StringBuffer data = new StringBuffer();
                            String ip = (String) iterator.next();
                            List<DateVO> list = dateManager.getWeeklyLineChartQuarter();
                            for (DateVO dateVO : list){
                                long ipCount = monitoringGraphsManager.getTransDetailsFromBlackListedIP(terminalVO, dateVO, ip);
                                if (ipCount > maxRatioCompositeDaily){
                                    maxRatioCompositeDaily = ipCount;
                                }
                                if (isData){
                                    if (data1.length() > 0){
                                        data1.append(",");
                                    }
                                    data1.append("\"" + dateVO.getDateLabel() + "\"");
                                }
                                if (data.length() > 0){
                                    data.append(",");
                                }
                                data.append(Double.valueOf(ipCount).intValue());
                            }
                            hashMap.put(ip, data.toString());
                            isData = false;
                        }
                        StringBuffer stringBuffer = new StringBuffer();
                        Iterator iterator1 = hashMap.keySet().iterator();
                        int colorId = 0;
                        while (iterator1.hasNext()){
                            String label = (String) iterator1.next();
                            String labelValue = (String) hashMap.get(label);
                            String labelStr = "\"label\":\"" + label + "\"";
                            String labelStr1 = "\"backgroundColor\":" + colorArray[colorId];
                            String data = "\"data\":[" + labelValue + "]";
                            if (stringBuffer.length() > 0){
                                stringBuffer.append(",");
                            }
                            stringBuffer.append("{" + labelStr + "," + labelStr1 + "," + data + "}");
                            colorId = colorId + 1;
                        }
                        if (data1.length() > 0 && hashMap.size() > 0){
                            weeklyJsonStr = "{\"labels\":[" + data1.toString() + "],\"datasets\":[" + stringBuffer.toString() + "]}";
                            String weekDays[] = data1.toString().split(",");
                            weeklyTableData = new StringBuffer();
                            weeklyTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            weeklyTableData.append("<tr>");
                            weeklyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + weekDays.length + 1 + "\">" + displayRuleName + " [In Current Week]" + "</td>");
                            weeklyTableData.append("</tr>");
                            weeklyTableData.append("<tr>");
                            weeklyTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            for (int i = 0; i < weekDays.length; i++){
                                weeklyTableData.append("<td align=\"center\" class=\"th0\">" + weekDays[i].replace("\"", "") + "</td>");
                            }
                            weeklyTableData.append("</tr>");
                            Iterator iterator2 = hashMap.keySet().iterator();
                            while (iterator2.hasNext()){
                                String label = (String) iterator2.next();
                                String labelValue = (String) hashMap.get(label);
                                String dataArr[] = labelValue.split(",");
                                weeklyTableData.append("<tr>");
                                weeklyTableData.append("<td align=\"center\" class=\"th0\">" + label + "</td>");
                                for (String data : dataArr)               {
                                    weeklyTableData.append("<td align=\"center\">" + data + "</td>");
                                }
                                weeklyTableData.append("</tr>");
                            }
                            weeklyTableData.append("</table>");
                        }
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedCard.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        List<String> cardList = monitoringGraphsManager.getListOfBlacklistedCard();
                        Iterator iterator = cardList.iterator();
                        while (iterator.hasNext()){
                            StringBuffer data = new StringBuffer();
                            String cardnumber = (String) iterator.next();
                            String cardArr[] = cardnumber.split(":");
                            List<DateVO> list = dateManager.getWeeklyLineChartQuarter();
                            for (DateVO dateVO : list){
                                long cardCount = monitoringGraphsManager.getTransDetailsFromBlackListedCard(terminalVO, dateVO, cardArr[0], cardArr[1]);
                                if (cardCount > maxRatioCompositeDaily){
                                    maxRatioCompositeDaily = cardCount;
                                }
                                if (isData){
                                    if (data1.length() > 0){
                                        data1.append(",");
                                    }
                                    data1.append("\"" + dateVO.getDateLabel() + "\"");
                                }
                                if (data.length() > 0){
                                    data.append(",");
                                }
                                data.append(Double.valueOf(cardCount).intValue());
                            }
                            hashMap.put(cardnumber, data.toString());
                            isData = false;
                        }
                        StringBuffer stringBuffer = new StringBuffer();
                        Iterator iterator1 = hashMap.keySet().iterator();
                        int colorId = 0;
                        while (iterator1.hasNext()){
                            String label = (String) iterator1.next();
                            String labelValue = (String) hashMap.get(label);
                            String labelStr = "\"label\":\"" + label + "\"";
                            String labelStr1 = "\"backgroundColor\":" + colorArray[colorId];
                            String data = "\"data\":[" + labelValue + "]";
                            if (stringBuffer.length() > 0){
                                stringBuffer.append(",");
                            }
                            stringBuffer.append("{" + labelStr + "," + labelStr1 + "," + data + "}");
                            colorId = colorId + 1;
                        }
                        if (data1.length() > 0 && hashMap.size() > 0){
                            weeklyJsonStr = "{\"labels\":[" + data1.toString() + "],\"datasets\":[" + stringBuffer.toString() + "]}";
                            String weekDays[] = data1.toString().split(",");
                            weeklyTableData = new StringBuffer();
                            weeklyTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            weeklyTableData.append("<tr>");
                            weeklyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + weekDays.length + 1 + "\">" + displayRuleName + " [In Current Week]" + "</td>");
                            weeklyTableData.append("</tr>");
                            weeklyTableData.append("<tr>");
                            weeklyTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            for (int i = 0; i < weekDays.length; i++){
                                weeklyTableData.append("<td align=\"center\" class=\"th0\">" + weekDays[i].replace("\"", "") + "</td>");
                            }
                            weeklyTableData.append("</tr>");
                            Iterator iterator2 = hashMap.keySet().iterator();
                            while (iterator2.hasNext()){
                                String label = (String) iterator2.next();
                                String labelValue = (String) hashMap.get(label);
                                String dataArr[] = labelValue.split(",");
                                weeklyTableData.append("<tr>");
                                weeklyTableData.append("<td align=\"center\" class=\"th0\">" + label + "</td>");
                                for (String data : dataArr){
                                    weeklyTableData.append("<td align=\"center\">" + data + "</td>");
                                }
                                weeklyTableData.append("</tr>");
                            }
                            weeklyTableData.append("</table>");
                        }
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedEmail.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        List<String> emailList = monitoringGraphsManager.getListOfBlacklistedEmails();
                        Iterator iterator = emailList.iterator();
                        while (iterator.hasNext()){
                            StringBuffer data = new StringBuffer();
                            String emailAddress = (String) iterator.next();
                            List<DateVO> list = dateManager.getWeeklyLineChartQuarter();
                            for (DateVO dateVO : list){
                                long emailCount = monitoringGraphsManager.getTransDetailsFromBlackListedEmails(terminalVO, dateVO, emailAddress);
                                if (emailCount > maxRatioCompositeDaily){
                                    maxRatioCompositeDaily = emailCount;
                                }
                                if (isData){
                                    if (data1.length() > 0){
                                        data1.append(",");
                                    }
                                    data1.append("\"" + dateVO.getDateLabel() + "\"");
                                }
                                if (data.length() > 0){
                                    data.append(",");
                                }
                                data.append(Double.valueOf(emailCount).intValue());
                            }
                            hashMap.put(emailAddress, data.toString());
                            isData = false;
                        }
                        StringBuffer stringBuffer = new StringBuffer();
                        Iterator iterator1 = hashMap.keySet().iterator();
                        int colorId = 0;
                        while (iterator1.hasNext()){
                            String label = (String) iterator1.next();
                            String labelValue = (String) hashMap.get(label);
                            String labelStr = "\"label\":\"" + label + "\"";
                            String labelStr1 = "\"backgroundColor\":" + colorArray[colorId];
                            String data = "\"data\":[" + labelValue + "]";
                            if (stringBuffer.length() > 0){
                                stringBuffer.append(",");
                            }
                            stringBuffer.append("{" + labelStr + "," + labelStr1 + "," + data + "}");
                            colorId = colorId + 1;
                        }
                        if (data1.length() > 0 && hashMap.size() > 0){
                            weeklyJsonStr = "{\"labels\":[" + data1.toString() + "],\"datasets\":[" + stringBuffer.toString() + "]}";
                            String weekDays[] = data1.toString().split(",");
                            weeklyTableData = new StringBuffer();
                            weeklyTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            weeklyTableData.append("<tr>");
                            weeklyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + weekDays.length + 1 + "\">" + displayRuleName + " [In Current Week]" + "</td>");
                            weeklyTableData.append("</tr>");
                            weeklyTableData.append("<tr>");
                            weeklyTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            for (int i = 0; i < weekDays.length; i++){
                                weeklyTableData.append("<td align=\"center\" class=\"th0\">" + weekDays[i].replace("\"", "") + "</td>");
                            }
                            weeklyTableData.append("</tr>");
                            Iterator iterator2 = hashMap.keySet().iterator();
                            while (iterator2.hasNext()){
                                String label = (String) iterator2.next();
                                String labelValue = (String) hashMap.get(label);
                                String dataArr[] = labelValue.split(",");
                                weeklyTableData.append("<tr>");
                                weeklyTableData.append("<td align=\"center\" class=\"th0\">" + label + "</td>");
                                for (String data : dataArr){
                                    weeklyTableData.append("<td align=\"center\">" + data + "</td>");
                                }
                                weeklyTableData.append("</tr>");
                            }
                            weeklyTableData.append("</table>");
                        }
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedName.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        List<String> nameList = monitoringGraphsManager.getListOfBlacklistedNames();
                        Iterator iterator = nameList.iterator();
                        while (iterator.hasNext()){
                            StringBuffer data = new StringBuffer();
                            String names = (String) iterator.next();
                            String nameArr[] = names.split(" ");
                            List<DateVO> list = dateManager.getWeeklyLineChartQuarter();
                            for (DateVO dateVO : list){
                                long nameCount = monitoringGraphsManager.getTransDetailsFromBlackListedNames(terminalVO, dateVO, nameArr[0], nameArr[1]);
                                if (nameCount > maxRatioCompositeDaily){
                                    maxRatioCompositeDaily = nameCount;
                                }
                                if (isData){
                                    if (data1.length() > 0){
                                        data1.append(",");
                                    }
                                    data1.append("\"" + dateVO.getDateLabel() + "\"");
                                }
                                if (data.length() > 0){
                                    data.append(",");
                                }
                                data.append(Double.valueOf(nameCount).intValue());
                            }
                            hashMap.put(names, data.toString());
                            isData = false;
                        }
                        StringBuffer stringBuffer = new StringBuffer();
                        Iterator iterator1 = hashMap.keySet().iterator();
                        int colorId = 0;
                        while (iterator1.hasNext()){
                            String label = (String) iterator1.next();
                            String labelValue = (String) hashMap.get(label);
                            String labelStr = "\"label\":\"" + label + "\"";
                            String labelStr1 = "\"backgroundColor\":" + colorArray[colorId];
                            String data = "\"data\":[" + labelValue + "]";
                            if (stringBuffer.length() > 0){
                                stringBuffer.append(",");
                            }
                            stringBuffer.append("{" + labelStr + "," + labelStr1 + "," + data + "}");
                            colorId = colorId + 1;
                        }
                        if (data1.length() > 0 && hashMap.size() > 0){
                            weeklyJsonStr = "{\"labels\":[" + data1.toString() + "],\"datasets\":[" + stringBuffer.toString() + "]}";
                            String weekDays[] = data1.toString().split(",");
                            weeklyTableData = new StringBuffer();
                            weeklyTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            weeklyTableData.append("<tr>");
                            weeklyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + weekDays.length + 1 + "\">" + displayRuleName + " [In Current Week]" + "</td>");
                            weeklyTableData.append("</tr>");
                            weeklyTableData.append("<tr>");
                            weeklyTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            for (int i = 0; i < weekDays.length; i++){
                                weeklyTableData.append("<td align=\"center\" class=\"th0\">" + weekDays[i].replace("\"", "") + "</td>");
                            }
                            weeklyTableData.append("</tr>");
                            Iterator iterator2 = hashMap.keySet().iterator();
                            while (iterator2.hasNext()){
                                String label = (String) iterator2.next();
                                String labelValue = (String) hashMap.get(label);
                                String dataArr[] = labelValue.split(",");
                                weeklyTableData.append("<tr>");
                                weeklyTableData.append("<td align=\"center\" class=\"th0\">" + label + "</td>");
                                for (String data : dataArr){
                                    weeklyTableData.append("<td align=\"center\">" + data + "</td>");
                                }
                                weeklyTableData.append("</tr>");
                            }
                            weeklyTableData.append("</table>");
                        }
                    }
                }
            }
            if ("all".equals(frequency) || "weekly".equals(frequency)){
                String tableHeaderFreq="in current month";
                if ("Y".equals(isWeeklyExecution)){
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
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
                        for (DateVO dateVO : list){
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");

                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailWeekly){
                                maxRatioDetailWeekly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);
                        }
                        weeklyNewJsonStr = "{\"labels\":[" + label + "],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + catagoryData1 + "]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[" + catagoryData2 + "]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[" + catagoryData3 + "]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[" + catagoryData4 + "]}]}";
                        DateVO dateVO = dateManager.getCurrentMonthDateRange();
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
                            weeklyNewJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }
                        weeklyNewTableData = monitoringGraphsManager.prepareStackBarChartTable(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName,tableHeaderFreq, monitoringParameterVO);
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardSameAmountConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        alertThreshold = monitoringParameterMappingVO.getWeeklyAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getWeeklySuspensionThreshold();
                        DateVO compositDateVO=dateManager.getCurrentMonthDateRange();
                        double[] doubles=monitoringGraphsManager.getMinMaxTransactionAmountByDate(terminalVO,compositDateVO);
                        List<int[]> ints = monitoringGraphsManager.getCategoryByThreshold(doubles[0],doubles[1]);

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
                        for (DateVO dateVO : list){
                            HashMap hashMap = monitoringGraphsManager.getCardCountBasedOnBinAndAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailWeekly){
                                maxRatioDetailWeekly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);
                        }
                        weeklyNewJsonStr = "{\"labels\":[" + label + "],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + catagoryData1 + "]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[" + catagoryData2 + "]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[" + catagoryData3 + "]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[" + catagoryData4 + "]}]}";
                        HashMap hashMap = monitoringGraphsManager.getCardCountBasedOnBinAndAmountCategory(terminalVO, compositDateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
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
                            weeklyNewJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }
                        weeklyNewTableData = monitoringGraphsManager.prepareStackBarChartTable(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName,tableHeaderFreq, monitoringParameterVO);
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
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
                        for (DateVO dateVO : list){
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailWeekly){
                                maxRatioDetailWeekly = totalData;
                            }

                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);
                        }
                        weeklyNewJsonStr = "{\"labels\":[" + label + "],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + catagoryData1 + "]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[" + catagoryData2 + "]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[" + catagoryData3 + "]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[" + catagoryData4 + "]}]}";
                        DateVO dateVO = dateManager.getCurrentMonthDateRange();
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
                            weeklyNewJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }
                        weeklyNewTableData = monitoringGraphsManager.prepareStackBarChartTable(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName,tableHeaderFreq, monitoringParameterVO);
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
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
                        String category4 = categoryArray4[0] + " and above";
                        StringBuffer label = new StringBuffer();
                        StringBuffer catagoryData1 = new StringBuffer();
                        StringBuffer catagoryData2 = new StringBuffer();
                        StringBuffer catagoryData3 = new StringBuffer();
                        StringBuffer catagoryData4 = new StringBuffer();
                        List<DateVO> list = dateManager.getWeeksOfMonthNew();
                        for (DateVO dateVO : list){
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinUsedDays(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailWeekly){
                                maxRatioDetailWeekly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);
                        }
                        weeklyNewJsonStr = "{\"labels\":[" + label + "],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + catagoryData1 + "]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[" + catagoryData2 + "]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[" + catagoryData3 + "]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[" + catagoryData4 + "]}]}";
                        DateVO dateVO = dateManager.getCurrentMonthDateRange();
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
                            weeklyNewJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }
                        weeklyNewTableData = monitoringGraphsManager.prepareStackBarChartTable(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName,tableHeaderFreq, monitoringParameterVO);
                    }
                    data1 = new StringBuffer();
                    boolean isData = true;
                    HashMap hashMap = new HashMap();
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedCountry.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        List<String> countryList = monitoringGraphsManager.getListOfBlacklistedCountry();
                        Iterator iterator = countryList.iterator();
                        while (iterator.hasNext()){
                            StringBuffer data = new StringBuffer();
                            String country = (String) iterator.next();
                            List<DateVO> list = dateManager.getWeeksOfMonthNew();
                            for (DateVO dateVO : list){
                                long countryCount = monitoringGraphsManager.getTransDetailsFromBlackListedCountry(terminalVO, dateVO, country);
                                if (countryCount > maxRatioCompositeWeekly){
                                    maxRatioCompositeWeekly = countryCount;
                                }

                                if (isData){
                                    if (data1.length() > 0){
                                        data1.append(",");
                                    }
                                    data1.append("\"" + dateVO.getDateLabel() + "\"");
                                }
                                if (data.length() > 0){
                                    data.append(",");
                                }
                                data.append(Double.valueOf(countryCount).intValue());
                            }
                            hashMap.put(country, data.toString());
                            isData = false;
                        }
                        StringBuffer stringBuffer1 = new StringBuffer();
                        Iterator iterator1 = hashMap.keySet().iterator();
                        int colorId = 0;
                        while (iterator1.hasNext()){
                            String label = (String) iterator1.next();
                            String labelValue = (String) hashMap.get(label);
                            String labelStr = "\"label\":\"" + label + "\"";
                            String labelStr1 = "\"backgroundColor\":" + colorArray[colorId];
                            String data = "\"data\":[" + labelValue + "]";
                            if (stringBuffer1.length() > 0){
                                stringBuffer1.append(",");
                            }
                            stringBuffer1.append("{" + labelStr + "," + labelStr1 + "," + data + "}");
                            colorId = colorId + 1;
                        }
                        if (data1.length() > 0 && hashMap.size() > 0){
                            weeklyNewJsonStr = "{\"labels\":[" + data1.toString() + "],\"datasets\":[" + stringBuffer1.toString() + "]}";
                            weeklyNewTableData = new StringBuffer();
                            String month[] = data1.toString().split(",");
                            weeklyNewTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            weeklyNewTableData.append("<tr>");
                            weeklyNewTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + month.length + 1 + "\">" + displayRuleName + " [In Current Month]" + "</td>");
                            weeklyNewTableData.append("</tr>");
                            weeklyNewTableData.append("<tr>");
                            weeklyNewTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            for (int i = 0; i < month.length; i++){
                                weeklyNewTableData.append("<td align=\"center\" class=\"th0\">" + month[i].replace("\"", "") + "</td>");
                            }
                            weeklyNewTableData.append("</tr>");
                            Iterator iterator2 = hashMap.keySet().iterator();
                            while (iterator2.hasNext()){
                                String label = (String) iterator2.next();
                                String labelValue = (String) hashMap.get(label);
                                String dataArr[] = labelValue.split(",");
                                weeklyNewTableData.append("<tr>");
                                weeklyNewTableData.append("<td align=\"center\" class=\"th0\">" + label + "</td>");
                                for (String data : dataArr){
                                    weeklyNewTableData.append("<td align=\"center\">" + Double.valueOf(data).intValue() + "</td>");
                                }
                                weeklyNewTableData.append("</tr>");
                            }
                            weeklyNewTableData.append("</table>");
                        }
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedIP.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        List<String> ipList = monitoringGraphsManager.getListOfBlacklistedIP();
                        Iterator iterator = ipList.iterator();
                        while (iterator.hasNext()){
                            StringBuffer data = new StringBuffer();
                            String ip = (String) iterator.next();
                            List<DateVO> list = dateManager.getWeeksOfMonthNew();
                            for (DateVO dateVO : list)
                            {
                                long ipCount = monitoringGraphsManager.getTransDetailsFromBlackListedIP(terminalVO, dateVO, ip);
                                if (ipCount > maxRatioCompositeWeekly){
                                    maxRatioCompositeWeekly = ipCount;
                                }
                                if (isData){
                                    if (data1.length() > 0){
                                        data1.append(",");
                                    }
                                    data1.append("\"" + dateVO.getDateLabel() + "\"");
                                }
                                if (data.length() > 0){
                                    data.append(",");
                                }
                                data.append(Double.valueOf(ipCount).intValue());
                            }
                            hashMap.put(ip, data.toString());
                            isData = false;
                        }
                        StringBuffer stringBuffer1 = new StringBuffer();
                        Iterator iterator1 = hashMap.keySet().iterator();
                        int colorId = 0;
                        while (iterator1.hasNext()){
                            String label = (String) iterator1.next();
                            String labelValue = (String) hashMap.get(label);
                            String labelStr = "\"label\":\"" + label + "\"";
                            String labelStr1 = "\"backgroundColor\":" + colorArray[colorId];
                            String data = "\"data\":[" + labelValue + "]";
                            if (stringBuffer1.length() > 0){
                                stringBuffer1.append(",");
                            }
                            stringBuffer1.append("{" + labelStr + "," + labelStr1 + "," + data + "}");
                            colorId = colorId + 1;
                        }
                        if (data1.length() > 0 && hashMap.size() > 0){
                            weeklyNewJsonStr = "{\"labels\":[" + data1.toString() + "],\"datasets\":[" + stringBuffer1.toString() + "]}";
                            weeklyNewTableData = new StringBuffer();
                            String month[] = data1.toString().split(",");
                            weeklyNewTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            weeklyNewTableData.append("<tr>");
                            weeklyNewTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + month.length + 1 + "\">" + displayRuleName + " [In Current Month]" + "</td>");
                            weeklyNewTableData.append("</tr>");
                            weeklyNewTableData.append("<tr>");
                            weeklyNewTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            for (int i = 0; i < month.length; i++){
                                weeklyNewTableData.append("<td align=\"center\" class=\"th0\">" + month[i].replace("\"", "") + "</td>");
                            }
                            weeklyNewTableData.append("</tr>");
                            Iterator iterator2 = hashMap.keySet().iterator();
                            while (iterator2.hasNext()){
                                String label = (String) iterator2.next();
                                String labelValue = (String) hashMap.get(label);
                                String dataArr[] = labelValue.split(",");
                                weeklyNewTableData.append("<tr>");
                                weeklyNewTableData.append("<td align=\"center\" class=\"th0\">" + label + "</td>");
                                for (String data : dataArr){
                                    weeklyNewTableData.append("<td align=\"center\">" + Double.valueOf(data).intValue() + "</td>");
                                }
                                weeklyNewTableData.append("</tr>");
                            }
                            weeklyNewTableData.append("</table>");
                        }
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedCard.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        List<String> cardList = monitoringGraphsManager.getListOfBlacklistedCard();
                        Iterator iterator = cardList.iterator();
                        while (iterator.hasNext()){
                            StringBuffer data = new StringBuffer();
                            String cardnumber = (String) iterator.next();
                            String cardArr[] = cardnumber.split(":");
                            List<DateVO> list = dateManager.getWeeksOfMonthNew();
                            for (DateVO dateVO : list){
                                long cardCount = monitoringGraphsManager.getTransDetailsFromBlackListedCard(terminalVO, dateVO, cardArr[0], cardArr[1]);
                                if (cardCount > maxRatioCompositeWeekly){
                                    maxRatioCompositeWeekly = cardCount;
                                }
                                if (isData){
                                    if (data1.length() > 0){
                                        data1.append(",");
                                    }
                                    data1.append("\"" + dateVO.getDateLabel() + "\"");
                                }
                                if (data.length() > 0){
                                    data.append(",");
                                }
                                data.append(Double.valueOf(cardCount).intValue());
                            }
                            hashMap.put(cardnumber, data.toString());
                            isData = false;
                        }
                        StringBuffer stringBuffer1 = new StringBuffer();
                        Iterator iterator1 = hashMap.keySet().iterator();
                        int colorId = 0;
                        while (iterator1.hasNext()){
                            String label = (String) iterator1.next();
                            String labelValue = (String) hashMap.get(label);
                            String labelStr = "\"label\":\"" + label + "\"";
                            String labelStr1 = "\"backgroundColor\":" + colorArray[colorId];
                            String data = "\"data\":[" + labelValue + "]";
                            if (stringBuffer1.length() > 0){
                                stringBuffer1.append(",");
                            }
                            stringBuffer1.append("{" + labelStr + "," + labelStr1 + "," + data + "}");
                            colorId = colorId + 1;
                        }
                        if (data1.length() > 0 && hashMap.size() > 0){
                            weeklyNewJsonStr = "{\"labels\":[" + data1.toString() + "],\"datasets\":[" + stringBuffer1.toString() + "]}";
                            weeklyNewTableData = new StringBuffer();
                            String month[] = data1.toString().split(",");
                            weeklyNewTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            weeklyNewTableData.append("<tr>");
                            weeklyNewTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + month.length + 1 + "\">" + displayRuleName + " [In Current Month]" + "</td>");
                            weeklyNewTableData.append("</tr>");
                            weeklyNewTableData.append("<tr>");
                            weeklyNewTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            for (int i = 0; i < month.length; i++){
                                weeklyNewTableData.append("<td align=\"center\" class=\"th0\">" + month[i].replace("\"", "") + "</td>");
                            }
                            weeklyNewTableData.append("</tr>");
                            Iterator iterator2 = hashMap.keySet().iterator();
                            while (iterator2.hasNext()){
                                String label = (String) iterator2.next();
                                String labelValue = (String) hashMap.get(label);
                                String dataArr[] = labelValue.split(",");
                                weeklyNewTableData.append("<tr>");
                                weeklyNewTableData.append("<td align=\"center\" class=\"th0\">" + label + "</td>");
                                for (String data : dataArr){
                                    weeklyNewTableData.append("<td align=\"center\">" + Double.valueOf(data).intValue() + "</td>");
                                }
                                weeklyNewTableData.append("</tr>");
                            }
                            weeklyNewTableData.append("</table>");
                        }
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedEmail.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        List<String> emailList = monitoringGraphsManager.getListOfBlacklistedEmails();
                        Iterator iterator = emailList.iterator();
                        while (iterator.hasNext()){
                            StringBuffer data = new StringBuffer();
                            String emailAddress = (String) iterator.next();
                            List<DateVO> list = dateManager.getWeeksOfMonthNew();
                            for (DateVO dateVO : list){
                                long emailCount = monitoringGraphsManager.getTransDetailsFromBlackListedEmails(terminalVO, dateVO, emailAddress);
                                if (emailCount > maxRatioCompositeWeekly){
                                    maxRatioCompositeWeekly = emailCount;
                                }
                                if (isData){
                                    if (data1.length() > 0){
                                        data1.append(",");
                                    }
                                    data1.append("\"" + dateVO.getDateLabel() + "\"");
                                }
                                if (data.length() > 0){
                                    data.append(",");
                                }
                                data.append(Double.valueOf(emailCount).intValue());
                            }
                            hashMap.put(emailAddress, data.toString());
                            isData = false;
                        }
                        StringBuffer stringBuffer1 = new StringBuffer();
                        Iterator iterator1 = hashMap.keySet().iterator();
                        int colorId = 0;
                        while (iterator1.hasNext()){
                            String label = (String) iterator1.next();
                            String labelValue = (String) hashMap.get(label);
                            String labelStr = "\"label\":\"" + label + "\"";
                            String labelStr1 = "\"backgroundColor\":" + colorArray[colorId];
                            String data = "\"data\":[" + labelValue + "]";
                            if (stringBuffer1.length() > 0){
                                stringBuffer1.append(",");
                            }
                            stringBuffer1.append("{" + labelStr + "," + labelStr1 + "," + data + "}");
                            colorId = colorId + 1;
                        }
                        if (data1.length() > 0 && hashMap.size() > 0){
                            weeklyNewJsonStr = "{\"labels\":[" + data1.toString() + "],\"datasets\":[" + stringBuffer1.toString() + "]}";
                            weeklyNewTableData = new StringBuffer();
                            String month[] = data1.toString().split(",");
                            weeklyNewTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            weeklyNewTableData.append("<tr>");
                            weeklyNewTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + month.length + 1 + "\">" + displayRuleName + " [In Current Month]" + "</td>");
                            weeklyNewTableData.append("</tr>");
                            weeklyNewTableData.append("<tr>");
                            weeklyNewTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            for (int i = 0; i < month.length; i++){
                                weeklyNewTableData.append("<td align=\"center\" class=\"th0\">" + month[i].replace("\"", "") + "</td>");
                            }
                            weeklyNewTableData.append("</tr>");
                            Iterator iterator2 = hashMap.keySet().iterator();
                            while (iterator2.hasNext()){
                                String label = (String) iterator2.next();
                                String labelValue = (String) hashMap.get(label);
                                String dataArr[] = labelValue.split(",");
                                weeklyNewTableData.append("<tr>");
                                weeklyNewTableData.append("<td align=\"center\" class=\"th0\">" + label + "</td>");
                                for (String data : dataArr){
                                    weeklyNewTableData.append("<td align=\"center\">" + Double.valueOf(data).intValue() + "</td>");
                                }
                                weeklyNewTableData.append("</tr>");
                            }
                            weeklyNewTableData.append("</table>");
                        }
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedName.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        List<String> nameList = monitoringGraphsManager.getListOfBlacklistedNames();
                        Iterator iterator = nameList.iterator();
                        while (iterator.hasNext()){
                            StringBuffer data = new StringBuffer();
                            String names = (String) iterator.next();
                            String nameArr[] = names.split(" ");
                            List<DateVO> list = dateManager.getWeeksOfMonthNew();
                            for (DateVO dateVO : list){
                                long nameCount = monitoringGraphsManager.getTransDetailsFromBlackListedNames(terminalVO, dateVO, nameArr[0], nameArr[1]);
                                if (nameCount > maxRatioCompositeWeekly){
                                    maxRatioCompositeWeekly = nameCount;
                                }
                                if (isData){
                                    if (data1.length() > 0){
                                        data1.append(",");
                                    }
                                    data1.append("\"" + dateVO.getDateLabel() + "\"");
                                }
                                if (data.length() > 0){
                                    data.append(",");
                                }
                                data.append(Double.valueOf(nameCount).intValue());
                            }
                            hashMap.put(names, data.toString());
                            isData = false;
                        }
                        StringBuffer stringBuffer1 = new StringBuffer();
                        Iterator iterator1 = hashMap.keySet().iterator();
                        int colorId = 0;
                        while (iterator1.hasNext()){
                            String label = (String) iterator1.next();
                            String labelValue = (String) hashMap.get(label);
                            String labelStr = "\"label\":\"" + label + "\"";
                            String labelStr1 = "\"backgroundColor\":" + colorArray[colorId];
                            String data = "\"data\":[" + labelValue + "]";
                            if (stringBuffer1.length() > 0){
                                stringBuffer1.append(",");
                            }
                            stringBuffer1.append("{" + labelStr + "," + labelStr1 + "," + data + "}");
                            colorId = colorId + 1;
                        }
                        if (data1.length() > 0 && hashMap.size() > 0){
                            weeklyNewJsonStr = "{\"labels\":[" + data1.toString() + "],\"datasets\":[" + stringBuffer1.toString() + "]}";
                            weeklyNewTableData = new StringBuffer();
                            String month[] = data1.toString().split(",");
                            weeklyNewTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            weeklyNewTableData.append("<tr>");
                            weeklyNewTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + month.length + 1 + "\">" + displayRuleName + " [In Current Month]" + "</td>");
                            weeklyNewTableData.append("</tr>");
                            weeklyNewTableData.append("<tr>");
                            weeklyNewTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            for (int i = 0; i < month.length; i++){
                                weeklyNewTableData.append("<td align=\"center\" class=\"th0\">" + month[i].replace("\"", "") + "</td>");
                            }
                            weeklyNewTableData.append("</tr>");
                            Iterator iterator2 = hashMap.keySet().iterator();
                            while (iterator2.hasNext()){
                                String label = (String) iterator2.next();
                                String labelValue = (String) hashMap.get(label);
                                String dataArr[] = labelValue.split(",");
                                weeklyNewTableData.append("<tr>");
                                weeklyNewTableData.append("<td align=\"center\" class=\"th0\">" + label + "</td>");
                                for (String data : dataArr){
                                    weeklyNewTableData.append("<td align=\"center\">" + Double.valueOf(data).intValue() + "</td>");
                                }
                                weeklyNewTableData.append("</tr>");
                            }
                            weeklyNewTableData.append("</table>");
                        }
                    }
                }
                else{
                    logger.debug("Data Not Found");
                }
            }
            if ("all".equals(frequency) || "monthly".equals(frequency)){
                String tableHeaderFreq="in last six month";
                if ("Y".equals(isMonthlyExecution)){
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Amount.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        alertThreshold = monitoringParameterMappingVO.getMonthlyAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getMonthlySuspensionThreshold();
                        List<int[]> ints = monitoringGraphsManager.getCategoryByThreshold(alertThreshold,suspensionThreshold);

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
                        for (DateVO dateVO : list){
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailMonthly){
                                maxRatioDetailMonthly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);
                        }
                        monthlyJsonStr = "{\"labels\":[" + label + "],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + catagoryData1 + "]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[" + catagoryData2 + "]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[" + catagoryData3 + "]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[" + catagoryData4 + "]}]}";
                        DateVO dateVO = dateManager.getLastSixCalendarMonthDateRangeForGraph();
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
                            monthlyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }
                        monthlyTableData = monitoringGraphsManager.prepareStackBarChartTable(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName,tableHeaderFreq, monitoringParameterVO);
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardSameAmountConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        alertThreshold = monitoringParameterMappingVO.getMonthlyAlertThreshold();
                        suspensionThreshold = monitoringParameterMappingVO.getMonthlySuspensionThreshold();
                        DateVO compositDateVO = dateManager.getLastSixCalendarMonthDateRangeForGraph();
                        double[] doubles=monitoringGraphsManager.getMinMaxTransactionAmountByDate(terminalVO,compositDateVO);
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
                        for (DateVO dateVO : list){
                            HashMap hashMap = monitoringGraphsManager.getCardCountBasedOnBinAndAmountCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailMonthly){
                                maxRatioDetailMonthly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);
                        }
                        monthlyJsonStr = "{\"labels\":[" + label + "],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + catagoryData1 + "]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[" + catagoryData2 + "]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[" + catagoryData3 + "]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[" + catagoryData4 + "]}]}";
                        HashMap hashMap = monitoringGraphsManager.getCardCountBasedOnBinAndAmountCategory(terminalVO, compositDateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
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
                            monthlyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }
                        monthlyTableData = monitoringGraphsManager.prepareStackBarChartTable(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName,tableHeaderFreq, monitoringParameterVO);
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
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
                        for (DateVO dateVO : list){
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinCategory(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailMonthly){
                                maxRatioDetailMonthly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);
                        }
                        monthlyJsonStr = "{\"labels\":[" + label + "],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + catagoryData1 + "]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[" + catagoryData2 + "]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[" + catagoryData3 + "]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[" + catagoryData4 + "]}]}";
                        DateVO dateVO = dateManager.getLastSixCalendarMonthDateRangeForGraph();
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
                            monthlyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }
                        monthlyTableData = monitoringGraphsManager.prepareStackBarChartTable(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName,tableHeaderFreq, monitoringParameterVO);
                    }
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
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
                        String category4 = categoryArray4[0] + " and above";

                        StringBuffer label = new StringBuffer();
                        StringBuffer catagoryData1 = new StringBuffer();
                        StringBuffer catagoryData2 = new StringBuffer();
                        StringBuffer catagoryData3 = new StringBuffer();
                        StringBuffer catagoryData4 = new StringBuffer();
                        List<DateVO> list = dateManager.getMonthlyLineChartQuarter();
                        for (DateVO dateVO : list){
                            HashMap hashMap = monitoringGraphsManager.getTransactionCountBasedOnBinUsedDays(terminalVO, dateVO, categoryArray1, categoryArray2, categoryArray3, categoryArray4);
                            data01 = (Long) hashMap.get("categoryCounter1");
                            data02 = (Long) hashMap.get("categoryCounter2");
                            data03 = (Long) hashMap.get("categoryCounter3");
                            data04 = (Long) hashMap.get("categoryCounter4");
                            totalData = data01 + data02 + data03 + data04;
                            if (totalData > maxRatioDetailMonthly){
                                maxRatioDetailMonthly = totalData;
                            }
                            monitoringGraphsManager.appendDataForStackBarChart(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label);
                            String labelName = dateVO.getDateLabel();
                            label.append("\"" + labelName + "\"");
                            catagoryData1.append(data01);
                            catagoryData2.append(data02);
                            catagoryData3.append(data03);
                            catagoryData4.append(data04);
                        }
                        monthlyJsonStr = "{\"labels\":[" + label + "],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + catagoryData1 + "]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[" + catagoryData2 + "]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[" + catagoryData3 + "]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[" + catagoryData4 + "]}]}";
                        DateVO dateVO = dateManager.getLastSixMonthDateRange();
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
                            monthlyJsonStr1 = "{\"labels\":[\"" + category1 + "\",\"" + category2 + "\",\"" + category3 + "\",\"" + category4 + "\"],\"datasets\":[{\"label\":\"" + category1 + "\",\"backgroundColor\":\"#68c39f\",\"data\":[" + data01 + ",0,0,0]},{\"label\":\"" + category2 + "\",\"backgroundColor\":\"#edce8c\",\"data\":[0," + data02 + ",0,0]},{\"label\":\"" + category3 + "\",\"backgroundColor\":\"#Abb7b7\",\"data\":[0,0," + data03 + ",0]},{\"label\":\"" + category4 + "\",\"backgroundColor\":\"#4a525f\",\"data\":[0,0,0," + data04 + "]}]}";
                        }
                        monthlyTableData = monitoringGraphsManager.prepareStackBarChartTable(catagoryData1, catagoryData2, catagoryData3, catagoryData4, label, category1, category2, category3, category4, data01, data02, data03, data04, displayRuleName,tableHeaderFreq, monitoringParameterVO);
                    }
                    data1 = new StringBuffer();
                    boolean isData = true;
                    HashMap hashMap = new HashMap();
                    if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedCountry.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        List<String> countryList = monitoringGraphsManager.getListOfBlacklistedCountry();
                        Iterator iterator = countryList.iterator();
                        while (iterator.hasNext()){
                            StringBuffer data = new StringBuffer();
                            String country = (String) iterator.next();
                            List<DateVO> list = dateManager.getMonthlyLineChartQuarter();
                            for (DateVO dateVO : list){
                                long countryCount = monitoringGraphsManager.getTransDetailsFromBlackListedCountry(terminalVO, dateVO, country);
                                if(countryCount > maxRatioCompositeMonthly){
                                    maxRatioCompositeMonthly = countryCount;
                                }
                                if (isData){
                                    if (data1.length() > 0){
                                        data1.append(",");
                                    }
                                    data1.append("\"" + dateVO.getDateLabel() + "\"");
                                }
                                if (data.length() > 0){
                                    data.append(",");
                                }
                                data.append(Double.valueOf(countryCount).intValue());
                            }
                            hashMap.put(country, data.toString());
                            isData = false;
                        }
                        StringBuffer stringBuffer1 = new StringBuffer();
                        Iterator iterator1 = hashMap.keySet().iterator();
                        int colorId = 0;
                        while (iterator1.hasNext()){
                            String label = (String) iterator1.next();
                            String labelValue = (String) hashMap.get(label);
                            String labelStr = "\"label\":\"" + label + "\"";
                            String labelStr1 = "\"backgroundColor\":" + colorArray[colorId];
                            String data = "\"data\":[" + labelValue + "]";
                            if (stringBuffer1.length() > 0){
                                stringBuffer1.append(",");
                            }
                            stringBuffer1.append("{" + labelStr + "," + labelStr1 + "," + data + "}");
                            colorId = colorId + 1;
                        }
                        if (data1.length() > 0 && hashMap.size() > 0){
                            monthlyJsonStr = "{\"labels\":[" + data1.toString() + "],\"datasets\":[" + stringBuffer1.toString() + "]}";
                            monthlyTableData = new StringBuffer();
                            String month[] = data1.toString().split(",");
                            monthlyTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            monthlyTableData.append("<tr>");
                            monthlyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + month.length + 1 + "\">" + displayRuleName + " [Last Six Months]" + "</td>");
                            monthlyTableData.append("</tr>");
                            monthlyTableData.append("<tr>");
                            monthlyTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            for (int i = 0; i < month.length; i++){
                                monthlyTableData.append("<td align=\"center\" class=\"th0\">" + month[i].replace("\"", "") + "</td>");
                            }
                            monthlyTableData.append("</tr>");
                            Iterator iterator2 = hashMap.keySet().iterator();
                            while (iterator2.hasNext()){
                                String label = (String) iterator2.next();
                                String labelValue = (String) hashMap.get(label);
                                String dataArr[] = labelValue.split(",");
                                monthlyTableData.append("<tr>");
                                monthlyTableData.append("<td align=\"center\" class=\"th0\">" + label + "</td>");
                                for (String data : dataArr){
                                    monthlyTableData.append("<td align=\"center\">" + Double.valueOf(data).intValue() + "</td>");
                                }
                                monthlyTableData.append("</tr>");
                            }
                            monthlyTableData.append("</table>");
                        }
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedIP.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        List<String> ipList = monitoringGraphsManager.getListOfBlacklistedIP();
                        Iterator iterator = ipList.iterator();
                        while (iterator.hasNext()){
                            StringBuffer data = new StringBuffer();
                            String ip = (String) iterator.next();
                            List<DateVO> list = dateManager.getMonthlyLineChartQuarter();
                            for (DateVO dateVO : list){
                                long ipCount = monitoringGraphsManager.getTransDetailsFromBlackListedIP(terminalVO, dateVO, ip);
                                if (ipCount > maxRatioCompositeMonthly){
                                    maxRatioCompositeMonthly = ipCount;
                                }

                                if (isData){
                                    if (data1.length() > 0){
                                        data1.append(",");
                                    }
                                    data1.append("\"" + dateVO.getDateLabel() + "\"");
                                }
                                if (data.length() > 0){
                                    data.append(",");
                                }
                                data.append(Double.valueOf(ipCount).intValue());
                            }
                            hashMap.put(ip, data.toString());
                            isData = false;
                        }
                        StringBuffer stringBuffer1 = new StringBuffer();
                        Iterator iterator1 = hashMap.keySet().iterator();
                        int colorId = 0;
                        while (iterator1.hasNext()){
                            String label = (String) iterator1.next();
                            String labelValue = (String) hashMap.get(label);
                            String labelStr = "\"label\":\"" + label + "\"";
                            String labelStr1 = "\"backgroundColor\":" + colorArray[colorId];
                            String data = "\"data\":[" + labelValue + "]";
                            if (stringBuffer1.length() > 0){
                                stringBuffer1.append(",");
                            }
                            stringBuffer1.append("{" + labelStr + "," + labelStr1 + "," + data + "}");
                            colorId = colorId + 1;
                        }
                        if (data1.length() > 0 && hashMap.size() > 0){
                            monthlyJsonStr = "{\"labels\":[" + data1.toString() + "],\"datasets\":[" + stringBuffer1.toString() + "]}";
                            monthlyTableData = new StringBuffer();
                            String month[] = data1.toString().split(",");
                            monthlyTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            monthlyTableData.append("<tr>");
                            monthlyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + month.length + 1 + "\">" + displayRuleName + " [Last Six Months]" + "</td>");
                            monthlyTableData.append("</tr>");
                            monthlyTableData.append("<tr>");
                            monthlyTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            for (int i = 0; i < month.length; i++){
                                monthlyTableData.append("<td align=\"center\" class=\"th0\">" + month[i].replace("\"", "") + "</td>");
                            }
                            monthlyTableData.append("</tr>");
                            Iterator iterator2 = hashMap.keySet().iterator();
                            while (iterator2.hasNext()){
                                String label = (String) iterator2.next();
                                String labelValue = (String) hashMap.get(label);
                                String dataArr[] = labelValue.split(",");
                                monthlyTableData.append("<tr>");
                                monthlyTableData.append("<td align=\"center\" class=\"th0\">" + label + "</td>");
                                for (String data : dataArr){
                                    monthlyTableData.append("<td align=\"center\">" + Double.valueOf(data).intValue() + "</td>");
                                }
                                monthlyTableData.append("</tr>");
                            }
                            monthlyTableData.append("</table>");
                        }
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedCard.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        List<String> cardList = monitoringGraphsManager.getListOfBlacklistedCard();
                        Iterator iterator = cardList.iterator();
                        while (iterator.hasNext()){
                            StringBuffer data = new StringBuffer();
                            String cardnumber = (String) iterator.next();
                            String cardArr[] = cardnumber.split(":");
                            List<DateVO> list = dateManager.getMonthlyLineChartQuarter();
                            for (DateVO dateVO : list){
                                long cardCount = monitoringGraphsManager.getTransDetailsFromBlackListedCard(terminalVO, dateVO, cardArr[0], cardArr[1]);
                                if (cardCount > maxRatioCompositeMonthly){
                                    maxRatioCompositeMonthly = cardCount;
                                }
                                if (isData){
                                    if (data1.length() > 0){
                                        data1.append(",");
                                    }
                                    data1.append("\"" + dateVO.getDateLabel() + "\"");
                                }
                                if (data.length() > 0){
                                    data.append(",");
                                }
                                data.append(Double.valueOf(cardCount).intValue());
                            }
                            hashMap.put(cardnumber, data.toString());
                            isData = false;
                        }
                        StringBuffer stringBuffer1 = new StringBuffer();
                        Iterator iterator1 = hashMap.keySet().iterator();
                        int colorId = 0;
                        while (iterator1.hasNext()){
                            String label = (String) iterator1.next();
                            String labelValue = (String) hashMap.get(label);
                            String labelStr = "\"label\":\"" + label + "\"";
                            String labelStr1 = "\"backgroundColor\":" + colorArray[colorId];
                            String data = "\"data\":[" + labelValue + "]";
                            if (stringBuffer1.length() > 0){
                                stringBuffer1.append(",");
                            }
                            stringBuffer1.append("{" + labelStr + "," + labelStr1 + "," + data + "}");
                            colorId = colorId + 1;
                        }
                        if (data1.length() > 0 && hashMap.size() > 0){
                            monthlyJsonStr = "{\"labels\":[" + data1.toString() + "],\"datasets\":[" + stringBuffer1.toString() + "]}";
                            monthlyTableData = new StringBuffer();
                            String month[] = data1.toString().split(",");
                            monthlyTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            monthlyTableData.append("<tr>");
                            monthlyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + month.length + 1 + "\">" + displayRuleName + " [Last Six Months]" + "</td>");
                            monthlyTableData.append("</tr>");
                            monthlyTableData.append("<tr>");
                            monthlyTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            for (int i = 0; i < month.length; i++){
                                monthlyTableData.append("<td align=\"center\" class=\"th0\">" + month[i].replace("\"", "") + "</td>");
                            }
                            monthlyTableData.append("</tr>");
                            Iterator iterator2 = hashMap.keySet().iterator();
                            while (iterator2.hasNext()){
                                String label = (String) iterator2.next();
                                String labelValue = (String) hashMap.get(label);
                                String dataArr[] = labelValue.split(",");
                                monthlyTableData.append("<tr>");
                                monthlyTableData.append("<td align=\"center\" class=\"th0\">" + label + "</td>");
                                for (String data : dataArr){
                                    monthlyTableData.append("<td align=\"center\">" + Double.valueOf(data).intValue() + "</td>");
                                }
                                monthlyTableData.append("</tr>");
                            }
                            monthlyTableData.append("</table>");
                        }
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedEmail.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        List<String> emailList = monitoringGraphsManager.getListOfBlacklistedEmails();
                        Iterator iterator = emailList.iterator();
                        while (iterator.hasNext()){
                            StringBuffer data = new StringBuffer();
                            String emailAddress = (String) iterator.next();
                            List<DateVO> list = dateManager.getMonthlyLineChartQuarter();
                            for (DateVO dateVO : list){
                                long emailCount = monitoringGraphsManager.getTransDetailsFromBlackListedEmails(terminalVO, dateVO, emailAddress);
                                if (emailCount > maxRatioCompositeMonthly){
                                    maxRatioCompositeMonthly = emailCount;
                                }
                                if (isData){
                                    if (data1.length() > 0){
                                        data1.append(",");
                                    }
                                    data1.append("\"" + dateVO.getDateLabel() + "\"");
                                }
                                if (data.length() > 0){
                                    data.append(",");
                                }
                                data.append(Double.valueOf(emailCount).intValue());
                            }
                            hashMap.put(emailAddress, data.toString());
                            isData = false;
                        }
                        StringBuffer stringBuffer1 = new StringBuffer();
                        Iterator iterator1 = hashMap.keySet().iterator();
                        int colorId = 0;
                        while (iterator1.hasNext()){
                            String label = (String) iterator1.next();
                            String labelValue = (String) hashMap.get(label);
                            String labelStr = "\"label\":\"" + label + "\"";
                            String labelStr1 = "\"backgroundColor\":" + colorArray[colorId];
                            String data = "\"data\":[" + labelValue + "]";
                            if (stringBuffer1.length() > 0){
                                stringBuffer1.append(",");
                            }
                            stringBuffer1.append("{" + labelStr + "," + labelStr1 + "," + data + "}");
                            colorId = colorId + 1;
                        }
                        if (data1.length() > 0 && hashMap.size() > 0){
                            monthlyJsonStr = "{\"labels\":[" + data1.toString() + "],\"datasets\":[" + stringBuffer1.toString() + "]}";
                            monthlyTableData = new StringBuffer();
                            String month[] = data1.toString().split(",");
                            monthlyTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            monthlyTableData.append("<tr>");
                            monthlyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + month.length + 1 + "\">" + displayRuleName + " [Last Six Months]" + "</td>");
                            monthlyTableData.append("</tr>");
                            monthlyTableData.append("<tr>");
                            monthlyTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            for (int i = 0; i < month.length; i++){
                                monthlyTableData.append("<td align=\"center\" class=\"th0\">" + month[i].replace("\"", "") + "</td>");
                            }
                            monthlyTableData.append("</tr>");
                            Iterator iterator2 = hashMap.keySet().iterator();
                            while (iterator2.hasNext()){
                                String label = (String) iterator2.next();
                                String labelValue = (String) hashMap.get(label);
                                String dataArr[] = labelValue.split(",");
                                monthlyTableData.append("<tr>");
                                monthlyTableData.append("<td align=\"center\" class=\"th0\">" + label + "</td>");
                                for (String data : dataArr){
                                    monthlyTableData.append("<td align=\"center\">" + Double.valueOf(data).intValue() + "</td>");
                                }
                                monthlyTableData.append("</tr>");
                            }
                            monthlyTableData.append("</table>");
                        }
                    }
                    else if (MonitoringCategory.Others.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.BlockedName.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory())){
                        List<String> nameList = monitoringGraphsManager.getListOfBlacklistedNames();
                        Iterator iterator = nameList.iterator();
                        while (iterator.hasNext()){
                            StringBuffer data = new StringBuffer();
                            String names = (String) iterator.next();
                            String nameArr[] = names.split(" ");
                            List<DateVO> list = dateManager.getMonthlyLineChartQuarter();
                            for (DateVO dateVO : list){
                                long nameCount = monitoringGraphsManager.getTransDetailsFromBlackListedNames(terminalVO, dateVO, nameArr[0], nameArr[1]);
                                if (nameCount > maxRatioCompositeMonthly){
                                    maxRatioCompositeMonthly = nameCount;
                                }
                                if (isData){
                                    if (data1.length() > 0){
                                        data1.append(",");
                                    }
                                    data1.append("\"" + dateVO.getDateLabel() + "\"");
                                }
                                if (data.length() > 0){
                                    data.append(",");
                                }
                                data.append(Double.valueOf(nameCount).intValue());
                            }
                            hashMap.put(names, data.toString());
                            isData = false;
                        }
                        StringBuffer stringBuffer1 = new StringBuffer();
                        Iterator iterator1 = hashMap.keySet().iterator();
                        int colorId = 0;
                        while (iterator1.hasNext()){
                            String label = (String) iterator1.next();
                            String labelValue = (String) hashMap.get(label);
                            String labelStr = "\"label\":\"" + label + "\"";
                            String labelStr1 = "\"backgroundColor\":" + colorArray[colorId];
                            String data = "\"data\":[" + labelValue + "]";
                            if (stringBuffer1.length() > 0){
                                stringBuffer1.append(",");
                            }
                            stringBuffer1.append("{" + labelStr + "," + labelStr1 + "," + data + "}");
                            colorId = colorId + 1;
                        }
                        if (data1.length() > 0 && hashMap.size() > 0){
                            monthlyJsonStr = "{\"labels\":[" + data1.toString() + "],\"datasets\":[" + stringBuffer1.toString() + "]}";
                            monthlyTableData = new StringBuffer();
                            String month[] = data1.toString().split(",");
                            monthlyTableData.append("<table border=\"1\" style=\"width:90%; height:100px;\">");
                            monthlyTableData.append("<tr>");
                            monthlyTableData.append("<td align=\"center\" class=\"th0\" colspan=\"" + month.length + 1 + "\">" + displayRuleName + " [Last Six Months]" + "</td>");
                            monthlyTableData.append("</tr>");
                            monthlyTableData.append("<tr>");
                            monthlyTableData.append("<td align=\"center\" class=\"th0\">&nbsp;</td>");
                            for (int i = 0; i < month.length; i++){
                                monthlyTableData.append("<td align=\"center\" class=\"th0\">" + month[i].replace("\"", "") + "</td>");
                            }
                            monthlyTableData.append("</tr>");
                            Iterator iterator2 = hashMap.keySet().iterator();
                            while (iterator2.hasNext()){
                                String label = (String) iterator2.next();
                                String labelValue = (String) hashMap.get(label);
                                String dataArr[] = labelValue.split(",");
                                monthlyTableData.append("<tr>");
                                monthlyTableData.append("<td align=\"center\" class=\"th0\">" + label + "</td>");
                                for (String data : dataArr){
                                    monthlyTableData.append("<td align=\"center\">" + Double.valueOf(data).intValue() + "</td>");
                                }
                                monthlyTableData.append("</tr>");
                            }
                            monthlyTableData.append("</table>");
                        }
                    }
                }
            }
            request.setAttribute("displayRuleName", displayRuleName);
            request.setAttribute("dailyJsonStr", dailyJsonStr);
            request.setAttribute("weeklyJsonStr", weeklyJsonStr);
            request.setAttribute("weeklyNewJsonStr", weeklyNewJsonStr);
            request.setAttribute("monthlyJsonStr", monthlyJsonStr);
            request.setAttribute("dailyJsonStr1", dailyJsonStr1);
            request.setAttribute("weeklyJsonStr1", weeklyJsonStr1);
            request.setAttribute("weeklyNewJsonStr1", weeklyNewJsonStr1);
            request.setAttribute("monthlyJsonStr1", monthlyJsonStr1);
            request.setAttribute("monitoringUnit", monitoringUnit);
            request.setAttribute("maxRatioCompositeHoursly", maxRatioCompositeHoursly);
            request.setAttribute("maxRatioCompositeDaily", maxRatioCompositeDaily);
            request.setAttribute("maxRatioCompositeWeekly", maxRatioCompositeWeekly);
            request.setAttribute("maxRatioCompositeMonthly", maxRatioCompositeMonthly);
            request.setAttribute("maxRatioDetailHoursly", maxRatioDetailHoursly);
            request.setAttribute("maxRatioDetailDaily", maxRatioDetailDaily);
            request.setAttribute("maxRatioDetailWeekly", maxRatioDetailWeekly);
            request.setAttribute("maxRatioDetailMonthly", maxRatioDetailMonthly);
            if (dailyTableData != null){
                request.setAttribute("dailyTableData", dailyTableData.toString());
            }
            if (weeklyTableData != null){
                request.setAttribute("weeklyTableData", weeklyTableData.toString());
            }
            if (weeklyNewTableData != null){
                request.setAttribute("weeklyNewTableData", weeklyNewTableData.toString());
            }
            if (monthlyTableData != null){
                request.setAttribute("monthlyTableData", monthlyTableData.toString());
            }
            if (dailyTableDataToggle != null){
                request.setAttribute("dailyTableDataToggle", dailyTableDataToggle.toString());
            }
            if (weeklyTableDataToggle != null){
                request.setAttribute("weeklyTableDataToggle", weeklyTableDataToggle.toString());
            }
            if (monthlyTableDataToggle != null){
                request.setAttribute("monthlyTableDataToggle", monthlyTableDataToggle.toString());
            }
            if (MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardSameAmountConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardSameAmount.toString().equals(monitoringParameterVO.getMonitoingKeyword()) || MonitoringKeyword.SameCardConsecutive.toString().equals(monitoringParameterVO.getMonitoingKeyword())){
                rd = request.getRequestDispatcher("/riskRuleToggleBarChart.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
            else{
                rd = request.getRequestDispatcher("/riskRuleBarChart.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
        }
        else if ("ProgressBarChart".equals(displayChartType)){
            RequestDispatcher rd=null;
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
            if("all".equals(frequency) || "daily".equals(frequency)){
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
                        dailyTableData = monitoringGraphsManager.prepareProgressBarChartTable(actualInactiveDays, alertThresholdOrange, suspensionThresholdRed, "Total chargebacks", tableHeader,tableHeaderUnit);
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
                            dailyTableData = monitoringGraphsManager.prepareProgressBarChartTable(actualInactiveDays, alertThresholdOrange, suspensionThresholdRed, "Actual inactive period", tableHeader,tableHeaderUnit);
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
                            dailyTableData = dailyTableData = monitoringGraphsManager.prepareProgressBarChartTable(actualInactiveDays, alertThresholdOrange, suspensionThresholdRed, "Actual inactive period", tableHeader,tableHeaderUnit);
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
                            dailyTableData = dailyTableData = monitoringGraphsManager.prepareProgressBarChartTable(actualInactiveDays, alertThresholdOrange, suspensionThresholdRed, "Actual inactive period", tableHeader,tableHeaderUnit);
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
                                dailyTableData = dailyTableData = monitoringGraphsManager.prepareProgressBarChartTable(actualInactiveDays, alertThresholdOrange, suspensionThresholdRed, "Transactions", tableHeader,tableHeaderUnit);
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
                        RequestDispatcher rd1 = request.getRequestDispatcher("/riskRulePendingTransactions.jsp?ctoken=" + user.getCSRFToken());
                        rd1.forward(request, response);
                        return;
                    }
                }
            }
            if ("all".equals(frequency) || "weekly".equals(frequency)){
                if ("Y".equals(isWeeklyExecution)){
                    if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                        weeklyMessage = "Total number of chargeback-[in current week]";
                        tableHeader = "Total chargeback and thereshold[in current week]";
                        tableHeaderUnit="count";
                        DateVO dateVO = dateManager.getCalendarCurrentWeekDateRange();
                        List<TransactionVO> todaysCBTransactionVOsList = merchantMonitoringManager.getTransactionListByTimestamp(terminalVO, dateVO, "chargeback");
                        weeklyActualInactiveDays = todaysCBTransactionVOsList.size();
                        weeklyAlertThresholdOrange = monitoringParameterMappingVO.getWeeklyAlertThreshold().intValue();
                        weeklySuspensionThresholdRed = monitoringParameterMappingVO.getWeeklySuspensionThreshold().intValue();
                        weeklyTableData = monitoringGraphsManager.prepareProgressBarChartTable(weeklyActualInactiveDays, weeklyAlertThresholdOrange, weeklySuspensionThresholdRed, "Total Chargebacks", tableHeader,tableHeaderUnit);
                    }
                }
            }
            if ("all".equals(frequency) || "monthly".equals(frequency)){
                if ("Y".equals(isMonthlyExecution)){
                    if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel())){
                        monthlyMessage = "Total number of chargebacks [in current month]";
                        tableHeader = "Total chargeback and thereshold[in current month]";
                        tableHeaderUnit="count";
                        DateVO dateVO = dateManager.getCurrentMonthDateRange();
                        List<TransactionVO> todaysCBTransactionVOsList = merchantMonitoringManager.getTransactionListByTimestamp(terminalVO, dateVO, "chargeback");
                        monthlyActualInactiveDays = todaysCBTransactionVOsList.size();
                        monthlyAlertThresholdOrange = monitoringParameterMappingVO.getMonthlyAlertThreshold().intValue();
                        monthlySuspensionThresholdRed = monitoringParameterMappingVO.getMonthlySuspensionThreshold().intValue();
                        monthlyTableData = monitoringGraphsManager.prepareProgressBarChartTable(monthlyActualInactiveDays, monthlyAlertThresholdOrange, monthlySuspensionThresholdRed, "Total Chargebacks", tableHeader,tableHeaderUnit);
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
            rd = request.getRequestDispatcher("/riskRuleProgressBarChart.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        else{
            logger.debug("Chart Type Not Specified");
            request.setAttribute("message", "Chart Type Not Specified");
            RequestDispatcher rd1 = request.getRequestDispatcher("/riskRuleBarChart.jsp?ctoken=" + user.getCSRFToken());
            rd1.forward(request, response);
            return;
        }
    }

    private String validateParameters(HttpServletRequest req)
    {
        logger.debug(":::::::inside validateParameters method::::");
        InputValidator inputValidator = new InputValidator();
        String error = "";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MEMBERID);
        inputFieldsListOptional.add(InputFields.TERMINALID);
        inputFieldsListOptional.add(InputFields.RISKRULEID);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, false);
        if (!errorList.isEmpty())
        {
            for (InputFields inputFields : inputFieldsListOptional)
            {
                if (errorList.getError(inputFields.toString()) != null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error + errorList.getError(inputFields.toString()).getMessage();
                }
                error = error + "<BR>";
            }
        }
        return error;
    }
}