import com.directi.pg.*;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
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


public class UnblockEmail extends HttpServlet
{
    private static Logger logger = new Logger(UnblockEmail.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Entering in UnBlockEmail");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        logger.debug("success");
        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        res.setContentType("text/html");
        ServletContext application = getServletContext();
        PrintWriter out = res.getWriter();
        logger.debug("Get requested parameter");

        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        String emailaddr=null;
        try
        {
            validateMandatoryParameter(req);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid Email ID",e);
            sErrorMessage.append("No email address is UNblocked.<br> \r\n");
        }
        emailaddr = req.getParameter("emailaddr");

        logger.debug("execute delete query to unblock email"+emailaddr);
        StringBuffer emailquery = new StringBuffer("delete from blockedemail where emailaddr=? and type='email'");

        Connection con = null;
        PreparedStatement pstmt=null;
        try
        {
            con = Database.getConnection();
            pstmt= con.prepareStatement(emailquery.toString());
            pstmt.setString(1,emailaddr);
            int i = pstmt.executeUpdate();
            if (i > 0)
            {
                sSuccessMessage.append("Email addresses  "+emailaddr+"  is UNblocked. <br>  \r\n");
            }

            StringBuilder chargeBackMessage = new StringBuilder();
            chargeBackMessage.append(sSuccessMessage.toString());
            chargeBackMessage.append("<BR/>");
            chargeBackMessage.append(sErrorMessage.toString());

            String redirectpage = "/servlet/BlockedEmailList?ctoken="+user.getCSRFToken();
            req.setAttribute("cbmessage", chargeBackMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
            rd.forward(req, res);

        }
        catch (SystemError se)
        {
            logger.error("SystemError in UnblockEmail::::::",se);
            out.println(Functions.ShowMessage("Error", "Internal Error while Unblock Email"));

        }
        catch (Exception e)
        {   logger.error("SystemError in UnblockEmail::::::",e);
            out.println(Functions.ShowMessage("Error!", "Internal Error while Unblock Email"));
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
        inputFieldsListMandatory.add(InputFields.EMAILADDR);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}
