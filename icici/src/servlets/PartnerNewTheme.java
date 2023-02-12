import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.manager.PartnerManager;
import com.manager.vo.DefaultThemeVO;
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

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 3/9/15
 * Time: 10:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerNewTheme extends HttpServlet
{
    private static Logger  logger=new Logger(PartnerNewTheme.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doProcess(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {

        doProcess(request, response);
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
        String defaultTheme=request.getParameter("defaultTheme");

        RequestDispatcher rd=request.getRequestDispatcher("/partnerNewTheme.jsp?ctoken="+user.getCSRFToken());

        if (!ESAPI.validator().isValidInput("defaultTheme", defaultTheme, "SafeString", 100, false))
        {
            msg="Invalid Theme Name";
            logger.error(msg);
            request.setAttribute("message",msg);
            rd.forward(request,response);
            return;
        }

        PartnerManager partnerManager=new PartnerManager();
        DefaultThemeVO defaultThemeVO=new DefaultThemeVO();
        defaultThemeVO.setDefaultThemeName(defaultTheme);
        try
        {
            //New Check Theme is available in Default_Theme table.
            if(partnerManager.isUniqueTheme(defaultThemeVO))
            {
                String status=partnerManager.addNewDefaultTheme(defaultThemeVO);
                if("success".equalsIgnoreCase(status))
                {
                    msg="New Default Theme Added Successfully.";
                }
                else
                {
                    msg="New Default Theme Adding Failed.";
                }
            }
            else
            {
                msg="Default Theme Is Already Exist.";
            }
            request.setAttribute("message",msg);
            rd.forward(request,response);
        }
        catch (PZDBViolationException dbe)
        {
            logger.error("PZDBViolationException in PartnerNewTheme.java------",dbe);
            PZExceptionHandler.handleDBCVEException(dbe,"", "");
            request.setAttribute("message","Internal Error occurred : Please contact your Admin");
            rd.forward(request,response);
            return;
        }

    }
}
