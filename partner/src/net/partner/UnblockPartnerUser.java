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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Admin on 24-Jun-20.
 */
@WebServlet(name = "UnblockPartnerUser")
public class UnblockPartnerUser extends HttpServlet
{
    Logger logger=new Logger(UnblockPartnerUser.class.getName());
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request,response);
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Entering in UnBlock Subpartner Account");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner=new PartnerFunctions();
        PartnerManager partnerManager = new PartnerManager();
        String partnerid=(String)session.getAttribute("merchantid");

        System.out.println("hello");
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
        Hashtable hash = null;
        StringBuffer emailquery = new StringBuffer("UPDATE user SET unblocked='unlocked',faillogincount='0' WHERE unblocked='locked' AND faillogincount>='5' AND login= ? AND roles='subpartner'");

        Connection con = null;
        PreparedStatement pstmt = null;
        try
        {
            con = Database.getConnection();
            pstmt= con.prepareStatement(emailquery.toString());
            pstmt.setString(1,login);
            logger.debug("update query::::"+pstmt);
            int i = pstmt.executeUpdate();
            if (i > 0)
                logger.debug("process is done successfully");
            String msg= login+" Account has been Unlocked successfully";
            req.setAttribute("msg",msg);
            RequestDispatcher rd = req.getRequestDispatcher("/partnerChildList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SystemError se)
        {
            logger.error("SystemError in UnblockPartnerUser::::::",se);
        }
        catch (Exception e)
        {
            logger.error("SystemError in UnblockPartnerUser::::::",e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
    }

    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.LOGIN);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}
