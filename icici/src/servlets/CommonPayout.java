import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.fileupload.FileUploadBean;
import com.logicboxes.util.ApplicationProperties;
import com.manager.utils.FileHandlingUtil;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;
import servlets.ProcessPayoutUpload;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 8/31/2020.
 */
public class CommonPayout extends HttpServlet
{
    private static Logger log = new Logger(CommonPayout.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
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
        RequestDispatcher rd = req.getRequestDispatcher("/commonpayout.jsp?ctoken="+user.getCSRFToken());

        String errormsg = "";
        String EOL = "<BR>";


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
        if (!ESAPI.validator().isValidInput("memberid", fub.getFieldValue("memberid"), "OnlyNumber", 10, false))
            errormsg = "Invalid Merchant Id" + EOL;


        if (!ESAPI.validator().isValidInput("terminalid", fub.getFieldValue("terminalid"), "OnlyNumber", 10, false))
            errormsg = errormsg +  "Invalid Terminal Id";


        if(errormsg.length()>0){
            req.setAttribute("Result", errormsg);
            req.setAttribute("msg","Error");
            rd.forward(req, res);
            return;
        }
        String terminalid =fub.getFieldValue("terminalid");
        String memberid= fub.getFieldValue("memberid");

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
            ProcessPayoutUpload process=new ProcessPayoutUpload();
            response=process.uploadPayout(filePath, sSuccessMessage, sErrorMessage, actionExecutorId, actionExecutorName,terminalid,memberid);
            req.setAttribute("Result","<table class=\"tablestyle  table-bordered\">" +response.toString()+ "</table>");
            req.setAttribute("msg","Result");
            rd.forward(req, res);
            return;

        }
        catch (Exception e){
            log.error("Exception--->",e);
            req.setAttribute("Result", e.getMessage());
            req.setAttribute("msg","Error");
            log.error("File---->"+File);
            FileHandlingUtil fileHandlingUtil=new FileHandlingUtil();
            fileHandlingUtil.deleteFile(File);
            rd.forward(req, res);
            return;

        }
    }

    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.MEMBERID);
        inputFieldsListMandatory.add(InputFields.TERMINALID);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}
