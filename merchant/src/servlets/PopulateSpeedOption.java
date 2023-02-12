import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.manager.ApplicationManager;
import com.manager.vo.ActionVO;
import com.vo.applicationManagerVOs.ApplicationManagerVO;
import com.vo.applicationManagerVOs.NavigationVO;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/20/15
 * Time: 8:15 PM
 * To change this template use File | Settings | File Templates.
 */

public class PopulateSpeedOption extends HttpServlet
{


    private Logger logger=new Logger(PopulateSpeedOption.class.getName());

    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {

        //instance created for util classes
        Functions functions=new Functions();
        Merchants merchants =new Merchants();
        //instance created for Vo
        ApplicationManagerVO applicationManagerVO=new ApplicationManagerVO();
        NavigationVO navigationVO = new NavigationVO();
        ActionVO actionVO = new ActionVO();
        //instance of manager
        ApplicationManager applicationManager = new ApplicationManager();
        HttpSession session = Functions.getNewSession(request);
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!merchants.isLoggedIn(session))
        {   logger.debug("Merchant is logout ");
            response.sendRedirect("/merchant/logout.jsp");
            return;
        }


        RequestDispatcher rdSuccessU = request.getRequestDispatcher("/speedoption.jsp?MES=Success&ctoken="+user.getCSRFToken());

        try
        {
            logger.debug("inside populate Application");
            applicationManagerVO.setMemberId(session.getAttribute("merchantid").toString());
            if(functions.isValueNull(String.valueOf(session.getAttribute("applicationManagerVO"))))
            {
                applicationManagerVO=(ApplicationManagerVO)session.getAttribute("applicationManagerVO");
            }
            else
            {
                applicationManager.populateAppllicationData(applicationManagerVO);
            }
            
            /*if(functions.isValueNull(applicationManagerVO.getStatus()) && (ApplicationStatus.STEP1_SUBMIT.toString().equals(applicationManagerVO.getStatus()) || ApplicationStatus.SUBMIT.toString().equals(applicationManagerVO.getStatus()) || ApplicationStatus.SAVED.toString().equals(applicationManagerVO.getStatus())))
            {
                actionVO.setView();
            }*/

            //set in session value
            session.setAttribute("actionVO",actionVO);
            session.setAttribute("applicationManagerVO",applicationManagerVO);
            session.setAttribute("navigationVO",navigationVO);

            rdSuccessU.forward(request,response);

        }
        catch (Exception e)
        {
            logger.error("Mail class exception::",e);
        }

    }


}

