import com.directi.pg.Logger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.ChargeManager;
import com.manager.TerminalManager;
import com.manager.dao.PayoutDAO;
import com.manager.vo.BankWireManagerVO;
import com.manager.vo.ChargeVO;
import com.manager.vo.TerminalVO;
import org.owasp.esapi.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Sandip on 6/17/2017.
 */
public class GetSettlementCurrencyDetails extends HttpServlet
{
    private static Logger logger = new Logger(GetSettlementCurrencyDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        String bankWireId = req.getParameter("bankwireid");
        try
        {
            //Get settlement currency for all terminals
            PayoutDAO payoutDAO=new PayoutDAO();
            BankWireManagerVO bankWireManagerVO=payoutDAO.getBankWireDetails(bankWireId,"Y","N");

            String accountId=bankWireManagerVO.getAccountId();

            TerminalManager terminalManager=new TerminalManager();
            GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
            String processingCurrency=gatewayAccount.getCurrency();

            HashMap<TerminalVO,List<ChargeVO>> stringListHashMap=new HashMap();

            ChargeManager chargeManager=new ChargeManager();


            List<TerminalVO> terminalVOList=terminalManager.getTerminalsByAccountID(accountId,null,null);
            Set<String> settlementCurrSet=null;
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

            logger.debug("stringListHashMap:::::::"+stringListHashMap);
            logger.debug("stringListHashMap:::::::"+stringListHashMap);

            req.setAttribute("stringListHashMap",stringListHashMap);
            req.setAttribute("settlementCurrSet", settlementCurrSet);
            req.setAttribute("processingCurrency",processingCurrency);
        }
        catch (Exception e)
        {
            logger.debug("Exception::::::::" + e);
            req.setAttribute("statusMsg","Internal error while processing your request.");
        }
        req.getRequestDispatcher("/merchantdynamicreport.jsp?ctoken=" + user.getCSRFToken()).forward(req, res);
    }
}
