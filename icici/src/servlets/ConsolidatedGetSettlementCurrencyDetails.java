import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.ChargeManager;
import com.manager.TerminalManager;
import com.manager.dao.PayoutDAO;
import com.manager.vo.BankWireManagerVO;
import com.manager.vo.ChargeVO;
import com.manager.vo.TerminalVO;
import org.apache.poi.ss.formula.functions.Function3Arg;
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
 * Created by Mahima on 6/17/2020.
 */
public class ConsolidatedGetSettlementCurrencyDetails extends HttpServlet
{
    private static Logger logger = new Logger(ConsolidatedGetSettlementCurrencyDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        String bankWireId = req.getParameter("bankwireid");
        String parent_bankWireId = req.getParameter("parent_bankWireId");
        String cardtypeid=req.getParameter("cardtypeid");
        String paymodeid=req.getParameter("paymodeid");
        Functions functions = new Functions();
        try
        {
            //Get settlement currency for all terminals
            PayoutDAO payoutDAO=new PayoutDAO();
            BankWireManagerVO bankWireManagerVO = null;
            List<BankWireManagerVO> bankWireManagerVOList = null;
            String accountId = "";
            RequestDispatcher rd = req.getRequestDispatcher("/consolidatedReport.jsp?&ctoken=" + user.getCSRFToken());
            if(!functions.isValueNull(bankWireId) && !functions.isValueNull(parent_bankWireId)){
                req.setAttribute("statusMsg","Please select BankWire Id Or Parent BankWIre Id");
                rd.forward(req, res);
                return;
            }
            else if (functions.isValueNull(parent_bankWireId)){
                bankWireManagerVOList=payoutDAO.getBankWireDetailsList(parent_bankWireId, "Y", "N");
            }else {
                bankWireManagerVO=payoutDAO.getBankWireDetails(bankWireId,"Y","N");
                accountId=bankWireManagerVO.getAccountId();
            }

            TerminalManager terminalManager=new TerminalManager();
            GatewayAccount gatewayAccount= null;
            String processingCurrency ="";
            List<GatewayAccount> accountList = new ArrayList<>();
            if (functions.isValueNull(parent_bankWireId)){
                if (bankWireManagerVOList!=null){
                    for (BankWireManagerVO vo : bankWireManagerVOList){
                        gatewayAccount= GatewayAccountService.getGatewayAccount(vo.getAccountId());
                        processingCurrency=gatewayAccount.getCurrency();
                        accountList.add(gatewayAccount);
                    }
                }
            }else{
                gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
                processingCurrency=gatewayAccount.getCurrency();
            }

            HashMap<TerminalVO,List<ChargeVO>> stringListHashMap=new HashMap();

            ChargeManager chargeManager=new ChargeManager();


            List<TerminalVO> terminalVOList=null;

            if (functions.isValueNull(parent_bankWireId)){
                if (bankWireManagerVOList!=null){
                    for (BankWireManagerVO vo : bankWireManagerVOList){
                        gatewayAccount = GatewayAccountService.getGatewayAccount(vo.getAccountId());
                        processingCurrency=gatewayAccount.getCurrency();
                        accountList.add(gatewayAccount);
                        accountId += vo.getAccountId()+",";
                    }
                }
                if (accountId.endsWith(",")){ accountId=accountId.replaceFirst(".$","");}
                terminalVOList=terminalManager.getTerminalsByAccAndBankId(accountId, parent_bankWireId,cardtypeid, paymodeid);
            } else{
                terminalVOList=terminalManager.getTerminalsByAccountID(accountId,cardtypeid, paymodeid);
            }
            req.setAttribute("accountId", accountId);

            Set<String> settlementCurrSet=new HashSet();
            if(terminalVOList!=null && terminalVOList.size()>0)
            {
                settlementCurrSet=new HashSet();
                for (TerminalVO terminalVO : terminalVOList)
                {
                    if (!processingCurrency.equalsIgnoreCase(terminalVO.getSettlementCurrency()))
                    {
                        settlementCurrSet.add(terminalVO.getSettlementCurrency());
                    }

                    List tempList=chargeManager.getDynamicChargesAsPerTerminal(terminalVO);
                    if(tempList.size()>0){
                        stringListHashMap.put(terminalVO,tempList);
                    }
                }
            }
            req.setAttribute("settlementCurrSet", settlementCurrSet);


            logger.debug("stringListHashMap:::::::"+stringListHashMap);
            logger.debug("stringListHashMap:::::::" + stringListHashMap);

            req.setAttribute("stringListHashMap",stringListHashMap);
            req.setAttribute("processingCurrency",processingCurrency);
        }
        catch (Exception e)
        {
            logger.debug("Catch Exception::::::::" + e);
            req.setAttribute("statusMsg","Internal error while processing your request.");
        }
        req.getRequestDispatcher("/consolidatedReport.jsp?ctoken=" + user.getCSRFToken()).forward(req, res);
    }
}