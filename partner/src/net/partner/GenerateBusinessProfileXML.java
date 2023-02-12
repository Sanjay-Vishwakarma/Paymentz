package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ProfileManagementManager;
import com.manager.vo.businessRuleVOs.BusinessProfile;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.operations.PZOperations;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created on 23/9/15.
 */
public class GenerateBusinessProfileXML extends HttpServlet
{
    private static Logger logger = new Logger(GenerateRiskProfileXML.class.getName());
    private Functions functions = new Functions();

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        HttpSession session = Functions.getNewSession(request);

        String partnerid = (String)session.getAttribute("merchantid");

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        //Manager Instance
        ProfileManagementManager profileManagementManager = new ProfileManagementManager();

        //VO instance
        BusinessProfile businessProfile=null;

        RequestDispatcher rdError= request.getRequestDispatcher("/businessProfile.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        try
        {
            businessProfile=profileManagementManager.getBusinessProfileWithAllDetails((String) session.getAttribute("merchantid"), null, null);

            if(businessProfile!=null && !businessProfile.getProfiles().isEmpty())
            {
                profileManagementManager.generateBusinessProfileXML(businessProfile, response);
            }
            else
            {
                request.setAttribute("catchError","There is no Business Profile");
                rdError.forward(request,response);
                return;
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDViolation Exception while getting risk profile details:::",e);
            PZExceptionHandler.handleDBCVEException(e, "partnerId:::" + (String) session.getAttribute("merchantid"), PZOperations.BUSINESSPROFILE);
            request.setAttribute("catchError","Kindly check for the Business Profile after sometime");
            rdError.forward(request,response);
            return;
        }
        catch (PZTechnicalViolationException e)
        {
            logger.error("PZDViolation Exception while getting risk profile details:::",e);
            PZExceptionHandler.handleTechicalCVEException(e, "partnerId:::" + (String) session.getAttribute("merchantid"), PZOperations.BUSINESSPROFILE);
            request.setAttribute("catchError","Kindly check for the Business Profile after sometime");
            rdError.forward(request,response);
            return;
        }

    }

}
