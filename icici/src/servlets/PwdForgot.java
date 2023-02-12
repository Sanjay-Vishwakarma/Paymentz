import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
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
import java.util.ArrayList;
import java.util.List;

public class PwdForgot extends HttpServlet
{
    private static Logger log = new Logger(PwdForgot.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = Functions.getNewSession(request);
        User user =  (User)session.getAttribute("Anonymous");



        log.debug("successful");

        String redirectpage = null;
        String username=null;

        RequestDispatcher rd = null;
        try
        {
            //validateMandatoryParameter(request);
            username = ESAPI.validator().getValidInput("Username",request.getParameter("username"),"SafeString",100,false);
        }

        catch (ValidationException e)
        {

            log.error("Invalid Username" ,e);
            request.setAttribute("action","F");
            redirectpage="/icici/loginerror.jsp&ctoken="+user.getCSRFToken();
            rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request, response);

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
            flag = Admin.AdminforgotPassword(username, user);
            if (flag == true)
            {   response.sendRedirect("/icici/pwdmailsent.jsp");
                log.info("send mail on merchant id on this page"+redirectpage);

            }
            else
            {
                log.debug("Invalid emailID ");
                request.setAttribute("action","F");
                redirectpage="/loginerror.jsp?action=F";
                rd = request.getRequestDispatcher(redirectpage);
                rd.forward(request, response);
            }
            }
            else
            {
                log.debug("Invalid USERNAME");
                request.setAttribute("action","E");
                redirectpage="/loginerror.jsp?action=E";
                rd = request.getRequestDispatcher(redirectpage);
                rd.forward(request, response);
            }

            log.debug("Process Successful for Forgotpassword");


        }
        catch (SystemError se)
        {   log.error("System Error:::::::",se);
            out.println(Functions.ShowMessage("Error", se.toString()));
            //System.out.println(se.toString());

        }
        catch (Exception e)
        {   log.error("Exception:::::::",e);
            out.println(Functions.ShowMessage("Error!", e.toString()));
        }
        /*rd = request.getRequestDispatcher(redirectpage);
        rd.forward(request, response);*/
    }
    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.USERNAME);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}

