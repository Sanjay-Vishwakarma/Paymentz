import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.manager.ChargeManager;
import com.manager.vo.PartnerCommissionVO;
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

public class ActionPartnerCommission extends HttpServlet
{
    private static Logger log = new Logger(ActionPartnerCommission.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        Connection conn = null;
        String errormsg="";

        String action=req.getParameter("action");
        String id=req.getParameter("commissionid");

        if(action.equalsIgnoreCase("modify"))
        {
            try
            {
                ChargeManager chargeManager=new ChargeManager();
                PartnerCommissionVO partnerCommissionVO=chargeManager.getPartnerCommissionDetails(id);

                req.setAttribute("action",action);
                req.setAttribute("partnerCommissionVO",partnerCommissionVO);

            }
            catch (PZDBViolationException  e)
            {
                errormsg="<center><font class=\"text\" face=\"arial\"><b>"+e.getMessage()+"</b></font></center>";
            }
        }
        else
        {
            errormsg = "<center><font class=\"text\" face=\"arial\"><b> Action is not defined</b></font></center>";
        }

        req.setAttribute("action",action);
        req.setAttribute("errormessage",errormsg);
        RequestDispatcher rd = req.getRequestDispatcher("/actionPartnerCommission.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);

    }
}
