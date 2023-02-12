package net.partner;
import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.PartnerManager;
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

public class UpdateEmailSetting extends HttpServlet
{
    private static Logger logger = new Logger(UpdateEmailSetting.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        String errorMsg = "";
        String EOL = "<br>";
        PartnerFunctions partner=new PartnerFunctions();
        PartnerManager partnerManager = new PartnerManager();

        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        StringBuilder chargeBackMessage = new StringBuilder();

        String partnerId = req.getParameter("partnerid");
        String merchantTransaction=req.getParameter("merchantTransaction");
        String billingDescriptor=req.getParameter("billingDescriptor");
        String salesTransaction=req.getParameter("salesTransaction");
        String adminFailedTransaction=req.getParameter("adminFailedTransaction");
        String partnerCardRegistration=req.getParameter("partnerCardRegistration");
        String merchantCardRegistration=req.getParameter("merchantCardRegistration");
        String payoutTransaction=req.getParameter("payoutTransaction");
       // String exceptionDetails=req.getParameter("exceptionDetails");
        String fraudFailedTransaction=req.getParameter("fraudFailedTransaction");
        String chargebackTransaction=req.getParameter("chargebackTransaction");
        String refundTransaction=req.getParameter("refundTransaction");
        String failedTransaction=req.getParameter("failedTransaction");
        String rejectTransaction=req.getParameter("rejectTransaction");
        System.out.println("partnerId::::"+partnerId);


        //TODO:Need to add validation support

        int updRecs = 0;
        Functions functions = new Functions();
        if (functions.isValueNull(partnerId))
        {
            Connection cn = null;
            PreparedStatement pstmt = null;
            if (errorMsg.length() > 0)
            {
                String redirectpage = "/partnerpreference.jsp?ctoken=" + user.getCSRFToken();
                req.setAttribute("cbmessage", errorMsg);
                RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
                rd.forward(req, res);
                return;
            }

            try
            {
                cn = Database.getConnection();
                String query = "UPDATE partner_configuration SET salesBillingDescriptor=?,merchantSalesTransaction=?,salesAdminFailedTransaction=?,salesPartnerCardRegistration=?,salesMerchantCardRegistration=?,salesPayoutTransaction=?,fraudFailedTransaction=?,chargebackTransaction=?,refundTransaction=?,failedTransaction=?,rejectTransaction=? WHERE partnerId=?";
                pstmt = cn.prepareStatement(query);
                pstmt.setString(1, billingDescriptor);
                pstmt.setString(2, salesTransaction);
                pstmt.setString(3, adminFailedTransaction);
                pstmt.setString(4, partnerCardRegistration);
                pstmt.setString(5, merchantCardRegistration);
                pstmt.setString(6, payoutTransaction);
                pstmt.setString(7, fraudFailedTransaction);
                pstmt.setString(8, chargebackTransaction);
                pstmt.setString(9, refundTransaction);
                pstmt.setString(10, failedTransaction);
                pstmt.setString(11, rejectTransaction);
                pstmt.setString(12, partnerId);
                logger.debug("result " + pstmt.toString());
                int result = pstmt.executeUpdate();
                if (result > 0)
                {
                    updRecs++;
                }
            }
            catch (Exception e)
            {
                logger.error("Exception:::::", e);
                req.setAttribute("error", errorMsg);
            }
            finally
            {
                Database.closePreparedStatement(pstmt);
                Database.closeConnection(cn);

            }
        }
        sSuccessMessage.append(updRecs + " Records Updated");
        chargeBackMessage.append(sSuccessMessage.toString());
        String redirectpage = "/partnerEmailSetting.jsp?ctoken=" + user.getCSRFToken();
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        req.setAttribute("error", sErrorMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);
    }
}