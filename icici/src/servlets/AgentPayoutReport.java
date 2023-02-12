import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.ChargeManager;
import com.manager.PayoutManager;
import com.manager.TerminalManager;
import com.manager.dao.PayoutDAO;
import com.manager.vo.BankWireManagerVO;
import com.manager.vo.ChargeVO;
import com.manager.vo.TerminalVO;
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
import java.util.*;

/**
 * Created by Mahima on 6/17/2017.
 */
public class AgentPayoutReport extends HttpServlet
{
    private static Logger log = new Logger(AgentPayoutReport.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        RequestDispatcher rd = req.getRequestDispatcher("/agentcommissioncron.jsp?&ctoken=" + user.getCSRFToken());
        String agentId=req.getParameter("agentid");
        String memberId=req.getParameter("memberid");
        String bankWireId = req.getParameter("bankwireid");

        PayoutManager payoutManager = new PayoutManager();
        TerminalManager terminalManager = new TerminalManager();
        HashMap<String,List<TerminalVO>> stringListHashMap=new HashMap<>();
        PayoutDAO payoutDAO = new PayoutDAO();
        Functions functions = new Functions();
        StringBuilder sErrorMessage = new StringBuilder();
        String statusMsg = "";
        String EOL = "<br>";

        if (!functions.isValueNull(bankWireId))
        {
            req.setAttribute("statusMsg", "Please select BankWire Id");
            rd.forward(req, res);
            return;
        }
        try
        {
            if (!ESAPI.validator().isValidInput("agentid", agentId, "Numbers", 6, true))
            {
                log.debug("Invalid AgentId");
                sErrorMessage.append("Invalid Agent ID" + EOL);
            }
            if (!ESAPI.validator().isValidInput("memberid", memberId, "Numbers", 50, true))
            {
                log.debug("Invalid memberid");
                sErrorMessage.append("Invalid member ID" + EOL);
            }
            if (!ESAPI.validator().isValidInput("accountid", req.getParameter("accountid"), "Numbers", 50, true))
            {
                log.debug("Invalid accountid");
                sErrorMessage.append("Invalid account ID" + EOL);
            }
            if (!ESAPI.validator().isValidInput("bankwireid", bankWireId, "Numbers", 6, false))
            {
                log.debug("Invalid bankwireid");
                sErrorMessage.append("Invalid Bankwire Id" + EOL);
            }
            if(sErrorMessage.length()>0){
                req.setAttribute("statusMsg",sErrorMessage.toString());
                rd.forward(req, res);
                return;
            }
            String accountId ="";
            BankWireManagerVO bankWireManagerVO = payoutDAO.getBankWireListForAgentCommissionCron(bankWireId, "Y", "N", "Y");
            if(bankWireManagerVO!=null) {
                accountId = bankWireManagerVO.getAccountId();
            }
            else
            {
                req.setAttribute("statusMsg","Active terminals not founds on account");
                rd.forward(req, res);
                return;
            }

            String[] terminalIds=req.getParameterValues("terminalId");
            List<TerminalVO> totalTerminalList = terminalManager.getTerminalsByAccountIdForAgent(accountId);
            List<TerminalVO> filterList=terminalManager.getFilterListBasedOnInput(agentId,memberId,accountId);
            List<TerminalVO> settledList=terminalManager.getSettledTerminalListForAgent(bankWireId);
            List<TerminalVO> pendingList=new ArrayList<>();

            if(filterList.size()==0){
                req.setAttribute("statusMsg","Active terminals not founds on account");
                rd.forward(req, res);
                return;
            }
            if (terminalIds == null && totalTerminalList!=null && totalTerminalList.size()>0 )
            {
                req.setAttribute("terminalList", totalTerminalList);
                req.setAttribute("settledList", settledList);
                req.setAttribute("filterList", filterList);
                rd.forward(req, res);
                return;
            }

            if(settledList.size()>0)
            {
                boolean isNotSettled=false;
                for (TerminalVO terminalVO : totalTerminalList)
                {
                    isNotSettled=false;
                    for (TerminalVO terminalVO1 : settledList)
                    {
                        if (terminalVO.getTerminalId().equals(terminalVO1.getTerminalId()) && terminalVO.getAgentId().equals(terminalVO1.getAgentId()))
                        {
                            isNotSettled=false;
                        }
                        else {
                            isNotSettled=true;
                        }
                    }
                    if(isNotSettled)
                    {
                        pendingList.add(terminalVO);
                    }
                }
            }
            else{
                pendingList=totalTerminalList;
            }

            List<TerminalVO> listOfTerminalReq=new ArrayList<>();
            TerminalVO terminalVO1=null;
            if(terminalIds!=null)
            {
                String agentId1="";
                for(String terminalId:terminalIds)
                {
                    terminalVO1=new TerminalVO();
                    if(functions.isValueNull(accountId) && !agentId1.equals(req.getParameter("agentid_" + terminalId))){
                        listOfTerminalReq=new ArrayList<>();
                    }
                    agentId1=req.getParameter("agentid_" + terminalId);
                    terminalVO1.setTerminalId(req.getParameter("terminalid_" + terminalId));
                    terminalVO1.setAccountId(req.getParameter("accountid_" + terminalId));
                    terminalVO1.setMemberId(req.getParameter("memberid_" + terminalId));
                    terminalVO1.setAgentId(req.getParameter("agentid_" + terminalId));
                    listOfTerminalReq.add(terminalVO1);
                    stringListHashMap.put(terminalVO1.getAgentId(),listOfTerminalReq);
                }
            }

            log.error("PendingList Size on AgentPayout:::::"+pendingList.size());
            List<String> stringList = payoutManager.agentCommissionCron(bankWireId, stringListHashMap, pendingList);
            req.setAttribute("result", stringList);
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException:::::", e);
            statusMsg = "Internal error while processing your request";
        }
        catch (Exception e)
        {
            log.error("Exception:::::", e);
            statusMsg = "Internal error while processing your request";
        }
        req.setAttribute("statusMsg", statusMsg);
        rd.forward(req, res);
        return;
    }
}