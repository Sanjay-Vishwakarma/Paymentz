package net.partner;

import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.PartnerManager;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Namrata on 6/13/2020.
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
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partnerFunctions = new PartnerFunctions();
        if(!partnerFunctions.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/logout.jsp");
            return;
        }
        String errorMsg = "";
        String partnerId = req.getParameter("partnerId");

        res.setContentType("text/html");
        Functions functions = new Functions();

        if(!ESAPI.validator().isValidInput("partnerid", partnerId, "Numbers", 10, false)){
            errorMsg = "Invalid partnerId";
        }else if(functions.isValueNull(partnerId) && partnerId.equals(String.valueOf(session.getAttribute("partnerId"))))
        {
            errorMsg = errorMsg + "Invalid Access";
        }
        else if (functions.isValueNull(partnerId) && !partnerFunctions.isPartnerSuperpartnerMapped(partnerId,String.valueOf(session.getAttribute("partnerId"))))
        {
            errorMsg = errorMsg + "Invalid Partner Mapping";
        }

        if(functions.isValueNull(errorMsg)){
            req.setAttribute("error", errorMsg);
            RequestDispatcher rd = req.getRequestDispatcher("/addTemplateColors.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        try
        {

                PartnerManager partnerManager = new PartnerManager();
                Map<String, Object> partnerPresentTemplateDetails = partnerManager.getPartnerSavedMemberTemplateDetails(partnerId);
                req.setAttribute("partnerTemplateColor", partnerPresentTemplateDetails);
                req.setAttribute("partnerId", partnerId);
                RequestDispatcher rd = req.getRequestDispatcher("/addTemplateColors.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
               return;

        }
        catch (Exception e)
        {
            logger.error("Error while set reserves:", e);
        }

    }
}
