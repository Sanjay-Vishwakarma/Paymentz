import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.dao.MerchantMonitoringDAO;
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
 * Created by supriya on 5/19/2016.
 */
public class UpdateMonitoringParameterMaster extends HttpServlet
{
    private static Logger logger=new Logger(UpdateMonitoringParameterMaster.class.getName());
    private static Functions functions=new Functions();

    public void doGet(HttpServletRequest req, HttpServletResponse res)throws IOException, ServletException
    {
        doPost(req,res);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res)throws IOException, ServletException
    {
        HttpSession session=Functions.getNewSession(req);
        User user=(User)session.getAttribute("ESAPIUserSessionKey");
        if(!Admin.isLoggedIn(session))
        {
            logger.debug("Admin islogout");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String status="failed";
        String statusDescription="";
        RequestDispatcher rdError=req.getRequestDispatcher("/updateMonitoringParameterDetails.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess=req.getRequestDispatcher("/updateMonitoringParameterDetails.jsp?MES=success&ctoken="+user.getCSRFToken());
        RequestDispatcher rdUpdatingSuccess=req.getRequestDispatcher("/monitoringParameterMaster.jsp?MES=success&ctoken="+user.getCSRFToken());

        String EOL = "<BR>";
        StringBuilder sberror=new StringBuilder();

        MerchantMonitoringDAO merchantMonitoringDAO=new MerchantMonitoringDAO();
        if(functions.isValueNull(req.getParameter("modify")))
        {
            try
            {
                MonitoringParameterVO monitoringParameterVO=null;
                String monitoing_para_id=req.getParameter("modify");
                monitoringParameterVO=merchantMonitoringDAO.getMonitoringParameterDetails(monitoing_para_id);
                req.setAttribute("monitoringParameterVO", monitoringParameterVO);
                rdSuccess.forward(req,res);
            }
            catch (Exception e)
            {
                logger.error(e);
            }
        }

        else if(functions.isValueNull(req.getParameter("update")))
        {
            logger.debug("set values:::");
            MonitoringParameterVO monitoringParameterVO=new MonitoringParameterVO();
            monitoringParameterVO.setMonitoringParameterId(Integer.parseInt(req.getParameter("monitoingParaId")));
            monitoringParameterVO.setMonitoringParameterName(req.getParameter("monitoingParaName"));
            monitoringParameterVO.setMonitoingParaTechName(req.getParameter("monitoingParaTechName"));
            monitoringParameterVO.setMonitoingDeviation(req.getParameter("monitoingDeviation"));
            monitoringParameterVO.setMonitoingCategory(req.getParameter("monitoingCategory"));
            monitoringParameterVO.setMonitoingKeyword(req.getParameter("monitoingKeyword"));
            monitoringParameterVO.setMonitoingSubKeyword(req.getParameter("monitoingSubKeyword"));
            monitoringParameterVO.setMonitoingAlertCategory(req.getParameter("monitoingAlertCategory"));
            monitoringParameterVO.setMonitoingOnChannel(req.getParameter("monitoingOnChannel"));
            monitoringParameterVO.setMonitoringUnit(req.getParameter("monitoringUnit"));
            monitoringParameterVO.setDefaultAlertMsg(req.getParameter("defaultAlertMessage"));

            monitoringParameterVO.setDefaultIsAlertToAdmin(req.getParameter("defaultIsAlertToAdminValue"));
            monitoringParameterVO.setDefaultIsAlertToAdminSales(req.getParameter("defaultIsAlertToAdminSalesValue"));
            monitoringParameterVO.setDefaultIsAlertToAdminRF(req.getParameter("defaultIsAlertToAdminRFValue"));
            monitoringParameterVO.setDefaultIsAlertToAdminCB(req.getParameter("defaultIsAlertToAdminCBValue"));
            monitoringParameterVO.setDefaultIsAlertToAdminFraud(req.getParameter("defaultIsAlertToAdminFraudValue"));
            monitoringParameterVO.setDefaultIsAlertToAdminTech(req.getParameter("defaultIsAlertToAdminTechValue"));

            monitoringParameterVO.setDefaultIsAlertToMerchant(req.getParameter("defaultIsAlertToMerchantValue"));
            monitoringParameterVO.setDefaultIsAlertToMerchantSales(req.getParameter("defaultIsAlertToMerchantSalesValue"));
            monitoringParameterVO.setDefaultIsAlertToMerchantRF(req.getParameter("defaultIsAlertToMerchantRFValue"));
            monitoringParameterVO.setDefaultIsAlertToMerchantCB(req.getParameter("defaultIsAlertToMerchantCBValue"));
            monitoringParameterVO.setDefaultIsAlertToMerchantFraud(req.getParameter("defaultIsAlertToMerchantFraudValue"));
            monitoringParameterVO.setDefaultIsAlertToMerchantTech(req.getParameter("defaultIsAlertToMerchantTechValue"));

            monitoringParameterVO.setDefaultIsAlertToPartner(req.getParameter("defaultIsAlertToPartnerValue"));
            monitoringParameterVO.setDefaultIsAlertToPartnerSales(req.getParameter("defaultIsAlertToPartnerSalesValue"));
            monitoringParameterVO.setDefaultIsAlertToPartnerRF(req.getParameter("defaultIsAlertToPartnerRFValue"));
            monitoringParameterVO.setDefaultIsAlertToPartnerCB(req.getParameter("defaultIsAlertToPartnerCBValue"));
            monitoringParameterVO.setDefaultIsAlertToPartnerFraud(req.getParameter("defaultIsAlertToPartnerFraudValue"));
            monitoringParameterVO.setDefaultIsAlertToPartnerTech(req.getParameter("defaultIsAlertToPartnerTechValue"));

            monitoringParameterVO.setDefaultIsAlertToAgent(req.getParameter("defaultIsAlertToAgentValue"));
            monitoringParameterVO.setDefaultIsAlertToAgentSales(req.getParameter("defaultIsAlertToAgentSalesValue"));
            monitoringParameterVO.setDefaultIsAlertToAgentRF(req.getParameter("defaultIsAlertToAgentRFValue"));
            monitoringParameterVO.setDefaultIsAlertToAgentCB(req.getParameter("defaultIsAlertToAgentCBValue"));
            monitoringParameterVO.setDefaultIsAlertToAgentFraud(req.getParameter("defaultIsAlertToAgentFraudValue"));
            monitoringParameterVO.setDefaultIsAlertToAgentTech(req.getParameter("defaultIsAlertToAgentTechValue"));

            monitoringParameterVO.setDefaultAlertActivation(req.getParameter("defaultAlertActivationValue"));
            monitoringParameterVO.setDefaultSuspensionActivation(req.getParameter("defaultSuspensionActivationValue"));

            monitoringParameterVO.setIsDailyExecution(req.getParameter("isdailyexecutionvalue"));
            monitoringParameterVO.setIsWeeklyExecution(req.getParameter("isweeklyexecutionvalue"));
            monitoringParameterVO.setIsMonthlyExecution(req.getParameter("ismonthlyexecutionvalue"));
            monitoringParameterVO.setDisplayChartType(req.getParameter("displayChartType"));

            String hiddenDefaultAlertThreshold = req.getParameter("hiddenDefaultAlertThreshold");
            String hiddenDefaultSuspensionThreshold = req.getParameter("hiddenDefaultSuspensionThreshold");
            String hiddenWeeklyAlertThreshold = req.getParameter("hiddenWeeklyAlertThreshold");
            String hiddenWeeklySuspensionThreshold = req.getParameter("hiddenWeeklySuspensionThreshold");
            String hiddenMonthlyAlertThreshold = req.getParameter("hiddenMonthlyAlertThreshold");
            String hiddenMonthlySuspensionThreshold = req.getParameter("hiddenMonthlySuspensionThreshold");

            String error=performMandatoryParameterValidation(monitoringParameterVO);

            if(error.length()>0)
            {
                sberror.append(error);
            }

            String defaultAlertThreshold="0.00";
            String defaultSuspensionThreshold="0.00";
            String weeklyAlertThreshold="0.00";
            String weeklySuspensionThreshold="0.00";
            String monthlyAlertThreshold="0.00";
            String monthlySuspensionThreshold="0.00";

            if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()) || MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()))
            {
                if ("Y".equals(req.getParameter("isdailyexecutionvalue")))
                {
                    if (!ESAPI.validator().isValidInput("defaultAlertThreshold", req.getParameter("defaultAlertThreshold"), "AmountMinus", 20, false))
                    {
                        sberror.append("Invalid Daily Alert Threshold-[Sample Format:00.00]" + EOL);
                    }
                    else
                    {
                        defaultAlertThreshold = req.getParameter("defaultAlertThreshold");
                    }

                    if (!ESAPI.validator().isValidInput("defaultSuspensionThreshold", req.getParameter("defaultSuspensionThreshold"), "AmountMinus", 20, false))
                    {
                        sberror.append("Invalid Daily Suspension Threshold-[Sample Format:00.00]" + EOL);
                    }
                    else
                    {
                        defaultSuspensionThreshold = req.getParameter("defaultSuspensionThreshold");
                    }
                }
                else
                {
                    if (!ESAPI.validator().isValidInput("defaultAlertThreshold", hiddenDefaultAlertThreshold, "AmountMinus", 20, true))
                    {
                        sberror.append("Invalid Daily Alert Threshold-[Sample Format:00.00]" + EOL);
                    }
                    else
                    {
                        defaultAlertThreshold = hiddenDefaultAlertThreshold;
                    }

                    if (!ESAPI.validator().isValidInput("defaultSuspensionThreshold", hiddenDefaultSuspensionThreshold, "AmountMinus", 20, true))
                    {
                        sberror.append("Invalid Daily Suspension Threshold-[Sample Format:00.00]" + EOL);
                    }
                    else
                    {
                        defaultSuspensionThreshold = hiddenDefaultSuspensionThreshold;
                    }
                }

                if ("Y".equals(req.getParameter("isweeklyexecutionvalue")))
                {
                    if (!ESAPI.validator().isValidInput("weeklyAlertThreshold", req.getParameter("weeklyAlertThreshold"), "AmountMinus", 20, false))
                    {
                        sberror.append("Invalid Weekly Alert Threshold [Sample Format:00.00]" + EOL);
                    }
                    else
                    {
                        weeklyAlertThreshold = req.getParameter("weeklyAlertThreshold");
                    }

                    if (!ESAPI.validator().isValidInput("weeklySuspensionThreshold", req.getParameter("weeklySuspensionThreshold"), "AmountMinus", 20, false))
                    {
                        sberror.append("Invalid Weekly Suspension Threshold [Sample Format:00.00]" + EOL);
                    }
                    else
                    {
                        weeklySuspensionThreshold = req.getParameter("weeklySuspensionThreshold");
                    }
                }
                else
                {
                    if (!ESAPI.validator().isValidInput("weeklyAlertThreshold", hiddenWeeklyAlertThreshold, "AmountMinus", 20, true))
                    {
                        sberror.append("Invalid Weekly Alert Threshold [Sample Format:00.00]" + EOL);
                    }
                    else
                    {
                        weeklyAlertThreshold = hiddenWeeklyAlertThreshold;
                    }

                    if (!ESAPI.validator().isValidInput("weeklySuspensionThreshold", hiddenWeeklySuspensionThreshold, "AmountMinus", 20, true))
                    {
                        sberror.append("Invalid Weekly Suspension Threshold [Sample Format:00.00]" + EOL);
                    }
                    else
                    {
                        weeklySuspensionThreshold = hiddenWeeklySuspensionThreshold;
                    }
                }

                if ("Y".equals(req.getParameter("ismonthlyexecutionvalue")))
                {
                    if (!ESAPI.validator().isValidInput("monthlyAlertThreshold", req.getParameter("monthlyAlertThreshold"), "AmountMinus", 20, false))
                    {
                        sberror.append("Invalid Monthly Alert Threshold [Sample Format:00.00]" + EOL);
                    }
                    else
                    {
                        monthlyAlertThreshold = req.getParameter("monthlyAlertThreshold");
                    }

                    if (!ESAPI.validator().isValidInput("monthlySuspensionThreshold", req.getParameter("monthlySuspensionThreshold"), "AmountMinus", 20, false))
                    {
                        sberror.append("Invalid Monthly Suspension Threshold [Sample Format:00.00]" + EOL);
                    }
                    else
                    {
                        monthlySuspensionThreshold = req.getParameter("monthlySuspensionThreshold");
                    }
                }
                else
                {
                    if (!ESAPI.validator().isValidInput("monthlyAlertThreshold", hiddenMonthlyAlertThreshold, "AmountMinus", 20, true))
                    {
                        sberror.append("Invalid Monthly Alert Threshold [Sample Format:00.00]" + EOL);
                    }
                    else
                    {
                        monthlyAlertThreshold = hiddenMonthlyAlertThreshold;
                    }

                    if (!ESAPI.validator().isValidInput("monthlySuspensionThreshold", hiddenMonthlySuspensionThreshold, "AmountMinus", 20, true))
                    {
                        sberror.append("Invalid Monthly Suspension Threshold [Sample Format:00.00]" + EOL);
                    }
                    else
                    {
                        monthlySuspensionThreshold = hiddenMonthlySuspensionThreshold;
                    }
                }
            }
            else
            {
                if ("Y".equals(req.getParameter("isdailyexecutionvalue")))
                {
                    if (!ESAPI.validator().isValidInput("defaultAlertThreshold", req.getParameter("defaultAlertThreshold"), "OnlyNumber", 20, false))
                    {
                        sberror.append("Invalid Daily Alert Threshold-[Sample Format:10]" + EOL);
                    }
                    else
                    {
                        defaultAlertThreshold = req.getParameter("defaultAlertThreshold");
                    }

                    if (!ESAPI.validator().isValidInput("defaultSuspensionThreshold", req.getParameter("defaultSuspensionThreshold"), "OnlyNumber", 20, false))
                    {
                        sberror.append("Invalid Daily Suspension Threshold-[Sample Format:10]" + EOL);
                    }
                    else
                    {
                        defaultSuspensionThreshold = req.getParameter("defaultSuspensionThreshold");
                    }
                }
                else
                {
                    if (!ESAPI.validator().isValidInput("defaultAlertThreshold", hiddenDefaultAlertThreshold, "OnlyNumber", 20, true))
                    {
                        sberror.append("Invalid Daily Alert Threshold-[Sample Format:10]" + EOL);
                    }
                    else
                    {
                        defaultAlertThreshold = hiddenDefaultAlertThreshold;
                    }

                    if (!ESAPI.validator().isValidInput("defaultSuspensionThreshold", hiddenDefaultSuspensionThreshold, "OnlyNumber", 20, true))
                    {
                        sberror.append("Invalid Daily Suspension Threshold-[Sample Format:10]" + EOL);
                    }
                    else
                    {
                        defaultSuspensionThreshold = hiddenDefaultSuspensionThreshold;
                    }
                }

                if ("Y".equals(req.getParameter("isweeklyexecutionvalue")))
                {
                    if (!ESAPI.validator().isValidInput("weeklyAlertThreshold", req.getParameter("weeklyAlertThreshold"), "OnlyNumber", 20, false))
                    {
                        sberror.append("Invalid Weekly Alert Threshold [Sample Format:10]" + EOL);
                    }
                    else
                    {
                        weeklyAlertThreshold = req.getParameter("weeklyAlertThreshold");
                    }

                    if (!ESAPI.validator().isValidInput("weeklySuspensionThreshold", req.getParameter("weeklySuspensionThreshold"), "OnlyNumber", 20, false))
                    {
                        sberror.append("Invalid Weekly Suspension Threshold [Sample Format:10]" + EOL);
                    }
                    else
                    {
                        weeklySuspensionThreshold = req.getParameter("weeklySuspensionThreshold");
                    }
                }
                else
                {
                    if (!ESAPI.validator().isValidInput("weeklyAlertThreshold", hiddenWeeklyAlertThreshold, "OnlyNumber", 20, true))
                    {
                        sberror.append("Invalid Weekly Alert Threshold [Sample Format:10]" + EOL);
                    }
                    else
                    {
                        weeklyAlertThreshold = hiddenWeeklyAlertThreshold;
                    }

                    if (!ESAPI.validator().isValidInput("weeklySuspensionThreshold", hiddenWeeklySuspensionThreshold, "OnlyNumber", 20, true))
                    {
                        sberror.append("Invalid Weekly Suspension Threshold [Sample Format:10]" + EOL);
                    }
                    else
                    {
                        weeklySuspensionThreshold = hiddenWeeklySuspensionThreshold;
                    }
                }

                if ("Y".equals(req.getParameter("ismonthlyexecutionvalue")))
                {
                    if (!ESAPI.validator().isValidInput("monthlyAlertThreshold", req.getParameter("monthlyAlertThreshold"), "OnlyNumber", 20, false))
                    {
                        sberror.append("Invalid Monthly Alert Threshold [Sample Format:10]" + EOL);
                    }
                    else
                    {
                        monthlyAlertThreshold = req.getParameter("monthlyAlertThreshold");
                    }

                    if (!ESAPI.validator().isValidInput("monthlySuspensionThreshold", req.getParameter("monthlySuspensionThreshold"), "OnlyNumber", 20, false))
                    {
                        sberror.append("Invalid Monthly Suspension Threshold [Sample Format:10]" + EOL);
                    }
                    else
                    {
                        monthlySuspensionThreshold = req.getParameter("monthlySuspensionThreshold");
                    }
                }
                else
                {
                    if (!ESAPI.validator().isValidInput("monthlyAlertThreshold", hiddenMonthlyAlertThreshold, "OnlyNumber", 20, true))
                    {
                        sberror.append("Invalid Monthly Alert Threshold [Sample Format:10]" + EOL);
                    }
                    else
                    {
                        monthlyAlertThreshold = hiddenMonthlyAlertThreshold;
                    }

                    if (!ESAPI.validator().isValidInput("monthlySuspensionThreshold", hiddenMonthlySuspensionThreshold, "OnlyNumber", 20, true))
                    {
                        sberror.append("Invalid Monthly Suspension Threshold [Sample Format:10]" + EOL);
                    }
                    else
                    {
                        monthlySuspensionThreshold = hiddenMonthlySuspensionThreshold;
                    }
                }
            }

            if("N".equals(monitoringParameterVO.getIsDailyExecution()) && "N".equals(monitoringParameterVO.getIsWeeklyExecution()) && "N".equals(monitoringParameterVO.getIsMonthlyExecution()))
            {
                sberror.append("Invalid Frequency,PLease select at least one frequency"+EOL);
            }

            if(Double.valueOf(defaultAlertThreshold) > Double.valueOf(defaultSuspensionThreshold))
            {
                sberror.append("Invalid Daily Alert Threshold (Daily Alert Threshold should not be greater than Daily Suspension Threshold)"+EOL);
            }
            if(Double.valueOf(weeklyAlertThreshold) > Double.valueOf(weeklySuspensionThreshold))
            {
                sberror.append("Invalid Weekly Alert Threshold (Weekly Alert Threshold should not be greater than Weekly Suspension Threshold)"+EOL);
            }
            if(Double.valueOf(monthlyAlertThreshold) > Double.valueOf(monthlySuspensionThreshold))
            {
                sberror.append("Invalid Monthly Alert Threshold (Monthly Alert Threshold should not be greater than Monthly Suspension Threshold)"+EOL);
            }

            if (MonitoringCategory.Success.toString().equals(monitoringParameterVO.getMonitoingCategory()) && MonitoringKeyword.Chargeback.toString().equals(monitoringParameterVO.getMonitoingKeyword()) && MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()) && MonitoringAlertType.Others.toString().equals(monitoringParameterVO.getMonitoingAlertCategory()) && MonitoringChannelLevel.All.toString().equals(monitoringParameterVO.getMonitoingOnChannel()))
            {
                if(Double.valueOf(defaultAlertThreshold) > Double.valueOf(weeklyAlertThreshold))
                {
                    sberror.append("Invalid Daily Alert Threshold (Daily Alert Threshold should not be greater than Weekly Alert Threshold)"+EOL);
                }
                if(Double.valueOf(defaultSuspensionThreshold) > Double.valueOf(weeklySuspensionThreshold))
                {
                    sberror.append("Invalid Daily Suspension Threshold (Daily Suspension Threshold should not be greater than Weekly Suspension Threshold)"+EOL);
                }
                if(Double.valueOf(defaultAlertThreshold) > Double.valueOf(monthlyAlertThreshold))
                {
                    sberror.append("Invalid Daily Alert Threshold (Daily Alert Threshold should not be greater than Monthly Alert Threshold)"+EOL);
                }
                if(Double.valueOf(defaultSuspensionThreshold) > Double.valueOf(monthlySuspensionThreshold))
                {
                    sberror.append("Invalid Daily Suspension Threshold (Daily Suspension Threshold should not be greater than Monthly Suspension Threshold)"+EOL);
                }
                if(Double.valueOf(weeklyAlertThreshold) > Double.valueOf(monthlyAlertThreshold))
                {
                    sberror.append("Invalid Weekly Alert Threshold (Weekly Alert Threshold should not be greater than Monthly Alert Threshold)"+EOL);
                }
                if(Double.valueOf(weeklySuspensionThreshold) > Double.valueOf(monthlySuspensionThreshold))
                {
                    sberror.append("Invalid Weekly Suspension Threshold (Weekly Suspension Threshold should not be greater than Monthly Suspension Threshold)"+EOL);
                }
            }

