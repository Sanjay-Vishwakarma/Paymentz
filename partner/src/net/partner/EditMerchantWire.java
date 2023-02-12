package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Logger;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Admin on 7/25/2019.
 */
public class EditMerchantWire extends HttpServlet
{
    Logger logger = new Logger(EditMerchantWire.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }

    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.debug("inside EditMerchantWire:::::::::::::");
       HttpSession session = request.getSession();
         PartnerFunctions partner = new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }


        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        RequestDispatcher rd=request.getRequestDispatcher("/merchantWireReports.jsp?ctoken="+user.getCSRFToken());

        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        String memberId = request.getParameter("memberid");
        String terminalId = request.getParameter("terminalid");
        String action = request.getParameter("action");
        String status = request.getParameter("status");
        String firstdate = request.getParameter("firstdate");
        String lastdate = request.getParameter("lastdate");
        String settledid = request.getParameter("settledid");

        Connection con = null;
        HashMap memberHash = new HashMap();
        if ("modify".equals(action))
        {
            PreparedStatement pstmt = null;
            try
            {
                StringBuffer query = new StringBuffer();
                con = Database.getConnection();
                query = new StringBuffer("update merchant_wiremanager set status = ? WHERE toid= ? and terminalid = ? and settledid = ?");

                pstmt = con.prepareStatement(query.toString());
                pstmt.setString(1, status);
                pstmt.setString(2, memberId);
                pstmt.setString(3, terminalId);
                pstmt.setString(4, settledid);
                logger.debug("pstmt::::" + pstmt);
                int i = pstmt.executeUpdate();
                if (i > 0)
                {
                    logger.debug("inside Updated Successfully::::");
                    request.setAttribute("status", "Updated Successfully");
                }
            }
            catch (Exception e)
            {
                logger.error("Exception in  method: ", e);
            }
            finally
            {
                Database.closeConnection(con);
            }
            request.setAttribute("memberDetail", memberHash);
            rd.forward(request, response);
            return;
        }
    }
}
