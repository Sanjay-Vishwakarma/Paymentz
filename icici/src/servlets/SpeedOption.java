import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.AppRequestManager;
import com.manager.ApplicationManager;
import com.enums.Module;
import com.vo.applicationManagerVOs.ApplicationManagerVO;

import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * Created by admin on 2/4/16.
 */
public class SpeedOption extends HttpServlet
{

    private static Logger log = new Logger(SpeedOption.class.getName());
    Functions functions = new Functions();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        //Manager instance

        ApplicationManager applicationManager = new ApplicationManager();
        AppRequestManager appRequestManager = new AppRequestManager();
        //FileManager fileManager = new FileManager();
        //Vo instance
        ApplicationManagerVO applicationManagerVO=null;
        boolean isUpdate=false;

        ValidationErrorList validationErrorList = null;
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/speedoptionview.jsp?MES=Success&ctoken="+user.getCSRFToken());
        RequestDispatcher rdError= request.getRequestDispatcher("/speedoptionview.jsp?MES=ERR&ctoken="+user.getCSRFToken());

        try
        {
            applicationManagerVO=appRequestManager.getApplicationManagerVO(session);
            applicationManagerVO.setSpeed_user(Module.ADMIN.name());

            if("Save".equals(request.getParameter("action")))
            {
                validationErrorList=appRequestManager.validateSpeed1Request(request,null,applicationManagerVO,true,false);
            }
            else
            {
                validationErrorList=appRequestManager.validateSpeed1Request(request,null,applicationManagerVO,false,false);
            }


            if("Save".equals(request.getParameter("action")))
            {
                isUpdate=applicationManager.saveStep1Page(applicationManagerVO);
                applicationManagerVO.setNotificationMessage(isUpdate,"Organisation Profile"," Saved Successfully");
            }
            else
            {
                if(validationErrorList.isEmpty())
                {
                    log.debug("submitAllProfile");
                    isUpdate=applicationManager.submitAllProfile(applicationManagerVO,true,false);
                    applicationManagerVO.setNotificationMessage(isUpdate,"Organisation Profile"," Submitted Successfully");
                }
            }

        }
        catch(Exception e)
        {
            log.error("Exception in Speed option",e);

        }

        //set in session value
        request.setAttribute("applicationManagerVO",applicationManagerVO);
        session.setAttribute("applicationManagerVO",applicationManagerVO);
        request.setAttribute("validationErrorList",validationErrorList);

        if(!validationErrorList.isEmpty())
        {
            rdError.forward(request,response);
            return;
        }
        else
        {
            request.setAttribute("update",isUpdate);
            rdSuccess.forward(request,response);
            return;
        }
    }

}
