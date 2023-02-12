package servlets;

import com.directi.pg.Admin;
import com.directi.pg.Logger;
import org.owasp.esapi.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 2/23/15
 * Time: 1:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListofApplicationMember  extends HttpServlet
{
    Logger logger=new Logger(ListofApplicationMember.class.getName());

    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        HttpSession session = request.getSession();

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
    }
}
