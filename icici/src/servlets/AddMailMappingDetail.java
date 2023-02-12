import com.directi.pg.Admin;
import com.directi.pg.Logger;
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


/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 5/22/14
 * Time: 5:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddMailMappingDetail extends HttpServlet
{
    private static Logger log = new Logger(AddMailMappingDetail.class.getName());
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
        String eventid="";
        String fromentityid="";
        String toentityid="";
        String templateid="";
        String subject="";

        if (!ESAPI.validator().isValidInput("eventid",request.getParameter("eventid"),"Numbers",20,false))
        {
            log.error("Invalid mail eventid.");
            errorMsg="Invalid Mail eventid.";
            request.setAttribute("error",errorMsg);
            RequestDispatcher rd = request.getRequestDispatcher("/addmailmapping.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        else
            eventid=request.getParameter("eventid");

        if (!ESAPI.validator().isValidInput("fromentityid",request.getParameter("fromentityid"),"Numbers",20,false))
        {
            log.error("Invalid mail fromentityid.");
            errorMsg="Invalid Mail fromentityid.";
            request.setAttribute("error",errorMsg);
            RequestDispatcher rd = request.getRequestDispatcher("/addmailmapping.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        else
            fromentityid=request.getParameter("fromentityid");

        if (!ESAPI.validator().isValidInput("toentityid",request.getParameter("toentityid"),"Numbers",20,false))
        {
            log.error("Invalid mail toentityid.");
            errorMsg="Invalid Mail toentityid.";
            request.setAttribute("error",errorMsg);
            RequestDispatcher rd = request.getRequestDispatcher("/addmailmapping.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        else
            toentityid=request.getParameter("toentityid");

        if (!ESAPI.validator().isValidInput("templateid",request.getParameter("templateid"),"Numbers",20,false))
        {
            log.error("Invalid mail templateid.");
            errorMsg="Invalid Mail templateid.";
            request.setAttribute("error",errorMsg);
            RequestDispatcher rd = request.getRequestDispatcher("/addmailmapping.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        else
            templateid=request.getParameter("templateid");

        if (!ESAPI.validator().isValidInput("subject",request.getParameter("subject"),"Description",60,false))
        {
            log.error("Invalid mail subject.");
            errorMsg="Invalid Mail subject.";
            request.setAttribute("error",errorMsg);
            RequestDispatcher rd = request.getRequestDispatcher("/addmailmapping.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        else
            subject=request.getParameter("subject");

        errorMsg=mailUIUtils.addMailMapping(eventid,fromentityid,toentityid,templateid,subject);
        request.setAttribute("error",errorMsg);
        RequestDispatcher rd = request.getRequestDispatcher("/addmailmapping.jsp?ctoken="+user.getCSRFToken());
        rd.forward(request, response);
    }
}
