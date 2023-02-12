package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.manager.FraudRuleManager;
import com.manager.vo.fraudruleconfVOs.FraudSystemAccountVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.apache.commons.lang.StringUtils;
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
import java.util.ResourceBundle;


/**
 * Created by NAMRATA B. BARI on 18/01/2020.
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
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String ActionFraudSystemAccount_updated_errormsg = StringUtils.isNotEmpty(rb1.getString("ActionFraudSystemAccount_updated_errormsg")) ? rb1.getString("ActionFraudSystemAccount_updated_errormsg") : "Fraud System Account Configuration Updated SuccessFully";
        String ActionFraudSystemAccount_failed_errormsg = StringUtils.isNotEmpty(rb1.getString("ActionFraudSystemAccount_failed_errormsg")) ? rb1.getString("ActionFraudSystemAccount_failed_errormsg") : "Fraud System Account Configuration Updating Failed";


        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
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
            logger.debug("message..." + e.getMessage());
            req.setAttribute("msg",msg + e.getMessage() + EOL);
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
                StringBuffer error = new StringBuffer();

                accountVO.setUserName(req.getParameter("username"));
                accountVO.setPassword(req.getParameter("pwd"));
                accountVO.setIsTest(req.getParameter("isTest"));
                accountVO.setContactName(req.getParameter("contactname"));
                accountVO.setContactEmail(req.getParameter("contactemail"));
                accountVO.setFraudSystemAccountId(fsaccountid);
                Functions functions = new Functions();

                if (!ESAPI.validator().isValidInput("contactname", req.getParameter("contactname"), "SafeString", 255, false) || functions.hasHTMLTags(req.getParameter("contactname")))
                {
                    error.append("Invalid Contact Name<BR>");
                }
                if (!ESAPI.validator().isValidInput("contactemail", req.getParameter("contactemail"), "Email", 255, false))
                {
                    error.append("Invalid Contact Email<BR>");
                }
                if (!ESAPI.validator().isValidInput("userName", req.getParameter("username"), "SafeString", 255, true) || functions.hasHTMLTags(req.getParameter("username")))
                {
                    error.append("Invalid User Name<BR>");
                }
                if (!ESAPI.validator().isValidInput("pwd", req.getParameter("pwd"), "SafeString", 255, true) || functions.hasHTMLTags(req.getParameter("pwd")))
                {
                    error.append("Invalid Password<BR>");
                }
                if(error.length() > 0)
                {
                    req.setAttribute("msg", error);
                    rd.forward(req,res);
                    return;
                }

                String status = ruleManager.updateFraudAccount(accountVO);
                if("success".equals(status))
                {
                    message.append(ActionFraudSystemAccount_updated_errormsg);
                    req.setAttribute("updateMsg", message);
                    rd.forward(req,res);
                    return;
                }
                else
                {
                    error.append(ActionFraudSystemAccount_failed_errormsg);
                    req.setAttribute("msg", error);
                    rd.forward(req,res);
                    return;
                }
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("SQL Exception while updating fraud account mapping details", e);
            req.setAttribute("msg", e);
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
