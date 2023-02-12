import com.directi.pg.*;
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
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by admin on 4/2/2016.
 */
public class UpdateMonitoringParameterMapping extends HttpServlet
{
    private static Logger logger = new Logger(UpdateMonitoringParameterMapping.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String error = "";
        String success = "";
        Connection conn = null;
        PreparedStatement pstmnt=null;

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
            RequestDispatcher rd = req.getRequestDispatcher("/mappingMaster.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        StringBuffer sb = new StringBuffer();
        sb.append("<table width=\"1000%\" class=\"table table-striped table-bordered table-green dataTable\">");
        sb.append("<tr>");
        sb.append("<td valign=\"middle\" align=\"center\" class=\"th0\">Rule ID</td>");
        sb.append("<td valign=\"middle\" align=\"center\" class=\"th0\">Status</td>");
        sb.append("<td valign=\"middle\" align=\"center\" class=\"th0\">Description</td>");
        sb.append("</tr>");

        try
        {
            conn = Database.getConnection();
            for (String mappingId : mappingIds)
            {
                String alertActivation = req.getParameter("alertActiovation_" + mappingId);
                String alertThreshold = req.getParameter("alertThreshold_" + mappingId);
                String suspensionActivation = req.getParameter("suspensionActivation_" + mappingId);
                String suspensionThreshold = req.getParameter("suspensionThreshold_" + mappingId);
                String monitoringUnit = req.getParameter("monitoringUnit_" + mappingId);
                String ruleId = req.getParameter("monitoringParameterId_" + mappingId);
                String isAlertToAdmin = req.getParameter("isAlertToAdmin_" + mappingId);
                String isAlertToMerchant = req.getParameter("isAlertToMerchant_" + mappingId);
                String isAlertToPartner = req.getParameter("isAlertToPartner_" + mappingId);
                String isAlertToAgent = req.getParameter("isAlertToAgent_" + mappingId);

                if ("%".equals(monitoringUnit))
                {
                    if (!ESAPI.validator().isValidInput("alertThreshold", alertThreshold, "AmountStr", 20, false))
                    {
                        sb.append(getFormattedMsg(ruleId, "Failed", "Invalid alert threshold"));
                        continue;
                    }
                    if("Y".equals(suspensionActivation))
                    {
                        if (!ESAPI.validator().isValidInput("suspensionThreshold", suspensionThreshold, "AmountStr", 20, false))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid suspension threshold"));
                            continue;
                        }
                    }
                    else
                    {
                        if (!ESAPI.validator().isValidInput("suspensionThreshold", suspensionThreshold, "AmountStr", 20, true))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid suspension threshold"));
                            continue;
                        }
                    }
                }
                else
                {
                    if (!ESAPI.validator().isValidInput("alertThreshold", alertThreshold, "OnlyNumber", 20, false))
                    {
                        sb.append(getFormattedMsg(ruleId, "Failed", "Invalid alert threshold"));
                        continue;
                    }
                    if("Y".equals(suspensionActivation))
                    {
                        if (!ESAPI.validator().isValidInput("suspensionThreshold", suspensionThreshold, "OnlyNumber", 20, false))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid suspension threshold"));
                            continue;
                        }
                    }
                    else
                    {
                        if (!ESAPI.validator().isValidInput("suspensionThreshold", suspensionThreshold, "OnlyNumber", 20, true))
                        {
                            sb.append(getFormattedMsg(ruleId, "Failed", "Invalid suspension threshold"));
                            continue;
                        }
                    }
                }

                if (!functions.isValueNull(alertThreshold))
                {
                    alertThreshold = Functions.round(0.00, 2);
                }
                if (!functions.isValueNull(suspensionThreshold))
                {
                    suspensionThreshold = Functions.round(0.00, 2);
                }

                String query = "update member_account_monitoringpara_mapping set alert_activation=?,alert_threshold=?,suspension_activation=?,suspension_threshold=?,isalerttoadmin=?,isalerttomerchant=?,isalerttopartner=?,isalerttoagent=? where mappingid=? and memberid=? and terminalid=?";
                pstmnt = conn.prepareStatement(query);
                pstmnt.setString(1, alertActivation);
                pstmnt.setString(2, alertThreshold);
                pstmnt.setString(3, suspensionActivation);
                pstmnt.setString(4, suspensionThreshold);
                pstmnt.setString(5, isAlertToAdmin);
                pstmnt.setString(6, isAlertToMerchant);
                pstmnt.setString(7, isAlertToPartner);
                pstmnt.setString(8, isAlertToAgent);
                pstmnt.setString(9, mappingId);
                pstmnt.setString(10, memberId);
                pstmnt.setString(11, terminalId);
                int k = pstmnt.executeUpdate();
                if (k > 0)
                {
                    sb.append(getFormattedMsg(ruleId, "Success", "Successfully updated"));
                }
                else
                {
                    success = "Could Not Update Monitoring Parameter Mapping. ";
                    sb.append(getFormattedMsg(ruleId, "Failed", "Could not be update"));
                }
            }
        }
        catch (SystemError e)
        {
            logger.error("SystemError while Monitoring Parameter Mapping Update", e);
            error = error + "Could Not Update Monitoring Parameter Mapping.";
            sb.append(getFormattedMsg("None", "Failed", error));
        }
        catch (SQLException e)
        {
            logger.error("SQLException while updating Monitoring Parameter Mapping ", e);
            error = error + "Could Not Update Monitoring Parameter Mapping.";
            sb.append(getFormattedMsg("None", "Failed", error));
        }
        finally
        {
            Database.closePreparedStatement(pstmnt);
            Database.closeConnection(conn);
        }
        req.setAttribute("success1", success);
        req.setAttribute("error1", error);
        req.setAttribute("updatemsg", sb.toString());
        RequestDispatcher rd = req.getRequestDispatcher("/mappingMaster.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req, res);
    }

    public String getFormattedMsg(String ruleId, String status, String statusMsg)
    {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<tr>");
        stringBuffer.append("<td align=\"center\">" + ruleId + "</td>");
        stringBuffer.append("<td align=\"center\">" + status + "</td>");
        stringBuffer.append("<td align=\"center\">" + statusMsg + "</td>");
        stringBuffer.append("</tr>");
        return stringBuffer.toString();
    }
}
