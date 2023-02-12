import com.directi.pg.*;
import com.manager.ChargeManager;
import com.manager.vo.PartnerCommissionVO;
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
import java.sql.Connection;

public class UpdatePartnerCommission  extends HttpServlet
{
    private static Logger log = new Logger(UpdatePartnerCommission.class.getName());
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
        StringBuffer errormsg=new StringBuffer();

        String commissionValue=req.getParameter("commissionvalue");
       /* String startDate=req.getParameter("startdate");
        String endDate=req.getParameter("enddate");*/
        String commissionId=req.getParameter("commissionid");

        if (!ESAPI.validator().isValidInput("comissionvalue",commissionValue,"AmountStr", 10, false))
        {
            errormsg.append("Invalid commission value<BR>");
        }
        /*if(!ESAPI.validator().isValidInput("startdate", req.getParameter("startdate"), "SafeString", 100, false))
        {
            errormsg.append("Invalid Start Date<BR>");
        }
        if(!ESAPI.validator().isValidInput("enddate", req.getParameter("enddate"), "SafeString", 100, false))
        {
            errormsg.append("Invalid End Date<BR>");
        }*/
        if(errormsg.length()>0)
        {
            req.setAttribute("errormessage",errormsg.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/actionPartnerCommission.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        try
        {
            PartnerCommissionVO partnerCommissionVO=new PartnerCommissionVO();
            partnerCommissionVO.setCommissionValue(Double.valueOf(commissionValue));
            /*partnerCommissionVO.setStartDate(startDate);
            partnerCommissionVO.setEndDate(endDate);*/
            partnerCommissionVO.setCommissionId(commissionId);

            ChargeManager chargeManager=new ChargeManager();
            boolean b=chargeManager.updatePartnerCommissionValue(partnerCommissionVO);
            if(b)
            {
                errormsg.append("Commission mapping updated successfully.");
            }
            else
            {
                errormsg.append("Commission updation failed.");
            }
        }
        catch (PZDBViolationException pzdve)
        {
             log.error(pzdve);
            errormsg.append(""+pzdve.getMessage());
        }
        finally
        {
           Database.closeConnection(conn);
        }

        req.setAttribute("errormessage",errormsg.toString());
        RequestDispatcher rd = req.getRequestDispatcher("/actionPartnerCommission.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
}

