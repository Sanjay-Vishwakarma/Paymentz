import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.manager.ChargeManager;
import com.manager.vo.payoutVOs.WLPartnerCommissionVO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Naushad on 11/28/2016.
 */
public class UpdateWLPartnerCommMapping extends HttpServlet
{
    public Logger log =new Logger(UpdateWLPartnerCommMapping.class.getName());
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
        StringBuffer errorMsg=new StringBuffer();
        String commissionValue=req.getParameter("commission_value");
        String commissionId=req.getParameter("commissionid");
        String isInputRequired=req.getParameter("isinput_required");
        String isActive=req.getParameter("isActive");

        if (!ESAPI.validator().isValidInput("comission_value",commissionValue,"NDigitsAmount", 10, false))
        {
            errorMsg.append("Invalid commission value<BR>");
        }
        if(errorMsg.length()>0)
        {
            req.setAttribute("errormessage",errorMsg.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/actionwlpartnercommmapping.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        try
        {
            WLPartnerCommissionVO wlPartnerCommissionVO =new WLPartnerCommissionVO();
            wlPartnerCommissionVO.setCommissionValue(Double.valueOf(commissionValue));
            wlPartnerCommissionVO.setIsInputRequired(isInputRequired);
            wlPartnerCommissionVO.setIsActive(isActive);
            wlPartnerCommissionVO.setCommissionId(commissionId);
            ChargeManager chargeManager=new ChargeManager();
            boolean b=chargeManager.updateWLPartnerCommissionValue(wlPartnerCommissionVO);
            if(b)
            {
                errorMsg.append(" WL Partner Commission updated successfully.");
            }
            else
            {
                errorMsg.append(" WL Partner Commission updation failed.");
            }
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException::::::::"+e);
            errorMsg.append("Internal error while processing your request.");
        }
        req.setAttribute("errormessage",errorMsg.toString());
        RequestDispatcher rd = req.getRequestDispatcher("/actionwlpartnercommmapping.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
}
