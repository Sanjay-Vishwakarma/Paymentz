import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.FraudRuleManager;
import com.manager.vo.fraudruleconfVOs.FraudSystemAccountVO;
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

import java.util.*;
/**
 Created by IntelliJ IDEA.
 User: Supriya
 Date: 10/2/16
 Time: 4:50 PM
 **/
public class ManagePartnerFraudSystemAccount extends HttpServlet
{
    private static Logger logger = new Logger(ManagePartnerFraudSystemAccount.class.getName());
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

        RequestDispatcher rd=request.getRequestDispatcher("/managePartnerFraudSystemAccount.jsp?ctoken="+user.getCSRFToken());

        String partnerId = null;
        String fsAccountId =null;
        String isActive=null;
        partnerId = request.getParameter("partnerid");
        fsAccountId = request.getParameter("fsAccount");
        isActive = request.getParameter("isActive");

        StringBuffer errorMsg = new StringBuffer();
        StringBuffer message=new StringBuffer();
        if (!ESAPI.validator().isValidInput("partnerid", partnerId, "Numbers", 10, false))
        {
            errorMsg.append("Invalid Partner<BR>");
        }
        if (!ESAPI.validator().isValidInput("fsAccount", fsAccountId,"SafeString", 100, false)){
            errorMsg.append("Invalid FraudSystem Account<BR>");
        }
        if(!ESAPI.validator().isValidInput("isActive",isActive,"SafeString",2,false))
        {
            errorMsg.append("Invalid IsActive<BR>");
        }
        if(errorMsg.length()>0)
        {
            request.setAttribute("statusMsg", errorMsg.toString());
            rd.forward(request, response);
            return;
        }

        FraudSystemAccountVO fraudSystemAccountVO = new FraudSystemAccountVO();
        fraudSystemAccountVO.setFraudSystemAccountId(fsAccountId);

        MerchantFraudAccountVO merchantFraudAccountVO=new MerchantFraudAccountVO();
        merchantFraudAccountVO.setIsActive(isActive);

        try
        {
            FraudRuleManager fraudRuleManager = new FraudRuleManager();
            if(fraudRuleManager.isAlreadyAllocated(partnerId, fsAccountId))
            {
                String status = fraudRuleManager.allocateNewFraudSystemAccount(partnerId, fsAccountId,isActive);
                if ("success".equals(status))
                {
                    message.append("Allocated successfully.");
                }
                else
                {
                    message.append("Allocation failed.");
                }
            }
            else
            {
                message.append("Allocation already available.");
            }
            request.setAttribute("statusMsg", message.toString());
            rd.forward(request, response);
            return;
        }
        catch (PZDBViolationException pze)
        {
            logger.error("PZDBViolationException::::::",pze);
            request.setAttribute("statusMsg","Internal error while processing your request" );
            rd.forward(request, response);
            return;
        }
        catch (Exception e)
        {
            logger.error("Exception::::::",e);
            request.setAttribute("statusMsg","Internal error while processing your request");
            rd.forward(request, response);
            return;
        }
    }
}