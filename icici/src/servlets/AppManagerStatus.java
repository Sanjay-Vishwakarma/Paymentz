import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.AppRequestManager;
import com.manager.ApplicationManager;
import com.manager.enums.ApplicationStatus;
import com.manager.enums.BankApplicationStatus;
import com.manager.enums.Module;
import com.manager.vo.ActionVO;
import com.vo.applicationManagerVOs.ApplicationManagerVO;
import com.vo.applicationManagerVOs.BankApplicationMasterVO;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;




public class AppManagerStatus extends HttpServlet
{
    private static Logger logger = new Logger(AppManagerStatus.class.getName());
    private Functions functions = new Functions();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {

        HttpSession session = request.getSession();

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        User user = (User) session.getAttribute("ESAPIUserSessionKey");  

        ApplicationManager applicationManager = new ApplicationManager();

        AppRequestManager appRequestManager =new AppRequestManager();

        ApplicationManagerVO applicationManagerVO=null;

        BankApplicationMasterVO bankApplicationMasterVO=null;

        ActionVO actionVO = new ActionVO();

        boolean saved=false;
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/servlet/ListofAppMember?MES=Success&ctoken="+user.getCSRFToken());

        try
        {
            applicationManagerVO=appRequestManager.getApplicationManagerVO(session);
            applicationManagerVO.setUser(Module.ADMIN.name());
            String speed_status=applicationManagerVO.getSpeed_status();



            if(ApplicationStatus.MODIFIED.name().equals(request.getParameter("status")))
            {
                applicationManagerVO.setAppliedToModify("N");
                //this is for set clause
                bankApplicationMasterVO=new BankApplicationMasterVO();
                bankApplicationMasterVO.setStatus(BankApplicationStatus.INVALIDATED.name());

                saved=applicationManager.updateBankApplicationMasterVO(bankApplicationMasterVO,null,applicationManagerVO.getMemberId());
            }

            logger.debug("SPEED::::"+request.getParameter("SPEED"));

            if(ApplicationStatus.SAVED.name().equals(request.getParameter("status")) || ApplicationStatus.SUBMIT.name().equals(request.getParameter("status")) || ApplicationStatus.MODIFIED.name().equals(request.getParameter("status"))|| ((ApplicationStatus.VERIFIED.name().equals(request.getParameter("status"))) && !("Y".equals(request.getParameter("SPEED")))))
            {
                applicationManagerVO.setStatus(request.getParameter("status"));
                applicationManagerVO.setKyc_Status(request.getParameter("status"));
                applicationManagerVO.setMaf_Status(request.getParameter("status"));
                saved=applicationManager.updateAppManagerStatus(applicationManagerVO);

            }
            else if(ApplicationStatus.STEP1_SAVED.name().equals(request.getParameter("status")) || ApplicationStatus.STEP1_SUBMIT.name().equals(request.getParameter("status")) || ((ApplicationStatus.VERIFIED.name().equals(request.getParameter("status"))) && ("Y".equals(request.getParameter("SPEED")))))
            {
                applicationManagerVO.setSpeed_status(request.getParameter("status"));

                applicationManagerVO.setSpeed_user(applicationManagerVO.getSpeed_user());

                if(functions.isValueNull(applicationManagerVO.getSpeed_user()))
                {
                    applicationManagerVO.setSpeed_user(applicationManagerVO.getSpeed_user());
                }



                saved=applicationManager.updateAppManagerStatus(applicationManagerVO);

            }

            request.setAttribute("SAVED",saved);
            request.setAttribute("apptoid",applicationManagerVO.getMemberId());
            logger.error("saved"+saved);

            rdSuccess.forward(request, response);



        }
        catch (Exception e)
        {
            logger.error("Main class exception::", e);
        }

    }

}