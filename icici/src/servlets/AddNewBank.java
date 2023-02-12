import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.AppFileManager;
import com.vo.applicationManagerVOs.AppFileDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Vishal on 6/5/2017.
 */
public class AddNewBank extends HttpServlet
{
    public static Logger logger = new Logger(AddNewBank.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        HttpSession session = request.getSession();
        logger.debug("Enter in New partner ");
        if (!Admin.isLoggedIn(session))
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        response.setContentType("multipart/form-data");
        AppFileManager fileManager = new AppFileManager();

        try
        {
            Map<String,AppFileDetailsVO> fileDetailsVOMap = null;

            fileDetailsVOMap = fileManager.addBankTemplate(request);
            logger.debug("fileDetailsVOMap-->"+fileDetailsVOMap);

            request.setAttribute("fileDetailsVOMap", fileDetailsVOMap);
            RequestDispatcher rd = request.getRequestDispatcher("/addNewBank.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
        }
        catch (PZTechnicalViolationException e)
        {
            logger.debug(e.getMessage());
        }
        catch (PZDBViolationException e)
        {
           logger.error("Catch PZDBViolationException...",e);
        }
        catch (SystemError systemError)
        {
           logger.error("Catch SystemError...",systemError);
        }
    }
}
