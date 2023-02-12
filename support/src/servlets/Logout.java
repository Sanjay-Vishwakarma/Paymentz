import com.directi.pg.CustomerSupport;
import com.directi.pg.Logger;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

public class Logout extends HttpServlet
{
   static Logger log=new Logger("logger1");
    static final String classname= Logout.class.getName();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Long logintime= (Long)session.getAttribute("LoginDate");
        Integer csId= (Integer) session.getAttribute("csId");
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        Enumeration<String> enames = session.getAttributeNames();
//        try
//        {
////            CustomerSupport.updateLogout(csId,logintime);
//        }
//        catch (SQLException e)
//        {
//            log.error( classname+" SQL connection EXCEPTION::",e);  //To change body of catch statement use File | Settings | File Templates.
//        }
        while (enames.hasMoreElements())
        {
            String name = enames.nextElement();
            if (!name.equals("JSESSIONID"))
            {
                attributes.put(name, session.getAttribute(name));
            }

        }

        // invalidate the session
        if(!CustomerSupport.isLoggedIn(session))
        {
            ESAPI.authenticator().logout();
            response.sendRedirect("/support/login.jsp");
            session.invalidate();
            return;
        }
        session.invalidate();

        log.debug(classname+" invalidating all session and logging out");
        ESAPI.authenticator().logout();
        log.debug(classname+" forwarding to custSuppLogin");
        response.sendRedirect("/support/login.jsp");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);

    }
}
