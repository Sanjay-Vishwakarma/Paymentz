import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import servlets.MailUIUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: Naushad
 * Date: 2/28/17
 * Time: 5:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddSmsMappingDetail extends HttpServlet
{
    private static Logger log = new Logger(AddSmsMappingDetail.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        log.debug("Entering in SheepingDetailList");
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        MailUIUtils mailUIUtils=new MailUIUtils();
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        String errorMsg="";
        String templateId="";
        String eventId="";
        String fromEntityId="";
        String toEntityId="";
        String subject="";

        if (!ESAPI.validator().isValidInput("smstemplateid",request.getParameter("smstemplateid"),"Numbers",20,false))
        {
            log.error("Invalid sms templateid.");
            errorMsg="Invalid sms templateid.";
            request.setAttribute("error",errorMsg);
            RequestDispatcher rd = request.getRequestDispatcher("/addsmsmapping.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        else
            templateId=request.getParameter("smstemplateid");

        if (!ESAPI.validator().isValidInput("eventid",request.getParameter("eventid"),"Numbers",20,false))
        {
            log.error("Invalid sms eventid.");
            errorMsg="Invalid sms eventid.";
            request.setAttribute("error",errorMsg);
            RequestDispatcher rd = request.getRequestDispatcher("/addsmsmapping.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        else
            eventId=request.getParameter("eventid");

        if (!ESAPI.validator().isValidInput("fromentityid",request.getParameter("fromentityid"),"Numbers",20,false))
        {
            log.error("Invalid sms fromentityid.");
            errorMsg="Invalid sms fromentityid.";
            request.setAttribute("error",errorMsg);
            RequestDispatcher rd = request.getRequestDispatcher("/addsmsmapping.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        else
            fromEntityId=request.getParameter("fromentityid");
            //System.out.println("From Entity::::::::::::"+fromEntityId);

        if (!ESAPI.validator().isValidInput("toentityid",request.getParameter("toentityid"),"Numbers",20,false))
        {
            log.error("Invalid sms toentityid.");
            errorMsg="Invalid sms toentityid.";
            request.setAttribute("error",errorMsg);
            RequestDispatcher rd = request.getRequestDispatcher("/addsmsmapping.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        else
            toEntityId=request.getParameter("toentityid");
            //System.out.println("To Entity::::::::::::::"+toEntityId);

        if (!ESAPI.validator().isValidInput("smssubject",request.getParameter("smssubject"),"SafeString",60,false))
        {
            log.error("Invalid sms subject.");
            errorMsg="Invalid sms subject.";
            request.setAttribute("error",errorMsg);
            RequestDispatcher rd = request.getRequestDispatcher("/addsmsmapping.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        else
            subject=request.getParameter("smssubject");

        errorMsg=mailUIUtils.addSmsMapping(templateId,eventId,fromEntityId,toEntityId, subject);
        request.setAttribute("error",errorMsg);
        RequestDispatcher rd = request.getRequestDispatcher("/addsmsmapping.jsp?ctoken="+user.getCSRFToken());
        rd.forward(request, response);
    }
}
