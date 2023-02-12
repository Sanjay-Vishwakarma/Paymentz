import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.FraudRuleManager;
import com.manager.vo.fraudruleconfVOs.FraudSystemSubAccountVO;

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
 * Created by Sneha on 16/7/15.
 */
public class ManageFraudSystemAccountSubAccount extends HttpServlet
{
    private static Logger logger = new Logger(ManageFraudSystemAccountSubAccount.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException , IOException
    {
        doService(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException , IOException
    {

        doService(request, response);
    }
    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException , IOException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("session out");
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }

        String fsAccount =request.getParameter("fsAccount");
        String subaccountName =request.getParameter("subaccountName");
        String subUserName =request.getParameter("subUserName");
        String subpwd =request.getParameter("subpwd");
        String isActive =request.getParameter("isActive");

        RequestDispatcher rd=request.getRequestDispatcher("/manageFraudSystemAccountSubAccount.jsp?ctoken="+user.getCSRFToken());
        StringBuffer errorMsg = new StringBuffer();

        if (!ESAPI.validator().isValidInput("fsAccount", fsAccount,"contactName", 100, false)){
            errorMsg.append("Invalid Fraud System Account<BR>");
        }
        if (!ESAPI.validator().isValidInput("subaccountName", subaccountName, "contactName", 255, false)){
            errorMsg.append("Invalid Sub Account Name<BR>");
        }
        if (!ESAPI.validator().isValidInput("subUserName", subUserName, "contactName", 255, true)){
            errorMsg.append("Invalid User Name<BR>");
        }
        if (!ESAPI.validator().isValidInput("usernumber", subpwd, "Numbers", 255, true)){
            errorMsg.append("Invalid User Number<BR>");
        }
        if(errorMsg.length()>0)
        {
            request.setAttribute("statusMsg",errorMsg.toString());
            logger.error(errorMsg.toString());
            rd.forward(request,response);
            return;
        }

        StringBuffer message = new StringBuffer();
        FraudSystemSubAccountVO systemSubAccountVO = new FraudSystemSubAccountVO();
        systemSubAccountVO.setFraudSystemAccountId(fsAccount);
        systemSubAccountVO.setSubAccountName(subaccountName);
        systemSubAccountVO.setUserName(subUserName);
        systemSubAccountVO.setPassword(subpwd);
        systemSubAccountVO.setIsActive(isActive);

        try
        {
            FraudRuleManager fraudRuleManager = new FraudRuleManager();
            if(fraudRuleManager.isFraudSystemSubAccountUnique(systemSubAccountVO.getFraudSystemAccountId(),systemSubAccountVO.getSubAccountName()))
            {
                String status = fraudRuleManager.addNewFraudSystemSubAccount(systemSubAccountVO);
                if ("success".equals(status))
                {
                    message.append("Fraud Sub Account Added Successfully");
                }
                else
                {
                    message.append("Fraud Sub Account Adding Failed");
                }
            }
            else
            {
                message.append("Fraud Sub Account Already Exist");
            }

        }
        catch (PZDBViolationException e)
        {
            logger.error(e);
            message.append(e.getMessage());
        }
        request.setAttribute("statusMsg", message.toString());
        rd.forward(request, response);
        return;
    }
}
