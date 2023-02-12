package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.manager.FraudRuleManager;
import com.manager.vo.fraudruleconfVOs.MerchantFraudAccountVO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Created by Sneha on 9/9/15.
 */
public class ActionPartnerMerchantFraudAccount extends HttpServlet
{
    private static Logger logger = new Logger(ActionPartnerMerchantFraudAccount.class.getName());
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
        String ActionPartnerMerchantFraudAccount_updated_errormsg = StringUtils.isNotEmpty(rb1.getString("ActionPartnerMerchantFraudAccount_updated_errormsg")) ? rb1.getString("ActionPartnerMerchantFraudAccount_updated_errormsg") : "Merchant Fraud Configuration Updated Successfully";
        String ActionPartnerMerchantFraudAccount_updation_errormsg = StringUtils.isNotEmpty(rb1.getString("ActionPartnerMerchantFraudAccount_updation_errormsg")) ? rb1.getString("ActionPartnerMerchantFraudAccount_updation_errormsg") : "Merchant Fraud Configuration Updation Failed";

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        String merchantfraudserviceid =req.getParameter("merchantfraudserviceid");
        String action =req.getParameter("action");
        String fssubaccountid =req.getParameter("fssubaccountid");

        RequestDispatcher rd = req.getRequestDispatcher("/actionPartnerMerchantFraudAccount.jsp?ctoken=" + user.getCSRFToken());

        try
        {
            MerchantFraudAccountVO merchantAccountVO = new MerchantFraudAccountVO();
            FraudRuleManager ruleManager = new FraudRuleManager();
            if ("modify".equalsIgnoreCase(action))
            {
                try
                {
                    merchantAccountVO = ruleManager.getMerchantFraudAccountDetails(merchantfraudserviceid,fssubaccountid);
                    req.setAttribute("merchantAccountVO", merchantAccountVO);
                }
                catch (PZDBViolationException e)
                {
                    logger.error("PZDBViolationException::"+e);
                    req.setAttribute("updateMsg", "Could Not Getting Merchant Fraud Account Details,Some Technical Exception");
                }
                catch (Exception e)
                {
                    logger.error("Exception::"+e);
                    req.setAttribute("updateMsg", "Could Not Getting Merchant Fraud Account Details,Some Technical Exception");
                }
                rd.forward(req, res);
                return;
            }
            else if ("update".equalsIgnoreCase(action))
            {
                StringBuffer updateMsg = new StringBuffer();
                merchantAccountVO.setSubmerchantUsername(req.getParameter("submerchantUsername"));
                merchantAccountVO.setSubmerchantPassword(req.getParameter("submerchantPassword"));
                merchantAccountVO.setIsActive(req.getParameter("isActive"));
                merchantAccountVO.setMerchantFraudAccountId(merchantfraudserviceid);
                merchantAccountVO.setFsSubAccountId(fssubaccountid);
                merchantAccountVO.setIsVisible(req.getParameter("isVisible"));
                Functions functions = new Functions();
                if(functions.hasHTMLTags(req.getParameter("submerchantUsername"))){
                    req.setAttribute("updateMsg", "Invalid Fraud System SubMerchant Username");
                    rd = req.getRequestDispatcher("/partnerMerchantFraudAccount.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req,res);
                    return;
                }
                if(functions.hasHTMLTags(req.getParameter("submerchantPassword"))){
                    req.setAttribute("updateMsg", "Invalid Fraud System SubMerchant Password");
                    rd = req.getRequestDispatcher("/partnerMerchantFraudAccount.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req,res);
                    return;
                }

                String status = ruleManager.updateMerchantFraudAccountFROMPSP(merchantAccountVO);
                if("success".equals(status))
                {
                    updateMsg.append(ActionPartnerMerchantFraudAccount_updated_errormsg);
                }
                else
                {
                    updateMsg.append(ActionPartnerMerchantFraudAccount_updation_errormsg);
                }
                req.setAttribute("updateMsg", updateMsg.toString());
                rd = req.getRequestDispatcher("/partnerMerchantFraudAccount.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req,res);
                return;

            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException::"+e);
            req.setAttribute("updateMsg", "Merchant Fraud Account Configuration Could Not Updated,Some Technical Exception");
            rd.forward(req,res);
            return;
        }
        catch (Exception e)
        {
            logger.error("Exception::"+e);
            req.setAttribute("updateMsg", "Merchant Fraud Account Configuration Could Not Updated,Some Technical Exception");
            rd.forward(req,res);
            return;

        }
        //rd.forward(req,res);
    }
}
