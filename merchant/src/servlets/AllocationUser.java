import com.directi.pg.Logger;
import com.directi.pg.Merchants;
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


/**
 * Created by admin on 4/25/2016.
 */
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
        Merchants merchants = new Merchants();
        if (!merchants.isLoggedIn(session))
        {
            logger.debug("member is logout ");
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        String userId = request.getParameter("userid");
        String merchantId = request.getParameter("merchantid");
        String login = request.getParameter("login");
        String[] masterModuleId = request.getParameterValues("moduleid");
        String[] subModuleId = request.getParameterValues("submoduleid");
       // StringBuilder sErrorMessage = new StringBuilder();

        RequestDispatcher rd = request.getRequestDispatcher("/allocationUser.jsp?ctoken=" + user.getCSRFToken());
        MerchantModuleManager merchantModuleManager = new MerchantModuleManager();

        String msg = "";
        try
        {
            if (masterModuleId != null || subModuleId != null)
            {
                merchantModuleManager.isMappingRemoved(userId);
                merchantModuleManager.isSubMappingRemoved(userId);
                if (masterModuleId != null)
                {
                    for (String moduleId : masterModuleId)
                    {
                        MerchantModulesMappingVO merchantModulesMappingVO = new MerchantModulesMappingVO();
                        merchantModulesMappingVO.setMerchantId(merchantId);
                        merchantModulesMappingVO.setModuleId(moduleId);
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
                    for (String moduleId1 : subModuleId)
                    {
                        /* if (moduleId1.equals("8"))
                        {
                           sErrorMessage.append("Module is not allowed to Submerchant");
                            if (sErrorMessage.length() > 0)
                            {
                                request.setAttribute("error", sErrorMessage.toString());
                                rd.forward(request, response);
                                return;
                            }


                        }
                        else
                        {*/
                            MerchantModulesMappingVO merchantModulesMappingVO = new MerchantModulesMappingVO();
                            merchantModulesMappingVO.setMerchantId(merchantId);
                            merchantModulesMappingVO.setSubModuleId(moduleId1);
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
            msg = "Internal error occurred,please contact your merchant.";
            request.setAttribute("message", msg);
            request.setAttribute("login", login);
            request.setAttribute("userid", userId);
            rd.forward(request, response);
            return;
        }
        request.setAttribute("message", msg);
        request.setAttribute("login", login);
        request.setAttribute("userid", userId);
        rd.forward(request, response);
    }
}
