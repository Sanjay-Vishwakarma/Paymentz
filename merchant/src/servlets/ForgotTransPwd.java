import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.directi.pg.SystemError;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class ForgotTransPwd extends HttpServlet
{
    private static Logger log = new Logger(ForgotTransPwd.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        Merchants merchants = new Merchants();
        if (!merchants.isLoggedIn(session))
        {
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if(Functions.validateCSRFAnonymos(request.getParameter("ctoken"),user))
        {
            session.setAttribute("ESAPIUserSessionKey",user);
        }
        else
        {
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        String username = (String) session.getAttribute("username");
        String redirectpage = "/error.jsp?ctoken=" + user.getCSRFToken();

        boolean flag = false;

        try
        {
            flag = merchants.forgotTransPassword(username);
            if (flag == true)
            {
                redirectpage = "/ftranspwdsentmail.jsp?ctoken=" + user.getCSRFToken();
            }

            log.debug(redirectpage);
        }
        catch (SystemError se)
        {
            out.println(Functions.NewShowConfirmation1("Error", "Internal system error , Mail is not sent"));
        }
        catch (Exception e)
        {
            out.println(Functions.NewShowConfirmation1("Error!", "Internal system error , Mail is not sent"));
        }
        RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
        rd.forward(request, response);
    }
}
