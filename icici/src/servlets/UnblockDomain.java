import com.directi.pg.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;


public class UnblockDomain extends HttpServlet
{

    private static Logger logger = new Logger(UnblockDomain.class.getName());
    //private Object PareparedStatement;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Entering in  UnblockDomain");
        res.setContentType("text/html");
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

                logger.debug("success");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        ServletContext application = getServletContext();
        PrintWriter out = res.getWriter();
        String domain=null;
        //Vector blockeddomain = (Vector) application.getAttribute("BLOCKEDDOMAIN");
        try
        {
            //domain = ESAPI.validator().getValidInput("domain",req.getParameter("domain"),"URL",50,false);
            validateMandatoryParameter(req);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid Domain",e);
            sErrorMessage.append("Internal Error Exist record not Found.<br> \r\n");
        }
        domain = req.getParameter("domain");

        StringBuffer domainquery = new StringBuffer("delete from blockedemail where emailaddr=? and type='domain'");
        Connection con = null;
        PreparedStatement pstmt = null;
        try
        {
            con = Database.getConnection();
            pstmt=con.prepareStatement(domainquery.toString());
            pstmt.setString(1,domain);
            int i = pstmt.executeUpdate();
            if (i > 0)
            {  // blockeddomain.remove(domain);
             sSuccessMessage.append("domain names are UNblocked. <br>  \r\n");
            //application.setAttribute("BLOCKEDDOMAIN", blockeddomain);

            }
        }
        catch (SystemError se)
        {   logger.error("SystemError in UnblockDomain::::::",se);

            sErrorMessage.append("Internal Error while Unblock Domain.<br> \r\n");
            //System.out.println(se.toString());

        }
        catch (Exception e)
        {   logger.error("SystemError in UnblockDomain::::::",e);
            sErrorMessage.append("Internal Error while Unblock Domain.<br> \r\n");
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }

        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());

        String redirectpage = "/servlet/BlockedDomainList?ctoken="+user.getCSRFToken();
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);
    }
    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.DOMAIN);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}
