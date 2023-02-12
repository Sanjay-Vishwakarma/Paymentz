package servlets;

import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.PartnerManager;
import com.manager.enums.PartnerTemplatePreference;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/15/15
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddTemplateColor extends HttpServlet
{
    private static Logger logger = new Logger(AddTemplateColor.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("member is logout ");
            res.sendRedirect("/icici/admin/logout.jsp");
            return;
        }
           String errorMsg = "";
           String partnerId = req.getParameter("partnerId");
           res.setContentType("text/html");
        try
        {
            if (ESAPI.validator().isValidInput("partnerId ", req.getParameter("partnerId"), "OnlyNumber", 100, false))
            {
                PartnerManager partnerManager = new PartnerManager();
                Map<String, Object> partnerPresentTemplateDetails = partnerManager.getPartnerSavedMemberTemplateDetails(partnerId);
                req.setAttribute("partnerTemplateColor", partnerPresentTemplateDetails);
                req.setAttribute("partnerId", partnerId);
            }
            else
            {
                errorMsg = errorMsg + "Invalid Partner Id";
            }
        }
        catch (Exception e)
        {
            logger.error("Error while set reserves:", e);
        }

             req.setAttribute("errormessage", errorMsg);
             RequestDispatcher rd = req.getRequestDispatcher("/addTemplateColors.jsp?ctoken=" + user.getCSRFToken());
             rd.forward(req, res);
    }
}