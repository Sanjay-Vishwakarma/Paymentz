package servlets;

import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.manager.PartnerModuleManager;
import com.manager.vo.PartnerModulesMappingVO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/*
 *Created by IntelliJ IDEA.
 User: Sanjeet
 Date: 15/4/2019
 Time: 12:41 PM
 To change this template use File | Settings | File Templates.
 */

public class PartnerUserModuleAllocation extends HttpServlet
{
    private static Logger logger = new Logger(PartnerUserModuleAllocation.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }

    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            return;
        }

        String userId = request.getParameter("userid");
        String moduleId = request.getParameter("moduleid");
        String partnerId = request.getParameter("partnerid");
        String login = request.getParameter("login");

        String[] masterModuleId = request.getParameterValues("moduleid");
        String[] subModuleId = request.getParameterValues("submoduleid");

        RequestDispatcher rd = request.getRequestDispatcher("/partnerUserModuleAllocation.jsp?ctoken=" + user.getCSRFToken());
        PartnerModuleManager partnerModuleManager = new PartnerModuleManager();

        String msg = "";
        String successMsg = "";

        try
        {
            if (masterModuleId != null || subModuleId != null)
            {
                PartnerModulesMappingVO partnerModulesMappingVO = null;
                partnerModuleManager.isSubMappingRemoved(userId);
                partnerModuleManager.isMappingRemoved(userId);
                if (masterModuleId != null)
                {
                    for (String moduleId1 : masterModuleId)
                    {
                        partnerModulesMappingVO = new PartnerModulesMappingVO();
                        partnerModulesMappingVO.setModuleId(moduleId1);
                        partnerModulesMappingVO.setUserId(userId);
                        partnerModulesMappingVO.setPartnerId(partnerId);

                        String status = partnerModuleManager.addNewModuleMapping(partnerModulesMappingVO);
                        if ("success".equalsIgnoreCase(status))
                        {
                            successMsg = "Module Mapping Updated";
                        }
                        else
                        {
                            msg = "Module Mapping Failed";
                        }
                    }
                }
                if (subModuleId != null)
                {
                    for (String subModuleId1 : subModuleId)
                    {
                        partnerModulesMappingVO = new PartnerModulesMappingVO();
                        partnerModulesMappingVO.setSubModuleId(subModuleId1);
                        partnerModulesMappingVO.setUserId(userId);
                        partnerModulesMappingVO.setPartnerId(partnerId);
                        String status = partnerModuleManager.addNewSubModuleMapping(partnerModulesMappingVO);
                        if ("success".equalsIgnoreCase(status))
                        {
                            successMsg = "Module Mapping Updated";
                        }
                        else
                        {
                            msg = "Module Mapping Failed";
                        }
                    }
                }
            }
            else
            {
                partnerModuleManager.isMappingRemoved(userId);
                partnerModuleManager.isSubMappingRemoved(userId);
                successMsg = "Module Mapping Updated";
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException:::::", e);
            msg = "Internal error occurred,please contact your Partner.";
            request.setAttribute("login", login);
            request.setAttribute("userid", userId);
            request.setAttribute("moduleid", moduleId);
            request.setAttribute("partnerid", partnerId);
            request.setAttribute("message", msg);
            rd.forward(request, response);
            return;
        }
        request.setAttribute("login", login);
        request.setAttribute("userid", userId);
        request.setAttribute("moduleid", moduleId);
        request.setAttribute("partnerid", partnerId);
        request.setAttribute("message", msg);
        request.setAttribute("success", successMsg);
        rd.forward(request, response);
    }
}
