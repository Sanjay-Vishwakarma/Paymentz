import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.MerchantMonitoringManager;
import com.manager.vo.merchantmonitoring.MonitoringParameterVO;
import com.manager.vo.merchantmonitoring.enums.*;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Vishal on 5/11/2016.
 */
public class AddNewMonitoringParameter extends HttpServlet
{
    Logger logger = new Logger(AddNewMonitoringParameter.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        logger.debug("Entering NewMonitoringParameter");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        String EOL = "<BR>";
        StringBuilder sberror = new StringBuilder();
        try
        {

            if (!ESAPI.validator().isValidInput("monitoingParaName", request.getParameter("monitoingParaName"), "SafeString", 255, false) || functions.hasHTMLTags(request.getParameter("monitoingParaName")))
            {
                sberror.append("Invalid Risk Rule Name" + EOL);
            }
            if (!ESAPI.validator().isValidInput("monitoingParaTechName", request.getParameter("monitoingParaTechName"), "SafeString", 255, false) || functions.hasHTMLTags(request.getParameter("monitoingParaTechName")))
            {
                sberror.append("Invalid Risk Rule Description" + EOL);
            }
            if (!ESAPI.validator().isValidInput("defaultAlertMessage", request.getParameter("defaultAlertMessage"), "SafeString", 255, false) || functions.hasHTMLTags(request.getParameter("defaultAlertMessage")))
            {
                sberror.append("Invalid Alert Message,");
            }
            if (!ESAPI.validator().isValidInput("monitoringUnit", request.getParameter("monitoringUnit"), "SafeString", 255, false))
            {
                sberror.append("Invalid Monitoring Unit" + EOL);
            }
            if (!ESAPI.validator().isValidInput("monitoingCategory", request.getParameter("monitoingCategory"), "SafeString", 255, false))
            {
                sberror.append("Invalid Monitoring Category" + EOL);
            }
            if (!ESAPI.validator().isValidInput("monitoingDeviation", request.getParameter("monitoingDeviation"), "SafeString", 255, false))
            {
                sberror.append("Invalid Monitoring Deviation" + EOL);
            }
            if (!ESAPI.validator().isValidInput("monitoingKeyword", request.getParameter("monitoingKeyword"), "SafeString", 255, false))
            {
                sberror.append("Invalid Monitoring Keyword" + EOL);
            }
            if (!ESAPI.validator().isValidInput("monitoingSubKeyword", request.getParameter("monitoingSubKeyword"), "SafeString", 255, false))
            {
                sberror.append("Invalid Monitoring Sub-Keyword" + EOL);
            }
            if (!ESAPI.validator().isValidInput("monitoingAlertCategory", request.getParameter("monitoingAlertCategory"), "SafeString", 255, false))
            {
                sberror.append("Invalid Monitoring Alert Category" + EOL);
            }
            if (!ESAPI.validator().isValidInput("monitoingOnChannel", request.getParameter("monitoingOnChannel"), "SafeString", 255, false))
            {
                sberror.append("Invalid Monitoring Channel" + EOL);
            }
            if (!ESAPI.validator().isValidInput("displayChartType", request.getParameter("displayChartType"), "SafeString", 255, false))
            {
                sberror.append("Invalid Display Chart Type" + EOL);
            }
            if (request.getParameter("isdailyexecution") == null && request.getParameter("isweeklyexecution") == null && request.getParameter("ismonthlyexecution") == null)
            {
                sberror.append("Invalid Frequency, Please select at least one frequency" + EOL);
            }

            MonitoringParameterVO monitoringParameterVO = new MonitoringParameterVO();
            String monitoringParaName = request.getParameter("monitoingParaName");
            String monitoringParaTechName = request.getParameter("monitoingParaTechName");
            String monitoringCategory = request.getParameter("monitoingCategory");
            String monitoingDeviation = request.getParameter("monitoingDeviation");
            String monitoringKeyword = request.getParameter("monitoingKeyword");
            String monitoringSubKeyword = request.getParameter("monitoingSubKeyword");
            String monitoringAlertCategory = request.getParameter("monitoingAlertCategory");
            String monitoringOnChannel = request.getParameter("monitoingOnChannel");
            String monitoringUnit = request.getParameter("monitoringUnit");
            String defaultAlertThreshold = request.getParameter("defaultAlertThreshold");
            String defaultSuspensionThreshold = request.getParameter("defaultSuspensionThreshold");
            String defaultAlertMessage = request.getParameter("defaultAlertMessage");
            String weeklyAlertThreshold = request.getParameter("weeklyAlertThreshold");
            String weeklySuspensionThreshold = request.getParameter("weeklySuspensionThreshold");
            String monthlyAlertThreshold = request.getParameter("monthlyAlertThreshold");
            String monthlySuspensionThreshold = request.getParameter("monthlySuspensionThreshold");
            String displayChartType = request.getParameter("displayChartType");

            if (MonitoringUnit.Percentage.toString().equals(monitoringUnit) || MonitoringKeyword.HighAmountOrder.toString().equals(monitoringKeyword))
            {
                if ("Y".equals(request.getParameter("isdailyexecution")))
                {
                    if (!ESAPI.validator().isValidInput("defaultAlertThreshold", request.getParameter("defaultAlertThreshold"), "AmountMinus", 20, false))
                    {
                        sberror.append("Invalid Daily Alert Threshold-[Sample Format:00.00]" + EOL);
                    }
                    else
                    {
                        defaultAlertThreshold = request.getParameter("defaultAlertThreshold");
                    }

                    if (!ESAPI.validator().isValidInput("defaultSuspensionThreshold", request.getParameter("defaultSuspensionThreshold"), "AmountMinus", 20, false))
                    {
                        sberror.append("Invalid Daily Suspension Threshold-[Sample Format:00.00]" + EOL);
                    }
                    else
                    {
                        defaultSuspensionThreshold = request.getParameter("defaultSuspensionThreshold");
                    }
                }
                if ("Y".equals(request.getParameter("isweeklyexecution")))
                {
                    if (!ESAPI.validator().isValidInput("weeklyAlertThreshold", request.getParameter("weeklyAlertThreshold"), "AmountMinus", 20, false))
                    {
                        sberror.append("Invalid Weekly Alert Threshold [Sample Format:00.00]" + EOL);
                    }
                    else
                    {
                        weeklyAlertThreshold = request.getParameter("weeklyAlertThreshold");
                    }

                    if (!ESAPI.validator().isValidInput("weeklySuspensionThreshold", request.getParameter("weeklySuspensionThreshold"), "AmountMinus", 20, false))
                    {
                        sberror.append("Invalid Weekly Suspension Threshold [Sample Format:00.00]" + EOL);
                    }
                    else
                    {
                        weeklySuspensionThreshold = request.getParameter("weeklySuspensionThreshold");
                    }
                }
                if ("Y".equals(request.getParameter("ismonthlyexecution")))
                {
                    if (!ESAPI.validator().isValidInput("monthlyAlertThreshold", request.getParameter("monthlyAlertThreshold"), "AmountMinus", 20, false))
                    {
                        sberror.append("Invalid Monthly Alert Threshold [Sample Format:00.00]" + EOL);
                    }
                    else
                    {
                        monthlyAlertThreshold = request.getParameter("monthlyAlertThreshold");
                    }

                    if (!ESAPI.validator().isValidInput("monthlySuspensionThreshold", request.getParameter("monthlySuspensionThreshold"), "AmountMinus", 20, false))
                    {
                        sberror.append("Invalid Monthly Suspension Threshold [Sample Format:00.00]" + EOL);
                    }
                    else
                    {
                        monthlySuspensionThreshold = request.getParameter("monthlySuspensionThreshold");
                    }
                }
            }
            else
            {
                if ("Y".equals(request.getParameter("isdailyexecution")))
                {
                    if (!ESAPI.validator().isValidInput("defaultAlertThreshold", request.getParameter("defaultAlertThreshold"), "OnlyNumber", 20, false))
                    {
                        sberror.append("Invalid Daily Alert Threshold-[Sample Format:10]" + EOL);
                    }
                    else
                    {
                        defaultAlertThreshold = request.getParameter("defaultAlertThreshold");
                    }

                    if (!ESAPI.validator().isValidInput("defaultSuspensionThreshold", request.getParameter("defaultSuspensionThreshold"), "OnlyNumber", 20, false))
                    {
                        sberror.append("Invalid Daily Suspension Threshold-[Sample Format:10]" + EOL);
                    }
                    else
                    {
                        defaultSuspensionThreshold = request.getParameter("defaultSuspensionThreshold");
                    }
                }
                if ("Y".equals(request.getParameter("isweeklyexecution")))
                {
                    if (!ESAPI.validator().isValidInput("weeklyAlertThreshold", request.getParameter("weeklyAlertThreshold"), "OnlyNumber", 20, false))
                    {
                        sberror.append("Invalid Weekly Alert Threshold [Sample Format:10]" + EOL);
                    }
                    else
                    {
                        weeklyAlertThreshold = request.getParameter("weeklyAlertThreshold");
                    }

                    if (!ESAPI.validator().isValidInput("weeklySuspensionThreshold", request.getParameter("weeklySuspensionThreshold"), "OnlyNumber", 20, false))
                    {
                        sberror.append("Invalid Weekly Suspension Threshold [Sample Format:10]" + EOL);
                    }
                    else
                    {
                        weeklySuspensionThreshold = request.getParameter("weeklySuspensionThreshold");
                    }
                }
                if ("Y".equals(request.getParameter("ismonthlyexecution")))
                {
                    try
                    {
                        if (!ESAPI.validator().isValidInput("monthlyAlertThreshold", request.getParameter("monthlyAlertThreshold"), "OnlyNumber", 20, false))
                        {
                            sberror.append("Invalid Monthly Alert Threshold [Sample Format:10]" + EOL);
                        }
                        else
                        {
                            monthlyAlertThreshold = request.getParameter("monthlyAlertThreshold");
                        }
                    }
                    catch (Exception e)
                    {
                        sberror.append("Invalid Monthly Alert Threshold [Sample Format:10]" + EOL);
                    }

                    if (!ESAPI.validator().isValidInput("monthlySuspensionThreshold", request.getParameter("monthlySuspensionThreshold"), "OnlyNumber", 20, false))
                    {
                        sberror.append("Invalid Monthly Suspension Threshold [Sample Format:10]" + EOL);
                    }
                    else
                    {
                        monthlySuspensionThreshold = request.getParameter("monthlySuspensionThreshold");
                    }
                }
            }

            if (sberror.length() > 0)
            {
                request.setAttribute("message", sberror.toString());
                RequestDispatcher rd = request.getRequestDispatcher("/addNewRiskParameter.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }

            double defaultAlertThresholdDbl = 0.00;
            double defaultSuspensionThresholdDbl = 0.00;
            double weeklyAlertThresholdDbl = 0.00;
            double weeklySuspensionThresholdDbl = 0.00;
            double monthlyAlertThresholdDbl = 0.00;
            double monthlySuspensionThresholdDbl = 0.00;

            if (functions.isValueNull(defaultAlertThreshold))
            {
                defaultAlertThresholdDbl = Double.valueOf(defaultAlertThreshold);
            }
            if (functions.isValueNull(defaultSuspensionThreshold))
            {
                defaultSuspensionThresholdDbl = Double.valueOf(defaultSuspensionThreshold);
            }
            if (functions.isValueNull(weeklyAlertThreshold))
            {
                weeklyAlertThresholdDbl = Double.valueOf(weeklyAlertThreshold);
            }
            if (functions.isValueNull(weeklySuspensionThreshold))
            {
                weeklySuspensionThresholdDbl = Double.valueOf(weeklySuspensionThreshold);
            }
            if (functions.isValueNull(monthlyAlertThreshold))
            {
                monthlyAlertThresholdDbl = Double.valueOf(monthlyAlertThreshold);
            }
            if (functions.isValueNull(monthlySuspensionThreshold))
            {
                monthlySuspensionThresholdDbl = Double.valueOf(monthlySuspensionThreshold);
            }

            if (defaultAlertThresholdDbl > defaultSuspensionThresholdDbl)
            {
                sberror.append("Invalid Daily Alert Threshold (Daily Alert Threshold should not be greater than Daily Suspension Threshold)" + EOL);
            }
            if (weeklyAlertThresholdDbl > weeklySuspensionThresholdDbl)
            {
                sberror.append("Invalid Weekly Alert Threshold (Weekly Alert Threshold should not be greater than Weekly Suspension Threshold)" + EOL);
            }
            if (monthlyAlertThresholdDbl > monthlySuspensionThresholdDbl)
            {
                sberror.append("Invalid Monthly Alert Threshold (Monthly Alert Threshold should not be greater than Monthly Suspension Threshold)" + EOL);
            }

            if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                if (defaultAlertThresholdDbl > weeklyAlertThresholdDbl)
                {
                    sberror.append("Invalid Daily Alert Threshold (Daily Alert Threshold should not be greater than Weekly Alert Threshold)" + EOL);
                }
                if (defaultSuspensionThresholdDbl > weeklySuspensionThresholdDbl)
                {
                    sberror.append("Invalid Daily Suspension Threshold (Daily Suspension Threshold should not be greater than Weekly Suspension Threshold)" + EOL);
                }
                if (defaultAlertThresholdDbl > monthlyAlertThresholdDbl)
                {
                    sberror.append("Invalid Daily Alert Threshold (Daily Alert Threshold should not be greater than Monthly Alert Threshold)" + EOL);
                }
                if (defaultSuspensionThresholdDbl > monthlySuspensionThresholdDbl)
                {
                    sberror.append("Invalid Daily Suspension Threshold (Daily Suspension Threshold should not be greater than Monthly Suspension Threshold)" + EOL);
                }
                if (weeklyAlertThresholdDbl > monthlyAlertThresholdDbl)
                {
                    sberror.append("Invalid Weekly Alert Threshold (Weekly Alert Threshold should not be greater than Monthly Alert Threshold)" + EOL);
                }
                if (weeklySuspensionThresholdDbl > monthlySuspensionThresholdDbl)
                {
                    sberror.append("Invalid Weekly Suspension Threshold (Weekly Suspension Threshold should not be greater than Monthly Suspension Threshold)" + EOL);
                }
            }

            if (sberror.length() > 0)
            {
                request.setAttribute("message", sberror.toString());
                RequestDispatcher rd = request.getRequestDispatcher("/addNewRiskParameter.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }


            String defaultIsAlertToAdmin = request.getParameter("defaultIsAlertToAdmin");
            if (!functions.isValueNull(defaultIsAlertToAdmin))
            {
                defaultIsAlertToAdmin = "N";
            }

            String defaultIsAlertToAdminSales = request.getParameter("defaultIsAlertToAdminSales");
            if (!functions.isValueNull(defaultIsAlertToAdminSales))
            {
                defaultIsAlertToAdminSales = "N";
            }

            String defaultIsAlertToAdminRF = request.getParameter("defaultIsAlertToAdminRF");
            if (!functions.isValueNull(defaultIsAlertToAdminRF))
            {
                defaultIsAlertToAdminRF = "N";
            }

            String defaultIsAlertToAdminCB = request.getParameter("defaultIsAlertToAdminCB");
            if (!functions.isValueNull(defaultIsAlertToAdminCB))
            {
                defaultIsAlertToAdminCB = "N";
            }

            String defaultIsAlertToAdminFraud = request.getParameter("defaultIsAlertToAdminFraud");
            if (!functions.isValueNull(defaultIsAlertToAdminFraud))
            {
                defaultIsAlertToAdminFraud = "N";
            }

            String defaultIsAlertToAdminTech = request.getParameter("defaultIsAlertToAdminTech");
            if (!functions.isValueNull(defaultIsAlertToAdminTech))
            {
                defaultIsAlertToAdminTech = "N";
            }

            String defaultIsAlertToMerchant = request.getParameter("defaultIsAlertToMerchant");
            if (!functions.isValueNull(defaultIsAlertToMerchant))
            {
                defaultIsAlertToMerchant = "N";
            }

            String defaultIsAlertToMerchantSales = request.getParameter("defaultIsAlertToMerchantSales");
            if (!functions.isValueNull(defaultIsAlertToMerchantSales))
            {
                defaultIsAlertToMerchantSales = "N";
            }
            String defaultIsAlertToMerchantRF = request.getParameter("defaultIsAlertToMerchantRF");
            if (!functions.isValueNull(defaultIsAlertToMerchantRF))
            {
                defaultIsAlertToMerchantRF = "N";
            }
            String defaultIsAlertToMerchantCB = request.getParameter("defaultIsAlertToMerchantCB");
            if (!functions.isValueNull(defaultIsAlertToMerchantCB))
            {
                defaultIsAlertToMerchantCB = "N";
            }
            String defaultIsAlertToMerchantFraud = request.getParameter("defaultIsAlertToMerchantFraud");
            if (!functions.isValueNull(defaultIsAlertToMerchantFraud))
            {
                defaultIsAlertToMerchantFraud = "N";
            }
            String defaultIsAlertToMerchantTech = request.getParameter("defaultIsAlertToMerchantTech");
            if (!functions.isValueNull(defaultIsAlertToMerchantTech))
            {
                defaultIsAlertToMerchantTech = "N";
            }

            String defaultIsAlertToPartner = request.getParameter("defaultIsAlertToPartner");
            if (!functions.isValueNull(defaultIsAlertToPartner))
            {
                defaultIsAlertToPartner = "N";
            }
            String defaultIsAlertToPartnerSales = request.getParameter("defaultIsAlertToPartnerSales");
            if (!functions.isValueNull(defaultIsAlertToPartnerSales))
            {
                defaultIsAlertToPartnerSales = "N";
            }
            String defaultIsAlertPartnerRF = request.getParameter("defaultIsAlertPartnerRF");
            if (!functions.isValueNull(defaultIsAlertPartnerRF))
            {
                defaultIsAlertPartnerRF = "N";
            }
            String defaultIsAlertToPartnerCB = request.getParameter("defaultIsAlertToPartnerCB");
            if (!functions.isValueNull(defaultIsAlertToPartnerCB))
            {
                defaultIsAlertToPartnerCB = "N";
            }
            String defaultIsAlertToPartnerFraud = request.getParameter("defaultIsAlertToPartnerFraud");
            if (!functions.isValueNull(defaultIsAlertToPartnerFraud))
            {
                defaultIsAlertToPartnerFraud = "N";
            }
            String defaultIsAlertToPartnerTech = request.getParameter("defaultIsAlertToPartnerTech");
            if (!functions.isValueNull(defaultIsAlertToPartnerTech))
            {
                defaultIsAlertToPartnerTech = "N";
            }

            String defaultIsAlertToAgent = request.getParameter("defaultIsAlertToAgent");
            if (!functions.isValueNull(defaultIsAlertToAgent))
            {
                defaultIsAlertToAgent = "N";
            }
            String defaultIsAlertToAgentSales = request.getParameter("defaultIsAlertToAgentSales");
            if (!functions.isValueNull(defaultIsAlertToAgentSales))
            {
                defaultIsAlertToAgentSales = "N";
            }
            String defaultIsAlertToAgentRF = request.getParameter("defaultIsAlertToAgentRF");
            if (!functions.isValueNull(defaultIsAlertToAgentRF))
            {
                defaultIsAlertToAgentRF = "N";
            }
            String defaultIsAlertToAgentCB = request.getParameter("defaultIsAlertToAgentCB");
            if (!functions.isValueNull(defaultIsAlertToAgentCB))
            {
                defaultIsAlertToAgentCB = "N";
            }
            String defaultIsAlertToAgentFraud = request.getParameter("defaultIsAlertToAgentFraud");
            if (!functions.isValueNull(defaultIsAlertToAgentFraud))
            {
                defaultIsAlertToAgentFraud = "N";
            }
            String defaultIsAlertToAgentTech = request.getParameter("defaultIsAlertToAgentTech");
            if (!functions.isValueNull(defaultIsAlertToAgentTech))
            {
                defaultIsAlertToAgentTech = "N";
            }
            String defaultAlertActivation = request.getParameter("defaultAlertActivation");
            if (!functions.isValueNull(defaultAlertActivation))
            {
                defaultAlertActivation = "N";
            }
            String defaultSuspensionActivation = request.getParameter("defaultSuspensionActivation");
            if (!functions.isValueNull(defaultSuspensionActivation))
            {
                defaultSuspensionActivation = "N";
            }

            String isdailyexecution = request.getParameter("isdailyexecution");
            if (!functions.isValueNull(isdailyexecution))
            {
                isdailyexecution = "N";
            }
            String isweeklyexecution = request.getParameter("isweeklyexecution");
            if (!functions.isValueNull(isweeklyexecution))
            {
                isweeklyexecution = "N";
            }
            String ismonthlyexecution = request.getParameter("ismonthlyexecution");
            if (!functions.isValueNull(ismonthlyexecution))
            {
                ismonthlyexecution = "N";
            }

            monitoringParameterVO.setMonitoringParameterName(monitoringParaName);
            monitoringParameterVO.setMonitoingParaTechName(monitoringParaTechName);
            monitoringParameterVO.setMonitoingCategory(monitoringCategory);
            monitoringParameterVO.setMonitoingDeviation(monitoingDeviation);
            monitoringParameterVO.setMonitoingKeyword(monitoringKeyword);
            monitoringParameterVO.setMonitoingSubKeyword(monitoringSubKeyword);
            monitoringParameterVO.setMonitoingAlertCategory(monitoringAlertCategory);
            monitoringParameterVO.setMonitoingOnChannel(monitoringOnChannel);
            monitoringParameterVO.setMonitoringUnit(monitoringUnit);
            monitoringParameterVO.setDefaultAlertThreshold(defaultAlertThresholdDbl);
            monitoringParameterVO.setDefaultSuspensionThreshold(defaultSuspensionThresholdDbl);
            monitoringParameterVO.setDefaultIsAlertToAdmin(defaultIsAlertToAdmin);
            monitoringParameterVO.setDefaultIsAlertToMerchant(defaultIsAlertToMerchant);
            monitoringParameterVO.setDefaultIsAlertToPartner(defaultIsAlertToPartner);
            monitoringParameterVO.setDefaultIsAlertToAgent(defaultIsAlertToAgent);
            monitoringParameterVO.setDefaultAlertActivation(defaultAlertActivation);
            monitoringParameterVO.setDefaultSuspensionActivation(defaultSuspensionActivation);
            monitoringParameterVO.setDefaultAlertMsg(defaultAlertMessage);

            monitoringParameterVO.setDefaultIsAlertToAdminSales(defaultIsAlertToAdminSales);
            monitoringParameterVO.setDefaultIsAlertToAdminFraud(defaultIsAlertToAdminFraud);
            monitoringParameterVO.setDefaultIsAlertToAdminRF(defaultIsAlertToAdminRF);
            monitoringParameterVO.setDefaultIsAlertToAdminCB(defaultIsAlertToAdminCB);
            monitoringParameterVO.setDefaultIsAlertToAdminTech(defaultIsAlertToAdminTech);

            monitoringParameterVO.setDefaultIsAlertToMerchantSales(defaultIsAlertToMerchantSales);
            monitoringParameterVO.setDefaultIsAlertToMerchantFraud(defaultIsAlertToMerchantFraud);
            monitoringParameterVO.setDefaultIsAlertToMerchantRF(defaultIsAlertToMerchantRF);
            monitoringParameterVO.setDefaultIsAlertToMerchantCB(defaultIsAlertToMerchantCB);
            monitoringParameterVO.setDefaultIsAlertToMerchantTech(defaultIsAlertToMerchantTech);

            monitoringParameterVO.setDefaultIsAlertToPartnerSales(defaultIsAlertToPartnerSales);
            monitoringParameterVO.setDefaultIsAlertToPartnerFraud(defaultIsAlertToPartnerFraud);
            monitoringParameterVO.setDefaultIsAlertToPartnerRF(defaultIsAlertPartnerRF);
            monitoringParameterVO.setDefaultIsAlertToPartnerCB(defaultIsAlertToPartnerCB);
            monitoringParameterVO.setDefaultIsAlertToPartnerTech(defaultIsAlertToPartnerTech);

            monitoringParameterVO.setDefaultIsAlertToAgentSales(defaultIsAlertToAgentSales);
            monitoringParameterVO.setDefaultIsAlertToAgentFraud(defaultIsAlertToAgentFraud);
            monitoringParameterVO.setDefaultIsAlertToAgentRF(defaultIsAlertToAgentRF);
            monitoringParameterVO.setDefaultIsAlertToAgentCB(defaultIsAlertToAgentCB);
            monitoringParameterVO.setDefaultIsAlertToAgentTech(defaultIsAlertToAgentTech);

            monitoringParameterVO.setIsDailyExecution(isdailyexecution);
            monitoringParameterVO.setIsWeeklyExecution(isweeklyexecution);
            monitoringParameterVO.setIsMonthlyExecution(ismonthlyexecution);

            monitoringParameterVO.setWeeklyAlertThreshold(weeklyAlertThresholdDbl);
            monitoringParameterVO.setWeeklySuspensionThreshold(weeklySuspensionThresholdDbl);
            monitoringParameterVO.setMonthlyAlertThreshold(monthlyAlertThresholdDbl);
            monitoringParameterVO.setMonthlySuspensionThreshold(monthlySuspensionThresholdDbl);
            monitoringParameterVO.setDisplayChartType(displayChartType);

            String responseMsg = "";
            MerchantMonitoringManager merchantMonitoringManager = new MerchantMonitoringManager();
            boolean isParameterNameAvailable = merchantMonitoringManager.isParameterNameAvailable(monitoringParameterVO);
            if (!isParameterNameAvailable)
            {
                boolean isParameterAvailable = merchantMonitoringManager.isParameterAvailable(monitoringParameterVO);
                if (isParameterAvailable)
                {
                    responseMsg = "Risk Rule Parameter Already Available.";
                }
                else
                {
                    String status = merchantMonitoringManager.addNewMonitoringParameter(monitoringParameterVO);
                    if ("success".equalsIgnoreCase(status))
                    {
                        responseMsg = "Risk Rule Parameter Added Successfully On Account.";
                    }
                    else
                    {
                        responseMsg = "Risk Rule Parameter Adding Failed On Account.";
                    }
                }
            }
            else
            {
                responseMsg = "Risk Rule Name Already Used.";
            }
            request.setAttribute("message", responseMsg);
            RequestDispatcher rd = request.getRequestDispatcher("/addNewRiskParameter.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        catch (Exception e)
        {
            request.setAttribute("message", e.getMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/addNewRiskParameter.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
    }
}
