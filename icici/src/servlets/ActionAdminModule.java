import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.AdminModuleManager;

import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Naushad
 * Date: 2/10/16
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActionAdminModule extends HttpServlet
{
    static Logger log = new Logger(ActionAdminModule.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        String action=request.getParameter("action");
        String mappingId=request.getParameter("mappingid");
        RequestDispatcher rd = request.getRequestDispatcher("/adminModuleMappingList.jsp?ctoken="+user.getCSRFToken());

        if("remove".equalsIgnoreCase(action))
        {
            try
            {

                AdminModuleManager adminModuleManager=new AdminModuleManager();
                boolean b=adminModuleManager.removeAdminModuleMapping(mappingId);
                String statusMsg="";
                if(b)
                {
                    statusMsg="Module mapping removed successfully";
                }
                else
                {
                    statusMsg="Module mapping removing failed";
                }
                request.setAttribute("errormessage",statusMsg);
                rd.forward(request,response);
                return;
            }
            catch (PZDBViolationException e)
            {
                request.setAttribute("errormessage","Internal error while processing your request");
                rd.forward(request,response);
                return;
            }
        }
        else
        {
            request.setAttribute("errormessage","Invalid Action");
            rd.forward(request,response);
            return;
        }
    }
}
