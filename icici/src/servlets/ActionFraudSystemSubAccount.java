import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.FraudRuleManager;
import com.manager.vo.fraudruleconfVOs.FraudSystemSubAccountVO;

import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import java.util.*;


/**
 * Created by Sneha on 21/7/15.
 */
public class ActionFraudSystemSubAccount extends HttpServlet
{
    private static Logger logger = new Logger(ActionFraudSystemSubAccount.class.getName());
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
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String EOL = "<BR>";
        StringBuffer msg = null;
        RequestDispatcher rd = req.getRequestDispatcher("/actionFraudSystemSubAccount.jsp?ctoken="+user.getCSRFToken());
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            logger.error("Enter valid input", e);
            msg.append ("<center><font class=\"text\" face=\"arial\"><b>"+ msg + e.getMessage() + EOL + "</b></font></center>");
            logger.debug("message..." + e.getMessage());
            req.setAttribute("msg",msg.toString());
            rd.forward(req, res);
            return;
        }

        String action = req.getParameter("action");

        String fssubaccountid = req.getParameter("mappingid");
        String fsaccountid = req.getParameter("fsaccountid");

        try
        {
            FraudSystemSubAccountVO subAccountVO = new FraudSystemSubAccountVO();
            FraudRuleManager ruleManager = new FraudRuleManager();
            if(action.equalsIgnoreCase("modify"))
            {
                subAccountVO = ruleManager.getFraudSubAccountDetails(fssubaccountid, fsaccountid);
                req.setAttribute("subaccountDetails", subAccountVO);
                rd.forward(req, res);
            }
            else if("update".equalsIgnoreCase(action))
            {
                StringBuffer updateMsg = new StringBuffer();

                subAccountVO.setFraudSystemSubAccountId(fssubaccountid);
                subAccountVO.setFraudSystemAccountId(fsaccountid);
                subAccountVO.setSubAccountName(req.getParameter("subaccountname"));
                subAccountVO.setUserName(req.getParameter("subusername"));
                subAccountVO.setPassword(req.getParameter("subpwd"));
                subAccountVO.setIsActive(req.getParameter("isActive"));
                String status = ruleManager.updateFraudSubAccount(subAccountVO);
                if("success".equals(status))
                {
                    updateMsg.append("Fraud Sub Account Configuration Updated Successfully");
                }
                else
                {
                    updateMsg.append("Fraud Sub Account Configuration Updating Failed");
                }
                req.setAttribute("updateMsg", updateMsg.toString());
                rd.forward(req, res);
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("SQL Exception while updating fraud account mapping details", e);
            req.setAttribute("updateMsg", e.getMessage());
            rd.forward(req, res);

        }
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
