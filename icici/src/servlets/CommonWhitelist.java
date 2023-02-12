import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.manager.WhiteListManager;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
/**
 * Created with IntelliJ IDEA.
 * User: Mahima
 * Date: 25/03/18
 * Time: 2:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommonWhitelist extends HttpServlet
{
    private static Logger logger = new Logger(CommonWhitelist.class.getName());
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
            logger.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        String id[] = req.getParameterValues("id");
        String error = "";
        id = req.getParameterValues("id");
        if(id!=null)
        {
            Connection conn = null;
            WhiteListManager whiteListManager = new WhiteListManager();
            try
            {
                for (String mappingId : id)
                {
                    String isTemp = req.getParameter("isTemp_" + mappingId);
                    whiteListManager.getCommonWhiteList(isTemp, mappingId);
                }
            }
            catch (Exception e)
            {
                logger.error("Error while CommonWhitelist :", e);
                req.setAttribute("error", error);
            }
            finally
            {
                Database.closeConnection(conn);
            }
        }
        sSuccessMessage.append("Records Updated Successfully");
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());

        String redirectpage = "/uploadwhitelistemaildetails.jsp?ctoken=" + user.getCSRFToken();
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);
    }
}