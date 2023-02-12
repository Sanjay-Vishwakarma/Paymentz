package net.partner;

import com.directi.pg.*;
import com.directi.pg.fileupload.FileUploadBean;
import com.logicboxes.util.ApplicationProperties;
import com.manager.utils.FileHandlingUtil;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Namrata on 18-09-2019.
 */
public class UploadFraudFile extends HttpServlet
{
    private static Logger log = new Logger(UploadChargebackFile.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner = new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");

        }

        RequestDispatcher rd = req.getRequestDispatcher("/uploadFraud.jsp?ctoken="+user.getCSRFToken());

        StringBuilder sErrorMessage = new StringBuilder();
        StringBuilder sSuccessMessage = new StringBuilder();
        FileUploadBean fub = new FileUploadBean();
        String path = null;
        path = ApplicationProperties.getProperty("MPR_FILE_STORE");
        String logPath = ApplicationProperties.getProperty("LOG_STORE");
        fub.setLogpath(logPath);
        fub.setSavePath(path);
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        String role = "";
        for (String s:user.getRoles())
        {
            role = role.concat(s);
        }
        auditTrailVO.setActionExecutorName(role+"-"+user.getAccountName());
        auditTrailVO.setActionExecutorId((String)session.getAttribute("merchantid"));
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
            UploadFraudList uploadFraud=new UploadFraudList();
            response=uploadFraud.uploadFraud(filePath,sSuccessMessage,sErrorMessage,auditTrailVO);
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
