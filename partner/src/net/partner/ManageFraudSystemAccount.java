package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.manager.FraudRuleManager;
import com.manager.vo.fraudruleconfVOs.FraudSystemAccountVO;
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
 * Created by NAMRATA B. BARI on 20/01/2020.
 */
public class ManageFraudSystemAccount extends HttpServlet
{
    private static Logger logger = new Logger(ManageFraudSystemAccount.class.getName());
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
        String ManageFraudSystemAccount_Fraud_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageFraudSystemAccount_Fraud_errormsg")) ? rb1.getString("ManageFraudSystemAccount_Fraud_errormsg") : "Invalid Fraud System ID<BR>";
        String ManageFraudSystemAccount_Merchantid_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageFraudSystemAccount_Merchantid_errormsg")) ? rb1.getString("ManageFraudSystemAccount_Merchantid_errormsg") : "Invalid Fraud System MerchantId<BR>";
        String ManageFraudSystemAccount_name_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageFraudSystemAccount_name_errormsg")) ? rb1.getString("ManageFraudSystemAccount_name_errormsg") : "Invalid Contact Name<BR>";
        String ManageFraudSystemAccount_mail_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageFraudSystemAccount_mail_errormsg")) ? rb1.getString("ManageFraudSystemAccount_mail_errormsg") : "Invalid Contact Email<BR>";
        String ManageFraudSystemAccount_username_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageFraudSystemAccount_username_errormsg")) ? rb1.getString("ManageFraudSystemAccount_username_errormsg") : "Invalid UserName<BR>";
        String ManageFraudSystemAccount_added_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageFraudSystemAccount_added_errormsg")) ? rb1.getString("ManageFraudSystemAccount_added_errormsg") : "Fraud System Account Added Successfully.";
        String ManageFraudSystemAccount_adding_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageFraudSystemAccount_adding_errormsg")) ? rb1.getString("ManageFraudSystemAccount_adding_errormsg") : "Fraud System Account Adding Failed.";
        String ManageFraudSystemAccount_alraedy_errormsg = StringUtils.isNotEmpty(rb1.getString("ManageFraudSystemAccount_alraedy_errormsg")) ? rb1.getString("ManageFraudSystemAccount_alraedy_errormsg") : "Fraud System Account Already Used.";

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        String fsid = request.getParameter("fsid");
        String accountName = request.getParameter("accountName");
        String userName = request.getParameter("userName");
        String pwd = request.getParameter("pwd");
        String isTest = request.getParameter("isTest");
        String contactname = request.getParameter("contactname");
        String contactemail = request.getParameter("contactemail");

        RequestDispatcher rd=request.getRequestDispatcher("/manageFraudSystemAccount.jsp?ctoken="+user.getCSRFToken());

        StringBuffer errorMsg = new StringBuffer();
        StringBuffer message=new StringBuffer();
        Functions functions = new Functions();

        if (!ESAPI.validator().isValidInput("fsid", fsid, "Numbers", 10, false))
        {
            errorMsg.append(ManageFraudSystemAccount_Fraud_errormsg);
        }
        if (!ESAPI.validator().isValidInput("accountName", accountName, "SafeString", 255, false) || functions.hasHTMLTags(accountName))
        {
            errorMsg.append(ManageFraudSystemAccount_Merchantid_errormsg);
        }
        if (!ESAPI.validator().isValidInput("contactname", contactname, "SafeString", 255, false) || functions.hasHTMLTags(contactname))
        {
            errorMsg.append(ManageFraudSystemAccount_name_errormsg);
        }
        if (!ESAPI.validator().isValidInput("contactemail", contactemail, "Email",100, false) )
        {
            errorMsg.append(ManageFraudSystemAccount_mail_errormsg);
        }
        if (!ESAPI.validator().isValidInput("userName", userName, "SafeString", 255, true) || functions.hasHTMLTags(userName))
        {
            errorMsg.append(ManageFraudSystemAccount_username_errormsg);
        }
        if (!ESAPI.validator().isValidInput("pwd", pwd, "SafeString", 255, true) || functions.hasHTMLTags(pwd))
        {
            errorMsg.append("Invalid Password<BR>");
        }
        if(errorMsg.length()>0)
        {
            request.setAttribute("error", errorMsg.toString());
            logger.error(errorMsg.toString());
            rd.forward(request, response);
            return;
        }

        FraudSystemAccountVO fraudSystemAccountVO = new FraudSystemAccountVO();

        fraudSystemAccountVO.setFraudSystemId(fsid);
        fraudSystemAccountVO.setAccountName(accountName);
        fraudSystemAccountVO.setUserName(userName);
        fraudSystemAccountVO.setPassword(pwd);
        fraudSystemAccountVO.setIsTest(isTest);
        fraudSystemAccountVO.setContactName(contactname);
        fraudSystemAccountVO.setContactEmail(contactemail);

        try
        {
            FraudRuleManager fraudRuleManager = new FraudRuleManager();
            if(fraudRuleManager.isFraudSystemAccountUnique(accountName))
            {
                String status = fraudRuleManager.addNewFraudSystemAccount(fraudSystemAccountVO);
                if ("success".equals(status))
                {
                    message.append(ManageFraudSystemAccount_added_errormsg);
                }
                else
                {
                    errorMsg.append(ManageFraudSystemAccount_adding_errormsg);
                }
            }
            else
            {
                message.append(ManageFraudSystemAccount_alraedy_errormsg);
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error(e);
            errorMsg.append(e.getMessage());
        }
        catch (Exception e)
        {
            logger.error(e);
            errorMsg.append(e.getMessage());
        }
        request.setAttribute("statusMsg", message.toString());
        request.setAttribute("error", errorMsg.toString());
        rd.forward(request, response);
        return;
    }
}