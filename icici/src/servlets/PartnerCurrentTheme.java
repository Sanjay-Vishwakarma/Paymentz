import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.manager.PartnerManager;
import com.manager.vo.CurrentThemeVO;
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
 * Created by admin on 11-12-2017.
 */
public class PartnerCurrentTheme extends HttpServlet
{
    private static Logger logger=new Logger(PartnerCurrentTheme.class.getName());
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
        String currentTheme=request.getParameter("currentTheme");
        String themeType=request.getParameter("theme_type");

        RequestDispatcher rd=request.getRequestDispatcher("/partnerCurrentTheme.jsp?ctoken="+user.getCSRFToken());

        if (!ESAPI.validator().isValidInput("currentTheme", currentTheme, "Description", 100, false))
        {
            msg="Invalid Theme Name";
            logger.error(msg);
            request.setAttribute("message",msg);
            rd.forward(request,response);
            return;
        }

        PartnerManager partnerManager=new PartnerManager();
        CurrentThemeVO currentThemeVO=new CurrentThemeVO();
        currentThemeVO.setCurrentThemeName(currentTheme);
        currentThemeVO.setThemeType(themeType);
        try
        {
            //New Check Theme is available in Default_Theme table.
            if(partnerManager.isUniqueCurrentTheme(currentThemeVO))
            {
                String status=partnerManager.addNewCurrentTheme(currentThemeVO);
                if("success".equalsIgnoreCase(status))
                {
                    msg="New Theme Added Successfully.";
                }
                else
                {
                    msg="New Theme Adding Failed.";
                }
            }
            else
            {
                msg=" Theme Already Exist.";
            }
            request.setAttribute("message",msg);
            rd.forward(request,response);
        }
        catch (PZDBViolationException dbe)
        {
            logger.error("PZDBViolationException in PartnerCurrentTheme.java------",dbe);
            PZExceptionHandler.handleDBCVEException(dbe, "", "");
            request.setAttribute("message","Internal Error occurred : Please contact your Admin");
            rd.forward(request,response);
            return;
        }

    }
}
