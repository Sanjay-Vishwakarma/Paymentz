import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.manager.FraudRuleManager;
import com.manager.vo.fraudruleconfVOs.RuleMasterVO;
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
/**
 * Created with IntelliJ IDEA.
 * User: kiran
 * Date: 7/15/15
 * Time: 3:51PM
 * To change this template use File | Settings | File Templates.
 */
public class AddNewFraudRule extends HttpServlet
{
    private static Logger logger=new Logger(AddNewFraudRule.class.getName());
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
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }

        String ruleName=request.getParameter("rulename");
        String ruleDescription=request.getParameter("ruledescription");
        String ruleGroup=request.getParameter("rulegroup");
        String ruleDefaultScore=request.getParameter("score");
        String ruleDefaultStatus = request.getParameter("status");

        StringBuffer sb=new StringBuffer();

        RequestDispatcher rd=request.getRequestDispatcher("/addNewFraudRule.jsp?ctoken="+user.getCSRFToken());

        if (!ESAPI.validator().isValidInput("rulename",ruleName,"Description",255,false))
        {
            sb.append("Invalid Rule Name <br>");
        }
        if (!ESAPI.validator().isValidInput("ruledescription",ruleDescription,"alphanum",255,true))
        {
            sb.append("Invalid Rule Description <br>");
        }
        if (!ESAPI.validator().isValidInput("rulegroup",ruleGroup,"alphanum",255,false))
        {
            sb.append("Invalid Rule Group <br>");
        }
        if (!ESAPI.validator().isValidInput("ruledefaultscore",ruleDefaultScore,"Numbers",2,false))
        {
            sb.append("Invalid Default Score <br>");
        }
        if (!ESAPI.validator().isValidInput("ruleDefaultStatus",ruleDefaultStatus,"alphanum",255,false))
        {
            sb.append("Invalid Satus <br>");
        }
        if(sb.length()>0)
        {
            request.setAttribute("statusMsg",sb.toString());
            rd.forward(request,response);
            return;
        }

        RuleMasterVO ruleMasterVO=new RuleMasterVO();
        ruleMasterVO.setRuleName(ruleName);
        ruleMasterVO.setRuleDescription(ruleDescription);
        ruleMasterVO.setRuleGroup(ruleGroup);
        ruleMasterVO.setDefaultScore(ruleDefaultScore);
        ruleMasterVO.setDefaultStatus(ruleDefaultStatus);
        FraudRuleManager fraudRuleManager=new FraudRuleManager();
        try
        {

            if (fraudRuleManager.isFraudRuleUnique(ruleMasterVO.getRuleName()))
            {
                String status=fraudRuleManager.addNewFraudRule(ruleMasterVO);
                if("success".equals(status)){
                    sb.append("Fraud Rule added successFully");
                }
               else{
                    sb.append("Fraud Rule adding failed");
                }
            }
            else
            {
                sb.append("Fraud Rule already exist in the system.");
            }
        }
        catch (PZDBViolationException e)
        {
            sb.append(e.getMessage());
            logger.error("Exception while performing operation on rule master::::"+e);
        }
        catch (Exception e)
        {
            sb.append(e.getMessage());
            logger.error("Exception while performing operation on rule master::::"+e);
        }
        request.setAttribute("statusMsg",sb.toString());
        rd.forward(request,response);
    }
}
