import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.manager.ChargeManager;
import com.manager.vo.payoutVOs.WLPartnerCommissionVO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;

/**
 * Created by Naushad on 11/28/2016.
 */
public class ActionWLPartnerCommMapping extends HttpServlet
{
    private static Logger log =new Logger(ActionWLPartnerCommMapping.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String errormsg="";
        String action=req.getParameter("action");
        String id=req.getParameter("commissionid");
        if(action.equalsIgnoreCase("modify"))
        {
            try
            {
                ChargeManager chargeManager=new ChargeManager();
                WLPartnerCommissionVO wlPartnerCommissionVO=chargeManager.getWLPartnerCommissionDetails(id);
                req.setAttribute("action",action);
                req.setAttribute("wlPartnerCommissionVO",wlPartnerCommissionVO);
            }
            catch (PZDBViolationException  e)
            {
                errormsg="Internal error while processing your request.";
            }
        }
        else
        {
            errormsg = "Invalid action specified";
        }
        req.setAttribute("action",action);
        req.setAttribute("errormessage",errormsg);
        RequestDispatcher rd = req.getRequestDispatcher("/actionwlpartnercommmapping.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

}
