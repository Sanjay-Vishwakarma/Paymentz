import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.MerchantMonitoringManager;
import com.manager.TerminalManager;
import com.manager.vo.TerminalVO;
import com.manager.vo.merchantmonitoring.MonitoringParameterMappingVO;
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
 * Created by Vishal on 5/12/2016.
 */
public class MonitoringParameterMapping extends HttpServlet
{
    Logger logger = new Logger(MonitoringParameterMapping.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        logger.error("Inside MonitoringParameterMapping Servlet");
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session)){
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        String EOL = "<BR>";
        StringBuilder sberror = new StringBuilder();
        Functions functions = new Functions();

        String monitoringParameterId = request.getParameter("monitoringParameterId");
        String memberId = request.getParameter("memberId");
        String terminalId = request.getParameter("terminalId");

        if(functions.isValueNull(memberId) && !functions.isValueNull(terminalId))
        {
            RequestDispatcher rd = request.getRequestDispatcher("/manageMonitoringParameter.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        if (!ESAPI.validator().isValidInput("monitoringParameterId",request.getParameter("monitoringParameterId"),"Description",11,false))
        {
            sberror.append("Invalid Risk Rule Name"+EOL);
        }
        if (!ESAPI.validator().isValidInput("memberId", request.getParameter("memberId"),"Description",11,false))
        {
            sberror.append("Invalid Member Id"+EOL);
        }
        if (!ESAPI.validator().isValidInput("terminalId", request.getParameter("terminalId"),"Description",11,false))
        {
            sberror.append("Invalid Terminal Id"+EOL);
        }
        if (!ESAPI.validator().isValidInput("alertMessage",request.getParameter("alertMessage"),"Description",255,false))
        {
            sberror.append("Invalid Alert Message"+EOL);
        }
        if(request.getParameter("isdailyexecution")==null && request.getParameter("isweeklyexecution")==null && request.getParameter("ismonthlyexecution")==null)
        {
            sberror.append("Invalid Frequency, Please select at least one frequency"+EOL);
        }

        MerchantMonitoringManager merchantMonitoringManager = new MerchantMonitoringManager();
        MonitoringParameterVO monitoringParameterVO = null;
        String alertThreshold = null;
        String suspensionThreshold = null;
        String weeklyAlertThreshold = null;
        String weeklySuspensionThreshold = null;
        String monthlyAlertThreshold = null;
        String monthlySuspensionThreshold = null;
        try
        {
            monitoringParameterVO = merchantMonitoringManager.getMonitoringParameterDetails(monitoringParameterId);
            if (monitoringParameterVO == null)
            {
                request.setAttribute("message", "Risk Rule Not Found");
                RequestDispatcher rd = request.getRequestDispatcher("/manageMonitoringParameter.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
        }
        catch (Exception e)
        {
            request.setAttribute("message", e.getMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/manageMonitoringParameter.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }

        if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()) || MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()))
        {
            if("Y".equals(request.getParameter("isdailyexecution")))
            {
                if (!ESAPI.validator().isValidInput("alertThreshold", request.getParameter("alertThreshold"), "AmountMinus", 20, false))
                {
                    sberror.append("Invalid Daily Alert Threshold-[Sample Format:00.00]" + EOL);
                }
                else
                {
                    alertThreshold = request.getParameter("alertThreshold");
                }

                if (!ESAPI.validator().isValidInput("suspensionThreshold", request.getParameter("suspensionThreshold"), "AmountMinus", 20, false))
                {
                    sberror.append("Invalid Daily Suspension Threshold-[Sample Format:00.00]" + EOL);
                }
                else
                {
                    suspensionThreshold = request.getParameter("suspensionThreshold");
                }
            }
            if("Y".equals(request.getParameter("isweeklyexecution")))
            {
                if(!ESAPI.validator().isValidInput("weeklyAlertThreshold", request.getParameter("weeklyAlertThreshold"), "AmountMinus", 20, false))
                {
                    sberror.append("Invalid Weekly Alert Threshold [Sample Format:00.00" + EOL);
                }
                else
                {
                    weeklyAlertThreshold = request.getParameter("weeklyAlertThreshold");
                }

                if(!ESAPI.validator().isValidInput("weeklySuspensionThreshold", request.getParameter("weeklySuspensionThreshold"), "AmountMinus", 20, false))
                {
                    sberror.append("Invalid Weekly Suspension Threshold [Sample Format:00.00]" + EOL);
                }
                else
                {
                    weeklySuspensionThreshold = request.getParameter("weeklySuspensionThreshold");
                }
            }
            if("Y".equals(request.getParameter("ismonthlyexecution")))
            {
                if(!ESAPI.validator().isValidInput("monthlyAlertThreshold", request.getParameter("monthlyAlertThreshold"), "AmountMinus", 20, false))
                {
                    sberror.append("Invalid Monthly Alert Threshold [Sample Format:00.00" + EOL);
                }
                else
                {
                    monthlyAlertThreshold = request.getParameter("monthlyAlertThreshold");
                }

                if(!ESAPI.validator().isValidInput("monthlySuspensionThreshold", request.getParameter("monthlySuspensionThreshold"), "AmountMinus", 20, false))
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
            if("Y".equals(request.getParameter("isdailyexecution")))
            {
                if (!ESAPI.validator().isValidInput("alertThreshold", request.getParameter("alertThreshold"), "OnlyNumber", 20, false))
                {
                    sberror.append("Invalid Daily Alert Threshold-[Sample Format:10)" + EOL);
                }
                else
                {
                    alertThreshold = request.getParameter("alertThreshold");
                }

                if (!ESAPI.validator().isValidInput("suspensionThreshold", request.getParameter("suspensionThreshold"), "OnlyNumber", 20, false))
                {
                    sberror.append("Invalid Daily Suspension Threshold-[Sample Format:10]" + EOL);
                }
                else
                {
                    suspensionThreshold = request.getParameter("suspensionThreshold");
                }
            }
            if("Y".equals(request.getParameter("isweeklyexecution")))
            {
                if(!ESAPI.validator().isValidInput("weeklyAlertThreshold", request.getParameter("weeklyAlertThreshold"), "OnlyNumber", 20, false))
                {
                    sberror.append("Invalid Weekly Alert Threshold [Sample Format:10)"+ EOL);
                }
                else
                {
                    weeklyAlertThreshold = request.getParameter("weeklyAlertThreshold");
                }

                if(!ESAPI.validator().isValidInput("weeklySuspensionThreshold", request.getParameter("weeklySuspensionThreshold"), "OnlyNumber", 20, false))
                {
                    sberror.append("Invalid Weekly Suspension Threshold [Sample Format:10)" + EOL);
                }
                else
                {
                    weeklySuspensionThreshold = request.getParameter("weeklySuspensionThreshold");
                }
            }
            if("Y".equals(request.getParameter("ismonthlyexecution")))
            {
                if(!ESAPI.validator().isValidInput("monthlyAlertThreshold", request.getParameter("monthlyAlertThreshold"), "OnlyNumber", 20, false))
                {
                    sberror.append("Invalid Monthly Alert Threshold [Sample Format:10)" + EOL);
                }
                else
                {
                    monthlyAlertThreshold = request.getParameter("monthlyAlertThreshold");
                }

                if(!ESAPI.validator().isValidInput("monthlySuspensionThreshold", request.getParameter("monthlySuspensionThreshold"), "OnlyNumber", 20, false))
                {
                    sberror.append("Invalid Monthly Suspension Threshold [Sample Format:10]" + EOL);
                }
                else
                {
                    monthlySuspensionThreshold = request.getParameter("monthlySuspensionThreshold");
                }
            }
        }

        double alertThresholdDbl = 0.00;
        double suspensionThresholdDbl = 0.00;
        double weeklyAlertThresholdDbl = 0.00;
        double weeklySuspensionThresholdDbl = 0.00;
        double monthlyAlertThresholdDbl = 0.00;
        double monthlySuspensionThresholdDbl = 0.00;

        if (functions.isValueNull(alertThreshold))
        {
            alertThresholdDbl = Double.valueOf(alertThreshold);
        }
        if (functions.isValueNull(suspensionThreshold))
        {
            suspensionThresholdDbl = Double.valueOf(suspensionThreshold);
        }
        if(functions.isValueNull(weeklyAlertThreshold))
        {
            weeklyAlertThresholdDbl = Double.valueOf(weeklyAlertThreshold);
        }
        if(functions.isValueNull(weeklySuspensionThreshold))
        {
            weeklySuspensionThresholdDbl = Double.valueOf(weeklySuspensionThreshold);
        }
        if(functions.isValueNull(monthlyAlertThreshold))
        {
            monthlyAlertThresholdDbl = Double.valueOf(monthlyAlertThreshold);
        }
        if(functions.isValueNull(monthlySuspensionThreshold))
        {
            monthlySuspensionThresholdDbl = Double.valueOf(monthlySuspensionThreshold);
        }

        if(alertThresholdDbl > suspensionThresholdDbl)
        {
            sberror.append("Invalid Daily Alert Threshold (Daily Alert Threshold should not be greater than Daily Suspension Threshold)" + EOL);
        }
        if(weeklyAlertThresholdDbl > weeklySuspensionThresholdDbl)
        {
            sberror.append("Invalid Weekly Alert Threshold (Weekly Alert Threshold should not be greater than Weekly Suspension Threshold)" + EOL) ;
        }
        if(monthlyAlertThresholdDbl > monthlySuspensionThresholdDbl)
        {
            sberror.append("Invalid Monthly Alert Threshold (Monthly Alert Threshold should not be greater than Monthly Suspension Threshold)" + EOL);
        }

        if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
        {
            if(alertThresholdDbl > weeklyAlertThresholdDbl)
            {
                sberror.append("Invalid Daily Alert Threshold (Daily Alert Threshold should not be greater Weekly Alert Threshold)" + EOL) ;
            }
            if(suspensionThresholdDbl > weeklySuspensionThresholdDbl)
            {
                sberror.append("Invalid Daily Suspension Threshold (Daily Suspension Threshold should not be greater Weekly Suspension Threshold)" + EOL) ;
            }
            if(alertThresholdDbl > monthlyAlertThresholdDbl)
            {
                sberror.append("Invalid Daily Alert Threshold (Daily Alert Threshold should not be greater Monthly Alert Threshold)" + EOL) ;
            }
            if(suspensionThresholdDbl > monthlySuspensionThresholdDbl)
            {
                sberror.append("Invalid Daily Suspension Threshold (Daily Suspension Threshold should not be greater Monthly Suspension Threshold)" + EOL) ;
            }
            if(weeklyAlertThresholdDbl > monthlyAlertThresholdDbl)
            {
                sberror.append("Invalid Weekly Alert Threshold (Weekly Alert Threshold should not be greater Monthly Alert Threshold)" + EOL) ;
            }
            if(weeklySuspensionThresholdDbl > monthlySuspensionThresholdDbl)
            {
                sberror.append("Invalid Weekly Suspension Threshold (Weekly Suspension Threshold should not be greater Monthly Suspension Threshold)" + EOL) ;
            }
        }

        if (sberror.length() > 0)
        {
            request.setAttribute("message", sberror.toString());
            RequestDispatcher rd = request.getRequestDispatcher("/manageMonitoringParameter.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }

        TerminalManager terminalManager = new TerminalManager();
        TerminalVO terminalVO=null;
        try
        {
            terminalVO = terminalManager.getMemberTerminalfromMemberAndTerminal(memberId,terminalId,"");
            if(terminalVO == null)
            {
                request.setAttribute("message","Member Terminal Configuration Not Found");
                RequestDispatcher rd=request.getRequestDispatcher("/manageMonitoringParameter.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request,response);
                return;
            }
        }
        catch (Exception e)
        {
            logger.error("Exception:::::::"+e);
            request.setAttribute("message","Internal Error while Processing Your Request");
            RequestDispatcher rd=request.getRequestDispatcher("/manageMonitoringParameter.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request,response);
            return;
        }

        String isAlertToAdmin = request.getParameter("isAlertToAdmin");
        if(!functions.isValueNull(isAlertToAdmin))
        {
            isAlertToAdmin="N";
        }

        String isAlertToAdminSales = request.getParameter("isAlertToAdminSales");
        if(!functions.isValueNull(isAlertToAdminSales))
        {
            isAlertToAdminSales="N";
        }

        String isAlertToAdminRF = request.getParameter("isAlertToAdminRF");
        if(!functions.isValueNull(isAlertToAdminRF))
        {
            isAlertToAdminRF="N";
        }

        String isAlertToAdminCB = request.getParameter("isAlertToAdminCB");
        if(!functions.isValueNull(isAlertToAdminCB))
        {
            isAlertToAdminCB="N";
        }

        String isAlertToAdminFraud = request.getParameter("isAlertToAdminFraud");
        if(!functions.isValueNull(isAlertToAdminFraud))
        {
            isAlertToAdminFraud="N";
        }

        String isAlertToAdminTech = request.getParameter("isAlertToAdminTech");
        if(!functions.isValueNull(isAlertToAdminTech))
        {
            isAlertToAdminTech="N";
        }

        String isAlertToMerchant = request.getParameter("isAlertToMerchant");
        if(!functions.isValueNull(isAlertToMerchant))
        {
            isAlertToMerchant="N";
        }

        String isAlertToMerchantSales = request.getParameter("isAlertToMerchantSales");
        if(!functions.isValueNull(isAlertToMerchantSales))
        {
            isAlertToMerchantSales="N";
        }

        String isAlertToMerchantRF = request.getParameter("isAlertToMerchantRF");
        if(!functions.isValueNull(isAlertToMerchantRF))
        {
            isAlertToMerchantRF="N";
        }

        String isAlertToMerchantCB = request.getParameter("isAlertToMerchantCB");
        if(!functions.isValueNull(isAlertToMerchantCB))
        {
            isAlertToMerchantCB="N";
        }

        String isAlertToMerchantFraud = request.getParameter("isAlertToMerchantFraud");
        if(!functions.isValueNull(isAlertToMerchantFraud))
        {
            isAlertToMerchantFraud="N";
        }

        String isAlertToMerchantTech = request.getParameter("isAlertToMerchantTech");
        if(!functions.isValueNull(isAlertToMerchantTech))
        {
            isAlertToMerchantTech="N";
        }

        String isAlertToPartner = request.getParameter("isAlertToPartner");
        if(!functions.isValueNull(isAlertToPartner))
        {
            isAlertToPartner="N";
        }

        String isAlertToPartnerSales = request.getParameter("isAlertToPartnerSales");
        if(!functions.isValueNull(isAlertToPartnerSales))
        {
            isAlertToPartnerSales="N";
        }

        String isAlertToPartnerRF = request.getParameter("isAlertToPartnerRF");
        if(!functions.isValueNull(isAlertToPartnerRF))
        {
            isAlertToPartnerRF="N";
        }

        String isAlertToPartnerCB = request.getParameter("isAlertToPartnerCB");
        if(!functions.isValueNull(isAlertToPartnerCB))
        {
            isAlertToPartnerCB="N";
        }

        String isAlertToPartnerFraud = request.getParameter("isAlertToPartnerFraud");
        if(!functions.isValueNull(isAlertToPartnerFraud))
        {
            isAlertToPartnerFraud="N";
        }

        String isAlertToPartnerTech = request.getParameter("isAlertToPartnerTech");
        if(!functions.isValueNull(isAlertToPartnerTech))
        {
            isAlertToPartnerTech="N";
        }

        String isAlertToAgent = request.getParameter("isAlertToAgent");
        if(!functions.isValueNull(isAlertToAgent))
        {
            isAlertToAgent="N";
        }

        String isAlertToAgentSales = request.getParameter("isAlertToAgentSales");
        if(!functions.isValueNull(isAlertToAgentSales))
        {
            isAlertToAgentSales="N";
        }

        String isAlertToAgentCB = request.getParameter("isAlertToAgentCB");
        if(!functions.isValueNull(isAlertToAgentCB))
        {
            isAlertToAgentCB="N";
        }

        String isAlertToAgentRF = request.getParameter("isAlertToAgentRF");
        if(!functions.isValueNull(isAlertToAgentRF))
        {
            isAlertToAgentRF="N";
        }

        String isAlertToAgentFraud = request.getParameter("isAlertToAgentFraud");
        if(!functions.isValueNull(isAlertToAgentFraud))
        {
            isAlertToAgentFraud="N";
        }

        String isAlertToAgentTech = request.getParameter("isAlertToAgentTech");
        if(!functions.isValueNull(isAlertToAgentTech))
        {
            isAlertToAgentTech="N";
        }

        String alertActivation = request.getParameter("alertActivation");
        if(!functions.isValueNull(alertActivation))
        {
            alertActivation="N";
        }

        String suspensionActivation = request.getParameter("suspensionActivation");
        if(!functions.isValueNull(suspensionActivation))
        {
            suspensionActivation="N";
        }

        String alertMessage = request.getParameter("alertMessage");

        String isdailyexecution = request.getParameter("isdailyexecution");
        if(!functions.isValueNull(isdailyexecution))
        {
            isdailyexecution = "N";
        }
        String isweeklyexecution = request.getParameter("isweeklyexecution");
        if(!functions.isValueNull(isweeklyexecution))
        {
            isweeklyexecution = "N";
        }
        String ismonthlyexecution = request.getParameter("ismonthlyexecution");
        if(!functions.isValueNull(ismonthlyexecution))
        {
            ismonthlyexecution = "N";
        }

        MonitoringParameterMappingVO monitoringParameterMappingVO = new MonitoringParameterMappingVO();
        monitoringParameterMappingVO.setMonitoringParameterId(Integer.parseInt(monitoringParameterId));
        monitoringParameterMappingVO.setMemberId(Integer.parseInt(memberId));
        monitoringParameterMappingVO.setTerminalId(Integer.parseInt(terminalId));
        monitoringParameterMappingVO.setAlertThreshold(alertThresholdDbl);
        monitoringParameterMappingVO.setSuspensionThreshold(suspensionThresholdDbl);
        monitoringParameterMappingVO.setIsAlertToAdmin(isAlertToAdmin);
        monitoringParameterMappingVO.setIsAlertToMerchant(isAlertToMerchant);
        monitoringParameterMappingVO.setIsAlertToPartner(isAlertToPartner);
        monitoringParameterMappingVO.setIsAlertToAgent(isAlertToAgent);
        monitoringParameterMappingVO.setAlertActivation(alertActivation);
        monitoringParameterMappingVO.setSuspensionActivation(suspensionActivation);
        monitoringParameterMappingVO.setAlertMessage(alertMessage);

        monitoringParameterMappingVO.setIsAlertToAdminSales(isAlertToAdminSales);
        monitoringParameterMappingVO.setIsAlertToAdminRF(isAlertToAdminRF);
        monitoringParameterMappingVO.setIsAlertToAdminCB(isAlertToAdminCB);
        monitoringParameterMappingVO.setIsAlertToAdminFraud(isAlertToAdminFraud);
        monitoringParameterMappingVO.setIsAlertToAdminTech(isAlertToAdminTech);

        monitoringParameterMappingVO.setIsAlertToMerchantSales(isAlertToMerchantSales);
        monitoringParameterMappingVO.setIsAlertToMerchantRF(isAlertToMerchantRF);
        monitoringParameterMappingVO.setIsAlertToMerchantCB(isAlertToMerchantCB);
        monitoringParameterMappingVO.setIsAlertToMerchantFraud(isAlertToMerchantFraud);
        monitoringParameterMappingVO.setIsAlertToMerchantTech(isAlertToMerchantTech);

        monitoringParameterMappingVO.setIsAlertToPartnerSales(isAlertToPartnerSales);
        monitoringParameterMappingVO.setIsAlertToPartnerRF(isAlertToPartnerRF);
        monitoringParameterMappingVO.setIsAlertToPartnerCB(isAlertToPartnerCB);
        monitoringParameterMappingVO.setIsAlertToPartnerFraud(isAlertToPartnerFraud);
        monitoringParameterMappingVO.setIsAlertToPartnerTech(isAlertToPartnerTech);

        monitoringParameterMappingVO.setIsAlertToAgentSales(isAlertToAgentSales);
        monitoringParameterMappingVO.setIsAlertToAgentRF(isAlertToAgentRF);
        monitoringParameterMappingVO.setIsAlertToAgentCB(isAlertToAgentCB);
        monitoringParameterMappingVO.setIsAlertToAgentFraud(isAlertToAgentFraud);
        monitoringParameterMappingVO.setIsAlertToAgentTech(isAlertToAgentTech);

        monitoringParameterMappingVO.setIsDailyExecution(isdailyexecution);
        monitoringParameterMappingVO.setIsWeeklyExecution(isweeklyexecution);
        monitoringParameterMappingVO.setIsMonthlyExecution(ismonthlyexecution);

        monitoringParameterMappingVO.setWeeklyAlertThreshold(weeklyAlertThresholdDbl);
        monitoringParameterMappingVO.setWeeklySuspensionThreshold(weeklySuspensionThresholdDbl);
        monitoringParameterMappingVO.setMonthlyAlertThreshold(monthlyAlertThresholdDbl);
        monitoringParameterMappingVO.setMonthlySuspensionThreshold(monthlySuspensionThresholdDbl);

        String responseMsg="";
        try
        {
            boolean isMappingAvailable=merchantMonitoringManager.isMappingAvailable(memberId,terminalId,monitoringParameterId);
            if(isMappingAvailable)
            {
                responseMsg="Risk Rule Already Mapped On Account";
            }
            else
            {
                String status=merchantMonitoringManager.monitoringParameterMapping(monitoringParameterMappingVO);
                if("success".equalsIgnoreCase(status))
                {
                    responseMsg="Risk Rule Mapped Successfully On Account";
                }
                else
                {
                    responseMsg="Risk Rule Mapping Failed On Account";
                }
            }
            request.setAttribute("message",responseMsg);
            RequestDispatcher rd=request.getRequestDispatcher("/manageMonitoringParameter.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request,response);
            return;
        }
        catch (Exception e)
        {
            request.setAttribute("message",e.getMessage());
            RequestDispatcher rd=request.getRequestDispatcher("/manageMonitoringParameter.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request,response);
            return;
        }
    }
}
