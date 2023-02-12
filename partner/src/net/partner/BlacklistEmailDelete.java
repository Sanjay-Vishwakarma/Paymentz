package net.partner;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.dao.BlacklistDAO;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Kalyani on 19/1/2022.
 */
public class BlacklistEmailDelete extends HttpServlet
{
    private static Logger logger = new Logger(BlacklistEmailDelete.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        PartnerFunctions partnerFunctions = new PartnerFunctions();
        BlacklistDAO blacklistDAO = new BlacklistDAO();

        HttpSession session = Functions.getNewSession(req);
        if (!partnerFunctions.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/logout.jsp");
            return;
        }
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        String email ="";
        String msg = "";
        String errormsg = "";
        String redirectPage = "";

        String ids[] = req.getParameterValues("id");
        for (String id : ids)
        {
            try
            {
                boolean flag = blacklistDAO.deleteBlacklistEmail(id);
                logger.debug("flag::::" + flag);
                if (flag)
                {
                    msg = "Emails unblocked successfully";
                }
                else
                {
                    errormsg = "Unblock failed";
                }
            }
            catch (Exception e)
            {
                logger.debug("Exception:::::" + e);
            }
        }
        req.setAttribute("error",errormsg);
        req.setAttribute("msg", msg.toString());
        redirectPage = "/blockEmail.jsp?ctoken=";

        RequestDispatcher rd = req.getRequestDispatcher(redirectPage + user.getCSRFToken());
        rd.forward(req, res);
        return;
    }

}
