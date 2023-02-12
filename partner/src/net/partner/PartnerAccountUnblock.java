package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.payment.emexpay.vo.request;
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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Admin on 18-Jun-20.
 */
@WebServlet(name = "PartnerAccountUnblock")
public class PartnerAccountUnblock extends HttpServlet
{
    Logger logger=new Logger(PartnerAccountUnblock.class.getName());
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request,response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session=request.getSession();
        logger.debug("Entering in UnblockPartnerAccountt");
        PartnerFunctions partner=new PartnerFunctions();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }
        response.setContentType("text/html");

        //PrintWriter out = response.getWriter();
        String login=null;
        try
        {
            validateMandatoryParameter(request);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid Merchant login",e);
        }
        login = request.getParameter("login");
        Hashtable hash = null;
        StringBuffer emailquery = new StringBuffer("UPDATE user SET unblocked='unlocked' ,faillogincount='0' WHERE unblocked='locked' AND login= ? AND roles='partner'");
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
            request.setAttribute("msg",msg);
            RequestDispatcher rd = request.getRequestDispatcher("/partnerlist.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
        }
        catch (SystemError se)
        {
            logger.error("SystemError in PartnerAccountUnblock::::::",se);
        }
        catch (Exception e)
        {
            logger.error("SystemError in PartnerAccountUnblock::::::",e);
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
