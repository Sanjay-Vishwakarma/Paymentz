

import com.directi.pg.*;
import com.directi.pg.fileupload.FileUploadBean;
import com.logicboxes.util.ApplicationProperties;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Admin on 7/9/2020.
 */
public class CommonChargebackUpload extends HttpServlet
{
    private static Logger log = new Logger(CommonChargebackUpload.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        RequestDispatcher rd = request.getRequestDispatcher("/commonChargebackUplaod.jsp?ctoken="+user.getCSRFToken());

        String filePath = ApplicationProperties.getProperty("MPR_FILE_STORE");
        String logPath = ApplicationProperties.getProperty("LOG_STORE");
        String userName = null;

        FileUploadBean fub = new FileUploadBean();
        Functions functions = new Functions();
        CommonChargeback commonChargeback = new CommonChargeback();

        fub.setSavePath(filePath);
        fub.setLogpath(logPath);

        AuditTrailVO auditTrailVO = new AuditTrailVO();
        auditTrailVO.setActionExecutorName("Admin" + "-" + session.getAttribute("username").toString());
        auditTrailVO.setActionExecutorId("1");

        try
        {
            fub.doUpload(request, userName);
        }
        catch (SystemError sys)
        {
            request.setAttribute("ERROR", sys.getMessage());
            rd.forward(request,response);
            return;

        }

        String name=fub.getFilename();
        String gateway = fub.getFieldValue("gateway");
        StringBuffer val=null;
        if (functions.isValueNull(gateway))
        {
            if (ApplicationProperties.getProperty("MPR_FILE_STORE") + name != null)
            {
                try
                {
                    String fullFileLocation = ApplicationProperties.getProperty("MPR_FILE_STORE") + name;
                    val = commonChargeback.processChargeback(fullFileLocation, gateway, auditTrailVO);
                }
                catch (Exception e)
                {
                    request.setAttribute("ERROR", e.getMessage());
                    rd.forward(request,response);
                    return;
                }
            }
        }
        else
        {
            request.setAttribute("ERROR","Kindly select account Id.");
            rd.forward(request,response);
            return;
        }
        rd = request.getRequestDispatcher("/commonChargeBackFile.jsp?ctoken="+user.getCSRFToken());
        request.setAttribute("result",val.toString());
        rd.forward(request,response);
        return;

    }
}
