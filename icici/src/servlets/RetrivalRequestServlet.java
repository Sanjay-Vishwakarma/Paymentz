import com.directi.pg.*;

import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * Date: Nov 3, 2006
 * Time: 6:15:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class RetrivalRequestServlet extends HttpServlet
{       private static Logger logger = new Logger(RetrivalRequestServlet.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        PrintWriter out = response.getWriter();
        String action=null;
        String icicitransid=null;
        String date=null;
        HttpSession session = request.getSession();
        Transaction transaction = new Transaction();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        logger.debug("success");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("member is logout ");
            response.sendRedirect("/icici/admin/logout.jsp");
            return;
        }
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        try
        {
            validateMandatoryParameter(request);
        }
        catch(ValidationException e)
        {
         logger.error("Invalid Input:::::::",e);
        }

        action = request.getParameter("action");
        icicitransid = request.getParameter("icicitransid");
        date = request.getParameter("date");

        try
        {
            if ("sendrequest".equals(action))
            {
                transaction.processSendRetrivalRequest(icicitransid, date);

                sSuccessMessage.append("Mail Sent Successfully to Merchant for processing Retrival Request of transid \r\n" + icicitransid);
            }
            else
            {
                transaction.processDocReceived(icicitransid);
                out.println(Functions.ShowMessage("Message", "Docs received for transid  " + icicitransid));
                sErrorMessage.append("Docs received for transid \r\n " + icicitransid);

            }
        }
        catch (SystemError systemError)
        {   logger.error("Error:::::",systemError);

            sErrorMessage.append("Internal System  Error ");

        }
        catch (SQLException e)
        {   logger.error("Error!",e);
            sErrorMessage.append("Internal System  Error ");

        }

        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());

        String redirectpage = "/chargebacklist.jsp?ctoken="+user.getCSRFToken();
        request.setAttribute("cbmessage", chargeBackMessage.toString());
        RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
        rd.forward(request, response);

    }
    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.ACTION);
        inputFieldsListMandatory.add(InputFields.ICICITRANSEID);
        inputFieldsListMandatory.add(InputFields.DATE_SMALL);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}
