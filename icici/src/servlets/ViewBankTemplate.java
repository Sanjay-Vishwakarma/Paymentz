import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.GatewayManager;
import com.manager.vo.gatewayVOs.GatewayTypeVO;
import org.owasp.esapi.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

//import com.manager.FileManager;
//import com.manager.utils.AppFileHandlingUtil;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 5/5/15
 * Time: 2:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class ViewBankTemplate  extends HttpServlet
{
    private static Logger logger = new Logger(ViewBankTemplate.class.getName());
    private static Functions functions = new Functions();
    //private static String log_store = ApplicationProperties.getProperty("LOG_STORE");
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = Functions.getNewSession(request);
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        //Manager Instance
        GatewayManager gatewayManager = new GatewayManager();
//        FileManager fileManager= new FileManager();
        //Util instance
//        AppFileHandlingUtil fileHandlingUtil = new AppFileHandlingUtil();
        //Vo instance
        GatewayTypeVO gatewayTypeVO=null;

        /*try
        {
            if(functions.isValueNull(request.getParameter("action")))
            {
                gatewayTypeVO = gatewayManager.getGatewayTypeForPgTypeId(request.getParameter("action"));
//                File file=fileManager.getBankAppTemplate(gatewayTypeVO);

                //sending file if found
//                fileHandlingUtil.openPdfFile(file, response);
            }
        }
        catch (PZTechnicalViolationException pte)
        {
            logger.error("File Not found exception or IOException::", pte);  //To change body of catch statement use File | Settings | File Templates.
            PZExceptionHandler.handleTechicalCVEException(pte, session.getAttribute("merchantid").toString(), "Exception while downloading the files from server related to Wire Reports(.pdf Or .xls)");
            request.setAttribute("file", "no");
            functions.NewShowConfirmation("Sorry","Some internal error caused");
        }*/
    }
}
