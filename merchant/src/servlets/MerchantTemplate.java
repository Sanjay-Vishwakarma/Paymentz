import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.directi.pg.SystemError;
import com.directi.pg.Template;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Hashtable;

//import java.io.PrintWriter;

//import java.util.*;


public class MerchantTemplate extends HttpServlet
{
    private static Logger Log = new Logger(MerchantTemplate.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        response.setContentType("text/html");
        Merchants merchants = new Merchants();
       // PrintWriter out = response.getWriter();

        //String data = request.getParameter("data");
        Hashtable hash = null;
        // Hashtable imagehash = null;
        String redirectpage = null;

        HttpSession session = request.getSession();
        if (!merchants.isLoggedIn(session))
        {   Log.debug("member is logout ");
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        Log.debug("CSRF check successful ");

        try
        {
            hash = Template.getMemberTemplateDetails((String) session.getAttribute("merchantid"));
            Hashtable temphash = merchants.getMemberTemplateDetails((String) session.getAttribute("merchantid"));
            hash.put("template", (String) temphash.get("template"));
            hash.put("autoredirect", (String) temphash.get("autoredirect"));
            hash.put("checksumalgo", (String) temphash.get("checksumalgo"));
            //imagehash=Merchants.getMemberImagesDetails((String)session.getAttribute("merchantid"));
            redirectpage = "/merchanttemplate.jsp?ctoken=" + user.getCSRFToken();

        }
        catch (SystemError se)
        {
            Log.error("Leaving Merchants throwing SQL Exception as System Error : ",se);

            redirectpage = "/error.jsp";
        }
        catch (Exception e)
        {
            Log.error("Leaving Merchants throwing Exception : ", e);
            redirectpage = "/error.jsp";
        }
        request.setAttribute("details", hash);
        // request.setAttribute("images",imagehash);

        Log.debug("DETAILS r "+hash.toString());
        request.setAttribute("error",request.getAttribute("error"));
        RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
        rd.forward(request, response);

        Log.info("Leaving MerchantTemplate");
    }
}
