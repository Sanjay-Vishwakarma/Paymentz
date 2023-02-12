package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.dao.WhiteListDAO;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by suneeta on 4/18/2019.
 */
public class WhitelistEmailDelete extends HttpServlet
{
    private static Logger logger = new Logger(WhitelistDelete.class.getName());
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        PartnerFunctions partnerFunctions = new PartnerFunctions();
        WhiteListDAO whiteListDAO = new WhiteListDAO();
        HttpSession session= Functions.getNewSession(request);
        if(!partnerFunctions.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/logout.jsp");
            return;
        }
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        String memberid ="";
        String isTemp ="";
        String msg = "";
        String errormsg = "";
        String ids[]=request.getParameterValues("id");
        for (String id:ids)
        {
            memberid=request.getParameter("memberid_"+id);
            isTemp=request.getParameter("isTemp_"+id);
            try
            {
                boolean flag = whiteListDAO.removeCardEmailEntry(isTemp,id);
                if (flag)
                {
                    msg= "Records Updated Successful for Member ID:" + memberid;
                }
                else
                {
                    errormsg = "Update failed";
                }
            }catch (Exception e)
            {
                logger.debug("Exception:::::"+e);
            }
        }
        request.setAttribute("error",errormsg);
        request.setAttribute("msg", msg.toString());
        RequestDispatcher rd= request.getRequestDispatcher("/whitelistEmail.jsp?ctoken="+user.getCSRFToken());
        rd.forward(request,response);
        return;
    }


}
