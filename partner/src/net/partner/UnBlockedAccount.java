package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.PartnerManager;
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

/**
 * Created by IntelliJ IDEA.
 * User: Ajit
 * Date: Aug 17, 2012
 * Time: 1:36:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class UnBlockedAccount extends HttpServlet
{
    private static Logger logger = new Logger(UnBlockedAccount.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        logger.debug("Entering in UnBlock Partner Account");
        HttpSession session = req.getSession();
        PartnerFunctions partner=new PartnerFunctions();
        PartnerManager partnerManager = new PartnerManager();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        logger.debug("success");

        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        res.setContentType("text/html");
        logger.debug("Get requested parameter");
        String login=null;
        try
        {
            InputValidator inputValidator = new InputValidator();
            List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
            inputFieldsListMandatory.add(InputFields.LOGIN);

            inputValidator.InputValidations(req,inputFieldsListMandatory,false);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid Merchant login",e);
        }
        login = req.getParameter("login");
        Connection con = null;
        PreparedStatement pstmt = null;
        try
        {
            String msg= "";
            boolean flag = partnerManager.getUnBlockedAccount(login);
            if (flag)
            {
                logger.debug("process is done successfully");
                msg= login+" Account has been Unblocked successfully";
            }
            req.setAttribute("msg",msg);
            RequestDispatcher rd = req.getRequestDispatcher("BlockedMerchantList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SystemError se)
        {
            logger.error("SystemError in UnblockEmail::::::",se);
        }
        catch (Exception e)
        {
            logger.error("SystemError in UnblockEmail::::::",e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
    }
   /* private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.LOGIN);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }*/
}
