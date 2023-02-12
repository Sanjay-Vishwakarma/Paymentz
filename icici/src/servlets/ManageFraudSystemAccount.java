import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.FraudRuleManager;
import com.manager.vo.fraudruleconfVOs.FraudSystemAccountVO;
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
 * Created by Sneha on 7/16/2015.
 */
public class ManageFraudSystemAccount extends HttpServlet
{
    private static Logger logger = new Logger(ManageFraudSystemAccount.class.getName());
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
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("session out");
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }

        String fsid = request.getParameter("fsid");
        String accountName = request.getParameter("accountName");
        String userName = request.getParameter("userName");
        String pwd = request.getParameter("pwd");
        String isTest = request.getParameter("isTest");
        String contactname = request.getParameter("contactname");
        String contactemail = request.getParameter("contactemail");

        RequestDispatcher rd=request.getRequestDispatcher("/manageFraudSystemAccount.jsp?ctoken="+user.getCSRFToken());

        StringBuffer errorMsg = new StringBuffer();
        StringBuffer message=new StringBuffer();
        Functions functions = new Functions();

        if (!ESAPI.validator().isValidInput("fsid", fsid, "Numbers", 10, false))
        {
            errorMsg.append("Invalid Fraud System ID<BR>");
        }
        if (!ESAPI.validator().isValidInput("accountName", accountName, "SafeString", 255, false) || functions.hasHTMLTags(accountName))
        {
            errorMsg.append("Invalid Fraud System MerchantId<BR>");
        }
        if (!ESAPI.validator().isValidInput("contactname", contactname, "contactName", 255, false))
        {
            errorMsg.append("Invalid Contact Name<BR>");
        }
        if (!ESAPI.validator().isValidInput("contactemail", contactemail, "Email",100, false))
        {
            errorMsg.append("Invalid Contact Email<BR>");
        }
        if (!ESAPI.validator().isValidInput("userName", userName, "SafeString", 255, true) || functions.hasHTMLTags(userName))
        {
            errorMsg.append("Invalid UserName<BR>");
        }
        if ( functions.hasHTMLTags(pwd))
        {
            errorMsg.append("Invalid Password<BR>");
        }
        if(errorMsg.length()>0)
        {
            request.setAttribute("statusMsg", errorMsg.toString());
            logger.error(errorMsg.toString());
            rd.forward(request, response);
            return;
        }

        FraudSystemAccountVO fraudSystemAccountVO = new FraudSystemAccountVO();

        fraudSystemAccountVO.setFraudSystemId(fsid);
        fraudSystemAccountVO.setAccountName(accountName);
        fraudSystemAccountVO.setUserName(userName);
        fraudSystemAccountVO.setPassword(pwd);
        fraudSystemAccountVO.setIsTest(isTest);
        fraudSystemAccountVO.setContactName(contactname);
        fraudSystemAccountVO.setContactEmail(contactemail);

        try
        {
            FraudRuleManager fraudRuleManager = new FraudRuleManager();
            if(fraudRuleManager.isFraudSystemAccountUnique(accountName))
            {
                String status = fraudRuleManager.addNewFraudSystemAccount(fraudSystemAccountVO);
                if ("success".equals(status))
                {
                    message.append("Fraud System Account Added Successfully.");
                }
                else
                {
                    message.append("Fraud System Account Adding Failed.");
                }
            }
            else
            {
                message.append("Fraud System Account Already Used.");
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error(e);
            message.append(e.getMessage());
        }
        catch (Exception e)
        {
            logger.error(e);
            message.append(e.getMessage());
        }
        request.setAttribute("statusMsg", message.toString());
        rd.forward(request, response);
        return;
    }
}