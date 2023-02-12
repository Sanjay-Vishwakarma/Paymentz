import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.manager.dao.AgentDAO;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.payoutVOs.AgentWireVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Pranav on 19-04-2017.
 */
public class UpdateAgentWire extends HttpServlet
{
     Logger logger=new Logger(UpdateAgentWire.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
    {

            logger.debug("Entering Into UpdateAgentWire");
            HttpSession session = request.getSession();

            User user =  (User)session.getAttribute("ESAPIUserSessionKey");

            if (!Admin.isLoggedIn(session))
            {
                logger.debug("Admin is logout ");
                response.sendRedirect("/icici/logout.jsp");
                return;
            }

            String errormsg = "";
            String EOL = "<BR>";
            String mailSent = "";
            StringBuffer sberror = new StringBuffer();


            if (!ESAPI.validator().isValidInput("settledate", request.getParameter("settledate"), "fromDate", 16, false))
            {

               sberror.append("Invalid Settle Date"+EOL);
            }
            if ("".equals(request.getParameter("id")))
            {
                sberror.append(" Invalid Settled ID");
            }
            if (!ESAPI.validator().isValidInput("agentchargeamount", request.getParameter("agentchargeamount"), "AmountMinus", 12, false) || request.getParameter("agentchargeamount").length() > 12)
            {
                sberror.append("Invalid Agent Charge Amount");
            }
            if (!ESAPI.validator().isValidInput("agentunpaidamount", request.getParameter("agentunpaidamount"), "AmountMinus", 12, false) || request.getParameter("agentunpaidamount").length() > 12)
            {
                sberror.append("Invalid Agent Unpaid Amount ");
            }
            if (!ESAPI.validator().isValidInput("agenttotalfundedamount", request.getParameter("agenttotalfundedamount"), "AmountMinus", 12, false) || request.getParameter("agenttotalfundedamount").length() > 12)
            {
                sberror.append("Invalid Agent Total Funded Amount ");
            }

            if(sberror.length()>0)
        {
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + sberror.toString() + EOL + "</b></font></center>";
            request.setAttribute("message",errormsg);
            RequestDispatcher rd = request.getRequestDispatcher("/agentWireList.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Calendar cal = Calendar.getInstance();

            String wireId=request.getParameter("id");
            String settlementDate=request.getParameter("settledate");
            double agentChargeAmount=Double.valueOf(request.getParameter("agentchargeamount"));
            double agentUnpaidAmount=Double.valueOf(request.getParameter("agentunpaidamount"));
            double agentTotalFundedAmount=Double.valueOf(request.getParameter("agenttotalfundedamount"));
            String status=request.getParameter("status");

            CommonFunctionUtil commonFunctionUtil= new CommonFunctionUtil();
            String settleDateTimestamp=commonFunctionUtil.convertDatepickerToTimestamp(settlementDate,dateFormat.format(cal.getTime()) );

            AgentWireVO agentWireVO=new AgentWireVO();
            agentWireVO.setSettledId(wireId);
            agentWireVO.setSettleDate(settleDateTimestamp);
            agentWireVO.setAgentChargeAmount(agentChargeAmount);
            agentWireVO.setAgentUnpaidAmount(agentUnpaidAmount);
            agentWireVO.setAgentTotalFundedAmount(agentTotalFundedAmount);
            agentWireVO.setStatus(status);

            AgentDAO agentDAO=new AgentDAO();
            agentDAO.updateAgentWire(agentWireVO);

            if(agentWireVO.isUpdated())
            {
                //Call Payout Mail Email With Attachment
                errormsg += "<center><font class=\"textb\" face=\"arial\"><b>Wire Updated Successfully</b></font></center>";
            }
            else
            {
                errormsg += "<center><font class=\"textb\" face=\"arial\"><b>Wire Updation Failed</b></font></center>";
            }
            request.setAttribute("message", errormsg);
            RequestDispatcher rd = request.getRequestDispatcher("/agentWireList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);

        }
    }

