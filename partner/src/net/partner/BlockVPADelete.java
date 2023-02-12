package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.BlacklistManager;
import com.manager.dao.BlacklistDAO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Kalyani on 31/12/2021.
 */
public class BlockVPADelete extends HttpServlet
{
    private static Logger log=new Logger(BlockVPADelete.class.getName());

    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        PartnerFunctions partnerFunctions=new PartnerFunctions();
        BlacklistDAO blacklistDAO=new BlacklistDAO();
        HttpSession session= Functions.getNewSession(request);
        if (!partnerFunctions.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/logout.jsp");
            return;
        }
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        String ids[]=request.getParameterValues("id");
        String message = "";
        String errormsg = "";
        String redirectPage = "";
        for(String id:ids)
        {
            System.out.println("ids");
            try
            {
                boolean flag = blacklistDAO.unblockVpaAddressPartner(id);
                System.out.println("flag"+flag);
                log.debug("flag::::" + flag);
                if (flag)
                {
                    message = "Records UnBlocked Successful";
                }
                else
                {
                    errormsg = "Delete Unblocked";
                }
            }
            catch(PZDBViolationException e)
            {
                log.debug("Exception:::::" + e);
            }
        }
        request.setAttribute("error",errormsg);
        request.setAttribute("message", message.toString());
        redirectPage = "/blockVPAAddress.jsp?ctoken=";
        RequestDispatcher rd = request.getRequestDispatcher(redirectPage + user.getCSRFToken());
        rd.forward(request, response);
        return;
    }
}
