import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.manager.WhiteListManager;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Mahima Rai.
 * Date: 19/05/18
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeleteDetails extends HttpServlet
{
    static Logger log = new Logger(DeleteDetails.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        String statusMsg = "";
        RequestDispatcher rd = request.getRequestDispatcher("/whitelistdetails.jsp?ctoken="+user.getCSRFToken());
        WhiteListManager whiteListManager = new WhiteListManager();
        String[] mappingIds=request.getParameterValues("id");

        if(mappingIds==null || "".equals(mappingIds) || mappingIds.length<1){
            request.setAttribute("error","Invalid request,mapping not found");
            rd.forward(request,response);
            return;
        }
        try{
            int k=0;
            int j=0;
            for(String mappingId:mappingIds)
            {
                String isTemp = request.getParameter("isTemp_" + mappingId);
                System.out.println("isTemp----->"+isTemp);
                boolean delete = whiteListManager.removeCardEmailEntry(isTemp,mappingId);
                if (delete){
                    k++;
                }
                else
                {
                    j++;
                }
            }
            statusMsg ="Successful="+k+", Failed="+j;
            request.setAttribute("error", statusMsg);
            rd.forward(request, response);
            return;
        }
        catch (PZDBViolationException e)
        {
            request.setAttribute("error","Internal error while processing your request");
            rd.forward(request,response);
            return;
        }
    }
}
