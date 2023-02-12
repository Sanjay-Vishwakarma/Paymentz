import com.directi.pg.*;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import java.util.*;

public class OrganisationProfile extends HttpServlet
{
    private static Logger Log = new Logger(OrganisationProfile.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        response.setContentType("text/html");
        Merchants merchants = new Merchants();
        PrintWriter out = response.getWriter();

        Hashtable hash = null;
        String redirectpage = null;

        HttpSession session = request.getSession();
        if (!merchants.isLoggedIn(session))
        {
            response.sendRedirect("/merchant/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        Log.debug("CSRF check successful ");

        try
        {
            hash = merchants.getOrganisationDetails((String) session.getAttribute("merchantid"));
            redirectpage = "/updateComProfile.jsp?ctoken="+user.getCSRFToken();

        }
        catch (SystemError se)
        {
            Log.error("Leaving Merchants throwing SQL Exception as System Error : " ,se);
            redirectpage = "/error.jsp";
        }
        catch (Exception e)
        {
            Log.error("Leaving Merchants throwing Exception : " , e);
            redirectpage = "/error.jsp";
        }
        request.setAttribute("details", hash);
        //Log.debug("In OrganisationProfile.java..................." + (String) hash.get("company_type"));

        RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
        rd.forward(request, response);

        Log.info("Leaving generateKey");
    }
}
