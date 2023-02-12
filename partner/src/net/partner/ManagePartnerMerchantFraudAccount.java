package net.partner;

import com.directi.pg.Logger;
import com.manager.FraudRuleManager;
import com.manager.vo.fraudruleconfVOs.MerchantFraudAccountVO;
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
import java.io.PrintWriter;

/**
 * Created by Sneha on 9/9/15.
 */
public class ManagePartnerMerchantFraudAccount extends HttpServlet
{
    private static Logger logger = new Logger(ManagePartnerMerchantFraudAccount.class.getName());
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

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        String mid = req.getParameter("mid");
        String accid = req.getParameter("fsaccountid");
        String isActive = req.getParameter("isActive");
        String isVisible = req.getParameter("isVisible");

        RequestDispatcher rd=req.getRequestDispatcher("/managePartnerMerchantFraudAccount.jsp?ctoken=" + user.getCSRFToken());
        StringBuffer sb = new StringBuffer();
        if (!ESAPI.validator().isValidInput("memberid", mid, "Numbers",11, false))
        {
            sb.append("Invalid MemberId <br>");
        }
        if (!ESAPI.validator().isValidInput("fsaccountid", accid, "Numbers", 11,false))
        {
            sb.append("Invalid Fraud Account/Website <br>");
        }
        if (!ESAPI.validator().isValidInput("isTest", isActive, "SafeString", 255, false))
        {
            sb.append("Invalid isActive<br>");
        }
        if (!ESAPI.validator().isValidInput("isVisible", isVisible, "SafeString", 255, false))
        {
            sb.append("Invalid isVisible<br>");
        }
        if(sb.length()>0)
        {
            req.setAttribute("statusMsg", sb.toString());
            rd.forward(req, res);
            return;
        }

        MerchantFraudAccountVO merchantFraudAccountVO = new MerchantFraudAccountVO();
        merchantFraudAccountVO.setMemberId(mid);
        merchantFraudAccountVO.setFsSubAccountId(accid);
        merchantFraudAccountVO.setIsActive(isActive);
        merchantFraudAccountVO.setIsVisible(isVisible);
        try
        {
            FraudRuleManager fraudRuleManager = new FraudRuleManager();

            if(fraudRuleManager.isMerchantUnique(merchantFraudAccountVO.getMemberId()) && fraudRuleManager.isFraudAccountUnique(merchantFraudAccountVO.getFsSubAccountId()))
            {
                String status = fraudRuleManager.addNewMerchantFraudAccount(merchantFraudAccountVO);
                if ("success".equals(status))
                {
                    sb.append("Fraud Account Mapped Successfully");
                }
                else
                {
                    sb.append("Fraud Account Mapping Failed");
                }
            }
            else
            {
                sb.append("Fraud Account Already Mapped,Please Select Diff One");
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error(e);
            sb.append(e.getMessage());
        }
        req.setAttribute("statusMsg", sb.toString());
        rd.forward(req, res);
        return;
    }
}
