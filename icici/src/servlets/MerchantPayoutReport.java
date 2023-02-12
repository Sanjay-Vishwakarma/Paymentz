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
 * Created by Sandip on 6/17/2017.
 */
public class MerchantPayoutReport extends HttpServlet
{
    private static Logger log = new Logger(MerchantPayoutReport.class.getName());
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

        RequestDispatcher rd = req.getRequestDispatcher("/merchantdynamicreport.jsp?&ctoken=" + user.getCSRFToken());

        String bankWireId=req.getParameter("bankwireid");
        String settlementCurrency = req.getParameter("processingcurrency");
        String dynamicMappingIds = req.getParameter("dynamicmappingids");

        PayoutManager payoutManager=new PayoutManager();
        TerminalManager terminalManager=new TerminalManager();
        ChargeManager chargeManager=new ChargeManager();
        PayoutDAO payoutDAO=new PayoutDAO();
        Functions functions=new Functions();

        if(!functions.isValueNull(bankWireId)){
            req.setAttribute("statusMsg","Please select BankWire Id");
            rd.forward(req, res);
            return;
        }
        HashMap<TerminalVO,List<ChargeVO>> stringListHashMap=new HashMap();
        HashMap<String, String> currencyConversionRate = new HashMap();
        HashMap<String, String> dynamicCountAmountMap = new HashMap();
        Set<String> settlementCurrSet=new HashSet<>();

        BankWireManagerVO bankWireManagerVO=payoutDAO.getBankWireDetails(bankWireId,"Y","N");
        String accountId=bankWireManagerVO.getAccountId();

        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        String processingCurrency=gatewayAccount.getCurrency();

        String statusMsg = "";
        StringBuffer sbError = new StringBuffer();
        try
        {
            String[] terminalIds=req.getParameterValues("terminalId");
            List<TerminalVO> terminalVOList = terminalManager.getTerminalsByAccountID(accountId,null,null);
            List<TerminalVO> settledList=terminalManager.getSettledTerminalList(bankWireId,null, null);
            List<TerminalVO> pendingList=new ArrayList<>();

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
                rd.forward(req, res);
                return;
            }

            req.setAttribute("stringListHashMap", stringListHashMap);
            req.setAttribute("settlementCurrSet", settlementCurrSet);
            req.setAttribute("processingCurrency", processingCurrency);
            req.setAttribute("dynamicCountAmountMap", dynamicCountAmountMap);

            log.debug("PendingList Size::::"+pendingList.size());

            List<String> stringList=payoutManager.merchantPayoutReportBasedOnBankWire1(bankWireId, dynamicCountAmountMap,listOfTerminalReq,pendingList);
            req.setAttribute("result", stringList);
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException:::::",e);
            statusMsg = "Internal error while processing your request";
        }
        catch (Exception e)
        {
            log.error("Exception:::::",e);
            statusMsg = "Internal error while processing your request";
        }
        req.setAttribute("statusMsg", statusMsg);
        rd.forward(req, res);
        return;
    }
}
