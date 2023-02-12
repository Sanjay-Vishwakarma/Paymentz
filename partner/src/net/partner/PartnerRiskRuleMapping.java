package net.partner;

import com.directi.pg.*;
import com.manager.MerchantMonitoringManager;
import com.manager.vo.merchantmonitoring.MonitoringParameterMappingVO;
import com.manager.vo.merchantmonitoring.MonitoringParameterVO;
import com.manager.vo.merchantmonitoring.enums.*;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;

/**
 * Created by supriya on 4/2/2016.
 */

public class PartnerRiskRuleMapping extends HttpServlet
{
    private static Logger logger = new Logger(PartnerRiskRuleMapping.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        String error = "";
        String success = "";
        Connection conn = null;
        String actionExecutor = (String) session.getAttribute("username");

        String memberId = req.getParameter("memberid");
        String terminalId = req.getParameter("terminalid");
        String[] mappingIds = req.getParameterValues("mappingid_" + terminalId);
        String EOL = "<BR>";
        StringBuilder sberror = new StringBuilder();
        Functions functions = new Functions();
        if (mappingIds == null)
        {
            sberror.append("Please select risk rules to update at server side" + EOL);
            req.setAttribute("error1", sberror.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/partnerRiskRuleMapping.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        StringBuffer sb = new StringBuffer();
        /*sb.append("<table width=\"1000%\" class=\"table table-striped table-bordered table-green dataTable\">");
        sb.append("<tr>");
        sb.append("<td valign=\"middle\" align=\"center\" class=\"th0\">Rule ID</td>");
        sb.append("<td valign=\"middle\" align=\"center\" class=\"th0\">Status</td>");
        sb.append("<td valign=\"middle\" align=\"center\" class=\"th0\">Description</td>");
        sb.append("</tr>");*/

        sb.append("<div class=\"row reporttable\">");
        sb.append("<div class=\"col-md-12\">");
        sb.append("<div class=\"widget\">");
        sb.append(" <div class=\"widget-header\">");
        sb.append("<h2><i class=\"fa fa-table\"></i>&nbsp;&nbsp;<strong>Report</strong></h2>");
        sb.append("</div>");
        sb.append("<div class=\"widget-content padding\" style=\"overflow-x: auto;\">");
        sb.append("<table align=\"center\" width=\"50%\" class=\"display table table table-striped table-bordered table-hover dataTable\" style=\"font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;\">");
        sb.append("<thead>");
        sb.append("<tr>");
        sb.append("<td valign=\"middle\" align=\"center\" style=\"background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;\"><b>Rule ID</b></td>");
        sb.append("<td valign=\"middle\" align=\"center\" style=\"background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;\"><b>Status</b></td>");
        sb.append("<td valign=\"middle\" align=\"center\" style=\"background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;\"><b>Description</b></td>");
        sb.append("</tr>");
        sb.append("</thead>");

        MonitoringParameterMappingVO monitoringParameterMappingVO = new MonitoringParameterMappingVO();
        MerchantMonitoringManager merchantMonitoringManager = new MerchantMonitoringManager();
        try
        {
            conn = Database.getConnection();
            for (String mappingId : mappingIds)
            {
                MonitoringParameterVO monitoringParameterVO = merchantMonitoringManager.getMonitoringParameterDetails(mappingId);

                String ruleId = req.getParameter(terminalId+"_monitoringParameterId_" + mappingId);
                String alertActivation = req.getParameter(terminalId+"_alertActiovation_" + mappingId);
                String alertThreshold = req.getParameter(terminalId+"_alertThreshold_" + mappingId);
                String hiddenAlertThreshold = req.getParameter(terminalId+"_hiddenAlertThreshold_" + mappingId);

                String weeklyAlertThreshold = req.getParameter(terminalId+"_weeklyAlertThreshold_" + mappingId);
                String hiddenWeeklyAlertThreshold = req.getParameter(terminalId+"_hiddenWeeklyAlertThreshold_" + mappingId);
                String monthlyAlertThreshold = req.getParameter(terminalId+"_monthlyAlertThreshold_" + mappingId);
                String hiddenMonthlyAlertThreshold = req.getParameter(terminalId+"_hiddenMonthlyAlertThreshold_" + mappingId);

                String suspensionActivation = req.getParameter(terminalId+"_suspensionActivation_" + mappingId);
                String suspensionThreshold = req.getParameter(terminalId+"_suspensionThreshold_" + mappingId);
                String hiddenSuspensionThreshold = req.getParameter(terminalId+"_hiddenSuspensionThreshold_" + mappingId);
                String weeklySuspensionThreshold = req.getParameter(terminalId+"_weeklySuspensionThreshold_" + mappingId);
                String hiddenWeeklySuspensionThreshold = req.getParameter(terminalId+"_hiddenWeeklySuspensionThreshold_" + mappingId);
                String monthlySuspensionThreshold = req.getParameter(terminalId+"_monthlySuspensionThreshold_" + mappingId);
                String hiddenMonthlySuspensionThreshold = req.getParameter(terminalId+"_hiddenMonthlySuspensionThreshold_" + mappingId);

                String monitoringUnit = req.getParameter(terminalId+"_monitoringUnit_" + mappingId);

                String isAlertToPartner = req.getParameter(terminalId+"_isAlertToPartner_" + mappingId);

                String isAlertToPartnerSalesTeam = req.getParameter(terminalId+"_isAlertToPartnerSales_" + mappingId);
                String isAlertToPartnerRFTeam = req.getParameter(terminalId+"_isAlertToPartnerRF_" + mappingId);
                String isAlertToPartnerCBTeam = req.getParameter(terminalId+"_isAlertToPartnerCB_" + mappingId);
                String isAlertToPartnerFraudTeam = req.getParameter(terminalId+"_isAlertToPartnerFraud_" + mappingId);
                String isAlertToPartnerTechTeam = req.getParameter(terminalId+"_isAlertToPartnerTech_" + mappingId);

                String isAlertToMerchant = req.getParameter(terminalId+"_isAlertToMerchant_" + mappingId);

                String isAlertToMerchantSalesTeam = req.getParameter(terminalId+"_isAlertToMerchantSales_" + mappingId);
                String isAlertToMerchantRFTeam = req.getParameter(terminalId+"_isAlertToMerchantRF_" + mappingId);
                String isAlertToMerchantCBTeam = req.getParameter(terminalId+"_isAlertToMerchantCB_" + mappingId);
                String isAlertToMerchantFraudTeam = req.getParameter(terminalId+"_isAlertToMerchantFraud_" + mappingId);
                String isAlertToMerchantTechTeam = req.getParameter(terminalId+"_isAlertToMerchantTech_" + mappingId);

                String isAlertToAdmin = req.getParameter(terminalId+"_isAlertToAdmin_" + mappingId);

                String isAlertToAdminSales = req.getParameter(terminalId+"_isAlertToAdminSales_" + mappingId);
                String isAlertToAdminRF = req.getParameter(terminalId+"_isAlertToAdminRF_" + mappingId);
                String isAlertToAdminCB = req.getParameter(terminalId+"_isAlertToAdminCB_" + mappingId);
                String isAlertToAdminFraud = req.getParameter(terminalId+"_isAlertToAdminFraud_" + mappingId);
                String isAlertToAdminTech = req.getParameter(terminalId+"_isAlertToAdminTech_" + mappingId);

                String isAlertToAgent = req.getParameter(terminalId+"_isAlertToAgent_"+mappingId);

                String isAlertToAgentSales = req.getParameter(terminalId+"_isAlertToAgentSales_" + mappingId);
                String isAlertToAgentRF = req.getParameter(terminalId+"_isAlertToAgentRF_" + mappingId);
                String isAlertToAgentCB = req.getParameter(terminalId+"_isAlertToAgentCB_" + mappingId);
                String isAlertToAgentFraud = req.getParameter(terminalId+"_isAlertToAgentFraud_" + mappingId);
                String isAlertToAgentTech = req.getParameter(terminalId+"_isAlertToAgentTech_" + mappingId);

                String alertMessage = req.getParameter(terminalId+"_alertMessage_"+mappingId.trim());

                String isDailyExecution = req.getParameter(terminalId+"_isdailyexecution_" + mappingId);
                String isWeeklyExecution = req.getParameter(terminalId+"_isweeklyexecution_" + mappingId);
                String isMonthlyExecution = req.getParameter(terminalId+"_ismonthlyexecution_" + mappingId);

                if ("%".equals(monitoringUnit))
                {
                    if("Y".equals(isDailyExecution))
                    {
                        if (!ESAPI.validator().isValidInput("alertThreshold", alertThreshold, "AmountMinus", 20, false))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Daily Alert Threshold"));
                            continue;
                        }

                        if (!ESAPI.validator().isValidInput("suspensionThreshold", suspensionThreshold, "AmountMinus", 20, false))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Daily Suspension Threshold"));
                            continue;
                        }
                    }
                    else
                    {
                        if (!ESAPI.validator().isValidInput("alertThreshold", hiddenAlertThreshold, "AmountMinus", 20, true))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Daily Alert Threshold"));
                            continue;
                        }
                        else
                        {
                            alertThreshold = hiddenAlertThreshold;
                        }

                        if (!ESAPI.validator().isValidInput("suspensionThreshold", hiddenSuspensionThreshold, "AmountMinus", 20, true))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Daily Suspension Threshold"));
                            continue;
                        }
                        else
                        {
                            suspensionThreshold = hiddenSuspensionThreshold;
                        }
                    }

                    if("Y".equals(isWeeklyExecution))
                    {
                        if (!ESAPI.validator().isValidInput("weeklyAlertThreshold", weeklyAlertThreshold, "AmountMinus", 20, false))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Weekly Alert Threshold"));
                            continue;
                        }

                        if (!ESAPI.validator().isValidInput("weeklySuspensionThreshold", weeklySuspensionThreshold, "AmountMinus", 20, false))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Weekly Suspension Threshold"));
                            continue;
                        }
                    }
                    else
                    {
                        if (!ESAPI.validator().isValidInput("weeklyAlertThreshold", hiddenWeeklyAlertThreshold, "AmountMinus", 20, true))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Weekly Alert Threshold"));
                            continue;
                        }
                        else
                        {
                            weeklyAlertThreshold = hiddenWeeklyAlertThreshold;
                        }

