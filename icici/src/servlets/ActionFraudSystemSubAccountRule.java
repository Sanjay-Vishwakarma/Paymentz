import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.FraudRuleManager;
import com.manager.vo.fraudruleconfVOs.FraudSystemSubAccountRuleVO;

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
 * Created by PZ on 21/7/15.
 */
public class ActionFraudSystemSubAccountRule extends HttpServlet
{
    private static Logger logger = new Logger(ActionFraudSystemSubAccountRule.class.getName());
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
        String statusMsg="";
        RequestDispatcher rd=req.getRequestDispatcher("/actionFraudSystemSubAccountRule.jsp?ctoken="+user.getCSRFToken());
        try
        {
            if(action.equalsIgnoreCase("modify"))
            {
                String fssubaccountid = req.getParameter("fssubaccountid");
                String ruleId=req.getParameter("ruleid");

                FraudRuleManager fraudRuleManager=new FraudRuleManager();
                FraudSystemSubAccountRuleVO fraudSystemSubAccountRuleVO=fraudRuleManager.getSubAccountLevelFraudRuleDetails(fssubaccountid, ruleId);
                logger.debug("fraudSystemSubAccountRuleVO=>"+fraudSystemSubAccountRuleVO.toString());
                req.setAttribute("fraudSystemSubAccountRuleVO",fraudSystemSubAccountRuleVO);
                rd.forward(req,res);
                return;
            }
            else if(action.equalsIgnoreCase("update"))
            {
                String ruleId=req.getParameter("ruleid");
                String fssubaccountid=req.getParameter("fssubaccountid");
                String score=req.getParameter("score");
                String status=req.getParameter("status");
                String value=req.getParameter("value");

                if (!ESAPI.validator().isValidInput("status",status,"SafeString",25,false))
                {
                    sb.append("Invalid Rule Status<BR>");
                }
                if (!ESAPI.validator().isValidInput("score",score,"Numbers",2,false))
                {
                    sb.append("Invalid Default Score<BR>");
                }
                /*if(functions.isValueNull(value))
                {
                    if (!ESAPI.validator().isValidInput("value",value,"SafeString",100,false))
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

                FraudSystemSubAccountRuleVO fraudSystemSubAccountRuleVO=new FraudSystemSubAccountRuleVO();
                fraudSystemSubAccountRuleVO.setRuleId(ruleId);
                fraudSystemSubAccountRuleVO.setFsSubAccountId(fssubaccountid);
                fraudSystemSubAccountRuleVO.setScore(score);
                fraudSystemSubAccountRuleVO.setStatus(status);
                fraudSystemSubAccountRuleVO.setValue(value);

                FraudRuleManager fraudRuleManager=new FraudRuleManager();
                String msg=fraudRuleManager.updateSubAccountLevelFraudRule(fraudSystemSubAccountRuleVO);
                if("success".equals(msg))
                {
                    statusMsg="Fraud Sub Account Rule Configuration Updated Successfully";
                }
                else
                {
                    statusMsg="Fraud Sub Account Rule Configuration Updation Failed";
                }
                req.setAttribute("statusMsg",statusMsg);
                rd.forward(req,res);
                return;
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error(e);
            req.setAttribute("statusMsg",e.getMessage());
            rd=req.getRequestDispatcher("/actionFraudSystemSubAccountRule.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req,res);
            return;
        }
    }
}
