import com.directi.pg.*;
import com.directi.pg.fileupload.FileUploadBean;
import com.logicboxes.util.ApplicationProperties;
import com.manager.utils.FileHandlingUtil;
import com.payment.request.PZFileVO;
import com.payment.response.PZChargebackRecord;
import org.owasp.esapi.User;
import servlets.UploadChargebackList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Mahima
 * Date: 9/4/18
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadChargebackFile extends HttpServlet
{
    private static Logger log = new Logger(UploadChargebackFile.class.getName());
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
        RequestDispatcher rd = req.getRequestDispatcher("/bulkChargebackUpload.jsp?ctoken="+user.getCSRFToken());

        StringBuilder sErrorMessage = new StringBuilder();
        StringBuffer sSuccessMessage = new StringBuffer();
        Functions functions=new Functions();
        String path = null;
        path = ApplicationProperties.getProperty("MPR_FILE_STORE");

        FileUploadBean fub = new FileUploadBean();
        fub.setSavePath(path);

        try
        {
            fub.doUpload(req,null);
        }
        catch(SystemError sys){
            log.error("SystemError:::::",sys);
            req.setAttribute("errorMsg", "Please provide valid file or file name should not contains space");
            req.setAttribute("type",fub.getFieldValue("type"));
            req.setAttribute("gateway",fub.getFieldValue("gateway"));
            req.setAttribute("File",fub.getFilename());
            rd.forward(req, res);
        }
        String File=fub.getFilename();
        String filePath=path+File;
        String type=fub.getFieldValue("type");
        String gateway=fub.getFieldValue("gateway");

        req.setAttribute("type", fub.getFieldValue("type"));
        req.setAttribute("gateway", fub.getFieldValue("gateway"));
        req.setAttribute("File",File);

        if(functions.isValueNull(type)){
            if(type.equals("paymentId") && !functions.isValueNull(gateway)){
                req.setAttribute("errorMsg","Please select the bank name as you are uploading the file on payment id level.");
                rd.forward(req, res);
            }
        }

        AuditTrailVO auditTrailVO = new AuditTrailVO();
        auditTrailVO.setActionExecutorName("Admin" + "-" + session.getAttribute("username").toString()+"(Bulk Chargeback Uploaded)");
        auditTrailVO.setActionExecutorId("0");

        if(sErrorMessage.length()>0)
        {
            req.setAttribute("errorMsg",sErrorMessage);
            FileHandlingUtil fileHandlingUtil=new FileHandlingUtil();
            fileHandlingUtil.deleteFile(File);
            rd.forward(req, res);
        }
        try
        {
            UploadChargebackFile commonChargeback=new UploadChargebackFile();
            String fullFileLocation = ApplicationProperties.getProperty("MPR_FILE_STORE") + File;
            sSuccessMessage=commonChargeback.processChargebackWithoutGateway(fullFileLocation, auditTrailVO,type,gateway);

            req.setAttribute("error",sSuccessMessage);
            req.setAttribute("errorMsg",sErrorMessage);
        }
        catch (Exception e){
            req.setAttribute("error", e.getMessage());
        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("errorMsg", sErrorMessage.toString());
        req.setAttribute("error", chargeBackMessage.toString());
        rd.forward(req, res);
    }
    public StringBuffer processChargebackWithoutGateway(String completeFileName, AuditTrailVO auditTrailVO,String type,String gateway) throws SystemError
    {
        PZFileVO pzFileVO = new PZFileVO();
        pzFileVO.setFilepath(completeFileName);

        log.error("-----Before readChargebackFile()-----"+pzFileVO);
        FileHandlingUtil fileHandlingUtil=new FileHandlingUtil();
        List<PZChargebackRecord> vTransactions = fileHandlingUtil.readChargebackFile(pzFileVO);
        log.error("-----After readChargebackFile()----"+vTransactions.size());

        StringBuffer resultString = new StringBuffer();
        if(vTransactions != null && vTransactions.size()>0)
        {
            UploadChargebackList uploadChargebackList=new UploadChargebackList();
            resultString = uploadChargebackList.processChargeback(vTransactions,auditTrailVO,type,gateway);
        }
        else
        {
            resultString.append("No Transactions Found");
        }
        return resultString;
    }
}