            if(functions.isValueNull(defaultAlertThreshold))
            {
                monitoringParameterVO.setDefaultAlertThreshold(Double.valueOf(defaultAlertThreshold));
            }
            else
            {
                monitoringParameterVO.setDefaultAlertThreshold(0.00);
            }
            if(functions.isValueNull(defaultSuspensionThreshold))
            {
                monitoringParameterVO.setDefaultSuspensionThreshold(Double.valueOf(defaultSuspensionThreshold));
            }
            else
            {
                monitoringParameterVO.setDefaultSuspensionThreshold(0.00);
            }
            if(functions.isValueNull(weeklyAlertThreshold))
            {
                monitoringParameterVO.setWeeklyAlertThreshold(Double.valueOf(weeklyAlertThreshold));
            }
            else
            {
                monitoringParameterVO.setWeeklyAlertThreshold(0.00);
            }
            if(functions.isValueNull(weeklySuspensionThreshold))
            {
                monitoringParameterVO.setWeeklySuspensionThreshold(Double.valueOf(weeklySuspensionThreshold));
            }
            else
            {
                monitoringParameterVO.setWeeklySuspensionThreshold(0.00);
            }
            if(functions.isValueNull(monthlyAlertThreshold))
            {
                monitoringParameterVO.setMonthlyAlertThreshold(Double.valueOf(monthlyAlertThreshold));
            }
            else
            {
                monitoringParameterVO.setMonthlyAlertThreshold(0.00);
            }
            if(functions.isValueNull(monthlySuspensionThreshold))
            {
                monitoringParameterVO.setMonthlySuspensionThreshold(Double.valueOf(monthlySuspensionThreshold));
            }
            else
            {
                monitoringParameterVO.setMonthlySuspensionThreshold(0.00);
            }

