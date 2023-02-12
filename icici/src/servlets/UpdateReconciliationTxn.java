package servlets;

import com.directi.pg.*;
import com.directi.pg.fileupload.FileUploadBean;
import com.logicboxes.util.ApplicationProperties;
import com.manager.utils.FileHandlingUtil;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Namrata Bari on 6/8/2020.
 */
public class UpdateReconciliationTxn  extends HttpServlet
{
    private static Logger log = new Logger(UpdateReconciliationTxn.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doProcess(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        doProcess(req, res);
    }

    public void doProcess(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        RequestDispatcher rd = req.getRequestDispatcher("/updatedReconTxnList.jsp?ctoken="+user.getCSRFToken());

        StringBuilder sErrorMessage = new StringBuilder();
        StringBuilder sSuccessMessage = new StringBuilder();
        FileUploadBean fub = new FileUploadBean();
        String path = null;
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        path = ApplicationProperties.getProperty("MPR_FILE_STORE");
        String logPath = ApplicationProperties.getProperty("LOG_STORE");
        fub.setLogpath(logPath);
        fub.setSavePath(path);
        try
        {
            fub.doUpload(req,null);
        }
        catch(SystemError sys){
            req.setAttribute("Result",sys.getMessage());
            req.setAttribute("msg","Error");
            rd.forward(req, res);
            return;

        }
        String File=fub.getFilename();
        String filePath=path+File;
        if(sErrorMessage.length()>0)
        {
            req.setAttribute("Result",sErrorMessage);
            req.setAttribute("msg","Error");
            FileHandlingUtil fileHandlingUtil=new FileHandlingUtil();
            fileHandlingUtil.deleteFile(File);
            rd.forward(req, res);
            return;

        }
        try
        {
            StringBuilder response=null;
            ProcessUploadReconTxn processUploadReconTxn=new ProcessUploadReconTxn();
            response=processUploadReconTxn.uploadReconTxn(filePath, sSuccessMessage, sErrorMessage,actionExecutorId,actionExecutorName);
            req.setAttribute("Result","<table class=\"tablestyle  table-bordered\">" +response.toString()+ "</table>");
            req.setAttribute("msg","Result");
            rd.forward(req, res);
            return;

        }
        catch (Exception e){
            req.setAttribute("Result", e.getMessage());
            req.setAttribute("msg","Error");
            rd.forward(req, res);
            return;

        }
    }
}
