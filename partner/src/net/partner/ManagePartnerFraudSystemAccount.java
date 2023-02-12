package net.partner;

import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.manager.FraudRuleManager;
import com.manager.vo.fraudruleconfVOs.FraudSystemAccountVO;
import com.manager.vo.fraudruleconfVOs.MerchantFraudAccountVO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
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
 Created by IntelliJ IDEA.
 User: NAMRATA B. BARI
 Date: 21/02/2020
 Time: 12:59 PM
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
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String ManagePartnerFraudSystemAccount_Partner_errormsg = StringUtils.isNotEmpty(rb1.getString("ManagePartnerFraudSystemAccount_Partner_errormsg")) ? rb1.getString("ManagePartnerFraudSystemAccount_Partner_errormsg") : "Invalid Partner<BR>";
        String ManagePartnerFraudSystemAccount_fraudsystem_errormsg = StringUtils.isNotEmpty(rb1.getString("ManagePartnerFraudSystemAccount_fraudsystem_errormsg")) ? rb1.getString("ManagePartnerFraudSystemAccount_fraudsystem_errormsg") : "Invalid FraudSystem Account<BR>";
        String ManagePartnerFraudSystemAccount_isactive_errormsg = StringUtils.isNotEmpty(rb1.getString("ManagePartnerFraudSystemAccount_isactive_errormsg")) ? rb1.getString("ManagePartnerFraudSystemAccount_isactive_errormsg") : "Invalid IsActive<BR>";
        String ManagePartnerFraudSystemAccount_allocated_errormsg = StringUtils.isNotEmpty(rb1.getString("ManagePartnerFraudSystemAccount_allocated_errormsg")) ? rb1.getString("ManagePartnerFraudSystemAccount_allocated_errormsg") : "Allocated successfully.";
        String ManagePartnerFraudSystemAccount_already_errormsg = StringUtils.isNotEmpty(rb1.getString("ManagePartnerFraudSystemAccount_already_errormsg")) ? rb1.getString("ManagePartnerFraudSystemAccount_already_errormsg") : "Allocation already available.";


        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
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
            errorMsg.append(ManagePartnerFraudSystemAccount_Partner_errormsg);
        }
        if (!ESAPI.validator().isValidInput("fsAccount", fsAccountId,"SafeString", 100, false)){
            errorMsg.append(ManagePartnerFraudSystemAccount_fraudsystem_errormsg);
        }
        if(!ESAPI.validator().isValidInput("isActive",isActive,"SafeString",2,false))
        {
            errorMsg.append(ManagePartnerFraudSystemAccount_isactive_errormsg);
        }
        if(errorMsg.length()>0)
        {
            request.setAttribute("error", errorMsg.toString());
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
                    message.append(ManagePartnerFraudSystemAccount_allocated_errormsg);
                }
                else
                {
                    errorMsg.append(ManagePartnerFraudSystemAccount_isactive_errormsg);
                }
            }
            else
            {
                message.append(ManagePartnerFraudSystemAccount_already_errormsg);
            }
            request.setAttribute("statusMsg", message.toString());
            request.setAttribute("error", errorMsg.toString());
            rd.forward(request, response);
            return;
        }
        catch (PZDBViolationException pze)
        {
            logger.error("PZDBViolationException::::::",pze);
            request.setAttribute("error","Internal error while processing your request" );
            rd.forward(request, response);
            return;
        }
        catch (Exception e)
        {
            logger.error("Exception::::::",e);
            request.setAttribute("error","Internal error while processing your request");
            rd.forward(request, response);
            return;
        }
    }
}