                        if (!ESAPI.validator().isValidInput("weeklySuspensionThreshold", hiddenWeeklySuspensionThreshold, "AmountMinus", 20, true))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Weekly Suspension Threshold"));
                            continue;
                        }
                        else
                        {
                            weeklySuspensionThreshold = hiddenWeeklySuspensionThreshold;
                        }
                    }

                    if("Y".equals(isMonthlyExecution))
                    {
                        if (!ESAPI.validator().isValidInput("monthlyAlertThreshold", monthlyAlertThreshold, "AmountMinus", 20, false))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Monthly Alert Threshold"));
                            continue;
                        }

                        if (!ESAPI.validator().isValidInput("monthlySuspensionThreshold", monthlySuspensionThreshold, "AmountMinus", 20, false))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Monthly Suspension Threshold"));
                            continue;
                        }
                    }
                    else
                    {
                        if (!ESAPI.validator().isValidInput("monthlyAlertThreshold", hiddenMonthlyAlertThreshold, "AmountMinus", 20, true))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Monthly Alert Threshold"));
                            continue;
                        }
                        else
                        {
                            monthlyAlertThreshold = hiddenMonthlyAlertThreshold;
                        }

                        if (!ESAPI.validator().isValidInput("monthlySuspensionThreshold", hiddenMonthlySuspensionThreshold, "AmountMinus", 20, true))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Monthly Suspension Threshold"));
                            continue;
                        }
                        else
                        {
                            monthlySuspensionThreshold = hiddenMonthlySuspensionThreshold;
                        }
                    }
                }
                else if("Cnt".equals(monitoringUnit) || "Day".equals(monitoringUnit))
                {
                    if("Y".equals(isDailyExecution))
                    {
                        if (!ESAPI.validator().isValidInput("alertThreshold", alertThreshold, "OnlyNumber", 20, false))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Daily Alert Threshold"));
                            continue;
                        }

                        if (!ESAPI.validator().isValidInput("suspensionThreshold", suspensionThreshold, "OnlyNumber", 20, false))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Daily Suspension Threshold"));
                            continue;
                        }
                    }
                    else
                    {
                        if (!ESAPI.validator().isValidInput("alertThreshold", hiddenAlertThreshold, "OnlyNumber", 20, true))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Daily Alert Threshold"));
                            continue;
                        }
                        else
                        {
                            alertThreshold = hiddenAlertThreshold;
                        }

                        if (!ESAPI.validator().isValidInput("suspensionThreshold", hiddenSuspensionThreshold, "OnlyNumber", 20, true))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Daily Suspension Threshold"));
                            continue;
                        }
                        else
                        {
                            suspensionThreshold = hiddenSuspensionThreshold;
                        }
                    }

                    if("Y".equals(isWeeklyExecution))
                    {
                        if (!ESAPI.validator().isValidInput("weeklyAlertThreshold", weeklyAlertThreshold, "OnlyNumber", 20, false))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Weekly Alert Threshold"));
                            continue;
                        }

                        if (!ESAPI.validator().isValidInput("weeklySuspensionThreshold", weeklySuspensionThreshold, "OnlyNumber", 20, false))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Weekly Suspension Threshold"));
                            continue;
                        }
                    }
                    else
                    {
                        if (!ESAPI.validator().isValidInput("weeklyAlertThreshold", hiddenWeeklyAlertThreshold, "OnlyNumber", 20, true))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Weekly Alert Threshold"));
                            continue;
                        }
                        else
                        {
                            weeklyAlertThreshold = hiddenWeeklyAlertThreshold;
                        }

                        if (!ESAPI.validator().isValidInput("weeklySuspensionThreshold", hiddenWeeklySuspensionThreshold, "OnlyNumber", 20, true))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Weekly Suspension Threshold"));
                            continue;
                        }
                        else
                        {
                            weeklySuspensionThreshold = hiddenWeeklySuspensionThreshold;
                        }
                    }

                    if("Y".equals(isMonthlyExecution))
                    {
                        if (!ESAPI.validator().isValidInput("monthlyAlertThreshold", monthlyAlertThreshold, "OnlyNumber", 20, false))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Monthly Alert Threshold"));
                            continue;
                        }

                        if (!ESAPI.validator().isValidInput("monthlySuspensionThreshold", monthlySuspensionThreshold, "OnlyNumber", 20, false))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Monthly Suspension Threshold"));
                            continue;
                        }
                    }
                    else
                    {
                        if (!ESAPI.validator().isValidInput("monthlyAlertThreshold", hiddenMonthlyAlertThreshold, "OnlyNumber", 20, true))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Monthly Alert Threshold"));
                            continue;
                        }
                        else
                        {
                            monthlyAlertThreshold = hiddenMonthlyAlertThreshold;
                        }

                        if (!ESAPI.validator().isValidInput("monthlySuspensionThreshold", hiddenMonthlySuspensionThreshold, "OnlyNumber", 20, true))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Monthly Suspension Threshold"));
                            continue;
                        }
                        else
                        {
                            monthlySuspensionThreshold = hiddenMonthlySuspensionThreshold;
                        }
                    }
                }
                else
                {
                    if("Y".equals(isDailyExecution))
                    {
                        if (!ESAPI.validator().isValidInput("alertThreshold", alertThreshold, "AmountMinus", 20, false))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Daily Alert Threshold"));
                            continue;
                        }

                        if (!ESAPI.validator().isValidInput("suspensionThreshold", suspensionThreshold, "AmountMinus", 20, false))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Daily Suspension Threshold"));
                            continue;
                        }
                    }
                    else
                    {
                        if (!ESAPI.validator().isValidInput("alertThreshold", hiddenAlertThreshold, "AmountMinus", 20, true))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Daily Alert Threshold"));
                            continue;
                        }
                        else
                        {
                            alertThreshold = hiddenAlertThreshold;
                        }

                        if (!ESAPI.validator().isValidInput("suspensionThreshold", hiddenSuspensionThreshold, "AmountMinus", 20, true))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Daily Suspension Threshold"));
                            continue;
                        }
                        else
                        {
                            suspensionThreshold = hiddenSuspensionThreshold;
                        }
                    }

                    if("Y".equals(isWeeklyExecution))
                    {
                        if (!ESAPI.validator().isValidInput("weeklyAlertThreshold", weeklyAlertThreshold, "AmountMinus", 20, false))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Weekly Alert Threshold"));
                            continue;
                        }

                        if (!ESAPI.validator().isValidInput("weeklySuspensionThreshold", weeklySuspensionThreshold, "AmountMinus", 20, false))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Weekly Suspension Threshold"));
                            continue;
                        }
                    }
                    else
                    {
                        if (!ESAPI.validator().isValidInput("weeklyAlertThreshold", hiddenWeeklyAlertThreshold, "AmountMinus", 20, true))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Weekly Alert Threshold"));
                            continue;
                        }
                        else
                        {
                            weeklyAlertThreshold = hiddenWeeklyAlertThreshold;
                        }

                        if (!ESAPI.validator().isValidInput("weeklySuspensionThreshold", hiddenWeeklySuspensionThreshold, "AmountMinus", 20, true))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Weekly Suspension Threshold"));
                            continue;
                        }
                        else
                        {
                            weeklySuspensionThreshold = hiddenWeeklySuspensionThreshold;
                        }
                    }

                    if("Y".equals(isMonthlyExecution))
                    {
                        if (!ESAPI.validator().isValidInput("monthlyAlertThreshold", monthlyAlertThreshold, "AmountMinus", 20, false))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Monthly Alert Threshold"));
                            continue;
                        }

                        if (!ESAPI.validator().isValidInput("monthlySuspensionThreshold", monthlySuspensionThreshold, "AmountMinus", 20, false))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Monthly Suspension Threshold"));
                            continue;
                        }
                    }
                    else
                    {
                        if (!ESAPI.validator().isValidInput("monthlyAlertThreshold", hiddenMonthlyAlertThreshold, "AmountMinus", 20, true))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Monthly Alert Threshold"));
                            continue;
                        }
                        else
                        {
                            monthlyAlertThreshold = hiddenMonthlyAlertThreshold;
                        }

                        if (!ESAPI.validator().isValidInput("monthlySuspensionThreshold", hiddenMonthlySuspensionThreshold, "AmountMinus", 20, true))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Monthly Suspension Threshold"));
                            continue;
                        }
                        else
                        {
                            monthlySuspensionThreshold = hiddenMonthlySuspensionThreshold;
                        }
                    }
                }

                if(Double.valueOf(alertThreshold) > Double.valueOf(suspensionThreshold))
                {
                    sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Daily Alert Threshold (Daily Alert Threshold should not be greater than Daily Suspension Threshold)"));
                    continue;
                }
                if(Double.valueOf(weeklyAlertThreshold) > Double.valueOf(weeklySuspensionThreshold))
                {
                    sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Weekly Alert Threshold (Weekly Alert Threshold should not be greater than Weekly Suspension Threshold)"));
                    continue;
                }
                if(Double.valueOf(monthlyAlertThreshold) > Double.valueOf(monthlySuspensionThreshold))
                {
                    sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Monthly Alert Threshold (Monthly Alert Threshold should not be greater than Monthly Suspension Threshold)"));
                    continue;
                }

                if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
                {
                    if(Double.valueOf(alertThreshold) > Double.valueOf(weeklyAlertThreshold))
                    {
                        sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Daily Alert Threshold (Daily Alert Threshold should not be greater than Weekly Alert Threshold)"));
                        continue;
                    }
                    if(Double.valueOf(suspensionThreshold) > Double.valueOf(weeklySuspensionThreshold))
                    {
                        sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Daily Suspension Threshold (Daily Suspension Threshold should not be greater than Weekly Suspension Threshold)"));
                        continue;
                    }
                    if(Double.valueOf(alertThreshold) > Double.valueOf(monthlyAlertThreshold))
                    {
                        sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Daily Alert Threshold (Daily Alert Threshold should not be greater than Monthly Alert Threshold)"));
                        continue;
                    }
                    if(Double.valueOf(suspensionThreshold) > Double.valueOf(monthlySuspensionThreshold))
                    {
                        sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Daily Suspension Threshold (Daily Suspension Threshold should not be greater than Monthly Suspension Threshold)"));
                        continue;
                    }
                    if(Double.valueOf(weeklyAlertThreshold) > Double.valueOf(monthlyAlertThreshold))
                    {
                        sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Weekly Alert Threshold (Weekly Alert Threshold should not be greater than Monthly Alert Threshold)"));
                        continue;
                    }
                    if(Double.valueOf(weeklySuspensionThreshold) > Double.valueOf(monthlySuspensionThreshold))
                    {
                        sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Weekly Suspension Threshold (Weekly Suspension Threshold should not be greater than Monthly Suspension Threshold)"));
                        continue;
                    }
                }

                if("N".equals(isDailyExecution) && "N".equals(isWeeklyExecution) && "N".equals(isMonthlyExecution))
                {
                    sb.append(getFormattedMsg(ruleId, "Failed", "Invalid Frequency, Please select at least one frequency"));
                    continue;
                }

                if (!functions.isValueNull(alertThreshold))
                {
                    alertThreshold = Functions.round(0.00, 2);
                }
                if (!functions.isValueNull(weeklyAlertThreshold))
                {
                    weeklyAlertThreshold = Functions.round(0.00, 2);
                }
                if (!functions.isValueNull(monthlyAlertThreshold))
                {
                    monthlyAlertThreshold = Functions.round(0.00, 2);
                }

                if (!functions.isValueNull(suspensionThreshold))
                {
                    suspensionThreshold = Functions.round(0.00, 2);
                }
                if (!functions.isValueNull(weeklySuspensionThreshold))
                {
                    weeklySuspensionThreshold = Functions.round(0.00, 2);
                }
                if (!functions.isValueNull(monthlySuspensionThreshold))
                {
                    monthlySuspensionThreshold = Functions.round(0.00, 2);
                }

                monitoringParameterMappingVO.setMonitoringParameterId(Integer.parseInt(mappingId));
                monitoringParameterMappingVO.setMemberId(Integer.parseInt(memberId));
                monitoringParameterMappingVO.setTerminalId(Integer.parseInt(terminalId));
                monitoringParameterMappingVO.setAlertThreshold(Double.valueOf(alertThreshold));
                monitoringParameterMappingVO.setWeeklyAlertThreshold(Double.valueOf(weeklyAlertThreshold));
                monitoringParameterMappingVO.setMonthlyAlertThreshold(Double.valueOf(monthlyAlertThreshold));
                monitoringParameterMappingVO.setSuspensionThreshold(Double.valueOf(suspensionThreshold));
                monitoringParameterMappingVO.setWeeklySuspensionThreshold(Double.valueOf(weeklySuspensionThreshold));
                monitoringParameterMappingVO.setMonthlySuspensionThreshold(Double.valueOf(monthlySuspensionThreshold));
                monitoringParameterMappingVO.setIsAlertToAdmin(isAlertToAdmin);
                monitoringParameterMappingVO.setIsAlertToMerchant(isAlertToMerchant);
                monitoringParameterMappingVO.setIsAlertToAgent(isAlertToAgent);
                monitoringParameterMappingVO.setIsAlertToPartner(isAlertToPartner);
                monitoringParameterMappingVO.setAlertActivation(alertActivation);
                monitoringParameterMappingVO.setSuspensionActivation(suspensionActivation);
                monitoringParameterMappingVO.setAlertMessage(alertMessage);

                monitoringParameterMappingVO.setIsAlertToAdminSales(isAlertToAdminSales);
                monitoringParameterMappingVO.setIsAlertToAdminRF(isAlertToAdminRF);
                monitoringParameterMappingVO.setIsAlertToAdminCB(isAlertToAdminCB);
                monitoringParameterMappingVO.setIsAlertToAdminFraud(isAlertToAdminFraud);
                monitoringParameterMappingVO.setIsAlertToAdminTech(isAlertToAdminTech);

                monitoringParameterMappingVO.setIsAlertToAgentSales(isAlertToAgentSales);
                monitoringParameterMappingVO.setIsAlertToAgentRF(isAlertToAgentRF);
                monitoringParameterMappingVO.setIsAlertToAgentCB(isAlertToAgentCB);
                monitoringParameterMappingVO.setIsAlertToAgentFraud(isAlertToAgentFraud);
                monitoringParameterMappingVO.setIsAlertToAgentTech(isAlertToAgentTech);

                monitoringParameterMappingVO.setIsAlertToMerchantSales(isAlertToMerchantSalesTeam);
                monitoringParameterMappingVO.setIsAlertToMerchantRF(isAlertToMerchantRFTeam);
                monitoringParameterMappingVO.setIsAlertToMerchantCB(isAlertToMerchantCBTeam);
                monitoringParameterMappingVO.setIsAlertToMerchantFraud(isAlertToMerchantFraudTeam);
                monitoringParameterMappingVO.setIsAlertToMerchantTech(isAlertToMerchantTechTeam);

                monitoringParameterMappingVO.setIsAlertToPartnerSales(isAlertToPartnerSalesTeam);
                monitoringParameterMappingVO.setIsAlertToPartnerRF(isAlertToPartnerRFTeam);
                monitoringParameterMappingVO.setIsAlertToPartnerCB(isAlertToPartnerCBTeam);
                monitoringParameterMappingVO.setIsAlertToPartnerFraud(isAlertToPartnerFraudTeam);
                monitoringParameterMappingVO.setIsAlertToPartnerTech(isAlertToPartnerTechTeam);

                monitoringParameterMappingVO.setIsDailyExecution(isDailyExecution);
                monitoringParameterMappingVO.setIsWeeklyExecution(isWeeklyExecution);
                monitoringParameterMappingVO.setIsMonthlyExecution(isMonthlyExecution);

                boolean isAvailable = merchantMonitoringManager.isMappingAvailable(memberId, terminalId, ruleId);
                boolean flag = true;
                if (isAvailable)
                {
                    boolean isRemoved = merchantMonitoringManager.removeRiskRuleMapping(memberId, terminalId, ruleId);
                    if (!isRemoved)
                    {
                        flag = false;
                    }
                }
                if (flag)
                {
                    String status = merchantMonitoringManager.monitoringParameterMappingFromPartnerAccount(monitoringParameterMappingVO);
                    if ("success".equalsIgnoreCase(status))
                    {
                        //String status1 = merchantMonitoringManager.monitoringParameterMappingLogDetails(monitoringParameterMappingVO, actionExecutor);
                        sb.append(getFormattedMsg(ruleId, "Success", "Successfully updated"));
                    }
                    else
                    {
                        sb.append(getFormattedMsg(ruleId, "Failed", "Could not be update"));
                    }
                }
                else
                {
                    sb.append(getFormattedMsg(ruleId, "Failed", "Could not be update"));
                }
            }
        }
        catch (SystemError e)
        {
            logger.error("SystemError while Monitoring Parameter Mapping Update", e);
            error = error + "Could Not Update Monitoring Parameter Mapping.";
            logger.error("SystemError---" + e);
            sb.append(getFormattedMsg("None", "Failed", error));
        }
        catch (PZDBViolationException e)
        {
            logger.error("SQLException while updating Monitoring Parameter Mapping ", e);
            error = error + "Could Not Update Monitoring Parameter Mapping.";
            logger.error("PZDBViolationException---" + e);
            sb.append(getFormattedMsg("None", "Failed", error));
        }
        finally
        {
            Database.closeConnection(conn);
        }
        req.setAttribute("success1", success);
        req.setAttribute("error1", error);
        req.setAttribute("updatemsg", sb.toString());
        RequestDispatcher rd = req.getRequestDispatcher("/partnerRiskRuleMapping.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req, res);
    }

    public String getFormattedMsg(String ruleId, String status, String statusMsg)
    {
        StringBuffer stringBuffer = new StringBuffer();
        /*stringBuffer.append("<tr>");
        stringBuffer.append("<td align=\"center\">" + ruleId + "</td>");
        stringBuffer.append("<td align=\"center\">" + status + "</td>");
        stringBuffer.append("<td align=\"center\">" + statusMsg + "</td>");
        stringBuffer.append("</tr>");*/

        stringBuffer.append("<tbody>");
        stringBuffer.append("<tr>");
        stringBuffer.append("<td data-label=\"Rule ID\" align=\"center\">" + ruleId + "</td>");
        stringBuffer.append("<td data-label=\"Status\" align=\"center\">" + status + "</td>");
        stringBuffer.append("<td data-label=\"Description\" align=\"center\">" + statusMsg + "</td>");
        stringBuffer.append("</tr>");
/*        stringBuffer.append("</tbody>");
        stringBuffer.append("</table>");
        stringBuffer.append("</div>");
        stringBuffer.append("</div>");
        stringBuffer.append("</div>");
        stringBuffer.append("</div>");*/

        return stringBuffer.toString();
    }
}
