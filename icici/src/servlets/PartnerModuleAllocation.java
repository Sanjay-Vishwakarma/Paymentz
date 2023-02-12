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

/**
 * Created by Admin on 06-03-2020.
 */
public class PartnerModuleAllocation extends HttpServlet
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

        String moduleId = request.getParameter("moduleid");
        String partnerId = request.getParameter("partnerid");
        String role = request.getParameter("role");

        String[] masterModuleId = request.getParameterValues("moduleid");
        String[] subModuleId = request.getParameterValues("submoduleid");

        RequestDispatcher rd = request.getRequestDispatcher("/partnerModuleAllocation.jsp?ctoken=" + user.getCSRFToken());
        PartnerModuleManager partnerModuleManager = new PartnerModuleManager();

        String msg = "";
        String successMsg = "";

        try
        {
            if (masterModuleId != null || subModuleId != null)
            {
                PartnerModulesMappingVO partnerModulesMappingVO = null;
                partnerModuleManager.isPartnersSubMappingRemovedNew(partnerId);
                partnerModuleManager.isPartnerMappingRemovedNew(partnerId);
                if (masterModuleId != null)
                {
                    for (String moduleId1 : masterModuleId)
                    {
                        partnerModulesMappingVO = new PartnerModulesMappingVO();
                        partnerModulesMappingVO.setModuleId(moduleId1);
                        partnerModulesMappingVO.setUserId("0");
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
                        partnerModulesMappingVO.setUserId("0");
                        partnerModulesMappingVO.setPartnerId(partnerId);
                        String status = partnerModuleManager.addNewSubModuleMapping(partnerModulesMappingVO);
                        if ("success".equalsIgnoreCase(status))
                        {
                            System.out.println(successMsg);
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
                partnerModuleManager.isPartnerMappingRemoved(partnerId);
                partnerModuleManager.isPartnersSubMappingRemoved(partnerId);
                successMsg = "Module Mapping Updated";
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException:::::", e);
            msg = "Internal error occurred,please contact your Partner.";
            request.setAttribute("moduleid", moduleId);
            request.setAttribute("partnerid", partnerId);
            request.setAttribute("role", role);
            request.setAttribute("message", msg);
            rd.forward(request, response);
            return;
        }
          request.setAttribute("moduleid", moduleId);
        request.setAttribute("partnerid", partnerId);
        request.setAttribute("role", role);
        request.setAttribute("message", msg);
        request.setAttribute("success", successMsg);
        rd.forward(request, response);
    }
}
