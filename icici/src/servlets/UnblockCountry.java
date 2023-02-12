import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
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
import java.util.Vector;


public class UnblockCountry extends HttpServlet
{
    private static Logger logger = new Logger(UnblockCountry.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {   logger.debug("Entering in UnblockCountry");
        res.setContentType("text/html");
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        logger.debug("success");


        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        ServletContext application = getServletContext();
        PrintWriter out = res.getWriter();

        Vector blockedCountry = (Vector) application.getAttribute("BLOCKEDCOUNTRY");
        String country=null;
        try
        {
           //country= ESAPI.validator().getValidInput("country",req.getParameter("country"),"SafeString",60,false);
            validateMandatoryParameter(req);
        }
        catch (ValidationException e)
        {
            logger.error("Invalid country",e);
        }
        country = req.getParameter("country");
        String countryQuery = "delete from blockedcountry where country=?";
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();

        Connection con = null;
        PreparedStatement pstmt=null;
        try
        {
            con = Database.getConnection();
            pstmt=con.prepareStatement(countryQuery);
            pstmt.setString(1,country);
            int i = pstmt.executeUpdate();
            if (i > 0)
                blockedCountry.remove(country);

            application.setAttribute("BLOCKEDCOUNTRY", blockedCountry);
            sSuccessMessage.append(country+" is Unblocked. <br>  \r\n");

        }
        catch (SystemError se)
        {   logger.error("SystemError in UnblockCountry::::::",se);

            sErrorMessage.append("Internal Error while Unblock Country.<br> \r\n");
        }
        catch (Exception e)
        {   logger.error("Exception in UnblockCountry:::::::",e);
            sErrorMessage.append("Internal Error while Unblock Country.<br> \r\n");
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

        String redirectpage = "/servlet/BlockedCountryList?ctoken="+user.getCSRFToken();
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);
    }
    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.COUNTRY);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}