            if(sberror.length()>0)
            {
                req.setAttribute("monitoringParameterVO",monitoringParameterVO);
                req.setAttribute("error",sberror.toString());
                rdError.forward(req,res);
                return;
            }
            else
            {
                double defaultAlertThresholdDbl = 0.00;
                double defaultSuspensionThresholdDbl = 0.00;
                double weeklyAlertThresholdDbl = 0.00;
                double weeklySuspensionThresholdDbl = 0.00;
                double monthlyAlertThresholdDbl = 0.00;
                double monthlySuspensionThresholdDbl = 0.00;

                if(functions.isValueNull(defaultAlertThreshold))
                {
                    defaultAlertThresholdDbl = Double.valueOf(defaultAlertThreshold);
                }
                if(functions.isValueNull(defaultSuspensionThreshold))
                {
                    defaultSuspensionThresholdDbl = Double.valueOf(defaultSuspensionThreshold);
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

                monitoringParameterVO.setDefaultAlertThreshold(Double.valueOf(defaultAlertThresholdDbl));
                monitoringParameterVO.setDefaultSuspensionThreshold(Double.valueOf(defaultSuspensionThresholdDbl));
                monitoringParameterVO.setWeeklyAlertThreshold(weeklyAlertThresholdDbl);
                monitoringParameterVO.setWeeklySuspensionThreshold(weeklySuspensionThresholdDbl);
                monitoringParameterVO.setMonthlyAlertThreshold(monthlyAlertThresholdDbl);
                monitoringParameterVO.setMonthlySuspensionThreshold(monthlySuspensionThresholdDbl);

                try
                {
                    String orgName="";
                    orgName=req.getParameter("orgMonitoingParaName");
                    boolean isValidRequest=true;
                    if(!(orgName.equalsIgnoreCase(monitoringParameterVO.getMonitoringParameterName())))
                    {
                        boolean b=merchantMonitoringDAO.isParameterNameAvailable(monitoringParameterVO);
                        if(b)
                        {
                            isValidRequest=false;
                            status="failed";
                            statusDescription="Risk Rule Name Already Available.";
                        }
                    }
                    boolean b1=merchantMonitoringDAO.isParameterNameAvailableBasedOnId(monitoringParameterVO);
                    if(!b1)
                    {
                        boolean b3=merchantMonitoringDAO.isParameterAvailable(monitoringParameterVO);
                        if(b3)
                        {
                            isValidRequest=false;
                            status="failed";
                            statusDescription="Risk Rule Parameter Already Available.";
                        }
                    }
                    if(isValidRequest)
                    {
                        boolean isParameterUpdated=merchantMonitoringDAO.updateMonitoringParameter(monitoringParameterVO);
                        if(isParameterUpdated){
                            status="success";
                            statusDescription="Risk Rule Parameter Updated successfully";
                        }
                        else{
                            status="failed";
                            statusDescription="Risk Rule Parameter Updation Failed ";
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.error("Exception : ",e);
                    status="failed";
                    statusDescription=e.getMessage();
                }
                req.setAttribute("status",status);
                req.setAttribute("statusDescription", statusDescription);
                rdUpdatingSuccess.forward(req, res);
            }
        }
    }
    public String performMandatoryParameterValidation(MonitoringParameterVO monitoringParameterVO)
    {
        StringBuffer sb=new StringBuffer();
        if(!ESAPI.validator().isValidInput("monitoingParaName",monitoringParameterVO.getMonitoringParameterName(), "SafeString", 255,false) || functions.hasHTMLTags(monitoringParameterVO.getMonitoringParameterName()))
        {
            sb.append("Invalid Risk Rule Name<BR>");
        }
        if(!ESAPI.validator().isValidInput("monitoingParaTechName",monitoringParameterVO.getMonitoingParaTechName(), "SafeString", 255,false) || functions.hasHTMLTags(monitoringParameterVO.getMonitoingParaTechName()))
        {
            sb.append("Invalid Risk Rule Tech Name,");
        }
        if(!ESAPI.validator().isValidInput("monitoringUnit",monitoringParameterVO.getMonitoringUnit(), "SafeString", 50,false))
        {
            sb.append("Invalid Monitoring Unit,");
        }
        if(!ESAPI.validator().isValidInput("monitoingCategory",monitoringParameterVO.getMonitoingCategory(), "SafeString", 50,false))
        {
            sb.append("Invalid Monitoring Category,");
        }
        if(!ESAPI.validator().isValidInput("monitoingDeviation",monitoringParameterVO.getMonitoingDeviation(), "SafeString", 50,false))
        {
            sb.append("Invalid Monitoring Deviation,");
        }
        if(!ESAPI.validator().isValidInput("monitoingKeyword",monitoringParameterVO.getMonitoingKeyword(), "SafeString", 50,false))
        {
            sb.append("Invalid Monitoring Keyword,");
        }
        if(!ESAPI.validator().isValidInput("monitoingSubKeyword",monitoringParameterVO.getMonitoingSubKeyword(), "SafeString", 50,false))
        {
            sb.append("Invalid Monitoring Sub Keyword,");
        }
        if(!ESAPI.validator().isValidInput("monitoingAlertCategory",monitoringParameterVO.getMonitoingAlertCategory(), "SafeString", 50,false))
        {
            sb.append("Invalid Monitoring Alert Category,");
        }
        if(!ESAPI.validator().isValidInput("monitoingOnChannel",monitoringParameterVO.getMonitoingOnChannel(), "SafeString", 50,false))
        {
            sb.append("Invalid Monitoring channel,");
        }
        if(!ESAPI.validator().isValidInput("defaultAlertMessage",monitoringParameterVO.getDefaultAlertMsg(), "SafeString", 255,false) || functions.hasHTMLTags(monitoringParameterVO.getDefaultAlertMsg()))
        {
            sb.append("Invalid Alert Message,");
        }
        if(!ESAPI.validator().isValidInput("displayChartType", monitoringParameterVO.getDisplayChartType(), "SafeString", 255, false))
        {
            sb.append("Invalid Display Chart Type");
        }

        return sb.toString();
    }
}