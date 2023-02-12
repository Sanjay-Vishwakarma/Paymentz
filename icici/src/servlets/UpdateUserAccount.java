package servlets;
import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.AdminManager;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Admin on 17-11-2021.
 */
public class UpdateUserAccount extends HttpServlet
{
    private static Logger log= new Logger(UpdateUserAccount.class.getName());
    Functions functions= new Functions();
    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        HttpSession session= request.getSession();
        User user= (User)session.getAttribute("ESAPIUserSessionKey");
        AdminManager adminManager= new AdminManager();
        String actionExecuterId=(String) session.getAttribute("merchantid");
        if (!Admin.isLoggedIn(session))
        {
            log.debug("admin logout");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        RequestDispatcher rd= request.getRequestDispatcher("/adminlist.jsp?ctoken="+user.getCSRFToken());

        String signuptime = request.getParameter("signuptime")!=null?request.getParameter("signuptime"):"";
        String adminid= request.getParameter("adminid")!=null?request.getParameter("adminid"):"";
        String login= request.getParameter("login")!=null?request.getParameter("login"):"";
        String contact_emails= request.getParameter("contact_emails")!= null?request.getParameter("contact_emails"):"";
        String isActive= (request.getParameter("isActive"));

        log.error("signuptime--------"+signuptime);
        log.error("adminid--------"+adminid);
        log.error("login--------"+login);
        log.error("contact_emails--------"+contact_emails);
        log.error("isActive--------"+isActive);

        String  status= null;
        try
        {
            status= adminManager.getUpdateAction( adminid, isActive, login);
            request.setAttribute("msg",status);
            log.error("status from servlet::: "+status);
            rd.forward(request, response);
            return;
        }
        catch (Exception e)
        {
            log.error("SystemError::: ",e);
        }

    }
}
