import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.FraudRuleManager;
import com.manager.vo.fraudruleconfVOs.FraudSystemAccountRuleVO;

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
 * Created by kiran on 20/7/15.
 */
public class ActionFraudSystemAccountRule extends HttpServlet
{
    private static Logger logger = new Logger(ActionFraudSystemAccountRule.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String action=req.getParameter("action");
        StringBuffer sb=new StringBuffer();
        RequestDispatcher rd=req.getRequestDispatcher("/actionFraudSystemAccountRule.jsp?ctoken="+user.getCSRFToken());

        try
        {
            if(action.equalsIgnoreCase("modify"))
            {
                String fsaccountid = req.getParameter("fsaccountid");
                String ruleId=req.getParameter("ruleid");

                FraudRuleManager fraudRuleManager=new FraudRuleManager();
                FraudSystemAccountRuleVO fraudSystemAccountRuleVO=fraudRuleManager.getAccountLevelFraudRuleDetails(fsaccountid,ruleId);
                req.setAttribute("fraudSystemAccountRuleVO",fraudSystemAccountRuleVO);
                rd=req.getRequestDispatcher("/actionFraudSystemAccountRule.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req,res);
                return;
            }
            else if(action.equalsIgnoreCase("update"))
            {
                String ruleId=req.getParameter("ruleid");
                String fsAccountId=req.getParameter("fsaccountid");
                String score=req.getParameter("score");
                String status=req.getParameter("status");
                String value=req.getParameter("value");
                if (!ESAPI.validator().isValidInput("status",status,"SafeString",255,false))
                {
                    sb.append("Invalid Rule status,");
                }
                if (!ESAPI.validator().isValidInput("score",score,"Numbers",2,false))
                {
                    sb.append("Invalid Default Score");
                }
                /*if(functions.isValueNull(value))
                {
                    if (!ESAPI.validator().isValidInput("value",value,"Numbers",10,false))
                    {
                        sb.append("Invalid Limit<BR>");
                    }
                }*/
                if(sb.length()>0)
                {
                    req.setAttribute("statusMsg",sb.toString());
                    rd.forward(req,res);
                    return;
                }
                FraudSystemAccountRuleVO fraudSystemAccountRuleVO=new FraudSystemAccountRuleVO();
                fraudSystemAccountRuleVO.setRuleId(ruleId);
                fraudSystemAccountRuleVO.setFsAccountId(fsAccountId);
                fraudSystemAccountRuleVO.setScore(score);
                fraudSystemAccountRuleVO.setStatus(status);
                fraudSystemAccountRuleVO.setValue(value);

                FraudRuleManager fraudRuleManager=new FraudRuleManager();
                String msg=fraudRuleManager.updateAccountLevelFraudRule(fraudSystemAccountRuleVO);
                String statusMsg="";
                if("success".equals(msg))
                {
                    statusMsg="Fraud Account Rule Mapping Updated Successfully";
                }
                else
                {
                    statusMsg="Fraud Account Rule Mapping Updation Failed";
                }
                req.setAttribute("statusMsg",statusMsg);
                rd=req.getRequestDispatcher("/actionFraudSystemAccountRule.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req,res);
                return;
            }
        }
        catch (PZDBViolationException e)
        {

        }
    }
}


