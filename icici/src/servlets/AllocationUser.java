package servlets;

import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.manager.MerchantModuleManager;
import com.manager.vo.MerchantModulesMappingVO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/*Created by IntelliJ IDEA.
  User: sanjeet
  Date: 4/9/19
  Time: 02:42 PM
  To change this template use File | Settings | File Templates.*/

public class AllocationUser extends HttpServlet
{
    private static Logger logger = new Logger(AllocationUser.class.getName());

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

        String msg = "";
        String userId = request.getParameter("userid");
        String moduleId = request.getParameter("moduleid");
        String merchantId = request.getParameter("merchantid");
        String login = request.getParameter("login");

        String[] masterModuleId = request.getParameterValues("moduleid");
        String[] subModuleId = request.getParameterValues("submoduleid");

        logger.debug("inside AllocationUser.java::");

        RequestDispatcher rd = request.getRequestDispatcher("/allocationUser.jsp?ctoken=" + user.getCSRFToken());
        MerchantModuleManager merchantModuleManager = new MerchantModuleManager();
       /* logger.debug("masterModuleId:::"+masterModuleId+":::masterModuleId size::"+masterModuleId.length);
        logger.debug("subModuleId:::"+subModuleId+":::subModuleId size::"+subModuleId.length);
        logger.debug("userId:::"+userId);
        System.out.println("userId:::"+userId);*/

        try
        {
            if (masterModuleId != null || subModuleId != null)
            {
                merchantModuleManager.isMappingRemoved(userId);
                merchantModuleManager.isSubMappingRemoved(userId);

                if (masterModuleId != null)
                {
                    for (String moduleId1 : masterModuleId)
                    {
                        MerchantModulesMappingVO merchantModulesMappingVO = new MerchantModulesMappingVO();
                        merchantModulesMappingVO.setMerchantId(merchantId);
                        merchantModulesMappingVO.setModuleId(moduleId1);
                        merchantModulesMappingVO.setUserId(userId);
                        String status = merchantModuleManager.addNewModuleMapping(merchantModulesMappingVO);
                        if ("success".equalsIgnoreCase(status))
                        {
                            msg = "Module Mapping Updated";
                        }
                        else
                        {
                            msg = "Module Mapping Failed";
                        }
                    }
                }
                if (subModuleId != null)
                {
                    for (String moduleId2 : subModuleId)
                    {
                        MerchantModulesMappingVO merchantModulesMappingVO = new MerchantModulesMappingVO();
                        merchantModulesMappingVO.setMerchantId(merchantId);
                        merchantModulesMappingVO.setSubModuleId(moduleId2);
                        merchantModulesMappingVO.setUserId(userId);
                        String status = merchantModuleManager.addNewSubModuleMapping(merchantModulesMappingVO);
                        if ("success".equalsIgnoreCase(status))
                        {
                            msg = "Module Mapping updated";
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
                merchantModuleManager.isMappingRemoved(userId);
                merchantModuleManager.isSubMappingRemoved(userId);
                msg = "Module mapping updated.";
            }
        }
        catch (PZDBViolationException dbe)
        {
            logger.error("PZDBViolationException:::::", dbe);
            msg = "Internal error occurred,please contact tech support team";
            request.setAttribute("login", login);
            request.setAttribute("userid", userId);
            request.setAttribute("moduleid", moduleId);
           /* request.setAttribute("memberid", memberid);*/
            request.setAttribute("error", msg);
            rd.forward(request, response);
            return;
        }
        if ("Module Mapping Failed".equals(msg))
        {
            request.setAttribute("message", msg);
        }
        else
        {
            request.setAttribute("success", msg);
        }
        rd.forward(request, response);
    }
}
