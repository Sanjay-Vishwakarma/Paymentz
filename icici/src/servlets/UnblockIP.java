import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;


public class UnblockIP extends HttpServlet
{

    private static Logger logger = new Logger(UnblockIP.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Entering in UnblockIP");
        res.setContentType("text/html");
        HttpSession session = req.getSession();

        User user = (User) session.getAttribute("ESAPIUserSessionKey");


        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        PrintWriter out = res.getWriter();
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();

        String startipcode = req.getParameter("startipcode");
        String endipcode = req.getParameter("endipcode");
        StringBuffer ipquery = new StringBuffer("delete from blockedip where startipcode=? and endipcode=?");


        Connection con = null;
        PreparedStatement pstmt=null;
        try
        {
            con = Database.getConnection();
            pstmt=con.prepareStatement(ipquery.toString());
            pstmt.setString(1,startipcode);
            pstmt.setString(2,endipcode);
            int i = pstmt.executeUpdate();
            if (i != 1)

                sErrorMessage.append("Error while blocking IP.<br> \r\n");
            else
            {
                sSuccessMessage.append("IP addresses  "+startipcode+"  to  "+endipcode+"  are Unblocked. <br>  \r\n");

            }
        }
        catch (SystemError se)
        {
            sErrorMessage.append("Internal Error While Unblock IP.<br> \r\n");
        }
        catch (Exception e)
        {
            sErrorMessage.append("Internal Error While Unblock IP.<br> \r\n");
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }

        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());

        String redirectpage = "/servlet/BlockedIPList?ctoken="+user.getCSRFToken();
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);
    }
}
