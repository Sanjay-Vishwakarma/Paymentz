import com.directi.pg.*;
import com.manager.ChargeManager;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.AgentCommissionVO;
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

public class UpdateAgentCommission  extends HttpServlet
{
    private static Logger log = new Logger(UpdateAgentCommission.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Functions functions=new Functions();

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        Connection conn = null;
        StringBuffer errormsg=new StringBuffer();
        RequestDispatcher rd = req.getRequestDispatcher("/actionAgentCommission.jsp?ctoken="+user.getCSRFToken());

        String commissionValue=req.getParameter("commissionvalue");
        String startDate=req.getParameter("startdate");
        String endDate=req.getParameter("enddate");
        String commissionId=req.getParameter("commissionid");
        CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
        String message=commonFunctionUtil.newValidateDate(startDate,endDate,null,null);
        if (functions.isValueNull(message))
        {
            req.setAttribute("errormessage", message);
            rd.forward(req, res);
            return;
        }



        if (!ESAPI.validator().isValidInput("comissionvalue",commissionValue,"AmountStr", 10, false))
        {
            errormsg.append("Invalid commission value<BR>");
        }
        if(!ESAPI.validator().isValidInput("startdate", req.getParameter("startdate"), "SafeString", 100, false))
        {
            errormsg.append("Invalid Start Date<BR>");
        }
        if(!ESAPI.validator().isValidInput("enddate", req.getParameter("enddate"), "SafeString", 100, false))
        {
            errormsg.append("Invalid End Date<BR>");
        }
        if(errormsg.length()>0)
        {
            req.setAttribute("errormessage",errormsg.toString());
            rd.forward(req, res);
            return;
        }
        try
        {
            AgentCommissionVO agentCommissionVO=new AgentCommissionVO();
            agentCommissionVO.setCommissionValue(Double.valueOf(commissionValue));
            agentCommissionVO.setStartDate(commonFunctionUtil.convertDatepickerToTimestamp(startDate,"00:00:00"));
            agentCommissionVO.setEndDate(commonFunctionUtil.convertDatepickerToTimestamp(endDate,"23:59:59"));
            agentCommissionVO.setCommissionId(commissionId);

            ChargeManager chargeManager=new ChargeManager();
            boolean b=chargeManager.updateAgentCommissionValue(agentCommissionVO);
            if(b)
            {
                errormsg.append("Commission Updated Successfully.");
            }
            else
            {
                errormsg.append("Commission Updation Failed.");
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
        rd.forward(req, res);
    }
}
