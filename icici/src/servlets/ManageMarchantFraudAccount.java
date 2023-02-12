import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.FraudRuleManager;
import com.manager.vo.fraudruleconfVOs.FraudSystemSubAccountVO;
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

/**
 * Created by Sneha on 16/7/15.
 */
public class ManageMarchantFraudAccount extends HttpServlet
{
    private static Logger logger = new Logger(ManageMarchantFraudAccount.class.getName());
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
        String website =request.getParameter("subaccountName");
        String submerchantUsername=request.getParameter("submerchantUsername");
        String submerchantPassword =request.getParameter("submerchantPassword");
        String memberId =request.getParameter("memberid");
        String isActive =request.getParameter("isActive");
        String isVisible =request.getParameter("isVisible");
        String isOnlineFraudCheck =request.getParameter("isonlinefraudcheck");
        String isAPIUser =request.getParameter("isapiuser");

        RequestDispatcher rd=request.getRequestDispatcher("/manageMarchantFraudAccount.jsp?ctoken="+user.getCSRFToken());
        StringBuffer errorMsg = new StringBuffer();
        StringBuffer message = new StringBuffer();
        Functions functions=new Functions();

        if (!ESAPI.validator().isValidInput("fsAccount", fsAccount,"Numbers", 11, false)){
            errorMsg.append("Invalid Fraud System Account<BR>");
        }
        if (!ESAPI.validator().isValidInput("website", website, "Description", 255, false)){
            errorMsg.append("Invalid Fraud Account/Website<BR>");
        }
        if (!ESAPI.validator().isValidInput("memberid", memberId,"Numbers",11,false)){
            errorMsg.append("Invalid Member ID <BR>");
        }
        if (!ESAPI.validator().isValidInput("partnerId", request.getParameter("partnerId"),"Numbers",11,false)){
            errorMsg.append("Invalid Partner ID <BR>");
        }
        if (!ESAPI.validator().isValidInput("submerchantUsername", request.getParameter("submerchantUsername"),"SafeString",100,true) || functions.hasHTMLTags(request.getParameter("submerchantUsername"))){
            errorMsg.append("Invalid Fraud System SubMerchant UserName <BR>");
        }
        if (functions.hasHTMLTags(submerchantPassword)){
            errorMsg.append("Invalid Fraud System SubMerchant Password <BR>");
        }
        if(errorMsg.length()>0)
        {
            request.setAttribute("statusMsg",errorMsg.toString());
            logger.error(errorMsg.toString());
            rd.forward(request,response);
            return;
        }

        if(!functions.isValueNull(isAPIUser))
        {
           isAPIUser="N";
        }

        FraudSystemSubAccountVO systemSubAccountVO = new FraudSystemSubAccountVO();

        systemSubAccountVO.setFraudSystemAccountId(fsAccount);
        systemSubAccountVO.setSubAccountName(website);
        systemSubAccountVO.setIsActive(isActive);
        systemSubAccountVO.setSubmerchantUsername(submerchantUsername);
        systemSubAccountVO.setSubmerchantPassword(submerchantPassword);

        MerchantFraudAccountVO merchantFraudAccountVO=new MerchantFraudAccountVO();
        merchantFraudAccountVO.setMemberId(memberId);
        merchantFraudAccountVO.setIsActive(isActive);
        merchantFraudAccountVO.setIsVisible(isVisible);
        merchantFraudAccountVO.setIsOnlineFraudCheck(isOnlineFraudCheck);
        merchantFraudAccountVO.setIsAPIUser(isAPIUser);
        systemSubAccountVO.setMerchantFraudAccountVO(merchantFraudAccountVO);

        try
        {
            FraudRuleManager fraudRuleManager = new FraudRuleManager();
            if(fraudRuleManager.isFraudSystemSubAccountUnique(systemSubAccountVO.getFraudSystemAccountId(),systemSubAccountVO.getSubAccountName()))
            {
                if(fraudRuleManager.isMerchantFraudAccountAvailable(memberId,systemSubAccountVO.getSubAccountName()) || fraudRuleManager.isMerchantInternalFraudAccountAvailable(memberId,systemSubAccountVO.getSubAccountName()))
                {
                    String status = fraudRuleManager.addNewFraudSystemSubAccountFROMPSP(systemSubAccountVO);
                    if ("success".equals(status))
                    {
                        message.append("Merchant Fraud Account Added Successfully");
                    }
                    else
                    {
                        message.append("Merchant Fraud Account Adding Failed");
                    }
                }
                else
                {
                    message.append("Merchant Fraud Account Already Available");
                }
            }
            else
            {
                message.append("Fraud Account Already Available");
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
