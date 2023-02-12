import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.FraudRuleManager;
import com.manager.vo.fraudruleconfVOs.MerchantFraudAccountVO;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sneha on 22/7/15.
 */
public class ActionFraudSystemMerchantAccount extends HttpServlet
{
    private static Logger logger = new Logger(ActionFraudSystemMerchantAccount.class.getName());
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

        logger.debug("Entering in ActionFraudSystemMerchantAccount");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String EOL = "<BR>";
        StringBuffer msg = new StringBuffer();
        RequestDispatcher rd = req.getRequestDispatcher("/actionFraudSystemMerchantAccount.jsp?ctoken=" + user.getCSRFToken());
        Functions functions = new Functions();
        try
        {
            validateOptionalParameter(req);
            if (functions.hasHTMLTags(req.getParameter("submerchantUsername"))){
                msg.append("Invalid Fraud System SubMerchant UserName <BR>");
            }
            if (functions.hasHTMLTags(req.getParameter("submerchantPassword"))){
                msg.append("Invalid Fraud System SubMerchant Password <BR>");
            }
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
        String merchantfraudserviceid = req.getParameter("mappingid");
        String fssubaccountid = req.getParameter("fssubaccountid");

        try
        {
            MerchantFraudAccountVO merchantAccountVO = new MerchantFraudAccountVO();
            FraudRuleManager ruleManager = new FraudRuleManager();

            if ("modify".equalsIgnoreCase(action))
            {
                merchantAccountVO = ruleManager.getMerchantFraudAccountDetails(merchantfraudserviceid, fssubaccountid);
                req.setAttribute("merchantAccountVO", merchantAccountVO);
                rd.forward(req, res);
            }
            else if("update".equalsIgnoreCase(action))
            {
                StringBuffer updateMsg = new StringBuffer();
                merchantAccountVO.setSubmerchantUsername(req.getParameter("submerchantUsername"));
                merchantAccountVO.setSubmerchantPassword(req.getParameter("submerchantPassword"));
                merchantAccountVO.setIsActive(req.getParameter("isActive"));
                merchantAccountVO.setMerchantFraudAccountId(merchantfraudserviceid);
                merchantAccountVO.setFsSubAccountId(fssubaccountid);
                merchantAccountVO.setIsVisible(req.getParameter("isVisible"));
                merchantAccountVO.setIsOnlineFraudCheck(req.getParameter("isOnlineFraudCheck"));
                merchantAccountVO.setIsAPIUser(req.getParameter("isAPIUser"));

                String status = ruleManager.updateMerchantFraudAccount(merchantAccountVO);

                if("success".equals(status))
                {
                    updateMsg.append("Merchant Fraud Account Configuration Updated Successfully");
                }
                else
                {
                    updateMsg.append("Merchant Fraud Account Configuration Updation Failed");
                }
                req.setAttribute("updateMsg", updateMsg.toString());
                rd.forward(req, res);
            }
        }
        catch (PZDBViolationException e)
        {
            logger.debug("PZDBViolationException ::"+e);
            req.setAttribute("updateMsg", "Error While Reading The Data From Database");
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
