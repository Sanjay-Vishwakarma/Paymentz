package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.ChargeManager;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.ChargeVO;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Roshan on 3/21/2018.
 */
public class UpdatePartnerAccountsCharges extends HttpServlet
{
    private static Logger log = new Logger(UpdatePartnerAccountsCharges.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner = new PartnerFunctions();
        Functions functions=new Functions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        Connection conn = null;
        String errorMsg = "";
        String EOL = "<BR>";

        ChargeManager chargeManager = new ChargeManager();
        String memberCharge = req.getParameter("memberchargeval");
        String agentCharge = req.getParameter("agentchargeval");
        String partnerCharge = req.getParameter("partnerchargeval");
        String startDate = req.getParameter("startdate");
        String endDate = req.getParameter("enddate");
        String mappingId = req.getParameter("mappingid");
        String lastUpdatedStartDate = req.getParameter("pstartdate");
        String lastUpdatedEndDate = req.getParameter("penddate");
        String version = req.getParameter("version");
        try
        {
            boolean flag = true;
            StringBuilder sbError = new StringBuilder();
            if (!ESAPI.validator().isValidInput("memberchargeval", req.getParameter("memberchargeval"), "AmountStr", 20, false))
            {
                sbError.append("Invalid Member Value" + EOL);
            }
            if (!ESAPI.validator().isValidInput("agentchargeval", req.getParameter("agentchargeval"), "AmountStr", 20, false))
            {
                sbError.append("Invalid Agent Value" + EOL);
            }
            if (!ESAPI.validator().isValidInput("partnerchargeval", req.getParameter("partnerchargeval"), "AmountStr", 20, false))
            {
                sbError.append("Invalid Partner Value" + EOL);
            }
            if (!ESAPI.validator().isValidInput("startdate", req.getParameter("startdate"), "fromDate", 20, false))
            {
                sbError.append("Invalid Start Date" + EOL);
            }
            if (!ESAPI.validator().isValidInput("enddate", req.getParameter("enddate"), "fromDate", 20, false))
            {
                sbError.append("Invalid End Date" + EOL);
            }
            if (sbError.length() > 0)
            {
                HashMap rsDetails = chargeManager.getChargeInfo(mappingId);
                req.setAttribute("action", "modify");
                req.setAttribute("chargedetails", rsDetails);
                req.setAttribute("errormessage", sbError.toString());
                RequestDispatcher rd = req.getRequestDispatcher("/actionPartnerAccountCharges.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            CommonFunctionUtil commonFunctionUtil=new CommonFunctionUtil();
            String message=commonFunctionUtil.newValidateDate(startDate,endDate,null,null);
            if(functions.isValueNull(message))
            {
                HashMap rsDetails = chargeManager.getChargeInfo(mappingId);
                req.setAttribute("action", "modify");
                req.setAttribute("chargedetails", rsDetails);
                req.setAttribute("errormessage", message);
                RequestDispatcher rd = req.getRequestDispatcher("/actionPartnerAccountCharges.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            if ("false".equals(version))
            {
                startDate = commonFunctionUtil.convertDatepickerToTimestamp(startDate, "00:00:00");
                endDate = commonFunctionUtil.convertDatepickerToTimestamp(endDate, "23:59:59");

                ChargeVO chargeVO = new ChargeVO();
                chargeVO.setChargevalue(memberCharge);
                chargeVO.setAgentChargeValue(agentCharge);
                chargeVO.setPartnerChargeValue(partnerCharge);
                chargeVO.setStartdate(startDate);
                chargeVO.setEnddate(endDate);
                chargeVO.setMappingid(mappingId);

                boolean result = chargeManager.updateMerchantCharge(chargeVO);
                if (result)
                {
                    errorMsg += "<center><font class=\"textb\" ><b>Charge updated successfully. " + EOL + "</b></font></center>";
                }
                else
                {
                    errorMsg += "<center><font class=\"textb\" ><b> Charge updating failed." + EOL + "</b></font></center>";
                }
            }
            else
            {
                lastUpdatedStartDate = commonFunctionUtil.convertTimestampToDatepicker(lastUpdatedStartDate);
                lastUpdatedEndDate = commonFunctionUtil.convertTimestampToDatepicker(lastUpdatedEndDate);
                String message1 = commonFunctionUtil.newValidateDate(startDate, endDate, lastUpdatedStartDate, lastUpdatedEndDate);
                if (message1 != null)
                {
                    req.setAttribute("successmessage", message1);
                    RequestDispatcher rd = req.getRequestDispatcher("/actionPartnerAccountCharges.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                PreparedStatement preparedStatement = null;
                ResultSet rs = null;
                try
                {
                    startDate = commonFunctionUtil.convertDatepickerToTimestamp(startDate, "00:00:00");
                    endDate = commonFunctionUtil.convertDatepickerToTimestamp(endDate, "23:59:59");
                    conn = Database.getConnection();
                    SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
                    Date newStartDate = sdf.parse(startDate);
                    Date dateBefore = new Date(newStartDate.getTime() - 1 * 24 * 3600 * 1000);
                    String q1 = "SELECT MAX(chargeversionId) AS chargeversionId FROM ChargeVersionMemberMaster WHERE member_accounts_charges_mapping_id=?";
                    preparedStatement = conn.prepareStatement(q1);
                    preparedStatement.setString(1, mappingId);
                    rs = preparedStatement.executeQuery();
                    if (rs.next())
                    {
                        String last_update_version = rs.getString("chargeversionId");
                        String updatememberhistory = "UPDATE ChargeVersionMemberMaster SET effectiveEndDate=? WHERE chargeversionId=? AND  member_accounts_charges_mapping_id=?";
                        preparedStatement = conn.prepareStatement(updatememberhistory);
                        preparedStatement.setString(1, sdf.format(dateBefore) + " 23:59:59");
                        preparedStatement.setString(2, last_update_version);
                        preparedStatement.setString(3, mappingId);
                        int i = preparedStatement.executeUpdate();
                        if (i == 1)
                        {
                            String q2 = "UPDATE member_accounts_charges_mapping SET chargevalue=?,agentchargevalue=?,partnerchargevalue=? WHERE mappingid=?";
                            preparedStatement = conn.prepareStatement(q2);
                            preparedStatement.setString(1, memberCharge);
                            preparedStatement.setString(2, agentCharge);
                            preparedStatement.setString(3, partnerCharge);
                            preparedStatement.setString(4, mappingId);
                            int j = preparedStatement.executeUpdate();
                            if (j == 1)
                            {
                                String qry = "INSERT INTO ChargeVersionMemberMaster (merchantChargeValue,agentCommision,partnerCommision,effectiveStartDate,effectiveEndDate,member_accounts_charges_mapping_id) VALUES (?,?,?,?,?,?)";
                                PreparedStatement pstmt = conn.prepareStatement(qry);
                                pstmt.setString(1, memberCharge);
                                pstmt.setString(2, agentCharge);
                                pstmt.setString(3, partnerCharge);
                                pstmt.setString(4, startDate);
                                pstmt.setString(5, endDate);
                                pstmt.setString(6, mappingId);
                                pstmt.executeUpdate();
                                int k = preparedStatement.executeUpdate();
                                if (k == 1)
                                {
                                    errorMsg += "<center><font class=\"textb\" ><b>Charge updated successfully. " + EOL + "</b></font></center>";
                                }
                                else
                                {
                                    errorMsg += "<center><font class=\"textb\" ><b>Charge updating failed. " + EOL + "</b></font></center>";
                                }
                            }
                            else
                            {
                                errorMsg += "<center><font class=\"textb\" ><b> Charge updating failed. " + EOL + "</b></font></center>";
                            }
                        }
                        else
                        {
                            errorMsg += "<center><font class=\"textb\" ><b> Charge updating failed." + EOL + "</b></font></center>";
                        }
                    }
                    else
                    {
                        errorMsg += "<center><font class=\"textb\" ><b> update charges record Failed. " + EOL + "</b></font></center>";
                    }

                }
                catch (SystemError systemError)
                {
                    log.error("SystemError:::::", systemError);
                    req.setAttribute("errormessage", "Internal error while updating charge on member account.");
                    RequestDispatcher rd = req.getRequestDispatcher("/actionPartnerAccountCharges.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                catch (SQLException e)
                {
                    log.error("SQLException::::::", e);
                    req.setAttribute("errormessage", "Internal error while updating charge on member account.");
                    RequestDispatcher rd = req.getRequestDispatcher("/actionPartnerAccountCharges.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                catch (ParseException e)
                {
                    log.error("ParseException:::::", e);
                    req.setAttribute("errormessage", "Internal error while updating charge on member account.");
                    RequestDispatcher rd = req.getRequestDispatcher("/actionPartnerAccountCharges.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                finally
                {
                    Database.closeResultSet(rs);
                    Database.closePreparedStatement(preparedStatement);
                    Database.closeConnection(conn);
                }
            }
            req.setAttribute("successmessage", errorMsg);
            RequestDispatcher rd = req.getRequestDispatcher("/actionPartnerAccountCharges.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException:::::::::" + e);
        }
    }
}
