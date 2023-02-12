
import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.directi.pg.PzEncryptor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;

import org.owasp.esapi.User;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Oct 2, 2012
 * Time: 5:12:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateKey  extends HttpServlet
{
    private static Logger Log = new Logger(UpdateKey.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   Log.debug("Admin is logout ");
            response.sendRedirect("/icici/Logout.jsp");
            return;
        }

        PrintWriter out = response.getWriter();
        int result =0;
        String str="";
        try
        {
           result= PzEncryptor.updateSecretKeyInDatabase(request.getParameter("secretkey"));
            out.println("Your new secretkey is update");

        }

        catch (Exception e)
        {
            Log.error("Error! in Generating new Secret Key", e);
            out.println("Your new secretkey is NOT update");
        }


       RequestDispatcher rd = request.getRequestDispatcher("/updatedkey.jsp?ctoken="+user.getCSRFToken());
       rd.forward(request, response);

    }

}
