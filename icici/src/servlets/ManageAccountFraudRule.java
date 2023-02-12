import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.FraudRuleManager;
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
import java.util.ArrayList;
import java.util.List;

import java.util.*;
/**
 * Created by Sneha on 27/7/15.
 */
public class ManageAccountFraudRule extends HttpServlet
{
    private static Logger logger = new Logger(ManageAccountFraudRule.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doService(request, response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {

        doService(request, response);
    }

    public void doService(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("session out");
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }

        Functions functions = new Functions();
        RequestDispatcher rd = request.getRequestDispatcher("/accountFraudRuleList.jsp?ctoken=" + user.getCSRFToken());

        String fsAccountId = request.getParameter("fsaccountid");
        String[] ruleIds = request.getParameterValues("ruleid");
        String score = null;
        String status = null;
        String fsId=null;

        StringBuffer updateMsg = new StringBuffer();
        FraudRuleManager fraudRuleManager=new FraudRuleManager();
        StringBuffer sb = new StringBuffer();
        try
        {

            FraudSystemAccountVO fraudSystemAccountVO=fraudRuleManager.getFraudAccountDetails(fsAccountId);
            if(fraudSystemAccountVO.getFraudSystemAccountId()==null)
            {
                request.setAttribute("updateMsg","FraudSystem Account Not Found.");
                rd.forward(request,response);
                return;
            }

            fsId=fraudSystemAccountVO.getFraudSystemId();
            //Step1:add intimation entry in fraud intimation
            FraudRuleChangeIntimationVO intimationVO=new FraudRuleChangeIntimationVO();

            intimationVO.setFraudSystemId(fsId);
            intimationVO.setFsAccountId(fsAccountId);
            intimationVO.setFsSubAccountId("");
            intimationVO.setPartnerId("");
            intimationVO.setMemberId("");
            intimationVO.setStatus("Initiated");

            List<FraudSystemAccountRuleVO> accountRuleVOList=new ArrayList<FraudSystemAccountRuleVO>();
            FraudSystemAccountRuleVO accountRuleVO=null;
            for (String ruleId : ruleIds)
            {
                score = request.getParameter("score_" + ruleId);
                status = request.getParameter("status_" + ruleId);
                if(functions.isNumericVal(score))
                {
                    accountRuleVO = new FraudSystemAccountRuleVO();
                    accountRuleVO.setRuleId(ruleId);
                    accountRuleVO.setScore(score);
                    accountRuleVO.setStatus(status);
                    accountRuleVOList.add(accountRuleVO);
                }
                else
                {
                    sb.append(ruleId);
                }
            }
            if(sb.length()>0)
            {
                request.setAttribute("updateMsg","Please enter valid input");
                rd.forward(request,response);
                return;
            }
            else
            {
                if(accountRuleVOList.size()>0)
                {
                    AccountRuleChangeRequestVO changeRequestVO=new AccountRuleChangeRequestVO(intimationVO,
                            accountRuleVOList,fsAccountId,fsId);
                    String updatedMsg=fraudRuleManager.handleAccountFraudRuleChangeRequest(changeRequestVO);
                    if("success".equals(updatedMsg))
                    {
                        updateMsg.append("Rules updated successfully");
                    }
                    else
                    {
                        updateMsg.append("Rules updating failed");
                    }
                }
                else
                {
                    updateMsg.append("No valid rules to update");
                }
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("Error while performing db operation in ::::"+ e);
            updateMsg.append(e.getMessage());
        }
        catch (Exception e){
            logger.error("Error while performing db operation in ::::"+ e);
            updateMsg.append(e.getMessage());
        }
        request.setAttribute("updateMsg", updateMsg.toString());
        rd.forward(request, response);

    }
}
