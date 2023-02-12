import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.ChargeManager;
import com.manager.vo.AgentCommissionVO;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ActionAgentCommission extends HttpServlet
{
    private static Logger log = new Logger(ActionAgentCommission.class.getName());
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

        String errormsg="";

        String action=req.getParameter("action");
        String id=req.getParameter("commissionid");
        ChargeManager chargeManager=new ChargeManager();
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        ResultSet rs2 = null;

        String id1 = req.getParameter("id");
        AgentCommissionVO agentCommissionVO = new AgentCommissionVO();
        try
        {
            conn = Database.getRDBConnection();
            String qry = "";

            if ("history".equalsIgnoreCase(action))
            {
                qry = "SELECT * FROM agent_commission WHERE id=?";
                pstmt = conn.prepareStatement(qry);
                pstmt.setString(1, id1);
                rs = pstmt.executeQuery();
                int i = 1;
                while (rs.next())
                {
                    agentCommissionVO.setCommissionId(rs.getString("id"));
                    agentCommissionVO.setAgentId(rs.getString("agentid"));
                    agentCommissionVO.setMemberId(rs.getString("memberid"));
                    agentCommissionVO.setTerminalId(rs.getString("terminalid"));
                    agentCommissionVO.setChargeId(rs.getString("chargeid"));
                    agentCommissionVO.setChargeValue(rs.getString("commission_value"));
                    agentCommissionVO.setStartDate(rs.getString("startdate"));
                    agentCommissionVO.setEndDate(rs.getString("enddate"));
                    agentCommissionVO.setSequenceNo(rs.getString("sequence_no"));
                    i = i + 1;
                }

                req.setAttribute("agentCommissionVO1",agentCommissionVO);
            }
        }
        catch (SystemError systemError)
        {
           log.error("Catch SystemError...",systemError);
        }
        catch (SQLException e)
        {
           log.error("Catch SQLException....",e);
        }

        if(action.equalsIgnoreCase("modify"))
        {
            try
            {
                agentCommissionVO=chargeManager.getAgentCommissionDetails(id);

            }
            catch (PZDBViolationException  e)
            {
                errormsg="<center><font class=\"text\" face=\"arial\"><b>"+e.getMessage()+"</b></font></center>";
            }
        }
        /*else
        {
            errormsg = "<center><font class=\"text\" face=\"arial\"><b>Action is not defined</b></font></center>";
        }*/
        req.setAttribute("action",action);
        req.setAttribute("agentCommissionVO",agentCommissionVO);
        req.setAttribute("errormessage",errormsg);
        RequestDispatcher rd = req.getRequestDispatcher("/actionAgentCommission.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
}
