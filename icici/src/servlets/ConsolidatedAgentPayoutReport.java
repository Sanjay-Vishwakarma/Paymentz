import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.*;
import com.manager.dao.*;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.*;
import com.manager.vo.merchantmonitoring.DateVO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Mahima on 6/17/2017.
 */
public class ConsolidatedAgentPayoutReport extends HttpServlet
{
    private static Logger log = new Logger(ConsolidatedAgentPayoutReport.class.getName());
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

        RequestDispatcher rd = req.getRequestDispatcher("/consolidatedAgentReport.jsp?&ctoken=" + user.getCSRFToken());
        String agentId=req.getParameter("agentid");
        String startDate=req.getParameter("startDate");
        String endDate=req.getParameter("endDate");
        String startTime=req.getParameter("starttime");
        String endTime=req.getParameter("endtime");

        ConsolidatedPayoutManager consolidatedPayoutManager = new ConsolidatedPayoutManager();
        TerminalManager terminalManager = new TerminalManager();
        TreeMap<String,TerminalVO> stringListHashMap=new TreeMap<>();
        Set<String> processingCurrencySet=new HashSet<>();
        StringBuilder sErrorMessage = new StringBuilder();
        /*SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");*/
        Functions functions=new Functions();
        String statusMsg = "";
        String EOL = "<br>";

