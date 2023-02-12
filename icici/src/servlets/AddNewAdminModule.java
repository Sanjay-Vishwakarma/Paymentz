import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.AdminModuleManager;
import com.manager.vo.AdminModuleVO;

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
public class AddNewAdminModule extends HttpServlet
{
    private static Logger  logger=new Logger(AddNewAdminModule.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doProcess(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {

        doProcess(request,response);
    }
    public void doProcess(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if(!Admin.isLoggedIn(session))
        {
            logger.debug("session out");
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }

        String msg="";
        String moduleName=request.getParameter("modulename");

        RequestDispatcher rd=request.getRequestDispatcher("/addNewAdminModule.jsp?ctoken="+user.getCSRFToken());

        if (!ESAPI.validator().isValidInput("modulename", moduleName, "Description", 100, false))
        {
            msg="Invalid Module Name";
            logger.error(msg);
            request.setAttribute("message",msg);
            rd.forward(request,response);
            return;
        }

        AdminModuleManager adminModuleManager=new AdminModuleManager();
        AdminModuleVO adminModuleVO=new AdminModuleVO();
        adminModuleVO.setModuleName(moduleName);
        try
        {
            //New Check module is available in admin module master.
            if(adminModuleManager.isUniqueName(adminModuleVO))
            {
                String status=adminModuleManager.addNewAdminModule(adminModuleVO);
                if("success".equalsIgnoreCase(status))
                {
                    msg="New Admin Module Added Successfully.";
                }
                else
                {
                    msg="New Admin Module Adding Failed.";
                }
            }
            else
            {
                msg="Module Is Already Available In Module Master.";
            }
            request.setAttribute("message",msg);
            rd.forward(request,response);
        }
        catch (PZDBViolationException dbe)
        {
            logger.error("PZDBViolationException in AddNewAdminModule.java------",dbe);
            PZExceptionHandler.handleDBCVEException(dbe,"", "");
            request.setAttribute("message","Internal Error occurred : Please contact your Admin");
            rd.forward(request,response);
            return;
        }

    }
}
