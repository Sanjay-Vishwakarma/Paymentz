import com.directi.pg.*;

import com.payment.validators.InputFields;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import java.util.*;

public class EditCSEDetails extends HttpServlet
{  private static Logger logger=new Logger("logger1");
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {

        HttpSession session = Functions.getNewSession(request);
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        boolean update = false;
        if (!com.directi.pg.Admin.isLoggedIn(session))
        {
            response.sendRedirect("/icici/logout.jsp");
        }

        Hashtable error, detail = null;
        int csId =  Integer.parseInt(request.getParameter("Eid"));

        List<InputFields> inputFieldsListOptional = new ArrayList();
        inputFieldsListOptional.add(InputFields.CSEEid);
        inputFieldsListOptional.add(InputFields.EMAILADDR);
        inputFieldsListOptional.add(InputFields.PHONENO);
        error = CustomerSupport.validateMandatoryParameters(request, inputFieldsListOptional);

        if (error.isEmpty())
        {

            String Emailid = request.getParameter("emailaddr");
            String Phoneno = request.getParameter("phoneno");
            try
            {
                update = CustomerSupport.adminUpdateCSE(csId, Emailid, Phoneno);
                request.setAttribute("update", update);
                RequestDispatcher rd = request.getRequestDispatcher("/cseManager.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
            }
            catch (SystemError systemError)
            {
                logger.error(" System Error::", systemError);  //To change body of catch statement use File | Settings | File Templates.
            }
            catch (Exception e)
            {
                logger.error(" main class exception::", e);
            }
        }
        else
        {
            request.setAttribute("error", error);
            RequestDispatcher rd = request.getRequestDispatcher("/servlet/CSEviewupdate?MES=X&submit=Edit&csId="+csId+"&ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }

    }
}
