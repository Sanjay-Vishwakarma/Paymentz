import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.FraudRuleManager;
import com.manager.vo.fraudruleconfVOs.FraudSystemAccountVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sneha on 20/7/15.
 */
public class ActionFraudSystemAccount extends HttpServlet
{
    private static Logger logger = new Logger(ActionFraudSystemAccount.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        doProcess(request, response);
    }
    public void doProcess(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String EOL = "<BR>";
        StringBuffer msg = new StringBuffer();
        RequestDispatcher rd = req.getRequestDispatcher("/actionFraudSystemAccount.jsp?ctoken="+user.getCSRFToken());
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            logger.error("Enter valid input", e);
            msg.append ("<center><font class=\"text\" face=\"arial\"><b>"+ msg + e.getMessage() + EOL + "</b></font></center>");
            logger.debug("message..." + e.getMessage());
            req.setAttribute("msg",msg);
            rd.forward(req,res);
            return;
        }

        String action = req.getParameter("action");
        String fsaccountid = req.getParameter("mappingid");

        try
        {
            FraudRuleManager ruleManager = new FraudRuleManager();
            FraudSystemAccountVO accountVO = new FraudSystemAccountVO();
            if (action.equalsIgnoreCase("modify"))
            {
                accountVO=ruleManager.getFraudAccountDetails(fsaccountid);
                req.setAttribute("accountDetails",accountVO);
                rd.forward(req,res);
                return;
            }
            else if(action.equalsIgnoreCase("update"))
            {
                StringBuffer message = new StringBuffer();

                accountVO.setUserName(req.getParameter("username"));
                accountVO.setPassword(req.getParameter("pwd"));
                accountVO.setIsTest(req.getParameter("isTest"));
                accountVO.setContactName(req.getParameter("contactname"));
                accountVO.setContactEmail(req.getParameter("contactemail"));
                accountVO.setFraudSystemAccountId(fsaccountid);
                Functions functions = new Functions();
                if (!ESAPI.validator().isValidInput("contactname", req.getParameter("contactname"), "SafeString", 255, false) || functions.hasHTMLTags(req.getParameter("contactname")))
                {
                    message.append("Invalid Contact Name<BR>");
                }
                if (!ESAPI.validator().isValidInput("contactemail", req.getParameter("contactemail"), "Email", 255, false))
                {
                    message.append("Invalid Contact Email<BR>");
                }
                if (!ESAPI.validator().isValidInput("userName", req.getParameter("username"), "SafeString", 255, true) || functions.hasHTMLTags(req.getParameter("username")))
                {
                    message.append("Invalid UserName<BR>");
                }
                if (functions.hasHTMLTags(req.getParameter("pwd")))
                {
                    message.append("Invalid Password<BR>");
                }
                if(message.length() > 0)
                {
                    req.setAttribute("updateMsg", message);
                    rd.forward(req,res);
                    return;
                }

                String status = ruleManager.updateFraudAccount(accountVO);
                if("success".equals(status))
                {
                    message.append("Fraud System Account Configuration Updated SuccessFully");
                }
                else
                {
                    message.append("Fraud System Account Configuration Updating Failed");
                }
                req.setAttribute("updateMsg", message);
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("SQL Exception while updating fraud account mapping details", e);
            req.setAttribute("updateMsg", e);
        }
        rd.forward(req,res);
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.MAPPINGID);
        inputFieldsListMandatory.add(InputFields.ACTION);
        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}
