package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.manager.MerchantMonitoringManager;
import com.manager.TerminalManager;
import com.manager.vo.TerminalVO;
import com.manager.vo.merchantmonitoring.MonitoringParameterMappingVO;
import com.manager.vo.merchantmonitoring.MonitoringParameterVO;
import com.manager.vo.merchantmonitoring.enums.*;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Created by Vishal on 3/2/2017.
 */
public class PartnerMonitoringParameterMapping extends HttpServlet
{
    Logger logger = new Logger(PartnerMonitoringParameterMapping.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }
        RequestDispatcher rd = request.getRequestDispatcher("/partnerManageMonitoringParameter.jsp?ctoken=" + user.getCSRFToken());
        String EOL = "<BR>";
        StringBuilder sberror = new StringBuilder();
        Functions functions = new Functions();
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String PartnerMonitoringParameterMapping_PartnerId_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_PartnerId_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_PartnerId_errormsg") : "Invalid Partner Id";
        String PartnerMonitoringParameterMapping_MemberId_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_MemberId_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_MemberId_errormsg") : "Invalid Member Id";
        String PartnerMonitoringParameterMapping_Member_mapping_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_Member_mapping_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_Member_mapping_errormsg") : "Invalid partner member mapping.";
        String PartnerMonitoringParameterMapping_partner_member_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_partner_member_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_partner_member_errormsg") : "Invalid partner member mapping.";
        String PartnerMonitoringParameterMapping_partner_mapping_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_partner_mapping_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_partner_mapping_errormsg") : "Invalid partner mapping.";
        String PartnerMonitoringParameterMapping_terminalId_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_terminalId_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_terminalId_errormsg") : "Invalid Terminal Id";
        String PartnerMonitoringParameterMapping_please_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_please_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_please_errormsg") : "Invalid Frequency, Please select at least one frequency";
        String PartnerMonitoringParameterMapping_risk_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_risk_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_risk_errormsg") : "Risk Rule Not Found";
        String PartnerMonitoringParameterMapping_threshold_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_threshold_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_threshold_errormsg") : "Invalid Daily Alert Threshold-[Sample Format:00.00]";
        String PartnerMonitoringParameterMapping_suspension_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_suspension_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_suspension_errormsg") : "Invalid Daily Suspension Threshold-[Sample Format:00.00]";
        String PartnerMonitoringParameterMapping_alert_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_alert_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_alert_errormsg") : "Invalid Weekly Alert Threshold [Sample Format:00.00";
        String PartnerMonitoringParameterMapping_weeklysuspension_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_weeklysuspension_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_weeklysuspension_errormsg") : "Invalid Weekly Suspension Threshold [Sample Format:00.00]";
        String PartnerMonitoringParameterMapping_monthly_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_monthly_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_monthly_errormsg") : "Invalid Monthly Alert Threshold [Sample Format:00.00";
        String PartnerMonitoringParameterMapping_suspension1_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_suspension1_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_suspension1_errormsg") : "Invalid Monthly Suspension Threshold [Sample Format:00.00]";
        String PartnerMonitoringParameterMapping_threshold1_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_threshold1_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_threshold1_errormsg") : "Invalid Daily Alert Threshold-[Sample Format:10)";
        String PartnerMonitoringParameterMapping_suspension2_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_suspension2_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_suspension2_errormsg") : "Invalid Daily Suspension Threshold-[Sample Format:10]";
        String PartnerMonitoringParameterMapping_weekly_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_weekly_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_weekly_errormsg") : "Invalid Weekly Alert Threshold [Sample Format:10)";
        String PartnerMonitoringParameterMapping_suspension3_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_suspension3_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_suspension3_errormsg") : "Invalid Weekly Suspension Threshold [Sample Format:10)";
        String PartnerMonitoringParameterMapping_sample_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_sample_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_sample_errormsg") : "Invalid Monthly Alert Threshold [Sample Format:10)";
        String PartnerMonitoringParameterMapping_threshold10_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_threshold10_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_threshold10_errormsg") : "Invalid Monthly Suspension Threshold [Sample Format:10]";
        String PartnerMonitoringParameterMapping_configuration_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_configuration_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_configuration_errormsg") : "Member Terminal Configuration Not Found";
        String PartnerMonitoringParameterMapping_already_mapped_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_already_mapped_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_already_mapped_errormsg") : "Risk Rule Already Mapped On Account";
        String PartnerMonitoringParameterMapping_mapped_successfully_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_mapped_successfully_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_mapped_successfully_errormsg") : "Risk Rule Mapped Successfully On Account";
        String PartnerMonitoringParameterMapping_failed_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerMonitoringParameterMapping_failed_errormsg")) ? rb1.getString("PartnerMonitoringParameterMapping_failed_errormsg") : "Risk Rule Mapping Failed On Account";







        String monitoringParameterId = request.getParameter("monitoringParameterId");
        String memberId = request.getParameter("memberId");
        String terminalId = request.getParameter("terminalId");
        String partnerId = (String) session.getAttribute("merchantid");
        String pid = request.getParameter("pid");
        if (!ESAPI.validator().isValidInput("pid", request.getParameter("pid"), "Numbers", 11, true))
        {
            sberror.append (PartnerMonitoringParameterMapping_PartnerId_errormsg+EOL) ;
        }
        if (!ESAPI.validator().isValidInput("memberId", request.getParameter("memberId"),"Numbers",11,false))
        {
            sberror.append(PartnerMonitoringParameterMapping_MemberId_errormsg+EOL);

        }
        try
        {
            if(functions.isValueNull(request.getParameter("pid")) && partner.isPartnerSuperpartnerMapped(request.getParameter("pid"), partnerId))
            {
                if (!partner.isPartnerMemberMapped(request.getParameter("memberId"), request.getParameter("pid")))
                {

                    sberror.append(PartnerMonitoringParameterMapping_Member_mapping_errormsg+EOL);
                }
            }
            else if(!functions.isValueNull(request.getParameter("pid"))){

                if ( !partner.isPartnerSuperpartnerMembersMapped(request.getParameter("memberId"), partnerId))
                {
                    sberror.append(PartnerMonitoringParameterMapping_partner_member_errormsg+EOL);
                }
            }
            else{
                sberror.append(PartnerMonitoringParameterMapping_partner_mapping_errormsg+EOL);
            }
        }catch(Exception e)
        {
            logger.error("Exception---" + e);
        }

        if (!ESAPI.validator().isValidInput("terminalId", request.getParameter("terminalId"),"Numbers",11,false))
        {
            sberror.append(PartnerMonitoringParameterMapping_terminalId_errormsg+EOL);
        }
        if (!ESAPI.validator().isValidInput("monitoringParameterId",request.getParameter("monitoringParameterId"),"Numbers",11,false))
        {
            sberror.append("Invalid Risk Rule Name"+EOL);
        }
        if(request.getParameter("isdailyexecution")==null && request.getParameter("isweeklyexecution")==null && request.getParameter("ismonthlyexecution")==null)
        {
            sberror.append(PartnerMonitoringParameterMapping_please_errormsg+EOL);
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
                request.setAttribute("error", PartnerMonitoringParameterMapping_risk_errormsg);
                rd.forward(request, response);
                return;
            }
        }
        catch (Exception e)
        {
            request.setAttribute("error", e.getMessage());
            rd.forward(request, response);
            return;
        }

        if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()) || MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()))
        {
            if("Y".equals(request.getParameter("isdailyexecution")))
            {
                if (!ESAPI.validator().isValidInput("alertThreshold", request.getParameter("alertThreshold"), "AmountMinus", 20, false))
                {
                    sberror.append(PartnerMonitoringParameterMapping_threshold_errormsg + EOL);
                }
                else
                {
                    alertThreshold = request.getParameter("alertThreshold");
                }

                if (!ESAPI.validator().isValidInput("suspensionThreshold", request.getParameter("suspensionThreshold"), "AmountMinus", 20, false))
                {
                    sberror.append(PartnerMonitoringParameterMapping_suspension_errormsg + EOL);
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
                    sberror.append(PartnerMonitoringParameterMapping_alert_errormsg + EOL);
                }
                else
                {
                    weeklyAlertThreshold = request.getParameter("weeklyAlertThreshold");
                }

                if(!ESAPI.validator().isValidInput("weeklySuspensionThreshold", request.getParameter("weeklySuspensionThreshold"), "AmountMinus", 20, false))
                {
                    sberror.append(PartnerMonitoringParameterMapping_weeklysuspension_errormsg + EOL);
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
                    sberror.append(PartnerMonitoringParameterMapping_monthly_errormsg + EOL);
                }
                else
                {
                    monthlyAlertThreshold = request.getParameter("monthlyAlertThreshold");
                }

                if(!ESAPI.validator().isValidInput("monthlySuspensionThreshold", request.getParameter("monthlySuspensionThreshold"), "AmountMinus", 20, false))
                {
                    sberror.append(PartnerMonitoringParameterMapping_suspension1_errormsg + EOL);
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
                    sberror.append(PartnerMonitoringParameterMapping_threshold1_errormsg + EOL);
                }
                else
                {
                    alertThreshold = request.getParameter("alertThreshold");
                }

                if (!ESAPI.validator().isValidInput("suspensionThreshold", request.getParameter("suspensionThreshold"), "OnlyNumber", 20, false))
                {
                    sberror.append(PartnerMonitoringParameterMapping_suspension2_errormsg + EOL);
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
                    sberror.append(PartnerMonitoringParameterMapping_weekly_errormsg+ EOL);
                }
                else
                {
                    weeklyAlertThreshold = request.getParameter("weeklyAlertThreshold");
                }

                if(!ESAPI.validator().isValidInput("weeklySuspensionThreshold", request.getParameter("weeklySuspensionThreshold"), "OnlyNumber", 20, false))
                {
                    sberror.append(PartnerMonitoringParameterMapping_suspension3_errormsg + EOL);
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
                    sberror.append(PartnerMonitoringParameterMapping_sample_errormsg + EOL);
                }
                else
                {
                    monthlyAlertThreshold = request.getParameter("monthlyAlertThreshold");
                }

                if(!ESAPI.validator().isValidInput("monthlySuspensionThreshold", request.getParameter("monthlySuspensionThreshold"), "OnlyNumber", 20, false))
                {
                    sberror.append( PartnerMonitoringParameterMapping_threshold10_errormsg+ EOL);
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
            request.setAttribute("error", sberror.toString());
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
                request.setAttribute("error",PartnerMonitoringParameterMapping_configuration_errormsg);
                rd.forward(request,response);
                return;
            }
        }
        catch (Exception e)
        {
            logger.error("Exception:::::::"+e);
            request.setAttribute("message","Internal Error while Processing Your Request");
            rd.forward(request,response);
            return;
        }

        String isAlertToAdmin = monitoringParameterVO.getDefaultIsAlertToAdmin();
        String isAlertToAdminSales = monitoringParameterVO.getDefaultIsAlertToAdminSales();
        String isAlertToAdminRF = monitoringParameterVO.getDefaultIsAlertToAdminRF();
        String isAlertToAdminCB = monitoringParameterVO.getDefaultIsAlertToAdminCB();
        String isAlertToAdminFraud = monitoringParameterVO.getDefaultIsAlertToAdminFraud();
        String isAlertToAdminTech = monitoringParameterVO.getDefaultIsAlertToAdminTech();

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

        String isAlertToAgent = monitoringParameterVO.getDefaultIsAlertToAgent();
        String isAlertToAgentSales = monitoringParameterVO.getDefaultIsAlertToAgentSales();
        String isAlertToAgentCB = monitoringParameterVO.getDefaultIsAlertToAgentCB();
        String isAlertToAgentRF = monitoringParameterVO.getDefaultIsAlertToAgentRF();
        String isAlertToAgentFraud = monitoringParameterVO.getDefaultIsAlertToAgentFraud();
        String isAlertToAgentTech = monitoringParameterVO.getDefaultIsAlertToAgentTech();

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

        String alertMessage = monitoringParameterVO.getDefaultAlertMsg();

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
        String error="";
        try
        {
            boolean isMappingAvailable=merchantMonitoringManager.isMappingAvailable(memberId,terminalId,monitoringParameterId);
            if(isMappingAvailable)
            {
                error=PartnerMonitoringParameterMapping_already_mapped_errormsg;
            }
            else
            {
                String status=merchantMonitoringManager.monitoringParameterMapping(monitoringParameterMappingVO);
                 if("success".equalsIgnoreCase(status))
                {
                    responseMsg=PartnerMonitoringParameterMapping_mapped_successfully_errormsg;
                }
                else
                {
                    error=PartnerMonitoringParameterMapping_failed_errormsg;
                }
            }
             request.setAttribute("message",responseMsg);
            request.setAttribute("error",error);
            rd.forward(request,response);
            return;
        }
        catch (Exception e)
        {
            request.setAttribute("error",e.getMessage());
            rd.forward(request,response);
            return;
        }
    }
}

