import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.PayoutManager;
import com.manager.dao.AgentDAO;
import com.manager.vo.AgentDetailsVO;
import com.manager.vo.SettlementDateVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.payoutVOs.*;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User:sandip
 * Date: 12/12/14
 * Time: 4:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenerateAgentWire extends HttpServlet
{
    Logger  logger=new Logger("test");
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
      doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        logger.debug("Entering Into Generate AgentWire Servlet");

        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        try
        {
            AgentPayoutReportVO agentPayoutReportVO=(AgentPayoutReportVO)session.getAttribute("agentPayoutReportVO");

            AgentDetailsVO agentDetailsVO=agentPayoutReportVO.getAgentDetailsVO();
            TerminalVO terminalVO=agentPayoutReportVO.getTerminalVO();
            SettlementDateVO settlementDateVO=agentPayoutReportVO.getSettlementDateVO();

            AgentDAO agentDAO=new AgentDAO();
            Date date=new  Date();

            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            PayoutManager payoutManager=new PayoutManager();

            String agentPayoutPDFFileName=payoutManager.createAgentReportFile(agentPayoutReportVO);

            AgentWireVO agentWireVO=new AgentWireVO();

            //agentWireVO.setSettleDate(dateFormat.format(date));
            agentWireVO.setSettlementStartDate(settlementDateVO.getSettlementStartDate());
            agentWireVO.setSettlementEndDate(settlementDateVO.getSettlementEndDate());
            agentWireVO.setAgentChargeAmount(agentPayoutReportVO.getAgentTotalChargesAmount());
            agentWireVO.setAgentUnpaidAmount(agentPayoutReportVO.getAgentWireUnpaidAmount());
            agentWireVO.setAgentTotalFundedAmount(agentPayoutReportVO.getAgentTotalFundedAmount());
            agentWireVO.setCurrency(agentPayoutReportVO.getCurrency());
            agentWireVO.setStatus("unpaid");
            agentWireVO.setSettlementReportFileName(agentPayoutPDFFileName);
            agentWireVO.setMarkedForDeletion("N");
            agentWireVO.setAgentId(agentDetailsVO.getAgentId());
            agentWireVO.setMemberId(terminalVO.getMemberId());
            agentWireVO.setAccountId(terminalVO.getAccountId());
            agentWireVO.setTerminalId(terminalVO.getTerminalId());
            agentWireVO.setPayModeId(terminalVO.getPaymodeId());
            agentWireVO.setCardTypeId(terminalVO.getCardTypeId());
            agentWireVO.setDeclinedCoverDateUpTo(settlementDateVO.getDeclinedEndDate());
            agentWireVO.setReversedCoverDateUpTo(settlementDateVO.getReversedEndDate());
            agentWireVO.setChargebackCoverDateUpTo(settlementDateVO.getChargebackEndDate());

            String wireStatus=payoutManager.createAgentWire(agentWireVO);

            request.setAttribute("wireStatus",wireStatus);
            RequestDispatcher rd = request.getRequestDispatcher("/agentPayoutReport.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request,response);
            logger.debug("forward by GenerateAgentWire.....");
        }
        catch (SystemError systemError)
        {
          logger.error("SystemError===="+systemError);
        }
        catch (SQLException se)
        {
          logger.error("SQLException===="+se);
        }
        catch (Exception e)
        {
          logger.error("GenericException===="+e);
        }

    }
}
