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
 * User: Naushad
 * Date: 2/28/17
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeleteSMSMapping extends HttpServlet
{
    static Logger log = new Logger(DeleteSMSMapping.class.getName());
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
        String mappingId="";

        if (!ESAPI.validator().isValidInput("mappingid",request.getParameter("mappingid"),"Numbers",20,false))
        {
            log.error("Invalid SMS mappingid.");
            errorMsg="Invalid SMS Mappingid.";
            request.setAttribute("error",errorMsg);
            RequestDispatcher rd = request.getRequestDispatcher("/smsmappinglist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        else
            mappingId=request.getParameter("mappingid");

        errorMsg=mailUIUtils.deleteSMSMapping(mappingId);
        request.setAttribute("error",errorMsg);
        RequestDispatcher rd = request.getRequestDispatcher("/smsmappinglist.jsp?ctoken="+user.getCSRFToken());
        rd.forward(request, response);
    }
}
