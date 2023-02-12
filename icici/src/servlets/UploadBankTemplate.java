import com.directi.pg.*;
import com.manager.AppFileManager;
import com.vo.applicationManagerVOs.AppFileDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
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
 * Created with IntelliJ IDEA.
 * User: NIKET
 * Date: 4/30/15
 * Time: 10:45 PM
 * To change this template use File | Settings | File Templates.
 */

public class UploadBankTemplate extends HttpServlet
{
    Logger logger = new Logger(UploadBankTemplate.class.getName());

    //private static String log_store = ApplicationProperties.getProperty("LOG_STORE");
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = Functions.getNewSession(request);
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        logger.debug("Entering in  UploadBankTemplate");
        //File Manager Instance
        AppFileManager fileManager= new AppFileManager();
        //Map declaration
        Map<String,AppFileDetailsVO> fileDetailsVOMap=null;
        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }


        StringBuffer queryString = new StringBuffer();
        // RequestDispatcher rdSuccess= request.getRequestDispatcher("/gatewayInterface.jsp?Success=YES&ctoken="+user.getCSRFToken());

        try
        {

            fileDetailsVOMap =fileManager.uploadMultipleTemplateForBank(request,queryString);
            request.setAttribute("fileDetailsVOMap",fileDetailsVOMap);
            // details
            //response.setStatus(HttpServletResponse.SC_RESET_CONTENT);
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");


        }
        catch (PZTechnicalViolationException e)
        {
            logger.error("PZTechnicalViolationException while uploading template for Bank",e);
            PZExceptionHandler.handleTechicalCVEException(e, null, "Uploading Bank Template");
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException while uploading template for Bank",e);
            PZExceptionHandler.handleDBCVEException(e, null, "Uploading Bank Template");
        }
        logger.debug("queryString::::"+queryString);
        RequestDispatcher rd = request.getRequestDispatcher("/servlet/listGatewayTypeDetails?ctoken=" + user.getCSRFToken()+queryString);
        rd.forward(request, response);
    }

}
