import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ChargeManager;
import com.manager.TerminalManager;
import com.manager.dao.AgentDAO;
import com.manager.dao.ChargesDAO;
import com.manager.dao.PayoutDAO;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.AgentCommissionVO;
import com.manager.vo.ChargeVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.payoutVOs.ChargeMasterVO;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 10/10/2015.
 */
public class ManageAgentCommission extends HttpServlet
{
    Logger logger = new Logger(ManageAgentCommission.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        doProcess(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        doProcess(request,response);
    }
    public void doProcess(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;

        TerminalManager terminalManager = new TerminalManager();
        StringBuilder sbError = new StringBuilder();
        StringBuilder validationBuffer = new StringBuilder();
        RequestDispatcher rd = request.getRequestDispatcher("/manageAgentCommission.jsp?ctoken=" + user.getCSRFToken());
        String status = "";
        CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();

        try
        {
            String commissionIds = request.getParameter("commissionids");
            String commissionIdsArr[] = commissionIds.split("\\|");
            String memberId = request.getParameter("memberid");
            String agentId = request.getParameter("agentid");
            String terminalId = request.getParameter("terminalid");
            String fromTerminal=request.getParameter("fromTerminal");
            String toTerminal=request.getParameter("toTerminal");
            String checked=request.getParameter("copyCharge");
            if(!functions.isValueNull(terminalId) && functions.isValueNull(toTerminal)){
                terminalId=toTerminal;
            }
            String deleteids=request.getParameter("deletedId");
            String deletedId[]=deleteids.split(",");
            List<String> list=new ArrayList<>();
            for (String commissionid:commissionIdsArr){
                String id=commissionid.split("-")[0];
                list.add(commissionid);
                for(String dl:deletedId){
                    if(id.equals(dl)){
                        list.remove(commissionid);
                    }
                }
            }
            validation(validationBuffer, request);
            if (validationBuffer.length() > 0)
            {
                request.setAttribute("sberror", validationBuffer.toString());
                logger.error("ErrorMSg---"+validationBuffer.toString());
                request.setAttribute("commissionids1", commissionIds);
                if(validationBuffer.toString().equals("Commissions Not Found.Please select the at least one Commission<BR>")){
                    request.setAttribute("commissionids1", null);
                }
                rd = request.getRequestDispatcher("/manageAgentCommission.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }

            for(String id2 : list)
            {
                String id = id2.split("-")[0];
                String startDate = request.getParameter("fromdate_" + id);
                String endDate = request.getParameter("todate_" + id);

                String message = commonFunctionUtil.newValidateDate(request.getParameter("fromdate_" + id), request.getParameter("todate_" + id), null, null);
                if (functions.isValueNull(message))
                {
                    request.setAttribute("sberror", message.toString());
                    request.setAttribute("commissionids1", commissionIds);
                    rd = request.getRequestDispatcher("/manageAgentCommission.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
            }
            for (String id1 : list)
            {
                String id = id1.split("-")[0];
                String commissionId = request.getParameter("commissionId_" + id);
                String agentCommission = request.getParameter("agentCommission_" + id);
                String sequenceNumber = request.getParameter("sequenceNumber_" + id);
                String startDate = request.getParameter("fromdate_" + id);
                String endDate = request.getParameter("todate_" + id);

                TerminalVO terminalVO = terminalManager.getTerminalByTerminalId(terminalId);
                String timestampStartDate = commonFunctionUtil.convertDatepickerToTimestamp(startDate, "00:00:00");
                String timestampEndDate = commonFunctionUtil.convertDatepickerToTimestamp(endDate, "23:59:59");

                ChargeManager chargeManager = new ChargeManager();
                AgentDAO agentDAO=new AgentDAO();
                ChargeMasterVO chargeMasterVO = chargeManager.getBusinessChargeDetails(commissionId);
                AgentCommissionVO agentCommissionVO=new AgentCommissionVO();
                PayoutDAO payoutDAO=new PayoutDAO();
                if(functions.isValueNull(commissionId)){
                    if(chargeMasterVO!=null && (chargeMasterVO.getKeyword().equals("DomesticTotal") || chargeMasterVO.getKeyword().equals("InternationalTotal")))
                    {
                        String country=payoutDAO.getGatewayCountryFromAccountId(terminalVO.getAccountId());
                        if(!functions.isValueNull(country)){
                            request.setAttribute("sberror","Gateway Country not found OR Please map the country on gateway.");
                            request.setAttribute("commissionids1", commissionIds);
                            rd = request.getRequestDispatcher("/manageAgentCommission.jsp?ctoken=" + user.getCSRFToken());
                            rd.forward(request, response);
                            return;
                        }
                    }
                }

                agentCommissionVO.setAgentId(agentId);
                agentCommissionVO.setMemberId(memberId);
                agentCommissionVO.setTerminalId(terminalVO.getTerminalId());
                agentCommissionVO.setChargeId(commissionId);
                agentCommissionVO.setChargeValue(agentCommission);
                agentCommissionVO.setStartDate(timestampStartDate);
                agentCommissionVO.setEndDate(timestampEndDate);
                agentCommissionVO.setActionExecutorId(actionExecutorId);
                agentCommissionVO.setActionExecutorName(actionExecutorName);
                agentCommissionVO.setSequenceNo(sequenceNumber);

                boolean isChargeAppliedOnAgent = agentDAO.isChargeAppliedOnAgent(agentCommissionVO);
                if (!isChargeAppliedOnAgent)
                {
                    chargeManager.deleteCommissionDetails(agentCommissionVO);
                }
                boolean checkAvailability = chargeManager.checkAgentSequenceNoAvailability(agentCommissionVO);
                if (checkAvailability)
                {
                    status = chargeManager.insertNewAgentCommission(agentCommissionVO);
                    if ("success".equals(status))
                    {
                        request.setAttribute("sberror", "Commission mapped successfully.");
                        continue;
                    }
                    else
                    {
                        request.setAttribute("sberror","Invalid member-agent configuration.");
                        continue;
                    }
                }
                else
                {
                    request.setAttribute("sberror","Sequence number is already allocated on same terminal,please provide unique." + " " + sequenceNumber);
                    request.setAttribute("commissionids1", commissionIds);
                    rd = request.getRequestDispatcher("/manageAgentCommission.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
            }
        }

        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException::::" , e);
            sbError.append("Internal error while adding new charge on member account.");
            request.setAttribute("sberror", sbError.toString());
            rd.forward(request, response);
            return;
        }
        catch (Exception e)
        {
            logger.error("Exception::::" , e);
            sbError.append("Internal error while adding new charge on member account.");
            request.setAttribute("sberror", sbError.toString());
            rd.forward(request, response);
            return;
        }


        rd = request.getRequestDispatcher("/manageAgentCommission.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(request, response);
        return;
    }

    public void validation(StringBuilder validationBuffer, HttpServletRequest request)
    {
        Functions functions=new Functions();
        String commissionIds = request.getParameter("commissionids");
        String commissionIdsArr[] = commissionIds.split("\\|");
        String deleteids=request.getParameter("deletedId");
        String deletedId[]=deleteids.split(",");

        //String agentid = ;
        // System.out.println("agentid=="+agentid);
        String memberId = request.getParameter("memberid");
        String terminalId = request.getParameter("terminalid");
        StringBuffer vb=new StringBuffer();
        boolean isValid=true;
        String EOL = "<BR>";

        /*String toTerminal=request.getParameter("toTerminal");
        if(!functions.isValueNull(terminalId) && functions.isValueNull(toTerminal)){
            terminalId=toTerminal;
        }*/
        List<String> list=new ArrayList<>();
        for (String commissionid:commissionIdsArr){
            String id=commissionid.split("-")[0];
            list.add(commissionid);
            for(String dl:deletedId){
                if(id.equals(dl)){
                    list.remove(commissionid);
                }
            }
        }
        if(list==null || list.size()==0){
            isValid=false;
            vb.append( "Commission Not Found.Please select the at least one commission" );
            validationBuffer.append(vb.toString() + EOL);
        }

        List sequenceList = new ArrayList<>();
        for (String id1 : list)
        {
            String id = id1.split("-")[0];
            String commissionName = id1.split("-")[1];
            String commissionId = request.getParameter("commissionId" + id);
            String agentCommission = request.getParameter("agentCommission_" + id);
            String startDate = request.getParameter("fromdate_" + id);
            String endDate = request.getParameter("todate_" + id);
            String isInputRequired = request.getParameter("isinputrequired_" + id);
            String sequenceNumber = request.getParameter("sequenceNumber_" + id);

            if (!ESAPI.validator().isValidInput("memberid", memberId, "Numbers", 20, false) || "0".equals(request.getParameter("memberid")))
            {
                isValid=false;
                vb.append( "Invalid Member Id." );
            }

            if (!ESAPI.validator().isValidInput("terminalid", terminalId, "Numbers", 5, false) || "0".equals(request.getParameter("terminalid")))
            {
                isValid=false;
                vb.append( "Invalid Terminal Id.");
            }
            /*if (!ESAPI.validator().isValidInput("commissionId", commissionId, "Numbers", 50, false))
            {
                isValid=false;
                vb.append("Invalid Commission Name." );
            }*/
            if (!ESAPI.validator().isValidInput("agentCommission", agentCommission, "AmountStr", 20, false))
            {
                isValid=false;
                vb.append("Invalid Commission fee." );
            }
            if (!ESAPI.validator().isValidInput("startDate", startDate, "fromDate", 20, false))
            {
                isValid=false;
                vb.append( "Invalid Start Date.");
            }
            if (!ESAPI.validator().isValidInput("endDate", endDate, "toDate", 20, false))
            {
                isValid=false;
                vb.append("Invalid End Date." );
            }
            if (!ESAPI.validator().isValidInput("sequenceNumber", sequenceNumber, "Numbers", 10, false))
            {
                isValid=false;
                vb.append( "Invalid Sequence Number" );
            }
            if(sequenceList.contains(sequenceNumber))
            {
                isValid = false;
                vb.append("Invalid Sequence Number"+EOL);
            }
            else
            {
                sequenceList.add(sequenceNumber);
            }
            if (isValid == false)
            {
                validationBuffer.append(commissionName + "-" + vb.toString() + EOL);
            }
        }
    }
}
