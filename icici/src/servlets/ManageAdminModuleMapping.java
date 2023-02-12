import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.AdminModuleManager;
import com.manager.dao.AdminModuleDAO;
import com.manager.vo.AdminModulesMappingVO;

import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 3/9/15
 * Time: 10:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class ManageAdminModuleMapping extends HttpServlet
{
    private static Logger logger = new Logger(ManageAdminModuleMapping.class.getName());

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
            logger.debug("session out");
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }

        StringBuffer msg = new StringBuffer();
        List<String> adminList = new ArrayList();
        List<String> moduleList = new ArrayList();
        StringBuffer sb = new StringBuffer();
        String adminId1 = "";
        String moduleId1 = "";
        String moduleId = request.getParameter("moduleid");
        String adminId = request.getParameter("adminid");
        System.out.println("moduleId"+moduleId);

        String[] elements_adminid = adminId.split(",");
        String[] elements_moduleid = moduleId.split(",");
        adminList = Arrays.asList(elements_adminid);
        moduleList = Arrays.asList(elements_moduleid);

        RequestDispatcher rd = request.getRequestDispatcher("/manageAdminModulesMapping.jsp?ctoken=" + user.getCSRFToken());

        if (!ESAPI.validator().isValidInput("moduleid", moduleId, "SafeString", 100, false))
        {
            sb.append("Invalid Module ID");
            sb.append(msg + ",");
            logger.error(msg);

        }
        if (!ESAPI.validator().isValidInput("adminid", adminId, "SafeString", 100, false))
        {
            sb.append("Invalid Admin ID");
            logger.error(msg);
        }
        if (sb.length() > 0)
        {
            request.setAttribute("message", sb.toString());
            rd.forward(request, response);
            return;
        }

        AdminModuleManager adminModuleManager = new AdminModuleManager();
        AdminModulesMappingVO adminModulesMappingVO = new AdminModulesMappingVO();
        String modulename="";
        String adminname="";
        try
        {
            for (String adminsList : adminList)
            {
                adminId1 = adminsList;
                adminModulesMappingVO.setAdminId(adminId1);
                adminname= AdminModuleDAO.getAdminName(adminId1);
                for (String modulelists : moduleList)
                {
                    moduleId1 = modulelists;
                    adminModulesMappingVO.setModuleId(moduleId1);
                    modulename= AdminModuleDAO.getModuleName(moduleId1);


                    //New Check module is available in admin module master.
                    boolean isMappingAvaliable=adminModuleManager.isMappingAvailable(adminModulesMappingVO);
                    if (!isMappingAvaliable)
                    {
                        String status = adminModuleManager.addNewModuleMapping(adminModulesMappingVO);
                        if ("success".equalsIgnoreCase(status))
                        {
                            msg.append(modulename+  " Is Mapped Successfully for " +adminname+"<BR>");
                        }
                        else
                        {
                            msg.append(modulename+ " Module Mapping Failed for " +adminname+"<BR>");
                        }
                    }
                    else
                    {
                        msg.append(modulename+" Is Already Mapped for " +adminname+"<BR>");

                    }
                }

            }
        }
        catch(PZDBViolationException dbe)
        {
            logger.error("PZDBViolationException in AddNewAdminModule.java------", dbe);
            PZExceptionHandler.handleDBCVEException(dbe, "", "");
            request.setAttribute("message", "Internal Error occurred : Please contact your Admin");
            rd.forward(request, response);
            return;
        }
        if (msg.length() > 0)
        {
            request.setAttribute("message", msg.toString());
            rd.forward(request, response);
            return;
        }
    }
}




