package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.manager.FraudRuleManager;
import com.manager.vo.fraudruleconfVOs.FraudSystemSubAccountVO;
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
 * Created by Sneha on 10/9/15.
 */
public class AddNewPartnerMerchantFraudAccount extends HttpServlet
{
    private static Logger logger = new Logger(PartnerMerchantFraudAccountList.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
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
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String AddNewPartnerMerchantFraudAccount_Fraud_errormsg = StringUtils.isNotEmpty(rb1.getString("AddNewPartnerMerchantFraudAccount_Fraud_errormsg")) ? rb1.getString("AddNewPartnerMerchantFraudAccount_Fraud_errormsg") : "Invalid Fraud System Account <br>";
        String AddNewPartnerMerchantFraudAccount_account_errormsg = StringUtils.isNotEmpty(rb1.getString("AddNewPartnerMerchantFraudAccount_account_errormsg")) ? rb1.getString("AddNewPartnerMerchantFraudAccount_account_errormsg") : "Invalid Fraud Account/ Website <br>";
        String AddNewPartnerMerchantFraudAccount_merchantid_errormsg = StringUtils.isNotEmpty(rb1.getString("AddNewPartnerMerchantFraudAccount_merchantid_errormsg")) ? rb1.getString("AddNewPartnerMerchantFraudAccount_merchantid_errormsg") : "Invalid Merchant ID <br>";
        String AddNewPartnerMerchantFraudAccount_isactive_errormsg = StringUtils.isNotEmpty(rb1.getString("AddNewPartnerMerchantFraudAccount_isactive_errormsg")) ? rb1.getString("AddNewPartnerMerchantFraudAccount_isactive_errormsg") : "Invalid IsActive <br>";
        String AddNewPartnerMerchantFraudAccount_isvisible_errormsg = StringUtils.isNotEmpty(rb1.getString("AddNewPartnerMerchantFraudAccount_isvisible_errormsg")) ? rb1.getString("AddNewPartnerMerchantFraudAccount_isvisible_errormsg") : "Invalid IsVisible <br>";
        String AddNewPartnerMerchantFraudAccount_added_errormsg = StringUtils.isNotEmpty(rb1.getString("AddNewPartnerMerchantFraudAccount_added_errormsg")) ? rb1.getString("AddNewPartnerMerchantFraudAccount_added_errormsg") : "Merchant Fraud Account Added Successfully.";
        String AddNewPartnerMerchantFraudAccount_adding_errormsg = StringUtils.isNotEmpty(rb1.getString("AddNewPartnerMerchantFraudAccount_adding_errormsg")) ? rb1.getString("AddNewPartnerMerchantFraudAccount_adding_errormsg") : "Merchant Fraud Account Adding Failed.";
        String AddNewPartnerMerchantFraudAccount_already_errormsg = StringUtils.isNotEmpty(rb1.getString("AddNewPartnerMerchantFraudAccount_already_errormsg")) ? rb1.getString("AddNewPartnerMerchantFraudAccount_already_errormsg") : "Fraud Account/Website Already Used,Please Enter Unique.";
        String AddNewPartnerMerchantFraudAccount_available_errormsg = StringUtils.isNotEmpty(rb1.getString("AddNewPartnerMerchantFraudAccount_available_errormsg")) ? rb1.getString("AddNewPartnerMerchantFraudAccount_available_errormsg") : "Merchant Fraud Account Is Already Available.";

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        RequestDispatcher rd=request.getRequestDispatcher("/addNewPartnerMerchantFraudAccount.jsp?ctoken="+user.getCSRFToken());
        StringBuffer errorMsg = new StringBuffer();
        Functions functions = new Functions();

        String fsAccount =request.getParameter("fsAccount");
        String subaccountName =request.getParameter("subaccountName");
        String memberId=request.getParameter("memberid");
        String submerchantUsername=request.getParameter("submerchantUsername");
        String submerchantPassword=request.getParameter("submerchantPassword");
        String isActive =request.getParameter("isActive");
        String isVisible =request.getParameter("isVisible");

        if (!ESAPI.validator().isValidInput("fsAccount", fsAccount,"Numbers",10,false)){
            errorMsg.append(AddNewPartnerMerchantFraudAccount_Fraud_errormsg);
        }
        if (!ESAPI.validator().isValidInput("subaccountName", subaccountName, "SafeString",25,false) || functions.hasHTMLTags(subaccountName)){
            errorMsg.append(AddNewPartnerMerchantFraudAccount_account_errormsg);
        }
        if (!ESAPI.validator().isValidInput("memberId",memberId,"Numbers",10,false)){
            errorMsg.append(AddNewPartnerMerchantFraudAccount_merchantid_errormsg);
        }
        if (!ESAPI.validator().isValidInput("isActive",isActive,"SafeString",2,false)){
            errorMsg.append(AddNewPartnerMerchantFraudAccount_isactive_errormsg);
        }
        if (functions.hasHTMLTags(submerchantUsername)){
            errorMsg.append("Invalid Fraud System SubMerchant Username");
        }
        if (functions.hasHTMLTags(submerchantPassword)){
            errorMsg.append("Invalid Fraud System SubMerchant Password");
        }
        if (!ESAPI.validator().isValidInput("isVisible",isVisible,"SafeString",2,false)){
            errorMsg.append(AddNewPartnerMerchantFraudAccount_isvisible_errormsg);
        }
        if(errorMsg.length()>0)
        {
            request.setAttribute("statusMsg",errorMsg);
            logger.error(errorMsg.toString());
            rd.forward(request,response);
            return;
        }

        StringBuffer message = new StringBuffer();
        StringBuffer successMessage = new StringBuffer();
        FraudSystemSubAccountVO systemSubAccountVO = new FraudSystemSubAccountVO();
        systemSubAccountVO.setFraudSystemAccountId(fsAccount);
        systemSubAccountVO.setSubAccountName(subaccountName);
        systemSubAccountVO.setIsActive(isActive);
        systemSubAccountVO.setSubmerchantUsername(submerchantUsername);
        systemSubAccountVO.setSubmerchantPassword(submerchantPassword);

        MerchantFraudAccountVO merchantFraudAccountVO=new MerchantFraudAccountVO();
        merchantFraudAccountVO.setMemberId(memberId);
        merchantFraudAccountVO.setIsActive(isActive);
        merchantFraudAccountVO.setIsVisible(isVisible);
        merchantFraudAccountVO.setIsOnlineFraudCheck("Y");
        merchantFraudAccountVO.setIsAPIUser("N");
        systemSubAccountVO.setMerchantFraudAccountVO(merchantFraudAccountVO);
        try
        {
            FraudRuleManager fraudRuleManager = new FraudRuleManager();
            if(fraudRuleManager.isMerchantFraudAccountAvailable(memberId,subaccountName) || fraudRuleManager.isMerchantInternalFraudAccountAvailable(memberId,subaccountName))
            {
                if(fraudRuleManager.isFraudSystemSubAccountUnique(subaccountName))
                {
                    String status = fraudRuleManager.addNewFraudSystemSubAccountFROMPSP(systemSubAccountVO);
                    if ("success".equals(status))
                    {
                        successMessage.append(AddNewPartnerMerchantFraudAccount_added_errormsg);
                    }
                    else
                    {
                        message.append(AddNewPartnerMerchantFraudAccount_adding_errormsg);
                    }
                }
                else
                {
                    message.append(AddNewPartnerMerchantFraudAccount_already_errormsg);
                }
            }
            else
            {
                message.append(AddNewPartnerMerchantFraudAccount_available_errormsg);
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error(e);
            message.append("Could Not Added Merchant Fraud Account,Some Internal Exception.");
        }
        catch (Exception e)
        {
            logger.error(e);
            message.append("Could Not Added Merchant Fraud Account,Some Internal Exception.");
        }
        request.setAttribute("statusMsg",message);
        request.setAttribute("success",successMessage);
        rd.forward(request, response);
    }
}
