
import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: admin
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

        logger.debug("Entering in UnBlock Merchant Account");
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        logger.debug("success");

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        res.setContentType("text/html");

        PrintWriter out = res.getWriter();
        logger.debug("Get requested parameter");
        //Vector blockedemail = (Vector) application.getAttribute("BLOCKEDEMAIL");
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

        logger.debug("execute update query to unblock account"+login);
        StringBuffer emailquery = new StringBuffer("update user set unblocked='unlocked' ,faillogincount='0' where unblocked='locked' and faillogincount>='3' and login=? and roles='merchant'");

        Connection con = null;
        PreparedStatement pstmt=null;
        try
        {
            con = Database.getConnection();
            pstmt= con.prepareStatement(emailquery.toString());
            pstmt.setString(1,login);
            int i = pstmt.executeUpdate();
            if (i > 0)
                // blockedemail.remove(emailaddr);
                logger.debug("process is done successfully");
            //application.setAttribute("BLOCKEDEMAIL", blockedemail);
            String msg= login+" Account has been Unlocked successfully";
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
    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.LOGIN);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}
