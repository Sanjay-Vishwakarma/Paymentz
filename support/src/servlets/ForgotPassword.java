import com.directi.pg.CustomerSupport;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
public class ForgotPassword extends HttpServlet
{
    static Logger log = new Logger("logger1");
    static String classname= ForgotPassword.class.getName();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = Functions.getNewSession(request);
        User user =  (User)session.getAttribute("Anonymous");
        String ctoken= request.getParameter("ctoken");
        if(Functions.validateCSRFAnonymos(request.getParameter("ctoken"), user))
        {

            session.setAttribute("Anonymous",user);

        }
        else
        {     log.debug("session out");
            response.sendRedirect("/support/sessionout.jsp");

            return;
        }

        String redirectpage = null;
        String username=null;

        RequestDispatcher rd = null;
        try
        {
            username = ESAPI.validator().getValidInput("Username",request.getParameter("username"),"SafeString",100,false);
        }

        catch (ValidationException e)
        {

            log.error(classname+" Invalid Username" ,e);
            request.setAttribute("action","F");

            redirectpage="/forgotPassword.jsp?MES=F&ctoken=" + user.getCSRFToken();
            rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request, response);
          return;
        }

        String activation = null;
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        String data = request.getParameter("data");

        boolean flag = false;

        try
        {


            user =  ESAPI.authenticator().getUser(username);
            if(user!=null)
            {
            flag = CustomerSupport.forgotPassword(username, user);
            if (flag == true)
            {
                log.info(classname+" send mail on support id on this page"+redirectpage);
                request.setAttribute("ctoken",ctoken);
                redirectpage= "/successForwarded.jsp?ctoken="+ctoken;
            }
            else
            {
                log.debug(classname+" Invalid emailID ");
                request.setAttribute("action","F");

                redirectpage="/loginerror.jsp?ctoken="+ctoken;
            }
            rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request, response);

            log.debug(classname+" Process Successful for Forgotpassword");
            }
            else
            {
                user=(User)session.getAttribute("Anonymous");
                rd = request.getRequestDispatcher("/forgotPassword.jsp?MES=X&ctoken="+user.getCSRFToken());
                rd.forward(request,response);
            }

        }
        catch (Exception e)
        {   log.error(classname+" Exception:::::::",e);
            out.println(Functions.NewShowConfirmation("Error!", e.toString()));
        }

    }
}