        try
        {
            String action=req.getParameter("action");
            if(functions.isValueNull(action) && action.equals("getinfo")){
                if (!ESAPI.validator().isValidInput("agentid", agentId, "Numbers", 6, false))
                {
                    log.debug("Invalid AgentId");
                    sErrorMessage.append("Invalid Agent Id OR please provide the Agent Id" + EOL);
                    req.setAttribute("statusMsg",sErrorMessage.toString());
                    rd.forward(req, res);
                    return;
                }
                String previousWireEndDate=terminalManager.getPreviousWireDetails(agentId);
                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                String date=null;
                String time=null;
                if(functions.isValueNull(previousWireEndDate))
                {
                    cal.setTime(targetFormat.parse(previousWireEndDate));
                    cal.add(Calendar.SECOND, 1);
                    previousWireEndDate = targetFormat.format(cal.getTime());
                    CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
                    String[] dateTimePicker = commonFunctionUtil.convertTimestampToDateTimePicker(previousWireEndDate);

                    date = dateTimePicker[0];
                    time = dateTimePicker[1];
                }
                req.setAttribute("startDate",date);
                req.setAttribute("starttime",time);
                rd.forward(req, res);
                return;
            }
            if (!ESAPI.validator().isValidInput("agentid", agentId, "Numbers", 6, false))
            {
                log.debug("Invalid AgentId");
                sErrorMessage.append("Invalid Agent Id OR please provide the Agent Id" + EOL);
            }
            if (!ESAPI.validator().isValidInput("startDate", startDate, "fromDate", 25, false))
            {
                log.debug("Invalid start date or start date should not be empty");
                sErrorMessage.append("Invalid start date or start date should not be empty" + EOL);
            }
            if (!ESAPI.validator().isValidInput("endDate", endDate, "fromDate", 25, false))
            {
                log.debug("Invalid start date or start date should not be empty");
                sErrorMessage.append("Invalid end date or end date should not be empty" + EOL);
            }
            if (!ESAPI.validator().isValidInput("starttime", startTime, "time", 25, false))
            {
                log.debug("Invalid start time or start time should not be empty");
                sErrorMessage.append("Invalid start time or start time should not be empty" + EOL);
            }
            if (!ESAPI.validator().isValidInput("endtime", endTime, "time", 25, false))
            {
                log.debug("Invalid start date or start date should not be empty");
                sErrorMessage.append("Invalid end time or end time should not be empty" + EOL);
            }
            /*if(fromDate<toDate){
                log.debug("End date should not be greater then start date.");
                sErrorMessage.append("End date should not be greater then start date." + EOL);
            }*/
            if(sErrorMessage.length()>0){
                req.setAttribute("statusMsg",sErrorMessage.toString());
                req.setAttribute("startDate",startDate);
                req.setAttribute("starttime",startTime);
                rd.forward(req, res);
                return;
            }
            String fromDate = String.valueOf(convertStringToTimestamp(startDate+" "+startTime));
            String toDate = String.valueOf(convertStringToTimestamp(endDate + " " + endTime));
            String[] terminalIds=req.getParameterValues("terminalId");
            List<TerminalVO> totalTerminalList = terminalManager.getTerminalDetailsForAgent(agentId,fromDate,toDate);
            List<TerminalVO> settledList=terminalManager.getSettledTerminalListForConsolidatedAgent(agentId,fromDate,toDate);
            List<TerminalVO> pendingList=new ArrayList<>();

            if(functions.isValueNull(action))
            {
                if(action.equals("proceed"))
                {
                    HashMap<String, String> currencyConversionRate = new HashMap();
                    processingCurrencySet= (Set<String>) session.getAttribute("processingCurrencySet");

                    String conversionCurr=null;
                    String settlementCurrency="EUR";
                    if(processingCurrencySet!=null)
                    {
                        for (String proccessCurr : processingCurrencySet)
                        {
                            if (proccessCurr.contains(settlementCurrency))
                            {
                                currencyConversionRate.put(proccessCurr + "_" + settlementCurrency, "1.00");
                            }
                            if (!proccessCurr.contains(settlementCurrency))
                                conversionCurr = proccessCurr;
                            else
                                currencyConversionRate.put(proccessCurr + "_" + settlementCurrency, "1.00");

                            String conversionRate = req.getParameter(proccessCurr + "_" + settlementCurrency + "_conversion_rate");
                            if (!ESAPI.validator().isValidInput(conversionCurr + "_" + settlementCurrency + "_conversion_rate", conversionRate, "NDigitsAmount", 20, false))
                            {
                                sErrorMessage.append("Invalid " + conversionCurr + " conversion rate for " + settlementCurrency + "<BR>");
                            }
                            else
                            {
                                currencyConversionRate.put(conversionCurr + "_" + settlementCurrency, conversionRate);
                            }
                        }
                    }
                    TreeMap<String,TerminalVO> stringListHashMapNew= (TreeMap<String,TerminalVO >) session.getAttribute("stringListHashMap");
                    List<String> result=consolidatedPayoutManager.agentCommissionConsolidatedReport(fromDate, toDate, currencyConversionRate, stringListHashMapNew, pendingList);
                    req.setAttribute("startDate",startDate);
                    req.setAttribute("starttime",startTime);
                    req.setAttribute("result",result);
                    rd.forward(req,res);
                    return;
                }
            }

            if (terminalIds == null && totalTerminalList!=null && totalTerminalList.size()>0 )
            {
                req.setAttribute("terminalList", totalTerminalList);
                req.setAttribute("settledList", settledList);
                req.setAttribute("startDate",startDate);
                req.setAttribute("starttime",startTime);
                //req.setAttribute("filterList", filterList);
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

            TerminalVO terminalVO1=null;
            if(terminalIds!=null)
            {
                String agentId1="";
                for(String terminalWireId:terminalIds)
                {
                    terminalVO1=new TerminalVO();
                    agentId1=req.getParameter("agentid_" + terminalWireId)+"_"+terminalWireId;
                    System.out.println("All sets in servlet" +  agentId1);
                    terminalVO1.setTerminalId(req.getParameter("terminalid_" + terminalWireId));
                    terminalVO1.setAccountId(req.getParameter("accountid_" + terminalWireId));
                    terminalVO1.setMemberId(req.getParameter("memberid_" + terminalWireId));
                    terminalVO1.setAgentId(req.getParameter("agentid_" + terminalWireId));
                    terminalVO1.setWireId(req.getParameter("wireid_" + terminalWireId));
                    terminalVO1.setStartDate(req.getParameter("startdt_" + terminalWireId));
                    terminalVO1.setEndDate(req.getParameter("enddt_" + terminalWireId));
                    terminalVO1.setPaymodeId(req.getParameter("paymodeid_" + terminalWireId));
                    terminalVO1.setCardTypeId(req.getParameter("cardtypeid_" + terminalWireId));
                    stringListHashMap.put(agentId1,terminalVO1);
                }
            }

            log.error("stringListHashMap:::::"+stringListHashMap);
            log.error("PendingList Size on AgentPayout:::::"+pendingList.size());
            String cyclememberlist = null;
            List<String> stringList = new ArrayList<String>();

            String isSettlementCronExecuted = "Y";
            String isAgentCommissionCronExecuted = "N";
            String isPayoutCronExcuted="Y";

            TransactionManager transactionManager = new TransactionManager();
            ChargeManager chargeManager = new ChargeManager();
            PayoutDAO payoutDAO=new PayoutDAO();

            Set<String> key=stringListHashMap.keySet();

            for(String agentTerminalWireId:key)
            {
                TerminalVO terminalVO2 = stringListHashMap.get(agentTerminalWireId);
                String memberId = terminalVO2.getMemberId();
                String terminalId = terminalVO2.getTerminalId();
                String accountId = terminalVO2.getAccountId();
                String wireId=terminalVO2.getWireId();
                BankWireManagerVO bankWireManagerVO = payoutDAO.getBankWireListForAgentCommissionCron(wireId, isSettlementCronExecuted, isAgentCommissionCronExecuted,isPayoutCronExcuted);
                if (bankWireManagerVO == null)
                {
                    cyclememberlist = wireId + ":" + memberId + ":" + accountId + ":"+ terminalVO2.getAgentId() + ":"+terminalId+":Failed:No New Wire To Execute Agent Commission Cron";
                    stringList.add(cyclememberlist);
                    log.debug(cyclememberlist);
                    continue;
                }

                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO2.getAccountId());
                DateVO dateVO = new DateVO();
                dateVO.setStartDate(bankWireManagerVO.getServer_start_date());
                dateVO.setEndDate(bankWireManagerVO.getServer_end_date());

                boolean pendingTransaction = transactionManager.checkPendingTransactionOfMerchant(gatewayAccount, memberId, terminalId, dateVO);
                if (pendingTransaction)
                {
                    cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":"+ terminalVO2.getAgentId() + ":"+terminalId+":Failed:Transaction status need to be corrected";
                    stringList.add(cyclememberlist);
                    log.debug(cyclememberlist);
                    continue;
                }
                //terminalVO2 = terminalManager.getTerminalsByMemberAccountIdForPayoutReportRequest(memberId, accountId, terminalId);
                String memberFirstTransactionDateOnTerminal = payoutDAO.getMemberFirstTransactionOnTerminal(terminalVO2);
                if (functions.isValueNull(memberFirstTransactionDateOnTerminal))
                {
                    List<AgentCommissionVO> agentCommissionVOs = chargeManager.getAgentCommissionOnTerminal(terminalVO2.getAgentId(), memberId, terminalVO2.getTerminalId());
                    if (agentCommissionVOs.size()== 0)
                    {
                        cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":"+ terminalVO2.getAgentId() + ":"+ terminalVO2.getTerminalId() + ":Failed:No Charges Found On Terminal";
                        stringList.add(cyclememberlist);
                        log.debug(stringList);
                        continue;
                    }
                    else {
                        session.setAttribute("stringListHashMap",stringListHashMap);
                        cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":" + terminalVO2.getAgentId()+":"+terminalVO2.getTerminalId() + ":Success:You Can Proceed For this";
                        AccountDAO accountDAO=new AccountDAO();
                        String processingCurrency=accountDAO.getProcessingCurrency(accountId);
                        processingCurrencySet.add(processingCurrency);
                        log.debug(cyclememberlist);
                        stringList.add(cyclememberlist);
                        continue;
                    }
                }
                else
                {
                    cyclememberlist = bankWireManagerVO.getBankwiremanagerId() + ":" + memberId + ":" + accountId + ":"+ terminalVO2.getAgentId() + ":"+ terminalVO2.getTerminalId() + ":Failed:No Transaction Found";
                    stringList.add(cyclememberlist);
                    log.debug(cyclememberlist);
                    continue;
                }
            }
            req.setAttribute("stringList", stringList);
            req.setAttribute("stringListHashMap",stringListHashMap);
            req.setAttribute("processingCurrencySet",processingCurrencySet);
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
        req.setAttribute("startDate",startDate);
        req.setAttribute("starttime",startTime);
        rd.forward(req, res);
        return;
    }
    private static Timestamp convertStringToTimestamp(String date) throws ParseException
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        Date parsedDate = null;
        parsedDate = dateFormat.parse(date);
        Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
        return timestamp;
    }
}