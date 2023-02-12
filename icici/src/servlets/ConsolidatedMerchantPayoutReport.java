import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.*;
import com.manager.dao.PayoutDAO;
import com.manager.vo.BankWireManagerVO;
import com.manager.vo.ChargeVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.merchantmonitoring.DateVO;
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
public class ConsolidatedMerchantPayoutReport extends HttpServlet
{
    private static Logger log = new Logger(ConsolidatedMerchantPayoutReport.class.getName());
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

        RequestDispatcher rd = req.getRequestDispatcher("/consolidatedReport.jsp?&ctoken=" + user.getCSRFToken());
        String bankWireId=req.getParameter("bankwireid");
        String parent_bankWireId = req.getParameter("parent_bankWireId");
        String cardtypeid = "";
        String paymodid = "";
        Functions functions=new Functions();

        if (functions.isValueNull(req.getParameter("cardtypeid")))
        {
            String cardtypeids = req.getParameter("cardtypeid");
            String cardtypeidAry[] = cardtypeids.split("-");
            cardtypeid = cardtypeidAry[0];
        }
        if (functions.isValueNull(req.getParameter("paymodid")))
        {
            String paymodeids = req.getParameter("paymodid");
            String paymodeid[] = paymodeids.split(" - ");
            paymodid = paymodeid[0];
        }
        String settlementCurrency = req.getParameter("processingcurrency");
        String dynamicMappingIds = req.getParameter("dynamicmappingids");
        ConsolidatedPayoutManager consolidatedPayoutManager=new ConsolidatedPayoutManager();
        TerminalManager terminalManager=new TerminalManager();
        ChargeManager chargeManager=new ChargeManager();
        PayoutDAO payoutDAO=new PayoutDAO();
        if(!functions.isValueNull(bankWireId) && !functions.isValueNull(parent_bankWireId)){
            req.setAttribute("statusMsg","Please select BankWire Id Or Parent BankWIre Id");
            rd.forward(req, res);
            return;
        }
        HashMap<TerminalVO,List<ChargeVO>> stringListHashMap=new HashMap();
        HashMap<String, String> currencyConversionRate = new HashMap();
        HashMap<String, String> dynamicCountAmountMap = new HashMap();
        Set<String> settlementCurrSet=new HashSet<>();

        BankWireManagerVO bankWireManagerVO=null;
        List<BankWireManagerVO> voList=null;
        List<GatewayAccount> accountList=new ArrayList<>();
        GatewayAccount gatewayAccount= null;
        String processingCurrency ="";
        List<TerminalVO> settledList = new ArrayList<>();
        String accountId="";
        String statusMsg = "";
        StringBuffer sbError = new StringBuffer();

