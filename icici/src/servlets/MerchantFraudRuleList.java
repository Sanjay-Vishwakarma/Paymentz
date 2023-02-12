import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.FraudRuleManager;
import com.manager.vo.PaginationVO;
import com.manager.vo.fraudruleconfVOs.*;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Created by Sneha on 25/7/15.
 */
public class MerchantFraudRuleList extends HttpServlet
{
    private static Logger logger = new Logger(MerchantFraudRuleList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }
    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.debug("Entering Into MarchantFraudRuleList");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("session out");
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }

        RequestDispatcher rd = request.getRequestDispatcher("/merchantFraudRule.jsp?ctoken="+user.getCSRFToken());
        StringBuffer msg = new StringBuffer();

        String fsaccountid = request.getParameter("fsaccountid");
        FraudRuleManager ruleManager = new FraudRuleManager();
        PaginationVO paginationVO = new PaginationVO();
        try
        {
            paginationVO.setInputs("fsaccountid=" +fsaccountid);
            paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"), 1));
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"), 3));

            List<RuleMasterVO> accountLevelRuleMapping=ruleManager.getAccountLevelRiskRuleList(fsaccountid,paginationVO);

            if(fsaccountid != null && !fsaccountid.isEmpty())
            {
                if (accountLevelRuleMapping != null && !accountLevelRuleMapping.isEmpty())
                {
                    request.setAttribute("paginationVO",paginationVO);
                    request.setAttribute("fsaccountid", fsaccountid);
                    request.setAttribute("accountLevelRuleMapping", accountLevelRuleMapping);
                }
                else
                {
                    msg.append("Rules are not mapped for selected master account");
                }
            }
            else
            {
                msg.append("selected account lost during network transfer,please try again");
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("Error while performing db operation in ::::"+ e);
            msg.append(e.getMessage());
        }
        request.setAttribute("msg", msg.toString());
        rd.forward(request, response);
    }
}
