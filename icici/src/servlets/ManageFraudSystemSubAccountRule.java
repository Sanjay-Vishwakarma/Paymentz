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
 * Created by kiran on 17/7/15.
 */
public class ManageFraudSystemSubAccountRule extends HttpServlet
{
    private static Logger logger=new Logger(ManageFraudSystemSubAccountRule.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doProcess(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {

        doProcess(request,response);
    }
    public void doProcess(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if(!Admin.isLoggedIn(session))
        {
            logger.debug("session out");
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }

        StringBuffer sb=new StringBuffer();
        Functions functions=new Functions();
        String fsSubAccount=request.getParameter("fssubaccount");
        String ruleId=request.getParameter("ruleid");
        String score=request.getParameter("score");
        String status=request.getParameter("status");
        String value=request.getParameter("value");
        String value1=request.getParameter("value1");
        String value2=request.getParameter("value2");

        RequestDispatcher rd=request.getRequestDispatcher("/manageFraudSystemSubAccountRule.jsp?ctoken="+user.getCSRFToken());

        if (!ESAPI.validator().isValidInput("fsaccountid",fsSubAccount,"Numbers",11,false))
        {
            sb.append("Invalid Fraud Sub Account<BR>");
        }
        if (!ESAPI.validator().isValidInput("ruleid",ruleId,"Numbers",11,false))
        {
            sb.append("Invalid Rule Name<BR>");
        }
        if (!ESAPI.validator().isValidInput("score",score,"Numbers",2,false))
        {
            sb.append("Invalid Score<BR>");
        }
        if (!ESAPI.validator().isValidInput("status",status,"SafeString",25,false))
        {
            sb.append("Invalid Status<BR>");
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
            request.setAttribute("statusMsg",sb.toString());
            logger.error(sb.toString());
            rd.forward(request,response);
            return;
        }

        FraudSystemSubAccountRuleVO fraudSystemSubAccountRuleVO=new FraudSystemSubAccountRuleVO();

        fraudSystemSubAccountRuleVO.setFsSubAccountId(fsSubAccount);
        fraudSystemSubAccountRuleVO.setRuleId(ruleId);
        fraudSystemSubAccountRuleVO.setScore(score);
        fraudSystemSubAccountRuleVO.setStatus(status);
        fraudSystemSubAccountRuleVO.setValue(value);
        fraudSystemSubAccountRuleVO.setValue1(value1);
        fraudSystemSubAccountRuleVO.setValue2(value2);

        FraudRuleManager fraudRuleManager=new FraudRuleManager();

        try
        {
            if(fraudRuleManager.isFraudSubAccountRuleMapUnique(fraudSystemSubAccountRuleVO.getFsSubAccountId(),fraudSystemSubAccountRuleVO.getRuleId()))
            {
                String statusMsg = fraudRuleManager.addNewFraudSubAccountRuleMap(fraudSystemSubAccountRuleVO);
                if ("success".equals(statusMsg))
                {
                    sb.append("Fraud Sub Account Rule Mapping Added Successfully");
                }
                else
                {
                    sb.append("Fraud Sub Account Rule Mapping Adding Failed");
                }
            }
            else
            {
                sb.append("Fraud Sub Account Rule Mapping Already Available");
            }
        }
        catch (PZDBViolationException e)
        {
            sb.append(e.getMessage());
            logger.error(sb.toString());
        }
        catch (Exception e)
        {
            sb.append(e.getMessage());
            logger.error(sb.toString());
        }

        request.setAttribute("statusMsg",sb.toString());
        rd.forward(request,response);
        return;
    }
}