        if (functions.isValueNull(parent_bankWireId)){
            voList=payoutDAO.getBankWireDetailsList(parent_bankWireId, "Y","N");
            if (voList!=null){
                for (BankWireManagerVO vo : voList){
                    gatewayAccount = GatewayAccountService.getGatewayAccount(vo.getAccountId());
                    processingCurrency=gatewayAccount.getCurrency();
                    accountList.add(gatewayAccount);
                    accountId += vo.getAccountId()+",";
                }
            }
        }else{
            bankWireManagerVO=payoutDAO.getBankWireDetails(bankWireId, "Y", "N");
            accountId = bankWireManagerVO.getAccountId();
            gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            processingCurrency=gatewayAccount.getCurrency();
        }
        try
        {
            String[] terminalIds=req.getParameterValues("terminalId");
            List<TerminalVO> terminalVOList = null;
            if (accountId.endsWith(",")){ accountId=accountId.replaceFirst(".$","");}
            if (functions.isValueNull(parent_bankWireId)){
                terminalVOList = terminalManager.getTerminalsByAccAndBankId(accountId, parent_bankWireId,cardtypeid, paymodid);}
            else{
                terminalVOList = terminalManager.getTerminalsByAccountID(accountId,cardtypeid, paymodid);
            }
            if (functions.isValueNull(parent_bankWireId)){
                if (voList!=null&& voList.size()>0){
                    for (BankWireManagerVO managerVO : voList){
                        bankWireId += managerVO.getBankwiremanagerId()+",";
                    }
                }
                if (bankWireId.endsWith(",")){ bankWireId = bankWireId.replaceFirst(".$","");}
                settledList=terminalManager.getSettledTerminalList(bankWireId, cardtypeid, paymodid);
            }else{
                settledList=terminalManager.getSettledTerminalList(bankWireId, cardtypeid, paymodid);
            }
            List<TerminalVO> pendingList=new ArrayList<>();

            String action=req.getParameter("action");

            if(functions.isValueNull(action))
            {
                if(action.equals("proceed"))
                {
                    List<String> result = null;
                    HashMap<String,List<TerminalVO>> getListOfMember= (HashMap<String, List<TerminalVO>>) session.getAttribute("getListDetails");
                    if (functions.isValueNull(parent_bankWireId)){
                        result=consolidatedPayoutManager.consolidatedMerchantPayoutReportBasedOnBankWire(parent_bankWireId,getListOfMember,pendingList);
                    } else{
                        result=consolidatedPayoutManager.consolidatedMerchantPayoutReportBasedOnBankWire(bankWireId,getListOfMember,pendingList);
                    }
                    req.setAttribute("result",result);
                    req.setAttribute("accountId", accountId);
                    rd.forward(req,res);
                    return;
                }
            }

            if(terminalIds==null && terminalVOList!=null && terminalVOList.size()>0 )
            {
                for (TerminalVO terminalVO : terminalVOList)
                {
                    List<ChargeVO> tempList = chargeManager.getDynamicChargesAsPerTerminal(terminalVO);
                    if (tempList.size() > 0) {
                        stringListHashMap.put(terminalVO, tempList);
                    }
                    if (!processingCurrency.equalsIgnoreCase(terminalVO.getSettlementCurrency())) {
                        settlementCurrSet.add(terminalVO.getSettlementCurrency());
                    }
                }

                if (functions.isValueNull(settlementCurrency))
                {
                    String settlementCurrencies[] = settlementCurrency.split(",");
                    for (String currency : settlementCurrencies)
                    {
                        String conversionRate = req.getParameter(currency + "_conversion_rate");
                        if (!ESAPI.validator().isValidInput(currency + "_conversion_rate", conversionRate, "NDigitsAmount", 20, false)) {
                            sbError.append("Invalid " + currency + " conversion_rate" + " Value<BR>");
                        }
                        else {
                            currencyConversionRate.put(currency, conversionRate);
                        }
                    }
                }
                if (sbError.length() > 0)
                {
                    req.setAttribute("processingCurrency", processingCurrency);
                    req.setAttribute("settlementCurrency", settlementCurrency);
                    req.setAttribute("settlementCurrSet", settlementCurrSet);
                    req.setAttribute("currencyConversionRate", currencyConversionRate);
                    req.setAttribute("statusMsg", sbError.toString());
                    req.setAttribute("accountId", accountId);
                    rd.forward(req, res);
                    return;
                }
                req.setAttribute("terminalList", terminalVOList);
                req.setAttribute("settledList", settledList);
                req.setAttribute("processingCurrency", processingCurrency);
                req.setAttribute("settlementCurrency", settlementCurrency);
                req.setAttribute("currencyConversionRate", currencyConversionRate);
                req.setAttribute("dynamicCountAmountMap", dynamicCountAmountMap);
                req.setAttribute("stringListHashMap", stringListHashMap);
                req.setAttribute("settlementCurrSet", settlementCurrSet);
                req.setAttribute("accountId", accountId);
                rd.forward(req, res);
                return;
            }

            if(settledList.size()>0) {
                for (TerminalVO terminalVO : terminalVOList) {
                    for (TerminalVO terminalVO1 : settledList) {
                        if (!terminalVO.getTerminalId().equals(terminalVO1.getTerminalId())) {
                            pendingList.add(terminalVO);
                        }
                    }
                }
            }
            else{
                pendingList=terminalVOList;
            }

            if (functions.isValueNull(dynamicMappingIds))
            {
                String dynamicMappingIdsArr[] = dynamicMappingIds.split(",");
                for (String mappingId : dynamicMappingIdsArr)
                {
                    ChargeVO chargeVO=chargeManager.getChargeDetails(mappingId);
                    if(chargeVO==null){
                        sbError.append("Charge not found:"+mappingId+"<BR>");
                        continue;
                    }
                    boolean validCounter=false;
                    boolean validAmount=false;
                    String chargeCounter = req.getParameter(mappingId+"_counter");
                    String chargeAmount = req.getParameter(mappingId+"_amount");

                    if(!"-".equalsIgnoreCase(chargeCounter)){
                        if(!ESAPI.validator().isValidInput(mappingId + "_count", chargeCounter, "NDigitsAmount", 20, true))
                        {
                            sbError.append("Invalid " + chargeVO.getChargename() + " count <BR>");
                        }
                        else{
                            validCounter=true;
                        }
                    }
                    else{
                        validCounter=true;
                    }

                    if (!ESAPI.validator().isValidInput(mappingId + "_amount", chargeAmount, "NDigitsAmount", 20, true))
                    {
                        sbError.append("Invalid " + chargeVO.getChargename() + " amount <BR>");
                    }
                    else{
                        validAmount=true;
                    }
                    if(validCounter && validAmount){
                        dynamicCountAmountMap.put(mappingId,chargeCounter+":"+chargeAmount);
                    }
                }
            }

            List<TerminalVO> listOfTerminalReq=new ArrayList<>();
            TerminalVO terminalVO1=null;
            String conversionRate="1.00";
            if(terminalIds!=null)
            {
                for(String terminalId:terminalIds)
                {
                    terminalVO1=new TerminalVO();
                    terminalVO1.setTerminalId(terminalId);
                    if (functions.isValueNull(parent_bankWireId) && functions.isValueNull(req.getParameter("wireId_" + terminalId))){
                    terminalVO1.setWireId(req.getParameter("wireId_" + terminalId));}
                    else{   terminalVO1.setWireId(bankWireId);   }
                    if (functions.isValueNull(parent_bankWireId) && functions.isValueNull(req.getParameter("parentWireId_" + terminalId))){
                        terminalVO1.setParentBankWireId(req.getParameter("parentWireId_" + terminalId));}
                    terminalVO1.setAccountId(req.getParameter("accountid_" + terminalId));
                    terminalVO1.setMemberId(req.getParameter("memberid_" + terminalId));
                    terminalVO1.setSettlementCurrency(req.getParameter("settlementCurrency_" + terminalId));
                    if(functions.isValueNull(req.getParameter("currencyConversionRate_" + terminalId))) {
                        conversionRate=req.getParameter("currencyConversionRate_" + terminalId);
                    }
                    terminalVO1.setConversionRate(Double.parseDouble(conversionRate));
                    listOfTerminalReq.add(terminalVO1);
                }
            }

            if(listOfTerminalReq!=null && listOfTerminalReq.size()>0)
            {
                settlementCurrSet=new HashSet();
                for (TerminalVO terminalVO : listOfTerminalReq) {
                    if (!processingCurrency.equalsIgnoreCase(terminalVO.getSettlementCurrency())) {
                        settlementCurrSet.add(terminalVO.getSettlementCurrency());
                    }
                }
            }
            else{
                req.setAttribute("statusMsg","Active terminals not founds on account");
                req.setAttribute("accountId", accountId);
                rd.forward(req, res);
                return;
            }
            if (sbError.length() > 0)
            {
                req.setAttribute("settlementCurrSet", settlementCurrSet);
                req.setAttribute("processingCurrency", processingCurrency);
                req.setAttribute("settlementCurrency", settlementCurrency);
                req.setAttribute("currencyConversionRate", currencyConversionRate);
                req.setAttribute("dynamicCountAmountMap", dynamicCountAmountMap);
                req.setAttribute("statusMsg", sbError.toString());
                req.setAttribute("accountId", accountId);
                rd.forward(req, res);
                return;
            }

           /* req.setAttribute("stringListHashMap", stringListHashMap);
            req.setAttribute("settlementCurrSet", settlementCurrSet);
            req.setAttribute("processingCurrency", processingCurrency);
            req.setAttribute("dynamicCountAmountMap", dynamicCountAmountMap);
            rd.forward(req,res);
            //return;
*/

            log.debug("PendingList Size::::"+pendingList.size());

            //List<String> stringList=payoutManager.consolidatedMerchantPayoutReportBasedOnBankWire1(bankWireId, dynamicCountAmountMap,listOfTerminalReq,pendingList);
            String cyclememberlist=null;
            List<String> stringList=new ArrayList<String>();

            String issettlementcronexceuted="Y";
            String ispayoutcronexcuted="N";

            log.debug("Payout Cron Is Going To Execute:::::");
            log.debug("Pending List:::::"+pendingList.size());
//            bankWireManagerVO=payoutDAO.getBankWireDetails(bankWireId,issettlementcronexceuted,ispayoutcronexcuted);
            TransactionManager transactionManager=new TransactionManager();

            HashMap<String,List<TerminalVO>> getListOfData=new HashMap<>();
            List<TerminalVO> getTerminalVOs=new ArrayList<>();
            DateVO dateVO=new DateVO();

            if (functions.isValueNull(parent_bankWireId)){
//                List<BankWireManagerVO> wireDetailsList=payoutDAO.getBankWireDetailsList(parent_bankWireId, issettlementcronexceuted, ispayoutcronexcuted);
                bankWireManagerVO=payoutDAO.getBankWireDetails(parent_bankWireId,issettlementcronexceuted,ispayoutcronexcuted);
            } else{
                bankWireManagerVO=payoutDAO.getBankWireDetails(bankWireId,issettlementcronexceuted,ispayoutcronexcuted);
            }
            if(bankWireManagerVO == null)
            {
                cyclememberlist="0:0:0:0:Failed:No New Wire Received From  Bank Manager";
                stringList.add(cyclememberlist);
                log.debug(cyclememberlist);
                //return;
            }
            dateVO.setStartDate(bankWireManagerVO.getServer_start_date());
            dateVO.setEndDate(bankWireManagerVO.getServer_end_date());

            for(TerminalVO terminalVO2:listOfTerminalReq)
            {
                String memberId = terminalVO2.getMemberId();
                String terminalId = terminalVO2.getTerminalId();
                Double conversionrate=terminalVO2.getConversionRate();
                String cycleId =terminalVO2.getWireId();
                String parentcycleId =terminalVO2.getParentBankWireId()!=null?terminalVO2.getParentBankWireId():null;
                terminalVO2= terminalManager.getTerminalsByMemberAccountIdForPayoutReportRequest(memberId,accountId,terminalId);
                terminalVO2.setConversionRate(conversionrate);
                String memberFirstTransactionDateOnTerminal = payoutDAO.getMemberFirstTransactionDateOnTerminalId(terminalVO2);
                terminalVO2.setWireId(cycleId);
                terminalVO2.setParentBankWireId(parentcycleId);
                if (functions.isValueNull(memberFirstTransactionDateOnTerminal))
                {
                    boolean pendingTransaction = transactionManager.checkPendingTransactionOfMerchant(gatewayAccount, memberId, terminalId, dateVO);
                    if (pendingTransaction)
                    {
                        cyclememberlist = cycleId + ":" + terminalVO2.getMemberId()  +":" + terminalVO2.getAccountId()+ ":" +terminalVO2.getTerminalId()+":Failed:Transaction status need to be corrected";
                        stringList.add(cyclememberlist);
                        log.debug(cyclememberlist);
                        continue;
                    }
                    List<ChargeVO> chargeVOs = payoutDAO.getChargesAsPerTerminal(terminalVO2);
                    if(chargeVOs.size()==0)
                    {
                        cyclememberlist = cycleId + ":" + memberId + ":" + terminalVO2.getAccountId() + ":" + terminalVO2.getTerminalId() + ":Failed:No Charges Mapped On Terminal";
                        stringList.add(cyclememberlist);
                        log.debug(cyclememberlist);
                        continue;
                    }else {
                        if(getListOfData.containsKey(memberId)){
                            getTerminalVOs.add(terminalVO2);
                        }else {
                            getTerminalVOs=new ArrayList<>();
                            getTerminalVOs.add(terminalVO2);
                        }
                        getListOfData.put(memberId,getTerminalVOs);

                        String firstCurrency = terminalVO2.getSettlementCurrency();
                        if (terminalVO1.getSettlementCurrency()==null){
                            cyclememberlist = cycleId + ":" + memberId + ":" + terminalVO2.getAccountId() + ":" + terminalVO2.getTerminalId() + ":Success:You Can Proceed For this";
                            log.debug(cyclememberlist);
                            stringList.add(cyclememberlist);
                            continue;
                        }else if (listOfTerminalReq.stream().allMatch(x -> x.getSettlementCurrency()!=null && x.getSettlementCurrency().equalsIgnoreCase(firstCurrency)))
                        {
                            cyclememberlist = cycleId + ":" + memberId + ":" + terminalVO2.getAccountId() + ":" + terminalVO2.getTerminalId() + ":Success:You Can Proceed For this";
                            log.debug(cyclememberlist);
                            stringList.add(cyclememberlist);
                            continue;
                        }
                        else {
                            req.setAttribute("statusMsg","Please change the Settlement Currency of terminals and try again.");
                            req.setAttribute("accountId", accountId);
                            rd.forward(req, res);
                            return;
                        }

                    }
                }
                else
                {
                    cyclememberlist = cycleId + ":" + memberId + ":" + terminalVO2.getAccountId() + ":" + terminalVO2.getTerminalId() + ":Failed:Terminal with no Activities";
                    stringList.add(cyclememberlist);
                    log.debug(cyclememberlist);
                    continue;
                }
            }
            log.error("StringList in java:::"+stringList);
            log.error("getListOfData in java:::" + getListOfData);
            req.setAttribute("stringList", stringList);
            req.setAttribute("getListOfData", getListOfData);
            req.setAttribute("pendingList",pendingList);
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException:::::",e);
            statusMsg = "Internal error while processing your request";
        }
        catch (Exception e)
        {
            log.error("Catch Exception:::::",e);
            statusMsg = "Internal error while processing your request";
        }
        req.setAttribute("statusMsg", statusMsg);
        req.setAttribute("accountId", accountId);
        rd.forward(req, res);
        return;
    }
}