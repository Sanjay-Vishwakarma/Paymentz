package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ProfileManagementManager;
import com.manager.vo.userProfileVOs.UserSetting;
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
 * Created on 24/9/15.
 */
public class GenerateUserProfileXML extends HttpServlet
{
    private static Logger logger = new Logger(GenerateUserProfileXML.class.getName());
    private Functions functions = new Functions();

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        HttpSession session = Functions.getNewSession(request);


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
        UserSetting userSetting=null;

        RequestDispatcher rdError= request.getRequestDispatcher("/userProfile.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        try
        {
            userSetting=profileManagementManager.getMapUserProfile((String) session.getAttribute("merchantid"));

            if(userSetting!=null && !userSetting.getMembers().isEmpty())
            {
                profileManagementManager.generateUserProfileXML(userSetting,response);
            }
            else
            {
                request.setAttribute("catchError","There is no Risk Profile");
                rdError.forward(request,response);
                return;
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDViolation Exception while getting User profile details:::",e);
            PZExceptionHandler.handleDBCVEException(e, "partnerId:::" + (String) session.getAttribute("merchantid"), PZOperations.USERID);
            request.setAttribute("catchError","Kindly check for the User Prolfile after sometime");
            rdError.forward(request,response);
            return;
        }
        catch (PZTechnicalViolationException e)
        {
            logger.error("PZDViolation Exception while getting User profile details:::",e);
            PZExceptionHandler.handleTechicalCVEException(e, "partnerId:::" + (String) session.getAttribute("merchantid"), PZOperations.USERID);
            request.setAttribute("catchError","Kindly check for the User Profile after sometime");
            rdError.forward(request,response);
            return;
        }

    }

}
