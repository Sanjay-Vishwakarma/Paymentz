package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Created by Kanchan on 04-03-2021.
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates
 */
public class SetReservesCallback extends HttpServlet
{
    private static Logger logger = new Logger(SetReservesCallback.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.debug("entering in setReservesCallback...");
        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner = new PartnerFunctions();
        logger.debug("success...");
        String error = "";

        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/logout.jsp");
            return;
        }

        if (!partner.isEmptyOrNull(error))
        {
            String redirectpage = "/merchantcallbacksettings.jsp?ctoken=" + user.getCSRFToken();
            request.setAttribute("error", error);
            RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request, response);
            return;
        }
        else
        {
            String memberId = request.getParameter("memberid");
            String reconciliationNotification = request.getParameter("reconciliationNotification");
            String transactionNotification = request.getParameter("transactionNotification");
            String refundNotification = request.getParameter("refundNotification");
            String chargebackNotification = request.getParameter("chargebackNotification");
            String payoutNotification = request.getParameter("payoutNotification");
            String inquiryNotification =request.getParameter("inquiryNotification");

            StringBuilder sSuccessMessage = new StringBuilder();
            StringBuilder query = new StringBuilder();

            response.setContentType("text/html");
            Connection con = null;
            PreparedStatement pstmt = null;

            if (memberId != null)
            {
                try
                {
                    con = Database.getConnection();
                    query.append("Update members m JOIN merchant_configuration mc ON m.memberid=mc.memberid set reconciliationNotification=?,transactionNotification=?,refundNotification=?,chargebackNotification=?,payoutNotification=?,inquiryNotification=? where m.memberid=?");

                    pstmt = con.prepareStatement(query.toString());
                    pstmt.setString(1, reconciliationNotification);
                    pstmt.setString(2, transactionNotification);
                    pstmt.setString(3, refundNotification);
                    pstmt.setString(4, chargebackNotification);
                    pstmt.setString(5, payoutNotification);
                    pstmt.setString(6, inquiryNotification);
                    pstmt.setString(7, memberId);

                    logger.debug("result:: " + pstmt);
                    logger.debug("inside try block::+++ "+ query);
                    int result = pstmt.executeUpdate();

                    if (result > 0)
                    {
                        sSuccessMessage.append(result).append(" Records Updated");
                    }
                }
                catch (SQLException se)
                {
                    logger.error("SQLException :: " + se);
                    request.setAttribute("error",se.getMessage());
                }

                catch (Exception e)
                {
                    logger.error("Exception while set reserves callback", e);
                    request.setAttribute("error",e.getMessage());
                }
                finally
                {
                    Database.closePreparedStatement(pstmt);
                    Database.closeConnection(con);
                }
            }

            request.setAttribute("message", sSuccessMessage.toString());
            RequestDispatcher rd = request.getRequestDispatcher("/merchantcallbacksettings.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }
    }
}
