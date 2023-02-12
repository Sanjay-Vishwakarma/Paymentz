package net.partner;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.PartnerManager;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 28-Jun-20.
 */
@WebServlet(name = "UnblockMerchantUser")
public class UnblockMerchantUser extends HttpServlet
{
    private static Logger logger = new Logger(UnblockMerchantUser.class.getName());
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Entering in UnblockMerchantUser");
        HttpSession session = req.getSession();
        PartnerFunctions partner=new PartnerFunctions();
        PartnerManager partnerManager = new PartnerManager();
        // String partnerid=(String)session.getAttribute("merchantid");
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        logger.debug("success");

        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        res.setContentType("text/html");
        String login=null;
        try
        {
            validateMandatoryParameter(req);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid Merchant login",e);
        }
        login = req.getParameter("login");
        try
        {
            boolean flag = partnerManager.getUnBlockedUserAccount(login);
            if (flag)
            {
                logger.debug("process is done successfully");
                String msg= login+" Account has been Unblocked successfully";
                req.setAttribute("msg",msg);
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError in UnblockMerchantUser::::::",se);
        }
        catch (Exception e)
        {
            logger.error("SystemError in UnblockMerchantUser::::::",e);
        }
        RequestDispatcher rd = req.getRequestDispatcher("/memberChildList.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.LOGIN);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}
