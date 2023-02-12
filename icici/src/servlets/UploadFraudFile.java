import com.directi.pg.Admin;
import com.directi.pg.AuditTrailVO;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.fileupload.FileUploadBean;
import com.logicboxes.util.ApplicationProperties;
import com.manager.utils.FileHandlingUtil;
import org.owasp.esapi.User;
import servlets.UploadBulkFraudList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Mahima
 * Date: 9/4/18
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadFraudFile extends HttpServlet
{
    private static Logger log = new Logger(UploadFraudFile.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        RequestDispatcher rd = req.getRequestDispatcher("/bulkFraudUpload.jsp?ctoken="+user.getCSRFToken());

        StringBuilder sErrorMessage = new StringBuilder();
        StringBuilder sSuccessMessage = new StringBuilder();
        String path = null;
        path = ApplicationProperties.getProperty("MPR_FILE_STORE");
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        auditTrailVO.setActionExecutorId("0");
        auditTrailVO.setActionExecutorName("Admin"+ "-"+user.getAccountName());

        FileUploadBean fub = new FileUploadBean();
        fub.setSavePath(path);

        try
        {
            fub.doUpload(req,null);
        }
        catch(SystemError sys){
            log.error("SystemError:::::",sys);
            req.setAttribute("sErrorMessage","Please provide valid file");
            rd.forward(req, res);
        }
        String File=fub.getFilename();
        String filePath=path+File;

        if(sErrorMessage.length()>0)
        {
            req.setAttribute("sErrorMessage",sErrorMessage);
            FileHandlingUtil fileHandlingUtil=new FileHandlingUtil();
            fileHandlingUtil.deleteFile(File);
            rd.forward(req, res);
        }
        try
        {
            StringBuilder response=null;
            UploadBulkFraudList uploadBulkFraudList=new UploadBulkFraudList();
            response=uploadBulkFraudList.uploadFraud(filePath,sSuccessMessage,sErrorMessage,auditTrailVO);

            req.setAttribute("error",response.toString());
            req.setAttribute("sSuccessMessage",sSuccessMessage);
            req.setAttribute("sErrorMessage",sErrorMessage);
        }
        catch (Exception e){
            req.setAttribute("error", e.getMessage());
        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("sErrorMessage", sErrorMessage.toString());
        req.setAttribute("sSuccessMessage", chargeBackMessage.toString());
        rd.forward(req, res);
    }
}
