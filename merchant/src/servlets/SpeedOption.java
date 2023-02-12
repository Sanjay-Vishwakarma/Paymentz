import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
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
import java.io.PrintWriter;
import java.util.ResourceBundle;
import java.util.function.Function;

/**
 * Created by admin on 2/2/2016.
 */
public class SpeedOption extends HttpServlet
{
    private static Logger log = new Logger(SpeedOption.class.getName());

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {


        PrintWriter out = response.getWriter();
        Merchants merchants = new Merchants();
        HttpSession session = request.getSession();
        if (!merchants.isLoggedIn(session))
        {
            response.sendRedirect("/merchant/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        //Manager instance

        ApplicationManager applicationManager = new ApplicationManager();
        AppRequestManager appRequestManager = new AppRequestManager();
        //FileManager fileManager = new FileManager();
        //Vo instance
        ApplicationManagerVO applicationManagerVO=null;
        boolean isUpdate=false;


        ValidationErrorList validationErrorList = null;
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/speedoption.jsp?MES=Success&ctoken="+user.getCSRFToken());
        RequestDispatcher rdError= request.getRequestDispatcher("/speedoption.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        Functions functions=new Functions();
        ResourceBundle rb1 = null;
        String language_property1 = (String)session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);

        String SpeedOption_PrivetForm_errormsg = !functions.isEmptyOrNull(rb1.getString("SpeedOption_PrivetForm_errormsg"))?rb1.getString("SpeedOption_PrivetForm_errormsg"): "Privet Form";
        String SpeedOption_saved_errormsg = !functions.isEmptyOrNull(rb1.getString("SpeedOption_saved_errormsg"))?rb1.getString("SpeedOption_saved_errormsg"): "Saved Successfully";
        String SpeedOption_submitted_errormsg = !functions.isEmptyOrNull(rb1.getString("SpeedOption_submitted_errormsg"))?rb1.getString("SpeedOption_submitted_errormsg"): "Submitted Successfully";


        try
        {
            applicationManagerVO=appRequestManager.getApplicationManagerVO(session);
            applicationManagerVO.setMemberId(session.getAttribute("merchantid").toString());
            applicationManagerVO.setSpeed_user(Module.MERCHANT.name());

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
                applicationManagerVO.setNotificationMessage(isUpdate,SpeedOption_PrivetForm_errormsg,SpeedOption_saved_errormsg);
                applicationManagerVO.setMessageColorClass("bg-info");
            }
            else
            {
                if(validationErrorList.isEmpty())
                {
                    log.debug("submitAllProfile");
                    isUpdate=applicationManager.submitAllProfile(applicationManagerVO,true,false);
                    applicationManagerVO.setNotificationMessage(isUpdate,SpeedOption_PrivetForm_errormsg,SpeedOption_submitted_errormsg);
                    applicationManagerVO.setMessageColorClass("bg-info");
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